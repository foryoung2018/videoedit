package com.wmlive.hhvideo.heihei.record.utils;

import android.os.Handler;
import android.view.View;


import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.utils.KLog;


/**
 * 倒计时
 */
public class TimerUtil {
    long startTime;
    long delayTime;
    View view;
    boolean flag;
    Handler handler;
    int count;
    public void startTimer(View view,int lastTotalTime, long delayTime, Handler handler) {
        count = lastTotalTime;
        this.delayTime = delayTime;
        this.view = view;
        flag = true;
        this.handler = handler;
        startTime = System.currentTimeMillis();
        view.postDelayed(countdownRunnable, delayTime);
    }

    private Runnable countdownRunnable = new Runnable() {
        @Override
        public void run() {
            if(flag && count<=(RecordManager.get().getSetting().maxVideoDuration) / 1000){//正在录制，时间在范围内
                view.postDelayed(this, getDelayTime());
                count++;
                KLog.d("TimeUtils--->"+count);
                handler.sendEmptyMessage(count);
            }
        }
    };

    /**
     * 取消定时器
     */
    public void stopTimer(){
        flag = false;
        if(handler!=null) {
            handler.sendEmptyMessage(0);
        }
    }

    /**
     * 算出真正的时间
     * @return
     */
    private long getDelayTime(){
        long realyTime = (System.currentTimeMillis()-startTime)%delayTime;
        return delayTime-realyTime;
    }


}
