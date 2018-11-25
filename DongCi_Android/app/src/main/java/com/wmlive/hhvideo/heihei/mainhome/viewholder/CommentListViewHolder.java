package com.wmlive.hhvideo.heihei.mainhome.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 6/19/2017.
 * 评论列表中的item
 */

public class CommentListViewHolder extends BaseRecyclerViewHolder {
    @BindView(R.id.ivAvatar)
    public ImageView ivAvatar;
    @BindView(R.id.tvName)
    public TextView tvName;
    @BindView(R.id.tvComment)
    public TextView tvComment;
    @BindView(R.id.llStarts)
    public LinearLayout llStarts;
    @BindView(R.id.tvStartCount)
    public TextView tvStartCount;
    @BindView(R.id.ivStarts)
    public ImageView ivStarts;
    @BindView(R.id.viewTopLine)
    public View viewTopLine;
    @BindView(R.id.ivVerifyIcon)
    public ImageView ivVerifyIcon;

    public CommentListViewHolder(ViewGroup parent, int layoutId) {
        super(parent, layoutId);
    }
}
