package com.wmlive.networklib.callback;


import com.wmlive.networklib.entity.BaseResponse;

import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 5/1/2017.
 * 专用于RxJava结合Retrofit的网络回调
 */

public interface RxNetworkCallback<T extends BaseResponse> {
    /**
     * 请求开始前
     *
     * @param showLoadingView 是否显示正在加载的dialog，默认是true
     * @param requestCode     请求码，默认是0
     * @param message         需要显示的信息
     * @param disposable
     */
    void onRequestStart(boolean showLoadingView, int requestCode, String message, Disposable disposable);

    /**
     * 请求成功回调
     *
     * @param requestCode
     * @param message
     * @param response
     */
    void onRequestDataReady(int requestCode, String message, T response);

    /**
     * 请求失败回调
     *
     * @param requestCode 接口请求码
     * @param serverCode  服务器返回的错误码
     * @param message     服务器返回的错误信息
     */
    void onRequestDataError(int requestCode, int serverCode, String message);
}
