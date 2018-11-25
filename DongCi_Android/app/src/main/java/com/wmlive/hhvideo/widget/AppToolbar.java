package com.wmlive.hhvideo.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wmlive.hhvideo.utils.DeviceUtils;

import cn.wmlive.hhvideo.R;

/**
 * 这是APP的toolbar，可在toolbar左侧和中间添加View
 * Created by lsq on 12/12/2016.
 */

public class AppToolbar extends Toolbar {
    private TextView centerTitle;
    private int titleSize = 22;
    private int titleColor = Color.WHITE;

    public AppToolbar(Context context) {
        this(context, null);
    }

    public AppToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        titleColor = context.getResources().getColor(R.color.colorToolbarTitleText);
        titleSize = DeviceUtils.px2dip(context, context.getResources().getDimensionPixelOffset(R.dimen.text_size_title));
    }

    /**
     * 在toolbar中间添加布局
     *
     * @param layoutId
     */
    public View addCenterView(int layoutId, OnClickListener listener) {
        return addView(LayoutInflater.from(getContext()).inflate(layoutId, null), Gravity.CENTER, listener);
    }

    /**
     * 在toolbar中间添加布局
     *
     * @param layoutId
     */
    public View addCenterView(int layoutId, OnClickListener listener, int width) {
        if (centerTitle != null) {
            centerTitle.setVisibility(GONE);
        }
        return addView(LayoutInflater.from(getContext()).inflate(layoutId, null), Gravity.CENTER, listener, width);
    }

    /**
     * 在toolbar中间添加布局
     */
    public View addCenterView(View view, OnClickListener listener) {
        if (centerTitle != null) {
            centerTitle.setVisibility(GONE);
        }
        return addView(view, Gravity.CENTER, listener);
    }

    /**
     * 在toolbar左侧添加布局
     */
    public View addLeftView(@LayoutRes int layoutId, OnClickListener listener) {
        return addView(LayoutInflater.from(getContext()).inflate(layoutId, null), Gravity.LEFT, listener);
    }

    /**
     * 在toolbar左侧添加布局
     */
    public View addLeftView(View view, OnClickListener listener) {
        return addView(view, Gravity.LEFT, listener);
    }

    /**
     * @deprecated 在toolbar右侧添加布局，建议使用menu的方式添加
     */
    public View addRightView(@LayoutRes int layoutId, OnClickListener listener) {
        return addView(LayoutInflater.from(getContext()).inflate(layoutId, null), Gravity.RIGHT, listener);
    }

    /**
     * @deprecated 在toolbar右侧添加布局，建议使用menu的方式添加
     */
    public View addRightView(View view, OnClickListener listener) {
        return addView(view, Gravity.RIGHT, listener);
    }

    private View addView(View view, int gravity, OnClickListener listener) {
        return addView(view, gravity, listener, LayoutParams.WRAP_CONTENT);
    }

    private View addView(View view, int gravity, OnClickListener listener, int width) {
        LayoutParams layoutParams = new LayoutParams(width, LayoutParams.MATCH_PARENT, gravity);
        addView(view, layoutParams);
        if (view != null && listener != null) {
            if (view instanceof ViewGroup) {    //这个地方的View层级最多两层!!!
                ViewGroup viewGroup = (ViewGroup) view;
                int size = viewGroup.getChildCount();
                if (size > 0) {
                    for (int i = 0; i < size; i++) {
                        viewGroup.getChildAt(i).setOnClickListener(listener);
                    }
                }
            } else {
                view.setOnClickListener(listener);
            }
        }
        return view;
    }

    /**
     * 设置居中的Title
     *
     * @param strId
     */
    public void setCenterTitle(int strId) {
        setCenterTitle(getResources().getString(strId));
    }

    /**
     * 设置居中的Title
     */
    public void setCenterTitle(String title) {
        if (centerTitle == null) {
            centerTitle = new TextView(this.getContext());
            centerTitle.setTextColor(titleColor);
            centerTitle.setTextSize(titleSize);
            centerTitle.setGravity(Gravity.CENTER);
            centerTitle.setMaxEms(12);
            centerTitle.setMaxLines(1);
            centerTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            // centerTitle.setHorizontallyScrolling(true);
            centerTitle.setFocusableInTouchMode(true);
//            TextPaint tp = centerTitle.getPaint();
            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, Gravity.CENTER);
            addView(centerTitle, layoutParams);
        }
        centerTitle.setText(title);
    }

    /**
     * 设置中间title的文字颜色
     *
     * @param colorId
     */
    public void setCenterTitleColor(@ColorInt int colorId) {
        if (centerTitle != null) {
            centerTitle.setTextColor(colorId);
        }
    }

    /**
     * 设置中间title的文字大小
     *
     * @param size
     */
    public void setCenterTitleSize(int size) {
        if (centerTitle != null) {
            centerTitle.setTextSize(size);
        }
    }

    public TextView getCenterTitleView() {
        return centerTitle;
    }

    /**
     * 设置Toolbar的背景颜色
     *
     * @param color
     */
    public void setAllBackgroundColor(@ColorInt int color) {
        if (centerTitle != null) {
            centerTitle.setBackgroundColor(Color.TRANSPARENT);
        }
        this.setBackgroundColor(color);
    }
}
