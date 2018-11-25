package com.wmlive.hhvideo.heihei.beans.main;

import com.wmlive.hhvideo.common.base.BaseModel;
import com.wmlive.hhvideo.heihei.beans.discovery.TopicInfoBean;
import com.wmlive.hhvideo.heihei.beans.discovery.TopicTypeListBean;
import com.wmlive.hhvideo.heihei.personal.widget.ProductTypePanel;

import java.util.List;

/**
 * Created by Administrator on 6/21/2017.
 * 各种页面跳转视频播放列表的参数
 */

public class MultiTypeVideoBean extends BaseModel {
    public long userId;
    public long topicId;
    public long musicId;
    public long videoId;//单个作品的时候
    public int currentProductType = ProductTypePanel.TYPE_PRODUCT;
    public int nextPageOffset;
    public String pageFrom;//从哪个页面跳转过来的

    public int currentPosition;
    public List<ShortVideoItem> shortVideoItemList;
    public TopicInfoBean topicListBean;

    public MultiTypeVideoBean() {
    }

    public MultiTypeVideoBean(long userId, long topicId, long musicId, int currentPosition, List<ShortVideoItem> shortVideoItemList) {
        this.userId = userId;
        this.topicId = topicId;
        this.musicId = musicId;
        this.currentPosition = currentPosition;
        this.shortVideoItemList = shortVideoItemList;
    }


    public MultiTypeVideoBean(long videoId) {
        this.videoId = videoId;
    }

    @Deprecated
    public static MultiTypeVideoBean createUserParma(long userId, int currentPosition, List<ShortVideoItem> shortVideoItemList) {
        return new MultiTypeVideoBean(userId, 0, 0, currentPosition, shortVideoItemList);
    }

    public static MultiTypeVideoBean createTopicParma(long topicId, int currentPosition, List<ShortVideoItem> shortVideoItemList) {
        return new MultiTypeVideoBean(0, topicId, 0, currentPosition, shortVideoItemList);
    }

    public static MultiTypeVideoBean createTopicParma(long topicId, int currentPosition, TopicTypeListBean.TopicListBean topicListBean, List<ShortVideoItem> shortVideoItemList) {
        MultiTypeVideoBean multiTypeVideoBean = new MultiTypeVideoBean(0, topicId, 0, currentPosition, shortVideoItemList);
        if (topicListBean != null) {
            multiTypeVideoBean.topicListBean = new TopicInfoBean();
            multiTypeVideoBean.topicListBean.setName(topicListBean.getName());
            multiTypeVideoBean.topicListBean.setDescription(topicListBean.getDescription());
            multiTypeVideoBean.topicListBean.setShare_info(topicListBean.getShare_info());
        }
        return multiTypeVideoBean;
    }

    public static MultiTypeVideoBean createMusicParma(long musicId, int currentPosition, List<ShortVideoItem> shortVideoItemList) {
        return new MultiTypeVideoBean(0, 0, musicId, currentPosition, shortVideoItemList);
    }

    public static MultiTypeVideoBean createExplosionParma(int currentPosition, List<ShortVideoItem> shortVideoItemList) {
        return new MultiTypeVideoBean(0, 0, 0, currentPosition, shortVideoItemList);
    }

    public static MultiTypeVideoBean createSingleVideoParma(long videoId) {
        return new MultiTypeVideoBean(videoId);
    }

    public TopicInfoBean getTopicListBean() {
        return topicListBean;
    }

    public long getUserId() {
        return userId;
    }

    public long getTopicId() {
        return topicId;
    }

    public void setTopicId(long topicId) {
        this.topicId = topicId;
    }

    public long getMusicId() {
        return musicId;
    }

    public long getVideoId() {
        return videoId;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public List<ShortVideoItem> getShortVideoItemList() {
        return shortVideoItemList;
    }
}
