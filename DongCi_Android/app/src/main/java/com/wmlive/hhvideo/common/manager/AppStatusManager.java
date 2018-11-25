package com.wmlive.hhvideo.common.manager;

/**
 * Created by XueFei on 2017/7/12.
 * <p>
 * 应用被强杀，通过status跳到splashActivity
 */

public class AppStatusManager {
    public static final int STATUS_FORCE_KILLED = -1;//默认值//应用被强杀
    public static final int STATUS_NORMAL = 0;//app正常启动
    public static final int ACTION_RESTART_APP = 1;//重启

    public static final String KEY_HOME_ACTION = "key_home_action";

    //APP状态 初始值为没启动 不在前台状态
    public int appStatus = STATUS_NORMAL;

    public static AppStatusManager appStatusManager;

    private AppStatusManager() {

    }


    public static AppStatusManager getInstance() {
        if (appStatusManager == null) {
            appStatusManager = new AppStatusManager();
        }
        return appStatusManager;
    }

    public int getAppStatus() {
        return appStatus;
    }

    public void setAppStatus(int appStatus) {
        this.appStatus = appStatus;
    }
}
