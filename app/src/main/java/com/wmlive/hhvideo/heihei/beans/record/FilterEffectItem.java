package com.wmlive.hhvideo.heihei.beans.record;

import android.content.Context;
import android.graphics.Rect;

import cn.wmlive.hhvideo.R;


/**
 * 特效类
 *
 * @author scott
 */
public class FilterEffectItem {
//    EffectType type;
    int color;
    float startTime;
    float endTime;
    Rect specialRect = new Rect();

    /**
     * 特效构造函数
     *
     * @param type      特效类型
     * @param startTime 特效开始时间
     * @param endTime   特效结束时间
     */
//    public FilterEffectItem(Context context, EffectType type, float startTime, float endTime) {
//        this.type = type;
//        this.startTime = startTime;
//        this.endTime = endTime;
//        setColor(context, type);
//    }

    /**
     * 特效构造函数
     *
//     * @param type 特效类型
     */
//    public FilterEffectItem(Context context, EffectType type) {
//        this.type = type;
//        setColor(context, type);
//    }
//
//    private void setColor(Context context, EffectType type) {
//        int color = 0;
//        if (type == EffectType.TREMBLE) {
//            color = context.getResources().getColor(R.color.color_effect_tremble_bg);
//        } else if (type == EffectType.AWAKENE) {
//            color = context.getResources().getColor(R.color.color_effect_awakened_bg);
//        } else if (type == EffectType.HEARTBEAT) {
//            color = context.getResources().getColor(R.color.color_effect_heartbeat_bg);
//        } else if (type == EffectType.SPOTLIGHT) {
//            color = context.getResources().getColor(R.color.color_effect_spotlight_bg);
//        }
//        this.color = color;
//    }
//
//    public int getColor() {
//        return color;
//    }
//
//    public EffectType getType() {
//        return type;
//    }

    public void setSpecialRect(Rect rect) {
        specialRect = rect;
    }

    public void setSpecialRect(int left, int top, int right, int bottom) {
        specialRect.set(left, top, right, bottom);
    }

    public Rect getSpecialRect() {
        return specialRect;
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

