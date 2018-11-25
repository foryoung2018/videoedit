package com.dongci.sun.gpuimglibrary.api;

import com.dongci.sun.gpuimglibrary.R;
import com.dongci.sun.gpuimglibrary.common.FilterInfoEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 录制参数
 */
public class DCRecorderConfig {

    /**
     * 960* 540 16:9
     */
    private int videoWidth = 720;
    private int videoHeight = 960;
    private int fps = 24;
    private int bitrate = (int) (2.5 * 1000 * 1000);//码率
    private boolean enableFront = true;
    private boolean canBeautity = true;
    private int level = 0;
    private boolean enableFrontMirror;
    private boolean autoFocus = true;
    private boolean autoFocusRecording = true;
    private boolean isFlashEnable = false;
    private boolean isFlashModeEnable;
    /***视频保存路径*/
    private String videoPath;

    private int keyFrameTime = 1;

    public int getKeyFrameTime() {
        return keyFrameTime;
    }

    public void setKeyFrameTime(int keyFrameTime) {
        this.keyFrameTime = keyFrameTime;
    }


    public int getVideoWidth() {
        return videoWidth;
    }

    public void setVideoWidth(int videoWidth) {
        this.videoWidth = videoWidth;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public void setVideoHeight(int videoHeight) {
        this.videoHeight = videoHeight;
    }

    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public boolean isEnableFront() {
        return enableFront;
    }

    public void setEnableFront(boolean enableFront) {
        this.enableFront = enableFront;
    }

    public boolean isCanBeautity() {
        return canBeautity;
    }

    public void setCanBeautity(boolean canBeautity) {
        this.canBeautity = canBeautity;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isEnableFrontMirror() {
        return enableFrontMirror;
    }

    public void setEnableFrontMirror(boolean enableFrontMirror) {
        this.enableFrontMirror = enableFrontMirror;
    }

    public boolean isAutoFocus() {
        return autoFocus;
    }

    public void setAutoFocus(boolean autoFocus) {
        this.autoFocus = autoFocus;
    }

    public boolean isAutoFocusRecording() {
        return autoFocusRecording;
    }

    public void setAutoFocusRecording(boolean autoFocusRecording) {
        this.autoFocusRecording = autoFocusRecording;
    }

    public boolean isFlashEnable() {
        return isFlashEnable;
    }

    public void setFlashEnable(boolean flashEnable) {
        isFlashEnable = flashEnable;
    }

    public boolean isFlashModeEnable() {
        return isFlashModeEnable;
    }

    public void setFlashModeEnable(boolean flashModeEnable) {
        isFlashModeEnable = flashModeEnable;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public final static List<FilterInfoEntity> FILTER_LIST = new ArrayList<>();

    static {
        int index = 0;
        FILTER_LIST.add(new FilterInfoEntity(index, R.drawable.recorder_filter_0, R.string.filter_1));
        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_1, R.string.filter_2));
        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_2, R.string.filter_3));
        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_3, R.string.filter_4));
        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_4, R.string.filter_5));
        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_5, R.string.filter_6));
        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_6, R.string.filter_7));
        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_7, R.string.filter_8));
        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_8, R.string.filter_9));
        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_9, R.string.filter_10));
        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_10, R.string.filter_11));
        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_11, R.string.filter_12));
        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_12, R.string.filter_13));
        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_13, R.string.filter_14));
        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_14, R.string.filter_15));
        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_15, R.string.filter_16));
        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_16, R.string.filter_17));
        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_17, R.string.filter_18));
        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_18, R.string.filter_19));
        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_19, R.string.filter_20));
        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_20, R.string.filter_21));
        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_21, R.string.filter_22));
        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_22, R.string.filter_23));
        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_23, R.string.filter_24));
        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_24, R.string.filter_25));
        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_25, R.string.filter_26));
        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_26, R.string.filter_27));
//        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_27, R.string.filter_28));
        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_28, R.string.filter_29));
        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_29, R.string.filter_30));
//        FILTER_LIST.add(new FilterInfoEntity(index++, R.drawable.recorder_filter_29, R.string.filter_31));
    }


}
