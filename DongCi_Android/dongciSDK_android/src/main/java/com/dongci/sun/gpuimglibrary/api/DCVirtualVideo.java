package com.dongci.sun.gpuimglibrary.api;

import com.dongci.sun.gpuimglibrary.player.DCAsset;
import com.dongci.sun.gpuimglibrary.player.DCScene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 控制视频的添加层
 */
public class DCVirtualVideo {

    List<DCScene> scenes;

    private List<DCScene> createScene(List<DCAsset> assets) {
        DCScene scene = new DCScene();
        scene.assets = assets;
        scenes = Arrays.asList(scene);
        return scenes;
    }

    public void reset() {

    }

    public void clearEffects() {

    }

    public void build() {

    }

    public void addScence() {
        if (scenes == null)
            scenes = new ArrayList<>();
//        scenes.add
    }


    //        int width = 720;
//        int height = 1280;
//        final String path0 = path ;//Environment.getExternalStorageDirectory() + "/Movies/BuildAWeapon.mp4";
////        final String path1 = path ;//Environment.getExternalStorageDirectory() + "/Movies/BuildAWeapon.mp4";
////        final String path2 = path ;//Environment.getExternalStorageDirectory() + "/Movies/BuildAWeapon.mp4";
////        final String path3 = path ;//Environment.getExternalStorageDirectory() + "/Movies/BuildAWeapon.mp4";
////        final String path4 = path ;//Environment.getExternalStorageDirectory() + "/Movies/BuildAWeapon.mp4";
////        final String path5 = path ;//Environment.getExternalStorageDirectory() + "/Movies/BuildAWeapon.mp4";
//
//        //                 w
//        //      ------ ---- ---- ------
//        //     |      |    |    |      |
//        //     |   0  |    |    |   4  |
//        //   h  ------|  2 |  3 |------
//        //     |      |    |    |      |
//        //     |   1  |    |    |   5  |
//        //      ------ ---- ---- ------
//        Log.d("tag","path-->"+((new File(path0)).exists())+path0);
//        DCAsset asset0 = new DCAsset();
//        asset0.filePath = path0;
//        asset0.type = DCAsset.DCAssetTypeVideo;
//        asset0.fillType = DCAsset.DCAssetFillTypeScaleToFit;
//        asset0.cropRect = new RectF(0, 0, width, height);
//        asset0.rectInVideo = new RectF(0, 0.0f, 0.3f, 0.4f);
//        asset0.timeRange = new DCAsset.TimeRange(0L, 120000000L);
//        asset0.startTimeInScene = 0L;
//
//        DCAsset asset1 = new DCAsset();
//        asset1.filePath = path1;
//        asset1.type = DCAsset.DCAssetTypeVideo;
//        asset1.fillType = DCAsset.DCAssetFillTypeScaleToFit;
//        asset1.cropRect = new RectF(0, 0, width, height);
//        asset1.rectInVideo = new RectF(0.0f, 0.4f, 0.3f, 0.8f);
//        asset1.timeRange = new DCAsset.TimeRange(0L, 120000000L);
//        asset1.startTimeInScene = 0L;
//
//        DCAsset asset2 = new DCAsset();
//        asset2.filePath = path2;
//        asset2.type = DCAsset.DCAssetTypeVideo;
//        asset2.fillType = DCAsset.DCAssetFillTypeScaleToFit;
//        asset2.cropRect = new RectF(0, 0, width, height);
//        asset2.rectInVideo = new RectF(0.3f, 0.0f, 0.5f, 1.0f);
//        asset2.timeRange = new DCAsset.TimeRange(0L, 120000000L);
//        asset2.startTimeInScene = 0L;
//
//        DCAsset asset3 = new DCAsset();
//        asset3.filePath = path3;
//        asset3.type = DCAsset.DCAssetTypeVideo;
//        asset3.fillType = DCAsset.DCAssetFillTypeScaleToFit;
//        asset3.cropRect = new RectF(0, 0, width, height);
//        asset3.rectInVideo = new RectF(0.5f, 0, 0.7f, 1.0f);
//        asset3.timeRange = new DCAsset.TimeRange(0L, 120000000L);
//        asset3.startTimeInScene = 0L;
//
//        DCAsset asset4 = new DCAsset();
//        asset4.filePath = path4;
//        asset4.type = DCAsset.DCAssetTypeVideo;
//        asset4.fillType = DCAsset.DCAssetFillTypeScaleToFit;
//        asset4.cropRect = new RectF(0, 0, width, height);
//        asset4.rectInVideo = new RectF(0.7f, 0.0f, 1.0f, 0.5f);
//        asset4.timeRange = new DCAsset.TimeRange(0L, 120000000L);
//        asset4.startTimeInScene = 0L;
//
//        DCAsset asset5 = new DCAsset();
//        asset5.filePath = path5;
//        asset5.type = DCAsset.DCAssetTypeVideo;
//        asset5.fillType = DCAsset.DCAssetFillTypeScaleToFit;
//        asset5.cropRect = new RectF(0, 0, width, 720);
//        asset5.rectInVideo = new RectF(0.7f, 0.5f, 1.0f, 1.0f);
//        asset5.timeRange = new DCAsset.TimeRange(0L, 120000000L);
//        asset5.startTimeInScene = 0L;

//        List<DCAsset> assets = Arrays.asList(asset0,asset1,asset2,asset3, asset4, asset5);//, asset1, asset2, asset3, asset4, asset5

}
