package com.wmlive.hhvideo.heihei.message.utils;

import android.media.MediaPlayer;
import android.text.TextUtils;

import com.wmlive.hhvideo.heihei.message.listener.IMMediaPlayOtherSoundListener;
import com.wmlive.hhvideo.heihei.message.listener.MediaPlayerStatusListener;
import com.wmlive.hhvideo.utils.KLog;

import java.lang.ref.WeakReference;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;


/**
 * IM 工具播放类
 * Created by admin on 2017/4/6.
 */

public class IMMediaPlayUtils {
    private static IMMediaPlayUtils mIMMediaPlayUtils;
    private MediaPlayer mMediaPlayer;
    private MediaPlayerStatusListener mediaPlayerStatusListener;

    private WeakReference<IMMediaPlayOtherSoundListener> imMediaPlayOtherSoundListenerWeakReference;

    private String strPlayingMsgId = "";//正在播放的MsgId
    private int iPlayingPosition = -1;//正在播放的position

    private boolean isAutoOrderPlay = false;//是否自动顺序播放
    private BlockingDeque<String> imMediaPlayerCacheQueue = new LinkedBlockingDeque<>();//带播放的缓存队列
    private BlockingDeque<String> imMediaPlayingQueue = new LinkedBlockingDeque<>();//播放中的队列


    public static IMMediaPlayUtils getInstance() {
        if (mIMMediaPlayUtils == null) {
            synchronized (IMMediaPlayUtils.class) {
                if (mIMMediaPlayUtils == null) {
                    mIMMediaPlayUtils = new IMMediaPlayUtils();
                }
            }
        }
        return mIMMediaPlayUtils;
    }

    private IMMediaPlayUtils() {
        mMediaPlayer = new MediaPlayer();
        strPlayingMsgId = "";
        iPlayingPosition = -1;
    }

    /**
     * 设置播放器回调
     *
     * @param mediaPlayerStatusListener
     */
    public void setMediaPlayerStatusListener(MediaPlayerStatusListener mediaPlayerStatusListener) {
        this.mediaPlayerStatusListener = mediaPlayerStatusListener;
    }

    /**
     * 设置自动顺序播放功能
     *
     * @param imMediaPlayOtherSoundListener
     */
    public void setImMediaPlayOtherSoundListener(IMMediaPlayOtherSoundListener imMediaPlayOtherSoundListener) {
        this.imMediaPlayOtherSoundListenerWeakReference = new WeakReference<>(imMediaPlayOtherSoundListener);
    }

    /**
     * 添加需要播放的信息
     *
     * @param msgId
     */
    public void addIMOtherSoundToFooterQueue(String msgId) {
        if (!imMediaPlayerCacheQueue.contains(msgId)) {
            imMediaPlayerCacheQueue.add(msgId);
        }
    }

    /**
     * 添加需要播放的信息
     *
     * @param msgId
     */
    public void addIMOtherSoundToHeadQueue(String msgId) {
        if (!imMediaPlayerCacheQueue.contains(msgId)) {
            imMediaPlayerCacheQueue.addFirst(msgId);
        }
    }

    /**
     * 设置自动顺序播放队列
     *
     * @param msgId
     */
    public void setIMOtherSoundPlayingQueue(String msgId) {
        if (imMediaPlayerCacheQueue.contains(msgId)) {
            isAutoOrderPlay = true;
            KLog.e("im_detail_play", "-----isAutoOrderPlay------true-");
        } else {
            isAutoOrderPlay = false;
            KLog.e("im_detail_play", "-----isAutoOrderPlay------false-");
        }
        if (isAutoOrderPlay) {
            imMediaPlayingQueue.clear();
            int size = imMediaPlayerCacheQueue.size();
            boolean isAddToPlayingQueue = false;
            for (int i = 0; i < size; i++) {
                String value = imMediaPlayerCacheQueue.poll();
                KLog.e("im_detail_play", "-----imMediaPlayerCacheQueue-------:" + value);
                if (!isAddToPlayingQueue) {
                    if (value != null && !TextUtils.isEmpty(value) && msgId.equals(value)) {
                        imMediaPlayingQueue.add(value);
                        isAddToPlayingQueue = true;
                    }
                } else {
                    imMediaPlayingQueue.add(value);
                }
                imMediaPlayerCacheQueue.add(value);
            }
            int issize = imMediaPlayingQueue.size();
            for (int i = 0; i < issize; i++) {
                String value = imMediaPlayerCacheQueue.peek();
                KLog.e("im_detail_play", "-----imMediaPlayingQueue-------:" + value);
            }
        }
    }

    /**
     * 移除制动的元素
     *
     * @param msgId
     */
    private void removeIMOtherSoundDataByMsgId(String msgId) {
        try {
            if (imMediaPlayerCacheQueue.contains(msgId)) {
                imMediaPlayerCacheQueue.removeFirstOccurrence(msgId);
            }
        } catch (Exception e) {

        }
        try {
            if (imMediaPlayingQueue.contains(msgId)) {
                imMediaPlayingQueue.removeFirstOccurrence(msgId);
            }
        } catch (Exception e) {

        }
    }


    /**
     * 开始播放----自己发送的语音
     *
     * @param position 位置
     * @param msgId    信息id
     * @param path     播放地址
     */
    public void startMediaPlayByPathToMe(final int position, final String msgId, final String path) {
        KLog.e("im_detail_play","------------startMediaPlayByPathToMe----------position:"+position+",msgId=:"+msgId+",path=:"+path);
        if (mMediaPlayer == null) {
            return;
        }else{
            mMediaPlayer  = new MediaPlayer();
        }
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(path);
            //  mMediaPlayer.prepare();//同步
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    KLog.e("im_detail_play","------------startMediaPlayByPathToMe----------onPrepared---");
                    strPlayingMsgId = msgId;
                    iPlayingPosition = position;
                    mediaPlayerStatusListener.playOnStartListener(msgId);
                    mMediaPlayer.start();
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    KLog.e("im_detail_play","------------startMediaPlayByPathToMe----------onCompletion---");
                    strPlayingMsgId = "";
                    iPlayingPosition = -1;
                    mediaPlayerStatusListener.playOnCompleteListener(msgId);
                }
            });
        } catch (Exception e) {
            KLog.e("im_detail_play","------------startMediaPlayByPathToMe----------Exception---");
            e.printStackTrace();
            mediaPlayerStatusListener.playOnErrorListener(msgId);
        }
    }

    /**
     * 开始播放----对方的录音信息
     *
     * @param otherPosition 位置
     * @param otherMsgId    信息id
     * @param otherPath     播放地址
     */
    public void startMediaPlayByPathToOther(final int otherPosition, final String otherMsgId, final String otherPath) {
        if (mMediaPlayer == null) {
            return;
        }else{
            mMediaPlayer = new MediaPlayer();
        }
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(otherPath);
            //  mMediaPlayer.prepare();//同步
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    strPlayingMsgId = otherMsgId;
                    iPlayingPosition = otherPosition;
                    mediaPlayerStatusListener.playOnStartListener(otherMsgId);
                    mMediaPlayer.start();
                    try {
                        //删除开始播放的msgId;
                        imMediaPlayingQueue.poll();
                    } catch (Exception e) {

                    }
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    strPlayingMsgId = "";
                    iPlayingPosition = -1;
                    mediaPlayerStatusListener.playOnCompleteListener(otherMsgId);
                    if (isAutoOrderPlay) {
                        //自动播放
                        //移除队列中指定的元素
                        removeIMOtherSoundDataByMsgId(otherMsgId);
                        String strItemMsgId = imMediaPlayingQueue.peek();
                        if (null != imMediaPlayOtherSoundListenerWeakReference && null != imMediaPlayOtherSoundListenerWeakReference.get() && null != strItemMsgId && !TextUtils.isEmpty(strItemMsgId)) {
                            imMediaPlayOtherSoundListenerWeakReference.get().autoStartIMMediaPlayOtherSoundItem(strItemMsgId);
                        } else {
                            //结束自动播放
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            mediaPlayerStatusListener.playOnErrorListener(otherMsgId);
            removeIMOtherSoundDataByMsgId(otherMsgId);
        }
    }

    /**
     * 停止播放自己的语音
     */
    public void stopMediaPlayByPathToMe() {
        stopMediaPlay();
    }

    /**
     * 停止播放
     */
    private void stopMediaPlay() {
        try {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                mMediaPlayer.reset();
                mediaPlayerStatusListener.playOnCompleteListener(strPlayingMsgId);
            }
        } catch (Exception e) {
            mediaPlayerStatusListener.playOnErrorListener(strPlayingMsgId);
        }
    }

    /**
     * 释放播放器
     */
    public void destoryMediaPlay() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        mMediaPlayer = null;
        mIMMediaPlayUtils =  null;
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

    /**
     * 获取正在播放的msgId
     *
     * @return
     */
    public String getStrPlayingMsgId() {
        return strPlayingMsgId;
    }

    /**
     * 获取正在播放的position
     *
     * @return
     */
    public int getiPlayingPosition() {
        return iPlayingPosition;
    }

    /**
     * 停止播放所有播放
     */
    public void stopAllPlayingMsg(){
        if(mMediaPlayer!=null && mMediaPlayer.isPlaying()){
            try {
                    mMediaPlayer.stop();
                    mMediaPlayer.reset();
            } catch (Exception e) {
            }
            if(!TextUtils.isEmpty(strPlayingMsgId) && mediaPlayerStatusListener!=null){
                mediaPlayerStatusListener.playOnCompleteListener(strPlayingMsgId);
            }
        }
    }
}
