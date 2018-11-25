package com.wmlive.hhvideo.service.presenter;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.log.MaterialDownLoad;
import com.wmlive.hhvideo.heihei.beans.log.VideoDownLoad;
import com.wmlive.hhvideo.heihei.beans.log.VideoUpload;
import com.wmlive.hhvideo.heihei.beans.main.UserBehavior;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.service.view.UserBehaviorView;
import com.wmlive.networklib.entity.BaseResponse;
import com.wmlive.networklib.observer.DCNetObserver;

import java.util.HashMap;

/**
 * 上传用户行为presenter
 * Created by vhawk on 2017/6/20.
 */

public class UserBehaviorPresenter extends BasePresenter<UserBehaviorView> {
    public UserBehaviorPresenter(UserBehaviorView view) {
        super(view);
    }

    /**
     * 上送用户观看视频，下载，分享灯行为
     *
     * @param userBehavior
     */
    public void sendUserBehavior(UserBehavior userBehavior) {
        executeRequest(HttpConstant.TYPE_USER_BEHAVIOR,
                getHttpApi().sendUserBehavior(InitCatchData.opusLogs(),
                        userBehavior.getVideoId(),
                        userBehavior.getWtachlLength(),
                        userBehavior.getShareCount(),
                        userBehavior.getDownloadCount())
        ).subscribe(new DCNetObserver<BaseResponse>() {
            @Override
            public void onRequestDataReady(int requestCode, String message, BaseResponse response) {
                if (viewCallback != null) {
                    viewCallback.handleUserBehaviorSucceed(message);
                }
            }

            @Override
            public void onRequestDataError(int requestCode, int serverCode, String message) {
                if (viewCallback != null) {
                    viewCallback.handleUserBehaviorFailure(message);
                }
            }
        });

    }

    public void sendPlayDownloadLog(UserBehavior userBehavior) {
//        executeRequest(HttpConstant.TYPE_USER_BEHAVIOR,getHttpApi().sendUserBehavior())
    }

    /**
     * 素材下载日志发送
     *
     * @param materialDownLoad
     */
    public void sendMaterialDownloadLog(MaterialDownLoad materialDownLoad, HashMap<String, String> map) {
        executeRequest(HttpConstant.TYPE_DOWNLOAD_MATERIAL, getHttpApi().sendMaterialDownLoad(InitCatchData.getInitCatchData().log.getDownloadMaterialsLog(),
                materialDownLoad.getUrl(),
                materialDownLoad.getMaterial_id(),
                materialDownLoad.getFile_len(),
                materialDownLoad.getDownload_len(),
                materialDownLoad.getDownload_duration(),
                materialDownLoad.getDownload_speed(),
                materialDownLoad.getRes(), map)).subscribe(new DCNetObserver<BaseResponse>() {
            @Override
            public void onRequestDataReady(int requestCode, String message, BaseResponse response) {
                if (viewCallback != null) {
                    viewCallback.handleUserBehaviorSucceed(message);
                }
            }

            @Override
            public void onRequestDataError(int requestCode, int serverCode, String message) {
                if (viewCallback != null) {
                    viewCallback.handleUserBehaviorFailure(message);
                }
            }
        });
    }

    /**
     * 发送下载视频日志
     *
     * @param materialDownLoad
     */
    public void sendVideoDownloadLog(VideoDownLoad materialDownLoad, HashMap<String, String> map) {
        executeRequest(HttpConstant.TYPE_DOWNLOAD_VIDEO, getHttpApi().sendVideoDownLoad(InitCatchData.getInitCatchData().log.getDownloadVideoLog(),
                materialDownLoad.getUrl(),
                materialDownLoad.getOpus_id(),
                materialDownLoad.getFile_len(),
                materialDownLoad.getDownload_len(),
                materialDownLoad.getDownload_duration(),
                materialDownLoad.getDownload_speed(),
                materialDownLoad.getBuffer_duration(),
                materialDownLoad.getBuffer_count(),
                map)).subscribe(new DCNetObserver<BaseResponse>() {
            @Override
            public void onRequestDataReady(int requestCode, String message, BaseResponse response) {
                if (viewCallback != null) {
                    viewCallback.handleUserBehaviorSucceed(message);
                }
            }

            @Override
            public void onRequestDataError(int requestCode, int serverCode, String message) {
                if (viewCallback != null) {
                    viewCallback.handleUserBehaviorFailure(message);
                }
            }
        });
    }

    /**
     * 视频上传
     *
     * @param videoUpload
     */
    public void sendVideoUploadLog(VideoUpload videoUpload, HashMap<String, String> map) {
        executeRequest(HttpConstant.TYPE_UPLOAD_VIDEO, getHttpApi().sendVideoUpload(InitCatchData.getInitCatchData().log.getUploadVideoLog(),
                videoUpload.getFile_len(),
                videoUpload.getUpoload_duration(),
                videoUpload.getUpload_speed(),
                videoUpload.getRes(),
                map)
        ).subscribe(new DCNetObserver<BaseResponse>() {
            @Override
            public void onRequestDataReady(int requestCode, String message, BaseResponse response) {
                if (viewCallback != null) {
                    viewCallback.handleUserBehaviorSucceed(message);
                }
            }

            @Override
            public void onRequestDataError(int requestCode, int serverCode, String message) {
                if (viewCallback != null) {
                    viewCallback.handleUserBehaviorFailure(message);
                }
            }
        });
    }
}
