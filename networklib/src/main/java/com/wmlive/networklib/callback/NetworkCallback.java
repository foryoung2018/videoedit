package com.wmlive.networklib.callback;


import com.wmlive.networklib.entity.ResponseEntity;

/**
 * 网络请求回调
 */
public interface NetworkCallback<T> {

    void onRequestStart(int requestCode, String message);

    /**
     * 请求成功回调
     */
    void onRequestDataReady(int requestCode, String message, ResponseEntity<T> response);

    /**
     * 请求失败回调
     */
    void onRequestDataError(int requestCode, String message);
}
