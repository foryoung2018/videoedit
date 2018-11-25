package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

import cn.wmlive.hhvideo.R;


/**
 * 进度条支持最大最小区间
 * Created by JIAN on 2017/5/12.
 */

public class ExtProgressBar extends IProgressBar {

    private String TAG = ExtProgressBar.this.toString();

    private int color_p = Color.GREEN;//进度颜色
    private int color_min = Color.WHITE;

    public ExtProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        pMin = new Paint();
        pMin.setColor(color_min);
        pMin.setAntiAlias(true);
        pProgress = new Paint();
        color_p = getResources().getColor(R.color.ext_progressbar);
        pProgress.setColor(color_p);
        pProgress.setAntiAlias(true);
    }

    private Paint pMin, pProgress;
    private int ITEM_WIDTHPX = 6;//最小时间的宽度
    private Rect rectProgress = new Rect();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //进度条


//        Log.e("ondraw", "onDraw: --->" + mProgress);
        rectProgress.set(getLeft(), getTop(), progressTodp(mProgress), getBottom());
        canvas.drawRect(rectProgress, pProgress);

        //进度条末端
        if (getProgress() != 0) {
            rectProgress.set(rectProgress.right, getTop(), rectProgress.right + 5, getBottom());
            canvas.drawRect(rectProgress, pMin);
        }


        //最小刻度
//        if (mMin != 0) {
//            int minpx = progressTodp(mMin);
//            int left = getLeft() + minpx - (ITEM_WIDTHPX / 2);
//            rectProgress.set(left, getTop(), left + ITEM_WIDTHPX, getBottom());
//            canvas.drawRect(rectProgress, pMin);
//        }

        for (int j = 0; j < items.size(); j++) {
//            Log.e("item", "onDraw: ------------->" + j + "...." + items.get(j).rect.toShortString()+"........."+items.get(j).getProgress());
            canvas.drawRect(items.get(j).rect, pMin);
        }

        //视频事件段，画一个色块


    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
//        Log.e(TAG, "onVisibilityChanged: " + changedView + "............." + visibility);
    }

    private class ItemInfo {

        public ItemInfo(int progress, Rect rect) {
            this.progress = progress;
            this.rect = rect;
        }

        public int getProgress() {
            return progress;
        }

        public Rect getRect() {
            return rect;
        }

        @Override
        public String toString() {
            return "ItemInfo{" +
                    "progress=" + progress +
                    ", rect=" + rect.toShortString() +
                    '}';
        }

        private int progress;
        private Rect rect;

    }

    private ArrayList<ItemInfo> items = new ArrayList<ItemInfo>();

    /**
     * 检测内部是否存在该时间点
     *
     * @param progress
     * @return
     */
    private boolean isContains(int progress) {
        int len = items.size();
        boolean contains = false;
        if (len > 0) {
            for (int i = 0; i < len; i++) {
                if (items.get(i).progress == progress) {
                    contains = true;
                    break;
                }
            }
        }
        return contains;
    }

    /**
     * 每段视频录制完成，新增一个节点
     *
     * @param progress
     */
    public void addItemLine(int progress) {
        if (!isContains(progress)) {
            int pleft = progressTodp(progress) - ITEM_WIDTHPX / 2;
            Rect rect = new Rect(pleft, getTop(), pleft + ITEM_WIDTHPX, getBottom());
            items.add(new ItemInfo(progress, rect));
            invalidate();
        }


    }

    private int[] tempItems = null;

    /**
     * 恢复时间视频片段
     *
     * @param itemsLines
     */
    public void addItemLines(int[] itemsLines) {
        items.clear();
        if (null != itemsLines) {
            int len = itemsLines.length;
            if (len > 0) {
                for (int i = 0; i < len; i++) {
                    int pleft = progressTodp(itemsLines[i]) - ITEM_WIDTHPX / 2;
                    Rect rect = new Rect(pleft, getTop(), pleft + ITEM_WIDTHPX, getBottom());
//                    Log.e(TAG, "addItemLines: "+itemsLines[i]);
                    ItemInfo info = new ItemInfo(itemsLines[i], rect);
                    items.add(info);
                }
                tempItems = null;
                invalidate();
            } else {
                tempItems = itemsLines;
            }
        }

    }

    /**
     * 删除最后一个节点
     */
    public void removeLastItem() {
        int size = items.size();
        if (size >= 1) {
            items.remove(size - 1);
            invalidate();
        }
    }

    public void removeAllItem() {
        items.clear();
        invalidate();
    }


    /**
     * 释放资源
     */
    public void recycle() {
        super.recycle();
        rectProgress = null;
        pMin = null;
        pProgress = null;
    }

}
