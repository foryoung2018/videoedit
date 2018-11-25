package com.wmlive.hhvideo.heihei.record.config;

public enum RecordConfig {
    RECORD_HEIGHT(720,960),RECORD_LOW(480,640);
    private int width;
    private int height;

    public final int FPS = 24;
    public final int KEY_FRAME = 48;
    public final int BITRATE_VIDEO = 15000*1000;
    public final int BITRATE_AUDIO = 96000;
    public final int SIMPLE_RATE = 44100;
    public final int CHANNEL_COUNT = 1;


    private RecordConfig(int w,int h){
        width = w;
        height = h;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }




}
