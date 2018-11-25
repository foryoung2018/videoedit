package com.wmlive.hhvideo.heihei.mainhome.presenter;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.discovery.DiscMessageEntity;
import com.wmlive.hhvideo.heihei.beans.discovery.DiscMessageResponse;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.preferences.SPUtils;
import com.wmlive.networklib.observer.DCNetObserver;

import java.util.List;

/**
 * Created by lsq on 5/28/2018 - 3:03 PM
 * 类描述：
 */
public class DiscoverMessagePresenter extends BasePresenter<DiscoverMessagePresenter.IDiscoverMessageView> {
    private int offset;

    public DiscoverMessagePresenter(IDiscoverMessageView view) {
        super(view);
    }

    public void getMessageList(boolean isRefresh) {
        executeRequest(HttpConstant.TYPE_DISC_MESSAGE_LIST, getHttpApi().getListSystemNews(InitCatchData.getListSystemNews(), offset = isRefresh ? 0 : offset))
                .subscribe(new DCNetObserver<DiscMessageResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, DiscMessageResponse response) {
                        if (viewCallback != null) {
                            offset = response.getOffset();
                            viewCallback.onDiscoverMessageListOk(isRefresh, response.isHas_more(), response.news);
                            if (!CollectionUtil.isEmpty(response.news)) {
                                DiscMessageEntity entity = response.news.get(0);
                                if (entity != null) {
                                    SPUtils.putLong(DCApplication.getDCApp(), SPUtils.KEY_LATEST_DISCOVERY_NEWS_TIME, entity.create_time);
                                }
                            }
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (viewCallback != null) {
                            viewCallback.onDiscoverMessageListFail(isRefresh, message);
                        }
                    }
                });

    }

    public interface IDiscoverMessageView extends BaseView {
        void onDiscoverMessageListOk(boolean isRefresh, boolean hasMore, List<DiscMessageEntity> news);

        void onDiscoverMessageListFail(boolean isRefresh, String message);
    }
}
