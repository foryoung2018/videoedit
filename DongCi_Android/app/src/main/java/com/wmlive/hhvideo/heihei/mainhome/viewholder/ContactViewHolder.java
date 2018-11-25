package com.wmlive.hhvideo.heihei.mainhome.viewholder;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by Administrator on 3/15/2018.
 */

public class ContactViewHolder extends BaseRecyclerViewHolder {
    @BindView(R.id.ivVerifyIcon)
    public ImageView ivVerifyIcon;
    @BindView(R.id.tvFocusLabel)
    public TextView tvFocusLabel;
    @BindView(R.id.ivAvatar)
    public ImageView ivAvatar;
    @BindView(R.id.tvName)
    public TextView tvName;

    public ContactViewHolder(ViewGroup parent, int layoutId) {
        super(parent, layoutId);
        int i = R.layout.item_contact;
    }
}
