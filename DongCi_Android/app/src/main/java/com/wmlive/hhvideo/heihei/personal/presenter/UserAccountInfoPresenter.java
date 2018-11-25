package com.wmlive.hhvideo.heihei.personal.presenter;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.personal.UserAccountResponse;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.personal.view.IUserAccountInfoView;
import com.wmlive.networklib.observer.DCNetObserver;

/**
 * Created by XueFei on 2017/7/26.
 * <p>
 * 账户 信息
 */

public class UserAccountInfoPresenter extends BasePresenter<IUserAccountInfoView> {
    public UserAccountInfoPresenter(IUserAccountInfoView view) {
        super(view);
    }

    /**
     * 获取账户 信息
     */
    public void getAccountInfo() {
        executeRequest(HttpConstant.TYPE_USER_ACCOUNT_INFO, getHttpApi().getAccountInfo(InitCatchData.getGoldAccount()))
                .subscribe(new DCNetObserver<UserAccountResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, UserAccountResponse response) {
                        if (response.getUser_gold_account() != null) {
                            AccountUtil.setUserGoldAccount(response.getUser_gold_account());
                        }
                        if (null != viewCallback) {
                            viewCallback.handleInfoSucceed(response);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (null != viewCallback) {
                            viewCallback.handleInfoFailure(message);
                        }
                    }
                });
    }
}
