package com.wmlive.hhvideo.heihei.beans.main;

import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.beans.opus.UploadMaterialEntity;
import com.wmlive.networklib.entity.BaseResponse;

import java.util.List;

/**
 * Created by vhawk on 2017/5/24.
 */

public class ShortVideoInfoResponse extends BaseResponse {

    private ShortVideoItem opus;

    private UserInfo user;

    public List<UploadMaterialEntity> materials;

    public ShortVideoItem getOpus() {
        return opus;
    }

    public ShortVideoInfoResponse setOpus(ShortVideoItem opus) {
        this.opus = opus;
        return this;
    }

    public UserInfo getUser() {
        return user;
    }

    public ShortVideoInfoResponse setUser(UserInfo user) {
        this.user = user;
        return this;
    }

    public String getMusicName() {
        return getOpus() == null ? null : getOpus().getMusic_name();
    }

    @Override
    public String toString() {
        return "ShortVideoInfoResponse{" +
                "opus=" + opus +
                ", user=" + user +
                ", materials=" + materials +
                '}';
    }

    public boolean isFollow() {
        return !(getUser() == null || getUser().getRelation() == null) && getUser().getRelation().is_follow;
    }

    public boolean isLikeVideo() {
        return getOpus() != null && getOpus().is_like();
    }
}
