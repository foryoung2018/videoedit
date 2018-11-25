package com.wmlive.hhvideo.heihei.record.presenter;

import android.content.res.AssetManager;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.frame.FrameInfo;
import com.wmlive.hhvideo.heihei.beans.frame.FrameSortBean;
import com.wmlive.hhvideo.heihei.beans.frame.Frames;
import com.wmlive.hhvideo.heihei.beans.opus.OpusMaterialEntity;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.utils.JsonUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.preferences.SPUtils;
import com.wmlive.networklib.observer.DCNetObserver;

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
 * Created by hsing on 2017/12/5.
 */

public class RecordPresenter extends BasePresenter<RecordPresenter.IRecordView> {

    private List<FrameSortBean> layoutList;

    public RecordPresenter(IRecordView view) {
        super(view);
    }

    public interface IRecordView extends BaseView {
        void onGetFrameInfo(boolean result, boolean isNetwork);

        void onGetMaterial(OpusMaterialEntity response);
    }

    /**
     * 获取作品素材列表
     *
     * @param opusId
     */
    public void getOpusMaterial(long opusId) {
        executeRequest(HttpConstant.TYPE_GET_MATERIAL, getHttpApi().getOpusMaterial(InitCatchData.getOpusMaterial(), opusId))
                .subscribe(new DCNetObserver<OpusMaterialEntity>() {

                    @Override
                    public void onRequestDataReady(int requestCode, String message, OpusMaterialEntity response) {
                        if (viewCallback != null) {
                            viewCallback.onGetMaterial(response);
                        }
                    }

                    @Override
                    public void onRequestDataError(int requestCode, int serverCode, String message) {
                        onRequestError(HttpConstant.TYPE_GET_MATERIAL, message);
                    }
                });
    }

    public void getFrameList(String frameLayout) {
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
                            FrameInfo mFrameInfo = getFrameInfo(frameLayout);
                            RecordManager.get().newProductEntity(mFrameInfo);
                            KLog.i("hsing", "使用网络画框数据");
                            if (viewCallback != null) {
                                viewCallback.onGetFrameInfo(true, true);
                            }
                        } else {
                            if (viewCallback != null) {
                                viewCallback.onGetFrameInfo(false, false);
                            }
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        getLocalFrameLayouts(frameLayout);
                    }
                });
    }

    /**
     * 获取本地framelayout数据
     *
     * @param frameLayout
     */
    private void getLocalFrameLayouts(String frameLayout) {
        Observable.just(1)
                .subscribeOn(Schedulers.io())
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        AssetManager assetManager = DCApplication.getDCApp().getAssets();
                        try {
                            InputStream is = assetManager.open("listView2.json");
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
                            FrameInfo mFrameInfo = getFrameInfo(frameLayout);
                            RecordManager.get().newProductEntity(mFrameInfo);
                            KLog.i("hsing", "使用本地画框数据");
                            if (viewCallback != null) {
                                viewCallback.onGetFrameInfo(true, false);
                            }
                        } else {
                            if (viewCallback != null) {
                                viewCallback.onGetFrameInfo(false, false);
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        if (viewCallback != null) {
                            viewCallback.onGetFrameInfo(false, false);
                        }
                    }
                });
    }

//    public FrameInfo getFrameInfo(String frameLayout) {
//        int size = frameList.size();
//        for (int i = 0; i < size; i++) {
//            FrameInfo frame = frameList.get(i);
//            if (frame.name.equalsIgnoreCase(frameLayout)) {
//                return frame;
//            }
//        }
//        return frameList.get(0);
//    }
    public FrameInfo getFrameInfo(String frameLayout) {
        if (layoutList != null) {
            for (FrameSortBean bean : layoutList) {
                List<FrameInfo> layout = bean.layout;
                for (FrameInfo info : layout) {
                    if (info.name.equalsIgnoreCase(frameLayout)) {
                        return info;
                    }
                }
            }
        }
        return layoutList.get(0).layout.get(0);
    }

}
