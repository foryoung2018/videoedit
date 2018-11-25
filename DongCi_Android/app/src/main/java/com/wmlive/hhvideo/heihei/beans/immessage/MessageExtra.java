package com.wmlive.hhvideo.heihei.beans.immessage;

/**
 * Created by lsq on 2/1/2018.6:52 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class MessageExtra {
    public long opus_id;
    public long user_id;
    public long comment_id;
    public long count;
    public long name;
    public long icon_url;
    public long gift_type;
    public long id;

    @Override
    public String toString() {
        return "MessageExtra{" +
                "opus_id=" + opus_id +
                ", user_id=" + user_id +
                ", comment_id=" + comment_id +
                ", count=" + count +
                ", name=" + name +
                ", icon_url=" + icon_url +
                ", gift_type=" + gift_type +
                ", id=" + id +
                '}';
    }
}
