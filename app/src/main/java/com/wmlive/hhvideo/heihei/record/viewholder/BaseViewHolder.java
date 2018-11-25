package com.wmlive.hhvideo.heihei.record.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by wenlu on 2017/9/6.
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {

    public int viewType;
    public View itemView;

    public BaseViewHolder(View itemView, int viewType) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.itemView = itemView;
        this.viewType = viewType;
    }

    public Context getContext() {
        return itemView.getContext();
    }

}
