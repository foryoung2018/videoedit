package com.wmlive.hhvideo.heihei.message.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.example.lamemp3.MP3Recorder;
import com.wmlive.hhvideo.utils.KLog;

import java.io.File;
import java.io.IOException;

/**
 * 录音的播放及录制
 * Created by admin on 2017/3/8.
 */

public class RecorderAndPlayUtil {

    private static final int PLAY = 347;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case PLAY:
                    try {
                        if (mPlayer.isPlaying() && mPlayer.getCurrentPosition() < mPlayer.getDuration()) {
                            mPlayerCompletion.playToPosition(mPlayer.getCurrentPosition() * 100 / mPlayer.getDuration());
                            handler.sendEmptyMessageDelayed(PLAY, 200);
                        } else {
                            mPlayerCompletion.playToPosition(100);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (mPlayerCompletion != null) {
                            mPlayerCompletion.playToPosition(100);
                        }
                    }
            }
        }
    };

    /**
     * 录音播放完成监听
     */
    public interface onPlayerCompletionListener {
        public void onPlayerCompletion();

        public void onPlayerStart();

        public void onPlayerTime(int time);

        void playToPosition(int position);
    }

    private onPlayerCompletionListener mPlayerCompletion;

    public void setOnPlayerCompletionListener(onPlayerCompletionListener l) {
        mPlayerCompletion = l;
    }

    private MediaPlayer mPlayer = null;
    private String mPlayingPath = null;
    private MP3Recorder mMP3Recorder = null;

    public RecorderAndPlayUtil() {
        mPlayer = new MediaPlayer();
        mMP3Recorder = new MP3Recorder();
    }

    /**
     * 开始录制
     */
    public void startRecording() {
        mMP3Recorder.start();
    }

    /**
     * 停止录音
     */
    public void stopRecording() {
        mMP3Recorder.stop();
    }

    /**
     * 暂停录制
     */
    public void pauseRecording() {
        mMP3Recorder.pause();
    }

    /**
     * 继续录制
     */
    public void reStoreRecording() {
        mMP3Recorder.restore();
    }

    public void setMP3RecorderDirPath(String dirspath) {
        if (!TextUtils.isEmpty(dirspath)) {
            mMP3Recorder.setStrFileDirPath(dirspath);
        }
    }

    /**
     * 开始播放
     *
     * @param filePath
     */
    public void startPlaying(String filePath) {
        if (filePath == null) {
            return;
        }
        if (mPlayingPath != null && mPlayingPath.equals(filePath)
                && mPlayer != null && mPlayer.isPlaying()) {
            stopPlaying();
            mPlayingPath = null;
            return;
        }
        mPlayingPath = filePath;
        stopPlaying();
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayerCompletion.onPlayerCompletion();
            }
        });

        try {
            mPlayer.setDataSource(filePath);
            mPlayer.prepare();
            mPlayerCompletion.onPlayerTime(mPlayer.getDuration());
            mPlayer.start();
            mPlayerCompletion.onPlayerStart();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        handler.sendEmptyMessage(PLAY);
    }

    /**
     *
     */
    public void pausePlaying() {
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
            }
        }
    }

    /**
     * 继续播放
     */
    public void reStartPalaying() {
        if (mPlayer != null) {
            mPlayer.start();
        }

    }

    /**
     * 停止播放
     */
    public void stopPlaying() {
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
            }
        }
    }

    /**
     * 获取当期那播放器播放位置
     *
     * @return
     */
    public int getPlayCurrentPosition() {
        int position = -1;
        if (mPlayer != null && mPlayer.isPlaying()) {
            position = mPlayer.getCurrentPosition();
        }
        return position;
    }

    /**
     * 释放所有资源
     */
    public void release() {
        stopRecording();
        if (mPlayer != null) {
            try {
                if (mPlayer.isPlaying()) {
                    mPlayer.stop();
                }
                mPlayer.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取录音地址
     *
     * @return
     */
    public String getRecorderPath() {
        return mMP3Recorder.getFilePath();
    }

    /**
     * 获取录制对象
     *
     * @return
     */
    public MP3Recorder getRecorder() {
        return mMP3Recorder;
    }

    public boolean getRecorderStatus() {
        if (mMP3Recorder != null) {
            return mMP3Recorder.isRecording();
        } else {
            return false;
        }
    }

    /**
     * 是否为暂停状态
     *
     * @return
     */
    public boolean isRecordPauseStatus() {
        if (mMP3Recorder != null) {
            return mMP3Recorder.isPaus();
        } else {
            return false;
        }
    }

    /**
     * 删除MP3文件
     */
    public void delMp3File() {
        try {
            String strMp3Path = getRecorderPath();
            if (!TextUtils.isEmpty(strMp3Path)) {
                File mMp3File = new File(strMp3Path);
                if (mMp3File != null && mMp3File.exists()) {
                    mMp3File.delete();
                }
            }
        } catch (Exception e) {

        }

    }

    /**
     * 判断是是否有录音权限
     */
    public static boolean isRecordeHasPermission() {
        int audioSource = MediaRecorder.AudioSource.MIC;// 音频获取源
        // 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
        int sampleRateInHz = 44100;
        //设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
        int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
        // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int bufferSizeInBytes = 0;// 缓冲区字节大小
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
        //开始录制音频
        try {
            AudioRecord audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
            // 防止某些手机崩溃，例如联想
            audioRecord.startRecording();
            KLog.d("xxxx", "audioRecord.getRecordingState() " + audioRecord.getRecordingState());
            /**
             * 根据开始录音判断是否有录音权限
             */
            if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING && audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED) {
                return false;
            }
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
