package com.wmlive.hhvideo.utils;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.List;

public class ServiceUtils {

    /**
     * 校验某个服务是否还存在
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        // 校验服务是否还存在  
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info : services) {
            // 得到所有正在运行的服务的名称  
            String name = info.service.getClassName();
            if (serviceName.equals(name)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 开始循环开去服务
     *
     * @param con
     * @param triggerAtMillis 第一次执行的时间
     * @param intervalMillis  两次间隔时间
     * @param cls
     * @param action
     * @param packageName
     */
    public static void startPollingService(Context con, long triggerAtMillis, long intervalMillis, Class<?> cls, String action, String packageName) {
        // 获取AlarmManager 系统服务
        AlarmManager manager = (AlarmManager) con.getSystemService(Context.ALARM_SERVICE);
        Intent mIntent = new Intent(con, cls);
        mIntent.setAction(action);
        mIntent.setPackage(packageName);
        PendingIntent pendingIntent = PendingIntent.getService(con, 0, mIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        // 触发时间
        long time = System.currentTimeMillis();
        if (triggerAtMillis <= 0) {
            triggerAtMillis = time;
        } else {
            triggerAtMillis = time + triggerAtMillis * 1000;
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            //参数2是开始时间、参数3是允许系统延迟的时间
//            manager.setWindow(AlarmManager.RTC, triggerAtMillis, intervalMillis * 1000, pendingIntent);
//        } else {
        manager.setRepeating(AlarmManager.RTC, triggerAtMillis, intervalMillis * 1000, pendingIntent);
        // }
        KLog.e("time_check", "------------startPollingService-----start ---: " + triggerAtMillis);
    }

    /**
     * 停止定时服务
     *
     * @param con
     * @param cls
     * @param action
     */
    public static void stopPollingService(Context con, Class<?> cls, String action, String packageName) {
        AlarmManager manager = (AlarmManager) con.getSystemService(Context.ALARM_SERVICE);
        Intent mInent = new Intent(con, cls);
        mInent.setAction(action);
        mInent.setPackage(packageName);
        PendingIntent pendingIntent = PendingIntent.getService(con, 0, mInent, PendingIntent.FLAG_CANCEL_CURRENT);
        manager.cancel(pendingIntent);
        KLog.e("time_check", "------------stopPollingService-----stop ---");
    }
}  