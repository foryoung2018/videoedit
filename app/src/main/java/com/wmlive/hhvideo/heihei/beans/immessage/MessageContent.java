package com.wmlive.hhvideo.heihei.beans.immessage;

/**
 * Created by lsq on 2/1/2018.6:32 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class MessageContent {
    public String desc;
    public String title;
    public String text;
    public String icon;
    public MessageJump jump;
    public MessageExtra extr_param;

    // audio
    public String audio;
    public String sign; // 文件签名
    public int length;
    public String local_path; //本地路径
    public String local_msg_id; //本地消息Id

    @Override
    public String toString() {
        return "MessageContent{" +
                "desc='" + desc + '\'' +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", icon='" + icon + '\'' +
                ", jump=" + jump +
                ", extr_param=" + extr_param +
                ", audio='" + audio + '\'' +
                ", length=" + length +
                ", local_path='" + local_path + '\'' +
                ", local_msg_id='" + local_msg_id + '\'' +
                '}';
    }
}
