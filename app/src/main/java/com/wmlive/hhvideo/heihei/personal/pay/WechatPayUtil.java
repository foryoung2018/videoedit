package com.wmlive.hhvideo.heihei.personal.pay;

import android.app.Activity;

import com.alibaba.fastjson.JSONObject;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.wmlive.hhvideo.utils.JsonUtils;
import com.wmlive.hhvideo.utils.ToastUtil;

import cn.wmlive.hhvideo.R;

/**
 * Created by XueFei on 2017/7/26.
 * <p>
 * 微信支付
 */

public class WechatPayUtil {
    public static String WX_APP_ID = "wx4efac1ce792d3ce5";

    public static void wxChatPay(Activity activity, String data) {
        IWXAPI api = WXAPIFactory.createWXAPI(activity, WX_APP_ID);
        if (!api.isWXAppInstalled()) {
            ToastUtil.showToast("请先安装微信客户端");
            return;
        }
        //1.1，先检测微信app是否支持，否则不允许支付；
//        if (!api.isWXAppSupportAPI()) {
//            ToastUtil.showToast("请先升级微信客户端");
//            return;
//        }

        api.registerApp(WX_APP_ID);

        JSONObject json = JsonUtils.parseObject(data);
        if (json != null) {
            PayReq req = new PayReq();
            req.appId = json.getString("appid");
            req.partnerId = json.getString("partnerid");
            req.prepayId = json.getString("prepayid");
            req.nonceStr = json.getString("noncestr");
            req.timeStamp = json.getString("timestamp");
            req.packageValue = json.getString("package");
            req.sign = json.getString("sign");
            api.sendReq(req);
        } else {
            ToastUtil.showToast(R.string.hintErrorDataDelayTry);
        }
    }
}
