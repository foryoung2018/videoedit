package com.wmlive.hhvideo.heihei.quickcreative;

import java.util.List;

/**
 * 创意视频的通用配置文件解析bean
 */
public class ConfigJsonBean {

    /**
     * version : 1.0.0
     * video_script : [{"id":1,"name":"vs_static_with_decoration_0001.json"},{"id":2,"name":"vs_static_with_decoration_0002.json"},{"id":3,"name":"vs_static_with_decoration_0003.json"},{"id":4,"name":"vs_static_with_decoration_0004.json"},{"id":5,"name":"vs_static_with_decoration_0005.json"},{"id":6,"name":"vs_static_with_decoration_0006.json"}]
     * recordDuration : 1.5
     * thumbnail_generate_time : 24.625
     * midiurl : woyaochirou.mid
     * background_music : woyaochirouBGM.wav
     * audio_script : audioconfig.json
     * items : [{"tips":"大声喊出「我～」","image":"1.jpeg"},{"tips":"喊出「要～」","image":"2.jpeg"},{"tips":"你想要「吃！」","image":"3.jpeg"},{"tips":"你想吃「肉～」","image":"4.jpeg"},{"tips":"严肃地说出「bong～」","image":"5.jpeg"},{"tips":"再来一个「啪～」的声音","image":"6.jpeg"}]
     */

    private String version;
    private String recordDuration;
    private String thumbnail_generate_time;
    private String midiurl;
    private String background_music;
    private String audio_script;
    private List<VideoScriptBean> video_script;
    private List<ItemsBean> items;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRecordDuration() {
        return recordDuration;
    }

    public void setRecordDuration(String recordDuration) {
        this.recordDuration = recordDuration;
    }

    public String getThumbnail_generate_time() {
        return thumbnail_generate_time;
    }

    public void setThumbnail_generate_time(String thumbnail_generate_time) {
        this.thumbnail_generate_time = thumbnail_generate_time;
    }

    public String getMidiurl() {
        return midiurl;
    }

    public void setMidiurl(String midiurl) {
        this.midiurl = midiurl;
    }

    public String getBackground_music() {
        return background_music;
    }

    public void setBackground_music(String background_music) {
        this.background_music = background_music;
    }

    public String getAudio_script() {
        return audio_script;
    }

    public void setAudio_script(String audio_script) {
        this.audio_script = audio_script;
    }

    public List<VideoScriptBean> getVideo_script() {
        return video_script;
    }

    public void setVideo_script(List<VideoScriptBean> video_script) {
        this.video_script = video_script;
    }

    public List<ItemsBean> getItems() {
        return items;
    }

    public void setItems(List<ItemsBean> items) {
        this.items = items;
    }

    public static class VideoScriptBean {
        /**
         * id : 1
         * name : vs_static_with_decoration_0001.json
         */

        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class ItemsBean {
        /**
         * tips : 大声喊出「我～」
         * image : 1.jpeg
         */

        private String tips;
        private String image;

        public String getTips() {
            return tips;
        }

        public void setTips(String tips) {
            this.tips = tips;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }

    @Override
    public String toString() {
        return "ConfigJsonBean{" +
                "version='" + version + '\'' +
                ", recordDuration='" + recordDuration + '\'' +
                ", thumbnail_generate_time='" + thumbnail_generate_time + '\'' +
                ", midiurl='" + midiurl + '\'' +
                ", background_music='" + background_music + '\'' +
                ", audio_script='" + audio_script + '\'' +
                ", video_script=" + video_script +
                ", items=" + items +
                '}';
    }
}
