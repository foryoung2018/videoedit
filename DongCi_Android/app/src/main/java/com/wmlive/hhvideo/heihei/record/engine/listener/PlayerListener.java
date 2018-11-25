package com.wmlive.hhvideo.heihei.record.engine.listener;

public interface PlayerListener {

    void onPlayerPrepared();

    boolean onPlayerError(int var1, int var2);

    void onPlayerCompletion();

    void onGetCurrentPosition(float var1);
}
