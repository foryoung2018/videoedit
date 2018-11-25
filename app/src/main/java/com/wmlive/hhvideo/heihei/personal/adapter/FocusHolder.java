package com.wmlive.hhvideo.heihei.personal.adapter;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by XueFei on 2017/5/31.
 * <p>
 * 关注
 */

public class FocusHolder extends BaseRecyclerViewHolder {
    @BindView(R.id.iv_focus_item_head)
    public ImageView ivFocusItemHead;
    @BindView(R.id.tv_focus_item_name)
    public TextView tvFocusItemName;
    @BindView(R.id.iv_focus_item_state)
    public ImageView ivFocusItemState;
    @BindView(R.id.ivVerifyIcon)
    public ImageView ivVerifyIcon;
    @BindView(R.id.tvFollow)
    public TextView tvFollow;

    public FocusHolder(ViewGroup parent, int layoutId) {
        super(parent, layoutId);
    }
}
