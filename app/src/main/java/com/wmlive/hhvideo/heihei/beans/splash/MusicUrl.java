package com.wmlive.hhvideo.heihei.beans.splash;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * Created by vhawk on 2017/5/22.
 */

public class MusicUrl extends BaseModel {

    /**
     * listOpusByMusic : http://api.dongci-test.wmlives.com/api/music/list-music-opus
     * myFavMusic : http://api.dongci-test.wmlives.com/api/music/my-fav-music
     * getMusicByCat : http://api.dongci-test.wmlives.com/api/music/get-cat-music
     * getCategory : http://api.dongci-test.wmlives.com/api/music/get-category
     * favMusic : http://api.dongci-test.wmlives.com/api/music/fav-music
     * search : http://api.dongci-test.wmlives.com/api/music/search
     */

    private String listOpusByMusic;
    private String myFavMusic;
    private String getMusicByCat;
    private String getCategory;
    private String favMusic;
    private String search;

    public String getListOpusByMusic() {
        return listOpusByMusic;
    }

    public void setListOpusByMusic(String listOpusByMusic) {
        this.listOpusByMusic = listOpusByMusic;
    }

    public String getMyFavMusic() {
        return myFavMusic;
    }

    public void setMyFavMusic(String myFavMusic) {
        this.myFavMusic = myFavMusic;
    }

    public String getGetMusicByCat() {
        return getMusicByCat;
    }

    public void setGetMusicByCat(String getMusicByCat) {
        this.getMusicByCat = getMusicByCat;
    }

    public String getGetCategory() {
        return getCategory;
    }

    public void setGetCategory(String getCategory) {
        this.getCategory = getCategory;
    }

    public String getFavMusic() {
        return favMusic;
    }

    public void setFavMusic(String favMusic) {
        this.favMusic = favMusic;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    @Override
    public String toString() {
        return "MusicUrl{" +
                "listOpusByMusic='" + listOpusByMusic + '\'' +
                ", myFavMusic='" + myFavMusic + '\'' +
                ", getMusicByCat='" + getMusicByCat + '\'' +
                ", getCategory='" + getCategory + '\'' +
                ", favMusic='" + favMusic + '\'' +
                ", search='" + search + '\'' +
                '}';
    }
}
