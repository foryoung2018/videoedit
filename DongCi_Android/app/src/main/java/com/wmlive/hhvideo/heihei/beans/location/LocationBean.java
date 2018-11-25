package com.wmlive.hhvideo.heihei.beans.location;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * 位置信息
 * Created by kangzhen on 2017/7/19.
 */

public class LocationBean extends BaseModel {
    private double latitude;
    private double longitude;
    private String cityName;
    private String address;

    public LocationBean() {
    }

    public LocationBean(double latitude, double longitude, String cityName, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.cityName = cityName;
        this.address = address;
    }

    public void updateLocationBean(double latitude, double longitude, String cityName, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.cityName = cityName;
        this.address = address;
    }

    public void updateLocationBean(LocationBean bean) {
        this.latitude = bean.getLatitude();
        this.longitude = bean.getLongitude();
        this.cityName = bean.getCityName();
        this.address = bean.getAddress();
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "LocationBean{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", cityName='" + cityName + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
