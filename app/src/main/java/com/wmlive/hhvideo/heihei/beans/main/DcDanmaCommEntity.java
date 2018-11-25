package com.wmlive.hhvideo.heihei.beans.main;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * Created by lsq on 8/1/2017.
 * 礼物弹幕内容
 */

public class DcDanmaCommEntity extends BaseModel {
    public int count;
    public String icon_url;
    public String icon_webp_url;
    public int gift_type;
    public long id;
    public String name;

    public DcDanmaCommEntity() {
    }

    public DcDanmaCommEntity(long id, int count, String icon_url, String name) {
        this.id = id;
        this.count = count;
        this.icon_url = icon_url;
        this.name = name;
    }
}
