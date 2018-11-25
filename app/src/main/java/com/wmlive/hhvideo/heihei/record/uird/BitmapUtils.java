package com.wmlive.hhvideo.heihei.record.uird;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import java.io.File;
import java.io.FileOutputStream;

public class BitmapUtils {
    public BitmapUtils() {
    }

    public static Bitmap rorateBmp(Bitmap var0, int var1) {
        Matrix var2 = new Matrix();
        var2.postRotate((float)var1);
        int var3 = var0.getWidth();
        int var4 = var0.getHeight();
        return Bitmap.createBitmap(var0, 0, 0, var3, var4, var2, true);
    }

    public static void saveBitmapToFile(Bitmap var0, int var1, String var2) throws Exception {
        saveBitmapToFile(var0, false, var1, var2);
    }

    public static void saveBitmapToFile(Bitmap var0, boolean var1, int var2, String var3) throws Exception {
        if (var0 != null) {
            File var4 = new File(var3);
            if (var4.exists()) {
                var4.createNewFile();
            }

            FileOutputStream var5 = new FileOutputStream(var4);
            var0.compress(var1 ? CompressFormat.PNG : CompressFormat.JPEG, Math.max(10, var2), var5);
            var5.flush();
            var5.close();
        }
    }

    public static Bitmap getZoomBitmap(Bitmap var0, int var1, int var2) {
        double var3 = ((double)var1 + 0.0D) / (double)var0.getWidth();
        double var5 = ((double)var2 + 0.0D) / (double)var0.getHeight();
        float var7 = (float)(1.0D * var3);
        float var8 = (float)(1.0D * var5);
        Matrix var9 = new Matrix();
        var9.postScale(var7, var8);
        return Bitmap.createBitmap(var0, 0, 0, var0.getWidth(), var0.getHeight(), var9, true);
    }

    public static Bitmap dividePart(Bitmap var0, Rect var1) {
        int var2 = var1.width();
        int var3 = var1.height();
        Rect var4 = new Rect(0, 0, var2, var3);
        Bitmap var5 = Bitmap.createBitmap(var2, var3, Config.ARGB_8888);
        Canvas var6 = new Canvas(var5);
        var6.drawBitmap(var0, var1, var4, (Paint)null);
        return var5;
    }

    public static Bitmap cropCenter(Bitmap var0, int var1, int var2) {
        int var3 = (var0.getWidth() - var1) / 2;
        int var4 = (var0.getHeight() - var2) / 2;
        Rect var5 = new Rect(var3, var4, var3 + var1, var4 + var2);
        return dividePart(var0, var5);
    }

    public static Bitmap getScaleBitmap(Bitmap var0, int var1, int var2) {
        double var3 = ((double)var1 + 0.0D) / (double)var2;
        double var5 = ((double)var0.getWidth() + 0.0D) / ((double)var0.getHeight() + 0.0D);
        int var7;
        if (var5 > var3) {
            var7 = (int)((double)var0.getHeight() * var3);
            var0 = cropCenter(var0, var7, var0.getHeight());
        } else if (var5 < var3) {
            var7 = var0.getWidth() * var2 / var1;
            var0 = cropCenter(var0, var0.getWidth(), var7);
        }

        return getBitmap(var0, var1, var2);
    }

    public static Bitmap getBitmap(Bitmap var0, int var1, int var2) {
        return var0.getWidth() != var1 ? getZoomBitmap(var0, var1, var2) : var0;
    }

    public static Bitmap createMirrorHor(Bitmap var0) {
        Bitmap var1 = Bitmap.createBitmap(var0.getWidth(), var0.getHeight(), Config.ARGB_8888);
        Canvas var2 = new Canvas(var1);
        var2.drawBitmap(var1, 0.0F, 0.0F, (Paint)null);
        Matrix var3 = new Matrix();
        var3.postScale(-1.0F, 1.0F);
        var3.postTranslate((float)var1.getWidth(), 0.0F);
        var2.setMatrix(var3);
        var2.save();
        return var1;
    }

    public static Bitmap getCircle(Bitmap var0) {
        Bitmap var1 = null;
        if (var0 != null) {
            int var2 = Math.min(var0.getWidth(), var0.getHeight());
            var1 = createRoundedCornerBitmap(var0, var2, 2, -1);
        }

        return var1;
    }

    public static Bitmap createRoundedCornerBitmap(Bitmap var0, int var1, int var2, int var3) {
        Bitmap var4 = Bitmap.createBitmap(var0.getWidth(), var0.getHeight(), Config.ARGB_8888);
        Canvas var5 = new Canvas(var4);
        int var6 = -12434878;
        Paint var7 = new Paint();
        Rect var8 = new Rect(0, 0, var0.getWidth(), var0.getHeight());
        RectF var9 = new RectF(var8);
        float var10 = (float)var1;
        var7.setAntiAlias(true);
        var7.setColor(-12434878);
        var5.drawARGB(0, 0, 0, 0);
        var5.drawRoundRect(var9, var10, var10, var7);
        var7.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        var5.drawBitmap(var0, var8, var8, var7);
        var7.setStyle(Style.STROKE);
        var7.setColor(var3);
        var7.setStrokeWidth((float)var2);
        var5.drawRoundRect(var9, var10, var10, var7);
        return var4;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap var0) {
        Bitmap var1 = Bitmap.createBitmap(var0.getWidth(), var0.getHeight(), Config.ARGB_8888);
        Canvas var2 = new Canvas(var1);
        int var3 = -12434878;
        Paint var4 = new Paint();
        Rect var5 = new Rect(0, 0, var0.getWidth(), var0.getHeight());
        RectF var6 = new RectF(var5);
        float var7 = (float)(var0.getWidth() / 2);
        var4.setAntiAlias(true);
        var2.drawARGB(0, 0, 0, 0);
        var4.setColor(-12434878);
        var2.drawRoundRect(var6, var7, var7, var4);
        var4.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        var2.drawBitmap(var0, var5, var5, var4);
        return var1;
    }

    public Bitmap toRoundBitmap(Bitmap var1) {
        int var2 = var1.getWidth();
        int var3 = var1.getHeight();
        float var4;
        float var5;
        float var6;
        float var7;
        float var8;
        float var9;
        float var10;
        float var11;
        float var12;
        if (var2 <= var3) {
            var4 = (float)(var2 / 2);
            var6 = 0.0F;
            var8 = (float)var2;
            var5 = 0.0F;
            var7 = (float)var2;
            var3 = var2;
            var9 = 0.0F;
            var10 = 0.0F;
            var11 = (float)var2;
            var12 = (float)var2;
        } else {
            var4 = (float)(var3 / 2);
            float var13 = (float)((var2 - var3) / 2);
            var5 = var13;
            var7 = (float)var2 - var13;
            var6 = 0.0F;
            var8 = (float)var3;
            var2 = var3;
            var9 = 0.0F;
            var10 = 0.0F;
            var11 = (float)var3;
            var12 = (float)var3;
        }

        Bitmap var20 = Bitmap.createBitmap(var2, var3, Config.ARGB_8888);
        Canvas var14 = new Canvas(var20);
        int var15 = -12434878;
        Paint var16 = new Paint();
        Rect var17 = new Rect((int)var5, (int)var6, (int)var7, (int)var8);
        Rect var18 = new Rect((int)var9, (int)var10, (int)var11, (int)var12);
        RectF var19 = new RectF(var18);
        var16.setAntiAlias(true);
        var14.drawARGB(0, 0, 0, 0);
        var16.setColor(-12434878);
        var14.drawRoundRect(var19, var4, var4, var16);
        var16.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        var14.drawBitmap(var1, var17, var18, var16);
        return var20;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap var0, int var1, int var2) {
        Bitmap var3 = Bitmap.createBitmap(var1, var1, Config.ARGB_8888);
        Canvas var4 = new Canvas(var3);
        int var5 = -16777216;
        Paint var6 = new Paint();
        Rect var7 = new Rect(0, 0, var1, var1);
        RectF var8 = new RectF(var7);
        float var9 = 360.0F;
        var6.setAntiAlias(true);
        var4.drawARGB(0, 0, 0, 0);
        var6.setColor(-16777216);
        var4.drawRoundRect(var8, 360.0F, 360.0F, var6);
        var6.setColor(-16777216);
        var6.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        Bitmap var10 = null;
        int var11 = var0.getWidth();
        int var12 = var0.getHeight();
        float var13 = (float)var1 / (float)var11;
        float var14 = (float)var1 / (float)var12;
        float var15 = Math.max(var13, var14);
        Matrix var16 = new Matrix();
        var16.setScale(var15, var15);
        var10 = Bitmap.createBitmap(var0, 0, 0, var11, var12, var16, true);
        var4.drawBitmap(var10, (Rect)null, var7, var6);
        var6.setStyle(Style.STROKE);
        var6.setColor(-1513240);
        var6.setStrokeWidth((float)var2);
        var4.drawCircle(var8.centerX(), var8.centerY(), (var8.height() - (float)var2) / 2.0F + 1.0F, var6);
        return var3;
    }
}
