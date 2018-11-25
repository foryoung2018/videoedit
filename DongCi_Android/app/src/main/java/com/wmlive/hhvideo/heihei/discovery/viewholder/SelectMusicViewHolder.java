package com.wmlive.hhvideo.heihei.discovery.viewholder;

import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 6/2/2017.
 * 搜索完音乐后选择音乐的item
 */

public class SelectMusicViewHolder extends BaseRecyclerViewHolder {
    @BindView(R.id.ivCover)
    public ImageView ivCover;
    @BindView(R.id.tvName)
    public TextView tvName;
    @BindView(R.id.tvArtist)
    public TextView tvArtist;
    @BindView(R.id.tvDuring)
    public TextView tvDuring;
    @BindView(R.id.ivCollect)
    public ImageView ivCollect;
    @BindView(R.id.ivPlay)
    public ImageView ivPlay;
    @BindView(R.id.rlUseMusic)
    public RelativeLayout rlUseMusic;
    @BindView(R.id.tvUseMusic)
    public TextView tvUseMusic;
    private Animation rotateAnimation;

    public SelectMusicViewHolder(ViewGroup parent, int layoutId) {
        super(parent, layoutId);
    }

    public void setPlayIcon(int status) {
        switch (status) {
            case 0: //暂停
                ivPlay.setImageResource(R.drawable.icon_search_music_play);
                if (null != rotateAnimation) {
                    ivPlay.clearAnimation();
                }
                break;
            case 1://播放
                ivPlay.setImageResource(R.drawable.icon_search_music_pause);
                if (null != rotateAnimation) {
                    ivPlay.clearAnimation();
                }
                break;
            case 2://缓冲
                ivPlay.setImageResource(R.drawable.icon_search_music_load);
                if (null == rotateAnimation) {
                    rotateAnimation = AnimationUtils.loadAnimation(getRootView().getContext(), R.anim.anim_cd_rotate);
                    rotateAnimation.setInterpolator(new LinearInterpolator(getRootView().getContext(), null));
                }
                ivPlay.startAnimation(rotateAnimation);
                break;
        }
    }

    public void clearAnimation() {
        if (null != ivPlay && null != rotateAnimation) {
            ivPlay.clearAnimation();
            KLog.i("=============释放动画");
            rotateAnimation = null;
        }
    }

}
