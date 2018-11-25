package com.wmlive.hhvideo.heihei.record.activity;

import android.content.Context;
import android.content.Intent;
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
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.BaseModel;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.heihei.discovery.activity.SearchVideoActivity;
import com.wmlive.hhvideo.heihei.mainhome.OnSingleClickListener;
import com.wmlive.hhvideo.heihei.record.config.RecordSettingSDK;
import com.wmlive.hhvideo.heihei.record.engine.PlayerEngine;
import com.wmlive.hhvideo.heihei.record.engine.VideoEngine;
import com.wmlive.hhvideo.heihei.record.engine.config.MVideoConfig;
import com.wmlive.hhvideo.heihei.record.engine.config.aVideoConfig;
import com.wmlive.hhvideo.heihei.record.engine.constant.SdkConstant;
import com.wmlive.hhvideo.heihei.record.engine.content.ExportContentFactory;
import com.wmlive.hhvideo.heihei.record.engine.content.PlayerContentFactory;
import com.wmlive.hhvideo.heihei.record.engine.content.PlayerViewFactory;
import com.wmlive.hhvideo.heihei.record.engine.listener.ExportListener;
import com.wmlive.hhvideo.heihei.record.engine.listener.PlayerCreateListener;
import com.wmlive.hhvideo.heihei.record.engine.listener.PlayerListener;
import com.wmlive.hhvideo.heihei.record.engine.listener.VideoListener;
import com.wmlive.hhvideo.heihei.record.engine.model.MediaObject;
import com.wmlive.hhvideo.heihei.record.engine.model.Scene;
import com.wmlive.hhvideo.heihei.record.engine.utils.VideoUtils;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.manager.RecordSetting;
import com.wmlive.hhvideo.heihei.record.utils.RecordFileUtil;
import com.wmlive.hhvideo.heihei.record.widget.CircleProgressDialog;
import com.wmlive.hhvideo.heihei.record.widget.CustomTrimVideoView;
import com.wmlive.hhvideo.heihei.record.widget.ExtRadioGroup;
import com.wmlive.hhvideo.heihei.record.widget.SysAlertDialog;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.FileUtil;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.SdkUtils;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.networklib.util.EventHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

import static com.wmlive.hhvideo.heihei.discovery.activity.SearchVideoActivity.TYPE_FROM_DIRECT_UPLOAD;
import static com.wmlive.hhvideo.heihei.discovery.activity.SearchVideoActivity.TYPE_FROM_RECORD;
import static com.wmlive.hhvideo.heihei.record.manager.RecordSetting.MAX_UPLOAD_VIDEO_DURATION;
import static com.wmlive.hhvideo.heihei.record.manager.RecordSetting.MAX_VIDEO_DURATION;
import static com.wmlive.hhvideo.heihei.record.utils.RecordUtilSdk.exportLocalUploadVideo;

/**
 * 新版本地导入，视频裁剪
 */
public class TrimVideoActivityNew extends DcBaseActivity {
    @BindView(R.id.act_trim_video_content)
    RelativeLayout rlVideoContent;
    @BindView(R.id.iv_play_state)
    ImageView mIvPlayState;
    @BindView(R.id.extSpeed)
    ExtRadioGroup mExtSpeed;
    @BindView(R.id.customTrimVideoView)
    CustomTrimVideoView mCustomTrimVideoView;

    private float mTrimMaxDuration = MAX_VIDEO_DURATION / 1000f; //最大截取时间(单位秒)


    private int startType;
    private int shortVideoIndex;
    private String shortVideoPath;
    float startTime;
    float endTime;

    private TextView tvNext;
    private CircleProgressDialog dialog;

    private boolean pauseClick = false;
    Scene mScene;
    PlayerEngine playerEngine;


    @Override
    protected void onSingleClick(View v) {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_trim_video_new;
    }

    public void initData(){
        super.initData();
        initIntentData();

        initView();
    }

    private void initIntentData() {
        Intent intent = getIntent();
        if (null != intent) {
            startType = intent.getIntExtra(SearchVideoActivity.START_TYPE_FROM, TYPE_FROM_RECORD);
            shortVideoIndex = intent.getIntExtra(SearchVideoActivity.SHORT_VIDEO_INDEX, 0);
            shortVideoPath = intent.getStringExtra(SearchVideoActivity.SHORT_VIDEO_PATH);
        }
        if (startType == TYPE_FROM_DIRECT_UPLOAD) {//直接上传
            mTrimMaxDuration = MAX_UPLOAD_VIDEO_DURATION / 1000f;
        } else {//格子录制
            mTrimMaxDuration = MAX_VIDEO_DURATION / 1000f;
//            copyFile();
        }
        startTime = 0;
        endTime = VideoUtils.getVideoLength(shortVideoPath)/1000;
        KLog.i("initView---filePath>>" +shortVideoPath);
    }

    private void initView(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initTitleBar();
        initDiff();
        initThumbnail();

    }

    private void initTitleBar(){
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
        tvNext.setPadding(10, 6, DeviceUtils.dip2px(TrimVideoActivityNew.this, 15), 6);
        setToolbarRightView(tvNext, new OnSingleClickListener() {

            @Override
            protected void onSingleClick(View v) {
                KLog.i("xxxx", "onSingleClick");
                if (playerEngine != null && !playerEngine.isNull()) {
                    playerEngine.release();
                }

                doNextVideoProcess();
//                clipVideo((long) startTime * 1000 * 1000, (long) ((endTime - startTime) * 1000 * 1000));

            }
        });
    }



    /**
     * 视频初始化
     */
    private void initDiff() {
        if (startType == TYPE_FROM_DIRECT_UPLOAD) {//本地上传,直接导入
            initPlayer();
            if (RecordManager.get().getProductEntity() == null)
                RecordManager.get().newProductEntity(RecordManager.get().getFrameInfo());
        } else {//格子拍摄，直接加载，
            initPlayer();
//            rotateVideo(shortVideoPath);
        }
    }

    /**
     * 初始化播放器
     */
    private void initPlayer(){
//        初始化 将要加载的数据
//        初始化 控件
//        创建播放器
//        给播放器设置数据
        MediaObject mediaObject = PlayerContentFactory.createMediaFromTrim(shortVideoPath);
        mScene = new Scene();
        mScene.assets.add(mediaObject);
        playerEngine = new PlayerEngine();
        TextureView textureView = initTexureView();
        playerEngine.build(textureView, new PlayerCreateListener() {
            @Override
            public void playCreated() {
                //设置数据
                //设置监听
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
                        if(!pauseClick){//自动播放结束，点击重新播放
                            if(playerEngine!=null)
                                playerEngine.seekTo(0);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showPauseView();
                                }
                            });

//                            start();//开始播放
                        }
                        KLog.i("onPlayerCompletion=-->"+pauseClick);
                        pauseClick = false;
                    }

                    @Override
                    public void onGetCurrentPosition(float position) {
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
//                playerPared();
                playerEngine.start();
                //添加虚拟视频添加场景
                playerEngine.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (playerEngine.isPlaying()) {
                            pause();
                        } else {
                            play();
                        }
                    }
                });
            }

        });


    }

    private TextureView initTexureView(){
        TextureView videoView = new TextureView(this);
        int[] viewInfo = PlayerViewFactory.measureViewWH(this,shortVideoPath);
        KLog.i("initTexureView---width>"+viewInfo[0]+"height::>"+viewInfo[1]);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(viewInfo[0], viewInfo[1]);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        videoView.setLayoutParams(layoutParams);
        if(rlVideoContent==null){
            ToastUtil.showToast("数据错误，请重试");
            return null;
        }
        rlVideoContent.removeAllViews();
        rlVideoContent.addView(videoView);
        return videoView;
    }


    /**
     * 初始化滑动截取控件
     */
    private void initThumbnail() {
        if(mCustomTrimVideoView==null)
            return;
        long duration = VideoUtils.getVideoLength(shortVideoPath) / 1000000;
        mCustomTrimVideoView.setOnRangeChangeListener(onRangeChangeListener);
        float max = duration < mTrimMaxDuration ? duration : mTrimMaxDuration;
        //sun
        PlayerEngine customPlayer = new PlayerEngine();
        customPlayer.setSnapShotResource(shortVideoPath);

        mCustomTrimVideoView.setPlayer(customPlayer, (int) (max * 1000), (int) (duration * 1000), (int) (max * 1000));
    }

    private void play(){
        if (playerEngine == null || playerEngine.isNull()) {//如果未空，直接初始化
            initPlayer();
            return;
        }
        playerEngine.start();
        mIvPlayState.setBackgroundResource(R.drawable.btn_player_pause);
        mIvPlayState.setVisibility(View.INVISIBLE);
    }

    private void pause(){
        if (playerEngine == null || playerEngine.isNull()) {
            return;
        }
        pauseClick = true;
        KLog.i("onPlayerCompletion=--pause>"+pauseClick);
        playerEngine.pause();
        showPauseView();
    }

    private void showPauseView(){
        mIvPlayState.setBackgroundResource(R.drawable.btn_player_play);
        mIvPlayState.setVisibility(View.VISIBLE);
    }

    /**
     *
     * 旋转视频,旋转ok 播放视频
     * @param path
     */
    private void rotateVideo(String path){
        KLog.i("rotate-->start"+path + +VideoUtils.getVideoLength(path));
        rotateDetail(path, new VideoListener() {

            @Override
            public void onStart() {
                KLog.i("rotate-->onStart");
                if (dialog == null) {
                    dialog = SysAlertDialog.showCircleProgressDialog(TrimVideoActivityNew.this, getString(R.string.join_media), true, false);
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
                KLog.i("rotate-->onFinish" + code + "path:" + outpath+VideoUtils.getVideoLength(outpath));
                if (code == SdkConstant.RESULT_SUCCESS)
                    if (!TextUtils.isEmpty(outpath) && new File(outpath).exists()) {
                        shortVideoPath = outpath;
                        splitVideo(shortVideoPath);
                    } else {
                        showToast(getString(R.string.generate_video_fail));
                        tvNext.setEnabled(false);
                    }
                hideDialog();
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

    private void hideDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }


    private void rotateDetail(final String path, VideoListener videoListener){
        try {
            DCMediaInfoExtractor.MediaInfo mediaInfo = DCMediaInfoExtractor.extract(path);
            KLog.i("videoEngine-->rotation:" + mediaInfo.videoInfo.rotation + " path:" + path );
            if (mediaInfo.videoInfo.rotation != 0) {
                String outPath = RecordFileUtil.createVideoFileByFilePath(path,"rotate");
                VideoEngine.rotateNew(path, outPath, videoListener);
            } else {
                videoListener.onFinish(SdkConstant.RESULT_SUCCESS,path);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showToast("视频处理失败，请重试！");
            finish();
        }
    }




    /**
     * 视频 音视频 分离
     */
    private void splitVideo(String video) {
//        prepareDir();
        if(RecordManager.get().getProductEntity().shortVideoList==null){
            ToastUtil.showToast("数据错误,请退出重试");
            return;
        }
        String basepath = RecordManager.get().getShortVideoEntity(shortVideoIndex).baseDir;
        KLog.i("import---videopath--pre0>" + basepath);
        String videoOut = RecordFileUtil.createVideoFile(basepath);
        String audioOut = RecordFileUtil.createAudioFile(basepath);
        KLog.i("import---videopath--pre>" + videoOut);
        KLog.i("import---videopath--pre2>" + audioOut);
        new VideoEngine().splitVideoAudio(video, videoOut, audioOut, new VideoListener() {
            @Override
            public void onStart() {
                dialog = SysAlertDialog.showCircleProgressDialog(TrimVideoActivityNew.this, getString(R.string.join_media), true, false);
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
                    toRecord(video, videoOut, audioOut);
                }else {
                    ToastUtil.showToast("视频处理失败");
                    FileUtil.deleteFile(videoOut);
                    FileUtil.deleteFile(audioOut);
                }
                hideDialog();
            }

            @Override
            public void onError() {
                FileUtil.deleteFile(videoOut);
                FileUtil.deleteFile(audioOut);
                hideDialog();
            }
        });
    }



    /**
     * 处理下一步操作
     */
    private void doNextVideoProcess(){
        if (dialog == null) {
            dialog = SysAlertDialog.showCircleProgressDialog(TrimVideoActivityNew.this, getString(R.string.video_doing), true, false);
            dialog.show();
        }
//        if(startType == TYPE_FROM_DIRECT_UPLOAD){//如果是直接上传，直接走导出视频，TimeRange 为裁剪时间
//            exportDirectVideo();
//        }else{//多格子录制 ，1.导出视频，(指定宽高，时间) 2.旋转视频 如果需要 3.分离音视频，
            clipVideo((long) startTime * 1000 * 1000, (long) ((endTime - startTime) * 1000 * 1000));
//        }


    }

    private void channel2To1(String path){
        VideoEngine.transformAudio2to1(path, new VideoListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onProgress(int progress) {
//                allProgress = RecordUtil.calculateProgress(progress, productEntity.shortVideoList.size(), perExportVideoProgress > 0 ? 15 : 20);

            }

            @Override
            public void onFinish(int code, String outpath) {//主线程 进行相应操作
                KLog.i("2-》1--》"+outpath);
                //更新进度界面
                shortVideoPath = outpath;
                if(VideoUtils.getVideoLength(shortVideoPath)==0 ){
                    ToastUtil.showToast("视频处理失败，请重试");
                }else {
                    exportDirectVideo();
                }

            }

            @Override
            public void onError() {

            }
        });
    }

    private void exportDirectVideo() {
        aVideoConfig video = VideoUtils.getMediaInfor(shortVideoPath);
        int[]target2 = getTargetWidthNew(video.getVideoWidth(),video.getVideoHeight());
        KLog.i("video-width=>"+video.getVideoWidth()+"height:>"+video.getVideoHeight());

        //设置最大时间
        MediaObject mediaObject = ExportContentFactory.getLocalUpload(shortVideoPath);
        Scene scene = new Scene();
        scene.addMedia(mediaObject);
        List<Scene> scenes = new ArrayList<Scene>(1);
        scenes.add(scene);


        MVideoConfig mVideoConfig = new MVideoConfig();
        //计算宽高，
        mVideoConfig.setKeyFrameTime(video.getVideoFrameRate()==0?30:video.getVideoFrameRate());//1s 一个关键帧

        mVideoConfig.setVideoPath(RecordFileUtil.createExportFile());
        mVideoConfig.setVideoEncodingBitRate(RecordSettingSDK.VIDEO_PUBLISH_BITRATE_HEIGHT);
        mVideoConfig.setDefaultAudioInfo();

        KLog.i("video-width=2>"+target2[0]+"height:>"+target2[1]);
        KLog.i("video-width=3>"+mVideoConfig.getVideoWidth()+"height:>"+mVideoConfig.getVideoHeight());
        mVideoConfig.setVideoSize(target2[0], target2[1]);
        KLog.i("video-width=4>"+mVideoConfig.getVideoWidth()+"height:>"+mVideoConfig.getVideoHeight());
        exportLocalUploadVideo(scenes, mVideoConfig, new ExportListener() {
            @Override
            public void onExportStart() {
                KLog.d("exportCombineVideo", "onExportStart: ");
                if (dialog == null) {
                    dialog = SysAlertDialog.showCircleProgressDialog(TrimVideoActivityNew.this, getString(R.string.video_doing), true, false);
                    dialog.show();
                }
            }

            @Override
            public void onExporting(int progress, int max) {
                KLog.d("exportCombineVideo", "onExporting: progress==" + progress);
                dialog.setProgress(progress);
            }

            @Override
            public void onExportEnd(int var1, String path) {
                KLog.d("exportCombineVideo", "onExportEnd: path==" + path);
                if(var1== SdkConstant.RESULT_SUCCESS){
                    toPublishFromUpload(path);
                }else {
                    ToastUtil.showToast("视频导出失败，请重试");
                    FileUtil.deleteFile(path);
                }
                hideDialog();
            }
        });
    }

    int localHeight = 960;
    int localWidth = 720;

    private int[] getTargetWidthNew(int videoWidth,int videoHeight){
        float targetWidth = videoWidth;
        float targetHeight = videoHeight;
        float rate = videoHeight*1.0f / videoWidth *1.0f;
        float localRate = localHeight*1.0f/localWidth*1.0f;
        if(videoHeight>localHeight){
            if(rate>localRate){
                targetHeight = Math.max(localHeight,localWidth);
                targetWidth = targetHeight*1.0f / rate;
            }else if(rate<localRate){
                targetWidth = Math.max(localHeight,localWidth);
                targetHeight = targetWidth * rate;
            }else {//
                targetHeight = Math.max(localHeight,localWidth);
                targetWidth = targetHeight*1.0f / rate;
            }

        }else {
            if(videoWidth>localWidth){
                targetWidth = Math.max(localHeight,localWidth);
                targetHeight = targetWidth* rate;
            }else if(videoWidth<localWidth){//太小了不用变

            }else {

            }
        }
        if(((int)targetHeight)%2!=0){
            targetHeight = targetHeight+1;
        }
        if(((int)targetWidth)%2!=0){
            targetWidth = targetWidth+1;
        }
        //需要可以整除16
        while (((int)targetHeight)%16 != 0){
            targetHeight++;
        }
        while (((int)targetWidth)%16 != 0){
            targetWidth++;
        }

        return new int[]{(int)targetWidth,(int)targetHeight};
    }

    private void clipVideo(long start,long duration){
        duration = Math.min(duration,(long) RecordSetting.MAX_VIDEO_DURATION*1000);
//        if (dialog == null) {
//            dialog = SysAlertDialog.showCircleProgressDialog(TrimVideoActivityNew.this, getString(R.string.join_media), true, false);
//        }
        new VideoEngine().cutVideoRecord(shortVideoPath, start, duration, new VideoListener() {

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
                    if(startType == TYPE_FROM_DIRECT_UPLOAD){//如果是直接上传，直接走导出视频，TimeRange 为裁剪时间
                        shortVideoPath = outpath;
                        channel2To1(shortVideoPath);

                    }else{//多格子录制 ，1.导出视频，(指定宽高，时间) 2.旋转视频 如果需要 3.分离音视频，
//                        clipVideo((long) startTime * 1000 * 1000, (long) ((endTime - startTime) * 1000 * 1000));
                        rotateVideo(outpath);
                    }
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

    /**
     * 直接导入，跳转
     */
    private void toPublishFromUpload(String videoPath){
        RecordManager.get().getProductEntity().combineVideoAudio = videoPath;
        KLog.i("import---trime--combineVideoAudio-->" + videoPath);
        RecordManager.get().updateProduct();
        LocalPublishActivity.startLocalPublishActivity(TrimVideoActivityNew.this, LocalPublishActivity.FORM_SEARCH);
        finish();
    }

    /**
     * 返回录像页面
     */
    private void toRecord(String combineVideo,String videoPath,String audioPath){
        LocalUploadResultEntity entity = new LocalUploadResultEntity();
        if(audioPath==null || new File(audioPath).length()<200) {//音频太小，该视频没有声音
            audioPath = null;
        }
        if(videoPath==null || new File(videoPath).length()<200) {//音频太小，该视频没有声音
            ToastUtil.showToast("视频处理失败");
            return;
        }
        entity.combineAV = combineVideo;
        entity.audioPath = audioPath;
        entity.videoPath = videoPath;
        KLog.i("toRecord-->"+combineVideo+VideoUtils.getVideoLength(combineVideo)+"||"+VideoUtils.getVideoLength(videoPath));
        //如果音频不存在，此视的音频文件很小，注意，之后的所有的音频操作，当音频文件很小时候，代表音频文件没有内容，不做未入处理
        EventHelper.post(GlobalParams.EventType.TYPE_TRIM_FINISH, entity);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        //暂停播放
        pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        if (playerEngine != null && !playerEngine.isNull()) {
            playerEngine.release();
        }
        playerEngine = null;
        if(dialog!=null)
            dialog.dismiss();
        dialog = null;
    }

    public static void startTrimVideoActivity(Context ctx, int index, String shortVideoPath, int startType) {
        Intent intent = new Intent(ctx, TrimVideoActivityNew.class);
        intent.putExtra(SearchVideoActivity.START_TYPE_FROM, startType);
        intent.putExtra(SearchVideoActivity.SHORT_VIDEO_INDEX, index);
        intent.putExtra(SearchVideoActivity.SHORT_VIDEO_PATH, shortVideoPath);
        ctx.startActivity(intent);
    }

    private CustomTrimVideoView.OnRangeChangeListener onRangeChangeListener = new CustomTrimVideoView.OnRangeChangeListener() {
        @Override
        public void onValuesChanged(int minValue, int maxValue, int duration, int changeType) {
            setTimeRange(minValue * 1f, maxValue * 1f);
            KLog.i("onValuesChanged-->" + duration + "duration-->" + Math.round((duration * 1f / 1000f)));
        }
    };



    private void setTimeRange(float startTime, float endTime) {
        this.startTime = startTime / 1000f;
        this.endTime = endTime / 1000f;
        MediaObject mediaObject = (MediaObject) mScene.assets.get(0);
        KLog.i("trime--start" + startTime + "end>" + endTime);
        if (endTime > 0) {
            mediaObject.setTimeRange(((long) startTime) * 1000, ((long) (endTime-startTime) * 1000));
        }

        List<Scene> scenes = new ArrayList<Scene>();
        scenes.add(mScene);
        if (playerEngine != null && !playerEngine.isNull()) {
            pause();
            playerEngine.setScenceAndPrepare(scenes);
            playerEngine.seekTo(0.0f);
        }
    }


    static class LocalUploadResultEntity extends BaseModel{
        String combineAV;
        String videoPath;
        String audioPath;

    }

}
