package com.wmlive.hhvideo.utils.location;

/**
 * Created by lsq on 10/23/2017.
 */

public class LocationEntity {
    public String province;
    public String city;
    public String cityCode;
    public String adCode;//区域编码;
    public String district;//区的名称;
    public String address = "北京市东城区东长安街";
    public double longitude = 116.3952753787;
    public double latitude = 39.9059534643;

    @Override
    public String toString() {
        return "LocationEntity{" +
                "province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", adCode='" + adCode + '\'' +
                ", district='" + district + '\'' +
                ", address='" + address + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
