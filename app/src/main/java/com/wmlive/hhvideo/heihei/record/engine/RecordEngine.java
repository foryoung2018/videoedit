package com.wmlive.hhvideo.heihei.record.engine;

import com.dongci.sun.gpuimglibrary.api.DCRecorderCore;
import com.dongci.sun.gpuimglibrary.api.listener.DCCameraListener;
import com.dongci.sun.gpuimglibrary.camera.CameraView;
import com.dongci.sun.gpuimglibrary.gles.filter.FilterUtils;

public class RecordEngine implements RecordEngineImpl{

    DCRecorderCore DcRecorderCore;

    public void init(CameraView cameraView){
//        DcRecorderCore = new DCRecorderCore(cameraView);
    }

    @Override
    public void enableBeautify(boolean isBeauty) {
        DcRecorderCore.enableBeautify(isBeauty);
    }

    @Override
    public void switchCamera() {
        DcRecorderCore.switchCamera(0);
    }

    @Override
    public void cameraAutoFocus(boolean autoFocus) {
        DcRecorderCore.cameraAutoFocus(autoFocus);
    }

    @Override
    public void setFlashMode(boolean flashMode) {
        DcRecorderCore.setFlashMode(flashMode);
    }

    @Override
    public boolean isSuuportBeautify() {
        return DcRecorderCore.isSuuportBeautify();
    }

    @Override
    public boolean isBeautifyEnabled() {
        return DcRecorderCore.isBeautifyEnabled();
    }

    @Override
    public boolean isFaceFront() {
        return DcRecorderCore.isFaceFront();
    }

    @Override
    public boolean getFlashMode() {
        return DcRecorderCore.getFlashMode();
    }

    @Override
    public void setDebugable(boolean debugable) {
        DcRecorderCore.setDebugable();//?
    }

    @Override
    public void setColorEffect(FilterUtils.FilterType filterType) {
            DcRecorderCore.switchFilter(filterType);
    }

    @Override
    public void recycleCameraView() {
        DcRecorderCore.recycleCameraView();
    }

    @Override
    public void UnnableOrientation() {

    }

    @Override
    public void isPreparing() {
        DcRecorderCore.isPreparing();
    }

    @Override
    public void onExit() {
        DcRecorderCore.onExit();
    }

    @Override
    public void unRegisterReceiver() {

    }

    @Override
    public void setCameraZoomHandler() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void screenShot() {

    }

    @Override
    public void setBeautyLevel(int level) {

    }

    @Override
    public void setEncoderConfig(RecordEngineConfig config) {
        DcRecorderCore.setEncoderConfig(config);
    }

    @Override
    public void startRecord() throws Exception {
        DcRecorderCore.stopRecord();
    }

    @Override
    public void stopRecord() {
        DcRecorderCore.stopRecord();
    }

    @Override
    public boolean isRecording() {
        return DcRecorderCore.isRecording();
    }

    @Override
    public void onPrepare(DCCameraListener dcCameraListenerTemp) {
        DcRecorderCore.onPrepare(dcCameraListenerTemp);
    }
}
