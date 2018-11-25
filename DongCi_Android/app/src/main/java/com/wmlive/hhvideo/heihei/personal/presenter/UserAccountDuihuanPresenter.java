package com.wmlive.hhvideo.heihei.personal.presenter;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.personal.UserAccountDuihuanResponse;
import com.wmlive.hhvideo.heihei.beans.personal.UserAccountResponse;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.personal.view.IUserAccountDuihuanView;
import com.wmlive.networklib.observer.DCNetObserver;

/**
 * Created by XueFei on 2017/7/26.
 * <p>
 * 兑换
 */

public class UserAccountDuihuanPresenter extends BasePresenter<IUserAccountDuihuanView> {
    public UserAccountDuihuanPresenter(IUserAccountDuihuanView view) {
        super(view);
    }

    /**
     * 获取兑换列表
     */
    public void getDuihuanList() {
        executeRequest(HttpConstant.TYPE_USER_ACCOUNT_DUIHUAN, getHttpApi().getAccountDuihuanList(InitCatchData.getP2gpackageList()))
                .subscribe(new DCNetObserver<UserAccountDuihuanResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, UserAccountDuihuanResponse response) {
                        if (null != viewCallback) {
                            viewCallback.handleDuihuanListSucceed(response);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (null != viewCallback) {
                            viewCallback.handleDuihuanListFailure(message);
                        }
                    }
                });
    }

    public void duiHuanJinbi(int id) {
        executeRequest(HttpConstant.TYPE_USER_ACCOUNT_DUIHUAN_JINBI, getHttpApi().getDuiHuanJinbi(InitCatchData.getDuihuanJinbi(), id))
                .subscribe(new DCNetObserver<UserAccountResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, UserAccountResponse response) {
                        if (response != null && response.getUser_gold_account() != null) {
                            AccountUtil.setUserGoldAccount(response.getUser_gold_account());
                        }
                        if (null != viewCallback) {
                            viewCallback.handleDuihuanJinbiSucceed(response);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (null != viewCallback) {
                            viewCallback.handleDuihuanJinbiFailure(id,message);
                        }
                    }
                });
    }
}
