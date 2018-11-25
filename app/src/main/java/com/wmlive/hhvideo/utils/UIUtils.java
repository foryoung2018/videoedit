package com.wmlive.hhvideo.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.alibaba.sdk.android.push.MsgService;
import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.heihei.mainhome.activity.MainActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.wmlive.hhvideo.R;

/**
 * Created by hsing on 2018/2/2.
 */

public class UIUtils {
    /**
     * 计算录音播放的长度
     *
     * @param time 时间
     * @return
     */
    public static int reckonViewWidthIM(int time) {
        if (time <= 3)//76-180  6
            return ImageUtils.dip2px(80);
        if (time <= 6)
            return ImageUtils.dip2px(86);
        if (time <= 9)
            return ImageUtils.dip2px(92);
        if (time <= 12)
            return ImageUtils.dip2px(98);
        if (time <= 15)
            return ImageUtils.dip2px(104);
        if (time <= 18)
            return ImageUtils.dip2px(110);
        if (time <= 21)
            return ImageUtils.dip2px(116);
        if (time <= 24)
            return ImageUtils.dip2px(122);
        if (time <= 27)
            return ImageUtils.dip2px(128);
        if (time <= 30)
            return ImageUtils.dip2px(134);
        if (time <= 33)
            return ImageUtils.dip2px(140);
        if (time <= 36)
            return ImageUtils.dip2px(146);
        if (time <= 39)
            return ImageUtils.dip2px(152);
        if (time <= 42)
            return ImageUtils.dip2px(159);
        if (time <= 45)
            return ImageUtils.dip2px(166);
        if (time <= 48)
            return ImageUtils.dip2px(173);
        if (time <= 51)
            return ImageUtils.dip2px(180);
        if (time <= 54)
            return ImageUtils.dip2px(187);
        if (time <= 57)
            return ImageUtils.dip2px(194);
        if (time <= 60)
            return ImageUtils.dip2px(200);
        return ImageUtils.dip2px(120);
    }

    /**
     * 多个关键字高亮变色
     *
     * @param color   变化的色值
     * @param text    文字
     * @param keyword 文字中的关键字数组
     * @return
     */
    public static SpannableStringBuilder matcherSearchTitle(int color, String text, String... keyword) {
        SpannableStringBuilder s = new SpannableStringBuilder(text);
        for (int i = 0; i < keyword.length; i++) {
            Pattern p = Pattern.compile(keyword[i]);
            Matcher m = p.matcher(s);
            while (m.find()) {
                int start = m.start();
                int end = m.end();
                s.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return s;
    }

    /**
     * 发送通知
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void setNotification(String title, String text, String link) {

        int id = (int) (Math.random() * 100);

        NotificationManager manager = (NotificationManager) DCApplication.getDCApp().getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelId = "com.wmlive.hhvideo.id";
            CharSequence channelName = "动次通知";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(false); //是否在桌面icon右上角展示小红点
            channel.setShowBadge(false); //是否在久按桌面图标时显示此渠道的通知
            channel.enableVibration(false);
            channel.setSound(null, null);
            manager.createNotificationChannel(channel);
            builder = new Notification.Builder(DCApplication.getDCApp(), channelId);
        } else {
            builder = new Notification.Builder(DCApplication.getDCApp());
        }


        Intent notificationIntent = new Intent(DCApplication.getDCApp(), MainActivity.class);

        notificationIntent.setAction(Intent.ACTION_VIEW);
        notificationIntent.setData(Uri.parse(link));
        PendingIntent contentIntent = PendingIntent.getActivity(DCApplication.getDCApp(), 0, notificationIntent, 0);
        Log.d("notification", "setNotification: link===" + link);
        builder.setContentTitle(title);
        builder.setContentText(text);
        builder.setSmallIcon(R.mipmap.icon);
//        builder.setLargeIcon(Icon.createWithResource(DCApplication.getDCApp(), R.drawable.jpush_notification_icon));
        builder.setTicker("收到一个新的通知");
        builder.setContentIntent(contentIntent);//执行intent
        Notification notification = builder.getNotification();//将builder对象转换为普通的notification
        notification.flags |= Notification.FLAG_AUTO_CANCEL;//点击通知后通知消失
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        manager.notify(id, notification);
    }
}
