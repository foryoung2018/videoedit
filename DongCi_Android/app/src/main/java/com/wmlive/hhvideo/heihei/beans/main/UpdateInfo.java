package com.wmlive.hhvideo.heihei.beans.main;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * 系统升级的bean
 * Created by kangzhen on 2017/6/16.
 */

public class UpdateInfo extends BaseModel {
    private String app_url;//pp下载或更新页面url
    private boolean force;//是否强制更新
    private String app_name;//app名称
    private String tips_title;//弹窗title
    private String file_name;//
    private String tips_text;//弹窗内容
    private String version;//最新版本号
    private String build_version;//
    private String down_compatible;//向下兼容版本号

    public UpdateInfo() {
    }

    public UpdateInfo(String app_url, boolean force, String app_name, String tips_title, String file_name, String tips_text, String version, String build_version, String down_compatible) {
        this.app_url = app_url;
        this.force = force;
        this.app_name = app_name;
        this.tips_title = tips_title;
        this.file_name = file_name;
        this.tips_text = tips_text;
        this.version = version;
        this.build_version = build_version;
        this.down_compatible = down_compatible;
    }

    public String getApp_url() {
        return app_url;
    }

    public void setApp_url(String app_url) {
        this.app_url = app_url;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getTips_title() {
        return tips_title;
    }

    public void setTips_title(String tips_title) {
        this.tips_title = tips_title;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getTips_text() {
        return tips_text;
    }

    public void setTips_text(String tips_text) {
        this.tips_text = tips_text;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getBuild_version() {
        return build_version;
    }

    public void setBuild_version(String build_version) {
        this.build_version = build_version;
    }

    public String getDown_compatible() {
        return down_compatible;
    }

    public void setDown_compatible(String down_compatible) {
        this.down_compatible = down_compatible;
    }

    @Override
    public String toString() {
        return "UpdateInfo{" +
                "app_url='" + app_url + '\'' +
                ", force=" + force +
                ", app_name='" + app_name + '\'' +
                ", tips_title='" + tips_title + '\'' +
                ", file_name='" + file_name + '\'' +
                ", tips_text='" + tips_text + '\'' +
                ", version='" + version + '\'' +
                ", build_version='" + build_version + '\'' +
                ", down_compatible='" + down_compatible + '\'' +
                '}';
    }
}
