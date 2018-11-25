package com.wmlive.hhvideo.heihei.record.activity;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.dongci.sun.gpuimglibrary.api.DCCameraConfig;
import com.dongci.sun.gpuimglibrary.api.DCVideoManager;
import com.dongci.sun.gpuimglibrary.api.listener.DCVideoListener;
import com.dongci.sun.gpuimglibrary.common.FileUtils;
import com.dongci.sun.gpuimglibrary.common.SLClipVideo;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.record.engine.VideoEngine;
import com.wmlive.hhvideo.heihei.record.engine.config.MVideoConfig;
import com.wmlive.hhvideo.heihei.record.engine.constant.SdkConstant;
import com.wmlive.hhvideo.heihei.record.engine.listener.ExportListener;
import com.wmlive.hhvideo.heihei.record.engine.listener.VideoListener;
import com.wmlive.hhvideo.heihei.record.engine.listener.VideosListener;
import com.wmlive.hhvideo.heihei.record.engine.utils.VideoUtils;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.utils.RecordFileUtil;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtilSdk;
import com.wmlive.hhvideo.heihei.record.widget.CircleProgressDialog;
import com.wmlive.hhvideo.heihei.record.widget.CustomFrameView;
import com.wmlive.hhvideo.heihei.record.widget.SysAlertDialog;
import com.wmlive.hhvideo.utils.FileUtil;
import com.wmlive.hhvideo.utils.KLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.wmlive.hhvideo.R;

import static com.wmlive.hhvideo.heihei.record.manager.RecordManager.PREFIX_EDITING_FILE;
import static com.wmlive.hhvideo.heihei.record.manager.RecordManager.SUFFIX_AUDIO_FILE;
import static com.wmlive.hhvideo.heihei.record.manager.RecordManager.SUFFIX_VIDEO_FILE;
import static com.wmlive.hhvideo.utils.ToastUtil.showToast;

/**
 * 对于录制好的视频进行处理逻辑
 */
public class RecordActivitySdkVideoHelper {

    private final String TAG = "RecordActivitySdkVideoHelper";

    RecordActivitySdk act;
    private CircleProgressDialog dialog;

    CustomFrameView customFrameView;
    /**
     * 已经组合的数据
     */
    List<String> compsedList;

    public RecordActivitySdkVideoHelper(RecordActivitySdk act) {
        this.act = act;
    }

    public RecordActivitySdk getAct(){
        if(act==null)
            act = RecordActivitySdk.mContext;
        return act;
    }


    /**
     * 1.拼接音频 视频
     *
     * @param index
     * @param listener
     */
    public void composeAndExportNew(int index, VideoListener listener) {

        ShortVideoEntity currentEntity = RecordManager.get().getShortVideoEntity(index);
        RecordUtilSdk.composeVideoAudio(currentEntity, new VideosListener() {
            @Override
            public void onStart() {
                listener.onStart();
            }

            @Override
            public void onProgress(int progress) {
                listener.onProgress(progress);
            }

            @Override
            public void onFinish(int code, String... outpath) {
                KLog.i(index + "composeAndExport--onFinish-->" + code + "patH:>" + outpath);
                if (code == SdkConstant.RESULT_SUCCESS) {
                    if (-1 < index && index < getAct().smallRecordViewList.size()) {
                        getAct().smallRecordViewList.get(index).showStatus(
                                false, true, currentEntity.isImport(), currentEntity.reachMin(), false);
                        RecordManager.get().getShortVideoEntity(index).editingVideoPath = outpath[0];
                        RecordManager.get().getShortVideoEntity(index).editingAudioPath = outpath[1];
                        RecordManager.get().updateProduct();
                    }
                    //开始混合
                    mixAudio(listener);
                } else if (code == -1) {//没有资源，不需要合
                    mixAudio(listener);//失败
                } else if (code != -1) {//没有视频资源，只走音频一次就可以
                    listener.onFinish(code, null);
                    KLog.i("composeMixSucess--composeVideo>" + code);
                    KLog.i("dialog--->composeAudio-->dismiss");
                    dismissDialog();
                }
            }

            @Override
            public void onError(int code, String msg) {
                listener.onError();
            }
        });
    }

    /*
     * 多路音频 变 一路
     * @param videoListener
     */
    public void mixAudio(VideoListener videoListener) {
        RecordUtilSdk.mixAudios(RecordManager.get().getProductEntity(), new VideoListener() {
            @Override
            public void onStart() {
                showDialog();
                if (videoListener != null)
                    videoListener.onStart();
            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onFinish(int code, String outpath) {
                KLog.i("mixAudio--onFinish>" + code);
                //混合成功后
                if (code == SdkConstant.RESULT_SUCCESS) {
                    KLog.i("mixAudio--onFinish-deleteFile-combineAudio>" + RecordManager.get().getProductEntity().combineAudio);
                    FileUtil.deleteFile(RecordManager.get().getProductEntity().combineAudio);
                    RecordManager.get().getProductEntity().combineAudio = outpath;
                    RecordManager.get().updateProduct();
                }
                if (videoListener != null)
                    videoListener.onFinish(code, outpath);
            }

            @Override
            public void onError() {
                videoListener.onError();
            }
        });
    }

    /**
     * 同时剪裁 之前录制的视频，音频
     *
     * @param videoListener
     */
    public void cut(VideoListener videoListener) {

        SLClipVideo.duration = getAct().playerEngine.getDuration();
        cutCount = 0;
        DCVideoManager dcVideoManager = new DCVideoManager();
        KLog.i("=====onRecordEnd:--cut-video-start:-length" + VideoUtils.getVideoLength(getAct().dcRecorderHelper.tempVideoPath));
        long duration = VideoUtils.getVideoLength(getAct().dcRecorderHelper.tempVideoPath);
        KLog.e("=====onRecordEnd:--cut-video-start:-duration" + duration);
        dcVideoManager.cutRecord(getAct().dcRecorderHelper.tempVideoPath, getAct().playerEngine.getCutTime(getAct())/1000,duration, new DCVideoListener() {

            @Override
            public void onStart() {
                KLog.i("=====onRecordEnd:--cut-video-start:" + getAct().playerEngine.getCutTime(getAct()));
                videoListener.onStart();
            }

            @Override
            public void onProgress(int progress) {
                KLog.i("=====onRecordEnd:--cut-onProgress:" + progress);
            }

            @Override
            public void onFinish(int code, String outpath) {
                KLog.i(code + "=====onRecordEnd:--cut-video-onFinish" + outpath);
                KLog.i(code + "=====onRecordEnd:--cut-video>" + VideoUtils.getVideoLength(outpath));
                if (code == SdkConstant.RESULT_SUCCESS) {
                    FileUtil.deleteFile(getAct().dcRecorderHelper.tempVideoPath);
                    // 将录制信息更新
                    getAct().dcRecorderHelper.tempVideoPath = outpath;
                    cutSuccess(code, videoListener);
                } else {//导出失败
                    videoListener.onFinish(code, outpath);
                    KLog.e("=====onRecordEnd:--cut-Failed>" + code);

                }

            }

            @Override
            public void onError() {
                KLog.e("=====onRecordEnd:--cut-Failed>error");
                videoListener.onError();
            }
        });

        final String audioPath = getAct().dcRecorderHelper.getTempAudioPath();
        KLog.i("=====onRecordEnd:--cut-audio1:" + audioPath);
        String outpath = audioPath.substring(0, audioPath.length() - 4) + "_output" + SUFFIX_AUDIO_FILE;
        FileUtils.createFile(outpath);
        long d = (VideoUtils.getVideoLength(getAct().dcRecorderHelper.tempVideoPath) * 1000 - getAct().playerEngine.getCutTime(getAct()));
        dcVideoManager.cutAudio(audioPath, outpath, getAct().playerEngine.getCutTime(getAct()), d, 1.0f, new DCVideoListener() {

            @Override
            public void onStart() {
                KLog.i("=====onRecordEnd:--cut-start:" + getAct().playerEngine.getCutTime(getAct()));
            }

            @Override
            public void onProgress(int progress) {
                KLog.i("=====onRecordEnd:--cut-onProgress:" + progress);
            }

            @Override
            public void onFinish(int code, String outpath) {
                KLog.i(code + "=====onRecordEnd:--cut-audio-onFinish" + outpath);
                KLog.i(code + "=====onRecordEnd:--cut-audio-finish>" + DCVideoManager.getVideoLength(outpath));
                if (code == SdkConstant.RESULT_SUCCESS) {
                    FileUtil.deleteFile(audioPath);//删除裁剪前的音频文件
//                    FileUtil.deleteFiles();
                    // 将录制信息更新
                    getAct().dcRecorderHelper.setTempAudioPath(outpath);
                    cutSuccess(code, videoListener);
                } else {//导出失败
                    videoListener.onFinish(code, outpath);
                }
            }

            @Override
            public void onError() {
                KLog.e("=====onRecordEnd:--cut-Failed>error");
                videoListener.onError();
            }
        });
    }

    int cutCount;

    private void cutSuccess(int code, VideoListener videoListener) {
        cutCount++;
        if (cutCount == 2) {//音频，视频剪切同时成功，
            videoListener.onFinish(code, "");
        }
    }

    /**
     * 下一步
     */
    public void doNext() {
        Log.e("msg", "1111111111");
        if (RecordManager.get().getProductEntity() != null
                && !RecordManager.get().getProductEntity().hasVideo()) {
            showToast("还没有录制视频");
            return;
        }
        if (!RecordManager.get().getProductEntity().isReachMin()) {
            showToast("要拍够" + (int) RecordManager.get().getSetting().getMinDuration() + "秒才可以发布哦");
            return;
        }

        if (!RecordManager.get().getProductEntity().hasJoinVideo()) {
            // 无共同创作视频，视为个人作品
            RecordManager.get().getProductEntity().originalId = 0;
        }

        if (RecordManager.get().getShortVideoEntity(getAct().getCurrentPreviewIndex()).hasEditingFile()) {// 当前视频有编辑后的视频
            RecordManager.get().updateProduct();
            //  PublishActivity.startPublishActivity(act, true);
        }
        for (ShortVideoEntity shortVideoEntity : RecordManager.get().getProductEntity().shortVideoList) {
            KLog.i("record--videopath--->" + shortVideoEntity.editingVideoPath);
        }
        if (getAct().playerEngine != null)
            getAct().playerEngine.reset();
        //TODO release MediaPlayer
        //新版的导出 合成后的视频
        showDialog();
        composeAndExportNew(getAct().getCurrentPreviewIndex(), new VideoListener() {
            @Override
            public void onStart() {
                showDialog();
            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onFinish(int code, String outpath) {
                KLog.i("donext--onFinish-->"+code+"path"+outpath);
                if (code == SdkConstant.RESULT_SUCCESS) {//组合成功
                    muxVideoToNext();
                } else if (code == -1) {//当前类没有视频，不需要合并
                    muxVideoToNext();
                } else {
                    if (dialog != null) {
                        dialog.dismiss();
                        KLog.i("dialog---->composeAndExport-->dismiss");
                    }
                }
            }

            @Override
            public void onError() {
                if (dialog != null) {
                    dialog.dismiss();
                    KLog.i("dialog--->composeAndExport-->error");
                }
            }
        });
    }


    /**
     * 组合后跳转到下一个页面
     */
    private void muxVideoToNext() {
        //不需要合成视频，直接跳转，
        dismissDialog();
        if(getAct()==null)//页面已经销毁
            return;
        KLog.i("dialog---->dismiss");
        if (getAct().dcRecorderHelper.isFullRecord) {//如果当前是 放大的，缩小
            getAct().recordActivitySdkView.exitFullRecord(getAct().smallRecordViewList.get(getAct().getCurrentPreviewIndex()).getPreview());
            getAct().dcRecorderHelper.isFullRecord = false;
        }
        PublishActivity.startPublishActivity(getAct(), true,getAct().recordType);
        getAct().finish();
//        RecordUtilSdk.muxAudioVideo(RecordManager.get().getProductEntity(), new ExportListener() {
//
//            @Override
//            public void onExportStart() {
//                if (!dialog.isShowing()) {
//                    dialog.show();
//                }
//            }
//
//            @Override
//            public void onExporting(int progress, int max) {
//
//            }
//
//            @Override
//            public void onExportEnd(int var1, String path) {
//                KLog.i("donext--muxVideoToNext--onFinish-->");
//
//            }
//        });
    }


    /**
     * 初始化
     * 导出视频信息
     */
    private MVideoConfig initVideoConfig(ShortVideoEntity videoEntity) {
        //测试地址
        String outPath = Environment.getExternalStorageDirectory() + File.separator + "outPut.mp4";
        //创建一个导出路径
        final String editingPath = RecordFileUtil.createTimestampFile(videoEntity.baseDir,
                PREFIX_EDITING_FILE, SUFFIX_VIDEO_FILE, true);
        MVideoConfig videoConfig = new MVideoConfig();
        videoConfig.setVideoPath(editingPath);
//        videoConfig.setVideoSize(360, 640);
        return videoConfig;
    }


    /**
     * 获取当前的 组合列表
     *
     * @return 是否需要合并
     */
    public boolean getComposeList() {
        List<ShortVideoEntity> shortVideoList = RecordManager.get().getProductEntity().shortVideoList;
        //当前需要加载的数据
        boolean needCompose = true;
        //老的数据
        if (compsedList == null) {
            compsedList = new ArrayList<String>();
            needCompose = true;
            //需要重新合并
        } else {
//            1.composeList
//            2.videoList
//              1.为空，2.为空 不需要合并
//                      2不为空，需要合并
//            1.不为空  2.为空， 需要合并(清空)
//                      2.不为空，不为空的部分如果都一致，不合并
//                                否则 需要合并
//

            boolean hasNew = hasNewAudio();
            if (hasNew) {//直接去合并
                needCompose = true;
            } else {
                boolean shortEmpty = isShortVideoEmpty();
                boolean composeEmpty = isComposeListEmpty();
                if (composeEmpty && shortEmpty) {//都是空的
                    needCompose = false;
                } else if (composeEmpty && !shortEmpty) {//之前为空, 新数据不为空
                    needCompose = true;
                } else if (!composeEmpty && shortEmpty) {//之前不为空, 新数据为空 清空数据了
                    needCompose = true;
                    RecordManager.get().getProductEntity().combineAudio = null;
                    RecordManager.get().updateProduct();
                    //音频变空
                } else {//都不为空 时候
                    for (int i = 0; i < shortVideoList.size(); i++) {//现在格子中的音频是否 都包含在compsedList 中
                        if (TextUtils.isEmpty(shortVideoList.get(i).editingAudioPath))//只对比不为空的部分，
                            continue;
                        for (String s : compsedList) {
                            if (TextUtils.isEmpty(s))//只对比不为空的部分，
                                continue;
                            if (!s.equals(shortVideoList.get(i).editingAudioPath)) {//存在一个不一致的，需要合并
                                needCompose = true;
                                break;
                            }
                            KLog.i("combineData-exportProductVideoTemp-Size>" + compsedList.size() + "mdeias》》" + shortVideoList.size());
                        }
                        if (needCompose)//存在不一致，需要合并
                            break;
                    }
                }
            }
            //操作一下
        }
        if (needCompose) {//需要合并，
            compsedList.clear();
            for (ShortVideoEntity mediaObject : shortVideoList) {
                compsedList.add(mediaObject.editingVideoPath);
            }
        }

        KLog.i("exportProductVideoTemp-Size-needcompose>" + needCompose + "mdeias》》");
        return needCompose;
    }

    /**
     * composeList  是否全部为null
     *
     * @return
     */
    private boolean isComposeListEmpty() {
        boolean composeEmpty = false;
        for (String s : compsedList) {
            if (TextUtils.isEmpty(s)) {//为空
                composeEmpty = true;
                continue;
            } else {
                composeEmpty = false;
                break;
            }
        }
        return composeEmpty;
    }

    /**
     * 刚录制完成，没有合并的音频
     *
     * @return
     */
    private boolean hasNewAudio() {
        boolean shortEmpty = false;
        List<ShortVideoEntity> shortVideoList = RecordManager.get().getProductEntity().shortVideoList;
        for (int i = 0; i < shortVideoList.size(); i++) {//现在格子中的音频是否 都包含在compsedList 中
//            KLog.i(i+"combineData---exportProductVideoTemp-compsed"+compsedList.get(i)+"mdeias》》"+shortVideoList.get(i).editingVideoPath);
            //遍历，
            if ((shortVideoList.get(i).editingAudioPath == null) && shortVideoList.get(i).hasClipVideo()) {// 已经录制，但是没有合并
                shortEmpty = true;
                break;
            }
        }
        return shortEmpty;
    }

    private boolean isShortVideoEmpty() {
        boolean shortEmpty = false;
        List<ShortVideoEntity> shortVideoList = RecordManager.get().getProductEntity().shortVideoList;
        for (int i = 0; i < shortVideoList.size(); i++) {//现在格子中的音频是否 都包含在compsedList 中
//            KLog.i(i+"combineData---exportProductVideoTemp-compsed"+compsedList.get(i)+"mdeias》》"+shortVideoList.get(i).editingVideoPath);
            //遍历，
            if (TextUtils.isEmpty(shortVideoList.get(i).editingAudioPath)) {//如果当前的是空的
                shortEmpty = true;
                continue;
            } else {
                shortEmpty = false;
                break;
            }
        }
        return shortEmpty;
    }

    /**
     * 共同创作进来的
     *
     * @param shortVideoList
     */
    public void transformAudio2to1(List<ShortVideoEntity> shortVideoList) {
        KLog.d("TAG", "transformAudio2to1-: onStart==pre");
//        VideoEngine.transformAudio2to1(shortVideoList, new VideoListener() {
//            @Override
//            public void onStart() {
//                KLog.d("TAG", "transformAudio2to1-: onStart==");
//                if (dialog == null) {
//                    //隐藏遮罩
//                    if (act.cutdown != null)
//                        act.cutdown.setVisibility(View.GONE);
//                    showDialog();
//                } else if (!dialog.isShowing()) {
//                    dialog.show();
//                }
//            }
//
//            @Override
//            public void onProgress(int progress) {
////                if (dialog != null) {
////                    dialog.setProgress(progress / 2);
////                }
//            }
//
//            @Override
//            public void onFinish(int code, String outpath) {//主线程 进行相应操作
//                KLog.d("TAG", "transformAudio2to1-onFinish: outpath==" + outpath);
//                combineData();
                RecordUtilSdk.splitAndMuxAudio(new VideoListener() {
                    @Override
                    public void onStart() {
                        KLog.d("TAG", "split-start: outpath==" );
                        if (dialog == null)
                            showDialog();
//                    //隐藏遮罩
                    if (getAct().cutdown != null)
                        getAct().cutdown.setVisibility(View.GONE);

                        if (!dialog.isShowing()) {
                            dialog.setProgress(0);
                            dialog.show();
                        }
                    }

                    @Override
                    public void onProgress(int progress) {

                    }

                    @Override
                    public void onFinish(int code, String outpath) {
                        KLog.d("TAG", code + "split-onFinish: outpath==" + outpath);
                        if (code == SdkConstant.RESULT_SUCCESS) {//保存合并后的音频
                            RecordManager.get().getProductEntity().combineAudio = outpath;
                            RecordManager.get().updateProduct();
                            getAct().runOnUiThread(new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    KLog.d("currentPreviewIndex==spit--before>" + getAct().getCurrentPreviewIndex());
                                    getAct().recordActivitySdkView.combinePreview(false, false, false);//播放器
                                    getAct().updateRecordItemView();
                                    getAct().recordActivitySdkView.resetMenu(RecordManager.get().getShortVideoEntity(getAct().getCurrentPreviewIndex()));

                                    getAct().selectSmallCamera(getAct().getCurrentPreviewIndex());

                                    if (dialog != null) {
                                        dialog.dismiss();
                                    }
                                }
                            }));
                        } else {
                            getAct().runOnUiThread(new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if (dialog != null) {
                                        dialog.dismiss();
                                    }
                                }
                            }));
                        }
                    }

                    @Override
                    public void onError() {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });
//            }
//
//            @Override
//            public void onError() {
//                if (dialog != null) {
//                    dialog.dismiss();
//                }
//            }
//        });
    }

    /**
     * 合成数据，
     */
    public void combineData() {
        if (getAct().playerEngine != null)
            getAct().playerEngine.release();
        boolean compose = getComposeList();
        KLog.i("combineData-->" + compose);
        if (compose) {//需要合并
            mixAudio(new VideoListener() {
                @Override
                public void onStart() {
                    getAct().cutdown.setVisibility(View.GONE);
                    showDialog();
                }

                @Override
                public void onProgress(int progress) {

                }

                @Override
                public void onFinish(int code, String outpath) {
                    KLog.i("combineData--finish>" + code);
                    if (code == DCCameraConfig.SUCCESS) {//
                        getAct().recordActivitySdkView.combinePreview(false, false, false);
                    } else {//没有视频资源
                        getAct().recordActivitySdkView.combinePreview(false, false, false);
                    }
                    dismissDialog();
                }

                @Override
                public void onError() {
                    KLog.e("combineData--error>");
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });
        } else {
            getAct().recordActivitySdkView.combinePreview(false, false, false);
        }
    }

    public void showDialog() {
        if (dialog == null) {
            dialog = SysAlertDialog.createCircleProgressDialog(getAct(), act.getString(R.string.join_preview), true, false);
        }
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    public void dismissDialog() {
        if (dialog != null)
            dialog.dismiss();
    }

    public void onDestory() {
        act = null;
        dismissDialog();
    }
}
