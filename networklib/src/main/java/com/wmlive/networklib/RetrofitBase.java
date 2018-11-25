package com.wmlive.networklib;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.wmlive.networklib.callback.NetworkCallback;
import com.wmlive.networklib.callback.RxNetworkCallback;
import com.wmlive.networklib.entity.BaseResponse;
import com.wmlive.networklib.okhttp.CookieManager;
import com.wmlive.networklib.retrofit.FastJsonConverterFactory;
import com.wmlive.networklib.retrofit.HttpLoggerInterceptor;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Created by Administrator on 4/30/2017.
 * <p>
 * 基本的Retrofit配置
 */

public abstract class RetrofitBase {

    protected final String LOG_TAG = "[--wmlive_network--]";
    protected Context context;

    private Retrofit retrofit;

    private OkHttpClient okHttpClient;

    private String cachePath;  //缓存路径

    private String baseUrl;

    private String webCookieHost = "wmlives.com";

    private boolean isDebug;

    private OkHttpClient.Builder okHttpBuilder;

    private CookieManager cookieManager;

    public RetrofitBase(Context context) {
        this.context = context;
    }

    /**
     * 初始化
     */
    public RetrofitBase with(String baseUrl, boolean isDebug, String cachePath) {
        this.baseUrl = baseUrl;
        this.isDebug = isDebug;
        this.cachePath = cachePath;
        okHttpBuilder = new OkHttpClient.Builder();
        return this;
    }

    public RetrofitBase setWebCookieHost(String host) {
        webCookieHost = host;
        return this;
    }

    /**
     * 添加okHttp的拦截器
     *
     * @param interceptors
     * @return
     */
    public RetrofitBase addInterceptors(Interceptor... interceptors) {
        if (null == okHttpBuilder) {
            throw new NullPointerException("You must invoke with method first!!!");
        }
        for (Interceptor interceptor : interceptors) {
            okHttpBuilder.addInterceptor(interceptor);
        }
        return this;
    }

    /**
     * 创建Retrofit请求框架
     */
    public void create() {
        if (getRetrofit() != null) {
            throw new IllegalStateException("Retrofit has been initialized!!!");
        }
        synchronized (RetrofitBase.this) {
            cookieManager = new CookieManager(context);
            cookieManager.setWebHost(webCookieHost);
            okHttpClient = okHttpBuilder
//                    .cache(new Cache(new File(getCachePath()), 1024 << 17))
                    .readTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .addInterceptor(new HttpLoggerInterceptor().setLevel(isDebug ?
                            HttpLoggerInterceptor.Level.HEADERS : HttpLoggerInterceptor.Level.NONE))
//                    .addInterceptor(new CacheInterceptor(context))
                    .cookieJar(cookieManager)
//                    .addInterceptor(new CommonInterceptor())
//                    .addInterceptor(new SignInterceptor(this))
//                    .authenticator(new AuthenticatorManager())
                    .build();
            retrofit = new Retrofit.Builder()
                    .addConverterFactory(FastJsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(baseUrl)
                    .client(okHttpClient)
                    .build();
        }
    }

    public Context getContext() {
        return context;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public void clearCookie() {
        if (cookieManager != null) {
            cookieManager.clearCookie();
        }
    }

    public CookieManager getCookieManager() {
        return cookieManager;
    }

    /**
     * http缓存目录
     *
     * @return
     */
    private String getCachePath() {
        if (TextUtils.isEmpty(cachePath)) {
            cachePath = context.getExternalCacheDir() != null ?
                    context.getExternalCacheDir().getAbsolutePath() :
                    Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "app_http_cache";
        }
        return cachePath;
    }

    /**
     * 取消请求
     *
     * @param tag
     * @param requestCode
     */
    public abstract boolean cancelRequest(String tag, int requestCode);

    /**
     * 取消一组请求
     *
     * @param tag
     */
    public abstract boolean cancelRequest(String tag);

    public abstract <T> void asyncRequest(final String tag, final int requestCode, final Call<T> requestCall, final NetworkCallback<T> responseListener);

    public abstract <T> void syncRequest(final String tag, final int requestCode, final Call<T> requestCall, final NetworkCallback<T> responseListener);

    public <T extends BaseResponse> Observable<Response<T>> getObservable(String tag, int requestCode, Observable<Response<T>> observable, RxNetworkCallback<T> callback) {
        return null;
    }

}
