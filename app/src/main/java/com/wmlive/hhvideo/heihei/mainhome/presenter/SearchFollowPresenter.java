package com.wmlive.hhvideo.heihei.mainhome.presenter;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.search.SearchResponse;
import com.wmlive.hhvideo.heihei.beans.search.SearchUserBean;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.networklib.observer.DCNetObserver;

import java.util.List;

/**
 * Created by Administrator on 3/20/2018.
 */

public class SearchFollowPresenter extends BasePresenter<SearchFollowPresenter.ISearchFollowView> {
    private int offset;

    public SearchFollowPresenter(ISearchFollowView view) {
        super(view);
    }

    public void search(boolean isRefresh, String keyword) {
        executeRequest(HttpConstant.TYPE_IM_SEARCH_FOLLOW,
                getHttpApi().searchFollow(InitCatchData.getImFollowerSearch(), keyword, offset = isRefresh ? 0 : offset))
                .subscribe(new DCNetObserver<SearchResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, SearchResponse response) {
                        if (viewCallback != null) {
                            offset = response.getOffset();
                            viewCallback.onSearchOk(isRefresh, response.getUsers(), response.isHas_more());
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (viewCallback != null) {
                            viewCallback.onSearchFail(isRefresh, message);
                        }
                    }
                });
    }

    public interface ISearchFollowView extends BaseView {
        void onSearchOk(boolean isRefresh, List<SearchUserBean> bean, boolean hasMore);

        void onSearchFail(boolean isRefresh, String message);
    }
}
