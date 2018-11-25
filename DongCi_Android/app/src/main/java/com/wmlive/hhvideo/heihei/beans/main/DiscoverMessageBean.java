package com.wmlive.hhvideo.heihei.beans.main;

import com.wmlive.networklib.entity.BaseResponse;

/**
 * Created by lsq on 7/12/2017.
 * {
 * "data": {
 * "latest_time": 1499763907,
 * "expires": 300,
 * "has_new": true
 * },
 * "error_code": 0,
 * "error_msg": "success"
 * }
 */

public class DiscoverMessageBean extends BaseResponse {
    /**
     * data : {"latest_time":1499763907,"expires":300,"has_new":true}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * latest_time : 1499763907
         * expires : 300
         * has_new : true
         */

        private int latest_time;
        private int latest_news_count;
        private int expires;
        private boolean has_new;

        public int getLatest_time() {
            return latest_time;
        }

        public void setLatest_time(int latest_time) {
            this.latest_time = latest_time;
        }

        public int getExpires() {
            return expires;
        }

        public void setExpires(int expires) {
            this.expires = expires;
        }

        public boolean isHas_new() {
            return has_new;
        }

        public void setHas_new(boolean has_new) {
            this.has_new = has_new;
        }

        public int getLatest_news_count() {
            return latest_news_count;
        }

        public void setLatest_news_count(int latest_news_count) {
            this.latest_news_count = latest_news_count;
        }
    }
}
