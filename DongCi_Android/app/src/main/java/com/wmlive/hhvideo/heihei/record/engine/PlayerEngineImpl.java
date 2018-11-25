package com.wmlive.hhvideo.heihei.record.engine;

import android.view.View;
import android.view.ViewGroup;

import com.wmlive.hhvideo.heihei.record.engine.constant.AspectRatioFitMode;
import com.wmlive.hhvideo.heihei.record.engine.listener.PlayerListener;

/**
 * 播放器管理，开始，暂停，重置，绑定显示
 */
public interface PlayerEngineImpl {

    public void start();
    public void seekTo(float position);
    public long getCurrentPosition();
    public long getDuration();
    public void pause();
    public void release();
    public abstract int getWidth();
    public int getHeight();
    public void setVisibility(int visibility);
    public void setLayoutParams(ViewGroup.MarginLayoutParams params);
    public void reset();
    public boolean isPlaying();
    public void setPreviewAspectRatio(float ratio);
    public void setAspectRatioFitMode(int mode);//两种模式可以选择
    public void setBackgroundColor(int id);
    public void setOnPlaybackListener(PlayerListener playerListener);//设置播放器信息毁掉
    public void setOnClickListener(View.OnClickListener clickListener);//点击事件
    public void setAutoRepeat(boolean autoRepeat);


}
