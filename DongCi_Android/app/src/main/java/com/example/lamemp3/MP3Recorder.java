package com.example.lamemp3;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;

import com.wmlive.hhvideo.utils.AppCacheFileUtils;
import com.wmlive.hhvideo.utils.KLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Random;

/**
 * 录音
 * Created by admin on 2017/3/8.
 */

public class MP3Recorder {

    private String mFilePath = null;//录音文件存放路径
    private int sampleRate = 0;
    private boolean isRecording = false;
    private boolean isPause = false;
    private Handler handler = null;
    private String strFileDirPath = AppCacheFileUtils.getAppAudioCachePath() + "mp3/";
    private static final int defaultRecordMinValue = 20;
    private int iCurrentDBMinValue = defaultRecordMinValue;

    /**
     * 开始录音
     */
    public static final int MSG_REC_STARTED = 1;

    /**
     * 结束录音
     */
    public static final int MSG_REC_STOPPED = 2;

    /**
     * 暂停录音
     */
    public static final int MSG_REC_PAUSE = 3;

    /**
     * 继续录音
     */
    public static final int MSG_REC_RESTORE = 4;

    /**
     * 缓冲区挂了,采样率手机不支持
     */
    public static final int MSG_ERROR_GET_MIN_BUFFERSIZE = -1;

    /**
     * 创建文件时扑街了
     */
    public static final int MSG_ERROR_CREATE_FILE = -2;

    /**
     * 初始化录音器时扑街了
     */
    public static final int MSG_ERROR_REC_START = -3;

    /**
     * 录音的时候出错
     */
    public static final int MSG_ERROR_AUDIO_RECORD = -4;

    /**
     * 编码时挂了
     */
    public static final int MSG_ERROR_AUDIO_ENCODE = -5;

    /**
     * 写文件时挂了
     */
    public static final int MSG_ERROR_WRITE_FILE = -6;

    /**
     * 没法关闭文件流
     */
    public static final int MSG_ERROR_CLOSE_FILE = -7;


    public RecordMessageInterface mRecordMessageInterface;

    /**
     * 录音信息回调接口
     */
    public interface RecordMessageInterface {
        /**
         * 录音分贝
         *
         * @param volumeValue
         */
        public void onRecordVolumeValue(int volumeValue);
    }

    Object mLock;

    public MP3Recorder() {
        //this.sampleRate = 8000;
        this.sampleRate = 44100;
        mLock = new Object();
    }

    public void setRecordMessageInterface(RecordMessageInterface obj) {
        mRecordMessageInterface = obj;
    }

    public String getStrFileDirPath() {
        return strFileDirPath;
    }

    public void setStrFileDirPath(String strFileDirPath) {
        this.strFileDirPath = strFileDirPath;
    }

    /**
     * 开片
     */
    public void start() {
        if (isRecording) {
            return;
        }

        new Thread() {
            @Override
            public void run() {
                try {
                    File dir = new File(getStrFileDirPath());
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                } catch (Exception e) {
                    strFileDirPath = AppCacheFileUtils.getAppAudioCachePath();
                }
                iCurrentDBMinValue = defaultRecordMinValue;
                mFilePath = strFileDirPath + System.currentTimeMillis() + ".mp3";
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
                // 根据定义好的几个配置，来获取合适的采集数据需要的缓冲区的大小缓冲大小(sampleRate： 采样率 ，channelConfig：声道设置， audioFormat：编码制式和采样大小  )
                final int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
                if (minBufferSize < 0) {
                    if (handler != null) {
                        handler.sendEmptyMessage(MSG_ERROR_GET_MIN_BUFFERSIZE);
                    }
                    return;
                }
                AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize * 2);

                // 0.5秒的缓冲
                int bufferSize = (sampleRate * (16 / 8) * 1 * 5) / 10;
                short[] buffer = new short[bufferSize];
                byte[] mp3buffer = new byte[(int) (7200 + buffer.length * 2 * 1.25)];

                FileOutputStream output = null;
                try {
                    output = new FileOutputStream(new File(mFilePath));
                } catch (FileNotFoundException e) {
                    if (handler != null) {
                        handler.sendEmptyMessage(MSG_ERROR_CREATE_FILE);
                    }
                    return;
                }
                MP3Recorder.init(sampleRate, 1, sampleRate, 32);
                isRecording = true; // 录音状态
                isPause = false; // 录音状态
                try {
                    try {
                        audioRecord.startRecording(); // 开启录音获取音频数据
                    } catch (Exception e) {
                        // 不给录音...
                        if (handler != null) {
                            handler.sendEmptyMessage(MSG_ERROR_REC_START);
                        }
                        return;
                    }

                    try {
                        // 开始录音
                        if (handler != null) {
                            handler.sendEmptyMessage(MSG_REC_STARTED);
                        }

                        int readSize = 0;
                        boolean pause = false;
                        while (isRecording) {
                            /*--暂停--*/
                            if (isPause) {
                                if (!pause) {
                                    handler.sendEmptyMessage(MSG_REC_PAUSE);
                                    pause = true;
                                }
                                continue;
                            }
                            if (pause) {
                                handler.sendEmptyMessage(MSG_REC_RESTORE);
                                pause = false;
                            }
                            /*--End--*/
                            /*--实时录音写数据--*/
                            readSize = audioRecord.read(buffer, 0, minBufferSize);

                            if (readSize < 0) {
                                if (handler != null) {
                                    handler.sendEmptyMessage(MSG_ERROR_AUDIO_RECORD);
                                }
                                break;
                            } else if (readSize == 0) {
                                //正常
                                int i = 0;
                            } else {
                                int encResult = MP3Recorder.encode(buffer, buffer, readSize, mp3buffer);
                                if (encResult < 0) {
                                    if (handler != null) {
                                        handler.sendEmptyMessage(MSG_ERROR_AUDIO_ENCODE);
                                    }
                                    break;
                                }
                                if (encResult != 0) {
                                    try {
                                        output.write(mp3buffer, 0, encResult);
                                    } catch (Exception e) {
                                        if (handler != null) {
                                            handler.sendEmptyMessage(MSG_ERROR_WRITE_FILE);
                                        }
                                        break;
                                    }
                                }
                            }
                            //获取当前分贝数
//                            getVoiceSize(readSize, buffer);

                            if (null != mRecordMessageInterface) {
                                mRecordMessageInterface.onRecordVolumeValue(getVoiceLevel(readSize, buffer));
                            }

//                            // 计算分贝
//                            long v = 0;
//                            // 将 buffer 内容取出，进行平方和运算
//                            for (int i = 0; i < buffer.length; i++) {
//                                v += buffer[i] * buffer[i];
//                            }
//                            // 平方和除以数据总长度，得到音量大小。
//                            double mean = v / (double) readSize;
//                            double volume = 10 * Math.log10(mean);
//                            KLog.e("MP3Recorder", "分贝值:" + volume);
//                            if(null!=mRecordMessageInterface){
//                                mRecordMessageInterface.onRecordVolumeValue(volume);
//                            }
                            /*--End--*/
                        }
                        /*--录音完--*/
                        int flushResult = MP3Recorder.flush(mp3buffer);
                        if (flushResult < 0) {
                            if (handler != null) {
                                handler.sendEmptyMessage(MSG_ERROR_AUDIO_ENCODE);
                            }
                        }
                        if (flushResult != 0) {
                            try {
                                output.write(mp3buffer, 0, flushResult);
                            } catch (Exception e) {
                                if (handler != null) {
                                    handler.sendEmptyMessage(MSG_ERROR_WRITE_FILE);
                                }
                            }
                        }
                        try {
                            output.close();
                        } catch (Exception e) {
                            if (handler != null) {
                                handler.sendEmptyMessage(MSG_ERROR_CLOSE_FILE);
                            }
                        }
                        /*--End--*/
                    } finally {
                        audioRecord.stop();
                        audioRecord.release();
                    }
                } finally {
                    MP3Recorder.close();
                    isRecording = false;
                }
                if (handler != null) {
                    handler.sendEmptyMessage(MSG_REC_STOPPED);
                }
            }
        }.start();
    }

    public void stop() {
        isRecording = false;
    }

    public void pause() {
        isPause = true;
        //mLock.notify();
    }

    public void restore() {
        isPause = false;
        //mLock.notify();
    }

    public boolean isRecording() {
        return isRecording;
    }

    public boolean isPaus() {
        if (!isRecording) {
            return false;
        }
        return isPause;
    }

    public String getFilePath() {
        return mFilePath;
    }

    /**
     * 采样值为16bit时，根据pcm数据获取分贝
     *
     * @param readSize
     * @param buffer
     * @return
     */
    public int getVoiceLevel(int readSize, short[] buffer) {
        double sample;
        double sum = 0;
        for (short rawSample : buffer) {
            sample = rawSample / 65535.0;
            sum += sample * sample;
        }
        double rms = Math.sqrt(sum / readSize);
        double db = 20 * Math.log10(rms) + 50;
        return (int) (db * 3);
    }

    // 获得声音的level
    public int getVoiceSize(int r, short[] buffer) {
        if (isRecording) {
            try {
                long v = 0;
                // 将 buffer 内容取出，进行平方和运算
                for (int i = 0; i < buffer.length; i++) {
                    v += buffer[i] * buffer[i];
                }
                // 平方和除以数据总长度，得到音量大小。
                double mean = v / (double) r;
                double volume = 10 * Math.log10(mean);
                //int volumeValue = (((int) volume / 10) - 1);
                int volumeValue = (int) Math.floor(volume);
                int ivalue = 0;//当前音贝值
                try {
                    ivalue = (volumeValue - iCurrentDBMinValue) * 100 / (90 - iCurrentDBMinValue);
                    ivalue = Math.abs(ivalue);
                } catch (Exception e) {

                } finally {
                    if (ivalue <= 0) {
                        Random random = new Random();
                        ivalue = 8 + random.nextInt(5);
                    }
                }
                if (volumeValue < iCurrentDBMinValue) {
                    iCurrentDBMinValue = volumeValue;
                }
                KLog.e("MP3Recorder", "分贝值:" + ivalue);
                if (null != mRecordMessageInterface && ivalue > 0) {
                    mRecordMessageInterface.onRecordVolumeValue(ivalue);
                }
                return ivalue;
            } catch (Exception e) {

            }
        }
        return 1;
    }

    /**
     * 录音状态管理
     *
     * @see RecMicToMp3#MSG_REC_STARTED
     * @see RecMicToMp3#MSG_REC_STOPPED
     * @see RecMicToMp3#MSG_REC_PAUSE
     * @see RecMicToMp3#MSG_REC_RESTORE
     * @see RecMicToMp3#MSG_ERROR_GET_MIN_BUFFERSIZE
     * @see RecMicToMp3#MSG_ERROR_CREATE_FILE
     * @see RecMicToMp3#MSG_ERROR_REC_START
     * @see RecMicToMp3#MSG_ERROR_AUDIO_RECORD
     * @see RecMicToMp3#MSG_ERROR_AUDIO_ENCODE
     * @see RecMicToMp3#MSG_ERROR_WRITE_FILE
     * @see RecMicToMp3#MSG_ERROR_CLOSE_FILE
     */
    public void setHandle(Handler handler) {
        this.handler = handler;
    }

    /*--以下为Native部分--*/
    static {
        System.loadLibrary("mp3lame");
    }

    /**
     * 初始化录制参数
     */
    public static void init(int inSamplerate, int outChannel,
                            int outSamplerate, int outBitrate) {
        init(inSamplerate, outChannel, outSamplerate, outBitrate, 7);
    }

    /**
     * 初始化录制参数 quality:0=很好很慢 9=很差很快
     */
    public native static void init(int inSamplerate, int outChannel,
                                   int outSamplerate, int outBitrate, int quality);

    /**
     * 音频数据编码(PCM左进,PCM右进,MP3输出)
     */
    public native static int encode(short[] buffer_l, short[] buffer_r,
                                    int samples, byte[] mp3buf);

    /**
     * 刷干净缓冲区
     */
    public native static int flush(byte[] mp3buf);

    /**
     * 结束编码
     */
    public native static void close();
}

