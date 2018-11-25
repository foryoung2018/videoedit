package com.wmlive.hhvideo.heihei.quickcreative;

import java.util.List;

public class BgConfigJsonBean {

    /**
     * version : 1.0.0
     * bgVideo : bgVideo.mp4
     * decorations : [{"name":"d4","mask":"d4_mask.png","frameRate":24,"images":["d4_001.png","d4_002.png","d4_003.png","d4_004.png.","d4_005.png","d4_006.png","d4_007.png","d4_008.png","d4_009.png","d4_010.png","d4_011.png","d4_012.png","d4_013.png","d4_014.png","d4_015.png","d4_016.png","d4_017.png","d4_018.png"]}]
     */

    private String version;
    private String bgVideo;
    private List<DecorationsBean> decorations;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getBgVideo() {
        return bgVideo;
    }

    public void setBgVideo(String bgVideo) {
        this.bgVideo = bgVideo;
    }

    public List<DecorationsBean> getDecorations() {
        return decorations;
    }

    public void setDecorations(List<DecorationsBean> decorations) {
        this.decorations = decorations;
    }

    public static class DecorationsBean {
        /**
         * name : d4
         * mask : d4_mask.png
         * frameRate : 24
         * images : ["d4_001.png","d4_002.png","d4_003.png","d4_004.png.","d4_005.png","d4_006.png","d4_007.png","d4_008.png","d4_009.png","d4_010.png","d4_011.png","d4_012.png","d4_013.png","d4_014.png","d4_015.png","d4_016.png","d4_017.png","d4_018.png"]
         */

        private String name;
        private String mask;
        private int frameRate;
        private List<String> images;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMask() {
            return mask;
        }

        public void setMask(String mask) {
            this.mask = mask;
        }

        public int getFrameRate() {
            return frameRate;
        }

        public void setFrameRate(int frameRate) {
            this.frameRate = frameRate;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }
    }
}
