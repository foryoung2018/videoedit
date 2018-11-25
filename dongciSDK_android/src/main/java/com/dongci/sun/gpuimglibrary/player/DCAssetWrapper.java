package com.dongci.sun.gpuimglibrary.player;

import android.graphics.Bitmap;
import android.media.MediaCodec;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.RequiresApi;
import android.view.Surface;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by zhangxiao on 2018/6/5.
 *
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class DCAssetWrapper {

    interface OnVideoFrameReadyListener
    {
        void OnVideoFrameReady(DCAssetWrapper assetWrapper, MediaCodec.BufferInfo bufferInfo);
    }

    interface OnAudioFrameReadyListener
    {
        void OnAudioFrameReady(DCAssetWrapper assetWrapper, byte[] buffer, MediaCodec.BufferInfo bufferInfo);
    }

    interface OnCompletionListener
    {
        void OnCompletion(DCAssetWrapper assetWrapper);
    }

    interface OnAudioCompletionListener {
        void OnAudioCompletion(DCAssetWrapper assetWrapper);
    }

    interface OnSeekCompletionListener {
        void OnSeekCompletion(DCAssetWrapper assetWrapper);
    }

    public DCAsset mAsset;
    volatile boolean needAudioFrame = true;
    volatile boolean needVideoFrame = true;


    OnVideoFrameReadyListener mOnVideoFrameReadyListener = null;
    OnAudioFrameReadyListener mOnAudioFrameReadyListener = null;
    OnCompletionListener mOnCompletionListener = null;
    OnAudioCompletionListener mOnAudioCompletionListener = null;
    OnSeekCompletionListener mOnSeekCompletionListener = null;

    boolean mIsForExporting = false;
    boolean mHasVideoTrack = true;
    boolean mHasAudioTrack = true;

    volatile boolean mVideoIsDone;
    volatile boolean mAudioIsDone;
    volatile boolean mSeekIsCompleted;
    volatile boolean mNeedToSupplyVideoFrame;

    volatile boolean mFirstVideoFrameDone;

    long mCurrentTimestamp;
    public volatile boolean mPaused;

    public static final int OPEN = 0;
    public static final int PREPARED = 1;
    public static final int PAUSE = 2;
    public static final int RESUME = 3;
    public static final int PLAYING = 4;
    public static final int FINISH = 5;
    public static final int CLOSE = 6;

    @IntDef({OPEN, PREPARED, PAUSE, RESUME, PLAYING, FINISH,CLOSE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PLAYSTATUS {
    }

    @PLAYSTATUS
    volatile int     mAudioStatus;


    DCAssetWrapper(DCAsset asset) {
        mAsset = asset;
        mVideoIsDone = true;
        mAudioIsDone = true;
        mSeekIsCompleted = true;
        mNeedToSupplyVideoFrame = false;
        mAudioStatus  = OPEN;
    }

    public DCAsset getAsset() {
        return mAsset;
    }

    void setSurface(Surface surface) throws NullPointerException{
    }

    void notifyNeedVideoFrameSync(){

    }

    void setOnVideoFrameReadyListener(OnVideoFrameReadyListener listener) {
        mOnVideoFrameReadyListener = listener;
    }

    void setOnAudioFrameReadyListener(OnAudioFrameReadyListener listener) {
        mOnAudioFrameReadyListener = listener;
    }

    void setOnCompletionListener(OnCompletionListener listener) {
        mOnCompletionListener = listener;
    }

    void setOnAudioCompletionListener(OnAudioCompletionListener listener) {
        mOnAudioCompletionListener = listener;
    }

    void setOnSeekCompletionListener(OnSeekCompletionListener listener) {
        mOnSeekCompletionListener = listener;
    }

    int getWidth(){
        return 0;
    }

    int getHeight(){
        return 0;
    }

    void setExporting(boolean exporting) {
        mIsForExporting = exporting;
    }

    void setHasVideoTrack(boolean exporting) {
        mHasVideoTrack = exporting;
    }

    void setHasAudioTrack(boolean exporting) {
        mHasAudioTrack = exporting;
    }

    void prepare() throws IOException{
    }

    void play() {
        mPaused = false;
    }

    void pause() {
        mPaused = true;
    }

    void resume() {
        mPaused = false;
    }

    void seekTo(long us) {
    }

    void release() {
        mOnVideoFrameReadyListener = null;
        mOnAudioFrameReadyListener = null;
        mOnCompletionListener = null;
        mOnAudioCompletionListener = null;
        mOnSeekCompletionListener = null;
    }

    boolean isVideoFinished() {
        return true;
    }

    boolean isAudioFinished() {
        return true;
    }

    boolean isCurrentFrameAudio() {
        return true;
    }

    void onVideoFrameAvailable() {
    }

    public Bitmap getBitmap(int index) {
        return null;
    }

    public int getBitmapCount() {
        return 0;
    }

    void setCurrentTimestamp(long timestamp) {
        mCurrentTimestamp = timestamp;
    }

    public long getmCurrentTimestamp1(){
        return mCurrentTimestamp;
    }

    void setVolume(float volume) {}
}
