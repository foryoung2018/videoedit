package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import com.wmlive.hhvideo.heihei.beans.frame.LayoutInfo;
import com.wmlive.hhvideo.utils.DrawPathUtils;
import com.wmlive.hhvideo.utils.KLog;

/**
 * Created by lsq on 8/24/2017.
 */

public class AnomalyView extends FrameLayout {

    LayoutInfo mInfo;
    Path mPath;
    private ChildEventListener mChildTouchListener;
    private Region region;
    private RectF rectF;
    private int startX;
    private int startY;
    private int startLeft;
    private int startTop;
    private int startRight;
    private int startBottom;
    private long lastTime = 0;
    private boolean canDrag = false; // 是否可以拖动
    private int touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

    public AnomalyView(@NonNull Context context) {
        super(context);
        init();
    }

    public AnomalyView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnomalyView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        region = new Region();
        rectF = new RectF();
    }

    public void setLayoutInfo(LayoutInfo info) {
        setLayoutInfo(info, false);
    }

    /**
     * @param info
     * @param canDrag 是否可拖动
     */
    public void setLayoutInfo(LayoutInfo info, boolean canDrag) {
        mInfo = info;
        this.canDrag = canDrag;
        if (mInfo != null) {
            mPath = DrawPathUtils.getDrawPath(mInfo);
            //计算控制点的边界
            mPath.computeBounds(rectF, true);
            //设置区域路径和剪辑描述的区域
            region.setEmpty();
            region.setPath(mPath, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
            float height = (rectF.bottom - rectF.top);
            float width = (rectF.right - rectF.left);
            KLog.i("====rectF:" + rectF.toString());
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        int left = getLeft();
        int top = getTop();

//        KLog.i("path", "--AnomalyView判断点是否则范围内---- event.getX() " + x + " event.getY() " + y);
//        KLog.i("path", "--AnomalyView判断点是否则范围内----" +region.contains(x+left, y+top));
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getX();
                startY = (int) event.getY();
                startLeft = left;
                startTop = top;
                startRight = getRight();
                startBottom = getBottom();
                setDrawCacheBitmap();
                return true;
//                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_SCROLL:
                if (canDrag) {
                    int deltaX = x - startX;
                    int deltaY = y - startY;
                    int newLeft = left + deltaX;
                    int newTop = top + deltaY;
                    int newRight = getRight() + deltaX;
                    int newButtom = getBottom() + deltaY;
                    setSize(newLeft, newTop, newRight, newButtom);
//                if (region.contains((int) (x+mInfo.x), (int) (y+mInfo.y)))
                    if (region.contains(x + left, y + top)) {
                        this.setVisibility(View.VISIBLE);
                        KLog.d("isOutsideChild==false");
                        mChildTouchListener.onChildTouchEvent((int) getTag(), left, top, getWidth(), getHeight(), event, false);
                        return true;
                    } else {
                        KLog.d("isOutsideChild==true");
                        if (mChildTouchListener != null) {
                            this.setVisibility(View.VISIBLE);
                            mChildTouchListener.onChildTouchEvent((int) getTag(), left, top, getWidth(), getHeight(), event, true);
                        }
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (canDrag) {
                    setSize(startLeft, startTop, startRight, startBottom);
                    this.setVisibility(View.VISIBLE);
                    if (region.contains(x + left, y + top)) {
                        mChildTouchListener.onChildTouchEvent((int) getTag(), left, top, getWidth(), getHeight(), event, false);
                        return true;
                    } else {
                        if (mChildTouchListener != null) {
                            mChildTouchListener.onChildTouchEvent((int) getTag(), left, top, getWidth(), getHeight(), event, true);
                        }
                    }
                } else {
                    long nowTime = System.currentTimeMillis();
                    if (nowTime - lastTime > 300) {
                        if (Math.abs(startX - event.getX()) < touchSlop
                                && Math.abs(startY - event.getY()) < touchSlop) {
                            if (null != mChildTouchListener) {
                                mChildTouchListener.onChildClick((int) getTag(), left, top, (int) mInfo.getWidth(), (int) mInfo.height);
                            }
                        }
                    }
                    lastTime = nowTime;
                }
                break;
        }
//        return region.contains((int) (event.getX()+mInfo.x), (int) (event.getY()+mInfo.y));
        return super.onTouchEvent(event);
    }

    private void setSize(int mleft, int mtop, int mright, int mbottom) {
        layout(mleft, mtop, mright, mbottom);
    }

    private void setDrawCacheBitmap() {
        setDrawingCacheEnabled(true);
        buildDrawingCache();  //启用DrawingCache并创建位图
        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(getDrawingCache());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            setDrawingCacheEnabled(false);  //禁用DrawingCahce
        }
        if (mChildTouchListener != null && bitmap != null) {
            mChildTouchListener.setDrawCacheBitmap(bitmap);
        }

    }

    public interface ChildEventListener {
        /**
         * @param index          子view在父view的顺序
         * @param x              子view位于父view的x轴坐标
         * @param y              子view位于父view的y轴坐标
         * @param event          touch事件
         * @param isOutsideChild 是否在子view外面
         */
        void onChildTouchEvent(int index, int x, int y, int width, int height, MotionEvent event, boolean isOutsideChild);

        /**
         * 子view点击事件
         *
         * @param index  子view在父view的顺序
         * @param x      子view位于父view的x轴坐标
         * @param y      子view位于父view的y轴坐标
         * @param width  宽
         * @param height 高
         */
        void onChildClick(int index, int x, int y, int width, int height);

        void setDrawCacheBitmap(Bitmap drawCacheBitmap);
    }

    public void setChildTouchListener(ChildEventListener childTouchListener) {
        mChildTouchListener = childTouchListener;
    }
    public void setImageVisible(Boolean b){

    }

}
