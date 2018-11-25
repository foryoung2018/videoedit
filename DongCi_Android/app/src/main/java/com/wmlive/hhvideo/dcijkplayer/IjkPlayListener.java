package com.wmlive.hhvideo.dcijkplayer;

/**
 * Created by yangjiangang on 2018/7/27.
 */

public interface IjkPlayListener {

    void onPlayStart();

    void onLoopStart();

    void onPlayStop();

    void onPlayPause();//自动暂停

    void onClickPause();//点击暂停

    /**
     * 连续点击
     *
     * @param rawX 在屏幕上的X坐标
     * @param rawY 在屏幕上的Y坐标
     */
    void onDoubleClick(float rawX, float rawY);//连续点击

    void onPlayResume();

    void onPlayError(int errorCode);

    void onFileError(int code, String errorMessage);

    void onPlayCompleted();

    void onPlayBufferStart();

    void onPlayBufferEnd();

    void onPlayPreparing();

    void onPlayPrepared();

    void onAudioRenderingStart();

    void onVideoRenderingStart();

    void onVideoRotationChanged(int rotate);

    void onPlayingPosition(long position);

    void onPlayTimeCompleted(long videoId, String url, int videoDuring);//完成规定时间的播放

}
