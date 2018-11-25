package com.wmlive.hhvideo.heihei.mainhome.presenter;

import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.main.Comment;
import com.wmlive.hhvideo.heihei.beans.main.CommentDataCount;
import com.wmlive.hhvideo.heihei.beans.main.CommentDeleteResponse;
import com.wmlive.hhvideo.heihei.beans.main.VideoCommentListResponse;
import com.wmlive.hhvideo.heihei.beans.opus.OpusLikeCommentResponse;
import com.wmlive.hhvideo.heihei.beans.personal.DecibelEntity;
import com.wmlive.hhvideo.heihei.beans.personal.DecibelListResponse;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.networklib.observer.DCNetObserver;

import java.util.List;

/**
 * Created by lsq on 6/19/2017.
 * 评论列表页面
 */

public class CommentListPresenter extends RecommendPresenter<CommentListPresenter.ICommentListView> {
    private int commentOffset;//评论列表的offset
    private int pointListOffset;//评论列表的offset

    public CommentListPresenter(CommentListPresenter.ICommentListView view) {
        super(view);
    }

    //获取评论的列表
    public void getCommentList(final boolean isRefresh, long videoId) {
        executeRequest(HttpConstant.TYPE_COMMENT_LIST, getHttpApi().fetchCommentList(InitCatchData.opusListOpusComment(), videoId, commentOffset = (isRefresh ? 0 : commentOffset)))
                .subscribe(new DCNetObserver<VideoCommentListResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, VideoCommentListResponse response) {
                        if (null != viewCallback) {
                            commentOffset = response.getOffset();
                            viewCallback.onCommentListOk(isRefresh, response.getComments(), response.isHas_more());
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(isRefresh ? HttpConstant.TYPE_COMMENT_LIST : HttpConstant.TYPE_COMMENT_LIST + 1, message);
                    }
                });
    }

    //获取分贝列表
    public void getDecibelList(final boolean isRefresh, long videoId) {
        executeRequest(HttpConstant.TYPE_DECIBEL_LIST, getHttpApi().getVideoDecibelList(InitCatchData.opusOpusPointList(), videoId, pointListOffset = (isRefresh ? 0 : pointListOffset)))
                .subscribe(new DCNetObserver<DecibelListResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, DecibelListResponse response) {
                        if (null != viewCallback) {
                            pointListOffset = response.getOffset();
                            viewCallback.onDecibelListOk(isRefresh, response.data, response.isHas_more(), response.statistic);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(isRefresh ? HttpConstant.TYPE_DECIBEL_LIST : HttpConstant.TYPE_DECIBEL_LIST + 1, message);
                    }
                });
    }

    //删除评论
    public void deleteComment(final int position, final long commentId, long opusId) {
        executeRequest(HttpConstant.TYPE_COMMENT_DELETE, getHttpApi().deleteComment(InitCatchData.opusDeleteComment(), commentId, opusId))
                .subscribe(new DCNetObserver<CommentDeleteResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, CommentDeleteResponse response) {
                        if (viewCallback != null) {
                            viewCallback.onDeleteCommentOk(position, commentId, response.getData_count());
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(HttpConstant.TYPE_COMMENT_DELETE, message);
                    }
                });
    }

    /**
     * 评论 点赞
     */
    public void clickLike(final int position, long comment_id, final int is_cancel) {
        executeRequest(HttpConstant.TYPE_COMMENT_CLICK_STARS, getHttpApi().likeComment(InitCatchData.opusLikeComment(), comment_id, is_cancel))
                .subscribe(new DCNetObserver<OpusLikeCommentResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, OpusLikeCommentResponse response) {
                        if (viewCallback != null) {
                            viewCallback.onLikeOK(position, is_cancel != 1, response);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(HttpConstant.TYPE_COMMENT_CLICK_STARS, message);
                    }
                });
    }

    public interface ICommentListView extends RecommendPresenter.IRecommendView {
        void onDecibelListOk(boolean isRefresh, List<DecibelEntity> list, boolean hasMore, DecibelListResponse.StatisticEntity statistic);

        void onCommentListOk(boolean isRefresh, List<Comment> list, boolean hasMore);

        void onDeleteCommentOk(int position, long commentId, CommentDataCount countBean);

        void onLikeOK(int position, boolean isLike, OpusLikeCommentResponse response);
    }
}
