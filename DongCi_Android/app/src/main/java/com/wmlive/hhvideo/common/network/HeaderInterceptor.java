package com.wmlive.hhvideo.common.network;

import android.text.TextUtils;

import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.utils.HeaderUtils;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by vhawk on 2017/5/22.
 */

public class HeaderInterceptor implements Interceptor {


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        HttpUrl.Builder httpUrlBuilder = originalRequest.url().newBuilder();

        HttpUrl httpUrl = originalRequest.url();
        boolean needToken = true;//是否需要token
//        if (httpUrl != null && httpUrl.pathSegments() != null) {
//            KLog.i("=======path:" + Arrays.toString(httpUrl.pathSegments().toArray()));
//            List<String> path = httpUrl.pathSegments();
//            for (String s : path) {
//                if ("api".equals(s) || "opus".equals(s)) {
//                    continue;
//                }
//                //以下是不需要token的接口
//                if ("init".equals(s)//启动接口
//                        || "list-opus-recommend".equals(s) //推荐列表
//                        || "list-opus-latest".equals(s)//最新列表
//                        || "list-topic".equals(s)//发现话题列表
//                        ) {
//                    KLog.i("======接口：" + s + " 不需要token");
//                    needToken = false;
//                    break;
//                }
//            }
//        }
        if (needToken) {
            //添加通用参数token
            String token = AccountUtil.getToken();
            if (!TextUtils.isEmpty(token)) {
                if ("get".equalsIgnoreCase(originalRequest.method())) {
                    String getToken = httpUrl.queryParameter("token");
                    if (TextUtils.isEmpty(getToken)) {
                        httpUrlBuilder.addQueryParameter("token", token);
                    }
                } else if ("post".equalsIgnoreCase(originalRequest.method())) {
                    if (originalRequest.body() != null && originalRequest.body() instanceof FormBody) {
                        FormBody oldFormBody = (FormBody) originalRequest.body();
                        boolean needAddToken = true;
                        for (int i = 0; i < oldFormBody.size(); i++) {
                            if ("token".equalsIgnoreCase(oldFormBody.encodedName(i))) {
                                needAddToken = false;
                                break;
                            }
                        }
                        if (needAddToken) {
                            httpUrlBuilder.addEncodedQueryParameter("token", token);
                        }
                    }
                } else {
                    // TODO: 5/31/2017 其他方式未处理
                }
            }
        }
        Request.Builder requestBuilder = originalRequest.newBuilder()
                .addHeader("app_name", HeaderUtils.getAppName())
                .addHeader("app_version", HeaderUtils.getAppVersion())
                .addHeader("os_version", HeaderUtils.getOsVersion())
                .addHeader("os_platform", HeaderUtils.getOsPlatform())
                .addHeader("device_model", HeaderUtils.getDeviceModel())
                .addHeader("device_id", HeaderUtils.getDeviceIdMsg())
                .addHeader("device_resolution", HeaderUtils.getDeviceResolution())
                .addHeader("device_ac", HeaderUtils.getDeviceAc())
                .addHeader("api_version", HeaderUtils.getApiVersion())
                .addHeader("build_number", HeaderUtils.getBuildNumber())
                .addHeader("channel", HeaderUtils.getChannel())
                .addHeader("lat_lon", HeaderUtils.getLocationInfo())
                .url(httpUrlBuilder.build());
        Response result1 = null;
        try {
            result1 = chain.proceed(requestBuilder.build());
        }catch (Exception e){
            e.printStackTrace();
        }
        return result1;
    }
}
