package com.wmlive.hhvideo.heihei.personal.presenter;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.personal.UserAccountChargeCreateOrderResponse;
import com.wmlive.hhvideo.heihei.beans.personal.UserAccountChargeResponse;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.personal.view.IUserAccountChargeView;
import com.wmlive.networklib.observer.DCNetObserver;

/**
 * Created by XueFei on 2017/7/26.
 * <p>
 * 充值
 */

public class UserAccountChargePresenter extends BasePresenter<IUserAccountChargeView> {
    public UserAccountChargePresenter(IUserAccountChargeView view) {
        super(view);
    }

    /**
     * 获取充值列表
     */
    public void getChargeList() {
        executeRequest(HttpConstant.TYPE_USER_ACCOUNT_CHARGE, getHttpApi().getAccountChargeList(InitCatchData.getPaypackageList()))
                .subscribe(new DCNetObserver<UserAccountChargeResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, UserAccountChargeResponse response) {
                        if (null != viewCallback) {
                            viewCallback.handleChargeListSucceed(response);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (null != viewCallback) {
                            viewCallback.handleChargeListFailure(message);
                        }
                    }
                });
    }

    /**
     * 创建订单
     */
    public void getCreateOrder(long id) {
        executeRequest(HttpConstant.TYPE_USER_ACCOUNT_ChARGE_CREATE_ORDER, getHttpApi().getCreateOrder(InitCatchData.getCcreateOrder(), id))
                .subscribe(new DCNetObserver<UserAccountChargeCreateOrderResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, UserAccountChargeCreateOrderResponse response) {
                        if (null != viewCallback) {
                            viewCallback.handleChargeCreateOrderSucceed(response);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (null != viewCallback) {
                            viewCallback.handleChargeCreateOrderFailure(id, message);
                        }
                    }
                });
    }
}
