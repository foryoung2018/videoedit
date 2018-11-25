package com.wmlive.hhvideo.heihei.beans.recordmv;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * Author：create by jht on 2018/10/10 16:52
 * Email：haitian.jiang@welines.cn
 */
public class MvBgEntity extends BaseModel{

    /**
     * status : 1
     * bg_name : bg_test_1
     * bg_resource : http://s1.wmlives.com/data/dongci/creative_resource/20181010105449227988.zip
     * bg_cover : http://s1.wmlives.com/data/dongci/user_cover/2017062616_100077_zBrSCO6Zuj.png!cover_img_thumbnail
     * is_default : 1
     * default_download : 1
     * bg_md5 : C36189936F8831309F873EF925E36C23
     */

    public int status;
    public String bg_name;
    public String bg_resource;
    public String bg_cover;
    public int is_default;
    public int default_download;
    public String bg_md5;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getBg_name() {
        return bg_name;
    }

    public void setBg_name(String bg_name) {
        this.bg_name = bg_name;
    }

    public String getBg_resource() {
        return bg_resource;
    }

    public void setBg_resource(String bg_resource) {
        this.bg_resource = bg_resource;
    }

    public String getBg_cover() {
        return bg_cover;
    }

    public void setBg_cover(String bg_cover) {
        this.bg_cover = bg_cover;
    }

    public int getIs_default() {
        return is_default;
    }

    public void setIs_default(int is_default) {
        this.is_default = is_default;
    }

    public int getDefault_download() {
        return default_download;
    }

    public void setDefault_download(int default_download) {
        this.default_download = default_download;
    }

    public String getBg_md5() {
        return bg_md5;
    }

    public void setBg_md5(String bg_md5) {
        this.bg_md5 = bg_md5;
    }

    public MvBgEntity() {
    }

    @Override
    public String toString() {
        return "MvBgEntity{" +
                "status=" + status +
                ", bg_name='" + bg_name + '\'' +
                ", bg_resource='" + bg_resource + '\'' +
                ", bg_cover='" + bg_cover + '\'' +
                ", is_default=" + is_default +
                ", default_download=" + default_download +
                ", bg_md5='" + bg_md5 + '\'' +
                '}';
    }
}
