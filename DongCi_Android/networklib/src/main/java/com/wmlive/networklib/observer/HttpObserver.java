package com.wmlive.networklib.observer;

import com.wmlive.networklib.callback.RxNetworkCallback;
import com.wmlive.networklib.entity.BaseResponse;
import com.wmlive.networklib.util.NetLog;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.Response;

/**
 * Created by Administrator on 5/1/2017.
 * 专用于RxJava结合Retrofit的事件订阅Observer
 * <p>
 * <p>
 * /**
 * 使用方法
 * presenter.checkUpdate(16002)
 * .subscribe(HttpObserver.buildDefault());
 * .subscribe(new HttpObserver<UpdateEntity>() {
 *
 * @Override public void onRequestDataReady(int requestCode, String message, ResponseEntity<UpdateEntity> response) {
 * KLog.i("-----" + response.toString());
 * }
 * @Override public void onRequestDataError(int requestCode, String message) {
 * <p>
 * }
 * });
 */

public abstract class HttpObserver<T extends BaseResponse> implements Observer<Response<T>>, RxNetworkCallback {


    @Override
    public void onSubscribe(Disposable d) {
        onRequestStart(true, 0, "正在请求数据...", d);
    }

    @Override
    public void onNext(Response<T> response) {
        if (response.isSuccessful()) {
            NetLog.i("RxJava请求数据成功");
            T entity = response.body();
            if (response.code() == 0) {
                onRequestDataReady(0, entity.getError_msg(), entity);
            } else {
                onRequestDataError(0, entity.getError_code(), entity.getError_msg());
            }
        }
    }

    @Override
    public void onError(Throwable t) {
        NetLog.i("RxJava请求数据失败");
        String result;
        if (t instanceof java.net.SocketTimeoutException) {
            result = "服务器超时";
        } else if (t instanceof IllegalArgumentException) {
            result = "请求参数错误";
        } else {
            result = "网络错误";
        }
        t.printStackTrace();
        onRequestDataError(0, -1, result);
    }

    @Override
    public void onComplete() {
        NetLog.i("RxJava请求完成");
    }

    @Override
    public void onRequestStart(boolean showLoadingView, int requestCode, String message, Disposable disposable) {
        //  这里写加载前的动画
    }

    /**
     * 创建一个默认的HttpObserver，单纯用于事件订阅，不处理事件
     *
     * @param <T>
     * @return
     */
    public static <T extends BaseResponse> HttpObserver buildDefault() {
        return new HttpObserver<T>() {

            @Override
            public void onRequestDataReady(int requestCode, String message, BaseResponse response) {

            }

            @Override
            public void onRequestDataError(int requestCode, int serverCode, String message) {

            }
        };
    }
}
