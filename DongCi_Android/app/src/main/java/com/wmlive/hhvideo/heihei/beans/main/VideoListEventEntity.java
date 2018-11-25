package com.wmlive.hhvideo.heihei.beans.main;

import com.wmlive.hhvideo.heihei.beans.discovery.Banner;

import java.util.List;

/**
 * Created by lsq on 5/7/2018 - 6:26 PM
 * 类描述：
 */
public class VideoListEventEntity {
    public boolean isRefresh;
    public int fromPageId;
    public List<ShortVideoItem> videoList;
    public List<Banner> bannerList;
    public boolean hasMore;
    public int nextPageOffset;
}
