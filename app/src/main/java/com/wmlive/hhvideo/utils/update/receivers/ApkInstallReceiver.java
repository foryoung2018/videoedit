package com.wmlive.hhvideo.utils.update.receivers;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.wmlive.hhvideo.heihei.mainhome.activity.MainActivity;
import com.wmlive.hhvideo.utils.preferences.SPUtils;

/**
 * --------------------------------------------------------------- :
 * Jianfei.G Create: 2016/10/12 18.32
 * --------------------------------------------------------------- Describe: 在线更新下载功能
 * --------------------------------------------------------------- Changes:
 * --------------------------------------------------------------- 2016/10/12 18
 * : Create by Jianfei.G
 * ---------------------------------------------------------------
 */

public class ApkInstallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            long downloadApkId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            long id = SPUtils.getLong(context, "downloadId", -1L);
            if (downloadApkId == id) {
                MainActivity.updateApkStatus = 1;
                installApk(context, downloadApkId);
            }
        }
    }

    public static void installApk(Context context, long downloadApkId) {
        DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        try {
            Cursor cursor = dManager.query(new DownloadManager.Query().setFilterById(downloadApkId));
            if (cursor == null) {
                return;
            }
            cursor.moveToFirst();
            String path = "file://" + cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
            Uri downloadFileUri = Uri.parse(path);

            if (downloadFileUri != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                context.startActivity(intent);
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Intent install = new Intent(Intent.ACTION_VIEW);
                Uri downloadFileUri = dManager.getUriForDownloadedFile(downloadApkId);
                if (downloadFileUri != null) {
                    install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                    install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(install);
                } else {
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
}
