package com.wmlive.hhvideo.heihei.beans.discovery;

import com.wmlive.hhvideo.common.base.BaseModel;
import com.wmlive.hhvideo.heihei.beans.main.ShareInfo;

public class MusicInfoBean extends BaseModel {
    /**
     * album_cover :
     * album_id : 1
     * album_name : 成都
     * album_name_cn : 成都
     * genre :
     * hot : 6
     * id : 1
     * is_favorite : true
     * longs : 0
     * music_path :
     * name : 成都
     * name_cn : 成都
     * share_info : {"share_desc":"黑黑-原创音乐短视频社区","share_title":"场面已经控制不住，在黑黑《成都》这歌要被玩上天啦！戳这里>>","share_url":"http://api-02.wmlives.com/share/music/1","share_weibo_desc":"场面已经控制不住，在黑黑《成都》这歌要被玩上天啦！戳这里>>"}
     * singer_id : 1
     * singer_name : 赵雷
     * use_count : 0
     */

    private String album_cover;
    private int album_id;
    private String album_name;
    private String album_name_cn;
    private String genre;
    private int hot;
    private String id;
    private boolean is_favorite;
    private int longs;
    private String music_path;
    private String name;
    private String name_cn;
    private ShareInfo share_info;
    private int singer_id;
    private String singer_name;
    private int use_count;

    public String getAlbum_cover() {
        return album_cover;
    }

    public void setAlbum_cover(String album_cover) {
        this.album_cover = album_cover;
    }

    public int getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(int album_id) {
        this.album_id = album_id;
    }

    public String getAlbum_name() {
        return album_name;
    }

    public void setAlbum_name(String album_name) {
        this.album_name = album_name;
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

    public int getHot() {
        return hot;
    }

    public void setHot(int hot) {
        this.hot = hot;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isIs_favorite() {
        return is_favorite;
    }

    public void setIs_favorite(boolean is_favorite) {
        this.is_favorite = is_favorite;
    }

    public int getLongs() {
        return longs;
    }

    public void setLongs(int longs) {
        this.longs = longs;
    }

    public String getMusic_path() {
        return music_path;
    }

    public void setMusic_path(String music_path) {
        this.music_path = music_path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName_cn() {
        return name_cn;
    }

    public void setName_cn(String name_cn) {
        this.name_cn = name_cn;
    }

    public ShareInfo getShare_info() {
        return share_info;
    }

    public void setShare_info(ShareInfo share_info) {
        this.share_info = share_info;
    }

    public int getSinger_id() {
        return singer_id;
    }

    public void setSinger_id(int singer_id) {
        this.singer_id = singer_id;
    }

    public String getSinger_name() {
        return singer_name;
    }

    public void setSinger_name(String singer_name) {
        this.singer_name = singer_name;
    }

    public int getUse_count() {
        return use_count;
    }

    public void setUse_count(int use_count) {
        this.use_count = use_count;
    }

}
