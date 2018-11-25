package com.wmlive.hhvideo.heihei.record.model;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.common.manager.LogFileManager;
import com.wmlive.hhvideo.common.manager.MyAppActivityManager;
import com.wmlive.hhvideo.heihei.beans.opus.PublishResponseEntity;
import com.wmlive.hhvideo.heihei.beans.opus.ShareEventEntity;
import com.wmlive.hhvideo.heihei.db.ProductEntity;
import com.wmlive.hhvideo.heihei.mainhome.activity.MainActivity;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.presenter.AbsPublishView;
import com.wmlive.hhvideo.heihei.record.presenter.PublishLocalPresenter;
import com.wmlive.hhvideo.heihei.record.presenter.PublishPresenter;
import com.wmlive.hhvideo.heihei.record.service.PublishBGService;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtil;
import com.wmlive.hhvideo.utils.MyClickListener;
import com.wmlive.hhvideo.utils.PopupWindowUtils;
import com.wmlive.hhvideo.widget.dialog.CustomDialog;
import com.wmlive.networklib.util.EventHelper;

import cn.wmlive.hhvideo.BuildConfig;
import cn.wmlive.hhvideo.R;

public class PublishModel {

    private PopupWindow popupWindow;
    private CustomDialog customDialog;
    private PublishPresenter publishPresenter;
    private PublishLocalPresenter publishLocalPresenter;
    private int retryTime;
    private int uploadType;//上传类型 0: 多格子拍摄， 1: 本地直接上传 2: 速拍
    public static final int TYPE_RECORD = 0;
    public static final int TYPE_UPLOAD = 1;
    public static final int TYPE_CREATE4 = 2;

    public PublishModel(int isLocalUpload) {
        this.uploadType = isLocalUpload;
    }

    /**
     * 重新连上网络
     */
    public void onNetConnect() {
        if (disConnected) {
            retryPublish();
            EventHelper.post(GlobalParams.EventType.TYPE_PUBLISH_RETRY);
            disConnected = false;
        }

    }

    boolean disConnected = false;

    /**
     * 断网
     */
    public void onNetDisconnect() {
        disConnected = true;
    }

    public AbsPublishView publishView = new AbsPublishView() {
        @Override
        public void onPublishStart(int index) {
            //show publish note
            EventHelper.post(GlobalParams.EventType.TYPE_PUBLISH_START, index);
            //set publish state
//            RecordUtil.moveToPublishing(RecordManager.get().getProductEntity());
            GlobalParams.StaticVariable.ispublishing = true;
        }

        @Override
        public void onPublishing(int index, int progress) {
            //update pb
            EventHelper.post(GlobalParams.EventType.TYPE_PUBLISH_PROGRESS, progress);
        }

        @Override
        public void onPublishOk(PublishResponseEntity entity) {


            GlobalParams.StaticVariable.ispublishing = false;
            //2.uodate progressbar
            EventHelper.post(GlobalParams.EventType.TYPE_PUBLISH_FINISH);

            //delete draft
            if (!BuildConfig.DEBUG) {
                deleteProduct();//release delete
            }
            RecordManager.get().clearAll();
            //1. show share
            showShare(entity);
            //3.stop service
            stopPublishService();
            //1.发布成功删除(没有导出)
            // 2. 发布失败不删除(不管导出)
            // 3.发布成功，导出成功或者失败 都删除，
//
//

        }

        @Override
        public void onExportLocal(int code, PublishResponseEntity entity) {

        }

        @Override
        public void onPublishFail(int type, String message) {
            if (retryTime < 3) {
                retryTime++;
                EventHelper.post(GlobalParams.EventType.TYPE_PUBLISH_RETRY);
                retryPublish();
                Log.d("dddd", "onPublishFail:  retryTime==" + retryTime);
                return;
            }
            //1.send result
            EventHelper.post(GlobalParams.EventType.TYPE_PUBLISH_ERRER);
            GlobalParams.StaticVariable.ispublishing = false;
            //3. make product null
            RecordManager.get().clearAll();
            //4.show error
            showPublishError();
            //5.upload log
            LogFileManager.getInstance().saveLogInfo("publish product", "type:" + type + "message" + message);
            //2.stop service
            stopPublishService();
        }
    };

    /**
     * 显示分享页面
     *
     * @param entity
     */
    private void showShare(PublishResponseEntity entity) {
        if (entity != null && entity.share_info != null) {
            DcBaseActivity activity = (DcBaseActivity) MyAppActivityManager.getInstance().currentActivity();
            if (activity == null) {
                return;
            }

            RelativeLayout rootView = activity.getRootView();
            popupWindow = PopupWindowUtils.showUploadResultPanel(activity, rootView, new MyClickListener() {
                @Override
                protected void onMyClick(View v) {
                    entity.share_info.objId = entity.opus_id;
                    entity.share_info.shareType = ShareEventEntity.TYPE_OPUS;
                    switch (v.getId()) {
                        case R.id.tvOk:
                            //隐藏弹窗
                            dismissPupop();
                            break;
                        case R.id.llWeChat:
                            entity.share_info.shareTarget = ShareEventEntity.TARGET_WECHAT;
                            if (entity.share_info.wxprogram_share_info != null) {
                                activity.wxMinAppShare(0, entity.share_info, null);
                            } else {
                                activity.wechatShare(0, entity.share_info);
                            }
                            break;
                        case R.id.llCircle:
                            entity.share_info.shareTarget = ShareEventEntity.TARGET_FRIEND;
                            activity.wechatShare(1, entity.share_info);
                            break;
                        case R.id.llWeibo:
                            entity.share_info.shareTarget = ShareEventEntity.TARGET_WEIBO;
                            activity.weiboShare(entity.share_info);
                            break;
                        case R.id.llQQ:
                            entity.share_info.shareTarget = ShareEventEntity.TARGET_QQ;
                            activity.qqShare(entity.share_info);
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }

    public void showPublishError() {
        Activity activity = MyAppActivityManager.getInstance().currentActivity();
        customDialog = new CustomDialog(activity, R.style.BaseDialogTheme);
        customDialog.setContent("当前网络不可用\n作品已保存到草稿箱");
        customDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                customDialog.dismiss();
            }
        });
        customDialog.show();
    }

    private void dismissPupop() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    private void deleteProduct() {
        final ProductEntity productEntity = RecordManager.get().getProductEntity();
        RecordManager.get().setPublishingProductId(0);
        RecordUtil.deleteProduct(productEntity, true);
    }

    public void retryPublish(){
        switch(uploadType){
            case TYPE_RECORD://录制
                publishPresenter.retryPublish();
                break;
            case TYPE_UPLOAD://直接上传
                publishLocalPresenter.retryPublish();
                break;
            case TYPE_CREATE4://4.0
                break;
        }

    }


    /**
     * 是否保存到本地
     *
     * @param ifsave
     */
    public void publish(boolean ifsave) {
        switch(uploadType){
            case TYPE_RECORD://录制
                publishPresenter = new PublishPresenter(publishView);
                publishPresenter.preparePublish(RecordManager.get().getProductEntity(), ifsave);
                break;
            case TYPE_UPLOAD://直接上传
                publishLocalPresenter = new PublishLocalPresenter(publishView);
                publishLocalPresenter.preparePublish(RecordManager.get().getProductEntity());
                break;
            case TYPE_CREATE4://4.0
                break;
        }
//        if(isLocalUpload){//本地直接上传
//            publishLocalPresenter = new PublishLocalPresenter(publishView);
//            publishLocalPresenter.preparePublish(RecordManager.get().getProductEntity());
//        }else {//

//        }

    }

    private void stopPublishService() {
        Activity activity = MyAppActivityManager.getInstance().currentActivity();
        if(activity==null)
            return;
        Intent intent = new Intent(activity, PublishBGService.class);
        activity.stopService(intent);
    }

}
