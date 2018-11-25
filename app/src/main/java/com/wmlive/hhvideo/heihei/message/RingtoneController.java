package com.wmlive.hhvideo.heihei.message;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.wmlive.hhvideo.DCApplication;

import java.util.concurrent.ConcurrentHashMap;

import cn.wmlive.hhvideo.R;

/**
 * Created by hsing on 2018/3/8.
 */

public class RingtoneController {

    private static final int MESSAGE = R.raw.send_message;
    private static final int RINGBELL = R.raw.ring_bell;
    private static final int IM_ACCETP_MSG = R.raw.im_accept_message_music;
    private static final int IM_NOTIFINATION = R.raw.im_notifination_music;

    private static final ConcurrentHashMap<Integer, Integer> mMaps = new ConcurrentHashMap<>();

    private static SoundPool sp = new SoundPool(50, AudioManager.STREAM_MUSIC, 5);

    private static float LEFT_VOLUME = (float) 0.4;
    private static float RIGHT_VOLUME = (float) 0.4;

    /**
     * 播放发送消息铃声
     */
    public static void playMessageRingtone(Context context) {
        if (mMaps.containsKey(MESSAGE)) {
            int soundId = mMaps.get(MESSAGE);
            sp.play(soundId, LEFT_VOLUME, RIGHT_VOLUME, 1, 0, 1);
            return;
        }
        final int soundId = sp.load(context, MESSAGE, 0);
        sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {

            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                mMaps.put(MESSAGE, soundId);
                sp.play(soundId, (float) 0.8, (float) 0.8, 1, 0, 1);
            }
        });
    }

    /**
     * 播放铃铛响起铃声
     */
    public static void playRingBellRingtone(Context context) {
        if (mMaps.containsKey(RINGBELL)) {
            int soundId = mMaps.get(RINGBELL);
            sp.play(soundId, LEFT_VOLUME, RIGHT_VOLUME, 1, 0, 1);
            return;
        }
        final int soundId = sp.load(context, RINGBELL, 0);
        sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {

            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                mMaps.put(RINGBELL, soundId);
                sp.play(soundId, LEFT_VOLUME, RIGHT_VOLUME, 1, 0, 1);
            }
        });
    }

    /**
     * IM接收信息的音效
     */
    public static void playIMAcceptMusic() {
        if (mMaps.containsKey(IM_ACCETP_MSG)) {
            int soundId = mMaps.get(IM_ACCETP_MSG);
            sp.play(soundId, LEFT_VOLUME, RIGHT_VOLUME, 1, 0, 1);
            return;
        }
        final int soundId = sp.load(DCApplication.getDCApp(), IM_ACCETP_MSG, 0);
        sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {

            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                mMaps.put(IM_ACCETP_MSG, soundId);
                sp.play(soundId, LEFT_VOLUME, RIGHT_VOLUME, 1, 0, 1);
            }
        });
    }
    /**
     * IM 通知音效
     */
    public static void playIMNotifinationMusic() {
        if (mMaps.containsKey(IM_NOTIFINATION)) {
            int soundId = mMaps.get(IM_NOTIFINATION);
            sp.play(soundId, LEFT_VOLUME, RIGHT_VOLUME, 1, 0, 1);
            return;
        }
        final int soundId = sp.load(DCApplication.getDCApp(), IM_NOTIFINATION, 0);
        sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {

            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                mMaps.put(IM_NOTIFINATION, soundId);
                sp.play(soundId, LEFT_VOLUME, RIGHT_VOLUME, 1, 0, 1);
            }
        });
    }
}
