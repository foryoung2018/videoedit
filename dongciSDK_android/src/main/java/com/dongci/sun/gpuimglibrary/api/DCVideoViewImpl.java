package com.dongci.sun.gpuimglibrary.api;

public interface DCVideoViewImpl {

    public void setAspectRatioFitMode();

    public void setPreviewAspectRatio();

    public void setAutoRepeat(boolean repeat);

    public long getDuration();

    public long getCurrentPosition();

    public void reset();

    public boolean isPlaying();

    /**
     *
     */
    public void start();

    public void pause();

    public void seekTo(float position);
}
