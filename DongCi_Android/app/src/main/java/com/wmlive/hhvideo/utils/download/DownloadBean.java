package com.wmlive.hhvideo.utils.download;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by wenlu on 2017/9/20.
 */

public class DownloadBean implements Parcelable {
    public int downloadId;
    public String downloadUrl;
    public String savePath;
    public String saveName;
    public String saveSuffix;
    public String wholePathName;
    public int index;
    public int realDownloadId;

    /**
     * @param downloadId    下载ID
     * @param downloadUrl   下载Url
     * @param wholePathName 存储路径
     */
    public DownloadBean(int downloadId, String downloadUrl, String wholePathName) {
        this.downloadId = downloadId;
        this.downloadUrl = downloadUrl;
        this.wholePathName = wholePathName;
    }

    public DownloadBean(int downloadId, String downloadUrl, String savePath, String saveName, String saveSuffix) {
        this(downloadId, downloadUrl, savePath, saveName, saveSuffix, 0);
    }

    /**
     * @param downloadId  下载ID
     * @param downloadUrl 下载Url
     * @param savePath    存储文件夹
     * @param saveName    文件名
     * @param saveSuffix  文件后缀
     */
    public DownloadBean(int downloadId, String downloadUrl, String savePath, String saveName, String saveSuffix, int index) {
        this.downloadId = downloadId;
        this.downloadUrl = downloadUrl;
        this.savePath = savePath;
        this.saveName = saveName;
        this.saveSuffix = saveSuffix;
        this.index = index;
        if (DownloadUtil.isUrl(this.downloadUrl)) {
            if (TextUtils.isEmpty(this.saveName)) {
                this.saveName = this.downloadUrl.substring(this.downloadUrl.lastIndexOf("/") + 1, this.downloadUrl.length());
                if (TextUtils.isEmpty(this.saveSuffix)) {
                    if (this.saveName.contains(".")) {//如果含有扩展名
                        this.saveSuffix = this.saveName.substring(this.saveName.lastIndexOf(".") + 1, this.saveName.length());
                        this.saveName = this.saveName.substring(0, this.saveName.lastIndexOf("."));
                    }
                }
                if (TextUtils.isEmpty(this.saveName)) {
                    this.saveName = String.valueOf(System.currentTimeMillis());
                }
            }
        }
        this.wholePathName = this.savePath + File.separator + this.saveName + "." + this.saveSuffix;
    }

    protected DownloadBean(Parcel in) {
        downloadId = in.readInt();
        downloadUrl = in.readString();
        savePath = in.readString();
        saveName = in.readString();
        saveSuffix = in.readString();
        wholePathName = in.readString();
        index = in.readInt();
        realDownloadId = in.readInt();
    }

    public static final Creator<DownloadBean> CREATOR = new Creator<DownloadBean>() {
        @Override
        public DownloadBean createFromParcel(Parcel in) {
            return new DownloadBean(in);
        }

        @Override
        public DownloadBean[] newArray(int size) {
            return new DownloadBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(downloadId);
        dest.writeString(downloadUrl);
        dest.writeString(savePath);
        dest.writeString(saveName);
        dest.writeString(saveSuffix);
        dest.writeString(wholePathName);
        dest.writeInt(index);
        dest.writeInt(realDownloadId);
    }

    @Override
    public String toString() {
        return "DownloadBean{" +
                "downloadId=" + downloadId +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", savePath='" + savePath + '\'' +
                ", saveName='" + saveName + '\'' +
                ", saveSuffix='" + saveSuffix + '\'' +
                ", wholePathName='" + wholePathName + '\'' +
                ", index=" + index +
                ", realDownloadId=" + realDownloadId +
                '}';
    }
}
