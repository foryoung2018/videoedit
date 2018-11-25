package com.wmlive.hhvideo.heihei.record.engine.config;

public interface VideoConfigImpl {

    public void enableHWEncoder(boolean enableHWEncoder);

    public void enableHWDecoder(boolean enableHWDecoder);

    public void setVideoEncodingBitRate(int bitRate);

    public void setKeyFrameTime(int key);

    public void setVideoSize(int width,int height);

    public void setVideoFrameRate(int frameRate);

    public long getVideoDuration();

    public void setVideoDuration(long duration);

    public void setAspectRatio(float ratio);

    public void setAudioEncodingParameters(int channelCount,int sample_rate,int bit_rate);

}
