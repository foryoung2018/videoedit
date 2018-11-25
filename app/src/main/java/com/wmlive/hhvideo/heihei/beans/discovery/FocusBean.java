package com.wmlive.hhvideo.heihei.beans.discovery;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 *  Created by jht on 2017/7/31
 * 聚焦实体类
 */
public class FocusBean extends BaseModel{
    /**
     * link : hhvideo://opus/detail?id=1581490151
     * cover : http://s1.wmlives.com/data/dongci/social/20180731110149783679.jpg
     * sub_title : 欢乐谷扛把子
     * title : Highcharts Demo
     */

    private String link;
    private String cover;
    private String sub_title;
    private String title;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getSub_title() {
        return sub_title;
    }

    public void setSub_title(String sub_title) {
        this.sub_title = sub_title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
