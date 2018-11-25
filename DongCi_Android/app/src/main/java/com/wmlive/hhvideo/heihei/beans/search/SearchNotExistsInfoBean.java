package com.wmlive.hhvideo.heihei.beans.search;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * 搜索之话题搜索
 * Created by kangzhen on 2017/6/2.
 */

public class SearchNotExistsInfoBean extends BaseModel {
    private String name;
    private String desc;

    public SearchNotExistsInfoBean() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
