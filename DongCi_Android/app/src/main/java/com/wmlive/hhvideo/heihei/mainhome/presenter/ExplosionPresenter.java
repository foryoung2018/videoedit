package com.wmlive.hhvideo.heihei.mainhome.presenter;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.main.RecommendResponse;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.mainhome.view.ExplosionView;
import com.wmlive.networklib.observer.DCNetObserver;

/**
 * Created by vhawk on 2017/6/1.
 * Modify by lsq
 */

public class ExplosionPresenter extends BasePresenter<ExplosionView> {

    private int offset;

    public ExplosionPresenter(ExplosionView view) {
        super(view);
    }


    public void explosionVideo(final boolean isRefresh) {
        executeRequest(HttpConstant.TYPE_EXPLOSION_VIDEO, getHttpApi().explosionVideo(InitCatchData.opusListOpusByTime(), offset = isRefresh ? 0 : offset))
                .subscribe(new DCNetObserver<RecommendResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, RecommendResponse response) {
                        offset = response.getOffset();
                        if (viewCallback != null) {
                            viewCallback.handleExplosionSucceed(isRefresh,
                                    response.time_opus_list, response.isHas_more());
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(isRefresh ? HttpConstant.TYPE_EXPLOSION_VIDEO : (HttpConstant.TYPE_EXPLOSION_VIDEO + 1), message);
                    }
                });
    }

}
