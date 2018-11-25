package com.wmlive.hhvideo.heihei.discovery.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 5/28/2018 - 4:26 PM
 * 类描述：
 */
public class RecommendVideoViewHolder extends BaseRecyclerViewHolder {
    @BindView(R.id.flRecycler)
    public RecyclerView flRecycler;

    public RecommendVideoViewHolder(ViewGroup parent, int layoutId) {
        super(parent, layoutId);
//        int i = R.layout.item_recomend_video;
    }
}
