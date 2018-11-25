package com.wmlive.hhvideo.heihei.personal.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.common.base.DcBaseFragment;
import com.wmlive.hhvideo.heihei.beans.personal.UserAccountChargeCreateOrderResponse;
import com.wmlive.hhvideo.heihei.beans.personal.UserAccountChargeEntry;
import com.wmlive.hhvideo.heihei.beans.personal.UserAccountChargeResponse;
import com.wmlive.hhvideo.heihei.beans.personal.UserAccountEntity;
import com.wmlive.hhvideo.heihei.beans.personal.UserAccountResponse;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.beans.splash.PayUrl;
import com.wmlive.hhvideo.heihei.personal.adapter.MyFragmentPagerAdapter;
import com.wmlive.hhvideo.heihei.personal.fragment.RecyclerScrollListener;
import com.wmlive.hhvideo.heihei.personal.fragment.UserAccountChargeFragment;
import com.wmlive.hhvideo.heihei.personal.fragment.UserAccountDuihuanFragment;
import com.wmlive.hhvideo.heihei.personal.pay.AlipayUtil;
import com.wmlive.hhvideo.heihei.personal.pay.PayResult;
import com.wmlive.hhvideo.heihei.personal.pay.WechatPayUtil;
import com.wmlive.hhvideo.heihei.personal.presenter.UserAccountChargePresenter;
import com.wmlive.hhvideo.heihei.personal.presenter.UserAccountInfoPresenter;
import com.wmlive.hhvideo.heihei.personal.view.IUserAccountChargeView;
import com.wmlive.hhvideo.heihei.personal.view.IUserAccountInfoView;
import com.wmlive.hhvideo.utils.CommonUtils;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.JsonUtils;
import com.wmlive.hhvideo.utils.KLog;
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
 * 充值账户
 */

public class UserAccountEarningsActivity extends DcBaseActivity implements
        IUserAccountInfoView, IUserAccountChargeView {
    @BindView(R.id.tv_count)
    TextView tv_count;//钻石余额

    @BindView(R.id.ll_container)
    LinearLayout ll_container;
    @BindView(R.id.ll_1)
    LinearLayout ll_1;
    @BindView(R.id.ll_2)
    LinearLayout ll_2;
    @BindView(R.id.ll_3)
    LinearLayout ll_3;
    @BindView(R.id.tv_money)
    TextView tv_money;
    @BindView(R.id.tv_money2)
    TextView tv_money2;
    @BindView(R.id.tv_money3)
    TextView tv_money3;
    @BindView(R.id.tv_diament)
    TextView tv_diament;
    @BindView(R.id.tv_diament2)
    TextView tv_diament2;
    @BindView(R.id.tv_diament3)
    TextView tv_diament3;
    @BindView(R.id.rl_ali)
    RelativeLayout rl_ali;
    @BindView(R.id.rl_wechat)
    RelativeLayout rl_wechat;
    @BindView(R.id.iv_ali)
    ImageView iv_ali;
    @BindView(R.id.iv_wechat)
    ImageView iv_wechat;
    @BindView(R.id.tv_charge)
    TextView tv_charge;

    @BindView(R.id.ll_main)
    LinearLayout ll_main;

    @BindView(R.id.ll_empty)
    LinearLayout ll_empty;

    @BindView(R.id.tv_pay_tips)
    TextView tvPayTips;

    @BindView(R.id.tv_wechat_num)
    TextView tvWechatNum;


    public UserAccountInfoPresenter userAccountInfoPresenter;
    private UserAccountChargePresenter userAccountChargePresenter;
    private long user_id = 0;
    public static final String KEY_PARAM = "user_id";

    private List<UserAccountChargeEntry> wechatList = new ArrayList<>();
    private List<UserAccountChargeEntry> alipayList = new ArrayList<>();

    private static final int PAY_TYPE_ALY = 0;
    private static final int PAY_TYPE_WECHAT = 1;
    private int currentPayType = 0;
    private long packetId;
    private int curentItem;

    public static void startUserAccountActivity(Context context, long user_id) {
        Intent intent = new Intent(context, UserAccountEarningsActivity.class);
        intent.putExtra(KEY_PARAM, user_id);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_user_account_earnings;
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle("", true);

        if (!NetUtil.isNetworkConnected(this)) {
            ll_empty.setVisibility(View.VISIBLE);
            ll_main.setVisibility(View.GONE);
            return;
        } else {
            ll_main.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        }
        //获取账户信息
        userAccountInfoPresenter = new UserAccountInfoPresenter(this);
        addPresenter(userAccountInfoPresenter);

        //获取充值信息
        userAccountChargePresenter = new UserAccountChargePresenter(this);
        addPresenter(userAccountChargePresenter);
        userAccountChargePresenter.getChargeList();

        iv_ali.setSelected(true);
        iv_wechat.setSelected(false);
        currentPayType = PAY_TYPE_ALY;
        user_id = getIntent().getLongExtra(KEY_PARAM, 0);
        choosePayCount(1);

        bindListener();

        tvWechatNum.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        PayUrl pay = InitCatchData.getPayTips();
        String text = "";
        if (null != pay) {
            text = pay.getPayTips();
            if (!TextUtils.isEmpty(text)) {
                tvPayTips.setText(text);
            }
            text = pay.getWechat();
            if (!TextUtils.isEmpty(text)) {
                tvWechatNum.setText(text);
            }
        }
        userAccountInfoPresenter.getAccountInfo();
        registerReceiver();
    }

    private void bindListener() {
        tvWechatNum.setOnClickListener(this);
        ll_1.setOnClickListener(this);
        ll_2.setOnClickListener(this);
        ll_3.setOnClickListener(this);
        rl_ali.setOnClickListener(this);
        rl_wechat.setOnClickListener(this);
        tv_charge.setOnClickListener(this);
    }


    //支付宝 回调
    @SuppressLint("HandlerLeak")
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
                        refreshPressenter();
                    } else if (TextUtils.equals(resultStatus, "6001")) {
                        showToast("支付取消");
                    } else {
                        showToast("支付失败");
                    }

                    Map<String, String> map = new HashMap<>(6);
                    map.put("package_id", String.valueOf(packetId));
                    if (TextUtils.equals(resultStatus, "9000")) {
                        refreshPressenter();
                    } else {
                        map.put("reason", TextUtils.equals(resultStatus, "6001") ? "支付取消" : "支付失败");
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void handleInfoSucceed(UserAccountResponse response) {
        UserAccountEntity userAccountEntity = response.getUser_gold_account();
        refreshInfo(userAccountEntity);
    }

    @Override
    public void handleInfoFailure(String error_msg) {
        showToast(error_msg);

    }

    @Override
    protected void onSingleClick(View v) {
        switch (v.getId()) {
            case R.id.tv_wechat_num:
                ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setPrimaryClip(ClipData.newPlainText(null, tvWechatNum.getText()));
                showToast(R.string.copy_succeed);
                break;
            case R.id.ll_1:
                choosePayCount(1);
                break;
            case R.id.ll_2:
                choosePayCount(2);
                break;
            case R.id.ll_3:
                choosePayCount(3);
                break;
            case R.id.rl_ali:
                iv_ali.setSelected(true);
                iv_wechat.setSelected(false);
                currentPayType = PAY_TYPE_ALY;
                refreshCharList(alipayList);
                break;
            case R.id.rl_wechat:
                iv_wechat.setSelected(true);
                iv_ali.setSelected(false);
                currentPayType = PAY_TYPE_WECHAT;
                refreshCharList(wechatList);
                break;
            case R.id.tv_charge:
                if (NetUtil.isNetworkConnected(this)) {
                    if (currentPayType == PAY_TYPE_ALY) {
                        UserAccountChargeEntry userAccountChargeEntry = alipayList.get(curentItem);
                        packetId = userAccountChargeEntry.getId();
                        KLog.d("userAccountChargeEntry==" + userAccountChargeEntry);
                    } else {
                        UserAccountChargeEntry userAccountChargeEntry = wechatList.get(curentItem);
                        KLog.d("userAccountChargeEntry==" + userAccountChargeEntry);
                        packetId = userAccountChargeEntry.getId();
                    }

                    userAccountChargePresenter.getCreateOrder(packetId);
                } else {
                    showToast(R.string.network_null);
                }
                break;
            default:
                break;
        }
    }

    private void choosePayCount(int i) {
        ll_1.setSelected(i == 1);
        tv_money.setSelected(i == 1);
        tv_diament.setSelected(i == 1);
        ll_2.setSelected(i == 2);
        tv_money2.setSelected(i == 2);
        tv_diament2.setSelected(i == 2);
        ll_3.setSelected(i == 3);
        tv_money3.setSelected(i == 3);
        tv_diament3.setSelected(i == 3);
        curentItem = i - 1;
    }

    /**
     * 刷新信息
     *
     * @param userAccountEntity
     */
    public void refreshInfo(UserAccountEntity userAccountEntity) {
        tv_count.setText(String.valueOf(userAccountEntity.getGold()));
//        tvUserAccountHongbao.setText(CommonUtils.doubleTrans(userAccountEntity.getPoint_worth() / 100));
//        tvUserAccountJinbi.setText(String.valueOf(userAccountEntity.getPoint()));
    }

    public void refreshPressenter() {
        if (null != userAccountInfoPresenter) {
            userAccountInfoPresenter.getAccountInfo();
        }
    }

    @Override
    public void handleChargeListSucceed(UserAccountChargeResponse response) {
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
        if (currentPayType == PAY_TYPE_ALY) {
            refreshCharList(alipayList);
        } else {
            refreshCharList(wechatList);
        }

    }

    private void refreshCharList(List<UserAccountChargeEntry> list) {
        if (list.size() == 0) {
            return;
        }
        if (list.size() == 1) {
            tv_money.setText((int) list.get(0).getPay_money() / 100 + "元");
            tv_diament.setText("(" + list.get(0).getGold() + "钻石)");

        } else if (list.size() == 2) {
            tv_money.setText((int) list.get(0).getPay_money() / 100 + "元");
            tv_diament.setText("(" + list.get(0).getGold() + "钻石)");
            tv_money2.setText((int) list.get(1).getPay_money() / 100 + "元");
            tv_diament2.setText("(" + list.get(1).getGold() + "钻石)");
        } else {
            tv_money.setText((int) list.get(0).getPay_money() / 100 + "元");
            tv_diament.setText("(" + list.get(0).getGold() + "钻石)");
            tv_money2.setText((int) list.get(1).getPay_money() / 100 + "元");
            tv_diament2.setText("(" + list.get(1).getGold() + "钻石)");
            tv_money3.setText((int) list.get(2).getPay_money() / 100 + "元");
            tv_diament3.setText("(" + list.get(2).getGold() + "钻石)");
        }
    }

    @Override
    public void handleChargeListFailure(String error_msg) {
        showToast(error_msg);
    }

    /**
     * '
     * <p>
     * 生成支付订单成功
     *
     * @param response
     */
    @Override
    public void handleChargeCreateOrderSucceed(UserAccountChargeCreateOrderResponse response) {
        JSONObject jsonObject = JsonUtils.parseObject(response.getData());
        if (jsonObject != null) {
            if (currentPayType == PAY_TYPE_WECHAT) {
                WechatPayUtil.wxChatPay(UserAccountEarningsActivity.this, jsonObject.getString("data"));
            } else {
                AlipayUtil.alipay(UserAccountEarningsActivity.this, jsonObject.getString("data"), mHandler);
            }
        }
    }

    @Override
    public void handleChargeCreateOrderFailure(long id, String error_msg) {
        showToast(error_msg);
    }

    public static final String WECHAT_PAY_SUCCEED = "wechat_pay_succeed";
    private ManageActivityBroadCast manageActivityBroadCast;

    private void registerReceiver() {
        if (null == manageActivityBroadCast) {
            manageActivityBroadCast = new ManageActivityBroadCast();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WECHAT_PAY_SUCCEED);
        registerReceiver(manageActivityBroadCast, intentFilter);
    }

    private class ManageActivityBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                String action = intent.getAction();
                int result = intent.getIntExtra("result", 0);
                if (!TextUtils.isEmpty(action) && action.equals(WECHAT_PAY_SUCCEED)) {
                    if (result == 1) {
                        refreshPressenter();
                    } else {
                    }
                }
            }
        }
    }
}
