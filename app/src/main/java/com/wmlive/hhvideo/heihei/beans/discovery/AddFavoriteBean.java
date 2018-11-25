package com.wmlive.hhvideo.heihei.beans.discovery;

import com.wmlive.networklib.entity.BaseResponse;

/**
 * Created by lsq on 6/5/2017.
 */

public class AddFavoriteBean extends BaseResponse {
    private boolean is_favorite;

    public boolean is_favorite() {
        return is_favorite;
    }

    public void setIs_favorite(boolean is_favorite) {
        this.is_favorite = is_favorite;
    }
}
