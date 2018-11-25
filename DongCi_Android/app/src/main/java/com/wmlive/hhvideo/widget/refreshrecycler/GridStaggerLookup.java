package com.wmlive.hhvideo.widget.refreshrecycler;


import android.support.v7.widget.GridLayoutManager;

/**
 * 该类用于生成交错效果，要么 1 行 2 列，要么 1 行 1 列
 */
public class GridStaggerLookup extends GridLayoutManager.SpanSizeLookup {
    int size = 0;
    private RefreshRecyclerView refreshRecycleView;

    public GridStaggerLookup(RefreshRecyclerView view, int size) {
        refreshRecycleView = view;
        this.size = size;
    }

    @Override
    public int getSpanSize(int position) {

//        // 每隔 3 个位置占据 2 列，其他位置则占 1 列
//        int pos = position % 3 == 0 ? 2 : 1;
//        return pos;
        if (position == 1 && refreshRecycleView.getHeader() != null) {
            return 2;
        }
        return position < (size + (refreshRecycleView.getHeader() == null ? 0 : 1)) ? 2 : 1;
    }

    public interface LayoutListenr {
        int onLayout();
    }
}