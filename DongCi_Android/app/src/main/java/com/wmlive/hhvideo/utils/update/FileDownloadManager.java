package com.wmlive.hhvideo.utils.update;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.utils.ToastUtil;

import cn.wmlive.hhvideo.R;

/**
 * --------------------------------------------------------------- :
 * Jianfei.G Create: 2016/10/12 19:13
 * --------------------------------------------------------------- Describe: 在线更新下载功能 使用Android下载管理器
 * --------------------------------------------------------------- Changes:
 * --------------------------------------------------------------- 2017/03/03 16
 * : Create by Jianfei.G
 * ---------------------------------------------------------------
 */
public class FileDownloadManager {
    private DownloadManager dm;
    private Context context;
    private static FileDownloadManager instance;

    private FileDownloadManager(Context context) {
        dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        this.context = context.getApplicationContext();
    }

    public static FileDownloadManager getInstance(Context context) {
        if (instance == null) {
            instance = new FileDownloadManager(context);
        }
        return instance;
    }

    /**
     * @param uri
     * @param title
     * @param description
     * @return download id
     */
    public long startDownload(String uri, String title, String description, String fileName) {
        if (TextUtils.isEmpty(uri)) {
            ToastUtil.showToast(DCApplication.getDCApp().getString(R.string.hintErrorDataDelayTry));
            return -1L;
        }
        Uri downloadUri = Uri.parse(uri);
        String scheme = downloadUri.getScheme();
        if (scheme == null || (!scheme.equals("http") && !scheme.equals("https"))) {
            ToastUtil.showToast(DCApplication.getDCApp().getString(R.string.hintErrorDataDelayTry));
            return -1L;
        }
        DownloadManager.Request req = new DownloadManager.Request(downloadUri);


        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        req.setAllowedOverRoaming(true);

        // req.setAllowedOverRoaming(false);

        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        setSaveFuction(FILE_SAVE_DOWNLOAD, req, fileName);
        req.setTitle(title);
        req.setDescription(description);
        // req.setMimeType("application/vnd.android.package-archive");
        return dm.enqueue(req);

        // dm.openDownloadedFile()
    }

    // file:///storage/emulated/0/Android/data/your-package/files/Download/update.apk
    private static final int FILE_SAVE_PACKAGE = 1;
    // file:///storage/emulated/0/Download/update.apk
    private static final int FILE_SAVE_DOWNLOAD = 2;
    // 自定义文件路径 req.setDestinationUri()
    private static final int FILE_SAVE_FILE = 3;

    private void setSaveFuction(int type, DownloadManager.Request req, String fileName) {
        switch (type) {
            case 1:
                req.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName + ".apk");
                break;
            case 2:
                req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName + ".apk");
                break;
            case 3:
                req.setDestinationUri(null);
                break;
            default:
                break;
        }
    }

    /**
     * 获取文件保存的路径
     *
     * @param downloadId an ID for the download, unique across the system. This ID is
     *                   used to make future calls related to this download.
     * @return file path
     * @see FileDownloadManager#getDownloadUri(long)
     */
    public String getDownloadPath(long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor c = dm.query(query);
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    return c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI));
                }
            } finally {
                c.close();
            }
        }
        return null;
    }

    /**
     * 获取保存文件的地址
     *
     * @param downloadId an ID for the download, unique across the system. This ID is
     *                   used to make future calls related to this download.
     * @see FileDownloadManager#getDownloadPath(long)
     */
    public Uri getDownloadUri(long downloadId) {
        return dm.getUriForDownloadedFile(downloadId);
    }

    public DownloadManager getDm() {
        return dm;
    }

    /**
     * 获取下载状态
     *
     * @param downloadId an ID for the download, unique across the system. This ID is
     *                   used to make future calls related to this download.
     * @return int
     * @see DownloadManager#STATUS_PENDING
     * @see DownloadManager#STATUS_PAUSED
     * @see DownloadManager#STATUS_RUNNING
     * @see DownloadManager#STATUS_SUCCESSFUL
     * @see DownloadManager#STATUS_FAILED
     */
    public int getDownloadStatus(long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor c = dm.query(query);
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    return c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));

                }
            } finally {
                c.close();
            }
        }
        return -1;
    }
}
