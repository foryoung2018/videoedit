package com.wmlive.hhvideo.heihei.beans.personal;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * Created by XueFei on 2017/6/5.
 * <p>
 * 举报
 */

public class ReportType extends BaseModel {
    private int resource;
    private int id;
    private String desc;


    public ReportType() {
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "ReportType{" +
                "resource=" + resource +
                ", id=" + id +
                ", desc='" + desc + '\'' +
                '}';
    }
}
