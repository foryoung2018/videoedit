package com.wmlive.hhvideo.heihei.personal.presenter;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.search.SearchResponse;
import com.wmlive.hhvideo.heihei.beans.search.SearchUserBean;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.networklib.observer.DCNetObserver;

import java.util.List;

/**
 * Created by Administrator on 3/22/2018.
 */

public class BlockListPresenter extends BasePresenter<BlockListPresenter.IBlockListView> {

    private int offset = 0;

    public BlockListPresenter(IBlockListView view) {
        super(view);
    }

    public void getBlockList(boolean isRefresh) {
        executeRequest(HttpConstant.TYPE_BLOCK_LIST + (isRefresh ? 0 : 1), getHttpApi().getBlockList(InitCatchData.getUserBlacklist(), offset = isRefresh ? 0 : offset))
                .subscribe(new DCNetObserver<SearchResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, SearchResponse response) {
                        if (viewCallback != null) {
                            viewCallback.onBlockListOk(isRefresh, response.getUsers(), response.isHas_more());
                        }

                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(HttpConstant.TYPE_BLOCK_LIST + (isRefresh ? 0 : 1), message);
                    }
                });

    }

    public interface IBlockListView extends BaseView {
        void onBlockListOk(boolean isRefresh, List<SearchUserBean> userInfoList, boolean hasMore);
    }
}
