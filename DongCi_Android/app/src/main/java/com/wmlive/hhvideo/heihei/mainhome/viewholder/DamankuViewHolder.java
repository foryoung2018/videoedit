package com.wmlive.hhvideo.heihei.mainhome.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.widget.CircleImageView;

import cn.wmlive.hhvideo.R;
import master.flame.danmaku.danmaku.model.android.ViewCacheStuffer;

/**
 * Created by lsq on 8/1/2017.
 * 弹幕的ViewHolder
 */

public class DamankuViewHolder extends ViewCacheStuffer.ViewHolder {

    public LinearLayout llGiftPanel;
    public CircleImageView ivAvatar;
    public TextView tvName;
    public TextView tvGift;
    public ImageView ivGift;
    public TextView tvGiftCount;
    public TextView tvContent;

    public LinearLayout llGiftPanel2;
    public CircleImageView ivAvatar2;
    public TextView tvName2;
    public TextView tvGift2;
    public TextView tvHitCount2;

    public DamankuViewHolder(View itemView) {
        super(itemView);
        llGiftPanel = (LinearLayout) itemView.findViewById(R.id.llGiftPanel);
        ivAvatar = (CircleImageView) itemView.findViewById(R.id.ivAvatar);
        tvName = (TextView) itemView.findViewById(R.id.tvName);
        tvGift = (TextView) itemView.findViewById(R.id.tvGift);
        ivGift = (ImageView) itemView.findViewById(R.id.ivGift);
        tvGiftCount = (TextView) itemView.findViewById(R.id.tvGiftCount);
        tvContent = (TextView) itemView.findViewById(R.id.tvContent);


        llGiftPanel2 = (LinearLayout) itemView.findViewById(R.id.llGiftPanel2);
        ivAvatar2 = (CircleImageView) itemView.findViewById(R.id.ivAvatar2);
        tvName2 = (TextView) itemView.findViewById(R.id.tvName2);
        tvGift2 = (TextView) itemView.findViewById(R.id.tvGift2);
        tvHitCount2 = (TextView) itemView.findViewById(R.id.tvHitCount2);
    }
}
