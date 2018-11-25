package com.wmlive.hhvideo.heihei.mainhome.presenter;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.discovery.Banner;
import com.wmlive.hhvideo.heihei.beans.discovery.TopicInfoBean;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.beans.main.RecommendResponse;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoLoveResponse;
import com.wmlive.hhvideo.heihei.beans.personal.ReportType;
import com.wmlive.hhvideo.heihei.beans.personal.ReportTypeResponse;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.networklib.entity.BaseResponse;
import com.wmlive.networklib.observer.DCNetObserver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lsq on 6/19/2017.
 * RecommendNew页面的Presenter
 */

public class RecommendPresenter<T extends RecommendPresenter.IRecommendView> extends BasePresenter<T> {

    private int videoOffset;//视频列表的offset

    public RecommendPresenter(T view) {
        super(view);
    }

    public int getVideoOffset() {
        return videoOffset;
    }

    public void setVideoOffset(int offset) {
        videoOffset = offset;
    }

    //获取推荐视频列表
    public void getRecommendVideoList(final boolean isRefresh) {
        executeRequest(HttpConstant.TYPE_RECOMMEND_VIDEO_LIST, getHttpApi().fetchRecommendVideo(InitCatchData.opusListOpusByRecommendV2(), videoOffset = isRefresh ? 0 : videoOffset))
                .subscribe(new DCNetObserver<RecommendResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, RecommendResponse response) {
                        if (viewCallback != null) {
                            videoOffset = response.getOffset();
                            viewCallback.onVideoListOk(isRefresh, response.recommend_opus_list, response.recommend_banners, response.recommend_users, response.isHas_more());
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(isRefresh ? HttpConstant.TYPE_RECOMMEND_VIDEO_LIST : (HttpConstant.TYPE_RECOMMEND_VIDEO_LIST + 1), message);
                    }
                });
    }

    //关注列表
    public void getFollowList(final boolean isRefresh) {
        executeRequest(HttpConstant.TYPE_FOLLOW_VIDEO_LIST, getHttpApi().fetchAttentionVideo(InitCatchData.opusListOpusByFollow(), videoOffset = isRefresh ? 0 : videoOffset))
                .subscribe(new DCNetObserver<RecommendResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, RecommendResponse response) {
                        if (viewCallback != null) {
                            videoOffset = response.getOffset();
                            viewCallback.onVideoListOk(isRefresh, response.opus_list, response.recommend_banners, response.recommend_users, response.isHas_more());
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(isRefresh ? HttpConstant.TYPE_FOLLOW_VIDEO_LIST : (HttpConstant.TYPE_FOLLOW_VIDEO_LIST + 1), message);
                    }
                });
    }


    //音乐列表
    public void getMusicList(final boolean isRefresh, long musicId) {
        executeRequest(HttpConstant.TYPE_DISCOVERY_MUSIC_LIST, getHttpApi().musicOpusList(InitCatchData.musicListOpusByMusic(), musicId, videoOffset = isRefresh ? 0 : videoOffset))
                .subscribe(new DCNetObserver<RecommendResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, RecommendResponse response) {
                        if (viewCallback != null) {
                            videoOffset = response.getOffset();
                            viewCallback.onVideoListOk(isRefresh, response.music_opus, response.recommend_banners, response.recommend_users, response.isHas_more());
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(isRefresh ? HttpConstant.TYPE_DISCOVERY_MUSIC_LIST : (HttpConstant.TYPE_DISCOVERY_MUSIC_LIST + 1), message);
                    }
                });
    }

    //话题列表
    public void getTopicList(final boolean isRefresh, long topicId) {
        executeRequest(HttpConstant.TYPE_DISCOVERY_TOPIC_LIST, getHttpApi().topicOpusList(InitCatchData.topicListOpusByTopic(), topicId, videoOffset = isRefresh ? 0 : videoOffset))
                .subscribe(new DCNetObserver<RecommendResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, RecommendResponse response) {
                        if (viewCallback != null) {
                            videoOffset = response.getOffset();
                            viewCallback.onVideoListOk(isRefresh, response.topic_opus, response.recommend_banners, response.recommend_users, response.isHas_more());
                            viewCallback.onTopicInfoOk(response.topic_info);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(isRefresh ? HttpConstant.TYPE_DISCOVERY_TOPIC_LIST : (HttpConstant.TYPE_DISCOVERY_TOPIC_LIST + 1), message);
                    }
                });
    }

    //话题列表
    public void getExplosionList(final boolean isRefresh) {
        executeRequest(HttpConstant.TYPE_EXPLOSION_VIDEO, getHttpApi().explosionVideo(InitCatchData.opusListOpusByTime(), videoOffset = isRefresh ? 0 : videoOffset))
                .subscribe(new DCNetObserver<RecommendResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, RecommendResponse response) {
                        if (viewCallback != null) {
                            videoOffset = response.getOffset();
                            viewCallback.onVideoListOk(isRefresh, response.time_opus_list, response.recommend_banners, response.recommend_users, response.isHas_more());
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(isRefresh ? HttpConstant.TYPE_EXPLOSION_VIDEO : (HttpConstant.TYPE_EXPLOSION_VIDEO + 1), message);
                    }
                });
    }

    //点赞
    public void likeVideo(final int position, final long videoId, boolean isLike, boolean isFlyLike) {
        executeRequest(HttpConstant.TYPE_VIDEO_LOVE, getHttpApi().loveVideo(InitCatchData.opusLikeOpus(), videoId))
                .subscribe(new DCNetObserver<ShortVideoLoveResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, ShortVideoLoveResponse response) {
                        if (null != viewCallback) {
                            viewCallback.onLikeOk(videoId, position, response);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (null != viewCallback) {
                            if (serverCode == 30005) {//视频被删除了
                                viewCallback.onVideoError(position, videoId, message);
                            }
                            viewCallback.onLikeFail(videoId, position, isFlyLike);
                        }
                    }
                });
    }

    //获取举报列表
    public void getReportType() {
        executeRequest(HttpConstant.TYPE_REPORT_TYPE_LIST, getHttpApi().fetchReportType(InitCatchData.opusListReportType()))
                .subscribe(new DCNetObserver<ReportTypeResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, ReportTypeResponse response) {
                        if (viewCallback != null) {
                            viewCallback.onReportListOk(response.getReport_type_list());
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(HttpConstant.TYPE_REPORT_TYPE_LIST, message);
                    }
                });
    }

    //举报作品
    public void reportWorks(final int position, final long videoId, int reportId) {
        executeRequest(HttpConstant.TYPE_REPORT,
                getHttpApi().reportWorks(InitCatchData.opusReport(), videoId, reportId))
                .subscribe(new DCNetObserver<BaseResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, BaseResponse response) {
                        if (viewCallback != null) {
                            viewCallback.onReportOk();
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (serverCode == 30005) {//视频被删除了
                            if (null != viewCallback) {
                                viewCallback.onVideoError(position, videoId, message);
                            }
                        } else {
                            onRequestError(HttpConstant.TYPE_REPORT, message);
                        }
                    }
                });
    }

    //删除视频
    public void deleteVideo(final int position, final long shortVideoId) {
        executeRequest(HttpConstant.TYPE_VIDEO_DELETE
                , getHttpApi().deleteVideo(InitCatchData.opusDeleteOpus(), shortVideoId))
                .subscribe(new DCNetObserver<BaseResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, BaseResponse response) {
                        if (viewCallback != null) {
                            viewCallback.onDeleteVideoOk(position, shortVideoId);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(HttpConstant.TYPE_VIDEO_DELETE, message);
                    }
                });
    }

    public interface IRecommendView extends BaseView {
        void onVideoListOk(boolean isRefresh, List<ShortVideoItem> list, List<Banner> bannerList, List<UserInfo> recommendUsers, boolean hasMore);

        void onVideoError(int position, long videoId, String message);

        void onReportListOk(List<ReportType> list);

        void onTopicInfoOk(TopicInfoBean bean);

        void onLikeOk(long videoId, int position, ShortVideoLoveResponse bean);

        void onLikeFail(long videoId, int position, boolean isFlyLike);

        void onReportOk();

        void onDeleteVideoOk(int position, long videoId);

    }
}
