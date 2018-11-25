package com.wmlive.hhvideo.heihei.record.utils;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.text.TextUtils;

public final class PaintUtils {
    public PaintUtils() {
    }

    public static int getWidth(Paint var0, String var1) {
        if (var0 != null && !TextUtils.isEmpty(var1)) {
            float[] var2 = new float[var1.length()];
            int var3 = 0;
            var0.getTextWidths(var1, var2);

            for(int var4 = 0; var4 < var2.length; ++var4) {
                var3 += (int)Math.ceil((double)var2[var4]);
            }

            return var3;
        } else {
            return 0;
        }
    }

    public static int[] getHeight(Paint var0) {
        if (var0 == null) {
            return new int[2];
        } else {
            FontMetrics var1 = var0.getFontMetrics();
            int var2 = (int)Math.ceil((double)(var1.bottom - var1.top));
            int var3 = (int)Math.ceil((double)var1.descent);
            return new int[]{var2, var3};
        }
    }
}
