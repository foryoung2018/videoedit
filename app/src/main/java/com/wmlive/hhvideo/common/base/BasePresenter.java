package com.wmlive.hhvideo.common.base;

import android.content.Context;
import android.text.TextUtils;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.network.ApiService;
import com.wmlive.hhvideo.common.network.DCRequest;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.CommonUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.networklib.callback.RxNetworkCallback;
import com.wmlive.networklib.entity.BaseResponse;
import com.wmlive.networklib.util.NetUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import retrofit2.Response;

/**
 * Created by vhawk on 2017/5/19.
 * Modify by lsq
 */

public abstract class BasePresenter<V extends BaseView> implements IBasePresenter, RxNetworkCallback {
    private List<String> requestTags = new ArrayList<>(4);
    protected V viewCallback;
    protected Context context;

    public BasePresenter(V view) {
        this.viewCallback = view;
        addRequestTag(view.getClass().getSimpleName());
    }

    @Override
    public void bindContext(Context context) {
        this.context = context;
        if (context != null) {
            addRequestTag(context.getClass().getSimpleName());
        }
    }

    /**
     * 执行一个网络请求
     * 此方法支持RxJava
     *
     * @param requestCode 请求的类型区分code
     * @param observable  从请求接口Api中获取Observable
     * @param <T>
     * @return
     */
    public <T extends BaseResponse> Observable<Response<T>> executeRequest(int requestCode, Observable<Response<T>> observable) {
        return executeRequest(addRequestTag(null), requestCode, observable, null);
    }

    /**
     * 执行一个网络请求
     * 此方法支持RxJava
     *
     * @param requestCode 请求的类型区分code
     * @param observable  从请求接口Api中获取Observable
     * @param <T>
     * @return
     */
    public <T extends BaseResponse> Observable<Response<T>> executeRequest(String tag, int requestCode, Observable<Response<T>> observable) {
        return executeRequest(addRequestTag(tag), requestCode, observable, null);
    }

    /**
     * 执行一个网络请求
     * 此方法支持RxJava
     *
     * @param requestCode 请求的类型区分code
     * @param observable  从请求接口Api中获取Observable
     * @param callback    如果是null，则在subscribe中去处理数据：如果不是null，则可以在BasePresenter的子类中重写NetworkCallback的方法去处理数据
     * @param <T>
     * @return
     */
    public <T extends BaseResponse> Observable<Response<T>> executeRequest(int requestCode, Observable<Response<T>> observable, RxNetworkCallback<T> callback) {
        return executeRequest(addRequestTag(null), requestCode, observable, callback);
    }

    /**
     * 执行一个网络请求
     * 此方法支持RxJava
     *
     * @param tag         请求的tag，如果传null，则使用{@link #requestTags}中的第一个元素
     * @param requestCode 请求的类型区分code
     * @param observable  从请求接口Api中获取Observable
     * @param callback    如果是null，则在subscribe中去处理数据：如果不是null，则可以在BasePresenter的子类中重写NetworkCallback的方法去处理数据
     * @param <T>
     * @return
     */
    public <T extends BaseResponse> Observable<Response<T>> executeRequest(String tag, int requestCode, Observable<Response<T>> observable, RxNetworkCallback<T> callback) {
        return DCRequest.getRetrofit().getObservable(addRequestTag(tag), requestCode, observable, callback);
    }


    /**
     * 添加网络请求的tag，用于取消请求之用
     *
     * @param tag
     * @return
     */
    private String addRequestTag(String tag) {
        if (TextUtils.isEmpty(tag)) {
            if (!CollectionUtil.isEmpty(requestTags)) {
                return requestTags.get(0);
            } else {
                tag = String.valueOf(CommonUtils.getRandom(100000, 999999));
                requestTags.add(tag);
                return tag;
            }
        } else {
            if (!requestTags.contains(tag)) {
                KLog.i("=====add new tag:" + tag);
                requestTags.add(tag);
            }
            return tag;
        }
    }

    /**
     * 获取一个网络请求的Call或者Observable
     *
     * @return HttpApi
     */
    public ApiService getHttpApi() {
        return DCRequest.getHttpApi();
    }

    /**
     * 取消页面所有的请求
     *
     * @param tag
     */
    public void unSubscribe(String... tag) {
        for (String s : tag) {
            DCRequest.getRetrofit().cancelRequest(s);
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void destroy() {
        unSubscribe((String[]) requestTags.toArray(new String[requestTags.size()]));  //取消该页面的所有请求
        if (viewCallback != null) {
            KLog.i("===========destroy:" + this.getClass().getSimpleName());
            viewCallback = null;
        }
    }

    @Override
    public void onRequestStart(boolean showLoadingView, int requestCode, String message, Disposable disposable) {

    }

    @Override
    public void onRequestDataReady(int requestCode, String message, BaseResponse response) {

    }

    @Override
    public void onRequestDataError(int requestCode, int serverCode, String message) {
        if (NetUtil.getNetworkState(DCApplication.getDCApp()) == 2) {
            ToastUtil.showToast("请检查网络设置");
        }
    }

    /**
     * 请求服务器数据出错
     */
    protected void onRequestError(int requestCode, String message) {
        if (viewCallback != null) {
            viewCallback.onRequestDataError(requestCode, message);
        }
    }

}
