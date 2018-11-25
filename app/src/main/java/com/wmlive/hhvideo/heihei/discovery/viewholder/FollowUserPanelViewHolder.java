package com.wmlive.hhvideo.heihei.discovery.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 9/20/2017.
 */

public class FollowUserPanelViewHolder extends BaseRecyclerViewHolder {

    @BindView(R.id.tvMore)
    public ImageView tvMore;
    @BindView(R.id.rvFollows)
    public RecyclerView rvFollows;

    public FollowUserPanelViewHolder(ViewGroup parent, int layoutId) {
        super(parent, layoutId);
    }
}
