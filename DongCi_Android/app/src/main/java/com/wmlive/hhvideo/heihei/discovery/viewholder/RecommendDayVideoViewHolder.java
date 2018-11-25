package com.wmlive.hhvideo.heihei.discovery.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 5/28/2018 - 4:34 PM
 * 类描述：
 */
public class RecommendDayVideoViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.ivPic)
    public ImageView ivPic;
    @BindView(R.id.ivAvatar)
    public ImageView ivAvatar;
    @BindView(R.id.tvName)
    public TextView tvName;
    @BindView(R.id.tvTopic)
    public TextView tvTopic;
    @BindView(R.id.tvLikeCount)
    public TextView tvLikeCount;
    @BindView(R.id.tvCommentCount)
    public TextView tvCommentCount;
    @BindView(R.id.ivJoin)
    public ImageView ivJoin;
    @BindView(R.id.ivShadow)
    public ImageView ivShadow;

    public RecommendDayVideoViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
//        int i = R.layout.item_recommend_day_video;
    }
}
