package com.wmlive.hhvideo.heihei.beans.main;

import com.wmlive.hhvideo.heihei.beans.discovery.Banner;
import com.wmlive.hhvideo.heihei.beans.discovery.MusicInfoBean;
import com.wmlive.hhvideo.heihei.beans.discovery.TopicInfoBean;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.networklib.entity.BaseResponse;

import java.util.List;

/**
 * Created by vhawk on 2017/5/24.
 */

public class RecommendResponse extends BaseResponse {
    public List<ShortVideoItem> recommend_opus_list;//推荐列表

    public List<ShortVideoItem> opus_list;//关注列表
    public int follow_count;//关注的人数

    public MusicInfoBean music_info;
    public List<ShortVideoItem> music_opus;//音乐列表

    public TopicInfoBean topic_info;
    public List<ShortVideoItem> topic_opus; //话题列表

    public List<ShortVideoItem> time_opus_list;//最新列表

    public List<Banner> recommend_banners;//广告

    public List<UserInfo> recommend_users;
}
