package com.wmlive.hhvideo.fresco;

import android.graphics.PointF;

import com.facebook.drawee.drawable.ScalingUtils;

/**
 * 远程图片 http://, https:// HttpURLConnection <br>
 * 本地文件 file:// FileInputStream<br>
 * Content provider content:// ContentResolver<br>
 * asset目录下的资源 asset:// AssetManager<br>
 * res目录下的资源 res:// ,"res://包名(实际可以是任何字符串甚至留空)/" + R.drawable.ic_launcher<br>
 *
 * @author CCCMAX
 */
public class FrescoParam {

    private String URI = "";
    public int DefaultImageID = 0;

    public int image_type = ImageType.ORIGINAL;
    public int size_mode = SizeMode.L;

    public ScalingUtils.ScaleType scaletype = ScalingUtils.ScaleType.CENTER_CROP;

    public PointF scaleFocusPoint = null;

    /**
     * 圆形，如果设置圆，圆角半径失效
     */
    private boolean isRound = false;
    /**
     * 圆角半径
     */
    private float Radius_topLeft, Radius_topRight, Radius_bottomRight, Radius_bottomLeft;

    public void setNoRoundingParams(boolean noRoundingParams) {
        this.noRoundingParams = noRoundingParams;
    }

    private boolean noRoundingParams = true;

    /**
     * 描边颜色
     */
    private int Borde_color = 0xFFFFFFFF;
    /**
     * 描边宽度
     */
    private int Borde_width = -1;

    /**
     * 图片加载失败时 是否可以点击重新加载
     */
    private boolean ClickToRetryEnabled = false;

    public int FailsImageID = 0;//加载失败是的占位符

    public FrescoParam() {
    }

    public FrescoParam(String uri) {
        setURI(uri);
    }

    public FrescoParam setURI(String uri) {
        this.URI = uri;
        if (uri != null)
            if (uri.startsWith("/storage/") || uri.startsWith("/system") || uri.startsWith("/mnt")) {
                this.URI = "file://" + uri;
            }
        return FrescoParam.this;
    }

    public FrescoParam setDefaultImage(int resid) {
        this.DefaultImageID = resid;
        return FrescoParam.this;
    }

    public FrescoParam setFailsImage(int resid) {
        this.FailsImageID = resid;
        return FrescoParam.this;
    }

    /**
     * view缩放模式
     *
     * @param scale_type
     */
    public FrescoParam setScaleType(ScalingUtils.ScaleType scale_type) {
        scaletype = scale_type;
        return FrescoParam.this;
    }

    /**
     * 设置图片缩放模式时的中心点
     *
     * @param x 范围0~1 0屏幕左边 1屏幕右边
     * @param y 范围0~1 0屏幕上边 1屏幕下边
     * @return
     */
    public FrescoParam setScaleFocusPoint(float x, float y) {
        scaleFocusPoint = new PointF(x, y);
        return FrescoParam.this;
    }

    /**
     * 描边
     *
     * @param color
     * @param width
     * @return
     */
    public FrescoParam setBorde(int color, int width) {
        this.Borde_color = color;
        this.Borde_width = width;
        return FrescoParam.this;
    }

    public int getBordeColor() {
        return Borde_color;
    }

    public int getBordeWidth() {
        return Borde_width;
    }

    /**
     * 设置圆形
     *
     * @param isround
     */
    public FrescoParam setRoundAsCircle(boolean isround) {
        isRound = isround;
        noRoundingParams = false;
        return FrescoParam.this;
    }


    /**
     * 圆角半径
     *
     * @param radius
     */
    public FrescoParam setRoundedCornerRadius(float radius) {
        setRoundedCornerRadius(radius, radius, radius, radius);
        noRoundingParams = false;
        return FrescoParam.this;
    }

    /**
     * 加载图片失败时 点击重新加载
     *
     * @param retry
     * @return
     */
    public FrescoParam setClickToRetryEnabled(boolean retry) {
        this.ClickToRetryEnabled = retry;
        return FrescoParam.this;
    }

    public boolean isNoRoundingParams() {
        return noRoundingParams;
    }

    public boolean getRoundAsCircle() {
        return isRound;
    }

    public float getRadius_TL() {
        return Radius_topLeft;
    }

    public float getRadius_TR() {
        return Radius_topRight;
    }

    public float getRadius_BL() {
        return Radius_bottomLeft;
    }

    public float getRadius_BR() {
        return Radius_bottomRight;
    }

    public String getURI() {
        return URI;
    }

    public boolean getClickToRetryEnabled() {
        return ClickToRetryEnabled;
    }

    /**
     * 圆角半径
     *
     * @param topLeft     左上
     * @param topRight    右上
     * @param bottomLeft  左下
     * @param bottomRight 右下
     */
    public void setRoundedCornerRadius(float topLeft, float topRight, float bottomLeft, float bottomRight) {
        Radius_topLeft = topLeft;
        Radius_topRight = topRight;
        Radius_bottomRight = bottomRight;
        Radius_bottomLeft = bottomLeft;

    }

    public static interface ImageType {

        public final static int ORIGINAL = 0;// 原图
    }

    public static interface SizeMode {

        public final static int S = 1;
        public final static int M = 2;
        public final static int L = 3;
    }
}
