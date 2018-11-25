/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.loopback;

import android.Manifest;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Locale;


/**
 * This is the main activity of the Loopback app. Two tests (latency test and buffer test) can be
 * initiated here. Note: buffer test and glitch detection is the same test, it's just that this test
 * has two parts of result.
 */

public class DCLoopbackTool {
    private static final String TAG = "LoopbackActivity";

    private static final int SAVE_TO_WAVE_REQUEST = 42;
    private static final int SAVE_TO_PNG_REQUEST = 43;
    private static final int SAVE_TO_TXT_REQUEST = 44;
    private static final int SAVE_RECORDER_BUFFER_PERIOD_TO_TXT_REQUEST = 45;
    private static final int SAVE_PLAYER_BUFFER_PERIOD_TO_TXT_REQUEST = 46;
    private static final int SAVE_RECORDER_BUFFER_PERIOD_TO_PNG_REQUEST = 47;
    private static final int SAVE_PLAYER_BUFFER_PERIOD_TO_PNG_REQUEST = 48;
    private static final int SAVE_RECORDER_BUFFER_PERIOD_TIMES_TO_TXT_REQUEST = 49;
    private static final int SAVE_PLAYER_BUFFER_PERIOD_TIMES_TO_TXT_REQUEST = 50;
    private static final int SAVE_GLITCH_OCCURRENCES_TO_TEXT_REQUEST = 51;
    private static final int SAVE_GLITCH_AND_CALLBACK_HEATMAP_REQUEST = 52;

    private static final int SETTINGS_ACTIVITY_REQUEST = 54;

    private static final int THREAD_SLEEP_DURATION_MS = 200;
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO_LATENCY = 201;
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO_BUFFER = 202;
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_RESULTS = 203;
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SCRIPT = 204;
    private static final int LATENCY_TEST_STARTED = 300;
    private static final int LATENCY_TEST_ENDED = 301;
    private static final int BUFFER_TEST_STARTED = 302;
    private static final int BUFFER_TEST_ENDED = 303;
    private static final int CALIBRATION_STARTED = 304;
    private static final int CALIBRATION_ENDED = 305;
    private static final int NO_TEST_ACTIVE = 306;
    private static final int MESSAGE_TIMEOUT = 307;

    // 0-100 controls compression rate, currently ignore because PNG format is being used
    private static final int EXPORTED_IMAGE_QUALITY = 100;

    private static final int HISTOGRAM_EXPORT_WIDTH = 2000;
    private static final int HISTOGRAM_EXPORT_HEIGHT = 2000;
    private static final int CONSTANT_TIMEOUT = 6000;
    private static final int HEATMAP_DRAW_WIDTH = 2560;
    private static final int HEATMAP_DRAW_HEIGHT = 1440;
    private static final int HEATMAP_EXPORT_DIVISOR = 2;

    public static LoopbackAudioThread mAudioThread = null;
    public static NativeAudioThread mNativeAudioThread = null;
    public static Thread mCalibrationThread;
    //    public static WavePlotView mWavePlotView;
    public static String mTestStartTimeString = "IncorrectTime";  // The time the test begins
    public static final String FILE_SAVE_PATH = "file://mnt/sdcard/";

    public static TextView mTextInfo;
    public static TextView mTextViewCurrentLevel;
    public static TextView mTextViewResultSummary;

    public static final String TAG_RETAINED_FRAGMENT = "RetainedFragment";
    //    public static RetainedFragment mRetainedFragment;
    public static int mTestType;
    public static Correlation mCorrelation = new Correlation();
    public static BufferPeriod mRecorderBufferPeriod = new BufferPeriod();
    public static BufferPeriod mPlayerBufferPeriod = new BufferPeriod();

    // for native buffer period
    public static int[] mNativeRecorderBufferPeriodArray;
    public static int mNativeRecorderMaxBufferPeriod;
    public static double mNativeRecorderStdDevBufferPeriod;
    public static int[] mNativePlayerBufferPeriodArray;
    public static int mNativePlayerMaxBufferPeriod;
    public static double mNativePlayerStdDevBufferPeriod;
    public static BufferCallbackTimes mRecorderCallbackTimes;
    public static BufferCallbackTimes mPlayerCallbackTimes;

    public static final String INTENT_SAMPLING_FREQUENCY = "SF";
    public static final String INTENT_CHANNEL_INDEX = "CI";
    public static final String INTENT_CORRELATION_BLOCK_SIZE = "BS";
    public static final String INTENT_FILENAME = "FileName";
    public static final String INTENT_RECORDER_BUFFER = "RecorderBuffer";
    public static final String INTENT_PLAYER_BUFFER = "PlayerBuffer";
    public static final String INTENT_AUDIO_THREAD = "AudioThread";
    public static final String INTENT_MIC_SOURCE = "MicSource";
    public static final String INTENT_PERFORMANCE_MODE = "PerformanceMode";
    public static final String INTENT_AUDIO_LEVEL = "AudioLevel";
    public static final String INTENT_IGNORE_FIRST_FRAMES = "IgnoreFirstFrames";
    public static final String INTENT_TEST_TYPE = "TestType";
    public static final String INTENT_BUFFER_TEST_DURATION = "BufferTestDuration";
    public static final String INTENT_NUMBER_LOAD_THREADS = "NumLoadThreads";
    public static final String INTENT_ENABLE_SYSTRACE = "CaptureSysTrace";
    public static final String INTENT_ENABLE_WAVCAPTURE = "CaptureWavs";
    public static final String INTENT_NUM_CAPTURES = "NumCaptures";
    public static final String INTENT_WAV_DURATION = "WavDuration";

    // for running the test using adb command
    public static volatile boolean mIntentRunning; // if it is running triggered by intent with parameters
    public static String mIntentFileName;
    public static volatile boolean isDCLoopbackTestOK;

    // Note: these values should only be assigned in restartAudioSystem()
    public static int mAudioThreadType = Constant.UNKNOWN;
    public static int mMicSource;
    public static int mPerformanceMode;
    public static int mSamplingRate;
    public static int mChannelIndex;
    public static int mSoundLevel;
    public static int mPlayerBufferSizeInBytes;
    public static int mRecorderBufferSizeInBytes;
    public static int mIgnoreFirstFrames; // TODO: this only applies to native mode
    public static CaptureHolder mCaptureHolder;

    // for buffer test
    public static int[] mGlitchesData;
    public static boolean mGlitchingIntervalTooLong;
    public static int mFFTSamplingSize;
    public static int mFFTOverlapSamples;
    public static long mBufferTestStartTime;
    public static int mBufferTestElapsedSeconds;
    public static int mBufferTestDurationInSeconds;
    public static int mBufferTestWavePlotDurationInSeconds;

    // threads that load CPUs
    public static LoadThread[] mLoadThreads;

    public static int systemVolumeLevel;

    public static boolean noVolume = true;
    private static boolean bound = false;

    // for getting the Service
    public static boolean mBound = false;
    public static AudioTestService mAudioTestService;
    public static final ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mAudioTestService = ((AudioTestService.AudioTestBinder) service).getService();
            mBound = true;
//            doCalibration();
            startlatencytest();
        }

        public void onServiceDisconnected(ComponentName className) {
            mAudioTestService = null;
            mBound = false;
        }
    };

    public static Handler mMessageHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case LoopbackAudioThread.LOOPBACK_AUDIO_THREAD_MESSAGE_LATENCY_REC_STARTED:
                    log("got message java latency test started!!");
                    log("Java Latency Test Started");
                    resetResults();
//                    refreshState();
                    refreshPlots();
                    break;

                case LoopbackAudioThread.LOOPBACK_AUDIO_THREAD_MESSAGE_LATENCY_REC_ERROR:
                    log("got message java latency test rec can't start!!");
                    log("Java Latency Test Recording Error. Please try again");
//                    refreshState();
                    stopAudioTestThreads();
                    mIntentRunning = false;
//                    refreshSoundLevelBar();
                    break;

                case LoopbackAudioThread.LOOPBACK_AUDIO_THREAD_MESSAGE_LATENCY_REC_STOP:
                case LoopbackAudioThread.LOOPBACK_AUDIO_THREAD_MESSAGE_LATENCY_REC_COMPLETE:
                    if (mAudioThread != null) {
//                        mRetainedFragment.setWaveData(mAudioThread.getWaveData());
                        mRecorderCallbackTimes = mRecorderBufferPeriod.getCallbackTimes();
                        mPlayerCallbackTimes = mPlayerBufferPeriod.getCallbackTimes();
//                        mCorrelation.computeCorrelation(mRetainedFragment.getWaveData(), mSamplingRate);
                        log("got message java latency rec complete!!");
                        refreshPlots();
//                        refreshState();

                        switch (msg.what) {
                            case LoopbackAudioThread.LOOPBACK_AUDIO_THREAD_MESSAGE_LATENCY_REC_STOP:
                                log("Java Latency Test Stopped");
                                break;
                            case LoopbackAudioThread.LOOPBACK_AUDIO_THREAD_MESSAGE_LATENCY_REC_COMPLETE:
                                log("Java Latency Test Completed");
                                break;
                        }

                        stopAudioTestThreads();
                        if (mIntentRunning && mIntentFileName != null && mIntentFileName.length() > 0) {
                            saveAllTo(mIntentFileName);
                        }
                        mIntentRunning = false;
                    }
//                    refreshSoundLevelBar();
                    break;

                case LoopbackAudioThread.LOOPBACK_AUDIO_THREAD_MESSAGE_BUFFER_REC_STARTED:
                    log("got message java buffer test rec started!!");
                    log("Java Buffer Test Started");
                    resetResults();
//                    refreshState();
                    refreshPlots();
                    mBufferTestStartTime = System.currentTimeMillis();
                    break;

                case LoopbackAudioThread.LOOPBACK_AUDIO_THREAD_MESSAGE_BUFFER_REC_ERROR:
                    log("got message java buffer test rec can't start!!");
                    log("Java Buffer Test Recording Error. Please try again");
//                    refreshState();
                    stopAudioTestThreads();
                    mIntentRunning = false;
//                    refreshSoundLevelBar();
                    break;

                case LoopbackAudioThread.LOOPBACK_AUDIO_THREAD_MESSAGE_BUFFER_REC_STOP:
                case LoopbackAudioThread.LOOPBACK_AUDIO_THREAD_MESSAGE_BUFFER_REC_COMPLETE:
                    if (mAudioThread != null) {
//                        mRetainedFragment.setWaveData(mAudioThread.getWaveData());
                        mGlitchesData = mAudioThread.getAllGlitches();
                        mGlitchingIntervalTooLong = mAudioThread.getGlitchingIntervalTooLong();
                        mFFTSamplingSize = mAudioThread.getFFTSamplingSize();
                        mFFTOverlapSamples = mAudioThread.getFFTOverlapSamples();
                        mRecorderCallbackTimes = mRecorderBufferPeriod.getCallbackTimes();
                        mPlayerCallbackTimes = mPlayerBufferPeriod.getCallbackTimes();
                        refreshPlots();  // only plot that last few seconds
//                        refreshState();
                        //rounded up number of seconds elapsed
                        mBufferTestElapsedSeconds =
                                (int) ((System.currentTimeMillis() - mBufferTestStartTime +
                                        Constant.MILLIS_PER_SECOND - 1) / Constant.MILLIS_PER_SECOND);
                        switch (msg.what) {
                            case LoopbackAudioThread.LOOPBACK_AUDIO_THREAD_MESSAGE_BUFFER_REC_STOP:
                                log("Java Buffer Test Stopped");
                                break;
                            case LoopbackAudioThread.LOOPBACK_AUDIO_THREAD_MESSAGE_BUFFER_REC_COMPLETE:
                                log("Java Buffer Test Completed");
                                break;
                        }
                        if (getApp().isCaptureEnabled()) {
                            mCaptureHolder.stopLoopbackListenerScript();
                        }
                        stopAudioTestThreads();
                        if (mIntentRunning && mIntentFileName != null && mIntentFileName.length() > 0) {
                            saveAllTo(mIntentFileName);
                        }
                        mIntentRunning = false;
                    }
//                    refreshSoundLevelBar();
                    break;

                case NativeAudioThread.LOOPBACK_NATIVE_AUDIO_THREAD_MESSAGE_LATENCY_REC_STARTED:
                    log("got message native latency test rec started!!");
//                    showToast("Native Latency Test Started");
                    resetResults();
//                    refreshState();
                    refreshPlots();
                    break;

                case NativeAudioThread.LOOPBACK_NATIVE_AUDIO_THREAD_MESSAGE_BUFFER_REC_STARTED:
                    log("got message native buffer test rec started!!");
                    log("Native Buffer Test Started");
//                    resetResults();
//                    refreshState();
//                    refreshPlots();
                    mBufferTestStartTime = System.currentTimeMillis();
                    break;

                case NativeAudioThread.LOOPBACK_NATIVE_AUDIO_THREAD_MESSAGE_LATENCY_REC_ERROR:
                    log("got message native latency test rec can't start!!");
                    log("Native Latency Test Recording Error. Please try again");
//                    refreshState();
                    mIntentRunning = false;
//                    refreshSoundLevelBar();
                    break;

                case NativeAudioThread.LOOPBACK_NATIVE_AUDIO_THREAD_MESSAGE_BUFFER_REC_ERROR:
                    log("got message native buffer test rec can't start!!");
                    log("Native Buffer Test Recording Error. Please try again");
//                    refreshState();
                    mIntentRunning = false;
//                    refreshSoundLevelBar();
                    break;

                case NativeAudioThread.LOOPBACK_NATIVE_AUDIO_THREAD_MESSAGE_BUFFER_REC_STOP:
                case NativeAudioThread.LOOPBACK_NATIVE_AUDIO_THREAD_MESSAGE_LATENCY_REC_STOP:
                case NativeAudioThread.LOOPBACK_NATIVE_AUDIO_THREAD_MESSAGE_BUFFER_REC_COMPLETE:
                case NativeAudioThread.LOOPBACK_NATIVE_AUDIO_THREAD_MESSAGE_LATENCY_REC_COMPLETE:
                case NativeAudioThread.LOOPBACK_NATIVE_AUDIO_THREAD_MESSAGE_BUFFER_REC_COMPLETE_ERRORS:
                case NativeAudioThread.LOOPBACK_NATIVE_AUDIO_THREAD_MESSAGE_LATENCY_REC_COMPLETE_ERRORS:
                    if (mNativeAudioThread != null) {
                        mGlitchesData = mNativeAudioThread.getNativeAllGlitches();
                        mGlitchingIntervalTooLong = mNativeAudioThread.getGlitchingIntervalTooLong();
                        mFFTSamplingSize = mNativeAudioThread.getNativeFFTSamplingSize();
                        mFFTOverlapSamples = mNativeAudioThread.getNativeFFTOverlapSamples();
//                        mRetainedFragment.setWaveData(mNativeAudioThread.getWaveData());
                        mNativeRecorderBufferPeriodArray = mNativeAudioThread.getRecorderBufferPeriod();
                        mNativeRecorderMaxBufferPeriod =
                                mNativeAudioThread.getRecorderMaxBufferPeriod();
                        mNativeRecorderStdDevBufferPeriod =
                                mNativeAudioThread.getRecorderStdDevBufferPeriod();
                        mNativePlayerBufferPeriodArray = mNativeAudioThread.getPlayerBufferPeriod();
                        mNativePlayerMaxBufferPeriod = mNativeAudioThread.getPlayerMaxBufferPeriod();
                        mNativePlayerStdDevBufferPeriod =
                                mNativeAudioThread.getPlayerStdDevBufferPeriod();
                        mRecorderCallbackTimes = mNativeAudioThread.getRecorderCallbackTimes();
                        mPlayerCallbackTimes = mNativeAudioThread.getPlayerCallbackTimes();
                        Log.i("===yang","mCorrelation 0 : " + mCorrelation.mAverage);
                        if (msg.what != NativeAudioThread.
                                LOOPBACK_NATIVE_AUDIO_THREAD_MESSAGE_BUFFER_REC_COMPLETE) {
                            mCorrelation.computeCorrelation(mNativeAudioThread.getWaveData(),
                                    mSamplingRate);
                        }
                        Log.i("===yang","mCorrelation 1 : " + mCorrelation.mAverage);

                        log("got message native buffer test rec complete!!");
                        refreshPlots();
//                        refreshState();
                        //rounded up number of seconds elapsed
                        mBufferTestElapsedSeconds =
                                (int) ((System.currentTimeMillis() - mBufferTestStartTime +
                                        Constant.MILLIS_PER_SECOND - 1) / Constant.MILLIS_PER_SECOND);
                        switch (msg.what) {
                            case NativeAudioThread.
                                    LOOPBACK_NATIVE_AUDIO_THREAD_MESSAGE_BUFFER_REC_COMPLETE_ERRORS:
                            case NativeAudioThread.
                                    LOOPBACK_NATIVE_AUDIO_THREAD_MESSAGE_LATENCY_REC_COMPLETE_ERRORS:
                                log("Native Test Completed with Fatal Errors");
                                break;
                            case NativeAudioThread.LOOPBACK_NATIVE_AUDIO_THREAD_MESSAGE_BUFFER_REC_STOP:
                            case NativeAudioThread.
                                    LOOPBACK_NATIVE_AUDIO_THREAD_MESSAGE_LATENCY_REC_STOP:
                                log("Native Test Stopped");
                                break;
                            default:
                                Log.d("sun", "complete-->" + mCorrelation.mAverage);
//                                showToast("Native Test Completed");
                                testCompleted(mCorrelation);
                                break;
                        }


                        stopAudioTestThreads();
                        if (mIntentRunning && mIntentFileName != null && mIntentFileName.length() > 0) {
                            saveAllTo(mIntentFileName);
                        }
                        mIntentRunning = false;

                        if (getApp().isCaptureEnabled()) {
                            mCaptureHolder.stopLoopbackListenerScript();
                        }
                    }  // mNativeAudioThread != null
//                    refreshSoundLevelBar();
                    break;

                default:
                    log("Got message:" + msg.what);
                    break;
            }

            // Control UI elements visibility specific to latency or buffer/glitch test
            switch (msg.what) {

                // Latency test started
                case LoopbackAudioThread.LOOPBACK_AUDIO_THREAD_MESSAGE_LATENCY_REC_STARTED:
                case NativeAudioThread.LOOPBACK_NATIVE_AUDIO_THREAD_MESSAGE_LATENCY_REC_STARTED:
                    /*
                    setTransportButtonsState(LATENCY_TEST_STARTED);
                    */
                    break;

                // Latency test ended
                case LoopbackAudioThread.LOOPBACK_AUDIO_THREAD_MESSAGE_LATENCY_REC_COMPLETE:
                case LoopbackAudioThread.LOOPBACK_AUDIO_THREAD_MESSAGE_LATENCY_REC_ERROR:
                case LoopbackAudioThread.LOOPBACK_AUDIO_THREAD_MESSAGE_LATENCY_REC_STOP:
                case NativeAudioThread.LOOPBACK_NATIVE_AUDIO_THREAD_MESSAGE_LATENCY_REC_ERROR:
                case NativeAudioThread.LOOPBACK_NATIVE_AUDIO_THREAD_MESSAGE_LATENCY_REC_COMPLETE:
                case NativeAudioThread.LOOPBACK_NATIVE_AUDIO_THREAD_MESSAGE_LATENCY_REC_STOP:
                case NativeAudioThread.
                        LOOPBACK_NATIVE_AUDIO_THREAD_MESSAGE_LATENCY_REC_COMPLETE_ERRORS:
                    /*
                    setTransportButtonsState(LATENCY_TEST_ENDED);
                    */
                    break;

                // Buffer test started
                case LoopbackAudioThread.LOOPBACK_AUDIO_THREAD_MESSAGE_BUFFER_REC_STARTED:
                case NativeAudioThread.LOOPBACK_NATIVE_AUDIO_THREAD_MESSAGE_BUFFER_REC_STARTED:
                    /*
                    setTransportButtonsState(BUFFER_TEST_STARTED);
                    */
                    break;

                // Buffer test ended
                case LoopbackAudioThread.LOOPBACK_AUDIO_THREAD_MESSAGE_BUFFER_REC_COMPLETE:
                case LoopbackAudioThread.LOOPBACK_AUDIO_THREAD_MESSAGE_BUFFER_REC_ERROR:
                case LoopbackAudioThread.LOOPBACK_AUDIO_THREAD_MESSAGE_BUFFER_REC_STOP:
                case NativeAudioThread.LOOPBACK_NATIVE_AUDIO_THREAD_MESSAGE_BUFFER_REC_ERROR:
                case NativeAudioThread.LOOPBACK_NATIVE_AUDIO_THREAD_MESSAGE_BUFFER_REC_COMPLETE:
                case NativeAudioThread.LOOPBACK_NATIVE_AUDIO_THREAD_MESSAGE_BUFFER_REC_STOP:
                case NativeAudioThread.
                        LOOPBACK_NATIVE_AUDIO_THREAD_MESSAGE_BUFFER_REC_COMPLETE_ERRORS:
                    /*
                    setTransportButtonsState(BUFFER_TEST_ENDED);
                    */
                    break;

                // Sound Calibration started
                case CALIBRATION_STARTED:
                    /*
                    setTransportButtonsState(CALIBRATION_STARTED);
                    */
                    break;

                // Sound Calibration ended
                case CALIBRATION_ENDED:
                    /*
                    setTransportButtonsState(CALIBRATION_ENDED);
                    */
                    break;
                case MESSAGE_TIMEOUT:
                    stopBind();
                    destroy();
                    break;

                default:
                    log("Got message:" + msg.what);
                    break;
            }
        }
    };

    //****************** testCompleted ******************//

    /**
     * 裁剪时间 ，ms
     */
    public static long cutTime = 0;

    public static int testCount = 0;

    public static SharedPreferences sharedPreferences;
    public static void setValue(long value){
        sharedPreferences = loopbackContext.getSharedPreferences("loopback", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("mAverage",value);
        editor.commit();
    }

    public static long getValue(){
        sharedPreferences = loopbackContext.getSharedPreferences("loopback", Context.MODE_PRIVATE);
        return sharedPreferences.getLong("mAverage",50);
    }

    public static void testCompleted(Correlation mCorrelation) {
        testCount ++;
//        Log.i("===yang","  testCompleted  testCount=  " + testCount);
        cutTime = (long) mCorrelation.mAverage * 1000;
        Log.i("DCLoopbackTool", "mRms:" + mCorrelation.mRms);
        Log.i("DCLoopbackTool", "mAverage:" + mCorrelation.mAverage);
        Log.i("DCLoopbackTool", "mEstimatedLatencyMs:" + mCorrelation.mEstimatedLatencyMs);
        Log.i("DCLoopbackTool", "mEstimatedLatencySamples:" + mCorrelation.mEstimatedLatencySamples);
        Log.i("DCLoopbackTool", "mEstimatedLatencyConfidence:" + mCorrelation.mEstimatedLatencyConfidence);
        if(mCorrelation.mAverage==0){//测试失败，重试
            if(testCount<4){//4次
                startlatencytest();
                return;
            }else{
                cutTime = getValue();
            }
        }else{
            setValue(cutTime);
        }
        //重置为之前的系统音量
        if(noVolume){
            AudioManager am = (AudioManager) loopbackContext.getSystemService(Context.AUDIO_SERVICE);
            am.setStreamVolume(AudioManager.STREAM_MUSIC,
                    systemVolumeLevel, 0);
        }
        Log.e("DCLoopbackTool", "finish--mAverage:" + mCorrelation.mAverage);
        isDCLoopbackTestOK = true;
        Log.i("===yang","  testCompleted ");
        stopBind();
        destroy();
    }

    public static void stopBind(){
        if(loopbackContext instanceof DCLoopbackTestInterface ){
            ((DCLoopbackTestInterface)loopbackContext).onTestCompleted();
        }

        mMessageHandler.removeCallbacksAndMessages(null);
        if(loopbackContext!=null&&mBound){
            loopbackContext.unbindService(mServiceConnection);
            Log.i("===yang","  stopBind :mBound:" + mBound);
            mBound = false;
        }
    }

    public interface DCLoopbackTestInterface{
        void onTestCompleted();
    }

    //****************** testCompleted ******************//

    public static class RetainedFragment extends Fragment {
        public static double[] mWaveData;    // this is where we store the data for the wave plot

        // this method is only called once for this fragment
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        public void setWaveData(double[] waveData) {
            this.mWaveData = waveData;
        }

        public double[] getWaveData() {
            return mWaveData;
        }
    }

    public static Context loopbackContext;

    public static void onStart(Context context) {
        loopbackContext = context;
        Intent audioTestIntent = new Intent(context, AudioTestService.class);
        context.startService(audioTestIntent);
        bound = context.bindService(audioTestIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        mMessageHandler.sendEmptyMessageDelayed(MESSAGE_TIMEOUT,CONSTANT_TIMEOUT);
        isDCLoopbackTestOK = false;
        if (bound) {
            log("Successfully bound to service!");
        } else {
            log("Failed to bind service!");
        }

    }

    protected void onStop() {

        log("Activity on stop!");
        // Unbind from the service
        if (mBound) {
            loopbackContext.unbindService(mServiceConnection);
            mBound = false;
        }

    }

    public void onNewIntent(Intent intent) {
        log("On New Intent called!");
        applyIntent(intent);
    }

    /**
     * This method will be called whenever the test starts running (either by operating on the
     * device or by adb command). In the case where the test is started through adb command,
     * adb parameters will be read into intermediate variables.
     */
    private void applyIntent(Intent intent) {
        Bundle b = intent.getExtras();
        if (b != null && !mIntentRunning) {
            // adb shell am start -n org.drrickorang.loopback/.LoopbackActivity
            // --ei SF 48000 --es FileName test1 --ei RecorderBuffer 512 --ei PlayerBuffer 512
            // --ei AudioThread 1 --ei MicSource 3 --ei AudioLevel 12
            // --ei TestType 223 --ei BufferTestDuration 60 --ei NumLoadThreads 4
            // --ei CI -1 --ez CaptureSysTrace true --ez CaptureWavs false --ei NumCaptures 5
            // --ei WavDuration 15

            // Note: for native mode, player and recorder buffer sizes are the same, and can only be
            // set through player buffer size

            boolean hasRecordAudioPermission = hasRecordAudioPermission();
            boolean hasWriteFilePermission = hasWriteFilePermission();
            if (!hasRecordAudioPermission || !hasWriteFilePermission) {
                if (!hasRecordAudioPermission) {
                    log("Missing Permission: RECORD_AUDIO");
                }

                if (!hasWriteFilePermission) {
                    log("Missing Permission: WRITE_EXTERNAL_STORAGE");
                }

                return;
            }

            if (b.containsKey(INTENT_AUDIO_THREAD)) {
                int newAudioThreadType = b.getInt(INTENT_AUDIO_THREAD);
                if (newAudioThreadType != getApp().getAudioThreadType()) {
                    getApp().setAudioThreadType(newAudioThreadType);
                    getApp().computeDefaults();
                }
                mIntentRunning = true;
            }

            if (b.containsKey(INTENT_PERFORMANCE_MODE)) {
                int newPerformanceMode = b.getInt(INTENT_PERFORMANCE_MODE);
                if (newPerformanceMode != getApp().getPerformanceMode()) {
                    getApp().setPerformanceMode(newPerformanceMode);
                    getApp().computeDefaults();
                }
                mIntentRunning = true;
            }

            if (b.containsKey(INTENT_BUFFER_TEST_DURATION)) {
                getApp().setBufferTestDuration(b.getInt(INTENT_BUFFER_TEST_DURATION));
                mIntentRunning = true;
            }

            if (b.containsKey(INTENT_SAMPLING_FREQUENCY)) {
                getApp().setSamplingRate(b.getInt(INTENT_SAMPLING_FREQUENCY));
                mIntentRunning = true;
            }

            if (b.containsKey(INTENT_CORRELATION_BLOCK_SIZE)) {
                mCorrelation.setBlockSize(b.getInt(INTENT_CORRELATION_BLOCK_SIZE));
                mIntentRunning = true;
            }

            if (b.containsKey(INTENT_CHANNEL_INDEX)) {
                getApp().setChannelIndex(b.getInt(INTENT_CHANNEL_INDEX));
                mChannelIndex = b.getInt(INTENT_CHANNEL_INDEX);
                mIntentRunning = true;
            }

            if (b.containsKey(INTENT_FILENAME)) {
                mIntentFileName = b.getString(INTENT_FILENAME);
                mIntentRunning = true;
            }

            if (b.containsKey(INTENT_RECORDER_BUFFER)) {
                getApp().setRecorderBufferSizeInBytes(
                        b.getInt(INTENT_RECORDER_BUFFER) * Constant.BYTES_PER_FRAME);
                mIntentRunning = true;
            }

            if (b.containsKey(INTENT_PLAYER_BUFFER)) {
                getApp().setPlayerBufferSizeInBytes(
                        b.getInt(INTENT_PLAYER_BUFFER) * Constant.BYTES_PER_FRAME);
                mIntentRunning = true;
            }

            if (b.containsKey(INTENT_MIC_SOURCE)) {
                getApp().setMicSource(b.getInt(INTENT_MIC_SOURCE));
                mIntentRunning = true;
            }

            if (b.containsKey(INTENT_IGNORE_FIRST_FRAMES)) {
                getApp().setIgnoreFirstFrames(b.getInt(INTENT_IGNORE_FIRST_FRAMES));
                mIntentRunning = true;
            }

            if (b.containsKey(INTENT_AUDIO_LEVEL)) {
                int audioLevel = b.getInt(INTENT_AUDIO_LEVEL);
                if (audioLevel >= 0) {
                    AudioManager am = (AudioManager) loopbackContext.getSystemService(Context.AUDIO_SERVICE);
                    am.setStreamVolume(AudioManager.STREAM_MUSIC, audioLevel, 0);
                    getApp().setSoundLevelCalibrationEnabled(false);
                } else { // AudioLevel of -1 means automatically calibrate
                    getApp().setSoundLevelCalibrationEnabled(true);
                }
                mIntentRunning = true;
            }

            if (b.containsKey(INTENT_NUMBER_LOAD_THREADS)) {
                getApp().setNumberOfLoadThreads(b.getInt(INTENT_NUMBER_LOAD_THREADS));
                mIntentRunning = true;
            }

            if (b.containsKey(INTENT_ENABLE_SYSTRACE)) {
                getApp().setCaptureSysTraceEnabled(b.getBoolean(INTENT_ENABLE_SYSTRACE));
                mIntentRunning = true;
            }

            if (b.containsKey(INTENT_ENABLE_WAVCAPTURE)) {
                getApp().setCaptureWavsEnabled(b.getBoolean(INTENT_ENABLE_WAVCAPTURE));
                mIntentRunning = true;
            }

            if (b.containsKey(INTENT_NUM_CAPTURES)) {
                getApp().setNumberOfCaptures(b.getInt(INTENT_NUM_CAPTURES));
                mIntentRunning = true;
            }

            if (b.containsKey(INTENT_WAV_DURATION)) {
                getApp().setBufferTestWavePlotDuration(b.getInt(INTENT_WAV_DURATION));
                mIntentRunning = true;
            }

            if (mIntentRunning || b.containsKey(INTENT_TEST_TYPE)) {
                // run tests with provided or default parameters
//                refreshState();

                // if no test is specified then Latency Test will be run
                int testType = b.getInt(INTENT_TEST_TYPE,
                        Constant.LOOPBACK_PLUG_AUDIO_THREAD_TEST_TYPE_LATENCY);
                Log.d("tag","testType--->"+testType);
                switch (testType) {
                    case Constant.LOOPBACK_PLUG_AUDIO_THREAD_TEST_TYPE_BUFFER_PERIOD:
                        startBufferTest();
                        break;
                    case Constant.LOOPBACK_PLUG_AUDIO_THREAD_TEST_TYPE_CALIBRATION:
                        doCalibration();
                        break;
                    case Constant.LOOPBACK_PLUG_AUDIO_THREAD_TEST_TYPE_LATENCY:
                    default:
                        startlatencytest();
                        break;
                }
            }

        } else {
            if (mIntentRunning && b != null) {
                log("Test already in progress");
                log("Test already in progress");
            }
        }
    }


    /**
     * Stop all currently running threads that are related to audio test.
     */
    public static void stopAudioTestThreads() {
        log("stopping audio threads");
        if (mAudioThread != null) {
            try {
                mAudioThread.finish();
                mAudioThread.join(Constant.JOIN_WAIT_TIME_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mAudioThread = null;
        }

        if (mNativeAudioThread != null) {
            try {
                mNativeAudioThread.finish();
                mNativeAudioThread.join(Constant.JOIN_WAIT_TIME_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mNativeAudioThread = null;
        }

        stopLoadThreads();
        System.gc();
    }


    public static void destroy() {
        loopbackContext.stopService(new Intent(loopbackContext, AudioTestService.class));
        stopAudioTestThreads();
    }

    protected void onResume() {
        log("on resume called");
        /*
        setTransportButtonsState(NO_TEST_ACTIVE);
        */
    }

    /*
    protected void onPause() {
        // this means that this activity will not be recreated now, user is leaving it
        // or the activity is otherwise finishing
        if (isFinishing()) {
            FragmentManager fm = getFragmentManager();
            // we will not need this fragment anymore, this may also be a good place to signal
            // to the retained fragment object to perform its own cleanup.
            fm.beginTransaction().remove(mRetainedFragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tool_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Respond to user selecting action bar buttons
        int i = item.getItemId();
        if (i == R.id.action_help) {
            if (!isBusy()) {
                // Launch about Activity
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
            } else {
                showToast("Test in progress... please wait");
            }

            return true;
        } else if (i == R.id.action_settings) {
            if (!isBusy()) {
                // Launch settings activity
                Intent mySettingsIntent = new Intent(this, SettingsActivity.class);
                startActivityForResult(mySettingsIntent, SETTINGS_ACTIVITY_REQUEST);
            } else {
                showToast("Test in progress... please wait");
            }
            return true;
        } else {
        }

        return super.onOptionsItemSelected(item);
    }
    */

    /**
     * Check if the app is busy (running test).
     */
    public static boolean isBusy() {
        boolean busy = false;

        if (mAudioThread != null && mAudioThread.mIsRunning) {
            busy = true;
        }

        if (mNativeAudioThread != null && mNativeAudioThread.mIsRunning) {
            busy = true;
        }

        return busy;
    }


    /**
     * Create a new audio thread according to the settings.
     */
    public static void restartAudioSystem() {
        log("restart audio system...");

        int sessionId = 0; /* FIXME runtime test for am.generateAudioSessionId() in API 21 */

        mAudioThreadType = getApp().getAudioThreadType();
        mSamplingRate = getApp().getSamplingRate();
        mChannelIndex = getApp().getChannelIndex();
        mPlayerBufferSizeInBytes = getApp().getPlayerBufferSizeInBytes();
        mRecorderBufferSizeInBytes = getApp().getRecorderBufferSizeInBytes();
        mTestStartTimeString = (String) DateFormat.format("MMddkkmmss",
                System.currentTimeMillis());
        mMicSource = getApp().getMicSource();
        mPerformanceMode = getApp().getPerformanceMode();
        mIgnoreFirstFrames = getApp().getIgnoreFirstFrames();
        AudioManager am = (AudioManager) loopbackContext.getSystemService(Context.AUDIO_SERVICE);
        mSoundLevel = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        mBufferTestDurationInSeconds = getApp().getBufferTestDuration();
        mBufferTestWavePlotDurationInSeconds = getApp().getBufferTestWavePlotDuration();

        mCaptureHolder = new CaptureHolder(getApp().getNumStateCaptures(),
                getFileNamePrefix(), getApp().isCaptureWavSnippetsEnabled(),
                getApp().isCaptureSysTraceEnabled(), getApp().isCaptureBugreportEnabled(),
                loopbackContext, mSamplingRate);

        log(" current sampling rate: " + mSamplingRate);
        stopAudioTestThreads();

        // select java or native audio thread
        int micSourceMapped;

        switch (mAudioThreadType) {

            case Constant.AUDIO_THREAD_TYPE_JAVA:
                micSourceMapped = getApp().mapMicSource(Constant.AUDIO_THREAD_TYPE_JAVA, mMicSource);

                int expectedRecorderBufferPeriod = Math.round(
                        (float) (mRecorderBufferSizeInBytes * Constant.MILLIS_PER_SECOND)
                                / (Constant.BYTES_PER_FRAME * mSamplingRate));
                mRecorderBufferPeriod.prepareMemberObjects(
                        Constant.MAX_RECORDED_LATE_CALLBACKS_PER_SECOND * mBufferTestDurationInSeconds,
                        expectedRecorderBufferPeriod, mCaptureHolder);

                int expectedPlayerBufferPeriod = Math.round(
                        (float) (mPlayerBufferSizeInBytes * Constant.MILLIS_PER_SECOND)
                                / (Constant.BYTES_PER_FRAME * mSamplingRate));
                mPlayerBufferPeriod.prepareMemberObjects(
                        Constant.MAX_RECORDED_LATE_CALLBACKS_PER_SECOND * mBufferTestDurationInSeconds,
                        expectedPlayerBufferPeriod, mCaptureHolder);

                mAudioThread = new LoopbackAudioThread(mSamplingRate, mPlayerBufferSizeInBytes,
                        mRecorderBufferSizeInBytes, micSourceMapped,
                        /* no performance mode */ mRecorderBufferPeriod,
                        mPlayerBufferPeriod, mTestType, mBufferTestDurationInSeconds,
                        mBufferTestWavePlotDurationInSeconds, loopbackContext.getApplicationContext(),
                        mChannelIndex, mCaptureHolder);
                mAudioThread.setMessageHandler(mMessageHandler);
                mAudioThread.mSessionId = sessionId;
                mAudioThread.start();
                break;
            case Constant.AUDIO_THREAD_TYPE_NATIVE_SLES:
            case Constant.AUDIO_THREAD_TYPE_NATIVE_AAUDIO:
                micSourceMapped = getApp().mapMicSource(Constant.AUDIO_THREAD_TYPE_NATIVE_SLES,
                        mMicSource);
                int performanceModeMapped = getApp().mapPerformanceMode(mPerformanceMode);
                // Note: mRecorderBufferSizeInBytes will not actually be used, since recorder buffer
                // size = player buffer size in native mode
                mNativeAudioThread = new NativeAudioThread(mAudioThreadType, mSamplingRate,
                        mPlayerBufferSizeInBytes, mRecorderBufferSizeInBytes,
                        micSourceMapped, performanceModeMapped, mTestType,
                        mBufferTestDurationInSeconds, mBufferTestWavePlotDurationInSeconds,
                        mIgnoreFirstFrames, mCaptureHolder);
                mNativeAudioThread.setMessageHandler(mMessageHandler);
                mNativeAudioThread.mSessionId = sessionId;
                mNativeAudioThread.start();
                break;
        }

        startLoadThreads();

        mMessageHandler.post(new Runnable() {
            @Override
            public void run() {
//                refreshState();
            }
        });
    }


    /**
     * Start all LoadThread.
     */
    public static void startLoadThreads() {

        if (getApp().getNumberOfLoadThreads() > 0) {

            mLoadThreads = new LoadThread[getApp().getNumberOfLoadThreads()];

            for (int i = 0; i < mLoadThreads.length; i++) {
                mLoadThreads[i] = new LoadThread("Loopback_LoadThread_" + i);
                mLoadThreads[i].start();
            }
        }
    }


    /**
     * Stop all LoadThread.
     */
    public static void stopLoadThreads() {
        log("stopping load threads");
        if (mLoadThreads != null) {
            for (int i = 0; i < mLoadThreads.length; i++) {
                if (mLoadThreads[i] != null) {
                    try {
                        mLoadThreads[i].requestStop();
                        mLoadThreads[i].join(Constant.JOIN_WAIT_TIME_MS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mLoadThreads[i] = null;
                }
            }
        }
    }

    public static void resetBufferPeriodRecord(BufferPeriod recorderBufferPeriod,
                                               BufferPeriod playerBufferPeriod) {
        recorderBufferPeriod.resetRecord();
        playerBufferPeriod.resetRecord();
    }

    private boolean canPerformBufferTest() {
        // Retrieve the thread type from the app, because mAudioThreadType
        // only gets populated during the start of the test.
        switch (getApp().getAudioThreadType()) {
            case Constant.AUDIO_THREAD_TYPE_JAVA:
            case Constant.AUDIO_THREAD_TYPE_NATIVE_SLES:
                return true;
        }
        // Buffer test isn't yet implemented for AAudio.
        return false;
    }

    /*

    private void setTransportButtonsState(int state) {
        Button latencyStart = (Button) findViewById(R.id.buttonStartLatencyTest);
        Button bufferStart = (Button) findViewById(R.id.buttonStartBufferTest);
        Button calibrationStart = (Button) findViewById(R.id.buttonCalibrateSoundLevel);

        boolean canEnableBufferTest = canPerformBufferTest();

        switch (state) {
            case NO_TEST_ACTIVE:
                latencyStart.setEnabled(true);
                bufferStart.setEnabled(canEnableBufferTest);
                calibrationStart.setEnabled(true);
                break;

            case LATENCY_TEST_STARTED:
                findViewById(R.id.zoomAndSaveControlPanel).setVisibility(View.INVISIBLE);
                mTextViewResultSummary.setText("");
                findViewById(R.id.glitchReportPanel).setVisibility(View.INVISIBLE);
                latencyStart.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_stop, 0, 0, 0);
                bufferStart.setEnabled(false);
                calibrationStart.setEnabled(false);
                break;

            case LATENCY_TEST_ENDED:
                findViewById(R.id.zoomAndSaveControlPanel).setVisibility(View.VISIBLE);
                latencyStart.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_play_arrow, 0, 0, 0);
                bufferStart.setEnabled(canEnableBufferTest);
                calibrationStart.setEnabled(true);
                break;

            case BUFFER_TEST_STARTED:
                findViewById(R.id.zoomAndSaveControlPanel).setVisibility(View.INVISIBLE);
                mTextViewResultSummary.setText("");
                findViewById(R.id.glitchReportPanel).setVisibility(View.INVISIBLE);
                bufferStart.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_stop, 0, 0, 0);
                latencyStart.setEnabled(false);
                calibrationStart.setEnabled(false);
                break;

            case BUFFER_TEST_ENDED:
                findViewById(R.id.zoomAndSaveControlPanel).setVisibility(View.VISIBLE);
                findViewById(R.id.resultSummary).setVisibility(View.VISIBLE);
                findViewById(R.id.glitchReportPanel).setVisibility(View.VISIBLE);
                bufferStart.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_play_arrow, 0, 0, 0);
                latencyStart.setEnabled(true);
                calibrationStart.setEnabled(true);
                break;

            case CALIBRATION_STARTED:
                findViewById(R.id.zoomAndSaveControlPanel).setVisibility(View.INVISIBLE);
                findViewById(R.id.resultSummary).setVisibility(View.INVISIBLE);
                findViewById(R.id.glitchReportPanel).setVisibility(View.INVISIBLE);
                bufferStart.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_stop, 0, 0, 0);
                latencyStart.setEnabled(false);
                bufferStart.setEnabled(false);
                calibrationStart.setEnabled(false);
                break;

            case CALIBRATION_ENDED:
                findViewById(R.id.zoomAndSaveControlPanel).setVisibility(View.VISIBLE);
                findViewById(R.id.resultSummary).setVisibility(View.VISIBLE);
                findViewById(R.id.glitchReportPanel).setVisibility(View.VISIBLE);
                bufferStart.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_play_arrow, 0, 0, 0);
                latencyStart.setEnabled(true);
                bufferStart.setEnabled(canEnableBufferTest);
                calibrationStart.setEnabled(true);
                break;

            default:
                break;
        }
    }

    */

    public static void doCalibrationIfEnabled(final Runnable onComplete) {
        Log.d("sun","getApp().isSoundLevelCalibrationEnabled()"+getApp().isSoundLevelCalibrationEnabled());
        if (getApp().isSoundLevelCalibrationEnabled()) {
            doCalibration(onComplete);
        } else {
            if (onComplete != null) {
                onComplete.run();
            }
        }
    }

    private void doCalibration() {
        doCalibration(null);
    }

    public static void doCalibration(final Runnable onComplete) {
        if (isBusy()) {
            log("Test in progress... please wait");
            return;
        }

        if (!hasRecordAudioPermission()) {
            requestRecordAudioPermission(PERMISSIONS_REQUEST_RECORD_AUDIO_LATENCY);
            // Returning, otherwise we don't know if user accepted or rejected.
            return;
        }

        log("Calibrating sound level...");
        final SoundLevelCalibration calibration =
                new SoundLevelCalibration(getApp().getAudioThreadType(),
                        getApp().getSamplingRate(),
                        getApp().getPlayerBufferSizeInBytes(),
                        getApp().getRecorderBufferSizeInBytes(),
                        getApp().getMicSource(), getApp().getPerformanceMode(), loopbackContext);

        calibration.setChangeListener(new SoundLevelCalibration.SoundLevelChangeListener() {
            @Override
            void onChange(int newLevel) {
//                refreshSoundLevelBar();
            }
        });

        mCalibrationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                calibration.calibrate();
                log("Calibration complete");
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });

        mCalibrationThread.start();
    }

    /**
     * Start the latency test.
     */
    public void onButtonLatencyTest(View view) throws InterruptedException {
        if (isBusy()) {
            stopTests();
            return;
        }

        // Ensure we have RECORD_AUDIO permissions
        // On Android M (API 23) we must request dangerous permissions each time we use them
        if (hasRecordAudioPermission()) {
            startlatencytest();
        } else {
            requestRecordAudioPermission(PERMISSIONS_REQUEST_RECORD_AUDIO_LATENCY);
        }

    }

    public static void startlatencytest() {
        Log.i("===yang "," startlatencytest ");
        if (isBusy()) {
            log("Test in progress... please wait");
            return;
        }
        Log.i("===yang "," startlatencytest1 ");

        if(noVolume){
            AudioManager am = (AudioManager) loopbackContext.getSystemService(Context.AUDIO_SERVICE);
            systemVolumeLevel = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            am.setStreamVolume(AudioManager.STREAM_MUSIC,
                    0, 0);
        }

        doCalibrationIfEnabled(latencyTestRunnable);
    }

    public static Runnable latencyTestRunnable = new Runnable() {
        @Override
        public void run() {
            if (isBusy()) {
                log("Test in progress... please wait");
                return;
            }

            resetBufferPeriodRecord(mRecorderBufferPeriod, mPlayerBufferPeriod);

            mTestType = Constant.LOOPBACK_PLUG_AUDIO_THREAD_TEST_TYPE_LATENCY;
            restartAudioSystem();
            try {
                Thread.sleep(THREAD_SLEEP_DURATION_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            switch (mAudioThreadType) {
                case Constant.AUDIO_THREAD_TYPE_JAVA:
                    if (mAudioThread != null) {
                        mAudioThread.runTest();
                    }
                    break;
                case Constant.AUDIO_THREAD_TYPE_NATIVE_SLES:
                case Constant.AUDIO_THREAD_TYPE_NATIVE_AAUDIO:
                    if (mNativeAudioThread != null) {
                        mNativeAudioThread.runTest();
                    }
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * Start the Buffer (Glitch Detection) Test.
     */
    public void onButtonBufferTest(View view) throws InterruptedException {
        if (isBusy()) {
            stopTests();
            return;
        }

        if (hasRecordAudioPermission()) {
            startBufferTest();
        } else {
            requestRecordAudioPermission(PERMISSIONS_REQUEST_RECORD_AUDIO_BUFFER);
        }
    }


    private void startBufferTest() {
        // In the interactive mode the buffer test button should be disabled
        // if the buffer test isn't supported, but the function can also be invoked
        // via an intent.
        if (!canPerformBufferTest()) {
            log("Buffer test is not supported with this thread type");
            log("Buffer test is not supported with this thread type");
            return;
        }

        if (!isBusy()) {
//            mBarMasterLevel.setEnabled(false);
            resetBufferPeriodRecord(mRecorderBufferPeriod, mPlayerBufferPeriod);
            mTestType = Constant.LOOPBACK_PLUG_AUDIO_THREAD_TEST_TYPE_BUFFER_PERIOD;
            restartAudioSystem();   // in this function a audio thread is created
            try {
                Thread.sleep(THREAD_SLEEP_DURATION_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            switch (mAudioThreadType) {
                case Constant.AUDIO_THREAD_TYPE_JAVA:
                    if (mAudioThread != null) {
                        mAudioThread.runBufferTest();
                    }
                    break;
                case Constant.AUDIO_THREAD_TYPE_NATIVE_SLES:
                case Constant.AUDIO_THREAD_TYPE_NATIVE_AAUDIO:
                    if (mNativeAudioThread != null) {
                        mNativeAudioThread.runBufferTest();
                    }
                    break;
                default:
                    break;
            }
        } else {
            int duration = 0;
            switch (mAudioThreadType) {
                case Constant.AUDIO_THREAD_TYPE_JAVA:
                    duration = mAudioThread.getDurationInSeconds();
                    break;
                case Constant.AUDIO_THREAD_TYPE_NATIVE_SLES:
                case Constant.AUDIO_THREAD_TYPE_NATIVE_AAUDIO:
                    duration = mNativeAudioThread.getDurationInSeconds();
                    break;
                default:
                    break;
            }
            log("Long-run Test in progress, in total should take " +
                    Integer.toString(duration) + "s, please wait");
        }
    }


    /**
     * Stop the ongoing test.
     */
    public void stopTests() throws InterruptedException {
        if (mAudioThread != null) {
            mAudioThread.requestStopTest();
        }

        if (mNativeAudioThread != null) {
            mNativeAudioThread.requestStopTest();
        }
    }

    public void onButtonCalibrateSoundLevel(final View view) {
        Message m = Message.obtain();
        m.what = CALIBRATION_STARTED;
        mMessageHandler.sendMessage(m);
        Runnable onComplete = new Runnable() {
            @Override
            public void run() {
                Message m = Message.obtain();
                m.what = CALIBRATION_ENDED;
                mMessageHandler.sendMessage(m);
            }
        };
        doCalibration(onComplete);
    }


    /***
     * Show dialog to choose to save files with filename dialog or not
     */
    /*
    public void onButtonSave(View view) {
        if (!isBusy()) {
            DialogFragment newFragment = new SaveFilesDialogFragment();
            newFragment.show(getFragmentManager(), "saveFiles");
        } else {
            showToast("Test in progress... please wait");
        }
    }
    */

    /**
     * Save five files: one .png file for a screenshot on the main activity, one .wav file for
     * the plot displayed on the main activity, one .txt file for storing various test results, one
     * .txt file for storing recorder buffer period data, and one .txt file for storing player
     * buffer period data.
     */
    /*
    private void SaveFilesWithDialog() {

        String fileName = "loopback_" + mTestStartTimeString;

        //Launch filename choosing activities if available, otherwise save without prompting
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            launchFileNameChoosingActivity("text/plain", fileName, ".txt", SAVE_TO_TXT_REQUEST);
            launchFileNameChoosingActivity("image/png", fileName, ".png", SAVE_TO_PNG_REQUEST);
            launchFileNameChoosingActivity("audio/wav", fileName, ".wav", SAVE_TO_WAVE_REQUEST);
            launchFileNameChoosingActivity("text/plain", fileName, "_recorderBufferPeriod.txt",
                    SAVE_RECORDER_BUFFER_PERIOD_TO_TXT_REQUEST);
            launchFileNameChoosingActivity("text/plain", fileName, "_recorderBufferPeriodTimes.txt",
                    SAVE_RECORDER_BUFFER_PERIOD_TIMES_TO_TXT_REQUEST);
            launchFileNameChoosingActivity("image/png", fileName, "_recorderBufferPeriod.png",
                    SAVE_RECORDER_BUFFER_PERIOD_TO_PNG_REQUEST);
            launchFileNameChoosingActivity("text/plain", fileName, "_playerBufferPeriod.txt",
                    SAVE_PLAYER_BUFFER_PERIOD_TO_TXT_REQUEST);
            launchFileNameChoosingActivity("text/plain", fileName, "_playerBufferPeriodTimes.txt",
                    SAVE_PLAYER_BUFFER_PERIOD_TIMES_TO_TXT_REQUEST);
            launchFileNameChoosingActivity("image/png", fileName, "_playerBufferPeriod.png",
                    SAVE_PLAYER_BUFFER_PERIOD_TO_PNG_REQUEST);

            if (mGlitchesData != null) {
                launchFileNameChoosingActivity("text/plain", fileName, "_glitchMillis.txt",
                        SAVE_GLITCH_OCCURRENCES_TO_TEXT_REQUEST);
                launchFileNameChoosingActivity("image/png", fileName, "_heatMap.png",
                        SAVE_GLITCH_AND_CALLBACK_HEATMAP_REQUEST);
            }
        } else {
            saveAllTo(fileName);
        }
    }
*/
    /**
     * Launches an activity for choosing the filename of the file to be saved
     */
    /*
    public void launchFileNameChoosingActivity(String type, String fileName, String suffix,
                                               int RequestCode) {
        Intent FilenameIntent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        FilenameIntent.addCategory(Intent.CATEGORY_OPENABLE);
        FilenameIntent.setType(type);
        FilenameIntent.putExtra(Intent.EXTRA_TITLE, fileName + suffix);
        startActivityForResult(FilenameIntent, RequestCode);
    }
    */

    public static String getFileNamePrefix() {
        if (mIntentFileName != null && !mIntentFileName.isEmpty()) {
            return mIntentFileName;
        } else {
            return "loopback_" + mTestStartTimeString;
        }
    }

    /**
     * See the documentation on onButtonSave()
     */
    public static void saveAllTo(String fileName) {

        if (!hasWriteFilePermission()) {
            requestWriteFilePermission(PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_RESULTS);
            return;
        }

        log("Saving files to: " + fileName + ".(wav,png,txt)");

        //save to a given uri... local file?
//        saveToWaveFile(Uri.parse(FILE_SAVE_PATH + fileName + ".wav"));
/*
        saveScreenShot(Uri.parse(FILE_SAVE_PATH + fileName + ".png"));
*/
        saveTextToFile(Uri.parse(FILE_SAVE_PATH + fileName + ".txt"), getReport().toString());

        int[] bufferPeriodArray = null;
        int maxBufferPeriod = Constant.UNKNOWN;
        switch (mAudioThreadType) {
            case Constant.AUDIO_THREAD_TYPE_JAVA:
                bufferPeriodArray = mRecorderBufferPeriod.getBufferPeriodArray();
                maxBufferPeriod = mRecorderBufferPeriod.getMaxBufferPeriod();
                break;
            case Constant.AUDIO_THREAD_TYPE_NATIVE_SLES:
            case Constant.AUDIO_THREAD_TYPE_NATIVE_AAUDIO:
                bufferPeriodArray = mNativeRecorderBufferPeriodArray;
                maxBufferPeriod = mNativeRecorderMaxBufferPeriod;
                break;
            default:
                break;
        }
        saveBufferPeriod(Uri.parse(FILE_SAVE_PATH + fileName + "_recorderBufferPeriod.txt"),
                bufferPeriodArray, maxBufferPeriod);
        saveHistogram(Uri.parse(FILE_SAVE_PATH + fileName + "_recorderBufferPeriod.png"),
                bufferPeriodArray, maxBufferPeriod);
        saveTextToFile(Uri.parse(FILE_SAVE_PATH + fileName + "_recorderBufferPeriodTimes.txt"),
                mRecorderCallbackTimes.toString());

        bufferPeriodArray = null;
        maxBufferPeriod = Constant.UNKNOWN;
        switch (mAudioThreadType) {
            case Constant.AUDIO_THREAD_TYPE_JAVA:
                bufferPeriodArray = mPlayerBufferPeriod.getBufferPeriodArray();
                maxBufferPeriod = mPlayerBufferPeriod.getMaxBufferPeriod();
                break;
            case Constant.AUDIO_THREAD_TYPE_NATIVE_SLES:
            case Constant.AUDIO_THREAD_TYPE_NATIVE_AAUDIO:
                bufferPeriodArray = mNativePlayerBufferPeriodArray;
                maxBufferPeriod = mNativePlayerMaxBufferPeriod;
                break;
            default:
                break;
        }
        saveBufferPeriod(Uri.parse(FILE_SAVE_PATH + fileName + "_playerBufferPeriod.txt")
                , bufferPeriodArray, maxBufferPeriod);
        saveHistogram(Uri.parse(FILE_SAVE_PATH + fileName + "_playerBufferPeriod.png"),
                bufferPeriodArray, maxBufferPeriod);
        saveTextToFile(Uri.parse(FILE_SAVE_PATH + fileName + "_playerBufferPeriodTimes.txt"),
                mPlayerCallbackTimes.toString());

        if (mGlitchesData != null) {
            saveGlitchOccurrences(Uri.parse(FILE_SAVE_PATH + fileName + "_glitchMillis.txt"),
                    mGlitchesData);
            saveHeatMap(Uri.parse(FILE_SAVE_PATH + fileName + "_heatMap.png"),
                    mRecorderCallbackTimes, mPlayerCallbackTimes,
                    GlitchesStringBuilder.getGlitchMilliseconds(mFFTSamplingSize,
                            mFFTOverlapSamples, mGlitchesData, mSamplingRate),
                    mGlitchingIntervalTooLong, mBufferTestElapsedSeconds, fileName);
        }

    }

    /*
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        log("ActivityResult request: " + requestCode + "  result:" + resultCode);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {

                case SAVE_TO_WAVE_REQUEST:
                    log("got SAVE TO WAV intent back!");
                    if (resultData != null) {
                        saveToWaveFile(resultData.getData());
                    }
                    break;

                case SAVE_TO_PNG_REQUEST:
                    log("got SAVE TO PNG intent back!");
                    if (resultData != null) {
                        saveScreenShot(resultData.getData());
                    }
                    break;

                case SAVE_TO_TXT_REQUEST:
                    if (resultData != null) {
                        saveTextToFile(resultData.getData(), getReport().toString());
                    }
                    break;

                case SAVE_RECORDER_BUFFER_PERIOD_TO_TXT_REQUEST:
                    if (resultData != null) {
                        int[] bufferPeriodArray = null;
                        int maxBufferPeriod = Constant.UNKNOWN;
                        switch (mAudioThreadType) {
                            case Constant.AUDIO_THREAD_TYPE_JAVA:
                                bufferPeriodArray = mRecorderBufferPeriod.getBufferPeriodArray();
                                maxBufferPeriod = mRecorderBufferPeriod.getMaxBufferPeriod();
                                break;
                            case Constant.AUDIO_THREAD_TYPE_NATIVE_SLES:
                            case Constant.AUDIO_THREAD_TYPE_NATIVE_AAUDIO:
                                bufferPeriodArray = mNativeRecorderBufferPeriodArray;
                                maxBufferPeriod = mNativeRecorderMaxBufferPeriod;
                                break;
                        }
                        saveBufferPeriod(resultData.getData(), bufferPeriodArray, maxBufferPeriod);
                    }
                    break;

                case SAVE_PLAYER_BUFFER_PERIOD_TO_TXT_REQUEST:
                    if (resultData != null) {
                        int[] bufferPeriodArray = null;
                        int maxBufferPeriod = Constant.UNKNOWN;
                        switch (mAudioThreadType) {
                            case Constant.AUDIO_THREAD_TYPE_JAVA:
                                bufferPeriodArray = mPlayerBufferPeriod.getBufferPeriodArray();
                                maxBufferPeriod = mPlayerBufferPeriod.getMaxBufferPeriod();
                                break;
                            case Constant.AUDIO_THREAD_TYPE_NATIVE_SLES:
                            case Constant.AUDIO_THREAD_TYPE_NATIVE_AAUDIO:
                                bufferPeriodArray = mNativePlayerBufferPeriodArray;
                                maxBufferPeriod = mNativePlayerMaxBufferPeriod;
                                break;
                        }
                        saveBufferPeriod(resultData.getData(), bufferPeriodArray, maxBufferPeriod);
                    }
                    break;

                case SAVE_RECORDER_BUFFER_PERIOD_TO_PNG_REQUEST:
                    if (resultData != null) {
                        int[] bufferPeriodArray = null;
                        int maxBufferPeriod = Constant.UNKNOWN;
                        switch (mAudioThreadType) {
                            case Constant.AUDIO_THREAD_TYPE_JAVA:
                                bufferPeriodArray = mRecorderBufferPeriod.getBufferPeriodArray();
                                maxBufferPeriod = mRecorderBufferPeriod.getMaxBufferPeriod();
                                break;
                            case Constant.AUDIO_THREAD_TYPE_NATIVE_SLES:
                            case Constant.AUDIO_THREAD_TYPE_NATIVE_AAUDIO:
                                bufferPeriodArray = mNativeRecorderBufferPeriodArray;
                                maxBufferPeriod = mNativeRecorderMaxBufferPeriod;
                                break;
                        }
                        saveHistogram(resultData.getData(), bufferPeriodArray, maxBufferPeriod);
                    }
                    break;

                case SAVE_PLAYER_BUFFER_PERIOD_TO_PNG_REQUEST:
                    if (resultData != null) {
                        int[] bufferPeriodArray = null;
                        int maxBufferPeriod = Constant.UNKNOWN;
                        switch (mAudioThreadType) {
                            case Constant.AUDIO_THREAD_TYPE_JAVA:
                                bufferPeriodArray = mPlayerBufferPeriod.getBufferPeriodArray();
                                maxBufferPeriod = mPlayerBufferPeriod.getMaxBufferPeriod();
                                break;
                            case Constant.AUDIO_THREAD_TYPE_NATIVE_SLES:
                            case Constant.AUDIO_THREAD_TYPE_NATIVE_AAUDIO:
                                bufferPeriodArray = mNativePlayerBufferPeriodArray;
                                maxBufferPeriod = mNativePlayerMaxBufferPeriod;
                                break;
                        }
                        saveHistogram(resultData.getData(), bufferPeriodArray, maxBufferPeriod);
                    }
                    break;

                case SAVE_PLAYER_BUFFER_PERIOD_TIMES_TO_TXT_REQUEST:
                    if (resultData != null) {
                        saveTextToFile(resultData.getData(),
                                mPlayerCallbackTimes.toString());
                    }
                    break;

                case SAVE_RECORDER_BUFFER_PERIOD_TIMES_TO_TXT_REQUEST:
                    if (resultData != null) {
                        saveTextToFile(resultData.getData(),
                                mRecorderCallbackTimes.toString());
                    }
                    break;

                case SAVE_GLITCH_OCCURRENCES_TO_TEXT_REQUEST:
                    if (resultData != null) {
                        saveGlitchOccurrences(resultData.getData(), mGlitchesData);
                    }
                    break;

                case SAVE_GLITCH_AND_CALLBACK_HEATMAP_REQUEST:
                    if (resultData != null && mGlitchesData != null && mRecorderCallbackTimes != null
                            && mPlayerCallbackTimes != null) {
                        saveHeatMap(resultData.getData(), mRecorderCallbackTimes, mPlayerCallbackTimes,
                                GlitchesStringBuilder.getGlitchMilliseconds(mFFTSamplingSize,
                                        mFFTOverlapSamples, mGlitchesData, mSamplingRate),
                                mGlitchingIntervalTooLong, mBufferTestElapsedSeconds,
                                resultData.getData().toString());
                    }

                case SETTINGS_ACTIVITY_REQUEST:
                    log("return from new settings!");
                    break;

                default:
                    break;

            }
        }
    }
*/

    /**
     * Refresh the sound level bar on the main activity to reflect the current sound level
     * of the system.
     */
    /*
    public static void refreshSoundLevelBar() {
        mBarMasterLevel.setEnabled(true);
        AudioManager am = (AudioManager) loopbackContext.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        mBarMasterLevel.setProgress(currentVolume);

        mTextViewCurrentLevel.setText(String.format("Current Sound Level: %d/%d", currentVolume,
                mBarMasterLevel.getMax()));
    }
    */

    /**
     * Reset all results gathered from previous round of test (if any).
     */
    public static void resetResults() {
        mCorrelation.invalidate();
        mNativeRecorderBufferPeriodArray = null;
        mNativePlayerBufferPeriodArray = null;
        mPlayerCallbackTimes = null;
        mRecorderCallbackTimes = null;
        mGlitchesData = null;
//        mRetainedFragment.mWaveData = null;
    }


    /**
     * Get the file path from uri. Doesn't work for all devices.
     */
    public static String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor1 = loopbackContext.getContentResolver().query(uri, projection, null, null, null);
        if (cursor1 == null) {
            return uri.getPath();
        }

        int ColumnIndex = cursor1.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor1.moveToFirst();
        String path = cursor1.getString(ColumnIndex);
        cursor1.close();
        return path;
    }


    /**
     * Zoom out the plot to its full size.
     */
    public void onButtonZoomOutFull(View view) {
//        double fullZoomOut = mWavePlotView.getMaxZoomOut();
//        mWavePlotView.setZoom(fullZoomOut);
//        mWavePlotView.refreshGraph();
    }


    /**
     * Zoom out the plot.
     */
    public void onButtonZoomOut(View view) {
//        double zoom = mWavePlotView.getZoom();
//        zoom = 2.0 * zoom;
//        mWavePlotView.setZoom(zoom);
//        mWavePlotView.refreshGraph();
    }


    /**
     * Zoom in the plot.
     */
    public void onButtonZoomIn(View view) {
//        double zoom = mWavePlotView.getZoom();
//        zoom = zoom / 2.0;
//        mWavePlotView.setZoom(zoom);
//        mWavePlotView.refreshGraph();
    }


    /**
     * Go to RecorderBufferPeriodActivity
     */
    public void onButtonRecorderBufferPeriod(View view) {
        if (!isBusy()) {
            Intent RecorderBufferPeriodIntent = new Intent(loopbackContext,
                    RecorderBufferPeriodActivity.class);
            int recorderBufferSizeInFrames = mRecorderBufferSizeInBytes / Constant.BYTES_PER_FRAME;
            log("recorderBufferSizeInFrames:" + recorderBufferSizeInFrames);

            switch (mAudioThreadType) {
                case Constant.AUDIO_THREAD_TYPE_JAVA:
                    RecorderBufferPeriodIntent.putExtra("recorderBufferPeriodArray",
                            mRecorderBufferPeriod.getBufferPeriodArray());
                    RecorderBufferPeriodIntent.putExtra("recorderBufferPeriodMax",
                            mRecorderBufferPeriod.getMaxBufferPeriod());
                    break;
                case Constant.AUDIO_THREAD_TYPE_NATIVE_SLES:
                case Constant.AUDIO_THREAD_TYPE_NATIVE_AAUDIO:
                    RecorderBufferPeriodIntent.putExtra("recorderBufferPeriodArray",
                            mNativeRecorderBufferPeriodArray);
                    RecorderBufferPeriodIntent.putExtra("recorderBufferPeriodMax",
                            mNativeRecorderMaxBufferPeriod);
                    break;
            }

            RecorderBufferPeriodIntent.putExtra("recorderBufferSize", recorderBufferSizeInFrames);
            RecorderBufferPeriodIntent.putExtra("samplingRate", mSamplingRate);
            loopbackContext.startActivity(RecorderBufferPeriodIntent);
        } else
            log("Test in progress... please wait");
    }


    /**
     * Go to PlayerBufferPeriodActivity
     */
    public void onButtonPlayerBufferPeriod(View view) {
        if (!isBusy()) {
            Intent PlayerBufferPeriodIntent = new Intent(loopbackContext, PlayerBufferPeriodActivity.class);
            int playerBufferSizeInFrames = mPlayerBufferSizeInBytes / Constant.BYTES_PER_FRAME;

            switch (mAudioThreadType) {
                case Constant.AUDIO_THREAD_TYPE_JAVA:
                    PlayerBufferPeriodIntent.putExtra("playerBufferPeriodArray",
                            mPlayerBufferPeriod.getBufferPeriodArray());
                    PlayerBufferPeriodIntent.putExtra("playerBufferPeriodMax",
                            mPlayerBufferPeriod.getMaxBufferPeriod());
                    break;
                case Constant.AUDIO_THREAD_TYPE_NATIVE_SLES:
                case Constant.AUDIO_THREAD_TYPE_NATIVE_AAUDIO:
                    PlayerBufferPeriodIntent.putExtra("playerBufferPeriodArray",
                            mNativePlayerBufferPeriodArray);
                    PlayerBufferPeriodIntent.putExtra("playerBufferPeriodMax",
                            mNativePlayerMaxBufferPeriod);
                    break;
            }

            PlayerBufferPeriodIntent.putExtra("playerBufferSize", playerBufferSizeInFrames);
            PlayerBufferPeriodIntent.putExtra("samplingRate", mSamplingRate);
            loopbackContext.startActivity(PlayerBufferPeriodIntent);
        } else
            log("Test in progress... please wait");
    }


    /**
     * Display pop up window of recorded glitches
     */
    /*
    public void onButtonGlitches(View view) {
        if (!isBusy()) {
            if (mGlitchesData != null) {
                // Create a PopUpWindow with scrollable TextView
                View puLayout = this.getLayoutInflater().inflate(R.layout.report_window, null);
                PopupWindow popUp = new PopupWindow(puLayout, ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT, true);

                // Generate report of glitch intervals and set pop up window text
                TextView GlitchText =
                        (TextView) popUp.getContentView().findViewById(R.id.ReportInfo);
                GlitchText.setText(GlitchesStringBuilder.getGlitchString(mFFTSamplingSize,
                        mFFTOverlapSamples, mGlitchesData, mSamplingRate,
                        mGlitchingIntervalTooLong, estimateNumberOfGlitches(mGlitchesData)));

                // display pop up window, dismissible with back button
                popUp.showAtLocation((View) findViewById(R.id.linearLayoutMain), Gravity.TOP, 0, 0);
            } else {
                showToastImportant("Please run the buffer test to get data");
            }

        } else {
            showToast("Test in progress... please wait");
        }
    }
*/

    /**
     * Display pop up window of recorded metrics and system information
     */
    /*
    public void onButtonReport(View view) {
        if (!isBusy()) {
            if ((mTestType == Constant.LOOPBACK_PLUG_AUDIO_THREAD_TEST_TYPE_BUFFER_PERIOD
                    && mGlitchesData != null)
                    || (mTestType == Constant.LOOPBACK_PLUG_AUDIO_THREAD_TEST_TYPE_LATENCY
                    && mCorrelation.isValid())) {
                // Create a PopUpWindow with scrollable TextView
                View puLayout = this.getLayoutInflater().inflate(R.layout.report_window, null);
                PopupWindow popUp = new PopupWindow(puLayout, ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT, true);

                // Generate report of glitch intervals and set pop up window text
                TextView reportText =
                        (TextView) popUp.getContentView().findViewById(R.id.ReportInfo);
                reportText.setText(getReport().toString());

                // display pop up window, dismissible with back button
                popUp.showAtLocation((View) findViewById(R.id.linearLayoutMain), Gravity.TOP, 0, 0);
            } else {
                showToastImportant("Please run the tests to get data");
            }

        } else {
            showToast("Test in progress... please wait");
        }
    }
*/

    /**
     * Display pop up window of recorded metrics and system information
     */
    /*
    public void onButtonHeatMap(View view) {
        if (!isBusy()) {
            if (mTestType == Constant.LOOPBACK_PLUG_AUDIO_THREAD_TEST_TYPE_BUFFER_PERIOD
                    && mGlitchesData != null && mRecorderCallbackTimes != null
                    && mRecorderCallbackTimes != null) {

                // Create a PopUpWindow with heatMap custom view
                View puLayout = this.getLayoutInflater().inflate(R.layout.heatmap_window, null);
                PopupWindow popUp = new PopupWindow(puLayout, ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT, true);

                ((LinearLayout) popUp.getContentView()).addView(
                        new GlitchAndCallbackHeatMapView(this, mRecorderCallbackTimes,
                                mPlayerCallbackTimes,
                                GlitchesStringBuilder.getGlitchMilliseconds(mFFTSamplingSize,
                                        mFFTOverlapSamples, mGlitchesData, mSamplingRate),
                                mGlitchingIntervalTooLong, mBufferTestElapsedSeconds,
                                getResources().getString(R.string.heatTitle)));

                popUp.showAtLocation((View) findViewById(R.id.linearLayoutMain), Gravity.TOP, 0, 0);

            } else {
                showToastImportant("Please run the tests to get data");
            }

        } else {
            showToast("Test in progress... please wait");
        }
    }
*/

    /**
     * Redraw the plot according to mWaveData
     */
    public static void refreshPlots() {
//        mWavePlotView.setData(mRetainedFragment.getWaveData(), mSamplingRate);
//        mWavePlotView.redraw();
    }

    static String audioThreadTypeToString(int audioThreadType) {
        switch (audioThreadType) {
            case Constant.AUDIO_THREAD_TYPE_JAVA:
                return "JAVA";
            case Constant.AUDIO_THREAD_TYPE_NATIVE_SLES:
                return "NATIVE (SLES)";
            case Constant.AUDIO_THREAD_TYPE_NATIVE_AAUDIO:
                return "NATIVE (AAUDIO)";
        }
        return "DEFAULT";
    }

    private static void log(String msg) {
        Log.v(TAG, msg);
    }

    /*

    public void showToast(final String msg) {
        doShowToast(msg, false);
    }

    public void showToastImportant(final String msg) {
        doShowToast(msg, true);
    }

    private void doShowToast(final String msg, boolean isImportant) {
        // If launched from an intent, do not show unimportant toasts.
        // Intents are typically used by scripts, which run actions at high
        // rate. On some versions of Android this causes the toasts to
        // queue up, and they continue displaying long after the app has
        // finished the actions.
        if (mIntentRunning && !isImportant) {
            return;
        }
        // Make sure UI manipulations are only done on the UI thread
        runOnUiThread(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 10, 10);
                toast.show();
            }
        });
    }
    */

    /**
     * Get the application that runs this activity. Wrapper for getApplication().
     */
    public static LoopbackApplication getApp() {
        return new LoopbackApplication(loopbackContext);
    }

    /**
     * Save a .wav file of the wave plot on the main activity.
     */
//    public static void saveToWaveFile(Uri uri) {
//        double[] waveData = mRetainedFragment.getWaveData();
//        if (waveData != null && waveData.length > 0) {
//            AudioFileOutput audioFileOutput = new AudioFileOutput(loopbackContext.getApplicationContext(), uri,
//                    mSamplingRate);
//            boolean status = audioFileOutput.writeData(waveData);
//            if (status) {
//                String wavFileAbsolutePath = getPath(uri);
//                // for some devices getPath fails
//                if (wavFileAbsolutePath != null) {
//                    File file = new File(wavFileAbsolutePath);
//                    wavFileAbsolutePath = file.getAbsolutePath();
//                } else {
//                    wavFileAbsolutePath = "";
//                }
//                log("Finished exporting wave File " + wavFileAbsolutePath);
//            } else {
//                log("Something failed saving wave file");
//            }
//
//        }
//    }


    /**
     * Save a screenshot of the main activity.
     */
    /*
    private void saveScreenShot(Uri uri) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        FileOutputStream outputStream;
        try {
            parcelFileDescriptor = getApplicationContext().getContentResolver().
                    openFileDescriptor(uri, "w");

            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            outputStream = new FileOutputStream(fileDescriptor);

            log("Done creating output stream");

            LinearLayout LL = (LinearLayout) findViewById(R.id.linearLayoutMain);

            View v = LL.getRootView();
            v.setDrawingCacheEnabled(true);
            Bitmap b = v.getDrawingCache();

            //save
            b.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            parcelFileDescriptor.close();
            v.setDrawingCacheEnabled(false);
        } catch (Exception e) {
            log("Failed to open png file " + e);
        } finally {
            try {
                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                log("Error closing ParcelFile Descriptor");
            }
        }
    }
    */

    public static void saveHistogram(Uri uri, int[] bufferPeriodArray, int maxBufferPeriod) {
        // Create and histogram view bitmap
        HistogramView recordHisto = new HistogramView(loopbackContext, null);
        recordHisto.setBufferPeriodArray(bufferPeriodArray);
        recordHisto.setMaxBufferPeriod(maxBufferPeriod);

        // Draw histogram on bitmap canvas
        Bitmap histoBmp = Bitmap.createBitmap(HISTOGRAM_EXPORT_WIDTH,
                HISTOGRAM_EXPORT_HEIGHT, Bitmap.Config.ARGB_8888); // creates a MUTABLE bitmap
        recordHisto.fillCanvas(new Canvas(histoBmp), histoBmp.getWidth(), histoBmp.getHeight());

        saveImage(uri, histoBmp);
    }

    public static void saveHeatMap(Uri uri, BufferCallbackTimes recorderCallbackTimes,
                                   BufferCallbackTimes playerCallbackTimes, int[] glitchMilliseconds,
                                   boolean glitchesExceeded, int duration, String title) {
        Bitmap heatBmp = Bitmap.createBitmap(HEATMAP_DRAW_WIDTH, HEATMAP_DRAW_HEIGHT,
                Bitmap.Config.ARGB_8888);
        GlitchAndCallbackHeatMapView.fillCanvas(new Canvas(heatBmp), recorderCallbackTimes,
                playerCallbackTimes, glitchMilliseconds, glitchesExceeded, duration,
                title);
        saveImage(uri, Bitmap.createScaledBitmap(heatBmp,
                HEATMAP_DRAW_WIDTH / HEATMAP_EXPORT_DIVISOR,
                HEATMAP_DRAW_HEIGHT / HEATMAP_EXPORT_DIVISOR, false));
    }

    /**
     * Save an image to file.
     */
    public static void saveImage(Uri uri, Bitmap bmp) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        FileOutputStream outputStream;
        try {
            parcelFileDescriptor = loopbackContext.getApplicationContext().getContentResolver().
                    openFileDescriptor(uri, "w");

            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            outputStream = new FileOutputStream(fileDescriptor);

            log("Done creating output stream");

            // Save compressed bitmap to file
            bmp.compress(Bitmap.CompressFormat.PNG, EXPORTED_IMAGE_QUALITY, outputStream);
            parcelFileDescriptor.close();
        } catch (Exception e) {
            log("Failed to open png file " + e);
        } finally {
            try {
                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                log("Error closing ParcelFile Descriptor");
            }
        }
    }


    /**
     * Save a .txt file of the given buffer period's data.
     * First column is time, second column is count.
     */
    public static void saveBufferPeriod(Uri uri, int[] bufferPeriodArray, int maxBufferPeriod) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        FileOutputStream outputStream;
        if (bufferPeriodArray != null) {
            try {
                parcelFileDescriptor = loopbackContext.getApplicationContext().getContentResolver().
                        openFileDescriptor(uri, "w");

                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                outputStream = new FileOutputStream(fileDescriptor);
                log("Done creating output stream for saving buffer period");

                int usefulDataRange = Math.min(maxBufferPeriod + 1, bufferPeriodArray.length);
                int[] usefulBufferData = Arrays.copyOfRange(bufferPeriodArray, 0, usefulDataRange);

                String endline = "\n";
                String delimiter = ",";
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < usefulBufferData.length; i++) {
                    sb.append(i + delimiter + usefulBufferData[i] + endline);
                }

                outputStream.write(sb.toString().getBytes());

            } catch (Exception e) {
                log("Failed to open text file " + e);
            } finally {
                try {
                    if (parcelFileDescriptor != null) {
                        parcelFileDescriptor.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log("Error closing ParcelFile Descriptor");
                }
            }
        }

    }

    /**
     * Save a .txt file of various test results.
     */
    public static void saveTextToFile(Uri uri, String outputText) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        FileOutputStream outputStream;
        try {
            parcelFileDescriptor = loopbackContext.getApplicationContext().getContentResolver().
                    openFileDescriptor(uri, "w");

            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            outputStream = new FileOutputStream(fileDescriptor);
            log("Done creating output stream");

            outputStream.write(outputText.getBytes());
            parcelFileDescriptor.close();
        } catch (Exception e) {
            log("Failed to open text file " + e);
        } finally {
            try {
                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                log("Error closing ParcelFile Descriptor");
            }
        }
    }

    public static StringBuilder getReport() {
        String endline = "\n";
        final int stringLength = 300;
        StringBuilder sb = new StringBuilder(stringLength);
        sb.append("DateTime = " + mTestStartTimeString + endline);
        sb.append(INTENT_SAMPLING_FREQUENCY + " = " + mSamplingRate + endline);
        sb.append(INTENT_CHANNEL_INDEX + " = " + mChannelIndex + endline);
        sb.append(INTENT_RECORDER_BUFFER + " = " + mRecorderBufferSizeInBytes /
                Constant.BYTES_PER_FRAME + endline);
        sb.append(INTENT_PLAYER_BUFFER + " = " + mPlayerBufferSizeInBytes /
                Constant.BYTES_PER_FRAME + endline);
        sb.append(INTENT_AUDIO_THREAD + " = " + mAudioThreadType + endline);

        String audioType = audioThreadTypeToString(mAudioThreadType);
        sb.append(INTENT_AUDIO_THREAD + "_String = " + audioType + endline);

        sb.append(INTENT_MIC_SOURCE + " = " + mMicSource + endline);
        sb.append(INTENT_MIC_SOURCE + "_String = " + getApp().getMicSourceString(mMicSource)
                + endline);
        sb.append(INTENT_AUDIO_LEVEL + " = " + mSoundLevel + endline);
        Log.d("tag", "result--===:>" + sb.toString());
        switch (mTestType) {

            case Constant.LOOPBACK_PLUG_AUDIO_THREAD_TEST_TYPE_LATENCY:
                sb.append(INTENT_IGNORE_FIRST_FRAMES + " = " + mIgnoreFirstFrames + endline);
                if (mCorrelation.isValid()) {
                    sb.append(String.format("LatencyMs = %.2f", mCorrelation.mEstimatedLatencyMs)
                            + endline);
                } else {
                    sb.append(String.format("LatencyMs = unknown") + endline);
                }

                sb.append(String.format("LatencyConfidence = %.2f",
                        mCorrelation.mEstimatedLatencyConfidence) + endline);

                sb.append(String.format("Average = %.4f", mCorrelation.mAverage) + endline);
                sb.append(String.format("RMS = %.4f", mCorrelation.mRms) + endline);
                break;

            case Constant.LOOPBACK_PLUG_AUDIO_THREAD_TEST_TYPE_BUFFER_PERIOD:
                sb.append("Buffer Test Duration (s) = " + mBufferTestDurationInSeconds + endline);

                // report recorder results
                int[] recorderBufferData = null;
                int recorderBufferDataMax = 0;
                double recorderBufferDataStdDev = 0.0;
                switch (mAudioThreadType) {
                    case Constant.AUDIO_THREAD_TYPE_JAVA:
                        recorderBufferData = mRecorderBufferPeriod.getBufferPeriodArray();
                        recorderBufferDataMax = mRecorderBufferPeriod.getMaxBufferPeriod();
                        recorderBufferDataStdDev = mRecorderBufferPeriod.getStdDevBufferPeriod();
                        break;
                    case Constant.AUDIO_THREAD_TYPE_NATIVE_SLES:
                    case Constant.AUDIO_THREAD_TYPE_NATIVE_AAUDIO:
                        recorderBufferData = mNativeRecorderBufferPeriodArray;
                        recorderBufferDataMax = mNativeRecorderMaxBufferPeriod;
                        recorderBufferDataStdDev = mNativeRecorderStdDevBufferPeriod;
                        break;
                }
                // report expected recorder buffer period
                if (recorderBufferData != null) {
                    // this is the range of data that actually has values
                    int usefulDataRange = Math.min(recorderBufferDataMax + 1,
                            recorderBufferData.length);
                    int[] usefulBufferData = Arrays.copyOfRange(recorderBufferData, 0,
                            usefulDataRange);
                    PerformanceMeasurement measurement = new PerformanceMeasurement(
                            mRecorderCallbackTimes.getExpectedBufferPeriod(), usefulBufferData);
                    double recorderPercentAtExpected =
                            measurement.percentBufferPeriodsAtExpected();
                    double benchmark = measurement.computeWeightedBenchmark();
                    int outliers = measurement.countOutliers();
                    sb.append("Expected Recorder Buffer Period (ms) = " +
                            mRecorderCallbackTimes.getExpectedBufferPeriod() + endline);
                    sb.append("Recorder Buffer Periods At Expected = " +
                            String.format("%.5f%%", recorderPercentAtExpected * 100) + endline);

                    sb.append("Recorder Buffer Period Std Dev = "
                            + String.format(Locale.US, "%.5f ms", recorderBufferDataStdDev)
                            + endline);

                    // output thousandths of a percent not at expected buffer period
                    sb.append("kth% Late Recorder Buffer Callbacks = "
                            + String.format("%.5f", (1 - recorderPercentAtExpected) * 100000)
                            + endline);
                    sb.append("Recorder Benchmark = " + benchmark + endline);
                    sb.append("Recorder Number of Outliers = " + outliers + endline);
                } else {
                    sb.append("Cannot Find Recorder Buffer Period Data!" + endline);
                }

                // report player results
                int[] playerBufferData = null;
                int playerBufferDataMax = 0;
                double playerBufferDataStdDev = 0.0;
                switch (mAudioThreadType) {
                    case Constant.AUDIO_THREAD_TYPE_JAVA:
                        playerBufferData = mPlayerBufferPeriod.getBufferPeriodArray();
                        playerBufferDataMax = mPlayerBufferPeriod.getMaxBufferPeriod();
                        playerBufferDataStdDev = mPlayerBufferPeriod.getStdDevBufferPeriod();
                        break;
                    case Constant.AUDIO_THREAD_TYPE_NATIVE_SLES:
                    case Constant.AUDIO_THREAD_TYPE_NATIVE_AAUDIO:
                        playerBufferData = mNativePlayerBufferPeriodArray;
                        playerBufferDataMax = mNativePlayerMaxBufferPeriod;
                        playerBufferDataStdDev = mNativePlayerStdDevBufferPeriod;
                        break;
                }
                // report expected player buffer period
                sb.append("Expected Player Buffer Period (ms) = " +
                        mPlayerCallbackTimes.getExpectedBufferPeriod() + endline);
                if (playerBufferData != null) {
                    // this is the range of data that actually has values
                    int usefulDataRange = Math.min(playerBufferDataMax + 1,
                            playerBufferData.length);
                    int[] usefulBufferData = Arrays.copyOfRange(playerBufferData, 0,
                            usefulDataRange);
                    PerformanceMeasurement measurement = new PerformanceMeasurement(
                            mPlayerCallbackTimes.getExpectedBufferPeriod(), usefulBufferData);
                    double playerPercentAtExpected = measurement.percentBufferPeriodsAtExpected();
                    double benchmark = measurement.computeWeightedBenchmark();
                    int outliers = measurement.countOutliers();
                    sb.append("Player Buffer Periods At Expected = "
                            + String.format("%.5f%%", playerPercentAtExpected * 100) + endline);

                    sb.append("Player Buffer Period Std Dev = "
                            + String.format(Locale.US, "%.5f ms", playerBufferDataStdDev)
                            + endline);

                    // output thousandths of a percent not at expected buffer period
                    sb.append("kth% Late Player Buffer Callbacks = "
                            + String.format("%.5f", (1 - playerPercentAtExpected) * 100000)
                            + endline);
                    sb.append("Player Benchmark = " + benchmark + endline);
                    sb.append("Player Number of Outliers = " + outliers + endline);

                } else {
                    sb.append("Cannot Find Player Buffer Period Data!" + endline);
                }
                // report glitches per hour
                int numberOfGlitches = estimateNumberOfGlitches(mGlitchesData);
                double testDurationInHours = mBufferTestElapsedSeconds
                        / (double) Constant.SECONDS_PER_HOUR;

                // Report Glitches Per Hour if sufficient data available, ie at least half an hour
                if (testDurationInHours >= .5) {
                    int glitchesPerHour = (int) Math.ceil(numberOfGlitches / testDurationInHours);
                    sb.append("Glitches Per Hour = " + glitchesPerHour + endline);
                }
                sb.append("Total Number of Glitches = " + numberOfGlitches + endline);

                // report if the total glitching interval is too long
                sb.append("Total glitching interval too long =  " +
                        mGlitchingIntervalTooLong);

                sb.append("\nLate Player Callbacks = ");
                sb.append(mPlayerCallbackTimes.getNumLateOrEarlyCallbacks());
                sb.append("\nLate Player Callbacks Exceeded Capacity = ");
                sb.append(mPlayerCallbackTimes.isCapacityExceeded());
                sb.append("\nLate Recorder Callbacks = ");
                sb.append(mRecorderCallbackTimes.getNumLateOrEarlyCallbacks());
                sb.append("\nLate Recorder Callbacks Exceeded Capacity = ");
                sb.append(mRecorderCallbackTimes.isCapacityExceeded());
                sb.append("\n");
        }


        String info = getApp().getSystemInfo();
        sb.append("SystemInfo = " + info + endline);

        return sb;
    }

    /**
     * Save a .txt file of of glitch occurrences in ms from beginning of test.
     */
    public static void saveGlitchOccurrences(Uri uri, int[] glitchesData) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        FileOutputStream outputStream;
        try {
            parcelFileDescriptor = loopbackContext.getApplicationContext().getContentResolver().
                    openFileDescriptor(uri, "w");

            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            outputStream = new FileOutputStream(fileDescriptor);

            log("Done creating output stream");

            outputStream.write(GlitchesStringBuilder.getGlitchStringForFile(mFFTSamplingSize,
                    mFFTOverlapSamples, glitchesData, mSamplingRate).getBytes());
        } catch (Exception e) {
            log("Failed to open text file " + e);
        } finally {
            try {
                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                log("Error closing ParcelFile Descriptor");
            }
        }
    }

    /**
     * Estimate the number of glitches. This version of estimation will count two consecutive
     * glitching intervals as one glitch. This is because two time intervals are partly overlapped.
     * Note: If the total glitching intervals exceed the length of glitchesData, this estimation
     * becomes incomplete. However, whether or not the total glitching interval is too long will
     * also be indicated, and in the case it's true, we know something went wrong.
     */
    private static int estimateNumberOfGlitches(int[] glitchesData) {
        final int discard = 10; // don't count glitches occurring at the first few FFT interval
        boolean isPreviousGlitch = false; // is there a glitch in previous interval or not
        int previousFFTInterval = -1;
        int count = 0;
        // if there are three consecutive glitches, the first two will be counted as one,
        // the third will be counted as another one
        for (int i = 0; i < glitchesData.length; i++) {
            if (glitchesData[i] > discard) {
                if (glitchesData[i] == previousFFTInterval + 1 && isPreviousGlitch) {
                    isPreviousGlitch = false;
                    previousFFTInterval = glitchesData[i];
                } else {
                    isPreviousGlitch = true;
                    previousFFTInterval = glitchesData[i];
                    count += 1;
                }
            }

        }

        return count;
    }


    /**
     * Estimate the number of glitches. This version of estimation will count the whole consecutive
     * intervals as one glitch. This version is not currently used.
     * Note: If the total glitching intervals exceed the length of glitchesData, this estimation
     * becomes incomplete. However, whether or not the total glitching interval is too long will
     * also be indicated, and in the case it's true, we know something went wrong.
     */
    private static int estimateNumberOfGlitches2(int[] glitchesData) {
        final int discard = 10; // don't count glitches occurring at the first few FFT interval
        int previousFFTInterval = -1;
        int count = 0;
        for (int i = 0; i < glitchesData.length; i++) {
            if (glitchesData[i] > discard) {
                if (glitchesData[i] != previousFFTInterval + 1) {
                    count += 1;
                }
                previousFFTInterval = glitchesData[i];
            }
        }
        return count;
    }

    /**
     * Check whether we have the RECORD_AUDIO permission
     *
     * @return true if we do
     */
    public static boolean hasRecordAudioPermission() {
        boolean hasPermission = (ContextCompat.checkSelfPermission(loopbackContext,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED);

        log("Has RECORD_AUDIO permission? " + hasPermission);
        return hasPermission;
    }

    /**
     * Requests the RECORD_AUDIO permission from the user
     */
    public static void requestRecordAudioPermission(int requestCode) {

        String requiredPermission = Manifest.permission.RECORD_AUDIO;

        // If the user previously denied this permission then show a message explaining why
        // this permission is needed
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) loopbackContext,
                requiredPermission)) {

            log("This app needs to record audio through the microphone to test " +
                    "the device's performance");
        }

        // request the permission.
        ActivityCompat.requestPermissions((Activity) loopbackContext, new String[]{requiredPermission}, requestCode);
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        // Save all files or run requested test after being granted permissions
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_RESULTS) {
                saveAllTo(getFileNamePrefix());
            } else if (requestCode == PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SCRIPT) {
                AtraceScriptsWriter.writeScriptsToFile(loopbackContext);
            } else if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO_BUFFER) {
                startBufferTest();
            } else if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO_LATENCY) {
                startlatencytest();
            }
        }
    }

    /**
     * Check whether we have the WRITE_EXTERNAL_STORAGE permission
     *
     * @return true if we do
     */
    public static boolean hasWriteFilePermission() {
        boolean hasPermission = (ContextCompat.checkSelfPermission(loopbackContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

        log("Has WRITE_EXTERNAL_STORAGE? " + hasPermission);
        return hasPermission;
    }

    /**
     * Requests the WRITE_EXTERNAL_STORAGE permission from the user
     */
    public static void requestWriteFilePermission(int requestCode) {

        String requiredPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        // request the permission.
        ActivityCompat.requestPermissions((Activity) loopbackContext, new String[]{requiredPermission}, requestCode);
    }

    /**
     * Receive results from save files DialogAlert and either save all files directly
     * or use filename dialog
     */
    public void onSaveDialogSelect(DialogFragment dialog, boolean saveWithoutDialog) {
        if (saveWithoutDialog) {
            saveAllTo("loopback_" + mTestStartTimeString);
        } else {
            /*
            SaveFilesWithDialog();
            */
        }
    }

    private void restoreInstanceState(Bundle in) {
        mTestType = in.getInt("mTestType");
        mMicSource = in.getInt("mMicSource");
        mAudioThreadType = in.getInt("mAudioThreadType");
        mSamplingRate = in.getInt("mSamplingRate");
        mChannelIndex = in.getInt("mChannelIndex");
        mSoundLevel = in.getInt("mSoundLevel");
        mPlayerBufferSizeInBytes = in.getInt("mPlayerBufferSizeInBytes");
        mRecorderBufferSizeInBytes = in.getInt("mRecorderBufferSizeInBytes");

        mTestStartTimeString = in.getString("mTestStartTimeString");

        mGlitchesData = in.getIntArray("mGlitchesData");
        if (mGlitchesData != null) {
            mGlitchingIntervalTooLong = in.getBoolean("mGlitchingIntervalTooLong");
            mFFTSamplingSize = in.getInt("mFFTSamplingSize");
            mFFTOverlapSamples = in.getInt("mFFTOverlapSamples");
            mBufferTestStartTime = in.getLong("mBufferTestStartTime");
            mBufferTestElapsedSeconds = in.getInt("mBufferTestElapsedSeconds");
            mBufferTestDurationInSeconds = in.getInt("mBufferTestDurationInSeconds");
            mBufferTestWavePlotDurationInSeconds =
                    in.getInt("mBufferTestWavePlotDurationInSeconds");

//            findViewById(R.id.glitchReportPanel).setVisibility(View.VISIBLE);
        }

//        if (mRetainedFragment.getWaveData() != null) {
//            mCorrelation = in.getParcelable("mCorrelation");
//            mPlayerBufferPeriod = in.getParcelable("mPlayerBufferPeriod");
//            mRecorderBufferPeriod = in.getParcelable("mRecorderBufferPeriod");
//            mPlayerCallbackTimes = in.getParcelable("mPlayerCallbackTimes");
//            mRecorderCallbackTimes = in.getParcelable("mRecorderCallbackTimes");
//
//            mNativePlayerBufferPeriodArray = in.getIntArray("mNativePlayerBufferPeriodArray");
//            mNativePlayerMaxBufferPeriod = in.getInt("mNativePlayerMaxBufferPeriod");
//            mNativeRecorderBufferPeriodArray = in.getIntArray("mNativeRecorderBufferPeriodArray");
//            mNativeRecorderMaxBufferPeriod = in.getInt("mNativeRecorderMaxBufferPeriod");

//            mWavePlotView.setData(mRetainedFragment.getWaveData(), mSamplingRate);
//            refreshState();
//            findViewById(R.id.zoomAndSaveControlPanel).setVisibility(View.VISIBLE);
//            findViewById(R.id.resultSummary).setVisibility(View.VISIBLE);
//        }

    }

    protected void onSaveInstanceState(Bundle out) {

        out.putInt("mTestType", mTestType);
        out.putInt("mMicSource", mMicSource);
        out.putInt("mAudioThreadType", mAudioThreadType);
        out.putInt("mSamplingRate", mSamplingRate);
        out.putInt("mChannelIndex", mChannelIndex);
        out.putInt("mSoundLevel", mSoundLevel);
        out.putInt("mPlayerBufferSizeInBytes", mPlayerBufferSizeInBytes);
        out.putInt("mRecorderBufferSizeInBytes", mRecorderBufferSizeInBytes);
        out.putString("mTestStartTimeString", mTestStartTimeString);

        out.putParcelable("mCorrelation", mCorrelation);
        out.putParcelable("mPlayerBufferPeriod", mPlayerBufferPeriod);
        out.putParcelable("mRecorderBufferPeriod", mRecorderBufferPeriod);
        out.putParcelable("mPlayerCallbackTimes", mPlayerCallbackTimes);
        out.putParcelable("mRecorderCallbackTimes", mRecorderCallbackTimes);

        out.putIntArray("mNativePlayerBufferPeriodArray", mNativePlayerBufferPeriodArray);
        out.putInt("mNativePlayerMaxBufferPeriod", mNativePlayerMaxBufferPeriod);
        out.putIntArray("mNativeRecorderBufferPeriodArray", mNativeRecorderBufferPeriodArray);
        out.putInt("mNativeRecorderMaxBufferPeriod", mNativeRecorderMaxBufferPeriod);

        // buffer test values
        out.putIntArray("mGlitchesData", mGlitchesData);
        out.putBoolean("mGlitchingIntervalTooLong", mGlitchingIntervalTooLong);
        out.putInt("mFFTSamplingSize", mFFTSamplingSize);
        out.putInt("mFFTOverlapSamples", mFFTOverlapSamples);
        out.putLong("mBufferTestStartTime", mBufferTestStartTime);
        out.putInt("mBufferTestElapsedSeconds", mBufferTestElapsedSeconds);
        out.putInt("mBufferTestDurationInSeconds", mBufferTestDurationInSeconds);
        out.putInt("mBufferTestWavePlotDurationInSeconds", mBufferTestWavePlotDurationInSeconds);
    }

}
