package com.wmlive.hhvideo.heihei.mainhome.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 3/8/2018.2:39 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class MessageBannerViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.ivBanner)
    public ImageView ivBanner;
    @BindView(R.id.llBannerRoot)
    public LinearLayout llBannerRoot;
    @BindView(R.id.llEmpty)
    public LinearLayout llEmpty;

    public MessageBannerViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
//        int i = R.layout.item_message_banner;
    }
}
