package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.LruCache;
import android.view.View;

import com.wmlive.hhvideo.heihei.record.engine.PlayerEngine;
import com.wmlive.hhvideo.heihei.record.utils.CoreUtils;
import com.wmlive.hhvideo.utils.ScreenUtil;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.wmlive.hhvideo.R;

/**
 * 时间轴 (包含缩略图和字幕时间段信息)
 *
 * @author JIAN
 */
public class ThumbNailLine extends View {
    public final String TAG = "ThumbNailLine";

    private int mMaxTrimDuration = 15000;

    private Paint mPaintBg = new Paint();
    private int mThumbnailWidth;
    private int mThumbnailHeight;
    private Rect mRectLastSrc = new Rect();
    private Rect mRectbg = new Rect();
    private int mScreenWidth;
    private float mWidthPerSecond;
    /**
     * 多视频编辑
     */
    private boolean isMultEdit = false;


    /**
     * @param context
     * @param attrs
     */
    public ThumbNailLine(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public ThumbNailLine(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initThread(context);
        mPaintBg.setColor(Color.BLACK);
        mPaintBg.setAntiAlias(true);
        mThumbnailWidth = getResources().getDimensionPixelSize(
                R.dimen.trim_thumbnail_width);
        mThumbnailHeight = getResources().getDimensionPixelSize(
                R.dimen.trim_thumbnail_height);
        mScreenWidth = CoreUtils.getMetrics().widthPixels - 2 * ScreenUtil.dip2px(context,CustomTrimVideoView.MARGIN_LEFT_RIGHT);
    }

    private Bitmap mTempBmp;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                | Paint.FILTER_BITMAP_FLAG));

        canvas.drawRect(mRectbg, mPaintBg);

        Map<Integer, ThumbInfo> maps = mMemoryCache.snapshot();
        Set<Entry<Integer, ThumbInfo>> entrySet = maps.entrySet();

        for (Entry<Integer, ThumbInfo> item : entrySet) {
            ThumbInfo temp = item.getValue();
            if (mlefttime <= temp.nTime && temp.nTime <= mrighttime) {
                if (temp != null && temp.bmp != null && !temp.bmp.isRecycled()) {
                    canvas.drawBitmap(temp.bmp, null, temp.rect, null);
                    if (mTempBmp == null) {
                        mTempBmp = Bitmap.createBitmap(temp.bmp);
                    }
                } else {
                    if (mTempBmp != null && !mTempBmp.isRecycled()) {
                        canvas.drawBitmap(mTempBmp, null, temp.rect, null);
                    }
                }
            }
        }
        entrySet.clear();
        maps.clear();
    }

    /**
     * 通过距离，计算出当前宽度的时间
     *
     * @param scrollX 单位px
     * @return 单位ms
     */
    public int getProgress(int scrollX) {
        return (int) (mDuration * ((scrollX) / (params[0] + .0)));
    }

    /**
     * 是否多视频编辑
     *
     * @param multEdit
     */
    public void setMultEdit(boolean multEdit) {
        isMultEdit = multEdit;
    }


    private int mDuration;

    private int[] params = new int[2];
//    private VirtualVideo mVirtualVideo;
    PlayerEngine mPlayerEngine;
    public int[] setPlayer(PlayerEngine playerEngine, float duration, float maxTrimDuration) {
        recycle();
        if (isMultEdit) {
            // 多视频编辑指定最长时间，等比不拉伸
            mMaxTrimDuration = (int) (maxTrimDuration * 1000f);
        } else {
            // max 作为图册全屏幕宽展示的时间长度， dutarion为总时间长度
            float max = maxTrimDuration < duration ? maxTrimDuration : duration;
            mMaxTrimDuration = (int) (max * 1000f);
        }
        mPlayerEngine = playerEngine;
        mDuration = (int) (duration * 1000);
        mWidthPerSecond = (mScreenWidth + 0.0f) / (mMaxTrimDuration + 0.0f);
        itemTime = (int) ((mMaxTrimDuration * mThumbnailWidth + 0.0f) / (mScreenWidth + 0.0f));

        // params[0]为图册的总长度
        params[0] = (int) (mWidthPerSecond * mDuration);
        params[1] = mThumbnailHeight;

        mRectbg.set(0, 0, params[0], mThumbnailHeight);
        isEditorPrepared = true;
        return params;
    }

    private final int THUMBITEM = 6;
    private Handler mhandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case THUMBITEM:
                    lastrefleshtime = System.currentTimeMillis();
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

    private int itemTime = 0;
    private int visibleCount = 10; // 屏幕区域内左边第一个可见的缩略图的时刻

    public void prepare(int visibleWidth) {
        visibleCount = (int) Math.ceil(((visibleWidth) + .0)
                / mThumbnailWidth) + 2;// 可见的所率图个数;
    }

    private int leftCount = 10;
    private int lastLeft = -10;

    /**
     * 开始加载图片
     */
    public void setStartThumb(int scrollX) {
        long tempflesh = System.currentTimeMillis();
        leftCount = (int) Math.ceil((scrollX + .0)
                / mThumbnailWidth) - 2;// 已滑动到左边的个数

        mlefttime = (leftCount) * itemTime;
        mrighttime = (leftCount + visibleCount) * itemTime;

        if (lastrefleshtime == 0 || tempflesh - lastrefleshtime > 100 || lastLeft != leftCount) { // 减少刷新频率
            lastLeft = leftCount;
            Rect temp;
            int nLeft = leftCount * mThumbnailWidth;// 时间线左边的偏移
            for (int i = 0; i < visibleCount; i++) {
                int key = (int) ((leftCount + i) * itemTime);
                if (0 <= key) {
                    if (key > mDuration) {
                        key = mDuration;
                    }
                    ThumbInfo info = mMemoryCache.get(key);
                    if (null == info || null == info.bmp
                            || info.isloading == false) {
                        int mleft = nLeft + i * mThumbnailWidth;
                        if (mleft <= params[0]) { // 防止超过视频边界
                            temp = new Rect(mleft, 0, mleft + mThumbnailWidth, mThumbnailHeight);
                            downloadImage(key, temp);
                        }
                    }
                }
            }
            lastrefleshtime = tempflesh;
            invalidate();
        }
    }

    private long lastrefleshtime = 0;
    private int mlefttime = 0, mrighttime = mDuration;
    private boolean isEditorPrepared = false;

    private void downloadImage(final int nTime, final Rect rect) {
        final Bitmap bitmap = getBitmapFromMemCache(nTime);
        boolean hasBmp = (bitmap != null && !bitmap.isRecycled());
        if (hasBmp) {
            mhandler.sendEmptyMessage(THUMBITEM);
        } else {
            if (isEditorPrepared && (!hasBmp)) {
                ThumbInfo info = new ThumbInfo(nTime, rect, null);
                info.isloading = true;
                mMemoryCache.put(nTime, info);
                getThreadPool().execute(new Runnable() {

                    @Override
                    public void run() {
                        if (mlefttime <= nTime && nTime <= mrighttime) {
                            Bitmap tempBitmap = getBitmapFromMemCache(nTime);
                            boolean hasBmp = (tempBitmap != null && !tempBitmap.isRecycled());
                            if (hasBmp) {
                                return;
                            }

//                            Bitmap bitmap = Bitmap.createBitmap(mThumbnailWidth, mThumbnailHeight,
//                                    Bitmap.Config.ARGB_8888);
                            Bitmap bitmap = mPlayerEngine.getSnapShot(nTime , mThumbnailWidth,mThumbnailHeight);

                            if (mPlayerEngine != null && bitmap!=null) {
                                addBitmapToMemoryCache(nTime, rect, bitmap);
                                mhandler.sendEmptyMessage(THUMBITEM);
                            } else {
                                ThumbInfo info = new ThumbInfo(nTime, rect, null);
                                info.isloading = false;
                                mMemoryCache.put(nTime, info);
                                bitmap.recycle();
                            }
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


    /**
     * 释放资源
     */
    public void recycle() {
        isEditorPrepared = false;
        lastrefleshtime = 0;
        mMemoryCache.evictAll();
        if (mTempBmp != null) {
            mTempBmp = null;
        }
        mlefttime = 0;
        mrighttime = 0;
    }

}
