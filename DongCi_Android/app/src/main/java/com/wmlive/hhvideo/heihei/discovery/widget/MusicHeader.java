package com.wmlive.hhvideo.heihei.discovery.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.wmlive.hhvideo.heihei.beans.discovery.MusicInfoBean;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;
import com.wmlive.hhvideo.utils.imageloader.LoadCallback;
import com.wmlive.hhvideo.widget.BaseCustomView;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 6/6/2017.
 * 话题的头部
 */

public class MusicHeader extends BaseCustomView {
    @BindView(R.id.ivCover)
    ImageView ivCover;
    @BindView(R.id.ivCoverTop)
    ImageView ivCoverTop;
    @BindView(R.id.ivBackCover)
    ImageView ivBackCover;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvAlbum)
    TextView tvAlbum;
    @BindView(R.id.tvUse)
    TextView tvUse;
    @BindView(R.id.flUse)
    FrameLayout flUse;
    private OnMusicUseListener onMusicUseListener;
    private MusicInfoBean musicInfoBean;

    public MusicHeader(Context context) {
        super(context);
    }

    public MusicHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {
        flUse.setOnClickListener(this);
    }

    public MusicHeader setData(MusicInfoBean bean) {
        if (bean != null) {
            musicInfoBean = bean;
            ivCoverTop.setVisibility(GONE);
            GlideLoader.loadImage(musicInfoBean.getAlbum_cover(), ivBackCover);
            GlideLoader.loadCircleImage(musicInfoBean.getAlbum_cover(), ivCover, R.drawable.bg_search_music_default, new LoadCallback() {
                @Override
                public void onDrawableLoaded(Drawable drawable) {
                    ivCoverTop.setVisibility(VISIBLE);
                }
            });
            tvName.setText(musicInfoBean.getName());
            tvAlbum.setText(musicInfoBean.getSinger_name());
        }
        return this;
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.flUse:
                if (musicInfoBean != null && null != onMusicUseListener) {
                    onMusicUseListener.onUseMusicClick(musicInfoBean);
                }
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_music_header;
    }

    public void setOnMusicUseListener(OnMusicUseListener onMusicUseListener) {
        this.onMusicUseListener = onMusicUseListener;
    }

    public interface OnMusicUseListener {
        void onUseMusicClick(MusicInfoBean bean);
    }
}
