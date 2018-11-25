package com.wmlive.hhvideo.heihei.beans.log;

/**
 * 视频下载情况
 *
 */
public class VideoDownLoad {
    /**最终url*/
    private String url;
    /**素材ID*/
    private String opus_id;
    /**完整素材文件大小:KB*/
    private String file_len;

    /**下载素材大小:KB*/
    private String download_len;
    /***下载所用时间:s**/
    private String download_duration;
    /***平均下载速度KB/s*/
    private String download_speed;

    private String buffer_count;
    private String buffer_duration;

    public VideoDownLoad(String url, String opus_id, String file_len, String download_len, String download_duration, String download_speed, String buffer_count, String buffer_duration) {
        this.url = url;
        this.opus_id = opus_id;
        this.file_len = file_len;
        this.download_len = download_len;
        this.download_duration = download_duration;
        this.download_speed = download_speed;
        this.buffer_count = buffer_count;
        this.buffer_duration = buffer_duration;
    }

    public void setBuffer_duration(String buffer_duration) {
        this.buffer_duration = buffer_duration;
    }

    public String getBuffer_duration() {
        return buffer_duration;
    }


    public void setUrl(String url) {
        this.url = url;
    }

    public void setOpus_id(String opus_id) {
        this.opus_id = opus_id;
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

    public void setBuffer_count(String buffer_count) {
        this.buffer_count = buffer_count;
    }


    public String getUrl() {
        return url;
    }

    public String getOpus_id() {
        return opus_id;
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

    public String getBuffer_count() {
        return buffer_count;
    }

    @Override
    public String toString() {
        return "VideoDownLoad{" +
                "url='" + url + '\'' +
                ", opus_id='" + opus_id + '\'' +
                ", file_len='" + file_len + '\'' +
                ", download_len='" + download_len + '\'' +
                ", download_duration='" + download_duration + '\'' +
                ", download_speed='" + download_speed + '\'' +
                ", buffer_count='" + buffer_count + '\'' +
                ", buffer_duration='" + buffer_duration + '\'' +
                '}';
    }
}
