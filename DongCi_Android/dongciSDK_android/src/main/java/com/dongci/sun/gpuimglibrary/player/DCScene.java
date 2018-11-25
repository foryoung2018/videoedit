package com.dongci.sun.gpuimglibrary.player;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangxiao on 2018/6/6.
 *
 */

public class DCScene {

    // asset type
    public static final int DCAssetTypeVideo = 0;
    public static final int DCAssetTypeAudio = 1;
    public static final int DCAssetTypeImage = 2;
    @IntDef({DCAssetTypeVideo, DCAssetTypeAudio, DCAssetTypeImage})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DCAssetType {
    }

    public List<DCAsset> assets;
    public DCTransition transition;

    public DCScene() {
        assets = new ArrayList<>();
    }
}
