package com.wmlive.hhvideo.heihei.discovery.viewholder;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wmlive.hhvideo.widget.CustomFontTextView;
import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 9/18/2017.
 */

public class FollowUserViewHolder extends BaseRecyclerViewHolder {
    @BindView(R.id.ivAvatar)
    public ImageView ivAvatar;
    @BindView(R.id.tvNickname)
    public TextView tvNickname;
    @BindView(R.id.tvFollow)
    public CustomFontTextView tvFollow;
    @BindView(R.id.ivVerifyIcon)
    public ImageView ivVerifyIcon;

    public FollowUserViewHolder(ViewGroup parent, int layoutId) {
        super(parent, layoutId);
    }
}
