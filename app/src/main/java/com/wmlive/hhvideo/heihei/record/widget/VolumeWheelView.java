package com.wmlive.hhvideo.heihei.record.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;


/**
 * 自定义刻度尺，支持惯性滚动
 * 用于dongci项目音量调节
 */
public class VolumeWheelView extends View {

    public static final String TAG = VolumeWheelView.class.getSimpleName();
    private Context mContext;

    private Rect mRect;

    private int max = 100; //最大刻度
    private int min = 0;
    private int mCountScale = max-min; //滑动的总刻度

    private float minVolume ;
    private static final int sumScale = 14;

    private VelocityTracker mVelocityTracker = null ; //处理触摸的速率
    public static int SNAP_VELOCITY = 600 ; //最小的滑动速率
    private int screenWidth = 720; //默认屏幕分辨率
    private int curScreen = 0 ; //当前屏幕
    private int mMinVelocity;
    private int mScaleMargin = 60; //刻度间距
    private int mScaleHeight = 2*8; //刻度线的高度
    private int mMidHeight = 2*40; //中间刻度线的高度
    private int mScaleMaxHeight = mScaleHeight*2; //整刻度线高度

    private int mRectWidth = max * mScaleMargin; //总宽度
    private int mRectHeight = 150; //高度

    private Scroller mScroller;
    private float mScrollLastX;

    private int mTempScale = screenWidth/mScaleMargin/2; //判断滑动方向
    private int mScreenMidCountScale = screenWidth/mScaleMargin/2; //中间刻度

    private OnScrollListener onScrollListener;

    private int countShow;

    private String tag = VolumeWheelView.class.getSimpleName();
    private VolumeChangeListener volumeChangeListener;
    private int mMaxVelocity;
    private int mTouchSlop;
    private boolean mSlide;
    private GestureDetector gestureDetector;


    public interface OnScrollListener{
        void onScrollScale(int scale);
    }

    public interface VolumeChangeListener{
        void onVolumeChange(int volume);
    }

    public void setVolumeChangeListener(VolumeChangeListener listener){
        this.volumeChangeListener = listener;
    }


    public VolumeWheelView(Context context) {
        super(context);
        mContext = context;
        init();
    }



    public VolumeWheelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();

    }

    public VolumeWheelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawScale(canvas);
        onDrawPointer(canvas); //画指针
        super.onDraw(canvas);
    }

    public void volumeUp(boolean isIncrease){
        if(isIncrease){
            smoothScrollBy(mScaleMargin,0);
        }else{
            smoothScrollBy(-mScaleMargin,0);
        }
        postInvalidate();
    }

    private boolean isMeasured;
    private int width,height;
    private float currentVolume = 100;
    private  float midVulume = 50;



    /**
     * 画刻度
     * */
    private void onDrawScale(Canvas canvas){
        if(canvas == null) return;
        Paint mPaint = new Paint();
        mPaint.setColor(Color.GRAY);
        mPaint.setTextSize(20);
        for(int i=0; i<max; i++){
            if(i!=0 && i!=max){
                if(i%10==0){ //整值
                    canvas.drawLine(i*mScaleMargin, mRectHeight, i*mScaleMargin, mRectHeight-mScaleMaxHeight, mPaint);
                    //整值文字
                    canvas.drawText(String.valueOf(i), i*mScaleMargin, mRectHeight-mScaleMaxHeight-10, mPaint);
                } else {
                    canvas.drawLine(i*mScaleMargin, mRectHeight, i*mScaleMargin, mRectHeight-mScaleHeight, mPaint);
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!isMeasured) {
            width = getMeasuredWidth();
            height = getMeasuredHeight();
            mScaleMargin = width/sumScale;
            width = mScaleMargin*sumScale;
            setMeasuredDimension(width, height);
            isMeasured = true;
        }
    }

    /**
     * 画指针
     * */
    private void onDrawPointer(Canvas canvas){
        if(canvas == null) return;
        Paint mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeWidth(2);
        canvas.drawLine(width/2, height/2-mMidHeight/2,
                width/2, height/2+mMidHeight/2, mPaint);
    }

    /**
     * 获取手机分辨率--W
     * */
    public static int getPhoneW(Context context){
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int disW = dm.widthPixels;
        return disW;
    }

    private void drawScale(Canvas canvas) {
        int finalX =  mScroller.getFinalX();
        int currX = mScroller.getCurrX();
        Log.i(VolumeWheelView.TAG, "computeScroll: finalX:" + finalX +" | currX:" + currX);
        currentVolume = max -  (currX/mScaleMargin);
//        volumeChangeListener.onVolumeChange((int)currentVolume);
        minVolume = currentVolume - sumScale/2;
        if(currentVolume>=max){
            volumeChangeListener.onVolumeChange((int)max);
        }else if(currentVolume<=min){
            volumeChangeListener.onVolumeChange((int)min);
        }else {
            volumeChangeListener.onVolumeChange((int)currentVolume);
        }

        Log.i(TAG, "drawScale:  currentVolume:" + currentVolume);
        float volume = minVolume;
        int oneDelta= currX%mScaleMargin;
        while (oneDelta<=width){
            drawScaleX(oneDelta,canvas,volume);
            volume ++;
            oneDelta += mScaleMargin;
        }
    }

    Paint mPaint;
    private void drawScaleX(int oneDelta, Canvas canvas, float volume) {
        if(volume<min||volume>max){
            return;
        }
        mPaint.setAlpha((int) (255-Math.abs(currentVolume - volume) % (sumScale / 2+1))*30);
        if(volume%5==0){
            canvas.drawLine(oneDelta, height/2-mScaleMaxHeight, oneDelta, height/2+mScaleMaxHeight, mPaint);
        }else{
            canvas.drawLine(oneDelta, height/2-mScaleHeight, oneDelta, height/2+mScaleHeight, mPaint);
        }
    }

    private void init() {
        mMinVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();
        ViewConfiguration configuration =  ViewConfiguration.get(mContext);
        mMaxVelocity = configuration.getScaledMaximumFlingVelocity();
        mTouchSlop = configuration.getScaledTouchSlop();
        screenWidth = getPhoneW(mContext);
        mTempScale = screenWidth/mScaleMargin/2; //判断滑动方向
        mScreenMidCountScale = screenWidth/mScaleMargin/2; //中间刻度
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(2);
        mScroller = new Scroller(mContext);
    }
    float velocityX = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();

        acquireVelocityTracker(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mScroller != null && !mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mScrollLastX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                int dataX = (int) (x - mScrollLastX);
                if (dataX < 0) {
                    if (currentVolume >= max) {
                        dataX= 0;
                    }
                } else{
                    if (currentVolume <= min) {
                        dataX= 0;
                    }
                }


                smoothScrollBy(dataX, 0);
                mScrollLastX = x;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (mVelocityTracker != null) {
                    // 添加触摸对象
                    // 计算当前速率
                    mVelocityTracker.computeCurrentVelocity(1000);
                    velocityX = mVelocityTracker.getXVelocity();
                    if (Math.abs(velocityX) > 600
                            ){
                        mSlide = true;
                    }else {
                        mSlide = false;
                    }
                }

                int scrollX = getScrollX();
                int scrollY = getScrollY();

                if (mSlide) {
                    Log.i(TAG, "fling:  scrollx:" + scrollX + " mScroller.getStartX():" + mScroller.getStartX() +" mScroller.getFinalX()" + mScroller.getFinalX() + "velocityX: "  +velocityX);
                    velocityX = velocityX/8;
                    mScroller.fling(mScroller.getStartX(), 0, (int) velocityX, 0, (int)(min-max)*mScaleMargin, (int) (max-min)*mScaleMargin, 0, 0);
                }else{

                }

                moveEnd();

                break;
        }
        return true;
    }

    private void moveEnd() {
        int padding = mScroller.getFinalX()%mScaleMargin;
        int finalX = 0;


        if(mScaleMargin - Math.abs(padding)>=Math.abs(padding)){
            finalX = -Math.abs(padding);
        }else{
            finalX = mScaleMargin - Math.abs(padding);
        }

        if(padding>0){
            smoothScrollBy(finalX, 0);
        }else if(padding<0){
            smoothScrollBy(-finalX, 0);
        }
        post(new Runnable() {
            @Override
            public void run() {
                correct();
            }
        });
        invalidate();
        if (mVelocityTracker != null )
            releaseVelocityTracker();
        mSlide= false;
    }


    private void acquireVelocityTracker(final MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }


    private void correct() {
        if(currentVolume>max){
            smoothScrollBy(-(int)(max-currentVolume)*mScaleMargin,0);
        }
        if(currentVolume<min) {
            smoothScrollBy(-(int)(min-currentVolume)*mScaleMargin,0);
        }
    }


    public void smoothScrollBy(int dx, int dy){
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy,1300);
    }

    public void smoothScrollTo(int fx, int fy){
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        smoothScrollBy(dx, dy);
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }


    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
//            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            if (mScroller.getCurrX() == mScroller.getFinalX()) { // over
                moveEnd();
            } else {
                int currX = mScroller.getCurrX();
                int finalX =  mScroller.getFinalX();
//                smoothScrollBy(currX-finalX,0);
            }
            invalidate();
//            mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy);
            if(currentVolume<=min){
                currentVolume=min;
            }
            if(currentVolume>=max){
                currentVolume=max;
            }

            volumeChangeListener.onVolumeChange((int)currentVolume);
        }
    }
}
