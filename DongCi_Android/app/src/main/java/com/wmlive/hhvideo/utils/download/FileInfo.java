package com.wmlive.hhvideo.utils.download;

import android.text.TextUtils;

/**
 * Created by lsq on 6/5/2017.
 * 下载文件信息
 */

public class FileInfo {
    private long fileSize;
    private long modifyTime;
    private boolean supportRange;
    private String downloadUrl;
    private String savePath;
    private String contentType;

    public FileInfo() {
    }

    public FileInfo(long fileSize, long modifyTime, boolean supportRange) {
        this.fileSize = fileSize;
        this.modifyTime = modifyTime;
        this.supportRange = supportRange;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public boolean isSupportRange() {
        return supportRange;
    }

    public void setSupportRange(boolean supportRange) {
        this.supportRange = supportRange;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getContentType() {
        return contentType;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    /**
     * 获取文件的扩展名
     *
     * @return
     */
    public String getFileSuffix() {
        String suffix = null;
        if (!TextUtils.isEmpty(contentType)) {
            if (contentType.contains("/")) {
                suffix = contentType.substring(contentType.indexOf("/") + 1, contentType.length());
            }
        }
        return suffix;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
