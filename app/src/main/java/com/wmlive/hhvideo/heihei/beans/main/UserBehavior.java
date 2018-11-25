package com.wmlive.hhvideo.heihei.beans.main;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * 用户行为
 * Created by vhawk on 2017/6/20.
 */

public class UserBehavior extends BaseModel {


    /**
     * 视频id
     */
    private long videoId;
    /**
     * 观看时长
     */
    private long wtachlLength;
    /**
     * 分享次数
     */
    private int shareCount = 0;
    /**
     * 下载次数
     */
    private int downloadCount = 0;

    public UserBehavior() {
    }

    public UserBehavior(long videoId, long wtachlLength, int shareCount, int downloadCount) {
        this.videoId = videoId;
        this.wtachlLength = wtachlLength;
        this.shareCount = shareCount;
        this.downloadCount = downloadCount;
    }

    public long getVideoId() {
        return videoId;
    }

    public void setVideoId(long videoId) {
        this.videoId = videoId;
    }

    public long getWtachlLength() {
        return wtachlLength;
    }

    public void setWtachlLength(long wtachlLength) {
        this.wtachlLength = wtachlLength;
    }

    public int getShareCount() {
        return shareCount;
    }

    public void setShareCount(int shareCount) {
        this.shareCount = shareCount;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    @Override
    public String toString() {
        return "UserBehavior{" +
                "videoId=" + videoId +
                ", wtachlLength=" + wtachlLength +
                ", shareCount=" + shareCount +
                ", downloadCount=" + downloadCount +
                '}';
    }
}
