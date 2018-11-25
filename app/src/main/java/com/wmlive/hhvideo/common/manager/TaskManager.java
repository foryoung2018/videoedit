package com.wmlive.hhvideo.common.manager;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.manager.message.BaseTask;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.networklib.entity.UrlBean;
import com.wmlive.networklib.util.NetUtil;

import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by lsq on 2/2/2018.3:10 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class TaskManager {
    private ThreadPoolExecutor threadExecutor;
    private boolean started;

    private static final class Holder {
        private static final TaskManager INSTANCE = new TaskManager();
    }

    public static TaskManager get() {
        return Holder.INSTANCE;
    }

    /**
     * 初始化线程池
     *
     * @param coreSize 核心线程数
     */
    public void initExecutor(int coreSize) {
        if (!started) {
            threadExecutor = new ThreadPoolExecutor(coreSize < 0 ? 2 : coreSize,
                    coreSize + 3,
                    60, TimeUnit.SECONDS,
                    new PriorityBlockingQueue<>());
            started = true;
        }
    }

    public void getAllIp() {
        executeTask(new BaseTask() {
            @Override
            public void run() {
                GlobalParams.StaticVariable.sAliyunUploadIp = NetUtil.ping("amazsic-bucket-01.oss-cn-beijing.aliyuncs.com");
            }
        });
        //备用：http://pv.sohu.com/cityjson?ie=utf-8
        executeTask(new BaseTask() {
            @Override
            public void run() {
                String url = "http://ip.taobao.com/service/getIpInfo.php?ip=myip";
                NetUtil.getLocalPublicIp(url, new NetUtil.CallBackListener() {
                    @Override
                    public void onSucess(UrlBean.DataBean dataBean) {
                        if (dataBean!=null){
                            if (!TextUtils.isEmpty(dataBean.ip)) {
                                GlobalParams.StaticVariable.sLocalPublicIp = dataBean.ip;
                            }
                            if (!TextUtils.isEmpty(dataBean.region)) {
                                GlobalParams.StaticVariable.ipRegion = dataBean.region;
                            }
                            if (!TextUtils.isEmpty(dataBean.city)) {
                                GlobalParams.StaticVariable.ipCity = dataBean.city;
                            }
                            if (!TextUtils.isEmpty(dataBean.isp)) {
                                GlobalParams.StaticVariable.netName = dataBean.isp;
                            }
                        }
                    }

                    @Override
                    public void onFail(String errorMsg) {
                        KLog.i("=====getLocalPublicIp:" + errorMsg);
                    }
                });

            }
        });
        executeTask(new BaseTask() {
            @Override
            public void run() {
                String url = "https://ipip.yy.com/get_ip_info.php";
                NetUtil.getLocalPublicIp(url, new NetUtil.CallBackListener() {
                    @Override
                    public void onSucess(UrlBean.DataBean dataBean) {
                        if(dataBean!=null){
                            if (!TextUtils.isEmpty(dataBean.cip)) {
                                GlobalParams.StaticVariable.sLocalPublicIp = dataBean.cip;
                            }
                            if (!TextUtils.isEmpty(dataBean.province)) {
                                GlobalParams.StaticVariable.ipRegion = dataBean.province;
                            }
                            if (!TextUtils.isEmpty(dataBean.city)) {
                                GlobalParams.StaticVariable.ipCity = dataBean.city;
                            }
                            if (!TextUtils.isEmpty(dataBean.isp)) {
                                GlobalParams.StaticVariable.netName = dataBean.isp;
                            }
                        }
                    }

                    @Override
                    public void onFail(String errorMsg) {
                        KLog.i("=====getLocalPublicIp:" + errorMsg);
                    }
                });

            }
        });
    }

    /**
     * 添加一个任务
     *
     * @param runnable
     */
    public <T extends BaseTask> void executeTask(T runnable) {
        if (threadExecutor == null) {
            started = false;
            initExecutor(2);
        }
        threadExecutor.execute(runnable);
    }

    /**
     * 关闭线程池
     */
    public void shutdown() {
        if (threadExecutor != null) {
            if (!threadExecutor.isShutdown()) {
                threadExecutor.shutdownNow();
            }
            threadExecutor = null;
        }
        started = false;
    }

    /**
     * 获取当前进程的包名
     *
     * @param context
     * @return
     */
    public static String getCurrentProcessName(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.RunningAppProcessInfo> processInfoList = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo appProcess : processInfoList) {
                if (appProcess.pid == android.os.Process.myPid()) {
                    return appProcess.processName;
                }
            }
        }
        return null;
    }

    public static boolean isRunningOnBackground(Context context) {
        ActivityManager acm = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (acm != null) {
            List<ActivityManager.RunningAppProcessInfo> runApps = acm.getRunningAppProcesses();
            if (runApps != null && !runApps.isEmpty()) {
                for (ActivityManager.RunningAppProcessInfo app : runApps) {
                    if (app.processName.equals(context.getPackageName())) {
                        if (app.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
