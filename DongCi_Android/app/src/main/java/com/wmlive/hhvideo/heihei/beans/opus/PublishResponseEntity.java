package com.wmlive.hhvideo.heihei.beans.opus;

import com.wmlive.hhvideo.heihei.beans.main.ShareInfo;
import com.wmlive.networklib.entity.BaseResponse;

/**
 * Created by lsq on 9/13/2017.
 */

public class PublishResponseEntity extends BaseResponse {
    public long opus_id;
    public ShareInfo share_info;

    @Override
    public String toString() {
        return "PublishResponseEntity{" +
                "opus_id=" + opus_id +
                ", share_info=" + share_info +
                '}';
    }
}
