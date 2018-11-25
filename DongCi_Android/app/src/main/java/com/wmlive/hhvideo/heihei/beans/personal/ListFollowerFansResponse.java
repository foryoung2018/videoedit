package com.wmlive.hhvideo.heihei.beans.personal;

import com.wmlive.hhvideo.heihei.beans.search.SearchUserBean;
import com.wmlive.networklib.entity.BaseResponse;

import java.util.List;

/**
 * Created by XueFei on 2017/6/1.
 * <p>
 * 关注  粉丝 列表
 */

public class ListFollowerFansResponse extends BaseResponse {
    public List<SearchUserBean> users;

    public ListFollowerFansResponse() {
    }

    @Override
    public String toString() {
        return "ListFollowerFansResponse{" +
                "users=" + users +
                '}';
    }
}
