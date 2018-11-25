package com.wmlive.hhvideo.heihei.record.engine.model;

import com.dongci.sun.gpuimglibrary.player.DCAsset;
import com.dongci.sun.gpuimglibrary.player.DCTransition;

import java.util.ArrayList;
import java.util.List;

public class Scene<T extends MediaObject> implements SceneImpl {

    public List<T> assets;
    public DCTransition transition;

    public Scene() {
        assets = new ArrayList<T>();
    }

    public void addMedia(T asset) {
        assets.add(asset);
    }

    public void setPermutationMode(int mode){

    }

    public long getDuration(){
        long duration = 0;
        for(MediaObject mediaObject:assets){
            if(mediaObject.getDuration()>duration){
                duration = mediaObject.getDuration();
            }
        }
        return duration;
    }
}
