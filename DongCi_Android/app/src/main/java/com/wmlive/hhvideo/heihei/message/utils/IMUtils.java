package com.wmlive.hhvideo.heihei.message.utils;

import com.wmlive.hhvideo.heihei.beans.immessage.DcMessage;
import com.wmlive.hhvideo.heihei.beans.immessage.MessageContent;
import com.wmlive.hhvideo.heihei.db.MessageDetail;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.utils.TimeUtil;

import java.util.List;
import java.util.Random;

/**
 * Created by hsing on 2018/3/8.
 */

public class IMUtils {

    public static final long TIME_DELAY_BETWEEN_MESSAGE = 300; // 超过5分钟显示消息时间

    /**
     * 获取本地IM 消息id
     * @return
     */
    public static String getLocalMsgId(){
        //当前的系统时间
        Random random=new Random();
        int randomValue = random.nextInt(100);
        StringBuffer sb = new StringBuffer();
        sb.append(TimeUtil.getDefaultCurrentTime()).append(randomValue);
        return sb.toString();
    }

    /**
     * 创建时间消息
     * @param messageDetail
     * @return
     */
    public static MessageDetail createTimeMessage(MessageDetail messageDetail) {
        if (messageDetail != null) {
            //添加一条时间记录
            MessageDetail imMsgSystemTime = null;
            //获取最后一条数据的时间
            imMsgSystemTime = new MessageDetail();
            imMsgSystemTime.setLocal_msg_id(messageDetail.getLocal_msg_id() + "t");
            imMsgSystemTime.setStatus(MessageDetail.IM_STATUS_SENDING);
            imMsgSystemTime.setCreate_time(messageDetail.getCreate_time() - 1);

            MessageContent contentTimeBean = new MessageContent();
            contentTimeBean.text = String.valueOf(imMsgSystemTime.create_time);
            imMsgSystemTime.setMessageContent(contentTimeBean);
            imMsgSystemTime.fromUserId = messageDetail.getFromUserId();
            imMsgSystemTime.toUserId = messageDetail.getToUserId();
            imMsgSystemTime.belongUserId = AccountUtil.getUserId();
            imMsgSystemTime.setImType(DcMessage.TYPE_IM_CHAT);
            imMsgSystemTime.setMsg_type(MessageDetail.TYPE_SYSTIME_CONTENT);
            return imMsgSystemTime;
        } else {
            return null;
        }
    }

    /**
     * 增加时间消息
     * @param messageDetailList
     * @param lastIMMsgCreateTime
     */
    public static void addTimeMessageToList(List<MessageDetail> messageDetailList, long lastIMMsgCreateTime) {
        for (int i = 0; i < messageDetailList.size(); i++) {
            if (i == 0) {
                MessageDetail messageDetail = messageDetailList.get(i);
                if (lastIMMsgCreateTime > 0) {
                    if (messageDetail.getCreate_time() - lastIMMsgCreateTime >= TIME_DELAY_BETWEEN_MESSAGE) {
                        MessageDetail timeMessage = IMUtils.createTimeMessage(messageDetail);
                        if (timeMessage != null) {
                            messageDetailList.add(i, timeMessage);
                            i++;
                        }
                    }
                } else {
                    MessageDetail timeMessage = IMUtils.createTimeMessage(messageDetail);
                    if (timeMessage != null) {
                        messageDetailList.add(i, timeMessage);
                        i++;
                    }
                }
            } else {
                MessageDetail preMsg = messageDetailList.get(i - 1);
                MessageDetail curMsg = messageDetailList.get(i);

                if (MessageDetail.TYPE_SYSTIME_CONTENT.equals(preMsg.getMsg_type())
                        || MessageDetail.TYPE_SYSTIME_CONTENT.equals(curMsg.getMsg_type())) {
                    return;
                }
                if (curMsg.getCreate_time() - preMsg.getCreate_time() >= TIME_DELAY_BETWEEN_MESSAGE) {
                    MessageDetail timeMessage = IMUtils.createTimeMessage(curMsg);
                    if (timeMessage != null) {
                        messageDetailList.add(i, timeMessage);
                        i ++;
                    }
                }
            }
        }
    }
}
