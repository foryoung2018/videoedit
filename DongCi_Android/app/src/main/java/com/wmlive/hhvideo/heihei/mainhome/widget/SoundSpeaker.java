package com.wmlive.hhvideo.heihei.mainhome.widget;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.text.TextUtils;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.SdkUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lsq on 1/24/2018.10:40 AM
 *
 * @author lsq
 * @describe 添加描述
 */

public class SoundSpeaker implements SoundPool.OnLoadCompleteListener {
    private SoundPool soundPool;
    private Map<String, Integer> soundIdMap;

    public SoundSpeaker() {
        if (SdkUtils.isLollipop()) {
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setMaxStreams(10);
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            builder.setAudioAttributes(attrBuilder.build());
            soundPool = builder.build();
        } else {
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        }
        soundPool.setOnLoadCompleteListener(this);
        soundIdMap = new HashMap<>(4);
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        KLog.i("======音频加载完成，准备播放 sampleId:" + sampleId + " status:" + status);
        int result = soundPool.play(sampleId, 1, 1, 1, 0, 1);
        KLog.i("====音频播放：" + (result > 0 ? "成功" : "失败"));
    }

    public void playSound(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            KLog.i("=====需要播放的音频文件是：" + filePath);
            if (soundIdMap.containsKey(filePath)) {
                soundPool.play(soundIdMap.get(filePath), 1, 1, 1, 0, 1);
                KLog.i("=====加载已有的的音频文件是：" + filePath + " ,soundId:" + soundIdMap.get(filePath));
            } else {
                int soundId = 0;
                if (filePath.startsWith("/")) {
                    soundId = soundPool.load(filePath, 1);
                } else {
                    try {
                        soundId = soundPool.load(DCApplication.getDCApp().getAssets().openFd(filePath), 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (soundId > 0) {
                    soundIdMap.put(filePath, soundId);
                }
                KLog.i("=====加载新的的音频文件是：" + filePath + " ,soundId:" + soundId);
            }
        }
    }

    public void release() {
        if (soundPool != null) {
            soundPool.release();
        }
    }
}
