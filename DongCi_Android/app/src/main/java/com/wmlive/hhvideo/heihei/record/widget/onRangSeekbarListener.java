package com.wmlive.hhvideo.heihei.record.widget;

/**
 * Created by JIAN on 2017/5/15.
 */

public interface onRangSeekbarListener {


    /**
     * 开始触摸bar
     *
     * @param minValue
     * @param maxValue
     * @param currentValue
     */
    void onRangBarStart(long minValue, long maxValue,
                        long currentValue);


    /**
     * 响应值改变时 moving
     *
     * @param minValue
     * @param maxValue
     * @param currentValue
     */
    void onRangbarChanging(long minValue, long maxValue,
                           long currentValue);

    /**
     * 手势结束
     *
     * @param minValue
     * @param maxValue
     * @param currentValue
     */
    void onRangBarChanged(long minValue, long maxValue,
                          long currentValue);


}
