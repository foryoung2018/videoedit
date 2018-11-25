package com.wmlive.hhvideo.heihei.mainhome.presenter;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoLoveResponse;
import com.wmlive.hhvideo.heihei.beans.personal.ReportType;
import com.wmlive.hhvideo.heihei.beans.personal.ReportTypeResponse;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.networklib.entity.BaseResponse;
import com.wmlive.networklib.observer.DCNetObserver;

import java.util.List;

/**
 * Created by hsing on 2018/4/12.
 */
@Deprecated
public class OpusDetailPresenter<T extends OpusDetailPresenter.IOpusDetailView> extends BasePresenter<T> {

    public OpusDetailPresenter(T view) {
        super(view);
    }

    //点赞
    public void likeVideo(final int position, final long videoId, boolean isFlyLike) {
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



    public interface IOpusDetailView extends BaseView {

        void onLikeOk(long videoId, int position, ShortVideoLoveResponse bean);

        void onLikeFail(long videoId, int position, boolean isFlyLike);

        void onReportOk();

        void onReportListOk(List<ReportType> list);

        void onDeleteVideoOk(int position, long videoId);

        void onVideoError(int position, long videoId, String message);


    }
}