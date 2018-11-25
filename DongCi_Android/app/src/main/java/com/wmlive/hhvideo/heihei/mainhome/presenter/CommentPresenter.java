package com.wmlive.hhvideo.heihei.mainhome.presenter;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.main.VideoCommentResponse;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.networklib.observer.DCNetObserver;

/**
 * Created by lsq on 6/15/2017.
 * 评论的Presenter
 */

public class CommentPresenter extends BasePresenter<CommentPresenter.ICommentView> {

    private int offset;

    public CommentPresenter(ICommentView view) {
        super(view);
    }

    //评论
    public void comment(int position,final long videoId, String comment, String remindUserId, long replayUserId) {
        executeRequest(HttpConstant.TYPE_FETCH_COMMENT, getHttpApi()
                .commentVideo(InitCatchData.opusCommendOpus(), videoId, comment, remindUserId, replayUserId))
                .subscribe(new DCNetObserver<VideoCommentResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, VideoCommentResponse response) {
                        if (viewCallback != null) {
                            viewCallback.onCommentOk(position,response);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (serverCode == 30005 || serverCode == 30004) {//视频被删除了或评论失败
                            if (null != viewCallback) {
                                viewCallback.onCommentFailed(message);
                            }
                        } else {
                            onRequestError(HttpConstant.TYPE_FETCH_COMMENT, message);
                        }
                    }
                });
    }

    public interface ICommentView extends BaseView {
        void onCommentOk(int position,VideoCommentResponse bean);

        void onCommentFailed(String msg);
    }
}
