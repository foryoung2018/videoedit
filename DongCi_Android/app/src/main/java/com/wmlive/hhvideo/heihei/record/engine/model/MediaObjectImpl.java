package com.wmlive.hhvideo.heihei.record.engine.model;

import android.content.Context;
import android.graphics.RectF;

public interface MediaObjectImpl {

    public void MediaObject(String framePath);

    public void setShowRectF(RectF rectF);//cropRect

    public void setAspectRatioFitMode(int type);//

    /**
     * 设置播放速度
     * @param speed
     */
    public void setSpeed(float speed);//无

    /**
     * 是否静音
     * @param mute
     */
    public void setAudioMute(boolean mute);//静音，0 设置为0

    public void setMixFactor();

    public long getDuration();

    //新sdk 有的功能
    public void setRectInVideo(RectF rectf);

    public void setStartTimeInScene(long startTimeInScene);

    public void setTimeRange(long start,long duration);

    /**
     * 秒
     * @param start
     * @param duration
     */
    public void setTimeRange(float start,float duration);

    public void setVolume(float volume);

    public void setWeights(float weights);

    public void setIndex(int index);

    public void setScaleNorm(float scaleNorm);

    public void setInputScale(float inputScale);

    public void setVideoWidth(int width);

    public void setVideoHeight(int height);

}
