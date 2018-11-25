package com.dongci.sun.gpuimglibrary.player;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by zhangxiao on 2018/6/6.
 *
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class DCMediaFileWriter {

    private class AudioSampleCacheItem {
        MediaCodec.BufferInfo bufferInfo;
        ByteBuffer buffer;
    }

    private static final String TAG = "DCMediaFileWriter";
    private static final int INVALID_TRACK_INDEX = -1;
    private static final int TIMEOUT_USEC = 10000;
    private static final boolean VERBOSE = true;

    private MediaCodec mVideoEncoder;
    private DCInputSurface mInputSurface;
    private MediaCodec.BufferInfo mVideoBufferInfo;
    private ByteBuffer[] mVideoEncoderOutputBuffer;
    private int mFrameRate;

    private MediaCodec mAudioEncoder;
    private ByteBuffer[] mAudioEncoderInputBuffer;
    private ByteBuffer[] mAudioEncoderOutputBuffer;
    private int mAudioSampleIndex;
    private int mSampleRate;

    private MediaMuxer mMuxer;
    private int mVideoTrackIndex;
    private int mAudioTrackIndex;
    private int mVideoFrameIndex;
    private long mAudioPresentationTimeUs;

    private String mOutputPath;
    private boolean mIsOpened;

    private MediaFormat mVideoFormat = null;
    private MediaFormat mAudioFormat = null;

    Queue<ByteBuffer> mAudioSampleCache = new LinkedList<>();



    private static final int MuxerNull = 0;
    private static final int MuxerInited = 1;
    private static final int MuxerStarted = 2;
    private static final int MuxerWrittenAudio = 4;
    private static final int MuxerWrittenVideo = 8;
    private static final int MuxerWrittenAll   = 14;
    @IntDef({MuxerNull,MuxerInited, MuxerStarted, MuxerWrittenAudio,MuxerWrittenVideo,MuxerWrittenAll})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MuxerStatus {
    }

    private @MuxerStatus int mMuxerStatus;

    DCMediaFileWriter(String outputPath) {
        mOutputPath = outputPath;
        mIsOpened = false;
        mMuxerStatus = MuxerNull;
    }

    void setup(MediaFormat videoOutputFormat,
                      MediaFormat audioOutputFormat, int videoRotaion)
            throws IOException, IllegalStateException {

        if (mIsOpened){
            throw new IllegalStateException("writer is already opened");
        }


        mMuxer = new MediaMuxer(mOutputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        if (videoRotaion >= 0){
            mMuxer.setOrientationHint(videoRotaion);
        }
        mMuxerStatus = MuxerInited;

        mVideoTrackIndex = INVALID_TRACK_INDEX;
        mAudioTrackIndex = INVALID_TRACK_INDEX;

        mVideoFormat = videoOutputFormat;
        mAudioFormat = audioOutputFormat;

        if (mVideoFormat != null) {
            setupVideoEncoder(videoOutputFormat);
        }
        if (mAudioFormat != null) {
            setupAudioEncoder(audioOutputFormat);
        }

        mVideoFrameIndex = 0;
        mAudioSampleIndex = 0;
        mAudioPresentationTimeUs = 0;
        mIsOpened = true;
    }

    boolean isOpened() {
        return mIsOpened;
    }

    void writeOneVideoFrameFromSurface(long timestamp) {
        if (mVideoFormat == null) {
            return;
        }
        if (!mIsOpened) {
            throw new IllegalStateException("writer is not ready for writing, setup it first");
        }
        writeVideoInternal(false, timestamp);
    }
    void writeOneAudioSample(ByteBuffer audioSample) {
        if (mAudioFormat == null) {
            return;
        }
        if (!mIsOpened) {
            throw new IllegalStateException("writer is not ready for writing, setup it first");
        }

        if(mMuxerStatus < MuxerStarted)  {
            mAudioSampleCache.add(audioSample);
            return;
        }

        if (!mAudioSampleCache.isEmpty()) {
            while(!mAudioSampleCache.isEmpty() && mAudioSampleCache.peek() != null) {
                writeAudioInternal(mAudioSampleCache.poll(),false);
            }
        }
        writeAudioInternal(audioSample, false);
    }
    void finish(){
        if (mIsOpened) {
            if (mAudioFormat != null) {
                writeAudioInternal(null, true);
            }
            if (mVideoFormat != null) {
                writeVideoInternal(true, -1);
            }
        }

        if (mVideoEncoder != null) {
            mVideoEncoder.stop();
            mVideoEncoder.release();
            mVideoEncoder = null;
        }

        if (mAudioEncoder != null) {
            mAudioEncoder.stop();
            mAudioEncoder.release();
            mAudioEncoder = null;
        }

        if (mMuxer != null){
            if(mMuxerStatus >= MuxerWrittenAll) {
                mMuxer.stop();
                mMuxer.release();
            }

            mMuxer = null;
        }
        mMuxerStatus = MuxerNull;
        mIsOpened = false;
        mAudioSampleCache.clear();

    }

    void release(){

        if (mIsOpened) {
            writeAudioInternal(null, true);
            writeVideoInternal(true, -1);
        }

        if (mVideoEncoder != null) {
            mVideoEncoder.stop();
            mVideoEncoder.release();
            mVideoEncoder = null;
        }

        if (mAudioEncoder != null) {
            mAudioEncoder.stop();
            mAudioEncoder.release();
            mAudioEncoder = null;
        }

        if (mMuxer != null){
            if(mMuxerStatus >= MuxerWrittenAll) {
                mMuxer.stop();
                mMuxer.release();
            }

            mMuxer = null;
        }

        if (mInputSurface != null) {
            mInputSurface.release();
            mInputSurface = null;
        }
        mMuxerStatus = MuxerNull;

        mVideoBufferInfo = null;
        mAudioSampleCache.clear();
    }

    private void setupVideoEncoder(MediaFormat videoOutputFormat) throws IOException {

        mFrameRate = videoOutputFormat.getInteger(MediaFormat.KEY_FRAME_RATE);
        mVideoBufferInfo = new MediaCodec.BufferInfo();
        if (VERBOSE) Log.d(TAG, "video output format: " + videoOutputFormat);
        mVideoEncoder = MediaCodec.createEncoderByType(
                videoOutputFormat.getString(MediaFormat.KEY_MIME));

        mVideoEncoder.configure(videoOutputFormat,
                null,
                null,
                MediaCodec.CONFIGURE_FLAG_ENCODE);
        mInputSurface = new DCInputSurface(mVideoEncoder.createInputSurface());
        mInputSurface.makeCurrent();
        mVideoEncoder.start();
        mVideoEncoderOutputBuffer = mVideoEncoder.getOutputBuffers();
    }

    private void setupAudioEncoder(MediaFormat audioOutputFormat)
            throws IOException, RuntimeException {
        if (VERBOSE) Log.d(TAG, "audio output format: " + audioOutputFormat);
        mSampleRate = audioOutputFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
        mAudioEncoder = MediaCodec.createEncoderByType(
                audioOutputFormat.getString(MediaFormat.KEY_MIME));
        mAudioEncoder.configure(audioOutputFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mAudioEncoder.start();
        mAudioEncoderInputBuffer = mAudioEncoder.getInputBuffers();
        mAudioEncoderOutputBuffer = mAudioEncoder.getOutputBuffers();

        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int audioEncoderStatus = mAudioEncoder.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC);

        if(audioEncoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            MediaFormat format = mAudioEncoder.getOutputFormat();
            mAudioTrackIndex = mMuxer.addTrack(format);
            if ((mVideoTrackIndex != INVALID_TRACK_INDEX
                    && mAudioTrackIndex != INVALID_TRACK_INDEX) || (mAudioTrackIndex != INVALID_TRACK_INDEX && mVideoFormat == null)) {
                mMuxer.start();
                mMuxerStatus = MuxerStarted;
                Log.d(TAG, "Audio Muxer Start " + format);
            }
        }
    }


    private MediaCodecInfo selectCodec(String mimeType) {
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (!codecInfo.isEncoder() ||
                    !codecInfo.getName().startsWith("OMX.")) {
                continue;
            }
            String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (types[j].equalsIgnoreCase(mimeType)) {
                    return codecInfo;
                }
            }
        }
        return null;
    }

    private void writeAudioInternal(ByteBuffer audioSample, boolean isEOS){
        feedAudioEncoder(audioSample, isEOS);
        drainAudioEncoder(isEOS);
    }

    private void feedAudioEncoder(ByteBuffer audioSample, boolean isEOS) {
        Log.d(TAG, "feedAudioEncoder: " + mAudioPresentationTimeUs);
        int inputBufferIndex = mAudioEncoder.dequeueInputBuffer(TIMEOUT_USEC);
        if (inputBufferIndex >= 0) {
            if (isEOS){
                mAudioEncoder.queueInputBuffer(
                        inputBufferIndex,
                        0 /* offset */,
                        0 /* size */,
                        mAudioPresentationTimeUs /* timeUs */,
                        MediaCodec.BUFFER_FLAG_END_OF_STREAM);
            }else{
                ByteBuffer buffer = mAudioEncoderInputBuffer[inputBufferIndex];
                buffer.clear(); // for API level 16
                buffer.put(audioSample);
                mAudioEncoder.queueInputBuffer(inputBufferIndex, 0, audioSample.limit(),
                        mAudioPresentationTimeUs, 0);
                if (VERBOSE) {
                    Log.d(TAG, "feed audio encoder "
                            + audioSample.limit()
                            + " bytes present time "
                            + mAudioPresentationTimeUs);
                }
                // TODO: change constant.
                mAudioPresentationTimeUs += audioSample.limit()/2 * 1000000 / mSampleRate;
            }
        }
    }

    private void drainAudioEncoder(boolean isEOS) throws RuntimeException {
        while(true){
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int audioEncoderStatus = mAudioEncoder.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC);
            if (audioEncoderStatus >= 0){
                ByteBuffer encodedData = mAudioEncoderOutputBuffer[audioEncoderStatus];
                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG)!=0) {
                    if (VERBOSE) Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
                    bufferInfo.size = 0;
                }
                if (bufferInfo.size != 0){
                    encodedData.position(bufferInfo.offset);
                    encodedData.limit(bufferInfo.offset + bufferInfo.size);
                    AudioSampleCacheItem cacheItem = new AudioSampleCacheItem();
                    cacheItem.bufferInfo = bufferInfo;
                    cacheItem.buffer = ByteBuffer.allocate(encodedData.limit());
                    cacheItem.buffer.put(encodedData);

                    if(mMuxerStatus >= MuxerStarted) {
                        mMuxer.writeSampleData(mAudioTrackIndex, cacheItem.buffer, cacheItem.bufferInfo);
                        if((mMuxerStatus & MuxerWrittenAudio) == 0) {
                            mMuxerStatus += MuxerWrittenAudio;
                        }
                    }
                    if (VERBOSE) {
                        Log.d(TAG, "audio encoder output sample index "
                                + mAudioSampleIndex
                                + " "
                                + bufferInfo.size
                                + " bytes");
                    }
                    mAudioSampleIndex++;
                }
                mAudioEncoder.releaseOutputBuffer(audioEncoderStatus, false);
                if (isEOS){
                    if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        if (VERBOSE) Log.d(TAG, "audio encoder output EOS.");
                        return;
                    }else{
                        continue; // read all buffered encoded data
                    }
                }else{
                    if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        Log.w(TAG, "reached end of audio stream unexpectedly");
                    }
                    return;
                }
            }else if (audioEncoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                if (VERBOSE) Log.d(TAG, "audio encoder output buffer is not available ");
                return;
            } else if (audioEncoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // should happen before receiving buffers, and should only happen once
                if (mAudioTrackIndex != -1) {
                    throw new RuntimeException("audio encoder output format changed twice");
                }
                //TODO: add audioTrack in "setupAudioEncoder"
               /* MediaFormat format = mAudioEncoder.getOutputFormat();
                mAudioTrackIndex = mMuxer.addTrack(format);
                if (mVideoTrackIndex != INVALID_TRACK_INDEX
                        && mAudioTrackIndex != INVALID_TRACK_INDEX) {
                    mMuxer.start();
                }
                Log.d(TAG, "audio encoder output format changed " + format);*/
            } else if (audioEncoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                mAudioEncoderOutputBuffer = mAudioEncoder.getOutputBuffers();
                if (VERBOSE) Log.d(TAG, "audio encoder output buffers changed");
            } else {
                assert(false);
                if (VERBOSE) Log.d(TAG, "unexpected audio encoder status");
            }
        }
    }

    private void writeVideoInternal(boolean isEOS, long timestamp) {
        feedVideoEncoder(isEOS, timestamp);
        drainVideoEncoder(isEOS);
    }

    private void feedVideoEncoder(boolean isEOS, long timestamp) {
        if (isEOS) {
            mVideoEncoder.signalEndOfInputStream();
        }else{
            long presentationTimeUs = timestamp;
            mInputSurface.setPresentationTime(presentationTimeUs * 1000);
            mInputSurface.swapBuffers();
        }
    }

    private void drainVideoEncoder(boolean isEOS) throws RuntimeException {
        while (true) {
            int videoEncoderStatus = mVideoEncoder.dequeueOutputBuffer(mVideoBufferInfo, TIMEOUT_USEC);
            if (videoEncoderStatus >= 0) {
                ByteBuffer encodedData = mVideoEncoderOutputBuffer[videoEncoderStatus];
                if ((mVideoBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG)!=0) {
                    if (VERBOSE) Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
                    mVideoBufferInfo.size = 0;
                }
                if (mVideoBufferInfo.size != 0) {

                    encodedData.position(mVideoBufferInfo.offset);
                    encodedData.limit(mVideoBufferInfo.offset + mVideoBufferInfo.size);
                    if(mMuxerStatus >= MuxerStarted) {
                        mMuxer.writeSampleData(mVideoTrackIndex, encodedData, mVideoBufferInfo);
                        if((mMuxerStatus & MuxerWrittenVideo) == 0) {
                            mMuxerStatus += MuxerWrittenVideo;
                        }
                    }
                    if (VERBOSE) {
                        Log.d(TAG, "write video frame present time "
                                + mVideoBufferInfo.presentationTimeUs/1000
                                + " ms"
                                + mVideoBufferInfo.size
                                + " bytes");
                    }
                }
                mVideoEncoder.releaseOutputBuffer(videoEncoderStatus, false);
                if (isEOS){
                    if((mVideoBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0){
                        if (VERBOSE) Log.d(TAG, "video encoder output EOS.");
                        return;
                    }else{
                        continue;
                    }
                }else{
                    if ((mVideoBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        Log.w(TAG, "reached end of video stream unexpectedly");
                    }
                    return;
                }
            }else if (videoEncoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                if (VERBOSE) Log.d(TAG, "no output from video encoder available");
                return;
            } else if (videoEncoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                if (VERBOSE) Log.d(TAG, "video encoder output buffers changed");
                mVideoEncoderOutputBuffer = mVideoEncoder.getOutputBuffers();
            } else if (videoEncoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // should happen before receiving buffers, and should only happen once
                if (mVideoTrackIndex != INVALID_TRACK_INDEX) {
                    throw new RuntimeException("video encoder output format changed twice");
                }
                MediaFormat newFormat = mVideoEncoder.getOutputFormat();
                mVideoTrackIndex = mMuxer.addTrack(newFormat);
                if ((mVideoTrackIndex != INVALID_TRACK_INDEX
                        && mAudioTrackIndex != INVALID_TRACK_INDEX) || (mVideoTrackIndex != INVALID_TRACK_INDEX && mAudioFormat == null)) {
                    mMuxer.start();
                    mMuxerStatus = MuxerStarted;
                    if (mAudioFormat == null) {
                        mMuxerStatus += MuxerWrittenAudio;
                    }
                }

                if (VERBOSE) Log.d(TAG, "video encoder output format changed: " + newFormat);
            }else {
                if (VERBOSE) Log.d(TAG, "unexpected video encoder status");
            }
        }
    }

    private long computePresentationTimeUs(int frameIndex) {
        return (long)(frameIndex * 1000000.0 / mFrameRate);
    }
}
