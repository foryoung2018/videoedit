package com.wmlive.hhvideo.heihei.personal.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.search.SearchUserBean;
import com.wmlive.hhvideo.heihei.personal.adapter.BlankListAdapter;
import com.wmlive.hhvideo.heihei.personal.presenter.BlockListPresenter;
import com.wmlive.hhvideo.heihei.personal.presenter.BlockUserPresenter;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

public class BlankListActivity extends DcBaseActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        RefreshRecyclerView.OnLoadMoreListener,
        BlockUserPresenter.IBlockUserView,
        BlankListAdapter.OnUnblockClickListener, BlockListPresenter.IBlockListView {

    @BindView(R.id.rvBlankList)
    RefreshRecyclerView rvBlankList;
    private BlankListAdapter blankListAdapter;
    private BlockUserPresenter blockUserPresenter;
    private BlockListPresenter blockListPresenter;

    @Override
    protected void onSingleClick(View v) {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_blank_list;
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle("黑名单", true);
        blankListAdapter = new BlankListAdapter(new ArrayList<SearchUserBean>(4), rvBlankList);
        rvBlankList.setLayoutManager(new LinearLayoutManager(this));
        rvBlankList.setAdapter(blankListAdapter);
        rvBlankList.setOnRefreshListener(this);
        rvBlankList.setOnLoadMoreListener(this);
        blankListAdapter.setUnblockClickListener(this);
        blankListAdapter.setShowImg(true);
        blankListAdapter.setEmptyStr(R.string.noBlockList);
        blockUserPresenter = new BlockUserPresenter(this);
        blockListPresenter = new BlockListPresenter(this);
        addPresenter(blockUserPresenter, blockListPresenter);
        rvBlankList.autoRefresh(500);
    }

    @Override
    public void onRefresh() {
        blockListPresenter.getBlockList(true);
    }

    @Override
    public void onLoadMore() {
        blockListPresenter.getBlockList(false);
    }

    @Override
    public void onGetBlockUserOk(int position, long userId, boolean isBlock) {
        if (!isBlock) {
            showToast("已解除拉黑");
            if (blankListAdapter != null) {
                blankListAdapter.refreshItem(position, userId);
            }
        }
    }

    @Override
    public void onUnblockClick(int dataPosition, long userId) {
        blockUserPresenter.blockUser(dataPosition, userId, true);
    }

    @Override
    public void onRequestDataError(int requestCode, String message) {
        super.onRequestDataError(requestCode, message);
        blankListAdapter.addData(requestCode == HttpConstant.TYPE_BLOCK_LIST, null, true);
    }

    @Override
    public void onBlockListOk(boolean isRefresh, List<SearchUserBean> userInfoList, boolean hasMore) {
        blankListAdapter.addData(isRefresh, userInfoList, hasMore);
    }
}
