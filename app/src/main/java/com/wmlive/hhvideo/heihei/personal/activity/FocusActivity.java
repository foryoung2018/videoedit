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
import com.wmlive.hhvideo.heihei.personal.adapter.FocusAdapter;
import com.wmlive.hhvideo.heihei.personal.presenter.FocusPresenter;
import com.wmlive.hhvideo.heihei.personal.view.IFocusView;
import com.wmlive.hhvideo.widget.dialog.CustomDialog;
import com.wmlive.hhvideo.widget.refreshrecycler.OnRecyclerItemClickListener;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by XueFei on 2017/5/31.
 * <p>
 * 关注页
 */

public class FocusActivity extends DcBaseActivity implements SwipeRefreshLayout.OnRefreshListener,
        RefreshRecyclerView.OnLoadMoreListener, IFocusView,
        FocusAdapter.OnClickFocusCustom,
        OnRecyclerItemClickListener, FollowUserPresenter.IFollowUserView {
    @BindView(R.id.rv_focus_list)
    RefreshRecyclerView rvFocusList;

    private FocusAdapter focusAdapter;

    private FocusPresenter focusPresenter;
    private FollowUserPresenter followUserPresenter;

    private Intent intent;

    private long user_id = 0;
    private long current_id = 0;
    private UserInfo userInfo;

    public static final String KEY_PARAM = "user";

    public static void startFocusActivity(Context context, UserInfo userInfo) {
        Intent intent = new Intent(context, FocusActivity.class);
        intent.putExtra(KEY_PARAM, userInfo);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_focus;
    }


    @Override
    protected void initData() {
        super.initData();
        focusPresenter = new FocusPresenter(this);
        followUserPresenter = new FollowUserPresenter(this);
        addPresenter(focusPresenter, followUserPresenter);

        intent = getIntent();
        userInfo = (UserInfo) intent.getSerializableExtra(KEY_PARAM);
        if (null != userInfo) {
            user_id = userInfo.getId();
            current_id = AccountUtil.getUserId();

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            rvFocusList.setLayoutManager(linearLayoutManager);
            focusAdapter = new FocusAdapter(rvFocusList, new ArrayList<SearchUserBean>(), current_id);
            rvFocusList.setAdapter(focusAdapter);
            rvFocusList.setOnRefreshListener(this);
            rvFocusList.setOnLoadMoreListener(this);
            focusAdapter.setOnRecyclerItemClickListener(this);
            focusAdapter.setOnClickCustom(this);

            rvFocusList.autoRefresh();
            if (user_id == current_id) {
                setTitle("我关注的", true);
//                focusAdapter.setEmptyStr(R.string.user_home_focus);
                rvFocusList.setCustomEmptyView(R.layout.view_empty_follow, -1, getResources().getString(R.string.user_home_focus));
            } else {
                setTitle("Ta的关注", true);

//                focusAdapter.setEmptyStr(R.string.user_other_home_focus);
                rvFocusList.setCustomEmptyView(R.layout.view_empty_follow, -1, getResources().getString(R.string.user_other_home_focus));
            }
            focusAdapter.setShowImg(true);

        }
    }

    @Override
    public void onFocusListOk(boolean isRefresh, ListFollowerFansResponse response, boolean hasMore) {
        List<SearchUserBean> users = response.users;
        focusAdapter.addData(isRefresh, users);
    }

    @Override
    public void onFocusFail(boolean isRefresh, String message) {
        showToast(message);
        focusAdapter.showError(isRefresh);
    }

    @Override
    public void onRefresh() {
        focusPresenter.getFocusList(true, user_id);
    }

    @Override
    public void onLoadMore() {
        focusPresenter.getFocusList(false, user_id);
    }

    @Override
    public void onFollowUserOk(boolean isRecommendFollow, boolean isFollowAll, int position, long userId, long videoId, boolean isFollowed) {
        SearchUserBean entry = focusAdapter.getItemData(mPosition);
        if (null != entry) {
            UserHomeRelation relation = entry.getRelation();
            if (null != relation) {
                relation.is_follow = isFollowed;
                focusAdapter.notifyItemChanged(mPosition);
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
                final SearchUserBean entry = focusAdapter.getItemData(position);
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
//                                    focusPresenter.updateFocusState(isFollow, entry.getId());
                                    followUserPresenter.follow(position, entry.getId(), isFollow);
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
//                            focusPresenter.updateFocusState(isFollow, entry.getId());
                            followUserPresenter.follow(position, entry.getId(), isFollow);
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
