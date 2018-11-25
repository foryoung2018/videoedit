package com.wmlive.hhvideo.heihei.beans.quickcreative;

import com.wmlive.networklib.entity.BaseResponse;

import java.util.List;

public class CreativeTemplateListBean extends BaseResponse {

    private List<BgListBean> bg_list;

    public void setRemove_bg_list(List<String> remove_bg_list) {
        this.remove_bg_list = remove_bg_list;
    }

    public List<String> getRemove_bg_list() {
        return remove_bg_list;
    }

    private List<String> remove_bg_list;
    private List<String> remove_template_list;
    private List<TemplateListBean> template_list;

    public List<BgListBean> getBg_list() {
        return bg_list;
    }

    public void setBg_list(List<BgListBean> bg_list) {
        this.bg_list = bg_list;
    }


    public List<String> getRemove_template_list() {
        return remove_template_list;
    }

    public void setRemove_template_list(List<String> remove_template_list) {
        this.remove_template_list = remove_template_list;
    }

    public List<TemplateListBean> getTemplate_list() {
        return template_list;
    }

    public void setTemplate_list(List<TemplateListBean> template_list) {
        this.template_list = template_list;
    }

    public static class BgListBean {
        /**
         * status : 1
         * bg_name : bg_test_1
         * bg_resource : http://s1.wmlives.com/data/dongci/creative_resource/20181010105449227988.zip
         * bg_cover : http://s1.wmlives.com/data/dongci/user_cover/2017062616_100077_zBrSCO6Zuj.png!cover_img_thumbnail
         * is_default : 1
         * default_download : 1
         * bg_md5 : C36189936F8831309F873EF925E36C23
         */

        private int status;
        private String bg_name;
        private String bg_resource;
        private String bg_cover;
        private int is_default;
        private int default_download;
        private String bg_md5;

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
    }

    public static class TemplateListBean {
        /**
         * status : 1
         * zip_path : http://s1.wmlives.com/data/dongci/creative_resource/20181008185522768389.zip
         * template_name : dongci_v4.0_01
         * zip_md5 : F8AC4C327440A817833259BF5AA2D415
         * is_default : 1
         * default_bg : bg_test_2
         * default_download : 1
         * template_cover : http://s1.wmlives.com/data/dongci/creative_resource/20181008185522696827.jpeg
         */

        private int status;
        private String zip_path;
        private String template_name;
        private String zip_md5;
        private int is_default;
        private String default_bg;
        private int default_download;
        private String template_cover;

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
    }
}
