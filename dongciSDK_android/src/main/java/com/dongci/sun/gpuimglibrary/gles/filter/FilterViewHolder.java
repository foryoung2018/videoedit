package com.dongci.sun.gpuimglibrary.gles.filter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.dongci.sun.gpuimglibrary.R;
import com.dongci.sun.gpuimglibrary.common.ExtCircleImageView;


/**
 * Created by lsq on 8/28/2017.
 * 滤镜选择面板的item
 */

public class FilterViewHolder extends RecyclerView.ViewHolder {
    public ExtCircleImageView ivFilter;
    public TextView tvTitle;

    public FilterViewHolder(View itemView) {
        super(itemView);
        ivFilter = (ExtCircleImageView) itemView.findViewById(R.id.ivFilter);
        tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
    }
}
