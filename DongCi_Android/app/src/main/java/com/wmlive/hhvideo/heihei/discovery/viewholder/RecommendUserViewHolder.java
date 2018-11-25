package com.wmlive.hhvideo.heihei.discovery.viewholder;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 9/20/2017.
 */

public class RecommendUserViewHolder extends BaseRecyclerViewHolder {
    @BindView(R.id.ivAvatar)
    public ImageView ivAvatar;
    @BindView(R.id.tvNickname)
    public TextView tvNickname;
    @BindView(R.id.tvDcId)
    public TextView tvDcId;
    @BindView(R.id.ivFollow)
    public ImageView ivFollow;
    @BindView(R.id.ivVerifyIcon)
    public ImageView ivVerifyIcon;
    @BindView(R.id.tvFollow)
    public TextView tvFollow;

    public RecommendUserViewHolder(ViewGroup parent, int layoutId) {
        super(parent, layoutId);
    }
}
