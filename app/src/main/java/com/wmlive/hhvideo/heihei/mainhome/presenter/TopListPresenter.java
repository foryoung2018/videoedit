package com.wmlive.hhvideo.heihei.mainhome.presenter;

import android.util.Log;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.heihei.beans.opus.TopListResponse;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.networklib.observer.DCNetObserver;

import java.util.List;

/**
 * Created by lsq on 5/28/2018 - 11:43 AM
 * 类描述：
 */
public class TopListPresenter extends BasePresenter<TopListPresenter.ITopListView> {

    private int offset;

    public TopListPresenter(ITopListView view) {
        super(view);
    }

    public void getTopList(boolean isRefresh) {
        executeRequest(HttpConstant.TYPE_TOP_LIST, getHttpApi().getOpusTopList(InitCatchData.getOpusTopList(), offset = isRefresh ? 0 : offset))
                .subscribe(new DCNetObserver<TopListResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, TopListResponse response) {
                        if (viewCallback != null) {
                            offset = response.getOffset();
                            viewCallback.onTopListOk(isRefresh, response.opus_list, response.isHas_more(), response.top_title);
                        }
                        Log.i("===yang","TopListResponse " + response.top_title);
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (viewCallback != null) {
                            viewCallback.onTopListFail(isRefresh, message);
                        }
                    }
                });
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public interface ITopListView extends BaseView {
        void onTopListOk(boolean isRefresh, List<ShortVideoItem> videoItemList, boolean hasMore,String tvDetail);

        void onTopListFail(boolean isRefresh, String message);
    }
}
