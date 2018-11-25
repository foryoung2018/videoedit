package com.wmlive.hhvideo.heihei.record.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dongci.sun.gpuimglibrary.player.DCMediaInfoExtractor;
import com.dongci.sun.gpuimglibrary.thirdParty.mp4compose.FillMode;
import com.dongci.sun.gpuimglibrary.thirdParty.mp4compose.composer.Mp4Composer;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.heihei.beans.frame.FrameInfo;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.discovery.activity.SearchVideoActivity;
import com.wmlive.hhvideo.heihei.mainhome.OnSingleClickListener;
import com.wmlive.hhvideo.heihei.record.engine.PlayerEngine;
import com.wmlive.hhvideo.heihei.record.engine.VideoEngine;
import com.wmlive.hhvideo.heihei.record.engine.config.MVideoConfig;
import com.wmlive.hhvideo.heihei.record.engine.config.aVideoConfig;
import com.wmlive.hhvideo.heihei.record.engine.constant.SdkConstant;
import com.wmlive.hhvideo.heihei.record.engine.listener.ExportListener;
import com.wmlive.hhvideo.heihei.record.engine.listener.PlayerCreateListener;
import com.wmlive.hhvideo.heihei.record.engine.listener.PlayerListener;
import com.wmlive.hhvideo.heihei.record.engine.listener.VideoListener;
import com.wmlive.hhvideo.heihei.record.engine.model.MAsset;
import com.wmlive.hhvideo.heihei.record.engine.model.MediaObject;
import com.wmlive.hhvideo.heihei.record.engine.model.Scene;
import com.wmlive.hhvideo.heihei.record.engine.utils.VideoUtils;
import com.wmlive.hhvideo.heihei.record.listener.VideoJoinListener;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.manager.RecordSetting;
import com.wmlive.hhvideo.heihei.record.utils.CoreUtils;
import com.wmlive.hhvideo.heihei.record.utils.RecordFileUtil;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtil;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtilSdk;
import com.wmlive.hhvideo.heihei.record.widget.CircleProgressDialog;
import com.wmlive.hhvideo.heihei.record.widget.CustomTrimVideoView;
import com.wmlive.hhvideo.heihei.record.widget.ExtRadioGroup;
import com.wmlive.hhvideo.heihei.record.widget.SysAlertDialog;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.FileUtil;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ScreenUtil;
import com.wmlive.hhvideo.utils.SdkUtils;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.networklib.util.EventHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

import static com.wmlive.hhvideo.heihei.discovery.activity.SearchVideoActivity.TYPE_FROM_DIRECT_UPLOAD;
import static com.wmlive.hhvideo.heihei.discovery.activity.SearchVideoActivity.TYPE_FROM_RECORD;
import static com.wmlive.hhvideo.heihei.record.manager.RecordManager.PREFIX_REVERSE_FILE;
import static com.wmlive.hhvideo.heihei.record.manager.RecordManager.SUFFIX_VIDEO_FILE;
import static com.wmlive.hhvideo.heihei.record.manager.RecordSetting.MAX_UPLOAD_VIDEO_DURATION;
import static com.wmlive.hhvideo.heihei.record.manager.RecordSetting.MAX_VIDEO_DURATION;

/**
 * 导入修剪视频页面
 */
public class TrimVideoActivity extends DcBaseActivity {

    private Scene<MediaObject> mScene;
    private PlayerEngine playerEngine;//默认播放器
    private MVideoConfig mTrimVideoConfig = new MVideoConfig();

    //    @BindView(R.id.videoViewsdk)
//    TextureView videoView;
    @BindView(R.id.act_trim_video_content)
    RelativeLayout rlVideoContent;
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
    private float mTrimMaxDuration = MAX_VIDEO_DURATION / 1000f; //最大截取时间(单位秒)

    private float startTime, endTime;
    /**
     * 是否第一次启动activity
     */
    private boolean mIsFirstCreate = true;
    /**
     * 是否是从后台恢复
     */
    private boolean mIsResume = true;
    private int shortVideoIndex;
    private String shortVideoPath;
    private int startType;


    private boolean mHWCodecEnabled = CoreUtils.hasJELLY_BEAN_MR2();
    private String mSaveMp4FileName;
    private String mImportOriginFile; // 视频导入备份文件
    private TextView tvNext;
    private CircleProgressDialog dialog;

    public static void startTrimVideoActivity(Context ctx, int index, String shortVideoPath, int startType) {
        Intent intent = new Intent(ctx, TrimVideoActivity.class);
        intent.putExtra(SearchVideoActivity.START_TYPE_FROM, startType);
        intent.putExtra(SearchVideoActivity.SHORT_VIDEO_INDEX, index);
        intent.putExtra(SearchVideoActivity.SHORT_VIDEO_PATH, shortVideoPath);
        ctx.startActivity(intent);
    }

    @Override
    protected void onSingleClick(View v) {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_trim_video;
    }

    @Override
    protected void initData() {
        super.initData();
        KLog.i("initView--->>" + RecordManager.get().getProductEntity());

        initView();
        KLog.i("initData--->>" + shortVideoPath);
        if(startType == TYPE_FROM_DIRECT_UPLOAD)//本地上传
            initRotate(shortVideoPath);
        KLog.i("initData---2>>" + RecordManager.get().getProductEntity());

    }

    private void configFrameInfo() {
        if (RecordManager.get().getProductEntity().frameInfo == null) {
            FrameInfo frameInfo = new FrameInfo();
            aVideoConfig videoConfig = VideoUtils.getMediaInfor(shortVideoPath);

            frameInfo.opus_width = videoConfig.getVideoWidth();
            frameInfo.opus_height = videoConfig.getVideoHeight();

        }
    }

    private void initView() {
        Intent intent = getIntent();
        if (null != intent) {
            startType = intent.getIntExtra(SearchVideoActivity.START_TYPE_FROM, TYPE_FROM_RECORD);
            shortVideoIndex = intent.getIntExtra(SearchVideoActivity.SHORT_VIDEO_INDEX, 0);
            shortVideoPath = intent.getStringExtra(SearchVideoActivity.SHORT_VIDEO_PATH);
        }
        if (startType == TYPE_FROM_DIRECT_UPLOAD) {
            mTrimMaxDuration = MAX_UPLOAD_VIDEO_DURATION / 1000f;
        } else {
            mTrimMaxDuration = MAX_VIDEO_DURATION / 1000f;
            KLog.i("initView---copy0>>" );
            copyFile();
            KLog.i("initView---copy-end>>");
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
        tvNext.setPadding(10, 6, DeviceUtils.dip2px(TrimVideoActivity.this, 15), 6);
        setToolbarRightView(tvNext, new OnSingleClickListener() {

            @Override
            protected void onSingleClick(View v) {
                KLog.i("xxxx", "onSingleClick");
//                pause();
                if (null != mExtSpeed) {
                    mExtSpeed.setIListener(null);
                }

                if (playerEngine != null && !playerEngine.isNull()) {
                    playerEngine.release();
                }
                clipVideo((long) startTime * 1000 * 1000, (long) ((endTime - startTime) * 1000 * 1000));

            }
        });
    }

    private void copyFile(){
        if (dialog == null) {
            dialog = SysAlertDialog.showCircleProgressDialog(TrimVideoActivity.this, getString(R.string.video_doing), true, false);
            dialog.show();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                shortVideoPath = RecordFileUtil.copyFile(shortVideoPath);
                mHandler.sendEmptyMessage(COPY_FILE);
            }
        }).start();
    }


    private void rorationVideo(String path){

        KLog.i("videoEngine-->rotate" + path);
        String[] temp = path.split(File.separator);
        String basePath = "";
        for (int i = 0; i < temp.length - 1; i++) {
            basePath = basePath + temp[i] + File.separator;
        }
        String outPath = RecordFileUtil.createVideoFile(basePath);

        DCMediaInfoExtractor.MediaInfo mediaInfo = null;
        try {
            mediaInfo = DCMediaInfoExtractor.extract(path);
            KLog.i("videoEngine-->rotation:" + mediaInfo.videoInfo.rotation + " path:" + path + " outPath:" + outPath);
            if (mediaInfo.videoInfo.rotation != 0) {

                new Mp4Composer(path, outPath)
                        .fillMode(FillMode.PRESERVE_ASPECT_FIT)
                        .listener(new Mp4Composer.Listener() {

                            @Override
                            public void onProgress(double progress) {
                                KLog.i("videoEngine","videoEngine-->progress:" + progress);

//                                if (null != dialog) {
//                                    dialog.setProgress((int) (50 + progress / 2));
//                                }else{
//                                    dialog = SysAlertDialog.showCircleProgressDialog(TrimVideoActivity.this, getString(R.string.join_media), true, false);
//                                    dialog.show();
//                                }

                            }

                            @Override
                            public void onCompleted() {
                                KLog.i("videoEngine","videoEngine-->onCompleted:");
//                                if (!TextUtils.isEmpty(outPath) && new File(outPath).exists()) {
//                                    if (dialog != null) {
//                                        dialog.setCancelable(true);
//                                        dialog.dismiss();
//                                    }
//                                    shortVideoPath = outPath;
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            initPlayer();
//                                            if (RecordManager.get().getProductEntity() == null)
//                                                RecordManager.get().newProductEntity(RecordManager.get().getFrameInfo());
//                                            //旋转之后
//                                            //如果是左侧上传，配置frameinfo
//                                            //                                configFrameInfo();
//                                        }
//                                    });
//                                } else {
//                                    showToast(getString(R.string.generate_video_fail));
//                                }
                            }

                            @Override
                            public void onCanceled() {
                                KLog.i("videoEngine","videoEngine-->onCanceled:");
//                                if (dialog != null) {
//                                    dialog.setMessage(getString(R.string.generate_video_fail));
//                                    dialog.setCancelable(true);
//                                    dialog.dismiss();
//                                }
                            }

                            @Override
                            public void onFailed(Exception exception) {
                                KLog.i("videoEngine","videoEngine-->onFailed");
//                                if (dialog != null) {
//                                    dialog.setMessage(getString(R.string.generate_video_fail));
//                                    dialog.setCancelable(true);
//                                    dialog.dismiss();
//                                }
                            }

                        })
                        .start();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void rotationVideo2(String path){
        KLog.i("rotate-->pre");
        long time = System.currentTimeMillis();
        rotateAndTransform(path, new VideoListener() {

            @Override
            public void onStart() {
                KLog.i("rotate-->onStart");
                if (dialog == null) {
                    dialog = SysAlertDialog.showCircleProgressDialog(TrimVideoActivity.this, getString(R.string.join_media), true, false);
                    dialog.show();
                }
            }

            @Override
            public void onProgress(int progress) {
                KLog.i("rotate-->onStart" + progress);
                //正在旋转，合并
                if (null != dialog) {
                    dialog.setProgress(progress );
                }
            }

            @Override
            public void onFinish(int code, String outpath) {
                KLog.i("rotate-->onFinish" + code + "path:" + outpath);
                KLog.i("rotate-->Time" + (System.currentTimeMillis()-time));

                if (code == SdkConstant.RESULT_SUCCESS)
                    if (!TextUtils.isEmpty(outpath) && new File(outpath).exists()) {
                        shortVideoPath = outpath;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initPlayer();
                                if (RecordManager.get().getProductEntity() == null)
                                    RecordManager.get().newProductEntity(RecordManager.get().getFrameInfo());
                                //旋转之后
                                //如果是左侧上传，配置frameinfo
//                                configFrameInfo();
                            }
                        });

                    } else {
                        showToast(getString(R.string.generate_video_fail));
                        tvNext.setEnabled(false);
                    }
                if (dialog != null) {
                    dialog.dismiss();
                }
            }

            @Override
            public void onError() {
                if (dialog != null) {
                    dialog.setMessage(getString(R.string.generate_video_fail));
                    dialog.setCancelable(true);
                    dialog.show();
                    tvNext.setEnabled(false);
                }
            }
        });

    }

    /**
     * 初始旋转
     *
     * @param path
     */
    private void initRotate(String path) {
        DCMediaInfoExtractor.MediaInfo mediaInfo = null;
        try {
            mediaInfo = DCMediaInfoExtractor.extract(path);
            KLog.i("videoEngine-->rotation:" + mediaInfo.videoInfo.rotation + " path:" + path );
            if (mediaInfo.videoInfo.rotation != 0) {
                rotationVideo2(path);
            } else {
                initPlayer();
                if (RecordManager.get().getProductEntity() == null)
                    RecordManager.get().newProductEntity(RecordManager.get().getFrameInfo());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


//        rorationVideo(path);

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
        if (playerEngine != null && !playerEngine.isNull())
            playerEngine.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null)
            dialog.dismiss();
        if (null != mExtSpeed) {
            mExtSpeed.setIListener(null);
        }
        if (mCustomTrimVideoView != null) {
            mCustomTrimVideoView.onDestroy();
        }
        if (playerEngine != null && !playerEngine.isNull()) {
            playerEngine.release();
        }
        playerEngine = null;
        //回收底部裁剪控件

    }

    /**
     * 初始化播放器
     */
    private void initPlayer() {
        //创建场景
        mScene = new Scene();
//        shortVideoPath = "/storage/emulated/0/DCIM/Camera/DC_ID_20180423_120135_output.mp4";
        aVideoConfig config = VideoUtils.getMediaInfor(shortVideoPath);
        //添加场景视频
        MediaObject asset = new MAsset(shortVideoPath);
        asset.setRectInVideo(new RectF(0, 0, 1, 1));
        asset.setAspectRatioFitMode(MediaObject.FillTypeScaleToFit);

        asset.setShowRectF(new RectF(0,0,config.getVideoWidth(),config.getVideoHeight()));
        mScene.assets.add(asset);
        playerEngine = new PlayerEngine();


        TextureView videoView = new TextureView(this);
        aVideoConfig mediaInfor = VideoUtils.getMediaInfor(shortVideoPath);
        float v = mediaInfor.getVideoWidth() * 1.0f / mediaInfor.getVideoHeight();
        int screenWidth = ScreenUtil.getWidth(this);
        int height = ScreenUtil.getHeight(this) - ScreenUtil.dip2px(this, 158);
        int videoViewWidth;
        int videoViewHeight;
        if (v > 1) {
            videoViewWidth = screenWidth;
            videoViewHeight = (int) (screenWidth / v);
        } else {
            videoViewHeight = height;
            videoViewWidth = (int) (height * v);
        }
        KLog.i("videowidth-->"+videoViewWidth+"videoHeight:>"+videoViewHeight);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(videoViewWidth, videoViewHeight);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        videoView.setLayoutParams(layoutParams);
        if(rlVideoContent==null){
            ToastUtil.showToast("数据错误，请重试");
            return;
        }
        rlVideoContent.removeAllViews();
        rlVideoContent.addView(videoView);
        playerEngine.setAspectRatioFitMode(MediaObject.FillTypeScaleToFit);
        //播放器加载虚拟视频资源  config.getVideoWidth(),config.getVideoHeight()
        playerEngine.build(videoView, new PlayerCreateListener() {
            @Override
            public void playCreated() {
                playerEngine.setOnPlaybackListener(new PlayerListener() {
                    @Override
                    public void onPlayerPrepared() {

                    }

                    @Override
                    public boolean onPlayerError(int what, int extra) {
                        toastFinish();
                        return false;
                    }

                    @Override
                    public void onPlayerCompletion() {

                    }

                    @Override
                    public void onGetCurrentPosition(float position) {
                        if (position < startTime - 0.05f) {
                            playerEngine.seekTo(startTime);
                        }
                        if (position > endTime) {
                            playerEngine.seekTo(startTime);
                        }
                    }
                });
                try{
                    playerEngine.addScene(mScene);
                }catch (Exception e){
                    e.printStackTrace();
                    ToastUtil.showToast("数据错误，请重试");
                    finish();
                    return;
                }

                playerEngine.setAutoRepeat(true);
                playerPared();
                playerEngine.start();
            }
        });

        //添加虚拟视频添加场景
        playerEngine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(frameLayout.getChildCount()>0)
//                    KLog.d("click--chicd>"+frameLayout.getChildAt(0).getHeight());
                if (playerEngine.isPlaying()) {
                    pause();
                } else {
                    start();
                }
            }
        });
    }

    private void playerPared() {
        KLog.d("Trim--onPlayerPrepared");
        if (mIsFirstCreate) {
            startTime = 0;
            endTime = Math.min(VideoUtils.getVideoLength(shortVideoPath) * 1f / 1000000f, mTrimMaxDuration);
            mNornalSpeedDuration = endTime - startTime;
        } else {
            if (mIsResume) {
                mIsResume = false;
                return;
            }
        }
        //操作截图
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(INIT_THUMBNAIL);
            }
        });
//        .executeEx(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });
        mIsFirstCreate = false;
        mIsResume = false;
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
        mCurrentSpeed = speeds[itemId];
        List<MediaObject> allMedia = mScene.assets;
        for (MediaObject mo : allMedia) {
            mo.setSpeed((float) mCurrentSpeed);
        }
        float speedDuration = (float) (mNornalSpeedDuration / mCurrentSpeed);
        if (itemId == 3 || itemId == 4) {
            mTrimMaxDuration = (float) Math.floor(RecordManager.get().getSetting().maxVideoDuration / 1000 / mCurrentSpeed);
        } else {
            mTrimMaxDuration = RecordManager.get().getSetting().maxVideoDuration / 1000;
        }
        int max = (int) ((speedDuration < mTrimMaxDuration ? speedDuration : mTrimMaxDuration) * 1000);
        //sun
//        mCustomTrimVideoView.setPlayer(mVirtualVideo, max, (int) (speedDuration * 1000), max);
        setTitleNotice((int) max);
        reload();
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
    private final int STEP_CLIP = 2;
    private final int STEP_TRACK = 3;// 2->1
    private final int STEP_ROTATE = 4;// 2->1

    private final int COPY_FILE = 5;// 2->1

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case COPY_FILE:
                    if(dialog!=null)
                        dialog.dismiss();
                    dialog = null;
                    initRotate(shortVideoPath);
                    break;
                case INIT_THUMBNAIL:
                    KLog.d("handleMessage:     case INIT_THUMBNAIL");
                    initThumbnail();
                    break;
                case STEP_TRACK://
                    KLog.d("handleMessage:   case STEP_TRACK");
                    String path = msg.obj.toString();
                    splitVideo(path);

//                    rotateAndTransform(path, new VideoListener() {
//                        @Override
//                        public void onStart() {
//                        }
//
//                        @Override
//                        public void onProgress(int progress) {
//                            KLog.i("rotate--onProgress--"+progress);
//                            //正在旋转，合并
//                            if (null != dialog) {
//                                dialog.setProgress(50 + progress / 2);
//                            }
//                        }
//
//                        @Override
//                        public void onFinish(int code, String outpath) {
//                            if (code == SdkConstant.RESULT_SUCCESS)
//                                if (!TextUtils.isEmpty(outpath) && new File(outpath).exists())
//
//                                else {
//                                    if (dialog != null) {
//                                        showToast(getString(R.string.generate_video_fail));
//                                        dialog.dismiss();
//                                    }
//                                }
//                        }
//
//                        @Override
//                        public void onError() {
//                            if (dialog != null) {
//                                dialog.setMessage(getString(R.string.generate_video_fail));
//                                dialog.setCancelable(true);
//                                dialog.dismiss();
//                            }
//                        }
//                    });
                    break;
            }
        }
    };

    /**
     * 初始化滑动截取控件
     */
    public void initThumbnail() {
        long duration = VideoUtils.getVideoLength(shortVideoPath) / 1000000;
        mCustomTrimVideoView.setOnRangeChangeListener(onRangeChangeListener);
        float max = duration < mTrimMaxDuration ? duration : mTrimMaxDuration;
        //sun
        setTitleNotice((int) max);

        PlayerEngine customPlayer = new PlayerEngine();
        customPlayer.setSnapShotResource(shortVideoPath);

        mCustomTrimVideoView.setPlayer(customPlayer, (int) (max * 1000), (int) (duration * 1000), (int) (max * 1000));
    }

    /**
     * 1.裁剪回调处理
     * 2.
     */
    interface VideoCallBack {
        void onSuccess(int code, String path);

        void onError();
    }

    private void clipVideo(long start, long duration) {
        KLog.d("clipVideo--->" + VideoUtils.getVideoLength(shortVideoPath) * 1f / 1000000f);
        KLog.d("clipVideo---dura>" + start + "dura" + duration);
        duration = Math.min(duration,(long)RecordSetting.MAX_VIDEO_DURATION*1000);
        if (dialog == null) {
            dialog = SysAlertDialog.showCircleProgressDialog(TrimVideoActivity.this, getString(R.string.join_media), true, false);
        }
        new VideoEngine().cutVideoRecord(mScene.assets.get(0).getFilePath(), start, duration, new VideoListener() {

            @Override
            public void onStart() {
                Log.e("SLClipVideo-22", "-------onStart");

                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }

            @Override
            public void onProgress(int progress) {
                if (null != dialog) {
                    dialog.setProgress(progress / 2);
                }
                Log.e("SLClipVideo-33", "-------onProgress:" + progress);
            }

            @Override
            public void onFinish(int code, String outpath) {
                Log.e("SLClipVideo-44", "clipVideo---onFinish>" + code + "path:>" + outpath + VideoUtils.getVideoLength(outpath) + (new File(outpath).exists()));
//                SLVideoProcessor.getInstance().destroy();
                if (code >= SdkConstant.RESULT_SUCCESS) {
                    Message msg = new Message();
                    msg.what = STEP_TRACK;
                    msg.obj = outpath;
                    mHandler.sendMessage(msg);
                } else {//剪裁失败
                    new File(outpath).delete();
                    if (dialog != null) {
                        dialog.setMessage(getString(R.string.generate_video_fail));
                        dialog.setCancelable(true);
                    }
                }
            }

            @Override
            public void onError() {
                KLog.d("clipVideo---onError>");
                if (dialog != null) {
                    dialog.setMessage(getString(R.string.generate_video_fail));
                    dialog.setCancelable(true);
                }
            }
        });
    }
//    rotate(outpath, new VideoListener() {
//        @Override
//        public void onStart() {
//
//        }
//
//        @Override
//        public void onProgress(int progress) {
//            if (null != dialog) {
//                dialog.setProgress(50 + progress / 2);
//            }
//        }
//
//        @Override
//        public void onFinish(int code, String outpath) {
//            if (code == SdkConstant.RESULT_SUCCESS) {
//                if (!TextUtils.isEmpty(outpath)) {
//
//                }
//            }
//        }
//
//        @Override
//        public void onError() {
//
//        }
//    });

    /**
     * 裁剪结束
     */
    private void doTrimFinish(String orgpath, String videoPath, String audioPath) {
        KLog.d("clipVideo---end>" + VideoUtils.getVideoLength(videoPath) * 1f / 1000000f);
        ShortVideoEntity shortVideoInfo = RecordManager.get().getShortVideoEntity(shortVideoIndex);
        shortVideoInfo.setImport(true);
        shortVideoInfo.hasEdited = true;
        shortVideoInfo.setVideoType(String.valueOf(SelectFrameActivity.VIDEO_TYPE_IMPORT));
        shortVideoInfo.editingVideoPath = videoPath;
        KLog.i("import---videopath-->" + videoPath);
        KLog.i("import---audiopath-->" + audioPath);
        if(audioPath==null || new File(audioPath).length()<200){//音频太小，无法裁剪
            ToastUtil.showToast("裁剪失败，请重试");
            return;
        }
        shortVideoInfo.editingAudioPath = audioPath;
        shortVideoInfo.combineVideoAudio = orgpath;
        if (prepareDir()) {
            mImportOriginFile = RecordFileUtil.createVideoFile(RecordManager.get().getShortVideoEntity(shortVideoIndex).baseDir);
            FileUtil.copy(shortVideoInfo.combineVideoAudio, mImportOriginFile);
            shortVideoInfo.importVideoPath = mImportOriginFile;
        }
        RecordManager.get().updateProduct();
        if (startType == TYPE_FROM_RECORD) {
            EventHelper.post(GlobalParams.EventType.TYPE_TRIM_FINISH);
        } else if (startType == SearchVideoActivity.TYPE_FROM_SEARCH) {
            RecordActivitySdk.startRecordActivity(TrimVideoActivity.this, RecordActivitySdk.TYPE_NORMAL);
        } else if (startType == SearchVideoActivity.TYPE_FROM_DIRECT_UPLOAD) {
            RecordManager.get().getProductEntity().combineVideo = videoPath;
            RecordManager.get().getProductEntity().combineVideoAudio = orgpath;
            KLog.i("import---trime--combineVideoAudio-->" + orgpath);
            RecordManager.get().updateProduct();
            LocalPublishActivity.startLocalPublishActivity(TrimVideoActivity.this, LocalPublishActivity.FORM_SEARCH);
        }
        finish();
        return;
    }


    /**
     * 对需要旋转的 视频进行旋转
     *
     * @param path
     * @param videoListener
     */
    private void rotateAndTransform(final String path, VideoListener videoListener) {
        if(videoListener!=null)
            videoListener.onStart();

        String outPath = RecordFileUtil.createVideoFileByFilePath(path,"rotate");
        VideoEngine.rotateNew(path, outPath, videoListener);

//        videoListener.onStart();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String outPathTemp = VideoEngine.rotate(path);//旋转
////                String outpath = VideoEngine.transformAudio2to1(outPathTemp);
//                videoListener.onFinish(SdkConstant.RESULT_SUCCESS, outPathTemp);
//            }
//        }) {
//        }.start();
    }

    private void splitVideo(String video) {
        prepareDir();
        String basepath = RecordManager.get().getShortVideoEntity(shortVideoIndex).baseDir;
        if(RecordManager.get().getProductEntity().shortVideoList==null){
            ToastUtil.showToast("数据错误,请退出重试");
            return;
        }
        KLog.i("import---videopath--pre0>" + basepath);
        String videoOut = RecordFileUtil.createVideoFile(basepath);
        String audioOut = RecordFileUtil.createAudioFile(basepath);
        KLog.i("import---videopath--pre>" + videoOut);
        KLog.i("import---videopath--pre2>" + audioOut);
        new VideoEngine().splitVideoAudio(video, videoOut, audioOut, new VideoListener() {
            @Override
            public void onStart() {
                dialog = SysAlertDialog.showCircleProgressDialog(TrimVideoActivity.this, getString(R.string.join_media), true, false);
                if(dialog!=null){
                    dialog.show();
                }
            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onFinish(int code, String outpath) {
                if (code == SdkConstant.RESULT_SUCCESS) {
                    doTrimFinish(video, videoOut, audioOut);
                }
                if(dialog!=null)
                    dialog.dismiss();
            }

            @Override
            public void onError() {
                if(dialog!=null)
                    dialog.dismiss();
            }
        });
    }


    /**
     * 导出视频
     */
    private void exportVideo() {
        List<MediaObject> allMedia = mScene.assets;
        for (MediaObject media : allMedia) {

//            media.setTimeRange(startTime * media.getSpeed(), endTime * media.getSpeed());
//            media.setAspectRatioFitMode(AspectRatioFitMode.KEEP_ASPECTRATIO_EXPANDING);
//            RectF clipRect = RecordFileUtil.getClipSrc(media.getWidth(), media.getHeight(), RecordManager.get().getFrameInfo().getLayoutAspectRatio(shortVideoIndex), 0f);
//            media.setClipRectF(clipRect);

            media.setTimeRange((long) startTime * media.getSpeed(), (long) endTime * media.getSpeed());
            media.setAspectRatioFitMode(MediaObject.FillTypeScaleToFit);
            RectF clipRect = RecordFileUtil.getClipSrc(media.getWidth(), media.getHeight(), RecordManager.get().getSetting().getVideoRatio(), 0f);
            media.setShowRectF(clipRect);
        }
        MVideoConfig videoConfig = new MVideoConfig();
        if (prepareDir()) {
            mSaveMp4FileName = RecordFileUtil.createVideoFile(RecordManager.get().getShortVideoEntity(shortVideoIndex).baseDir);
        }
        videoConfig.enableHWEncoder(mHWCodecEnabled);
        videoConfig.enableHWDecoder(mHWCodecEnabled);
        videoConfig.setVideoEncodingBitRate(RecordManager.get().getSetting().videoRecordBitrate);
        videoConfig.setKeyFrameTime(10);//关键帧间隔设置为0，方便快速倒序
        if (mTrimVideoConfig.getVideoWidth() >= mTrimVideoConfig.getVideoWidth()) {
            videoConfig.setVideoSize(720, 0);
            RecordManager.get().getShortVideoEntity(shortVideoIndex).quality = FrameInfo.VIDEO_QUALITY_HIGH;
        } else if (mTrimVideoConfig.getVideoWidth() >= RecordSetting.PRODUCT_WIDTH
                && mTrimVideoConfig.getVideoHeight() >= RecordSetting.PRODUCT_HEIGHT) {
            videoConfig.setVideoSize(RecordSetting.PRODUCT_WIDTH, RecordSetting.PRODUCT_HEIGHT);
            RecordManager.get().getShortVideoEntity(shortVideoIndex).quality = FrameInfo.VIDEO_QUALITY_HIGH;
        } else {
            videoConfig.setVideoSize(RecordSetting.VIDEO_WIDTH, RecordSetting.VIDEO_HEIGHT);
            RecordManager.get().getShortVideoEntity(shortVideoIndex).quality = FrameInfo.VIDEO_QUALITY_LOW;
        }
        KLog.d("ggq", "config====videoConfig.getVideoWidth()==" + videoConfig.getVideoWidth() + "==videoConfig.getVideoHeight()=" + videoConfig.getVideoHeight());
        KLog.d("ggq", "RecordManager.get().getFrameInfo().opus_width==" + RecordManager.get().getFrameInfo().opus_width + "  RecordManager.get().getFrameInfo().opus_height==" + RecordManager.get().getFrameInfo().opus_height);
        KLog.d("ggq", "mTrimVideoConfig.getVideoWidth()==" + mTrimVideoConfig.getVideoWidth() + "  mTrimVideoConfig.getVideoHeight()==" + mTrimVideoConfig.getVideoHeight());
        videoConfig.setVideoFrameRate(RecordManager.get().getSetting().videoFrameRate);
        List<Scene> scenes = new ArrayList<Scene>();
        scenes.add(mScene);
        if (playerEngine != null && !playerEngine.isNull())
            playerEngine.release();
        RecordUtilSdk.exportCombineVideo(scenes, videoConfig, mListenerSave);
    }
//    /**
//     * 导出视频
//     */
//    private void exportVideo() {
//        List<MediaObject> allMedia = mScene.getAllMedia();
//        for (MediaObject media : allMedia) {
//            media.setTimeRange(startTime * media.getSpeed(), endTime * media.getSpeed());
//            media.setAspectRatioFitMode(AspectRatioFitMode.KEEP_ASPECTRATIO_EXPANDING);
//            RectF clipRect = RecordFileUtil.getClipSrc(media.getWidth(), media.getHeight(), RecordManager.get().getSetting().getVideoRatio(), 0f);
//            media.setClipRectF(clipRect);
//        }
//        mVvSave = new VirtualVideo();
//        mVvSave.addScene(mScene);
//
//        aVideoConfig videoConfig = new aVideoConfig();
//
//        if (prepareDir()) {
//            mSaveMp4FileName = RecordFileUtil.createVideoFile(RecordManager.get().getShortVideoEntity(shortVideoIndex).baseDir);
//        }
//
//        videoConfig.enableHWEncoder(mHWCodecEnabled);
//        videoConfig.enableHWDecoder(mHWCodecEnabled);
//        videoConfig.setVideoEncodingBitRate(RecordManager.get().getSetting().videoRecordBitrate);
//        videoConfig.setKeyFrameTime(10);//关键帧间隔设置为0，方便快速倒序
//        if (mTrimVideoConfig.getVideoWidth() >= mTrimVideoConfig.getVideoHeight()) {
//            videoConfig.setVideoSize(720, 0);
//            RecordManager.get().getShortVideoEntity(shortVideoIndex).quality = FrameInfo.VIDEO_QUALITY_HIGH;
//        } else if (mTrimVideoConfig.getVideoWidth() >= RecordSetting.PRODUCT_WIDTH
//                && mTrimVideoConfig.getVideoHeight() >= RecordSetting.PRODUCT_HEIGHT) {
//            videoConfig.setVideoSize(RecordSetting.PRODUCT_WIDTH, RecordSetting.PRODUCT_HEIGHT);
//            RecordManager.get().getShortVideoEntity(shortVideoIndex).quality = FrameInfo.VIDEO_QUALITY_HIGH;
//        } else {
//            videoConfig.setVideoSize(RecordSetting.VIDEO_WIDTH, RecordSetting.VIDEO_HEIGHT);
//            RecordManager.get().getShortVideoEntity(shortVideoIndex).quality = FrameInfo.VIDEO_QUALITY_LOW;
//        }
//        videoConfig.setVideoFrameRate(RecordManager.get().getSetting().videoFrameRate);
//
//        mVvSave.export(this, mSaveMp4FileName, videoConfig, mListenerSave);
//    }

    private boolean prepareDir() {
        if (TextUtils.isEmpty(RecordManager.get().getProductEntity().baseDir)) {
            String productPath = RecordFileUtil.createTimestampDir(RecordFileUtil.getTempDir(), "");
            if (TextUtils.isEmpty(productPath)) {
                KLog.i("====创建productDir文件夹失败");
                return false;
            }
            RecordManager.get().getProductEntity().baseDir = productPath;
        }

        if (!RecordFileUtil.createDir(new File(RecordManager.get().getProductEntity().baseDir))) {
            KLog.i("====创建productDir文件夹失败");
            return false;
        }

        if (TextUtils.isEmpty(RecordManager.get().getShortVideoEntity(shortVideoIndex).baseDir)) {
            String shortVideoDir = RecordFileUtil.createTimestampDir(RecordManager.get().getProductEntity().baseDir, RecordManager.PREFIX_VIDEO_DIR);
            if (TextUtils.isEmpty(shortVideoDir)) {
                KLog.i("====创建shortVideoDir文件夹失败");
                return false;
            }
            ShortVideoEntity entity = RecordManager.get().getShortVideoEntity(shortVideoIndex);
            entity.baseDir = shortVideoDir;
            RecordManager.get().setShortVideoEntity(shortVideoIndex, entity);
        }

        if (!RecordFileUtil.createDir(new File(RecordManager.get().getShortVideoEntity(shortVideoIndex).baseDir))) {
            KLog.i("====创建shortVideoDir文件夹失败");
            return false;
        }
        return true;
    }

    private ExportListener mListenerSave = new ExportListener() {

        @Override
        public void onExportStart() {
            if (dialog == null) {
                dialog = SysAlertDialog.showCircleProgressDialog(TrimVideoActivity.this, getString(R.string.join_media), true, false);
                dialog.show();
            }
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        @Override
        public void onExporting(int nProgress, int nMax) {
            if (null != dialog) {
                dialog.setProgress(nProgress);
                dialog.setMax(nMax);
            }
        }

        @Override
        public void onExportEnd(int nResult, String path) {
            if (nResult >= SdkConstant.RESULT_SUCCESS) {
                if (!TextUtils.isEmpty(path)) {
                    ShortVideoEntity shortVideoInfo = RecordManager.get().getShortVideoEntity(shortVideoIndex);
                    shortVideoInfo.setImport(true);
                    shortVideoInfo.hasEdited = true;
                    shortVideoInfo.setVideoType(String.valueOf(SelectFrameActivity.VIDEO_TYPE_IMPORT));
                    shortVideoInfo.editingVideoPath = path;
                    if (prepareDir()) {
                        mImportOriginFile = RecordFileUtil.createVideoFile(RecordManager.get().getShortVideoEntity(shortVideoIndex).baseDir);
                        FileUtil.copy(shortVideoInfo.editingVideoPath, mImportOriginFile);
                        shortVideoInfo.importVideoPath = mImportOriginFile;
                    }
                    RecordManager.get().updateProduct();
                    if (startType == TYPE_FROM_RECORD) {
                        EventHelper.post(GlobalParams.EventType.TYPE_TRIM_FINISH);
                    } else if (startType == SearchVideoActivity.TYPE_FROM_SEARCH) {
                        RecordActivitySdk.startRecordActivity(TrimVideoActivity.this, RecordActivitySdk.TYPE_NORMAL);
                    } else if (startType == SearchVideoActivity.TYPE_FROM_DIRECT_UPLOAD) {
                        RecordManager.get().getProductEntity().combineVideo = path;
                        RecordManager.get().updateProduct();
                        LocalPublishActivity.startLocalPublishActivity(TrimVideoActivity.this, LocalPublishActivity.FORM_SEARCH);
                    }
                    finish();
                    return;
                }
                if (startType == TYPE_FROM_RECORD) {
                    EventHelper.post(GlobalParams.EventType.TYPE_TRIM_FINISH);
                } else if (startType == SearchVideoActivity.TYPE_FROM_SEARCH) {
                    RecordActivitySdk.startRecordActivity(TrimVideoActivity.this, RecordActivitySdk.TYPE_NORMAL);
                }
                finish();
            } else {
                new File(path).delete();
                if (dialog != null) {
                    dialog.setMessage(getString(R.string.generate_video_fail));
                    dialog.setCancelable(true);
                }
//                if (nResult != SdkConstant.RESULT_SAVE_CANCEL) {
//                    if ((nResult == SdkConstant.RESULT_CORE_ERROR_ENCODE_VIDEO
//                            || nResult == SdkConstant.RESULT_CORE_ERROR_OPEN_VIDEO_ENCODER)
//                            && mHWCodecEnabled) {
//                        // FIXME:开启硬编后出现了编码错误，使用软编再试一次
//                        mHWCodecEnabled = false;
//                        exportVideo();
//                        return;
//                    }
//                    if (dialog != null) {
//                        try {
//                            Activity activity = TrimVideoActivity.this;
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
            }
        }
    };


    protected void exportReverseVideo() {

        ShortVideoEntity entity = RecordManager.get().getShortVideoEntity(shortVideoIndex);
//        RecordFileUtil.cleanFilesByPrefix(entity.baseDir, PREFIX_REVERSE_FILE);
        final String reversePath = RecordFileUtil.createTimestampFile(entity.baseDir,
                PREFIX_REVERSE_FILE, SUFFIX_VIDEO_FILE, true);
        entity.editingReverseVideoPath = reversePath;
        RecordUtil.reverseVideo(getApplicationContext(), entity.editingVideoPath, reversePath, true, new VideoJoinListener() {
            @Override
            public void onJoinStart() {
            }

            @Override
            public void onJoining(int progress, int max) {

            }

            @Override
            public void onJoinEnd(boolean result, String message) {
                KLog.d("ggq", "onExportEnd");
                if (result) {
                    RecordManager.get().updateProduct();
                    if (dialog != null) {
                        try {
                            Activity activity = TrimVideoActivity.this;
                            if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
                                return;
                            }
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (startType == TYPE_FROM_RECORD) {
                        EventHelper.post(GlobalParams.EventType.TYPE_TRIM_FINISH);
                    } else if (startType == SearchVideoActivity.TYPE_FROM_SEARCH) {

                        RecordActivitySdk.startRecordActivity(TrimVideoActivity.this, RecordActivitySdk.TYPE_NORMAL);
//                        RecordActivity.startRecordActivity(TrimVideoActivity.this, RecordActivity.TYPE_NORMAL);
                    }
                    finish();

                } else {
                    if (dialog != null) {
                        try {
                            Activity activity = TrimVideoActivity.this;
                            if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
                                return;
                            }
                            if (dialog != null) {
                                dialog.setMessage(getString(R.string.generate_video_fail));
                                dialog.setCancelable(true);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private CustomTrimVideoView.OnRangeChangeListener onRangeChangeListener = new CustomTrimVideoView.OnRangeChangeListener() {
        @Override
        public void onValuesChanged(int minValue, int maxValue, int duration, int changeType) {
//            setStartTime(minValue * 1f / 1000f);
//            setEndTime(maxValue * 1f / 1000f);
            setTimeRange(minValue * 1f, maxValue * 1f);
            setTitleNotice((int) Math.round(duration * 1f / 1000f));
            KLog.i("onValuesChanged-->" + duration + "duration-->" + Math.round((duration * 1f / 1000f)));
        }
    };


    public void start() {
        if (playerEngine == null || playerEngine.isNull()) {
            return;
        }
        playerEngine.start();
        mIvPlayState.setBackgroundResource(R.drawable.btn_player_pause);
        mIvPlayState.setVisibility(View.INVISIBLE);
//        KLog.d("click--start>" + videoView.getHeight());
    }

    public void pause() {
        if (playerEngine == null || playerEngine.isNull()) {
            return;
        }
        playerEngine.pause();

        mIvPlayState.setBackgroundResource(R.drawable.btn_player_play);
        mIvPlayState.setVisibility(View.VISIBLE);
//        KLog.d("click--pause>" + videoView.getHeight());
    }

    public void seekTo(float sec) {
        if (playerEngine != null && !playerEngine.isNull())
            playerEngine.seekTo(sec);
    }

    public void stop() {

    }

    public float getDuration() {
        return playerEngine.getDuration();
    }

    public void setStartTime(float startTime) {
        this.startTime = startTime;
        KLog.i("clipVideo--setStartTime>" + startTime);
        seekTo(startTime);
    }

    public void setEndTime(float endTime) {
        this.endTime = endTime;
        KLog.i("clipVideo--endtime>" + endTime);
        seekTo(startTime);
    }

    public void reload() {
//        mVideoPlayer.reset();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        TextureView videoView = new TextureView(this);
        videoView.setLayoutParams(params);
        rlVideoContent.removeAllViews();
        rlVideoContent.addView(videoView);
        playerEngine.build(videoView, new PlayerCreateListener() {
            @Override
            public void playCreated() {
                start();
            }
        });
    }


    private void setTimeRange(float startTime, float endTime) {
        this.startTime = startTime / 1000f;
        this.endTime = endTime / 1000f;
        MediaObject mediaObject = mScene.assets.get(0);
        KLog.i("trime--start" + startTime + "end>" + endTime);
        if (endTime > 0) {
            mediaObject.setTimeRange(((long) startTime) * 1000, ((long) (endTime) * 1000));
        }
        List<Scene> scenes = new ArrayList<Scene>();
        scenes.add(mScene);
        if (playerEngine != null && !playerEngine.isNull()) {
            playerEngine.seekTo(0.0f);
            pause();
            playerEngine.setScenceAndPrepare(scenes);
        }

    }
}
