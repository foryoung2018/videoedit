package com.dongci.sun.gpuimglibrary.common;

import android.content.Context;

import com.dongci.sun.gpuimglibrary.gles.filter.GPUImageFilter;
import com.dongci.sun.gpuimglibrary.api.apiTest.LookUpUtils;

/**
 * Created by lsq on 8/28/2017.
 * 滤镜的实体类
 */

public class FilterInfoEntity {
    public int filterId;
    public int titleId;
    public int drawableId;

    public String titleStr = null;


    public void setFilter(GPUImageFilter filter) {
        this.filter = filter;
    }

    public GPUImageFilter getFilter(Context context) {
        if (filter == null)
            filter = LookUpUtils.createFilterLook(context, filterId);
        return filter;
    }

    private GPUImageFilter filter;

    public FilterInfoEntity(int filterId, int drawableId, int titleId) {
        this.filterId = filterId;
        this.titleId = titleId;
        this.drawableId = drawableId;
    }

    public FilterInfoEntity(int filterId, int drawableId, int lookupId, int titleId) {
        this.filterId = filterId;
        this.titleId = titleId;
        this.drawableId = drawableId;
    }

    public FilterInfoEntity(int filterId, int drawableId, String titlestr) {
        this.filterId = filterId;
        this.titleStr = titlestr;
        this.drawableId = drawableId;
    }

    @Override
    public String toString() {
        return "FilterInfoEntity{" +
                "filterId=" + filterId +
                ", titleStr=" + titleStr +
                ", drawableId=" + drawableId +
                '}';
    }
}
