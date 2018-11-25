package com.wmlive.hhvideo.utils;

import android.content.res.AssetManager;
import android.text.TextUtils;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.heihei.beans.frame.FrameInfo;
import com.wmlive.hhvideo.heihei.beans.frame.FrameSortBean;
import com.wmlive.hhvideo.heihei.beans.frame.Frames;
import com.wmlive.hhvideo.utils.preferences.SPUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wenlu on 2017/9/26.
 * 画框工具类
 */
public class FrameUtils {

    private boolean hasInit = false;
    private List<FrameInfo> frameList;

    private List<FrameSortBean> layoutList; // 所有比例画框的集合的集合


    private static final class Instance {
        static final FrameUtils INSTANCE = new FrameUtils();
    }

    public static FrameUtils ins() {
        return Instance.INSTANCE;
    }

    private FrameUtils() {
    }

    public void init() {
        if (hasInit && frameList != null) {
            return;
        }
        Observable.just(1)
                .subscribeOn(Schedulers.io())
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        return SPUtils.getString(DCApplication.getDCApp(), SPUtils.FRAME_LAYOUT_DATA, "");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String frames) throws Exception {
                        Frames f = JsonUtils.parseObject(frames, Frames.class);
                        if (f != null) {
                            layoutList = f.layouts;
                            hasInit = true;
                            KLog.i("hsing", "使用网络画框数据");
                        } else {
                            getLocalFrameLayouts();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        getLocalFrameLayouts();
                    }
                });
    }

    /**
     * 获取本地framelayout数据
     */
    private void getLocalFrameLayouts() {
        Observable.just(1)
                .subscribeOn(Schedulers.io())
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        try {
                        AssetManager assetManager = DCApplication.getDCApp().getAssets();
                            InputStream is = assetManager.open("listView.json");
                            BufferedReader br = new BufferedReader(new InputStreamReader(is));
                            StringBuffer stringBuffer = new StringBuffer();
                            String str = null;
                            while ((str = br.readLine()) != null) {
                                stringBuffer.append(str);
                            }
                            return stringBuffer.toString();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String frames) throws Exception {
                        Frames f = JsonUtils.parseObject(frames, Frames.class);
                        if (f != null) {
                            layoutList = f.layouts;
                            hasInit = true;
                            KLog.i("hsing", "使用本地画框数据");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    public List<FrameSortBean> getFrameList() {
        if (!hasInit || frameList == null) {
            init();
        }
        return layoutList;
    }

    public FrameInfo getFrameInfo(String frameLayout) {
        if (layoutList!=null){
            int size = layoutList.size();
            for(FrameSortBean bean:layoutList){
                List<FrameInfo> layout = bean.layout;
                for (FrameInfo info:layout){
                    if (info.name.equalsIgnoreCase(frameLayout)){
                        return info;
                    }
                }
            }
            return frameList==null?null:(frameList.size()>0?frameList.get(0):null);
        } else {
            init();
        }



//        if (frameList != null) {
//            int size = frameList.size();
//            for (int i = 0; i < size; i++) {
//                FrameInfo frame = frameList.get(i);
//                if (frame.name.equalsIgnoreCase(frameLayout)) {
//                    return frame;
//                }
//            }
//            return frameList.get(0);
//        } else {
//            init();
//        }
        return null;
    }

    /**
     * 是否有画框
     * @param frameLayout
     * @return
     */
    public boolean hasFrameInfo(String frameLayout) {
        return !TextUtils.isEmpty(frameLayout) && getFrameInfo(frameLayout) != null;
    }

    /**
     * 是否是单格视频
     * @param frameLayout
     * @return
     */
    public boolean isSingleFrame(String frameLayout) {
        FrameInfo frameInfo = getFrameInfo(frameLayout);
        if (frameInfo != null) {
            return frameInfo.video_count == 1;
        } else {
            return false;
        }
    }
}