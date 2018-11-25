package com.wmlive.networklib.okhttp;

public class CommonInterceptor{} /**implements Interceptor {
    private Context context;

    public CommonInterceptor(Context context) {
        context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder newBuilder = originalRequest.newBuilder();
        Request compressedRequest;
        if (!NetCheckUtil.isNetworkConnected(context)) {
            newBuilder.cacheControl(CacheControl.FORCE_CACHE);//从缓存中读取
        } else {
            newBuilder.cacheControl(CacheControl.FORCE_NETWORK);
        }
        newBuilder.header("User-Agent", "xxxxxxxx");
        compressedRequest = newBuilder.build();
        Response response = chain.proceed(compressedRequest);
        if (NetCheckUtil.isNetworkConnected(context)) {
            int maxAge = 60 * 60; // 有网络时 设置缓存超时时间一小时
            response = response.newBuilder()
                    .removeHeader("Pragma")
                    //清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                    .header("Cache-Control", "public, max-age=" + maxAge)//设置缓存超时时间
                    .build();
        } else {
            int maxStale = 60 * 5; // 无网络时，设置超时为5分钟
            response = response.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    //设置缓存策略，及超时策略
                    .build();
        }
        return response;
    }

    private RequestBody gzip(final RequestBody body) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return body.contentType();
            }

            @Override
            public long contentLength() {
                return -1; // 未知数据大小
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                BufferedSink gzipSink = Okio.buffer(new GzipSink(sink));
                body.writeTo(gzipSink);
                gzipSink.close();
            }
        };
    }
}
*/