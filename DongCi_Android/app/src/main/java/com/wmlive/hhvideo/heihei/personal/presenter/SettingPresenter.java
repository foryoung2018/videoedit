package com.wmlive.hhvideo.heihei.personal.presenter;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.personal.view.ISettingView;
import com.wmlive.networklib.entity.BaseResponse;
import com.wmlive.networklib.observer.DCNetObserver;

/**
 * Created by XueFei on 2017/6/5.
 * <p>
 * 设置
 */

public class SettingPresenter extends BasePresenter<ISettingView> {
    public SettingPresenter(ISettingView view) {
        super(view);
    }

    /**
     * 退出登录
     */
    public void loginOut() {
        executeRequest(HttpConstant.TYPE_PERSONAL_SIGNOUT, getHttpApi().logout(InitCatchData.userSignOut(), AccountUtil.getToken()))
                .subscribe(new DCNetObserver<BaseResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, BaseResponse response) {
                        if (null != viewCallback) {
                            viewCallback.handleLogoutSucceed();
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (null != viewCallback) {
                            viewCallback.handlerLogoutFailure(message);
                        }
                    }
                });
    }
}
