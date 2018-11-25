package com.wmlive.hhvideo.heihei.record.manager;

/**
 * Created by lsq on 8/31/2017.
 * 录制的速度
 */

public enum RecordSpeed {
    SLOWEST(0), SLOW(1), NORMAL(2), FAST(3), FASTEST(4);

    private double value = 1;
    private String title;

    RecordSpeed(int index) {
        switch (index) {
            case 0:
                value = 1.0 / 3;
                break;
            case 1:
                value = 1.0 / 2;
                break;
            case 2:
                value = 1;
                break;
            case 3:
                value = 2.0;
                break;
            case 4:
                value = 3.0;
                break;
            default:
                value = 1;
        }
        title = RecordSetting.SPEED_TITLE[isValid(index) ? index : 2];
    }

    public static double getSpeed(int index) {
        switch (index) {
            case 0:
                return SLOWEST.value;
            case 1:
                return SLOW.value;
            case 2:
                return NORMAL.value;
            case 3:
                return FAST.value;
            case 4:
                return FASTEST.value;
            default:
                return NORMAL.value;
        }
    }

    public double value() {
        return value;
    }

    public String title() {
        return title;
    }

    private boolean isValid(int index) {
        return index >= 0 && index < 5;
    }

}
