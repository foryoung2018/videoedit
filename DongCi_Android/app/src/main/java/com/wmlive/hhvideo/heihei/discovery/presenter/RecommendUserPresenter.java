package com.wmlive.hhvideo.heihei.discovery.presenter;

import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.discovery.RecommendUserResponse;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.mainhome.presenter.FollowUserPresenter;
import com.wmlive.networklib.observer.DCNetObserver;

import java.util.List;

/**
 * Created by lsq on 9/20/2017.
 */

public class RecommendUserPresenter extends FollowUserPresenter<RecommendUserPresenter.IRecommendUserView> {
    private int offset = 0;

    public RecommendUserPresenter(IRecommendUserView view) {
        super(view);
    }

    public void getUserList(final boolean isRefresh) {
        executeRequest(HttpConstant.TYPE_GET_RECOMMEND_USER + (isRefresh ? 0 : 1), getHttpApi().getRecommendUserList(InitCatchData.getRecommendUsers(), offset = isRefresh ? 0 : offset))
                .subscribe(new DCNetObserver<RecommendUserResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, RecommendUserResponse response) {
                        if (viewCallback != null) {
                            offset = response.getOffset();
                            viewCallback.onGetListOk(isRefresh, response.recommend_users, response.isHas_more());
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(HttpConstant.TYPE_GET_RECOMMEND_USER + (isRefresh ? 0 : 1), message);
                    }
                });
    }


    public interface IRecommendUserView extends FollowUserPresenter.IFollowUserView {
        void onGetListOk(boolean isRefresh, List<UserInfo> list, boolean hasMore);
    }
}
