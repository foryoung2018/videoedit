package com.wmlive.hhvideo.heihei.record.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dongci.sun.gpuimglibrary.api.DCVideoManager;
import com.dongci.sun.gpuimglibrary.api.listener.DCVideoListener;
import com.dongci.sun.gpuimglibrary.common.FileUtils;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.heihei.beans.frame.FrameInfo;
import com.wmlive.hhvideo.heihei.beans.record.ClipVideoEntity;
import com.wmlive.hhvideo.heihei.beans.record.EditVideoModel;
import com.wmlive.hhvideo.heihei.beans.record.MusicInfoEntity;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.db.ProductEntity;
import com.wmlive.hhvideo.heihei.record.adapter.EditVideoAdapter;
import com.wmlive.hhvideo.heihei.record.engine.PlayerEngine;
import com.wmlive.hhvideo.heihei.record.engine.VideoEngine;
import com.wmlive.hhvideo.heihei.record.engine.config.aVideoConfig;
import com.wmlive.hhvideo.heihei.record.engine.constant.SdkConstant;
import com.wmlive.hhvideo.heihei.record.engine.content.PlayerContentFactory;
import com.wmlive.hhvideo.heihei.record.engine.listener.PlayerCreateListener;
import com.wmlive.hhvideo.heihei.record.engine.listener.PlayerListener;
import com.wmlive.hhvideo.heihei.record.engine.listener.VideoListener;
import com.wmlive.hhvideo.heihei.record.engine.model.MAsset;
import com.wmlive.hhvideo.heihei.record.engine.model.MediaObject;
import com.wmlive.hhvideo.heihei.record.engine.model.Scene;
import com.wmlive.hhvideo.heihei.record.engine.utils.VideoUtils;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.manager.RecordSetting;
import com.wmlive.hhvideo.heihei.record.utils.RecordFileUtil;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtilSdk;
import com.wmlive.hhvideo.heihei.record.widget.CircleProgressDialog;
import com.wmlive.hhvideo.heihei.record.widget.CustomDragRecyclerView;
import com.wmlive.hhvideo.heihei.record.widget.CustomFrameView;
import com.wmlive.hhvideo.heihei.record.widget.CustomTrimMusicView;
import com.wmlive.hhvideo.heihei.record.widget.CustomTrimVideoView;
import com.wmlive.hhvideo.heihei.record.widget.SmallFrameView;
import com.wmlive.hhvideo.heihei.record.widget.SysAlertDialog;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.FileUtil;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ParamUtis;
import com.wmlive.hhvideo.utils.ScreenUtil;
import com.wmlive.hhvideo.utils.SdkUtils;
import com.wmlive.hhvideo.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindInt;
import butterknife.BindView;
import cn.wmlive.hhvideo.R;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.wmlive.hhvideo.heihei.record.engine.model.MediaObject.MediaObjectTypeImage;
import static com.wmlive.hhvideo.heihei.record.manager.RecordManager.SUFFIX_AUDIO_FILE;

//import com.rd.vecore.Music;
//import com.rd.vecore.VirtualVideo;
//import com.rd.vecore.VirtualVideoView;
//import com.rd.vecore.exception.InvalidArgumentException;
//import com.rd.vecore.exception.InvalidStateException;
//import com.rd.vecore.models.AspectRatioFitMode;
//import com.rd.vecore.models.MediaObject;
//import com.rd.vecore.models.PermutationMode;
//import com.rd.vecore.models.Scene;

/**
 * 编辑整个作品的页面，包括调整音量，裁剪，调序
 */
public class EditProductionActivity extends DcBaseActivity {

    public static final String TAG = EditProductionActivity.class.getSimpleName();
    public static final String REQUEST_PAGE_TYPE = "request_page_type";
    public static final String CURRENT_INDEX = "current_index";
    public static final String CURRENT_TYPE = "current_type";
    public static final int REQUEST_PAGE_TYPE_VOLUME = 1; // 调节音量
    public static final int REQUEST_PAGE_TYPE_EDITING = 2; // 编辑裁剪
    public static final int REQUEST_PAGE_TYPE_SORT = 3; // 排序
    private static final int MESSAGE_REFRESH_PLAYER = 14;
    private static final int MESSAGE_REFRESH_MUSIC = 15;
    private static final int MESSAGE_GET_SNAPSHOT = 16;

    @BindView(R.id.rlRoot)
    RelativeLayout rlRoot;
    @BindView(R.id.rlPlayerContainer)
    RelativeLayout rlPlayerContainer;
    @BindView(R.id.customFrameView)
    CustomFrameView customFrameView;

    @BindView(R.id.llVolumeBottomMenu)
    LinearLayout llVolumeBottomMenu;
    @BindView(R.id.llSortBottomMenu)
    LinearLayout llSortBottomMenu;
    @BindView(R.id.customDragRecyclerView)
    CustomDragRecyclerView customDragRecyclerView;

    @BindView(R.id.sbVoiceOriginal)
    SeekBar mSbVoiceOriginal;
//    @BindView(R.id.videoViewsdk)
//    TextureView videoViewSdk;
    @BindView(R.id.videoplayercontainer)
    FrameLayout videoContainer;

    @BindView(R.id.ivPlay)
    ImageView ivPlay;
    @BindView(R.id.ivVideoImage)
    ImageView ivVideoImage;
    @BindView(R.id.content)
    FrameLayout frameContent;

    //    private int mMusicMixFactor;
    private int mOriginalMixFactor;
    private int mOriginalShowMixFactor;

    private FrameInfo mFrameInfo;
    private int requestPageType;
    private PlayerEngine playerEngine;

    private EditVideoAdapter editVideoAdapter;
    private Scene<MediaObject> mScene;
    private List<MediaObject> mediaVideoList;
    private float[] mStartTimeArray; // 开始时间
    private float[] mEndTimeArray; // 结束时间
    private float[] mDurationTimeArray; // 视频截取时间
    private float[] mMaxTimeArray; // 视频真实最大时间
    private boolean needRefresh = true; // 是否需要刷新控件
    private float maxVideoDuration = 0;
    private float mTrimMaxDuration = RecordSetting.MAX_VIDEO_DURATION / 1000f; //最大截取时间(单位秒)
    private float minValueMusic; // 音乐起始时间
    private float maxValueMusic; // 音乐结束时间
    private float maxValuePlayMusic; // 播放的最大声音
    private CustomTrimMusicView mCustomTrimMusicView;
    private boolean hasMusic;
    private boolean isFirstInit = true; //首次初始化
    private List<SmallFrameView> itemViewList;
    private List<Integer> itemIndexList; // 存储位置变化
    private EditProductionActivity mContext;
    private List<Integer> hasEditVideos; // 纪录编辑视频
    private boolean needResetPlayer = true;
    private boolean needInitPlayer = true;
    private int[] screenSize;
    private boolean hasInitVideoAdapter;
    private int currentIndex;
    private int newCurrentIndex;
    private boolean hasSizeChange; // 视频展示大小变化

    private float resizeRate = 1;

    List<MediaObject> mediaObjects;

    public static Map<Integer,String> audiosMap = new HashMap<Integer,String>();

    ProductEntity productEntityTemp;

    private boolean isDestory = false;

    public static void startEditProductionActivity(Activity context, int requestPageType) {
        startEditProductionActivity(context, requestPageType, -1);
    }

    public static void startEditProductionActivity(Activity context, int requestPageType, int currentIndex) {
        Intent intent = new Intent(context, EditProductionActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(EditProductionActivity.REQUEST_PAGE_TYPE, requestPageType);
        bundle.putInt(EditProductionActivity.CURRENT_INDEX, currentIndex);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, PublishActivity.REQUEST_NEED_RELOAD);
    }

    @Override
    protected void onSingleClick(View v) {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_edit_production;
    }

    @Override
    protected void initData() {
        super.initData();
        mContext = EditProductionActivity.this;
        Intent intent = getIntent();
        if (intent != null) {
            try {
                productEntityTemp = (ProductEntity)RecordManager.get().getProductEntity().clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            screenSize = DeviceUtils.getScreenWH(mContext);
            playerEngine = new PlayerEngine();
            mFrameInfo = RecordManager.get().getFrameInfo();
            initDataList();
            requestPageType = intent.getIntExtra(EditProductionActivity.REQUEST_PAGE_TYPE, REQUEST_PAGE_TYPE_VOLUME);
            currentIndex = intent.getIntExtra(EditProductionActivity.CURRENT_INDEX, -1);
//            mMusicMixFactor = RecordManager.get().getProductEntity().musicMixFactor;
            mOriginalMixFactor = RecordManager.get().getProductEntity().originalMixFactor;
            mOriginalShowMixFactor = RecordManager.get().getProductEntity().originalShowMixFactor;
            // 判断音乐是否存在
            MusicInfoEntity musicInfoEntity = RecordManager.get().getProductEntity().musicInfo;
            ParamUtis.setLayoutParams2(this, videoContainer, mFrameInfo.canvas_height);
            initAudios();
            if (null != musicInfoEntity && !TextUtils.isEmpty(musicInfoEntity.getMusicPath())) {
                hasMusic = true;
                minValueMusic = musicInfoEntity.trimStart;
                maxValuePlayMusic = maxValueMusic = musicInfoEntity.trimEnd;
                KLog.i(TAG, "initData : maxValue " + maxValueMusic);
            }
            if (REQUEST_PAGE_TYPE_VOLUME == requestPageType) {
                initVolumeView();

                initPlayer();
            } else if (REQUEST_PAGE_TYPE_EDITING == requestPageType) {
                initEditingView();
                int minHeigt = screenSize[1] - (int) (screenSize[0] * 4 / 3.0f) - DeviceUtils.dip2px(mContext, 44) - DeviceUtils.getStatusBarHeight(mContext);
//                customDragRecyclerView.setPeekHeight(DeviceUtils.dip2px(mContext, 200), true);
                customDragRecyclerView.setPeekHeight(DeviceUtils.dip2px(mContext, 200), minHeigt, true);

                initPlayer();
            }

            initTitleBar();
        } else {
            toastFinish();
        }
    }

    /**
     * 初始化临时音频
     */
    public static void initAudios() {
        if(RecordManager.get().getProductEntity()==null)
            return;
        audiosMap = new HashMap<Integer,String>();
//        String[] audios = PublishActivity.getAudios();
//        String[] result = new String[audios.length];
//
//        Iterator iter = audiosMap.entrySet().iterator();
//        while (iter.hasNext()) {
//
//            Map.Entry entry = (Map.Entry) iter.next();
//
//            int key = Integer.parseInt(entry.getKey().toString());
//
//            String val = entry.getValue().toString();
//            String tempAudio = RecordFileUtil.createAudioFile(RecordManager.get().getShortVideoEntity(key).baseDir);
//            FileUtil.copyFile(new File(val),new File(tempAudio));
//            audiosMap.put(key,tempAudio);
//        }
    }


    /**
     * 初始化播放器
     */
    private void initPlayer() {
        if (playerEngine == null)
            return;
        playerEngine.reset();
        float v = mFrameInfo.opus_width * 1.0f / mFrameInfo.opus_height;
        // 设置视频宽高比
        playerEngine.setPreviewAspectRatio(v);
        Log.d("ggq", "initPlayer: ratio===" + v);
        playerEngine.setAspectRatioFitMode(MediaObject.FillTypeScaleToFit);
        int len = mFrameInfo.getLayout().size();
        if (len > 0) {
            //加载全部视频
            loadScence();
            aVideoConfig videoConfig = VideoUtils.getMediaInfor("");
//                boolean hasVideo = reload(playerEngine);
            KLog.i(TAG, "initPlayerData: initPlayer--=!");
            if (mediaObjects.size() > 0) {
                resizeRate = (resizeRate > 0 && resizeRate < 1) ? resizeRate : 1;
                TextureView textureView = new TextureView(this);
                videoContainer.removeAllViews();
                videoContainer.addView(textureView);
                Log.d("ggq", "initPlayer: videoConfig.getVideoWidth() * resizeRate==" + videoConfig.getVideoWidth() * resizeRate + "  videoConfig.getVideoHeight()==" + videoConfig.getVideoHeight());
                playerEngine.build(textureView, (int) (videoConfig.getVideoWidth() * resizeRate), (int) (videoConfig.getVideoHeight()), 24, false, new PlayerCreateListener() {
                    @Override
                    public void playCreated() {
                        dismissDialog();
                        setPlayListener();
                        if(playerEngine!=null){
                            playerEngine.setMediaAndPrepare(mediaObjects);
                            playerEngine.setAutoRepeat(false);
                        }
                    }
                });
            } else {
                KLog.e(TAG, "initPlayerData: 没有视频!");
                dismissDialog();
            }
        }else {
            dismissDialog();
        }

    }

    private void setPlayListener() {
        if (playerEngine != null) {
            playerEngine.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            playerEngine.setOnPlaybackListener(new PlayerListener() {
                @Override
                public void onPlayerPrepared() {
                    if (needRefresh || needInitPlayer) {
                        needInitPlayer = false;
                        start();
                    }
                    if (requestPageType == REQUEST_PAGE_TYPE_EDITING) {
                        if (!hasInitVideoAdapter) {
                            hasInitVideoAdapter = true;
                            editVideoAdapter.init();
                        }
                        if (hasSizeChange) {
                            hasSizeChange = false;
                            mHandler.removeMessages(MESSAGE_GET_SNAPSHOT);
                            mHandler.sendEmptyMessageDelayed(MESSAGE_GET_SNAPSHOT, 200);
                        }
                    }
                }

                @Override
                public boolean onPlayerError(int what, int extra) {
                    return false;
                }

                @Override
                public void onPlayerCompletion() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ivPlay.setVisibility(View.VISIBLE);
                        }
                    });
                    needResetPlayer = true;
                }

                @Override
                public void onGetCurrentPosition(float position) {
                    KLog.i("onGetCurrentPosition-edit" + position);
                }
            });

            playerEngine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (playerEngine != null && playerEngine.isPlaying()) {
                        pause(false);
                    } else {
                        if (needInitPlayer) {
                            initPlayer();
                        } else {
                            start();
                        }
                    }
                }
            });
        }
    }


    /**
     * pre: 当前播放器已经初始化完成
     * 给播放视频添加播放资源
     *
     * @param
     */
    private List<MediaObject> loadScence() {
        PlayerContentFactory factory = new PlayerContentFactory();
//        mediaObjects = factory.getPlayerMediaFromProductNew(RecordManager.get().getProductEntity());
        mediaObjects = factory.getPlayerMediaFromProductWidthAudio(productEntityTemp);

        KLog.i("loadScence--resizeRate>" + mediaObjects.size());
        return mediaObjects;
    }

    /**
     * 加载视频资源
     *
     * @param
     */

    private boolean reload(PlayerEngine playerEngine) {
        int len = mFrameInfo.getLayout().size();
        mScene = new Scene();
        boolean hasVideo = false;
        // 添加画框
        if (!TextUtils.isEmpty(mFrameInfo.sep_image)) {
            String frameImage = RecordFileUtil.getFrameImagePath(mFrameInfo.sep_image);
            KLog.e("frameImage:" + frameImage);
            if (frameImage != null) {
//                MediaObject mediabg = null;
////                try {
//                mediabg = new MAsset(frameImage);
//                mediabg.setShowRectF(new RectF(0, 0, 1, 1));//显示区域
//                mScene.addMedia(mediabg);
                hasVideo = true;
//                } catch (InvalidArgumentException e) {
//                    e.printStackTrace();
//                }
            }
        }
        mediaVideoList.clear();
        int count = mFrameInfo.getLayout().size();
        for (int i = 0; i < count; i++) {
            mediaVideoList.add(null);
        }
        for (int i = 0; i < len; i++) {

            ShortVideoEntity shortVideoEntity = RecordManager.get().getShortVideoEntity(i);
            if (null != shortVideoEntity && !TextUtils.isEmpty(shortVideoEntity.editingVideoPath)) {
                // 设置单个视频信息
                MediaObject media = null;
//                try {
                media = new MAsset(shortVideoEntity.editingVideoPath);
                media.setShowRectF(mFrameInfo.getLayoutRelativeRectF(i, 0f));
                media.setAspectRatioFitMode(MediaObject.FillTypeScaleToFit);
//                } catch (InvalidArgumentException e) {
//                    e.printStackTrace();
//                }
                if (media != null) {
                    int originalMix = shortVideoEntity.getOriginalMixFactor();
                    if (REQUEST_PAGE_TYPE_VOLUME == requestPageType) {
                        originalMix = (originalMix == 0 ? 1 : originalMix);
                        if (mOriginalMixFactor == 0) {
                            originalMix = 0;
                        } else if (mOriginalMixFactor <= 250) {
                            originalMix = (int) (originalMix * mOriginalMixFactor / 250f);
                        } else if (mOriginalMixFactor <= 500) {
                            originalMix = (int) (originalMix * mOriginalMixFactor / 250f + 250 - originalMix);
                        }
                        originalMix = originalMix > 500 ? 500 : originalMix;
                        originalMix = originalMix < 0 ? 0 : originalMix;
                    }
                    media.setAudioMute(true);
                    media.setMixFactor(originalMix);
                    float voiceStartTime = mStartTimeArray[i];

                    float trimDuration = shortVideoEntity.getDuring();
                    if (mStartTimeArray[i] != 0 || mEndTimeArray[i] != 0) {
                        trimDuration = (mEndTimeArray[i] - mStartTimeArray[i])*1000; // 截取时长
                    }

                    // 添加原声
                    if (shortVideoEntity.isUseOriginalAudio()) {
                        if (shortVideoEntity.getClipList().size() > 0) {
                            float totalTime = 0; // 所有视频时间
                            float timeline = 0; // 添加的视频时间线位置
                            for (ClipVideoEntity clipVideoEntity : shortVideoEntity.getClipList()) {
                                if (!TextUtils.isEmpty(clipVideoEntity.audioPath)
                                        && new File(clipVideoEntity.audioPath).exists()) {
                                    if (voiceStartTime < totalTime + clipVideoEntity.getDuring() && totalTime < trimDuration) {
                                        // 多段音频裁剪，找到在裁剪范围内的音频
                                        float startTime = voiceStartTime - totalTime;
                                        // 确定每个音频开始裁剪的位置(第一个找到的音频裁剪开始位置可能不为0 )
                                        KLog.i("=====添加原音:" + clipVideoEntity.audioPath);
                                        float during = clipVideoEntity.getDuring();
                                        if (totalTime + clipVideoEntity.getDuring() > trimDuration) {
                                            during = trimDuration - totalTime;
                                        }
//                                        try {
//                                            virtualVideo.addMusic(
//                                                    clipVideoEntity.audioPath,
//                                                    startTime,  // 音乐截取开始位置
//                                                    startTime + during, // 音乐截取结束位置
//                                                    timeline, // 音乐在主时间线的开始位置
//                                                    timeline + during, // 音乐在主时间线的结束位置
//                                                    originalMix,
//                                                    (float) RecordSpeed.getSpeed(clipVideoEntity.speedIndex),
//                                                    true);
//                                        } catch (InvalidArgumentException e) {
//                                            e.printStackTrace();
//                                        }
                                        timeline += clipVideoEntity.getDuring() - startTime;
                                        totalTime += clipVideoEntity.getDuring();
                                        voiceStartTime = totalTime;
                                    } else {
                                        totalTime += clipVideoEntity.getDuring();
                                    }
                                }
                            }
                        } else {
                            // 有音轨的视频将音频分离加载，使音频大小可调节
//                            Music originalVoice = null;
//                            try {
//                                originalVoice = VirtualVideo.createMusic(shortVideoEntity.editingVideoPath);
//                                if (mStartTimeArray[i] != 0 || mEndTimeArray[i] != 0) {
//                                    float during = mEndTimeArray[i] - mStartTimeArray[i];
//                                    originalVoice.setTimeRange(mStartTimeArray[i], mEndTimeArray[i]);
//                                    originalVoice.setTimelineRange(0, during);
//                                } else {
//                                    originalVoice.setTimeRange(0, shortVideoEntity.getDuring());
//                                    originalVoice.setTimelineRange(0, shortVideoEntity.getDuring());
//                                }
//                                virtualVideo.addMusic(originalVoice, true);
//                            } catch (InvalidArgumentException e) {
//                                e.printStackTrace();
//                            }
//                            if (originalVoice != null) {
//                                originalVoice.setMixFactor(originalMix);
//                            }
//                            media.setMixFactor(originalMix);
                        }
                    }

                    mMaxTimeArray[i] = media.getDuration();
                    // 计算视频最大时长 maxVideoDuration
                    if (needRefresh) { // 得到视频最长时间，不会多次刷新
                        if (media.getDuration() > maxVideoDuration) {
                            maxVideoDuration = media.getDuration();
                        }
                    }
                    if (mStartTimeArray[i] != 0 || mEndTimeArray[i] != 0) {
                        media.setTimeRange(mStartTimeArray[i]*1000 , (mEndTimeArray[i] *1000) );
                    }
                    mDurationTimeArray[i] = media.getDuration();

                    //演示：自定义裁剪区域，
                    media.setShowRectF(RecordFileUtil.getClipSrc(media.getWidth(), media.getHeight(), mFrameInfo.getLayoutAspectRatio(i), 0f));
                    mediaVideoList.add(i, media);
                    mScene.addMedia(media);
                    hasVideo = true;
                }
            }
        }
        if (needRefresh) {
            // 得到视频最终时长
            maxVideoDuration = maxVideoDuration > mTrimMaxDuration ? mTrimMaxDuration : maxVideoDuration;
            for (int i = 0; i < count; i++) {
                ShortVideoEntity shortVideoEntity = RecordManager.get().getShortVideoEntity(i);
                if (null != shortVideoEntity && !TextUtils.isEmpty(shortVideoEntity.editingVideoPath)) {
                    MediaObject media = null;
//                    try {
                    media = new MAsset(shortVideoEntity.editingVideoPath);
                    if (media.getDuration() > maxVideoDuration) {
                        hasEditVideos.set(i, i);
                    }
//                    } catch (InvalidArgumentException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        }
        maxValuePlayMusic = 0;
        for (int i = 0; i < count; i++) {
            if (mDurationTimeArray[i] > maxValuePlayMusic) {
                maxValuePlayMusic = mDurationTimeArray[i];
            }
        }
        if (maxValuePlayMusic > maxVideoDuration) {
            maxValuePlayMusic = maxVideoDuration;
        }
        if (hasVideo) {
            mScene.setPermutationMode(1);
//            mScene.setPermutationMode(PermutationMode.COMBINATION_MODE);
            playerEngine.addScene(mScene);
        }
        return hasVideo;
    }

    public void start() {
        if (playerEngine == null || playerEngine.isNull()) {
            return;
        }
        if (needResetPlayer) {
            playerEngine.seekTo(0);
        }
        playerEngine.start();
        ivPlay.setVisibility(View.INVISIBLE);
    }

    public void pause(boolean needResetPlayer) {
        this.needResetPlayer = needResetPlayer;
        if (playerEngine == null || playerEngine.isNull()) {
            return;
        }
        if (needResetPlayer) {
            playerEngine.seekTo(0f);
        }
        playerEngine.pause();
        ivPlay.setVisibility(View.VISIBLE);
//        if (hasMusic) {
//            mHandler.removeCallbacks(progressRunnable);
//        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        needRefresh = true;
        isFirstInit = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (REQUEST_PAGE_TYPE_EDITING == requestPageType
                || REQUEST_PAGE_TYPE_VOLUME == requestPageType) {
            if (isFirstInit) {
                isFirstInit = false;
            } else {
                // 恢复页面播放
                if (playerEngine != null && !playerEngine.isNull() && !playerEngine.isPlaying()) {
                    KLog.i(TAG, "onResume start video");
                    start();
                }
            }
        }
//        if(videoViewSdk!=null)
//            videoViewSdk.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (REQUEST_PAGE_TYPE_EDITING == requestPageType
                || REQUEST_PAGE_TYPE_VOLUME == requestPageType) {
            pause(true);
        }
        if (playerEngine != null && !playerEngine.isNull()) {
            playerEngine.release();
            playerEngine = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        productEntityTemp = null;
        isDestory = true;

    }


    /**
     * 初始化编辑页面
     */
    private void initEditingView() {
        llVolumeBottomMenu.setVisibility(View.GONE);
        llSortBottomMenu.setVisibility(View.GONE);
        setTitle("编辑视频", false);
        customFrameView.setVisibility(View.GONE);
        // 设置停靠高度
        customDragRecyclerView.setPeekHeight(300);
        customDragRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        List<EditVideoModel> models = new ArrayList<EditVideoModel>();
        // 添加视频信息
        List<ShortVideoEntity> shortVideoList = RecordManager.get().getProductEntity().shortVideoList;
        for (int i = 0, count = shortVideoList.size(); i < count; i++) {
            ShortVideoEntity shortVideoEntity = shortVideoList.get(i);
            if (shortVideoEntity != null && !TextUtils.isEmpty(shortVideoEntity.editingVideoPath)) {
                models.add(new EditVideoModel(EditVideoAdapter.VIEW_TYPE_EDITING_VIDEO, shortVideoEntity, i));
            }
        }
        // 添加音乐信息
        MusicInfoEntity musicInfo = RecordManager.get().getProductEntity().musicInfo;
        if (musicInfo != null && !TextUtils.isEmpty(musicInfo.getMusicPath())) {
            models.add(new EditVideoModel(EditVideoAdapter.VIEW_TYPE_EDITING_MUSIC, RecordManager.get().getProductEntity().musicInfo));
        }
        for (ShortVideoEntity entity : RecordManager.get().getProductEntity().shortVideoList) {
            KLog.i(TAG, "combinePreview--->" + entity.editingVideoPath);
        }

        maxVideoDuration = getMaxDuration(shortVideoList);
        KLog.d(TAG, "play-source---maxVideoDuration>" + maxVideoDuration);
        editVideoAdapter = new EditVideoAdapter(mContext, models);
        customDragRecyclerView.setAdapter(editVideoAdapter);
        // 设置初始化回调
        editVideoAdapter.setOnEditVideoCallback(new EditVideoAdapter.OnEditVideoCallback() {

            @Override
            public void initVideoGallery(int position, CustomTrimVideoView customTrimVideoView, int index) {
                KLog.d(TAG, position + "onBindViewHolder-->" + needRefresh);
                if (needRefresh) {
                    initThumbnail(position, customTrimVideoView, index);
                }
            }

            @Override
            public void initMusicView(int position, CustomTrimMusicView customTrimMusicView) {
                if (needRefresh) {
                    initMusic(position - 1, customTrimMusicView);
                }
            }
        });

        customDragRecyclerView.setOnSizeChangeListener(new CustomDragRecyclerView.OnSizeChangeListener() {
            @Override
            public void onSizeChangeStart() {
                if (playerEngine == null || playerEngine.getWidth() == 0 || playerEngine.getHeight() == 0) {
                    return;
                }
//                orginWidth = playerEngine.getWidth();
                pause(true);
                ivVideoImage.setVisibility(View.VISIBLE);
                playerEngine.setVisibility(View.GONE);
            }

            @Override
            public void onSizeChanged(int width, int height) {
                hasSizeChange = true;
                Log.d("ggq", "onSizeChanged: width==" + width + "  height==" + height);
                int rootHeight = rlRoot.getMeasuredHeight();
                int newHeight = rootHeight - height;
                ViewGroup.LayoutParams layoutParams = rlPlayerContainer.getLayoutParams();
                layoutParams.height = newHeight;
                rlPlayerContainer.setLayoutParams(layoutParams);
                ParamUtis.setLayoutParam(EditProductionActivity.this, videoContainer, mFrameInfo.canvas_height,ScreenUtil.px2dip(EditProductionActivity.this,height));
                if (playerEngine != null && playerEngine.isPlaying()) {
                    playerEngine.pause();
                }
            }

            @Override
            public void onSizeChangeEnd() {
                if (playerEngine != null && !playerEngine.isNull())
                    playerEngine.setVisibility(View.VISIBLE);
                ivVideoImage.setVisibility(View.GONE);
//                playerEngine.start();
            }
        });
    }

    /**
     * 获取所有视频中最长的视频长度
     *
     * @return
     */
    public float getMaxDuration(List<ShortVideoEntity> shortVideoList) {
        float temp = 0;
        for (ShortVideoEntity shortVideoEntity : shortVideoList) {
            if (shortVideoEntity.getDuring() > temp) {
                temp = shortVideoEntity.getDuring();
            }
        }
        return temp * 1000;
    }

    private void initMusic(int position, CustomTrimMusicView customTrimMusicView) {
        mCustomTrimMusicView = customTrimMusicView;
        customTrimMusicView.setOnRangeChangeListener(onRangeChangeListener);
        customTrimMusicView.setMultEdit(true);
        customTrimMusicView.setDuration(maxVideoDuration);
    }


    /**
     * 裁剪音乐回调
     */
    private CustomTrimMusicView.OnRangeChangeListener onRangeChangeListener = new CustomTrimMusicView.OnRangeChangeListener() {
        @Override
        public void rangeSeekBarValuesChanged(float minValue, float maxValue, float currentValue, float maxCropMusic) {
            KLog.i(TAG, "OnRangeChangeListener rangeSeekBarValuesChanged : currentValue " + currentValue + " maxValue " + maxValue);
            minValueMusic = currentValue;
            maxValueMusic = maxCropMusic;
        }

        @Override
        public void rangeSeekBarValuesChanging(float setValue) {

        }

        @Override
        public void onScrollChanged(float minValue, float maxValue, float currentValue, float maxCropMusic, boolean isUserScroll) {
            KLog.i(TAG, "OnRangeChangeListener onScrollChanged : currentValue " + currentValue + " maxValue " + maxCropMusic);
            minValueMusic = currentValue;
            maxValueMusic = maxCropMusic;
            needRefresh = false;
        }

    };

    /**
     * 初始化滑动截取控件
     */
    private void initThumbnail(final int position, final CustomTrimVideoView mCustomTrimVideoView, final int index) {
        if (RecordManager.get().getProductEntity()==null || RecordManager.get().getProductEntity().shortVideoList.size() < position || position < 0)
            return;

        mCustomTrimVideoView.setOnRangeChangeListener(new CustomTrimVideoView.OnRangeChangeListener() {
            @Override
            public void onValuesChanged(int minValue, int maxValue, int duration, int changeType) {
                mStartTimeArray[index] = minValue;
                mEndTimeArray[index] = maxValue;//
                hasEditVideos.set(index, index);
                refreshPlayTimeLine(index, 400, true, minValue, duration);
                Log.d("tag", index+"onValuesChanged-->" + maxValue + "min-value" + minValue);
            }
        });
        //需要视频的截图，只需要参数 视频的地址
        ShortVideoEntity shortVideoEntity = RecordManager.get().getProductEntity().shortVideoList.get(index);
        String editingVideoPath = shortVideoEntity.editingVideoPath == null ? shortVideoEntity.importVideoPath : shortVideoEntity.editingVideoPath;
        if (editingVideoPath == null)
            return;
        PlayerEngine playerEngine1 = new PlayerEngine();
        long duration = VideoUtils.getVideoLength(editingVideoPath) / 1000000;
        float max = duration < mTrimMaxDuration ? duration : mTrimMaxDuration;
        playerEngine1.setSnapShotResource(editingVideoPath);
        float d = ((mEndTimeArray[index] - mStartTimeArray[index]) );
        Log.d("tag", "ddd000--pre>" + d );//d * 1000

        if (d == 0) {
            d = duration*1000;
        } else {
//            d = duration - mStartTimeArray[index] / 1000;
        }
        Log.d("tag", "ddd000-->" + d + "start:"+(int) (mStartTimeArray[index] )+"End:"+mEndTimeArray[index]);//d * 1000
        mCustomTrimVideoView.setPlayer(playerEngine1, (int) (d ), (int) (duration * 1000), (int) maxVideoDuration, (int) (mStartTimeArray[index] ));
    }

    /**
     * 添加下面 剪切时间的 显示控件
     *
     * @param index
     * @return
     */
    private TextureView addTextureView(int index) {
        //移除掉之前 已经添加过的，
        for (int i = 0; i < frameContent.getChildCount(); i++) {
            if (Integer.parseInt(frameContent.getChildAt(i).getTag().toString()) == index) {//已经存在
                frameContent.removeViewAt(i);
                break;
            }
        }
        //每次都要添加一个新的
        TextureView tv = new TextureView(this);
        tv.setTag(index);
        tv.setVisibility(View.GONE);
        frameContent.addView(tv);
        return tv;
    }

    private void initDataList() {
        if (mFrameInfo == null) {
            return;
        }
        int count = mFrameInfo.getLayout().size();
        mStartTimeArray = new float[count];
        mEndTimeArray = new float[count];
        mDurationTimeArray = new float[count];
        mMaxTimeArray = new float[count];
        mediaVideoList = new ArrayList<MediaObject>();
        hasEditVideos = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            mediaVideoList.add(null);
            hasEditVideos.add(-1);
            float[] trimRange = RecordManager.get().getShortVideoEntity(i).getTrimRange();
            mStartTimeArray[i] = trimRange[0] ;
            mEndTimeArray[i] = (trimRange[1] );///1000f
            KLog.i(i + "initTime-->" + mEndTimeArray[i] + "start->" + mStartTimeArray[i]);
        }
    }

    /**
     * 调整播放控件大小
     */
    private void adjustPlayerSize() {
        rlPlayerContainer.post(new Runnable() {
            @Override
            public void run() {
                int width = rlPlayerContainer.getMeasuredWidth();
                if (0 != width) {
                    ViewGroup.MarginLayoutParams containerParams = (ViewGroup.MarginLayoutParams) rlPlayerContainer.getLayoutParams();
                    containerParams.width = width;
                    containerParams.height = (int) (width / (mFrameInfo.canvas_width * 1.0f / mFrameInfo.canvas_height));
                    int margin = (screenSize[0] - width) / 2;
                    containerParams.setMarginStart(margin);
                    containerParams.setMarginEnd(margin);
                    rlPlayerContainer.setLayoutParams(containerParams);
                    videoContainer.setLayoutParams(containerParams);
                    Log.d(TAG, "run: containerParams==" + containerParams.height + "   containerParams" + containerParams.width);
                }
            }
        });

    }

    /**
     * 音量调节
     */
    private void initVolumeView() {
        adjustPlayerSize();
        llVolumeBottomMenu.setVisibility(View.VISIBLE);
        customDragRecyclerView.setVisibility(View.GONE);
        llSortBottomMenu.setVisibility(View.GONE);
        setTitle("调节音量", false);
        customFrameView.setVisibility(View.GONE);
//        mSbVoiceMusic.setOnSeekBarChangeListener(onVoiceChangedListener);
//        mSbVoiceMusic.setEnabled(hasMusic);

        mSbVoiceOriginal.setOnSeekBarChangeListener(onVoiceChangedListener);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
//                if (mSbVoiceMusic == null) {
//                    return;
//                }
//                if (mSbVoiceMusic.isEnabled()) {
//                    mSbVoiceMusic.setProgress(mMusicMixFactor / 2);
//                    mSbVoiceMusic.setSecondaryProgress(0);
//                } else {
//                    mSbVoiceMusic.setProgress(0);
//                    mSbVoiceMusic.setSecondaryProgress(100);
//                }
                if (mSbVoiceOriginal.isEnabled()) {
                    mSbVoiceOriginal.setProgress(mOriginalShowMixFactor);
                    mSbVoiceOriginal.setSecondaryProgress(0);
                } else {
                    mSbVoiceOriginal.setProgress(0);
                    mSbVoiceOriginal.setSecondaryProgress(100);
                }
            }
        });
    }

    /**
     * 音量大小调节回调
     */
    private SeekBar.OnSeekBarChangeListener onVoiceChangedListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (requestPageType == REQUEST_PAGE_TYPE_VOLUME) {
                int id = seekBar.getId();
                if (id == R.id.sbVoiceMusic) {
//                    mMusicMixFactor = progress * 2;
//                mVirtualVideo.setMusicMixFactor(mMusicMixFactor);
                } else if (id == R.id.sbVoiceOriginal) {
                    mOriginalShowMixFactor = progress;
                    mOriginalMixFactor = progress * 5;
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int id = seekBar.getId();
            if (id == R.id.sbVoiceMusic) {
                needRefresh = false;
            } else {
                needRefresh = false;
                pause(true);
                if (playerEngine != null) {
                    playerEngine.seekTo(0);
                }
                initPlayer();
            }
        }
    };


    /**
     * 初始化Titlebar
     */
    private void initTitleBar() {
        TextView tvNext = new TextView(this);
        tvNext.setText("确认");
        tvNext.setTextSize(16);
        TypedValue tv = new TypedValue();
        if (SdkUtils.isLollipop()) {
//            getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, tv, true);
        }
        tvNext.setBackgroundResource(tv.resourceId);
        tvNext.setTextColor(getResources().getColor(R.color.hh_color_g));
        tvNext.setGravity(Gravity.CENTER);
        tvNext.setPadding(10, 6, DeviceUtils.dip2px(mContext, 15), 6);
        setToolbarRightView(tvNext, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (REQUEST_PAGE_TYPE_VOLUME == requestPageType) {
//                    RecordManager.get().setMusicMixFactor(mMusicMixFactor);
                    if (RecordManager.get().getProductEntity().originalMixFactor != mOriginalMixFactor) {
                        RecordManager.get().setOriginalMixFactor(mOriginalMixFactor);
                        RecordManager.get().getProductEntity().setOriginalShowMixFactor(mOriginalShowMixFactor);
                        int size = RecordManager.get().getProductEntity().shortVideoList.size();
                        for (int i = 0; i < size; i++) {
                            ShortVideoEntity shortVideoEntity = RecordManager.get().getShortVideoEntity(i);
                            if (null != shortVideoEntity && !TextUtils.isEmpty(shortVideoEntity.editingVideoPath)) {
                                shortVideoEntity.hasEdited = true;
                                int originalMix = shortVideoEntity.getOriginalMixFactor() == 0 ? 1 : shortVideoEntity.getOriginalMixFactor();
                                if (mOriginalMixFactor == 0) {
                                    originalMix = 0;
                                } else if (mOriginalMixFactor <= 250) {
                                    originalMix = (int) (originalMix * mOriginalMixFactor / 250f);
                                } else if (mOriginalMixFactor <= 500) {
                                    originalMix = (int) (originalMix * mOriginalMixFactor / 250f + 250 - originalMix);
                                }
                                originalMix = originalMix > 500 ? 500 : originalMix;
                                originalMix = originalMix < 0 ? 0 : originalMix;

                                shortVideoEntity.setOriginalMixFactor(originalMix);
                            }
                        }
                        RecordManager.get().updateProduct();
                    }
                    setResult(RESULT_OK, new Intent());
                    isDestory = true;
                    finish();
                } else if (REQUEST_PAGE_TYPE_EDITING == requestPageType) {
                    // 裁剪视频放到发布页，有标识needExport
//                    clipVideo();
                    toPublish();
                }
            }
        });

        TextView tvCancel = new TextView(this);
        tvCancel.setText("取消");
        tvCancel.setTextSize(16);
        tvCancel.setBackgroundResource(tv.resourceId);
        tvCancel.setTextColor(getResources().getColor(R.color.white));
        tvCancel.setGravity(Gravity.CENTER);
        tvCancel.setPadding(DeviceUtils.dip2px(mContext, 15), 6, 10, 6);
        setToolbarLeftView(tvCancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
//                toPublish();
            }
        });
        setBlackToolbar();
    }

    private CircleProgressDialog dialog;

    /**
     * 跳转到发布页面
     */
    private void toPublish() {
        for (int i = 0, size = hasEditVideos.size(); i < size; i++) {
            if (-1 != hasEditVideos.get(i)) {
                ShortVideoEntity shortVideoEntity = RecordManager.get().getShortVideoEntity(i);
                if (shortVideoEntity != null && !TextUtils.isEmpty(shortVideoEntity.editingVideoPath)) {
                    shortVideoEntity.setTrimRange(mStartTimeArray[i] , (mEndTimeArray[i]) );
                    shortVideoEntity.setNeedExport(true);
                    shortVideoEntity.hasEdited = true;
                    KLog.i(i + "toPublish--->" + mStartTimeArray[i] + "endTime-->" + mEndTimeArray[i]);
                    KLog.i("toPublish-----publish-end0>" + shortVideoEntity.trimStart + "endTime-->" + shortVideoEntity.trimEnd);
                }
            }
        }
        RecordManager.get().updateProduct();
        //将修改后的音频赋值给前一个页面的数据
        for(int i=0;i<PublishActivity.mContext.getAudios().length;i++){
            if(audiosMap.containsKey(i)){//如果已经裁剪过了
                PublishActivity.mContext.getAudios()[i] = audiosMap.get(i).toString();
            }
        }
//        for (ShortVideoEntity shortVideoEntity : RecordManager.get().getProductEntity().shortVideoList) {
//            KLog.i("toPublish-----publish-end>" + shortVideoEntity.trimStart + "endTime-->" + shortVideoEntity.trimEnd);
//        }
        setResult(RESULT_OK, new Intent());
        isDestory = true;
        finish();
    }

    int videoIndex = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_REFRESH_PLAYER:
//                    initPlayer();/**/
                    setTimeRange(msg.arg1);
                    break;
                case MESSAGE_REFRESH_MUSIC:
                    playerEngine.seekTo(0);
//                    start(true);
//                    initPlayer();
                    break;
                case MESSAGE_GET_SNAPSHOT:
                    if (playerEngine != null && !playerEngine.isNull() && ivVideoImage != null) {
                        Bitmap bitmap1 = playerEngine.getSnapShot(playerEngine.getCurrentPosition());
                        if (bitmap1 != null)
                            ivVideoImage.setImageBitmap(bitmap1);
//                        int videoWidth = screenSize[0];
//                        int videoHeight = (int) (videoWidth * 4 / 3.0f);
//
//                        Bitmap bitmap = Bitmap.createBitmap(videoWidth, videoHeight, Bitmap.Config.ARGB_8888);
//
//                        if (!playerEngine.isNull() && ivVideoImage != null
//                                && mVirtualVideo.getSnapshot(DCApplication.getDCApp(), mVideoPlayer.getCurrentPosition(), bitmap)) {
//                            ivVideoImage.setImageBitmap(bitmap);
//                        }
                    }

                    break;
            }
        }
    };

//    /**
//     * 更新当前播放器进度
//     */
//    private Runnable progressRunnable = new Runnable() {
//        @Override
//        public void run() {
//            if (null != mVideoPlayer && mCustomTrimMusicView != null) {
//                KLog.i(TAG, "mVideoPlayer.getCurrentPostion() " + mVideoPlayer.getCurrentPosition());
//                KLog.i(TAG, "mCustomTrimMusicView.setProgress " + (int) (mCustomTrimMusicView.getMinMusic() + mVideoPlayer.getCurrentPosition() * 1000));
//                int musicProgress = (int) (mCustomTrimMusicView.getMinMusic() + mVideoPlayer.getCurrentPosition() * 1000);
//                if (musicProgress <= maxValueMusic * 1000) {
//                    mCustomTrimMusicView.setProgress(musicProgress);
//                    mHandler.removeCallbacks(this);
//                    mHandler.postDelayed(this, 150);
//                }
//            }
//
//        }
//    };

    /***
     * 刷新视频的显示时间
     * @param position
     * @param delayedTime 延时时间
     * @param isUserScroll
     */
    private void refreshPlayTimeLine(int position, int delayedTime, boolean isUserScroll, int start, int duration) {
        mHandler.removeMessages(MESSAGE_REFRESH_PLAYER);
        Message msg = mHandler.obtainMessage();
        msg.what = MESSAGE_REFRESH_PLAYER;
        msg.arg1 = position;
        mHandler.sendMessageDelayed(msg, delayedTime);
//        mHandler.sendEmptyMessageDelayed(MESSAGE_REFRESH_PLAYER, delayedTime);
        KLog.i("hsing", "refresh video player" + position);
        needRefresh = false;
        if (isUserScroll) {
            hasEditVideos.set(position, position);
            pause(true);
            //播放器定位到指定的位置
            //重新设置资源，设置 TimeRange(start,duration)

        }
    }

    /**
     * @param index
     */
    private void setTimeRange(int index) {
        KLog.i(mediaObjects.size() + "setTimeRange==->" + index + "mStartTimeArray" + mStartTimeArray[index] + "mEndTimeArray" + mEndTimeArray[index]);
        mediaObjects = loadScence();
//        for (int i = 0; i < mediaObjects.size(); i++) {
//            MediaObject mediaObject = mediaObjects.get(i);
            if (index<mEndTimeArray.length && mEndTimeArray[index] > 0) {
//                mediaObject.setTimeRange(((long) mStartTimeArray[i] )*1000, ((long) (mEndTimeArray[i])*1000));
//                KLog.i("setTimeRange==->" + mediaObject.getTimeRange().toString() + "mStartTimeArray" + mStartTimeArray[i]*1000 + "mEndTimeArray" + mEndTimeArray[i]*1000);
                //临时播放使用
                ShortVideoEntity shortVideoEntity = productEntityTemp.shortVideoList.get(index);
                shortVideoEntity.setTrimRange(((long) mStartTimeArray[index] ), (long) (mEndTimeArray[index]));
                trimAudio(index,shortVideoEntity.editingAudioPath,(long)mStartTimeArray[index]*1000000,(long)mEndTimeArray[index]*1000000,shortVideoEntity.volume);
            }
//        }
        if (playerEngine != null)
            playerEngine.setMediaAndPrepare(mediaObjects);
    }

    /**
     * 裁剪音频，
     * 合成音频
      */
    private void trimAudio(int key,String audioPath,long startTime,long endTime,float volume){
        String outpath = audioPath.substring(0, audioPath.length() - 4) + "_output" + SUFFIX_AUDIO_FILE;
        FileUtils.createFile(outpath);
        long d = endTime - startTime;
        DCVideoManager dcVideoManager = new DCVideoManager();
        dcVideoManager.cutAudio(audioPath, outpath, startTime, d, volume, new DCVideoListener() {

            @Override
            public void onStart() {
                showDialog2(R.string.cutting);
                KLog.i("=====onRecordEnd:--cut-start:" +startTime+"duration-->"+d);
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
                    if(DCVideoManager.getVideoLength(outpath)<200){
                        ToastUtil.showToast("裁剪失败，请重试");
                        dismissDialog();
                        return;
                    }
                    //更新数据
                    audiosMap.put(key,outpath);
                    muxAudio();
//                    FileUtil.deleteFiles();
                    // 将录制信息更新

                } else {//导出失败
//                    videoListener.onFinish(code, outpath);
                    dismissDialog();
                }
            }

            @Override
            public void onError() {
                KLog.e("=====onRecordEnd:--cut-Failed>error");
//                videoListener.onError();
                dismissDialog();
            }
        });
    }

    private void muxAudio() {
        if(PublishActivity.mContext.getAudios()==null){
            PublishActivity.initAudios1(new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    if(msg.what==0){

                    }else if(msg.what==1){
                        muxAudioDetail();
                    }else if(msg.what==2){//失败
                        ToastUtil.showToast("数据错误,请重试");
                        finish();
                    }
                }
            });
            return;
        }
        muxAudioDetail();
    }

    private void muxAudioDetail(){
        String[] temp = new String[PublishActivity.mContext.getAudios().length];
        for(int i=0;i<PublishActivity.mContext.getAudios().length;i++){
            if(audiosMap.containsKey(i)){//如果已经裁剪过了
                temp[i] = audiosMap.get(i).toString();
            }else{
                temp[i] = PublishActivity.mContext.getAudios()[i];
            }
        }
        muxAudio(temp);
    }

    /**
     * 将 音频合并成一个单独的音频文件
     * 然后添加到播放器资源中，准备播放
     */
    private void muxAudio(String[] audiores){
        mixAudio(audiores,new VideoListener() {
            @Override
            public void onStart() {
                showDialog2(R.string.loading);
            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onFinish(int code, String outpath) {//进行处理
                KLog.i("mux--finish->"+code);
                if(audiores!=null){
                    for(String audio:audiores){//删除临时音频文件
//                        FileUtil.deleteFile(audio);
                    }
                }
                if(isDestory)
                    return;

                initPlayer();
            }

            @Override
            public void onError() {
                if(isDestory)
                    return;
                KLog.e("mux--onError->");
                dismissDialog();
            }


        });
    }

    public void showDialog2(int str) {
        if(isDestory)
            return;
        if(dialog==null)
            dialog = SysAlertDialog.showCircleProgressDialog(this, getApplicationContext().getResources().getString(str), true, false);
        dialog.show();
    }

    private void dismissDialog() {
        if(isDestory)
            return;
        if(dialog!=null)
            dialog.dismiss();
    }

    private void mixAudio(String[] audiores,VideoListener videoListener) {
        long time = System.currentTimeMillis();
        if(audiores==null){//默认数据
            RecordUtilSdk.mixAudios(RecordManager.get().getProductEntity(), new VideoListener() {
                @Override
                public void onStart() {
                    if (videoListener != null)
                        videoListener.onStart();
                }

                @Override
                public void onProgress(int progress) {

                }

                @Override
                public void onFinish(int code, String outpath) {
                    KLog.i(System.currentTimeMillis()+"mixAudio---onFinish-time>" + (System.currentTimeMillis()-time));
                    KLog.i("mixAudio---onFinish>" + code+outpath);
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
            return;
        }
        ArrayList<String> audios = new ArrayList<String>();
        for(String audio:audiores){
            if(!TextUtils.isEmpty(audio))
                audios.add(audio);
        }
        RecordUtilSdk.mixAudios(audios, new VideoListener() {
            @Override
            public void onStart() {
                if (videoListener != null)
                    videoListener.onStart();
            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onFinish(int code, String outpath) {
                KLog.i(System.currentTimeMillis()+"mixAudio---onFinish-time>" + (System.currentTimeMillis()-time));
                KLog.i("mixAudio---onFinish>" + code+outpath);
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


}
