package com.wmlive.hhvideo.service;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.heihei.beans.log.MaterialDownLoad;
import com.wmlive.hhvideo.heihei.beans.log.VideoDownLoad;
import com.wmlive.hhvideo.heihei.beans.log.VideoUpload;
import com.wmlive.hhvideo.heihei.beans.main.UserBehavior;
import com.wmlive.hhvideo.heihei.beans.opus.ShareEventEntity;
import com.wmlive.hhvideo.service.presenter.ShareEventPresenter;
import com.wmlive.hhvideo.service.presenter.UserBehaviorPresenter;
import com.wmlive.hhvideo.service.view.UserBehaviorView;
import com.wmlive.hhvideo.utils.HeaderUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.networklib.entity.EventEntity;
import com.wmlive.networklib.util.EventHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

/**
 * 后台上传用户行为
 * Created by vhawk on 2017/6/20.
 */

public class DCService extends DcBaseService implements UserBehaviorView, ShareEventPresenter.IShareEventView {

    private static final String TAG = "DCService";
    private UserBehaviorPresenter userBehaviorPresenter;
    private ShareEventPresenter shareEventPresenter;

    private long lastVideoID;
    private long lastVideoWatchLenth;

    @Override
    public void onCreate() {
        super.onCreate();
        KLog.v(TAG, "onCreate");
        EventHelper.register(this);
        userBehaviorPresenter = new UserBehaviorPresenter(this);
        shareEventPresenter = new ShareEventPresenter(this);
    }

    @Override
    public void onDestroy() {
        KLog.v(TAG, "onDestroy");
        EventHelper.unregister(this);
        if (userBehaviorPresenter != null) {
            userBehaviorPresenter.destroy();
            userBehaviorPresenter = null;
        }
        if (shareEventPresenter != null) {
            shareEventPresenter.destroy();
            shareEventPresenter = null;
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCustomCommand(Intent intent, int flags, int startId) {

    }

    @Override
    public void onPingCommand(Intent intent, int flags, int startId) {

    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onUser(EventEntity eventEntity) {
        if (eventEntity.code == GlobalParams.EventType.TYPE_USER_BEHAVIOR) {
            KLog.i("观看行为数据", "======收到观看行为数据000000" + "eventEntity.data=" + eventEntity.data);
            if (eventEntity.data != null && eventEntity.data instanceof UserBehavior) {
                UserBehavior behavior = (UserBehavior) eventEntity.data;
                if (behavior.getVideoId() == lastVideoID) {
                    if (behavior.getWtachlLength() > lastVideoWatchLenth) {
                        userBehaviorPresenter.sendUserBehavior((UserBehavior) eventEntity.data);
                        lastVideoID = behavior.getVideoId();
                        lastVideoWatchLenth = behavior.getWtachlLength();
                        KLog.i("观看行为数据", "======收到观看行为数据111111" + "eventEntity.data=" + eventEntity.data);
                    }
                } else {
                    userBehaviorPresenter.sendUserBehavior((UserBehavior) eventEntity.data);
                    lastVideoID = behavior.getVideoId();
                    lastVideoWatchLenth = behavior.getWtachlLength();
                    KLog.i("观看行为数据", "======收到观看行为数据222222" + "eventEntity.data=" + eventEntity.data);
                }
            }

        } else if (eventEntity.code == GlobalParams.EventType.TYPE_PLAY_DOWNLOAD) {//播放缓存
            if(eventEntity.data != null && eventEntity.data instanceof VideoDownLoad){
                VideoDownLoad videoDownLoad = (VideoDownLoad) eventEntity.data;
                KLog.d(TAG, "onUser: TYPE_PLAY_DOWNLOAD  videoDownLoad=="+videoDownLoad);
                userBehaviorPresenter.sendVideoDownloadLog(videoDownLoad,getGlobalParams());
            }
        } else if (eventEntity.code == GlobalParams.EventType.TYPE_CREATE_DOWNLOAD) {//素材下载
            if (eventEntity.data != null && eventEntity.data instanceof MaterialDownLoad) {
                MaterialDownLoad materialDownLoad = (MaterialDownLoad) eventEntity.data;
                userBehaviorPresenter.sendMaterialDownloadLog(materialDownLoad,getGlobalParams());
            }
        } else if (eventEntity.code == GlobalParams.EventType.TYPE_UPLOAD) {//视频上传
            if (eventEntity.data != null && eventEntity.data instanceof VideoUpload) {
                VideoUpload uploadVideo = (VideoUpload) eventEntity.data;
                userBehaviorPresenter.sendVideoUploadLog(uploadVideo, getGlobalParams());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onShareEvent(EventEntity eventEntity) {
        if (eventEntity.code == GlobalParams.EventType.TYPE_SHARE_EVENT) {
            KLog.i("======收到分享行为数据:" + eventEntity.data);
            if (eventEntity.data != null && eventEntity.data instanceof ShareEventEntity) {
                ShareEventEntity shareEventEntity = (ShareEventEntity) eventEntity.data;
                if (shareEventEntity.needUpload && shareEventEntity.isSuccess) {
                    shareEventPresenter.pushShare((ShareEventEntity) eventEntity.data);
                }
            }
        }
    }

    /**
     * 构造全局参数
     *
     * @return
     */
    private HashMap<String, String> getGlobalParams() {
        HashMap<String, String> map = new HashMap<>();
        map.put("device_id", HeaderUtils.getDeviceIdMsg());
        map.put("os_platform", HeaderUtils.getOsPlatform());
        map.put("app_version", HeaderUtils.getAppVersion());
        map.put("device_ac", HeaderUtils.getDeviceAc());
        map.put("local_ip", GlobalParams.StaticVariable.sLocalPublicIp);
        map.put("device_model", HeaderUtils.getDeviceModel());
        map.put("server_ip", GlobalParams.StaticVariable.sAliyunUploadIp);
        map.put("net_name",GlobalParams.StaticVariable.netName);
        map.put("ip_region",GlobalParams.StaticVariable.ipRegion);
        map.put("ip_city",GlobalParams.StaticVariable.ipCity);
        return map;
    }

    @Override
    public void onRequestDataError(int requestCode, String message) {
        KLog.i("========requestCode:" + requestCode + " ,message:" + message);
    }

    @Override
    public void handleUserBehaviorSucceed(String message) {
        KLog.v(TAG, message);
    }

    @Override
    public void handleUserBehaviorFailure(String message) {
        KLog.v(TAG, message);
    }

    @Override
    public void onShareOk() {
        KLog.i("=====onShareOk");
    }

    @Override
    public void onShareFail(int serverCode, String message) {
        KLog.i("=====onShareFail:" + serverCode + " ,message:" + message);
    }
}
