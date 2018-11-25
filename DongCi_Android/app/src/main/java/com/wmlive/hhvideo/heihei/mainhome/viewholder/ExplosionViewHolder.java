package com.wmlive.hhvideo.heihei.mainhome.viewholder;

import android.graphics.drawable.Animatable;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 8/16/2017.
 */

public class ExplosionViewHolder extends BaseRecyclerViewHolder {

    @BindView(R.id.iv_splash)
    public SimpleDraweeView ivSplash;
    @BindView(R.id.tv_play_count)
    public TextView tvPlayCount;
    @BindView(R.id.tv_love_count)
    public TextView tvLoveCount;
    @BindView(R.id.tv_music_name)
    public TextView tvMusicName;

    public ExplosionViewHolder(ViewGroup parent, int layoutId) {
        super(parent, layoutId);
    }

    public void showAnimImage(boolean show) {
        if (ivSplash.getController() != null) {
            Animatable animatable = ivSplash.getController().getAnimatable();
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