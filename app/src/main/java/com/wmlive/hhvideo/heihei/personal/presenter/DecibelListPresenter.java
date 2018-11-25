package com.wmlive.hhvideo.heihei.personal.presenter;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.personal.DecibelListResponse;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.networklib.observer.DCNetObserver;

/**
 * Created by lsq on 1/10/2018.11:00 AM
 *
 * @author lsq
 * @describe 添加描述
 */

public class DecibelListPresenter extends BasePresenter<DecibelListPresenter.IDecibelListView> {
    private int offset = 0;

    public DecibelListPresenter(IDecibelListView view) {
        super(view);
    }

    public void getDecibelList(boolean isRefresh, long userId) {
        executeRequest(HttpConstant.TYPE_GET_DECIBEL_LIST, getHttpApi().getDecibelList(InitCatchData.getUserPointList(), userId, offset = isRefresh ? 0 : offset))
                .subscribe(new DCNetObserver<DecibelListResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, DecibelListResponse response) {
                        offset = response.getOffset();
                        if (viewCallback != null) {
                            viewCallback.onDecibelListOk(isRefresh, response);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (viewCallback != null) {
                            viewCallback.onDecibelListFail(message);
                        }
                    }
                });
    }

    public interface IDecibelListView extends BaseView {
        void onDecibelListOk(boolean isRefresh, DecibelListResponse response);

        void onDecibelListFail(String message);
    }
}
