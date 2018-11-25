package com.wmlive.hhvideo.heihei.personal.presenter;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.personal.ListFollowerFansResponse;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.personal.view.IFansView;
import com.wmlive.networklib.observer.DCNetObserver;

/**
 * Created by XueFei on 2017/6/1.
 * <p>
 * 粉丝
 */

public class FansPresenter extends BasePresenter<IFansView> {
    private int offset;

    public FansPresenter(IFansView view) {
        super(view);
    }

    /**
     * 获取粉丝列表
     */
    public void getFansList(boolean isRefresh, long userId) {
        executeRequest(HttpConstant.TYPE_PERSONAL_FANS_LIST, getHttpApi().getListFans(InitCatchData.userListFans(), userId, offset = isRefresh ? 0 : offset))
                .subscribe(new DCNetObserver<ListFollowerFansResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, ListFollowerFansResponse response) {
                        if (null != viewCallback) {
                            offset = response.getOffset();
                            viewCallback.onFansListOk(isRefresh, response, response.isHas_more());
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (null != viewCallback) {
                            viewCallback.onFansListFail(isRefresh, message);
                        }
                    }
                });
    }

}
