package com.wmlive.hhvideo.heihei.beans.main;

import com.wmlive.hhvideo.common.base.BaseModel;
import com.wmlive.hhvideo.heihei.beans.opus.UploadMaterialEntity;

import java.util.List;

/**
 * Created by lsq on 6/4/2018 - 3:35 PM
 * 类描述：
 */
public class ShortVideoWarpItem extends BaseModel {
    public ShortVideoItem opus;
    public List<UploadMaterialEntity> materials;
}
