package com.dongci.sun.gpuimglibrary.player;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by zhangxiao on 2018/6/6.
 *
 */

public class DCTransition {

    public static final int DCTransitionTypeNone        = 0;
    public static final int DCTransitionTypeLeft        = 1; //左推
    public static final int DCTransitionTypeRight       = 2; //右推
    public static final int DCTransitionTypeUp          = 3; //上推
    public static final int DCTransitionTypeDown        = 4; //下推
    public static final int DCTransitionTypeFade        = 5; //淡入
    public static final int DCTransitionTypeBlinkBlack  = 6; //闪黑
    public static final int DCTransitionTypeBlinkWhite  = 7; //闪白
    public static final int DCTransitionTypeMask        = 8; //Mask

    @IntDef({DCTransitionTypeNone,
            DCTransitionTypeLeft,
            DCTransitionTypeRight,
            DCTransitionTypeUp,
            DCTransitionTypeDown,
            DCTransitionTypeFade,
            DCTransitionTypeBlinkBlack,
            DCTransitionTypeBlinkWhite,
            DCTransitionTypeMask})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DCTransitionType {
    }

    public @DCTransitionType int type;
    public DCAsset.TimeRange timeRange;
}
