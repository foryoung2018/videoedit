package com.wmlive.hhvideo.heihei.discovery.viewholder;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 6/1/2017.
 * 话题的头部
 */

public class TopicHeaderViewHolder extends BaseRecyclerViewHolder {
    @BindView(R.id.ivAvatar)
    public ImageView ivAvatar;
    @BindView(R.id.tvJoin)
    public TextView tvJoin;
    @BindView(R.id.tvDesc)
    public TextView tvDesc;

    public TopicHeaderViewHolder(ViewGroup parent, int layoutId) {
        super(parent, layoutId);
    }
}
