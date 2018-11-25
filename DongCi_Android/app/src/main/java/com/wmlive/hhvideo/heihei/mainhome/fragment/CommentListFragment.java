package com.wmlive.hhvideo.heihei.mainhome.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.BaseCompatActivity;
import com.wmlive.hhvideo.common.base.DcBaseFragment;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.discovery.Banner;
import com.wmlive.hhvideo.heihei.beans.discovery.TopicInfoBean;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.beans.main.Comment;
import com.wmlive.hhvideo.heihei.beans.main.CommentDataCount;
import com.wmlive.hhvideo.heihei.beans.main.RefreshCommentBean;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoLoveResponse;
import com.wmlive.hhvideo.heihei.beans.main.VideoCommentResponse;
import com.wmlive.hhvideo.heihei.beans.opus.OpusLikeCommentResponse;
import com.wmlive.hhvideo.heihei.beans.personal.DecibelEntity;
import com.wmlive.hhvideo.heihei.beans.personal.DecibelListResponse;
import com.wmlive.hhvideo.heihei.beans.personal.ReportType;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.mainhome.adapter.CommentListAdapter;
import com.wmlive.hhvideo.heihei.mainhome.presenter.CommentListPresenter;
import com.wmlive.hhvideo.heihei.mainhome.presenter.CommentPresenter;
import com.wmlive.hhvideo.heihei.mainhome.view.RefreshCommentListener;
import com.wmlive.hhvideo.heihei.personal.activity.UserHomeActivity;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.MyClickListener;
import com.wmlive.hhvideo.utils.PopupWindowUtils;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.widget.dialog.CommentDialog;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;
import com.wmlive.networklib.util.EventHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 5/2/2018 - 3:17 PM
 * 类描述：
 */
public class CommentListFragment extends DcBaseFragment implements
        RefreshRecyclerView.OnLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener,
        CommentListAdapter.OnCommentItemClickListener,
        CommentListPresenter.ICommentListView, CommentPresenter.ICommentView {
    @BindView(R.id.rvComment)
    RefreshRecyclerView rvComment;
    @BindView(R.id.tvCommentLabel)
    TextView tvCommentLabel;

    private static final String KEY_VIDEO_ID = "key_video_id";
    private static final String KEY_PAGE_ID = "key_page_id";
    private static final String KEY_VIDEO_POSITION = "key_video_position";
    private static final String KEY_COMMENT_COUNT = "key_comment_count";

    private CommentListAdapter commentListAdapter;
    private CommentListPresenter commentListPresenter;
    private CommentPresenter commentPresenter;
    private long videoId;
    private int pageId;
    private int videoPosition;
    private int commentCount;
    private PopupWindow popupWindow;
    private RefreshCommentListener refreshCommentListener;
    private CommentDialog commentDialog;


    public static CommentListFragment newInstance() {
        CommentListFragment commentListFragment = new CommentListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(LAZY_MODE, true);
        commentListFragment.setArguments(bundle);
        return commentListFragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_comment_list;
    }

    @Override
    public void onVisibleChange(int type, boolean visible) {
        super.onVisibleChange(type, visible);
        if (!visible) {
            if (commentDialog != null && commentDialog.isShowing()) {
                commentDialog.dismiss();
            }
        }
    }

    @Override
    protected void onSingleClick(View v) {
        switch (v.getId()) {
            case R.id.tvCommentLabel:
                if (AccountUtil.isLogin()) {
                    showCommentDialog(videoId, null, 0);
                } else {
                    showReLogin();
                }
            default:
                break;
        }
    }

    @Override
    protected void initData() {
        super.initData();
        commentListAdapter = new CommentListAdapter(new ArrayList<>(), rvComment);
        commentListAdapter.setCommentItemClickListener(this);
        rvComment.setLayoutManager(new LinearLayoutManager(getContext()));

        commentListAdapter.setShowImg(true);
        commentListAdapter.setEmptyStr(R.string.comment_null);
        rvComment.setOnLoadMoreListener(this);
        rvComment.setOnRefreshListener(this);
        tvCommentLabel.setOnClickListener(this);
        rvComment.setAdapter(commentListAdapter);
        commentListPresenter = new CommentListPresenter(this);
        commentPresenter = new CommentPresenter(this);
        tvCommentLabel.setVisibility(View.VISIBLE);
        addPresenter(commentListPresenter, commentPresenter);
    }

    @Override
    public void onRefresh() {
        commentListPresenter.getCommentList(true, videoId);
    }

    @Override
    public void onLoadMore() {
        commentListPresenter.getCommentList(false, videoId);
    }

    @Override
    public void onAvatarClick(long userId) {
        if (refreshCommentListener != null) {
            refreshCommentListener.onDismiss();
        }
        UserHomeActivity.startUserHomeActivity(getContext(), userId);
    }

    @Override
    public void onNameClick(int position, Comment comment) {
        if (AccountUtil.isLogin()) {
            if (null != comment && comment.getUser_id() > 0) {
                if (AccountUtil.getUserId() == comment.getUser_id()) {//自己的评论，进入删除动作
                    showDeletePop(position, comment);
                } else {//别人的评论，进行评论
                    showCommentDialog(videoId, comment.getUser() != null ? comment.getUser().getName() : null,
                            comment.getUser() != null ? comment.getUser().getId() : 0);
                }
            } else {
                ToastUtil.showToast(R.string.hintErrorDataDelayTry);
            }
        } else {
            showReLogin();
        }
    }

    private void showCommentDialog(final long videoId, String remindUserId, long replayUserId) {
        tvCommentLabel.setVisibility(View.GONE);
        commentDialog = new CommentDialog();
        String replayhint = replayUserId > 0 ? "回复:" + remindUserId : getResources().getString(R.string.stringLikeJustSay);
        commentDialog.showDialog((BaseCompatActivity) getContext(), tvCommentLabel.getText().toString().trim(), replayhint,
                new CommentDialog.CommentListener() {
                    @Override
                    public void onSendComment(String comment) {
                        commentPresenter.comment(videoPosition, videoId, comment, remindUserId, replayUserId);
                    }

                    @Override
                    public void onDialogDismiss(String lastMsg) {
//                        tvCommentLabel.setText(lastMsg);
                        tvCommentLabel.setVisibility(View.VISIBLE);
                    }
                });
        commentDialog.show(getActivity().getFragmentManager(), "CommentDialog");
    }

    private void showDeletePop(final int dataPosition, final Comment data) {
        View viewDelete = LayoutInflater.from(getContext()).inflate(R.layout.ppw_delete_comment, null);
        viewDelete.findViewById(R.id.tvDelete).setOnClickListener(new MyClickListener() {
            @Override
            protected void onMyClick(View v) {
                commentListPresenter.deleteComment(dataPosition, data.getId(), data.getOpus_id());
                if (null != popupWindow) {
                    popupWindow.dismiss();
                }
            }
        });
        viewDelete.findViewById(R.id.tvCancel).setOnClickListener(new MyClickListener() {
            @Override
            protected void onMyClick(View v) {
                if (null != popupWindow) {
                    popupWindow.dismiss();
                }
            }
        });
        popupWindow = PopupWindowUtils.createPopWindowFromBottom(rvComment, viewDelete, 0.5f);
    }

    private void showReportPop(final List<ReportType> list) {
        if (!CollectionUtil.isEmpty(list)) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.ppw_report_works, null);
            ListView listView = (ListView) view.findViewById(R.id.lvReportList);
            final PopupWindow popupWindow = PopupWindowUtils.createPopWindowFromBottom(rvComment, view);
            view.findViewById(R.id.tvCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });

            List<String> stringList = new ArrayList<>();
            final List<ReportType> reportTypes = new ArrayList<>();
            for (ReportType reportType : list) {
                if (reportType.getResource() == 0) {
                    reportTypes.add(reportType);
                    stringList.add(reportType.getDesc());
                }
            }
            listView.setAdapter(new ArrayAdapter<>(getContext(), R.layout.item_report_works,
                    stringList.toArray(new String[stringList.size()])));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    commentListPresenter.reportWorks(0, videoId, reportTypes.get(position).getId());
                    popupWindow.dismiss();
                }
            });
        } else {
            ToastUtil.showToast(R.string.hintErrorDataDelayTry);
        }
    }

    @Override
    public void onStarClick(int dataPosition, Comment comment) {
        if (AccountUtil.isLogin()) {
            if (!comment.is_like()) {
                KLog.i("===点赞的位置：" + dataPosition);
                commentListPresenter.clickLike(dataPosition, comment.getId(), comment.is_like() ? 1 : 0);
            }
        } else {
            showReLogin();
        }
    }

    @Override
    public void onDecibelListOk(boolean isRefresh, List<DecibelEntity> list, boolean hasMore, DecibelListResponse.StatisticEntity statistic) {

    }

    @Override
    public void onRequestDataError(int requestCode, String message) {
        super.onRequestDataError(requestCode, message);
        if (requestCode == HttpConstant.TYPE_COMMENT_LIST || (HttpConstant.TYPE_COMMENT_LIST + 1) == requestCode) {
            commentListAdapter.showError(false);
        }
    }

    @Override
    public void onCommentListOk(boolean isRefresh, List<Comment> list, boolean hasMore) {
        commentListAdapter.addData(isRefresh, list, hasMore);
        refreshCommentCount(true, commentCount);
    }


    @Override
    public void onDeleteCommentOk(int position, long commentId, CommentDataCount countBean) {
        ToastUtil.showToast("删除评论成功");
        if (null != countBean) {
            RefreshCommentBean commentBean = new RefreshCommentBean(pageId, "",
                    false, this.videoPosition, videoId, countBean.comment_count);
            EventHelper.post(GlobalParams.EventType.TYPE_REFRESH_COMMENT, commentBean);
            refreshCommentCount(true, countBean.comment_count);
        }
        if (commentListAdapter.getDataContainer().size() > 1) {
            commentListAdapter.refreshItemDelete(position, commentId);
        } else {
            commentListAdapter.addData(true, null, false);
        }
    }

    @Override
    public void onLikeOK(int dataPosition, boolean isLike, OpusLikeCommentResponse response) {
        Comment comment = commentListAdapter.getItemData(dataPosition);
        if (comment != null) {
            comment.setLike_count(response.getLike_count());
            comment.setIs_like(isLike);
            int refreshPosition = (dataPosition + (commentListAdapter.hasHeader() ? 1 : 0));
            KLog.i("===刷新点赞的位置：" + refreshPosition);
            commentListAdapter.notifyItemChanged(refreshPosition, "comment");
        }
    }


    @Override
    public void onVideoError(int position, long videoId, String message) {
        //视频被删除
        showToast(message);
    }

    @Override
    public void onReportListOk(List<ReportType> list) {
        InitCatchData.setReportEntry(list);
        if (videoId > 0 && !CollectionUtil.isEmpty(list)) {
            if (AccountUtil.isLogin()) {
                showReportPop(list);
            } else {
                showReLogin();
            }
        } else {
            ToastUtil.showToast(R.string.hintErrorDataDelayTry);
        }
    }

    public void refreshData(long videoId, int pageId, int commentCount, int videoPosition) {
        KLog.i("======refreshData:" + rvComment);
        this.videoId = videoId;
        this.pageId = pageId;
        this.commentCount = commentCount;
        this.videoPosition = videoPosition;
        if (rvComment != null) {
            rvComment.autoRefresh(0);
        }
    }

    public void clearData() {
        if (commentListAdapter != null) {
            KLog.i("=======clearData");
            commentListAdapter.clearData();
            refreshCommentCount(false, 0);
        }
    }

    @Override
    public void onVideoListOk(boolean isRefresh, List<ShortVideoItem> list, List<Banner> bannerList, List<UserInfo> userInfos, boolean hasMore) {
        //不需要
    }


    @Override
    public void onTopicInfoOk(TopicInfoBean bean) {
        //不需要
    }

    @Override
    public void onLikeOk(long videoId, int position, ShortVideoLoveResponse bean) {
        //不需要
    }

    @Override
    public void onLikeFail(long videoId, int position, boolean isFlyLike) {
        //不需要
    }

    @Override
    public void onReportOk() {
        showToast("举报成功");
    }

    @Override
    public void onDeleteVideoOk(int position, long videoId) {
        ToastUtil.showToast("删除作品成功");
        EventHelper.post(GlobalParams.EventType.TYPE_VIDEO_DELETE, videoId);
    }

    @Override
    public void onCommentOk(int position, VideoCommentResponse bean) {
        RefreshCommentBean commentBean = new RefreshCommentBean(pageId,
                bean.comment != null ? bean.comment.getTitle() : "",
                true,
                position,
                videoId,
                bean.data_count != null ? bean.data_count.comment_count : 0);
        commentBean.commentResponse = bean;
        refreshCommentList(commentBean);
        EventHelper.post(GlobalParams.EventType.TYPE_REFRESH_COMMENT, commentBean);
        showToast(getResources().getString(R.string.stringCommentReplyOk));
    }

    public void refreshCommentList(RefreshCommentBean commentBean) {
        if (null != commentBean.commentResponse && null != commentBean.commentResponse.comment) {
            commentListAdapter.insertData(commentBean.commentResponse.comment);
            if (commentBean.commentResponse.data_count != null) {
                refreshCommentCount(true, commentBean.commentResponse.data_count.comment_count);
            }
        }
    }

    private void refreshCommentCount(boolean reset, int count) {
        commentCount = count;
        if (refreshCommentListener != null) {
            refreshCommentListener.onRefreshComment(true, reset, count);
        }
    }

    public void setRefreshCommentListener(RefreshCommentListener listener) {
        refreshCommentListener = listener;
    }

    @Override
    public void onCommentFailed(String msg) {
        showToast(msg);
    }

}