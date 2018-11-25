package com.wmlive.networklib;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;

import com.wmlive.networklib.callback.RxNetworkCallback;
import com.wmlive.networklib.entity.BaseResponse;
import com.wmlive.networklib.util.NetLog;
import com.wmlive.networklib.util.NetStringUtil;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Created by Administrator on 4/30/2017.
 * 同时支持RxJava2和原生Call请求方式的Retrofit
 */

public class RxJava2Retrofit extends RetrofitWrap {
    private Map<String, SparseArray<Disposable>> disposableMap; //保存每个请求的Disposable

    public RxJava2Retrofit(Context context) {
        super(context);
    }


    /**
     * 获取请求的api接口，支持RxJava
     *
     * @param requestCode 请求的类型区分code
     * @param callback    如果是null，则在subscribe中去处理数据：如果不是null，则可以在BasePresenter的子类中重写NetworkCallback的方法去处理数据
     * @param observable  从请求接口Api中获取Observable
     * @param <T>
     * @return
     */
    @Override
    public <T extends BaseResponse> Observable<Response<T>> getObservable(String tag, int requestCode, Observable<Response<T>> observable, RxNetworkCallback<T> callback) {
        return observable.compose(composeScheduler(tag, requestCode, callback));
    }

    private <T extends BaseResponse> ObservableTransformer<Response<T>, Response<T>> composeScheduler(final String tag, final int requestCode, final RxNetworkCallback<T> callback) {
        return new ObservableTransformer<Response<T>, Response<T>>() {
            @Override
            public ObservableSource<Response<T>> apply(Observable<Response<T>> upstream) {
                return upstream
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(Disposable disposable) throws Exception {
                                addDispose(tag, requestCode, disposable);
                                if (null != callback) {
                                    NetLog.i("RxJava开始请求数据");
                                    callback.onRequestStart(true, requestCode, context.getString(R.string.hintRequestLoading), disposable);
                                }
                            }
                        })
                        .subscribeOn(AndroidSchedulers.mainThread()) //这里保证了doOnSubscribe运行在MainThread
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(new Consumer<Response<T>>() {
                            @Override
                            public void accept(Response<T> response) throws Exception {
                                if (null != callback) {
                                    if (response.isSuccessful()) {
                                        NetLog.i("RxJava请求数据成功");
                                        T entity = response.body();
                                        entity.setReqCode(requestCode);
                                        if (entity.getError_code() == 0) {
                                            callback.onRequestDataReady(requestCode, entity.getError_msg(), entity);
                                        } else {
                                            callback.onRequestDataError(requestCode, entity.getError_code(), entity.getError_msg());
                                        }
                                    } else {
                                        callback.onRequestDataError(requestCode, response.code(), NetStringUtil.getErrString(context, response.code()));
                                    }
                                    cancelRequest(tag, requestCode);
                                }
                            }
                        })
                        .doOnError(new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (null != callback) {
                                    NetLog.i("RxJava请求数据失败");
                                    throwable.printStackTrace();
                                    callback.onRequestDataError(requestCode, -1, NetStringUtil.getErrString(context, throwable));
                                    cancelRequest(tag, requestCode);
                                }
                            }
                        });

            }
        };
    }


    /**
     * 添加一个Disposable
     *
     * @param tag         请求的tag
     * @param requestCode 请求的requestCode
     * @param disposable
     */
    private void addDispose(final String tag, final int requestCode, final Disposable disposable) {
        if (!TextUtils.isEmpty(tag)) {
            if (disposableMap == null) {
                disposableMap = new WeakHashMap<>();
            }
            if (disposableMap.get(tag) == null) {
                SparseArray<Disposable> sparseArray = new SparseArray<>(2);
                sparseArray.append(requestCode, disposable);
                disposableMap.put(tag, sparseArray);
            } else {
                Disposable d = disposableMap.get(tag).get(requestCode);
                if (d != null && !d.isDisposed()) {
                    d.dispose();
                }
                disposableMap.get(tag).put(requestCode, disposable);
            }
        }
    }

    @Override
    public boolean cancelRequest(String tag, int requestCode) {
        if (!TextUtils.isEmpty(tag) && disposableMap != null) {
            Iterator<Map.Entry<String, SparseArray<Disposable>>> iterator = disposableMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, SparseArray<Disposable>> entry = iterator.next();
                if (entry.getKey().equals(tag)) {
                    SparseArray<Disposable> array = entry.getValue();
                    if (requestCode < 1) {  //如果requestCode是小于1，则取消tag对应的所有订阅
                        for (int i = 0; i < array.size(); i++) {
                            cancelObservable(array.keyAt(i), array);
                        }
                    } else {
                        cancelObservable(requestCode, array);
                    }
                    iterator.remove();
                    break;
                }
            }

//            if (disposableMap != null && disposableMap.get(tag) != null) {
//                SparseArray<Disposable> array = disposableMap.get(tag);
//                if (requestCode < 1) {  //如果requestCode是小于1，则取消tag对应的所有订阅
//                    for (int i = 0; i < array.size(); i++) {
//                        cancelObservable(array.keyAt(i), array);
//                    }
//                    return true;
//                } else {
//                    return cancelObservable(requestCode, array);
//                }
//            }
        }
        return true;
    }

    @Override
    public boolean cancelRequest(String tag) {
        return cancelRequest(tag, 0);
    }

    /**
     * 取消请求
     *
     * @param requestCode
     * @param array
     */
    private boolean cancelObservable(int requestCode, SparseArray<Disposable> array) {
        Disposable d = array.get(requestCode);
        if (null != d) {
            if (!d.isDisposed()) {
                d.dispose();
                NetLog.i("======dispose request：" + requestCode);
            }
            array.remove(requestCode);
            NetLog.i("======remove request code：" + requestCode);
            return true;
        }
        return false;
    }
}
