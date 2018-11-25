package com.wmlive.hhvideo.heihei.personal.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.wmlive.hhvideo.heihei.beans.personal.UserAccountDuihuanEntry;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshAdapter;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;

import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 * Created by XueFei on 2017/7/26.
 * <p>
 * 账户--兑换
 */

public class UserAccountDuihuanAdapter extends RefreshAdapter<UserAccountDuihuanHolder, UserAccountDuihuanEntry> {

    public UserAccountDuihuanAdapter(RefreshRecyclerView refreshView, List<UserAccountDuihuanEntry> list) {
        super(list, refreshView);
    }

    @Override
    public UserAccountDuihuanHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new UserAccountDuihuanHolder(parent, R.layout.item_user_account_duihuan);
    }

    @Override
    public void onBindHolder(UserAccountDuihuanHolder holder, final int position, UserAccountDuihuanEntry data) {
        holder.tvValue.setText(String.valueOf(String.valueOf(data.getGold())));
        holder.tvMoney.setText(String.valueOf(String.valueOf(data.getPoint() + "分贝")));
        holder.tvValueGiving.setText(data.getDesc());

        holder.tvMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickPayCustom.onPayClick(v, position);
            }
        });
    }

    private OnClickPayCustom mOnClickPayCustom;

    public interface OnClickPayCustom {
        void onPayClick(View view, int position);
    }

    public void setOnClickCustom(OnClickPayCustom onClickPayCustom) {
        mOnClickPayCustom = onClickPayCustom;
    }
}
