package com.wmlive.hhvideo.heihei.record.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;

import com.wmlive.hhvideo.heihei.record.utils.CoreUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ScreenUtil;

import cn.wmlive.hhvideo.R;


/**
 * 录制按钮
 *
 * @author JIAN
 * @date 2017-5-22 上午10:53:18
 */
public class ExtBtnRecord extends RotateImageView {

    private boolean isClickRecord = true;

    public void setClickRecord(boolean clickRecord) {
        KLog.i("record======setClickRecord");
        isClickRecord = clickRecord;
    }

    public ExtBtnRecord(Context context, AttributeSet attrs) {
        super(context, attrs);
        createHandler();
        Resources res = getResources();
        p.setColor(res.getColor(R.color.recorder_scale));
        p.setAntiAlias(true);
        p2.setXfermode(new PorterDuffXfermode(
                PorterDuff.Mode.CLEAR));
        p2.setColor(res.getColor(R.color.black));
        p2.setAntiAlias(true);
        hasL = CoreUtils.hasL();
        m20dp = getResources().getDimensionPixelSize(R.dimen.t20dp);
    }

    Paint p = new Paint();
    Paint p2 = new Paint();


    private int downx, downY;

    private boolean enableTouchScroll = false;

    /**
     * 是否支持长按滑动
     *
     * @param enable
     */
    public void enableTouchScroll(boolean enable) {
        enableTouchScroll = enable;
    }

    private boolean isforcedexit = false;

    /**
     * 录制超时,强制退出
     */
    public void onForcedExit() {
        istouching = false;
        if (null != anim) {
            anim.end();
            anim = null;
        }
        isforcedexit = true;
        removeCallbacks(onLongListener);
        removeCallbacks(onScaled);
        mUserLongClick = false;
        resetLocation();
    }

    private Runnable onLongListener = new Runnable() {
        @Override
        public void run() {
            mUserLongClick = true;
            showWaveAnimation();
        }
    };
    /**
     * 通知可以开始录制
     */
    private Runnable onScaled = new Runnable() {

        @Override
        public void run() {
            KLog.i("record-->onScaled-->");
            mRealLongClick = true;
            if (null != ionLongListener) {
                ionLongListener.onBegin();
                setStartRecord(true);
                KLog.i("record======setStartRecord-onScaled-true");
            }

        }
    };


    private final float DEFAULT_INTER_MIN = 0.78f;
    private final float DEFAULT_INTER_MAX = 0.95f;
    private final float INTER_ITEM = (DEFAULT_INTER_MAX - DEFAULT_INTER_MIN) / 15;
    private float frad = DEFAULT_INTER_MIN;
    private Rect rectLocation = new Rect();
    private boolean isFirst = true;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
//        Log.e("onLayout", isFirst + "onLayout: " + changed + "...." + left + ".." + top + "..." + right + "...." + bottom);
        if (isFirst && changed) {
//            Log.e("onLayout", isFirst + "onLayout: " + changed + "...." + left + ".." + top + "..." + right + "...." + bottom);
            rectLocation.set(left, top, right, bottom);
            int mw = rectLocation.width();
            FTARGETSCALE = (int) (mw * FSCALE);
            isFirst = false;
        }
    }


    private ViewGroup vp;

    public void setTranistion(ViewGroup mgroup) {
        vp = mgroup;
    }

    /**
     * 重置初始位置
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void resetLocation() {
        frad = DEFAULT_INTER_MIN;
        mHandler.removeMessages(MSG_SCALE);
        mHandler.removeMessages(MSG_SCALE_ANIM);
        if (null != vp && CoreUtils.hasKitKat()) {
            TransitionManager.beginDelayedTransition(vp);
        }
        setSize(rectLocation.left, rectLocation.top, rectLocation.right, rectLocation.bottom);


    }

    private boolean mUserLongClick = false;//是否消费长按了时间
    private boolean mRealLongClick = false;//是否消费长按了时间
    private boolean isMoved = false;//移除延时长按时间

    /**
     * 组件padding(layout.xml marginbottom 导致无法滑动到屏幕最底部 so 用padding 替换)
     */
    private final int m20dp;

    @Override
    protected void onDraw(Canvas canvas) {
//        Log.e("onDraw" + mUserLongClick, getLeft() + "_" + getTop() + "..." + getRight() + "__" + getBottom());
        if (mUserLongClick) {
            int tw = getWidth() / 2;
            int th = getHeight() / 2;
            int rad = Math.max(tw, th) - m20dp;
//            canvas.drawColor(Color.BLACK);
            canvas.drawCircle(tw, th, rad, p);
            canvas.drawCircle(tw, th, rad * frad, p2);
//            canvas.restoreToCount(sc);


        } else {
            super.onDraw(canvas);
//            canvas.drawColor(Color.BLACK);
        }
//        canvas.drawColor(Color.RED);


    }

    private boolean mEnable = true;

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        mEnable = b;
        KLog.i("setEnabled"+b);
    }

    private long lastTime = 0;
    private boolean moveOutRect = false;
    private int poffX = 0, poffY = 0;//记录按下时的手指位置与组件中心的偏移（滑动过程中保持手指在组件中的相对位置(一直左下角、右下角)）

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!mEnable) {
            return false;
        }
        boolean re = super.dispatchTouchEvent(event);
//        Log.e("dispatchTouchEvent",
//                "...." + event.getAction() + "..." + event.getX() + "*"
//                        + event.getY() + "...." + mUserLongClick + ".........." + isMoved + "....." + enableTouchScroll + "...." + getWidth() + "*" + getHeight() + "..........." + FTARGETSCALE + "*" + FTARGETSCALE + "...." + (m20dp * 2) + "*" + (m20dp * 2));
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                KLog.i("recordbtn-click-down");
                if ((System.currentTimeMillis() - lastTime) < 300) {//防止重复点击
                    return false;
                }
                downx = (int) event.getX();
                downY = (int) event.getY();
                int maxSize = 0;
                if (m20dp <= downx && downx <= (maxSize = rectLocation.width() - m20dp) && m20dp <= downY && downY <= maxSize) {
                    lastTime = System.currentTimeMillis();
                    moveOutRect = false;
                    isforcedexit = false;
                    isMoved = false;
                    istouching = true;
                    bScaled = false;
                    mUserLongClick = true;
                    mRealLongClick = true;
                    poffX = (downx - rectLocation.width() / 2);//手指与中心点的偏移
                    poffY = (downY - rectLocation.height() / 2);
                    if (enableTouchScroll) {
                        if (null != ionLongListener) {
                            ionLongListener.onActionDown();
                        }
//                        removeCallbacks(onLongListener);
//                        post(onLongListener);
//                        postDelayed(onScaled, ANIMATIONEACHOFFSET);
                    }
                } else {
                    return false;
                }
            }
            break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_SCROLL: {
                KLog.i("recordbtn-click-move-scroll");
//                int tx = (int) event.getX();
//                int ty = (int) event.getY();
////                if (!isMoved && !mRealLongClick) {
////                    if (Math.abs(downx - tx) > FTARGETSCALE / 5
////                            || Math.abs(downY - ty) > FTARGETSCALE / 5) {
////                        Log.e("move", "move: 111111111111111111");
////                        // 移动超过阈值，则表示移动了 不回调长按时间
////                        if (!mRealLongClick) {//
//////                            Log.e("moved", "dispatchTouchEvent: " + "_" + rectLocation.toShortString());
////                            removeCallbacks(onScaled);
////                            mUserLongClick = false;
////                            isMoved = true;
////                            resetLocation();
////                            return false;
////                        }
////                    }
////                } else {
//////                    Log.e("move", "move222222222222: ");
////                }
//
////                Log.e("move", rectLocation.toShortString() + "....." + tx + "*" + ty);
//                if (mRealLongClick && (!isforcedexit)) {
//                    if (enableTouchScroll) {
////                    if (enableTouchScroll && bScaled) {
//
//                        int left = getLeft();
//                        int top = getTop();
//                        int mtw = (int) (rectLocation.width() * fscale);
//                        int halfw = mtw / 2;
////                        int halfw = FTARGETSCALE / 2;
//                        int px = tx - halfw;
//                        int py = ty - halfw;
//
//                        int tleft = left + px;
//                        int ttop = top + py;
//
//
//                        Rect mtemp = new Rect(tleft, ttop, tleft + mtw,
//                                ttop + mtw);
//                        mtemp.offset(-(int) (poffX * fscale), -(int) (poffY * fscale));
//
//                        if (moveOutRect) {
////                            Log.e("move", "move33333333333333333: ");
////                            Log.e("move000", mtemp.toShortString() + " --> " + mtemp.width() + "*" + mtemp.height() + "..." + rectLocation.toShortString() + "...." + tx + "..." + ty);
//                            setSize(mtemp.left, mtemp.top, mtemp.right, mtemp.bottom);
//                        } else {
//
//
//                            int centerx = mtemp.left + (mtemp.width() / 2);
//                            int centery = mtemp.top + (mtemp.height() / 2);
////                            Log.e("move", "move244444444444444 ");
//
//
//                            if (Math.abs(rectLocation.centerX() - centerx) > FTARGETSCALE / 4 || Math.abs(rectLocation.centerY() - centery) > FTARGETSCALE / 4) {
////                            if (!rectLocation.contains(centerx, centery)) {//只有超过了默认的位置，才能算移动
//                                moveOutRect = true;
////                                Log.e("move", mtemp.toShortString() + " --> " + mtemp.width() + "*" + mtemp.height() + "..." + rectLocation.toShortString() + "...." + tx + "..." + ty);
//                                setSize(mtemp.left, mtemp.top, mtemp.right, mtemp.bottom);
//                            }
//                        }
//                    }
//                }
            }
            break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                KLog.i("recordbtn-click-up-cancel");
                istouching = false;
//                if (null != anim) {
//                    anim.end();
//                    anim = null;
//                }

//                Log.e("ACTION_UP" + isforcedexit, "ACTION_UP: " + "_" + rectLocation.toShortString());
                removeCallbacks(onScaled);
                mUserLongClick = false;
                mRealLongClick = false;
                resetLocation();
                if (!isforcedexit) {
                    if (mRealLongClick) {// 响应长按事件就不响应单击事件
                        if (null != ionLongListener) {//松开长按
                            KLog.i("=======点击了onEnd");
                            ionLongListener.onEnd();
                        }
                        isClickRecord = true;
                        mRealLongClick = false;
                    } else {
                        if (null != ionLongListener) { // 单击开始结束
                            KLog.i("record=======点击了onActionUp"+isClickRecord);
                            ionLongListener.onActionUp(isClickRecord);
                            isClickRecord = !isClickRecord;
                        }
                    }
                }
                lastTime = System.currentTimeMillis();
//                ExtBtnRecord.this.postDelayed(mPostResetUI, 300);
            }
            break;
            default:
                break;
        }
        return true;
    }

    private Runnable mPostResetUI = new Runnable() {
        @Override
        public void run() {
            mUserLongClick = false;
            mRealLongClick = false;
            ExtBtnRecord.this.invalidate();
        }
    };

    private final int MSG_SCALE = 564;//圆的半径
    private final int MSG_SCALE_ANIM = 565;//按住缩放
    boolean isjia = true;
    private float fscale = 1.0f;
    private Handler mHandler;

    private void createHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_SCALE: {
                        if (frad > DEFAULT_INTER_MAX) {
                            frad -= INTER_ITEM;
                            isjia = false;
                        } else {
                            if (isjia) {
                                frad += INTER_ITEM;
                            } else {
                                if (frad < DEFAULT_INTER_MIN) {
                                    isjia = true;
                                } else {
                                    frad -= INTER_ITEM;
                                }
                            }
                        }
                        invalidate();
                        mHandler.removeMessages(MSG_SCALE);
                        mHandler.sendEmptyMessageDelayed(MSG_SCALE, 38);
                    }
                    break;
                    case MSG_SCALE_ANIM: {
                        fscale = (float) msg.obj;
                        int tw = (int) (rectLocation.width() * fscale) / 2;
                        int th = (int) (rectLocation.height() * fscale) / 2;

                        int centerx = getLeft() + (getWidth() / 2);
                        int centery = getTop() + (getHeight() / 2);
                        int mleft = centerx - tw;
                        int mtop = centery - th;
                        int mright = centerx + tw;
                        int mbottom = centery + th;
                        setSize(mleft - 1, mtop - 1, mright + 1, mbottom + 1);
                        if (fscale >= FSCALE) {
                            bScaled = true;
                            mHandler.removeMessages(MSG_SCALE);
                            mHandler.sendEmptyMessage(MSG_SCALE);
                        }
                    }
                    break;
                    default:
                        break;
                }
            }
        };
    }

    //按住缩放完成
    private boolean bScaled = true;

    private boolean hasL = true;

    /**
     * 本身位置(相对于父容器的左上角)
     *
     * @param mleft
     * @param mtop
     * @param mright
     * @param mbottom
     */
    private void setSize(int mleft, int mtop, int mright, int mbottom) {
        if (getLeft() != mleft) {
//            Log.e("handleMessage", "handleMessage: -->" + (mright - mleft) + "*" + (mbottom - mtop) + "---->" + getWidth() + "*" + getHeight());
            if (hasL) {//5.0以上更改
                ExtBtnRecord.this.layout(mleft, mtop, mright, mbottom); //4.4调用此方法有被遮挡的情况
            } else {
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(mright - mleft, mbottom - mtop);
//            lp.setMargins(mleft, mtop, 0, 0);  切记不能setmargin()
                lp.leftMargin = mleft;
                lp.topMargin = mtop;
                lp.rightMargin = 0;
                lp.bottomMargin = 0;
                ExtBtnRecord.this.setLayoutParams(lp);
            }

        }
    }

    private final int ANIMATIONEACHOFFSET = 100;
    private float FSCALE = 1.35f;
    private int FTARGETSCALE = ScreenUtil.dip2px(getContext(),62);

    private ValueAnimator anim;
    private boolean istouching = false;

    private void getNewAnimationSet() {
        //创建一个加速器

        anim = ValueAnimator.ofFloat(1, FSCALE);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {


            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (istouching) {
                    //更新按住缩放
                    if (null != mHandler) {
                        mHandler.obtainMessage(MSG_SCALE_ANIM, (float) animation.getAnimatedValue()).sendToTarget();
                    }
                } else {
                    mUserLongClick = false;
                    if (null != mHandler) {
                        mHandler.removeMessages(MSG_SCALE_ANIM);
                        mHandler.removeMessages(MSG_SCALE);
                    }
                    invalidate();
                }
            }
        });
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setDuration(ANIMATIONEACHOFFSET);
        anim.start();
    }

    private void showWaveAnimation() {
        frad = DEFAULT_INTER_MIN;
        isjia = true;
        getNewAnimationSet();

    }

    private onLongListener ionLongListener;

    public void setLongListener(onLongListener longListener) {
        ionLongListener = longListener;
    }

    public void setStartRecord(boolean isRecord) {
        KLog.i("record======setStartRecord"+isRecord);
        this.isClickRecord = !isRecord;
    }

    public interface onLongListener {


        /**
         * 开始触摸,隐藏控制面板菜单
         */
        public abstract void onActionDown();

        /**
         * 长按开始录制
         */
        public abstract void onBegin();

        /**
         * 松开结束录制
         */
        public abstract void onEnd();

        /**
         * 响应单击和手势离开
         *
         * @param isClickRecord true 单击开始录制 false 单击结束录制
         */
        public abstract void onActionUp(boolean isClickRecord);
    }


    public void recycle() {
        mHandler.removeMessages(MSG_SCALE_ANIM);
        mHandler.removeMessages(MSG_SCALE);
        if (null != anim) {
            anim.end();
        }
        ExtBtnRecord.this.removeCallbacks(mPostResetUI);
//        mHandler = null;
    }
}
