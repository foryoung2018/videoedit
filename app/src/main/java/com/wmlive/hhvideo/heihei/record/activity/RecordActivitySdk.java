package com.wmlive.hhvideo.heihei.record.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.os.ResultReceiver;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dongci.sun.gpuimglibrary.api.DCCameraConfig;
import com.dongci.sun.gpuimglibrary.coder.TextureMovieEncoder;

import com.dongci.sun.gpuimglibrary.player.script.DCScriptManager;
import com.example.loopback.DCLoopbackTool;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.BaseCompatActivity;
import com.wmlive.hhvideo.common.base.BaseModel;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.common.base.IBasePresenter;
import com.wmlive.hhvideo.common.manager.MyAppActivityManager;
import com.wmlive.hhvideo.heihei.beans.frame.FrameInfo;
import com.wmlive.hhvideo.heihei.beans.opus.OpusMaterialEntity;
import com.wmlive.hhvideo.heihei.beans.opus.UploadMaterialEntity;
import com.wmlive.hhvideo.heihei.beans.record.ClipVideoEntity;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.discovery.DiscoveryUtil;
import com.wmlive.hhvideo.heihei.discovery.activity.SearchVideoActivity;
import com.wmlive.hhvideo.heihei.mainhome.activity.MainActivity;
import com.wmlive.hhvideo.heihei.mainhome.util.PublishUtils;
import com.wmlive.hhvideo.heihei.personal.activity.DraftBoxActivity;
import com.wmlive.hhvideo.heihei.record.adapter.CountDownAdapter;
import com.wmlive.hhvideo.heihei.record.adapter.FilterMirrorAdapter;
import com.wmlive.hhvideo.heihei.record.engine.DCRecorderHelper;
import com.wmlive.hhvideo.heihei.record.engine.PlayerEngine;
import com.wmlive.hhvideo.heihei.record.engine.constant.SdkConstant;
import com.wmlive.hhvideo.heihei.record.engine.listener.VideoListener;
import com.wmlive.hhvideo.heihei.record.engine.utils.VideoUtils;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.manager.RecordSetting;
import com.wmlive.hhvideo.heihei.record.manager.RecordSpeed;
import com.wmlive.hhvideo.heihei.record.presenter.RecordPresenter;
import com.wmlive.hhvideo.heihei.record.service.RecordActivitySdkViewImpl;
import com.wmlive.hhvideo.heihei.record.uird.ResultConstants;
import com.wmlive.hhvideo.heihei.record.utils.RecordFileUtil;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtil;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtilSdk;
import com.wmlive.hhvideo.heihei.record.utils.TimerUtil;
import com.wmlive.hhvideo.heihei.record.widget.CircleProgressDialog;
import com.wmlive.hhvideo.heihei.record.widget.CountdownView;
import com.wmlive.hhvideo.heihei.record.widget.CustomFrameView;
import com.wmlive.hhvideo.heihei.record.widget.ExtBtnRecord;
import com.wmlive.hhvideo.heihei.record.widget.ExtRadioGroup;
import com.wmlive.hhvideo.heihei.record.widget.FullRecordView;
import com.wmlive.hhvideo.heihei.record.widget.LiveCameraZoomHandler;
import com.wmlive.hhvideo.heihei.record.widget.LocateCenterHorizontalView;
import com.wmlive.hhvideo.heihei.record.widget.RecordMenuView;
import com.wmlive.hhvideo.heihei.record.widget.RecordOptionPanel;
import com.wmlive.hhvideo.heihei.record.widget.SmallRecordView;
import com.wmlive.hhvideo.heihei.record.widget.SysAlertDialog;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.FileUtil;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ParamUtis;
import com.wmlive.hhvideo.utils.ScreenUtil;
import com.wmlive.hhvideo.utils.StringUtils;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.utils.download.DownloadBean;
import com.wmlive.hhvideo.utils.download.FileDownload;
import com.wmlive.hhvideo.widget.CustomFontTextView;
import com.wmlive.hhvideo.widget.dialog.HeadsetDialog;
import com.wmlive.hhvideo.widget.dialog.PermissionDialog;
import com.wmlive.networklib.entity.EventEntity;
import com.wmlive.networklib.util.EventHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;
import io.reactivex.functions.Consumer;

import static com.wmlive.hhvideo.heihei.record.manager.RecordManager.PREFIX_COMBINE_FILE;
import static com.wmlive.hhvideo.heihei.record.manager.RecordManager.SUFFIX_AUDIO_FILE;
import static com.wmlive.hhvideo.heihei.record.manager.RecordSetting.FILTER_LIST;


/**
 * 视频录制的界面
 * 更换新的 播放sdk
 */
public class RecordActivitySdk extends DcBaseActivity implements
        RecordPresenter.IRecordView,
        CountdownView.OnCountdownEndListener,
        ExtRadioGroup.IGroupListener, LocateCenterHorizontalView.OnSelectedPositionChangedListener, FilterMirrorAdapter.OnFilterClickListener,
        CountDownAdapter.OnRecyclerViewItemClickListener, DCLoopbackTool.DCLoopbackTestInterface {

    @BindView(R.id.recordOptionPanel)
    public RecordOptionPanel recordOptionPanel;
    @BindView(R.id.videoViewsdk)
    public TextureView videoViewSdk;
    @BindView(R.id.sdkview_framelayout)
    public FrameLayout videoFrameLayout;
    @BindView(R.id.llSpeedPanel)
    public LinearLayout llSpeedPanel;
    @BindView(R.id.extSpeedPanel)
    public ExtRadioGroup extSpeedPanel;
    @BindView(R.id.filterIndicator)
    public TextView filterIndicator;
    @BindView(R.id.recordFilterSelector)
    public LocateCenterHorizontalView recordFilterSelector;
    @BindView(R.id.filter_layout)
    public RelativeLayout filterLayout;
    @BindView(R.id.countdownView)
    public CountdownView countdownView;
    @BindView(R.id.customFrameView)
    public CustomFrameView customFrameView;
    @BindView(R.id.btRecorder)
    public ExtBtnRecord btRecorder;
    @BindView(R.id.rlRoot)
    public RelativeLayout rlRoot;
    @BindView(R.id.flFullRecord)
    public FullRecordView flFullRecord;
    @BindView(R.id.fr_container)
    public FrameLayout fr_container;
    @BindView(R.id.rlPreview)
    public RelativeLayout rlPreview;
    @BindView(R.id.cutdown)
    public View cutdown;
    @BindView(R.id.count_down_rv)
    public RecyclerView countDownRv;
    @BindView(R.id.dcloopbackTestView)
    public FrameLayout dcloopbackTestView;

    public static final String EXTRA_RECORD_TYPE = "recordType";
    public static final String EXTRA_OPUS_ID = "extra_opus_id";
    public static final String EXTRA_FRAME_LAYOUT = "extra_frame_layout";
    public static final String EXTRA_REPLACE_POSITION = "extra_replace_position";
    public static final String EXTRA_RECORD_BACK = "recordBackType";


    public static final int TYPE_NORMAL = 10;//正常录制
    public static final int TYPE_DRAFT = 20;//草稿
    public static final int TYPE_TOGETHER = 30;//共同创作
    public static final int TYPE_IMPORT = 40;//本地上传
    public static final int REQUEST_EDIT_VIDEO = 50;
    public static final int TYPE_PBLISH = 60;//预览页面
    public static final int TV_NEXT_ID = 1001014;

    private LiveCameraZoomHandler mCameraZoomHlr;
    private CircleProgressDialog dialog;
    //    private VirtualVideo virtualVideo;
    public RecordMenuView recordMenu;

    public int recordType = TYPE_NORMAL;
    public List<SmallRecordView> smallRecordViewList;
    // 改为-1，需要做校验
    private int currentPreviewIndex = -1;
    public TextView tvNext;
    private boolean hasShowCropMusic; // 是否显示音乐裁剪
    private boolean needRefresh = true;
    private FrameInfo mFrameInfo;
    private long opusId;
    private String frameLayout;
    private ArrayList<Integer> downloadStateList;
    public static RecordActivitySdk mContext;
    private VideoReceiver resultReceiver;
    private Handler handler;
    private List<UploadMaterialEntity> materials;
    private int replacePosition;
    private boolean hasReplaceMaterial = false;
    private boolean isFirstEnter = true;
    private int recordBackType = 0;
    private long delayTime;
    private int videoQuality = FrameInfo.VIDEO_QUALITY_LOW;
    private FilterMirrorAdapter fiterSelectAdapter;
    private CountDownAdapter countDownAdapter;
    private HeadSetReceiver mReceiver;
    public PlayerEngine playerEngine;
    public DCRecorderHelper dcRecorderHelper;
    public RecordActivitySdkVideoHelper recordActivitySdkVideoHelper;
    public RecordActivitySdkViewImpl recordActivitySdkView;
    TimerUtil timerUtil;

    /**
     * 合并模式，每次视频录制完成后都合并成成一个视频
     */
    public boolean comBineMode = true;

    private List<ShortVideoEntity> shortVideoList;
    private int countDownNum = 3;//倒计时时间长度

    private boolean doRecordEnd = false;

    public static void startRecordActivity(final BaseCompatActivity ctx, final int recordType) {
        RecordManager.get().initSetting();
        startRecordActivity(ctx, recordType, 0, null, 0, TYPE_NORMAL);
    }

    /**
     * 主要处理发布页面返回操作
     */
    public static void startRecordActivity(final BaseCompatActivity ctx, final int recordType, final int recordBackType) {
        RecordManager.get().initSetting();
        startRecordActivity(ctx, recordType, 0, null, 0, recordBackType);
    }

    public static void startRecordActivity(final BaseCompatActivity ctx, final int recordType, final long opusId, final String frameLayout, final int replacePosition, final int recordBackType) {
        if (PublishUtils.showToast()) {
            return;
        }
        final BaseModel count = new BaseModel();
        new RxPermissions(ctx).requestEach(RecordSetting.RECORD_PERMISSIONS)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        KLog.i("====请求权限：" + permission.toString());
                        if (!permission.granted) {
                            if (Manifest.permission.CAMERA.equals(permission.name)) {
                                new PermissionDialog(ctx, 20).show();
                            } else if (Manifest.permission.RECORD_AUDIO.equals(permission.name)) {
                                new PermissionDialog(ctx, 10).show();
                            }
                        } else {
                            count.type++;
                        }
                        if (count.type == 3) {
                            KLog.i("=====获取权限：成功");
                            int result = -1;//-1表示权限获取失败，-2表示相机初始化失败，0表示权限和相机都成功
                            result = RecordManager.get().initRecordCore(ctx) ? 0 : -2;
                            if (result == 0) {
                                Intent intent = new Intent(ctx, RecordActivitySdk.class);
                                intent.putExtra(EXTRA_RECORD_TYPE, recordType);
                                intent.putExtra(EXTRA_OPUS_ID, opusId);
                                intent.putExtra(EXTRA_FRAME_LAYOUT, frameLayout);
                                intent.putExtra(EXTRA_REPLACE_POSITION, replacePosition);
                                intent.putExtra(EXTRA_RECORD_BACK, recordBackType);
                                ctx.startActivity(intent);
                            } else if (result == -1) {
                                ToastUtil.showToast("请在系统设置中允许App运行必要的权限");
                            } else {
                                KLog.i("=====初始化相机失败");
                                ToastUtil.showToast("初始化相机失败");
                                new PermissionDialog(ctx, 20).show();
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.getMessage();
                        KLog.i("=====初始化相机失败:" + throwable.getMessage());
                        ToastUtil.showToast("初始化相机失败");
//                        如果选择画框页面 存在。finish
                    }
                });
    }

    @Override
    protected void onSingleClick(View v) {
        switch (v.getId()) {
            case TV_NEXT_ID:
                KLog.d("onSingleClick: TV_NEXT_ID");
                recordActivitySdkVideoHelper.doNext();
                break;
            default:
                break;
        }

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_record_sdk;
    }

    @Override
    protected void initData() {
        super.initData();
        DCScriptManager.scriptManager().clearScripts();//防止没有清除脚本，
        mContext = RecordActivitySdk.this;
        recordType = getIntent().getIntExtra(EXTRA_RECORD_TYPE, TYPE_NORMAL);
        opusId = getIntent().getLongExtra(EXTRA_OPUS_ID, 0L);
        frameLayout = getIntent().getStringExtra(EXTRA_FRAME_LAYOUT);
        replacePosition = getIntent().getIntExtra(EXTRA_REPLACE_POSITION, 0);
        recordBackType = getIntent().getIntExtra(EXTRA_RECORD_BACK, 0);
        cutdown.setOnClickListener(this);
        ViewGroup.LayoutParams layoutParams = fr_container.getLayoutParams();
        layoutParams.height = ScreenUtil.getHeight(this) - ScreenUtil.dip2px(this, 160);
        layoutParams.width = -1;
        fr_container.setLayoutParams(layoutParams);
        recordActivitySdkVideoHelper = new RecordActivitySdkVideoHelper(this);
//        checkDCLoopbackTest(true);
        registerHeadset();

        if (!TextUtils.isEmpty(frameLayout)) {
            getPresenter().getFrameList(frameLayout);
//            recordActivitySdkVideoHelper.showDialog();
        } else {
            init();
        }

    }

    private void registerHeadset() {
        mReceiver = new HeadSetReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mReceiver, intentFilter);
    }

    private void checkDCLoopbackTest(boolean isFirst) {
        if (!DCLoopbackTool.isDCLoopbackTestOK || !isFirst && !dcRecorderHelper.isRecording()) {
            dcloopbackTestView.setVisibility(View.VISIBLE);
            DCLoopbackTool.onStart(getApplicationContext());
        }
    }

    public void init() {
        if (RecordManager.get().isFrameInfoValid()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            EventHelper.register(this);
            recordOptionPanel.setOptionClickListener(recordOptionClickListener);
            recordOptionPanel.getProgressBar().setInterval((int) RecordManager.get().getSetting().minVideoDuration,
                    (int) RecordManager.get().getSetting().maxVideoDuration);
            recordOptionPanel.setCountDown(1);//默认3秒
            countdownView.setCountdownEndListener(this);
            recordFilterSelector.setHasFixedSize(true);
            fiterSelectAdapter = new FilterMirrorAdapter(FILTER_LIST, 1000);
            fiterSelectAdapter.setOnFilterClickListener(this);
            recordFilterSelector.setLayoutManager(new LinearLayoutManager(RecordActivitySdk.this, LinearLayoutManager.HORIZONTAL, false));
            recordFilterSelector.setAdapter(fiterSelectAdapter);
            recordFilterSelector.setOnSelectedPositionChangedListener(this);

            //倒计时选择列表
            countDownAdapter = new CountDownAdapter(this,1);
            countDownRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            countDownRv.setAdapter(countDownAdapter);
            countDownAdapter.setOnItemClickListener(this);

            extSpeedPanel.addMenu(2, RecordSetting.SPEED_TITLE);
            extSpeedPanel.setIListener(this);
            btRecorder.setTranistion(rlRoot);
            btRecorder.enableTouchScroll(true);
            btRecorder.setLongListener(recordLongListener);
            RecordFileUtil.prepareDirIndex(currentPreviewIndex, true);
            initRecordItemView(false);
            initToolbarMenu();
            initRecorder();
            recordActivitySdkView = new RecordActivitySdkViewImpl();
            recordActivitySdkView.init(this);
//            initPlayer();
            timerUtil = new TimerUtil();

            switch (recordType) {
                case TYPE_NORMAL:
                case TYPE_DRAFT:
                case TYPE_IMPORT:
                case TYPE_TOGETHER:
                    if (recordType == TYPE_TOGETHER && opusId > 0) {
                        recordOptionPanel.setFrameEnable(false);
                    }
                    updateRecordItemView();
                    break;
                default:
                    break;
            }
            cutdown.setVisibility(View.GONE);
            KLog.i(recordType + "====正常进入" + frameLayout);
            if (recordType == TYPE_TOGETHER && TextUtils.isEmpty(frameLayout)) {//共同创作
                cutdown.setVisibility(View.VISIBLE);
                List<ShortVideoEntity> shortVideoList = RecordManager.get().getProductEntity().shortVideoList;
                Log.d("shortVideoList", "init:  共同创作  shortVideoList=====" + shortVideoList);
                recordActivitySdkVideoHelper.transformAudio2to1(shortVideoList);//转换声道

            } else if (recordType == TYPE_TOGETHER) {//替换 不做操作
                checkDCLoopbackTest(true);
//                DCLatencytestTool.startLatency(this);//校准操做
                recordActivitySdkVideoHelper.showDialog();
                //设置不让惦记
            } else if (recordType == TYPE_DRAFT) {
                //判断草稿的类型
                handleDraft();
            } else {
                //执行加载数据，视频合成中。。
                recordActivitySdkVideoHelper.combineData();
                KLog.d("init: combineData()");
            }

            recordActivitySdkView.resetMenu(null);
            if (!TextUtils.isEmpty(frameLayout)) {//共同创作的替换进来
                currentPreviewIndex = replacePosition;
                if (currentPreviewIndex == 0 && materials.size() == 1) {
                    selectSmallCamera(currentPreviewIndex, true);
                } else {
                    selectSmallCamera(currentPreviewIndex, false);
                }
                KLog.d("!TextUtils.isEmpty(frameLayout)==11111111111111 materials.size()==" + materials.size());
            } else {
                List<ShortVideoEntity> videoList = RecordManager.get().getProductEntity().shortVideoList;
                if (videoList != null) {
                    for (int i = 0, size = videoList.size(); i < size; i++) {
                        ShortVideoEntity videoEntity = videoList.get(i);
                        if (videoEntity != null && !videoEntity.hasVideo()) {
                            currentPreviewIndex = i;
                            selectSmallCamera(currentPreviewIndex);
                            break;
                        }
                    }
                    if(currentPreviewIndex==-1 && videoList.size()==1){//只有一个视频的时候，直接开始预览
                        currentPreviewIndex  = 0;
                        selectSmallCamera(currentPreviewIndex);
                    }
                }
            }

        } else {
            toastFinish();
        }
        KLog.d("currentPreviewIndex==init" + currentPreviewIndex);
    }

    private void handleDraft() {
        //从草稿箱进来后，进入校准页面
//        DCLatencytestTool.startLatency(this);
        checkDCLoopbackTest(false);
        List<ShortVideoEntity> shortVideoList = RecordManager.get().getProductEntity().shortVideoList;
        int index_clip = -1;
        int draftType = -1;
        for (int i = 0; i < shortVideoList.size(); i++) {
            ShortVideoEntity videoEntity = shortVideoList.get(i);
            if (videoEntity.getClipList().size() > 0 && TextUtils.isEmpty(videoEntity.editingVideoPath)) {//如果存在录制片段并且没有合成 1.旧版草稿 没有音轨 2.新版草稿有音轨
                List<ClipVideoEntity> clipList = videoEntity.getClipList();
                if (TextUtils.isEmpty(clipList.get(0).audioPath)) {//没有音轨 先去合成
                    draftType = 10;
                } else {//有音轨的情况  获取index=pre
                    draftType = 20;
                }
                index_clip = i;
                break;
            }
        }
        if (draftType == 10) {//对应3.3.6版草稿箱 有视频片段的情况
            ShortVideoEntity videoEntity = shortVideoList.get(index_clip);
            String outPath = RecordFileUtil.createTimestampFile(videoEntity.baseDir,
                    PREFIX_COMBINE_FILE, SUFFIX_AUDIO_FILE, true);
            RecordUtilSdk.composeAudios(videoEntity, outPath, new VideoListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onProgress(int progress) {

                }

                @Override
                public void onFinish(int code, String outpath) {
                    videoEntity.editingVideoPath = outPath;
                    recordActivitySdkVideoHelper.transformAudio2to1(shortVideoList);//转换声道

                }

                @Override
                public void onError() {

                }
            });
        } else if (draftType == 20) {//3.3.7版本草稿箱
            resetPreItem(index_clip, true, true, false, false);
        } else {
            recordActivitySdkVideoHelper.transformAudio2to1(shortVideoList);
        }
    }

    private void initToolbarMenu() {
        setTitle("", true);
        tvNext = new TextView(this);
        tvNext.setId(TV_NEXT_ID);
        tvNext.setText("下一步");
        tvNext.setTextSize(16);
        TypedValue tv = new TypedValue();
        tvNext.setBackgroundResource(tv.resourceId);
        tvNext.setGravity(Gravity.CENTER);
        tvNext.setPadding(10, 6, DeviceUtils.dip2px(RecordActivitySdk.this, 15), 6);
        setBlackToolbar();
        setToolbarRightView(tvNext, this);

        recordMenu = new RecordMenuView(this);
        recordMenu.setMenuClickListener(onMenuClickListener);
        setToolbarCenterView(recordMenu, null);
    }


    public void initRecordItemView(boolean initDir) {
        if (smallRecordViewList == null) {
            smallRecordViewList = new ArrayList<>();
            mFrameInfo = RecordManager.get().getFrameInfo();
            KLog.d("initRecordItemView-onGetFrameInfo-->" + mFrameInfo);
            String frameImagePath = RecordFileUtil.getFrameImagePath(mFrameInfo.sep_image);
            KLog.d("ggqBACK", "frameImagePath==" + frameImagePath);
            Bitmap bitmap = BitmapFactory.decodeFile(frameImagePath);
            rlPreview.setBackground(new BitmapDrawable(bitmap));
            KLog.d("DCPlayerManager-->initPlayer--init_>" + mFrameInfo);
            if (mFrameInfo != null && mFrameInfo.getLayout() != null) {
                videoQuality = mFrameInfo.video_quality;
                int size = mFrameInfo.getLayout().size();
                ParamUtis.setLayoutParam(this, customFrameView, mFrameInfo.canvas_height, 160);
                ParamUtis.setLayoutParam(this, rlPreview, mFrameInfo.canvas_height, 160);
                ParamUtis.setLayoutParam(this, flFullRecord, mFrameInfo.canvas_height, 160);//修改对应全屏容器宽高
                SmallRecordView smallRecordView;
                for (int i = 0; i < size; i++) {//更新画框的相关信息
                    smallRecordView = new SmallRecordView(this);
                    smallRecordView.setLayoutInfo(mFrameInfo.getLayout().get(i));
                    smallRecordView.setTag(i);
                    if (TextUtils.isEmpty(frameLayout)) {
                        smallRecordView.showAdd();
                    }
                    smallRecordView.setRecorderClickListener(smallRecordClickListener);
                    if (size == 1 && i == 0) {
                        smallRecordView.setShowZoom(true);
                    }
                    if (RecordManager.get().getShortVideoEntity(i).editingVideoPath != null) {
                        smallRecordView.showDuring(RecordManager.get().getShortVideoEntity(i).getDuringString());
                    }
                    smallRecordViewList.add(smallRecordView);
                }
                //同步画框内容的相关信息，
                List<ShortVideoEntity> shortVideoList = RecordManager.get().getProductEntity().shortVideoList;
                for (int j = 0; j < shortVideoList.size(); j++) {//更新地址
                    if (shortVideoList.get(j).hasVideo()) {
                        smallRecordViewList.get(j).hideAdd();
                    }
                }

                if (customFrameView != null) {
                    customFrameView.setFrameView(mFrameInfo, smallRecordViewList);
                    if (initDir) {
                        RecordFileUtil.prepareDirIndex(currentPreviewIndex, true);
                    }
                } else {
                    toastFinish();
                }
            } else {
                toastFinish();
            }
        }
    }

    /**
     * shortVideolist
     * 更新recordview 显示
     */
    public void updateRecordItemView() {
        ShortVideoEntity videoEntity;
        for (int i = 0, n = RecordManager.get().getProductEntity().shortVideoList.size(); i < n; i++) {
            videoEntity = RecordManager.get().getProductEntity().shortVideoList.get(i);
            if (videoEntity != null) {
                KLog.i(i + "import-->" + videoEntity.isImport());
                videoEntity.setOriginalMixFactor(RecordSetting.MAX_VOLUME / 2);
                if (TextUtils.isEmpty(frameLayout)) {
                    smallRecordViewList.get(i).showStatus(
                            false, videoEntity.hasVideo(), videoEntity.isImport(), videoEntity.reachMin());
                    smallRecordViewList.get(i).showDuring(videoEntity.getDuringString());
                } else {
                    boolean hasDownload = false;
                    if (materials != null) {
                        UploadMaterialEntity material = findMaterial(i);
                        if (material != null && !TextUtils.isEmpty(material.material_video)) {
                            hasDownload = true;
                        }
                    }
                    smallRecordViewList.get(i).showStatus(
                            false, videoEntity.hasVideo(), videoEntity.isImport(), videoEntity.reachMin(), hasDownload);
                }
                smallRecordViewList.get(i).setFocus(false);
            }
        }
    }


    private void initRecorder() {
        if (dcRecorderHelper == null) {
            dcRecorderHelper = new DCRecorderHelper(this);
        }
        dcRecorderHelper.initRecorderConfig();
        dcRecorderHelper.initRecordView(recordMenu, flFullRecord, btRecorder);
    }

    //顶部控制美颜和赏光灯
    private RecordMenuView.OnMenuClickListener onMenuClickListener = new RecordMenuView.OnMenuClickListener() {

        @Override
        public void onBeautyClick() {
            KLog.i("get--Width--2>" + rlPreview.getWidth() + "Height:>" + rlPreview.getHeight());
            dcRecorderHelper.beautyClick();
        }

        @Override
        public void onFlashClick() {
            dcRecorderHelper.flashClick();
        }

        @Override
        public void onToggleClick() {
            dcRecorderHelper.switchClick();
        }
    };

    //底部控制速度和滤镜
    private RecordOptionPanel.OnOptionClickListener recordOptionClickListener = new RecordOptionPanel.OnOptionClickListener() {
        @Override
        public void onCountdownClick() {
            if (countDownRv.getVisibility() == View.VISIBLE) {
                countDownRv.setVisibility(View.INVISIBLE);
            } else {
                countDownRv.setVisibility(View.VISIBLE);
                filterLayout.setVisibility(View.INVISIBLE);
            }
        }

        /**
         * 更改为 选择画框按钮的回调
         */
        @Override
        public void onSpeedClick() {
            allowRecord = false;
            if (dcRecorderHelper.isFullRecord) {
                exitFullRecord();
            }
            if (currentPreviewIndex > -1 && currentPreviewIndex < smallRecordViewList.size()) {
                final ShortVideoEntity videoEntity = RecordManager.get().getShortVideoEntity(currentPreviewIndex);
                if (!videoEntity.needJoin() && !TextUtils.isEmpty(videoEntity.editingVideoPath)
                        && new File(videoEntity.editingVideoPath).exists()) {
                    EditVideoGroupActivity.startEditVideoGroupActivity(RecordActivitySdk.this, EditProductionActivity.REQUEST_PAGE_TYPE_SORT, currentPreviewIndex, recordType);
                } else {
                    if ((TextUtils.isEmpty(videoEntity.editingVideoPath)
                            || !(new File(videoEntity.editingVideoPath).exists())) && videoEntity.hasVideo()) {
//                        entersort = true;
                        resetPreItem(currentPreviewIndex, true, true, false, true, false, true);
                    } else {
                        EditVideoGroupActivity.startEditVideoGroupActivity(RecordActivitySdk.this, EditProductionActivity.REQUEST_PAGE_TYPE_SORT, currentPreviewIndex, recordType);
                    }
                }
            } else {
                EditVideoGroupActivity.startEditVideoGroupActivity(RecordActivitySdk.this, EditProductionActivity.REQUEST_PAGE_TYPE_SORT, currentPreviewIndex, recordType);
            }

        }

        @Override
        public void onRollbackClick() {
            if (!countdownView.isStarted()) {
                recordActivitySdkView.hidePanel();
                KLog.d("ggqTAG", "7777777" + RecordManager.get().getShortVideoEntity(currentPreviewIndex).getClipList());
                if (RecordManager.get().getProductEntity() != null) {
                    final ShortVideoEntity videoEntity = RecordManager.get().getShortVideoEntity(currentPreviewIndex);
                    KLog.d("ggqTAG", "88888888" + RecordManager.get().getShortVideoEntity(currentPreviewIndex).getClipList());
                    if (videoEntity != null && !videoEntity.isImport()) {
                        if (videoEntity.hasEffectAndFilter()) {
                            SysAlertDialog.showAlertDialog(RecordActivitySdk.this, R.string.release_back_press_alert, R.string.release_back_press_cancel,
                                    null, R.string.release_back_press_confirm, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            videoEntity.deleteEffect();
                                            deleteClipVideo(videoEntity);
                                        }
                                    });
                        } else {
                            SysAlertDialog.showAlertDialog(RecordActivitySdk.this, R.string.delete_recorder_item_alert, R.string.release_back_press_cancel,
                                    null, R.string.release_back_press_confirm, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            deleteClipVideo(videoEntity);
                                        }
                                    });
                        }
                    }
                } else {
                    toastFinish();
                }
            }
        }

        @Override
        public void onFilterClick() {
            if (!RecordManager.get().getShortVideoEntity(currentPreviewIndex).isImport()) {
                if (!countdownView.isStarted()) {
                    llSpeedPanel.setVisibility(View.INVISIBLE);
                    filterLayout.setVisibility(filterLayout.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
                    //滤镜选择器
                    if (filterLayout.getVisibility() == View.VISIBLE) {
                        countDownRv.setVisibility(View.INVISIBLE);
                        int position = RecordManager.get().getShortVideoEntity(currentPreviewIndex).getFilterId();
                        if (position == 0) {
                            recordFilterSelector.moveToPosition(FILTER_LIST.size() * 40);
                        } else {
                            recordFilterSelector.moveToPosition(position);
                        }
                    }

                }
            }
        }
    };


    /**
     * 倒计时
     */
    private void startCountdown(int count) {
        if (!countdownView.isStarted()) {
            countdownView.setVisibility(View.VISIBLE);
            countdownView.start(count);
        }
    }

    private void deleteClipVideo(ShortVideoEntity videoEntity) {
        if (RecordManager.get().getProductEntity() == null || currentPreviewIndex < 0) {
            toastFinish();
            return;
        }
        if (videoEntity.deleteLastClipVideo()) {
            recordOptionPanel.getProgressBar().removeLastItem();
            recordOptionPanel.getProgressBar().setProgress(RecordManager.get().getShortVideoEntity(currentPreviewIndex).getDuringMS());
            videoEntity.setNeedJoin(true);
            RecordManager.get().updateProduct();
        }
        videoEntity.deleteEditingFile();
        seekPosition(currentPreviewIndex);
        //记时器初始化为撤销后的时间节点
        CustomFontTextView tv_record_time = smallRecordViewList.get(currentPreviewIndex).tv_record_time;
        tv_record_time.setText(DiscoveryUtil.convertTimeN((int) videoEntity.getDuring()));
        smallRecordViewList.get(currentPreviewIndex).showDuring(videoEntity.getDuringString());
        smallRecordViewList.get(currentPreviewIndex).showStatus(
                true, videoEntity.hasVideo(), videoEntity.isImport(), videoEntity.reachMin());
        recordActivitySdkView.resetMenu(videoEntity);

        //需要合成音频一次，mix 音频
        recordActivitySdkVideoHelper.mixAudio(new VideoListener() {
            @Override
            public void onStart() {
                recordActivitySdkVideoHelper.showDialog();
            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onFinish(int code, String outpath) {
                if (code == SdkConstant.RESULT_SUCCESS)
                    recordActivitySdkView.combinePreview(false, false, false);
                recordActivitySdkVideoHelper.dismissDialog();
            }

            @Override
            public void onError() {

            }
        });
    }

    private boolean allowRecord = true;

    //录制按钮的监听
    private ExtBtnRecord.onLongListener recordLongListener = new ExtBtnRecord.onLongListener() {
        //录制按下
        @Override
        public void onActionDown() {
            KLog.i("currentPreviewIndex-record-click-1->" + currentPreviewIndex);
            if (!hasFocus()) {
                selectSmallCamera(0);
            }
            KLog.i("currentPreviewIndex-record-click-->" + currentPreviewIndex);
            final ShortVideoEntity videoEntity = RecordManager.get().getShortVideoEntity(currentPreviewIndex);

            if (videoEntity.isImport()) {
                allowRecord = false;
                showToast("本地导入的视频不能再录制");
            } else {
                if (videoEntity.hasEffectAndFilter()) {
                    allowRecord = false;
                    SysAlertDialog.showAlertDialog(RecordActivitySdk.this, R.string.release_back_press_alert, R.string.release_back_press_cancel,
                            null, R.string.release_back_press_confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    videoEntity.deleteEffect();
                                    RecordFileUtil.deleteFiles(videoEntity.editingVideoPath);
                                    RecordManager.get().updateProduct();
                                }
                            });
                } else {
                    allowRecord = true;
                }
            }
        }

        //录制开始
        @Override
        public void onBegin() {
            KLog.i("onBegin---record>" + allowRecord);
            if (allowRecord) {
                recordOptionPanel.showAllOption(false);
                startRecord();
            }
        }

        //录制结束
        @Override
        public void onEnd() {
            allowRecord = true;
            recordOptionPanel.showAllOption(true);
        }

        //录制抬起 (单击录制 结束)
        @Override
        public void onActionUp(boolean isClickRecord) {
            KLog.i("record======setStartRecord-onActionUp-pre" + isClickRecord);
            if (isClickRecord) { // 单击录制
                if (RecordManager.get().getShortVideoEntity(currentPreviewIndex).reachMax()) {//已经最大长度
                    ToastUtil.showToast("已经录制最大长度");
                    return;
                }
                if (RecordManager.get().getProductEntity().getExtendInfo().needHeadsetTips && !RecordUtil.hasHeadset()) {
                    new HeadsetDialog(RecordActivitySdk.this)
                            .setHeadsetDialogClickListener(new HeadsetDialog.HeadsetDialogClickListener() {
                                @Override
                                public void onCancel() {
                                    RecordManager.get().getProductEntity().getExtendInfo().needHeadsetTips = true;
                                    btRecorder.setStartRecord(false);
                                    KLog.i("record======setStartRecord-onActionUp-onCancel" + RecordManager.get().getProductEntity().getExtendInfo().needHeadsetTips);
                                }

                                @Override
                                public void onContinue() {
                                    RecordManager.get().getProductEntity().getExtendInfo().needHeadsetTips = false;
                                    KLog.i("record======setStartRecord-onActionUp-onContinue" + RecordManager.get().getProductEntity().getExtendInfo().needHeadsetTips);
                                    if (allowRecord) {
                                        recordOptionPanel.showAllOption(false);
                                        recordActivitySdkView.hidePanel();
                                        if (countDownNum != 0) {
                                            startCountdown(countDownNum);
                                        } else {
                                            startRecord();
                                        }
                                    }
                                }
                            })
                            .show();
                } else {
                    if (allowRecord) {
                        recordOptionPanel.showAllOption(false);
                        recordActivitySdkView.hidePanel();
                        if (countDownNum != 0) {
                            startCountdown(countDownNum);
                        } else {
                            startRecord();
                        }
                    }
                }
            } else { // 单击结束
                allowRecord = true;
                recordOptionPanel.showAllOption(true);
                KLog.i("========showAllOption--click-end");
                stopRecord();
            }
        }
    };


    private boolean hasFocus() {
        boolean hasFocus = false;
        for (int i = 0, n = smallRecordViewList.size(); i < n; i++) {
            if (smallRecordViewList.get(i).isFocus()) {
                hasFocus = true;
                break;
            }
        }
        return hasFocus;
    }

    //退出全屏录制的监听
    private FullRecordView.OnExitFullRecordListener exitFullRecordListener = new FullRecordView.OnExitFullRecordListener() {
        @Override
        public void onExitFullRecord() {
            exitFullRecord();
        }
    };

    //小视频模式的点击事件
    private SmallRecordView.OnSmallRecordClickListener smallRecordClickListener = new SmallRecordView.OnSmallRecordClickListener() {
        @Override
        public void onEditClick(final int index, SmallRecordView view) {
            KLog.i("=====点击了编辑：" + index);
            recordActivitySdkView.hidePanel();
            final ShortVideoEntity videoEntity = RecordManager.get().getShortVideoEntity(index);
            if (videoEntity != null && videoEntity.hasVideo()) {
                if (videoEntity.hasEffectAndFilter()) {
                    SysAlertDialog.showAlertDialog(RecordActivitySdk.this, R.string.release_clear_effect_alert, R.string.release_back_press_cancel,
                            null, R.string.release_back_press_confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    videoEntity.deleteEffect();
                                    if (videoEntity.isImport() && !TextUtils.isEmpty(videoEntity.importVideoPath)
                                            && new File(videoEntity.importVideoPath).exists()) {
                                        videoEntity.editingVideoPath = RecordFileUtil.createVideoFile(videoEntity.baseDir);
                                        FileUtil.copy(videoEntity.importVideoPath, videoEntity.editingVideoPath);
                                    }
                                    RecordManager.get().updateProduct();
                                    enterEditor(index, videoEntity);
                                }
                            });
                } else {
                    if (videoEntity.isImport() && !TextUtils.isEmpty(videoEntity.importVideoPath)
                            && new File(videoEntity.importVideoPath).exists()) {
                        videoEntity.editingVideoPath = RecordFileUtil.createVideoFile(videoEntity.baseDir);
                        FileUtil.copy(videoEntity.importVideoPath, videoEntity.editingVideoPath);
                    }
                    RecordManager.get().updateProduct();
                    enterEditor(index, videoEntity);
                }
            }
        }

        @Override
        public void onUploadClick(int index, SmallRecordView view) {
            KLog.i("-------本地选择视频" + index);
            recordActivitySdkView.hidePanel();
            smallRecordViewList.get(currentPreviewIndex).showDuring("");
            currentPreviewIndex = index;
            SearchVideoActivity.startSearchVideoActivity(RecordActivitySdk.this, index, SearchVideoActivity.TYPE_FROM_RECORD, 0);
        }

        @Override
        public void onDeleteClick(final int index, SmallRecordView view) {
            KLog.i("=====点击了删除：" + index);
            recordActivitySdkView.hidePanel();
            final ShortVideoEntity videoEntity = RecordManager.get().getShortVideoEntity(index);
            SysAlertDialog.showAlertDialog(RecordActivitySdk.this, R.string.delete_all_recorder_item_alert, R.string.release_back_press_cancel,
                    null, R.string.release_back_press_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            boolean hasCancelDownload = false;
                            if (!TextUtils.isEmpty(frameLayout) &&
                                    downloadStateList != null && downloadStateList.get(index) == SelectFrameActivity.DOWNLOAD_STATE_DOWNLOADING) {
                                if (materials != null && materials.size() > 0) {
                                    UploadMaterialEntity materialEntity = findMaterial(index);
                                    if (materialEntity != null) {
                                        BaseDownloadTask baseDownloadTask = FileDownload.taskArray.get(materialEntity.downloadId);
                                        if (baseDownloadTask != null) {
                                            downloadStateList.set(index, SelectFrameActivity.DOWNLOAD_STATE_NONE);
                                            hasCancelDownload = true;
                                            FileDownload.pause(materialEntity.downloadId, baseDownloadTask.getId());
                                        }
                                    }
                                }
                            }
                            smallRecordViewList.get(index).hideCoverView();
                            smallRecordViewList.get(index).showAdd();
                            if (videoEntity.isImport()) {
                                RecordFileUtil.deleteFiles(videoEntity.editingVideoPath, videoEntity.importVideoPath);
                                videoEntity.editingVideoPath = null;
                                videoEntity.importVideoPath = null;
                                videoEntity.setImport(false, false);
                                videoEntity.setVideoType(String.valueOf(SelectFrameActivity.VIDEO_TYPE_RECORD));
                            } else {
                                videoEntity.editingVideoPath = null;
                                videoEntity.deleteAllClip();
                                videoEntity.deleteEditingFile();
                            }
                            videoEntity.setNeedJoin(true);
                            videoEntity.deleteEffect();
                            videoEntity.originalId = 0;

                            videoEntity.quality = videoQuality;
                            RecordManager.get().updateProduct();
                            if (!hasCancelDownload) {
                                if (index != currentPreviewIndex) {
                                    resetPreItem(currentPreviewIndex, true, true, true, false);
                                    selectSmallCamera(index);
                                } else {
                                    if (smallRecordViewList.size() == 1) {
                                        selectSmallCamera(index);
                                    } else {
                                        resetPreItem(currentPreviewIndex, true, true, true, false);
                                        smallRecordViewList.get(currentPreviewIndex).showStatus(true, videoEntity.hasVideo(), videoEntity.isImport(), videoEntity.reachMin());
                                        selectSmallCamera(index);
                                    }
                                }
                                recordActivitySdkView.loadRecordProgress(index);
                                seekPosition(currentPreviewIndex);
                                recordActivitySdkView.resetMenu(videoEntity);
                                KLog.i("dialog---show==--4>");
                            }
                        }
                    });
        }

        @Override
        public void onZoomClick(int index, SmallRecordView view) {
            KLog.i("=====点击了缩放：" + index);
            if (index != currentPreviewIndex) {
                resetPreItem(currentPreviewIndex, true, true, false, false);
                selectSmallCamera(index);
            }
            enterFullRecord();
            recordActivitySdkView.hidePanel();
        }

        @Override
        public void onAddClick(int index, SmallRecordView view) {
            if (doRecordEnd)//当前正在处理 录制结束操作
                return;

            KLog.i("onAddClick" + index);
            if (RecordManager.get().getProductEntity() == null) {
                toastFinish();
                return;
            }
            ShortVideoEntity videoEntity = RecordManager.get().getShortVideoEntity(index);
            if (videoEntity != null && !videoEntity.isImport()) {
                if (!hasFocus() || index != currentPreviewIndex) {
                    recordActivitySdkVideoHelper.showDialog();
                    cutdown.setVisibility(View.VISIBLE);
                    cutdown.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            cutdown.setVisibility(View.GONE);
                        }
                    }, 500);
                    int old = currentPreviewIndex;
                    currentPreviewIndex = index;
                    if (!RecordManager.get().getShortVideoEntity(old).isImport())//之前已经对本地导入进行合并了，不需要合并
                        resetPreItem(old, true, true, false, false);
                    else {
                        recordActivitySdkVideoHelper.dismissDialog();
                    }
                    selectSmallCamera(currentPreviewIndex);
                    int filterId = RecordManager.get().getShortVideoEntity(currentPreviewIndex).getFilterId();
                    dcRecorderHelper.switchFilter(filterId % FILTER_LIST.size());
                }
            }
            recordActivitySdkView.hidePanel();
        }

        @Override
        public void onRefreshClick(int index, SmallRecordView view) {
            int currentIndex = index;
            UploadMaterialEntity materialEntity = findMaterial(currentIndex);
            if (materialEntity != null) {
                String videopath = materialEntity.material_video;
                if (videoQuality == FrameInfo.VIDEO_QUALITY_HIGH && !TextUtils.isEmpty(materialEntity.material_video_high)) {
                    videopath = materialEntity.material_video_high;
                }
                if (!TextUtils.isEmpty(videopath)) {
                    DownloadBean downloadBean = new DownloadBean(materialEntity.material_index, videopath,
                            RecordFileUtil.getMaterialDir(), "", "mp4");
                    downloadMaterial(downloadBean, true);
                }
            }
        }

    };

    private void enterEditor(final int index, final ShortVideoEntity videoEntity) {
        if (index != currentPreviewIndex) {
            int preIndex = currentPreviewIndex;
            currentPreviewIndex = index;
            resetPreItem(preIndex, true, true, false, true, true);
            selectSmallCamera(currentPreviewIndex);
        } else {
            EditVideoActivity.startEditVideoActivity(RecordActivitySdk.this, index);
        }
    }


    public void onRecordEnd(int result) {
        doRecordEnd = true;
        pauseAllVideo();
//        KLog.i("RecordActivitySdk1", "onRecordEnd-playerEngine.getCutTime:" + playerEngine.getCutTime(this));

        // 开始裁剪视频
        if (playerEngine != null && playerEngine.getCutTime(this) - DCLoopbackTool.cutTime * 1000 > 0) {//开始截取的时间，大于0 ，开始裁剪视频

            recordActivitySdkVideoHelper.cut(new VideoListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onProgress(int progress) {

                }

                @Override
                public void onFinish(int code, String outpath) {
                    onRecordEndUpdate(result);
                }

                @Override
                public void onError() {
                    onRecordEndUpdate(result);
                }
            });
        } else {
            onRecordEndUpdate(result);
        }
        KLog.i("recordActivitySdk", "onRecordEnd--isClickable:" + smallRecordViewList.get(currentPreviewIndex).isClickable());
    }

    public void onRecordEndUpdate(int result) {
        ShortVideoEntity videoEntity = RecordManager.get().getShortVideoEntity(currentPreviewIndex);
        if (result >= DCCameraConfig.SUCCESS) {
            ClipVideoEntity entity = new ClipVideoEntity();
            entity.setVideoPath(dcRecorderHelper.tempVideoPath);
            entity.setAudioPath(dcRecorderHelper.getTempAudioPath());
//                entity.audioPath = tempAudioPath;
            entity.setSpeedIndex(videoEntity.getCurrentSpeedIndex());
            entity.supportFastReverse = (result == ResultConstants.SUCCESS_AND_ALL_KEY_FRAMES);

            KLog.i("videoplay=====onRecordEnd:pre44-getDuring>" + dcRecorderHelper.tempVideoPath + VideoUtils.getVideoLength(dcRecorderHelper.tempVideoPath));
            KLog.i("videoplay=====onRecordEnd:pre44-cutResult->>" + dcRecorderHelper.getTempAudioPath());
            if (entity.getDuring() > 0.01f) {

                videoEntity.addClipVideo(entity);
                int during = videoEntity.getDuringMS();
                KLog.i("=====during:" + during);
                recordOptionPanel.getProgressBar().setProgress(during);
                recordOptionPanel.getProgressBar().addItemLine(during);
                videoEntity.deleteEditingFile();
                videoEntity.setNeedJoin(true);
                RecordManager.get().updateProduct();//保存到数据库
                KLog.d("ggqTAG", RecordManager.get().getShortVideoEntity(currentPreviewIndex).getClipList());
            } else {
                RecordFileUtil.deleteFiles(dcRecorderHelper.tempVideoPath);
                RecordFileUtil.deleteFiles(dcRecorderHelper.getTempAudioPath());
//                    RecordFileUtil.deleteFiles(tempAudioPath);
//                showToast(getString(R.string.recorder_touch_short));
                KLog.i("videoplay=====录制视频时间太短--删除");
            }
            boolean hasVideo = videoEntity.getClipVideoSize() > 0;
            if (dcRecorderHelper.isFullRecord) {
                flFullRecord.showRecording(false);
                KLog.i("======onRecordEndUpdate--showRecording--false");
            }
            KLog.i("=====录制时间ok--before>" + videoEntity.getDuringString());
            KLog.i("=====onRecordEnd:-clickable-==before>" + smallRecordViewList.get(currentPreviewIndex).clickable);
            smallRecordViewList.get(currentPreviewIndex).showDuring(videoEntity.getDuringString());
            smallRecordViewList.get(currentPreviewIndex).showStatus(
                    true, hasVideo, false, videoEntity.reachMin());
            recordActivitySdkView.resetMenu(videoEntity);
            KLog.i("dialog---show==--2>");
            KLog.i("=====录制时间ok-->" + videoEntity.getDuringString());
        } else {
            KLog.i("=====录制失败 result:" + result);
        }
        doRecordEnd = false;
        recordActivitySdkVideoHelper.dismissDialog();
    }


    @Override
    public void onCountdownStart() {
        recordActivitySdkView.setNextEnable(false);
        btRecorder.setEnabled(false);
        recordOptionPanel.showAllOption(false);
        recordMenu.setVisibility(View.INVISIBLE);
        cutdown.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCountdownEnd() {
        startRecord();
        recordActivitySdkView.countDownEnd();
    }

    @Override
    public void onCountdownCancel() {
        recordActivitySdkView.countDownCancel();
    }

    //速度
    @Override
    public void onSpeedChanged(int itemId) {
        RecordManager.get().getShortVideoEntity(currentPreviewIndex).setCurrentSpeedIndex(itemId);
    }


    private void enterFullRecord() {
        dcRecorderHelper.isFullRecord = true;
        recordActivitySdkView.enterFullRecord(currentPreviewIndex, smallRecordViewList.get(currentPreviewIndex));
        ShortVideoEntity videoEntity = RecordManager.get().getShortVideoEntity(currentPreviewIndex);
        selectedPositionChanged(videoEntity.getFilterId());
    }

    /**
     * 推出全屏，打开小的摄像头
     */
    private void exitFullRecord() {
        dcRecorderHelper.isFullRecord = false;
        recordActivitySdkView.exitFullRecord(smallRecordViewList.get(currentPreviewIndex).getPreview());
//        selectSmallCamera(currentPreviewIndex);
        //设置滤镜
        ShortVideoEntity videoEntity = RecordManager.get().getShortVideoEntity(currentPreviewIndex);
        selectedPositionChanged(videoEntity.getFilterId());
    }

    public void selectSmallCamera(int index) {
        if (index == -1) {
            return;
        }
        selectSmallCamera(index, true);
    }

    private void selectSmallCamera(int index, boolean enableRecord) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        if (index < smallRecordViewList.size()) {
            currentPreviewIndex = index;
            //预览画面
            dcRecorderHelper.selectSmallCamera(smallRecordViewList.get(index).getPreview());
            recordActivitySdkView.selectSmallCamera(index, enableRecord);
        }
    }


    private void startRecord() {
        ShortVideoEntity videoEntity = RecordManager.get().getShortVideoEntity(currentPreviewIndex);
        boolean startRecordResult = dcRecorderHelper.startRecord(videoEntity, btRecorder);
        if (startRecordResult) {//成功
            startTimer();
            recordActivitySdkView.onStart();
        } else {//
            ToastUtil.showToast("录制失败，请重试");
        }
    }

    public void stopRecord() {
        doRecordEnd = true;
        if (dcRecorderHelper == null)
            initRecorder();
        if (dcRecorderHelper.isRecording()) {
            waitforClick();
        }
        stopTimer();
        boolean r = dcRecorderHelper.stopRecord();
        if (recordActivitySdkView == null) {
            recordActivitySdkView = new RecordActivitySdkViewImpl();
        }
        recordActivitySdkView.onRecordEnd(currentPreviewIndex);
    }

    /**
     * 防止快速点击
     */
    private void waitforClick() {
        cutdown.setVisibility(View.VISIBLE);
        cutdown.postDelayed(new Runnable() {
            @Override
            public void run() {
                cutdown.setVisibility(View.GONE);
            }
        }, 500);
    }


    private void startTimer() {
        if (currentPreviewIndex != -1) {
            ShortVideoEntity entity = RecordManager.get().getShortVideoEntity(currentPreviewIndex);
            delayTime = (long) (RecordSpeed.getSpeed(entity.getCurrentSpeedIndex()) * 1000);
            int lastTotalTime = (int) (entity.getDuring());
            CustomFontTextView tv_record_time = smallRecordViewList.get(currentPreviewIndex).tv_record_time;
            tv_record_time.setText(((int)entity.getDuring())+"s");
            KLog.i("录制前时间:--->"+entity.getDuring());
            TextView tvDuring = smallRecordViewList.get(currentPreviewIndex).tvDuring;
            tvDuring.setVisibility(View.GONE);
            //开始时间，即上一个视频的综合时间
            Log.i("超过了", (tv_record_time.getVisibility() == View.VISIBLE) + "TimeUtils");
            timerUtil.startTimer(tvDuring, lastTotalTime, delayTime, new Handler() {
                @Override
                public void handleMessage(Message msg) {//更新界面
                    if (msg.what == 0) {//停止,手动停止后

                    } else if (msg.what <= (RecordManager.get().getSetting().maxVideoDuration) / 1000) {//正常范围内{//正在进行
                        if (currentPreviewIndex != -1) {
                            tvDuring.setText(DiscoveryUtil.convertTime(msg.what));
                            tv_record_time.setText(DiscoveryUtil.convertTimeN(msg.what));
                            if (tv_record_time.getVisibility() != View.VISIBLE)
                                tv_record_time.setVisibility(View.VISIBLE);
                            if (dcRecorderHelper.isFullRecord) {//全屏，
                                flFullRecord.showTime();
                                flFullRecord.setTime(DiscoveryUtil.convertTimeN(msg.what));
                            }
                            Log.e("超过了", DiscoveryUtil.convertTimeN(msg.what) + "TimeUtils" + (tv_record_time.getVisibility() == View.VISIBLE));
                        }
                    } else if (msg.what > (RecordManager.get().getSetting().maxVideoDuration) / 1000) {
                        //进行相应的超时操作
                        Log.e("超过了", "TimeUtils" + msg.what);

//                        stopRecord();
                    }
                }
            });
        }
    }

    public void stopTimer() {
        if (timerUtil == null)
            return;
        timerUtil.stopTimer();
    }


    /**
     * @param nextBtnEnable 是否禁用next按钮点击
     */
    public void playAllVideo(boolean nextBtnEnable) {
        if (playerEngine != null && !playerEngine.isNull()) {
            float position = RecordManager.get().getShortVideoEntity(currentPreviewIndex).getDuring();
            KLog.i("====playAllVideo:-seekPosition" + position + "   playerEngine.getDuration()=" + playerEngine.getDuration());
            if (position * 1000 * 1000 * 1000 < playerEngine.getDuration() || position == playerEngine.getDuration()) {
//                playerEngine.seekTo(position);
//                playerEngine.start();
                KLog.i("播放视频--:" + position);
                playerEngine.seekToPlay((long) position * 1000 * 1000, true);
            } else {//如果当前是第一个录制的视频

            }
        }
        ShortVideoEntity videoEntity;
        KLog.i("======baseTimeStamp-》" + TextureMovieEncoder.baseTimeStamp);
        for (int i = 0, n = smallRecordViewList.size(); i < n; i++) {
            smallRecordViewList.get(i).setCanClick(false);
            videoEntity = RecordManager.get().getShortVideoEntity(i);
            if (videoEntity != null) {
                smallRecordViewList.get(i).showAllViews(false, i == currentPreviewIndex, videoEntity.hasVideo(), videoEntity.isImport(), videoEntity.reachMin());
                //正在播放，隐藏删除，
                smallRecordViewList.get(i).showPlaying(true);
            } else {
                RecordManager.get().getProductEntity().shortVideoList.set(i, new ShortVideoEntity());
            }

        }
        recordActivitySdkView.playVideo(currentPreviewIndex);
        showBack(false);
    }

    private float seekPosition(int currentIndex) {
        float position = RecordManager.get().getShortVideoEntity(currentIndex).getDuring();
        if (playerEngine != null && position <= playerEngine.getDuration()) {
            playerEngine.seekTo(position);
        }
        return position;
    }

    private void pauseAllVideo() {
//        KLog.i("====暂停播放视频-->" + (!playe/**/rEngine.isNull() && playerEngine.isPlaying()));
        if (playerEngine != null && playerEngine.isPlaying()) {
            playerEngine.pause();
        }
        showBack(true);
        recordActivitySdkView.pauseVideo(currentPreviewIndex);
    }

    /**
     * 将上一个视频进行 保存，合并 处理
     *
     * @param preIndex
     * @param needJoin
     * @param needCombine
     * @param combineAll
     * @param seekEnd
     */
    private void resetPreItem(final int preIndex, boolean needJoin, final boolean needCombine, final boolean combineAll, final boolean seekEnd) {
        resetPreItem(preIndex, needJoin, needCombine, combineAll, seekEnd, false);
    }

    private void resetPreItem(final int preIndex, boolean needJoin, final boolean needCombine, final boolean combineAll, final boolean seekEnd, final boolean entryEdit) {
        resetPreItem(preIndex, needJoin, needCombine, combineAll, seekEnd, entryEdit, false);
    }

    private void resetPreItem(final int preIndex, boolean needJoin, final boolean needCombine, final boolean combineAll, final boolean seekEnd, final boolean entryEdit, boolean entrySort) {
        if (preIndex > -1 && preIndex < smallRecordViewList.size()) {
            dcRecorderHelper.resetPreItem();
            final ShortVideoEntity videoEntity = RecordManager.get().getShortVideoEntity(preIndex);
            final boolean hasVideo = videoEntity.hasVideo();
            smallRecordViewList.get(preIndex).showStatus(
                    preIndex == currentPreviewIndex, hasVideo, videoEntity.isImport(), videoEntity.reachMin());
            smallRecordViewList.get(preIndex).setFocus(false);
            smallRecordViewList.get(preIndex).showDuring(videoEntity.getDuringString());
            KLog.i("====重置播放预览item:" + preIndex);
            KLog.i("playvideo====combinePreview:-before" + needJoin);
            resetPreItemPreview(preIndex, needJoin, needCombine, combineAll, seekEnd, entryEdit, entrySort, videoEntity);
        } else {
            recordActivitySdkVideoHelper.dismissDialog();
        }
    }

    private void resetPreItemPreview(final int preIndex, boolean needJoin, boolean needCombine, boolean combineAll, final boolean seekEnd,
                                     final boolean entryEdit, boolean entrySort, final ShortVideoEntity videoEntity) {
        boolean hasVideo = videoEntity.hasVideo();
        KLog.i(needCombine + "resetPreItemPreview======生成视频预览成功 resetPreItemPreview：" + needJoin + "isImprot-->" + videoEntity.isImport());
        if (needJoin) {
            if (videoEntity.isImport()) {
                if (comBineMode) {
                    if (playerEngine != null)
                        playerEngine.release();
                    recordActivitySdkVideoHelper.mixAudio(new VideoListener() {
                        @Override
                        public void onStart() {
                            recordActivitySdkVideoHelper.showDialog();
                        }

                        @Override
                        public void onProgress(int progress) {

                        }

                        @Override
                        public void onFinish(int code, String outpath) {
                            if (code == DCCameraConfig.SUCCESS) {//
                                if (needCombine) {
                                    KLog.i("realease--->", "init-player--onExportEnd");
                                    recordActivitySdkView.combinePreview(seekEnd, entryEdit, entrySort);
                                } else {
                                    recordActivitySdkVideoHelper.dismissDialog();
                                }
                            } else {
                                KLog.i("======生成视频预览失败 index：" + preIndex);
                                recordActivitySdkView.combinePreview(seekEnd, entryEdit, entrySort);
                            }
                        }

                        @Override
                        public void onError() {
                            recordActivitySdkVideoHelper.dismissDialog();
                        }
                    });
                } else if (needCombine) {
                    recordActivitySdkView.combinePreview(seekEnd, entryEdit, entrySort);
                }
            } else {
                if (!videoEntity.needJoin() && !TextUtils.isEmpty(videoEntity.editingVideoPath)
                        && new File(videoEntity.editingVideoPath).exists()) {
                    if (needCombine) {
                        recordActivitySdkView.combinePreview(seekEnd, entryEdit, entrySort);
                    } else {
                        recordActivitySdkVideoHelper.dismissDialog();
                    }
                } else {
                    if (comBineMode) {
                        if (playerEngine != null && !playerEngine.isNull())
                            playerEngine.release();

                        boolean compose = recordActivitySdkVideoHelper.getComposeList();
                        KLog.i(needCombine + "resetPreItemPreview======生成预览 ：" + compose);
                        if (compose) {
                            composeAndMix(preIndex, needCombine, combineAll, seekEnd, entryEdit, entrySort);
                        } else {//不需要组合
                            recordActivitySdkView.combinePreview(seekEnd, entryEdit, entrySort);
                        }
                    } else {
                        RecordUtilSdk.compose(videoEntity, new VideoListener() {
                            @Override
                            public void onStart() {
                                KLog.d("compose---onStart---show>");
                                if (dialog == null) {
                                    dialog = SysAlertDialog.createCircleProgressDialog(RecordActivitySdk.this, getString(R.string.join_preview), true, false);
                                }
                                if (!dialog.isShowing()) {
                                    dialog.show();
                                }
                            }

                            @Override
                            public void onProgress(int progress) {
                                KLog.d("compose---resetPreItemPreview---show>" + progress);
                                if (dialog != null) {
//                                    dialog.setProgress(progress / 10);
                                }
                            }

                            @Override
                            public void onFinish(int code, String outpath) {
                                KLog.i(needCombine + "compose======生成视频预览成功 index：" + code + "code" + preIndex + outpath);
                                if (code == DCCameraConfig.SUCCESS) {//
                                    //如果两个视频需要导出，
                                    smallRecordViewList.get(preIndex).showStatus(
                                            false, hasVideo, videoEntity.isImport(), videoEntity.reachMin());
                                    videoEntity.editingVideoPath = outpath;
                                    RecordManager.get().updateProduct();
                                    KLog.i("======editingVideoPath ：" + outpath);
                                    if (needCombine) {
                                        recordActivitySdkView.combinePreview(seekEnd, entryEdit, entrySort);
                                    }
                                } else {// 可能没有视频资源。
                                    KLog.i("======生成视频预览失败 index：" + preIndex);
//                                if (needCombine) {
//                                    combinePreview(combineAll, seekEnd, entryEdit, entrySort);
//                                }
                                }
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            }

                            @Override
                            public void onError() {

                            }
                        });
                    }
                }
            }
        }
    }


    private void composeAndMix(int index, boolean needCombine, boolean combineAll, boolean seekEnd, boolean entryEdit, boolean entrySort) {
        recordActivitySdkVideoHelper.composeAndExportNew(index, new VideoListener() {
            @Override
            public void onStart() {
                KLog.d("composeAndMix-onStart" + "start");
            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onFinish(int code, String outpath) {
                KLog.d("composeAndMix-onFinsh>" + code + "path" + outpath);
                if (code == SdkConstant.RESULT_SUCCESS) {//
                    if (needCombine) {
                        recordActivitySdkView.combinePreview(seekEnd, entryEdit, entrySort);
                    } else {
                        recordActivitySdkVideoHelper.dismissDialog();
                    }
                } else {
                    KLog.i("======生成视频预览失败 index：");
                    recordActivitySdkView.combinePreview(seekEnd, entryEdit, entrySort);
                }

            }

            @Override
            public void onError() {
                recordActivitySdkVideoHelper.dismissDialog();
                KLog.i("composeAndMix======生成视频预览失败 index：onError");
            }
        });
    }


    private float lastPosition = -1f;

    @Override
    protected void onResume() {
        super.onResume();
        KLog.i("RecordActivitySdk", "onResume--before>dialog.isShowing->"+recordActivitySdkVideoHelper.dialog.isShowing());
        doRecordEnd = false;
        if (!isFirstEnter) {
//            combinePreview(true, false, false);
            if (smallRecordViewList == null) {
                toastFinish();
                return;
            }
            if (currentPreviewIndex != -1 && currentPreviewIndex < smallRecordViewList.size()) {
                ShortVideoEntity videoEntity = RecordManager.get().getShortVideoEntity(currentPreviewIndex);
                if (videoEntity != null && !videoEntity.isImport()) {
                    selectSmallCamera(currentPreviewIndex);
                    KLog.i("RecordActivitySdk", "onResume-->release--Camera"+recordActivitySdkVideoHelper.dialog.isShowing());
                }
            }
            isFirstEnter = false;
        }
        if (dcRecorderHelper != null && dcRecorderHelper.isFullRecord) {
            recordActivitySdkView.enterFullRecord(currentPreviewIndex, smallRecordViewList.get(currentPreviewIndex));
        }
        if (playerEngine != null && !playerEngine.isNull() && lastPosition != -1f) {
            playerEngine.seekTo(lastPosition);
        } else {//如果未空，，重新初始化
            KLog.i("RecordActivitySdk", "onResume-->release--initPlayer" + recordType);
            if (recordType != TYPE_TOGETHER) {//替换 不做操作
                if (recordActivitySdkView == null)
                    recordActivitySdkView = new RecordActivitySdkViewImpl();
                recordActivitySdkView.combinePreview(false, false, false);
            }

        }
        KLog.i("RecordActivitySdk", "onResume-->dialog.isShowing->"+recordActivitySdkVideoHelper.dialog.isShowing());

        KLog.i("RecordActivitySdk", "onResume-->release");
    }

    @Override
    protected void onPause() {
        super.onPause();
        KLog.i("RecordActivitySdk", "onPause-->dialog.isShowing->"+recordActivitySdkVideoHelper.dialog.isShowing());
        isFirstEnter = false;
        stopRecord();
        btRecorder.setClickRecord(true);
        if (playerEngine != null && !playerEngine.isNull()) {
            if (playerEngine.isPlaying()) {
                lastPosition = playerEngine.getCurrentPosition();
                playerEngine.pause();
//                playerEngine.release();
            }

        } else {
            lastPosition = -1f;
        }
//        dcRecorderHelper.releaseCamera();
        recordActivitySdkVideoHelper.dismissDialog();
        KLog.i("RecordActivitySdk", "onPause--end>dialog.isShowing->"+recordActivitySdkVideoHelper.dialog.isShowing());
        KLog.i("RecordActivitySdk", "onPause-->release" + lastPosition);
    }

    @Override
    protected void onStop() {
        if (!StringUtils.isAppOnForeground(this)) {
            allowRecord = true;
            recordOptionPanel.showAllOption(true);
            stopRecord();
        }
        DCLoopbackTool.stopBind();
        recordActivitySdkVideoHelper.dismissDialog();
        KLog.i("RecordActivitySdk", "onStop-->dialog.isShowing->"+recordActivitySdkVideoHelper.dialog.isShowing());
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTrimOkEvent(EventEntity entity) {
        KLog.d("clipVideo--onTrimOkEvent" + entity.code);
        if (entity.code == GlobalParams.EventType.TYPE_TRIM_FINISH) {
            TrimVideoActivityNew.LocalUploadResultEntity localUploadResultEntity = (TrimVideoActivityNew.LocalUploadResultEntity)entity.data;
            ShortVideoEntity shortVideoEntity = RecordManager.get().getShortVideoEntity(currentPreviewIndex);
            //设置数据
            shortVideoEntity.combineVideoAudio = localUploadResultEntity.combineAV;
            shortVideoEntity.editingVideoPath = localUploadResultEntity.videoPath;
            shortVideoEntity.editingAudioPath = localUploadResultEntity.audioPath;
            shortVideoEntity.importVideoPath = localUploadResultEntity.combineAV;
            KLog.d("clipVideo--onTrimOkEvent-videopath" + localUploadResultEntity.videoPath);
            // 设置 导入信息
            shortVideoEntity.setImport(true);
            shortVideoEntity.hasEdited = true;
            shortVideoEntity.setVideoType(String.valueOf(SelectFrameActivity.VIDEO_TYPE_IMPORT));
//            RecordManager.get().updateProduct();

            //显示数据
            getWeakHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    recordActivitySdkVideoHelper.showDialog();
                    //查看当前录制的视频
                    resetPreItem(currentPreviewIndex, true, true, true, false);
                    recordActivitySdkView.resetMenu(RecordManager.get().getShortVideoEntity(currentPreviewIndex));

//                    btRecorder.onForcedExit();
//                    btRecorder.setImageResource(R.drawable.btn_recorder_start);
//                    btRecorder.setEnabled(false);
//                    btRecorder.setStartRecord(false);
//                    KLog.i("record======setStartRecord-onRecordEnd-false");
//                    btRecorder.enableTouchScroll(true);
                    KLog.i("dialog---show==--3>"+RecordManager.get().getShortVideoEntity(currentPreviewIndex).isImport());

                }
            }, 100);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEditOkEvent(EventEntity entity) {
        if (entity.code == GlobalParams.EventType.TYPE_EDIT_FINISH) {
            ShortVideoEntity videoEntity = RecordManager.get().getShortVideoEntity(currentPreviewIndex);
            KLog.i("=====本地视频裁剪完成，路径：" + videoEntity.editingVideoPath);
            if (!TextUtils.isEmpty(videoEntity.editingVideoPath) && new File(videoEntity.editingVideoPath).exists()) {
                recordActivitySdkView.combinePreview(false, false, false);
//                RecorderCore.recycleCameraView();
                smallRecordViewList.get(currentPreviewIndex).setFocus(true);
                smallRecordViewList.get(currentPreviewIndex).showStatus(
                        true, videoEntity.hasVideo(), videoEntity.isImport(), videoEntity.reachMin());
                recordMenu.setCutEnable(RecordManager.get().canCutMusic());
                recordMenu.setFlashEnable(false, false);
                recordMenu.setToggleEnable(false);
            }
        }
    }



    @Override
    protected void onDestroy() {
        KLog.d("clipVideo--ondestroy");
        EventHelper.unregister(this);
        if (null != countdownView) {
            countdownView.release();
        }
        if (null != flFullRecord) {
            flFullRecord.releaseView();
        }
        if (dcRecorderHelper != null)
            dcRecorderHelper.onDestroy();
        if (playerEngine != null && !playerEngine.isNull()) {
            playerEngine.release();
            playerEngine = null;
        }
        if (recordActivitySdkVideoHelper != null)
            recordActivitySdkVideoHelper.onDestory();
        videoFrameLayout.removeAllViews();
        FileDownload.pause(RecordActivitySdk.this);
        System.gc();
        System.runFinalization();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        DCLoopbackTool.destroy();
        mContext = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        KLog.i("index i " + " onBackPressed === pre " + recordFilterSelector.getVisibility() + "speed" + llSpeedPanel.getVisibility());

        if (dcloopbackTestView.getVisibility() == View.VISIBLE) {
            return;
        }

        if (filterLayout.getVisibility() == View.VISIBLE) {
            filterLayout.setVisibility(View.INVISIBLE);
            return;
        }
        if (dcRecorderHelper != null && dcRecorderHelper.isRecording()) {
            stopRecord();
            return;
        }
        if (countdownView.isStarted()) {
            countdownView.cancel();
            cutdown.setVisibility(View.GONE);
            return;
        }
//        KLog.i("index i " + " onBackPressed === isFullRecord" + dcRecorderHelper.isFullRecord);
        if (dcRecorderHelper != null && dcRecorderHelper.isFullRecord) {
            exitFullRecord();
            return;
        }
        if (RecordManager.get().getProductEntity() != null && RecordManager.get().getProductEntity().hasVideo()) {
            if (recordBackType == TYPE_DRAFT || recordType == TYPE_DRAFT) {
                SysAlertDialog.createAlertDialog(this, "", getString(R.string.back_draft_box), getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, getString(R.string.sure), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                startActivity(new Intent(mContext, DraftBoxActivity.class));
                                finish();
                            }
                        }, false, null)
                        .show();
            } else {
                KLog.i("index i " + " onBackPressed === create-AlertDialog" + this);
                SysAlertDialog.createAlertDialog(this, "", getString(R.string.giveup_recording), getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, getString(R.string.sure), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                RecordUtil.deleteProduct(RecordManager.get().getProductEntity(), true);
                                RecordManager.get().clearAll();
                                if (isExistActivity(MainActivity.class)) {
                                    MyAppActivityManager.getInstance().finishAllActivityExceptOne(MainActivity.class);
                                } else {
                                    finish();
                                }
                            }
                        }, false, null)
                        .show();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_EDIT_VIDEO:
                    if (dialog == null) {
                        dialog = SysAlertDialog.showCircleProgressDialog(RecordActivitySdk.this, getString(R.string.join_preview), true, false);
                    }
                    ShortVideoEntity videoEntity = RecordManager.get().getShortVideoEntity(currentPreviewIndex);

                    if (!TextUtils.isEmpty(videoEntity.editingVideoPath) && new File(videoEntity.editingVideoPath).exists()) {
                        smallRecordViewList.get(currentPreviewIndex).setFocus(true);
                        smallRecordViewList.get(currentPreviewIndex).showStatus(
                                true, videoEntity.hasVideo(), videoEntity.isImport(), videoEntity.reachMin());
                        KLog.i("dialog---show==--1>");
                        recordActivitySdkView.resetMenu(videoEntity);
                    }
                    getWeakHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recordActivitySdkView.combinePreview(false, false, false);
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        }
                    }, 500);
                    break;

                case PublishActivity.REQUEST_NEED_RELOAD://切换画框
                    KLog.i("exportProductVideoTemp--切换画框--combineVideo" + RecordManager.get().getProductEntity().combineVideo);
                    int index = data.getIntExtra(EditProductionActivity.CURRENT_INDEX, -1);
                    if (index != -1) {
                        currentPreviewIndex = index;
                    }
                    if (currentPreviewIndex < 0)
                        currentPreviewIndex = 0;
                    //重置画框
                    smallRecordViewList = null;
                    initRecordItemView(true);

                    recordActivitySdkVideoHelper.combineData();

                    //当前视频是否已经满了
//                    KLog.i("exportProductVideoTemp--切换画框--combineVideo"+RecordManager.get().getProductEntity().shortVideoList.get(currentPreviewIndex).getDuring());
                    allowRecord = true;

                    recordActivitySdkView.resetMenu(RecordManager.get().getProductEntity().shortVideoList.get(currentPreviewIndex));
//                    btRecorder.setStartRecord(isEmpty || !videoEntity.canContinueRecord());

                    recordActivitySdkView.resetView(currentPreviewIndex);

                    KLog.i("exportProductVideoTemp--切换画框--combineVideo--2>" + RecordManager.get().getProductEntity().combineVideo);
                    break;
            }
        }
    }

    @Override
    protected RecordPresenter getPresenter() {
        return new RecordPresenter(this);
    }

    @Override
    public void onGetFrameInfo(boolean result, boolean isNetwork) {
        if (result) {
            mFrameInfo = RecordManager.get().getFrameInfo();
            KLog.d("替换-onGetFrameInfo-->" + mFrameInfo);
            videoQuality = mFrameInfo.video_quality;
            RecordManager.get().initSetting();
            List<ShortVideoEntity> videoList = RecordManager.get().getProductEntity().shortVideoList;
            for (int i = 0, size = videoList.size(); i < size; i++) {
                if (videoList.get(i) != null && videoQuality == FrameInfo.VIDEO_QUALITY_HIGH) {
                    videoList.get(i).quality = FrameInfo.VIDEO_QUALITY_HIGH;
                }
            }
            RecordManager.get().getProductEntity().originalId = opusId;
            getPresenter().getOpusMaterial(opusId);
        } else {
            toastFinish();
        }
    }

    /**
     * 滤镜选择回调
     *
     * @param pos
     */
    @Override
    public void selectedPositionChanged(int pos) {
        Log.d("onFilterSelected", "selectIndex:" + pos);
        RecordManager.get().getShortVideoEntity(currentPreviewIndex).setFilterId(pos);
        dcRecorderHelper.switchFilter(pos % FILTER_LIST.size());
    }

    /**
     * 滤镜点击选择回调
     *
     * @param position
     */
    @Override
    public void onFilterItemClick(int position) {
        recordFilterSelector.moveToPosition(position);
    }

    /**
     * 倒计时时间选择回调
     *
     * @param position
     * @param data
     */
    @Override
    public void onCountDownItemClick(int position, String data) {
        recordOptionPanel.setCountDown(position);
        countDownNum = Integer.parseInt(data);
    }

    @Override
    public void onGetMaterial(OpusMaterialEntity response) {
        Activity activity = RecordActivitySdk.this;
        if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
            return;
        }
        handler = new Handler(Looper.getMainLooper());
        resultReceiver = new VideoReceiver(this, handler);
        materials = response.materials;
        currentPreviewIndex = replacePosition;
        initVideoList();
        initRecordItemView(true);
        ArrayList<DownloadBean> downloadList = new ArrayList<>();
        if (materials != null && materials.size() > 0) {
            for (int i = 0, size = materials.size(); i < size; i++) {
                UploadMaterialEntity materialEntity = materials.get(i);
                if (materialEntity != null) {
                    int realQuality = FrameInfo.VIDEO_QUALITY_LOW;
                    String videopath = materialEntity.material_video;
                    if (videoQuality == FrameInfo.VIDEO_QUALITY_HIGH && !TextUtils.isEmpty(materialEntity.material_video_high)) {
                        realQuality = FrameInfo.VIDEO_QUALITY_HIGH;
                        videopath = materialEntity.material_video_high;
                    }
                    if (!TextUtils.isEmpty(videopath)) {
                        if (materialEntity.material_index != replacePosition) {
//                            smallRecordViewList.get(materialEntity.material_index).setMaterialCover(materialEntity.material_cover, materialEntity.material_cover);
                            smallRecordViewList.get(materialEntity.material_index).showProgress();
                            smallRecordViewList.get(materialEntity.material_index).showDuring(DiscoveryUtil.convertTime((int) materialEntity.material_length / 1000));
                            int downloadId = FileDownloadUtils.generateId(videopath, RecordFileUtil.getMaterialDir());
                            materialEntity.downloadId = downloadId;
                            DownloadBean downloadBean = new DownloadBean(downloadId, videopath,
                                    RecordFileUtil.getMaterialDir(), "", "mp4", materialEntity.material_index);
                            downloadList.add(downloadBean);
                            ShortVideoEntity shortVideoEntity = RecordManager.get().getShortVideoEntity(materialEntity.material_index);
                            if (shortVideoEntity != null) {
                                shortVideoEntity.setImport(true, false);
                                shortVideoEntity.setVideoType(String.valueOf(SelectFrameActivity.VIDEO_TYPE_TEAMWORK));
                                shortVideoEntity.quality = realQuality;
                            }
                        } else {
                            hasReplaceMaterial = true;
                        }
                    }
                }
            }
            downloadMaterial(downloadList);
        }
        KLog.d("替换-onGetMaterial--init");
        init();
    }

    /**
     * 批量下载素材
     *
     * @param downloadList
     */
    private void downloadMaterial(ArrayList<DownloadBean> downloadList) {
        FileDownload.start(mContext, downloadList, resultReceiver, true);
    }

    private void downloadMaterial(DownloadBean downloadBean, boolean reDownload) {
        FileDownload.start(mContext, downloadBean, resultReceiver, reDownload, true);
    }

    @Override
    public void onTestCompleted() {
//        Log.i("===yang","testCompleted");
        if (dcloopbackTestView != null && dcloopbackTestView.getVisibility() != View.GONE) {
            dcloopbackTestView.setVisibility(View.GONE);
        }
    }


    private static class VideoReceiver extends ResultReceiver {
        private WeakReference<RecordActivitySdk> activity;

        @SuppressLint("RestrictedApi")
        public VideoReceiver(RecordActivitySdk activity, Handler handler) {
            super(handler);
            this.activity = new WeakReference<>(activity);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String message = resultData.getString("message");
            int downloadIndex = resultData.getInt("index");
            KLog.i("hsing", "downloadMaterial: " + downloadIndex + " resultCode " + resultCode);
            if (activity != null && activity.get() != null && activity.get().smallRecordViewList != null) {
                switch (resultCode) {
                    case FileDownload.RESULT_PREPARE:
                        activity.get().downloadStateList.set(downloadIndex, SelectFrameActivity.DOWNLOAD_STATE_DOWNLOADING);
                        activity.get().smallRecordViewList.get(downloadIndex).showProgress();
                        break;
                    case FileDownload.RESULT_PAUSE:
                        activity.get().dismissLoad();
                        break;
                    case FileDownload.RESULT_DOWNLOADING:
                        int progress = (int) resultData.getFloat("percent", 0f);
                        activity.get().smallRecordViewList.get(downloadIndex).setProgress(progress);
                        break;
                    case FileDownload.RESULT_ERROR:
                        activity.get().showToast(message);
                        activity.get().downloadStateList.set(downloadIndex, SelectFrameActivity.DOWNLOAD_STATE_ERROR);
                        activity.get().smallRecordViewList.get(downloadIndex).showMaterialCover(false);
//                        activity.get().refreshView(false);
                        break;
                    case FileDownload.RESULT_COMPLETE:
                        activity.get().downloadStateList.set(downloadIndex, SelectFrameActivity.DOWNLOAD_STATE_DOWNLOADED);
                        DownloadBean downloadBean = resultData.getParcelable("downloadBean");
                        if (null != downloadBean) {
                            if (RecordManager.get().getProductEntity() == null
                                    || RecordManager.get().getProductEntity().shortVideoList == null
                                    || downloadIndex < 0
                                    || downloadIndex >= RecordManager.get().getProductEntity().shortVideoList.size()) {
                                activity.get().toastFinish();
                            }
                            String savePath = downloadBean.wholePathName;
                            KLog.i("hsing", "下载视频" + downloadIndex + "完成，savePath" + savePath);
                            ShortVideoEntity shortVideoEntity = RecordManager.get().getShortVideoEntity(downloadIndex);
                            shortVideoEntity.importVideoPath = savePath;
                            UploadMaterialEntity materialEntity = activity.get().findMaterial(downloadIndex);
                            if (materialEntity != null) {
                                // 设置素材原始Id
                                shortVideoEntity.originalId = materialEntity.ori_id;
                                if (materialEntity.ori_id == 0) {
                                    shortVideoEntity.originalId = materialEntity.id;
                                }
                            }
                            String combineVideoAudio = RecordFileUtil.createVideoFile(RecordManager.get().getShortVideoEntity(downloadIndex).baseDir, String.valueOf(downloadIndex));
                            shortVideoEntity.combineVideoAudio = combineVideoAudio;
                            shortVideoEntity.coverUrl = materialEntity.material_cover;
                            KLog.i("======editingVideoPath ：" + combineVideoAudio);
                            if (!TextUtils.isEmpty(shortVideoEntity.combineVideoAudio)) {//&& !TextUtils.isEmpty(shortVideoEntity.editingVideoPath
                                try {
                                    FileUtil.copy(shortVideoEntity.importVideoPath, combineVideoAudio);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        activity.get().smallRecordViewList.get(downloadIndex).showMaterialCover(true);
//                        activity.get().refreshView(false);
                        break;
                    case FileDownload.RESULT_COMPLETE_ALL://下载完了，合并，
                        //尽量不要 activity.get().
                        activity.get().downloadComplete();
//                        activity.get().recordActivitySdkVideoHelper.transformAudio2to1(RecordManager.get().getProductEntity().shortVideoList);//转换声道
//                        activity.get().recordActivitySdkView.refreshView(activity.get().getCurrentPreviewIndex());
                        break;
                    default:
                        break;
                }
            }

        }
    }

    private void downloadComplete() {
        if (recordOptionPanel != null)
            recordOptionPanel.setFrameEnable(true);
        if (RecordManager.get().getProductEntity() == null)//异步下载，
            return;
        recordActivitySdkVideoHelper.transformAudio2to1(RecordManager.get().getProductEntity().shortVideoList);
    }

    /**
     * 根据素材位置得到素材
     *
     * @param materialIndex
     * @return
     */
    public UploadMaterialEntity findMaterial(int materialIndex) {
        if (materials != null && materials.size() > 0) {
            for (int i = 0, size = materials.size(); i < size; i++) {
                UploadMaterialEntity materialEntity = materials.get(i);
                if (materialEntity != null && materialEntity.material_index == materialIndex) {
                    return materialEntity;
                }
            }
        }
        return null;
    }

    private void initVideoList() {
        downloadStateList = new ArrayList<>();
        if (mFrameInfo != null && mFrameInfo.getLayout() != null) {
            int size = mFrameInfo.getLayout().size();
            for (int i = 0; i < size; i++) {
                downloadStateList.add(SelectFrameActivity.DOWNLOAD_STATE_NONE);
            }
        }
    }

    /**
     * 获取当时的item index
     *
     * @return
     */
    public int getCurrentPreviewIndex() {
        return currentPreviewIndex;
    }

    class HeadSetReceiver extends BroadcastReceiver {

        private boolean isChanged = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("===yang", "onReceive");
            String action = intent.getAction();
            if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(action) || "android.intent.action.HEADSET_PLUG".equals(action)) {

                if (intent.hasExtra("state")) {
                    int i = intent.getIntExtra("state", -1);
                    if (i != -1 && isChanged) {
                        checkDCLoopbackTest(false);
                    }
                    isChanged = true;
                }
            }
        }
    }
}

