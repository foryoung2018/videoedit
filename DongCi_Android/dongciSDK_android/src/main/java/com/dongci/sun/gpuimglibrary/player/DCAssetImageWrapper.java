package com.dongci.sun.gpuimglibrary.player;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.IOException;

/**
 * Created by zhangxiao on 2018/6/14.
 *
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class DCAssetImageWrapper extends DCAssetWrapper {
    private Bitmap mBitmap;

    DCAssetImageWrapper(DCAsset asset) {
        super(asset);
        mBitmap = BitmapFactory.decodeFile(mAsset.filePath);
    }

    @Override
    void prepare() throws IOException {
        super.prepare();
    }

    @Override
    public Bitmap getBitmap(int index) {
        return mBitmap;
    }

    @Override
    public int getBitmapCount() {
        return 1;
    }

    @Override
    int getWidth() {
        if(mBitmap != null){
            return mBitmap.getWidth();
        }
        return 0;
    }

    @Override
    int getHeight() {
        if(mBitmap != null){
            return mBitmap.getHeight();
        }
        return 0;
    }

    @Override
    void release() {
        super.release();
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }
}
