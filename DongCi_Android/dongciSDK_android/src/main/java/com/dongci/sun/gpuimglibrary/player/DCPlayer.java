package com.dongci.sun.gpuimglibrary.player;


import android.graphics.SurfaceTexture;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.dongci.sun.gpuimglibrary.api.apiTest.KLog;
import com.dongci.sun.gpuimglibrary.common.SLVideoTool;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class DCPlayer implements
        DCSceneWrapper.OnPositionUpdateListener,
        DCGLRenderer.OnVideoFrameReadyListener,
        DCSceneWrapper.OnAudioSampleReadyListener,
        DCSceneWrapper.OnCompletionListener,
        DCSceneWrapper.OnAllVideoFramesReadyListener {

    // interface
    public interface OnPreparedListener {
        void onPrepared(DCPlayer player);
    }

    public interface OnCompletionListener {
        void onCompletion(DCPlayer player);
    }

    public interface OnPositionUpdateListener {
        void onPositionUpdate(DCPlayer player, float progress, long timeStamp);
    }

    private enum PlayerState {
        Idle,
        Initialized,
        Preparing,
        Prepared,
        Started,
        Paused,
        Stopped,
        PlaybackCompleted,
    }

    private static final int MessageTypePlay = 0;
    private static final int MessageTypePause = 1;
    private static final int MessageTypeCompletion = 2;
    private static final int MessageTypeVideoFrameAvailable = 3;
    private static final int MessageTypeWriterIsFinished = 4;

    private static final String TAG = "DCPlayer";

    private static final String OUTPUT_VIDEO_MIME = "video/avc";
    private static final String OUTPUT_AUDIO_MIME = "audio/mp4a-latm";
    private static final int IFRAME_INTERVAL = 2; // sync frame every 2 second

    private volatile PlayerState mState;
    private List<DCSceneWrapper> mSceneWrapperList;

    private boolean mAutoPlay;

    private OnPositionUpdateListener mOnPositionUpdateListener;
    private OnCompletionListener mOnCompletionListener;
    private OnPreparedListener mOnPreparedListener;

    private int mVideoWidth;
    private int mVideoHeight;

    private boolean mRepeat = false;
    private DCGLRenderer mGLRenderer;

    private DCMediaFileWriter mMediaFileWriter;
    boolean mIsForExporting;
    private int mFPS = 24;
    private boolean mIsPrepare = false;
    private SurfaceTexture mSurfaceTexture = null;

    private int mWidth ;
    private int mHeight;

    //endregion
    private static int count;
    private static int countExport;
    private static int c;
    private boolean isExport;

//    static DCPlayer dcPlayer;
    /**
     * 获取Player
     * @param surfaceTexture
     * @param width
     * @param height
     * @param fps
     * @param autoplay
     * @return
     */
//    public static DCPlayer getInstance(SurfaceTexture surfaceTexture, int width, int height, int fps, boolean autoplay){
//        if(dcPlayer!=null){
//            dcPlayer.release();
//            dcPlayer = null;
//        }
//        dcPlayer = new DCPlayer(surfaceTexture,width,height,fps,autoplay);
//        return dcPlayer;
//    }
//
//    public static DCPlayer getInstance(int width, int height){
//        if(dcPlayer!=null){
//            dcPlayer.release();
//            dcPlayer = null;
//        }
//        dcPlayer = new DCPlayer(width,height);
//        return dcPlayer;
//    }



    // public methods
    public DCPlayer(SurfaceTexture surfaceTexture, int width, int height, int viewWidth, int viewHeight, int fps, boolean autoplay) {
        changeStateTo(PlayerState.Idle);
        mAutoPlay = autoplay;
        mFPS = fps;
        setSurfaceTexture(surfaceTexture, width, height, viewWidth, viewHeight);
        mSurfaceTexture = surfaceTexture;
        mWidth = width;
        mHeight = height;
        count++;
        Log.d(TAG,mIsForExporting+"realease--init-playerCount>"+count+"countExport:"+countExport);
    }

    public DCPlayer(int width, int height) {
        changeStateTo(PlayerState.Idle);
        setSurfaceTexture(width, height);
        mWidth = width;
        mHeight = height;
        mIsForExporting = true;
        countExport++;
        Log.d(TAG,mIsForExporting+"realease--init-playerCount>"+count+"countExport:"+countExport);
    }

//    public List<DCSceneWrapper> getSceneWrapper(){
//        return mSceneWrapperList;
//    }

    public void setIsExport(boolean export) {
        mIsForExporting = export;
    }

    public void setOnPositionUpdateListener(OnPositionUpdateListener listener) {
        mOnPositionUpdateListener = listener;
    }

    public void setOnCompletionListener(OnCompletionListener listener) {
        mOnCompletionListener = listener;
    }

    public void setOnPreparedListener(OnPreparedListener listener) {
        mOnPreparedListener = listener;
    }

    public void setScenes(List<DCScene> scenes) {
//        KLog.i("scences--getVolume>-pre");
        mSceneWrapperList = new ArrayList<>();
        for (DCScene scene : scenes) {
            DCSceneWrapper sceneWrapper = new DCSceneWrapper();
            sceneWrapper.setScene(scene);
            for(DCAsset dcAsset:scene.assets){
//                KLog.i("scences--getVolume>"+dcAsset.getVolume());
            }
            sceneWrapper.setPositionUpdateListener(this);
            sceneWrapper.setAudioSampleReadyListener(this);
            sceneWrapper.setOnCompletionListener(this);
            sceneWrapper.setOnAllVideoFramesReadyListener(this);
            sceneWrapper.setFPS(mFPS);
            sceneWrapper.setExporting(mIsForExporting);
            mSceneWrapperList.add(sceneWrapper);
        }
        changeStateTo(PlayerState.Initialized);
    }

    public void setTimeRange(List<DCScene> scenes) {
        int i =0;
        for (DCScene scene : scenes) {
            if(mSceneWrapperList != null &&mSceneWrapperList.size() > i) {
                if(mSceneWrapperList.get(i)!=null)
                    mSceneWrapperList.get(i).setTimeRange(scene);
                i++;
            } else {
                break;
            }
        }
    }

    public void setRepeat(boolean repeat) {
        mRepeat = repeat;
    }

    public int getVideoWidth() {
        return mVideoWidth;
    }

    public int getVideoHeight() {
        return mVideoHeight;
    }

    public void prepare() throws IllegalStateException, IOException{
        if (!check()) {
            return;
        }
        if(mState != PlayerState.Initialized
                && mState != PlayerState.Stopped) {
            return;
        }
        changeStateTo(PlayerState.Preparing);
        prepareInternal();
        Log.d("DCplayerManager", "=======setScenceAndPrepare-startThread==prepare--success");
    }

    public void play() throws IllegalStateException{
        if (!check()) {
            return;
        }
        if (mIsForExporting && mMediaFileWriter == null) {
            return;
        }
        if (mState == PlayerState.Started) {
            return;
        }
        if (mState == PlayerState.Prepared ||
                mState == PlayerState.Paused ||
                mState == PlayerState.PlaybackCompleted) {
            getSceneWrapper().play();
        }
        changeStateTo(PlayerState.Started);
        Log.d(TAG, "play");
    }

    public void pause() throws IllegalStateException{
        if (!check()) {
            return;
        }
        if(mState == PlayerState.Paused){
            return;
        }

        if (mState == PlayerState.Started && getSceneWrapper() != null) {
            getSceneWrapper().pause();
            changeStateTo(PlayerState.Paused);
        }
    }

    public void resume() {
        if (!check()) {
            return;
        }
        if (mState == PlayerState.Started) {
            return;
        }

        if (mState == PlayerState.Paused ||
                mState == PlayerState.Prepared) {
            getSceneWrapper().resume();
            changeStateTo(PlayerState.Started);
            Log.d(TAG, "resume");
        }
    }

    /**
     * 重置
     */
    public void reset() {
        release();
    }

    public long getCurrentTime(){
        return check() ? getSceneWrapper().getCurrenttime() : 0;
    }

    public long getDuration(){
        return check() ? getSceneWrapper().getDuration() : 0;
    }

    /**
     * @param usec             微秒
     * @param playAfterSeeking
     */
    public void seekTo(long usec, boolean playAfterSeeking){
        if (!check()) {
            return;
        }
        if(usec < 0){
            usec = 0;
        }
        getSceneWrapper().seekTo(usec, playAfterSeeking);
        Log.d("sun","seekto---dcplayer-->"+usec+"play"+playAfterSeeking);
        if(playAfterSeeking)
            changeStateTo(PlayerState.Started);
    }

    public void seekTo(float progress) {
        long timestamp = (long) (progress * getDuration());
        seekTo(timestamp, false);
    }

    public boolean isPlaying() {
        return mState == PlayerState.Started;
    }

    // export
    public void export(DCMediaInfoExtractor.MediaInfo outputFileInfo) {
        if (!check()) {
            return;
        }
        if (!mIsForExporting) {
            return;
        }

        if (mState != PlayerState.Prepared &&
                mState != PlayerState.PlaybackCompleted) {
            return;
        }

        mSceneWrapperList.get(0).setExporting(true);
        mSceneWrapperList.get(0).setHasVideoTrack(outputFileInfo.videoInfo != null);
        mSceneWrapperList.get(0).setHasAudioTrack(outputFileInfo.audioInfo != null);

        getSceneWrapper().setExporting(true);
        getSceneWrapper().setFPS((int) outputFileInfo.videoInfo.fps);
        mMediaFileWriter = new DCMediaFileWriter(outputFileInfo.filePath);

        int degree = 0;
        MediaFormat videoOutputFormat = null;
        if (outputFileInfo.videoInfo != null) {
            mSceneWrapperList.get(0).setFPS((int) outputFileInfo.videoInfo.fps);
            degree = outputFileInfo.videoInfo.degree;
            videoOutputFormat =
                    MediaFormat.createVideoFormat(OUTPUT_VIDEO_MIME, (int) outputFileInfo.videoInfo.width,
                            (int) outputFileInfo.videoInfo.height);
            videoOutputFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                    MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            videoOutputFormat.setInteger(MediaFormat.KEY_BIT_RATE, outputFileInfo.videoInfo.videoBitRate);
            videoOutputFormat.setInteger(MediaFormat.KEY_FRAME_RATE, (int) outputFileInfo.videoInfo.fps);
            videoOutputFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
        }
        KLog.i("export---videoBitRate--->"+outputFileInfo.videoInfo.videoBitRate);
        MediaFormat audioOutputFormat = null;
        if (outputFileInfo.audioInfo != null) {
            audioOutputFormat =
                    MediaFormat.createAudioFormat(OUTPUT_AUDIO_MIME, outputFileInfo.audioInfo.sampleRate,
                            outputFileInfo.audioInfo.channelCount);
            audioOutputFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, 2 /* OMX_AUDIO_AACObjectLC */);
            audioOutputFormat.setInteger(MediaFormat.KEY_BIT_RATE, outputFileInfo.audioInfo.audioBitRate);
        }

        try {
            mMediaFileWriter.setup(videoOutputFormat, audioOutputFormat, degree);
        } catch (IOException e) {
            e.printStackTrace();
        }
        play();
    }
    public void release() {
        if(mIsForExporting){
            if(countExport>0)
                countExport--;
        }else{
            if(count>0)
                count--;
        }

        Log.d(TAG,mIsForExporting+"realease--rel-playerCount>>"+count+"countExport:"+countExport);
        if (check()) {
            for (DCSceneWrapper sceneWrapper : mSceneWrapperList) {
                sceneWrapper.release();
            }
            mSceneWrapperList.clear();
        }
        mIsPrepare = false;

        if (mMediaFileWriter != null) {
            mMediaFileWriter.release();
            mMediaFileWriter = null;
        }

        if (mGLRenderer != null) {
            mGLRenderer.release();
            mGLRenderer = null;
        }
        Log.d(TAG, "release-->" + mGLRenderer);
        mOnPositionUpdateListener = null;
        mOnCompletionListener = null;
        mOnPreparedListener = null;
    }

    public void resize(SurfaceTexture surfaceTexture, int width, int height) {
        if (mGLRenderer != null) {
            mGLRenderer.release();
        }
//        mWidth = width;
//        mHeight = height;
        setSurfaceTexture(surfaceTexture, mWidth, mHeight, width, height);
        mGLRenderer.prepare();
    }

    // private methods
    public DCSceneWrapper getSceneWrapper() {
        return check() ? mSceneWrapperList.get(0) : null;
    }


        public List<DCAssetWrapper> getAssetWrappers() {
        return getSceneWrapper() == null ? null : getSceneWrapper().getAssetWrappers();
    }

    private void setSurfaceTexture(SurfaceTexture surfaceTexture, int width, int height, int viewWidth, int viewHeight) {
        mGLRenderer = new DCGLRenderer(surfaceTexture);
        mGLRenderer.setVideoSize(width, height);
        mGLRenderer.setViewSize(viewWidth, viewHeight);
        mGLRenderer.setPlayer(this);

        Log.d(TAG, "setSurfaceTexture-->" + mGLRenderer);
    }

    private void setSurfaceTexture(int width, int height) {
        mGLRenderer = new DCGLRenderer(width, height);
        mGLRenderer.setVideoSize(width, height);
        mGLRenderer.setPlayer(this);
        Log.d(TAG, "setSurfaceTexture-->" + mGLRenderer);
    }

    private void prepareInternal() throws IOException{
        if (!check()) {
            return;
        }
//        if(!mIsPrepare) {
//            mGLRenderer.prepare();
//            mIsPrepare = true;
//        } else {
//            resize(mSurfaceTexture,mWidth,mHeight);
//        }
        if(mGLRenderer==null){
            resize(mSurfaceTexture,mWidth,mHeight);
        }
        mGLRenderer.prepare();

        for (DCSceneWrapper sceneWrapper : mSceneWrapperList) {
            sceneWrapper.prepare();
        }
        changeStateTo(PlayerState.Prepared);
        if (mAutoPlay) {
            play();
        }
    }

    private void changeStateTo(PlayerState state) {
        mState = state;
        switch (mState) {
            case PlaybackCompleted:
//                KLog.i("changeStateTo-completed");
                onCompletion();
                break;
            case Prepared:
                onPrepared();
                break;
            case Started:
//                KLog.i("changeStateTo-Started");
                break;
            default:
                break;
        }
    }

    private void onCompletion() {
        if (mOnCompletionListener != null) {
            mOnCompletionListener.onCompletion(this);
        }
    }

    private void onPrepared() {
        Log.d("DCPlayer", "=======onPrepared-onPlayerPrepared==prepare--" + mOnPreparedListener);
        if (mOnPreparedListener != null) {
            mOnPreparedListener.onPrepared(this);
        }
    }

    private boolean check() {
        return !(mSceneWrapperList == null || mSceneWrapperList.size() == 0);
    }
    // delegate
    @Override
    public void OnPositionUpdate(float progress, long timeStamp) {
        if (mOnPositionUpdateListener != null) {
            mOnPositionUpdateListener.onPositionUpdate(this, progress, timeStamp);
        }
    }

    @Override
    public void OnVideoFrameReady(long timestamp) {

        if (SLVideoTool.playerStartPlayTime==0) {
            SLVideoTool.playerStartPlayTime = System.nanoTime();
//            Log.e("recordActivitySdk", "SLVideoTool.playerStartPlayTime:" + SLVideoTool.playerStartPlayTime);
        }

        if (mMediaFileWriter != null) {
            mMediaFileWriter.writeOneVideoFrameFromSurface(timestamp);
        }
        if (!mIsForExporting) {
            getSceneWrapper().onFrameAvailable();
        }
    }

    @Override
    public void OnAudioSampleReady(ByteBuffer audioSample) {
        if (mMediaFileWriter != null && mMediaFileWriter.isOpened()) {
            mMediaFileWriter.writeOneAudioSample(audioSample);
        }
    }

    @Override
    public void OnCompletion(DCSceneWrapper sceneWrapper) {
        Log.d(TAG, "------------------------------------------------------------------------------------ OnCompletion");
        //changeStateTo(PlayerState.PlaybackCompleted);
        if (mIsForExporting) {
            Message msg = handler.obtainMessage();
            msg.what = MessageTypeWriterIsFinished;
            handler.sendMessage(msg);
        } else if (mRepeat) {
            Message msg = handler.obtainMessage();
            msg.what = MessageTypeCompletion;
            handler.sendMessageDelayed(msg, 100);
        } else {
            changeStateTo(PlayerState.PlaybackCompleted);
        }
    }

    @Override
    public void OnAllVideoFramesReady(DCSceneWrapper sceneWrapper, long timestamp) {
        Message msg = handler.obtainMessage();
        msg.what = MessageTypeVideoFrameAvailable;
        Bundle bundle = new Bundle();
        bundle.putLong("pts", timestamp);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    // processing message
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MessageTypeCompletion:
                    if (mIsForExporting) {
                        play();
                    } else {
                        seekTo(0, true);
                    }
                    changeStateTo(PlayerState.PlaybackCompleted);
                    break;
                case MessageTypeVideoFrameAvailable:
                    Bundle bundle = msg.getData();
                    if (bundle != null) {
                        long pts = bundle.getLong("pts");
                        if (pts >= 0) {
                            if(mGLRenderer!=null)
                            mGLRenderer.drawFrame(pts);
                        }
                    }
                    break;
                case MessageTypeWriterIsFinished:
                    if (mMediaFileWriter != null) {
                        mMediaFileWriter.finish();
                    }
                    changeStateTo(PlayerState.PlaybackCompleted);
                    break;
            }
        }

        ;
    };
}

