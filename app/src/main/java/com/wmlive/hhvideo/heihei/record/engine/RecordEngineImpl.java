package com.wmlive.hhvideo.heihei.record.engine;

import com.dongci.sun.gpuimglibrary.api.listener.DCCameraListener;
import com.dongci.sun.gpuimglibrary.gles.filter.FilterUtils;

/**
 * 视频录制功能
 */
public interface RecordEngineImpl {

    /**
     * 是否打开美颜
     */
    void enableBeautify(boolean isBeauty);

    public void switchCamera();

    /**
     * 默认自动对焦
     * @param autoFocus
     */
    public void cameraAutoFocus(boolean autoFocus);

    /**
     * 默认关闭 灯光
     * @param flashMode
     */
    public void setFlashMode(boolean flashMode);

    public boolean isSuuportBeautify();

    public boolean isBeautifyEnabled();

    public boolean isFaceFront();

    public boolean getFlashMode();

    public void setDebugable(boolean debugable);

    /********************************************/

    public void setColorEffect(FilterUtils.FilterType filterType);

    public void recycleCameraView();
    /**
     * 初始化 禁止旋转
     */
    public void UnnableOrientation();

    public void isPreparing();

    public void onExit();

    public void unRegisterReceiver();

    /***
     * 暂时没有该功能
     */
    public void setCameraZoomHandler();

    /********************************************/
    /**
     * 开始预览
     */
    public void onResume();

    public void onDestroy();



    /**
     * 暂时没有需求,拍照
     */
    public void screenShot();

    /**
     * 设置美颜级别
     * @param level
     */
    public void setBeautyLevel(int level);


    public void setEncoderConfig(RecordEngineConfig config);

    public void startRecord() throws Exception;

    public void stopRecord();

    public boolean isRecording();

    public void onPrepare(DCCameraListener dcCameraListenerTemp);


}
