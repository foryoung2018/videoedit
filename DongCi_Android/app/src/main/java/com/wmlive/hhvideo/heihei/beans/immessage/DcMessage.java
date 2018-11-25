package com.wmlive.hhvideo.heihei.beans.immessage;

import com.wmlive.hhvideo.heihei.db.MessageDetail;

/**
 * Created by lsq on 2/5/2018.10:31 AM
 *
 * @author lsq
 * @describe 添加描述
 */

public class DcMessage {
    public static final String TYPE_IM = "IM";
    public static final String TYPE_IM_CHAT = "ImChat";
    public static final String TYPE_ACTION = "action";

    public String classify; //目前有TYPE_IM,TYPE_ACTION
    public String action;  //目前有：show-tip
    public String comments;  //消息类型的描述
    public MessageDetail message;

    @Override
    public String toString() {
        return "DcMessage{" +
                "classify='" + classify + '\'' +
                ", action='" + action + '\'' +
                ", comments='" + comments + '\'' +
                ", message=" + message +
                '}';
    }
}
