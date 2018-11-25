package com.wmlive.hhvideo.service.presenter;

import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.heihei.beans.opus.ShareEventEntity;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.networklib.entity.BaseResponse;
import com.wmlive.networklib.observer.DCNetObserver;

import java.util.HashMap;
import java.util.Map;

public class ShareEventPresenter extends BasePresenter<ShareEventPresenter.IShareEventView> {

    public ShareEventPresenter(IShareEventView view) {
        super(view);
    }

    public void pushShare(ShareEventEntity shareEventEntity) {
        Map<String, String> map = new HashMap<>(4);
        map.put("share_uuid", shareEventEntity.shareUuid);
        map.put("obj_id", shareEventEntity.objId);
        map.put("obj_type", shareEventEntity.objType);
        map.put("share_target", shareEventEntity.shareTarget);
        executeRequest(GlobalParams.EventType.TYPE_SHARE_EVENT, getHttpApi()
                .getShareLog(InitCatchData.getShareLog(), map))
                .subscribe(new DCNetObserver<BaseResponse>() {
                    @Override
                    public void onRequestDataReady(int requestCode, String message, BaseResponse response) {
                        if (viewCallback != null) {
                            viewCallback.onShareOk();
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        if (viewCallback != null) {
                            viewCallback.onShareFail(serverCode, message);
                        }
                    }
                });
    }

    public interface IShareEventView extends BaseView {

        void onShareOk();

        void onShareFail(int serverCode, String message);

    }
}
