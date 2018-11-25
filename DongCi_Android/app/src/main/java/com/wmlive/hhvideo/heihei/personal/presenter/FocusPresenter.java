package com.wmlive.hhvideo.heihei.personal.presenter;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.personal.ListFollowerFansResponse;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.personal.view.IFocusView;
import com.wmlive.networklib.observer.DCNetObserver;

/**
 * Created by XueFei on 2017/6/1.
 * <p>
 * 关注
 */

public class FocusPresenter extends BasePresenter<IFocusView> {
    private int offset;

    public FocusPresenter(IFocusView view) {
        super(view);
    }

    /**
     * 获取关注列表
     */
    public void getFocusList(boolean isRefresh, long userId) {
        executeRequest(HttpConstant.TYPE_PERSONAL_FOCUS_LIST, getHttpApi().getListFollower(InitCatchData.userListFollower(), userId, offset = isRefresh ? 0 : offset))
                .subscribe(new DCNetObserver<ListFollowerFansResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, ListFollowerFansResponse response) {
                        if (null != viewCallback) {
                            offset = response.getOffset();
                            viewCallback.onFocusListOk(isRefresh, response, response.isHas_more());
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (null != viewCallback) {
                            viewCallback.onFocusFail(isRefresh, message);
                        }
                    }
                });
    }

}
