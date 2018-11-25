package com.wmlive.networklib.okhttp;

import android.content.Context;

import com.wmlive.networklib.util.NetUtil;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lsq on 5/9/2017.
 * 缓存拦截器
 */

public class CacheInterceptor implements Interceptor {
    private Context context;

    public CacheInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (!NetUtil.isNetworkConnected(context)) {
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .url(request.url()).build();
        }

        Response response = chain.proceed(request);
        if (NetUtil.isNetworkConnected(context)) {
            int maxAge = 60; // read from cache for 1 minute
            response.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .build();
        } else {
            int maxStale = 60; // tolerate 4-weeks stale
            response.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .build();
        }
        return response;
    }
}
