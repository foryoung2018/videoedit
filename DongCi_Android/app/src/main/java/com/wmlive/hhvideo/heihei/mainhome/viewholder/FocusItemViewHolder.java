package com.wmlive.hhvideo.heihei.mainhome.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wmlive.hhvideo.R;

public class FocusItemViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.singer_name_tv)
    public TextView singerNameTv;
    @BindView(R.id.song_name_tv)
    public TextView songNameTv;
    @BindView(R.id.singer_cover_iv)
    public ImageView singerCoverIv;
    public FocusItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
