package com.wmlive.hhvideo.heihei.mainhome.viewholder;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.widget.CustomFontTextView;
import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 5/28/2018 - 2:49 PM
 * 类描述：
 */
public class DiscoverMessageViewHolder extends BaseRecyclerViewHolder {
    @BindView(R.id.tvTitle)
    public CustomFontTextView tvTitle;
    @BindView(R.id.tvEntry)
    public TextView tvEntry;
    @BindView(R.id.tvDesc)
    public TextView tvDesc;
    @BindView(R.id.ivPic)
    public ImageView ivPic;
    @BindView(R.id.llDesc)
    public LinearLayout llDesc;
    @BindView(R.id.tvTime)
    public TextView tvTime;

    public DiscoverMessageViewHolder(ViewGroup parent, int layoutId) {
        super(parent, layoutId);
//        int i = R.layout.item_discover_message;
    }
}
