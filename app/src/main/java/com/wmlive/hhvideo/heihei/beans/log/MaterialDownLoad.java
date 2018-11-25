package com.wmlive.hhvideo.heihei.beans.log;

/**
 * 素材下载情况
 *
 */
public class MaterialDownLoad {
    /**最终url*/
    private String url;
    /**素材ID*/
    private String material_id;
    /**完整素材文件大小:KB*/
    private String file_len;

    /**下载素材大小:KB*/
    private String download_len;
    /***下载所用时间:s**/
    private String download_duration;
    /***平均下载速度KB/s*/
    private String download_speed;
    /***下载结果：success fail cancel*/
    private String res;

    public void setUrl(String url) {
        this.url = url;
    }

    public void setMaterial_id(String material_id) {
        this.material_id = material_id;
    }

    public void setFile_len(String file_len) {
        this.file_len = file_len;
    }

    public void setDownload_len(String download_len) {
        this.download_len = download_len;
    }

    public void setDownload_duration(String download_duration) {
        this.download_duration = download_duration;
    }

    public void setDownload_speed(String download_speed) {
        this.download_speed = download_speed;
    }

    public void setRes(String res) {
        this.res = res;
    }


    public String getUrl() {
        return url;
    }

    public String getMaterial_id() {
        return material_id;
    }

    public String getFile_len() {
        return file_len;
    }

    public String getDownload_len() {
        return download_len;
    }

    public String getDownload_duration() {
        return download_duration;
    }

    public String getDownload_speed() {
        return download_speed;
    }

    public String getRes() {
        return res;
    }


}
