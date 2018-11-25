package com.wmlive.networklib.okhttp;

/**
 * Created by lsq on 12/2/2016.
 */

public class SignInterceptor {} /**implements Interceptor {
    private RetrofitWrap mYWRetrofit;

    public SignInterceptor(RetrofitWrap retrofit) {
        mYWRetrofit = retrofit;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (request.body() != null) {
            Request.Builder requestBuilder = request.newBuilder();
            FormBody.Builder newFormBody = new FormBody.Builder();
            Map<String, String> paramsMap = null;
            if (request.body() instanceof FormBody) {
                paramsMap = new HashMap<>();
                FormBody oldFormBody = (FormBody) request.body();
                for (int i = 0; i < oldFormBody.size(); i++) {
                    KLog.e("------request-key:" + oldFormBody.encodedName(i) + "-->value:" + oldFormBody.encodedValue(i));
                    if (!TextUtils.isEmpty(oldFormBody.encodedValue(i))) {//过滤掉空value
                        paramsMap.put(oldFormBody.encodedName(i), URLDecoder.decode(oldFormBody.encodedValue(i), "utf-8"));
                    }
                }
            } else if (request.body() instanceof MultipartBody) {
                // TODO: 12/8/2016  用到了再说！
            } else {

            }
            for (Map.Entry<String, String> entry : sign(paramsMap).entrySet()) {
                newFormBody.add(entry.getKey(), entry.getValue());
            }
            requestBuilder.method(request.method(), newFormBody.build());
            return chain.proceed(requestBuilder.build());
        }
        return chain.proceed(request);
    }

    private Map<String, String> sign(Map<String, String> map) {
        Map<String, String> newMap;
        newMap = (map != null) ? map : (new HashMap<String, String>());
        JSONObject params = new JSONObject(newMap);
        newMap.clear();
        newMap.put("appId", "1");
        newMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
        if (mYWRetrofit != null && !TextUtils.isEmpty(mYWRetrofit.token)) {
            newMap.put("token", mYWRetrofit.token);
        }
        newMap.put("params", params.toString().replaceAll("\\\\", ""));
        String signValue = DESUtil.encode(DESUtil.DES_KEY, (new JSONObject(newMap)).toString());
        newMap.clear();
        newMap.put("sign", signValue);
        return newMap;
    }
}
 */
