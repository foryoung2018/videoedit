package com.wmlive.hhvideo.heihei.discovery.presenter;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.discovery.AddFavoriteBean;
import com.wmlive.hhvideo.heihei.beans.discovery.MusicInfoBean;
import com.wmlive.hhvideo.heihei.beans.discovery.TopicInfoBean;
import com.wmlive.hhvideo.heihei.beans.main.RecommendResponse;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.networklib.observer.DCNetObserver;

import java.util.List;

/**
 * Created by lsq on 6/5/2017.
 * 话题页面的presenter
 */

public class TopicPresenter extends BasePresenter<TopicPresenter.ITopicView> {
    private int offset = 0;//偏移

    public TopicPresenter(ITopicView view) {
        super(view);
    }

    //获取话题列表内容
    public void getTopicList(final boolean isRefresh, long topicId) {
        executeRequest(HttpConstant.TYPE_DISCOVERY_TOPIC_LIST, getHttpApi().topicOpusList(InitCatchData.topicListOpusByTopic(), topicId, offset = isRefresh ? 0 : offset))
                .subscribe(new DCNetObserver<RecommendResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, RecommendResponse response) {
                        if (viewCallback != null) {
                            offset = response.getOffset();
                            viewCallback.onTopicInfoOk(response.topic_info);
                            viewCallback.onTopicListOk(isRefresh, response.topic_opus, response.isHas_more());
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(isRefresh ? HttpConstant.TYPE_DISCOVERY_TOPIC_LIST : (HttpConstant.TYPE_DISCOVERY_TOPIC_LIST + 1), message);
                    }
                });
    }

    //获取音乐列表内容
    public void getMusicList(final boolean isRefresh, long musicId) {
        executeRequest(HttpConstant.TYPE_DISCOVERY_MUSIC_LIST, getHttpApi().musicOpusList(InitCatchData.musicListOpusByMusic(), musicId, offset = isRefresh ? 0 : offset))
                .subscribe(new DCNetObserver<RecommendResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, RecommendResponse response) {
                        if (viewCallback != null) {
                            offset = response.getOffset();
                            viewCallback.onMusicInfoOk(response.music_info);
                            viewCallback.onMusicListOk(isRefresh, response.music_opus, response.isHas_more());
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(isRefresh ? HttpConstant.TYPE_DISCOVERY_MUSIC_LIST : (HttpConstant.TYPE_DISCOVERY_MUSIC_LIST + 1), message);
                    }
                });
    }

    //添加到收藏
    public void addFavorite(final int position, long musicId) {
        executeRequest(HttpConstant.TYPE_DISCOVERY_ADD_FAVORITE, getHttpApi().addFavorite(InitCatchData.musicFavMusic(), musicId))
                .subscribe(new DCNetObserver<AddFavoriteBean>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, AddFavoriteBean response) {
                        if (viewCallback != null) {
                            viewCallback.onAddFavoriteResult(position, response.is_favorite());
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(HttpConstant.TYPE_DISCOVERY_ADD_FAVORITE, message);
                    }
                });
    }

    public interface ITopicView extends BaseView {
        void onTopicInfoOk(TopicInfoBean bean);

        void onTopicListOk(boolean isRefresh, List<ShortVideoItem> list, boolean hasMore);

        void onMusicInfoOk(MusicInfoBean bean);

        void onMusicListOk(boolean isRefresh, List<ShortVideoItem> list, boolean hasMore);

        void onAddFavoriteResult(int position, boolean ok);
    }
}
