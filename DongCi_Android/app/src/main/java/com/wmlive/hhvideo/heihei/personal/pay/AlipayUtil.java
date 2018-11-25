package com.wmlive.hhvideo.heihei.personal.pay;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.alipay.sdk.app.PayTask;

import java.util.Map;

/**
 * Created by XueFei on 2017/7/26.
 * <p>
 * 支付宝支付
 */

public class AlipayUtil {
    public static final int SDK_PAY_FLAG = 1;
    public static final int SDK_AUTH_FLAG = 2;

    public static void alipay(final Activity activity, final String orderInfo, final Handler handler) {
        //异步处理
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                //新建任务
                PayTask alipay = new PayTask(activity);
                //获取支付结果
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }
}