package com.wmlive.hhvideo.heihei.beans.opus;

import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.heihei.beans.search.SearchMusicBean;
import com.wmlive.networklib.entity.BaseResponse;

import java.util.List;

/**
 * Created by wenlu on 2017/9/18.
 */

public class OpusMaterialEntity extends BaseResponse {

    public ShortVideoItem opus;

    public List<UploadMaterialEntity> materials;

    public SearchMusicBean music;
}
