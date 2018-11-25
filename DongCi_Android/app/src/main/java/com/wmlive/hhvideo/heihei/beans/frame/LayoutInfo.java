package com.wmlive.hhvideo.heihei.beans.frame;

import com.wmlive.hhvideo.utils.KLog;

import java.io.Serializable;

/**
 * Created by wenlu on 2017/8/25.
 */

public class LayoutInfo implements Serializable, Cloneable {

    public static final String TYPE_SHAPE_RECT = "rect";
    public static final String TYPE_SHAPE_CIRCLE = "circle";
    public static final String TYPE_SHAPE_OVAL = "oval";
    public static final String TYPE_SHAPE_ARC = "arc";

    public static float MINI_ZOOM_RATE = 0.1f;

    public float x = 0f;
    public float y = 0f;
    private float width = 0f;
    public float height = 0f;
    public float aspectRatio = 1f;

    public float xr = 0f;
    public float yr = 0f;
    public float wr = 0f;
    public float hr = 0f;

    public String layout_type = TYPE_SHAPE_RECT;

    public LayoutInfo() {

    }

    /**
     * @param x
     * @param y
     * @param w
     * @param h
     */
    public LayoutInfo(float x, float y, float w, float h) {
        this(x, y, w, h, TYPE_SHAPE_RECT);
    }

    /**
     * @param x
     * @param y
     * @param w
     * @param h
     * @param type
     */
    public LayoutInfo(float x, float y, float w, float h, String type) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.layout_type = type;
        aspectRatio = (w == 0f ? 1f : w) / (h == 0f ? 1f : h);
        KLog.i("======设置画框参数1：" + toString());
//        initPoints();
//        initPath();
    }

    public void setWidth(int width){
        this.width = width;
    }

    public float getWidth(){
        return width;
    }

    public float getAspectRatio() {
        aspectRatio = (width == 0f ? 1f : width) / (height == 0f ? 1f : height);
        return aspectRatio;
    }

    @Override
    public String toString() {
        return "LayoutInfo{" +
                "x=" + x +
                ", y=" + y +
                ", w=" + width +
                ", h=" + height +
                ", xr=" + xr +
                ", yr=" + yr +
                ", wr=" + wr +
                ", hr=" + hr +
                ", aspectRatio=" + getAspectRatio() +
                ", Layout_type='" + layout_type + '\'' +
                '}';
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        LayoutInfo infoEntity = null;
        try {
            infoEntity = (LayoutInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return infoEntity;
    }
}
