package com.wmlive.hhvideo.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.utils.KLog;

import butterknife.ButterKnife;

/**
 * 自定义组合控件的基类
 * 只需要实现两个参数的构造方法，有自定义属性的时候重写其他构造方法
 * Created by lsq on 1/5/2017.
 */

public abstract class BaseCustomView extends FrameLayout implements View.OnClickListener {
    private long lastClickTime;
    private int lastViewId;
    private long clickDelayTime = GlobalParams.Config.MINIMUM_CLICK_DELAY;

    public BaseCustomView(Context context) {
        this(context, null);
    }

    public BaseCustomView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseCustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseCustomView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initLayout(context, attrs, defStyleAttr);
    }

    private void initLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        initAttribute(attrs);
        int layoutId = getLayoutId();
        if (layoutId > 0) {
            LayoutInflater.from(context).inflate(layoutId, this);
        }
        ButterKnife.bind(this);
        initViews(context, attrs, defStyleAttr);
    }

    public void setViewLayoutParams(int width, int height) {
        setLayoutParams(new ViewGroup.LayoutParams(width, height));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initData();
    }

    protected abstract void initViews(Context context, AttributeSet attrs, int defStyle);

    public void initData() {

    }

    protected void setClickDelay(long delay) {
        clickDelayTime = delay;
    }

    @Override
    public void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        if ((lastViewId == v.getId())) {
            if (currentTime - lastClickTime > clickDelayTime) {
                onSingleClick(v);
            } else {
                KLog.i(this.getClass().getSimpleName(), "====快速点击无效");
                onDoubleClick(v);
            }
        } else {
            onSingleClick(v);
        }
        lastViewId = v.getId();
        lastClickTime = currentTime;
    }

    protected abstract int getLayoutId();

    protected void initAttribute(AttributeSet attrs) {
    }

    protected void onSingleClick(View v) {//单击
    }

    protected void onDoubleClick(View v) {//双击
    }

    public void showView(int visible) {

    }
}
