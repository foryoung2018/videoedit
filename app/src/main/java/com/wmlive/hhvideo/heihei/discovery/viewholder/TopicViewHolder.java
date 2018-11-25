package com.wmlive.hhvideo.heihei.discovery.viewholder;

import android.graphics.drawable.Animatable;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 6/1/2017.
 */

public class TopicViewHolder extends BaseRecyclerViewHolder {
    //    @BindView(R.id.rlItem)
//    public RatioLayout rlItem;
    @BindView(R.id.ivCover)
    public SimpleDraweeView ivCover;
    @BindView(R.id.ivTag)
    public ImageView ivTag;
    @BindView(R.id.tvCount)
    public TextView tvCount;

    public TopicViewHolder(ViewGroup parent, int layoutId) {
        super(parent, layoutId);
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
