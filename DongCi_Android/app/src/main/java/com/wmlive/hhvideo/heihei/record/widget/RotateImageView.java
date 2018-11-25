package com.wmlive.hhvideo.heihei.record.widget;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.ThumbnailUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;

import com.wmlive.hhvideo.heihei.record.uird.RecyclingBitmapDrawable;

public class RotateImageView extends TwoStateImageView implements Rotatable {
    private static int a = 270;
    private int b = 0;
    private int c = 0;
    private int d = 0;
    private boolean e = false;
    private boolean f = true;
    private long g = 0L;
    private long h = 0L;
    private boolean i = true;
    private Bitmap j;
    private Drawable[] k;
    private TransitionDrawable l;

    public RotateImageView(Context var1, AttributeSet var2) {
        super(var1, var2);
    }

    public RotateImageView(Context var1) {
        super(var1);
    }

    public void setFitCenterSizeMode(boolean var1) {
        this.i = var1;
        this.invalidate();
    }

    protected void onDetachedFromWindow() {
        this.setImageDrawable((Drawable)null);
        super.onDetachedFromWindow();
    }

    public void setImageDrawable(Drawable var1) {
        Drawable var2 = this.getDrawable();
        super.setImageDrawable(var1);
        a(var1, true);
        a(var2, false);
    }

    private static void a(Drawable var0, boolean var1) {
        if (var0 instanceof RecyclingBitmapDrawable) {
            ((RecyclingBitmapDrawable)var0).setIsDisplayed(var1);
        } else if (var0 instanceof LayerDrawable) {
            LayerDrawable var2 = (LayerDrawable)var0;
            int var3 = 0;

            for(int var4 = var2.getNumberOfLayers(); var3 < var4; ++var3) {
                a(var2.getDrawable(var3), var1);
            }
        }

    }

    public void enableAnimation(boolean var1) {
        this.f = var1;
    }

    protected int getDegree() {
        return this.d;
    }

    public void setOrientationAndSpeed(int var1, int var2) {
        a = var2;
        this.setOrientation(var1);
    }

    public void setOrientation(int var1) {
        var1 = var1 >= 0 ? var1 % 360 : var1 % 360 + 360;
        if (var1 != this.d) {
            this.d = var1;
            this.c = this.b;
            this.g = AnimationUtils.currentAnimationTimeMillis();
            int var2 = this.d - this.b;
            var2 = var2 >= 0 ? var2 : 360 + var2;
            var2 = var2 > 180 ? var2 - 360 : var2;
            this.e = var2 >= 0;
            this.h = this.g + (long)(Math.abs(var2) * 1000 / a);
            this.invalidate();
        }
    }

    protected void onDraw(Canvas var1) {
        Drawable var2 = this.getDrawable();
        if (var2 != null) {
            Rect var3 = var2.getBounds();
            int var4 = var3.right - var3.left;
            int var5 = var3.bottom - var3.top;
            if (var4 != 0 && var5 != 0) {
                int var8;
                int var9;
                if (this.b != this.d) {
                    long var6 = AnimationUtils.currentAnimationTimeMillis();
                    if (var6 < this.h) {
                        var8 = (int)(var6 - this.g);
                        var9 = this.c + a * (this.e ? var8 : -var8) / 1000;
                        var9 = var9 >= 0 ? var9 % 360 : var9 % 360 + 360;
                        this.b = var9;
                        this.invalidate();
                    } else {
                        this.b = this.d;
                    }
                }

                int var15 = this.getPaddingLeft();
                int var7 = this.getPaddingTop();
                var8 = this.getPaddingRight();
                var9 = this.getPaddingBottom();
                int var10 = this.getWidth() - var15 - var8;
                int var11 = this.getHeight() - var7 - var9;
                int var12 = var1.getSaveCount();
                if (this.getDrawable() instanceof NinePatchDrawable) {
                    this.getDrawable().getBounds().set(this.getPaddingLeft(), this.getPaddingTop(), var10 + this.getPaddingRight(), var11 + this.getPaddingBottom());
                    var4 = var10;
                    var5 = var11;
                } else if (this.getScaleType() == ScaleType.FIT_CENTER) {
                    float var13 = this.i ? Math.max((float)var10 / (float)var4, (float)var11 / (float)var5) : Math.min((float)var10 / (float)var4, (float)var11 / (float)var5);
                    var1.scale(var13, var13, (float)var10 / 2.0F, (float)var11 / 2.0F);
                }

                var1.translate((float)(var15 + var10 / 2), (float)(var7 + var11 / 2));
                var1.rotate((float)(-this.b));
                var1.translate((float)(-var4 / 2), (float)(-var5 / 2));

                try {
                    var2.draw(var1);
                } catch (Exception var14) {
                    ;
                }

                var1.restoreToCount(var12);
            }
        }
    }

    public void setBitmap(Bitmap var1) {
        if (var1 == null) {
            this.j = null;
            this.k = null;
            this.setImageDrawable((Drawable)null);
            this.setVisibility(View.GONE);
        } else {
            LayoutParams var2 = this.getLayoutParams();
            int var3 = var2.width - this.getPaddingLeft() - this.getPaddingRight();
            int var4 = var2.height - this.getPaddingTop() - this.getPaddingBottom();
            this.j = ThumbnailUtils.extractThumbnail(var1, var3, var4);
            if (this.k != null && this.f) {
                this.k[0] = this.k[1];
                this.k[1] = new BitmapDrawable(this.getContext().getResources(), this.j);
                this.l = new TransitionDrawable(this.k);
                this.setImageDrawable(this.l);
                this.l.startTransition(500);
            } else {
                this.k = new Drawable[2];
                this.k[1] = new BitmapDrawable(this.getContext().getResources(), this.j);
                this.setImageDrawable(this.k[1]);
            }

            this.setVisibility(View.VISIBLE);
        }
    }
}
