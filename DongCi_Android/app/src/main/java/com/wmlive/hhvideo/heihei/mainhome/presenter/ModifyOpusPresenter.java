package com.wmlive.hhvideo.heihei.mainhome.presenter;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.main.VideoModifyOpusResponse;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.networklib.observer.DCNetObserver;

/**
 * Created by lsq on 5/8/2018 - 7:51 PM
 * 类描述：
 */
public class ModifyOpusPresenter extends BasePresenter<ModifyOpusPresenter.IModifyOpusView> {

    public ModifyOpusPresenter(IModifyOpusView view) {
        super(view);
    }

    /**
     * 修改共同创作权限与描述
     *
     * @param position
     * @param shortVideoId
     */
    public void modifyOpus(final int position, final long shortVideoId, String title, String creationFlag) {
        executeRequest(HttpConstant.TYPE_EDIT_OPUS_INFO
                , getHttpApi().modifyOpus(InitCatchData.opusModifyOpus(), shortVideoId, title, creationFlag))
                .subscribe(new DCNetObserver<VideoModifyOpusResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, VideoModifyOpusResponse response) {
                        if (viewCallback != null) {
                            viewCallback.onModifyOpusOk(position, shortVideoId, response);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (viewCallback != null) {
                            viewCallback.onModifyOpusFail(position, shortVideoId, message);
                        }
                    }
                });
    }

    public interface IModifyOpusView extends BaseView {
        void onModifyOpusOk(int position, long videoId, VideoModifyOpusResponse response);

        void onModifyOpusFail(int position, long videoId, String message);
    }
}
