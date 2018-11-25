package com.wmlive.hhvideo.heihei.personal.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.heihei.beans.personal.DecibelListResponse;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.personal.adapter.DecibelListAdapter;
import com.wmlive.hhvideo.heihei.personal.presenter.DecibelListPresenter;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

public class DecibelListActivity extends DcBaseActivity<DecibelListPresenter> implements
        DecibelListPresenter.IDecibelListView,
        RefreshRecyclerView.OnLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener,
        DecibelListAdapter.OnUserClickListener {
    private static final String KEY_USER_ID = "key_user_id";

    @BindView(R.id.recyclerView)
    RefreshRecyclerView recyclerView;
    private DecibelListAdapter decibelListAdapter;
    private long userId;

    public static void startDecibelListActivity(Context context, long userId) {
        Intent intent = new Intent(context, DecibelListActivity.class);
        intent.putExtra(KEY_USER_ID, userId);
        context.startActivity(intent);
    }

    @Override
    protected DecibelListPresenter getPresenter() {
        return new DecibelListPresenter(this);
    }

    @Override
    protected void onSingleClick(View v) {

    }

    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getLongExtra(KEY_USER_ID, 0);
            if (userId > 0) {
                setTitle("分贝榜单", true);
                decibelListAdapter = new DecibelListAdapter(new ArrayList<>(), recyclerView, DecibelListAdapter.RANKLIST_TYPE_USER_OWN);
                decibelListAdapter.setChangeBackground(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                if (AccountUtil.getUserId() == userId) {
                    recyclerView.setCustomEmptyView(R.layout.view_recycler_no_data, DeviceUtils.dip2px(DCApplication.getDCApp(), 500), null);
                } else {
                    recyclerView.setCustomEmptyView(R.layout.view_recycler_no_data, DeviceUtils.dip2px(DCApplication.getDCApp(), 500), getResources().getString(R.string.ranklist_gift_other_null));
                }
                recyclerView.setAdapter(decibelListAdapter);
                recyclerView.autoRefresh(500);
                recyclerView.setOnRefreshListener(this);
                recyclerView.setOnLoadMoreListener(this);
                decibelListAdapter.setOnUserClickListener(this);
                recyclerView.autoRefresh(400);
            } else {
                toastFinish();
            }
        } else {
            toastFinish();
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_decibel_list;
    }

    @Override
    public void onDecibelListOk(boolean isRefresh, DecibelListResponse response) {
        decibelListAdapter.setStatistic(response != null ? response.statistic : new DecibelListResponse.StatisticEntity());
        decibelListAdapter.addData(isRefresh, response != null ? response.data : null);
    }

    @Override
    public void onDecibelListFail(String message) {
        showToast(message);
        decibelListAdapter.addData(true, null);
    }

    @Override
    public void onRefresh() {
        presenter.getDecibelList(true, userId);
    }

    @Override
    public void onLoadMore() {
        presenter.getDecibelList(false, userId);
    }

    @Override
    public void onUserClick(String userId) {
        if (!TextUtils.isEmpty(userId) && TextUtils.isDigitsOnly(userId)) {
            UserHomeActivity.startUserHomeActivity(this, Long.parseLong(userId));
        } else {
            showToast(R.string.hintErrorDataDelayTry);
        }
    }
}
