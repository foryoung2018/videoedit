package com.wmlive.hhvideo.heihei.mainhome.viewholder;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wmlive.hhvideo.widget.CustomFontTextView;
import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 5/28/2018 - 11:32 AM
 * 类描述：
 */
public class TopListViewHolder extends BaseRecyclerViewHolder {

    @BindView(R.id.tvLevel)
    public CustomFontTextView tvLevel;
    @BindView(R.id.ivPic)
    public ImageView ivPic;
    @BindView(R.id.tvName)
    public CustomFontTextView tvName;
    @BindView(R.id.tvTopic)
    public TextView tvTopic;
    @BindView(R.id.tvLikeCount)
    public TextView tvLikeCount;
    @BindView(R.id.tvCommentCount)
    public TextView tvCommentCount;
    @BindView(R.id.tvPeoples)
    public TextView tvPeoples;
    @BindView(R.id.ivJoin)
    public ImageView ivJoin;

    public TopListViewHolder(ViewGroup parent, int layoutId) {
        super(parent, layoutId);
//        int i = R.layout.item_top_list;
    }
}
