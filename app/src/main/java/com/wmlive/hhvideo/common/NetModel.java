package com.wmlive.hhvideo.common;

import com.wmlive.hhvideo.common.network.DCRequest;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.recordmv.MvMaterialEntity;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.networklib.observer.DCNetObserver;

import static com.wmlive.hhvideo.common.network.DCRequest.getHttpApi;

public class NetModel {


    public static void getOpusMaterial(long opusId, NetCallback viewCallback) {
        DCRequest.getRetrofit().getObservable("", HttpConstant.TYPE_GET_MATERIAL, getHttpApi().getMvOpusMaterial(InitCatchData.getOpusMaterial(), opusId), null)
                .subscribe(new DCNetObserver<MvMaterialEntity>() {

                    @Override
                    public void onRequestDataReady(int requestCode, String message, MvMaterialEntity response) {
                        if (viewCallback != null) {
                            viewCallback.onRequestOK(response);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        viewCallback.onRequestError(requestCode, message);
                    }
                });

    }


    public interface NetCallback {
        void onRequestOK(Object o);

        void onRequestError(int requestCode, Object o);
    }

}
