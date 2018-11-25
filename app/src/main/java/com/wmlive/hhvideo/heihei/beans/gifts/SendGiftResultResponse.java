package com.wmlive.hhvideo.heihei.beans.gifts;

import com.wmlive.hhvideo.heihei.beans.immessage.DcMessage;
import com.wmlive.hhvideo.heihei.beans.main.CommentDataCount;
import com.wmlive.hhvideo.heihei.beans.personal.UserAccountEntity;
import com.wmlive.networklib.entity.BaseResponse;

/**
 * Created by lsq on 1/16/2018.5:23 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class SendGiftResultResponse extends BaseResponse {
    public UserAccountEntity user_gold_account;
    public GiftRebateEntity settle_msg;
    public CommentDataCount opus_static;
    public DcMessage message;

    @Override
    public String toString() {
        return "SendGiftResultResponse{" +
                "user_gold_account=" + user_gold_account +
                ", settle_msg=" + settle_msg +
                ", opus_static=" + opus_static +
                ", message=" + message +
                '}';
    }
}
