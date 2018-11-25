package com.wmlive.hhvideo.heihei.record.widget;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

@SuppressLint("AppCompatCustomView")
public class TwoStateImageView extends ImageView {
    private final float a;
    private boolean b;

    public TwoStateImageView(Context var1, AttributeSet var2) {
        super(var1, var2);
        this.a = 0.4F;
        this.b = true;
    }

    public TwoStateImageView(Context var1) {
        this(var1, (AttributeSet)null);
    }

    public void setEnabled(boolean var1) {
        super.setEnabled(var1);
        if (this.b) {
            if (var1) {
                this.setAlpha(255);
            } else {
                this.setAlpha(102);
            }
        }

    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void enableFilter(boolean var1) {
        this.b = var1;
    }

    protected void onDraw(Canvas var1) {
        var1.drawColor(-16777216);
        if (this.getDrawable() instanceof BitmapDrawable) {
            BitmapDrawable var2 = (BitmapDrawable)this.getDrawable();
            if (var2 != null && var2.getBitmap() != null && var2.getBitmap().isRecycled()) {
                return;
            }
        }

        super.onDraw(var1);
    }
}
