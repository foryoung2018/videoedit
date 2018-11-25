package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.wmlive.hhvideo.heihei.record.adapter.EditVideoAdapter;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.KLog;

import cn.wmlive.hhvideo.R;

/**
 * Created by wenlu on 2017/9/6.
 */

public class CustomDragRecyclerView extends RecyclerView {
    private static final int DRAG_HEIGHT = 90; // 可拖动的顶部范围
    private int mPeekHeight = 200;
    private int startX;
    private int startY;
    private int startHeight;
    private int mTotalHight;
    private boolean canDrag;
    private OnSizeChangeListener mSizeChangedListener;
    private Drawable mHandleDrawable;
    private Rect rectHandle = new Rect();
    private int mMinHeight = 0;
    private int mMaxHeight = 0;
    private int viewWidth;
    private int viewHeight;
    private int screenWidth;
    private ActionType scrollType = ActionType.NONE;
    private int screenHeight;

    private enum ActionType {
        NONE,
        SCROLL_HORIZONTAL,
        SCROLL_VERTICAL
    }

    public CustomDragRecyclerView(Context context) {
        this(context, null);
    }

    public CustomDragRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomDragRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mHandleDrawable = getResources().getDrawable(R.drawable.icon_video_edit_handle);
        screenWidth = DeviceUtils.getScreenWH(context)[0];
        screenHeight = DeviceUtils.getScreenWH(context)[1];
        int left = (screenWidth - 45) / 2;
        rectHandle.set(left, 15, left + 45, 60);

        post(new Runnable() {
            @Override
            public void run() {
                viewWidth = getMeasuredWidth();
                viewHeight = getMeasuredHeight();
                KLog.i("hsing", "setSize: getMeasuredWidth" + viewWidth);
                if (null != mSizeChangedListener) {
                    mSizeChangedListener.onSizeChanged(viewWidth, viewHeight);
                }
            }
        });
    }

    public void setPeekHeight(int height) {
        setPeekHeight(height, false);
    }

    public void setPeekHeight(int height, boolean refreshHeight) {
        setPeekHeight(height, height, refreshHeight);
    }

    public void setPeekHeight(int height, int minHeight, boolean refreshHeight) {
        this.mMinHeight = minHeight;
        this.mMaxHeight = screenHeight - DeviceUtils.dip2px(getContext(), 240 + 44);
        this.mPeekHeight = height;
        if (refreshHeight) {
            setSize(mPeekHeight);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                | Paint.FILTER_BITMAP_FLAG));
        mHandleDrawable.setBounds(rectHandle);
        mHandleDrawable.draw(canvas);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                scrollType = ActionType.NONE;
                measureHeight();
                startHeight = getMeasuredHeight();
                startX = x;
                startY = y;
                int[] position = new int[2];
                this.getLocationOnScreen(position);
                canDrag = Math.abs(startY - position[1]) < DRAG_HEIGHT;
                KLog.i("hsing", "onInterceptTouchEvent ACTION_DOWN startHeight: " + startHeight + " startX: " + startX + " startY: " + startY);
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_SCROLL:
                int deltaX = x - startX;
                int deltaY = y - startY;
                KLog.i("hsing", "onInterceptTouchEvent ACTION_MOVE deltaX: " + deltaX + " deltaY: " + deltaY + " y: " + y + " startY: " + startY + " canDrag " + canDrag);
                if (canDrag) {
                    if (Math.abs(deltaY) - Math.abs(deltaX) > 10) {
//                    KLog.i("hsing", "onInterceptTouchEvent ACTION_MOVE canDrag deltaY: " + deltaY + " setSize: " + (startHeight+Math.abs(deltaY)));
                        if (null != mSizeChangedListener) {
                            mSizeChangedListener.onSizeChangeStart();
                        }
                        return true;
                    }
                } else {
                    if (scrollType == ActionType.NONE) {
                        if (Math.abs(deltaY) - Math.abs(deltaX) > 10) {
                            scrollType = ActionType.SCROLL_VERTICAL;
                        } else if (Math.abs(deltaX) - Math.abs(deltaY) > 10) {
                            scrollType = ActionType.SCROLL_HORIZONTAL;
                        }
                    }
                    KLog.i("hsing", "onInterceptTouchEvent ACTION_MOVE scrollType: " + scrollType + " canDrag " + canDrag);
                    if (scrollType == ActionType.SCROLL_VERTICAL) {
                        return super.onInterceptTouchEvent(event);
                    } else if (scrollType == ActionType.SCROLL_HORIZONTAL) {
                        return false;
                    }

                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                KLog.i("hsing", "onInterceptTouchEvent ACTION_UP endX: " + event.getRawX() + " endY: " + event.getRawY());
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (null != mSizeChangedListener) {
                    mSizeChangedListener.onSizeChangeStart();
                }
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_SCROLL:
                int deltaX = x - startX;
                int deltaY = y - startY;
                KLog.i("hsing", "ACTION_MOVE canDrag " + canDrag + " startX " + startX + " x " + x + " deltaX " + deltaX + " startY " + startY + " y " + y + " deltaY " + deltaY);
                if (canDrag) {
                    if (Math.abs(deltaY) > Math.abs(deltaX)) {
                        setSize(startHeight - deltaY);
                        return true;
                    }
                } else {
                    if (null != mSizeChangedListener && (deltaY != 0 || deltaY != 0)) {
                        mSizeChangedListener.onSizeChangeEnd();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                KLog.i("hsing", "ACTION_ " + (event.getAction() == MotionEvent.ACTION_UP ? "ACTION_UP" : "ACTION_CANCEL"));
                if (null != mSizeChangedListener) {
                    mSizeChangedListener.onSizeChangeEnd();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void setSize(int h) {
        ViewGroup.LayoutParams params = getLayoutParams();
        int height = h;
        if (h <= mMinHeight) {
            height = mMinHeight;
        } else if (h > mMaxHeight) {
            height = mMaxHeight;
        }
        params.height = height;
        params.width = (viewWidth == 0 ? screenWidth : viewWidth);
        if (null != mSizeChangedListener) {
            mSizeChangedListener.onSizeChanged(params.width, params.height);
        }
        setLayoutParams(params);
    }

    private void measureHeight() {
        mTotalHight = getPaddingTop() + getPaddingBottom();
        int count = getAdapter().getItemCount();
        for (int i = 0; i < count; i++) {
            if (getAdapter() instanceof EditVideoAdapter) {
                int height = ((EditVideoAdapter) getAdapter()).getItemViewHeight(i);
                mTotalHight += height;
            }
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        if (null != mSizeChangedListener) {
//            mSizeChangedListener.onSizeChanged(w, h);
//        }
    }

    public void setOnSizeChangeListener(OnSizeChangeListener listener) {
        this.mSizeChangedListener = listener;
    }

    public interface OnSizeChangeListener {
        void onSizeChangeStart();

        void onSizeChanged(int width, int height);

        void onSizeChangeEnd();
    }
}
