package com.wmlive.hhvideo.heihei.beans.recordmv;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * Author：create by admin on 2018/10/10 16:54
 * Email：haitian.jiang@welines.cn
 */
public class MvTemplateEntity extends BaseModel {

    /**
     * title :
     * status : 1
     * zip_path : http://s1.wmlives.com/data/dongci/creative_resource/20181008185522768389.zip
     * template_name : dongci_v4.0_01
     * zip_md5 : F8AC4C327440A817833259BF5AA2D415
     * is_default : 1
     * default_bg : bg_test_2
     * default_download : 1
     * template_cover : http://s1.wmlives.com/data/dongci/creative_resource/20181008185522696827.jpeg
     */

    public String title;
    public int status;
    public String zip_path;
    public String template_name;
    public String zip_md5;
    public int is_default;
    public String default_bg;
    public int default_download;
    public String template_cover;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getZip_path() {
        return zip_path;
    }

    public void setZip_path(String zip_path) {
        this.zip_path = zip_path;
    }

    public String getTemplate_name() {
        return template_name;
    }

    public void setTemplate_name(String template_name) {
        this.template_name = template_name;
    }

    public String getZip_md5() {
        return zip_md5;
    }

    public void setZip_md5(String zip_md5) {
        this.zip_md5 = zip_md5;
    }

    public int getIs_default() {
        return is_default;
    }

    public void setIs_default(int is_default) {
        this.is_default = is_default;
    }

    public String getDefault_bg() {
        return default_bg;
    }

    public void setDefault_bg(String default_bg) {
        this.default_bg = default_bg;
    }

    public int getDefault_download() {
        return default_download;
    }

    public void setDefault_download(int default_download) {
        this.default_download = default_download;
    }

    public String getTemplate_cover() {
        return template_cover;
    }

    public void setTemplate_cover(String template_cover) {
        this.template_cover = template_cover;
    }

    public MvTemplateEntity() {
    }

    @Override
    public String toString() {
        return "MvTemplateEntity{" +
                "title='" + title + '\'' +
                ", status=" + status +
                ", zip_path='" + zip_path + '\'' +
                ", template_name='" + template_name + '\'' +
                ", zip_md5='" + zip_md5 + '\'' +
                ", is_default=" + is_default +
                ", default_bg='" + default_bg + '\'' +
                ", default_download=" + default_download +
                ", template_cover='" + template_cover + '\'' +
                '}';
    }
}
