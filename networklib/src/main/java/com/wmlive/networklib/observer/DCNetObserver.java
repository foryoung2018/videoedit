package com.wmlive.networklib.observer;

import com.wmlive.networklib.callback.RxNetworkCallback;
import com.wmlive.networklib.entity.BaseResponse;
import com.wmlive.networklib.util.EventHelper;
import com.wmlive.networklib.util.NetLog;

import java.net.ConnectException;
import java.net.UnknownHostException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.Response;

/**
 * 网络接口回掉使用
 * 统一处理一部分全局错误吗
 * Created by vhawk on 2017/5/23.
 * Modify by lsq
 */
public abstract class DCNetObserver<T extends BaseResponse> implements Observer<Response<T>>, RxNetworkCallback<T> {
    private Disposable disposable;


    @Override
    public void onSubscribe(Disposable d) {
        disposable = d;
        onRequestStart(true, 0, "正在请求数据...", d);
    }

    @Override
    public void onNext(Response<T> response) {
        if (disposable != null && !disposable.isDisposed()) {
            if (response.isSuccessful()) {
                NetLog.i("RxJava请求数据成功");
                T entity = response.body();
                if (null != entity) {
                    //此处的requestCode参数没有参考价值，默认都是0
                    switch (entity.getError_code()) {
                        case 0:
                            onRequestDataReady(entity.getReqCode(), entity.getError_msg(), entity);
                            break;
                        case 30001:
//                            RxBus.getInstance().post(new LoginInvalidEvent());
                            NetLog.i("======30001");
                            //此处的requestCode参数没有参考价值，默认都是0
                            onRequestDataError(entity.getReqCode(), entity.getError_code(), entity.getError_msg());
                            EventHelper.post(30001, true);
                            break;
                        default:
                            //此处的requestCode参数没有参考价值，默认都是0
                            onRequestDataError(entity.getReqCode(), entity.getError_code(), entity.getError_msg());
                            break;
                    }
                } else {
                    onRequestDataError(0, response.code(), "服务器忙，请稍后再试" + response.code());
                }
            } else {
                onRequestDataError(0, response.code(), "服务器忙，请稍后再试" + response.code());
            }
            disposable.dispose();
        }
    }

    @Override
    public void onError(Throwable t) {
        if (disposable != null && !disposable.isDisposed()) {
            NetLog.i("RxJava请求数据失败:" + t.getMessage());
            String result;
            if (t instanceof java.net.SocketTimeoutException) {
                result = "网络不给力哦～";
            } else if (t instanceof IllegalArgumentException) {
                result = "请求参数错误";
            } else if (t instanceof UnknownHostException || t instanceof ConnectException) {
                result = "未知的主机地址，请检查url";
            } else {
                result = "网络不给力哦～";
            }
            t.printStackTrace();
            //此处的requestCode参数没有参考价值，默认都是0
            onRequestDataError(0, -1, result);
            disposable.dispose();
        }
    }

    @Override
    public void onComplete() {
        NetLog.i("RxJava请求完成");
    }

    @Override
    public void onRequestStart(boolean showLoadingView, int requestCode, String message, Disposable disposable) {
        // 这里写加载前的动画
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
