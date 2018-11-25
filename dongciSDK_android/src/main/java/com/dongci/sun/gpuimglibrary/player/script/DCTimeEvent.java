package com.dongci.sun.gpuimglibrary.player.script;

import com.dongci.sun.gpuimglibrary.player.DCOptions;

public class DCTimeEvent {
    public int trackId;
    public int assetId;
    public long eventTime;
    public long beginTime;
    public long endTime;
    public float scale;
    public long targetDuration;
    public @DCOptions.DCPlayMode int playMode;

    public DCTimeEvent() {
        this.playMode = DCOptions.DCPlayModeForward;
    }
}
