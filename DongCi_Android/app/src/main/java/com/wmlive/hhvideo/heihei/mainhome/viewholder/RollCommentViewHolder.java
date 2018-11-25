package com.wmlive.hhvideo.heihei.mainhome.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 5/10/2018 - 6:40 PM
 * 类描述：
 */
public class RollCommentViewHolder extends RecyclerView.ViewHolder {
    public TextView tvComment;
    public RollCommentViewHolder(View itemView) {
        super(itemView);
        tvComment = itemView.findViewById(R.id.tvComment);
//        int i= R.layout.item_roll_comment;
    }
}
