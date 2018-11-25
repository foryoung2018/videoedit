package com.wmlive.hhvideo.widget.refreshrecycler;

import android.view.View;

/**
 * Created by Administrator on 10/20/2016.
 */

public interface OnRecyclerItemClickListener<T> {
    /**
     * @param dataPosition 这个position是DataList的position，不是ItemView的position!!!
     * @param view
     * @param data
     */
    void onRecyclerItemClick(int dataPosition, View view, T data);
}