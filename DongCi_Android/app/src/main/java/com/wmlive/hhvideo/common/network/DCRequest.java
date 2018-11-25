package com.wmlive.hhvideo.common.network;

import android.content.Context;
import android.util.Log;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.utils.AppCacheFileUtils;
import com.wmlive.networklib.RetrofitBase;
import com.wmlive.networklib.RetrofitWrap;
import com.wmlive.networklib.RxJava2Retrofit;
import com.wmlive.networklib.util.NetLog;


/**
 * Retrofit网络请求ApiManager
 */
public class DCRequest {

    private static final String LOG_TAG = "[--wmlive_network--]";
    private ApiService httpApi;
    private RetrofitBase retrofit;//这个是全局对象，不影响Context内存泄漏
    private static final String DEBUG_COOKIE_HOST = "wmlives.com";
    private static final String RELEASE_COOKIE_HOST = "wmlive.cn";

    public DCRequest(){
        Log.d(LOG_TAG,"DCRequest-init"+retrofit);
    }

    private static final class YWRequestHolder {
        static final DCRequest INSTANCE = new DCRequest();

    }

    public static DCRequest getInstance() {
        return YWRequestHolder.INSTANCE;
    }

    /**
     * 初始化RxJava的Retrofit,请务必在Application中初始化
     *
     * @param context   Context
     * @param isDebug   是否是debug模式
     * @param cachePath http缓存路径
     */
    public void initRxJavaRetrofit(Context context, boolean isDebug, String cachePath, String logTag) {
        NetLog.init(isDebug, "DcAppLogNet");
        if (retrofit == null) {
            retrofit = new RxJava2Retrofit(context.getApplicationContext());
            retrofit.setWebCookieHost(GlobalParams.Config.IS_DEBUG ? DEBUG_COOKIE_HOST : RELEASE_COOKIE_HOST);
            retrofit.with(GlobalParams.Config.APP_DEBUG_URL, isDebug, cachePath)
                    .addInterceptors(new HeaderInterceptor())
                    .create();
        }
        if (httpApi == null) {
            httpApi = retrofit.getRetrofit().create(ApiService.class);
        }
        NetLog.i("retrofit初始化完成");
    }

    /**
     * 初始化Retrofit,请务必在Application中初始化
     *
     * @param context   Context
     * @param isDebug   是否是debug模式
     * @param cachePath http缓存路径
     */
    public void initRetrofit(Context context, boolean isDebug, String cachePath, String logTag) {
        NetLog.init(isDebug, logTag);
        if (retrofit == null) {
            retrofit = new RetrofitWrap(context.getApplicationContext());
            retrofit.with(GlobalParams.Config.APP_DEBUG_URL, isDebug, cachePath).addInterceptors(new HeaderInterceptor()).create();
        }
        httpApi = retrofit.getRetrofit().create(ApiService.class);
    }

    public static RetrofitBase getRetrofit() {
        if (getInstance().retrofit == null) {
            getInstance().initRxJavaRetrofit(DCApplication.getDCApp(),
                    GlobalParams.Config.IS_DEBUG,
                    AppCacheFileUtils.getAppHttpCachePath(),
                    GlobalParams.Config.APP_LOG_TAG);
        }
        return getInstance().retrofit;
    }

    /**
     * 获取HttpApi
     *
     * @return HttpApi
     */
    public static ApiService getHttpApi() {
        if (getInstance().httpApi == null) {
            if(getRetrofit().getRetrofit()==null)//异常情况，
                getInstance().retrofit=null;//重新初始化
            getInstance().httpApi = getRetrofit().getRetrofit().create(ApiService.class);
        }
        return getInstance().httpApi;
    }

}
