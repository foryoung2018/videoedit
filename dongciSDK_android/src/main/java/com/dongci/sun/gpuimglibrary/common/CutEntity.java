package com.dongci.sun.gpuimglibrary.common;

/**
 * 剪切变量
 */
public class CutEntity {

    public String path;
    public long start;
    public long duration;
    public String cutPath;
    /**
     * 原始音频
     */
    public String audioPath;
    /**
     * 裁剪后的音频
     */
    public String cutAudioPath;
    /**
     * 当前的音量
     */
    public float volume;

    public boolean cutResult;
}
