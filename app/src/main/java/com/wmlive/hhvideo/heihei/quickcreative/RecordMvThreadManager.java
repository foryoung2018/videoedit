package com.wmlive.hhvideo.heihei.quickcreative;

import com.wmlive.hhvideo.utils.KLog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 录制 线程
 */
public class RecordMvThreadManager {


    Map<Integer, RecordMvThreadManagerListener> Listeners;

    VoiceBeatManager voiceBeatManager;

    MvMaterialManager mvMaterialManager;

    RecordMvThreadManagerListener mvThreadManagerListener1;

    public void init(VoiceBeatManager voiceBeatManager, MvMaterialManager mvMaterialManager) {
        this.voiceBeatManager = voiceBeatManager;
        this.mvMaterialManager = mvMaterialManager;
        Listeners = new HashMap<Integer, RecordMvThreadManagerListener>();
    }

    public boolean hasProcessing() {
        return mvMaterialManager.hasProcessing() || voiceBeatManager.hasProcessing();
    }

    /**
     * 给事件添加回调。
     */
    public void addListener(RecordMvThreadManagerListener mvThreadManagerListener1) {
        this.mvThreadManagerListener1 = mvThreadManagerListener1;
        if (Listeners == null)
            Listeners = new HashMap<Integer, RecordMvThreadManagerListener>();
        if (mvMaterialManager.hasProcessing()) {//视频变成图片
            Listeners.put(0, mvThreadManagerListener1);
            KLog.i("RecordMvThreadManager==add-mvMaterialManager>" + Listeners.size());
            mvMaterialManager.setVideoListener(new MvMaterialManager.Video2ImgListener() {
                @Override
                public void onProgress(int index, int progress) {

                }

                @Override
                public void onFinish(int code) {
                    KLog.i("RecordMvThreadManager==onFinish-mvMaterialManager-before>");
                    if(Listeners==null){
                        if(mvThreadManagerListener1!=null)
                            mvThreadManagerListener1.onFinishAll();
                        return;
                    }

                    Listeners.remove(0);
                    if (Listeners.size() == 0) {
                        if(mvThreadManagerListener1!=null)
                            mvThreadManagerListener1.onFinishAll();
                    } else if (Listeners.size() == 1) {
                        if(mvThreadManagerListener1!=null)
                            mvThreadManagerListener1.onFinish(code);
                    }
                }
            });
        }
        if (voiceBeatManager.hasProcessing()) {//处理声音
            Listeners.put(1, mvThreadManagerListener1);
            KLog.i("RecordMvThreadManager==add-voiceBeatManager>" + Listeners.size());
            voiceBeatManager.setVideoListener(new VoiceBeatManager.VideoBeatListener() {
                @Override
                public void onProgress(int index, int progress) {

                }

                @Override
                public void onFinish(int code) {
                    KLog.i("RecordMvThreadManager==onFinish-voiceBeatManager-before>");
                    if(Listeners==null){
                        if(mvThreadManagerListener1!=null)
                            mvThreadManagerListener1.onFinishAll();
                        return;
                    }
                    KLog.i("RecordMvThreadManager==onFinish-voiceBeatManager>" + Listeners.size());
                    Listeners.remove(1);
                    if (Listeners.size() == 0) {
                        if(mvThreadManagerListener1!=null)
                            mvThreadManagerListener1.onFinishAll();
                    } else {
                        if(mvThreadManagerListener1!=null)
                            mvThreadManagerListener1.onFinish(code);
                    }
                }
            });
        }
    }

    public void removeListener() {
        if(Listeners!=null){
            Listeners.remove(0);
            Listeners.remove(1);
            Listeners = null;
        }
        mvThreadManagerListener1 = null;
    }

    public interface RecordMvThreadManagerListener {
        public void onProgress(int index, int progress);

        public void onFinish(int code);

        public void onFinishAll();
    }

}
