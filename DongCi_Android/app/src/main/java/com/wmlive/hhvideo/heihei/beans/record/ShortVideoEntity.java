package com.wmlive.hhvideo.heihei.beans.record;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

//import com.rd.vecore.Music;
//import com.rd.vecore.VirtualVideo;
//import com.rd.vecore.models.VideoConfig;
import com.wmlive.hhvideo.heihei.discovery.DiscoveryUtil;
import com.wmlive.hhvideo.heihei.record.config.RecordSettingSDK;
import com.wmlive.hhvideo.heihei.record.engine.VideoEngine;
import com.wmlive.hhvideo.heihei.record.engine.config.MVideoConfig;
import com.wmlive.hhvideo.heihei.record.engine.constant.SdkConstant;
import com.wmlive.hhvideo.heihei.record.engine.utils.VideoUtils;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.manager.RecordSetting;
import com.wmlive.hhvideo.heihei.record.manager.RecordSpeed;
import com.wmlive.hhvideo.heihei.record.utils.RecordFileUtil;
import com.wmlive.hhvideo.utils.CommonUtils;
import com.wmlive.hhvideo.utils.FileUtil;
import com.wmlive.hhvideo.utils.KLog;

import org.greenrobot.greendao.annotation.Transient;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lsq on 8/30/2017.
 * 单个视频的实体类
 */

public class ShortVideoEntity extends CloneableEntity implements Parcelable {

    public long originalId;//如果是自己录制的，则为0，如果是共同创作的是之前服务器生成的id
    private int currentSpeedIndex = RecordSpeed.NORMAL.ordinal();
    public String baseDir;
    public String editingVideoPath;
    public String importVideoPath;//本地导入的路径或者共同创作的路径，备份用，不做编辑操作
    public String editingReverseVideoPath;
    private List<ClipVideoEntity> clipList;
    private List<EffectEntity> effectList;
    private boolean isImport = false; //  是否导入视频
    private boolean useOriginalAudio = true; //  是否使用原音
    private int originalMixFactor = RecordSetting.MAX_VOLUME / 2;
    private int filterId;//当前滤镜的类型
    public boolean hasEdited;//被编辑过
    private boolean needJoin = true;//需要重新生成editingVideoPath
    public ExtendEntity extendInfo = new ExtendEntity();
    private boolean needExport; // 需要重新导出
    public float trimStart; // 截取开始时间
    public float trimEnd = 0; // 截取结束时间
    //    public transient List<Music> originalMusicList; // 原声对象
    public int quality = 1; // 上传素材质量 1 低 2 高
    /**
     * 当前作品的声音
     */
    public float volume = 1f;
    public String coverUrl;//素材的网络封面
    private float during = 0;
    /**
     * 当前对象的音频路径
     */
    public String editingAudioPath;
    /**
     * 组合后的视频 音频
     */
    public String combineVideoAudio;

    public void setCoverUrl(String path) {
        coverUrl = path;
    }

    public String getCoverUrl() {
        return coverUrl;
    }


    public boolean isImport() {
        return isImport;
    }

    public boolean canContinueRecord() {
        return !isImport && !reachMax();
    }

    public void setImport(boolean anImport) {
        isImport = anImport;
    }

    public void setImport(boolean anImport, boolean hasEdit) {
        hasEdited = hasEdit;
        isImport = anImport;
    }

    public String getVideoType() {
        if (extendInfo != null) {
            if (TextUtils.isEmpty(extendInfo.extendName)) {
                extendInfo.extendName = "2";
            }
            return extendInfo.extendName;
        }
        return "2";
    }

    public void setVideoType(String videoType) {
        if (extendInfo == null) {
            extendInfo = new ExtendEntity();
        }
        extendInfo.extendName = videoType;
    }

    public void setImgs(List<String> imgs) {
        if (extendInfo == null) {
            extendInfo = new ExtendEntity();
        }
        extendInfo.videoImgs = imgs;
    }

    public boolean isNeedExport() {
        return needExport;
    }

    public void setNeedExport(boolean needExport) {
        this.needExport = needExport;
    }

    public float[] getTrimRange() {
        return new float[]{trimStart, trimEnd};
    }

    /**
     * 设置截取的时间
     *
     * @param trimStart
     * @param trimEnd
     */
    public void setTrimRange(float trimStart, float trimEnd) {
        if (trimStart >= 0 && trimEnd >= 0 && (trimEnd > trimStart)) {
            this.trimStart = trimStart;
            this.trimEnd = Math.min(RecordSettingSDK.MAX_VIDEO_DURATION * 1000 + this.trimStart, trimEnd);
        }
    }

    public void setCurrentSpeedIndex(int index) {
        currentSpeedIndex = index;
    }

    public boolean needJoin() {
        return needJoin;
    }

    public void setNeedJoin(boolean needJoin) {
        this.needJoin = needJoin;
    }

    public int getCurrentSpeedIndex() {
        return currentSpeedIndex;
    }

    public boolean isUseOriginalAudio() {
        return useOriginalAudio;
    }

    public int getOriginalMixFactor() {
        return originalMixFactor;
    }

    public void setOriginalMixFactor(int originalMixFactor) {
        this.originalMixFactor = originalMixFactor;
    }

    public void setUseOriginalAudio(boolean useOriginalAudio) {
        this.useOriginalAudio = useOriginalAudio;
    }

    public void setFilterId(int type) {
        if (type != filterId) {
            filterId = type;
        }
    }

    public int getFilterId() {
        return filterId;
    }

    public float getDuring() {
        if (isImport) {
            if (during <= 0 && !TextUtils.isEmpty(editingVideoPath)) {
                if (importVideoPath == null) {
                    during = VideoUtils.getVideoLength(editingVideoPath) * 1f / 1000000f;
                } else {
                    during = VideoUtils.getVideoLength(importVideoPath) * 1f / 1000000f;
                }
                if (during < 6f) {
                    during = Math.round(during);
                }
                KLog.i("clicpDuring---import>" + during);
//                MVideoConfig videoConfig = new MVideoConfig();
//                VideoEngine.getMediaInfo(editingVideoPath, videoConfig);
//                during = videoConfig.getVideoDuration();
            }
        } else {
            during = 0;
            if (clipList != null && clipList.size() > 0) {
                for (ClipVideoEntity clipVideoEntity : clipList) {
                    during += clipVideoEntity.getDuring();
                    KLog.i("clicpDuring--->" + during);
                }
            }
        }
        return during > 0f ? during : 0f;
    }

    public boolean reachMin() {
        return getDuring() >= RecordManager.get().getSetting().getMinDuration();
    }

    public boolean reachMax() {
        return getDuring() >= RecordManager.get().getSetting().getMaxDuration();
    }

    public int getDuringMS() {
        return (int) (getDuring() * 1000);
    }

    public String getDuringString() {
        return DiscoveryUtil.convertTime((int) getDuring());
    }

    public long getEditingDuringMs() {
        if (!TextUtils.isEmpty(editingVideoPath) && new File(editingVideoPath).exists()) {
//            long d = (long) VirtualVideo.getMediaInfo(editingVideoPath, new VideoConfig());
            //sun  单位
            long d = VideoUtils.getVideoLength(editingVideoPath) / 1000;
            return (d > 0 ? d : 0);
        }
        return 0;
    }

    public List<ClipVideoEntity> getClipList() {
        if (clipList == null) {
            clipList = new ArrayList<>();
        }
        return clipList;
    }

    public int getClipVideoSize() {
        return getClipList().size();
    }

    public float getClipVideoDuring(int index) {
        if (index >= getClipList().size()) {
            index = 0;
        }
        return getClipList().get(index).getDuring();
    }

    public void addClipVideo(ClipVideoEntity clipVideoEntity) {
        if (clipVideoEntity != null) {
            getClipList().add(clipVideoEntity);
            hasEdited = true;
        }
    }

    public void deleteAllClip() {
        Iterator<ClipVideoEntity> iterator = clipList.iterator();
        ClipVideoEntity entity;
        while (iterator.hasNext()) {
            entity = iterator.next();
            if (entity != null) {
                entity.delete();
                iterator.remove();
            }
        }
    }

    public boolean hasClipVideo() {
        return getClipList().size() > 0;
    }

    /**
     * 删除所有 的编辑视频
     */
    public void deleteEditingFile() {
        RecordFileUtil.deleteFiles(editingVideoPath);
        if (combineVideoAudio != null)
            RecordFileUtil.deleteFiles(combineVideoAudio);
        if (editingAudioPath != null)
            RecordFileUtil.deleteFiles(editingAudioPath);
        combineVideoAudio = null;
        editingVideoPath = null;
        editingAudioPath = null;
    }

    public void deleteMvFile(){
        deleteEditingFile();
        //删除的图片
        File file = new File(baseDir+ File.separator+"img");
        file.deleteOnExit();

    }

    public boolean hasEditingFile() {
        if (!TextUtils.isEmpty(editingVideoPath)) {
            File file = new File(editingVideoPath);
            return file.exists() && !file.isDirectory();
        }
        return false;
    }

    /**
     * 是否有视频
     *
     * @return
     */
    public boolean hasVideo() {
        if (isImport) {
            return (!TextUtils.isEmpty(combineVideoAudio) && new File(combineVideoAudio).exists() || (!TextUtils.isEmpty(editingVideoPath) && new File(editingVideoPath).exists()));
        } else {
            return hasClipVideo();
        }
    }

    /**
     * 是否包含原创mv
     * @return
     */
    public boolean hasRecordMV(){
        return (!TextUtils.isEmpty(editingVideoPath) && new File(editingVideoPath).exists()) && (TextUtils.isEmpty(combineVideoAudio));
    }

    public boolean hasMvVideo() {
        return (!TextUtils.isEmpty(editingVideoPath) && new File(editingVideoPath).exists()) || (!TextUtils.isEmpty(combineVideoAudio) && new File(combineVideoAudio).exists());
    }

    public boolean hasEditingVideo() {
        return !TextUtils.isEmpty(editingVideoPath) && new File(editingVideoPath).exists();
    }

    public boolean hasCombineVideo(){
        return (!TextUtils.isEmpty(combineVideoAudio) && new File(combineVideoAudio).exists());
    }

    public long getEditingFileLength() {
        if (hasEditingVideo()) {
            return new File(editingVideoPath).length();
        }
        return 0;
    }

    public boolean hasEffectAndFilter() {
        return effectList != null && effectList.size() > 0;
    }

    public void deleteEffect() {
        if (effectList != null) {
            if (effectList.size() > 0) {
                hasEdited = true;
            }
            effectList.clear();
        }
        setFilterId(RecordSetting.FILTER_LIST.get(0).filterId);
    }

    /**
     * 删除最后一个视频片段
     *
     * @return
     */
    public boolean deleteLastClipVideo() {
        int index = (getClipList().size() - 1);
        if (hasClipVideo()) {
            ClipVideoEntity entity = getClipList().get(index);
            if (entity != null) {
                if (entity.delete()) {
                    deleteEditingVideo();
                    getClipList().remove(index);
                    KLog.i("====删除ClipVideo文件，index:" + index);
                    hasEdited = true;
                } else {
                    return false;
                }
                return true;
            } else {
                hasEdited = true;
                getClipList().remove(index);
                deleteEditingVideo();
            }
        } else {
            deleteEditingVideo();
        }
        return true;
    }

    private void deleteEditingVideo() {
        if (!TextUtils.isEmpty(editingVideoPath)) {
            File file = new File(editingVideoPath);
            if (file.exists() && !file.isDirectory()) {
                file.delete();
            }
        }
        if (!TextUtils.isEmpty(editingReverseVideoPath)) {
            File file = new File(editingReverseVideoPath);
            if (file.exists() && !file.isDirectory()) {
                file.delete();
            }
        }
        editingVideoPath = null;
        editingReverseVideoPath = null;
        KLog.i("====删除EditingVideo文件");
    }

    public void setClipList(List<ClipVideoEntity> clipList) {
        this.clipList = clipList;
    }

    public List<EffectEntity> getEffectList() {
        if (effectList == null) {
            effectList = new ArrayList<>();
        }
        return effectList;
    }

    public void setEffectList(List<EffectEntity> effectList) {
        this.effectList = effectList;
    }


    public ShortVideoEntity() {
        effectList = new ArrayList<>();
        clipList = new ArrayList<>();
//        originalMusicList = new ArrayList<>();
    }


    @Override
    public String toString() {
        return "ShortVideoEntity{" +
                "originalId=" + originalId +
                ", currentSpeedIndex=" + currentSpeedIndex +
                ", baseDir='" + baseDir + '\'' +
                ", editingVideoPath='" + editingVideoPath + '\'' +
                ", importVideoPath='" + importVideoPath + '\'' +
                ", editingReverseVideoPath='" + editingReverseVideoPath + '\'' +
                ", clipList=" + clipList +
                ", effectList=" + effectList +
                ", isImport=" + isImport +
                ", useOriginalAudio=" + useOriginalAudio +
                ", originalMixFactor=" + originalMixFactor +
                ", filterId=" + filterId +
                ", hasEdited=" + hasEdited +
                ", needJoin=" + needJoin +
                ", extendInfo=" + extendInfo +
                ", needExport=" + needExport +
                ", trimStart=" + trimStart +
                ", trimEnd=" + trimEnd +
                ", quality=" + quality +
                ", volume=" + volume +
                ", coverUrl='" + coverUrl + '\'' +
                ", during=" + during +
                ", editingAudioPath='" + editingAudioPath + '\'' +
                ", combineVideoAudio='" + combineVideoAudio + '\'' +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ShortVideoEntity videoEntity = null;
        try {
            videoEntity = (ShortVideoEntity) super.clone();
            if (videoEntity != null) {
                if (clipList != null) {
                    videoEntity.clipList = CommonUtils.cloneList(clipList);
                }

                if (effectList != null) {
                    videoEntity.effectList = CommonUtils.cloneList(effectList);
                }
                if (extendInfo != null) {
                    videoEntity.extendInfo = (ExtendEntity) extendInfo.clone();
                }
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return videoEntity;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.originalId);
        dest.writeInt(this.currentSpeedIndex);
        dest.writeString(this.baseDir);
        dest.writeString(this.editingVideoPath);
        dest.writeString(this.importVideoPath);
        dest.writeString(this.editingReverseVideoPath);
        dest.writeTypedList(this.clipList);
        dest.writeTypedList(this.effectList);
        dest.writeByte(this.isImport ? (byte) 1 : (byte) 0);
        dest.writeByte(this.useOriginalAudio ? (byte) 1 : (byte) 0);
        dest.writeInt(this.originalMixFactor);
        dest.writeInt(this.filterId);
        dest.writeByte(this.hasEdited ? (byte) 1 : (byte) 0);
        dest.writeByte(this.needJoin ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.extendInfo, flags);
        dest.writeByte(this.needExport ? (byte) 1 : (byte) 0);
        dest.writeFloat(this.trimStart);
        dest.writeFloat(this.trimEnd);
        dest.writeInt(this.quality);
        dest.writeFloat(this.during);
    }

    protected ShortVideoEntity(Parcel in) {
        this.originalId = in.readLong();
        this.currentSpeedIndex = in.readInt();
        this.baseDir = in.readString();
        this.editingVideoPath = in.readString();
        this.importVideoPath = in.readString();
        this.editingReverseVideoPath = in.readString();
        this.clipList = in.createTypedArrayList(ClipVideoEntity.CREATOR);
        this.effectList = in.createTypedArrayList(EffectEntity.CREATOR);
        this.isImport = in.readByte() != 0;
        this.useOriginalAudio = in.readByte() != 0;
        this.originalMixFactor = in.readInt();
        this.filterId = in.readInt();
        this.hasEdited = in.readByte() != 0;
        this.needJoin = in.readByte() != 0;
        this.extendInfo = in.readParcelable(ExtendEntity.class.getClassLoader());
        this.needExport = in.readByte() != 0;
        this.trimStart = in.readFloat();
        this.trimEnd = in.readFloat();
        this.quality = in.readInt();
        this.during = in.readFloat();
    }

    public static final Creator<ShortVideoEntity> CREATOR = new Creator<ShortVideoEntity>() {
        @Override
        public ShortVideoEntity createFromParcel(Parcel source) {
            return new ShortVideoEntity(source);
        }

        @Override
        public ShortVideoEntity[] newArray(int size) {
            return new ShortVideoEntity[size];
        }
    };
}
