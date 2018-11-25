package com.wmlive.hhvideo.push;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.push.AndroidPopupActivity;
import com.wmlive.hhvideo.heihei.mainhome.activity.MainActivity;
import com.wmlive.hhvideo.utils.JsonUtils;
import com.wmlive.hhvideo.utils.ToastUtil;

import java.util.Map;

import cn.wmlive.hhvideo.R;

/**
 * Created by XueFei on 2017/7/21.
 * <p>
 * 阿里云推送
 */

public class PopupPushActivity extends AndroidPopupActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 实现通知打开回调方法，获取通知相关信息
     *
     * @param title   标题
     * @param summary 内容
     * @param extMap  额外参数
     */
    @Override
    protected void onSysNoticeOpened(String title, String summary, Map<String, String> extMap) {
        Log.d(AliYunPushReceiver.REC_TAG, "onSysNoticeOpened, title: " + title + ", content: " + summary + ", extMap: " + extMap);
        String data = null;
        for (Map.Entry<String, String> entry : extMap.entrySet()) {
            Log.i(AliYunPushReceiver.REC_TAG, "@Get diy param : Key=" + entry.getKey() + " , Value=" + entry.getValue());
            if (entry.getKey().equalsIgnoreCase(LINK)) {
                data = entry.getValue();
            }
        }
        openNotification(this, data);
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
        }else {
            ToastUtil.showToast(R.string.hintErrorDataDelayTry);
        }
    }
}
