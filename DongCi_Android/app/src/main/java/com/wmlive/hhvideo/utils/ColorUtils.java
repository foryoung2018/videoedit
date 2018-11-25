package com.wmlive.hhvideo.utils;

import android.content.res.ColorStateList;
import android.graphics.Color;

import java.util.Random;

/**
 * Generate thumb and background color state list use tintColor
 * Created by kyle on 15/11/4.
 */
public class ColorUtils {
    private static final int ENABLE_ATTR = android.R.attr.state_enabled;
    private static final int CHECKED_ATTR = android.R.attr.state_checked;
    private static final int PRESSED_ATTR = android.R.attr.state_pressed;
    static private Random rand = new Random();

    public static ColorStateList generateThumbColorWithTintColor(final int tintColor) {
        int[][] states = new int[][]{
                {-ENABLE_ATTR, CHECKED_ATTR},
                {-ENABLE_ATTR},
                {PRESSED_ATTR, -CHECKED_ATTR},
                {PRESSED_ATTR, CHECKED_ATTR},
                {CHECKED_ATTR},
                {-CHECKED_ATTR}
        };

        int[] colors = new int[]{
                tintColor - 0xAA000000,
                0xFFBABABA,
                tintColor - 0x99000000,
                tintColor - 0x99000000,
                tintColor | 0xFF000000,
                0xFFEEEEEE
        };
        return new ColorStateList(states, colors);
    }

    public static ColorStateList generateBackColorWithTintColor(final int tintColor) {
        int[][] states = new int[][]{
                {-ENABLE_ATTR, CHECKED_ATTR},
                {-ENABLE_ATTR},
                {CHECKED_ATTR, PRESSED_ATTR},
                {-CHECKED_ATTR, PRESSED_ATTR},
                {CHECKED_ATTR},
                {-CHECKED_ATTR}
        };

        int[] colors = new int[]{
                tintColor - 0xE1000000,
                0x10000000,
                tintColor - 0xD0000000,
                0x20000000,
                tintColor - 0xD0000000,
                0x20000000
        };
        return new ColorStateList(states, colors);
    }

    static public int randomColor() {
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);
        return Color.rgb(r, g, b);
    }
}
