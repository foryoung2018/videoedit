package com.wmlive.hhvideo.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by lsq on 11/23/2017.8:07 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class RecyclerViewHelper {

    private OrientationHelper mVerticalHelper;
    private OrientationHelper mHorizontalHelper;
    private RecyclerView.LayoutManager layoutManager;

    public RecyclerViewHelper(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    public int findSnapViewPosition() {
        if (layoutManager instanceof LinearLayoutManager) {
            if (((LinearLayoutManager) layoutManager).getOrientation() == LinearLayoutManager.VERTICAL) {
                return findCenterViewPosition(layoutManager, getVerticalHelper(layoutManager));
            } else if (((LinearLayoutManager) layoutManager).getOrientation() == LinearLayoutManager.HORIZONTAL) {
                return findCenterViewPosition(layoutManager, getHorizontalHelper(layoutManager));
            }
        }
        return -1;
    }

    private int findCenterViewPosition(RecyclerView.LayoutManager layoutManager,
                                       OrientationHelper helper) {
        int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return -1;
        }

        View closestChild = null;
        final int center;
        if (layoutManager.getClipToPadding()) {
            center = helper.getStartAfterPadding() + helper.getTotalSpace() / 2;
        } else {
            center = helper.getEnd() / 2;
        }
        int absClosest = Integer.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < childCount; i++) {
            final View child = layoutManager.getChildAt(i);
            int childCenter = helper.getDecoratedStart(child)
                    + (helper.getDecoratedMeasurement(child) / 2);
            int absDistance = Math.abs(childCenter - center);

            if (absDistance < absClosest) {
                absClosest = absDistance;
//                closestChild = child;
                index = i;
            }
        }

        if (layoutManager instanceof LinearLayoutManager) {
            int firstP = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            return firstP + index;
        }
//        return closestChild;
        return index;
    }

    private OrientationHelper getVerticalHelper(@NonNull RecyclerView.LayoutManager layoutManager) {
        if (mVerticalHelper == null) {
            mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager);
        }
        return mVerticalHelper;
    }

    @NonNull
    private OrientationHelper getHorizontalHelper(
            @NonNull RecyclerView.LayoutManager layoutManager) {
        if (mHorizontalHelper == null) {
            mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
        }
        return mHorizontalHelper;
    }
}
