package com.wmlive.hhvideo.heihei.beans.discovery;

import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.networklib.entity.BaseResponse;

import java.util.List;

/**
 * Created by lsq on 9/20/2017.
 */

public class RecommendUserResponse extends BaseResponse {
    public List<UserInfo> recommend_users;
}
