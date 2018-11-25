package com.wmlive.hhvideo.heihei.beans.gifts;

import com.wmlive.hhvideo.heihei.beans.personal.UserAccountEntity;
import com.wmlive.networklib.entity.BaseResponse;

import java.util.List;

/**
 * Created by lsq on 1/5/2018.5:19 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class GiftListResponse extends BaseResponse {
    public List<GiftEntity> data;
    public UserAccountEntity user_gold_account;
}
