package com.wmlive.hhvideo.heihei.mainhome.presenter;

import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.discovery.FollowUserResponseEntity;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.networklib.observer.DCNetObserver;
import com.wmlive.networklib.util.EventHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lsq on 9/21/2017.
 */

public class FollowUserPresenter<T extends FollowUserPresenter.IFollowUserView> extends BasePresenter<T> {

    public FollowUserPresenter(T view) {
        super(view);
    }

    public void follow(final int position, final long userId, boolean isFollowed) {
        follow(false, position, userId, -1, isFollowed, -1);
    }

    public void follow(boolean isRecommenad, final int position, final long userId, final long videoId, boolean isFollowed, long messageId) {
        KLog.d("关注", "isFollowed==" + isFollowed);
        Map<String, String> map = new HashMap<>(2);
        map.put("user_id", String.valueOf(userId));
        map.put("flag", isFollowed ? "0" : "1");
        executeRequest(HttpConstant.TYPE_USER_FOLLOW, getHttpApi().followAnchor(InitCatchData.userFollowUser(), map))
                .subscribe(new DCNetObserver<FollowUserResponseEntity>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, FollowUserResponseEntity response) {
                        if (null != viewCallback) {
                            viewCallback.onFollowUserOk(isRecommenad, false, position, userId, videoId, response.is_follow);
                        }
                        if (response != null) {
                            response.userId = userId;
                            EventHelper.post(GlobalParams.EventType.TYPE_FOLLOW_OK, response);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (serverCode != GlobalParams.EventType.TYPE_RELOGIN) {
                            onRequestError(HttpConstant.TYPE_USER_FOLLOW, message);
                        }
                    }
                });
    }

    public void recommendFollow(final int position, final long userId, boolean isFollowed) {
        follow(true, position, userId, 0, isFollowed, 0);
    }

    public void followAll(String userIds) {
        executeRequest(HttpConstant.TYPE_USER_FOLLOW_ALL, getHttpApi().followAll(InitCatchData.getBatchFollowUser(), userIds))
                .subscribe(new DCNetObserver<FollowUserResponseEntity>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, FollowUserResponseEntity response) {
                        if (null != viewCallback) {
                            viewCallback.onFollowUserOk(true, true, 0, 0, 0, response.is_follow);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (serverCode != GlobalParams.EventType.TYPE_RELOGIN) {
                            onRequestError(HttpConstant.TYPE_USER_FOLLOW_ALL, message);
                        }
                    }
                });
    }

    public interface IFollowUserView extends BaseView {
        void onFollowUserOk(boolean isRecommendFollow, boolean isFollowAll, int position, long userId, long videoId, boolean isFollowed);
    }

}
