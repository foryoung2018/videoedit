package com.wmlive.hhvideo.heihei.splash.presenter;

import android.os.Build;
import android.text.TextUtils;

import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.splash.CheckDeviceResponse;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.beans.splash.InitUrlResponse;
import com.wmlive.hhvideo.heihei.mainhome.util.SharedPreferencesUtils;
import com.wmlive.hhvideo.heihei.splash.view.SplashView;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.networklib.observer.DCNetObserver;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiPredicate;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by vhawk on 2017/5/22.
 * Modify by lsq
 */

public class SplashPresenter<T extends SplashView> extends BasePresenter<T> {

    private Disposable disposable;

    public SplashPresenter(T view) {
        super(view);
    }

    public void init() {
        KLog.i("=====请求init");
        Map<String, String> options = new HashMap<>();
        options.put("app_name", "hhvideo");
        options.put("os_version", Build.VERSION.RELEASE);
        options.put("os_platform", "android");
        options.put("device_id", GlobalParams.StaticVariable.sUniqueDeviceId);
        options.put("app_version", String.valueOf(GlobalParams.Config.APP_VERSION));
        String initUrl;

        if (GlobalParams.Config.IS_DEBUG) {//这个用于Debug版本切换正式和测试环境
            if (GlobalParams.StaticVariable.sReleaseEnvironment) {
                initUrl = GlobalParams.Config.APP_RELEASE_URL;
            } else {
                initUrl = GlobalParams.Config.APP_DEBUG_URL;
            }
        } else {
            initUrl = GlobalParams.Config.APP_RELEASE_URL;
        }
        executeRequest(HttpConstant.TYPE_INIT, getHttpApi().init(initUrl + "init", options))  //注意：这个地方init接口是写死的!!!服务器修改一定要同步
                .retry(new BiPredicate<Integer, Throwable>() {//重试3次，总共请求4次
                    @Override
                    public boolean test(Integer integer, Throwable throwable) throws Exception {
                        KLog.i("=====开始第" + integer + "次尝试");
                        return integer < 4;
                    }
                })
                .subscribe(new DCNetObserver<InitUrlResponse>() {  //使用Observable的方式请求
                    @Override
                    public void onRequestStart(boolean showLoadingView, int requestCode, String message, Disposable disposable) {
                        super.onRequestStart(showLoadingView, requestCode, message, disposable);
                        KLog.i("SplashPresenter 开始请求");
                    }

                    @Override
                    public void onRequestDataReady(int requestCode, final String message, final InitUrlResponse response) {
                        KLog.i("0.0.0.0--》SplashPre-onRequestDataReady");
                        boolean isLoadOk = false;
                        KLog.i("=====InitUrlResponse:" + (response == null ? "null" : response.toString()));
                        if (response != null && !TextUtils.isEmpty(response.getVersion())) {//这个地方没做版本的升级判断
                            InitCatchData.setInitUrl(response);
                            GlobalParams.StaticVariable.sApiServiceVersion = response.getVersion();
                            isLoadOk = true;
                        }
                        if (viewCallback != null) {
                            viewCallback.initApiSucceed();
                            if (disposable != null && !disposable.isDisposed()) {
                                disposable.dispose();
                            }
                            disposable = Observable.just(isLoadOk)
                                    .subscribeOn(Schedulers.io())
                                    .map(new Function<Boolean, Boolean>() {
                                        @Override
                                        public Boolean apply(Boolean b) throws Exception {
                                            if (b) {
                                                KLog.i("保存到本地");
                                                InitCatchData.saveInitData(response);
                                                KLog.i("保存到本地完成");
                                            }
                                            return b;
                                        }
                                    })
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<Boolean>() {
                                        @Override
                                        public void accept(Boolean b) throws Exception {
                                            if (viewCallback != null) {
                                                viewCallback.onInitComplete();
                                                KLog.i("0.0.0.0--》SplashPre-onInitComplete");
                                            }
                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            if (viewCallback != null) {
                                                viewCallback.initApiError(message);
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        KLog.i("0.0.0.0--》SplashPre-onRequestDataError");
                        if (null != viewCallback) {
                            KLog.i("请求失败");
                            viewCallback.initApiError(message);
                        }
                    }
                });
    }

    public void checkDeviceId(String oldId, String newId) {
        String initUrl = InitCatchData.getReCheckDeviceInfo();
        KLog.d("recheckDeviceId", "initUrl====" + initUrl);
        if (TextUtils.isEmpty(initUrl)) {
            if (GlobalParams.Config.IS_DEBUG) {//这个用于Debug版本切换正式和测试环境
                if (GlobalParams.StaticVariable.sReleaseEnvironment) {
                    initUrl = GlobalParams.Config.APP_RELEASE_URL;
                } else {
                    initUrl = GlobalParams.Config.APP_DEBUG_URL;
                }
            } else {
                initUrl = GlobalParams.Config.APP_RELEASE_URL;
            }
            initUrl = initUrl + GlobalParams.Config.CHECK_DEVICE_ID_URL;
        }
        GlobalParams.StaticVariable.sUniqueDeviceId = newId;
        executeRequest(HttpConstant.TYPE_CHECK_DEVICE_ID, getHttpApi().checkDeviceId(initUrl, newId, oldId))
                .subscribe(new DCNetObserver<CheckDeviceResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, CheckDeviceResponse response) {
                        if (viewCallback != null) {
                            viewCallback.onCheckDeviceIdOk(response);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (viewCallback != null) {
                            viewCallback.initApiError(message);
                        }
                    }
                });
    }

    @Override
    public void destroy() {
        if (null != disposable && !disposable.isDisposed()) {
            disposable.dispose();
        }
        super.destroy();
    }
}
