package com.dongci.sun.gpuimglibrary.player;

import android.media.MediaCodec;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Created by zhangxiao on 2018/6/6.
 *
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class DCSceneWrapper implements DCAssetWrapper.OnVideoFrameReadyListener,
        DCAssetWrapper.OnAudioFrameReadyListener,
        DCAssetWrapper.OnCompletionListener,
        DCAssetWrapper.OnAudioCompletionListener,
        DCAssetWrapper.OnSeekCompletionListener {

    interface OnPositionUpdateListener
    {
        void OnPositionUpdate(float progress, long timeStamp);
    }

    interface OnAudioSampleReadyListener
    {
        void OnAudioSampleReady(ByteBuffer audioSample);
    }

    interface OnCompletionListener
    {
        void OnCompletion(DCSceneWrapper sceneWrapper);
    }

    interface OnAllVideoFramesReadyListener
    {
        void OnAllVideoFramesReady(DCSceneWrapper sceneWrapper, long timestamp);
    }

    private final String TAG = "DCSceneWrapper";
    private List<DCAssetWrapper> mAssetWrapperList;
    private volatile int mAvailableVideoFrameCount;
    private volatile int mAvailableAudioFrameCount;

    private OnPositionUpdateListener mPositionUpdateListener = null;
    private OnAudioSampleReadyListener mOnAudioSampleReadyListener = null;
    private OnCompletionListener mOnCompletionListener = null;
    private OnAllVideoFramesReadyListener mOnAllVideoFramesReadyListener = null;

    private List<DCAssetVideoWrapper.AudioBufferInfo> mAudioData = new ArrayList<>();
    private boolean mIsForExporting = false;
    private boolean mHasVideoTrack = true;
    private boolean mHasAudioTrack = true;
    private volatile boolean mIsCompleted;
    private int mFPS = 24;
    private volatile long mCurrentTimestamp = 0L;
    private final float AUDIO_RATIO = 0.0f;


    private  final static  double dropout_transition = 2.0;

    private  float[] mWeights ;
    private  float[] mScaleNormal ;
    private  float[] mInputScale ;

    private  float mWeightSum =0;

    // player
    private Thread mPlayThread;
    private boolean mPlayAfterSeeking;
    private boolean mIsStopPlayback;

    private  long upSideDownValue = 0;
    private  long downSideUpValue = 0;

    void setScene(DCScene scene) {
        int i=0;
        mWeights = new float[scene.assets.size()];
        mScaleNormal = new float[scene.assets.size()];
        mInputScale = new float[scene.assets.size()];

        mAssetWrapperList = new ArrayList<>();
        for (DCAsset asset : scene.assets) {
            DCAssetWrapper assetWrapper;
            switch(asset.type) {
                case DCAsset.DCAssetTypeImage:
                    assetWrapper = new DCAssetImageWrapper(asset);
                    break;
                case DCAsset.DCAssetTypeImages:
                    assetWrapper = new DCAssetImagesWrapper(asset);
                    break;
                case DCAsset.DCAssetTypeVideo:
                case DCAsset.DCAssetTypeAudio:
                default:
                    asset.index = i;
                    assetWrapper = new DCAssetVideoWrapper(asset);
                    mWeightSum +=asset.weights;
                    mWeights[asset.index] = asset.weights;
                    i++;
                    break;
            }
            assetWrapper.setOnVideoFrameReadyListener(this);
            assetWrapper.setOnAudioFrameReadyListener(this);
            assetWrapper.setOnCompletionListener(this);
            assetWrapper.setOnAudioCompletionListener(this);
            assetWrapper.setOnSeekCompletionListener(this);
            mAssetWrapperList.add(assetWrapper);
            asset.setAssetWrapper(assetWrapper);
        }
        for (DCAsset asset : scene.assets) {
            switch(asset.type) {
                case DCAsset.DCAssetTypeVideo:
                case DCAsset.DCAssetTypeAudio:
                default:
                    if(asset.weights >= 0.1f)
                        mScaleNormal[asset.index] = mWeightSum/asset.weights;
                    break;
            }
        }


    }

    void setTimeRange(DCScene scene) {
        int i=0;
        for (DCAsset asset : scene.assets) {
            if(mAssetWrapperList != null &&mAssetWrapperList.size() > i) {
                Log.e(TAG, "asset Mediaplayer settimerage " + asset.getTimeRange().startTime + " duration " + asset.getTimeRange().duration);
                mAssetWrapperList.get(i).getAsset().setTimeRange(asset.getTimeRange());
                i++;
            } else {
                break;
            }
        }

    }

    public List<DCAssetWrapper> getAssetWrappers() {
        return mAssetWrapperList;
    }

    void setPositionUpdateListener(OnPositionUpdateListener listener) {
        mPositionUpdateListener = listener;
    }

    void setAudioSampleReadyListener(OnAudioSampleReadyListener listener) {
        mOnAudioSampleReadyListener = listener;
    }

    void setOnCompletionListener(OnCompletionListener listener) {
        mOnCompletionListener = listener;
    }

    void setOnAllVideoFramesReadyListener(OnAllVideoFramesReadyListener listener) {
        mOnAllVideoFramesReadyListener = listener;
    }

    void prepare() throws IOException {
        for (DCAssetWrapper assetWrapper : mAssetWrapperList) {
            assetWrapper.prepare();
        }
        mIsCompleted = false;
    }

    void play() {
        mIsCompleted = false;
        mCurrentTimestamp = 0L;
        for (DCAssetWrapper assetWrapper : mAssetWrapperList) {
//            Log.d("sun","play---for-->"+assetWrapper.mAsset.index);
//            if(mAssetWrapperList.size()>2 && assetWrapper.mAsset.index == 0){//第一个不播放
//                continue;
//            }
            assetWrapper.play();
        }
        if (!mIsForExporting) {
            startPlayThread();
        }
    }

    void pause() {
        for (DCAssetWrapper assetWrapper : mAssetWrapperList) {
            assetWrapper.pause();
        }
    }

    void resume() {
        for (DCAssetWrapper assetWrapper : mAssetWrapperList) {
            assetWrapper.resume();
        }
    }

    void seekTo(long us, boolean playAfterSeeking) {
        mPlayAfterSeeking = playAfterSeeking;
        for (DCAssetWrapper assetWrapper : mAssetWrapperList) {
            assetWrapper.seekTo(us);
            assetWrapper.mFirstVideoFrameDone = false;
        }
    }

    void release() {
        mIsStopPlayback = true;
        if (mPlayThread != null) {
            mPlayThread.interrupt();
            mPlayThread = null;
        }

        if (mAssetWrapperList != null) {
            for (DCAssetWrapper assetWrapper : mAssetWrapperList) {
                assetWrapper.release();
            }
            mAssetWrapperList.clear();
        }

        mPositionUpdateListener = null;
        mOnAudioSampleReadyListener = null;
        mOnCompletionListener = null;
        mOnAllVideoFramesReadyListener = null;

        if (mAudioData != null) {
            mAudioData.clear();
        }
    }

    void setFPS(int fps) {
        if (fps <= 0) {
            throw new RuntimeException("FPS must be greater than  0.");
        }
        mFPS = fps;
    }

    long getDuration() {
        long max = 0;
        for (DCAssetWrapper assetWrapper : mAssetWrapperList) {
            DCAsset asset = assetWrapper.getAsset();
            if (asset.type != DCAsset.DCAssetTypeImage) {
                long d = asset.startTimeInScene + asset.getTimeRange().duration;
//                Log.d("tag","combinePreview--getduration"+d);
                if (d > max) {
                    max = d;
                }
            }
        }
        return max;
    }

    long getmCurrentTimestamp(){
        for (DCAssetWrapper assetWrapper : mAssetWrapperList) {
            DCAsset asset = assetWrapper.getAsset();

//            Log.d("tag","==---playTime--FramAvailable---TimeStamp"+time);
        }
        return mCurrentTimestamp;
    }

    void setExporting(boolean exporting) {
        mIsForExporting = exporting;
        for (DCAssetWrapper assetWrapper : mAssetWrapperList) {
            assetWrapper.setExporting(mIsForExporting);
        }
    }

    void setHasVideoTrack(boolean exporting) {
        mHasVideoTrack = exporting;
        for (DCAssetWrapper assetWrapper : mAssetWrapperList) {
            assetWrapper.setHasVideoTrack(mHasVideoTrack);
        }
    }

    void setHasAudioTrack(boolean exporting) {
        mHasAudioTrack = exporting;
        for (DCAssetWrapper assetWrapper : mAssetWrapperList) {
            assetWrapper.setHasAudioTrack(mHasAudioTrack);
        }
    }

    private  boolean AllFirstVideoFrameDone() {
        for (DCAssetWrapper wrapper : mAssetWrapperList) {
            if(!wrapper.mFirstVideoFrameDone && wrapper.getAsset().type == DCAsset.DCAssetTypeVideo) {
                return false;
            }
        }
        return true;
    }
    @Override
    synchronized
    public void OnVideoFrameReady(DCAssetWrapper assetWrapper, MediaCodec.BufferInfo bufferInfo) {
        Log.d(TAG,"==---OnVideoFrameReady>");
        if(!assetWrapper.mFirstVideoFrameDone) {
            assetWrapper.mFirstVideoFrameDone = true;
        }

        mAvailableVideoFrameCount++;
        int count = getActivatedVideoCount();
        if (mAvailableVideoFrameCount >= getActivatedVideoCount()) {
            if (mPositionUpdateListener != null) {
                long d = getDuration();
                if (d > 0) {
                    if (mIsForExporting) {
                        Log.d(TAG,AUDIO_RATIO+"AUDIO_RATIO==---OnVideoFrameReady>"+((float)mCurrentTimestamp / (float)d));
                        mPositionUpdateListener.OnPositionUpdate(AUDIO_RATIO + (1.0f - AUDIO_RATIO) * (float)mCurrentTimestamp / (float)d, mCurrentTimestamp);
                    } else {
                        Log.d("tag","==---playTime--FramAvailable---positionupdate-194>"+((float)mCurrentTimestamp / (float)d));
                        mPositionUpdateListener.OnPositionUpdate((float)mCurrentTimestamp / (float)d, mCurrentTimestamp);
                    }
                }
            }

            if(!AllFirstVideoFrameDone()) {
                return;
            }

            // only for exporting
            if (mIsForExporting && mOnAllVideoFramesReadyListener != null && mCurrentTimestamp >= 0) {
                mOnAllVideoFramesReadyListener.OnAllVideoFramesReady(this, mCurrentTimestamp);
            }

            if (mCurrentTimestamp < getDuration()) {
                mCurrentTimestamp += 1000 * 1000 / mFPS;
                if (mCurrentTimestamp >= getDuration()) {
                    mCurrentTimestamp = getDuration();
                }
            }

            mAvailableVideoFrameCount = 0;
            for (DCAssetWrapper wrapper : mAssetWrapperList) {
                DCAsset asset = wrapper.getAsset();
                long t = mCurrentTimestamp - asset.startTimeInScene + asset.getTimeRange().startTime;
                wrapper.setCurrentTimestamp(t);
                wrapper.needVideoFrame = true;
                wrapper.notifyNeedVideoFrameSync();
            }
        }
    }

    @Override
    synchronized
    public void OnAudioFrameReady(DCAssetWrapper assetWrapper, byte[] buffer, MediaCodec.BufferInfo bufferInfo) {
        Log.d(TAG,"==---OnAudioFrameReady>");
        mAvailableAudioFrameCount++;
        if (buffer != null) {
            mAudioData.add(new DCAssetVideoWrapper.AudioBufferInfo(assetWrapper.getAsset().getVolume(), buffer, bufferInfo.presentationTimeUs,-1));
        }

        if (mAvailableAudioFrameCount >= getActivatedAudioCount()) {
            // TODO: 2018/6/20  zhangxiao
//            if (mOnAudioSampleReadyListener != null && mIsForExporting) {
//                byte[] mixData = mixAudio(mAudioData);
//                mAudioData.clear();
//                if (mixData != null) {
//                    ByteBuffer byteBuffer = ByteBuffer.wrap(mixData);
//                    mOnAudioSampleReadyListener.OnAudioSampleReady(byteBuffer);
//                }
//            }

            if (mPositionUpdateListener != null && mIsForExporting) {
                long d = getDuration();
                if (d > 0) {
                    DCAsset asset = assetWrapper.getAsset();
                    long time = asset.startTimeInScene + bufferInfo.presentationTimeUs - asset.getTimeRange().startTime;
                    Log.e("recordActivitySdk","time_348:" + time);
                    Log.d("recordActivitySdk","==---playTime--FramAvailable---positionupdate-246>"+(AUDIO_RATIO * (float)time / (float)d));
                    Log.d("recordActivitySdk","==---OnAudioFrameReady>"+(AUDIO_RATIO * (float)time / (float)d));
                    mPositionUpdateListener.OnPositionUpdate(AUDIO_RATIO * (float)time / (float)d, time);
                }
            }

            mAvailableAudioFrameCount = 0;
            for (DCAssetWrapper wrapper : mAssetWrapperList) {
                if (!wrapper.isAudioFinished()) {
                    wrapper.needAudioFrame = true;
                }
            }
        }
    }

    @Override
    synchronized
    public void OnCompletion(DCAssetWrapper assetWrapper) {
        if (videoIsDone() && audioIsDone()) {
            if (mOnCompletionListener != null && !mIsCompleted) {
                mIsCompleted = true;
                mOnCompletionListener.OnCompletion(this);
            }
        }
    }

    @Override
    public void OnAudioCompletion(DCAssetWrapper assetWrapper) {
        if (getActivatedAudioCountByStatus() == 0) {
            if (mIsForExporting) {
                // TODO: 2018/6/20  zhangxiao
                final long d = 1024 * 1000000 / 44100;
                long t = 0L;
                while (!audioQueueIsEmpty()) {
                    for (DCAssetWrapper wrapper : mAssetWrapperList) {
                        if (wrapper.getAsset().type == DCAsset.DCAssetTypeVideo) {
                            DCAssetVideoWrapper videoWrapper = (DCAssetVideoWrapper)wrapper;
                            Queue<DCAssetVideoWrapper.AudioBufferInfo> queue = videoWrapper.mAudioSampleCache;
                            if (!queue.isEmpty() && queue.peek() != null && queue.peek().timestamp <= t) {
                                mAudioData.add(queue.poll());
                            }
                        }
                    }
                    if (mAudioData.size() > 0) {
                        byte[] mixData = mixAudioNew(mAudioData);
                        mAudioData.clear();
                        if (mixData != null) {
                            ByteBuffer byteBuffer = ByteBuffer.wrap(mixData);
                            mOnAudioSampleReadyListener.OnAudioSampleReady(byteBuffer);
                        }
                    }
                    t += d;
                    if (t > getDuration()) {
                        break;
                    }
                }
            }
        }
    }

    @Override
    synchronized
    public void OnSeekCompletion(DCAssetWrapper assetWrapper) {
        for (DCAssetWrapper wrapper : mAssetWrapperList) {
            if (!wrapper.mSeekIsCompleted) {
                return;
            }
        }
        if (mPlayAfterSeeking) {
            mPlayAfterSeeking = false;
            for (DCAssetWrapper wrapper : mAssetWrapperList) {
                wrapper.play();
            }
            if (!mIsForExporting) {
                startPlayThread();
            }
        }
    }

    private boolean audioQueueIsEmpty() {
        for (DCAssetWrapper wrapper : mAssetWrapperList) {
            if (wrapper.getAsset().type == DCAsset.DCAssetTypeVideo) {
                DCAssetVideoWrapper videoWrapper = (DCAssetVideoWrapper)wrapper;
                if (!videoWrapper.mAudioSampleCache.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private byte[] mixAudio(List<DCAssetVideoWrapper.AudioBufferInfo> audioData) {
        if (audioData.size() == 0) {
            return null;
        }

        if(audioData.size() == 1) {
            return audioData.get(0).bytes;
        }

        for(int rw = 0; rw < audioData.size(); ++rw){
            if(audioData.get(rw).bytes.length != audioData.get(0).bytes.length){
                return null;
            }
        }

        int row = audioData.size();
        int column = audioData.get(0).bytes.length / 2;
        short[][] sMulRoadAudioes = new short[row][column];

        for (int r = 0; r < row; ++r) {
            for (int c = 0; c < column; ++c) {
                sMulRoadAudioes[r][c] = (short) ((audioData.get(r).bytes[c * 2] & 0xff) | (audioData.get(r).bytes[c * 2 + 1] & 0xff) << 8);
            }
        }

        short[] sMixAudio = new short[column];
        int mixVal;
        int sr;
        for (int sc = 0; sc < column; ++sc) {
            mixVal = 0;
            sr = 0;
            for (; sr < row; ++sr) {
                mixVal += sMulRoadAudioes[sr][sc] * audioData.get(sr).volume;
            }
            sMixAudio[sc] = (short) (mixVal / row);
        }

        byte[] dest = new byte[column << 1];
        for (sr = 0; sr < column; ++sr) {
            dest[sr * 2] = (byte) (sMixAudio[sr] & 0x00FF);
            dest[sr * 2 + 1] = (byte) ((sMixAudio[sr] & 0xFF00) >> 8);
        }

        return dest;
    }

    private byte[] mixAudioNew(List<DCAssetVideoWrapper.AudioBufferInfo> audioData) {
        if (audioData.size() == 0) {
            return null;
        }

        if(audioData.size() == 1) {
            return audioData.get(0).bytes;
        }

        for(int rw = 0; rw < audioData.size(); ++rw){
            if(audioData.get(rw).bytes.length != audioData.get(0).bytes.length){
                return null;
            }
        }

        int row = audioData.size();
        int column = audioData.get(0).bytes.length / 2;
        short[][] sMulRoadAudioes = new short[row][column];

        int[] index = new int[row];

        for (int r = 0; r < row; ++r) {
            for (int c = 0; c < column; ++c) {
                sMulRoadAudioes[r][c] = (short) ((audioData.get(r).bytes[c * 2] & 0xff) | (audioData.get(r).bytes[c * 2 + 1] & 0xff) << 8);
            }
            index[r] = audioData.get(r).index;
        }



        short[] sMixAudio = new short[column];
        int sr = 0;

        calculate_scales(index,row,column);

        for (; sr < row; ++sr) {
            sMixAudio = mixVoice(sMixAudio,sMulRoadAudioes[sr],column,mInputScale[index[sr]] * audioData.get(sr).volume);
        }

        byte[] dest = new byte[column << 1];
        for (sr = 0; sr < column; ++sr) {
            dest[sr * 2] = (byte) (sMixAudio[sr] & 0x00FF);
            dest[sr * 2 + 1] = (byte) ((sMixAudio[sr] & 0xFF00) >> 8);
        }

        return dest;
    }

    void calculate_scales( int[] index,int row,int nb_samples)
    {
        float weight_sum = 0.f;
        int i;

        for (i = 0; i <row; i++) {
            weight_sum += mWeights[index[i]];
        }

        for (i = 0; i < row; i++) {

                if (mScaleNormal[index[i]] > weight_sum / mWeights[index[i]]) {
                    mScaleNormal[index[i]] -= ((mWeightSum / mWeights[index[i]]) / row) *
                            nb_samples / (dropout_transition * 44100);
                    mScaleNormal[index[i]] = Math.max(mScaleNormal[index[i]], weight_sum / mWeights[index[i]]);
                }
        }

        for (i = 0; i < row; i++) {
            if(mWeights[index[i]] >= 0.1f) {
                mInputScale[index[i]] = 1.0f / mScaleNormal[index[i]];
            } else {
                mInputScale[index[i]] = 0.0f;
            }
        }
    }


    private short[] mixVoice(short[] dst,short[] src,int len,double mul)
    {
        int i;
        for (i = 0; i < len; i++)
            dst[i] += src[i] * mul;
        return  dst;
    }

   private short[] mixTwoVoice(short[] voiceOne,short[] voiceTwo,int len)
   {
       long tempDownUpSideValue = 0;
       long tempUpSideDownValue = 0;
       short[] out = new short[len];

       for(int i=0;i<len ; i++)
       {
           int summedValue = voiceOne[i] + voiceTwo[i];

           if(-32768 < summedValue && summedValue < 32767)
           {
               //the value is within range -- good boy
           } else {
               //nasty calibration needed
               long tempCalibrateValue;
               tempCalibrateValue = Math.abs(summedValue) - (-32768); // here an optimization comes ;)

               if(summedValue < 0)
               {
                   //check the downside -- to calibrate
                   if(tempDownUpSideValue < tempCalibrateValue)
                       tempDownUpSideValue = tempCalibrateValue;
               } else {
                   //check the upside ---- to calibrate
                   if(tempUpSideDownValue < tempCalibrateValue)
                       tempUpSideDownValue = tempCalibrateValue;
               }
           }
       }

       downSideUpValue = tempDownUpSideValue;
       upSideDownValue = tempUpSideDownValue;

       for(int i=0;i<len;i++)
       {
           int summedValue = voiceOne[i] + voiceTwo[i];

           if(summedValue < 0)
           {
               out[i] =(short)( summedValue + downSideUpValue);
           } else if(summedValue > 0) {
               out[i] = (short)(summedValue - upSideDownValue);
           } else {
               out[i] = (short)summedValue;
           }
       }
       return  out;
   }

    synchronized int getActivatedVideoCount() {
        int count = 0;
        for (DCAssetWrapper assetWrapper : mAssetWrapperList) {
            if (!assetWrapper.isVideoFinished()) {
                count++;
            }
        }
        return count;
    }

    private synchronized boolean audioIsDone() {
        for (DCAssetWrapper assetWrapper : mAssetWrapperList) {
            if (!assetWrapper.mAudioIsDone) {
                return false;
            }
        }
        return true;
    }

    private synchronized boolean videoIsDone() {
        for (DCAssetWrapper assetWrapper : mAssetWrapperList) {
            if (!assetWrapper.mVideoIsDone) {
                return false;
            }
        }
        return true;
    }

    private synchronized int getActivatedAudioCount() {
        int count = 0;
        for (DCAssetWrapper assetWrapper : mAssetWrapperList) {
            if (!assetWrapper.isAudioFinished() && assetWrapper.isCurrentFrameAudio() && !((DCAssetVideoWrapper)assetWrapper).mCurrentAudioFrameIsDone) {
                count++;
            }
        }
        return count;
    }

    private synchronized int getActivatedAudioCountByStatus() {
        int count = 0;
        for (DCAssetWrapper assetWrapper : mAssetWrapperList) {
            if (assetWrapper.mAudioStatus < assetWrapper.FINISH && (assetWrapper.getAsset().type == DCAsset.DCAssetTypeVideo || assetWrapper.getAsset().type == DCAsset.DCAssetTypeAudio)) {
                count++;
            }
        }
        return count;
    }

    void onFrameAvailable() {
        for (DCAssetWrapper assetWrapper : mAssetWrapperList) {
            if (assetWrapper != null) {
                assetWrapper.onVideoFrameAvailable();
            }
        }
    }

    public long getCurrenttime(){
        long timestamp = 0;
        Log.d("sun","==---playTime--FramAvailable-时间-size:" + mAssetWrapperList.size());
        for (DCAssetWrapper wrapper : mAssetWrapperList) {
            DCAsset asset = wrapper.getAsset();
            if (asset.type == DCAsset.DCAssetTypeAudio) {
                DCAssetVideoWrapper videoWrapper = (DCAssetVideoWrapper)wrapper;
                timestamp = asset.startTimeInScene + videoWrapper.getPlayerTimestamp() - asset.getTimeRange().startTime;
                break;
            } else if (asset.type == DCAsset.DCAssetTypeVideo) {
                DCAssetVideoWrapper videoWrapper = (DCAssetVideoWrapper)wrapper;
                long time = asset.startTimeInScene + videoWrapper.getPlayerTimestamp() - asset.getTimeRange().startTime;
                Log.d("sun",wrapper.toString()+"==---playTime--FramAvailable-时间:" + time);
                if (timestamp < time) {
                    timestamp = time;
                }
            }
        }
        return timestamp;
    }

    private void startPlayThread() {
        if (mPlayThread != null) {
            mPlayThread.interrupt();
            mPlayThread = null;
        }
        mPlayThread = new Thread("PlayThread-" + System.currentTimeMillis()) {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {
                while (!mIsStopPlayback) {
                    try {
                        int count = 0;
                        long atimestamp = -1L;
                        long vtimestamp = -1L;
                        for (DCAssetWrapper wrapper : mAssetWrapperList) {
                            DCAsset asset = wrapper.getAsset();
                            if (asset.type == DCAsset.DCAssetTypeAudio) {
                                DCAssetVideoWrapper videoWrapper = (DCAssetVideoWrapper)wrapper;
                                atimestamp = asset.startTimeInScene + videoWrapper.getPlayerTimestamp() - asset.getTimeRange().startTime;
                            }else if (asset.type == DCAsset.DCAssetTypeVideo) {
                                DCAssetVideoWrapper videoWrapper = (DCAssetVideoWrapper)wrapper;
                                long time = asset.startTimeInScene + videoWrapper.getPlayerTimestamp() - asset.getTimeRange().startTime;
                                if (vtimestamp < time) {
                                    vtimestamp = time;
                                }
                            }

                            if (!wrapper.mPaused && wrapper.isVideoFinished() && wrapper.isAudioFinished()) {
                                wrapper.pause();
                            }
                            if (wrapper.mPaused && wrapper.isVideoFinished() && wrapper.isAudioFinished()) {
                                count++;
                            }
                        }
                        if (mPositionUpdateListener != null) {
                            float d = getDuration();
                            if (d > 0) {
                                long timestamp = atimestamp >= 0 ? atimestamp : vtimestamp;
                                mPositionUpdateListener.OnPositionUpdate(timestamp / d, timestamp);
                            }
                        }
                        if (count == mAssetWrapperList.size()) {
                            if (mOnCompletionListener != null) {
                                mOnCompletionListener.OnCompletion(DCSceneWrapper.this);
                            }
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        sleep(10);
                    } catch (InterruptedException interruptedException) {
                        // safe ignore
                    }
                }
            }
        };
        mPlayThread.start();
    }
}
