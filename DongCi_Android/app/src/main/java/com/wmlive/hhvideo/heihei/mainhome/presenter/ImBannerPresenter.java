package com.wmlive.hhvideo.heihei.mainhome.presenter;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.discovery.BannerListBean;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.networklib.observer.DCNetObserver;

/**
 * Created by Administrator on 3/13/2018.
 */

public class ImBannerPresenter extends BasePresenter<ImBannerPresenter.IImBanner> {

    public ImBannerPresenter(IImBanner view) {
        super(view);
    }

    public void getImBanner() {
        executeRequest(HttpConstant.TYPE_IM_BANNER, getHttpApi().getImBanner(InitCatchData.getImBanner()))
                .subscribe(new DCNetObserver<BannerListBean>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, BannerListBean response) {
                        if (viewCallback != null) {
                            viewCallback.onImBannerOk(response);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(requestCode, message);
                    }
                });
    }

    public interface IImBanner extends BaseView {
        void onImBannerOk(BannerListBean bannerList);

    }
}
