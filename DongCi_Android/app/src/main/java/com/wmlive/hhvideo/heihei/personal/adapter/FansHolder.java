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
 * 粉丝
 */

public class FansHolder extends BaseRecyclerViewHolder {
    @BindView(R.id.iv_fans_item_head)
    public ImageView ivFansItemHead;
    @BindView(R.id.tv_fans_item_name)
    public TextView tvFansItemName;
    @BindView(R.id.iv_fans_item_state)
    public ImageView ivFansItemState;
    @BindView(R.id.ivVerifyIcon)
    public ImageView ivVerifyIcon;
    @BindView(R.id.tvFollow)
    public TextView tvFollow;

    public FansHolder(ViewGroup parent, int layoutId) {
        super(parent, layoutId);
    }
}
