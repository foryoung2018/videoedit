package com.wmlive.hhvideo.heihei.record.listener;

/**
 * Created by lsq on 9/13/2017.
 */

public interface WebpJoinListener {
    void onStartJoin();

    void onJoining(float progress);

    void onJoinEnd(boolean result, String path);
}
