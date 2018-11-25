package com.wmlive.hhvideo.widget.refreshrecycler;

/**
 * Created by Administrator on 10/19/2016.
 */

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.wmlive.hhvideo.utils.DeviceUtils;


/**
 * 为每个子项提供页边距
 */
public class InsetDecoration extends RecyclerView.ItemDecoration {

    private int mInsetMargin = 2;

    public InsetDecoration(Context context) {
        this(context, 2);
    }

    public InsetDecoration(Context context, int margin) {
        super();
        mInsetMargin = DeviceUtils.dip2px(context, margin);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        // 对子视图的所有 4 个边界应用计算得出的页边距
        outRect.set(mInsetMargin, mInsetMargin, mInsetMargin, mInsetMargin);
    }
}