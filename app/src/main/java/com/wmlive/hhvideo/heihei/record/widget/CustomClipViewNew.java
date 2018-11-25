package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LruCache;
import android.view.MotionEvent;
import android.view.View;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.heihei.record.engine.PlayerEngine;
import com.wmlive.hhvideo.heihei.record.manager.RecordSetting;
import com.wmlive.hhvideo.heihei.record.presenter.EditLocalPresenter;
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

public class CustomClipViewNew extends View {
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
    private static final String TAG = CustomClipViewNew.class.getSimpleName() + "------";
    private float clipDuration = 2000;
    private int downloadCount = 15;

    private Resources res;
    private Drawable mHandleDrawable;
    private Rect rectHandle;
    private Rect shadowRight;
    private Rect shadowLeft;
    private int handleWidth;
    private int handleHeight;
    private int pressedThumb = NONE_THUMB_PRESSED;
    private Paint pShadow = new Paint();

    private int top, bottom;
    private boolean isEditorPrepared = false;
    private long lastRefleshTime;
    private int mLeftTime;
    private int mRightTime;
    private Bitmap mTempBmp;
    private int leftCount = 50;
    private int lastLeft = -50;
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

    private int handleLength;

    private Context context;
    private int leftOffset;
    private int rightOffset;
    private boolean isChangeing = false;
    private PlayerEngine engine;

    public CustomClipViewNew(Context context) {
        super(context);
        init();
    }

    public CustomClipViewNew(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomClipViewNew(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private int[] screenWH =new int[2];

    private void init() {
        res = getResources();
        screenWH[0]= res.getDisplayMetrics().widthPixels;
        screenWH[1]= res.getDisplayMetrics().heightPixels;
        setWillNotDraw(false);
//        setBackgroundColor(getResources().getColor(R.color.black));
        initThread(getContext());
        handleWidth = res.getDimensionPixelSize(R.dimen.dimen_2);
        handleHeight = res.getDimensionPixelSize(R.dimen.dimen_2);
        handleLength = res.getDimensionPixelSize(R.dimen.dimen_137);
        mThumbnailWidth = res.getDimensionPixelSize(R.dimen.trim_thumbnail_width);
        mThumbnailHeight = res.getDimensionPixelSize(R.dimen.trim_thumbnail_height);
        videoMinDuration = (int) RecordSetting.MIN_VIDEO_DURATION;
        videoMaxDuration = (int) RecordSetting.MAX_VIDEO_DURATION;
        mHandleDrawable = res.getDrawable(R.drawable.icon_video_upload_district_border);
        rectHandle = new Rect();
        shadowRight = new Rect();
        shadowLeft = new Rect();
        srcAlbumRect = new Rect();
        dstAlbumRect = new Rect();
        pShadow.setAntiAlias(true);
        pShadow.setStyle(Paint.Style.FILL);
        pShadow.setColor(res.getColor(R.color.transparent_black50));
    }

    private void initTopBottom() {
        top = handleHeight;
        bottom = getBottom() - getTop()-handleHeight;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initTopBottom();
        drawAlbum(canvas);
        drawShadow(canvas);
        drawProgress(canvas);
//        Paint paint = new Paint();
//        paint.setColor(getResources().getColor(R.color.black));
//        canvas.drawRect(new Rect(0,top,screenWH[0],bottom),paint);
        drawHandle(canvas);
    }

    private void drawProgress(Canvas canvas) {
//        Paint paint = new Paint();
//        paint.setStrokeWidth(5);
//        paint.setColor(Color.WHITE);
//        canvas.drawLine(leftOffset+handleWidth+progress*(handleLength+.0f),handleHeight,leftOffset+handleWidth+progress*(handleLength+.0f),mThumbnailHeight-handleHeight,paint);
    }

    private void drawShadow(Canvas canvas) {
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                | Paint.FILTER_BITMAP_FLAG));
        if (endPosition == 0) {
            return;
        }
        shadowRight.set(startPosition, top-handleHeight, leftOffset+handleWidth, bottom+handleHeight);
        shadowLeft.set(rightOffset-handleWidth , top-handleHeight, endPosition+handleWidth+500, bottom+handleHeight);
        canvas.drawRect(shadowRight, pShadow);
        canvas.drawRect(shadowLeft, pShadow);

    }


    private void drawHandle(Canvas canvas) {
        rectHandle.set(leftOffset, top-handleHeight, rightOffset, bottom+handleHeight);
        KLog.i("xxxxx", "rectHandle " + rectHandle);
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
                if(!isChangeing){
                    mListener.onValuesChangeStart();
                    isChangeing = true;
                }

                int deltaX = x - lastX;
                scrollPosition += deltaX;
                KLog.i("xxxxx", "endPosition " + endPosition);
                calculateValue();
                if (mListener != null) {
                    mListener.onValuesChanged(startTimeMillis, (long)(startTimeMillis+clipDuration), (long) clipDuration, MAX_THUMB_PRESSED);
                }
                invalidate();
                lastX = x;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (NONE_THUMB_PRESSED == pressedThumb) {

                }
                isChangeing = false;
                mListener.onValuesChangeEnd();
                break;
        }
        return super.onTouchEvent(event);
    }

    public void increase(float delta){
        scrollPosition += (int) (delta * mWidthPerMs);
        calculateValue();
        if (mListener != null) {
            mListener.onValuesChanged((long)startTimeMillis, (long)(startTimeMillis+clipDuration), (long)clipDuration, MAX_THUMB_PRESSED);
            mListener.onValuesChangeEnd();
        }
        invalidate();
    }

    private void calculateValue() {
        if(scrollPosition>=0){
            scrollPosition = 0;
        }else if(scrollPosition <=  handleLength - mMaxWidth ){
            scrollPosition = handleLength - mMaxWidth;
        }
        scrollTimeMillis = (int) Math.abs(scrollPosition / mWidthPerMs);
        startTimeMillis = scrollTimeMillis;
        startPosition = scrollPosition + handleWidth + leftOffset;
        endPosition = startPosition + mMaxWidth -handleWidth*2;
        endTimeMillis = startTimeMillis + (int) Math.abs((endPosition - handleWidth) / mWidthPerMs);
        KLog.i("-----yang", "calculateValue****scrollPosition [" +scrollPosition +"]****startTimeMillis" + "[" +startTimeMillis +"]");
        KLog.i(EditLocalPresenter.TAG, "setStartTime() called with: startTimeMillis = [" + startTimeMillis + "], endTimeMillis = [" + endTimeMillis + "], clipDuration = [" + clipDuration + "]");
        setStartThumb(scrollPosition,engine);
    }

    /**
     * 开始加载图片
     */
    public void setStartThumb(int scrollX,PlayerEngine playerEngine) {
        long tempflesh = System.currentTimeMillis();

        leftCount = -(int) Math.ceil((scrollX + .0) / mThumbnailWidth) ;// 已滑动到左边的个数
        KLog.i(TAG,"leftCount: " +leftCount);

        mLeftTime = (leftCount) * itemTime;
        mRightTime = (leftCount + visibleCount) * itemTime;

        if (lastRefleshTime == 0 || tempflesh - lastRefleshTime > 100 || lastLeft != leftCount) { // 减少刷新频率

            lastLeft = leftCount;
            Rect temp;
            int nLeft = leftCount * mThumbnailWidth;// 时间线左边的偏移

            for (int i = 0; i < downloadCount; i++) {

                int key = (leftCount + i) * itemTime;
                if (0 <= key) {
                    if (key > mTotalDuration) {
                        key = mTotalDuration;
                    }
                    ThumbInfo info = mMemoryCache.get(key);
                    if (null == info || null == info.bmp || info.isloading == false) {

                        int mleft =(leftCount + i)%downloadCount * mThumbnailWidth;
                        temp = new Rect(mleft, 0, mleft + mThumbnailWidth, mThumbnailHeight);
                        KLog.i(TAG, "setStartThumb() called with: key = [" + key + "], temp = [" + temp + "]");
                        downloadImage(key, temp,playerEngine);

                    }
                }
            }

            KLog.i("xxxx", "downloadImage key finish--> "+completeCount);
            lastRefleshTime = tempflesh;
            invalidate();
        }
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
     * @param ratio
     */
    public void setPlayer(PlayerEngine playerEngine, int duration, int totalDuration, int maxDuration, int startTime, float ratio) {
        this.invalidate();
        recycle();
        mDuration = duration;
        clipDuration = duration;
        mTotalDuration = (int)(totalDuration);
        KLog.i("xxxx", "snap-->duration " + duration + " totalDuration> " + totalDuration + " maxDuration " + maxDuration );
        mMaxDuration = (int)(maxDuration );
        mThumbnailWidth = (int)((mThumbnailHeight+0.f)*ratio);
        mStartTime = startTime;
        mContentWidth = DeviceUtils.getScreenWH(DCApplication.getDCApp())[0] - 2 * ScreenUtil.dip2px(getContext(),CustomTrimVideoView.MARGIN_LEFT_RIGHT) - 2 * handleWidth;
//        mContentWidth = screenWH[0] - 2 * handleWidth;
        mViewMaxWidth = DeviceUtils.getScreenWH(DCApplication.getDCApp())[0] - 2 *  ScreenUtil.dip2px(getContext(),CustomTrimVideoView.MARGIN_LEFT_RIGHT);
        int size = res.getDimensionPixelSize(R.dimen.dimen_137);
//        itemTime = (int) ((maxDuration * mThumbnailWidth + 0.0f) / (screenWH[0] + 0.0f));
//        mWidthPerMs = (mContentWidth + 0.0f) / (mMaxDuration + 0.0f);
        mWidthPerMs = (size + .0f )/ (clipDuration + .0f);
        itemTime = (int) ((mThumbnailWidth + 0.0f)/mWidthPerMs);
        mMaxWidth = (int) (mWidthPerMs * mTotalDuration);
        leftOffset= (screenWH[0] - handleLength) / 2;
        rightOffset= (screenWH[0] + handleLength) / 2;
        // 是否是单视频裁剪
        isSingleClip = totalDuration > maxDuration;
        // 单视频裁剪的控件操作宽度
        mSingleMaxWidth = (int) (mWidthPerMs * mMaxDuration);
//        mSingleMaxWidth = (int) (mWidthPerMs * size);
        mMinWidth = (int) (mWidthPerMs * videoMinDuration);
        visibleCount = (int) Math.ceil(((mMaxWidth) + 0.0f) / (mThumbnailWidth)) + 2;// 可见的缩略图个数;
        KLog.i("xxxx", "visibleCount: " +visibleCount);
        completeCount = 0;
        isEditorPrepared = true;
        calculatePosition();
        engine = playerEngine;
        downloadCount = screenWH[0]/mThumbnailWidth+5;
        setStartThumb(0,playerEngine);
    }

    private void drawAlbum(Canvas canvas) {
        if (completeCount != visibleCount) {
            Map<Integer, ThumbInfo> maps = mMemoryCache.snapshot();
            Set<Map.Entry<Integer, ThumbInfo>> entrySet = maps.entrySet();
//            albumBitmap = Bitmap.createBitmap(downloadCount*mThumbnailWidth, mThumbnailHeight, Bitmap.Config.RGB_565);
//            Canvas albumCanvas = new Canvas(albumBitmap);
            for(int i = 0 ; i<downloadCount ; i++){

                int key = (leftCount + i - 5)*itemTime;
                if(key>=mTotalDuration){
                    return;
                }
                ThumbInfo temp = maps.get(key);
                int left = scrollPosition%mThumbnailWidth + leftOffset+ handleWidth  + (i-5)*mThumbnailWidth;
                int top = handleHeight;
                int right = left + mThumbnailWidth;
                int bottom = mThumbnailHeight-handleHeight;
                Rect srcAlbumRect  = null;

                if(key>=mTotalDuration-itemTime){
//                    right = left + (int)((mTotalDuration-key)/itemTime+0.f)*mThumbnailWidth;
                    right = left + mThumbnailWidth*(mTotalDuration-key)/itemTime-handleWidth;
                }

//
//                if((leftCount+handleLength/mThumbnailWidth+1)*itemTime>=mTotalDuration){
//                    if(key >= mTotalDuration-itemTime){
//                        if(left>=rightOffset-2*handleWidth){
//                            return;
//                        }
//                        if(right>=rightOffset){
//                            right =mThumbnailWidth - (right- rightOffset) ;
//                        }
//                        srcAlbumRect = new Rect(0,top,right,bottom);
//                        right = left + right;
//                    }
//                }

                Rect dstAlbumRect = new Rect(left,top, right ,bottom);

                KLog.i(TAG, "drawAlbum() called with: key = [" + key + "]" +  " dstAlbumRect:" + "[" + dstAlbumRect + "]");

                KLog.i(TAG,"dstAlbumRect:" + dstAlbumRect);
                KLog.i(TAG,"dstAlbumRect:" + (leftCount + i - 2)*itemTime);

                if (temp != null && temp.bmp != null && !temp.bmp.isRecycled()) {
                    canvas.drawBitmap(temp.bmp, srcAlbumRect, dstAlbumRect, null);
                    if (mTempBmp == null) {
                        mTempBmp = Bitmap.createBitmap(temp.bmp);
                    }
                } else {
                    if (temp!=null&&mTempBmp != null && !mTempBmp.isRecycled()) {
//                        albumCanvas.drawBitmap(mTempBmp, null, temp.rect, null);
                        canvas.drawBitmap(mTempBmp, srcAlbumRect, dstAlbumRect, null);
                    }
                }
            }

            entrySet.clear();
            maps.clear();
        }

    }

    public void setRatio(float ratio) {
//        this.ratio = ratio;
//        mThumbnailWidth = (int)((mThumbnailHeight+0.f)*ratio);
//        downloadCount = screenWH[0]/mThumbnailWidth+5;
//        setPlayer(engine,(int)clipDuration,mTotalDuration,mMaxDuration,mStartTime);

    }

    private void calculatePosition() {
        scrollPosition = (int) (-mStartTime * mWidthPerMs);
        startPosition = scrollPosition + handleWidth + leftOffset;
        endPosition = (int) mMaxWidth + startPosition - handleWidth*2 ;
        KLog.i("xxxx", "startPosition " + startPosition + " endPosition " + endPosition + " scrollPosition " + scrollPosition);
    }

    private void downloadImage(final int nTime, final Rect rect,PlayerEngine playerEngine) {
        KLog.i(TAG, "downloadImage() called with: nTime = [" + nTime + "], rect = [" + rect + "], playerEngine = [" + playerEngine + "]");
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

                            KLog.i(TAG, "key " + nTime);
                            KLog.i(TAG, "mLeftTime " + nTime);
                            KLog.i(TAG, "mRightTime " + mRightTime);
                            Bitmap tempBitmap = getBitmapFromMemCache(nTime);
                            boolean hasBmp = (tempBitmap != null && !tempBitmap.isRecycled());
                            if (hasBmp) {
                                return;
                            }
//                            Bitmap bitmap = Bitmap.createBitmap(mThumbnailWidth, mThumbnailHeight,
//                                    Bitmap.Config.ARGB_8888);
                            KLog.i("snapshot","customClipview---getSnapShot>"+nTime);
                            Bitmap bitmap = playerEngine.getSnapShot(nTime*1000,mThumbnailWidth,mThumbnailHeight);
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
        int mCacheSize = maxMemory / 8;
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


    private float progress ;
    private float ratio ;
//    public void onPlayProgress(float position) {
//        progress = position;
//        invalidate();
//    }
//

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
        void onValuesChanged(long minValue, long maxValue, long duration, int changeType);
        void onValuesChangeEnd();
        void onValuesChangeStart();
    }
}
