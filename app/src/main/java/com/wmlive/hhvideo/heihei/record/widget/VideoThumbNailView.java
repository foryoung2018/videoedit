package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

//import com.rd.vecore.VirtualVideo;
//import com.rd.vecore.models.EffectType;
import com.wmlive.hhvideo.heihei.beans.record.FilterEffectItem;
import com.wmlive.hhvideo.heihei.beans.record.ThumbNailInfo;
import com.wmlive.hhvideo.heihei.beans.record.TimeEffectItem;
import com.wmlive.hhvideo.heihei.record.engine.PlayerEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.wmlive.hhvideo.R;

/**
 * 视频特效控件
 */
public class VideoThumbNailView extends View {
    private FilterEffectItem mCurFilterEffectItem;
    private ArrayList<FilterEffectItem> mArrFilterEffectItem;
    private TimeEffectItem mTimeEffectItem;

    private Paint mPaintPosition, mPaintSpecialRect;
    private Rect mRectPosition = new Rect();
    private Rect mRectTimeEffectHandle = new Rect();
    private Rect mRectReverseLeft = new Rect(), mRectReverseRight = new Rect();
    private int mTotalWidth;
    private boolean drawTimeEffect = false;
    private OnEffectChangeListener effectChangeListener;
    private Drawable mBmpTimeEffectHandle, mBmpSeekbarHandle;
    private Drawable mBmpReverseLeft, mBmpReverseRight;
    private int mTimeEffectHandleWidth, mTimeEffectHandleHeight;
    private int mReverseHandleWidth, mReverseHandleHeight;
    private int mSeekbarHandleWidth, mSeekbarHandleHeight;
    private int mExtraHandleHeight;

    private PorterDuffXfermode mPdxMode;
    private Resources mResources;

    public VideoThumbNailView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoThumbNailView(Context context, AttributeSet attrs,
                              int defStyle) {
        super(context, attrs, defStyle);
        mPaintPosition = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintSpecialRect = new Paint(Paint.ANTI_ALIAS_FLAG);

        mResources = getResources();
        mPaintPosition.setColor(context.getResources().getColor(R.color.color_thumbnail_position_rect));
        mBmpTimeEffectHandle = mResources.getDrawable(R.drawable.effect_time_handle);
        mBmpSeekbarHandle = mResources.getDrawable(R.drawable.thumb_seekbar_handle_n);
        mBmpReverseLeft = mResources.getDrawable(R.drawable.btn_reverse_left_n);
        mBmpReverseRight = mResources.getDrawable(R.drawable.btn_reverse_right_n);
        mTimeEffectHandleWidth = mBmpTimeEffectHandle.getIntrinsicWidth();
        mTimeEffectHandleHeight = mBmpTimeEffectHandle.getIntrinsicHeight();
        mSeekbarHandleWidth = mBmpSeekbarHandle.getIntrinsicWidth();
        mSeekbarHandleHeight = mBmpSeekbarHandle.getIntrinsicHeight();
        mReverseHandleWidth = mBmpReverseLeft.getIntrinsicWidth();
        mReverseHandleHeight = mBmpReverseLeft.getIntrinsicHeight();
        mPdxMode = new PorterDuffXfermode(PorterDuff.Mode.SRC);
        mArrFilterEffectItem = new ArrayList<>();
//        mTimeEffectItem = new TimeEffectItem(this.getContext());
        setWillNotDraw(false);
        initThread(context);
    }


    private final String TAG = "VideoThumbNailView";

//    private VirtualVideo mVirtualVideo;

    private PlayerEngine playerEngine;
    private float mDuration;
    private float mPosition;
    private int mPadding;

    private int[] params = new int[2];

    private int thumbW = 90, thumbH = 160;

    public int[] setPlayer(PlayerEngine playerEngine, int width, int padding, float duration) {
//        mVirtualVideo = virtualVideo;
        this.playerEngine = playerEngine;
        thumbW = getResources().getDimensionPixelSize(R.dimen.thumbnail_width);
        thumbH = getResources().getDimensionPixelSize(R.dimen.thumbnail_height);
        mPadding = padding;
        width -= (padding * 2);

        mDuration = duration;

        maxCount = width / thumbW + 1;

        params[0] = thumbW * maxCount;
        itemTime = mDuration / maxCount;

        if (params[0] > width) {
            params[0] = width;
        }
        mTotalWidth = params[0] - mSeekbarHandleWidth;
        params[1] = thumbH;
        if (mSeekbarHandleHeight > params[1]) {
            mExtraHandleHeight = (mSeekbarHandleHeight - params[1]) / 2;
        }
        return params;
    }

    private final int THUMBITEM = 10;
    private final Handler mhandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case THUMBITEM:
                    invalidate();
                    break;

                default:
                    break;
            }

        }

        ;
    };

    /**
     * 缓存Image的类，当存储Image的大小大于LruCache设定的值，系统自动释放内存
     */
    private HashMap<Integer, ThumbNailInfo> mMemoryCache;

    /**
     * 异步加载图片
     *
     * @param context
     */
    private void initThread(Context context) {
        mMemoryCache = new HashMap<Integer, ThumbNailInfo>();
    }

    /**
     * 下载Image的线程池
     */
    private ExecutorService mImageThreadPool = null;

    /**
     * 获取线程池的方法，因为涉及到并发的问题，我们加上同步锁
     *
     * @return
     */
    private ExecutorService getThreadPool() {
        if (mImageThreadPool == null) {
            synchronized (ExecutorService.class) {
                if (mImageThreadPool == null) {
                    // 单线程加载图片
                    mImageThreadPool = Executors.newSingleThreadExecutor();
                }
            }
        }
        return mImageThreadPool;
    }


    /**
     * 添加Bitmap到内存缓存
     *
     * @param key
     * @param bitmap
     */
    private void addBitmapToMemoryCache(Integer key, Rect src, Rect dst,
                                        boolean isleft, boolean isright, Bitmap bitmap) {
        ThumbNailInfo info = new ThumbNailInfo(key, src, dst, isleft, isright);
        info.bmp = bitmap;
        mMemoryCache.put(key, info);
    }

    /**
     * 从内存缓存中获取一个Bitmap
     *
     * @param key
     * @return
     */
    public Bitmap getBitmapFromMemCache(Integer key) {
        ThumbNailInfo info = mMemoryCache.get(key);
        return (null != info) ? info.bmp : null;
    }

    private int maxCount = 40;
    private float itemTime = 0;

    /**
     * 开始加载图片
     */
    public void setStartThumb() {
        Rect tdst = new Rect(0, 0, thumbW, thumbH),
                src = new Rect(0, 0, thumbW, thumbH);

        int splitTime = (int) (itemTime * 1000 / 2);

        downloadImage(splitTime, src, new Rect(tdst), true, false);

        for (int i = 1; i < maxCount; i++) {
            tdst = new Rect(tdst.right, tdst.top, tdst.right + thumbW,
                    tdst.bottom);
            splitTime += (itemTime * 1000);
            downloadImage(splitTime, src, tdst, false, false);
        }
    }

    private void downloadImage(final int nTime, final Rect src, final Rect dst,
                               final boolean isleft, final boolean isright) {

        Bitmap bitmap = getBitmapFromMemCache(nTime);
        if (bitmap != null) {
            mhandler.sendEmptyMessage(THUMBITEM);
        } else {
            if (mMemoryCache.get(nTime) == null) {
                addBitmapToMemoryCache(nTime, src, dst, isleft, isright, bitmap);

                getThreadPool().execute(new Runnable() {

                    @Override
                    public void run() {
                        if(playerEngine.isNull()){

                        }else{
                            Bitmap bitmap = playerEngine.getSnapShot(nTime);
                            if(bitmap!=null){//获取截图成功
//                                Bitmap bitmap = Bitmap.createBitmap(thumbW, thumbH,
//                                        Config.ARGB_8888);
                                addBitmapToMemoryCache(nTime, src, dst, isleft, isright, bitmap);
                                mhandler.sendEmptyMessage(THUMBITEM);
                            }
                        }

//                        if (null != mVirtualVideo
//                                && mVirtualVideo.getSnapshot(nTime / 1000f, bitmap)) {
//                            // 将Bitmap 加入内存缓存
//                            addBitmapToMemoryCache(nTime, src, dst, isleft, isright, bitmap);
//                            mhandler.sendEmptyMessage(THUMBITEM);
//                        } else {
//                            bitmap.recycle();
//                        }
                    }
                });
            }
        }

    }

    /**
     * 设置当前进度
     *
     * @param position 当前进度
     */
    public void setPosition(float position) {
        mPosition = position;
        int positionLeft = (int) (mTotalWidth * ((0.0 + mPosition) / (0.0 + mDuration)));
        int effectLeft = (int) (params[0] * ((0.0 + mPosition) / (0.0 + mDuration)));

        mRectPosition.set(positionLeft, 0 - mExtraHandleHeight,
                positionLeft + mSeekbarHandleWidth, thumbH + mExtraHandleHeight);

        if (mCurFilterEffectItem != null) {
            Rect rect = mCurFilterEffectItem.getSpecialRect();
            rect.right = effectLeft;
        }
        invalidate();
    }

    /**
     * 根据进度位置计算当前时间
     */
    public float getPostionByHandle(int handlerPos) {
        int minPos = 0;
        int maxPos = getWidth() - mSeekbarHandleWidth;
        if (handlerPos < minPos) {
            handlerPos = minPos;
        }
        if (handlerPos > maxPos) {
            handlerPos = maxPos;
        }
        return (float) ((handlerPos + 0.0) / (mTotalWidth + 0.0) * mDuration);
    }


    /**
     * 绘制滤镜特效区域
     */
    public void drawEffectRect(FilterEffectItem filterEffectItem) {
        int left = (int) (params[0] * ((0.0 + filterEffectItem.getStartTime()) / (0.0 + mDuration)));
        int right = (int) (params[0] * ((0.0 + filterEffectItem.getEndTime()) / (0.0 + mDuration)));
        filterEffectItem.setSpecialRect(left, 0, right, params[1]);
    }


    /**
     * 绘制滤镜特效区域
     */
    public void drawEffectRect(float start, FilterEffectItem filterEffectItem) {
        int left = (int) (params[0] * ((0.0 + start) / (0.0 + mDuration)));
        mCurFilterEffectItem = filterEffectItem;
        mCurFilterEffectItem.setStartTime(start);
        mCurFilterEffectItem.setSpecialRect(left, getTop(), left, getBottom());
    }

    /**
     * 停止绘制滤镜特效区域
     */
    public float stopDrawEffectRect(float end) {
        int right = (int) (params[0] * ((0.0 + end) / (0.0 + mDuration)));
        float startTime = 0;
        if (mCurFilterEffectItem != null) {
            mCurFilterEffectItem.setEndTime(end);
            Rect rect = mCurFilterEffectItem.getSpecialRect();
            rect.right = right;
            startTime = mCurFilterEffectItem.getStartTime();
            mCurFilterEffectItem = null;
        }
        invalidate();
        return startTime;
    }

    /**
     * 绘制时间特效区域
     */
    public void drawTimeEffectRect(float startTime, float endTime) {
//        int left = (int) (params[0] * ((0.0 + startTime) / (0.0 + mDuration)));
//        int right = (int) (params[0] * ((0.0 + endTime) / (0.0 + mDuration)));
//        int timeEffectRectWidth = right - left;
//        if (mTimeEffectItem.getType() == EffectType.REVERSE) {
//            mRectReverseLeft.left = left;
//            mRectReverseLeft.top = params[1] / 2 - mReverseHandleHeight / 2;
//            mRectReverseLeft.right = left + mReverseHandleWidth;
//            mRectReverseLeft.bottom = params[1] / 2 + mReverseHandleHeight / 2;
//            mRectReverseRight.left = right - mReverseHandleWidth;
//            mRectReverseRight.top = mRectReverseLeft.top;
//            mRectReverseRight.right = right;
//            mRectReverseRight.bottom = mRectReverseLeft.bottom;
//        } else {
//            mRectTimeEffectHandle.left = left + timeEffectRectWidth / 2 - mTimeEffectHandleWidth / 2;
//            mRectTimeEffectHandle.top = params[1] / 2 - mTimeEffectHandleHeight / 2;
//            mRectTimeEffectHandle.right = left + timeEffectRectWidth / 2 + mTimeEffectHandleWidth / 2;
//            mRectTimeEffectHandle.bottom = params[1] / 2 + mTimeEffectHandleHeight / 2;
//        }
//        mTimeEffectItem.setStartTime(startTime);
//        mTimeEffectItem.setEndTime(endTime);
//        mTimeEffectItem.setEffectRect(left, 0, right, params[1]);
//        invalidate();
    }


    /**
     * 绘制时间特效区域
     */
    public void drawTimeEffectRect(float position) {
        if (mTimeEffectItem == null) {
            return;
        }
//        if (mTimeEffectItem.getType() == EffectType.NONE) {
//            mTimeEffectItem.setEffectRect(0, 0, 0, 0);
//            mRectTimeEffectHandle.set(0, 0, 0, 0);
//            mRectReverseLeft.set(0, 0, 0, 0);
//            mRectReverseRight.set(0, 0, 0, 0);
//            invalidate();
//            return;
//        }

        int timeEffectRectWidth = params[0] / 5;
        int left = (int) (mTotalWidth * ((0.0 + position) / (0.0 + mDuration)) - timeEffectRectWidth / 2);
        int right = left + timeEffectRectWidth;
        if (left < 0) {
            left = 0;
            right = left + timeEffectRectWidth;
        }
        if (right > params[0]) {
            right = params[0];
            left = right - timeEffectRectWidth;
        }

        mRectTimeEffectHandle.left = left + timeEffectRectWidth / 2 - mTimeEffectHandleWidth / 2;
        mRectTimeEffectHandle.top = params[1] / 2 - mTimeEffectHandleHeight / 2;
        mRectTimeEffectHandle.right = left + timeEffectRectWidth / 2 + mTimeEffectHandleWidth / 2;
        mRectTimeEffectHandle.bottom = params[1] / 2 + mTimeEffectHandleHeight / 2;

        mTimeEffectItem.setEffectRect(left, 0, right, params[1]);
        float startTime = getPostionByHandle(left);
        float endTime = getPostionByHandle(right);
        mTimeEffectItem.setStartTime(startTime);
        mTimeEffectItem.setEndTime(endTime);
        invalidate();
    }


    /**
     * 绘制倒序区域
     *
     * @param position
     */
    private void drawReverseRect(float position) {
        int left = mTimeEffectItem.getEffectRect().left;
        int right = mTimeEffectItem.getEffectRect().right;
        if (mHandlePressed == HANDLE_PRESS_LEFT) {
            left = (int) (params[0] * ((0.0 + position) / (0.0 + mDuration)));
            if (left < 0) {
                left = 0;
            }
            if (left > mRectReverseRight.left - mReverseHandleWidth) {
                left = mRectReverseRight.left - mReverseHandleWidth;
            }
            mRectReverseLeft.left = left;
            mRectReverseLeft.right = left + mReverseHandleWidth;
        } else {
            right = (int) (params[0] * ((0.0 + position) / (0.0 + mDuration)));
            if (right > params[0]) {
                right = params[0];
            }
            if (right < mRectReverseLeft.right + mReverseHandleWidth) {
                right = mRectReverseLeft.right + mReverseHandleWidth;
            }
            mRectReverseRight.left = right - mReverseHandleWidth;
            mRectReverseRight.right = right;
        }
        mTimeEffectItem.setEffectRect(left, 0, right, params[1]);
        mTimeEffectItem.setStartTime(getPostionByHandle(left));
        mTimeEffectItem.setEndTime(getPostionByHandle(right));
        invalidate();
    }

    /**
     * 清除时间特效区域
     */
    public void recycleTimeEffectRect() {
        mTimeEffectItem.setEffectRect(0, 0, 0, 0);
        invalidate();
    }

    private final int HANDLE_PRESS_LEFT = 1;
    private final int HANDLE_PRESS_RIGHT = 2;
    private boolean isTouchTimeEffectRect = false;
    private int mHandlePressed = 0;
    private float mTouchDownX, mTouchDownY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        float position = getPostionByHandle((int) x);
        if (action == MotionEvent.ACTION_DOWN) {
            mTouchDownX = x;
            mTouchDownY = y;
//            if (mTimeEffectItem.getType() == EffectType.REVERSE) {
//                mHandlePressed = touchReverseHandle((int) x, (int) y);
//                if (mHandlePressed != 0) {
//                    return true;
//                }
//            } else {
//                if (isTouchTimeEffectHandle((int) x, (int) y)) {
//                    isTouchTimeEffectRect = true;
//                    return true;
//                }
//            }
            if (effectChangeListener != null) {
                effectChangeListener.onPositionMove(position);
            }
            setPosition(position);
            return true;
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (mHandlePressed != 0) {
                drawReverseRect(position);
                return true;
            }
            if (isTouchTimeEffectRect) {
                drawTimeEffectRect(position);
                return true;
            }
            if (effectChangeListener != null) {
                effectChangeListener.onPositionMove(position);
            }
            setPosition(position);
        } else if (action == MotionEvent.ACTION_UP) {
            if (isTouchTimeEffectRect || mHandlePressed != 0) {
                effectChangeListener.onPositionUp();
            }
            touchUp();
            isTouchTimeEffectRect = false;
        }
        return super.onTouchEvent(event);
    }


    /**
     * 设置滤镜特效列表
     *
     * @param arrFilterEffectItem
     */
    public void setFilterEffectList(ArrayList<FilterEffectItem> arrFilterEffectItem) {
        mArrFilterEffectItem = arrFilterEffectItem;
    }


    /**
     * 设置时间特效
     */
    public void setTimeEffect(TimeEffectItem timeEffectItem) {
        mTimeEffectItem = timeEffectItem;
    }

    /**
     * 判断是否在特效把手区域
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isTouchTimeEffectHandle(int x, int y) {
        if (x >= mRectTimeEffectHandle.left && x <= mRectTimeEffectHandle.right) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否点击到倒序左右把手
     *
     * @param x
     * @param y
     * @return
     */
    private int touchReverseHandle(int x, int y) {
        if (x >= mRectReverseLeft.left && x <= mRectReverseLeft.right) {
            mBmpReverseLeft = mResources.getDrawable(R.drawable.btn_reverse_left_p);
            return HANDLE_PRESS_LEFT;
        }
        if (x >= mRectReverseRight.left && x <= mRectReverseRight.right) {
            mBmpReverseRight = mResources.getDrawable(R.drawable.btn_reverse_right_p);
            return HANDLE_PRESS_RIGHT;
        }
        return 0;
    }

    /**
     * 触摸结束还原状态
     */
    private void touchUp() {
        mBmpReverseLeft = mResources.getDrawable(R.drawable.btn_reverse_left_n);
        mBmpReverseRight = mResources.getDrawable(R.drawable.btn_reverse_right_n);
        mHandlePressed = 0;
        invalidate();
    }

    private Bitmap mTempBmp;

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                | Paint.FILTER_BITMAP_FLAG));
        for (Entry<Integer, ThumbNailInfo> item : mMemoryCache.entrySet()) {
            ThumbNailInfo temp = item.getValue();
            if (temp != null && temp.bmp != null && !temp.bmp.isRecycled()) {
                canvas.drawBitmap(temp.bmp, null, temp.dst, null);
                if (mTempBmp == null) {
                    mTempBmp = temp.bmp;
                }
            } else {
                if (mTempBmp != null && !mTempBmp.isRecycled()) {
                    canvas.drawBitmap(mTempBmp, null, temp.dst, null);
                }
            }
        }
        int layer = canvas.saveLayer(0, getTop(), params[0], getBottom(), null,
                Canvas.ALL_SAVE_FLAG);

        canvas.drawColor(Color.TRANSPARENT);
        if (drawTimeEffect) {
//            mPaintSpecialRect.setColor(mTimeEffectItem.getColor());
            canvas.drawRect(mTimeEffectItem.getEffectRect(), mPaintSpecialRect);
//            if (mTimeEffectItem.getType() == EffectType.REVERSE) {
//                mBmpReverseLeft.setBounds(mRectReverseLeft);
//                mBmpReverseLeft.draw(canvas);
//                mBmpReverseRight.setBounds(mRectReverseRight);
//                mBmpReverseRight.draw(canvas);
//            } else if (mTimeEffectItem.getType() != EffectType.NONE) {
//                mBmpTimeEffectHandle.setBounds(mRectTimeEffectHandle);
//                mBmpTimeEffectHandle.draw(canvas);
//            }
        } else {
            for (FilterEffectItem item : mArrFilterEffectItem) {
//                mPaintSpecialRect.setColor(item.getColor());
                mPaintSpecialRect.setXfermode(mPdxMode);
                canvas.drawRect(item.getSpecialRect(), mPaintSpecialRect);
            }
        }
        canvas.restoreToCount(layer);
        mBmpSeekbarHandle.setBounds(mRectPosition);
        mBmpSeekbarHandle.draw(canvas);
        super.onDraw(canvas);
    }

    /**
     * 设置绘制出时间特效区域
     *
     * @param draw true 显示时间特效区域，false 显示滤镜特效区域
     */
    public void setDrawTimeEffect(boolean draw) {
        drawTimeEffect = draw;
        invalidate();
    }

    /**
     * 设置特效回调函数
     *
     * @param listener
     */
    public void setOnEffectChangeListener(OnEffectChangeListener listener) {
        effectChangeListener = listener;
    }

    /**
     * 释放资源
     */
    public void recycle() {
        if (playerEngine.isNull()) {
            playerEngine.release();
            playerEngine = null;
        }
        for (Entry<Integer, ThumbNailInfo> item : mMemoryCache.entrySet()) {
            ThumbNailInfo temp = item.getValue();
            if (null != temp) {
                temp.recycle();
            }
        }
        if (mTempBmp != null) {
            mTempBmp.recycle();
            mTempBmp = null;
        }
        mMemoryCache.clear();
        invalidate();
    }


    /**
     * 特效回调函数
     */
    public interface OnEffectChangeListener {
        void onPositionMove(float position);

        void onPositionUp();
    }

}

