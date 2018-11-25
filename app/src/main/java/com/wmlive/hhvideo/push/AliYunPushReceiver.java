package com.wmlive.hhvideo.push;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.push.MessageReceiver;
import com.alibaba.sdk.android.push.notification.CPushMessage;
import com.wmlive.hhvideo.heihei.mainhome.activity.MainActivity;
import com.wmlive.hhvideo.utils.JsonUtils;

import java.util.Map;

/**
 * Created by XueFei on 2017/7/21.
 * <p>
 * 阿里云
 * 用于接收推送的通知和消息
 */
public class AliYunPushReceiver extends MessageReceiver {

    // 消息接收部分的LOG_TAG
    public static final String REC_TAG = "aliyun_receiver";

    /**
     * 推送通知的回调方法
     *
     * @param context
     * @param title
     * @param summary
     * @param extraMap
     */
    @Override
    public void onNotification(Context context, String title, String summary, Map<String, String> extraMap) {
        if (null != extraMap) {
            for (Map.Entry<String, String> entry : extraMap.entrySet()) {
                Log.i(REC_TAG, "onNotification @Get diy param : Key=" + entry.getKey() + " , Value=" + entry.getValue());
            }
        } else {
            Log.i(REC_TAG, "onNotification @收到通知 && 自定义消息为空");
        }
        Log.i(REC_TAG, "onNotification 收到一条推送通知 ： " + title);
    }

    @Override
    protected void onNotificationReceivedInApp(Context context, String title, String summary, Map<String, String> extraMap, int openType, String openActivity, String openUrl) {
        Log.i(REC_TAG, "onNotificationReceivedInApp ： " + " : " + title + " : " + summary + "  " + extraMap + " : " + openType + " : " + openActivity + " : " + openUrl);
    }

    /**
     * 推送消息的回调方法
     *
     * @param context
     * @param cPushMessage
     */
    @Override
    public void onMessage(Context context, CPushMessage cPushMessage) {
        try {
            Log.i(REC_TAG, "收到一条推送消息 ： " + cPushMessage.getTitle());
        } catch (Exception e) {
            Log.i(REC_TAG, e.toString());
        }
    }

    /**
     * 从通知栏打开通知的扩展处理
     *
     * @param context
     * @param title
     * @param summary
     * @param extraMap
     */
    @Override
    public void onNotificationOpened(Context context, String title, String summary, String extraMap) {
        Log.i(REC_TAG, "onNotificationOpened ： " + " : " + title + " : " + summary + " : " + extraMap);
        openNotification(context, extraMap);
    }


    @Override
    public void onNotificationRemoved(Context context, String messageId) {
        Log.i(REC_TAG, "onNotificationRemoved ： " + messageId);
    }


    @Override
    protected void onNotificationClickedWithNoAction(Context context, String title, String summary, String extraMap) {
        Log.i(REC_TAG, "onNotificationClickedWithNoAction ： " + " : " + title + " : " + summary + " : " + extraMap);
    }

    /**
     * 打开普通消息
     */
    private final String LINK = "link";

    private void openNotification(Context context, String data) {
        //获取link数据 例如:{"link":"hhvideo://topic/detail?id=10001"}
        JSONObject jsonObject = JsonUtils.parseObject(data);
        if (null != jsonObject) {
            String link = jsonObject.getString(LINK);
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setClass(context, MainActivity.class);
            intent.putExtra(MainActivity.KEY_CONTENT, link);
            context.startActivity(intent);
        }
    }
}