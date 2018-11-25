package com.wmlive.hhvideo.heihei.discovery.viewholder;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 5/31/2017.
 * 搜索用户结果的item
 */

public class SearchUserViewHolder extends BaseRecyclerViewHolder {
    @BindView(R.id.ivAvatar)
    public ImageView ivAvatar;
    @BindView(R.id.tvName)
    public TextView tvName;
    @BindView(R.id.tvDesc)
    public TextView tvDesc;
    @BindView(R.id.tvCount)
    public TextView tvCount;
    @BindView(R.id.ivVerifyIcon)
    public ImageView ivVerifyIcon;

    public SearchUserViewHolder(ViewGroup parent, int layoutId) {
        super(parent, layoutId);
    }
}
