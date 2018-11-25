package com.dc.platform.voicebeating.audiotrack;

import android.media.AudioManager;
import android.media.AudioTimestamp;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IntDef;
import android.util.Log;

import com.dc.platform.voicebeating.DCVoiceBeatingTool;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import static android.media.AudioTrack.WRITE_NON_BLOCKING;

public class AudioPlayer implements  IPlayComplete {

    private static final String TAG = AudioPlayer.class.getSimpleName();

    public static final int STATE_MSG_ID = 0x0010;

    private Handler mHandler;

   public interface PlayingPositionListener{
        void  OnPlayingPosition(long time);
    }

    private PlayingPositionListener timeListener = null;


    public final static class AudioParam {

        public  int frequency;
        public  int channel;
        public  int samplebit;

        public AudioParam(int frequency, int channel, int samplebit) {
            this.frequency = frequency;
            this.channel = channel;
            this.samplebit = samplebit;
        }
    }


    private AudioParam mAudioParam;

    private AudioTrack mAudioTrack;

    private boolean mBReady = false;

    private PlayAudioThread mPlayAudioThread;

    private boolean mThreadExitFlag = false;

    private int mPrimePlaySize = 0;

    private double mPlayOffset = 0.0;

    private boolean mLoop = false;



    public static final int STATE_ERROR = -1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_PREPAREING = 1;
    public static final int STATE_PREPAREED = 2;
    public static final int STATE_PLAYING = 3;
    public static final int STATE_PAUSED = 4;
    public static final int STATE_STOPPED = 5;
    public static final int STATE_COMPLETED = 6;
    public static final int STATE_RELEASED = 7;

    @IntDef({STATE_ERROR, STATE_IDLE, STATE_PREPAREING, STATE_PREPAREED, STATE_PLAYING, STATE_PAUSED,STATE_STOPPED,STATE_COMPLETED,STATE_RELEASED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PLAYSTATE {
    }

    @PLAYSTATE
    private int mPlayState = STATE_IDLE;

    private  DCVoiceBeatingTool  mVoiceBeatingTool = null;


    private final Object mSyncObject = new Object();


    public AudioPlayer(Handler handler,boolean loop) {
        this.mHandler = handler;
        this.mLoop = loop;
    }

    public void setTimeListener(PlayingPositionListener listener) {
        this.timeListener = listener;
    }


    public void setAudioParam(AudioParam audioParam) {
        this.mAudioParam = audioParam;
    }


    public void setVoiceBeatingTool(DCVoiceBeatingTool tool) {
        mVoiceBeatingTool = tool;
    }

    public void setAutoRepeat(boolean loop) {
        this.mLoop = loop;
    }

    public boolean prepare() {

        if (mAudioParam == null) {
            return false;
        }
        setPlayState(STATE_PREPAREING);

        if (mBReady == true) {
            return true;
        }

        createAudioTrack();
        mBReady = true;

        setPlayState(STATE_PREPAREED);
        return true;
    }


    public boolean release() {
        stop();

        releaseAudioTrack();

        mBReady = false;
        setPlayState(STATE_RELEASED);

        return true;
    }


    public boolean play() {

        if (mBReady == false) {
            return false;
        }

        switch (mPlayState) {
            case STATE_PREPAREED:
                mPlayOffset = 0.0;
                setPlayState(STATE_PLAYING);
                startThread();
                break;

            case STATE_PAUSED:
                setPlayState(STATE_PLAYING);
                startThread();
                break;
        }

        return true;
    }


    public boolean pause() {
        if (mBReady == false) {
            return false;
        }


        if (mPlayState == STATE_PLAYING) {
            mAudioTrack.pause();
            setPlayState(STATE_PAUSED);
            stopThread();

        }

        return true;
    }



    public boolean resume() {
        if (mBReady == false) {
            return false;
        }


        if (mPlayState == STATE_PAUSED) {
            mAudioTrack.play();
            setPlayState(STATE_PLAYING);
            startThread();

        }

        return true;
    }


    public boolean stop() {
        if (mBReady == false) {
            return false;
        }

        setPlayState(STATE_STOPPED);
        stopThread();

        return true;
    }

    public boolean restart() {
        if (mBReady == false) {
            return false;
        }
        setPlayState(STATE_PLAYING);
        seek(0);
        synchronized (mSyncObject) {
            mPlayOffset = 0.0;
        }

        startThread();

        return true;
    }

    public long getcurrenttime () {
        if (mBReady == false) {
            return 0;
        }

       return (long)(mPlayOffset /2 *1000 /mAudioParam.frequency);

    }

    public long  seek(long time) {
        if (mBReady == false) {
            return -1;
        }
       long  seekTime = 0;
        synchronized (mSyncObject) {
            mAudioTrack.flush();
            long postion =  (long)(time * mAudioParam.frequency /1000.0f );

            if(mVoiceBeatingTool != null) {
                seekTime = 1000 * mVoiceBeatingTool.audioSeek((int)postion) / mAudioParam.frequency;
                mPlayOffset = seekTime * 2.0 * mAudioParam.frequency /1000;
            }

            //Log.e("seekPosition","mPlayOffset " + mPlayOffset );
        }


        return seekTime;
    }



    private void startThread() {
        if (mPlayAudioThread == null) {
            mThreadExitFlag = false;
            mPlayAudioThread = new PlayAudioThread();
            mPlayAudioThread.start();
        }
    }



    private void stopThread() {
        if (mPlayAudioThread != null) {
            mThreadExitFlag = true;
            try {
                mPlayAudioThread.join();
            } catch (Exception e) {
                e.printStackTrace();
            }

            mPlayAudioThread = null;
        }
    }


    private void releaseAudioTrack() {
        if (mAudioTrack != null) {
            mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }
    }




    private synchronized void setPlayState(int state) {
        mPlayState = state;

        if (mHandler != null) {
            Message msg = mHandler.obtainMessage(STATE_MSG_ID);
            msg.obj = mPlayState;
            msg.sendToTarget();

        }

    }


    private void createAudioTrack() {

        int minBufSize = AudioTrack.getMinBufferSize(mAudioParam.frequency,
                mAudioParam.channel,
                mAudioParam.samplebit);

        mPrimePlaySize = minBufSize * 2;

        Log.d(TAG, "---mPrimePlaySize---" + mPrimePlaySize);


        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                mAudioParam.frequency,
                mAudioParam.channel,
                mAudioParam.samplebit,
                minBufSize,
                AudioTrack.MODE_STREAM
        );

    }


    class PlayAudioThread extends Thread {

        @Override
        public void run() {

            Log.e(TAG, "PlayAudioThread  run mPlayOffset =  " + mPlayOffset);

            mAudioTrack.play();

            while (true) {
                if (mThreadExitFlag == true) {
                    break;
                }


                try {
                        ByteBuffer byteBuffer = null;
                        int size = 0;
                        synchronized (mSyncObject) {
                            byte[] data = mVoiceBeatingTool.getAudioData(mPrimePlaySize);

                            size = data.length;
                            if (size > 0) {
                                byteBuffer = ByteBuffer.wrap(data, 0, size);
                                mAudioTrack.write(byteBuffer, size, AudioTrack.WRITE_BLOCKING);
                                mPlayOffset += mPrimePlaySize;
                            }
                        }


                        if(size > 0) {
                            timeListener.OnPlayingPosition((long)(mPlayOffset * 1000.0f / 2  / mAudioParam.frequency));
                        }

                        if (size == 0) {
                            if(mLoop) {
                                Log.e("seekPosition","position audio " + mPlayOffset / 2 * 1000/mAudioParam.frequency );
                                mPlayOffset = 0;
                                setPlayState(STATE_PREPAREED);
                                //continue;
                            }
                            AudioPlayer.this.onPlayComplete();
                            break;
                        }

                } catch (Exception e) {
                    e.printStackTrace();
                    AudioPlayer.this.onPlayComplete();
                    break;

                }

            }

            mAudioTrack.flush();
            mAudioTrack.stop();

            Log.d(TAG, "PlayAudioThread    complete....");

        }
    }




    @Override
    public void onPlayComplete() {

        mPlayAudioThread = null;
        if (mPlayState != STATE_PAUSED) {
            setPlayState(STATE_COMPLETED);

        }

    }
}
