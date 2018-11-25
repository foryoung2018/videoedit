package com.wmlive.hhvideo.heihei.discovery.presenter;

import android.util.Log;

import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.discovery.Banner;
import com.wmlive.hhvideo.heihei.beans.discovery.BannerListBean;
import com.wmlive.hhvideo.heihei.beans.discovery.FocusBean;
import com.wmlive.hhvideo.heihei.beans.discovery.TopicTypeListBean;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.mainhome.presenter.FollowUserPresenter;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.networklib.observer.DCNetObserver;

import java.util.List;

/**
 * Created by lsq on 6/2/2017.
 * 发现页面的Presenter
 */

public class DiscoveryPresenter extends FollowUserPresenter<DiscoveryPresenter.IDiscoveryView> {

    //偏移
    private int offset = 0;

    public DiscoveryPresenter(IDiscoveryView view) {
        super(view);
    }


    //获取Banner的数据
    public void getBanner() {
        executeRequest(HttpConstant.TYPE_DISCOVERY_BANNER, getHttpApi().banner(InitCatchData.socialGetBanner()))
                .subscribe(new DCNetObserver<BannerListBean>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, BannerListBean response) {
                        if (viewCallback != null) {
                            viewCallback.onBannerOk(response.banners);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(HttpConstant.TYPE_DISCOVERY_BANNER, message);
                    }
                });
    }

    //获取Topic列表
    public void getTopicList(final boolean isRefresh) {
        executeRequest(HttpConstant.TYPE_DISCOVERY_TOPIC_HOME, getHttpApi().topicList(InitCatchData.topicListTopic(), isRefresh ? 0 : offset))
                .subscribe(new DCNetObserver<TopicTypeListBean>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, TopicTypeListBean response) {
                        if (viewCallback != null) {
                            offset = response.getOffset();
                            boolean hasRecommend = !CollectionUtil.isEmpty(response.day_recommend);
                            boolean hasFocus = !CollectionUtil.isEmpty(response.discover_focus);
                            boolean hasTextTopic = !CollectionUtil.isEmpty(response.text_topic);
                            if (isRefresh) {
                                if (!CollectionUtil.isEmpty(response.getTopic_list())) {
                                    response.getTopic_list().get(0).isFirst = true;
                                    if(hasTextTopic){
                                        response.getTopic_list().get(0).text_topic = response.text_topic;
                                    }
                                }
                                if(hasFocus){
                                    TopicTypeListBean.TopicListBean listBean = new TopicTypeListBean.TopicListBean();
                                    listBean.discover_focus = response.discover_focus;
                                    response.getTopic_list().add(0,listBean);
                                }
                                if (hasRecommend) {
                                    TopicTypeListBean.TopicListBean listBean = new TopicTypeListBean.TopicListBean();
//                                int size = response.getRecommend_users().size();
//                                listBean.recommend_users = response.getRecommend_users().subList(0, size > 20 ? 20 : size);
                                    listBean.day_recommend = response.day_recommend;
                                    response.getTopic_list().add(0, listBean);
                                }


                            }
                            viewCallback.onGetTopicListOk(isRefresh, response.getTopic_list(),
                                    response.isHas_more(), hasRecommend,hasFocus, response.peripheral_url,response.discover_btn);
                        }
                        Log.i("===yang","TopicTypeListBean " + response.discover_btn);
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(isRefresh ? HttpConstant.TYPE_DISCOVERY_TOPIC_HOME : (HttpConstant.TYPE_DISCOVERY_TOPIC_HOME + 1), message);
                    }
                });
    }

    public interface IDiscoveryView extends FollowUserPresenter.IFollowUserView {
        void onBannerOk(List<Banner> bannerList);

        void onGetTopicListOk(boolean isRefresh, List<TopicTypeListBean.TopicListBean> list,
                              boolean hasMore, boolean hasFollows,boolean hasFocus, String aroundUrl,String bgUrl);

    }
}
