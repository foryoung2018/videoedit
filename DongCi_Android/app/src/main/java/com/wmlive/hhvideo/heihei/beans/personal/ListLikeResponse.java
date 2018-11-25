package com.wmlive.hhvideo.heihei.beans.personal;

import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.utils.CommonUtils;
import com.wmlive.networklib.entity.BaseResponse;

import java.util.List;

/**
 * Created by XueFei on 2017/6/2.
 * <p>
 * 个人主页--喜欢的作品
 */

public class ListLikeResponse extends BaseResponse {
    private List<ShortVideoItem> user_like_list;
    private List<ShortVideoItem> co_create_opus;
    private List<ShortVideoItem> list_user_opus;

    public ListLikeResponse() {
    }

    public List<ShortVideoItem> getCo_create_opus() {
        return co_create_opus;
    }

    public void setCo_create_opus(List<ShortVideoItem> co_create_opus) {
        this.co_create_opus = co_create_opus;
    }

    public List<ShortVideoItem> getUser_like_list() {
        return user_like_list;
    }

    public void setUser_like_list(List<ShortVideoItem> user_like_list) {
        this.user_like_list = user_like_list;
    }

    public List<ShortVideoItem> getList_user_opus() {
        return list_user_opus;
    }

    public void setList_user_opus(List<ShortVideoItem> list_user_opus) {
        this.list_user_opus = list_user_opus;
    }

    @Override
    public String toString() {
        return "ListLikeResponse{" +
                "user_like_list=" + CommonUtils.printList(user_like_list) +
                "co_create_opus=" + CommonUtils.printList(co_create_opus) +
                "list_user_opus=" + CommonUtils.printList(list_user_opus) +
                '}';
    }
}
