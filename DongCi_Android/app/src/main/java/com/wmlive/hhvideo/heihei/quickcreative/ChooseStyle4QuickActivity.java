package com.wmlive.hhvideo.heihei.quickcreative;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
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
import com.dongci.sun.gpuimglibrary.player.script.DCScriptManager;
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

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

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
    private double duration;
    private TextureView textureView;
    private ConfigJsonBean configJsonBean;
    private AlphaAnimation mShowAnimation;//渐隐动画
    private AlphaAnimation mHideAnimation;//渐隐动画

//    private  boolean bSeekFlag =  false;
//    private long  lastSeekTime = 0;
//
//    private  long  lastCurrentTime = 0;

    private VoiceBeatingAudioPlayer mAudioPlayer = null;

    private DCVoiceBeatingTool voiceBeatingTool = new DCVoiceBeatingTool();
    private boolean isVoiceOk;


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
        initPlayer();
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
                mediaObject.setCropRect(new RectF(0, 0, 720, 960));
                mediaObject.setRectInVideo(new RectF(0, 0.5f * (1280.0f - 960.0f) / 1280.0f, 1.0f, 1.0f - 0.5f * (1280.0f - 960.0f) / 1280.0f));
                mediaObject.setTimeRange(new DCAsset.TimeRange(0L, VideoUtils.getVideoLength(videoEntity.editingVideoPath)));
                mediaObject.setStartTimeInScene(0L);
                mediaObject.setImagePaths(videoEntity.extendInfo.videoImgs);
                mediaObject.setFrameInterval((long) (1000000 * 1.0f / 12.0f));
                KLog.i("setFrameInterval--素材->" + (long) (1000000 * 1.0f / 12.0f));
                list.add(mediaObject);
            }

        }

        MediaObject mediaObject = new MAsset();
        mediaObject.assetId = list.size() + 1;
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
//        if (playerEngine != null) {
//            play();
//        }
    }

    private void getList() {
        loading(false, getString(R.string.hecheng));
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
        generatePreview(1);
    }

    private void initPlayer() {
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
                playerEngine.setAutoRepeat(true);
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
                        KLog.d(TAG, "onPlayerCompletion: ");
                    }

                    @Override
                    public void onGetCurrentPosition(float var1) {
                        KLog.d(TAG, "onGetCurrentPosition: ");
                    }
                });
            }
        });
        playerEngine.setAspectRatioFitMode(MediaObject.FillTypeScaleToFit);
        playerEngine.setPreviewAspectRatio(RecordManager.get().getSetting().getVideoRatio());


        mAudioPlayer = new VoiceBeatingAudioPlayer();

        mAudioPlayer.prepare();
        mAudioPlayer.setAutoRepeat(true);

        mAudioPlayer.setAudioPlayerListener(new VoiceBeatingAudioPlayer.AudioPlayerListener() {
            @Override
            public void OnPlayingPosition(long time) {
                Log.e("PlayerEngine audio ", "now audiotime is 1   " + time);

                long currenttime = playerEngine.getCurrentPosition();
                if (abs(currenttime / 1000 - time) > 200) {
                    float t = ((float) time) / 1000;
                    //playerEngine.getDcPlayerManager().seekTo(t);

                    mAudioPlayer.seek(currenttime / 1000 + 100);
                    Log.e("seekPosition", "seek to 1 " + t + " diff " + (currenttime / 1000 - time));

                }


                Log.e("PlayerEngine video ", "now videotime is 1   " + playerEngine.getCurrentPosition());
            }
        });

    }

    public int getHeight() {
        return rl_video_container.getHeight();
    }

    private void pausePlay() {
        if (playerEngine != null) {
            playerEngine.setAutoRepeat(false);
            playerEngine.pause();
        }
        if (mAudioPlayer != null)
            mAudioPlayer.pause();
        iv_play_icon.setVisibility(View.VISIBLE);
//        seekbar.setPlayStatus(true, SEEKBAR);
    }

    private void startPlay() {
        if (playerEngine != null) {
            playerEngine.setAutoRepeat(true);
            playerEngine.start();
        }
        if (mAudioPlayer != null)
            mAudioPlayer.play();
        iv_play_icon.setVisibility(View.GONE);
//        seekbar.setPlayStatus(false, SEEKBAR);
    }

    @Override
    protected void onSingleClick(View v) {

        if (v == tv_back) {
            onBack();
        } else if (v == tv_next) {

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
        KLog.d("itemClick:   position===" + position + "  imagePath==" + imagePath);
        iv_play_icon.setVisibility(View.GONE);
        loading(false, getString(R.string.hecheng));
        initPlayer();
        if (type == TYPE_BG) {
            bgListBean = bg_list.get(position);
            if (bgListBean != null) {
                int downloadId = FileDownloadUtils.generateId(bgListBean.getBg_resource(), savepath);
                DownloadBean downloadBean = new DownloadBean(downloadId, bgListBean.getBg_resource(),
                        savepath, "", "", 100);
                FileDownload.start(ChooseStyle4QuickActivity.this, downloadBean, fileDownloadReceiver, false, true);
            }
        } else if (type == TYPE_MUSIC) {
            templateListBean = template_list.get(position);
            if (templateListBean != null) {
                int downloadId = FileDownloadUtils.generateId(templateListBean.getZip_path(), savepath);
                DownloadBean downloadBean = new DownloadBean(downloadId, templateListBean.getZip_path(),
                        savepath, "", "", 200);
                FileDownload.start(ChooseStyle4QuickActivity.this, downloadBean, fileDownloadReceiver, false, true);
            }
        }
    }

    private String getBgVideo(String inputVideo) {
        String outPath = RecordFileUtil.createTimestampFile(RecordManager.get().getProductEntity().baseDir,
                RecordManager.PREFIX_CREATIVE_VIDEO_FILE,
                RecordManager.SUFFIX_VIDEO_FILE, true);

        //duration = VideoUtils.getAudioLength(RecordManager.get().getProductEntity().combineAudio) * 1.0f / 1000000.0f;

        duration = voiceBeatingTool.getAudioDuration() * 1.0 / 1000;
        KLog.d(TAG, "generatePreview: inputVideo==" + inputVideo);
        KLog.d(TAG, "generatePreview: audio-lenght==" + duration);
        try {
            SLVideoProcessor.getInstance().generateLoopVideo(inputVideo, duration, outPath);
            RecordManager.get().getProductEntity().combineVideo = outPath;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outPath;
    }

    /**
     * 生成创意视频预览
     */
    public void generatePreview(int type) {
        //需要的素材 1、zip包  2、背景图片  3、短视频加音频
        new Thread() {
            @Override
            public void run() {
                super.run();
                templateChange();
            }
        }.start();
    }

    private void bgVideoChange() {
        String imagePath = getZipFilePath(bgListBean.getBg_resource());//下载的bg资源包路径
        CreativeQuickUtils.doUnzip(imagePath, bgListBean.getBg_name());
        String combineAudio = RecordManager.get().getProductEntity().combineAudio;
        listbgVideo.clear();
        /**
         * 添加zip包中背景视频
         */
        String bg_path = savepath + bgListBean.getBg_name();
        String bgfileString = FileUtil.getFileString(bg_path + "/backgroundConfig.json");
        BgConfigJsonBean bgConfigJsonBean = JsonUtils.parseObject(bgfileString, BgConfigJsonBean.class);
        if (bgConfigJsonBean == null) {
            ToastUtil.showToast("背景获取失败");
            return;
        }

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
                    KLog.i("path-->" + imgPath);
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
                KLog.i("setFrameInterval" + ((long) (1000000 * 1.0f / (decorationsBean.getFrameRate() * 1.0f))));
                KLog.i("setDecorationMaskPath--》" + (bg_path + File.separator + decorationsBean.getMask()));
                listbgVideo.add(mediaObject);
            }
        }

    }

    private void templateChange() {
        String zipFilePath = getZipFilePath(templateListBean.getZip_path());//下载的zip文件路径
        CreativeQuickUtils.doUnzip(zipFilePath, templateListBean.getTemplate_name());
        String path = savepath + templateListBean.getTemplate_name();
        String fileString = FileUtil.getFileString(path + File.separator + "config.json");
        configJsonBean = JSON.parseObject(fileString, ConfigJsonBean.class);
        if (configJsonBean == null) {
            return;
        }
        int size = configJsonBean.getItems().size();
        if (size > 0 && size < 6) {
            shortVideoList = shortVideoList.subList(0, size - 1);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_musicname.setText(configJsonBean.getBackground_music());
                currentBgName = configJsonBean.getBackground_music();
                setShowAnimation(tv_musicname, 1000);
            }
        });

        KLog.d("dataReadyCallback", "stop thread begin");
        //切换模板停止上次的处理线程
        voiceBeatingTool.stopThread();

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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerEngine.setMediaAndPrepare(listPlay);
                playerEngine.start();

                mAudioPlayer.setVoiceBeatingTool(voiceBeatingTool);
                mAudioPlayer.play();

                KLog.d(TAG, "generatePreview: play");
                dismissLoad();
                iv_play_icon.setVisibility(View.GONE);
//                ToastUtil.showToast(currentBgName);
                setShowAnimation(tv_musicname, 3000);
//                handler.sendEmptyMessage(1);
            }
        });
    }

    public void combineVideoAudio() {
        List<MediaObject> list = new ArrayList<>();
        list.addAll(listbgVideo);
        list.addAll(this.list);
        if (listbgVideo.size() > 0) {
            Scene scene = new MScene();
            scene.assets = list;
            List<Scene> scenes = new ArrayList<Scene>(1);
            scenes.add(scene);
            MVideoConfig mVideoConfig = new MVideoConfig();
            loading(false, getString(R.string.hecheng));
            exportMvVideo(scenes, mVideoConfig, new ExportListener() {
                @Override
                public void onExportStart() {
                    KLog.d("exportCombineVideo", "onExportStart: ");
                }

                @Override
                public void onExporting(int progress, int max) {
                    KLog.d("exportCombineVideo", "onExporting: progress==" + progress);
                }

                @Override
                public void onExportEnd(int var1, String path) {
                    dismissLoad();
                    KLog.d("exportCombineVideo", "onExportEnd: path==" + path);
                    String coverPath = RecordManager.get().getProductEntity().baseDir + File.separator + RecordManager.PREFIX_COVER_FILE + RecordFileUtil.getTimestampString() + ".jpg";
                    if (configJsonBean != null) {
                        long l = 100000;
                        if (configJsonBean.getThumbnail_generate_time() != null) {

                            l = (long) Double.parseDouble(configJsonBean.getThumbnail_generate_time()) * 1000000;
                        }
                        RecordFileUtil.getVideoCover(path, coverPath, l, VIDEO_EXPORT_WIDTH, VIDEO_EXPORT_HEIGHT);
                    }
                    KLog.d(TAG, "onExportEnd: coverPath==" + coverPath);
                    RecordManager.get().getProductEntity().coverPath = coverPath;
                    RecordManager.get().getProductEntity().combineVideo = path;
                    if (templateListBean != null && bgListBean != null) {
                        RecordManager.get().getProductEntity().getExtendInfo().template_name = templateListBean.getTemplate_name();
                        RecordManager.get().getProductEntity().getExtendInfo().bg_name = bgListBean.getBg_name();
                    }

                    if (isVoiceOk) {
                        PublishMvActivity.startPublishActivity(ChooseStyle4QuickActivity.this, true);
                    } else {
                        ChooseStyle4QuickActivity.this.setOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void complete() {
                                PublishMvActivity.startPublishActivity(ChooseStyle4QuickActivity.this, true);
                            }
                        });
                    }
                }
            });
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
        play();
//        generatePreview(1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pausePlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (playerEngine != null) {
            playerEngine.reset();
            playerEngine.onDestroy();
            playerEngine = null;
        }
        if (mAudioPlayer != null) {
            mAudioPlayer.release();
        }

        if (voiceBeatingTool != null) {
            voiceBeatingTool.stopThread();
            voiceBeatingTool.destory();
            voiceBeatingTool = null;
        }
        DCScriptManager.scriptManager().clearScripts();
        handler.removeCallbacksAndMessages(null);
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
                    break;
                case FileDownload.RESULT_COMPLETE:
                    if (activity != null) {
                        DownloadBean downloadBean = resultData.getParcelable("downloadBean");
                        activity.get().generatePreview(downloadBean.index);
                    }
                    break;
            }
        }
    }

    private void saveAndBack() {
        if (templateListBean != null && bgListBean != null) {
            RecordManager.get().getProductEntity().extendInfo.template_name = templateListBean.getTemplate_name();
            RecordManager.get().getProductEntity().extendInfo.bg_name = bgListBean.getBg_name();
            RecordManager.get().updateProduct();
        }
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
    }

    @Override
    public void onBackPressed() {
        saveAndBack();
        super.onBackPressed();
    }
}
