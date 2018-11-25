package com.wmlive.hhvideo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.utils.DeviceUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hsing on 2018/4/18.
 */

public class AutoLineFeedView extends ViewGroup {
    private int viewMargin = 0;
    private int deviceWidth = DeviceUtils.getScreenWH(DCApplication.getDCApp())[0];
    private Map<String, View> viewMap = new HashMap<>();
    private int maxRow = -1;//最大行数
    private int rowCount = 1;

    public AutoLineFeedView(Context context) {
        super(context);
    }

    public AutoLineFeedView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 添加view
     *
     * @param childView
     * @param callback
     */
    public void addView(View childView, final OnChildClickListener callback) {
        childView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                if (callback != null) {
                    callback.onClick(view);
                }
            }
        });
        addView(childView);
//        viewMap.put(childView.getTag().toString(), childView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;// 整个group高度
        int totalWidth = 0;// 新增加的row的当前宽度
        int row = 1;
        for (int index = 0; index < getChildCount(); index++) {
            final View child = getChildAt(index);
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            totalWidth = totalWidth + child.getMeasuredWidth() + viewMargin;
            if (totalWidth > deviceWidth) {
                row++;
                totalWidth = child.getMeasuredWidth() + viewMargin;
            }
        }
        if (getChildCount() == 0) {
            height = 0;
        } else {
            if (maxRow == -1) {
                height = row * (getChildAt(0).getMeasuredHeight() + viewMargin) + viewMargin;
            } else {
                height = maxRow * (getChildAt(0).getMeasuredHeight() + viewMargin) + viewMargin;
            }
        }
        setMeasuredDimension(widthMeasureSpec, height);
    }

    @Override
    protected void onLayout(boolean arg0, int left, int top, int right, int bottom) {
        final int count = getChildCount();
        int row = 0;
        int lengthX = left;
        int lengthY = 0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            lengthX += width + viewMargin;
            lengthY = row * (height + viewMargin) + viewMargin + height;
            if (lengthX > right) {
                lengthX = width + viewMargin + left;
                row++;
                rowCount++;
                lengthY = row * (height + viewMargin) + viewMargin + height;
            }
            child.layout(lengthX - width - left, lengthY - height, lengthX, lengthY);
        }
    }

    public interface OnChildClickListener {
        void onClick(View view);
    }

    public void setMaxRow(int maxRow) {
        this.maxRow = maxRow;
    }

    public void setViewMargin(int margin) {
        this.viewMargin = margin;
    }

    public int getRowCount() {
        return rowCount;
    }

}
