package com.wmlive.hhvideo.heihei.beans.personal;

import com.wmlive.networklib.entity.BaseResponse;

/**
 * Created by hsing on 2018/3/13.
 */

public class BlockUserResponse extends BaseResponse {

    public boolean is_block;

    @Override
    public String toString() {
        return "BlockUserResponse{" +
                "is_block=" + is_block +
                '}';
    }
}
