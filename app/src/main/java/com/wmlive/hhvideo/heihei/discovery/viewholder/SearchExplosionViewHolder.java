package com.wmlive.hhvideo.heihei.discovery.viewholder;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 6/1/2017.
 * 搜索话题结果的item
 */

public class SearchExplosionViewHolder extends BaseRecyclerViewHolder {
    @BindView(R.id.ivType)
    public ImageView ivType;
    @BindView(R.id.tvTitle)
    public TextView tvTitle;
    @BindView(R.id.tvCount)
    public TextView tvCount;

    public SearchExplosionViewHolder(ViewGroup parent, int layoutId) {
        super(parent, layoutId);
    }
}
