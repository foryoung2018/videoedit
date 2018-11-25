package com.wmlive.hhvideo.heihei.record.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;


import com.wmlive.hhvideo.utils.ScreenUtil;

import cn.wmlive.hhvideo.R;

import static com.dongci.sun.gpuimglibrary.api.DCRecorderCore.getDcRecorderCore;


public class GlTouchView extends FrameLayout {
    private FocuView fview;
    private int mbottom;

    public GlTouchView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setAntiAlias(true);
        paint.setColor(getResources().getColor(R.color.transparent_black));
        fview = new FocuView(context, null);
        m_flignerDetector = new GestureDetector(context,
                new pressGestureListener());

        mbottom = getResources().getDimensionPixelSize(R.dimen.t120dp);
    }

    private boolean isadded = false;

    public void onPrepared() {
        if (null != fview) {
            if (!isadded) {
                isadded = true;
                fview.setVisibility(View.INVISIBLE);
                GlTouchView.this.addView(fview);
            }
        }
    }

    private Paint paint = new Paint();
    private Rect dst = new Rect();

    private boolean ismoving = false;

//    @Override
//    protected void dispatchDraw(Canvas canvas) {
//        super.dispatchDraw(canvas);
//        Log.e("dispatchDraw", ismoving + "dispatchDraw:xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx " + dst.toShortString());
//        if (ismoving) {
////            canvas.drawRect(dst, paint);
//        }
//    }

    private float downx = 0;
    private int targetX = 0, currentX = 0;
    private boolean isleftToRight = false;
    private int poffx;
    private static boolean enableMoveFilter = true;

    public static void enableMoveFilter(boolean enable) {
        enableMoveFilter = enable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int re = event.getAction();
        if (null != m_flignerDetector && event.getY() < (getHeight() - mbottom)) {//防止录制部分的双击继续传递到切换摄像头
            m_flignerDetector.onTouchEvent(event);
        }
//        if (null != m_hlrZoom) {
//            m_hlrZoom.onTouch(event);
//        }
//        Log.e("onTouchEvent", "onTouchEvent: " + re);
        if (enableMoveFilter) {
            if (re == MotionEvent.ACTION_DOWN) {
                doEnd = false;
                downx = event.getX();
                ismoving = false;
                if (null != anim) {
                    anim.end();
                    anim = null;
                }
                mHandler.removeMessages(MSG_END);
            } else if (re == MotionEvent.ACTION_MOVE) {
                float nx = event.getX();
                if (nx - downx > 10) {//从左到右
                    if (!isleftToRight) {//防止滑动----->右到左---->左到右
                        ismoving = false;
                        isleftToRight = true;
                    }
                    int nleft = getLeft();
                    poffx = (int) (nx - downx);
                    double temp = (poffx + 0.0f) / getWidth();
                    if (filterProportion != temp) {
                        filterProportion = temp;
                        currentX = poffx;
                        dst.set(nleft, getTop(), nleft + poffx, getBottom());
                        targetX = getRight();
                        if (!ismoving) {
                            ismoving = true;
                            if (null != m_ccvlListener) {
                                m_ccvlListener.onFilterChangeStart(false, filterProportion);
                            }
                        } else {
                            if (null != m_ccvlListener) {
                                m_ccvlListener.onFilterChanging(false, filterProportion);
                            }
                        }
                        invalidate();
                    }

                } else if (downx - nx > 10) {//从右到左
                    if (isleftToRight) {//防止滑动---左到右-->右到左
                        ismoving = false;
                        isleftToRight = false;
                    }
                    targetX = getLeft();
                    poffx = (int) (downx - nx);
                    double temp = 1 - ((poffx + 0.0) / getWidth());
                    if (filterProportion != temp) {
                        filterProportion = temp;
                        currentX = getWidth() - poffx;
                        dst.set(currentX, getTop(), getRight(), getBottom());
                        if (!ismoving) {
                            ismoving = true;
                            if (null != m_ccvlListener) {
                                m_ccvlListener.onFilterChangeStart(true, filterProportion);
                            }
                        } else {
                            if (null != m_ccvlListener) {
                                m_ccvlListener.onFilterChanging(true, filterProportion);
                            }
                        }

                        invalidate();
                    }
                }
            } else if (re == MotionEvent.ACTION_CANCEL || re == MotionEvent.ACTION_UP) {

                float poffx = Math.abs(event.getX() - downx);
                if ((doEnd && poffx >= getWidth() / 5) || (!doEnd && poffx > getWidth() / 2)) {
                    if (ismoving) {//松开手势时，执行切换
                        getNewAnimationSet(currentX, targetX, true);
                    }
                } else {
                    if (ismoving) {//松开手势时，取消切换
                        if (isleftToRight) {
                            targetX = 0;
                        } else {
                            targetX = getRight();
                        }
                        getNewAnimationSet(currentX, targetX, false);
                    }
                }
                fview.removeAll();
            }
        }
        return true;
    }

    //滤镜比例(从左到右 左边滤镜所占百分比)
    private double filterProportion = 0.01;

    private ValueAnimator anim;
    private boolean istouching = false;

    private void getNewAnimationSet(final int current, final int target, final boolean doEnd) {

//        Log.e("getNewAnimationSet", "getNewAnimationSet: " + current + "......" + target + "..." + doEnd);

        //创建一个加速器
        anim = ValueAnimator.ofInt(current, target);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {


            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int t = (int) animation.getAnimatedValue();
                if (doEnd) {
                    mHandler.removeMessages(MSG_END);
                    mHandler.obtainMessage(MSG_END, current, t).sendToTarget();
                } else {
                    mHandler.removeMessages(MSG_CANCEL);
                    if (current < target) {
                        mHandler.obtainMessage(MSG_CANCEL, current, t + 1).sendToTarget();
                    } else {
                        mHandler.obtainMessage(MSG_CANCEL, current, t - 1).sendToTarget();
                    }

                }
            }
        });
        anim.setInterpolator(new LinearInterpolator());//匀速移动
        anim.setDuration(200);
        anim.start();
    }

    private boolean doEnd = false;//判断手势离开时，是继续切换滤镜还是取消切换

    private final int MSG_END = 564, MSG_CANCEL = 565;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_END: {//松开手势，自动完成剩余部分的滑动
                    int tempTarget = msg.arg2;
                    int cur = msg.arg1;
                    if (cur < tempTarget) {//从左往右
                        if (tempTarget >= getRight()) {
                            ismoving = false;
                            if (null != m_ccvlListener) {
                                m_ccvlListener.onFilterChangeEnd(true);
                            }
                        } else {
                            int nleft = getLeft();
                            double temp = (tempTarget - nleft + 0.0f) / getWidth();
                            if (filterProportion != temp) {
                                filterProportion = temp;
                                dst.set(nleft, getTop(), tempTarget, getBottom());
                                if (null != m_ccvlListener) {
                                    m_ccvlListener.onFilterChanging(false, filterProportion);
                                }
                            }
                        }
                        invalidate();

                    } else {//从右往左
                        if (tempTarget <= getLeft()) {
                            ismoving = false;
                            if (null != m_ccvlListener) {
                                m_ccvlListener.onFilterChangeEnd(false);
                            }
                        } else {
                            double temp = (tempTarget + 0.0f) / getWidth();
                            if (filterProportion != temp) {
                                filterProportion = temp;
                                dst.set(tempTarget, getTop(), getRight(), getBottom());
                                if (null != m_ccvlListener) {
                                    m_ccvlListener.onFilterChanging(true, filterProportion);
                                }
                            }

                        }
                        invalidate();
                    }
                }
                break;
                case MSG_CANCEL: {//响应松开时，取消切换
                    int tempTarget = msg.arg2;  //定速取消 ,,变量  ->1080
                    int cur = msg.arg1;//离开时手势的位置
                    if (cur < tempTarget) {//滑动时从右到左--->取消时从左到右
                        if (tempTarget >= getRight()) {
                            ismoving = false;
                            if (null != m_ccvlListener) {
                                m_ccvlListener.onFilterChangeCanceled();
                            }
                        } else {
                            dst.set(tempTarget, getTop(), getRight(), getBottom());
                            int nleft = getLeft();
                            double temp = (tempTarget - nleft + 0.0f) / getWidth();
                            if (filterProportion != temp) {
                                filterProportion = temp;
                                if (null != m_ccvlListener) {
                                    m_ccvlListener.onFilterCanceling(false, filterProportion);
                                }
                            }
                        }
                        invalidate();
                    } else {//从右往左
                        if (tempTarget <= getLeft()) {
                            ismoving = false;
                            if (null != m_ccvlListener) {
                                m_ccvlListener.onFilterChangeCanceled();
                            }
                        } else {
                            dst.set(getLeft(), getTop(), tempTarget, getBottom());
                            double temp = (tempTarget + 0.0f) / getWidth();
                            if (filterProportion != temp) {
                                filterProportion = temp;
                                if (null != m_ccvlListener) {
                                    m_ccvlListener.onFilterCanceling(true, filterProportion);
                                }
                            }

                        }
                        invalidate();
                    }
                }
                break;
                default:
                    break;
            }
        }
    };


    /**
     * 手势listener
     *
     * @author abreal
     */
    private class pressGestureListener extends SimpleOnGestureListener {

        /*
         * (non-Javadoc)
         *
         * @see
         * android.view.GestureDetector.SimpleOnGestureListener#onLongPress(
         * android.view.MotionEvent)
         */
        @Override
        public void onLongPress(MotionEvent e) {
//            Log.d(TAG, "onLongPress");
            super.onLongPress(e);
        }


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {


            doEnd = true;
//            Log.e("onFling", "onFling: " + e1.getX() + ".............." + e2.getX());

//            if (m_ccvlListener != null) {
//                if (e1.getX() < e2.getX()) { // 向右fling
//                    m_ccvlListener.onSwitchFilterToRight();
//                } else { // 向左fling
//                    m_ccvlListener.onSwitchFilterToLeft();
//                }
//            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * android.view.GestureDetector.SimpleOnGestureListener#onSingleTapUp
         * (android.view.MotionEvent)
         */
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
//            Log.e(TAG, "onSingleTapUp: " + "");
            if (m_ccvlListener != null) {
                m_ccvlListener.onSingleTapUp(e);
            }
            if (!getDcRecorderCore().isFaceFront()) {
                int dx = (int) e.getX(), dy = (int) e.getY();
                int height = getHeight();
                int bh = getResources().getDimensionPixelSize(R.dimen.t175dp);
                int width = getWidth();
                int tw = ScreenUtil.dip2px(getContext(),70);
                if (dx < (width - tw) && dy > ScreenUtil.dip2px(getContext(),55) && dy < (height - bh)) {
                    fview.setLocation(dx, dy);
                }
            }
            return super.onSingleTapUp(e);
        }


        @Override //双击
        public boolean onDoubleTap(MotionEvent e) {
//            Log.e(TAG, "onDoubleTap: " + "");
            fview.removeAll();
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
//            Log.e(TAG, "onDoubleTapEvent: " + "");
//            GlTouchView.this.removeCallbacks(cameraFocus);
            fview.removeAll();
            if (m_ccvlListener != null) {
                m_ccvlListener.onDoubleTap(e);
            }
            return super.onDoubleTapEvent(e);
        }
    }

    private GestureDetector m_flignerDetector;

    public void setViewHandler(CameraCoderViewListener ccvl) {
        m_ccvlListener = ccvl;
    }

    protected CameraCoderViewListener m_ccvlListener;
//    private ICameraZoomHandler m_hlrZoom;


    /**
     * 切换摄像头特效Listener
     *
     * @author abreal
     */
    public interface CameraCoderViewListener {
        /**
         * 向左切换
         */
        void onSwitchFilterToLeft();

        /**
         * 向右切换
         */
        void onSwitchFilterToRight();

        /**
         * 单击
         *
         * @param e
         */
        void onSingleTapUp(MotionEvent e);

        /**
         * 双击
         *
         * @param e
         */
        void onDoubleTap(MotionEvent e);

        /**
         * 即将开始切换滤镜(准备同时绘制两个滤镜)
         *
         * @param leftORight       true,从右往左; false 从左往右
         * @param filterProportion 左边滤镜所占百分比
         */
        void onFilterChangeStart(boolean leftORight, double filterProportion);

        /**
         * 左右实时滑动滤镜
         *
         * @param leftORight       true,从右往左; false 从左往右
         * @param filterProportion 左边滤镜所占百分比
         */
        void onFilterChanging(boolean leftORight, double filterProportion);

        /**
         * 滑动滤镜结束(绘制完整的单个滤镜)
         */
        void onFilterChangeEnd(boolean leftToRight);

        /**
         * 手势离开取消滤镜
         *
         * @param leftORight       true,从右往左; false 从左往右
         * @param filterProportion 左边滤镜所占百分比
         */


        void onFilterCanceling(boolean leftORight, double filterProportion);

        /**
         * 取消切换滤镜
         */
        void onFilterChangeCanceled();

    }

//    public void setZoomHandler(ICameraZoomHandler hlrZoom) {
//        m_hlrZoom = hlrZoom;
//    }

    public void recycle() {
        if (null != anim) {
            anim.end();
            anim = null;
        }
        removeView(fview);
        m_flignerDetector = null;
        fview = null;
        m_ccvlListener = null;
    }
}

