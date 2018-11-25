package com.wmlive.hhvideo.heihei.personal.adapter;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by XueFei on 2017/7/26.
 * <p>
 * 账户--支付选择
 */

public class UserAccountChargePayHolder extends BaseRecyclerViewHolder {

    @BindView(R.id.ll_wechat_pay)
    LinearLayout llWechatPay;
    @BindView(R.id.iv_wechat_pay)
    ImageView ivWechatPay;
    @BindView(R.id.tv_wechat_pay)
    TextView tvWechatPay;
    @BindView(R.id.ll_alipay_pay)
    LinearLayout llAlipayPay;
    @BindView(R.id.iv_alipay_pay)
    ImageView ivAlipayPay;
    @BindView(R.id.tv_alipay_pay)
    TextView tvAlipayPay;

    public UserAccountChargePayHolder(ViewGroup parent, int layoutId) {
        super(parent, layoutId);
        ivWechatPay.setSelected(true);
        tvWechatPay.setSelected(true);
        ivAlipayPay.setSelected(false);
        tvAlipayPay.setSelected(false);
    }
}
