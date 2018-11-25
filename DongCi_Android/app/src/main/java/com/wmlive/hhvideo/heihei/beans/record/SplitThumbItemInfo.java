package com.wmlive.hhvideo.heihei.beans.record;

import android.graphics.Rect;

/**
 * 视频分割后的缩略图item
 *
 * @author JIAN
 */
public class SplitThumbItemInfo {
    public int nTime;// 图片时刻
    public Rect src; // canvas.drawBitmap(bitmap, src, dst, paint)
    public Rect dst;
    public boolean isLeft = false; // 是否在左右两边2px的边框线
    public boolean isRight = false;

    public SplitThumbItemInfo(int Time, Rect src, Rect dst, boolean isleft,
                              boolean isright) {
        this.nTime = Time;
        this.src = src;
        this.dst = dst;
        this.isLeft = isleft;
        this.isRight = isright;
    }

}
