package com.wmlive.hhvideo.heihei.mainhome.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.widget.BadgeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 2/8/2018.2:56 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class MessageViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.badgeView)
    public BadgeView badgeView;
    @BindView(R.id.rlMessageRoot)
    public RelativeLayout rlMessageRoot;
    @BindView(R.id.ivAvatar)
    public ImageView ivAvatar;
    @BindView(R.id.rlAvatar)
    public RelativeLayout rlAvatar;
    @BindView(R.id.tvName)
    public TextView tvName;
    @BindView(R.id.tvAction)
    public TextView tvAction;
    @BindView(R.id.tvDesc)
    public TextView tvDesc;
    @BindView(R.id.llMessageContent)
    public LinearLayout llMessageContent;
    @BindView(R.id.tvRightTime)
    public TextView tvRightTime;
    @BindView(R.id.tvBottomTime)
    public TextView tvBottomTime;
    @BindView(R.id.ivVideoThumb)
    public ImageView ivVideoThumb;
    @BindView(R.id.ivFollow)
    public ImageView ivFollow;
    @BindView(R.id.tvFollow)
    public TextView tvFollow;
    @BindView(R.id.ivTypeIcon)
    public ImageView ivTypeIcon;
    @BindView(R.id.ivImGift)
    public ImageView ivImGift;
    @BindView(R.id.ivStatus)
    public ImageView ivStatus;
    @BindView(R.id.ivVerifyIcon)
    public ImageView ivVerifyIcon;

    public MessageViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
