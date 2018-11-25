package com.wmlive.hhvideo.heihei.record.config;

/**
 * 导出的视频宽度
 */
public enum ExportConfig{
    EXPORT_11(720,720),EXPORT_34(720,960),EXPORT_169(960,544),EXPORT_916(544,960);

    public final int FPS = 24;
    public final int KEY_FRAME = 48;
    public static final int BITRATE_VIDEO = 2500*1000;//2.5 * 1000 * 1000
    public static final int BITRATE_VIDEO_MV = 3500*1000;//2.5 * 1000 * 1000
    public final int BITRATE_AUDIO = 96000;
    public final int SAMPLE_RATE = 44100;
    public final int CHANNEL_COUNT = 1;

    private int width = 0;
    private int height;

    private ExportConfig(int w,int h){
        width = w;
        height = h;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

//    2、导出的参数设置：
//    分辨率（根据画框比例调整）：
//            1:1比例：720x720、
//            4:3比例：960x720、
//            16:9比例：960x544、
//            9:16比例：544x960、
//    bps：1500kbps、
//    fps：24、
//    关键帧间隔：24*2=48、
//    profile：baseline、
//
//    audiobitrate：96Kbps、
//    samplerate：44100、
//    channelCount：1、

}