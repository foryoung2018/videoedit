package com.wmlive.hhvideo.heihei.beans.main;

import android.text.TextUtils;

/**
 * Created by lsq on 7/26/2017.
 * 弹幕的实体类
 */

public class DanmuEntity {

    //<d p="2.741000175476,1,25,‭16711935‬,1486737873,0,285e68d8,2987008796">试试酷不酷</d>
    public float time;   //0:时间(弹幕出现时间)   单位：秒
    public int type = 1;        //1:类型(1从右至左滚动弹幕|6从左至右滚动弹幕|5顶端固定弹幕|4底端固定弹幕|7高级弹幕|8脚本弹幕)
    public int textSize = 20;    //2:字号
    public int textColor = 16777215;  //3:颜色  ‭16777215‬是十进制白色，对应十六进制FFFFFF
    public long timestamp = 1486737873;  //     4:时间戳 ?  可选
    public long poolId = 0;     //     5:弹幕池id  可选
    public long userHashId = 0; // 6:用户hash      可选
    public long id = 0;         //  7:弹幕id
    public String text;     //弹幕文字

    public DanmuEntity(float time, long id, String text) {
        this.time = time;
        this.id = id;
        this.text = text;
    }

    public DanmuEntity(float time, long userHashId, long id, String text) {
        this.time = time;
        this.userHashId = userHashId;
        this.id = id;
        this.text = text;
    }

    public DanmuEntity(float time, int type, int textSize, int textColor, long timestamp, long poolId, long userHashId, long id, String text) {
        this.time = time;
        this.type = type;
        this.textSize = textSize;
        this.textColor = textColor;
        this.timestamp = timestamp;
        this.poolId = poolId;
        this.userHashId = userHashId;
        this.id = id;
        this.text = text;
    }

    /**
     * 生成一条xml弹幕
     *
     * @return
     */
    public String createDanmuString() {
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        StringBuilder sb = new StringBuilder(200);
        sb.append("<d p=\"")
                .append(String.valueOf(time)).append(",")
                .append(String.valueOf(type)).append(",")
                .append(String.valueOf(textSize)).append(",")
                .append(String.valueOf(textColor)).append(",")
                .append(String.valueOf(timestamp)).append(",")
                .append(String.valueOf(poolId)).append(",")
                .append(String.valueOf(userHashId)).append(",")
                .append(String.valueOf(id)).append("\">")
                .append(text)
                .append("</d>");
        return sb.toString();
    }
}