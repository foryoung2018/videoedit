package com.wmlive.hhvideo.heihei.beans.main;

import android.text.TextUtils;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * Created by lsq on 2/5/2018.4:58 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class SplashResourceEntity extends BaseModel {
    public short show_time;
    public String cover;
    public String file_md5;
    public String link;
    public int type;
    public String media_type;
    public String desc;
    public long start_time;
    public long end_time;

    public String localPath;

    public String getFileName() {
        if (!TextUtils.isEmpty(cover)) {
            int index = cover.lastIndexOf("/");
            if (index >= 0) {
                return cover.substring(index + 1, cover.length());
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "SplashResourceEntity{" +
                "show_time=" + show_time +
                ", cover='" + cover + '\'' +
                ", file_md5='" + file_md5 + '\'' +
                ", link='" + link + '\'' +
                ", type=" + type +
                ", media_type='" + media_type + '\'' +
                ", desc='" + desc + '\'' +
                ", start_time=" + start_time +
                ", end_time=" + end_time +
                ", localPath='" + localPath + '\'' +
                '}';
    }
}
