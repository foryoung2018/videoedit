package com.wmlive.hhvideo.heihei.quickcreative;

import com.wmlive.hhvideo.utils.KLog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 录制 线程
 */
public class RecordMvThreadManager {


    Map<Integer,RecordMvThreadManagerListener> Listeners;

    VoiceBeatManager voiceBeatManager;

    MvMaterialManager mvMaterialManager;

    public void init(VoiceBeatManager voiceBeatManager,MvMaterialManager mvMaterialManager){
        this.voiceBeatManager = voiceBeatManager;
        this.mvMaterialManager = mvMaterialManager;
        Listeners = new HashMap <Integer,RecordMvThreadManagerListener>();
    }

    public boolean hasProcessing(){
        return mvMaterialManager.hasProcessing() || voiceBeatManager.hasProcessing();
    }

    /**
     * 给事件添加回调。
     */
    public void addListener(RecordMvThreadManagerListener mvThreadManagerListener1 ){
        if(mvMaterialManager.hasProcessing()){//视频变成图片
            Listeners.put(0,mvThreadManagerListener1);
            KLog.i("RecordMvThreadManager==add-mvMaterialManager>"+Listeners.size());
            mvMaterialManager.setVideoListener(new MvMaterialManager.Video2ImgListener() {
                @Override
                public void onProgress(int index, int progress) {

                }

                @Override
                public void onFinish(int code) {
                    KLog.i("RecordMvThreadManager==onFinish-mvMaterialManager>"+Listeners.size());
                    Listeners.remove(0);
                    if(Listeners.size()==0){
                        mvThreadManagerListener1.onFinishAll();
                    }else if(Listeners.size()==1){
                        mvThreadManagerListener1.onFinish(code);
                    }
                }
            });
        }
        if(voiceBeatManager.hasProcessing()){//处理声音
            Listeners.put(1,mvThreadManagerListener1);
            KLog.i("RecordMvThreadManager==add-voiceBeatManager>"+Listeners.size());
            voiceBeatManager.setVideoListener(new VoiceBeatManager.VideoBeatListener() {
                @Override
                public void onProgress(int index, int progress) {

                }

                @Override
                public void onFinish(int code) {
                    KLog.i("RecordMvThreadManager==onFinish-voiceBeatManager>"+Listeners.size());
                    Listeners.remove(1);
                    if(Listeners.size()==0){
                        mvThreadManagerListener1.onFinishAll();
                    }else {
                        mvThreadManagerListener1.onFinish(code);
                    }
                }
            });
        }
    }

    public interface RecordMvThreadManagerListener{
        public void onProgress(int index, int progress);
        public void onFinish(int code);
        public void onFinishAll();
    }

}
