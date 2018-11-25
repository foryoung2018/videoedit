package com.dongci.sun.gpuimglibrary.player;

public interface DCPlayerImpl {

    void start();

    void pause();

    void stop();

    void reset();

    boolean isPlaying();

    long getDuration();

    public void seekTo(long position);

    public void setAutoRepeat(boolean repeat);

    /**
     * 宽高比
     * @param ratio
     */
    public void setPreviewAspectRatio(float ratio);

    public void setAspectRatioFitMode();

}
