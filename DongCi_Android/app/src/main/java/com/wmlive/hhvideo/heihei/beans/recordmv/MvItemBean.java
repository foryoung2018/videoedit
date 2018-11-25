package com.wmlive.hhvideo.heihei.beans.recordmv;

/**
 * 每一个录制的 视频素材 实体类
 */
public class MvItemBean {

    private String tips;
    private String image;

    private String audioPath;
    private String videoPath;
    private String combineVideoAudio;
    private int state;//0 未下载 1 下载中 2 下载完成

    private int beauty = 1;//默认美颜
    private int isFront = 1;//默认前置摄像头
    private int isFlash = 0;//默认不打开

}
