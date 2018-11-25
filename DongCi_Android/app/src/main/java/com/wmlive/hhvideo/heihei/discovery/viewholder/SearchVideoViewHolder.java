package com.wmlive.hhvideo.heihei.discovery.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 6/13/2017.
 */

public class SearchVideoViewHolder extends RecyclerView.ViewHolder {
    public ImageView ivCover;
    public TextView tvDuring;

    public SearchVideoViewHolder(View itemView) {
        super(itemView);
        ivCover = (ImageView) itemView.findViewById(R.id.ivCover);
        tvDuring = (TextView) itemView.findViewById(R.id.tvDuring);
    }
}
