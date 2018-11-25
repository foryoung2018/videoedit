package com.example.loopback;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class LoopBackTestAct extends LoopbackActivity {

    int testCount = 0;

    private double value = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //5s 后强制推出
        handler.sendEmptyMessageDelayed(0,5000);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            DCLatencytestTool.cutTime = getValue();
            finish();
        }
    };

    public void testCompleted(Correlation mCorrelation) {
        testCount ++;
        release();
        
        Log.i("RecordActivitySdk1", "mRms:" + mCorrelation.mRms);
        Log.i("RecordActivitySdk1", "mAverage:" + mCorrelation.mAverage);
        Log.i("RecordActivitySdk1", "mEstimatedLatencyMs:" + mCorrelation.mEstimatedLatencyMs);
        Log.i("RecordActivitySdk1", "mEstimatedLatencySamples:" + mCorrelation.mEstimatedLatencySamples);
        Log.i("RecordActivitySdk1", "mEstimatedLatencyConfidence:" + mCorrelation.mEstimatedLatencyConfidence);
        value = mCorrelation.mRms;
//        Toast.makeText(this,"mRms:"+value,Toast.LENGTH_LONG).show();
        if(value < 0.10){//测试失败，重试
            if(testCount < 4){//4次
                boolean result = startLatencyTest();
                return;
            }else{
                DCLatencytestTool.cutTime =(getValue()==0)?((long) (value * 1000)):getValue();
            }
        }else{
            DCLatencytestTool.cutTime = (long) (value * 1000);
            setValue(DCLatencytestTool.cutTime);
        }
//        重置为之前的系统音量
        if(noVolume){
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            am.setStreamVolume(AudioManager.STREAM_MUSIC,
                    systemVolumeLevel, 0);
        }
        DCLatencytestTool.cutTime =(DCLatencytestTool.cutTime==0)?((long) (value * 1000)):DCLatencytestTool.cutTime;
        Log.e("RecordActivitySdk1", "DCLatencytestTool.cutTime:" + value);
        finish();
    }
    SharedPreferences sharedPreferences;
    private void setValue(long value){
        sharedPreferences = getSharedPreferences("loopback", Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("mRms",value);
        editor.commit();
    }

    public long getValue(){
        sharedPreferences = getSharedPreferences("loopback", Context.MODE_MULTI_PROCESS);
        return sharedPreferences.getLong("mRms", (long) (0.20 * 1000));
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
