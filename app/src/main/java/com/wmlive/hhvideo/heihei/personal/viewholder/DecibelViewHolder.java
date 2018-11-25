package com.wmlive.hhvideo.heihei.personal.viewholder;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.widget.CustomFontTextView;
import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 1/10/2018.11:07 AM
 *
 * @author lsq
 * @describe 添加描述
 */

public class DecibelViewHolder extends BaseRecyclerViewHolder {
    @BindView(R.id.tvDecibelCount)
    public TextView tvDecibelCount;
    @BindView(R.id.rlDecibelCount)
    public RelativeLayout rlDecibelCount;
    @BindView(R.id.ivAvatar)
    public ImageView ivAvatar;
    @BindView(R.id.tvNickname)
    public TextView tvNickname;
    @BindView(R.id.tvDesc)
    public CustomFontTextView tvDesc;
    @BindView(R.id.ivRank)
    public ImageView ivRank;
    @BindView(R.id.ivVerifyIcon)
    public ImageView ivVerifyIcon;

    public DecibelViewHolder(ViewGroup parent, int layoutId) {
        super(parent, layoutId);
    }
}
