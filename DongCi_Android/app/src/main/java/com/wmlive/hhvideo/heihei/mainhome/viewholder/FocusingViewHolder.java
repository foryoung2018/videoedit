package com.wmlive.hhvideo.heihei.mainhome.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

public class FocusingViewHolder extends BaseRecyclerViewHolder {
    @BindView(R.id.focusSingerRecycler)
    public RecyclerView focusSingerRecycler;
    public FocusingViewHolder(ViewGroup parent, int layoutId) {
        super(parent, layoutId);
    }
}
