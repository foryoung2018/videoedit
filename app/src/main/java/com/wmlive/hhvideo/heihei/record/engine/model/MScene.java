package com.wmlive.hhvideo.heihei.record.engine.model;

import com.dongci.sun.gpuimglibrary.player.DCScene;

/**
 * Scence 转换成 scScence
 */
public class MScene extends Scene<MAsset> {

    DCScene dcScene;

    public DCScene getDCScene(){
        dcScene = new DCScene();
        for(MAsset asset:assets){
            dcScene.assets.add(asset.getAsset());
        }
        return dcScene;
    }

    /**
     * 添加播放资源
     * @param mediaPath
     */
    public void addMedia(String mediaPath){
        MAsset asset = new MAsset(mediaPath);
        assets.add(asset);
    }
}
