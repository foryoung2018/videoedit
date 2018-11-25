package com.wmlive.hhvideo.heihei.beans.personal;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * Created by XueFei on 2017/7/26.
 * <p>
 * 充值实体
 */

public class UserAccountChargeEntry extends BaseModel {
    private double pay_money;
    private double origin_money;
    private String product_id;
    private int gold;
    private String description;
    private long id;
    private String channel;
    private String name;

    public UserAccountChargeEntry() {
    }

    public double getPay_money() {
        return pay_money;
    }

    public void setPay_money(double pay_money) {
        this.pay_money = pay_money;
    }

    public double getOrigin_money() {
        return origin_money;
    }

    public void setOrigin_money(double origin_money) {
        this.origin_money = origin_money;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UserAccountChargeEntry{" +
                "pay_money=" + pay_money +
                ", origin_money=" + origin_money +
                ", product_id='" + product_id + '\'' +
                ", gold=" + gold +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", channel='" + channel + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
