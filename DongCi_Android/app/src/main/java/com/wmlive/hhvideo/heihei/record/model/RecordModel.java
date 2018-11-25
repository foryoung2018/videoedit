package com.wmlive.hhvideo.heihei.record.model;

import android.text.TextUtils;

import com.dongci.sun.gpuimglibrary.api.DCCameraConfig;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.record.activity.RecordActivitySdk;
import com.wmlive.hhvideo.heihei.record.activity.RecordActivitySdkVideoHelper;
import com.wmlive.hhvideo.heihei.record.engine.listener.ExportListener;
import com.wmlive.hhvideo.heihei.record.engine.listener.VideoListener;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtilSdk;
import com.wmlive.hhvideo.heihei.record.widget.SysAlertDialog;
import com.wmlive.hhvideo.utils.KLog;

import java.io.File;

import cn.wmlive.hhvideo.R;

/**
 * 录制页面数据更新
 * 对于数据的添加，更改删除，查询
 */
public class RecordModel {


    public boolean comBineMode = true;//合并模式

    RecordActivitySdkVideoHelper recordActivitySdkVideoHelper;

    public void setHelper(RecordActivitySdkVideoHelper recordActivitySdkVideoHelper){
        this.recordActivitySdkVideoHelper = recordActivitySdkVideoHelper;
    }

    public void init(){

    }

    /**
     * 对上一个格子进行相应的处理
     * @param preIndex
     * @param needJoin
     * @param needCombine
     * @param combineAll
     * @param seekEnd
     * @param entryEdit
     * @param entrySort
     * @param videoEntity
     * 1.是否需要组合，2.本地导入的视频，2.1 合并模式 2.1.1导出组合视频(导出一个整视频)   2.1.2 更新导出i地址 2.1.3 合并，combine显示播放
     *                 2.2 录制视频，2.2.1合并模式，2.2.2 合并+导出 组合视频 2.2.3 更新导出地址 2.2.4 合并combine 显示播放
     *
     *                 导出合并中进度条加载
     *e
     */
//    private void resetPreItemPreview(final int preIndex, boolean needJoin, boolean needCombine, boolean combineAll, final boolean seekEnd,
//                                     final boolean entryEdit, boolean entrySort, final ShortVideoEntity videoEntity) {
//        boolean hasVideo = videoEntity.hasVideo();
//        KLog.i(needCombine + "resetPreItemPreview======生成视频预览   成功 resetPreItemPreview：" + needJoin + "isImprot-->" + videoEntity.isImport());
//        if (needJoin) {
//            if (videoEntity.isImport()) {
////                if (needCombine) {
////                    combinePreview(combineAll, seekEnd, entryEdit, entrySort);
//                if (comBineMode) {
////                    exportProductVideoTemp
//                    if (playerEngine != null)
//                        playerEngine.release();
//                    RecordUtilSdk.exportProductVideoTemp(RecordManager.get().getProductEntity(), new ExportListener() {//preIndex
//
//                        @Override
//                        public void onExportStart() {
//                            if (dialog == null) {
//                                dialog = SysAlertDialog.createCircleProgressDialog(RecordActivitySdk.this, getString(R.string.join_preview), true, false);
//                            }
//                            if (!dialog.isShowing()) {
//                                dialog.setProgress(0);
//                                dialog.show();
//                            }
//                        }
//
//                        @Override
//                        public void onExporting(int progress, int max) {
//                            KLog.i(needCombine + "resetPreItemPreview======onExporting：" + progress);
//                            if (dialog != null) {
//                                dialog.setProgress(progress);
//                            }
//                        }
//
//                        @Override
//                        public void onExportEnd(int var1, String path) {
//                            KLog.i("resetPreItemPreview======onExportEnd：" + path);
//                            if (var1 == DCCameraConfig.SUCCESS) {//
//                                RecordManager.get().getProductEntity().combineVideo = path;
//
//                                RecordManager.get().updateProduct();
//
//                                getWeakHandler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if (needCombine) {
//                                            KLog.i("realease--->", "init-player--onExportEnd");
//                                            combinePreview(combineAll, seekEnd, entryEdit, entrySort);
//                                        }
//                                    }
//                                }, 100);
//
//                            } else {
//                                KLog.i("======生成视频预览失败 index：" + preIndex);
//                            }
//                            if (dialog != null && dialog.isShowing()) {
//                                dialog.dismiss();
//                            }
//
//                        }
//                    });
////                    recordActivitySdkVideoHelper.composeAndExport(smallRecordViewList, RecordManager.get().getProductEntity().shortVideoList
////                            , preIndex, currentPreviewIndex, customFrameView, mFrameInfo, 0, 100, new VideoListener() {
////                                @Override
////                                public void onStart() {
////                                    KLog.d("resetPreItemPreview-onStart");
////                                }
////
////                                @Override
////                                public void onProgress(int progress) {
////                                    KLog.d("resetPreItemPreview-onProgress" + progress);
////                                }
////
////                                @Override
////                                public void onFinish(int code, String outpath) {
////                                    KLog.d("resetPreItemPreview-onFinsh" + code + "path" + outpath);
////                                    if (code == DCCameraConfig.SUCCESS) {//
////                                        RecordManager.get().getProductEntity().combineVideo = outpath;
////
////                                        RecordManager.get().updateProduct();
////                                        if (needCombine) {
////                                            combinePreview(combineAll, seekEnd, entryEdit, entrySort);
////                                        }
////                                    } else {
////                                        KLog.i("======生成视频预览失败 index：" + preIndex);
////                                    }
////                                }
////
////                                @Override
////                                public void onError() {
////
////                                }
////                            });
////                    }
//                } else if (needCombine) {
////                    combinePreview(combineAll, seekEnd, entryEdit, entrySort);
//                }
//            } else {
//                if (!videoEntity.needJoin() && !TextUtils.isEmpty(videoEntity.editingVideoPath)
//                        && new File(videoEntity.editingVideoPath).exists()) {
//                    if (needCombine) {//直接预览
////                        combinePreview(combineAll, seekEnd, entryEdit, entrySort);
//                    }
//                } else {
//                    if (comBineMode) {
//                        if(recordActivitySdkVideoHelper.getComposeList()){
//                            if (playerEngine != null && !playerEngine.isNull())
//                                playerEngine.release();
//                            recordActivitySdkVideoHelper.composeAndExport(smallRecordViewList, RecordManager.get().getProductEntity().shortVideoList
//                                    , preIndex, currentPreviewIndex, customFrameView, mFrameInfo, 0, 100, new VideoListener() {
//                                        @Override
//                                        public void onStart() {
//                                            KLog.d("resetPreItemPreview-onStart");
//                                        }
//
//                                        @Override
//                                        public void onProgress(int progress) {
//                                            KLog.d("resetPreItemPreview-onProgress" + progress);
//                                        }
//
//                                        @Override
//                                        public void onFinish(int code, String outpath) {
//                                            KLog.d("resetPreItemPreview-onFinsh" + code + "path" + outpath);
//                                            if (code == DCCameraConfig.SUCCESS) {//
//                                                RecordManager.get().getProductEntity().combineVideo = outpath;
////                                        //如果两个视频需要导出，
////                                            smallRecordViewList.get(preIndex).showStatus(
////                                                    false, hasVideo, videoEntity.isImport(), videoEntity.reachMin());
////                                            videoEntity.editingVideoPath = outpath;
////                                            RecordManager.get().updateProduct();
//
//                                                if (needCombine) {
//                                                    combinePreview(combineAll, seekEnd, entryEdit, entrySort);
//                                                }
//                                            } else {
//                                                KLog.i("======生成视频预览失败 index：" + preIndex);
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onError() {
//
//                                        }
//                                    });
//                        }else {//不需要组合
//                            combinePreview(combineAll, seekEnd, entryEdit, entrySort);
//                        }
//                    } else {
//                        RecordUtilSdk.compose(videoEntity, new VideoListener() {
//                            @Override
//                            public void onStart() {
//                                KLog.d("compose---onStart---show>");
//                                if (dialog == null) {
//                                    dialog = SysAlertDialog.createCircleProgressDialog(RecordActivitySdk.this, getString(R.string.join_preview), true, false);
//                                }
//                                if (!dialog.isShowing()) {
//                                    dialog.show();
//                                }
//                            }
//
//                            @Override
//                            public void onProgress(int progress) {
//                                KLog.d("compose---resetPreItemPreview---show>" + progress);
//                                if (dialog != null) {
//                                    dialog.setProgress(progress / 10);
//                                }
//                            }
//
//                            @Override
//                            public void onFinish(int code, String outpath) {
//                                KLog.i(needCombine + "compose======生成视频预览成功 index：" + code + "code" + preIndex + outpath);
//                                if (code == DCCameraConfig.SUCCESS) {//
//                                    //如果两个视频需要导出，
//                                    smallRecordViewList.get(preIndex).showStatus(
//                                            false, hasVideo, videoEntity.isImport(), videoEntity.reachMin());
//                                    videoEntity.editingVideoPath = outpath;
//                                    RecordManager.get().updateProduct();
//                                    KLog.i("======editingVideoPath ：" + outpath);
//                                    if (needCombine) {
//                                        combinePreview(combineAll, seekEnd, entryEdit, entrySort);
//                                    }
//                                } else {// 可能没有视频资源。
//                                    KLog.i("======生成视频预览失败 index：" + preIndex);
////                                if (needCombine) {
////                                    combinePreview(combineAll, seekEnd, entryEdit, entrySort);
////                                }
//                                }
//                                if (dialog != null && dialog.isShowing()) {
//                                    dialog.dismiss();
//                                }
//                            }
//
//                            @Override
//                            public void onError() {
//
//                            }
//                        });
//                    }
//                }
//            }
//        }
//    }
}
