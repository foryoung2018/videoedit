package com.wmlive.hhvideo.heihei.personal.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.wmlive.hhvideo.common.base.DcBaseFragment;
import com.wmlive.hhvideo.heihei.beans.personal.UserAccountChargeCreateOrderResponse;
import com.wmlive.hhvideo.heihei.beans.personal.UserAccountChargeEntry;
import com.wmlive.hhvideo.heihei.beans.personal.UserAccountChargeResponse;
import com.wmlive.hhvideo.heihei.personal.activity.UserAccountEarningsActivity;
import com.wmlive.hhvideo.heihei.personal.adapter.UserAccountChargeAdapter;
import com.wmlive.hhvideo.heihei.personal.pay.AlipayUtil;
import com.wmlive.hhvideo.heihei.personal.pay.PayResult;
import com.wmlive.hhvideo.heihei.personal.pay.WechatPayUtil;
import com.wmlive.hhvideo.heihei.personal.presenter.UserAccountChargePresenter;
import com.wmlive.hhvideo.heihei.personal.view.IUserAccountChargeView;
import com.wmlive.hhvideo.utils.JsonUtils;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;
import com.wmlive.networklib.util.NetUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by XueFei on 2017/7/25.
 * <p>
 * 账户--充值列表
 */

public class UserAccountChargeFragment extends DcBaseFragment implements
        IUserAccountChargeView, SwipeRefreshLayout.OnRefreshListener,
        RecyclerScrollListener, UserAccountChargeAdapter.OnClickPayCustom {

    @BindView(R.id.rv_list)
    RefreshRecyclerView rvList;
    @BindView(R.id.ll_empty)
    LinearLayout llEmpty;
    @BindView(R.id.tvMessage)
    TextView tvMessage;

    private UserAccountChargeAdapter userAccountChargeAdapter;
    private List<UserAccountChargeEntry> wechatList;
    private List<UserAccountChargeEntry> alipayList;

    private UserAccountChargePresenter userAccountChargePresenter;

    private RecyclerScrollListener mScrollListener;
    private int mScrollY = 0;

    private long user_id;
    private int payType = 0;

    public static UserAccountChargeFragment newInstance() {
        UserAccountChargeFragment fragment = new UserAccountChargeFragment();
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_user_account_charge;
    }

    @Override
    protected void initData() {
        super.initData();
        userAccountChargePresenter = new UserAccountChargePresenter(this);
        addPresenter(userAccountChargePresenter);
        wechatList = new ArrayList<>();
        alipayList = new ArrayList<>();

        Bundle bundle = getArguments();
        user_id = bundle.getLong("user_id", 0);
        int headHeight = bundle.getInt("headHeight", 0);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvList.setLayoutManager(linearLayoutManager);
        userAccountChargeAdapter = new UserAccountChargeAdapter(rvList, new ArrayList<UserAccountChargeEntry>());
        rvList.setAdapter(userAccountChargeAdapter);
        rvList.setOnRefreshListener(this);
        rvList.setLoadMoreEnable(false);

        userAccountChargeAdapter.setItemTypes(new ArrayList<Integer>() {{
            add(UserAccountChargeAdapter.TYPE_CHARGE);
            add(UserAccountChargeAdapter.TYPE_PAY);
        }});
        userAccountChargeAdapter.setOnClickCustom(this);

        userAccountChargeAdapter.setShowEmptyView(false);

        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, headHeight);
        View view = new LinearLayout(getActivity());
        view.setLayoutParams(params);
        rvList.setHeader(view);

        rvList.getRecycleView().addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (userAccountChargeAdapter.getItemCount() > 3) {
                    mScrollY += dy;
                    if (mScrollListener != null) {
                        mScrollListener.Scrolled(mScrollY, 0);
                    }
                }
            }
        });

        if (NetUtil.isNetworkConnected(getActivity())) {
            rvList.autoRefresh();
        } else {
            showEmpty();
        }

        registerReceiver();
    }

    @Override
    public void handleChargeListSucceed(UserAccountChargeResponse response) {
        llEmpty.setVisibility(View.GONE);
        List<UserAccountChargeEntry> list = response.getPay_package();
        for (UserAccountChargeEntry entry : list) {
            if (entry.getChannel().equalsIgnoreCase("alipay")) {
                alipayList.add(entry);
            } else if (entry.getChannel().equalsIgnoreCase("wechat")) {
                wechatList.add(entry);
            }
        }
        wechatList.add(new UserAccountChargeEntry());//添加 支付 type
        alipayList.add(new UserAccountChargeEntry());
        userAccountChargeAdapter.addData(true, wechatList);
    }

    @Override
    public void handleChargeListFailure(String error_msg) {
        showToast(error_msg);
        rvList.setRefreshing(false);
    }

    @Override
    public void handleChargeCreateOrderSucceed(UserAccountChargeCreateOrderResponse response) {
        JSONObject jsonObject = JsonUtils.parseObject(response.getData());
        if (jsonObject != null) {
            if (payType == 0) {
                WechatPayUtil.wxChatPay(getActivity(), jsonObject.getString("data"));
            } else {
                AlipayUtil.alipay(getActivity(), jsonObject.getString("data"), mHandler);
            }
        }
    }

    @Override
    public void handleChargeCreateOrderFailure(long id, String error_msg) {
        showToast(error_msg);
    }

    @Override
    public void onRefresh() {
        wechatList.clear();
        alipayList.clear();
        userAccountChargePresenter.getChargeList();
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
        switch (v.getId()) {
            default:
                break;
        }
    }

    public void getmScrollListener(RecyclerScrollListener scrollListener) {
        mScrollListener = scrollListener;
    }

    private long packetId;

    @Override
    public void onPayClick(View view, int position) {
        if (NetUtil.isNetworkConnected(getActivity())) {
            UserAccountChargeEntry userAccountChargeEntry = userAccountChargeAdapter.getItemData(position);
            packetId = userAccountChargeEntry.getId();
            userAccountChargePresenter.getCreateOrder(packetId);
        } else {
            showToast(R.string.network_null);
        }
    }

    @Override
    public void onWechatPayClick(View view) {
        payType = 0;
        userAccountChargeAdapter.addData(true, wechatList);
    }

    @Override
    public void onAlipayPayClick(View view) {
        payType = 1;
        userAccountChargeAdapter.addData(true, alipayList);
    }

    //支付宝 回调
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AlipayUtil.SDK_PAY_FLAG:
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    //同步获取结果
                    String resultInfo = payResult.getResult();
                    Log.i("Pay", "Pay:" + resultInfo);
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        showToast("支付成功");
                        refreshInfo();
                    } else if (TextUtils.equals(resultStatus, "6001")) {
                        showToast("支付取消");
                    } else {
                        showToast("支付失败");
                    }

                    Map<String, String> map = new HashMap<>(6);
                    map.put("package_id", String.valueOf(packetId));
                    if (TextUtils.equals(resultStatus, "9000")) {
                        refreshInfo();
                    } else {
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void registerReceiver() {
        if (null == manageActivityBroadCast) {
            manageActivityBroadCast = new ManageActivityBroadCast();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WECHAT_PAY_SUCCEED);
        getActivity().registerReceiver(manageActivityBroadCast, intentFilter);
    }

    public static final String WECHAT_PAY_SUCCEED = "wechat_pay_succeed";
    private ManageActivityBroadCast manageActivityBroadCast;

    private class ManageActivityBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                String action = intent.getAction();
                int result = intent.getIntExtra("result", 0);
                if (!TextUtils.isEmpty(action) && action.equals(WECHAT_PAY_SUCCEED)) {
                    Map<String, String> map = new HashMap<>(6);
                    map.put("package_id", String.valueOf(packetId));
                    if (result == 1) {
                        refreshInfo();
                    } else {
                    }
                }
            }
        }
    }

    /**
     * 支付成功后 刷新账户信息
     */
    private void refreshInfo() {
        if (getActivity() instanceof UserAccountEarningsActivity) {
            UserAccountEarningsActivity acitivity = (UserAccountEarningsActivity) getActivity();
            acitivity.refreshPressenter();
        }
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
}
