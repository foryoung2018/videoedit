package com.wmlive.hhvideo.heihei.mainhome.util;

/**
 * Created by lsq on 8/1/2017.
 */

public class GiftDanmaEntity {
    public String avatarUrl;
    public String userName;
    public String giftName;
    public String giftUrl;
    public int giftCount;

    public GiftDanmaEntity() {
    }

    public GiftDanmaEntity(String avatarUrl, String userName, String giftName, String giftUrl, int giftCount) {
        this.avatarUrl = avatarUrl;
        this.userName = userName;
        this.giftName = giftName;
        this.giftUrl = giftUrl;
        this.giftCount = giftCount;
    }
}
