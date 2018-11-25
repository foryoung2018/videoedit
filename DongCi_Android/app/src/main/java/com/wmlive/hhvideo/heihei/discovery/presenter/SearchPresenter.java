package com.wmlive.hhvideo.heihei.discovery.presenter;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.search.SearchMusicBean;
import com.wmlive.hhvideo.heihei.beans.search.SearchResponse;
import com.wmlive.hhvideo.heihei.beans.search.SearchTopicBean;
import com.wmlive.hhvideo.heihei.beans.search.SearchUserBean;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.discovery.activity.SearchActivity;
import com.wmlive.networklib.observer.DCNetObserver;

import java.util.List;

/**
 * Created by lsq on 5/31/2017.
 * 搜索页面的Presenter
 */

public class SearchPresenter<T extends SearchPresenter.ISearchView> extends BasePresenter<T> {

    public SearchPresenter(SearchPresenter.ISearchView view) {
        super((T) view);
    }

    private int currentOffset = 0;//偏移

    //搜索
    public void search(final boolean isRefresh, String keyword, final String type) {
        executeRequest(HttpConstant.TYPE_DISCOVERY_SEARCH, getHttpApi().searchSearch(InitCatchData.searchSearch(), keyword, type, (currentOffset = isRefresh ? 0 : currentOffset)))
                .subscribe(new DCNetObserver<SearchResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, SearchResponse response) {
                        if (viewCallback != null) {
                            currentOffset = response.getOffset();
                            switch (type) {
                                case SearchActivity.TYPE_USER:
                                    viewCallback.onQueryUserOk(isRefresh, type, response.getUser_list(), response.isHas_more());
                                    break;
                                case SearchActivity.TYPE_TOPIC:
                                    viewCallback.onQueryTopicOk(isRefresh, type, response.getTopic_list(), response.isHas_more());

                                    break;
                                case SearchActivity.TYPE_MUSIC:
                                    viewCallback.onQueryMusicOk(isRefresh, type, response.getMusic_list(), response.isHas_more());
                                    break;
                                default:
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(isRefresh ? HttpConstant.TYPE_DISCOVERY_SEARCH : (HttpConstant.TYPE_DISCOVERY_SEARCH + 1), message);
                    }
                });
    }

    public interface ISearchView extends BaseView {

        void onQueryUserOk(boolean isRefresh, String type, List<SearchUserBean> bean, boolean hasMore);

        void onQueryTopicOk(boolean isRefresh, String type, List<SearchTopicBean> bean, boolean hasMore);

        void onQueryMusicOk(boolean isRefresh, String type, List<SearchMusicBean> bean, boolean hasMore);
    }
}
