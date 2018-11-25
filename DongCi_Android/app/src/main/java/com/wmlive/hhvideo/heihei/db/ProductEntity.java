package com.wmlive.hhvideo.heihei.db;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
//import com.rd.vecore.VirtualVideo;
//import com.rd.vecore.models.VideoConfig;
import com.dongci.sun.gpuimglibrary.api.DCVideoManager;
import com.wmlive.hhvideo.heihei.beans.frame.FrameInfo;
import com.wmlive.hhvideo.heihei.beans.record.MusicInfoEntity;
import com.wmlive.hhvideo.heihei.beans.record.ProductExtendEntity;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.beans.record.TopicInfoEntity;
import com.wmlive.hhvideo.heihei.record.engine.VideoEngine;
import com.wmlive.hhvideo.heihei.record.engine.config.MVideoConfig;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.utils.CommonUtils;
import com.wmlive.hhvideo.utils.JsonUtils;
import com.wmlive.hhvideo.utils.KLog;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Transient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsq on 8/30/2017.
 * 一个作品的实体类，其中包含1到多个视频
 */
@Entity
public class ProductEntity implements Cloneable {

    public static final short TYPE_EDITING = 10;//正常
    public static final short TYPE_DRAFT = 20;//草稿
    public static final short TYPE_PUBLISHING = 30;//发布中

    @Id(autoincrement = true)
    private Long id;

    @Transient
    public List<ShortVideoEntity> shortVideoList;

    @Transient
    public MusicInfoEntity musicInfo;

    @Transient
    public TopicInfoEntity topicInfo;

    @Transient
    public FrameInfo frameInfo;

    @Transient
    public ProductExtendEntity extendInfo;

    public long userId;
    public String combineVideoAudio;//发布作品时组合后的视频
    public String combineVideo;//发布作品时组合后的视频,没有声音
    public String combineAudio;//播放时候，需要的音频
    public long modifyTime;
    public String baseDir;
    public String webpPath;
    public String coverPath;
    public short productType = TYPE_EDITING;//作品类型
    public int musicMixFactor = 100; // 音乐声音混合大小
    public int originalMixFactor = 100; // 原声混合真实大小
    public int originalShowMixFactor = 50; // 原声混合展示大小
    public long originalId;//原作品的id


    private byte[] shortVideosBytes;
    private byte[] frameInfoBytes;
    private byte[] musicInfoBytes;
    private byte[] topicInfoBytes;
    private byte[] extendInfoBytes;

    @Generated(hash = 57077734)
    @Keep
    public ProductEntity(Long id, long userId, String combineVideoAudio, String combineVideo,
                         String combineAudio, long modifyTime, String baseDir, String webpPath, String coverPath,
                         short productType, int musicMixFactor, int originalMixFactor, int originalShowMixFactor,
                         long originalId, byte[] shortVideosBytes, byte[] frameInfoBytes, byte[] musicInfoBytes,
                         byte[] topicInfoBytes, byte[] extendInfoBytes) {
        this.id = id;
        this.userId = userId;
        this.combineVideoAudio = combineVideoAudio;
        this.combineVideo = combineVideo;
        this.combineAudio = combineAudio;
        this.modifyTime = modifyTime;
        this.baseDir = baseDir;
        this.webpPath = webpPath;
        this.coverPath = coverPath;
        this.productType = productType;
        this.musicMixFactor = musicMixFactor;
        this.originalMixFactor = originalMixFactor;
        this.originalShowMixFactor = originalShowMixFactor;
        this.originalId = originalId;
        this.shortVideosBytes = shortVideosBytes;
        this.frameInfoBytes = frameInfoBytes;
        this.musicInfoBytes = musicInfoBytes;
        this.topicInfoBytes = topicInfoBytes;
        this.extendInfoBytes = extendInfoBytes;
        setShortVideosBytes(shortVideosBytes);
        setFrameInfoBytes(frameInfoBytes);
        setMusicInfoBytes(musicInfoBytes);
        setTopicInfoBytes(topicInfoBytes);
        setExtendInfoBytes(extendInfoBytes);

    }

    @Generated(hash = 27353230)
    public ProductEntity() {
    }

    //    @Generated(hash = 1104508787)
//    @Keep
//    public ProductEntity(Long id, long userId, String combineVideo, long modifyTime, String baseDir,
//                         String webpPath, String coverPath, short productType, int musicMixFactor,
//                         int originalMixFactor, int originalShowMixFactor, long originalId,
//                         byte[] shortVideosBytes, byte[] frameInfoBytes,
//                         byte[] musicInfoBytes, byte[] topicInfoBytes, byte[] extendInfoBytes) {
//        this.id = id;
//        this.userId = userId;
//        this.combineVideo = combineVideo;
//        this.modifyTime = modifyTime;
//        this.baseDir = baseDir;
//        this.webpPath = webpPath;
//        this.coverPath = coverPath;
//        this.productType = productType;
//        this.musicMixFactor = musicMixFactor;
//        this.originalMixFactor = originalMixFactor;
//        this.originalShowMixFactor = originalShowMixFactor;
//        this.originalId = originalId;
//        setShortVideosBytes(shortVideosBytes);
//        setFrameInfoBytes(frameInfoBytes);
//        setMusicInfoBytes(musicInfoBytes);
//        setTopicInfoBytes(topicInfoBytes);
//        setExtendInfoBytes(extendInfoBytes);
//    }
    public void setFrameInfoBytes(byte[] frameInfoBytes) {
        if (frameInfoBytes != null) {
            frameInfo = JsonUtils.parseObject(new String(frameInfoBytes), FrameInfo.class);
        }
    }

    public void setMusicInfoBytes(byte[] musicInfoBytes) {
        if (musicInfoBytes != null) {
            musicInfo = JsonUtils.parseObject(musicInfoBytes, MusicInfoEntity.class);
        }
    }

    public void setShortVideosBytes(byte[] shortVideosBytes) {
        if (shortVideosBytes != null) {
            setShortVideos(JsonUtils.parseArray(new String(shortVideosBytes), ShortVideoEntity.class));
        }
    }

    public void setTopicInfoBytes(byte[] topicInfoBytes) {
        if (topicInfoBytes != null) {
            topicInfo = JsonUtils.parseObject(new String(topicInfoBytes), TopicInfoEntity.class);
        }
    }

    public void setExtendInfoBytes(byte[] extendInfoBytes) {
        if (extendInfoBytes != null) {
            extendInfo = JsonUtils.parseObject(new String(extendInfoBytes), ProductExtendEntity.class);
        }
    }

    public byte[] getFrameInfoBytes() {
        if (frameInfo != null) {
            return JSON.toJSONString(frameInfo).getBytes();
        } else {
            return null;
        }
    }

    public byte[] getMusicInfoBytes() {
        if (musicInfo != null) {
            return JSON.toJSONString(musicInfo).getBytes();
        } else {
            return null;
        }
    }

    public byte[] getShortVideosBytes() {
        if (shortVideoList != null) {
            return JSON.toJSONString(shortVideoList).getBytes();
        } else {
            return null;
        }
    }

    public byte[] getTopicInfoBytes() {
        if (topicInfo != null) {
            return JSON.toJSONString(topicInfo).getBytes();
        } else {
            return null;
        }
    }

    public byte[] getExtendInfoBytes() {
        if (extendInfo != null) {
            return JSON.toJSONString(extendInfo).getBytes();
        } else {
            return null;
        }
    }

    public boolean moveToDraft() {
        productType = TYPE_DRAFT;
        return true;
    }

    public void refreshModifyTime() {
        modifyTime = System.currentTimeMillis();
    }

    public long getModifyTime() {
        return this.modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public void setShortVideos(List<ShortVideoEntity> entityList) {
        if (shortVideoList == null) {
            shortVideoList = new ArrayList<>();
        }
        shortVideoList.clear();
        shortVideoList.addAll(entityList);
    }

    public static ProductEntity createProductEntity(FrameInfo frameInfo) {
        ProductEntity productEntity = new ProductEntity();
        productEntity.frameInfo = frameInfo;
        productEntity.musicInfo = new MusicInfoEntity();
        productEntity.shortVideoList = new ArrayList<>();
        productEntity.topicInfo = new TopicInfoEntity();
        if (frameInfo != null && frameInfo.getLayout() != null) {
            for (int i = 0, n = frameInfo.getLayout().size(); i < n; i++) {
                productEntity.shortVideoList.add(new ShortVideoEntity());
            }
        }
        return productEntity;
    }


    public String getBaseDir() {
        return this.baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public int getMusicMixFactor() {
        return this.musicMixFactor;
    }

    public void setMusicMixFactor(int musicMixFactor) {
        this.musicMixFactor = musicMixFactor;
    }

    public int getOriginalMixFactor() {
        return this.originalMixFactor;
    }

    public void setOriginalMixFactor(int originalMixFactor) {
        this.originalMixFactor = originalMixFactor;
    }

    public short getProductType() {
        return this.productType;
    }

    public void setProductType(short productType) {
        this.productType = productType;
    }


    public String getCombineVideo() {
        return this.combineVideo;
    }

    public void setCombineVideo(String combineVideo) {
        this.combineVideo = combineVideo;
    }


    public float getCombineVideoDuringMs() {
        float during = 0;
        KLog.i("PublishPresenter--getCombineVideoDuringMs:" + combineVideo);
        if (!TextUtils.isEmpty(combineVideo) && new File(combineVideo).exists()) {
            //sun
            during = DCVideoManager.getVideoLength(combineVideo);
//            during = VirtualVideo.getMediaInfo(combineVideo, new VideoConfig());
            KLog.d("during==" + during);
        }
        return (during > 0 ? during : 0) / 1000;
    }

    public boolean hasVideo() {
        if (shortVideoList != null) {
            boolean hasVideo = false;
            for (ShortVideoEntity videoEntity : shortVideoList) {
                if (videoEntity != null && videoEntity.hasVideo()) {
                    hasVideo = true;
                    break;
                }
            }
            return hasVideo;
        } else {
            return getExtendInfo().isLocalUploadVideo && hasLocalUploadVideo();
        }
    }

    /**
     * 是否包含创意视频
     * @return
     */
    public boolean hasMvVideo(){
        if (shortVideoList != null) {
            boolean hasVideo = false;
            for (ShortVideoEntity videoEntity : shortVideoList) {
                if (videoEntity != null && videoEntity.hasMvVideo()) {
                    hasVideo = true;
                    break;
                }
            }
            return hasVideo;
        }else {
            return false;
        }
    }

    /**
     * 是否包含自己录制的创意视频
     * @return
     */
    public boolean hasRecordMvVideo(){
        if (shortVideoList != null) {
            boolean hasVideo = false;
            for (ShortVideoEntity videoEntity : shortVideoList) {
                if (videoEntity != null && videoEntity.hasRecordMV()) {//录制的视频
                    hasVideo = true;
                    break;
                }
            }
            return hasVideo;
        }else {
            return false;
        }
    }

    public boolean hasLocalUploadVideo() {
        if (getExtendInfo().isLocalUploadVideo && !TextUtils.isEmpty(combineVideo)) {
            File file = new File(combineVideo);
            return file.exists() && file.isFile();
        }
        return false;
    }

    public boolean hasJoinVideo() {
        if (shortVideoList != null) {
            boolean hasJoin = false;
            for (ShortVideoEntity videoEntity : shortVideoList) {
                if (videoEntity != null && videoEntity.hasVideo() && videoEntity.originalId != 0) {
                    hasJoin = true;
                    break;
                }
            }
            return hasJoin;
        } else {
            return false;
        }
    }

    public boolean hasEdited() {
        if (hasVideo()) {
            if (shortVideoList != null) {
                for (ShortVideoEntity videoEntity : shortVideoList) {
                    if (videoEntity != null && videoEntity.hasEdited) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isReachMin() {
        if (shortVideoList != null && shortVideoList.size() > 0 && hasVideo()) {
            boolean isOk = true;
            for (ShortVideoEntity videoEntity : shortVideoList) {
                if (videoEntity != null) {
                    if (videoEntity.getDuring() == 0) {
                        continue;
                    }
                    KLog.i("videoEntity--getDuring" + videoEntity.getDuring());
                    if (videoEntity.getDuring() < RecordManager.get().getSetting().getMinDuration()) {
                        isOk = false;
                        break;
                    }
                }
            }
            return isOk;
        }
        return false;
    }

    public ProductExtendEntity getExtendInfo() {
        if (extendInfo == null) {
            extendInfo = new ProductExtendEntity();
        }
        return extendInfo;
    }

    public TopicInfoEntity getTopicInfo() {
        if (topicInfo == null) {
            topicInfo = new TopicInfoEntity();
        }
        return topicInfo;
    }

    public boolean isLocalUploadVideo() {
        return getExtendInfo().isLocalUploadVideo;
    }

    /**
     * 获取导出作品的宽和高
     *
     * @return
     */
    public int[] getProductWH() {
        int[] wh = new int[2];
        if (!TextUtils.isEmpty(combineVideo)) {
            File file = new File(combineVideo);
            if (file.exists() && !file.isDirectory()) {
                MVideoConfig videoConfig = new MVideoConfig();
                VideoEngine.getMediaInfo(combineVideo, videoConfig);
                wh[0] = videoConfig.getVideoWidth();
                wh[1] = videoConfig.getVideoHeight();
                getExtendInfo().videoWidth = wh[0];
                getExtendInfo().videoHeight = wh[1];
            }
        }
        return wh;
    }

    /**
     * 获取期望导出的视频的宽高，只对本地上传发布单个视频有效
     *
     * @return
     */
    public int[] getExceptWH() {
        int[] wh = new int[2];
        if (extendInfo == null) {
            extendInfo = new ProductExtendEntity();
        }
        wh[0] = extendInfo.videoWidth;
        wh[1] = extendInfo.videoHeight;
        return wh;
    }

    public float getExceptRatio() {
        if (extendInfo != null && extendInfo.videoWidth > 0 && extendInfo.videoHeight > 0) {
            return extendInfo.videoWidth * 1.0f / extendInfo.videoHeight;
        }
        return 1f;
    }

    public String getWebpPath() {
        return this.webpPath;
    }

    public void setWebpPath(String webpPath) {
        this.webpPath = webpPath;
    }

    public String getCoverPath() {
        return this.coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public long getOriginalId() {
        return this.originalId;
    }

    public void setOriginalId(long originalId) {
        this.originalId = originalId;
    }

    @Override
    public String toString() {
        return "ProductEntity{" +
                "id=" + id +
                ", combineVideo='" + combineVideo + '\'' +
                ", modifyTime=" + modifyTime +
                ", baseDir='" + baseDir + '\'' +
                ", webpPath='" + webpPath + '\'' +
                ", coverPath='" + coverPath + '\'' +
                ", productType=" + productType +
                ", musicMixFactor=" + musicMixFactor +
                ", originalMixFactor=" + originalMixFactor +
                ", originalId=" + originalId +
                ", userId=" + userId +
                ", shortVideoList=" + CommonUtils.printList(shortVideoList) +
                ", musicInfo=" + (musicInfo == null ? "null" : musicInfo.toString()) +
                ", topicInfo=" + (topicInfo == null ? "null" : topicInfo.toString()) +
                ", frameInfo=" + (frameInfo == null ? "null" : frameInfo.toString()) +
                ", extendInfo=" + (extendInfo == null ? "null" : extendInfo.toString()) +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ProductEntity infoEntity = null;
        try {
            infoEntity = (ProductEntity) super.clone();
            if (infoEntity != null) {
                if (shortVideoList != null) {
                    infoEntity.shortVideoList = CommonUtils.cloneList(shortVideoList);
                }
                if (musicInfo != null) {
                    infoEntity.musicInfo = (MusicInfoEntity) musicInfo.clone();
                }
                if (topicInfo != null) {
                    infoEntity.topicInfo = (TopicInfoEntity) topicInfo.clone();
                }
                if (frameInfo != null) {
                    infoEntity.frameInfo = frameInfo.deepClone();
                }
                if (extendInfo != null) {
                    infoEntity.extendInfo = (ProductExtendEntity) extendInfo.clone();
                }
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return infoEntity;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getUserId() {
        return this.userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getOriginalShowMixFactor() {
        return this.originalShowMixFactor;
    }

    public void setOriginalShowMixFactor(int originalShowMixFactor) {
        this.originalShowMixFactor = originalShowMixFactor;
    }

    public boolean isSingleFrame() {
        return shortVideoList != null && shortVideoList.size() == 1;
    }

    public String getCombineAudio() {
        return this.combineAudio;
    }

    public void setCombineAudio(String combineAudio) {
        this.combineAudio = combineAudio;
    }

    public String getCombineVideoAudio() {
        return this.combineVideoAudio;
    }

    public void setCombineVideoAudio(String combineVideoAudio) {
        this.combineVideoAudio = combineVideoAudio;
    }

}
