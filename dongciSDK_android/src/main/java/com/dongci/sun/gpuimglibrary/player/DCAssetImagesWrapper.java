package com.dongci.sun.gpuimglibrary.player;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DCAssetImagesWrapper extends DCAssetWrapper {
    private List<Bitmap> bitmaps = new ArrayList<>();

    DCAssetImagesWrapper(DCAsset asset) {
        super(asset);
        if (mAsset.imagePaths != null) {
            for (String path : mAsset.imagePaths) {
                //Bitmap bmp = BitmapFactory.decodeFile(path);
                if(DCBitmapManager.getInstance().contains(path)) {
                    bitmaps.add(DCBitmapManager.getInstance().getBitmap(path));
                } else {
                    Bitmap bmp = DCAssetImagesWrapper.readBitmapFromFileDescriptor(path);
                    Bitmap newBitmap = createWaterMaskBitmap(bmp);
                    if(newBitmap != null) {
                        bitmaps.add(newBitmap);
                        DCBitmapManager.getInstance().put(path,newBitmap);
                    }

                }
            }
        }
    }

    private static Bitmap createWaterMaskBitmap(Bitmap watermark) {
        if (watermark == null) {
            return null;
        }
        int width = watermark.getWidth();
        int height = watermark.getHeight();
        Bitmap newb = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newb);
        canvas.drawBitmap(watermark, 0, 0, null);
        canvas.drawColor(Color.alpha(1));
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return newb;
    }

    public static Bitmap readBitmapFromFileDescriptor(String filePath) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            return BitmapFactory.decodeFileDescriptor(fis.getFD());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
//            for (Bitmap bmp : bitmaps) {
//                if (bmp != null) {
//                    bmp.recycle();
//                }
//            }
         bitmaps.clear();
        }
    }
}
