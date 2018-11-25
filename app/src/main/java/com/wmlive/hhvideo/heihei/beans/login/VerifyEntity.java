package com.wmlive.hhvideo.heihei.beans.login;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * Created by lsq on 1/12/2018.6:14 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class VerifyEntity extends BaseModel {
    public String verify_reason;
    public String type; //normal:无认证，official：官方， master：达人，weibo：微博
    public String icon;
}