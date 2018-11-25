package com.wmlive.hhvideo.heihei.quickcreative;

import android.util.Log;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.opus.OpusMaterialEntity;
import com.wmlive.hhvideo.heihei.beans.quickcreative.CreativeTemplateListBean;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.networklib.observer.DCNetObserver;

public class ChooseStyle4QuickPresenter extends BasePresenter<ChooseStyle4QuickPresenter.IchooseStyleView> {

    public ChooseStyle4QuickPresenter(IchooseStyleView view) {
        super(view);
    }

    public interface IchooseStyleView extends BaseView {
        void getMusicList(CreativeTemplateListBean musiclist);
    }

    public void getCreativeList() {
        executeRequest(HttpConstant.TYPE_CREATIVELIST, getHttpApi().getCreativeList(InitCatchData.getCreativeList(), 0)).subscribe(new DCNetObserver<CreativeTemplateListBean>() {
            @Override
            public void onRequestDataReady(int requestCode, String message, CreativeTemplateListBean response) {
                KLog.d("onRequestDataReady: response==" + InitCatchData.getCreativeList() + response);
                if (viewCallback != null) {
                    viewCallback.getMusicList(response);
                }

            }

            @Override
            public void onRequestDataError(int requestCode, int serverCode, String message) {
                viewCallback.onRequestDataError(requestCode, message);
            }
        });
    }

}
