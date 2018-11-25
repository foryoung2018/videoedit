package com.wmlive.networklib;

import android.content.Context;
import android.text.TextUtils;

import com.wmlive.networklib.callback.NetworkCallback;
import com.wmlive.networklib.entity.ResponseEntity;
import com.wmlive.networklib.util.NetStringUtil;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * 不支持RxJava2的Retrofit，使用Retrofit的Call方式进行请求
 */
public class RetrofitWrap extends RetrofitBase {
    private Map<String, Map<Integer, Call>> requestMap = new ConcurrentHashMap<>();

    public RetrofitWrap(Context context) {
        super(context);
    }

    /**
     * 异步请求
     *
     * @param tag              请求tag，默认使用base中的mRequestTag
     * @param requestCode      请求的code，用于区分请求
     * @param requestCall      请求的Call
     * @param responseListener 请求回调
     * @param <T>              服务器返回数据解析后的实体类
     */
    @Override
    public <T> void asyncRequest(final String tag, final int requestCode, final Call<T> requestCall, final NetworkCallback<T> responseListener) {
        if (context == null || responseListener == null) {
            return;
        }

        Call<T> call;
        if (requestCall.isExecuted()) {
            call = requestCall.clone();
        } else {
            call = requestCall;
        }
        addCall(tag, requestCode, call);
        responseListener.onRequestStart(requestCode, context.getString(R.string.hintRequestLoading));
//        if (checkNetwork(tag, requestCode, responseListener) && checkTime(tag, requestCode, responseListener)) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (!requestCall.isCanceled()) {
                    if (response.isSuccessful()) {
                        if (response.code() == 200 && null != response.body()) {
                            ResponseEntity<T> result = new ResponseEntity<>();
                            result.code = response.code();
                            result.data = response.body();
                            result.reqCode = requestCode;
                            responseListener.onRequestDataReady(requestCode, result.message, result);
                        } else {
                            String msg;
                            if (!TextUtils.isEmpty(response.message())) {
                                msg = response.message();
                            } else {
                                msg = NetStringUtil.getErrString(context, NetStringUtil.ERR_CODE_201);
                            }
                            responseListener.onRequestDataError(requestCode, msg);
                        }
                    } else {
                        responseListener.onRequestDataError(requestCode, NetStringUtil.getErrString(context, response.code()));
                    }
                }
                cancelRequest(tag, requestCode);
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                if (!requestCall.isCanceled()) {
                    responseListener.onRequestDataError(requestCode, NetStringUtil.getErrString(context, t));
                }
                cancelRequest(tag, requestCode);
            }
        });
//        }
    }

    /**
     * 同步请求
     *
     * @param tag              请求tag，默认使用base中的mRequestTag
     * @param requestCode      请求的code，用于区分请求
     * @param requestCall      请求的Call
     * @param responseListener 请求回调
     * @param <T>              服务器返回数据解析后的实体类
     */
    @Override
    public <T> void syncRequest(final String tag, final int requestCode, final Call<T> requestCall, final NetworkCallback<T> responseListener) {
        if (responseListener == null) {
            return;
        }
        Call<T> call;
        try {
            if (requestCall.isExecuted()) {
                call = requestCall.clone();
            } else {
                call = requestCall;
            }
//            if (checkNetwork(tag, requestCode, responseListener) && checkTime(tag, requestCode, responseListener)) {
            Response<T> response = call.execute();
            addCall(tag, requestCode, call);
            responseListener.onRequestStart(requestCode, context.getString(R.string.hintRequestLoading));
            if (!requestCall.isCanceled()) {
                if (response.isSuccessful()) {
                    if (response.code() == 200 && null != response.body()) {
                        ResponseEntity<T> result = new ResponseEntity<>();
                        result.code = response.code();
                        result.data = response.body();
                        result.reqCode = requestCode;
                        responseListener.onRequestDataReady(requestCode, result.message, result);
                    } else {
                        String msg;
                        if (!TextUtils.isEmpty(response.message())) {
                            msg = response.message();
                        } else {
                            msg = NetStringUtil.getErrString(context, NetStringUtil.ERR_CODE_201);
                        }
                        responseListener.onRequestDataError(requestCode, msg);
                    }
                }
            } else {
                if (!requestCall.isCanceled()) {
                    responseListener.onRequestDataError(requestCode, NetStringUtil.getErrString(context, response.code()));
                }
            }
//            }
        } catch (IOException e) {
            if (!requestCall.isCanceled()) {
                responseListener.onRequestDataError(requestCode, NetStringUtil.getErrString(context, e));
            }
        } finally {
            cancelRequest(tag, requestCode);
        }
    }

    /**
     * 添加call到Map
     */
    private void addCall(String tag, Integer code, Call call) {
        if (tag == null) {
            return;
        }
        if (requestMap.get(tag) == null) {
            Map<Integer, Call> map = new ConcurrentHashMap<>();
            map.put(code, call);
            requestMap.put(tag, map);
        } else {
            requestMap.get(tag).put(code, call);
        }
    }

    @Override
    public boolean cancelRequest(String tag, int requestCode) {
        if (tag == null) {
            return false;
        }
        Map<Integer, Call> map = requestMap.get(tag);
        if (map == null) {
            return false;
        }
        if (requestCode < 1) {
            Iterator iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                Integer key = (Integer) iterator.next();
                Call call = map.get(key);
                if (call == null) {
                    continue;
                }
                call.cancel();
            }
            requestMap.remove(tag);
            return false;
        } else {
            if (map.containsKey(requestCode)) {
                Call call = map.get(requestCode);
                if (call != null) {
                    call.cancel();
                }
                map.remove(requestCode);
            }
            if (map.size() == 0) {
                requestMap.remove(tag);
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean cancelRequest(String tag) {
        return cancelRequest(tag, 0);
    }


}
