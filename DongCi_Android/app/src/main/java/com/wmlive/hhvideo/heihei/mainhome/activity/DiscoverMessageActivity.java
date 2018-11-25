package com.wmlive.hhvideo.heihei.mainhome.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.wmlive.hhvideo.common.DcRouter;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.heihei.beans.discovery.DiscMessageEntity;
import com.wmlive.hhvideo.heihei.mainhome.adapter.DiscoverMessageAdapter;
import com.wmlive.hhvideo.heihei.mainhome.presenter.DiscoverMessagePresenter;
import com.wmlive.hhvideo.widget.refreshrecycler.OnRecyclerItemClickListener;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

public class DiscoverMessageActivity extends DcBaseActivity<DiscoverMessagePresenter>
        implements DiscoverMessagePresenter.IDiscoverMessageView,
        OnRecyclerItemClickListener<DiscMessageEntity>,
        SwipeRefreshLayout.OnRefreshListener,
        RefreshRecyclerView.OnLoadMoreListener {

    @BindView(R.id.recyclerView)
    RefreshRecyclerView recyclerView;
    private DiscoverMessageAdapter discoverMessageAdapter;


    @Override
    protected DiscoverMessagePresenter getPresenter() {
        return new DiscoverMessagePresenter(this);
    }

    @Override
    protected void onSingleClick(View v) {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_discover_message;
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle("消息通知", true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        discoverMessageAdapter = new DiscoverMessageAdapter(new ArrayList<>(10), recyclerView);
        recyclerView.setAdapter(discoverMessageAdapter);
        discoverMessageAdapter.setOnRecyclerItemClickListener(this);
        discoverMessageAdapter.setEmptyStr(R.string.bell_message_null);
        discoverMessageAdapter.setShowImg(true);
        discoverMessageAdapter.setShowEmptyView(false);
        recyclerView.setOnRefreshListener(this);
        recyclerView.setOnLoadMoreListener(this);
        recyclerView.autoRefresh(300);
    }

    @Override
    public void onDiscoverMessageListOk(boolean isRefresh, boolean hasMore, List<DiscMessageEntity> news) {
        discoverMessageAdapter.setShowEmptyView(true);
        discoverMessageAdapter.addData(isRefresh, news, hasMore);
    }

    @Override
    public void onDiscoverMessageListFail(boolean isRefresh, String message) {
        discoverMessageAdapter.setShowEmptyView(true);
        showToast(message);
    }

    @Override
    public void onRecyclerItemClick(int dataPosition, View view, DiscMessageEntity data) {
        if (!TextUtils.isEmpty(data.link)) {
            DcRouter.linkTo(this, data.link);
        }
    }

    @Override
    public void onRefresh() {
        presenter.getMessageList(true);
    }

    @Override
    public void onLoadMore() {
        presenter.getMessageList(false);
    }
}
