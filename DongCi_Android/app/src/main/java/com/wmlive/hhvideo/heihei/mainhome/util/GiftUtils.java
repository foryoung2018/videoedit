package com.wmlive.hhvideo.heihei.mainhome.util;

import android.support.annotation.NonNull;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

/**
 * Created by lsq on 1/11/2018.6:02 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class GiftUtils {

    @NonNull
    public static ScaleAnimation getScaleAnimation(float scale, long durationMillis) {
        ScaleAnimation animation = new ScaleAnimation(1.0f, scale, 1.0f, scale,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(durationMillis);
        return animation;
    }

    @NonNull
    public static ScaleAnimation getScaleAnimation(float scale) {
        return getScaleAnimation(scale, 200);
    }

}
