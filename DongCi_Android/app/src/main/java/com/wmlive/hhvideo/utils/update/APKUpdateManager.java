package com.wmlive.hhvideo.utils.update;

import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import com.wmlive.hhvideo.heihei.mainhome.activity.MainActivity;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.utils.preferences.SPUtils;
import com.wmlive.hhvideo.utils.update.receivers.ApkInstallReceiver;

import cn.wmlive.hhvideo.R;

/**
 * --------------------------------------------------------------- :
 * Jianfei.G Create: 2016/10/12
 * --------------------------------------------------------------- Describe: 在线更新下载功能
 * --------------------------------------------------------------- Changes:
 * --------------------------------------------------------------- 2016/10/12
 * : Create by Jianfei.G
 * ---------------------------------------------------------------
 */

public class APKUpdateManager {
    public static final String TAG = APKUpdateManager.class.getSimpleName();
    public static final String KEY_DOWNLOAD_ID = "downloadId";

    public static void download(Context context, String url, String title, String fileName) {
        long downloadId = SPUtils.getLong(context, KEY_DOWNLOAD_ID, -1L);
        if (downloadId != -1L) {
            FileDownloadManager fdm = FileDownloadManager.getInstance(context);
            int status = fdm.getDownloadStatus(downloadId);
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                // 启动更新界面
                Uri uri = fdm.getDownloadUri(downloadId);
                if (uri != null) {
                    if (compare(getApkInfo(context, uri.getPath()), context)) {
                        MainActivity.updateApkStatus = 1;
                        ApkInstallReceiver.installApk(context, downloadId);
                        return;
                    } else {
                        MainActivity.updateApkStatus = 0;
                        fdm.getDm().remove(downloadId);
                        start(context, url, title, fileName);
                    }
                } else {
                    //uri 错误
                    start(context, url, title, fileName);
                }
            } else if (status == DownloadManager.STATUS_FAILED) {
                start(context, url, title, fileName);
            } else if (status == DownloadManager.STATUS_PAUSED) {
                fdm.getDm().remove(downloadId);
                start(context, url, title, fileName);
            } else if (status == DownloadManager.STATUS_PENDING) {
                ToastUtil.showToast(R.string.update_information_waiting);
            } else if (status == DownloadManager.STATUS_RUNNING) {
                ToastUtil.showToast(R.string.update_information_running);
            } else {
                //其他状态
                //开启下载
                start(context, url, title, fileName);
            }
        } else {
            start(context, url, title, fileName);
        }
    }

    /**
     * 下载的apk和当前版本比较
     *
     * @param apkInfo apk file's packageInfo
     * @param context Context
     * @return 如果当前版本小于apk的版本则返回true
     */
    public static boolean compare(PackageInfo apkInfo, Context context) {
        if (apkInfo == null) {
            return false;
        }
        String localPackage = context.getPackageName();
        if (apkInfo.packageName.equals(localPackage)) {
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(localPackage, 0);
                if (apkInfo.versionCode > packageInfo.versionCode) {
                    return true;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 获取apk程序信息[packageName,versionName...]
     *
     * @param context Context
     * @param path    apk path
     */
    public static PackageInfo getApkInfo(Context context, String path) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            return info;
        }
        return null;
    }

    private static void start(Context context, String url, String title, String fileName) {
        Toast.makeText(context, R.string.update_information_satrt, Toast.LENGTH_SHORT).show();
        long id = FileDownloadManager.getInstance(context).startDownload(url, title, "下载完成后点击打开", fileName);
        SPUtils.putLong(context, KEY_DOWNLOAD_ID, id);
    }


    /**
     * 获取当前下载情况
     *
     * @param context
     * @return
     */
    public static int getCurentDownLoadStatus(Context context) {
        long downloadId = SPUtils.getLong(context, KEY_DOWNLOAD_ID, -1L);
        if (downloadId != -1L) {
            FileDownloadManager fdm = FileDownloadManager.getInstance(context);
            int status = fdm.getDownloadStatus(downloadId);
            return status;
        } else {
            return -1;
        }

    }

}
