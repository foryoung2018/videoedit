package com.wmlive.hhvideo.heihei.beans.location;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * URL 包含信息
 * Author：create by jht on 2018/9/6 16:26
 * Email：haitian.jiang@welines.cn
 */
public class UrlBean extends BaseModel {
    public String ip;
    public String cip;
    public String region;
    public String city;
    public String isp;
    public String province;

    public UrlBean() {
    }

    public UrlBean(String ip, String cip, String region, String city, String isp, String province) {
        this.ip = ip;
        this.cip = cip;
        this.region = region;
        this.city = city;
        this.isp = isp;
        this.province = province;
    }

    @Override
    public String toString() {
        return "UrlBean{" +
                "ip='" + ip + '\'' +
                ", cip='" + cip + '\'' +
                ", region='" + region + '\'' +
                ", city='" + city + '\'' +
                ", isp='" + isp + '\'' +
                ", province='" + province + '\'' +
                '}';
    }
}
