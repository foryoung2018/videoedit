package com.wmlive.hhvideo.heihei.personal.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.wmlive.hhvideo.heihei.beans.personal.UserAccountChargeEntry;
import com.wmlive.hhvideo.utils.CommonUtils;
import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshAdapter;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;

import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 * Created by XueFei on 2017/7/26.
 * <p>
 * 账户--充值
 */

public class UserAccountChargeAdapter extends RefreshAdapter<BaseRecyclerViewHolder, UserAccountChargeEntry> {
    public static final int TYPE_CHARGE = 20;//普通列表
    public static final int TYPE_PAY = 30;//支付

    private int normalType = TYPE_CHARGE;

    public UserAccountChargeAdapter(RefreshRecyclerView refreshView, List<UserAccountChargeEntry> list) {
        super(list, refreshView);
    }

    @Override
    public BaseRecyclerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_PAY:
                return new UserAccountChargePayHolder(parent, R.layout.fragment_user_account_charge_footer);
            case TYPE_CHARGE:
            default:
                return new UserAccountChargeHolder(parent, R.layout.item_user_account_charge);
        }
    }

    @Override
    public void onBindHolder(BaseRecyclerViewHolder holder, final int position, UserAccountChargeEntry data) {
        if (normalType == TYPE_CHARGE) {
            UserAccountChargeHolder userAccountChargeHolder = (UserAccountChargeHolder) holder;
            userAccountChargeHolder.tvValue.setText(String.valueOf(data.getGold()));
            userAccountChargeHolder.tvMoney.setText(String.valueOf("￥" + CommonUtils.doubleTrans(data.getPay_money() / 100)));
            userAccountChargeHolder.tvValueGiving.setText(data.getDescription());

            userAccountChargeHolder.tvMoney.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickPayCustom.onPayClick(v, position);
                }
            });
        } else if (normalType == TYPE_PAY) {
            final UserAccountChargePayHolder userAccountChargePayHolder = (UserAccountChargePayHolder) holder;
            userAccountChargePayHolder.llWechatPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mOnClickPayCustom) {
                        userAccountChargePayHolder.ivWechatPay.setSelected(true);
                        userAccountChargePayHolder.tvWechatPay.setSelected(true);

                        userAccountChargePayHolder.ivAlipayPay.setSelected(false);
                        userAccountChargePayHolder.tvAlipayPay.setSelected(false);
                        mOnClickPayCustom.onWechatPayClick(v);
                    }
                }
            });
            userAccountChargePayHolder.llAlipayPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mOnClickPayCustom) {
                        userAccountChargePayHolder.ivWechatPay.setSelected(false);
                        userAccountChargePayHolder.tvWechatPay.setSelected(false);

                        userAccountChargePayHolder.ivAlipayPay.setSelected(true);
                        userAccountChargePayHolder.tvAlipayPay.setSelected(true);
                        mOnClickPayCustom.onAlipayPayClick(v);
                    }
                }
            });
        }
    }

    @Override
    public int getItemType(int position) {
        if (position == (getItemCount() - 2)) {
            normalType = TYPE_PAY;
        } else {
            normalType = TYPE_CHARGE;
        }
        return normalType;
    }

    private OnClickPayCustom mOnClickPayCustom;

    public interface OnClickPayCustom {
        void onPayClick(View view, int position);

        void onWechatPayClick(View view);

        void onAlipayPayClick(View view);
    }

    public void setOnClickCustom(OnClickPayCustom onClickPayCustom) {
        mOnClickPayCustom = onClickPayCustom;
    }

}
