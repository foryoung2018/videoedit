package com.wmlive.hhvideo.heihei.beans.personal;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * Created by XueFei on 2017/7/26.
 * <p>
 * 兑换实体
 */

public class UserAccountDuihuanEntry extends BaseModel {
    private int point;
    private int id;
    private int gold;
    private String desc;

    public UserAccountDuihuanEntry() {
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "UserAccountDuihuanEntry{" +
                "point=" + point +
                ", id=" + id +
                ", gold=" + gold +
                ", desc='" + desc + '\'' +
                '}';
    }
}
