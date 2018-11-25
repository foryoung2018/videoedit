package com.wmlive.hhvideo.utils;

import android.media.MediaPlayer;
import android.text.TextUtils;

/**
 * mediaPlay 工具
 */
public class MediaPlayUtil {
    //  private  MediaPlayUtil mMediaPlayUtil;
    private MediaPlayer mMediaPlayer;

    /**
     * 播放完成回调
     *
     * @param playOnCompleteListener
     */
    public void setPlayOnCompleteListener(MediaPlayer.OnCompletionListener playOnCompleteListener) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setOnCompletionListener(playOnCompleteListener);
        }
    }

    /**
     * 播放器错误
     *
     * @param errorListener
     */
    public void setPlayOnErrorListener(MediaPlayer.OnErrorListener errorListener) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setOnErrorListener(errorListener);
        }
    }

    /**
     * 当存储的网络流文件buffer 发生变化时调用
     *
     * @param onBufferingUpdateListener
     */
    public void setPlayOnBufferingUpdateListener(MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
        }
    }

    /**
     * 接收消息或警告时调用
     *
     * @param onInfoListener
     */
    public void setPlayOnInfoListener(MediaPlayer.OnInfoListener onInfoListener) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setOnInfoListener(onInfoListener);
        }
    }

    /**
     * 准备就绪时调用
     *
     * @param onPreparedListener
     */
    public void setPlayOnPreparedListener(MediaPlayer.OnPreparedListener onPreparedListener) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setOnPreparedListener(onPreparedListener);
        }
    }

    /**
     * 当seek 完成后回调
     *
     * @param onSeekCompleteListener
     */
    public void setPlayOnSeekCompleteListener(MediaPlayer.OnSeekCompleteListener onSeekCompleteListener) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setOnSeekCompleteListener(onSeekCompleteListener);
        }
    }


//    /**
//     * 实例化队形
//     *
//     * @return
//     */
//    public static MediaPlayUtil getInstance() {
//        if (mMediaPlayUtil == null) {
//            mMediaPlayUtil = new MediaPlayUtil();
//        }
//        return mMediaPlayUtil;
//    }

    public MediaPlayUtil() {
        mMediaPlayer = new MediaPlayer();
    }

    public static String afterPlayPath = "";
    /**
     * 播放
     *
     * @param soundFilePath
     */
    public String soundFilePath = "";

    public void initPlay(String soundFilePath) {
        if (mMediaPlayer == null || soundFilePath == null || TextUtils.isEmpty(soundFilePath)) {
            return;
        }
        try {
            this.soundFilePath = soundFilePath;
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(soundFilePath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getSoundFilePath() {
        return soundFilePath;
    }

    public void setSoundFilePath(String soundFilePath) {
        this.soundFilePath = soundFilePath;
    }

    /**
     * 暂停
     */
    public void pausePlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    /**
     * 停止
     */
    public synchronized void stopPlayer() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
    }

    public synchronized void resetPlayer() {
        if (mMediaPlayer != null) {
            try {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
                mMediaPlayer.reset();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void releasePlayer() {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mMediaPlayer = null;
        }
    }

    /**
     * 获取当前进度
     *
     * @return
     */
    public int getCurrentPositionPlayer() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            return mMediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    /**
     * 获取文件时间
     *
     * @return
     */
    public int getDutationPlayer() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            return mMediaPlayer.getDuration();
        } else {
            return 0;
        }
    }

    /**
     * 是否播放
     *
     * @return
     */
    public boolean isPlayingPlayer() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        } else {
            return false;
        }
    }
}
