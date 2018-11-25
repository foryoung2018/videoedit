package com.wmlive.hhvideo.heihei.mainhome.presenter;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.personal.DecibelEntity;
import com.wmlive.hhvideo.heihei.beans.personal.DecibelListResponse;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.networklib.observer.DCNetObserver;

import java.util.List;

/**
 * Created by lsq on 5/2/2018 - 4:04 PM
 * 类描述：
 */
public class DecibelListPresenter extends BasePresenter<DecibelListPresenter.IDecibelListView> {

    private int pointListOffset;

    public DecibelListPresenter(IDecibelListView view) {
        super(view);
    }

    //获取分贝列表
    public void getDecibelList(final boolean isRefresh, long videoId) {
        executeRequest(HttpConstant.TYPE_DECIBEL_LIST, getHttpApi().getVideoDecibelList(InitCatchData.opusOpusPointList(), videoId, pointListOffset = (isRefresh ? 0 : pointListOffset)))
                .subscribe(new DCNetObserver<DecibelListResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, DecibelListResponse response) {
                        if (null != viewCallback) {
                            pointListOffset = response.getOffset();
                            viewCallback.onDecibelListOk(isRefresh, response.data, response.isHas_more(), response.statistic);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(isRefresh ? HttpConstant.TYPE_DECIBEL_LIST : HttpConstant.TYPE_DECIBEL_LIST + 1, message);
                    }
                });
    }



    public interface IDecibelListView extends BaseView {
        void onDecibelListOk(boolean isRefresh, List<DecibelEntity> list, boolean hasMore, DecibelListResponse.StatisticEntity statistic);
    }
}
