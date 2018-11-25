package com.wmlive.hhvideo.heihei.personal.presenter;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.beans.personal.UserHomeResponse;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.networklib.observer.DCNetObserver;

/**
 * Created by lsq on 1/12/2018.11:19 AM
 *
 * @author lsq
 * @describe 添加描述
 */

public class BaseUserInfoPresenter<V extends BaseUserInfoPresenter.IBaseUserInfoView> extends BasePresenter<V> {

    public BaseUserInfoPresenter(V view) {
        super(view);
    }

    public void getPersonalInfo(long userId) {
        executeRequest(HttpConstant.TYPE_PERSONAL_HOME, getHttpApi().getUserHome(InitCatchData.userUserHome(), userId))
                .subscribe(new DCNetObserver<UserHomeResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, UserHomeResponse response) {
                        if (null != viewCallback) {
                            viewCallback.onGetUserInfoOk(response.getUser_info());
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (null != viewCallback) {
                            viewCallback.onGetUserInfoFail(message);
                        }
                    }
                });
    }


    public interface IBaseUserInfoView extends BaseView {
        void onGetUserInfoOk(UserInfo userInfo);

        void onGetUserInfoFail(String message);
    }
}
