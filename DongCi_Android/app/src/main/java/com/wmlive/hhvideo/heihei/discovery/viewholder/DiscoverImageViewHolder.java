package com.wmlive.hhvideo.heihei.discovery.viewholder;

import android.graphics.drawable.Animatable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wmlive.hhvideo.widget.RatioLayout;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 8/16/2017.
 */

public class DiscoverImageViewHolder extends RecyclerView.ViewHolder {
    public RatioLayout rlCover;
    public SimpleDraweeView ivCover;
    public View viewMore;

    public DiscoverImageViewHolder(View itemView) {
        super(itemView);
        ivCover = itemView.findViewById(R.id.ivCover);
        rlCover = itemView.findViewById(R.id.rlCover);
        viewMore = itemView.findViewById(R.id.viewMore);
    }

    public void showAnimImage(boolean show) {
        if (ivCover.getController() != null) {
            Animatable animatable = ivCover.getController().getAnimatable();
            if (animatable != null) {
                if (show) {
                    animatable.start();
                } else if (animatable.isRunning()) {
                    animatable.stop();
                }
            }
        }
    }
}