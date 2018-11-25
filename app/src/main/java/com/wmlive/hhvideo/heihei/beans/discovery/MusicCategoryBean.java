package com.wmlive.hhvideo.heihei.beans.discovery;

import com.wmlive.networklib.entity.BaseResponse;

import java.util.List;

/**
 * Created by lsq on 6/5/2017.
 * 音乐分类的返回bean
 */

public class MusicCategoryBean extends BaseResponse {

    private List<MusicCatBean> music_cat;

    public List<MusicCatBean> getMusic_cat() {
        return music_cat;
    }

    public void setMusic_cat(List<MusicCatBean> music_cat) {
        this.music_cat = music_cat;
    }

    public static class MusicCatBean {
        /**
         * cat_name : 热门
         * id : 1
         */

        private String cat_name;
        private long id;

        public String getCat_name() {
            return cat_name;
        }

        public void setCat_name(String cat_name) {
            this.cat_name = cat_name;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "MusicCatBean{" +
                    "cat_name='" + cat_name + '\'' +
                    ", id='" + id + '\'' +
                    '}';
        }
    }
}
