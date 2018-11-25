package com.wmlive.hhvideo.heihei.record.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.wmlive.hhvideo.heihei.record.widget.ExtCircleImageView;
import com.wmlive.hhvideo.widget.CustomFontTextView;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 8/28/2017.
 * 滤镜选择面板的item
 */

public class FilterViewHolder extends RecyclerView.ViewHolder {
    public CustomFontTextView tvTitle;

    public FilterViewHolder(View itemView) {
        super(itemView);
        tvTitle = (CustomFontTextView) itemView.findViewById(R.id.tvTitle);
    }
}
