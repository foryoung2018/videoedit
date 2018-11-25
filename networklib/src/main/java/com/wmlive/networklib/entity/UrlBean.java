package com.wmlive.networklib.entity;

/**
 * URL 包含信息
 * Author：create by jht on 2018/9/6 16:26
 * Email：haitian.jiang@welines.cn
 */
public class UrlBean{

    /**
     * code : 0
     * data : {"ip":"103.255.228.110","country":"中国","area":"","region":"北京","city":"北京","county":"XX","isp":"联通","country_id":"CN","area_id":"","region_id":"110000","city_id":"110100","county_id":"xx","isp_id":"100026","cip":"103.255.228.110","cname":"中国,北京,北京,联通,39.904989,116.405285,Asia/Shanghai,UTC+8,110000,86,CN,AP","province":"北京"}
     */

    private int code;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * ip : 103.255.228.110
         * country : 中国
         * area :
         * region : 北京
         * city : 北京
         * county : XX
         * isp : 联通
         * country_id : CN
         * area_id :
         * region_id : 110000
         * city_id : 110100
         * county_id : xx
         * isp_id : 100026
         * cip : 103.255.228.110
         * cname : 中国,北京,北京,联通,39.904989,116.405285,Asia/Shanghai,UTC+8,110000,86,CN,AP
         * province : 北京
         */

        public String ip;
        public String country;
        public String area;
        public String region;
        public String city;
        public String county;
        public String isp;
        public String country_id;
        public String area_id;
        public String region_id;
        public String city_id;
        public String county_id;
        public String isp_id;
        public String cip;
        public String province;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCounty() {
            return county;
        }

        public void setCounty(String county) {
            this.county = county;
        }

        public String getIsp() {
            return isp;
        }

        public void setIsp(String isp) {
            this.isp = isp;
        }

        public String getCountry_id() {
            return country_id;
        }

        public void setCountry_id(String country_id) {
            this.country_id = country_id;
        }

        public String getArea_id() {
            return area_id;
        }

        public void setArea_id(String area_id) {
            this.area_id = area_id;
        }

        public String getRegion_id() {
            return region_id;
        }

        public void setRegion_id(String region_id) {
            this.region_id = region_id;
        }

        public String getCity_id() {
            return city_id;
        }

        public void setCity_id(String city_id) {
            this.city_id = city_id;
        }

        public String getCounty_id() {
            return county_id;
        }

        public void setCounty_id(String county_id) {
            this.county_id = county_id;
        }

        public String getIsp_id() {
            return isp_id;
        }

        public void setIsp_id(String isp_id) {
            this.isp_id = isp_id;
        }

        public String getCip() {
            return cip;
        }

        public void setCip(String cip) {
            this.cip = cip;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }
    }
}
