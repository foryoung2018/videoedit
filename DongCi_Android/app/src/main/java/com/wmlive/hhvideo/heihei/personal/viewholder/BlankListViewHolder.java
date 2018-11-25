package com.wmlive.hhvideo.heihei.personal.viewholder;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by Administrator on 3/22/2018.
 */

public class BlankListViewHolder extends BaseRecyclerViewHolder {
    @BindView(R.id.ivAvatar)
    public ImageView ivAvatar;
    @BindView(R.id.tvName)
    public TextView tvName;
    @BindView(R.id.tvUnlock)
    public TextView tvUnlock;

    public BlankListViewHolder(ViewGroup parent, int layoutId) {
        super(parent, layoutId);
//        int i = R.layout.item_blank_list;
    }
}
