package com.wmlive.hhvideo.heihei.beans.search;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * 搜索中音乐bean
 * Created by kangzhen on 2017/6/2.
 */

public class SearchMusicBean extends BaseModel {
    private String name;
    private int album_id;
    private int longs;
    private long id;
    private String music_path;
    private String name_cn;
    private String singer_name;
    private int hot;
    private int use_count;
    private String album_cover;
    private boolean is_favorite;
    private String album_name_cn;
    private String genre;
    private String album_name;
    private int singer_id;
    private int playStatus;//自定义的字段，表示播放状态，0：暂停，1：播放，2：缓冲

    public SearchMusicBean() {
    }

    public int getPlayStatus() {
        return playStatus;
    }

    public void setPlayStatus(int playStatus) {
        this.playStatus = playStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(int album_id) {
        this.album_id = album_id;
    }

    public int getLongs() {
        return longs;
    }

    public void setLongs(int longs) {
        this.longs = longs;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMusic_path() {
        return music_path;
    }

    public void setMusic_path(String music_path) {
        this.music_path = music_path;
    }

    public String getName_cn() {
        return name_cn;
    }

    public void setName_cn(String name_cn) {
        this.name_cn = name_cn;
    }

    public String getSinger_name() {
        return singer_name;
    }

    public void setSinger_name(String singer_name) {
        this.singer_name = singer_name;
    }

    public int getHot() {
        return hot;
    }

    public void setHot(int hot) {
        this.hot = hot;
    }

    public int getUse_count() {
        return use_count;
    }

    public void setUse_count(int use_count) {
        this.use_count = use_count;
    }

    public String getAlbum_cover() {
        return album_cover;
    }

    public void setAlbum_cover(String album_cover) {
        this.album_cover = album_cover;
    }

    public boolean is_favorite() {
        return is_favorite;
    }

    public void setIs_favorite(boolean is_favorite) {
        this.is_favorite = is_favorite;
    }

    public String getAlbum_name_cn() {
        return album_name_cn;
    }

    public void setAlbum_name_cn(String album_name_cn) {
        this.album_name_cn = album_name_cn;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getAlbum_name() {
        return album_name;
    }

    public void setAlbum_name(String album_name) {
        this.album_name = album_name;
    }

    public int getSinger_id() {
        return singer_id;
    }

    public void setSinger_id(int singer_id) {
        this.singer_id = singer_id;
    }


    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", album_id=" + album_id +
                ", longs=" + longs +
                ", id=" + id +
                ", music_path='" + music_path + '\'' +
                ", name_cn='" + name_cn + '\'' +
                ", singer_name='" + singer_name + '\'' +
                ", hot=" + hot +
                ", use_count=" + use_count +
                ", album_cover='" + album_cover + '\'' +
                ", is_favorite=" + is_favorite +
                ", album_name_cn='" + album_name_cn + '\'' +
                ", genre='" + genre + '\'' +
                ", album_name='" + album_name + '\'' +
                ", singer_id=" + singer_id +
                '}';
    }
}
