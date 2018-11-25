package com.dongci.sun.gpuimglibrary.player;

import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Surface;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

import static java.lang.Thread.sleep;

/**
 * Created by zhangxiao on 2018/6/14.
 *
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class DCAssetVideoWrapper extends DCAssetWrapper {

    final static class AudioBufferInfo {
        float volume = 1.0f;
        byte[] bytes;
        long timestamp;
        int  index;

        AudioBufferInfo(float volume, byte[] bytes, long timestamp,int index) {
            this.volume = volume;
            this.bytes = bytes;
            this.timestamp = timestamp;
            this.index  = index;
        }

    }

    private static final int TIMEOUT_USEC = 20000;
    private static final String TAG = "DCAssetWrapper";
    private static final String VIDEO_PREFIX_IN_MIME = "video/";
    private static final String AUDIO_PREFIX_IN_MIME = "audio/";

    private boolean mIsExtractorReachedVideoEOS;
    private boolean mIsExtractorReachedAudioEOS;
    private volatile long mCurVideoPresentationTimeUs;
    private volatile long mCurAudioPresentationTimeUs;

    private DCMediaInfoExtractor.MediaInfo mMediaInfo;

    // video
    private MediaExtractor mVideoExtractor;
    private MediaCodec mVideoDecoder;
    private int mVideoTrackIndex;
    private MediaCodec.BufferInfo mVideoBufferInfo;
    private MediaFormat mVideoFormat;
    private Surface mSurface;
    private String mVideoMime;
    private long mLastRenderingTimeUs;

    // audio
    private MediaExtractor mAudioExtractor;
    private MediaCodec mAudioDecoder;
    private int mAudioTrackIndex;
    private ByteBuffer[] mAudioDecoderOutputBuffer;
    private MediaCodec.BufferInfo mAudioBufferInfo;
    private MediaFormat mAudioFormat;
    private AudioTrack mAudioTrack;
    private String mAudioMime;

    private Thread mVideoThread = null;
    private Thread mAudioThread = null;

    private final Object mVideoFrameSyncObject = new Object();
    private final Object mNeedVideoFrameSyncObject = new Object();
//    private final Object mPauseVideoSyncObject = new Object();
//    private final Object mPauseAudioSyncObject = new Object();
    volatile boolean mCurrentAudioFrameIsDone = true;

//    private volatile boolean mPaused;
    private boolean mIsStopPlayback;

    Queue<AudioBufferInfo> mAudioSampleCache = new LinkedList<>();

    private long mPrevDecoderOutputFramePtsUs;
    private long mEstimatedKeyframeInterval;
    private long mIdenticalFrameInterval;
    private long mMinVideoTimestamp;

    // player
    private MediaPlayer mMediaPlayer;
    private boolean mPlayerIsCompleted;
    private volatile boolean mIsPlayerStart = false;



    DCAssetVideoWrapper(DCAsset asset) {
        super(asset);
        mAsset = asset;
        mVideoIsDone = false;
        mAudioIsDone = false;
        if (mAsset.type != DCAsset.DCAssetTypeImage) {
            try {
//                String temp = Environment.getExternalStorageDirectory()+ File.separator+"dongci.mp4";
                Log.d("sun==video","VirtualVideDCAssetVideoWrapper--asset-filepath::>"+mAsset.filePath+"Exit:>"+new File(mAsset.filePath).exists()+new File(mAsset.filePath).length());
                if(!new File(mAsset.filePath).exists()
                        ||(new File(mAsset.filePath).length()==0)){//如果资源是空的，不播放
                    return;
                }
                Log.d("sun==video","VirtualVideDCAssetVideoWrapper--asset-filepath::>end>");

                //mAsset.filePath
                mMediaInfo = DCMediaInfoExtractor.extract(mAsset.filePath);

                // player  mAsset.filePath
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setDataSource(mAsset.filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mAudioStatus = OPEN;
    }

    void setSurface(Surface surface) throws NullPointerException{
        if(surface == null){
            throw new NullPointerException("Invalid argument.");
        }
        mSurface = surface;
        if (!mIsForExporting && mMediaPlayer != null) {
            mMediaPlayer.setSurface(mSurface);
        }
    }

    Surface getSurface() {
        return mSurface;
    }

    MediaFormat getAudioFormat() {
        return mAudioFormat;
    }

    long getPlayerTimestamp() {
        if(!mIsPlayerStart) {
            return 0;
        }
        return mMediaPlayer.getCurrentPosition() * 1000;
    }

    @Override
    int getWidth() {
        if(mMediaInfo != null){
            return (int)mMediaInfo.videoInfo.width;
        }
        return 0;
    }

    @Override
    int getHeight() {
        if(mMediaInfo != null){
            return (int)mMediaInfo.videoInfo.height;
        }
        return 0;
    }

    @Override
    void setExporting(boolean exporting) {
        mIsForExporting = exporting;
    }

    @Override
    void prepare() throws IOException{
        if (mIsForExporting) {
            // prepare extractor
            prepareVideo();
            prepareAudio();

            // reset decoder
            restartDecoder();

            // seek to dest position
            extractorSeekTo(mAsset.getTimeRange().startTime, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
            flush();
            resetPositionInfo();
        } else {
            if (mAsset.type == DCAsset.DCAssetTypeVideo) {
                mMediaPlayer.setSurface(mSurface);
            }
            // player
            mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    mSeekIsCompleted = true;
                    if (mOnSeekCompletionListener != null) {
                        mOnSeekCompletionListener.OnSeekCompletion(DCAssetVideoWrapper.this);
                    }
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mPlayerIsCompleted = true;
                }
            });
            mMediaPlayer.prepare();
            mMediaPlayer.setVolume(mAsset.getVolume(), mAsset.getVolume());
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mMediaPlayer.seekTo((int) (mAsset.getTimeRange().startTime / 1000), MediaPlayer.SEEK_CLOSEST);
            } else {
                mMediaPlayer.seekTo((int) (mAsset.getTimeRange().startTime / 1000));
            }
        }
    }

    private void prepareVideo() throws IOException {
        mVideoExtractor = new MediaExtractor();
        mVideoExtractor.setDataSource(mAsset.filePath);

        mEstimatedKeyframeInterval = mMediaInfo.videoInfo.perFrameDurationUs * mMediaInfo.videoInfo.fps;
        mIdenticalFrameInterval = (long)(mMediaInfo.videoInfo.perFrameDurationUs * 0.5);

        mVideoTrackIndex = -404;
        int numTracks = mVideoExtractor.getTrackCount();
        for (int i = 0; i < numTracks; i++) {
            MediaFormat format = mVideoExtractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith(VIDEO_PREFIX_IN_MIME)) {
                mVideoTrackIndex = i;
                mVideoExtractor.selectTrack(mVideoTrackIndex);
                mVideoFormat = mVideoExtractor.getTrackFormat(mVideoTrackIndex);
                mVideoMime = mVideoFormat.getString(MediaFormat.KEY_MIME);
            }
            else {
                Log.d(TAG, "unexpected track in source file");
            }
        }
        mMinVideoTimestamp = mVideoExtractor.getSampleTime();
    }

    private void prepareAudio() throws IOException {
        mAudioExtractor = new MediaExtractor();
        mAudioExtractor.setDataSource(mAsset.filePath);

        mAudioTrackIndex = -404;
        int numTracks = mAudioExtractor.getTrackCount();
        for (int i = 0; i < numTracks; i++) {
            MediaFormat format = mAudioExtractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith(AUDIO_PREFIX_IN_MIME)) {
                mAudioTrackIndex = i;
                mAudioExtractor.selectTrack(mAudioTrackIndex);
                mAudioFormat = mAudioExtractor.getTrackFormat(mAudioTrackIndex);
                mAudioMime = mAudioFormat.getString(MediaFormat.KEY_MIME);
            } else {
                Log.d(TAG, "unexpected track in source file");
            }
        }
        mAudioStatus = PREPARED;

    }

    @Override
    void play() {
        super.play();
        if (mIsForExporting) {
            restartDecoder();
            extractorSeekTo(mAsset.getTimeRange().startTime, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
            flush();
            resetPositionInfo();

            // start work threads
            if (mHasVideoTrack && mVideoTrackIndex >= 0) {
                startVideoThread();
            } else {
                mVideoIsDone = true;
            }
            if (mHasAudioTrack && mAudioTrackIndex >= 0) {
                startAudioThread();
            } else {
                mAudioIsDone = true;
            }
        } else {
            mMediaPlayer.start();
            mIsPlayerStart = true;
            mPlayerIsCompleted = false;
        }
    }

    @Override
    void pause() {
        super.pause();
        if (!mIsForExporting) {
            mMediaPlayer.pause();
        }
    }

    @Override
    void resume() {
        super.resume();
//        mPaused = false;
        if (!mIsForExporting) {
            mMediaPlayer.start();
            mIsPlayerStart = true;
        }
    }

    @Override
    void seekTo(long us) {
        long dt = 0L;
        if (us <= mAsset.startTimeInScene) {
            dt = mAsset.getTimeRange().startTime;
        } else if (us > mAsset.startTimeInScene && us < mAsset.startTimeInScene + mAsset.getTimeRange().duration) {
            dt = us - mAsset.startTimeInScene + mAsset.getTimeRange().startTime;
        } else if (us >= mAsset.startTimeInScene + mAsset.getTimeRange().duration) {
            dt = mAsset.getTimeRange().startTime + mAsset.getTimeRange().duration;
        }

        if (mIsForExporting) {
            // TODO: 2018/6/26 zhangxiao
//            extractorSeekTo(dt, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
//            flush();
        } else {
            mSeekIsCompleted = false;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mMediaPlayer.seekTo((int) (dt / 1000), MediaPlayer.SEEK_CLOSEST);
            } else {
                mMediaPlayer.seekTo((int) (dt / 1000));
            }
        }
    }

    @Override
    void setVolume(float volume) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(volume, volume);
        }
    }

    @Override
    void release() {
        mIsStopPlayback = true;
        if(mVideoThread != null){
            mVideoThread.interrupt();
            mVideoThread = null;
        }
        if(mAudioThread != null){
            mAudioThread.interrupt();
            mAudioThread = null;
        }
        if(mVideoDecoder!=null) {
            mVideoDecoder.stop();
            mVideoDecoder.release();
            mVideoDecoder = null;
        }
        if(mAudioDecoder != null){
            mAudioDecoder.stop();
            mAudioDecoder.release();
            mAudioDecoder = null;
        }
        if(mVideoExtractor!=null){
            mVideoExtractor.release();
            mVideoExtractor = null;
        }
        if(mAudioExtractor!=null){
            mAudioExtractor.release();
            mAudioExtractor = null;
        }

        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
        }

        if (mMediaPlayer != null) {
            if (mIsPlayerStart) {
                mIsPlayerStart = false;
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        mIsExtractorReachedVideoEOS = false;
        mIsExtractorReachedAudioEOS = false;

        mAudioStatus = CLOSE;

        needAudioFrame = true;
        mVideoIsDone = false;
        mAudioIsDone = false;
        mCurrentTimestamp = 0L;
        mLastRenderingTimeUs = 0L;
        mCurVideoPresentationTimeUs = Integer.MIN_VALUE;
        mCurAudioPresentationTimeUs = Integer.MIN_VALUE;
        mPrevDecoderOutputFramePtsUs = Integer.MIN_VALUE;
        mAudioSampleCache.clear();
        mMediaInfo = null;
        mVideoFormat = null;
        mAudioFormat = null;
    }

    private void flush() {
        if (mVideoDecoder != null) {
            mVideoDecoder.flush();
        }

        if (mAudioDecoder != null) {
            mAudioDecoder.flush();
        }

        if (mAudioTrack != null) {
            mAudioTrack.flush();
        }
    }

    private void restartDecoder() {
        try {
            // video
            if(mVideoTrackIndex >= 0)
            {
                if(mVideoDecoder != null){
                    mVideoDecoder.stop();
                    mVideoDecoder.release();
                    mVideoDecoder = null;
                }

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    mVideoDecoder = MediaCodec.createDecoderByType(mVideoMime);
                } else if (mAsset.index >= 3 ) {
                    mVideoDecoder = MediaCodec.createByCodecName("OMX.google.h264.decoder");
                } else {
                    mVideoDecoder = MediaCodec.createDecoderByType(mVideoMime);
                }

                //mVideoDecoder = MediaCodec.createDecoderByType(mVideoMime);
                mVideoDecoder.configure(mVideoFormat, mSurface, null, 0);
                mVideoDecoder.start();

                mVideoBufferInfo = new MediaCodec.BufferInfo();
            }

            // audio
            if(mAudioTrackIndex >= 0)
            {
                if(mAudioDecoder != null){
                    mAudioDecoder.stop();
                    mAudioDecoder.release();
                    mAudioDecoder = null;
                }

                mAudioDecoder = MediaCodec.createDecoderByType(mAudioMime);
                mAudioDecoder.configure(mAudioFormat, null, null, 0);
                mAudioDecoder.start();

                mAudioDecoderOutputBuffer = mAudioDecoder.getOutputBuffers();
                mAudioBufferInfo = new MediaCodec.BufferInfo();

                /*if(mAudioTrack == null)
                {
                    int sampleRateInHz = mAudioFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
                    int channelCount = mAudioFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
                    int channelConfig = channelCount == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO;
                    int encoding = AudioFormat.ENCODING_PCM_16BIT;
                    mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                            sampleRateInHz,
                            channelConfig,
                            encoding,
                            AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, encoding) * 2,
                            AudioTrack.MODE_STREAM);
                    mAudioTrack.setVolume(mAsset.volume);
                    mAudioTrack.play();
                }*/
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopComponent() {
        if(mVideoThread != null){
            mVideoThread.interrupt();
        }
        if(mAudioThread != null){
            mAudioThread.interrupt();
        }
        if(mVideoDecoder!=null) {
            mVideoDecoder.stop();
            mVideoDecoder.release();
            mVideoDecoder = null;
        }
        if(mAudioDecoder != null){
            mAudioDecoder.stop();
            mAudioDecoder.release();
            mAudioDecoder = null;
        }
        if(mVideoExtractor!=null){
            mVideoExtractor.release();
            mVideoExtractor = null;
        }
        if(mAudioExtractor!=null){
            mAudioExtractor.release();
            mAudioExtractor = null;
        }
        mMediaInfo = null;

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void resetPositionInfo() {
        needAudioFrame = true;
        mVideoIsDone = !mHasVideoTrack;
        mAudioIsDone = !mHasAudioTrack;
        mCurrentTimestamp = 0L;
        mLastRenderingTimeUs = 0L;
        mCurVideoPresentationTimeUs = Integer.MIN_VALUE;
        mCurAudioPresentationTimeUs = Integer.MIN_VALUE;
        mPrevDecoderOutputFramePtsUs = Integer.MIN_VALUE;
        mVideoBufferInfo = new MediaCodec.BufferInfo();
        mAudioBufferInfo = new MediaCodec.BufferInfo();
        mAudioSampleCache.clear();
    }

    private void extractorSeekTo(long timestamp, int seekFlag){
        mVideoExtractor.seekTo(timestamp, seekFlag);
        mAudioExtractor.seekTo(timestamp, seekFlag);
        mIsExtractorReachedVideoEOS = false;
        mIsExtractorReachedAudioEOS = false;
    }

    private boolean currentFrameIsTargetFrame(long currentTimestamp, long targetTimestamp){
        return (Math.abs(currentTimestamp - targetTimestamp) < mIdenticalFrameInterval
                || (targetTimestamp > mPrevDecoderOutputFramePtsUs && targetTimestamp < currentTimestamp));
    }

    private void extractorSeekToPreviousSync(long timestamp){
        while(true) {
            mVideoExtractor.seekTo(timestamp, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
            if (mVideoExtractor.getSampleTime() <= timestamp) {
                break;
            } else {
                timestamp = Math.max(0, timestamp - mEstimatedKeyframeInterval);
            }
        }
    }

    // video
    private void feedVideoDecoder() {
        if (mIsExtractorReachedVideoEOS) {
            return;
        }

        ByteBuffer[] inputBuffers = mVideoDecoder.getInputBuffers();
        int inIndex = mVideoDecoder.dequeueInputBuffer(TIMEOUT_USEC);
        if (inIndex >= 0) {
            ByteBuffer buffer = inputBuffers[inIndex];
            int sampleSize = mVideoExtractor.readSampleData(buffer, 0);
            if (sampleSize < 0) {
                Log.d(TAG, "VideoInputBuffer BUFFER_FLAG_END_OF_STREAM");
                mVideoDecoder.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                mIsExtractorReachedVideoEOS = true;
            } else {
                long sampleTime = mVideoExtractor.getSampleTime();
                mVideoDecoder.queueInputBuffer(inIndex, 0, sampleSize, sampleTime, 0);
                mVideoExtractor.advance();
            }
        }
    }

    private int drainVideoDecoder() {
        mPrevDecoderOutputFramePtsUs = mVideoBufferInfo.presentationTimeUs;
        int outIndex = mVideoDecoder.dequeueOutputBuffer(mVideoBufferInfo, TIMEOUT_USEC);
        switch (outIndex) {
            case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                Log.d(TAG, "INFO_OUTPUT_BUFFERS_CHANGED");
                break;
            case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                MediaFormat newFormat = mVideoDecoder.getOutputFormat();
                Log.d(TAG, "New format " + newFormat);
                break;
            case MediaCodec.INFO_TRY_AGAIN_LATER:
                Log.d(TAG, "no output from video decoder available");
                break;
            default:
                break;
        }
        return outIndex;
    }

    private boolean seekInternal(long timestamp){
        long prevFrameTime = mCurVideoPresentationTimeUs;
        if (currentFrameIsTargetFrame(mCurVideoPresentationTimeUs, timestamp)){
            return false;
        }
        mCurVideoPresentationTimeUs = timestamp;

        long seekInterval = timestamp - (mVideoBufferInfo.presentationTimeUs == 0? mMinVideoTimestamp: mVideoBufferInfo.presentationTimeUs);
        if (seekInterval < 0
                || (isVideoDecoderReachEOS() && mVideoBufferInfo.presentationTimeUs == 0)) {
            if (timestamp < mMinVideoTimestamp) {
                return false;
            } else {
                extractorSeekToPreviousSync(timestamp);
                if (isVideoDecoderReachEOS()) {
                    restartDecoder();
                } else {
                    mVideoDecoder.flush();
                }
            }
        } else
            {
            if (isVideoFinished()) {
                return false;
            }
            if (seekInterval > mEstimatedKeyframeInterval){
                extractorSeekToPreviousSync(timestamp);
                mVideoDecoder.flush();
            }
        }
        int prevBufferIndex = -1;
        boolean firstFrameFromDecoder = true;
        while (true) {
            feedVideoDecoder();
            int bufferIndex = drainVideoDecoder();
            if (bufferIndex >= 0) {
                boolean seekCompleted = false;
                if (currentFrameIsTargetFrame(mVideoBufferInfo.presentationTimeUs, timestamp)
                        || (firstFrameFromDecoder && mVideoBufferInfo.presentationTimeUs > timestamp)) {
                    mCurVideoPresentationTimeUs = mVideoBufferInfo.presentationTimeUs;
                    seekCompleted = true;
                } else if (isVideoFinished()) {
                    mCurVideoPresentationTimeUs = mMediaInfo.durationUs;
                    seekCompleted = true;
                }
                if (seekCompleted) {
                    long elapsedUs = (long)(System.nanoTime() / 1000.0) - mLastRenderingTimeUs;
                    long timeToBeWait = (long)((mVideoBufferInfo.presentationTimeUs - (prevFrameTime + elapsedUs)) / 1000.0);
                    if (prevFrameTime > 0 && timeToBeWait > 0 && !mIsForExporting) {
                        try {
                            sleep(timeToBeWait);
                        } catch (InterruptedException iex){
                            iex.printStackTrace();
                        }
                    }
                    if (prevBufferIndex != -1) {
                        if (isVideoFinished()) {
                            mVideoDecoder.releaseOutputBuffer(prevBufferIndex, timestamp);
                            mVideoDecoder.releaseOutputBuffer(bufferIndex,false);
                        } else {
                            mVideoDecoder.releaseOutputBuffer(prevBufferIndex, false);
                            mVideoDecoder.releaseOutputBuffer(bufferIndex, timestamp);
                        }
                    } else {
                        mVideoDecoder.releaseOutputBuffer(bufferIndex, timestamp);
                    }
                    mLastRenderingTimeUs = System.nanoTime() / 1000;
                    return true;
                } else {
                    if (prevBufferIndex != -1) {
                        mVideoDecoder.releaseOutputBuffer(prevBufferIndex, false);
                    }
                    prevBufferIndex = bufferIndex;
                }
                firstFrameFromDecoder = false;
            }
        }
    }

    // audio
    private void feedAudioDecoder() {
        if (mIsExtractorReachedAudioEOS) {
            return;
        }

        ByteBuffer[] inputBuffers = mAudioDecoder.getInputBuffers();
        int inIndex = mAudioDecoder.dequeueInputBuffer(TIMEOUT_USEC);
        if (inIndex >= 0) {
            ByteBuffer buffer = inputBuffers[inIndex];
            int sampleSize = mAudioExtractor.readSampleData(buffer, 0);

            if (sampleSize < 0) {
                Log.d(TAG, "AudioInputBuffer BUFFER_FLAG_END_OF_STREAM");
                mAudioDecoder.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                mIsExtractorReachedAudioEOS = true;
            } else {
                mAudioDecoder.queueInputBuffer(inIndex, 0, sampleSize, mAudioExtractor.getSampleTime(), 0);
                mAudioExtractor.advance();
            }
        }
    }

    private int drainAudioDecoder() {
        int outIndex = mAudioDecoder.dequeueOutputBuffer(mAudioBufferInfo, TIMEOUT_USEC);
        switch(outIndex)
        {
            case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                mAudioDecoderOutputBuffer = mAudioDecoder.getOutputBuffers();
                if(mAudioTrack !=null) {
                    mAudioTrack.setPlaybackRate(mAudioFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE));
                }
                Log.d(TAG, "audio decoder output buffers changed");
                break;
            case MediaCodec.INFO_TRY_AGAIN_LATER:
                Log.d(TAG, "no output from audio decoder available");
                break;
            case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                MediaFormat newFormat = mAudioDecoder.getOutputFormat();
                if(mAudioTrack !=null) {
                    mAudioTrack.setPlaybackRate(newFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE));
                }
                Log.d(TAG, "audio decoder output format changed: " + newFormat);
                break;
            default:
                break;
        }
        return outIndex;
    }

    @Override
    boolean isVideoFinished() {
        if (mIsForExporting) {
            return mCurrentTimestamp >= mAsset.getTimeRange().startTime + mAsset.getTimeRange().duration ||
                    currentFrameIsTargetFrame(mCurVideoPresentationTimeUs, mAsset.getTimeRange().startTime + mAsset.getTimeRange().duration) || isVideoDecoderReachEOS();
        } else {
            if (mAsset.type == DCAsset.DCAssetTypeVideo) {
                return mMediaPlayer.getCurrentPosition() * 1000 >= mAsset.getTimeRange().startTime + mAsset.getTimeRange().duration ||
                        currentFrameIsTargetFrame(mMediaPlayer.getCurrentPosition() * 1000, mAsset.getTimeRange().startTime + mAsset.getTimeRange().duration)
                        || mPlayerIsCompleted;
            } else {
                return true;
            }
        }
    }

    @Override
    boolean isAudioFinished() {
        if (mIsForExporting) {
            return mCurrentTimestamp >= mAsset.getTimeRange().startTime + mAsset.getTimeRange().duration ||
                    currentFrameIsTargetFrame(mCurAudioPresentationTimeUs, mAsset.getTimeRange().startTime + mAsset.getTimeRange().duration) || isAudioDecoderReachEOS();
        } else {
            return mMediaPlayer.getCurrentPosition() * 1000 >= mAsset.getTimeRange().startTime + mAsset.getTimeRange().duration ||
                    currentFrameIsTargetFrame(mMediaPlayer.getCurrentPosition() * 1000, mAsset.getTimeRange().startTime + mAsset.getTimeRange().duration) ||
                      mPlayerIsCompleted;
        }
    }

    @Override
    boolean isCurrentFrameAudio() {
        return mCurVideoPresentationTimeUs <= mCurVideoPresentationTimeUs;
    }

    private boolean isVideoDecoderReachEOS(){
        return ((mVideoBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0);
    }

    private boolean isAudioDecoderReachEOS(){
        return ((mAudioBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0);
    }

    private void playVideo(){
        if(mVideoTrackIndex < 0 || isVideoDecoderReachEOS()){
            Log.d(TAG, "onPlaybackComplete");
        }
        else
        {
            needVideoFrame = false;
            boolean updated = seekInternal(mCurrentTimestamp);
            if (updated) {
                awaitNewVideoFrame();
            }
            if (mOnVideoFrameReadyListener != null) {
                mOnVideoFrameReadyListener.OnVideoFrameReady(this, mVideoBufferInfo);
            }
        }
    }

    private void playAudio() {
        if(mAudioTrackIndex < 0 || isAudioDecoderReachEOS()){
            Log.d(TAG, "onPlaybackComplete");
        }
        else
        {
            feedAudioDecoder();
            int audioBufferIndex = drainAudioDecoder();
            if (!mIsForExporting) {
                while (!isVideoFinished() && mAudioBufferInfo.presentationTimeUs > mVideoBufferInfo.presentationTimeUs && !isAudioFinished()) {
                    mCurrentAudioFrameIsDone = true;
                }
            }
            mCurrentAudioFrameIsDone = false;
            if(audioBufferIndex >= 0){
                needAudioFrame = false;

                ByteBuffer decodedBuffer = mAudioDecoderOutputBuffer[audioBufferIndex];
                decodedBuffer.position(mAudioBufferInfo.offset); // for API level 16
                decodedBuffer.limit(mAudioBufferInfo.offset + mAudioBufferInfo.size); // for API level 16

                byte[] audioBuffer = new byte[mAudioBufferInfo.size];
                decodedBuffer.get(audioBuffer);

                if (!mIsForExporting) {
                    if(mAudioTrack != null) {
                        mAudioTrack.write(audioBuffer, 0, mAudioBufferInfo.size);
                    }
                } else {
                    mAudioSampleCache.add(new AudioBufferInfo(mAsset.getVolume(), audioBuffer, mAudioBufferInfo.presentationTimeUs,mAsset.index));
                }

                mAudioDecoder.releaseOutputBuffer(audioBufferIndex, false);
                mCurAudioPresentationTimeUs = mAudioBufferInfo.presentationTimeUs;

                // TODO: 2018/6/20  zhangxiao
                if (mOnAudioFrameReadyListener != null && !mIsForExporting) {
//                    if (mIsForExporting) {
//                        mOnAudioFrameReadyListener.OnAudioFrameReady(this, audioBuffer, mAudioBufferInfo);
//                    } else {
//                        mOnAudioFrameReadyListener.OnAudioFrameReady(this, null, null);
//                    }
                    Log.d(TAG,"==---playTime--FramAvailable-----playAudio--frameReady");
                    mOnAudioFrameReadyListener.OnAudioFrameReady(this, null, null);
                }
            }
        }
    }

    private void startVideoThread() {
        if (mVideoThread != null) {
            mVideoThread.interrupt();
            mVideoThread = null;
        }
        mVideoThread = new Thread("VideoThread-" + System.currentTimeMillis()) {
            @Override
            public void run() {
                try {
                    while (!mIsStopPlayback && !(mVideoTrackIndex < 0 || isVideoFinished())) {
                        if (mIsForExporting) {
                            if (!mPaused && needVideoFrame) {
                                playVideo();
                            } else {
                                synchronized (mNeedVideoFrameSyncObject) {
                                    try {
                                        mNeedVideoFrameSyncObject.wait(100);
                                    } catch (InterruptedException ie) {
                                    }
                                }

                            }
                        } else {
                            if (!mPaused) {
                                playVideo();
                            }
                        }
                    }
                    mVideoIsDone = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "----- exit video loop."+e);
                }
                Log.d(TAG, "----- exit video loop.");

                if (mOnCompletionListener != null) {
                    mOnCompletionListener.OnCompletion(DCAssetVideoWrapper.this);
                }

            }
        };
        mVideoThread.start();
    }

    private void startAudioThread() {
        if (mAudioThread != null) {
            mAudioThread.interrupt();
            mAudioThread = null;
        }
        Log.d(TAG, "----- exit audio loop.---startAudioThread");
        mAudioThread = new Thread("AudioThread-" + System.currentTimeMillis()) {
            @Override
            public void run() {
                try {
                    mAudioStatus = PLAYING;
                    while (!mIsStopPlayback && !(mAudioTrackIndex < 0 || isAudioFinished())) {
                        if (needAudioFrame && !mPaused || mIsForExporting) {
                            playAudio();
                        }
                    }
                    mAudioStatus = FINISH;
                    if (mOnAudioCompletionListener != null) {
                        mOnAudioCompletionListener.OnAudioCompletion(DCAssetVideoWrapper.this);
                    }
                    mAudioIsDone = true;
                    mCurrentAudioFrameIsDone = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "----- exit audio loop.---"+mAudioIsDone);

                if (mOnCompletionListener != null) {
                    mOnCompletionListener.OnCompletion(DCAssetVideoWrapper.this);
                }

            }
        };
        mAudioThread.start();
    }

    private void awaitNewVideoFrame() {
        final int TIMEOUT_MS = 50;
        synchronized (mVideoFrameSyncObject) {
            try {
                mVideoFrameSyncObject.wait(TIMEOUT_MS);
            } catch (InterruptedException ie) {
                // ignore
            }
        }
    }

    @Override
    void onVideoFrameAvailable() {
        synchronized (mVideoFrameSyncObject) {
            mVideoFrameSyncObject.notifyAll();
        }
    }

    void notifyNeedVideoFrameSync(){
        synchronized (mNeedVideoFrameSyncObject) {
            mNeedVideoFrameSyncObject.notifyAll();
        }
    }

}
