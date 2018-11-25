package com.wmlive.hhvideo.heihei.beans.gifts;

import com.wmlive.hhvideo.common.base.BaseModel;

import java.util.List;

/**
 * Created by lsq on 1/16/2018.5:26 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class GiftRebateEntity extends BaseModel {
    public List<RebateEntity> prize_message;
    public String description;
    public String title;

    @Override
    public String toString() {
        return "GiftRebateEntity{" +
                "prize_message=" + prize_message +
                ", description='" + description + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
