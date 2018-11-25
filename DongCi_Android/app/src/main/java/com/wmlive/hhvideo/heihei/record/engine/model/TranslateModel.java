package com.wmlive.hhvideo.heihei.record.engine.model;

import android.media.MediaScannerConnection;

import com.dongci.sun.gpuimglibrary.player.DCScene;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据类型转换
 */
public class TranslateModel {

    public static List<DCScene> scenceToDC(List<Scene> list){
        List<DCScene> dcScenes = new ArrayList<DCScene>();
        for(Scene scene:list){
            MScene mScene = new MScene();
            mScene.assets = scene.assets;
            dcScenes.add(mScene.getDCScene());
        }
        return dcScenes;
    }

    public static List<DCScene> mscenceToDC(List<MScene> list){
        List<DCScene> dcScenes = new ArrayList<DCScene>();
        for(MScene scene:list){
            dcScenes.add((scene).getDCScene());
        }
        return dcScenes;
    }

    public static List<Scene> strToScence(String path){
        List<Scene> list = new ArrayList<Scene>(1);
        Scene<MAsset> scene = new Scene<MAsset>();
        MAsset asset = new MAsset(path);
        List<MAsset> assets = new ArrayList<MAsset>();
        assets.add(asset);
        scene.assets = assets;
        list.add(scene);
        return list;
    }

    public static List<MScene> scenceToM(List<Scene> list){
        List<MScene> mScenes = new ArrayList<MScene>(list.size());
        if(list!=null && list.size()>0){
            for(Scene scene:list){
                mScenes.add((MScene) scene);
            }
        }
        return mScenes;
    }


}
