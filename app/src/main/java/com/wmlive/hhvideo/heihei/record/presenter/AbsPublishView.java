package com.wmlive.hhvideo.heihei.record.presenter;

import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.heihei.beans.opus.PublishResponseEntity;

/**
 * Created by lsq on 9/12/2017.
 */

public abstract class AbsPublishView implements BaseView {
    public abstract void onPublishStart(int index);

    public abstract void onPublishing(int index, int progress);

    public abstract void onPublishOk(PublishResponseEntity entity);

    public abstract void onExportLocal(int code,PublishResponseEntity entity);

    public abstract void onPublishFail(int type, String message);

    @Override
    public void onRequestDataError(int requestCode, String message) {

    }

}
