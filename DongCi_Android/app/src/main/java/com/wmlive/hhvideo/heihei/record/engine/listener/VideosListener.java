package com.wmlive.hhvideo.heihei.record.engine.listener;

public interface VideosListener {

    /**
     * 开始
     */
    public void onStart();

    /**
     * 进度
     * @param progress
     */
    public void onProgress(int progress);

    /**
     * 执行成功
     * @param code
     */
    public void onFinish(int code, String... outpath);

    /**
     * 执行失败
     */
    public void onError(int code,String msg);
}
