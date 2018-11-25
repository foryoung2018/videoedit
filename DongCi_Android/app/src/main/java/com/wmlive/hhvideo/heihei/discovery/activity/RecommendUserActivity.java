package com.wmlive.hhvideo.heihei.discovery.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.discovery.adapter.RecommendUserAdapter;
import com.wmlive.hhvideo.heihei.discovery.presenter.RecommendUserPresenter;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.login.activity.LoginActivity;
import com.wmlive.hhvideo.heihei.personal.activity.UserHomeActivity;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;
import com.wmlive.networklib.entity.EventEntity;
import com.wmlive.networklib.util.EventHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

public class RecommendUserActivity extends DcBaseActivity<RecommendUserPresenter> implements
        RecommendUserPresenter.IRecommendUserView,
        RecommendUserAdapter.OnFollowClickListener,
        RefreshRecyclerView.OnLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerView)
    RefreshRecyclerView recyclerView;
    private RecommendUserAdapter userAdapter;

    @Override
    protected RecommendUserPresenter getPresenter() {
        return new RecommendUserPresenter(this);
    }

    @Override
    protected void onSingleClick(View v) {

    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(getString(R.string.stringMoreUserTitle), true);
        EventHelper.register(this);
        userAdapter = new RecommendUserAdapter(new ArrayList<UserInfo>(), recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);
        userAdapter.setFollowClickListener(this);
        recyclerView.setOnLoadMoreListener(this);
        recyclerView.setOnRefreshListener(this);
        recyclerView.autoRefresh();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_recommend_user;
    }


    @Override
    public void onGetListOk(boolean isRefresh, List<UserInfo> list, boolean hasMore) {
        userAdapter.addData(isRefresh, list, hasMore);
    }

    @Override
    public void onFollowClick(int position, long userId, boolean isFollowed) {
        if (AccountUtil.isLogin()) {
            presenter.follow(position, userId, isFollowed);
        } else {
            showReLogin();
        }
    }

    @Override
    public void onAvatarClick(long userId) {
        UserHomeActivity.startUserHomeActivity(this, userId);
    }

    @Override
    public void onLoadMore() {
        presenter.getUserList(false);
    }

    @Override
    public void onRefresh() {
        presenter.getUserList(true);
    }

    @Override
    public void onFollowUserOk(boolean isRecommendFollow, boolean isFollowAll,int position, long userId, long videoId, boolean isFollowed) {
        showToast(isFollowed ? R.string.user_follower : R.string.user_unfollower);
        userAdapter.refresh(position, userId, isFollowed);
    }

    @Override
    public void onRequestDataError(int requestCode, String message) {
        if (requestCode == HttpConstant.TYPE_GET_RECOMMEND_USER
                || requestCode == HttpConstant.TYPE_GET_RECOMMEND_USER + 1) {
            userAdapter.addData(true, null);
        } else {
            super.onRequestDataError(requestCode, message);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK
                && data != null
                && requestCode == LoginActivity.REQUEST_CODE_RELOGIN) {
            if (data.getBooleanExtra(LoginActivity.KEY_LOGIN_RESULT, false)) {
                onRefresh();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventHelper.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSocialLoginOk(EventEntity eventEntity) {
        if (eventEntity != null && eventEntity.code == GlobalParams.EventType.TYPE_LOGIN_OK) {
            onRefresh();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
