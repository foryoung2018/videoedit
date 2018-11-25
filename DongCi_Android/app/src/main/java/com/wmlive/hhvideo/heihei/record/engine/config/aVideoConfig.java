package com.wmlive.hhvideo.heihei.record.engine.config;

import com.wmlive.hhvideo.heihei.record.config.ExportConfig;
import com.wmlive.hhvideo.heihei.record.config.RecordSettingSDK;

/**
 * 我方提供数据类型，框架中数据类型
 * 导出信息，
 */
public class aVideoConfig implements VideoConfigImpl{

    public boolean enableHWEncoder;
    public boolean enableHWDecoder;
    public int videoEncodingBitRate;
    public int keyFrameTime;
    public static int videoWidth;
    public static int videoHeight;
    public int videoFrameRate;
    public long videoDuration;
    public float videoRatio;//视频宽高比
    //新sdk 中添加的部分
    public String filePath;
    public int fps;
    public int totalFrames;
    public int rotation;
    //新sdk 音频部分
    public int sampleRate;
    public int channelCount;
    public int audioBitRate;
    //视频导出质量
    public int quality;

    private int frameType;

    /***
     * 默认值
     */
    public aVideoConfig(){//初始化默认值
        videoFrameRate = (int)(15 * 1000 * 1000);
        videoEncodingBitRate = ExportConfig.BITRATE_VIDEO;
        videoWidth = 720;
        videoHeight = 960;
        fps = 24;
        rotation = 90;
        sampleRate = 44100;
        channelCount = 1;
        audioBitRate =96000;
        quality = RecordSettingSDK.VIDEO_PUBLISH_BITRATE_LOW;
    }

    public void setVideoPath(String path){
        filePath = path;
    }

    public String getFilePath(){
        return filePath;
    }

    @Override
    public long getVideoDuration() {
        return this.videoDuration;
    }

    @Override
    public void setVideoDuration(long duration) {
        videoDuration = duration;
    }

    @Override
    public void setAspectRatio(float ratio) {
        videoRatio = ratio;
    }

    @Override
    public void setAudioEncodingParameters(int channelCount, int sample_rate, int bit_rate) {
        this.channelCount = channelCount;
        sampleRate = sample_rate;
        audioBitRate = bit_rate;
    }

    @Override
    public void enableHWEncoder(boolean enableHWEncoder) {
        this.enableHWEncoder = enableHWEncoder;
    }

    @Override
    public void enableHWDecoder(boolean enableHWDecoder) {
        this.enableHWDecoder = enableHWDecoder;
    }

    @Override
    public void setVideoEncodingBitRate(int bitRate) {
        videoEncodingBitRate = bitRate;
    }

    @Override
    public void setKeyFrameTime(int key) {
        keyFrameTime = key;
    }

    @Override
    public void setVideoSize(int width, int height) {
        videoWidth = width;
        videoHeight = height;
    }

    @Override
    public void setVideoFrameRate(int frameRate) {
        videoFrameRate = frameRate;
    }

    public int getVideoFrameRate(){
        return videoFrameRate;
    }

    public int getVideoWidth(){
        return videoWidth;
    }

    public int getVideoHeight(){
        return videoHeight;
    }

    /**
     * 设置当前的画框类型，根据类型设定导出宽高
     *
     * @param height
     */
    private void setFrameType(int height) {
        switch (height) {
            case 750://1:1 按照宽度适配
                videoHeight = ExportConfig.EXPORT_11.getHeight();
                videoWidth = ExportConfig.EXPORT_11.getWidth();
                break;
            case 1334://9:16  按照高度适配
                videoHeight = ExportConfig.EXPORT_916.getHeight();
                videoWidth = ExportConfig.EXPORT_916.getWidth();
                break;
            case 422://16:9  按照宽度适配
                videoHeight = ExportConfig.EXPORT_169.getHeight();
                videoWidth = ExportConfig.EXPORT_169.getWidth();
                break;
            case 425://16:9  按照宽度适配
                videoHeight = ExportConfig.EXPORT_169.getHeight();
                videoWidth = ExportConfig.EXPORT_169.getWidth();
                break;
            case 1000://3:4 按照比例的较小者适配
                videoHeight = ExportConfig.EXPORT_34.getHeight();
                videoWidth = ExportConfig.EXPORT_34.getWidth();
                break;
            default://默认设置为1:1
                videoHeight = ExportConfig.EXPORT_11.getHeight();
                videoWidth = ExportConfig.EXPORT_11.getWidth();
        }
    }
}
