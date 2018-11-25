package com.wmlive.hhvideo.heihei.beans.record;

/**
 * Created by lsq on 8/28/2017.
 * 滤镜的实体类
 */

public class FilterInfoEntity {
    public int filterId;
    public int titleId;
    public int drawableId;

    public FilterInfoEntity(int filterId, int drawableId, int titleId) {
        this.filterId = filterId;
        this.titleId = titleId;
        this.drawableId = drawableId;
    }

    @Override
    public String toString() {
        return "FilterInfoEntity{" +
                "filterId=" + filterId +
                ", titleId=" + titleId +
                ", drawableId=" + drawableId +
                '}';
    }
}
