package com.dongci.sun.gpuimglibrary.api;

import android.app.Activity;
import android.hardware.Camera;
import android.util.Log;
import android.widget.RelativeLayout;

import com.dongci.sun.gpuimglibrary.R;
import com.dongci.sun.gpuimglibrary.api.listener.DCCameraListener;
import com.dongci.sun.gpuimglibrary.camera.CameraView;
import com.dongci.sun.gpuimglibrary.gles.filter.FilterUtils;
import com.dongci.sun.gpuimglibrary.common.SLVideoComposer;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DCRecorderCore {

    public static DCRecorderConfig config;

    public static CameraView cameraView;

    static DCCameraListener dcCameraListener;

    static RelativeLayout parentRl;

    static DCRecorderCore dcRecorderCore;

    public void release() {
        config = null;
        cameraView = null;
        dcCameraListener = null;
        parentRl = null;
        dcRecorderCore = null;
    }

    public static DCRecorderCore getDcRecorderCore() {
        if (dcRecorderCore == null)
            dcRecorderCore = new DCRecorderCore();
        return dcRecorderCore;
    }

    public void initConfig() {
        if (config == null){
            config = new DCRecorderConfig();
        }
        setEncoderConfig(config);
    }

    public void prepare(RelativeLayout rl, DCCameraListener dcCameraListenerTemp) throws IllegalArgumentException {
        if (rl == null)
            throw new IllegalArgumentException("父容器不能为null");
        else {
            parentRl = rl;
            parentRl.removeAllViews();
            cameraView = null;
            init((Activity) rl.getContext());
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1,-1);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            parentRl.addView(cameraView, params);
            //预览
            onPrepare(dcCameraListenerTemp);
        }
        dcCameraListener = dcCameraListenerTemp;

    }

    private DCRecorderCore() {
    }

    public void init(Activity context) {
        if (cameraView == null)
            cameraView = new CameraView(context);
        initConfig();
    }

    /**
     * 更新录制的各项参数
     */
    private void updateConfig() {
        setVideoSize(config.getVideoWidth(), config.getVideoHeight());
        cameraView.setRecordBitrate(config.getBitrate());
        cameraView.setFps(config.getFps());
        cameraAutoFocus(config.isAutoFocus());
        setFlashMode(config.isFlashEnable());
        //
    }

    private void setVideoSize(int width, int height) {
        config.setVideoWidth(width);
        config.setVideoHeight(height);
        if (cameraView != null)
            cameraView.setVideoSize(width, height);
    }

    /**
     * 是否打开美颜
     */
    public void enableBeautify(boolean isBeauty) {
        cameraView.setBeautyLevel(isBeauty ? 4 : 0);
    }

    public void switchCamera(int cameraId) {
        cameraView.switchCamera(cameraId);
    }

    public void setUserFrontCamera() {
        cameraView.configCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
//        cameraView.switchCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    /**
     * 默认自动对焦
     *
     * @param autoFocus
     */
    public void cameraAutoFocus(boolean autoFocus) {
        cameraView.setAutoFocus(autoFocus);
    }

    /**
     * 默认关闭 灯光
     *
     * @param flashMode
     */
    public void setFlashMode(boolean flashMode) {
        cameraView.setFlashMode(flashMode);
    }

    public boolean isSuuportBeautify() {
        return true;
    }

    public boolean isBeautifyEnabled() {
        if (cameraView != null)
            return cameraView.getBeautyLevel() > 0;
        return false;
    }

    public static boolean isFaceFront() {
        return cameraView.isFaceFront();
    }

    public boolean getFlashMode() {
        if (cameraView != null)
            return cameraView.getFlashMode();
        return false;
    }

    public void setDebugable() {

    }

    public void recycleCameraView() {
        if (cameraView != null)
            cameraView.recycleCameraView();
        if (parentRl != null)
            parentRl.removeAllViews();
    }

    /**
     * 初始化 禁止旋转
     */
    public void UnnableOrientation() {

    }



    public static boolean isPreparing() {
        if (cameraView == null)
            return false;
        return cameraView.isRecording();
    }

    public void onExit() {
        cameraView.onDestroy();
        cameraView =null;
        config = null;
        release();
    }

    public void unRegisterReceiver() {

    }

    /***
     * 暂时没有该功能
     */
    public void setCameraZoomHandler() {

    }

    /********************************************/
    /**
     * 开始预览
     */
    public void onResume() {
        cameraView.onResume();
    }

    public void onDestroy() {
        if (cameraView != null)
            cameraView.onDestroy();
        cameraView = null;
        release();
    }

    /*
    切换滤镜
     */
    public void switchFilter(int index) {

        Log.e("selectFi", "index:" + index + "filer:" + FilterUtils.getAllFilterList().get(index));

        Log.e("selectFiler", "filer:" + FilterUtils.filterWithType(cameraView.getContext(), FilterUtils.getAllFilterList().get(index)));

        cameraView.switchFilter(FilterUtils.filterWithType(cameraView.getContext(), FilterUtils.getAllFilterList().get(index)));

    }

    public void switchFilter(FilterUtils.FilterType filterType) {

        cameraView.switchFilter(FilterUtils.filterWithType(cameraView.getContext(), filterType));

    }

    public void registWaterMark() {
        cameraView.addWaterMarker(30, 50, 0, 0, R.drawable.ic_launcher_round);
    }

    public void clearWaterMark() {
        cameraView.clearFocus();
    }

    /**
     * 暂时没有需求,拍照
     */
    public void screenShot() {

    }

    /**
     * 设置美颜级别
     *
     * @param level
     */
    public void setBeautyLevel(int level) {
        cameraView.setBeautyLevel(level);
    }

    /**
     * 是否设置镜像
     *
     * @param mirror
     */
    public void setMirror(boolean mirror) {
        cameraView.setMirror(mirror);
    }

    /**
     * 视频组合成一个短视频
     */
    public void composeVideos(final ArrayList videoList, final String outPath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                ArrayList videoList = new ArrayList<>();
//                //待合成的2个视频文件
//                videoList.add("test1.mp4");
//                videoList.add("test2.mp4");
                SLVideoComposer composer = new SLVideoComposer(videoList, outPath);
                final boolean result = composer.joinVideo();
                Log.i(TAG, "compose result: " + result);
            }
        }).start();
    }

    /***********************视频录制****************************/

    public void setOutPath(String outPutPath) {
        config.setVideoPath(outPutPath);
        cameraView.setSavePath(outPutPath);
        Log.d("sun-", "camera--size-:: -setOutPath" + outPutPath);
    }

    public void setEncoderConfig(DCRecorderConfig c) {
        if (c != null)
            config = c;
        updateConfig();
    }

    /**
     * 开始录制，
     *
     * @param videoPath 视频路径,同时保存了音频
     * @param speed     速度
     * @throws Exception
     */
    public void startRecord(String videoPath, double speed) throws Exception {
        if (videoPath == null)
            throw new Exception("out path is null,please setOutPath first");
        setOutPath(videoPath);
        cameraView.startRecord();
    }

    public void stopRecord() {
        cameraView.stopRecord();
    }

    public static boolean isRecording() {
        if (cameraView == null)
            return false;
        return cameraView.isRecording();
    }

    public void onPrepare(DCCameraListener dcCameraListenerTemp) {
        cameraView.onPrepare(dcCameraListenerTemp);
    }

    public void setPara(){
        cameraView.stickerInit();
    }


}