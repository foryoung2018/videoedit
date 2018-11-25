package com.wmlive.hhvideo.heihei.quickcreative;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.os.ResultReceiver;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import com.dc.platform.voicebeating.NotifyCallback;
import com.dc.platform.voicebeating.VoiceBeatingAudioPlayer;
import com.dc.platform.voicebeating.DCVoiceBeatingTool;
import com.dongci.sun.gpuimglibrary.common.SLVideoProcessor;
import com.dongci.sun.gpuimglibrary.player.DCAsset;
import com.dongci.sun.gpuimglibrary.player.DCBitmapManager;
import com.dongci.sun.gpuimglibrary.player.script.DCScriptManager;
import com.dongci.sun.gpuimglibrary.player.script.DCTimeEventManager;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.heihei.beans.quickcreative.CreativeTemplateListBean;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.mainhome.widget.VideoControlBar;
import com.wmlive.hhvideo.heihei.record.activity.PublishMvActivity;
import com.wmlive.hhvideo.heihei.record.engine.PlayerEngine;
import com.wmlive.hhvideo.heihei.record.engine.config.MVideoConfig;
import com.wmlive.hhvideo.heihei.record.engine.config.PlayerConfig;
import com.wmlive.hhvideo.heihei.record.engine.listener.ExportListener;
import com.wmlive.hhvideo.heihei.record.engine.listener.PlayerCreateListener;
import com.wmlive.hhvideo.heihei.record.engine.listener.PlayerListener;
import com.wmlive.hhvideo.heihei.record.engine.model.MAsset;
import com.wmlive.hhvideo.heihei.record.engine.model.MScene;
import com.wmlive.hhvideo.heihei.record.engine.model.MediaObject;
import com.wmlive.hhvideo.heihei.record.engine.model.Scene;
import com.wmlive.hhvideo.heihei.record.engine.utils.VideoUtils;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.utils.RecordFileUtil;
import com.wmlive.hhvideo.heihei.record.widget.CircleProgressDialog;
import com.wmlive.hhvideo.heihei.record.widget.SysAlertDialog;
import com.wmlive.hhvideo.heihei.record.widget.TextureVideoViewOutlineProvider;
import com.wmlive.hhvideo.utils.AppCacheFileUtils;
import com.wmlive.hhvideo.utils.FileUtil;
import com.wmlive.hhvideo.utils.JsonUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ScreenUtil;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.utils.download.DownloadBean;
import com.wmlive.hhvideo.utils.download.FileDownload;
import com.wmlive.hhvideo.utils.preferences.SPUtils;

import org.jcodec.scale.ColorUtil;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

import static com.wmlive.hhvideo.heihei.quickcreative.CreativeRecprderrvAdapter.NOTIFYTYPE_LOADING;
import static com.wmlive.hhvideo.heihei.quickcreative.CreativeRecprderrvAdapter.NOTIFYTYPE_SELECT;
import static com.wmlive.hhvideo.heihei.quickcreative.CreativeRecprderrvAdapter.TYPE_BG;
import static com.wmlive.hhvideo.heihei.quickcreative.CreativeRecprderrvAdapter.TYPE_MUSIC;
import static com.wmlive.hhvideo.heihei.record.manager.RecordSetting.VIDEO_EXPORT_HEIGHT;
import static com.wmlive.hhvideo.heihei.record.manager.RecordSetting.VIDEO_EXPORT_WIDTH;
import static com.wmlive.hhvideo.heihei.record.utils.RecordUtilSdk.exportMvVideo;
import static com.wmlive.hhvideo.utils.preferences.SPUtils.CREATIVE_ZIP_LIST;
import static java.lang.StrictMath.abs;

public class ChooseStyle4QuickActivity extends DcBaseActivity implements ChooseStyle4QuickPresenter.IchooseStyleView, CreativeRecprderrvAdapter.ItemCllickListener {


    @BindView(R.id.rv_bg)
    RecyclerView rv_bg;
    @BindView(R.id.rv_music)
    RecyclerView rv_music;
    @BindView(R.id.tv_back)
    TextView tv_back;
    @BindView(R.id.tv_next)
    TextView tv_next;
    @BindView(R.id.tv_musicname)
    TextView tv_musicname;
    @BindView(R.id.rl_video_container)
    RelativeLayout rl_video_container;
    @BindView(R.id.rl_root)
    RelativeLayout rl_root;
    @BindView(R.id.seekbar)
    VideoControlBar seekbar;
    @BindView(R.id.iv_play_icon)
    ImageView iv_play_icon;

    private static final String TAG = "ChooseStyle4QuickActivity";
    public static final String SEEKBAR = "ChooseStyle4QuickActivity";
    private CreativeRecprderrvAdapter adapterMusic;
    private CreativeRecprderrvAdapter adapterBG;
    private List<CreativeTemplateListBean.TemplateListBean> template_list;
    private List<CreativeTemplateListBean.BgListBean> bg_list;
    private CreativeTemplateListBean.TemplateListBean templateListBean;
    private CreativeTemplateListBean.BgListBean bgListBean;

    private final String savepath = AppCacheFileUtils.getAppCreativePath();
    private List<MediaObject> list = new ArrayList<MediaObject>();//素材视频 asset
    private List<MediaObject> listbgVideo = new ArrayList<MediaObject>();//背景视频和挂件 asset

    private Handler handler;
    private android.support.v4.os.ResultReceiver fileDownloadReceiver;
    private PlayerEngine playerEngine;
    private List<ShortVideoEntity> shortVideoList;
    private String currentBgName;
    private long duration;
    private TextureView textureView;
    private ConfigJsonBean configJsonBean;
    private AlphaAnimation mShowAnimation;//渐隐动画
    private AlphaAnimation mHideAnimation;//渐隐动画

    private VoiceBeatingAudioPlayer mAudioPlayer = null;

    private DCVoiceBeatingTool voiceBeatingTool = null;
    private boolean isVoiceOk;
    private boolean isStatePaused = false;
    private long bgmDuration = Long.MAX_VALUE;

    private boolean isPause = false;
    private boolean needReload = false;//第一次安装app到此页面没下载好


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_choose_style4_quick;
    }

    @Override
    protected ChooseStyle4QuickPresenter getPresenter() {
        return new ChooseStyle4QuickPresenter(this);
    }

    public static void startChooseStyleQuikActivity(Context context) {
        Intent intent = new Intent(context, ChooseStyle4QuickActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void initData() {
        super.initData();
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        adapterMusic = new CreativeRecprderrvAdapter(this, CreativeRecprderrvAdapter.TYPE_MUSIC);
        adapterBG = new CreativeRecprderrvAdapter(this, TYPE_BG);
        rv_music.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv_music.setAdapter(adapterMusic);
        rv_bg.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv_bg.setAdapter(adapterBG);
        adapterBG.setItemCllickListener(this);
        adapterMusic.setItemCllickListener(this);
        rl_video_container.setOnClickListener(this);
        iv_play_icon.setOnClickListener(this);
        tv_back.setOnClickListener(this);
        tv_next.setOnClickListener(this);
//        initSeekBar();
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1://更新进度条进度
                        seekbar.setPosition((int) playerEngine.getDuration() / 1000, (int) playerEngine.getCurrentPosition() / 1000);
                        Log.e("seekPosition", "position " + playerEngine.getCurrentPosition());
                        handler.sendEmptyMessageDelayed(1, 1000);
                        break;
                    case 2://隐藏歌曲名
                        setHideAnimation(tv_musicname, 1000);
                        break;
                }
            }
        };
        fileDownloadReceiver = new FileDownloadReceiver(ChooseStyle4QuickActivity.this, handler);
        shortVideoList = RecordManager.get().getProductEntity().shortVideoList;
        initLayoutParams();
        initVideoList();
        getList();

    }

    /**
     * 进度条处理
     */
    private void initSeekBar() {
        seekbar.init(SEEKBAR);
        seekbar.setOnControlBarListener(new VideoControlBar.OnControlBarClickListener() {
            @Override
            public void onPlayClick() {
                KLog.d(TAG, "onPlayClick: playerEngine.isPlaying()==" + playerEngine.isPlaying());
                if (playerEngine.isPlaying()) {

                } else {

                }
            }

            @Override
            public void onFullScreenClick(boolean isFull) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    KLog.d(TAG, "onProgressChanged: progress==" + progress + "  duration==" + duration);
                    KLog.d(TAG, "onProgressChanged: progress==" + progress + "  playerEngine.getDuration()==" + playerEngine.getDuration());

                    long position = mAudioPlayer.seek((long) (progress * duration * 10));
                    playerEngine.seekToPlay(position * 1000, true);
                    //playerEngine.seekToPlay((long) (progress * duration * 10000), true);


                    //mAudioPlayer.seek((long) (progress * duration * 10));
                    //mAudioPlayer.seek();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * 设置播放器宽高以及位置
     */
    private void initLayoutParams() {
        int height = ScreenUtil.getHeight(this) - ScreenUtil.dip2px(this, 180);
        int width = (int) (height * 9.0f / 16);
        KLog.d(TAG, "initLayoutParams: width===" + width + "  height==" + height);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rl_video_container.getLayoutParams();
        layoutParams.height = height;
        layoutParams.width = width;
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.addRule(RelativeLayout.BELOW, R.id.rl_bar);
        rl_video_container.setLayoutParams(layoutParams);
    }

    /**
     * 添加素材
     */
    private void initVideoList() {
        for (int i = 0; i < shortVideoList.size(); i++) {
            ShortVideoEntity videoEntity = shortVideoList.get(i);
            if (!TextUtils.isEmpty(videoEntity.editingVideoPath) && videoEntity.extendInfo != null && videoEntity.extendInfo.videoImgs != null) {
                MediaObject mediaObject = new MAsset();
                mediaObject.assetId = i + 1;
                mediaObject.setSourceType(DCAsset.DCAssetTypeImages);
                mediaObject.setFillType(DCAsset.DCAssetFillTypeScaleToFit);
                mediaObject.setCropRect(new RectF(0, 0, 720 / 2, 960 / 2));
                mediaObject.setRectInVideo(new RectF(0, 0.5f * (1280.0f - 960.0f) / 1280.0f, 1.0f, 1.0f - 0.5f * (1280.0f - 960.0f) / 1280.0f));
                mediaObject.setTimeRange(new DCAsset.TimeRange(0L, VideoUtils.getVideoLength(videoEntity.editingVideoPath)));
                KLog.i("素材 -TimeRange>" + VideoUtils.getVideoLength(videoEntity.editingVideoPath));
                mediaObject.setStartTimeInScene(0L);
                mediaObject.setImagePaths(videoEntity.extendInfo.videoImgs);
                mediaObject.setFrameInterval((long) (1000000 * 1.0f / 24.0f));
                KLog.i("setFrameInterval--素材->" + (long) (1000000 * 1.0f / 12.0f));
                list.add(mediaObject);
            }

        }

        MediaObject mediaObject = new MAsset();
        mediaObject.assetId = shortVideoList.size() + 2;
        mediaObject.setSourceType(DCAsset.DCAssetTypeImages);
        mediaObject.setFillType(DCAsset.DCAssetFillTypeScaleToFit);
        mediaObject.setCropRect(new RectF(0, 0, 512, 682));
        mediaObject.setRectInVideo(new RectF(0, 0.5f * (1280.0f - 960.0f) / 1280.0f, 1.0f, 1.0f - 0.5f * (1280.0f - 960.0f) / 1280.0f));
        mediaObject.setTimeRange(new DCAsset.TimeRange(0L, (long) (1.5 * 1000000.0)));
        mediaObject.setStartTimeInScene(0L);

        String path = AppCacheFileUtils.getAppCreativeAssetsPath() + "billboard.png";
        List<String> billboardUrls = new ArrayList<String>();
        billboardUrls.add(path);
        mediaObject.setImagePaths(billboardUrls);
        mediaObject.setFrameInterval((long) (1000000 * 1.0f / 12.0f));
        mediaObject.isBillboard = true;
        list.add(mediaObject);
        // billboard
//        LFAsset *asset7 = [[LFAsset alloc] init];
//        asset7.type = LFAssetTypeImages;
//        asset7.fillType = LFFillTypeScaleToFit;
//        asset7.cropRect = CGRectMake(0, 0, 512, 682);
//        asset7.rectInVideo = LFGridRectMake(CGPointMake(0, 0.5*(1280.0 - 960.0)/1280.0), CGPointMake(1.0, 1.0 - 0.5*(1280.0 - 960.0)/1280.0));
//        asset7.startTimeInScene = kCMTimeZero;
//        asset7.timeRange = CMTimeRangeMake(kCMTimeZero, CMTimeMakeWithSeconds(1.5, 600));
//        asset7.volume = 1.0;
//        asset7.imageUrls = billboardUrls;
//        asset7.frameInterval = 1.0 / 12.0;
//        asset7.assetId = (int)assets.count;
//        asset7.isBillboard = YES;

    }

    @Override
    protected void onResume() {
        super.onResume();
        isPause = false;
    }

    private void getList() {
        loading(false, getString(R.string.bianshen));
        String string = SPUtils.getString(this, CREATIVE_ZIP_LIST, "");
        if (TextUtils.isEmpty(string)) {//缓存为空时  联网获取
            getPresenter().getCreativeList();
        } else {
            CreativeTemplateListBean musiclist = JsonUtils.parseObject(string, CreativeTemplateListBean.class);
            KLog.d(TAG, "getList: musiclist==" + musiclist);
            setListDate(musiclist);
        }
    }

    private void setListDate(CreativeTemplateListBean musiclist) {
        String default_bg = RecordManager.get().getProductEntity().getExtendInfo().bg_name;
        String template_name = RecordManager.get().getProductEntity().getExtendInfo().template_name;

        //模板
        template_list = musiclist.getTemplate_list();
        List<String> musicImags = new ArrayList<>();
        int def = 0;
        int current = 0;
        for (int i = 0; i < template_list.size(); i++) {
            CreativeTemplateListBean.TemplateListBean templateListBean = template_list.get(i);
            musicImags.add(templateListBean.getTemplate_cover());
            if (templateListBean.getIs_default() == 1) {
                def = i;
            }
            if (templateListBean.getTemplate_name().equals(template_name)) {
                this.templateListBean = templateListBean;
                current = i;
            }
        }
        if (templateListBean == null) {
            this.templateListBean = template_list.get(def);
            adapterMusic.setSelectIndex(def);
            rv_music.scrollToPosition(def);
        } else {
            adapterMusic.setSelectIndex(current);
            rv_music.scrollToPosition(current);
        }
        adapterMusic.updateList(musicImags);


        //bg素材
        bg_list = musiclist.getBg_list();
        if (TextUtils.isEmpty(default_bg)) {
            default_bg = templateListBean.getDefault_bg();
        }
        List<String> bgImages = new ArrayList<>();
        int defbgindex = 0;
        for (int j = 0; j < bg_list.size(); j++) {
            CreativeTemplateListBean.BgListBean bgListBean = bg_list.get(j);
            if (bgListBean.getIs_default() == 1) {
                defbgindex = j;
            }
            bgImages.add(bgListBean.getBg_cover());
            if (bgListBean.getBg_name().equals(default_bg)) {
                adapterBG.setSelectIndex(j);
                rv_bg.scrollToPosition(j);
                this.bgListBean = bg_list.get(j);
            }
        }

        if (bgListBean == null) {
            this.bgListBean = bg_list.get(defbgindex);
            adapterBG.setSelectIndex(defbgindex);
            rv_bg.scrollToPosition(defbgindex);
        }
        adapterBG.updateList(bgImages);
        if (bgListBean == null || template_name == null) {
            showError("");
            return;
        }
        if (CreativeQuickUtils.isFileEmpty(templateListBean.getTemplate_name())
                || CreativeQuickUtils.isFileEmpty(bgListBean.getBg_name())) {
            ArrayList<DownloadBean> downloadList = new ArrayList<>();
            if (templateListBean != null) {
                int downloadId = FileDownloadUtils.generateId(templateListBean.getZip_path(), savepath);
                DownloadBean downloadBean = new DownloadBean(downloadId, templateListBean.getZip_path(),
                        savepath, "", "", 200);
                downloadList.add(downloadBean);
            }
            if (bgListBean != null) {
                int downloadId = FileDownloadUtils.generateId(bgListBean.getBg_resource(), savepath);
                DownloadBean downloadBean = new DownloadBean(downloadId, bgListBean.getBg_resource(),
                        savepath, "", "", 100);
                downloadList.add(downloadBean);
            }
            FileDownload.start(this, downloadList, fileDownloadReceiver, true);
            needReload = true;
        } else {
            generatePreview(1);
        }


    }


    private void initPlayer(List<MediaObject> listPlay) {
        if (playerEngine != null)
            playerEngine.reset();
        else
            playerEngine = new PlayerEngine();

        textureView = new TextureView(this);
        textureView.setOutlineProvider(new TextureVideoViewOutlineProvider(30));
        textureView.setClipToOutline(true);
        rl_video_container.removeAllViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        rl_video_container.addView(textureView, params);
        playerEngine.build(textureView, VIDEO_EXPORT_WIDTH, VIDEO_EXPORT_HEIGHT, PlayerConfig.fps, false, new PlayerCreateListener() {
            @Override
            public void playCreated() {
                if (bgConfigJsonBean != null && bgConfigJsonBean.getBackgroundColor() != null)//给播放器设置背景色
                    playerEngine.setBackgroundColor(Color.parseColor(bgConfigJsonBean.getBackgroundColor()));

                playerEngine.setMediaAndPrepare(listPlay);
                playerEngine.setAutoRepeat(true);
                isStatePaused = false;

                mAudioPlayer.setVoiceBeatingTool(voiceBeatingTool);
                if (isPause)//如果当前已经 暂停
                    return;
                mAudioPlayer.setAutoRepeat(true);
                mAudioPlayer.play();
                playerEngine.start();

                dismissLoad();
                iv_play_icon.setVisibility(View.GONE);
//                handler.sendEmptyMessage(1);
                KLog.d(TAG, "playCreated: ");
                playerEngine.setOnPlaybackListener(new PlayerListener() {
                    @Override
                    public void onPlayerPrepared() {
                        KLog.i(TAG, "**** * onPlayerPrepared currentPosition ");
                    }

                    @Override
                    public boolean onPlayerError(int var1, int var2) {
                        KLog.d(TAG, "onPlayerError: ");
                        return false;
                    }

                    @Override
                    public void onPlayerCompletion() {
                        if (mAudioPlayer != null && !isStatePaused) {
                            mAudioPlayer.restart();
                            KLog.d(TAG, "onPlayerCompletion: 11");
                        }
                        KLog.d(TAG, "onPlayerCompletion: ");
                    }

                    @Override
                    public void onGetCurrentPosition(float var1) {

                    }
                });
            }
        });
        playerEngine.setAspectRatioFitMode(MediaObject.FillTypeScaleToFit);
        playerEngine.setPreviewAspectRatio(RecordManager.get().getSetting().getVideoRatio());


        if (mAudioPlayer != null) {
            mAudioPlayer.release();
            mAudioPlayer = null;
        }

        if (mAudioPlayer == null) {
            mAudioPlayer = new VoiceBeatingAudioPlayer();
        }

        mAudioPlayer.prepare();
        mAudioPlayer.setAutoRepeat(true);

        mAudioPlayer.setAudioPlayerListener(new VoiceBeatingAudioPlayer.AudioPlayerListener() {
            @Override
            public void OnPlayingPosition(long time) {

                long currenttime = playerEngine.getCurrentPosition();

                if (abs(currenttime / 1000 - time) > 100 && currenttime < bgmDuration) {
                    mAudioPlayer.seek(currenttime / 1000);
                    Log.e("seekPosition", "seek to 1 " + (currenttime / 1000) + " diff " + (currenttime / 1000 - time));

                }
                Log.e("PlayerEngine video ", "now videotime is 1   " + playerEngine.getCurrentPosition());
            }
        });

    }

    private void startPlay() {
        isStatePaused = false;
        if (playerEngine != null) {
            playerEngine.setAutoRepeat(true);
            playerEngine.start();
            if (mAudioPlayer != null)
                mAudioPlayer.play();
            iv_play_icon.setVisibility(View.GONE);
        }
//        seekbar.setPlayStatus(false, SEEKBAR);
    }

    private void pausePlay() {
        isStatePaused = true;
        if (playerEngine != null) {
            playerEngine.setAutoRepeat(false);
            playerEngine.pause();
        }
        if (mAudioPlayer != null)
            mAudioPlayer.pause();
        iv_play_icon.setVisibility(View.VISIBLE);
        KLog.d(TAG, "pausePlay: ");
//        seekbar.setPlayStatus(true, SEEKBAR);
    }

    @Override
    protected void onSingleClick(View v) {

        if (v == tv_back) {
            onBack();
        } else if (v == tv_next) {
            loading();
            if (playerEngine != null) {
                pausePlay();
                playerEngine.reset();
            }
            combineVideoAudio();
        } else if (v == rl_video_container) {
            if (playerEngine.isPlaying()) {
                pausePlay();
            } else {
                startPlay();
            }

        } else if (v == iv_play_icon) {
            startPlay();
        }

    }

    @Override
    public void getMusicList(CreativeTemplateListBean musiclist) {
        setListDate(musiclist);
    }

    @Override
    public void itemClick(int position, String imagePath, int type) {
        if (type == TYPE_BG) {
            if (CreativeQuickUtils.isFileEmpty(bg_list.get(position).getBg_name())) {
                downloadBGZip(position);
            } else {
                bgListBean = bg_list.get(position);
                generatePreview(1);
                adapterBG.notifyDC(NOTIFYTYPE_SELECT, position);
            }

        } else if (type == TYPE_MUSIC) {
            if (CreativeQuickUtils.isFileEmpty(template_list.get(position).getTemplate_name())) {//文件不存在去下载
                downloadTemplateZip(position);
            } else {//文件存在 生成预览
                templateListBean = template_list.get(position);
                generatePreview(1);
                adapterMusic.notifyDC(NOTIFYTYPE_SELECT, position);
            }
        }
    }

    private void downloadBGZip(int index) {
        CreativeTemplateListBean.BgListBean bgListBean = bg_list.get(index);
        if (bgListBean != null) {
            int downloadId = FileDownloadUtils.generateId(bgListBean.getBg_resource(), savepath);
            DownloadBean downloadBean = new DownloadBean(downloadId, bgListBean.getBg_resource(),
                    savepath, "", "", index);
            downloadBean.type = 200;
            ArrayList<DownloadBean> downloadList = new ArrayList<>();
            downloadList.add(downloadBean);
            FileDownload.start(this, downloadList, fileDownloadReceiver, true);
            adapterBG.notifyPosition(true, index);
        }
    }

    private void downloadTemplateZip(int index) {
        CreativeTemplateListBean.TemplateListBean templateListBean = template_list.get(index);
        if (templateListBean != null) {
            int downloadId = FileDownloadUtils.generateId(templateListBean.getZip_path(), savepath);
            DownloadBean downloadBean = new DownloadBean(downloadId, templateListBean.getZip_path(),
                    savepath, "", "", index);
            downloadBean.type = 100;
            ArrayList<DownloadBean> downloadList = new ArrayList<>();
            downloadList.add(downloadBean);
            FileDownload.start(this, downloadList, fileDownloadReceiver, true);
            adapterMusic.notifyPosition(true, index);
        }
    }

    private String getBgVideo(String inputVideo) {
        String outPath = RecordFileUtil.createTimestampFile(RecordManager.get().getProductEntity().baseDir,
                RecordManager.PREFIX_CREATIVE_VIDEO_FILE,
                RecordManager.SUFFIX_VIDEO_FILE, true);

        //duration = VideoUtils.getAudioLength(RecordManager.get().getProductEntity().combineAudio) * 1.0f / 1000000.0f;

        duration = Math.max((long) voiceBeatingTool.getAudioDuration() * 1000,
                (long) VideoUtils.getAudioLength(savepath + templateListBean.getTemplate_name() + File.separator + configJsonBean.getBackground_music()));

        KLog.d(TAG, "generatePreview: inputVideo==" + inputVideo);
        KLog.d(TAG, "generatePreview: audio-lenght==" + duration);
        try {
            SLVideoProcessor.getInstance().generateLoopVideo(inputVideo, duration * 1.0 / 1000000, outPath);
            RecordManager.get().getProductEntity().combineVideo = outPath;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outPath;
    }

    private void onDownloadComplete(int type, int position) {
        Log.d("itemClick", "onDownloadComplete: type==" + type + "   position==" + position);
        if (type == 100) {
            String zipFilePath = getZipFilePath(template_list.get(position).getZip_path());//下载的zip文件路径
            CreativeQuickUtils.doUnzip(zipFilePath, template_list.get(position).getTemplate_name());
            adapterMusic.notifyPosition(false, position);
        } else if (type == 200) {
            String imagePath = getZipFilePath(bg_list.get(position).getBg_resource());//下载的bg资源包路径
            CreativeQuickUtils.doUnzip(imagePath, bg_list.get(position).getBg_name());
            adapterBG.notifyPosition(false, position);

        }

        if (needReload) {
            needReload = false;
            generatePreview(1);
        }

    }

    /**
     * 生成创意视频预览
     */
    public void generatePreview(int type) {
        //需要的素材 1、zip包  2、背景图片  3、短视频加音频
        loading(false, getString(R.string.bianshen));
        pausePlay();
        new Thread() {
            @Override
            public void run() {
                super.run();
                templateChange();
            }
        }.start();
    }

    BgConfigJsonBean bgConfigJsonBean;

    private void bgVideoChange() {

        String combineAudio = RecordManager.get().getProductEntity().combineAudio;
        listbgVideo.clear();

        /**
         * 添加zip包中背景视频
         */
        String bg_path = savepath + bgListBean.getBg_name();
        String bgfileString = FileUtil.getFileString(bg_path + "/backgroundConfig.json");
        bgConfigJsonBean = JsonUtils.parseObject(bgfileString, BgConfigJsonBean.class);
        if (bgConfigJsonBean == null) {
            ToastUtil.showToast("背景获取失败");
            return;
        }
        RecordManager.get().getProductEntity().extendInfo.bgColor = bgConfigJsonBean.getBackgroundColor();

        String bgVideo = getBgVideo(bg_path + File.separator + bgConfigJsonBean.getBgVideo());
        MediaObject mediaObjectBg = new MAsset(bgVideo);
//        mediaObjectBg.setShowRectF(new RectF(0,0,0.8f,1));
        listbgVideo.add(mediaObjectBg);

        KLog.d(TAG, "generatePreview: combineAudio==" + combineAudio);
        KLog.d(TAG, "generatePreview: bgVideo==" + bgVideo);

        /**
         * 添加挂件
         */

        if (bgConfigJsonBean.getDecorations() != null && bgConfigJsonBean.getDecorations().size() > 0) {
            BgConfigJsonBean.DecorationsBean decorationsBean = bgConfigJsonBean.getDecorations().get(0);
            if (decorationsBean != null && decorationsBean.getImages() != null && decorationsBean.getImages().size() > 0) {
                List<String> imagePaths = new ArrayList<String>(decorationsBean.getImages().size());
                for (String s : decorationsBean.getImages()) {
                    String imgPath = bg_path + File.separator + s;
                    imagePaths.add(imgPath);
                }
                MediaObject mediaObject = new MAsset();
                mediaObject.assetId = shortVideoList.size() + 1;
                mediaObject.setSourceType(DCAsset.DCAssetTypeImages);
                mediaObject.setFillType(DCAsset.DCAssetFillTypeScaleToFit);
                mediaObject.setCropRect(new RectF(0, 0, 512, 512));
                mediaObject.setRectInVideo(new RectF(0.5f * (720.0f - 512.0f) / 720.0f, 0.5f * (1280.0f - 512.0f) / 1280.0f, 1.0f - 0.5f * (720.0f - 512.0f) / 720.0f, 1.0f - 0.5f * (1280.0f - 512.0f) / 1280.0f));
                mediaObject.setImagePaths(imagePaths);
                mediaObject.setDecorationName(decorationsBean.getName());
                mediaObject.setDecorationMaskPath(bg_path + File.separator + decorationsBean.getMask());
                mediaObject.setFrameInterval((long) (1000000 * 1.0f / (decorationsBean.getFrameRate() * 1.0f)));
                KLog.d(TAG, "bgVideoChange: decorationsBean.getFrameRate()==" + decorationsBean.getFrameRate());
                KLog.i("setFrameInterval" + ((long) (1000000 * 1.0f / (decorationsBean.getFrameRate() * 1.0f))));
                KLog.i("setDecorationMaskPath--》" + (bg_path + File.separator + decorationsBean.getMask()));
                listbgVideo.add(mediaObject);
            }
        }

    }

    private void templateChange() {
        DCTimeEventManager.timeEventManager().clearEvents();
        String path = savepath + templateListBean.getTemplate_name();
        String fileString = FileUtil.getFileString(path + File.separator + "config.json");
        configJsonBean = JsonUtils.parseObject(fileString, ConfigJsonBean.class);
        if (configJsonBean == null) {
            dismissLoad();
            return;
        }
        int size = configJsonBean.getItems().size();


        if (size > 0 && size < 6) {
            shortVideoList = shortVideoList.subList(0, size - 1);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_musicname.setText(templateListBean.getTitle());
                currentBgName = configJsonBean.getBackground_music();
                setShowAnimation(tv_musicname, 1000);
            }
        });

        KLog.d("dataReadyCallback", "stop thread begin");
        //切换模板停止上次的处理线程
        if (voiceBeatingTool != null) {
            voiceBeatingTool.stopThread();
            voiceBeatingTool = null;
        }

        if (voiceBeatingTool == null) {
            voiceBeatingTool = new DCVoiceBeatingTool();
        }

        KLog.d("dataReadyCallback", "stop thread");

        KLog.d("configJsonBean", "generatePreview: configJsonBean==" + configJsonBean);
        if (configJsonBean != null) {
            voiceBeatingTool.setVoiceBeatCallback(new NotifyCallback() {
                @Override
                public void dataReadyCallback() {
                    //音频数据准备完成，可以获取音频数据
                    KLog.d("dataReadyCallback", "dataReadyCallback ready");

                    bgVideoChange();
                    //添加脚本
                    DCVoiceBeatingTool.prepareScript(savepath + templateListBean.getTemplate_name(), savepath + bgListBean.getBg_name() + "/backgroundConfig.json");
                    play();
                    if (templateListBean != null && bgListBean != null) {
                        RecordManager.get().getProductEntity().extendInfo.template_name = templateListBean.getTemplate_name();
                        RecordManager.get().getProductEntity().extendInfo.bg_name = bgListBean.getBg_name();
                        RecordManager.get().updateProduct();
                    }

                }
            });
            voiceBeatingTool.beatingVoice(path, shortVideoList, configJsonBean);

            KLog.d("dataReadyCallback", "beating voice");
            isVoiceOk = false;
            Thread thread = new Thread(new Runnable() {


                @Override
                public void run() {

                    voiceBeatingTool.generateBeatingVoiceFile();

                    KLog.d("dataReadyCallback", "generate file");
                    //wav文件生成完成
                    isVoiceOk = true;
                    if (onCompleteListener != null) {
                        onCompleteListener.complete();
                    }
                }
            });
            thread.start();
        }
    }

    public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }

    private OnCompleteListener onCompleteListener;


    public interface OnCompleteListener {
        void complete();
    }

    public void play() {
        List<MediaObject> listPlay = new ArrayList<>();
        listPlay.addAll(listbgVideo);
        listPlay.addAll(list);
        if (!TextUtils.isEmpty(configJsonBean.getBackground_music())) {
            String bg_music_path = savepath + templateListBean.getTemplate_name() + File.separator + configJsonBean.getBackground_music();
            MediaObject mediaObject = new MAsset(bg_music_path);
            KLog.i("播放item音频 的地址：" + bg_music_path);
            mediaObject.setSourceType(MediaObject.MediaObjectTypeAudio);
            mediaObject.assetId = shortVideoList.size() + 3;
            mediaObject.assetId = shortVideoList.size() + 3;
            mediaObject.setTimeRange(0, (long) duration);
            KLog.i("素材 -play>" + duration);
            bgmDuration = (long) VideoUtils.getAudioLength(savepath + templateListBean.getTemplate_name() + File.separator + configJsonBean.getBackground_music());
            //获取视频的时长
            mediaObject.setVolume(0.70f);
            listPlay.add(mediaObject);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initPlayer(listPlay);
            }
        });
    }

    private CircleProgressDialog dialog;

    public void showDialog() {
        if (dialog == null) {
            dialog = SysAlertDialog.createCircleProgressDialog(this, getString(R.string.hecheng), true, false);
        }
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    public void combineVideoAudio() {
        if (configJsonBean == null) {
            return;
        }
        List<MediaObject> list = new ArrayList<>();
        list.addAll(listbgVideo);
        list.addAll(this.list);
        if (!TextUtils.isEmpty(configJsonBean.getBackground_music())) {
            String bg_music_path = savepath + templateListBean.getTemplate_name() + File.separator + configJsonBean.getBackground_music();
            MediaObject mediaObject = new MAsset(bg_music_path);
            KLog.i("播放item音频 的地址：" + bg_music_path);
            mediaObject.setSourceType(MediaObject.MediaObjectTypeAudio);
            mediaObject.assetId = shortVideoList.size() + 3;
            mediaObject.setTimeRange(0, (long) VideoUtils.getAudioLength(bg_music_path));

            //获取视频的时长
            mediaObject.setVolume(0.7f);
            list.add(mediaObject);
        }
        if (listbgVideo.size() > 0 && playerEngine != null) {
            long l = 100000;
            if (configJsonBean.getThumbnail_generate_time() != null) {
                l = (long) Double.parseDouble(configJsonBean.getThumbnail_generate_time()) * 1000000;
            }
            RecordManager.get().getProductEntity().getExtendInfo().thumbnail_generate_time = l;

            if (templateListBean != null && bgListBean != null) {
                RecordManager.get().getProductEntity().getExtendInfo().template_name = templateListBean.getTemplate_name();
                RecordManager.get().getProductEntity().getExtendInfo().bg_name = bgListBean.getBg_name();
                if (!TextUtils.isEmpty(configJsonBean.getBackground_music())) {
                    RecordManager.get().getProductEntity().getExtendInfo().bgm_path
                            = savepath + templateListBean.getTemplate_name() + File.separator + configJsonBean.getBackground_music();
                }
            }
            if (isVoiceOk) {
                KLog.d(TAG, "combineVideoAudio: 111111");
                isVoiceOk = false;
                onCompleteListener = null;
                PublishMvActivity.startPublishActivity(ChooseStyle4QuickActivity.this, list, true);
            } else {
                ChooseStyle4QuickActivity.this.setOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void complete() {
                        KLog.d(TAG, "combineVideoAudio  complete: 2222222");
                        isVoiceOk = false;
                        onCompleteListener = null;
                        PublishMvActivity.startPublishActivity(ChooseStyle4QuickActivity.this, list, true);
                    }
                });
            }
        }

    }


    public void setShowAnimation(View view, int duration) {
        if (null == view || duration < 0) {
            return;
        }
        if (null != mShowAnimation) {
            mShowAnimation.cancel();
        }
        mShowAnimation = new AlphaAnimation(0.0f, 1.0f);
        mShowAnimation.setDuration(duration);
        mShowAnimation.setFillAfter(true);
        view.startAnimation(mShowAnimation);
        handler.removeMessages(2);
        handler.sendEmptyMessageDelayed(2, 3000);
    }

    public void setHideAnimation(View view, int duration) {

        if (null == view || duration < 0) {
            return;
        }

        if (null != mHideAnimation) {
            mHideAnimation.cancel();
        }
        // 监听动画结束的操作
        mHideAnimation = new AlphaAnimation(1.0f, 0.0f);
        mHideAnimation.setDuration(duration);
        mHideAnimation.setFillAfter(true);
        view.startAnimation(mHideAnimation);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        isVoiceOk = false;
        onCompleteListener = null;
        generatePreview(1);
    }

    @Override
    protected void onPause() {
        isPause = true;
        pausePlay();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        needReload = false;
        if (mAudioPlayer != null) {
            mAudioPlayer.release();
            mAudioPlayer = null;
        }

        if (playerEngine != null) {
            playerEngine.reset();
            playerEngine.onDestroy();
            playerEngine = null;
        }


        if (voiceBeatingTool != null) {
            voiceBeatingTool.stopThread();
            voiceBeatingTool = null;
        }
        isVoiceOk = false;
        onCompleteListener = null;
        DCBitmapManager.getInstance().clearBitmaps();
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * 显示失信息
     */
    public void showError(String s) {
        dismissLoad();
        if (TextUtils.isEmpty(s)) {
            ToastUtil.showToast("加载失败，请重试");
        } else {
            ToastUtil.showToast(s);
        }

    }

    public String getZipFilePath(String s) {
        return savepath + s.substring(s.lastIndexOf("/") + 1);
    }

    public class FileDownloadReceiver extends ResultReceiver {
        private WeakReference<ChooseStyle4QuickActivity> activity;

        @SuppressLint("RestrictedApi")
        public FileDownloadReceiver(ChooseStyle4QuickActivity activity, Handler handler) {
            super(handler);
            this.activity = new WeakReference<>(activity);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
//            String message = resultData.getString("message");
//            int percent = (int) resultData.getFloat("percent", 0f);
//            KLog.i("TESTTAG", "downloadMaterial: percent==" + percent + " resultCode " + resultCode + message + downloadBean);
            switch (resultCode) {
                case FileDownload.RESULT_PREPARE:
                    break;
                case FileDownload.RESULT_DOWNLOADING:
                    break;
                case FileDownload.RESULT_ERROR:
                    if (activity != null) {
                        activity.get().showError("");
                    }
                    break;
                case FileDownload.RESULT_COMPLETE:
                    KLog.d(TAG, "onReceiveResult: 单个下载任务完成");
//                    if (activity != null) {
//                        DownloadBean downloadBean = resultData.getParcelable("downloadBean");
//                        activity.get().generatePreview(downloadBean.index);
//                    }
                    break;
                case FileDownload.RESULT_COMPLETE_ALL:
                    KLog.d(TAG, "onReceiveResult: 多个下载任务全部完成");
                    if (activity != null) {
                        DownloadBean downloadBean = resultData.getParcelable("downloadBean");
                        activity.get().onDownloadComplete(downloadBean.type, downloadBean.index);
                    }
                    break;
            }
        }
    }

    private void saveAndBack() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        DCScriptManager.scriptManager().clearScripts();
    }

    @Override
    public void onBackPressed() {
        saveAndBack();
        super.onBackPressed();
    }
}
