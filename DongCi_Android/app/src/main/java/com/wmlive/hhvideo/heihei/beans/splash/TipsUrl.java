package com.wmlive.hhvideo.heihei.beans.splash;

import com.alibaba.fastjson.annotation.JSONField;
import com.wmlive.hhvideo.common.base.BaseModel;

import java.util.List;

/**
 * Created by XueFei on 2017/8/1.
 */

public class TipsUrl extends BaseModel {


    private String searchTips;
    private PayUrl pay;
    public EmojiGroupBean emojiGroup;
    public RecordCheck recordCheck;

    public TipsUrl() {
    }

    public String getSearchTips() {
        return searchTips;
    }

    public void setSearchTips(String searchTips) {
        this.searchTips = searchTips;
    }

    public PayUrl getPay() {
        return pay;
    }

    public void setPay(PayUrl pay) {
        this.pay = pay;
    }

    @Override
    public String toString() {
        return "TipsUrl{" +
                "searchTips='" + searchTips + '\'' +
                ", pay=" + pay +
                '}';
    }

    public static class EmojiGroupBean {
        @JSONField(name = "default")
        public List<String> emojiDefault;

    }

    public static class RecordCheck {
        public String tips;
        public boolean showTip;
    }
}
