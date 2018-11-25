package com.wmlive.hhvideo.heihei.record.listener;

/**
 * Created by lsq on 9/1/2017.
 * 合成视频的监听
 */

public interface VideoJoinListener {
    void onJoinStart();

    void onJoining(int progress, int max);

    void onJoinEnd(boolean result, String message);
}
