package com.wmlive.hhvideo.heihei.discovery.presenter;

import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.discovery.AddFavoriteBean;
import com.wmlive.hhvideo.heihei.beans.discovery.MusicCategoryBean;
import com.wmlive.hhvideo.heihei.beans.discovery.MusicResultBean;
import com.wmlive.hhvideo.heihei.beans.search.SearchMusicBean;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.networklib.observer.DCNetObserver;

import java.util.List;

/**
 * Created by lsq on 6/5/2017.
 * 音乐分类的Presenter
 */

public class SearchMusicPresenter extends SearchPresenter<SearchMusicPresenter.ISearchMusicView> {
    private int offset = 0;//偏移

    public SearchMusicPresenter(ISearchMusicView view) {
        super(view);
    }

    //获取音乐分类
    public void getMusicCategory() {
        executeRequest(HttpConstant.TYPE_DISCOVERY_GET_CATEGORY, getHttpApi().musicCategory(InitCatchData.musicGetCategory()))
                .subscribe(new DCNetObserver<MusicCategoryBean>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, MusicCategoryBean response) {
                        if (viewCallback != null) {
                            viewCallback.onGetCategoryOk(response.getMusic_cat());
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(HttpConstant.TYPE_DISCOVERY_GET_CATEGORY, message);
                    }
                });
    }

    //按照分类搜素
    public void searchMusic(final boolean isRefresh, final long catId) {
        executeRequest(HttpConstant.TYPE_DISCOVERY_SEARCH_BY_CAT, getHttpApi().searchMusicByCat(InitCatchData.musicGetMusicByCat(), catId, offset = isRefresh ? 0 : offset))
                .subscribe(new DCNetObserver<MusicResultBean>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, MusicResultBean response) {
                        if (viewCallback != null) {
                            offset = response.getOffset();
                            viewCallback.onSearchMusicOk(isRefresh, response.getCat_music(), response.isHas_more());
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(isRefresh ? HttpConstant.TYPE_DISCOVERY_SEARCH_BY_CAT : (HttpConstant.TYPE_DISCOVERY_SEARCH_BY_CAT + 1), message);
                    }
                });
    }

    //我的收藏
    public void getCollect(final boolean isRefresh) {
        executeRequest(HttpConstant.TYPE_DISCOVERY_COLLECT_LIST, getHttpApi().myCollect(InitCatchData.musicMyFavMusic(), offset = isRefresh ? 0 : offset))
                .subscribe(new DCNetObserver<MusicResultBean>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, MusicResultBean response) {
                        if (viewCallback != null) {
                            offset = response.getOffset();
                            viewCallback.onSearchCollectOk(isRefresh, response.getFavorites_music(), response.isHas_more());
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(isRefresh ? HttpConstant.TYPE_DISCOVERY_COLLECT_LIST : (HttpConstant.TYPE_DISCOVERY_COLLECT_LIST + 1), message);
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

    public interface ISearchMusicView extends SearchPresenter.ISearchView {
        void onGetCategoryOk(List<MusicCategoryBean.MusicCatBean> list);

        void onSearchMusicOk(boolean isRefresh, List<SearchMusicBean> list, boolean hasMore);

        void onSearchCollectOk(boolean isRefresh, List<SearchMusicBean> list, boolean hasMore);

        void onAddFavoriteResult(int position, boolean ok);
    }
}
