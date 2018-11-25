package com.wmlive.hhvideo.heihei.beans.record;

import android.content.Context;
import android.graphics.Rect;


import cn.wmlive.hhvideo.R;


/**
 * 特效类
 *
 * @author scott
 */
public class TimeEffectItem {
    private Context mContext;
//    private EffectType type = EffectType.NONE;
    private int color;
    private float startTime;
    private float endTime;
    private Rect effectRect = new Rect();

    /**
     * 特效构造函数
     *
//     * @param type      特效类型
//     * @param startTime 开始时间
//     * @param endTime   结束时间
     */
//    public TimeEffectItem(Context context, EffectType type, float startTime, float endTime) {
//        mContext = context;
//        this.type = type;
//        this.startTime = startTime;
//        this.endTime = endTime;
//        setColor(context);
//    }

//    public TimeEffectItem(Context context) {
//        mContext = context;
//        setColor(context);
//    }

//    public EffectType getType() {
//        return type;
//    }
//
//    public void setType(EffectType type) {
//        this.type = type;
//        setColor(mContext);
//    }
//
//    public int getColor() {
//        return color;
//    }
//
//    private void setColor(Context context) {
//        if (type == EffectType.NONE) {
//            color = 0;
//        } else {
//            color = context.getResources().getColor(R.color.color_time_effect_bg);
//        }
//    }

    public void setEffectRect(Rect rect) {
        effectRect = rect;
    }

    public Rect getEffectRect() {
        return effectRect;
    }

    public void setEffectRect(int left, int top, int right, int bottom) {
        effectRect.set(left, top, right, bottom);
    }


    public float getStartTime() {
        return startTime;
    }

    public void setStartTime(float startTime) {
        this.startTime = startTime;
    }

    public float getEndTime() {
        return endTime;
    }

    public void setEndTime(float endTime) {
        this.endTime = endTime;
    }
}
