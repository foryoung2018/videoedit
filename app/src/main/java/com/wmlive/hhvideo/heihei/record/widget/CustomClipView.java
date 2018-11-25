package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.LruCache;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.heihei.record.engine.PlayerEngine;
import com.wmlive.hhvideo.heihei.record.manager.RecordSetting;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ScreenUtil;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.wmlive.hhvideo.R;

/**
 * Created by hsing on 2018/1/8.
 */

public class CustomClipView extends View {
    /**
     * 未知thumb标识值
     */
    public static final int NONE_THUMB_PRESSED = 0;
    /**
     * 选择范围最小时，thumb标识值
     */
    public static final int MIN_THUMB_PRESSED = 1;
    /**
     * 选择范围最大时，thumb标识值
     */
    public static final int MAX_THUMB_PRESSED = 2;
    /**
     * 指定当前值时，thumb标识值
     */
    public static final int CURRENT_THUMB_PRESSED = 3;

    private static final int MESSAGE_ALBUM_ITEM = 100;

    private Resources res;
    private Drawable mHandleDrawable;
    private Rect rectHandle;
    private Rect shadowRight;
    private int handleWidth;
    private int pressedThumb = NONE_THUMB_PRESSED;
    private Paint pShadow = new Paint();

    private int top, bottom;
    private boolean isEditorPrepared = false;
    private long lastRefleshTime;
    private int mLeftTime;
    private int mRightTime;
    private Bitmap mTempBmp;
    private int leftCount = 10;
    private int lastLeft = -10;
    private int mThumbnailWidth;
    private int mThumbnailHeight;
    //    private VirtualVideo mVirtualVideo;
    private int mDuration;
    private int mTotalDuration;
    private int mMaxDuration;
    private int itemTime;
    private int mViewMaxWidth;
    private float mWidthPerMs;
    private int mMaxWidth;
    private int visibleCount;

    private int startX;
    private Bitmap albumBitmap;
    private int completeCount;
    private int mStartTime;
    private int startPosition;
    private int endPosition;
    private int scrollPosition;
    private int videoMinDuration;
    private int videoMaxDuration;
    private int mMinWidth;
    private int lastX;
    private Rect srcAlbumRect;
    private Rect dstAlbumRect;
    private OnRangeChangeListener mListener;
    private int scrollTimeMillis;
    private int startTimeMillis;
    private int endTimeMillis;
    private boolean canScrollWithDrag;
    private int mContentWidth;
    private int pressDeltaX;
    private boolean isSingleClip;
    private int mSingleMaxWidth;


    public CustomClipView(Context context) {
        super(context);
        init();
    }

    public CustomClipView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomClipView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        initThread(getContext());
        res = getResources();
        handleWidth = res.getDimensionPixelSize(R.dimen.dimen_10);
        mThumbnailWidth = res.getDimensionPixelSize(R.dimen.trim_thumbnail_width);
        mThumbnailHeight = res.getDimensionPixelSize(R.dimen.trim_thumbnail_height);
        videoMinDuration = (int) RecordSetting.MIN_VIDEO_DURATION;
        videoMaxDuration = (int) RecordSetting.MAX_VIDEO_DURATION;
        mHandleDrawable = res.getDrawable(R.drawable.trim_seekbar_handle_right);
        rectHandle = new Rect();
        shadowRight = new Rect();
        srcAlbumRect = new Rect();
        dstAlbumRect = new Rect();
        pShadow.setAntiAlias(true);
        pShadow.setStyle(Paint.Style.FILL);
        pShadow.setColor(res.getColor(R.color.transparent_black50));
    }

    private void initTopBottom() {
        top = 0;
        bottom = getBottom() - getTop();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initTopBottom();
        drawAlbum(canvas);
        drawHandle(canvas);
    }


    private void drawAlbum(Canvas canvas) {
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                | Paint.FILTER_BITMAP_FLAG));
        if (mMaxWidth <= 0) {
            return;
        }
        if (completeCount != visibleCount) {
            Map<Integer, ThumbInfo> maps = mMemoryCache.snapshot();
            Set<Map.Entry<Integer, ThumbInfo>> entrySet = maps.entrySet();
            albumBitmap = Bitmap.createBitmap(mMaxWidth, mThumbnailHeight, Bitmap.Config.ARGB_8888);
            Canvas albumCanvas = new Canvas(albumBitmap);
            for (Map.Entry<Integer, ThumbInfo> item : entrySet) {
                ThumbInfo temp = item.getValue();
                if (temp != null && temp.bmp != null && !temp.bmp.isRecycled()) {
                    albumCanvas.drawBitmap(temp.bmp, null, temp.rect, null);
                    if (mTempBmp == null) {
                        mTempBmp = Bitmap.createBitmap(temp.bmp);
                    }
                } else {
                    if (mTempBmp != null && !mTempBmp.isRecycled()) {
                        albumCanvas.drawBitmap(mTempBmp, null, temp.rect, null);
                    }
                }
            }
            entrySet.clear();
            maps.clear();
        }
        srcAlbumRect.set(-scrollPosition, top, mMaxWidth, bottom);
        dstAlbumRect.set(startPosition, top, mMaxWidth + scrollPosition - handleWidth, bottom);
        KLog.i("xxxxx", "srcAlbumRect " + srcAlbumRect);
        KLog.i("xxxxx", "dstAlbumRect " + dstAlbumRect);
        canvas.drawBitmap(albumBitmap, srcAlbumRect, dstAlbumRect, null);
    }

    private void drawHandle(Canvas canvas) {
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                | Paint.FILTER_BITMAP_FLAG));
        if (endPosition == 0) {
            return;
        }
        shadowRight.set(endPosition-handleWidth, top, mMaxWidth + scrollPosition-handleWidth, bottom);
        canvas.drawRect(shadowRight, pShadow);

        rectHandle.set(startPosition - handleWidth, top, endPosition - handleWidth, bottom);
        KLog.i("xxxxx", "rectHandle " + rectHandle);
//        rectHandle.set(startPosition - handleWidth, top, endPosition-handleWidth , bottom);
//        KLog.i("xxxxx", "rectHandle " + rectHandle);
        mHandleDrawable.setBounds(rectHandle);
        mHandleDrawable.draw(canvas);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getX();
                pressDeltaX = handleWidth - (rectHandle.right - startX);
                lastX = x;
                pressedThumb = evalPressedThumb(startX);
                KLog.i("xxxx", "pressedThumb " + pressedThumb + " pressDeltaX " + pressDeltaX);
                canScrollWithDrag = Math.abs(mMaxWidth - endPosition + handleWidth) <= 5;
                if (pressedThumb == NONE_THUMB_PRESSED || pressedThumb == MAX_THUMB_PRESSED) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_SCROLL:
                if (MAX_THUMB_PRESSED == pressedThumb) {
//                    KLog.i("xxxx", "x " + x + " handleWidth " + handleWidth + " mMinWidth " + mMinWidth);
                    if (mMaxWidth > mMinWidth) {
                        // 当视频时长大于六秒时
                        if (isSingleClip) {
                            // 单视频裁剪
                            if (x - pressDeltaX >= handleWidth + mSingleMaxWidth) {
                                endPosition = handleWidth + mSingleMaxWidth;
                            } else if (x - pressDeltaX <= handleWidth + mMinWidth) {
                                endPosition = handleWidth + mMinWidth;
                            } else {
                                endPosition = x - pressDeltaX;
                            }
                            if (endPosition - scrollPosition >= handleWidth + mSingleMaxWidth || canScrollWithDrag) {
                                scrollPosition = endPosition - mSingleMaxWidth - handleWidth;
                            }
                        } else {
                            // 多格视频裁剪
                            if (x - pressDeltaX >= handleWidth + mMaxWidth) {
                                endPosition = handleWidth + mMaxWidth;
                            } else if (x - pressDeltaX <= handleWidth + mMinWidth) {
                                endPosition = handleWidth + mMinWidth;
                            } else {
                                endPosition = x - pressDeltaX;
                            }
                            if (endPosition - scrollPosition >= handleWidth + mMaxWidth || canScrollWithDrag) {
                                scrollPosition = endPosition - mMaxWidth - handleWidth;
                            }
                        }
                    }
//                    KLog.i("xxxxx", "endPosition " + endPosition);
                    calculateValue();
                    if (mListener != null) {
                        mListener.onValuesChanged(startTimeMillis, endTimeMillis, endTimeMillis - startTimeMillis, MAX_THUMB_PRESSED);
                    }
                    invalidate();
                } else if (NONE_THUMB_PRESSED == pressedThumb) {
                    if (mMaxWidth > mMinWidth) {
                        // 当视频时长大于六秒时
                        int deltaX = x - lastX;
                        scrollPosition += deltaX;
                        if (scrollPosition <= -(mMaxWidth - mMinWidth)) {
                            scrollPosition = -(mMaxWidth - mMinWidth);
                        } else if (scrollPosition >= 0) {
                            scrollPosition = 0;
                        }
                        if (deltaX < 0 && endPosition - scrollPosition >= mMaxWidth + handleWidth) {
                            endPosition = mMaxWidth + handleWidth + scrollPosition;
                        }
                    } else {
                        // 当视频时长达不到六秒时
//                        endPosition = mMaxWidth;
                    }
                    KLog.i("xxxxx", "endPosition " + endPosition);
                    calculateValue();
                    if (mListener != null) {
                        mListener.onValuesChanged(startTimeMillis, endTimeMillis, endTimeMillis - startTimeMillis, MAX_THUMB_PRESSED);
                    }
                    invalidate();
                    lastX = x;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (NONE_THUMB_PRESSED == pressedThumb) {

                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void calculateValue() {
        scrollTimeMillis = (int) Math.abs(scrollPosition / mWidthPerMs);
        startTimeMillis = scrollTimeMillis;
        endTimeMillis = startTimeMillis + (int) Math.abs((endPosition - handleWidth) / mWidthPerMs);
        KLog.i("xxxxx", "calculateValue " + ((int) Math.abs((endPosition - handleWidth) / mWidthPerMs)));
    }

    private int evalPressedThumb(float touchX) {
        int result = NONE_THUMB_PRESSED;
        boolean minThumbPressed = isInThumbRange(touchX, rectHandle.left - handleWidth,
                rectHandle.left + 2 * handleWidth);
        boolean maxThumbPressed = isInThumbRange(touchX, rectHandle.right - 2 * handleWidth,
                rectHandle.right + handleWidth);

        if (minThumbPressed && maxThumbPressed) {
            result = (touchX / getWidth() > 0.5f) ? MIN_THUMB_PRESSED
                    : MAX_THUMB_PRESSED;
        } else if (minThumbPressed) {
            result = MIN_THUMB_PRESSED;
        } else if (maxThumbPressed) {
            result = MAX_THUMB_PRESSED;
        }
        return result;
    }

    private boolean isInThumbRange(float touchX, int rectStart, int rectEnd) {
        return touchX > rectStart && touchX < rectEnd;
    }

    /**
     * @param duration      目前截取的时长
     * @param totalDuration 视频总时长
     * @param maxDuration   多个视频最大时长
     * @param maxDuration   视频开始时间
     */
    public void setPlayer(PlayerEngine playerEngine,int duration, int totalDuration, int maxDuration, int startTime) {
        this.invalidate();
        recycle();
        mDuration = duration;
        mTotalDuration = (int)(totalDuration);

        mMaxDuration = (int)(maxDuration );
        mStartTime = startTime;
        mContentWidth = DeviceUtils.getScreenWH(DCApplication.getDCApp())[0] - 2 * ScreenUtil.dip2px(getContext(),CustomTrimVideoView.MARGIN_LEFT_RIGHT) - 2 * handleWidth;
        mViewMaxWidth = DeviceUtils.getScreenWH(DCApplication.getDCApp())[0] - 2 *  ScreenUtil.dip2px(getContext(),CustomTrimVideoView.MARGIN_LEFT_RIGHT);

        itemTime = (int) ((maxDuration * mThumbnailWidth + 0.0f) / (mContentWidth + 0.0f));
        mWidthPerMs = (mContentWidth + 0.0f) / (mMaxDuration + 0.0f);
        mMaxWidth = (int) (mWidthPerMs * mTotalDuration);

        // 是否是单视频裁剪
        isSingleClip = totalDuration > maxDuration;
        // 单视频裁剪的控件操作宽度
        mSingleMaxWidth = (int) (mWidthPerMs * mMaxDuration);
        KLog.i("xxxx", "mViewMaxWidth " + mViewMaxWidth + " mContentWidth " + mContentWidth + " mViewMaxWidth " + mContentWidth + " mMaxWidth " + mMaxWidth);
        mMinWidth = (int) (mWidthPerMs * videoMinDuration);
        visibleCount = (int) Math.ceil(((mMaxWidth) + 0.0f) / (mThumbnailWidth)) + 2;// 可见的缩略图个数;
        KLog.i("xxxx", "visibleCount: " +visibleCount);
        completeCount = 0;
        isEditorPrepared = true;
        calculatePosition();
        setStartThumb(0,playerEngine);
    }

    private void calculatePosition() {
        startPosition = 0 + handleWidth;
        endPosition = (int) (mDuration * mWidthPerMs) + handleWidth ;
        scrollPosition = (int) (-mStartTime * mWidthPerMs);
        KLog.i("xxxx", "startPosition " + startPosition + " endPosition " + endPosition + " scrollPosition " + scrollPosition);
    }

    /**
     * 开始加载图片
     */
    public void setStartThumb(int scrollX,PlayerEngine playerEngine) {
        long tempflesh = System.currentTimeMillis();
        leftCount = (int) Math.ceil((scrollX + .0) / mThumbnailWidth) - 2;// 已滑动到左边的个数

        mLeftTime = (leftCount) * itemTime;
        mRightTime = (leftCount + visibleCount) * itemTime;
        KLog.i("xxxx", "setStartThumb mLeftTime " + mLeftTime + " mRightTime " + mRightTime+"visibleCount:"+visibleCount);
        KLog.d("snapshot","visibleCount--->"+visibleCount);
        if (lastRefleshTime == 0 || tempflesh - lastRefleshTime > 100 || lastLeft != leftCount) { // 减少刷新频率
            lastLeft = leftCount;
            Rect temp;
            int nLeft = leftCount * mThumbnailWidth;// 时间线左边的偏移
            for (int i = 0; i < visibleCount; i++) {
                int key = (leftCount + i) * itemTime;
                if (0 <= key) {
                    if (key > mTotalDuration) {
                        key = mTotalDuration;
                    }
                    ThumbInfo info = mMemoryCache.get(key);
                    if (null == info || null == info.bmp
                            || info.isloading == false) {
                        int mleft = nLeft + i * mThumbnailWidth;
                        if (mleft <= mMaxWidth) { // 防止超过视频边界
                            temp = new Rect(mleft, 0, mleft + mThumbnailWidth, mThumbnailHeight);
                            KLog.i("xxxx", "downloadImage key " + key + " temp " + temp + " mMaxWidth " + mMaxWidth);
                            downloadImage(key, temp,playerEngine);
                        }
                    }
                }
            }
            KLog.i("xxxx", "downloadImage key finish--> "+completeCount);
            lastRefleshTime = tempflesh;
            invalidate();
        }
    }

    private void downloadImage(final int nTime, final Rect rect,PlayerEngine playerEngine) {
        final Bitmap bitmap = getBitmapFromMemCache(nTime);
        boolean hasBmp = (bitmap != null && !bitmap.isRecycled());
        if (hasBmp) {
            mHandler.sendEmptyMessage(MESSAGE_ALBUM_ITEM);
        } else {
            if (isEditorPrepared && (!hasBmp)) {
                ThumbInfo info = new ThumbInfo(nTime, rect, null);
                info.isloading = true;
                mMemoryCache.put(nTime, info);
                getThreadPool().execute(new Runnable() {

                    @Override
                    public void run() {
                        if (mLeftTime <= nTime && nTime <= mRightTime) {
                            Bitmap tempBitmap = getBitmapFromMemCache(nTime);
                            boolean hasBmp = (tempBitmap != null && !tempBitmap.isRecycled());
                            if (hasBmp) {
                                return;
                            }
//                            Bitmap bitmap = Bitmap.createBitmap(mThumbnailWidth, mThumbnailHeight,
//                                    Bitmap.Config.ARGB_8888);
                            Bitmap bitmap = playerEngine.getSnapShot(nTime,mThumbnailWidth,mThumbnailHeight);
                            KLog.i("snapshot","customClipview--->"+bitmap);
                            if (bitmap!=null) {
                                completeCount++;
                                addBitmapToMemoryCache(nTime, rect, bitmap);
                                mHandler.sendEmptyMessage(MESSAGE_ALBUM_ITEM);
                            } else {
                                completeCount++;
                                ThumbInfo info = new ThumbInfo(nTime, rect, null);
                                info.isloading = false;
                                mMemoryCache.put(nTime, info);
                                if(bitmap!=null)
                                    bitmap.recycle();
                            }

//                            if (mVirtualVideo != null && mVirtualVideo.getSnapshot(nTime / 1000f, bitmap)) {
//                                completeCount++;
//                                addBitmapToMemoryCache(nTime, rect, bitmap);
//                                mHandler.sendEmptyMessage(MESSAGE_ALBUM_ITEM);
//                            } else {
//                                completeCount++;
//                                ThumbInfo info = new ThumbInfo(nTime, rect, null);
//                                info.isloading = false;
//                                mMemoryCache.put(nTime, info);
//                                bitmap.recycle();
//                            }
                        } else {
                            ThumbInfo info = new ThumbInfo(nTime, rect, null);
                            info.isloading = false;
                            mMemoryCache.put(nTime, info);
                        }
                    }
                });
            }
        }
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case MESSAGE_ALBUM_ITEM:
                    lastRefleshTime = System.currentTimeMillis();
                    invalidate();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 缓存Image的类，当存储Image的大小大于LruCache设定的值，系统自动释放内存
     */
    private LruCache<Integer, ThumbInfo> mMemoryCache;

    /**
     * 异步加载图片
     *
     * @param context
     */
    private void initThread(Context context) {
        // 获取系统分配给每个应用程序的最大内存，每个应用系统分配32M
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int mCacheSize = maxMemory / 64;
        mMemoryCache = new LruCache<Integer, ThumbInfo>(mCacheSize) {
            // 必须重写此方法，来测量Bitmap的大小
            @Override
            protected int sizeOf(Integer key, ThumbInfo value) {

                if (null != value && null != value.bmp
                        && !value.bmp.isRecycled()) {
                    return value.bmp.getByteCount();
                }
                return 0;
            }

            @Override
            protected void entryRemoved(boolean evicted, Integer key,
                                        ThumbInfo oldValue, ThumbInfo newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
                if (null != oldValue) {
                    oldValue.recycle();
                }
            }
        };
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
    private void addBitmapToMemoryCache(Integer key, Rect rect, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null && bitmap != null) {
            ThumbInfo info = new ThumbInfo(key, rect, bitmap);
            info.isloading = false;
            mMemoryCache.put(key, info);
        }
    }

    /**
     * 从内存缓存中获取一个Bitmap
     *
     * @param key
     * @return
     */
    private Bitmap getBitmapFromMemCache(Integer key) {
        ThumbInfo info = mMemoryCache.get(key);
        return (null != info) ? info.bmp : null;
    }

    /**
     * 取单个thumb
     */
    private class ThumbInfo {
        int nTime;// 图片时刻
        Rect rect;
        Bitmap bmp;
        boolean isloading = false;

        public ThumbInfo(int Time, Rect rect, Bitmap bmp) {
            this.nTime = Time;
            this.rect = rect;
            this.bmp = bmp;
        }

        @Override
        public String toString() {
            return "ThumbInfo [nTime=" + nTime + ", rect=" + rect + ", bmp="
                    + ((null != bmp) ? bmp.getByteCount() : "null")
                    + ", isloading=" + isloading + "]";
        }

        public void recycle() {

            if (null != bmp) {
                if (!bmp.isRecycled()) {
                    bmp.recycle();
                }
                bmp = null;
            }
        }
    }

    /**
     * 释放资源
     */
    public void recycle() {
        isEditorPrepared = false;
        lastRefleshTime = 0;
        mMemoryCache.evictAll();
        if (mTempBmp != null) {
            mTempBmp = null;
        }
        mLeftTime = 0;
        mRightTime = 0;
    }

    public void setOnRangeChangeListener(OnRangeChangeListener listener) {
        mListener = listener;
    }

    public interface OnRangeChangeListener {

        /**
         * 响应值发生改变
         * @param minValue
         * @param maxValue
         * @param changeType
         */
        void onValuesChanged(int minValue, int maxValue, int duration, int changeType);
    }
}
