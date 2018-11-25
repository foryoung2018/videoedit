package com.dongci.sun.gpuimglibrary.api.listener;

public interface DCPlayerListener {

    public void onPrepared();

    public void onProgress(float progress);

    public void onComplete();

    boolean onPlayerError(int var1, int var2);

}
