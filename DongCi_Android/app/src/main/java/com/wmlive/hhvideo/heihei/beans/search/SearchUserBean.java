package com.wmlive.hhvideo.heihei.beans.search;

import android.text.TextUtils;

import com.wmlive.hhvideo.common.base.BaseModel;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.beans.login.VerifyEntity;
import com.wmlive.hhvideo.heihei.beans.personal.UserHomeData;
import com.wmlive.hhvideo.heihei.beans.personal.UserHomeRelation;

import java.util.List;

/**
 * 搜索中用户信息
 * Created by kangzhen on 2017/6/2.
 */

public class SearchUserBean extends BaseModel {
    private UserHomeRelation relation;
    private String name;
    private String cover_url;
    private List<?> honours;
    private String dc_num;
    private UserHomeData data;
    private long id;
    private String description;
    private VerifyEntity verify;
    public boolean isBlock = true;//黑名单列表使用
    private String gender;

    public String getGender() {
        return gender;
    }

    public boolean isFemale() {
        if (!TextUtils.isEmpty(gender)) {
            return UserInfo.FEMALE.equalsIgnoreCase(gender);
        }
        return false;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public VerifyEntity getVerify() {
        return verify;
    }

    public void setVerify(VerifyEntity verify) {
        this.verify = verify;
    }

    public SearchUserBean() {
    }

    public UserHomeRelation getRelation() {
        return relation;
    }

    public void setRelation(UserHomeRelation relation) {
        this.relation = relation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover_url() {
        return cover_url;
    }

    public void setCover_url(String cover_url) {
        this.cover_url = cover_url;
    }

    public List<?> getHonours() {
        return honours;
    }

    public void setHonours(List<?> honours) {
        this.honours = honours;
    }

    public String getDc_num() {
        return dc_num;
    }

    public void setDc_num(String dc_num) {
        this.dc_num = dc_num;
    }

    public UserHomeData getData() {
        return data;
    }

    public void setData(UserHomeData data) {
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "SearchUserBean{" +
                "relation=" + relation +
                ", name='" + name + '\'' +
                ", cover_url='" + cover_url + '\'' +
                ", honours='" + honours + '\'' +
                ", dc_num='" + dc_num + '\'' +
                ", data=" + data +
                ", id='" + id + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
