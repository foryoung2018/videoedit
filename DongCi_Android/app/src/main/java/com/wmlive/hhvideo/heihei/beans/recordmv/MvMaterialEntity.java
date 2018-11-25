package com.wmlive.hhvideo.heihei.beans.recordmv;

import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.heihei.beans.opus.UploadMaterialEntity;
import com.wmlive.hhvideo.heihei.beans.search.SearchMusicBean;
import com.wmlive.networklib.entity.BaseResponse;

import java.util.List;

/**
 * Created by jht on 2018/10/10.
 */

public class MvMaterialEntity extends BaseResponse {

    public ShortVideoItem opus;

    public List<UploadMaterialEntity> materials;

    public SearchMusicBean music;

    public MvTemplateEntity template;

    public MvBgEntity  bg;

}
