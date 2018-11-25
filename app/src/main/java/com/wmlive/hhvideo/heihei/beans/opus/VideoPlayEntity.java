package com.wmlive.hhvideo.heihei.beans.opus;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * Created by lsq on 5/22/2018 - 5:25 PM
 * 类描述：
 */
public class VideoPlayEntity extends BaseModel {
    public long videoId;
    public String url;
    public int during;
    public int current;
    //是否可用
    public boolean videoAvailable;

    public boolean isVideoAvailable() {
        videoAvailable = videoId > 0
                && during > 0
                && current > 0;
//                && !TextUtils.isEmpty(url);
        return videoAvailable;
    }

    @Override
    public String toString() {
        return "VideoPlayEntity{" +
                "videoId=" + videoId +
                ", url='" + url + '\'' +
                ", during=" + during +
                ", current=" + current +
                ", videoAvailable=" + isVideoAvailable() +
                '}';
    }
}
