package com.wmlive.hhvideo.heihei.record.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.os.ResultReceiver;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.dc.platform.voicebeating.DCVoiceBeatingTool;
import com.dongci.sun.gpuimglibrary.api.DCCameraConfig;
import com.dongci.sun.gpuimglibrary.camera.CameraView;
import com.dongci.sun.gpuimglibrary.camera.GpuConfig;
import com.dongci.sun.gpuimglibrary.common.SLVideoProcessor;
import com.wmlive.hhvideo.common.base.BaseCompatActivity;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.heihei.beans.record.MvConfigItem;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.beans.recordmv.MvBgEntity;
import com.wmlive.hhvideo.heihei.beans.recordmv.MvConfig;
import com.wmlive.hhvideo.heihei.beans.recordmv.MvMaterialEntity;
import com.wmlive.hhvideo.heihei.beans.recordmv.MvTemplateEntity;
import com.wmlive.hhvideo.heihei.beans.recordmv.SingleTemplateBean;
import com.wmlive.hhvideo.heihei.quickcreative.ChooseStyle4QuickActivity;
import com.wmlive.hhvideo.heihei.quickcreative.CreativeQuickUtils;
import com.wmlive.hhvideo.heihei.quickcreative.MvMaterialManager;
import com.wmlive.hhvideo.heihei.quickcreative.RecordMvThreadManager;
import com.wmlive.hhvideo.heihei.quickcreative.VoiceBeatManager;
import com.wmlive.hhvideo.heihei.record.activitypresenter.RecordMvActivityHelper;
import com.wmlive.hhvideo.heihei.record.activityview.RecordMvActivityView;
import com.wmlive.hhvideo.heihei.record.adapter.CountDownAdapter;
import com.wmlive.hhvideo.heihei.record.adapter.FilterMirrorAdapter;
import com.wmlive.hhvideo.heihei.record.adapter.MvMaterialAdapter;
import com.wmlive.hhvideo.heihei.record.config.RecordMvKeys;
import com.wmlive.hhvideo.heihei.record.engine.DCRecorderBaseHelper;
import com.wmlive.hhvideo.heihei.record.engine.constant.SdkConstant;
import com.wmlive.hhvideo.heihei.record.engine.listener.VideoListener;
import com.wmlive.hhvideo.heihei.record.listener.RecordListener;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.manager.RecordSetting;
import com.wmlive.hhvideo.heihei.record.presenter.RecordMvPresenter;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtilSdk;
import com.wmlive.hhvideo.heihei.record.widget.CountdownView;
import com.wmlive.hhvideo.heihei.record.widget.ExtBtnRecord;
import com.wmlive.hhvideo.heihei.record.widget.LocateCenterHorizontalView;
import com.wmlive.hhvideo.heihei.record.widget.RecordMenuView;
import com.wmlive.hhvideo.heihei.record.widget.RecordOptionPanelMV;
import com.wmlive.hhvideo.heihei.record.widget.TextureVideoViewOutlineProvider;
import com.wmlive.hhvideo.utils.AppCacheFileUtils;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.FileUtil;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.utils.download.Download;
import com.wmlive.hhvideo.utils.download.DownloadBean;
import com.wmlive.hhvideo.utils.download.FileDownload;
import com.wmlive.hhvideo.utils.preferences.SPUtils;
import com.wmlive.hhvideo.widget.CustomFontTextView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

import static com.wmlive.hhvideo.heihei.record.activitypresenter.RecordMvActivityHelper.EXTRA_RECORD_TYPE;
import static com.wmlive.hhvideo.heihei.record.manager.RecordSetting.FILTER_LIST;


/**
 * 4.0 Mv 录制页面
 */
public class RecordMvActivity extends DcBaseActivity implements LocateCenterHorizontalView.OnSelectedPositionChangedListener,
        RecordOptionPanelMV.OnOptionClickListener, MvMaterialAdapter.OnFrameItemClickListener, RecordMenuView.OnMenuClickListener,
        CountDownAdapter.OnRecyclerViewItemClickListener, RecordListener, FilterMirrorAdapter.OnFilterClickListener, CountdownView.OnCountdownEndListener,
        RecordMvPresenter.IRecordView {

    private static final int TV_NEXT_ID = 1001014;
    @BindView(R.id.recordFilterSelector)
    public LocateCenterHorizontalView recordFilterSelector;
    @BindView(R.id.recordOptionPanelMv)
    public RecordOptionPanelMV recordOptionPanelMV;
    @BindView(R.id.sdkview_framelayout)
    public FrameLayout playerContainer;
    @BindView(R.id.btRecorderMv)
    public ExtBtnRecord extBtnRecord;
    @BindView(R.id.countdownViewMv)
    public CountdownView countdownView;
    @BindView(R.id.llSpeedPanel)
    public LinearLayout llSpeedPanel;
    @BindView(R.id.filter_layout)
    public RelativeLayout filterLayout;
    @BindView(R.id.act_record_mv_mvlist)
    RecyclerView mvRecyclerView;//列表view
    @BindView(R.id.can_not_click_mvlist_ll)
    public
    RelativeLayout canNotClickMvlistLl;
    @BindView(R.id.act_record_mv_preview)
    public RelativeLayout previewRelative;
    @BindView(R.id.count_down_rv)
    public RecyclerView countDownRv;
    @BindView(R.id.record_action_tips_tv)
    public CustomFontTextView recordActionTipsTv;
    @BindView(R.id.act_record_mv_anmi)
    public ImageView imgAnim;

    private FilterMirrorAdapter fiterSelectAdapter;
    private int selectedFilterId;//当前选中的滤镜
    private CountDownAdapter countDownAdapter;
    public RecordMenuView recordMenu;
    public TextView tvNext;

    //    private ConfigJsonBean configJsonBean;
    private ResultReceiver resultReceiver;
    private Handler handler;//下载handle
    /**
     * 上一个页面来源
     * 1.录制
     * 2.草稿箱
     * 3.替换
     */
    public int typeFrom;
    DCRecorderBaseHelper dcRecorderBaseHelper;
    public RecordMvActivityHelper recordMvActivityHelper;
    public RecordMvActivityView recordMvActivityView;
    public MvMaterialAdapter mvMaterialAdapter;

    MvMaterialManager mvMaterialManager;

    VoiceBeatManager voiceBeatManager;
    /**
     * 当前所在的素材的id
     */
    public static int currentIndex;
    private int countDownNum;//录制倒计时时间
    private long opusId;
    //    private String currentTemplateName;
    private ArrayList<DownloadBean> downloadList;
    private int PREVIEW_CODE = 0000001;


    /**
     * 正常启动
     *
     * @param ctx
     * @param recordType
     * @param opusId
     */
    public static void startRecordMv(final BaseCompatActivity ctx, final int recordType, final long opusId) {
        RecordMvActivityHelper.startRecordActivity(ctx, recordType, opusId);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_record_mv;
    }

    @Override
    protected RecordMvPresenter getPresenter() {
        return new RecordMvPresenter(this);
    }

    @Override
    protected void initData() {
        super.initData();
        initProduct();
        initIntentData();
        initToolbarMenu();
        initView();
        initRecordHelper();
        initDifFrom();
        initFilterAndCountDown();

    }


    /**
     * 初始化作品实体类
     */
    private void initProduct() {
        RecordManager.get().getProductEntityMv(RecordMvActivityHelper.initDefaultFrame(MvConfig.MATERIALNUM)).getExtendInfo().productCreateType = RecordMvActivityHelper.TYPE_RECORD_MV;
        SPUtils.putInt(this, SPUtils.KEY_EDITING_STEP, RecordSetting.STEP_RECORD);
    }

    /**
     * 初始化上个activity传递的参数
     */
    private void initIntentData() {
        handler = new Handler(Looper.getMainLooper());
        opusId = getIntent().getLongExtra(RecordMvKeys.EXTRA_OPUS_ID, 0L);
        typeFrom = getIntent().getIntExtra(EXTRA_RECORD_TYPE, 0);
    }

    /**
     * 处理不同进入情况
     */
    private void initDifFrom() {
        switch (typeFrom) {
            case RecordMvActivityHelper.EXTRA_RECORD_TYPE_RECORD://录制
                handleTypeOfNormalRecrod();
                break;
            case RecordMvActivityHelper.EXTRA_RECORD_TYPE_DRAFT://草稿箱
                handleTypeOfDraft();
                break;
            case RecordMvActivityHelper.EXTRA_RECORD_TYPE_REPLACE://替换,踹轨
                handleTypeOfReplace();
                break;
            case RecordMvActivityHelper.EXTRA_RECORD_TYPE_USE_CURENT_TEMPLATE://使用该模板进行创作
                handleTypeOfUseCurentTemplate();
                break;
        }
    }

    /**
     * 从正常录制进入
     */
    private void handleTypeOfNormalRecrod() {
        //取默认模板
        String template_name = SPUtils.getString(this, SPUtils.CREATIVE_DEFALT_TEMPLATE_NAME, "");

        recordMvActivityHelper.readConfig(template_name);
        //Todo
        showMvList(RecordManager.get().getProductEntity().shortVideoList, RecordMvActivityHelper.configlist);
        showRecordTips();
    }

    /**
     * 从草稿箱进入
     */
    private void handleTypeOfDraft() {
        String template_name = RecordManager.get().getProductEntity().getExtendInfo().template_name;
        recordMvActivityHelper.readConfigOnly(template_name);
        recordMvActivityHelper.createConfigList(RecordManager.get().getProductEntity().shortVideoList.size());

        showMvList(RecordManager.get().getProductEntity().shortVideoList, RecordMvActivityHelper.configlist);
        showRecordTips();
    }

    /**
     * 替换或者踹轨进入
     */
    private void handleTypeOfReplace() {

        resultReceiver = new VideoReceiver(this, handler);
        getPresenter().getOpusMaterial(opusId);
    }

    /**
     * 使用当前模板创作进入
     */
    private void handleTypeOfUseCurentTemplate() {
        RecordMvActivityHelper.createMaterialList(MvConfig.MATERIALNUM);
        downloadList = new ArrayList<>();
        showMvList(RecordManager.get().getProductEntity().shortVideoList, RecordMvActivityHelper.configlist);
        resultReceiver = new VideoReceiver(this, handler);
        getPresenter().getOpusTemplate(opusId, "1");
    }

    /**
     * 将数据设置到控件中
     */
    private void initView() {
        recordMvActivityView = new RecordMvActivityView(this);
        recordOptionPanelMV.setOptionClickListener(this);
        extBtnRecord.setLongListener(recordLongListener);
        canNotClickMvlistLl.setOnClickListener(this);
    }

    /**
     * 初始化滤镜和倒计时控件
     */
    private void initFilterAndCountDown() {
        fiterSelectAdapter = new FilterMirrorAdapter(FILTER_LIST, 1000);
        fiterSelectAdapter.setOnFilterClickListener(this);
        recordFilterSelector.setLayoutManager(new LinearLayoutManager(RecordMvActivity.this, LinearLayoutManager.HORIZONTAL, false));
        recordFilterSelector.setAdapter(fiterSelectAdapter);
        recordFilterSelector.setOnSelectedPositionChangedListener(this);
        //倒计时选择列表
        countDownAdapter = new CountDownAdapter(this);
        countDownRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        countDownRv.setAdapter(countDownAdapter);
        countDownAdapter.setOnItemClickListener(this);
        //倒计时控制
        countdownView.setCountdownEndListener(this);

    }

    /**
     * 初始化顶部导航栏
     */
    private void initToolbarMenu() {
        setTitle("", true);
        tvNext = new TextView(this);
        tvNext.setId(TV_NEXT_ID);
        tvNext.setText("预览");
        tvNext.setTextSize(16);
        tvNext.setTextColor(getResources().getColor(R.color.green));
        TypedValue tv = new TypedValue();
        tvNext.setBackgroundResource(tv.resourceId);
        tvNext.setGravity(Gravity.CENTER);
        tvNext.setPadding(10, 6, DeviceUtils.dip2px(this, 15), 6);
        recordMenu = new RecordMenuView(this);
        recordMenu.setMenuClickListener(this);
        setBlackToolbar();
        setToolbarRightView(tvNext, this);
        setToolbarCenterView(recordMenu, null);
    }

    /**
     * 初始化录制Helper类
     */
    private void initRecordHelper() {
        mvMaterialManager = new MvMaterialManager();
        voiceBeatManager = new VoiceBeatManager();

        dcRecorderBaseHelper = new DCRecorderBaseHelper(this);
        dcRecorderBaseHelper.setRecordListener(this);
        dcRecorderBaseHelper.initRecorderConfig(recordMenu);
        dcRecorderBaseHelper.selectCamera(previewRelative);
        recordMvActivityHelper = new RecordMvActivityHelper(this);

        CameraView preview = (CameraView) previewRelative.getChildAt(0);
        preview.setOutlineProvider(new TextureVideoViewOutlineProvider(30));
        preview.setClipToOutline(true);
    }


    /**
     * 显示mv列表
     *
     * @param list
     */
    private void showMvList(List<ShortVideoEntity> list, List<MvConfigItem> configList) {
        mvMaterialAdapter = new MvMaterialAdapter(list, configList);
        GridLayoutManager layoutManage = new GridLayoutManager(this, configList.size());
        mvRecyclerView.setLayoutManager(layoutManage);
        mvRecyclerView.setAdapter(mvMaterialAdapter);
        mvMaterialAdapter.setItemClickListener(this);
    }

    private void showMvList(int count) {
        RecordMvActivityHelper.createMaterialList(count);
        RecordMvActivityHelper.createConfigList(count);
        showMvList(RecordManager.get().getProductEntity().shortVideoList, RecordMvActivityHelper.configlist);
    }


    @Override
    protected void onSingleClick(View v) {
        switch (v.getId()) {
            case TV_NEXT_ID:
                handleClickNextEvent();
                break;
            case R.id.btRecorderMv:
                handleClickRecordEvent();
                break;
        }
    }

    /**
     * 处理点击下一步按钮事件
     */
    private void handleClickNextEvent() {
        if (RecordMvActivityHelper.checkNext()) {//没有录制视频，
            splitMetarialVedioAudio();
            //视频转成图片
        } else {
            RecordMvActivityView.showRecordMvToPreviewDialog(this);
        }
    }

    /**
     * 判断 异步线程 是否完成
     */
    private void checkThreadToNext(){
        //有则分离，没有不需要分离，
        RecordMvThreadManager recordMvThreadManager = new RecordMvThreadManager();
        recordMvThreadManager.init(voiceBeatManager,mvMaterialManager);
        if(recordMvThreadManager.hasProcessing()){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recordMvActivityView.showDialog(R.string.loading);
                }
            });
            recordMvThreadManager.addListener(new RecordMvThreadManager.RecordMvThreadManagerListener(){

                @Override
                public void onProgress(int index, int progress) {

                }

                @Override
                public void onFinish(int code) {
                    KLog.i("finish-->");
                }

                @Override
                public void onFinishAll() {
                    KLog.i("finish--All>");
                    recordMvActivityView.dismissDialog();
                    RecordManager.get().updateProduct();
                    startActivityForResult(new Intent(RecordMvActivity.this, ChooseStyle4QuickActivity.class), PREVIEW_CODE);

                }
            });
        } else {
            startActivityForResult(new Intent(RecordMvActivity.this, ChooseStyle4QuickActivity.class), PREVIEW_CODE);
        }
    }

    /**
     * 处理点击录制按钮事件
     */
    private void handleClickRecordEvent() {
        recordMvActivityView.onRecordView();
        if (countDownNum != 0) {
            recordMvActivityView.startCountDown(countDownNum);
        } else {
            startRecordMv();
        }
        recordMvActivityView.hidePanel();
    }

    @Override
    public void onBackPressed() {
        if (RecordManager.get().getProductEntity().hasMvVideo()) {
            RecordMvActivityView.showRecordMvBackDialog(this);
        } else {
            finish();
        }
    }

    /**
     * 开始录制,删除之前的视频文件,如果存在的话
     */
    private void startRecordMv() {
        ShortVideoEntity shortVideoEntityMv = RecordManager.get().getProductEntity().shortVideoList.get(currentIndex);
        FileUtil.deleteFile(shortVideoEntityMv.editingAudioPath);
        FileUtil.deleteFile(shortVideoEntityMv.editingVideoPath);
        dcRecorderBaseHelper.startRecord(shortVideoEntityMv);
    }

    /**
     * 录制倒计时
     */
    @Override
    public void onCountdownClick() {
        if (countDownRv.getVisibility() == View.VISIBLE) {
            countDownRv.setVisibility(View.INVISIBLE);
            recordActionTipsTv.setVisibility(View.VISIBLE);
        } else {
            filterLayout.setVisibility(View.INVISIBLE);
            recordActionTipsTv.setVisibility(View.INVISIBLE);
            countDownRv.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 倒计时选择器列表回调
     *
     * @param position
     * @param data
     */
    @Override
    public void onCountDownItemClick(int position, String data) {
        KLog.i("click--" + position + "data::>" + data);
        countDownNum = Integer.parseInt(data);
    }

    /**
     * 倒计时开始
     */
    @Override
    public void onCountdownStart() {
        recordMvActivityView.hidePanel();
    }

    /**
     * 倒计时结束
     */
    @Override
    public void onCountdownEnd() {
        recordMvActivityView.countDownEnd();
        startRecordMv();
    }

    /**
     * 倒计时取消
     */
    @Override
    public void onCountdownCancel() {
        recordMvActivityView.countDownCancel();
    }

    /**
     * 滤镜选择回调
     *
     * @param pos
     */
    @Override
    public void selectedPositionChanged(int pos) {
        RecordManager.get().getShortVideoEntity(currentIndex).setFilterId(pos);
        dcRecorderBaseHelper.switchFilter(pos % FILTER_LIST.size());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlPreview:
                recordMvActivityView.hidePanel();
                showRecordTips();
                break;
        }
        super.onClick(v);
    }

    /**
     * 打开,关闭滤镜选择器
     */
    @Override
    public void onFilterClick() {
        filterLayout.setVisibility(filterLayout.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
        if (filterLayout.getVisibility() == View.VISIBLE) {
            countDownRv.setVisibility(View.INVISIBLE);
            recordActionTipsTv.setVisibility(View.INVISIBLE);
//            int position = RecordManager.get().getShortVideoEntity(currentIndex).getFilterId();
            if (selectedFilterId == 0) {
                recordFilterSelector.moveToPosition(FILTER_LIST.size() * 40);
            } else {
                recordFilterSelector.moveToPosition(selectedFilterId);
            }
        } else {
            recordActionTipsTv.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 滤镜点击选择回调
     *
     * @param position
     */
    @Override
    public void onFilterItemClick(int position) {
        recordFilterSelector.moveToPosition(position);
        selectedFilterId = position;
    }

    boolean lastPlay = false;

    @Override
    public void onFrameItemClick(ShortVideoEntity shortVideoEntityMv, int position) {
        //选中后开始录制
        KLog.i(currentIndex + "click->" + position);
        ShortVideoEntity shortTemp = RecordManager.get().getProductEntity().shortVideoList.get(currentIndex);
        KLog.i(currentIndex + "click-Frame>" + shortTemp.editingVideoPath);

        currentIndex = position;

        //更新ui状态,如果已经录制则显示进度,没有录制显示未0.
        showRecordTips();
        recordMvActivityView.hidePanel();
//        if (shortVideoEntityMv.hasMvVideo()) {
//            recordOptionPanelMV.getProgressBar().setProgress((int) (recordMvActivityHelper.mvConfig.duration * 1000));
//        } else {
//            recordOptionPanelMV.getProgressBar().setProgress(0);
//        }
        lastPlay = false;
    }

    @Override
    public void onFrameItemClickPlay(ShortVideoEntity shortVideoEntityMv, int position, ImageView playerIv) {
        //播放该视频
        KLog.i(currentIndex + "click-player>" + position + shortVideoEntityMv.hasVideo());
//        ShortVideoEntity shortTemp = RecordManager.get().getProductEntity().shortVideoList.get(currentIndex);
        KLog.i(currentIndex + "click-Frame>");
        recordMvActivityHelper.measurePreviewHeight(previewRelative);
        KLog.i("click-player>" + position);
        if (shortVideoEntityMv.hasMvVideo()) {//如果有视频
            if (lastPlay) {
                recordMvActivityView.playVideo(playerIv);
            } else {
                recordMvActivityView.playeVideo(shortVideoEntityMv, playerIv);
            }

        }
        lastPlay = true;
    }

    /**
     * 点击重试下载当前素材
     *
     * @param shortVideoEntityMv
     * @param positon
     */
    @Override
    public void onReloadingClick(ShortVideoEntity shortVideoEntityMv, int positon) {

        //开始下载
        DownloadBean downloadBean = downloadList.get(positon);
        RecordMvActivityHelper.configlist.get(downloadBean.index).state = 1;
        setMVListData(downloadBean.index);
        FileDownload.start(this, downloadBean, resultReceiver, true, true);
    }

    /**
     * 美颜效果开关
     */
    @Override
    public void onBeautyClick() {
        dcRecorderBaseHelper.beautyClick();
    }

    /**
     * 闪光灯开关
     */
    @Override
    public void onFlashClick() {
        dcRecorderBaseHelper.flashClick();
    }

    /**
     * 摄像头切换
     */
    @Override
    public void onToggleClick() {
        dcRecorderBaseHelper.switchClick();
    }


    @Override
    public void onProgress(int progress) {
        KLog.i("record-progress" + progress);
        //正在录制
        if (recordMvActivityHelper.mvConfig.duration * 1000 < progress) {//结束录制
            boolean result = dcRecorderBaseHelper.stopRecord();
            if (result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recordMvActivityView.showDialog(R.string.video_doing);
                    }
                });
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recordOptionPanelMV.getProgressBar().setProgress(progress);
                }
            });
        }
    }

    @Override
    public void onRecordEnd(int result, String videoPath, String audioPath) {
        KLog.i("record---end" + result + videoPath);
        if (result == DCCameraConfig.RECORD_PRE) {//预停止
            recordMvActivityView.handler.sendEmptyMessageDelayed(1, 300);
            return;
        } else if (result == DCCameraConfig.SUCCESS) {//正常停止
            recordMvActivityView.handler.removeMessages(1);
        }


        lastPlay = false;//用于重复播放
        ShortVideoEntity shortVideoEntityMv = RecordManager.get().getProductEntity().shortVideoList.get(currentIndex);

        //删除之前的数据
        shortVideoEntityMv.deleteMvFile();
        //保存录制的数据
        shortVideoEntityMv.combineVideoAudio = "";
        shortVideoEntityMv.editingAudioPath = audioPath;
        shortVideoEntityMv.editingVideoPath = videoPath;
        voiceBeatManager.start(currentIndex+1,shortVideoEntityMv);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recordMvActivityView.onStopRecordView();
                recordMvActivityView.dismissDialog();
                recordOptionPanelMV.getProgressBar().setProgress(recordOptionPanelMV.getProgressBar().getDuration());
                recordOptionPanelMV.getProgressBar().setProgress(0);
            }
        });
        //处理数据，更新ui
        mvMaterialManager.start(shortVideoEntityMv);
        shortVideoEntityMv.setCoverUrl(recordMvActivityHelper.getSnapShot(videoPath));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                KLog.i("click--enable--true");
                mvMaterialAdapter.notifyDataSetChanged();
//                Animation scale = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.record_mv_img);//获取平移缩放动画资源
//                imgAnim.startAnimation(scale);
//                imgAnim.setVisibility(View.VISIBLE);
//                imgAnim.setImageURI(Uri.parse(shortVideoEntityMv.getCoverUrl()));
            }
        });
        shortVideoEntityMv.originalId = 0;//原创视频
        onRecordEndUpdate(result);
    }

    private void onRecordEndUpdate(int result) {
        if (result >= DCCameraConfig.SUCCESS) {
            RecordManager.get().updateProduct();
        }
    }

    /**
     * 展示录制素材提示语
     */
    private void showRecordTips() {
        if (recordMvActivityHelper.getConfigJsonBean() != null) {
            recordActionTipsTv.setVisibility(View.VISIBLE);
            recordActionTipsTv.setText(recordMvActivityHelper.getConfigJsonBean().getItems().get(currentIndex).getTips());
            recordActionTipsTv.setTextSize(14);
        }
    }

    /**
     * 踹轨进来获取素材
     *
     * @param response
     */
    @Override
    public void onGetMaterial(MvMaterialEntity response) {
        //保存数据
        recordMvActivityHelper.setCurrentTemplateName(response.template.template_name);
        //转化数据 为 需要使用的，
        RecordMvActivityHelper.createConfigList(response.materials.size());
        RecordMvActivityHelper.createMaterialListWidthId(response.materials);
        //展示数据
        setMVListData();

        downloadList = new ArrayList<>();
        ArrayList<DownloadBean> metrialList = recordMvActivityHelper.createMetrialDownload(response);
        downloadList.addAll(metrialList);

        MvTemplateEntity mvTemplateEntity = response.template;
        MvBgEntity mvBgEntity = response.bg;
        ArrayList<DownloadBean> list = recordMvActivityHelper.createTemplateDownload(mvTemplateEntity, mvBgEntity);
        downloadList.addAll(list);
        //下载任务开启
        FileDownload.start(this, downloadList, resultReceiver, true);
    }

    /**
     * 使用该模板创作进来获取模板
     *
     * @param response
     */
    @Override
    public void onGetTemplate(SingleTemplateBean response) {
        RecordManager.get().getProductEntity().getExtendInfo().template_name = response.template.template_name;
        RecordManager.get().getProductEntity().getExtendInfo().template_name = response.bg.bg_name;
        recordMvActivityHelper.setCurrentTemplateName(response.template.template_name);

        MvTemplateEntity mvTemplateEntity = response.template;
        MvBgEntity mvBgEntity = response.bg;

        ArrayList<DownloadBean> downloadList = recordMvActivityHelper.createTemplateDownload(mvTemplateEntity, mvBgEntity);
        FileDownload.start(this, downloadList, resultReceiver, true);
    }


    private static class VideoReceiver extends ResultReceiver {
        private WeakReference<RecordMvActivity> activity;

        @SuppressLint("RestrictedApi")
        public VideoReceiver(RecordMvActivity activity, Handler handler) {
            super(handler);
            this.activity = new WeakReference<>(activity);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String message = resultData.getString("message");
            DownloadBean downloadBean = resultData.getParcelable("downloadBean");
            KLog.i("TESTTAG", "downloadMaterial: " + downloadBean + " resultCode " + resultCode + message);
            if (activity != null && activity.get() != null) {
                switch (resultCode) {
                    case FileDownload.RESULT_PREPARE:
                        break;
                    case FileDownload.RESULT_PAUSE:
                        break;
                    case FileDownload.RESULT_DOWNLOADING:
                        break;
                    case FileDownload.RESULT_ERROR:
                        activity.get().handleDownError(downloadBean);
                        break;
                    case FileDownload.RESULT_COMPLETE:
                        activity.get().handleDownSuccess(downloadBean);
                        break;
                    case FileDownload.RESULT_COMPLETE_ALL:
                        break;
                }
            }
        }
    }

    /**
     * 分离素材的视频和音频
     */
    private void splitMetarialVedioAudio() {
        KLog.i("splitMetarialVedioAudio--start");
        RecordUtilSdk.split(new VideoListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onFinish(int code, String outpath) {
                KLog.i("splitMetarialVedioAudio--onFinish");
                KLog.i("RecordMvActivity", " RecordUtilSdk.split: " + outpath);
                if (code == SdkConstant.RESULT_SUCCESS) {
                    //如果有的音频没有 预处理，需要进行处理
                    voiceBeatManager.checkAllData(RecordManager.get().getProductEntity().shortVideoList);
                    checkThreadToNext();
                } else {
                    ToastUtil.showToast("视频处理失败，请重试");
                }

            }

            @Override
            public void onError() {
                KLog.i("RecordMvActivity", " RecordUtilSdk.split: " + "error");
                ToastUtil.showToast("视频处理失败，请重试");
            }
        });
    }

    /**
     * 下载失败回调处理
     *
     * @param downloadBean
     */
    private void handleDownError(DownloadBean downloadBean) {
        if (downloadBean != null) {
            String savePath = downloadBean.wholePathName;
            if (!TextUtils.isEmpty(savePath) && savePath.endsWith(".zip")) {
                RecordMvActivityView.showTempDownloadFailDialog(this, downloadBean, resultReceiver);
            } else {
                int index = downloadBean.index;
                RecordMvActivityHelper.configlist.get(index).state = 0;
                setMVListData(index);
            }
        }
    }

    /**
     * 下载成功回调处理
     *
     * @param downloadBean
     */
    private void handleDownSuccess(DownloadBean downloadBean) {
        if (downloadBean != null) {
            String savePath = downloadBean.wholePathName;
            if (!TextUtils.isEmpty(savePath) && savePath.endsWith(".zip")) {
                CreativeQuickUtils.doUnzip(savePath, AppCacheFileUtils.getAppCreativePath());
                recordMvActivityHelper.readConfig(recordMvActivityHelper.getCurrentTemplateName());

                showRecordTips();
                showMvList(RecordManager.get().getProductEntity().shortVideoList, RecordMvActivityHelper.configlist);
                setMVListData();
            } else {
                int index = downloadBean.index;
                //保存数据
                RecordMvActivityHelper.configlist.get(index).state = 2;
                RecordManager.get().getProductEntity().shortVideoList.get(index).combineVideoAudio = downloadBean.wholePathName;

//                展示数据，控件
                setMVListData(index);
                recordMvActivityView.setNextEnable(true);

                //处理数据
                //将视频进行分离
                mvMaterialManager.start(RecordManager.get().getShortVideoEntity(index));

            }
        }
    }

    /**
     * 将数据设置到 mvlist
     */
    private void setMVListData(int index) {
        if (mvMaterialAdapter != null)
            mvMaterialAdapter.notifyItemChanged(index);
    }

    public void setMVListData() {
        if (mvMaterialAdapter != null)
            mvMaterialAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PREVIEW_CODE) {
//            读数据
            recordMvActivityHelper.readConfig(RecordManager.get().getProductEntity().getExtendInfo().template_name);

            showMvList(RecordManager.get().getProductEntity().shortVideoList, RecordMvActivityHelper.configlist);
        }
    }

    @Override
    protected void onDestroy() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        Download.pause(this);
        FileDownload.pause(this);
        currentIndex = 0;
        GpuConfig.context = null;
        super.onDestroy();
    }

    //录制按钮的监听
    private ExtBtnRecord.onLongListener recordLongListener = new ExtBtnRecord.onLongListener() {
        //录制按下
        @Override
        public void onActionDown() {
            KLog.i("recordLongListener--onActionDown");
        }

        //录制开始
        @Override
        public void onBegin() {

        }

        //录制结束
        @Override
        public void onEnd() {

        }

        //录制抬起 (单击录制 结束)
        @Override
        public void onActionUp(boolean isClickRecord) {
            KLog.i("recordLongListener--onActionUp");
            handleClickRecordEvent();
        }
    };
}
