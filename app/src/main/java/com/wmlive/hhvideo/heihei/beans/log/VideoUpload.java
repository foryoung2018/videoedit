package com.wmlive.hhvideo.heihei.beans.log;

/**
 * 视频上传
 */
public class VideoUpload {

    public String file_len;//视频大小 KB

    public String getFile_len() {
        return file_len;
    }

    public void setFile_len(String file_len) {
        this.file_len = file_len;
    }

    public String getUpoload_duration() {
        return upoload_duration;
    }

    public void setUpoload_duration(String upoload_duration) {
        this.upoload_duration = upoload_duration;
    }

    public String getUpload_speed() {
        return upload_speed;
    }

    public void setUpload_speed(String upload_speed) {
        this.upload_speed = upload_speed;
    }

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }

    public String upoload_duration;//上传时间: s
    public String upload_speed;//平均上传速度:KB/s
    public String res;


}
