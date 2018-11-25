package com.wmlive.hhvideo.heihei.record.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


//import com.rd.vecore.VirtualVideo;
//import com.rd.vecore.VirtualVideoView;
//import com.rd.vecore.exception.InvalidArgumentException;
//import com.rd.vecore.listener.ExportListener;
//import com.rd.vecore.models.EffectType;
//import com.rd.vecore.models.VideoConfig;
import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.heihei.beans.frame.FrameInfo;
import com.wmlive.hhvideo.heihei.beans.record.EffectEntity;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.discovery.activity.SearchVideoActivity;
import com.wmlive.hhvideo.heihei.record.adapter.FilterPanelAdapter;
import com.wmlive.hhvideo.heihei.record.engine.PlayerEngine;
import com.wmlive.hhvideo.heihei.record.engine.config.MVideoConfig;
import com.wmlive.hhvideo.heihei.record.engine.constant.SdkConstant;
import com.wmlive.hhvideo.heihei.record.engine.listener.ExportListener;
import com.wmlive.hhvideo.heihei.record.engine.listener.PlayerListener;
import com.wmlive.hhvideo.heihei.record.listener.VideoJoinListener;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.manager.RecordSetting;
import com.wmlive.hhvideo.heihei.record.utils.RecordFileUtil;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtil;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtilSdk;
import com.wmlive.hhvideo.heihei.record.widget.CircleProgressDialog;
import com.wmlive.hhvideo.heihei.record.widget.RecordEffectPanel;
import com.wmlive.hhvideo.heihei.record.widget.RecordFilterPanel;
import com.wmlive.hhvideo.heihei.record.widget.RecordVolumePanel;
import com.wmlive.hhvideo.heihei.record.widget.SysAlertDialog;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ToastUtil;

import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;


/**
 * 编辑单个视频的页面
 * 包括滤镜、声音，特效
 */
public class EditVideoActivity extends DcBaseActivity implements RecordEffectPanel.OnEffectListener,
//        VirtualVideoView.VideoViewListener,
        FilterPanelAdapter.OnFilterItemSelectListener, RecordVolumePanel.OnVolumeChangeListener {

    public static final String VIDEO_NEED_EXPORT = "video_need_export";
    @BindView(R.id.tvCancel)
    TextView tvCancel;
    @BindView(R.id.ivFilter)
    ImageView ivFilter;
    @BindView(R.id.ivEffect)
    ImageView ivEffect;
    @BindView(R.id.ivVolume)
    ImageView ivVolume;
    @BindView(R.id.tvSubmit)
    TextView tvSubmit;
    @BindView(R.id.panelEffect)
    RecordEffectPanel panelEffect;
    @BindView(R.id.panelFilter)
    RecordFilterPanel panelFilter;
    @BindView(R.id.panelVolume)
    RecordVolumePanel panelVolume;
    //    @BindView(R.id.videoPlayer)
//    VirtualVideoView mVideoPlayer;
    @BindView(R.id.videoViewsdk)
    TextureView videoViewsdk;

    @BindView(R.id.ivPlaySwitch)
    ImageView ivPlaySwitch;
    @BindView(R.id.videoContainer)
    FrameLayout videoContainer;
    @BindView(R.id.ivVideoThumb)
    ImageView ivVideoThumb;

    private int shortVideoIndex;
    private EditType selectMode = EditType.EFFECT;

    //    private VirtualVideo mVirtualVideo;
    private PlayerEngine playerEngine;
    private MVideoConfig mTrimVideoConfig = new MVideoConfig();

    private ShortVideoEntity videoEntity;
    private ShortVideoEntity shortVideoEntity;

    private int mOriginalMixFactor;
    private int mFilterType;
    private boolean hasModify; // 是否有修改
    private boolean needInitEffect = true;
    private boolean videoNeedExport; // 是否需要重新导出视频
    private List<EffectEntity> effectList;

    public static void startEditVideoActivity(Activity context, int index) {
        startEditVideoActivity(context, index, true);
    }

    public static void startEditVideoActivity(Activity context, int index, boolean needExport) {
        Intent intent = new Intent(context, EditVideoActivity.class);
        intent.putExtra(SearchVideoActivity.SHORT_VIDEO_INDEX, index);
        intent.putExtra(VIDEO_NEED_EXPORT, needExport);
        context.startActivityForResult(intent, RecordActivitySdk.REQUEST_EDIT_VIDEO);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_edit_video;
    }

    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();
        if (null != intent) {
            shortVideoIndex = intent.getIntExtra(SearchVideoActivity.SHORT_VIDEO_INDEX, 0);
            videoNeedExport = intent.getBooleanExtra(VIDEO_NEED_EXPORT, true);
        }
        tvCancel.setOnClickListener(this);
        tvSubmit.setOnClickListener(this);
        ivFilter.setOnClickListener(this);
        ivEffect.setOnClickListener(this);
        ivVolume.setOnClickListener(this);
        panelEffect.setVisibility(View.VISIBLE);
        panelEffect.setmEffectListener(this);

        panelFilter.setFilterItemSelectListener(this);

        ivPlaySwitch.setBackgroundResource(R.drawable.btn_player_play);
        ivPlaySwitch.setVisibility(View.VISIBLE);
        videoContainer.post(new Runnable() {
            @Override
            public void run() {
                if (videoContainer != null) {
                    ViewGroup.LayoutParams layoutParams = videoContainer.getLayoutParams();
                    if (layoutParams != null) {
                        layoutParams.height = (int) (videoContainer.getMeasuredWidth() * 4 / 3.0f);
                        videoContainer.setLayoutParams(layoutParams);
                    }
                }
            }
        });

        videoEntity = RecordManager.get().getShortVideoEntity(shortVideoIndex);
        if (videoEntity != null) {
            mOriginalMixFactor = videoEntity.getOriginalMixFactor();
            mFilterType = videoEntity.getFilterId();
            panelVolume.setVolume(mOriginalMixFactor, RecordSetting.MAX_VOLUME);
            panelVolume.setOnVolumeChangeListener(this);
            if (videoNeedExport) {
                ivVolume.setVisibility(View.GONE);
            }
            exportVideo();
        } else {
            toastFinish();
        }
    }

    private void exportVideo() {
        if (videoEntity.isImport() || !videoNeedExport) {
            try {
                shortVideoEntity = (ShortVideoEntity) videoEntity.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                toastFinish();
            }
            initPlayer();
        } else {
            RecordUtil.joinAndReverse(EditVideoActivity.this, videoEntity, true, new VideoJoinListener() {
                @Override
                public void onJoinStart() {
                    if (dialog == null) {
                        dialog = SysAlertDialog.createCircleProgressDialog(EditVideoActivity.this, getString(R.string.join_media), true, false);
                    }
                    if (!dialog.isShowing()) {
                        dialog.show();
                    }
                }

                @Override
                public void onJoining(int progress, int max) {
                    dialog.setProgress(progress / 10);
                }

                @Override
                public void onJoinEnd(boolean result, String message) {
                    if (RecordManager.get().getProductEntity() == null) {
                        toastFinish();
                    }
                    if (result) {
                        RecordManager.get().updateProduct();
                        try {
                            shortVideoEntity = (ShortVideoEntity) videoEntity.clone();
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                            toastFinish();
                        }
                        initPlayer();
                    } else {
                        ToastUtil.showToast("合成视频出错");
                    }
                    if (dialog != null) {
                        Activity activity = EditVideoActivity.this;
                        if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
                            return;
                        }
                        try {
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }


    /**
     * 初始化播放器
     */
    private void initPlayer() {
        //sun
//        VirtualVideo.getMediaInfo(shortVideoEntity.editingVideoPath, mTrimVideoConfig, true);
        reload();
        if (playerEngine == null) {
            KLog.i("====视频播放出错");
            toastFinish();
            return;
        }
        playerEngine.setPreviewAspectRatio(RecordManager.get().getSetting().getVideoRatio());
        playerEngine.setAutoRepeat(true);
        playerEngine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        playerEngine.setOnPlaybackListener(new PlayerListener() {
            @Override
            public void onPlayerPrepared() {
                playerEngine.setOriginalMixFactor(mOriginalMixFactor);
                if (needInitEffect) {
                    needInitEffect = false;
                    panelEffect.initEffect(shortVideoEntity);
                }
            }

            @Override
            public boolean onPlayerError(int var1, int var2) {
                return false;
            }

            @Override
            public void onPlayerCompletion() {
                ivPlaySwitch.setBackgroundResource(R.drawable.btn_player_play);
                ivPlaySwitch.setVisibility(View.VISIBLE);
            }

            @Override
            public void onGetCurrentPosition(float var1) {
                if (EditType.EFFECT == selectMode) {
                    panelEffect.setPosition(var1);
                }
            }
        });
    }

    private float mLastPlayPostion;
    private boolean mLastPlaying;

    @Override
    protected void onResume() {
        super.onResume();
        if (!playerEngine.isNull()) {
            playerEngine.seekTo(mLastPlayPostion);
            if (mLastPlaying) {
                playerEngine.start();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!playerEngine.isNull()) {
            mLastPlaying = playerEngine.isPlaying();
            mLastPlayPostion = playerEngine.getCurrentPosition();
            playerEngine.pause();
        }
    }

    private CircleProgressDialog dialog;

    @Override
    protected void onSingleClick(View v) {
        switch (v.getId()) {
            case R.id.tvCancel:
                // 还原滤镜 特效
                cancelEdit();
                break;
            case R.id.tvSubmit:
                if (!hasModify && !panelEffect.hasChangeEffects()) {
                    // 原声滤镜特效都没有作修改，直接返回
                    finish();
                    return;
                }
                if (RecordManager.get().getProductEntity() == null) {
                    toastFinish();
                }
                final ShortVideoEntity shortVideoInfo = RecordManager.get().getShortVideoEntity(shortVideoIndex);
                shortVideoInfo.setOriginalMixFactor(shortVideoEntity.getOriginalMixFactor());
                shortVideoInfo.setFilterId(shortVideoEntity.getFilterId());
                shortVideoInfo.setEffectList(shortVideoEntity.getEffectList());
                shortVideoInfo.hasEdited = true;
                // 存数据库
                RecordManager.get().updateProduct();
                final String exportPath = RecordFileUtil.createTimestampFile(shortVideoInfo.baseDir,
                        RecordManager.PREFIX_EDITING_FILE,
                        RecordManager.SUFFIX_VIDEO_FILE, true);
                if (!TextUtils.isEmpty(exportPath)) {
                    try {
                        final PlayerEngine playerEngine = new PlayerEngine();
//                        final VirtualVideo virtualVideo = new VirtualVideo();
                        //sun
                        if (RecordUtilSdk.loadSingleVideo(shortVideoEntity)) {//包含数据信息可以导出
                            //初始化 导出信息
                            MVideoConfig videoConfig = new MVideoConfig();
                            if (shortVideoInfo.quality == FrameInfo.VIDEO_QUALITY_HIGH) {
                                videoConfig.setVideoSize(RecordSetting.PRODUCT_WIDTH, RecordSetting.PRODUCT_HEIGHT);
                                videoConfig.setVideoEncodingBitRate(RecordSetting.VIDEO_PUBLISH_BITRATE);
                            } else {
                                videoConfig.setVideoSize(RecordSetting.VIDEO_WIDTH, RecordSetting.VIDEO_HEIGHT);
                                videoConfig.setVideoEncodingBitRate(RecordSetting.VIDEO_PUBLISH_BITRATE_SMALL);
                            }
                            videoConfig.setAspectRatio(RecordManager.get().getSetting().getVideoRatio());
                            if (RecordSetting.SET_AUDIO_RATE) {
                                videoConfig.setAudioEncodingParameters(1, RecordSetting.AUDIO_SAMPLING_RATE, RecordSetting.AUDIO_ENCODING_BITRATE);
                            }
                            videoConfig.enableHWDecoder(true);
                            videoConfig.enableHWEncoder(true);
                            videoConfig.setVideoFrameRate(RecordManager.get().getSetting().videoFrameRate);
                            RecordUtilSdk.exportSingleVideo(shortVideoEntity, videoConfig, new ExportListener() {
                                @Override
                                public void onExportStart() {
                                    if (dialog == null || !dialog.isShowing()) {
                                        dialog = SysAlertDialog.showCircleProgressDialog(EditVideoActivity.this, getString(R.string.join_media), true, false);
                                    }
                                }

                                @Override
                                public void onExporting(int var1, int var2) {
                                    KLog.i("====正在导出视频 progress：" + var1);
                                    if (dialog != null) {
                                        dialog.setProgress(var1 / 10);
                                    }
                                }

                                @Override
                                public void onExportEnd(int var1, String path) {
                                    if (RecordManager.get().getProductEntity() == null) {
                                        toastFinish();
                                    }
                                    if (var1 >= SdkConstant.RESULT_SUCCESS) {
//                                        RecordFileUtil.deleteFiles(shortVideoInfo.editingVideoPath);
                                        shortVideoInfo.editingVideoPath = exportPath;
                                        shortVideoInfo.setNeedJoin(false);
                                        RecordManager.get().updateProduct();
                                        KLog.i("====导出视频成功,路径：" + shortVideoInfo.editingVideoPath);
//                                        EventHelper.post(GlobalParams.EventType.TYPE_EDIT_FINISH);
                                    } else {
                                        KLog.i("====导出失败result：" + var1);
                                        showToast("导出视频失败");
                                    }

                                    playerEngine.release();
                                    if (dialog != null) {
                                        Activity activity = EditVideoActivity.this;
                                        if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
                                            return;
                                        }
                                        try {
                                            if (dialog != null) {
                                                dialog.dismiss();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    setResult(RESULT_OK, new Intent());
                                    finish();
                                }
                            });

                        } else {

                        }
//                        if (RecordUtil.loadSingleVideo(virtualVideo, shortVideoInfo, true)) {
//                            MVideoConfig videoConfig = new MVideoConfig();
//                            if (shortVideoInfo.quality == FrameInfo.VIDEO_QUALITY_HIGH) {
//                                videoConfig.setVideoSize(RecordSetting.PRODUCT_WIDTH, RecordSetting.PRODUCT_HEIGHT);
//                                videoConfig.setVideoEncodingBitRate(RecordSetting.VIDEO_PUBLISH_BITRATE);
//                            } else {
//                                videoConfig.setVideoSize(RecordSetting.VIDEO_WIDTH, RecordSetting.VIDEO_HEIGHT);
//                                videoConfig.setVideoEncodingBitRate(RecordSetting.VIDEO_PUBLISH_BITRATE_SMALL);
//                            }
//                            videoConfig.setAspectRatio(RecordManager.get().getSetting().getVideoRatio());
//                            if (RecordSetting.SET_AUDIO_RATE) {
//                                videoConfig.setAudioEncodingParameters(1, RecordSetting.AUDIO_SAMPLING_RATE, RecordSetting.AUDIO_ENCODING_BITRATE);
//                            }
//                            videoConfig.enableHWDecoder(false);
//                            videoConfig.enableHWEncoder(true);
//                            videoConfig.setVideoFrameRate(RecordManager.get().getSetting().videoFrameRate);
//
//                            virtualVideo.export(DCApplication.getDCApp(), exportPath, videoConfig, new ExportListener() {
//                                @Override
//                                public void onExportStart() {
//                                    if (dialog == null || !dialog.isShowing()) {
//                                        dialog = SysAlertDialog.showCircleProgressDialog(EditVideoActivity.this, getString(R.string.join_media), true, false);
//                                    }
//                                }
//
//                                @Override
//                                public boolean onExporting(int progress, int max) {
//                                    KLog.i("====正在导出视频 progress：" + progress);
////                                    if (dialog != null) {
////                                        dialog.setProgress(progress / 10);
////                                    }
//                                    return true;
//                                }
//
//                                @Override
//                                public void onExportEnd(int result) {
//                                    if (RecordManager.get().getProductEntity() == null) {
//                                        toastFinish();
//                                    }
//                                    if (result >= VirtualVideo.RESULT_SUCCESS) {
////                                        RecordFileUtil.deleteFiles(shortVideoInfo.editingVideoPath);
//                                        shortVideoInfo.editingVideoPath = exportPath;
//                                        shortVideoInfo.setNeedJoin(false);
//                                        RecordManager.get().updateProduct();
//                                        KLog.i("====导出视频成功,路径：" + shortVideoInfo.editingVideoPath);
////                                        EventHelper.post(GlobalParams.EventType.TYPE_EDIT_FINISH);
//                                    } else {
//                                        KLog.i("====导出失败result：" + result);
//                                        showToast("导出视频失败");
//                                    }
//
//                                    virtualVideo.release();
//                                    if (dialog != null) {
//                                        Activity activity = EditVideoActivity.this;
//                                        if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
//                                            return;
//                                        }
//                                        try {
//                                            if (dialog != null) {
//                                                dialog.dismiss();
//                                            }
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                    setResult(RESULT_OK, new Intent());
//                                    finish();
//                                }
//                            });
//                        } else {
//                            KLog.i("=====视频实体类出错");
//                            showToast("加载视频失败");
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (dialog != null) {
                            try {
                                Activity activity = EditVideoActivity.this;
                                if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
                                    return;
                                }
                                if (dialog != null) {
                                    dialog.dismiss();
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        KLog.i("====导出失败");
                        showToast("导出视频失败");
                    }

                } else {
                    KLog.i("====创建文件夹失败");
                    showToast("创建文件夹失败");
                }
                break;
            case R.id.ivFilter:
                selectMode = EditType.FILTER;
                panelFilter.setVisibility(View.VISIBLE);
                panelEffect.setVisibility(View.INVISIBLE);
                panelVolume.setVisibility(View.INVISIBLE);
                ivFilter.setImageDrawable(getResources().getDrawable(R.drawable.icon_video_topbar_filter_nor));
                ivEffect.setImageDrawable(getResources().getDrawable(R.drawable.icon_video_topbar_effects_dis));
                ivVolume.setImageDrawable(getResources().getDrawable(R.drawable.icon_video_topbar_vol_dis));
                playerEngine.setAutoRepeat(true);
                break;
            case R.id.ivEffect:
                selectMode = EditType.EFFECT;
                panelFilter.setVisibility(View.INVISIBLE);
                panelEffect.setVisibility(View.VISIBLE);
                panelVolume.setVisibility(View.INVISIBLE);
                ivFilter.setImageDrawable(getResources().getDrawable(R.drawable.icon_video_topbar_filter_dis));
                ivEffect.setImageDrawable(getResources().getDrawable(R.drawable.icon_video_topbar_effects_nor));
                ivVolume.setImageDrawable(getResources().getDrawable(R.drawable.icon_video_topbar_vol_dis));
                playerEngine.setAutoRepeat(false);
//                panelEffect.initEffect(shortVideoIndex);
                break;
            case R.id.ivVolume:
                selectMode = EditType.VOLUME;
                panelFilter.setVisibility(View.INVISIBLE);
                panelEffect.setVisibility(View.INVISIBLE);
                panelVolume.setVisibility(View.VISIBLE);
                ivFilter.setImageDrawable(getResources().getDrawable(R.drawable.icon_video_topbar_filter_dis));
                ivEffect.setImageDrawable(getResources().getDrawable(R.drawable.icon_video_topbar_effects_dis));
                ivVolume.setImageDrawable(getResources().getDrawable(R.drawable.icon_video_topbar_vol_nor));
                playerEngine.setAutoRepeat(true);
                break;
            case R.id.videoViewsdk:
                if (playerEngine.isPlaying()) {
                    playerEngine.pause();
                    ivPlaySwitch.setVisibility(View.VISIBLE);
                } else {
                    playerEngine.start();
                    ivPlaySwitch.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }

    private void cancelEdit() {
        if (!hasModify && !panelEffect.hasChangeEffects()) {
            // 原声滤镜特效都没有作修改，直接返回
            final ShortVideoEntity shortVideo = RecordManager.get().getShortVideoEntity(shortVideoIndex);
//            RecorderCore.setColorEffect(String.valueOf(shortVideo.getFilterId()));
            finish();
        } else {
            SysAlertDialog.showAlertDialog(EditVideoActivity.this, R.string.release_cancel_edit_alert, R.string.release_back_press_cancel,
                    null, R.string.release_back_press_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final ShortVideoEntity shortVideo = RecordManager.get().getShortVideoEntity(shortVideoIndex);
//                            RecorderCore.setColorEffect(String.valueOf(shortVideo.getFilterId()));
                            finish();
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        cancelEdit();
    }

    @Override
    public void start() {
        if (playerEngine.isNull()) {
            return;
        }
        playerEngine.start();
        ivPlaySwitch.setVisibility(View.INVISIBLE);
    }

    @Override
    public void pause() {
        if (playerEngine.isNull()) {
            return;
        }
        playerEngine.pause();
        ivPlaySwitch.setVisibility(View.VISIBLE);
    }

    @Override
    public void seekTo(float second) {
        playerEngine.seekTo(second);
    }

    @Override
    public float getDuration() {
        if (!playerEngine.isNull()) {
            return playerEngine.getDuration();
        }
        return 0f;
    }

    @Override
    public float getCurrentPosition() {
        if (!playerEngine.isNull()) {
            return playerEngine.getCurrentPosition();
        } else {
            return 0;
        }
    }

    @Override
    public boolean isPlaying() {
        return !playerEngine.isNull() && playerEngine.isPlaying();
    }

    @Override
    public void reload() {
        if (playerEngine.isNull()) {
            playerEngine = new PlayerEngine();
        }
        try {
            playerEngine.reset();
//            mVirtualVideo.reset();
            //sun待
            if (RecordUtilSdk.loadSingleVideo(shortVideoEntity)) {
                playerEngine.build(videoViewsdk, null);
                start();
            } else {
                KLog.e("====视频信息出错");
            }
//            if (RecordUtil.loadSingleVideo(mVirtualVideo, shortVideoEntity, true)) {

//            } else {
//                KLog.i("====视频信息出错");
//                toastFinish();
//            }
        } catch (Exception e) {
            e.printStackTrace();
            toastFinish();
        }
    }

    /**
     * 实时更新特效（主要是滤镜特效）
     */
    @Override
    public void updateEffects() {
//        try {
//            for (int effectOrinal = EffectType.TREMBLE.ordinal();
//                 effectOrinal <= EffectType.SPOTLIGHT.ordinal(); effectOrinal++) {
//                mVirtualVideo.clearEffect(EffectType.values()[effectOrinal]);
//            }
//            for (EffectEntity effectInfo : shortVideoEntity.getEffectList()) {
//                if (effectInfo.effectType.ordinal() >= EffectType.TREMBLE.ordinal()
//                        && effectInfo.effectType.ordinal() <= EffectType.SPOTLIGHT.ordinal()) {
//                    mVirtualVideo.addEffect(effectInfo.effectType, effectInfo.getStartTime(), effectInfo.getEndTime(), effectInfo);
//                }
//            }
//            if (isPlaying()) {
//                pause();
//            }
//            mVirtualVideo.updateEffects(mVideoPlayer);
//            if (isPlaying()) {
//                start();
//            }
//        } catch (InvalidArgumentException e) {
//            e.printStackTrace();
//        }
    }

//    @Override
//    public void onPlayerPrepared() {
//        mVirtualVideo.setOriginalMixFactor(mOriginalMixFactor);
//        if (needInitEffect) {
//            needInitEffect = false;
//            panelEffect.initEffect(shortVideoEntity);
//        }
//    }
//
//    @Override
//    public boolean onPlayerError(int i, int i1) {
//        return false;
//    }

    /**
     * 播放完成暂停
     */
//    @Override
//    public void onPlayerCompletion() {
//        ivPlaySwitch.setBackgroundResource(R.drawable.btn_player_play);
//        ivPlaySwitch.setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    public void onGetCurrentPosition(float position) {
//        if (EditType.EFFECT == selectMode) {
//            panelEffect.setPosition(position);
//        }
//    }

    /**
     * 滤镜选择回调
     *
     * @param selectIndex
     * @param selectFilter
     * @param oldId
     */
    @Override
    public void onFilterSelected(int selectIndex, int selectFilter, int oldId) {
        // 改变滤镜信息
//        RecorderCore.setColorEffect(String.valueOf(selectFilter));
        mFilterType = selectFilter;
        hasModify = true;
        shortVideoEntity.setFilterId(selectFilter);
        playerEngine.setFilterType(selectFilter);
        start();
    }

    /***
     * 设置原音大小回调
     * @param progress
     */
    @Override
    public void onOriginalVolumeChange(int progress, int value) {
        shortVideoEntity.setOriginalMixFactor(value);
        playerEngine.setOriginalMixFactor(value);
        mOriginalMixFactor = value;
        hasModify = true;
    }

    /**
     * 设置声道大小回调
     *
     * @param progress
     */
    @Override
    public void onTrackVolumeChange(int progress) {

    }

    @Override
    public void onOriginalStopTrackingTouch(int progress, int value) {
        shortVideoEntity.setOriginalMixFactor(value);
        playerEngine.setOriginalMixFactor(value);
        mOriginalMixFactor = value;
        hasModify = true;
        if (shortVideoEntity.isImport()) {
            reload();
            seekTo(0);
        }
    }

    public enum EditType {
        NONE,
        FILTER,
        EFFECT,
        VOLUME;

        private EditType() {
        }
    }

    @Override
    protected void onDestroy() {
        if (!playerEngine.isNull()) {
            playerEngine.release();
            playerEngine = null;
        }
        super.onDestroy();
    }
}
