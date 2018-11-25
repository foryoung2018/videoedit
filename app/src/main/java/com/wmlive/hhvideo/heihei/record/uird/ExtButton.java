//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wmlive.hhvideo.heihei.record.uird;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.ViewParent;
import android.widget.Button;

import com.wmlive.hhvideo.heihei.record.widget.Rotatable;

@SuppressLint("AppCompatCustomView")
public class ExtButton extends Button implements Rotatable {
    static final ColorFilter a = new LightingColorFilter(-7829368, 65793);
    static final ColorFilter b = new ColorMatrixColorFilter(new ColorMatrix(new float[]{0.2F, 0.2F, 0.2F, 0.0F, 0.0F, 0.2F, 0.2F, 0.2F, 0.0F, 0.0F, 0.2F, 0.2F, 0.2F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F}));
    private int c;
    private long d;
    private boolean e;

    public void enableExtState(boolean var1) {
        this.e = var1;
    }

    public boolean getExtState() {
        return this.e;
    }

    public ExtButton(Context var1) {
        this(var1, (AttributeSet)null);
    }

    public ExtButton(Context var1, AttributeSet var2) {
        super(var1, var2);
        this.c = 800;
        this.e = true;
    }

    public boolean performClick() {
        long var1 = System.currentTimeMillis();
        long var3 = var1 - this.d;
        if (0L < var3 && var3 < (long)this.c) {
            return false;
        } else {
            this.d = var1;
            return super.performClick();
        }
    }

    public boolean isFocused() {
        return this.getEllipsize() == TruncateAt.MARQUEE ? true : super.isFocused();
    }

    public void setRepeatClickIntervalTime(int var1) {
        this.c = var1;
    }

    public void setImageResource(int var1) {
        if (var1 == 0) {
            this.setCompoundDrawables((Drawable)null, (Drawable)null, (Drawable)null, (Drawable)null);
        } else {
            Drawable var2 = this.getResources().getDrawable(var1);
            var2.setBounds(0, 0, var2.getIntrinsicWidth(), var2.getIntrinsicHeight());
            this.setCompoundDrawablePadding(-var2.getIntrinsicHeight());
            this.setCompoundDrawables((Drawable)null, var2, (Drawable)null, (Drawable)null);
        }

    }

    @SuppressLint({"NewApi"})
    public void setOrientation(int var1) {
        if (VERSION.SDK_INT >= 11) {
            this.setRotation((float)(-var1));
        } else {
            ViewParent var2 = this.getParent();
            if (var2 instanceof Rotatable) {
                Rotatable var3 = (Rotatable)var2;
                var3.setOrientation(var1);
            }
        }

    }

    void a(Drawable var1, int[] var2) {
        boolean var3 = false;
        boolean var4 = false;
        int[] var8 = var2;
        int var7 = var2.length;

        for(int var6 = 0; var6 < var7; ++var6) {
            int var5 = var8[var6];
            if (var5 == 16842910) {
                var3 = true;
            } else if (var5 == 16842919) {
                var4 = true;
            }
        }

        if (this.e) {
            if (var3 && var4) {
                var1.setColorFilter(a);
            } else if (!var3) {
                var1.clearColorFilter();
                var1.setColorFilter(b);
            } else {
                var1.clearColorFilter();
            }
        }

        var1.invalidateSelf();
    }

    public void setBackgroundDrawable(Drawable var1) {
        if (var1 != null) {
            SAutoBgButtonBackgroundDrawable var2 = new SAutoBgButtonBackgroundDrawable(var1);
            super.setBackgroundDrawable(var2);
        } else {
            super.setBackgroundDrawable(var1);
        }

    }

    public void setCompoundDrawables(Drawable var1, Drawable var2, Drawable var3, Drawable var4) {
        BitmapDrawable var5;
        Rect var6;
        if (var2 != null && var2 instanceof BitmapDrawable) {
            var5 = (BitmapDrawable)var2;
            var6 = new Rect(((Drawable)var2).getBounds());
            var2 = new StateBitmapDrawable(var5.getBitmap());
            ((Drawable)var2).setBounds(var6);
        }

        if (var1 != null && var1 instanceof BitmapDrawable) {
            var5 = (BitmapDrawable)var1;
            var6 = new Rect(((Drawable)var1).getBounds());
            var1 = new StateBitmapDrawable(var5.getBitmap());
            ((Drawable)var1).setBounds(var6);
        }

        if (var3 != null && var3 instanceof BitmapDrawable) {
            var5 = (BitmapDrawable)var3;
            var6 = new Rect(((Drawable)var3).getBounds());
            var3 = new StateBitmapDrawable(var5.getBitmap());
            ((Drawable)var3).setBounds(var6);
        }

        if (var4 != null && var4 instanceof BitmapDrawable) {
            var5 = (BitmapDrawable)var4;
            var6 = new Rect(((Drawable)var4).getBounds());
            var4 = new StateBitmapDrawable(var5.getBitmap());
            ((Drawable)var4).setBounds(var6);
        }

        super.setCompoundDrawables((Drawable)var1, (Drawable)var2, (Drawable)var3, (Drawable)var4);
    }

    protected class SAutoBgButtonBackgroundDrawable extends LayerDrawable {
        public SAutoBgButtonBackgroundDrawable(Drawable var2) {
            super(new Drawable[]{var2});
        }

        protected boolean onStateChange(int[] var1) {
            ExtButton.this.a(this, var1);
            return super.onStateChange(var1);
        }

        public boolean isStateful() {
            return true;
        }
    }

    protected class StateBitmapDrawable extends BitmapDrawable {
        public StateBitmapDrawable(Bitmap var2) {
            super(var2);
        }

        protected boolean onStateChange(int[] var1) {
            ExtButton.this.a(this, var1);
            return super.onStateChange(var1);
        }

        public boolean isStateful() {
            return true;
        }
    }
}
