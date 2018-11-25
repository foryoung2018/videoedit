package com.wmlive.hhvideo.heihei.personal.util;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.wmlive.hhvideo.utils.DeviceUtils;

/**
 * Created by XueFei on 2017/6/6.
 * <p>
 * 一行 3列 距上 距左 space距离
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private boolean mIsHaveHeadView = false;//是否有headview

    public SpaceItemDecoration(Context context, int space, boolean isHaveHeadView) {
        this.space = DeviceUtils.dip2px(context, space);
        mIsHaveHeadView = isHaveHeadView;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int index = parent.getChildAdapterPosition(view);
        boolean isSingular = index % 2 != 0;
        outRect.bottom = space;
        if (mIsHaveHeadView) {
            outRect.left = index == 0 ? 0 : (isSingular ? space : (int) (space * 0.5f));
            outRect.right = index == 0 ? 0 : (isSingular ? (int) (space * 0.5f) : space);
            outRect.top = index == 0 ? 0 : (index > 2 ? 0 : space);
        } else {
            outRect.left = isSingular ? (int) (space * 0.5f) : space;
            outRect.right = isSingular ? space : (int) (space * 0.5f);
            outRect.top = index > 1 ? 0 : space;
        }
    }
}
