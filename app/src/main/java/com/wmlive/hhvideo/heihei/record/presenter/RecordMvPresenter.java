package com.wmlive.hhvideo.heihei.record.presenter;

import android.util.Log;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.opus.OpusMaterialEntity;
import com.wmlive.hhvideo.heihei.beans.quickcreative.CreativeTemplateBean;
import com.wmlive.hhvideo.heihei.beans.quickcreative.CreativeTemplateListBean;
import com.wmlive.hhvideo.heihei.beans.recordmv.MvMaterialEntity;
import com.wmlive.hhvideo.heihei.beans.recordmv.SingleTemplateBean;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.networklib.observer.DCNetObserver;

/**
 * Created by jht on 2018/10/9.
 */

public class RecordMvPresenter extends BasePresenter<RecordMvPresenter.IRecordView> {


    public RecordMvPresenter(IRecordView view) {
        super(view);
    }

    public interface IRecordView extends BaseView {
        void onGetMaterial(MvMaterialEntity response);

        void onGetTemplate(SingleTemplateBean response);

        void onGetTemlateSocal(CreativeTemplateBean response);

        void onRequestError(int code, String msg);
    }

    /**
     * 获取作品使用的模板
     *
     * @param opusId
     */
    public void getOpusTemplate(long opusId, String onlyTemplate) {
        executeRequest(HttpConstant.TYPE_GET_TEMPLATE, getHttpApi().getOpusTemplate(InitCatchData.getOpusMaterial(), opusId, onlyTemplate))
                .subscribe(new DCNetObserver<SingleTemplateBean>() {

                    @Override
                    public void onRequestDataReady(int requestCode, String message, SingleTemplateBean response) {
                        if (viewCallback != null) {
                            viewCallback.onGetTemplate(response);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(HttpConstant.TYPE_GET_TEMPLATE, message);
                    }
                });
    }


    /**
     * 获取MV作品素材列表
     *
     * @param opusId
     */
    public void getOpusMaterial(long opusId) {
        executeRequest(HttpConstant.TYPE_GET_MATERIAL, getHttpApi().getMvOpusMaterial(InitCatchData.getOpusMaterial(), opusId))
                .subscribe(new DCNetObserver<MvMaterialEntity>() {

                    @Override
                    public void onRequestDataReady(int requestCode, String message, MvMaterialEntity response) {
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


    public void getTemplate(String template_name, String bg_name) {
        executeRequest(HttpConstant.TYPE_TEMPLATE_SOCAL, getHttpApi().getTemplateSocal(InitCatchData.getCreativeSocial(), template_name, bg_name))
                .subscribe(new DCNetObserver<CreativeTemplateBean>() {

                    @Override
                    public void onRequestDataReady(int requestCode, String message, CreativeTemplateBean response) {
                        KLog.d("", "onRequestDataReady: response==="+response);
                        if (viewCallback != null) {
                            viewCallback.onGetTemlateSocal(response);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(HttpConstant.TYPE_GET_MATERIAL, message);
                    }
                });
    }


}
