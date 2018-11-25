package com.wmlive.hhvideo.heihei.personal.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.common.base.DcBaseFragment;
import com.wmlive.hhvideo.heihei.beans.personal.UserAccountDuihuanEntry;
import com.wmlive.hhvideo.heihei.beans.personal.UserAccountDuihuanResponse;
import com.wmlive.hhvideo.heihei.beans.personal.UserAccountEntity;
import com.wmlive.hhvideo.heihei.beans.personal.UserAccountResponse;
import com.wmlive.hhvideo.heihei.personal.activity.UserAccountEarningsActivity;
import com.wmlive.hhvideo.heihei.personal.adapter.UserAccountDuihuanAdapter;
import com.wmlive.hhvideo.heihei.personal.presenter.UserAccountDuihuanPresenter;
import com.wmlive.hhvideo.heihei.personal.view.IUserAccountDuihuanView;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;
import com.wmlive.networklib.util.NetUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by XueFei on 2017/7/25.
 * <p>
 * 账户--兑换列表
 */

public class UserAccountDuihuanFragment extends DcBaseFragment implements IUserAccountDuihuanView, SwipeRefreshLayout.OnRefreshListener, RecyclerScrollListener, UserAccountDuihuanAdapter.OnClickPayCustom {

    @BindView(R.id.rv_list)
    RefreshRecyclerView rvList;
    @BindView(R.id.ll_empty)
    LinearLayout llEmpty;
    @BindView(R.id.tvMessage)
    TextView tvMessage;

    private UserAccountDuihuanAdapter userAccountDuihuanAdapter;

    private UserAccountDuihuanPresenter userAccountDuihuanPresenter;

    private RecyclerScrollListener mScrollListener;
    private int mScrollY = 0;

    private long user_id;

    public static UserAccountDuihuanFragment newInstance() {
        UserAccountDuihuanFragment fragment = new UserAccountDuihuanFragment();
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_user_account_charge;
    }

    @Override
    protected void initData() {
        super.initData();

        userAccountDuihuanPresenter = new UserAccountDuihuanPresenter(this);
        addPresenter(userAccountDuihuanPresenter);

        Bundle bundle = getArguments();
        user_id = bundle.getLong("user_id", 0);
        int headHeight = bundle.getInt("headHeight", 0);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvList.setLayoutManager(linearLayoutManager);
        userAccountDuihuanAdapter = new UserAccountDuihuanAdapter(rvList, new ArrayList<UserAccountDuihuanEntry>());
        rvList.setAdapter(userAccountDuihuanAdapter);
        rvList.setOnRefreshListener(this);
        rvList.setLoadMoreEnable(false);

        userAccountDuihuanAdapter.setOnClickCustom(this);
        userAccountDuihuanAdapter.setShowEmptyView(false);

        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, headHeight);
        View view = new LinearLayout(getActivity());
        view.setLayoutParams(params);
        rvList.setHeader(view);

        rvList.getRecycleView().addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (userAccountDuihuanAdapter.getItemCount() > 3) {
                    mScrollY += dy;
                    if (mScrollListener != null) {
                        mScrollListener.Scrolled(mScrollY, 1);
                    }
                }
            }
        });

        if (NetUtil.isNetworkConnected(getActivity())) {
            rvList.autoRefresh();
        } else {
            showEmpty();
        }
    }

    @Override
    public void handleDuihuanListSucceed(UserAccountDuihuanResponse response) {
        llEmpty.setVisibility(View.GONE);
        userAccountDuihuanAdapter.addData(true, response.getPackage_list());
    }

    @Override
    public void handleDuihuanListFailure(String error_msg) {
        showToast(error_msg);
        rvList.setRefreshing(false);
    }

    @Override
    public void handleDuihuanJinbiSucceed(UserAccountResponse response) {
        if (getActivity() instanceof UserAccountEarningsActivity) {
            UserAccountEntity userAccountEntity = response.getUser_gold_account();
            UserAccountEarningsActivity acitivity = (UserAccountEarningsActivity) getActivity();
            acitivity.refreshInfo(userAccountEntity);
            showToast(R.string.user_duihuan_succeed);
        }
    }

    @Override
    public void handleDuihuanJinbiFailure(long id, String error_msg) {
        showToast(error_msg);
    }

    @Override
    public void onRefresh() {
        userAccountDuihuanPresenter.getDuihuanList();
    }

    @Override
    public void Scrolled(int distance, int pagePosition) {

    }

    @Override
    public void adjustScroll(int scrollHeight, int headerHeight) {
        if (rvList == null) {
            return;
        }
        mScrollY = headerHeight - scrollHeight;
        rvList.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (rvList.getChildCount() > 0) {
                    rvList.getViewTreeObserver().removeOnPreDrawListener(this);
                    ((LinearLayoutManager) rvList.getLayoutManager()).scrollToPositionWithOffset(0, -mScrollY);
                    return true;
                }
                return false;
            }
        });
    }


    @Override
    protected void onSingleClick(View v) {

    }

    public void getmScrollListener(RecyclerScrollListener scrollListener) {
        mScrollListener = scrollListener;
    }

    private void showEmpty() {
        tvMessage.setText(R.string.network_null);
        ViewTreeObserver vto = rvList.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rvList.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int rcvListHeight = rvList.getHeight();
                int headerHeight = rvList.getHeader().getHeight();

                llEmpty.getLayoutParams().height = rcvListHeight - headerHeight;
                llEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onPayClick(View view, int position) {
        if (NetUtil.isNetworkConnected(getActivity())) {
            UserAccountDuihuanEntry userAccountEntry = userAccountDuihuanAdapter.getItemData(position);
            userAccountDuihuanPresenter.duiHuanJinbi(userAccountEntry.getId());
        } else {
            showToast(R.string.network_null);
        }
    }
}
