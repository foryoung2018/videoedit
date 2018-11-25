package com.wmlive.hhvideo.heihei.beans.recordmv;

import com.wmlive.networklib.entity.BaseResponse;

/**
 * Author：create by jht on 2018/10/10 15:45
 * Email：haitian.jiang@welines.cn
 */
public class SingleTemplateBean extends BaseResponse{

    /**
     * bg : {"status":1,"bg_name":"bg_test_1","bg_resource":"http://s1.wmlives.com/data/dongci/creative_resource/20181010105449227988.zip","bg_cover":"http://s1.wmlives.com/data/dongci/user_cover/2017062616_100077_zBrSCO6Zuj.png!cover_img_thumbnail","is_default":1,"default_download":1,"bg_md5":"C36189936F8831309F873EF925E36C23"}
     * template : {"title":"","status":1,"zip_path":"http://s1.wmlives.com/data/dongci/creative_resource/20181008185522768389.zip","template_name":"dongci_v4.0_01","zip_md5":"F8AC4C327440A817833259BF5AA2D415","is_default":1,"default_bg":"bg_test_2","default_download":1,"template_cover":"http://s1.wmlives.com/data/dongci/creative_resource/20181008185522696827.jpeg"}
     * opus : {"music_album_cover":"http://s1.wmlives.com/data/dongci/music_album/20170614164830764098.jpg?x-oss-process=style/album_img_thumbnail","is_teamwork":1,"topic_name":"","visible":0,"create_time":1503571632,"opus_gif_cover":"http://s1.wmlives.com/data/dongci/opus_cover/2017082410_11182_XLWGt0hc4j.webp","frame_layout":"frame_a","id":11753,"lat_lon":"39.919063,116.450799","opus_small_cover":"http://s1.wmlives.com/data/dongci/opus_cover/2017082410_11182_Wn3jfGY5JD.jpg!video_img_thumbnail","is_delete":0,"topic_id":0,"owner_id":11182,"video_file_sign":"e4aeed754d4d1ae8f9bbaacbd54ba08e","music_id":254,"play_count":15,"opus_path":"http://s1.wmlives.com/data/dongci/opus/201708241011182VnhRZ79y4m","wonderful_tag":"","music_time":"","music_name":"月光爱人 李玟","opus_cover":"http://s1.wmlives.com/data/dongci/opus_cover/2017082410_11182_Wn3jfGY5JD.jpg!video_cover_img","title":"","like_count":2,"opus_length":12,"example":0,"at_user_ids":""}
     */

    public MvBgEntity bg;
    public MvTemplateEntity template;
    public OpusBean opus;

    public OpusBean getOpus() {
        return opus;
    }

    public void setOpus(OpusBean opus) {
        this.opus = opus;
    }


    public static class OpusBean {
        /**
         * music_album_cover : http://s1.wmlives.com/data/dongci/music_album/20170614164830764098.jpg?x-oss-process=style/album_img_thumbnail
         * is_teamwork : 1
         * topic_name :
         * visible : 0
         * create_time : 1503571632
         * opus_gif_cover : http://s1.wmlives.com/data/dongci/opus_cover/2017082410_11182_XLWGt0hc4j.webp
         * frame_layout : frame_a
         * id : 11753
         * lat_lon : 39.919063,116.450799
         * opus_small_cover : http://s1.wmlives.com/data/dongci/opus_cover/2017082410_11182_Wn3jfGY5JD.jpg!video_img_thumbnail
         * is_delete : 0
         * topic_id : 0
         * owner_id : 11182
         * video_file_sign : e4aeed754d4d1ae8f9bbaacbd54ba08e
         * music_id : 254
         * play_count : 15
         * opus_path : http://s1.wmlives.com/data/dongci/opus/201708241011182VnhRZ79y4m
         * wonderful_tag :
         * music_time :
         * music_name : 月光爱人 李玟
         * opus_cover : http://s1.wmlives.com/data/dongci/opus_cover/2017082410_11182_Wn3jfGY5JD.jpg!video_cover_img
         * title :
         * like_count : 2
         * opus_length : 12
         * example : 0
         * at_user_ids :
         */

        private String music_album_cover;
        private int is_teamwork;
        private String topic_name;
        private int visible;
        private int create_time;
        private String opus_gif_cover;
        private String frame_layout;
        private int id;
        private String lat_lon;
        private String opus_small_cover;
        private int is_delete;
        private int topic_id;
        private int owner_id;
        private String video_file_sign;
        private int music_id;
        private int play_count;
        private String opus_path;
        private String wonderful_tag;
        private String music_time;
        private String music_name;
        private String opus_cover;
        private String title;
        private int like_count;
        private int opus_length;
        private int example;
        private String at_user_ids;

        public String getMusic_album_cover() {
            return music_album_cover;
        }

        public void setMusic_album_cover(String music_album_cover) {
            this.music_album_cover = music_album_cover;
        }

        public int getIs_teamwork() {
            return is_teamwork;
        }

        public void setIs_teamwork(int is_teamwork) {
            this.is_teamwork = is_teamwork;
        }

        public String getTopic_name() {
            return topic_name;
        }

        public void setTopic_name(String topic_name) {
            this.topic_name = topic_name;
        }

        public int getVisible() {
            return visible;
        }

        public void setVisible(int visible) {
            this.visible = visible;
        }

        public int getCreate_time() {
            return create_time;
        }

        public void setCreate_time(int create_time) {
            this.create_time = create_time;
        }

        public String getOpus_gif_cover() {
            return opus_gif_cover;
        }

        public void setOpus_gif_cover(String opus_gif_cover) {
            this.opus_gif_cover = opus_gif_cover;
        }

        public String getFrame_layout() {
            return frame_layout;
        }

        public void setFrame_layout(String frame_layout) {
            this.frame_layout = frame_layout;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLat_lon() {
            return lat_lon;
        }

        public void setLat_lon(String lat_lon) {
            this.lat_lon = lat_lon;
        }

        public String getOpus_small_cover() {
            return opus_small_cover;
        }

        public void setOpus_small_cover(String opus_small_cover) {
            this.opus_small_cover = opus_small_cover;
        }

        public int getIs_delete() {
            return is_delete;
        }

        public void setIs_delete(int is_delete) {
            this.is_delete = is_delete;
        }

        public int getTopic_id() {
            return topic_id;
        }

        public void setTopic_id(int topic_id) {
            this.topic_id = topic_id;
        }

        public int getOwner_id() {
            return owner_id;
        }

        public void setOwner_id(int owner_id) {
            this.owner_id = owner_id;
        }

        public String getVideo_file_sign() {
            return video_file_sign;
        }

        public void setVideo_file_sign(String video_file_sign) {
            this.video_file_sign = video_file_sign;
        }

        public int getMusic_id() {
            return music_id;
        }

        public void setMusic_id(int music_id) {
            this.music_id = music_id;
        }

        public int getPlay_count() {
            return play_count;
        }

        public void setPlay_count(int play_count) {
            this.play_count = play_count;
        }

        public String getOpus_path() {
            return opus_path;
        }

        public void setOpus_path(String opus_path) {
            this.opus_path = opus_path;
        }

        public String getWonderful_tag() {
            return wonderful_tag;
        }

        public void setWonderful_tag(String wonderful_tag) {
            this.wonderful_tag = wonderful_tag;
        }

        public String getMusic_time() {
            return music_time;
        }

        public void setMusic_time(String music_time) {
            this.music_time = music_time;
        }

        public String getMusic_name() {
            return music_name;
        }

        public void setMusic_name(String music_name) {
            this.music_name = music_name;
        }

        public String getOpus_cover() {
            return opus_cover;
        }

        public void setOpus_cover(String opus_cover) {
            this.opus_cover = opus_cover;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getLike_count() {
            return like_count;
        }

        public void setLike_count(int like_count) {
            this.like_count = like_count;
        }

        public int getOpus_length() {
            return opus_length;
        }

        public void setOpus_length(int opus_length) {
            this.opus_length = opus_length;
        }

        public int getExample() {
            return example;
        }

        public void setExample(int example) {
            this.example = example;
        }

        public String getAt_user_ids() {
            return at_user_ids;
        }

        public void setAt_user_ids(String at_user_ids) {
            this.at_user_ids = at_user_ids;
        }
    }
}
