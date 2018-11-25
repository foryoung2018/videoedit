package com.wmlive.hhvideo.heihei.beans.discovery;

import com.wmlive.hhvideo.heihei.beans.search.SearchMusicBean;
import com.wmlive.networklib.entity.BaseResponse;

import java.util.List;

/**
 * Created by lsq on 6/5/2017.
 * 分类搜索音乐的结果
 */

public class MusicResultBean extends BaseResponse {

    /**
     * cat_id : 2
     * cat_music : [{"album_cover":"","album_id":2,"album_name":"花儿","album_name_cn":"","genre":"","hot":0,"id":3,"longs":0,"music_path":"","name":"那些花儿","name_cn":"那些花儿","singer_id":2,"singer_name":"朴树"}]
     */

    private int cat_id;
    private List<SearchMusicBean> cat_music;

    public int getCat_id() {
        return cat_id;
    }

    public void setCat_id(int cat_id) {
        this.cat_id = cat_id;
    }

    public List<SearchMusicBean> getCat_music() {
        return cat_music;
    }

    private List<SearchMusicBean> favorites_music;

    public void setCat_music(List<SearchMusicBean> cat_music) {
        this.cat_music = cat_music;
    }

    public List<SearchMusicBean> getFavorites_music() {
        return favorites_music;
    }

    public void setFavorites_music(List<SearchMusicBean> favorites_music) {
        this.favorites_music = favorites_music;
    }
}
