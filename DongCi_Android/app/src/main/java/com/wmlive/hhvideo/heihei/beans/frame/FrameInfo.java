package com.wmlive.hhvideo.heihei.beans.frame;

import android.graphics.RectF;
import android.util.Log;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.heihei.record.manager.RecordSetting;
import com.wmlive.hhvideo.utils.CommonUtils;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.KLog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenlu on 2017/8/25.
 * 注意：这个类不能再添加和删减字段!!!
 */

public class FrameInfo implements Serializable, Cloneable {
    private static final long serialVersionUID = -5900745583021315883L;

    /**
     * json数据中默认间隔
     */
    public static final int DEF_FRAME_BORDER_WIDTH = 2;
    public static final int VIDEO_QUALITY_LOW = 1;
    public static final int VIDEO_QUALITY_HIGH = 2;
    public float mZoomRate; // 原始坐标点相对于父view宽高的比例
    public float mZoomRatey; // 原始坐标点相对于父view宽高的比例
    public int id;
    public int video_count;
    public String name;
    public String description;
    public String frame_type;
    public String android_vision;
    public String ios_vision;
    public String sep_image;
    private List<LayoutInfo> layout = new ArrayList<>();
    private LayoutInfo anomalyLayout;
    public int realBorderWidth;
    public String publish_image;
    public int video_quality = 1;


    /*新借口属性*/
    public int layout_ratio;
    public int frame_index;
    public int is_recommend;
    public int opus_height;
    public int canvas_width;
    public int feed_show_height;
    public int canvas_height;
    public int opus_width;


    public FrameInfo() {
    }

    /**
     * @param id
     * @param count
     */
    public FrameInfo(int id, int count) {
        this.id = id;
        this.video_count = count;
    }

    public List<LayoutInfo> getLayout() {
        return layout;
    }

    public void setLayout(int parentHeight, List<LayoutInfo> layout) {
        this.layout = layout;
        setRealSize(DeviceUtils.getScreenWH(DCApplication.getDCApp())[0], parentHeight, false);
    }

    public boolean setRealSize(int parentWidth, int parentHeight, boolean hasBorder) {
        if (parentWidth > DeviceUtils.getScreenWH(DCApplication.getDCApp())[0]) {
            return false;
        }
        if (parentHeight > 200) {
            Log.d("tafg", "");
        }
        int dataModelWidth;
        int dataModelHeight;
        if (hasBorder) {
            dataModelWidth = canvas_width + 2 * DEF_FRAME_BORDER_WIDTH;
            dataModelHeight = canvas_height + 2 * DEF_FRAME_BORDER_WIDTH;
        } else {
            dataModelWidth = canvas_width;
            dataModelHeight = canvas_height;
        }
        mZoomRate = parentWidth * 1.0f / dataModelWidth;//当前view宽度与后台画框标准的比例
        mZoomRatey = parentHeight * 1.0f / dataModelHeight;

        realBorderWidth = (int) (mZoomRate * DEF_FRAME_BORDER_WIDTH);//画框边界按比例缩放
//        realBorderWidth = DEF_FRAME_BORDER_WIDTH;//画框边界按比例缩放
        if (realBorderWidth == 0) {
            realBorderWidth = 4;
            mZoomRate = parentWidth * 1.0f / (dataModelWidth + 100);
        }
        float h;
        KLog.d("ggq", "mZoomRate==" + mZoomRate);//宽的比例
        KLog.d("ggq", "mZoomRatey==" + mZoomRatey);//高的比例
        for (LayoutInfo info : layout) {
            info.xr = info.x * mZoomRate;
            info.yr = info.y * mZoomRatey;
            info.wr = info.getWidth() * mZoomRate;
            h = info.height * mZoomRatey;
            info.hr = h > parentHeight ? parentHeight : h;
            info.aspectRatio = (info.getWidth() == 0f ? 1f : info.getWidth()) / (info.height == 0f ? 1f : info.height);

        }
        return true;
    }

    public FrameInfo deepClone() {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(this);
            ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
            ObjectInputStream oi = new ObjectInputStream(bi);
            return (FrameInfo) (oi.readObject());
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(getClass().getSimpleName() + " serializable failure.");
        }
    }

    /**
     * 获取相对于父布局的宽高百分比布局矩形（无边框）
     *
     * @param layoutIndex 子布局位置索引
     * @return
     */
    public RectF getLayoutRelativeRectF(int layoutIndex) {
        return getLayoutRelativeRectF(layoutIndex, 0);
    }

    /**
     * 获取相对于父布局的宽高百分比布局矩形
     *
     * @param layoutIndex 子布局位置索引
     * @param borderWidth 边框宽度
     * @return
     */
    public RectF getLayoutRelativeRectF(int layoutIndex, float borderWidth) {

        int count = getLayout().size();
        if (layoutIndex >= 0 && layoutIndex < count) {
            LayoutInfo info = layout.get(layoutIndex);
            float offset = borderWidth * 0.5f;
            int dataModelWidth = canvas_width;
            int dataModelHeight = canvas_height;
            KLog.d("ggq", "canvas_width==" + canvas_width + "  canvas_height==" + canvas_height+"  info.aspectRatio=="+info.aspectRatio+"  info.x=="+info.x+" info.y=="+info.y);
            return new RectF(
                    (info.x + offset) / dataModelWidth,
                    (info.y + offset) / dataModelHeight,
                    (info.getWidth() + info.x - offset) / dataModelWidth,
                    (info.height + info.y - offset) / dataModelHeight);
        }
        return new RectF(0f, 0f, 1f, 1f);
    }

    public float getLayoutAspectRatio(int layoutIndex) {
        int count = getLayout().size();
        KLog.i("==========getLayoutAspectRatio1:");
        if (layoutIndex >= 0 && layoutIndex < count) {
            KLog.i("==========getLayoutAspectRatio2:" + layout.get(layoutIndex));
            return layout.get(layoutIndex).getAspectRatio();
        }
        return 1.0f;
    }

//    @Override
    public String toString() {
        return "FrameInfo{" +
                "mZoomRate=" + mZoomRate +
                ", id=" + id +
                ", video_count=" + video_count +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", frame_type='" + frame_type + '\'' +
                ", android_vision='" + android_vision + '\'' +
                ", ios_vision='" + ios_vision + '\'' +
                ", sep_image='" + sep_image + '\'' +
                ", layout=" + layout +
                ", anomalyLayout=" + anomalyLayout +
                ", realBorderWidth=" + realBorderWidth +
                ", publish_image='" + publish_image + '\'' +
                ", video_quality=" + video_quality +
                ", layout_ratio=" + layout_ratio +
                ", frame_index=" + frame_index +
                ", is_recommend=" + is_recommend +
                ", opus_height=" + opus_height +
                ", canvas_width=" + canvas_width +
                ", feed_show_height=" + feed_show_height +
                ", canvas_height=" + canvas_height +
                ", opus_width=" + opus_width +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        FrameInfo infoEntity = null;
        try {
            infoEntity = (FrameInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return infoEntity;
    }
}
