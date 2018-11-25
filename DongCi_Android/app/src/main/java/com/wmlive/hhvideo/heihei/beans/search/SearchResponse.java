package com.wmlive.hhvideo.heihei.beans.search;

import com.wmlive.networklib.entity.BaseResponse;

import java.util.List;

/**
 * s搜索结果
 * Created by kangzhen on 2017/6/2.
 */

public class SearchResponse extends BaseResponse {
    private List<SearchUserBean> users;//关注的人用户列表
    private List<SearchUserBean> user_list;//用户列表
    private List<SearchMusicBean> music_list;//音乐列表
    private SearchNotExistsInfoBean not_exists_info;//无搜索信息
    private List<SearchTopicBean> topic_list;//话题

    public SearchResponse() {
    }

    public List<SearchUserBean> getUser_list() {
        return user_list;
    }

    public void setUser_list(List<SearchUserBean> user_list) {
        this.user_list = user_list;
    }

    public List<SearchMusicBean> getMusic_list() {
        return music_list;
    }

    public void setMusic_list(List<SearchMusicBean> music_list) {
        this.music_list = music_list;
    }

    public SearchNotExistsInfoBean getNot_exists_info() {
        return not_exists_info;
    }

    public void setNot_exists_info(SearchNotExistsInfoBean not_exists_info) {
        this.not_exists_info = not_exists_info;
    }

    public List<SearchUserBean> getUsers() {
        return users;
    }

    public void setUsers(List<SearchUserBean> users) {
        this.users = users;
    }

    public List<SearchTopicBean> getTopic_list() {
        return topic_list;
    }

    public void setTopic_list(List<SearchTopicBean> topic_list) {
        this.topic_list = topic_list;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SearchResponse{").append("has_more=").append(isHas_more()).append(", offset=").append(getOffset()).append(",user_list=[");
        if (user_list != null) {
            for (SearchUserBean userbean : user_list) {
                sb.append(userbean.toString()).append(",");
            }
        }
        sb.append("],music_list=[");
        if (music_list != null) {
            for (SearchMusicBean musicbean : music_list) {
                sb.append(musicbean.toString()).append(",");
            }
        }
        sb.append("],topic_list=[");
        if (topic_list != null) {
            for (SearchTopicBean topicbean : topic_list) {
                sb.append(topicbean.toString()).append(",");
            }
        }
        sb.append("],not_exists_info=");
        if (not_exists_info != null) {
            sb.append(not_exists_info.toString());
        }
        sb.append("}");
        return sb.toString();
    }
}
