package com.wmlive.hhvideo.heihei.message.listener;

/**
 * IM 播放器状态接口
 * Created by admin on 2017/4/6.
 */

public interface MediaPlayerStatusListener {
    //开始播放
    public void playOnStartListener(String msgId);

    //播放完成
    public void playOnCompleteListener(String msgId);

    //播放器错误
    public void playOnErrorListener(String msgId);
}
