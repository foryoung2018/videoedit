package com.wmlive.hhvideo.heihei.record.engine;

import android.graphics.Bitmap;

import com.wmlive.hhvideo.heihei.beans.frame.FrameInfo;
import com.wmlive.hhvideo.heihei.record.engine.model.MScene;
import com.wmlive.hhvideo.heihei.record.engine.model.MediaObject;
import com.wmlive.hhvideo.heihei.record.engine.model.Scene;
import com.wmlive.hhvideo.heihei.record.widget.CustomFrameView;
import com.wmlive.hhvideo.heihei.record.widget.SmallRecordView;

import java.util.List;

public interface PlayerContentImpl {

//    public List<MediaObject> createMediaObject(boolean combineAll, int currentPreviewIndex, List<SmallRecordView> smallRecordViewList,
//                                               FrameInfo mFrameInfo, CustomFrameView customFrameView);

    public List<? extends Scene> createScenes(List<? extends MediaObject> assets);

    public void setScenceAndPrepare(List<Scene> scenes);

    public void setMediaAndPrepare(List<? extends MediaObject> list);

    /**
     *
     * @param scene
     */
    public void addScene(Scene scene);

    public Scene createScene();

    /**
     * 清除滤镜
     */
    public void clearEffects();

    public void setFilterType(int filterType);

    public void setOriginalMixFactor(int value);

    /**
     * 生成
     * @param time  毫秒
     * @param coverPath
     * @return 是否生成截图成功
     */
    public boolean getSnapShot(long time,String coverPath);

    /**
     * 毫秒
     * @param time
     * @return
     */
    public Bitmap getSnapShot(long time);

    public Bitmap getSnapShot(long time,int width,int height) ;

}
