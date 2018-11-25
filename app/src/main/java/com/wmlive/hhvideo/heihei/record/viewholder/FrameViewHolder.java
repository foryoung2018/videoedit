package com.wmlive.hhvideo.heihei.record.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wmlive.hhvideo.heihei.record.widget.CustomFrameView;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 8/28/2017.
 */

public class FrameViewHolder extends RecyclerView.ViewHolder {
    public CustomFrameView customFrameView;
    public RelativeLayout rlFrameSelect;
    public ImageView ivSelcect;

    public FrameViewHolder(View itemView) {
        super(itemView);
        customFrameView = (CustomFrameView) itemView.findViewById(R.id.customFrameView);
        rlFrameSelect = (RelativeLayout) itemView.findViewById(R.id.rlFrameSelect);
        ivSelcect = (ImageView) itemView.findViewById(R.id.iv_select);
    }
}
