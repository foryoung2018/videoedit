package com.wmlive.hhvideo.heihei.mainhome.presenter;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.main.DiscoverMessageBean;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.preferences.SPUtils;
import com.wmlive.networklib.observer.DCNetObserver;

/**
 * Created by lsq on 7/12/2017.
 * HomeFragment的presenter
 */

public class HomePresenter extends BasePresenter<HomePresenter.IHomeView> {

    public HomePresenter(IHomeView view) {
        super(view);
    }

    //获取发现的消息提醒
    public void getDiscoveryMessage(long latestTime) {
        KLog.i("====请求Discovery的消息");
        long newsTime = SPUtils.getLong(DCApplication.getDCApp(), SPUtils.KEY_LATEST_DISCOVERY_NEWS_TIME, 0);
        executeRequest(HttpConstant.TYPE_DISCOVER_MESSAGE, getHttpApi().getDiscoveryMessage(InitCatchData.newTopicCheck(), latestTime, newsTime))
                .subscribe(new DCNetObserver<DiscoverMessageBean>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, DiscoverMessageBean response) {
                        if (response.getData() != null) {
                            SPUtils.putLong(DCApplication.getDCApp(), SPUtils.KEY_LATEST_GET_DISCOVERY_MSG, response.getData().getLatest_time());
                            if (viewCallback != null) {
                                viewCallback.onGetDiscoveryMessage(response.getData().isHas_new(), response.getData().getExpires(), response.getData().getLatest_news_count());
                            }
                        } else {
                            if (viewCallback != null) {//如果服务器数据错误，则默认60秒后再去请求
                                viewCallback.onGetDiscoveryMessage(false, 60, 0);
                            }
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (viewCallback != null) {//如果请求数据错误，则默认60秒后再去请求
                            viewCallback.onGetDiscoveryMessage(false, 60, 0);
                        }
                    }
                });
    }

    public interface IHomeView extends BaseView {
        void onGetDiscoveryMessage(boolean hasNew, int nextTime, int unreadCount);
    }
}
