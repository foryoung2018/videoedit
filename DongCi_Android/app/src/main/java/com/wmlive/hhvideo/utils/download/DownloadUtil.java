package com.wmlive.hhvideo.utils.download;

import android.text.TextUtils;
import android.util.Patterns;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.net.HttpURLConnection.HTTP_MOVED_PERM;
import static java.net.HttpURLConnection.HTTP_MOVED_TEMP;
import static java.net.HttpURLConnection.HTTP_SEE_OTHER;

/**
 * Created by lsq on 6/5/2017.
 * 下载工具类
 */

public class DownloadUtil {

    /**
     * 是否是url
     *
     * @param s
     * @return
     */
    public static boolean isUrl(String s) {
        if (!TextUtils.isEmpty(s)) {
            return Patterns.WEB_URL.matcher(s).matches();
        }
        return false;
//        String p = "(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";
//        return s.matches(p);
    }

    /**
     * 获取网络文件的长度
     *
     * @param downloadUrl
     * @return
     */
    public static int getDownloadFileLength(String downloadUrl) {
        if (isUrl(downloadUrl)) {
            HttpURLConnection connection = null;
            URL url;
            try {
                url = new URL(downloadUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10 * 1000);
                connection.setReadTimeout(10 * 1000);
                connection.setRequestMethod("HEAD");
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    return connection.getContentLength();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return -1;
        } else {
            return -1;
        }
    }

    /**
     * 远程文件修改日期
     *
     * @param downloadUrl
     * @return
     */
    public static long fileLatestModify(String downloadUrl) {
        if (isUrl(downloadUrl)) {
            HttpURLConnection connection = null;
            URL url;
            try {
                url = new URL(downloadUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10 * 1000);
                connection.setReadTimeout(10 * 1000);
                connection.setRequestMethod("HEAD");
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {//200说明文件已修改，304说明文件未修改
                    return connection.getLastModified();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return -1;
        } else {
            return -1;
        }
    }

    /**
     * 获取远程文件的长度和修改时间
     *
     * @param downloadUrl
     * @return
     */
    public static FileInfo getRemoteFileInfo(String downloadUrl, long limitRange) {
        FileInfo fileInfo = new FileInfo();
        if (isUrl(downloadUrl)) {
            HttpURLConnection connection = null;
            boolean redirected = false;
            int redirectCount = 0;
            try {
                do {
                    connection = (HttpURLConnection) new URL(downloadUrl).openConnection();
                    connection.setConnectTimeout(10 * 1000);
                    connection.setReadTimeout(10 * 1000);
                    connection.setRequestMethod("GET");
                    if (limitRange > 0) {
                        connection.setRequestProperty("Range", "bytes=0-" + limitRange);
                    } else {
                        connection.setRequestProperty("Range", "bytes=0-");
                    }
                    connection.setRequestProperty("Content-Type", "");
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HTTP_MOVED_PERM
                            || responseCode == HTTP_MOVED_TEMP
                            || responseCode == HTTP_SEE_OTHER) {  //重定向
                        redirected = true;
                    } else if (responseCode == HttpURLConnection.HTTP_OK
                            || responseCode == HttpURLConnection.HTTP_PARTIAL) {
                        redirected = false;
                        fileInfo.setFileSize(connection.getContentLength());
                        fileInfo.setModifyTime(connection.getLastModified());
                        fileInfo.setContentType(connection.getContentType());
                        fileInfo.setDownloadUrl(downloadUrl);
                        fileInfo.setSupportRange(!TextUtils.isEmpty(connection.getHeaderField("Content-Range")));
                    }
                    if (redirected) {//如果是重定向
                        downloadUrl = connection.getHeaderField("Location");
                        redirectCount++;
                        connection.disconnect();
                        if (redirectCount > 5) {
                            break;
                        }
                    }
                } while (redirected);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return fileInfo;
        } else {
            return fileInfo;
        }

    }

    /*
     * Java文件操作 获取文件扩展名
     *
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /*
     * Java文件操作 获取不带扩展名的文件名
     */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }
}
