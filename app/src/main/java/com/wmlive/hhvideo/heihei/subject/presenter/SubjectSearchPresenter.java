package com.wmlive.hhvideo.heihei.subject.presenter;

import android.util.Log;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.search.SearchResponse;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.subject.View.SubjectSearchView;
import com.wmlive.networklib.observer.DCNetObserver;

/**
 * 话题的搜索
 * Created by kangzhen on 2017/6/2.
 */

public class SubjectSearchPresenter extends BasePresenter<SubjectSearchView> {

    public SubjectSearchPresenter(SubjectSearchView view) {
        super(view);
    }

    /**
     * 根据关键字获取话题
     *
     * @param keyword
     * @param offset
     */
    public void getSubjectByKeyword(final String keyword, int offset, final boolean isMore) {
        executeRequest(HttpConstant.TYPE_SEARCH_TOPI_CODE, getHttpApi().searchSearch(InitCatchData.searchSearch(), keyword, "topic", offset))
                .subscribe(new DCNetObserver<SearchResponse>() {

                    @Override
                    public void onRequestDataReady(int requestCode, String message, SearchResponse response) {
                        if (isMore) {
                            //更多
                            Log.e("im_edit", "----more----onRequestDataReady----------:");
                            viewCallback.searchTopicMoreSuccess(response, keyword);
                        } else {
                            Log.e("im_edit", "--------onRequestDataReady----------:");
                            viewCallback.searchTopicSuccess(response, keyword);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        viewCallback.onRequestDataError(requestCode, message);
                        Log.e("im_edit", "---------error---------:");
                    }
                });
    }
}
