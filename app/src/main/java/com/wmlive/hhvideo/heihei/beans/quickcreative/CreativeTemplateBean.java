package com.wmlive.hhvideo.heihei.beans.quickcreative;

import com.wmlive.networklib.entity.BaseResponse;

public class CreativeTemplateBean extends BaseResponse {

    /**
     * bg : {"bg_name":"bg6","bg_resource":"http://s1.wmlives.com/data/dongci/creative_resource/20181101202530942017.zip","bg_cover":"http://s1.wmlives.com/data/dongci/creative_resource/20181101202530845278.png","bg_md5":"0D889BF2994451C44D438D47692863E1"}
     * error_code : 0
     * error_msg : success
     * template : {"zip_path":"http://s1.wmlives.com/data/dongci/creative_resource/20181101181038332891.zip","title":"我要吃肉","template_name":"woyaochirou20181031","zip_md5":"0D1E456755659C9DB4733F13F7B1BA9A","default_bg":"bg6","template_cover":"http://s1.wmlives.com/data/dongci/creative_resource/20181101181038349775.png"}
     */

    private BgBean bg;
    private int error_code;
    private String error_msg;
    private TemplateBean template;

    public BgBean getBg() {
        return bg;
    }

    public void setBg(BgBean bg) {
        this.bg = bg;
    }

    public int getError_code() {
        return error_code;
    }


    public String getError_msg() {
        return error_msg;
    }


    public TemplateBean getTemplate() {
        return template;
    }

    public void setTemplate(TemplateBean template) {
        this.template = template;
    }

    public static class BgBean {
        /**
         * bg_name : bg6
         * bg_resource : http://s1.wmlives.com/data/dongci/creative_resource/20181101202530942017.zip
         * bg_cover : http://s1.wmlives.com/data/dongci/creative_resource/20181101202530845278.png
         * bg_md5 : 0D889BF2994451C44D438D47692863E1
         */

        private String bg_name;
        private String bg_resource;
        private String bg_cover;
        private String bg_md5;

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

        public String getBg_md5() {
            return bg_md5;
        }

        public void setBg_md5(String bg_md5) {
            this.bg_md5 = bg_md5;
        }
    }

    public static class TemplateBean {
        /**
         * zip_path : http://s1.wmlives.com/data/dongci/creative_resource/20181101181038332891.zip
         * title : 我要吃肉
         * template_name : woyaochirou20181031
         * zip_md5 : 0D1E456755659C9DB4733F13F7B1BA9A
         * default_bg : bg6
         * template_cover : http://s1.wmlives.com/data/dongci/creative_resource/20181101181038349775.png
         */

        private String zip_path;
        private String title;
        private String template_name;
        private String zip_md5;
        private String default_bg;
        private String template_cover;

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

        public String getDefault_bg() {
            return default_bg;
        }

        public void setDefault_bg(String default_bg) {
            this.default_bg = default_bg;
        }

        public String getTemplate_cover() {
            return template_cover;
        }

        public void setTemplate_cover(String template_cover) {
            this.template_cover = template_cover;
        }
    }
}
