package com.dongci.sun.gpuimglibrary.player;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DCAssetImagesWrapper extends DCAssetWrapper {
    private List<Bitmap> bitmaps = new ArrayList<>();

    DCAssetImagesWrapper(DCAsset asset) {
        super(asset);

        if (mAsset.imagePaths != null) {
            for (String path : mAsset.imagePaths) {
                Bitmap bmp = BitmapFactory.decodeFile(path);
                if (bmp != null) {
//                    Log.e("DCAssetImagesWrapper", "++++++++++++++++ path: " + path);
                    bitmaps.add(bmp);
                }
            }
        }
    }

    @Override
    void prepare() throws IOException {
        super.prepare();
    }

    @Override
    public Bitmap getBitmap(int index) {
        if (bitmaps.size() > 0 && bitmaps.size() > index) {
            return bitmaps.get(index);
        }
        return null;
    }

    @Override
    public int getBitmapCount() {
        return bitmaps.size();
    }

    @Override
    int getWidth() {
        if (bitmaps.size() > 0) {
            return bitmaps.get(0).getWidth();
        }
        return 0;
    }

    @Override
    int getHeight() {
        if (bitmaps.size() > 0) {
            return bitmaps.get(0).getHeight();
        }
        return 0;
    }

    @Override
    void release() {
        super.release();
        if (bitmaps != null) {
            for (Bitmap bmp : bitmaps) {
                if (bmp != null) {
                    bmp.recycle();
                }
            }
            bitmaps.clear();
        }
    }
}
