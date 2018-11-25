package com.wmlive.hhvideo.service;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.manager.gift.GiftManager;
import com.wmlive.hhvideo.heihei.mainhome.activity.MainActivity;
import com.wmlive.hhvideo.utils.KLog;
import java.util.List;

/**
 * Author：create by jht on 2018/8/22 11:57
 * Email：haitian.jiang@welines.cn
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class DcJobService extends JobService {

    private static final String CHANNEL_ID = "jobService";
    private int kJobId = 0;
    private NotificationChannel mNotificationChannel;
    private static final int NOTIFICATION_ID = 1001;

    @Override
    public void onCreate() {
        super.onCreate();
        runAsForegroundService();
    }

    private void runAsForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    getString(com.example.loopback.R.string.notificationText),
                    NotificationManager.IMPORTANCE_LOW);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mNotificationChannel);
        }

        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(com.example.loopback.R.drawable.icon).setContentTitle(getString(com.example.loopback.R.string.app_name))
                .setContentText(getString(com.example.loopback.R.string.notificationText));
        if (mNotificationChannel != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();
        } else {
            notification = builder.getNotification();
        }

        startForeground(NOTIFICATION_ID, notification);
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //  KLog.i("DcJobService", "jobService启动");
        scheduleJob(getJobInfo());
        return START_NOT_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        // KLog.i("DcJobService", "执行了onStartJob方法");
        boolean isDcServiceWork = isServiceWork(this, "com.wmlive.hhvideo.service.DCService");
        boolean isWebSocketServiceWork = isServiceWork(this, "com.wmlive.hhvideo.service.DcWebSocketService");
        boolean isGiftServiceWork = isServiceWork(this,"com.wmlive.hhvideo.service.GiftService");
        if (!isDcServiceWork) {
            this.startService(new Intent(this, DCService.class));
            // KLog.i("onStartJob", "启动DCService");
        }
        if(!isWebSocketServiceWork){
            DcWebSocketService.startSocket(DCApplication.getDCApp(), 1500);
            //  KLog.i("onStartJob", "启动DcWebSocketService");
        }
        if(!isGiftServiceWork){
            GiftManager.get().startGiftService();
            // KLog.i("onStartJob", "启动GiftService");
        }
        jobFinished(params,false);
        scheduleJob(getJobInfo());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        KLog.i("DcJobService", "执行了onStopJob方法");
        scheduleJob(getJobInfo());
        return true;
    }

    //将任务作业发送到作业调度中去
    public void scheduleJob(JobInfo t) {
        //  KLog.i("DcJobService", "调度job");
        JobScheduler tm =
                (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if(tm.getAllPendingJobs().size()<100){
            if(isJobPollServiceOn()){
                tm.cancel(kJobId);
            }else{
                tm.schedule(t);
            }
        }
    }

//    //将任务作业发送到作业调度中去
//    public void scheduleJob(JobInfo t) {
//        //  KLog.i("DcJobService", "调度job");
//        JobScheduler tm =
//                (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
//        if(isJobPollServiceOn()){
//            tm.cancel(kJobId);
//        }else{
//            if(tm.getAllPendingJobs().size()<100){
//                tm.schedule(t);
//            }
//        }
//    }

    public JobInfo getJobInfo() {
        JobInfo.Builder builder = new JobInfo.Builder(kJobId++, new ComponentName(getPackageName(), DcJobService.class.getName()));
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setPersisted(true);
        builder.setRequiresCharging(false);
        builder.setRequiresDeviceIdle(false);
        builder.setMinimumLatency(10 * 1000);
        return builder.build();
    }

    // 判断服务是否正在运行
    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(100);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    /**
     * 开启保活服务
     */
    public static void openJobService(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, DcJobService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else { // Pre-O behavior.
            context.startService(intent);
        }
    }

    /**
     * 停止服务
     */
    public static void stopJobService(Context context){
        Intent intent = new Intent();
        intent.setClass(context, DcJobService.class);
        context.stopService(intent);
    }

    /**
     * 判断一个任务是否已经安排过
     * @return
     */
    private boolean isJobPollServiceOn() {
        JobScheduler scheduler = (JobScheduler)getSystemService(Context.JOB_SCHEDULER_SERVICE);
        boolean hasBeenScheduled = false;
        //getAllPendingJobs得到是当前Package对应的已经安排的任务
        for (JobInfo jobInfo : scheduler.getAllPendingJobs()) {
            if (jobInfo.getId() == kJobId) {
                hasBeenScheduled = true;
                break;
            }
        }

        return hasBeenScheduled;
    }


}