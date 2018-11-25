package com.wmlive.hhvideo.heihei.beans.gifts;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * Created by lsq on 1/16/2018.4:42 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class GiftRecordEntity extends BaseModel {
    public String giftId;
    public int gold;
    public int clickCount;
    public int tempClickCount;
    public int decibelRebate;
    public int goldRebate;

    public boolean isPendingSettlement;

    public GiftRecordEntity() {
    }

    public boolean isFree() {
        return gold <= 0;
    }

    public GiftRecordEntity(String giftId, int gold) {
        this.giftId = giftId;
        this.gold = gold;
    }

    @Override
    public String toString() {
        return "GiftRecordEntity{" +
                "giftId='" + giftId + '\'' +
                ", gold=" + gold +
                ", clickCount=" + clickCount +
                ", tempClickCount=" + tempClickCount +
                ", decibelRebate=" + decibelRebate +
                ", goldRebate=" + goldRebate +
                '}';
    }
}
