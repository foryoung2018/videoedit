package com.wmlive.hhvideo.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.wmlive.hhvideo.utils.KLog;

import java.util.List;

/**
 * Created by lsq on 5/14/2018 - 4:13 PM
 * 类描述：
 */
public abstract class DcBaseService extends Service {
    protected static final String KEY_COMMAND = "command";
    protected static final int CMD_PING = -1000;
    protected static final String CHANNEL_NAME = "wlhhvideo";
    protected static int notificateId = 100;
    protected boolean isAlive = false;

    public static void ping(Context context, Class<? extends DcBaseService> cls) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(KEY_COMMAND, CMD_PING);
        KLog.d("ping");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            try {
                context.startForegroundService(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            context.startService(intent);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        KLog.w(getClass().getSimpleName() + " is onCreate");
        // safeStartForeground();
    }

    /**
     * startForeground a notification must be in both onCreate and onStartCommand,
     * because if your service is already created and somehow your activity is trying to start it again, onCreate won't be called.
     */
    public void safeStartForeground() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            try {
                NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (nm != null) {
                    NotificationChannel channel = new NotificationChannel(String.valueOf(notificateId),
                            CHANNEL_NAME, NotificationManager.IMPORTANCE_NONE);
                    nm.createNotificationChannel(channel);
                    startForeground(notificateId, new Notification.Builder(this, channel.getId()).build());
                }
                KLog.e(nm + "=======startForeground notificateId:" + notificateId);
                notificateId += 10;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String serviceName = getClass().getSimpleName();
            int cmd = intent.getIntExtra(KEY_COMMAND, 0);
            if (cmd == CMD_PING) {
//                KLog.e(serviceName + " receive a ping message");
                onPingCommand(intent, flags, startId);
            } else {
                onCustomCommand(intent, flags, startId);
            }
            KLog.e(serviceName + "=onStartCommand ，isAlive:" + isAlive);
        }
        isAlive = true;
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public abstract void onCustomCommand(Intent intent, int flags, int startId);

    public abstract void onPingCommand(Intent intent, int flags, int startId);

    @Override
    public void onDestroy() {
        super.onDestroy();
        KLog.e(getClass().getSimpleName() + " onDestroy");
    }


    private boolean isServiceRunning(final String className) {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> info = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (info == null || info.size() == 0) return false;
        for (ActivityManager.RunningServiceInfo aInfo : info) {
            if (className.equals(aInfo.service.getClassName())) return true;
        }
        return false;
    }
}
