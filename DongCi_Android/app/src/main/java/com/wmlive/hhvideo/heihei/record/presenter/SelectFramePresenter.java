package com.wmlive.hhvideo.heihei.record.presenter;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.opus.OpusMaterialEntity;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.networklib.observer.DCNetObserver;

/**
 * Created by lsq on 8/25/2017.
 */

public class SelectFramePresenter extends BasePresenter<SelectFramePresenter.ISelectFrameView> {

    public SelectFramePresenter(ISelectFrameView view) {
        super(view);
    }

    public interface ISelectFrameView extends BaseView {
        void onGetMaterial(OpusMaterialEntity response);
    }

    /**
     * 获取作品素材列表
     *
     * @param opusId
     */
    public void getOpusMaterial(long opusId) {
        executeRequest(HttpConstant.TYPE_GET_MATERIAL, getHttpApi().getOpusMaterial(InitCatchData.getOpusMaterial(), opusId))
                .subscribe(new DCNetObserver<OpusMaterialEntity>() {

                    @Override
                    public void onRequestDataReady(int requestCode, String message, OpusMaterialEntity response) {
                        if (viewCallback != null) {
                            viewCallback.onGetMaterial(response);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(HttpConstant.TYPE_GET_MATERIAL, message);
                    }
                });
    }
}
