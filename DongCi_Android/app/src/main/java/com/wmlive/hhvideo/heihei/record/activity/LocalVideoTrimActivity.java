package com.wmlive.hhvideo.heihei.record.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;



import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.heihei.beans.frame.FrameInfo;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.db.ProductEntity;
import com.wmlive.hhvideo.heihei.discovery.activity.SearchVideoActivity;
import com.wmlive.hhvideo.heihei.mainhome.OnSingleClickListener;
import com.wmlive.hhvideo.heihei.record.listener.VideoJoinListener;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.manager.RecordSetting;
import com.wmlive.hhvideo.heihei.record.utils.RecordFileUtil;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtil;
import com.wmlive.hhvideo.heihei.record.widget.CircleProgressDialog;
import com.wmlive.hhvideo.heihei.record.widget.CustomTrimVideoView;
import com.wmlive.hhvideo.heihei.record.widget.ExtRadioGroup;
import com.wmlive.hhvideo.heihei.record.widget.SysAlertDialog;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.FileUtil;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.SdkUtils;
import com.wmlive.networklib.util.EventHelper;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

import static com.wmlive.hhvideo.heihei.discovery.activity.SearchVideoActivity.TYPE_FROM_RECORD;
import static com.wmlive.hhvideo.heihei.record.manager.RecordManager.PREFIX_REVERSE_FILE;
import static com.wmlive.hhvideo.heihei.record.manager.RecordManager.SUFFIX_VIDEO_FILE;

public class LocalVideoTrimActivity extends DcBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onSingleClick(View v) {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_local_video_trim;
    }


//    private Scene mScene;
//    private VirtualVideo mVirtualVideo;
//    private VideoConfig mTrimVideoConfig = new VideoConfig();
//
//    @BindView(R.id.epv_player)
//    VirtualVideoView mVideoPlayer;
    @BindView(R.id.iv_play_state)
    ImageView mIvPlayState;

    @BindView(R.id.extSpeed)
    ExtRadioGroup mExtSpeed;
    @BindView(R.id.customTrimVideoView)
    CustomTrimVideoView mCustomTrimVideoView;

    private double mCurrentSpeed = 1;
    private final double[] speeds = new double[]{(double) (1.0 / 3), (double) (1.0 / 2), 1, 2.0, 3.0};
    private int mCurrentSpeedItem = 2;
    private float mNornalSpeedDuration;
    private float mTrimMaxDuration = RecordSetting.MAX_VIDEO_DURATION / 1000f; //最大截取时间(单位秒)

    private float startTime, endTime;
    /**
     * 是否第一次启动activity
     */
    private boolean mIsFirstCreate = true;
    /**
     * 是否是从后台恢复
     */
    private boolean mIsResume = true;
    private String shortVideoPath;

//    private VirtualVideo mVvSave;
    private String mSaveMp4FileName;
    private String mImportOriginFile; // 视频导入备份文件
    private TextView tvNext;
    private CircleProgressDialog dialog;

    public static void startLocalVideoTrimActivity(Context ctx, String shortVideoPath) {
        Intent intent = new Intent(ctx, LocalVideoTrimActivity.class);
        intent.putExtra(SearchVideoActivity.SHORT_VIDEO_PATH, shortVideoPath);
        ctx.startActivity(intent);
    }

    @Override
    protected void initData() {
        super.initData();
        initView();
        initPlayer();
        KLog.i("LocalVideoTrimActivity");
    }

    private void initView() {
        Intent intent = getIntent();
        if (null != intent) {
            shortVideoPath = intent.getStringExtra(SearchVideoActivity.SHORT_VIDEO_PATH);
        }
        initTitleBar();
        mExtSpeed.setIListener(new ExtRadioGroup.IGroupListener() {
            @Override
            public void onSpeedChanged(int itemId) {
                if (mCurrentSpeedItem == itemId) {
                    return;
                }
                if (itemId == 3) {
                    if (mNornalSpeedDuration < 6) {
                        mExtSpeed.setCheckedId(mCurrentSpeedItem);
                        showToast(getString(R.string.change_speed_error_6s, RecordSetting.MIN_VIDEO_DURATION / 1000));
                        return;
                    }
                }
                if (itemId == 4) {
                    if (mNornalSpeedDuration < 9) {
                        mExtSpeed.setCheckedId(mCurrentSpeedItem);
                        showToast(R.string.change_speed_error_9s);
                        return;
                    }
                }
                mCurrentSpeedItem = itemId;
                checkSpeed(itemId);
            }
        });
        mExtSpeed.addMenu(getSpeedIndex(), RecordSetting.SPEED_TITLE);
    }

    private void initTitleBar() {
        setTitle("", true);
        setBlackToolbar();
        tvNext = new TextView(this);
        tvNext.setText("下一步");
        tvNext.setTextSize(16);
        TypedValue tv = new TypedValue();
        if (SdkUtils.isLollipop()) {
//            getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, tv, true);
        }
        tvNext.setBackgroundResource(tv.resourceId);
        tvNext.setTextColor(getResources().getColor(R.color.hh_color_g));
        tvNext.setGravity(Gravity.CENTER);
        tvNext.setPadding(10, 6, DeviceUtils.dip2px(LocalVideoTrimActivity.this, 15), 6);
        setToolbarRightView(tvNext, new OnSingleClickListener() {

            @Override
            protected void onSingleClick(View v) {
                KLog.i("xxxx", "onSingleClick");
                pause();
                exportVideo();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsResume = true;
        start();
        if (mCustomTrimVideoView != null) {
            mCustomTrimVideoView.viewThumbnailInvalidate();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mExtSpeed) {
            mExtSpeed.setIListener(null);
        }
//        if (mVideoPlayer != null) {
//            mVideoPlayer.reset();
//        }
//        mVideoPlayer = null;
//        if (mVirtualVideo != null) {
//            mVirtualVideo.release();
//        }
//        mVirtualVideo = null;
    }

    /**
     * 初始化播放器
     */
    private void initPlayer() {
//        try {
//            VirtualVideo.getMediaInfo(shortVideoPath, mTrimVideoConfig, true);
//            //创建场景
//            mScene = VirtualVideo.createScene();
//            //添加场景视频
//            try {
//                mScene.addMedia(shortVideoPath);
//                mVirtualVideo = new VirtualVideo();
//                //添加虚拟视频添加场景
//                mVirtualVideo.addScene(mScene);
//                //播放器加载虚拟视频资源
//                mVirtualVideo.build(mVideoPlayer);
//            } catch (InvalidArgumentException e) {
//                e.printStackTrace();
//            }
//        } catch (InvalidStateException e) {
//            toastFinish();
//        }
//        mVideoPlayer.setAspectRatioFitMode(AspectRatioFitMode.KEEP_ASPECTRATIO_EXPANDING);
//        mVideoPlayer.setOnPlaybackListener(new VirtualVideoView.VideoViewListener() {
//            @Override
//            public void onPlayerPrepared() {
//                if (mIsFirstCreate) {
//                    startTime = 0;
//                    endTime = RecordSetting.MAX_VIDEO_DURATION / 1000f;
//                    mNornalSpeedDuration = endTime - startTime;
//                } else {
//                    if (mIsResume) {
//                        mIsResume = false;
//                        return;
//                    }
//                }
//                ThreadPoolUtils.executeEx(new Runnable() {
//                    @Override
//                    public void run() {
//                        mHandler.sendEmptyMessage(INIT_THUMBNAIL);
//                    }
//                });
//                mIsFirstCreate = false;
//                mIsResume = false;
//            }
//
//            @Override
//            public boolean onPlayerError(int what, int extra) {
//                LogUtil.e("onPlayerError," + what + "," + extra);
//                toastFinish();
//                return false;
//            }
//
//            @Override
//            public void onPlayerCompletion() {
//
//            }
//
//            @Override
//            public void onGetCurrentPosition(float position) {
//                if (position < startTime - 0.05f) {
//                    mVideoPlayer.seekTo(startTime);
//                }
//                if (position > endTime) {
//                    mVideoPlayer.seekTo(startTime);
//                }
//            }
//        });
//        mVideoPlayer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mVideoPlayer.isPlaying()) {
//                    pause();
//                } else {
//                    start();
//                }
//            }
//        });
//        mVideoPlayer.setAutoRepeat(true);
//        start();
    }

    /**
     * 获取当前速度的下标
     *
     * @return
     */
    private int getSpeedIndex() {
        int target = 2;
        int len = speeds.length;
        if (len > 0) {
            for (int i = 0; i < len; i++) {
                if (mCurrentSpeed == speeds[i]) {
                    target = i;
                    break;
                }
            }
        }
        return target;
    }


    /**
     * 设置媒体播放速度
     *
     * @param itemId
     */
    private void checkSpeed(int itemId) {
//        mCurrentSpeed = speeds[itemId];
//        List<MediaObject> allMedia = mScene.getAllMedia();
//        for (MediaObject mo : allMedia) {
//            mo.setSpeed((float) mCurrentSpeed);
//        }
//        float speedDuration = (float) (mNornalSpeedDuration / mCurrentSpeed);
//        if (itemId == 3 || itemId == 4) {
//            mTrimMaxDuration = (float) Math.floor(RecordManager.get().getSetting().maxVideoDuration / 1000 / mCurrentSpeed);
//        } else {
//            mTrimMaxDuration = RecordManager.get().getSetting().maxVideoDuration / 1000;
//        }
//        int max = (int) ((speedDuration < mTrimMaxDuration ? speedDuration : mTrimMaxDuration) * 1000);
//        mCustomTrimVideoView.setPlayer(mVirtualVideo, max, (int) (speedDuration * 1000), max);
//        setTitleNotice((int) max);
//        reload();
    }

    private void setTitleNotice(int duration) {
        if (duration < 6) {
            tvNext.setEnabled(false);
            tvNext.setTextColor(getResources().getColor(R.color.hh_color_b));
            tvNext.setText(getString(R.string.select_vidoe_error_6s));
        } else {
            tvNext.setEnabled(true);
            tvNext.setTextColor(getResources().getColor(R.color.hh_color_g));
            tvNext.setText("下一步");
        }
    }

    private final int INIT_THUMBNAIL = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == INIT_THUMBNAIL) {
//                initThumbnail(mVideoPlayer.getDuration(), mScene);
            }
        }
    };

    /**
     * 初始化滑动截取控件
     *
     * @param duration
     */
    public void initThumbnail(float duration) {
//        mCustomTrimVideoView.setOnRangeChangeListener(onRangeChangeListener);
//        VirtualVideo virtualVideo = new VirtualVideo();
//        virtualVideo.addScene(scene);
//        try {
//            virtualVideo.build(LocalVideoTrimActivity.this);
//        } catch (InvalidStateException e) {
//            e.printStackTrace();
//        }
//        float max = duration < mTrimMaxDuration ? duration : mTrimMaxDuration;
//        mCustomTrimVideoView.setPlayer(virtualVideo, (int) (max * 1000), (int) (duration * 1000), (int) (max * 1000));
//        setTitleNotice((int) max);
    }

    /**
     * 导出视频
     */
    private void exportVideo() {
//        List<MediaObject> allMedia = mScene.getAllMedia();
//        for (MediaObject media : allMedia) {
//            media.setTimeRange(startTime * media.getSpeed(), endTime * media.getSpeed());
//            media.setAspectRatioFitMode(AspectRatioFitMode.KEEP_ASPECTRATIO_EXPANDING);
//            RectF clipRect = RecordFileUtil.getClipSrc(media.getWidth(), media.getHeight(), media.getWidth() * 1.0f / media.getHeight(), 0f);
//            media.setClipRectF(clipRect);
//        }
//        mVvSave = new VirtualVideo();
//        mVvSave.addScene(mScene);
//
//        VideoConfig videoConfig = new VideoConfig();
//        ProductEntity productEntity = RecordManager.get().getProductEntity();
//        mSaveMp4FileName = RecordFileUtil.createVideoFile(RecordManager.get().getProductEntity().baseDir);
//        videoConfig.enableHWDecoder(CoreUtils.hasJELLY_BEAN_MR2());
//        videoConfig.enableHWDecoder(CoreUtils.hasJELLY_BEAN_MR2());
//        videoConfig.setVideoSize(productEntity.getExceptWH()[0], productEntity.getExceptWH()[1]);
//        videoConfig.setVideoEncodingBitRate(RecordManager.get().getSetting().videoPublishBitrate);
//        videoConfig.setAspectRatio(productEntity.getExceptRatio());
//        videoConfig.setOptimizeForNet(true);
//        videoConfig.setKeyFrameTime(10);//关键帧间隔设置为0，方便快速倒序
//        videoConfig.setVideoFrameRate(RecordManager.get().getSetting().videoFrameRate);
//        KLog.d("ggq", "mSaveMp4FileName==" + mSaveMp4FileName);
//        mVvSave.export(this, mSaveMp4FileName, videoConfig, mListenerSave);
    }

//    private ExportListener mListenerSave = new ExportListener() {
//
//        @Override
//        public void onExportStart() {
//            if (dialog == null) {
//                dialog = SysAlertDialog.showCircleProgressDialog(LocalVideoTrimActivity.this, getString(R.string.join_media), true, false);
//                dialog.show();
//            }
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        }
//
//        @Override
//        public boolean onExporting(int nProgress, int nMax) {
//            if (null != dialog) {
//                dialog.setProgress(nProgress);
//                dialog.setMax(nMax);
//            }
//            return true;
//        }
//
//        @Override
//        public void onExportEnd(int nResult) {
//            KLog.d("ggq", "onExportEnd  nResult==" + nResult);
//            if (nResult >= VirtualVideo.RESULT_SUCCESS) {
//                if (!TextUtils.isEmpty(mSaveMp4FileName)) {
//                    RecordManager.get().getProductEntity().combineVideo = mSaveMp4FileName;
//                }
//                LocalPublishActivity.startLocalPublishActivity(LocalVideoTrimActivity.this, LocalPublishActivity.FORM_SEARCH);
//                finish();
//            } else {
//                new File(mSaveMp4FileName).delete();
//                if (nResult != VirtualVideo.RESULT_SAVE_CANCEL) {
//                    if ((nResult == VirtualVideo.RESULT_CORE_ERROR_ENCODE_VIDEO
//                            || nResult == VirtualVideo.RESULT_CORE_ERROR_OPEN_VIDEO_ENCODER)
//                            && mHWCodecEnabled) {
//                        // FIXME:开启硬编后出现了编码错误，使用软编再试一次
//                        mHWCodecEnabled = false;
//                        exportVideo();
//                        return;
//                    }
//                    if (dialog != null) {
//                        try {
//                            Activity activity = LocalVideoTrimActivity.this;
//                            if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
//                                return;
//                            }
//                            if (dialog != null) {
//                                dialog.setMessage(getString(R.string.generate_video_fail));
//                                dialog.setCancelable(true);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        }
//    };

    private CustomTrimVideoView.OnRangeChangeListener onRangeChangeListener = new CustomTrimVideoView.OnRangeChangeListener() {
        @Override
        public void onValuesChanged(int minValue, int maxValue, int duration, int changeType) {
            setStartTime(minValue / 1000f);
            setEndTime(maxValue / 1000f);
            setTitleNotice(duration / 1000);
        }
    };


    public void start() {
//        if (mVideoPlayer == null) {
//            return;
//        }
//        mVideoPlayer.start();
        mIvPlayState.setBackgroundResource(R.drawable.btn_player_pause);
        mIvPlayState.setVisibility(View.INVISIBLE);
    }

    public void pause() {
//        if (mVideoPlayer == null) {
//            return;
//        }
//        mVideoPlayer.pause();
        mIvPlayState.setBackgroundResource(R.drawable.btn_player_play);
        mIvPlayState.setVisibility(View.VISIBLE);
    }

    public void seekTo(float sec) {
//        mVideoPlayer.seekTo(sec);
    }

    public void stop() {

    }

    public float getDuration() {
//        return mVideoPlayer.getDuration();
        return 0;
    }

    public void setStartTime(float startTime) {
        this.startTime = startTime;
        seekTo(startTime);
    }

    public void setEndTime(float endTime) {
        this.endTime = endTime;
        seekTo(startTime);
    }

    public void reload() {
//        try {
//            mVirtualVideo.build(mVideoPlayer);
//        } catch (InvalidStateException e) {
//            toastFinish();
//        }
        start();
    }
}
