package com.wmlive.hhvideo.heihei.record.uird;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class RecyclingBitmapDrawable extends BitmapDrawable {
    private int a = 0;
    private int b = 0;
    private boolean c;

    public RecyclingBitmapDrawable(Resources var1, Bitmap var2) {
        super(var1, var2);
    }

    public void setIsDisplayed(boolean var1) {
        synchronized(this) {
            if (var1) {
                ++this.b;
                this.c = true;
            } else {
                --this.b;
            }
        }

        this.a();
    }

    public void setIsCached(boolean var1) {
        synchronized(this) {
            if (var1) {
                ++this.a;
            } else {
                --this.a;
            }
        }

        this.a();
    }

    private synchronized void a() {
        if (this.a <= 0 && this.b <= 0 && this.c && this.b()) {
            this.getBitmap().recycle();
        }

    }

    private synchronized boolean b() {
        Bitmap var1 = this.getBitmap();
        return var1 != null && !var1.isRecycled();
    }
}

