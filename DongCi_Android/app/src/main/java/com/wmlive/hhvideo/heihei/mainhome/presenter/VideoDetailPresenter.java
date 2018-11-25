package com.wmlive.hhvideo.heihei.mainhome.presenter;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoInfoResponse;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.networklib.observer.DCNetObserver;

/**
 * Created by lsq on 12/6/2017.5:56 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class VideoDetailPresenter extends BasePresenter<VideoDetailPresenter.IVideoDetailView> {


    public VideoDetailPresenter(IVideoDetailView view) {
        super(view);
    }

    //获取视频的详情，同时获取礼物列表
    public void getVideoDetail(final int position, final long id, int barrage) {
        executeRequest(HttpConstant.TYPE_VIDEO_INFO, getHttpApi().fetchVideoInfo(InitCatchData.opusGetOpus(), id, barrage))
                .subscribe(new DCNetObserver<ShortVideoInfoResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, ShortVideoInfoResponse response) {
                        if (null != viewCallback) {
                            viewCallback.onVideoDetailOk(id, position, response);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (serverCode == 30005) {//视频被删除了
                            if (null != viewCallback) {
                                viewCallback.onVideoError(position, id, message);
                            }
                        } else {
                            onRequestError(HttpConstant.TYPE_VIDEO_INFO, message);
                        }
                    }
                });
    }

    public interface IVideoDetailView extends BaseView {
        void onVideoDetailOk(long videoId, int position, ShortVideoInfoResponse videoInfoBean);

        void onVideoError(int position, long videoId, String message);
    }
}
