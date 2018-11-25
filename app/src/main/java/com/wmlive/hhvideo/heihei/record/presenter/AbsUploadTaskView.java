package com.wmlive.hhvideo.heihei.record.presenter;

import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.heihei.beans.opus.UploadMaterialEntity;

/**
 * Created by lsq on 9/12/2017.
 */

public abstract class AbsUploadTaskView implements BaseView {
    public abstract void onUploading(int index, long currentSize, long totalSize);

    public abstract void onUploadOk(int index, UploadMaterialEntity entity);

    public abstract void onUploadFail(int index, String message);

    @Override
    public void onRequestDataError(int requestCode, String message) {

    }

}
