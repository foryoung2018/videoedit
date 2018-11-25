package com.wmlive.hhvideo.heihei.personal.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.beans.personal.ListFollowerFansResponse;
import com.wmlive.hhvideo.heihei.beans.personal.UserHomeRelation;
import com.wmlive.hhvideo.heihei.beans.search.SearchUserBean;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.mainhome.presenter.FollowUserPresenter;
import com.wmlive.hhvideo.heihei.personal.adapter.FansAdapter;
import com.wmlive.hhvideo.heihei.personal.presenter.FansPresenter;
import com.wmlive.hhvideo.heihei.personal.view.IFansView;
import com.wmlive.hhvideo.widget.dialog.CustomDialog;
import com.wmlive.hhvideo.widget.refreshrecycler.OnRecyclerItemClickListener;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by XueFei on 2017/5/31.
 * <p>
 * 粉丝页
 */

public class FansActivity extends DcBaseActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        RefreshRecyclerView.OnLoadMoreListener,
        IFansView, FansAdapter.OnClickFansCustom,
        OnRecyclerItemClickListener,
        FollowUserPresenter.IFollowUserView {
    @BindView(R.id.rv_fans_list)
    RefreshRecyclerView rvFansList;

    private FansAdapter fansAdapter;

    private long user_id = 0;
    private long current_id = 0;
    private UserInfo userInfo;

    private FansPresenter fansPresenter;
    private FollowUserPresenter followUserPresenter;

    public static final String KEY_PARAM = "user";

    public static void startFansActivity(Context context, UserInfo userInfo) {
        Intent intent = new Intent(context, FansActivity.class);
        intent.putExtra(KEY_PARAM, userInfo);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_fans;
    }


    @Override
    protected void initData() {
        super.initData();
        fansPresenter = new FansPresenter(this);
        followUserPresenter = new FollowUserPresenter(this);
        addPresenter(fansPresenter, followUserPresenter);
        userInfo = (UserInfo) getIntent().getSerializableExtra(KEY_PARAM);
        if (null != userInfo) {
            user_id = userInfo.getId();
            current_id = AccountUtil.getUserId();

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            rvFansList.setLayoutManager(linearLayoutManager);
            fansAdapter = new FansAdapter(rvFansList, new ArrayList<SearchUserBean>(), current_id);
            rvFansList.setAdapter(fansAdapter);
            rvFansList.setOnRefreshListener(this);
            rvFansList.setOnLoadMoreListener(this);
            rvFansList.autoRefresh();
            fansAdapter.setOnRecyclerItemClickListener(this);

            fansAdapter.setOnClickCustom(this);

            if (user_id == current_id) {
                setTitle("我的粉丝", true);
                rvFansList.setCustomEmptyView(R.layout.view_empty_follow, -1, getResources().getString(R.string.user_home_fans));
            } else {
                setTitle("Ta的粉丝", true);
                rvFansList.setCustomEmptyView(R.layout.view_empty_follow, -1, getResources().getString(R.string.user_other_home_fans));
            }
            fansAdapter.setShowImg(true);
        } else {
            toastFinish();
        }
    }

    @Override
    public void onFansListOk(boolean isRefresh, ListFollowerFansResponse response, boolean hasMore) {
        fansAdapter.addData(isRefresh, response.users, hasMore);
    }

    @Override
    public void onFansListFail(boolean isRefresh, String error_msg) {
        showToast(error_msg);
        fansAdapter.showError(isRefresh);
    }

    @Override
    public void onRefresh() {
        fansPresenter.getFansList(true, user_id);
    }

    @Override
    public void onLoadMore() {
        fansPresenter.getFansList(false, user_id);
    }

    @Override
    public void onFollowUserOk(boolean isRecommendFollow, boolean isFollowAll, int position, long userId, long videoId, boolean isFollowed) {
        SearchUserBean entry = fansAdapter.getItemData(position);
        if (null != entry) {
            UserHomeRelation relation = entry.getRelation();
            if (null != relation) {
                relation.is_follow = isFollowed;
                fansAdapter.notifyItemChanged(position);
                showToast(isFollowed ? R.string.user_follower : R.string.user_unfollower);
            }
        } else {
            showToast(R.string.net_error);
        }
    }

    @Override
    protected void onSingleClick(View v) {

    }

    int mPosition = -1;
    private CustomDialog customDialog;

    @Override
    public void onClick(View view, final int position) {
        if (view.getId() == R.id.tvFollow) {
            if (!AccountUtil.isLogin()) {
                showReLogin();
            } else {
                final SearchUserBean entry = fansAdapter.getItemData(position);
                if (null != entry) {
                    UserHomeRelation relation = entry.getRelation();
                    if (null != relation) {
                        mPosition = position;

                        final boolean isFollow = relation.is_follow;
                        if (isFollow) {
                            customDialog = new CustomDialog(this, R.style.BaseDialogTheme);
                            customDialog.setContent(getString(R.string.dialog_focus_tip));
                            customDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    customDialog.dismiss();
//                                    fansPresenter.updateFocusState(isFollow, entry.getId());
                                    followUserPresenter.follow(mPosition, entry.getId(), isFollow);
                                }
                            });
                            customDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    customDialog.dismiss();
                                }
                            });
                            customDialog.show();
                        } else {
//                            fansPresenter.updateFocusState(isFollow, entry.getId());
                            followUserPresenter.follow(mPosition, entry.getId(), isFollow);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onRecyclerItemClick(int dataPosition, View view, Object data) {
        SearchUserBean entry = (SearchUserBean) data;
        if (entry != null) {
            UserHomeActivity.startUserHomeActivity(this, entry.getId());
        } else {
            showToast(R.string.hintErrorDataDelayTry);
        }

    }

}
