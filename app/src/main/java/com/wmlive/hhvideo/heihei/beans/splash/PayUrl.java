package com.wmlive.hhvideo.heihei.beans.splash;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * Created by XueFei on 2017/7/28.
 */

public class PayUrl extends BaseModel {
    private String payTips;
    private String wechat;
    private String paypackageList;
    private String p2gpackageList;
    private String p2g;
    private String createOrder;
    private String iapOrderVerify;

    public PayUrl() {
    }

    public String getPayTips() {
        return payTips;
    }

    public void setPayTips(String payTips) {
        this.payTips = payTips;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getPaypackageList() {
        return paypackageList;
    }

    public void setPaypackageList(String paypackageList) {
        this.paypackageList = paypackageList;
    }

    public String getP2gpackageList() {
        return p2gpackageList;
    }

    public void setP2gpackageList(String p2gpackageList) {
        this.p2gpackageList = p2gpackageList;
    }

    public String getP2g() {
        return p2g;
    }

    public void setP2g(String p2g) {
        this.p2g = p2g;
    }

    public String getCreateOrder() {
        return createOrder;
    }

    public void setCreateOrder(String createOrder) {
        this.createOrder = createOrder;
    }

    public String getIapOrderVerify() {
        return iapOrderVerify;
    }

    public void setIapOrderVerify(String iapOrderVerify) {
        this.iapOrderVerify = iapOrderVerify;
    }

    @Override
    public String toString() {
        return "PayUrl{" +
                "payTips='" + payTips + '\'' +
                ", wechat='" + wechat + '\'' +
                ", paypackageList='" + paypackageList + '\'' +
                ", p2gpackageList='" + p2gpackageList + '\'' +
                ", p2g='" + p2g + '\'' +
                ", createOrder='" + createOrder + '\'' +
                ", iapOrderVerify='" + iapOrderVerify + '\'' +
                '}';
    }
}
