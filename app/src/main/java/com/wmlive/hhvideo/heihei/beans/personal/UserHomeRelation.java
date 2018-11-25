package com.wmlive.hhvideo.heihei.beans.personal;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * Created by XueFei on 2017/5/31.
 * <p>
 * <p>
 * 个人主页--关注 粉丝数据
 */

public class UserHomeRelation extends BaseModel {
    public int fans_count;
    public int follow_count;
    public boolean is_follow;
    public boolean is_fans;
    public boolean is_block;

    public UserHomeRelation() {
    }

    @Override
    public String toString() {
        return "UserHomeRelation{" +
                "fans_count=" + fans_count +
                ", is_follow=" + is_follow +
                ", follow_count=" + follow_count +
                ", is_fans=" + is_fans +
                '}';
    }
}
