package com.wmlive.hhvideo.heihei.mainhome.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.wmlive.hhvideo.common.base.DcBaseFragment;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.personal.DecibelEntity;
import com.wmlive.hhvideo.heihei.beans.personal.DecibelListResponse;
import com.wmlive.hhvideo.heihei.mainhome.presenter.DecibelListPresenter;
import com.wmlive.hhvideo.heihei.mainhome.view.RefreshCommentListener;
import com.wmlive.hhvideo.heihei.personal.activity.UserHomeActivity;
import com.wmlive.hhvideo.heihei.personal.adapter.DecibelListAdapter;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 5/2/2018 - 3:17 PM
 * 类描述：
 */
public class DecibelListFragment extends DcBaseFragment
        implements RefreshRecyclerView.OnLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener,
        DecibelListAdapter.OnUserClickListener,
        DecibelListPresenter.IDecibelListView {

    private static final String KEY_VIDEO_ID = "key_video_id";
    private static final String KEY_PAGE_ID = "key_page_id";
    private static final String KEY_VIDEO_POSITION = "key_video_position";
    private static final String KEY_COMMENT_COUNT = "key_comment_count";

    @BindView(R.id.rvComment)
    RefreshRecyclerView rvDecibel;
    private DecibelListAdapter decibelListAdapter;
    private DecibelListPresenter decibelListPresenter;
    private long videoId;
    private int pageId;
    private int videoPosition;
    private int commentCount;
    private RefreshCommentListener refreshCommentListener;

    public static DecibelListFragment newInstance() {
        DecibelListFragment decibelListFragment = new DecibelListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(LAZY_MODE, true);
        decibelListFragment.setArguments(bundle);
        return decibelListFragment;
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_decibel_list;
    }

    @Override
    protected void onSingleClick(View v) {

    }

    @Override
    protected void initData() {
        super.initData();
        decibelListAdapter = new DecibelListAdapter(new ArrayList<>(), rvDecibel, DecibelListAdapter.RANKLIST_TYPE_VIDEO_DETAIL);
        decibelListAdapter.setOnUserClickListener(this);
        rvDecibel.setCustomEmptyView(R.layout.view_recycler_no_data, -1, getResources().getString(R.string.ranklist_gift_other_null));
        rvDecibel.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDecibel.setNestedScrollingEnabled(false);
        rvDecibel.setOnLoadMoreListener(this);
        rvDecibel.setOnRefreshListener(this);
        rvDecibel.setAdapter(decibelListAdapter);
        decibelListPresenter = new DecibelListPresenter(this);
        addPresenter(decibelListPresenter);
        rvDecibel.autoRefresh(100);
    }

    @Override
    public void onLoadMore() {
        decibelListPresenter.getDecibelList(false, videoId);
    }

    @Override
    public void onRefresh() {
        decibelListPresenter.getDecibelList(true, videoId);
    }

    @Override
    public void onUserClick(String userId) {
        if (!TextUtils.isEmpty(userId) && TextUtils.isDigitsOnly(userId)) {
            if (refreshCommentListener != null) {
                refreshCommentListener.onDismiss();
            }
            UserHomeActivity.startUserHomeActivity(getContext(), Long.parseLong(userId));
        } else {
            showToast(R.string.hintErrorDataDelayTry);
        }
    }

    @Override
    public void onDecibelListOk(boolean isRefresh, List<DecibelEntity> list, boolean hasMore, DecibelListResponse.StatisticEntity statistic) {
        refreshDecibelCount(true, statistic != null ? statistic.total_point : 0);
        if (rvDecibel != null) {
            decibelListAdapter.setStatistic(statistic != null ? statistic : new DecibelListResponse.StatisticEntity());
            decibelListAdapter.addData(isRefresh, list);
        }
    }

    public void refreshData(long videoId, int pageId, int commentCount, int videoPosition) {
        KLog.i("======refreshData:" + rvDecibel);
        this.videoId = videoId;
        this.pageId = pageId;
        this.commentCount = commentCount;
        this.videoPosition = videoPosition;
        if (rvDecibel != null) {
            rvDecibel.autoRefresh(100);
        }
    }

    public void clearData() {
        if (decibelListAdapter != null) {
            KLog.i("=======clearData");
            decibelListAdapter.clearData();
            refreshDecibelCount(false, 0);
        }
    }

    private void refreshDecibelCount(boolean reset, int count) {
        if (refreshCommentListener != null) {
            refreshCommentListener.onRefreshComment(false, reset, count);
        }
    }

    public void setRefreshCommentListener(RefreshCommentListener listener) {
        refreshCommentListener = listener;
    }

    @Override
    public void onRequestDataError(int requestCode, String message) {
        super.onRequestDataError(requestCode, message);
        if (requestCode == HttpConstant.TYPE_DECIBEL_LIST || (HttpConstant.TYPE_DECIBEL_LIST + 1) == requestCode) {
            decibelListAdapter.showError(false);
        }
    }

}
