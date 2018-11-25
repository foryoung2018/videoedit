package com.wmlive.hhvideo.heihei.beans.quickcreative;

import com.wmlive.networklib.entity.BaseResponse;

import java.util.List;

public class CreativeTemplateListBean extends BaseResponse {


    private List<BgListBean> bg_list;
    private List<String> remove_bg_list;
    private List<String> remove_template_list;
    private List<TemplateListBean> template_list;

    public List<BgListBean> getBg_list() {
        return bg_list;
    }

    public void setBg_list(List<BgListBean> bg_list) {
        this.bg_list = bg_list;
    }

    public List<String> getRemove_bg_list() {
        return remove_bg_list;
    }

    public void setRemove_bg_list(List<String> remove_bg_list) {
        this.remove_bg_list = remove_bg_list;
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
         * bg_name : bg_test_06
         * bg_resource : http://s1.wmlives.com/data/dongci/creative_resource/20181017190222156148.zip
         * bg_cover : http://s1.wmlives.com/data/dongci/creative_resource/20181017161634670546.png
         * is_default : 1
         * default_download : 1
         * bg_md5 : 6F108BB538C88256480AC284975C47A8
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
         * zip_path : http://s1.wmlives.com/data/dongci/creative_resource/20181025192701813814.zip
         * title : 我要吃肉（内测少音符版）
         * template_name : template_test_10_less_note
         * zip_md5 : 0085AC648FFBDFACA5230BC8CF6BFE4E
         * is_default : 1
         * default_bg :
         * default_download : 1
         * template_cover : http://s1.wmlives.com/data/dongci/creative_resource/20181025164322115558.png
         */

        private int status;
        private String zip_path;
        private String title;
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

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
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
