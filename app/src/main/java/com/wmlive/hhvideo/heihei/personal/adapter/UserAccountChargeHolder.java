package com.wmlive.hhvideo.heihei.personal.adapter;

import android.view.ViewGroup;
import android.widget.TextView;

import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by XueFei on 2017/7/26.
 * <p>
 * 账户充值
 */

public class UserAccountChargeHolder extends BaseRecyclerViewHolder {

    @BindView(R.id.tv_value)
    TextView tvValue;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_value_giving)
    TextView tvValueGiving;

    public UserAccountChargeHolder(ViewGroup parent, int layoutId) {
        super(parent, layoutId);
    }
}
