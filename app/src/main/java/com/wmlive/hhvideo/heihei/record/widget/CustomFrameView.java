package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import android.widget.LinearLayout;

import android.widget.RelativeLayout;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.heihei.beans.frame.FrameInfo;
import com.wmlive.hhvideo.heihei.beans.frame.LayoutInfo;
import com.wmlive.hhvideo.heihei.record.manager.RecordSetting;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.DrawPathUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ScreenUtil;

import java.util.List;

import cn.wmlive.hhvideo.R;


/**
 * Created by wenlu on 2017/8/25.
 */

public class CustomFrameView extends ViewGroup {

    private static final String TAG = CustomFrameView.class.getSimpleName();
    private static final boolean DEF_BORDERS = true;
    private static final int DEF_BORDERS_COLOR = Color.WHITE;
    private static final float DEF_BORDER_WIDTH = 4f;
    private static final boolean DEF_BORDER_CORNERS_ROUND = true;

    private Paint paint;
    private int mViewWidth, mViewHeight;//this view的宽高，默认为屏幕宽。
    private boolean mBorderEnabled;
    private int mBorderColor;
    public float mBorderWidth;
    private boolean isMiniSize = false;

    private boolean useScreenWidth;

    private FrameInfo mFrameInfo = new FrameInfo(101, 1);
    private int selectIndex = 0;
    private int targetIndex = 0;
    private EventListener mEventListener;
    private ImageView dragView;
    private Bitmap drawCacheBitmap;
    private boolean mHasBorder;
    private Paint borderPaint;
    private boolean mAutoSize = true; // 自动调整布局大小
    private float videoSizeRatio;
    private int screenWidth;

    public void setFrameInfo(FrameInfo frameInfo) {
        setFrameInfo(frameInfo, false);
    }

    public void setFrameInfo(FrameInfo frameInfo, boolean isMiniSize) {
        setFrameInfo(frameInfo, isMiniSize, false);
    }

    public void setFrameInfo(FrameInfo frameInfo, boolean isMiniSize, boolean canDrag) {
        setFrameInfo(frameInfo, isMiniSize, canDrag, false);
    }

    /**
     * 列表里画框的显示
     *
     * @param frameInfo
     * @param isMiniSize
     * @param canDrag
     * @param hasBorder  是否绘制外边框
     */
    public void setFrameInfo(FrameInfo frameInfo, boolean isMiniSize, boolean canDrag, boolean hasBorder) {
        if (frameInfo == null) {
            throw new IllegalArgumentException("frameInfo null");
        }
        mFrameInfo = null;
        mFrameInfo = frameInfo.deepClone();
        if (mFrameInfo != null) {
            //        mFrameInfo = (FrameInfo) BeanUtils.deepClone(frameInfo);
            mHasBorder = hasBorder;
            LayoutParams layoutParams = getLayoutParams();
            int width = layoutParams.width;
            int height = layoutParams.height;
            mFrameInfo.setRealSize(width, height, hasBorder);
            initBorderPaint();
            removeAllViews();
            this.isMiniSize = isMiniSize;
            AnomalyView view;
            for (int i = 0, n = frameInfo.getLayout().size(); i < n; i++) {
                view = new AnomalyView(getContext());
                view.setTag(i);
                view.setLayoutInfo(mFrameInfo.getLayout().get(i), canDrag);
                addView(view);
            }
        }
    }

    public <T extends AnomalyView> void setFrameView(FrameInfo frameInfo, List<T> viewList) {
        setFrameView(frameInfo, viewList, false);
    }

    public <T extends AnomalyView> void setFrameView(FrameInfo frameInfo, List<T> viewList, boolean hasBorder) {
        if (frameInfo == null || viewList == null || viewList.size() == 0) {
            return;
        }
        mFrameInfo = null;
        mFrameInfo = frameInfo.deepClone();
        if (mFrameInfo != null) {
            mHasBorder = hasBorder;
            LayoutParams layoutParams = getLayoutParams();
            int width = layoutParams.width;
            int height = layoutParams.height;
            KLog.d("ggq", "layoutParams.width==" + layoutParams.width + "  layoutParams.height==" + layoutParams.height);
            mFrameInfo.setRealSize(width, height, hasBorder);
            initBorderPaint();
            removeAllViews();
            isMiniSize = false;
            for (T t : viewList) {
                if (t != null) {
                    addView(t);
                }
            }
        }
    }

    public CustomFrameView(Context context) {
        super(context);
        init(null);
    }

    public CustomFrameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomFrameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomFrameView, 0, 0);
        mBorderEnabled = a.getBoolean(R.styleable.CustomFrameView_cfv_show_border, DEF_BORDERS);
        mBorderColor = a.getColor(R.styleable.CustomFrameView_cfv_border_color, DEF_BORDERS_COLOR);
        mAutoSize = a.getBoolean(R.styleable.CustomFrameView_cfv_auto_size, true);
        a.recycle();

        videoSizeRatio = RecordSetting.VIDEO_DATA_WIDTH * 1.0f / RecordSetting.VIDEO_DATA_HEIGHT;
        screenWidth = DeviceUtils.getScreenWH(DCApplication.getDCApp())[0];
        initPaint();
//        post(new Runnable() {
//            @Override
//            public void run() {
//                reLayout();
//            }
//        });
    }

    public void setUseScreenWidth(boolean useScreenWidth) {
        this.useScreenWidth = useScreenWidth;
    }

    private void reLayout() {
        if (mAutoSize) {
            LayoutParams layoutParams = getLayoutParams();
            mViewWidth = useScreenWidth ? screenWidth : getMeasuredWidth();
            if (mViewWidth == 0) {
                return;
            }
            if (mViewWidth > screenWidth) {
                mViewWidth = screenWidth;
            }
            mViewHeight = (int) (mViewWidth / videoSizeRatio);

            KLog.v(TAG, "view:  mViewWidth post " + mViewWidth + ", " + mViewHeight + " ,mAutoSize-videoSizeRatio:" + videoSizeRatio);
            layoutParams.width = mViewWidth;
            layoutParams.height = mViewHeight;
            if (mFrameInfo != null) {
                mFrameInfo.setRealSize(mViewWidth, mViewHeight, mHasBorder);
            }
            requestLayout();
        } else {
            mViewWidth = useScreenWidth ? screenWidth : getMeasuredWidth();
            if (mViewWidth == 0) {
                return;
            }
            if (mViewWidth > screenWidth) {
                mViewWidth = screenWidth;
            }
            mViewHeight = (int) (mViewWidth / videoSizeRatio);
            KLog.d("ggq", "view:  mViewWidth post " + mViewWidth + ", " + mViewHeight + " ,videoSizeRatio:" + videoSizeRatio);
            LayoutParams layoutParams = getLayoutParams();
            layoutParams.width = mViewWidth;
            layoutParams.height = mViewHeight;
            if (mFrameInfo != null) {
                mFrameInfo.setRealSize(getMeasuredWidth(), getMeasuredHeight(), mHasBorder);
            }
        }
    }

    public void setVideoSizeRatio(float ratio) {
        if (ratio == 544 * 1.0f / 960 || ratio == 540 * 1.0f / 960) {
            ratio = ScreenUtil.getWidth(getContext()) * 1.0f / ScreenUtil.getHeight(getContext());
        }

        videoSizeRatio = ratio;
        KLog.d("ggq", "videoSizeRatio==" + videoSizeRatio);
        post(new Runnable() {
            @Override
            public void run() {
                reLayout();
                if (!mAutoSize) {
                    requestLayout();
                }
            }
        });
    }

    private void initPaint() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(mBorderColor);
        paint.setStrokeWidth(mBorderWidth);
        paint.setStyle(Paint.Style.STROKE);
        if (isMiniSize) {
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.SQUARE);
            paint.setPathEffect(new CornerPathEffect(50));
        }
    }

    private void initBorderPaint() {
        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(mBorderColor);
        if (mHasBorder && mFrameInfo != null) {

            if (isMiniSize) {
                borderPaint.setStrokeWidth(mFrameInfo.realBorderWidth + 1);//设置小画框宽度

            } else {
                borderPaint.setStrokeWidth(mFrameInfo.realBorderWidth * 2);//设置大画框宽度

            }
        } else {
            borderPaint.setStrokeWidth(mBorderWidth);
        }

        borderPaint.setStyle(Paint.Style.STROKE);
    }

    public void refresh() {
        initPaint();
        if (mAutoSize) {
            LayoutParams layoutParams = getLayoutParams();
            mViewHeight = getMeasuredHeight();
            if (mViewHeight == 0) {
                return;
            }
            mViewWidth = (int) (mViewHeight * videoSizeRatio);
            if (mViewWidth > screenWidth) {
                mViewWidth = screenWidth;
                mViewHeight = (int) (screenWidth / videoSizeRatio);
            }
            layoutParams.width = mViewWidth;
            layoutParams.height = mViewHeight;
            KLog.d(TAG, "view:  requestLayout mViewWidth " + mViewWidth + ", " + mViewHeight);
            if (mFrameInfo != null) {
                mFrameInfo.setRealSize(mViewWidth, mViewHeight, mHasBorder);
            }
            initBorderPaint();
            requestLayout();
        } else {
            mViewHeight = getMeasuredHeight();
            if (mViewHeight == 0) {
                return;
            }
            mViewWidth = (int) (mViewHeight * videoSizeRatio);
            if (mViewWidth > screenWidth) {
                mViewWidth = screenWidth;
                mViewHeight = (int) (screenWidth / videoSizeRatio);
            }
            KLog.d(TAG, "view:  requestLayout mViewWidth " + mViewWidth + ", " + mViewHeight + " ,videoSizeRatio:" + videoSizeRatio);
            if (mFrameInfo != null) {
                mFrameInfo.setRealSize(mViewWidth, mViewHeight, mHasBorder);
            }
        }
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);
        KLog.d("ggq", "view onSizeChanged: " + width + ", " + height);
        if (useScreenWidth) {
            reLayout();
        } else {
//            refresh();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        if (getChildCount() == mFrameInfo.layoutInfos.size()) {
        int position = 0;
        for (int index = 0; index < getChildCount(); index++) {
            View child = getChildAt(index);
            if (child instanceof ImageView) {
                continue;
            }
            LayoutInfo info = mFrameInfo.getLayout().get(position++);
//            if (child instanceof SmallFrameView)
            //set children into the center of the region rectangle
            if (isMiniSize) {
                int borderWidth = mFrameInfo.realBorderWidth;
                child.layout((int) info.xr, (int) info.yr,
                        (int) info.xr + (int) info.wr + 2 * borderWidth, (int) info.yr + (int) info.hr + 2 * borderWidth);
            } else {
                child.measure(
                        MeasureSpec.makeMeasureSpec((int) info.wr, MeasureSpec.AT_MOST),
                        MeasureSpec.makeMeasureSpec((int) info.hr, MeasureSpec.AT_MOST)
                );
                int borderWidth = mFrameInfo.realBorderWidth;
                if (mHasBorder) {
                    child.layout((int) info.xr + borderWidth, (int) info.yr + borderWidth,
                            (int) info.xr + (int) info.wr + borderWidth, (int) info.yr + (int) info.hr + borderWidth);
                } else {
                    child.layout((int) info.xr, (int) info.yr,
                            (int) (info.xr + info.wr), (int) (info.yr + info.hr));
                }
            }
        }
//        }
    }


    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        KLog.i(TAG, child.getTag() + "child-->getWidth--drawChild " + (child instanceof ImageView));
        if (child instanceof ImageView) {
            Path path = new Path();

            KLog.i(TAG, child.getTag() + "child-->getWidth() " + getWidth() + " getHeight()" + getHeight());

            path.addRect(new RectF(0, 0, getWidth(), getHeight()), Path.Direction.CW);
            canvas.clipPath(path, Region.Op.REPLACE);
            path.close();
            return super.drawChild(canvas, child, drawingTime);
        }
        Integer index = (Integer) child.getTag();
        LayoutInfo info = mFrameInfo.getLayout().get(index);

        boolean result = false;
        // firstly clip and draw children
//        canvas.clipPath(DrawPathUtils.getDrawPath(info, mHasBorder ? mFrameInfo.realBorderWidth : 0));
        result = super.drawChild(canvas, child, drawingTime);
        // then draw borders
        if (mBorderEnabled) {
            if (mHasBorder) {
                if (isMiniSize) {
                    canvas.drawPath(DrawPathUtils.getDrawBorderPathSmall(info, mHasBorder ? mFrameInfo.realBorderWidth : 0), borderPaint);
                } else {
                    canvas.drawPath(DrawPathUtils.getDrawBorderPath(info, mHasBorder ? mFrameInfo.realBorderWidth : 0), borderPaint);
                }
            } else {
//                canvas.drawPath(DrawPathUtils.getDrawBorderPath(info, 0), paint);
            }
        }

        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public void addView(View child) {
        if (child instanceof AnomalyView) {
            ((AnomalyView) child).setChildTouchListener(touchListener);
        } else {
            if (!(child instanceof ImageView))
                throw new IllegalArgumentException("CustomFrameView child must type AnomalyView");
        }
        super.addView(child);
    }

    private boolean hasInRange(int x, int y, LayoutInfo info) {
        Path mPath = DrawPathUtils.getDrawPath(info);
        //构造一个区域对象，左闭右开的。
        RectF r = new RectF();
        //计算控制点的边界
        mPath.computeBounds(r, true);
        //设置区域路径和剪辑描述的区域
        Region re = new Region();
        re.setPath(mPath, new Region((int) r.left, (int) r.top, (int) r.right, (int) r.bottom));
        //判断触摸点是否在封闭的path内 在返回true 不在返回false
        KLog.i(TAG, "--判断点是否则范围内---- event.getX() " + x + " event.getY() " + y);
        KLog.i(TAG, "--判断点是否则范围内----" + re.contains(x, y));
        return re.contains(x, y);
    }

    /**
     * 是否触发删除
     *
     * @param x
     * @param y
     * @return
     */
    private boolean hasInDeletRange(int x, int y) {
        int i = ScreenUtil.dip2px(getContext(), 60);
        return y < i;
    }

    private AnomalyView.ChildEventListener touchListener = new AnomalyView.ChildEventListener() {

        private int layoutsCount;

        @Override
        public void onChildTouchEvent(int index, int x, int y, int width, int height, MotionEvent event, boolean isOutsideChild) {
            layoutsCount = mFrameInfo.getLayout().size();
            selectIndex = index;
            boolean isShow = false;
            for (int i = 0; i < layoutsCount; i++) {
                LayoutInfo info = mFrameInfo.getLayout().get(i);
                if (info != null) {
                    if (hasInRange(x + (int) event.getX(), y + (int) event.getY(), info)) {
                        targetIndex = i;
                        break;
                    }
                }
            }
            if (hasInDeletRange((int) event.getRawX(), (int) event.getRawY())) {
                showDeleteField(true);
                isShow = true;
            } else {
                isShow = false;
                showDeleteField(false);
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_SCROLL:
                    View view = getChildAt(index);
                    if (dragView == null) {
                        if (null != drawCacheBitmap) {
                            RelativeLayout parent1 = (RelativeLayout) getParent();
                            FrameLayout parent = (FrameLayout) parent1.getParent();
                            dragView = parent.findViewById(R.id.iv_move_image);
                            FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(view.getWidth(), view.getHeight());
                            p.topMargin = getTop() + y;
                            p.leftMargin = getLeft() + x;
                            dragView.setLayoutParams(p);

                            dragView.layout(getLeft() + x, getTop() + y, getLeft() + x + view.getWidth(), getTop() + y + view.getHeight());
                            dragView.setScaleType(ImageView.ScaleType.FIT_XY);
                            dragView.setImageBitmap(drawCacheBitmap);
                            dragView.setAlpha(0.8f);

                            dragView.setVisibility(View.VISIBLE);
                            KLog.d("ggq", "dragview======event==" + event.getAction());
                        }
                    }
                    if (isOutsideChild) {
                        KLog.i(TAG, "layout x " + x + " y " + y + " view.getWidth() " + view.getWidth() + " view.getHeight() " + view.getHeight());
                        dragView.setVisibility(View.VISIBLE);
                        dragView.layout(getLeft() + x, getTop() + y, getLeft() + x + view.getWidth(), getTop() + y + view.getHeight());
                    } else {
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (dragView != null) {
                        dragView.setVisibility(View.GONE);
                        dragView = null;
                    }
                    showDeleteField(false);
                    if (isShow) {
                        if (mEventListener != null)
                            mEventListener.deleteFramItem(selectIndex);
                    } else {
                        if (mEventListener != null) {
                            mEventListener.onChangePosition(selectIndex, targetIndex);
                        }
                    }

                    break;
            }
            KLog.i("====onChildTouch:tag:" + index);
        }

        @Override
        public void onChildClick(int index, int x, int y, int width, int height) {
            if (mEventListener != null) {
                KLog.i("====onChildClick:" + index);
                mEventListener.onChildClick(index, x, y, width, height);
            }
        }

        @Override
        public void setDrawCacheBitmap(Bitmap bitmap) {
            drawCacheBitmap = bitmap;
        }

    };

    private void showDeleteField(boolean show) {
        if (mEventListener != null)
            mEventListener.showDeletField(show, selectIndex);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (isMiniSize) {
            return true;
        }
        return super.onInterceptTouchEvent(event);
    }

    public interface EventListener {

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

        void onChangePosition(int selectIndex, int targetIndex);

        void showDeletField(boolean show, int index);

        void deleteFramItem(int i);
    }

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }

    public RectF getChildRelativeRectF(int childIndex) {
        int count = getChildCount();
        Log.d("tag", "Edit---getChildCount>" + count);
        if (childIndex >= 0 && childIndex < count) {
            return mFrameInfo.getLayoutRelativeRectF(childIndex, 0);
        }
        return new RectF(0f, 0f, 1f, 1f);
    }

    public void setEnable(boolean enable) {
        if (enable) {
            mBorderColor = DEF_BORDERS_COLOR;
        } else {
            mBorderColor = getResources().getColor(R.color.hh_color_b);
        }
        initBorderPaint();
        invalidate();
    }
}
