package com.wmlive.hhvideo.heihei.beans.opus;

import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.networklib.entity.BaseResponse;

import java.util.List;

/**
 * Created by lsq on 5/30/2018 - 7:02 PM
 * 类描述：
 */
public class TopListResponse extends BaseResponse {
    public List<ShortVideoItem> opus_list;
    public String top_title;
}
