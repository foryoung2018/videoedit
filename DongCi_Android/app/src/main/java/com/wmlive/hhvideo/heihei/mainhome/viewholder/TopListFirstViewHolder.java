package com.wmlive.hhvideo.heihei.mainhome.viewholder;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wmlive.hhvideo.widget.CustomFontTextView;
import com.wmlive.hhvideo.widget.RatioLayout;
import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 5/31/2018 - 11:46 AM
 * 类描述：
 */
public class TopListFirstViewHolder extends BaseRecyclerViewHolder {
    @BindView(R.id.tvName)
    public CustomFontTextView tvName;
    @BindView(R.id.ivPic)
    public ImageView ivPic;
    @BindView(R.id.ivLevel)
    public ImageView ivLevel;
    @BindView(R.id.tvPeoples)
    public TextView tvPeoples;
    @BindView(R.id.rlPlayerContainer)
    public RatioLayout rlPlayerContainer;
    @BindView(R.id.ivShadow)
    public ImageView ivShadow;
    @BindView(R.id.tvTopic)
    public CustomFontTextView tvTopic;
    @BindView(R.id.tvLikeCount)
    public TextView tvLikeCount;
    @BindView(R.id.tvCommentCount)
    public TextView tvCommentCount;
    @BindView(R.id.ivJoin)
    public ImageView ivJoin;
    @BindView(R.id.topTitle)
    public CustomFontTextView topTitle;

    public TopListFirstViewHolder(ViewGroup parent, int layoutId) {
        super(parent, layoutId);
//        int i = R.layout.item_top_list_first;
    }
}
