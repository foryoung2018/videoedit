package com.dc.platform.voicebeating;

import android.media.AudioFormat;
import android.os.Handler;
import android.os.Message;


import com.dc.platform.voicebeating.audiotrack.AudioPlayer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

public class VoiceBeatingAudioPlayer {

    private Handler mAudioHandler = null;

    private AudioPlayer mAudioPlayer = null;

    private boolean mLoop = false;


   public interface AudioPlayerListener {
        void OnPlayingPosition(long time);
    }


    private AudioPlayerListener audioPlayerListener = null;

    public VoiceBeatingAudioPlayer () {

    }

    public VoiceBeatingAudioPlayer (boolean loop) {
        mLoop = loop;
    }


    public void setAudioPlayerListener(AudioPlayerListener listener) {
        this.audioPlayerListener = listener;
    }


    public boolean prepare() {

        mAudioHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case AudioPlayer.STATE_MSG_ID:
                        break;
                }
            }
        };

        mAudioPlayer = new AudioPlayer(mAudioHandler,mLoop);

        AudioPlayer.AudioParam audioParam = new AudioPlayer.AudioParam(44100,AudioFormat.CHANNEL_IN_LEFT,AudioFormat.ENCODING_PCM_16BIT);;

        mAudioPlayer.setAudioParam(audioParam);

        mAudioPlayer.setTimeListener(new AudioPlayer.PlayingPositionListener() {
            @Override
            public void OnPlayingPosition(long time) {
                audioPlayerListener.OnPlayingPosition(time);
            }
        });

        // 音频源准备
        return  mAudioPlayer.prepare();
    }


    public void setVoiceBeatingTool(DCVoiceBeatingTool tool) {
        mAudioPlayer.setVoiceBeatingTool(tool);
    }

    public void setAutoRepeat(boolean loop) {
        this.mLoop = loop;
        mAudioPlayer.setAutoRepeat(loop);
    }


    public boolean play() {
        if(mAudioPlayer == null) {
            return  false;
        }
        return  mAudioPlayer.play();
    }

    public boolean pause() {
        if(mAudioPlayer == null) {
            return  false;
        }
        return  mAudioPlayer.pause();
    }

    public boolean stop() {
        if(mAudioPlayer == null) {
            return  false;
        }
        return  mAudioPlayer.stop();
    }

    public long seek(long time) {
        if(mAudioPlayer == null) {
            return  -1;
        }
       return mAudioPlayer.seek(time);
    }

    public void release() {
        if(mAudioPlayer == null) {
            return ;
        }
        mAudioPlayer.release();
    }

    public long getcurrenttime() {
        if(mAudioPlayer == null) {
            return 0 ;
        }
        return     mAudioPlayer.getcurrenttime();

    }


}
