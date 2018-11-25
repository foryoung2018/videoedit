package com.wmlive.hhvideo.heihei.beans.splash;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * Created by vhawk on 2017/5/22.
 */

public class TopicUrl extends BaseModel {

    /**
     * listTopic : http://api.dongci-test.wmlives.com/api/topic/list-topic
     * listOpusByTopic : http://api.dongci-test.wmlives.com/api/topic/list-topic-opus
     * search : http://api.dongci-test.wmlives.com/api/topic/search
     * create : http://api.dongci-test.wmlives.com/api/topic/create
     * "newTopicCheck": "http:\/\/api.dongci-test.wmlives.com\/api\/topic\/notify"
     */

    private String listTopic;
    private String listOpusByTopic;
    private String search;
    private String create;
    private String newTopicCheck;
    private String listSystemNews;

    public String getListTopic() {
        return listTopic;
    }

    public void setListTopic(String listTopic) {
        this.listTopic = listTopic;
    }

    public String getListOpusByTopic() {
        return listOpusByTopic;
    }

    public void setListOpusByTopic(String listOpusByTopic) {
        this.listOpusByTopic = listOpusByTopic;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getCreate() {
        return create;
    }

    public void setCreate(String create) {
        this.create = create;
    }

    public String getNewTopicCheck() {
        return newTopicCheck;
    }

    public void setNewTopicCheck(String newTopicCheck) {
        this.newTopicCheck = newTopicCheck;
    }

    public String getListSystemNews() {
        return listSystemNews;
    }

    public void setListSystemNews(String listSystemNews) {
        this.listSystemNews = listSystemNews;
    }

    @Override
    public String toString() {
        return "TopicUrl{" +
                "listTopic='" + listTopic + '\'' +
                ", listOpusByTopic='" + listOpusByTopic + '\'' +
                ", search='" + search + '\'' +
                ", create='" + create + '\'' +
                ", newTopicCheck='" + newTopicCheck + '\'' +
                ", listSystemNews='" + listSystemNews + '\'' +
                '}';
    }
}
