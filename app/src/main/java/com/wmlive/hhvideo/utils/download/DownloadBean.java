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
    public int type;//文件类型

    public final static int DOWNLOAD_ID_TEMPLATE = 101;
    public final static int DOWNLOAD_ID_BG = 102;


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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.downloadId);
        dest.writeString(this.downloadUrl);
        dest.writeString(this.savePath);
        dest.writeString(this.saveName);
        dest.writeString(this.saveSuffix);
        dest.writeString(this.wholePathName);
        dest.writeInt(this.index);
        dest.writeInt(this.realDownloadId);
        dest.writeInt(this.type);
    }

    protected DownloadBean(Parcel in) {
        this.downloadId = in.readInt();
        this.downloadUrl = in.readString();
        this.savePath = in.readString();
        this.saveName = in.readString();
        this.saveSuffix = in.readString();
        this.wholePathName = in.readString();
        this.index = in.readInt();
        this.realDownloadId = in.readInt();
        this.type = in.readInt();
    }

    public static final Creator<DownloadBean> CREATOR = new Creator<DownloadBean>() {
        @Override
        public DownloadBean createFromParcel(Parcel source) {
            return new DownloadBean(source);
        }

        @Override
        public DownloadBean[] newArray(int size) {
            return new DownloadBean[size];
        }
    };
}
