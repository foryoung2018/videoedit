package com.wmlive.hhvideo.widget.refreshrecycler;

import android.view.View;

/**
 * Created by lsq on 1/6/2017.
 */

public interface OnRecyclerItemLongClickListener<T> {
    /**
     * @param dataPosition 这个position是DataList的position，不是ItemView的position!!!
     * @param view
     * @param data
     */
    void onRecyclerItemLongClick(int dataPosition, View view, T data);
}
