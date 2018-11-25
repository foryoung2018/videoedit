package com.dongci.sun.gpuimglibrary.player;

import android.graphics.Bitmap;
import java.util.HashMap;

public class DCBitmapManager {
    private HashMap<String,Bitmap> bitmapHashMap;

    private static DCBitmapManager instance;

    public static DCBitmapManager getInstance() {
        if (instance == null) {
            instance = new DCBitmapManager();
        }
        return instance;
    }

    private DCBitmapManager() {
        bitmapHashMap = new HashMap<>();
    }


    public void put(String path,Bitmap bitmap) {
        bitmapHashMap.put(path,bitmap);
    }

    public void clearBitmaps() {

        for (Bitmap bmp : bitmapHashMap.values()) {
            if (bmp != null) {
                    bmp.recycle();
                }
        }

        bitmapHashMap.clear();
    }

    public boolean contains(String path) {
        return bitmapHashMap.containsKey(path);
    }

    public Bitmap getBitmap(String path) {
        return bitmapHashMap.get(path);
    }


}
