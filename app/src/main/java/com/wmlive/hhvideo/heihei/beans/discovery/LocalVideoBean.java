package com.wmlive.hhvideo.heihei.beans.discovery;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * Created by lsq on 6/13/2017.
 * 本地视频对象
 */

public class LocalVideoBean extends BaseModel {
    public long id;
    public String title;
    public String album;
    public String artistName;
    public String name;
    public String mimeType;
    public long duration;
    public long size;
    public String path;
    public int expectWidth;
    public int expectHeight;
    public long date;

    public LocalVideoBean() {
    }

    public LocalVideoBean(long id, String title, String album, String artistName,
                          String name, String mimeType, long duration, long size, String path,long date) {
        this.id = id;
        this.title = title;
        this.album = album;
        this.artistName = artistName;
        this.name = name;
        this.mimeType = mimeType;
        this.duration = duration;
        this.size = size;
        this.path = path;
        this.date = date;
    }

    @Override
    public String toString() {
        return "LocalVideoBean{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", album='" + album + '\'' +
                ", artistName='" + artistName + '\'' +
                ", name='" + name + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", path='" + path + '\'' +
                ", expectWidth=" + expectWidth +
                ", expectHeight=" + expectHeight +
                ", date=" + date +
                '}';
    }
}
