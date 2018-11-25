package com.wmlive.hhvideo.heihei.beans.record;


import android.graphics.Bitmap;
import android.graphics.Rect;

/**
 * 视频分割前，取单个ThumbNail(拆分视频的单个缩略图)
 *
 * @author JIAN
 */
public class ThumbNailInfo extends SplitThumbItemInfo {

    public ThumbNailInfo(int Time, Rect src, Rect dst, boolean isleft,
                         boolean isright) {
        super(Time, src, dst, isleft, isright);
    }

    public Bitmap bmp;

    /**
     * 释放bmp
     */
    public void recycle() {
        if (null != bmp) {
            if (!bmp.isRecycled()) {
                bmp.recycle();
            }
        }
        bmp = null;
    }

}
