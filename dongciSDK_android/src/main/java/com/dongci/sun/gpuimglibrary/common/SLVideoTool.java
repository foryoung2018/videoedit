package com.dongci.sun.gpuimglibrary.common;

/**
 * Created by yangjiangang on 2018/8/13.
 */

public class SLVideoTool {

    // 录制开始时间戳
    public static volatile long startRecordTime;

    // 开始播放时间戳
    public static volatile long playerStartPlayTime;

    public static volatile long lastRecordTime;

    public static volatile long videoPts;

    public static volatile boolean isStartedToRecordAudio;

    /// 当前是否采集到了关键帧
    public static boolean hasKeyFrameVideo;

    // 是否开始录制
    public static boolean isStartRecord;

    // 多格
    public static boolean moreFrame;

}
