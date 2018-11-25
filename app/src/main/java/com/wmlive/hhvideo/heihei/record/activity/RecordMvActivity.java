package com.wmlive.hhvideo.heihei.record.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.os.ResultReceiver;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dongci.sun.gpuimglibrary.api.DCCameraConfig;
import com.dongci.sun.gpuimglibrary.camera.CameraView;
import com.dongci.sun.gpuimglibrary.player.script.DCScriptManager;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.BaseCompatActivity;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.heihei.beans.opus.UploadMaterialEntity;
import com.wmlive.hhvideo.heihei.beans.quickcreative.CreativeTemplateBean;
import com.wmlive.hhvideo.heihei.beans.record.MvConfigItem;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.beans.record.TopicInfoEntity;
import com.wmlive.hhvideo.heihei.beans.recordmv.MvConfig;
import com.wmlive.hhvideo.heihei.beans.recordmv.MvMaterialEntity;
import com.wmlive.hhvideo.heihei.beans.recordmv.SingleTemplateBean;
import com.wmlive.hhvideo.heihei.discovery.activity.SearchVideoActivity;
import com.wmlive.hhvideo.heihei.quickcreative.MvMaterialManager;
import com.wmlive.hhvideo.heihei.quickcreative.RecordMvThreadManager;
import com.wmlive.hhvideo.heihei.quickcreative.TemplaterManager;
import com.wmlive.hhvideo.heihei.quickcreative.VoiceBeatManager;
import com.wmlive.hhvideo.heihei.record.activitypresenter.RecordMvActivityHelper;
import com.wmlive.hhvideo.heihei.record.activityview.RecordMvActivityView;
import com.wmlive.hhvideo.heihei.record.adapter.CountDownAdapter;
import com.wmlive.hhvideo.heihei.record.adapter.FilterMirrorAdapter;
import com.wmlive.hhvideo.heihei.record.adapter.MvMaterialAdapter;
import com.wmlive.hhvideo.heihei.record.config.RecordMvKeys;
import com.wmlive.hhvideo.heihei.record.engine.DCRecorderBaseHelper;
import com.wmlive.hhvideo.heihei.record.listener.RecordListener;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.presenter.RecordMvPresenter;
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
import com.wmlive.hhvideo.utils.ScreenUtil;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.utils.download.Download;
import com.wmlive.hhvideo.utils.download.DownloadBean;
import com.wmlive.hhvideo.utils.download.FileDownload;
import com.wmlive.hhvideo.utils.preferences.SPUtils;
import com.wmlive.hhvideo.widget.CustomFontTextView;
import com.wmlive.networklib.entity.EventEntity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

import static com.wmlive.hhvideo.heihei.record.activitypresenter.RecordMvActivityHelper.EXTRA_BG_NAME;
import static com.wmlive.hhvideo.heihei.record.activitypresenter.RecordMvActivityHelper.EXTRA_RECORD_TYPE;
import static com.wmlive.hhvideo.heihei.record.activitypresenter.RecordMvActivityHelper.EXTRA_TEMPLATE_NAME;
import static com.wmlive.hhvideo.heihei.record.activitypresenter.RecordMvActivityHelper.configlist;
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
    public RelativeLayout canNotClickMvlistLl;
    @BindView(R.id.act_record_mv_preview)
    public RelativeLayout previewRelative;
    @BindView(R.id.count_down_rv)
    public RecyclerView countDownRv;
    @BindView(R.id.record_action_tips_tv)
    public CustomFontTextView recordActionTipsTv;
    @BindView(R.id.act_record_mv_anmi)
    public ImageView imgAnim;
    @BindView(R.id.icon_prompt_left)
    public ImageView iconPrompt;
    @BindView(R.id.tvUpload)
    public TextView tvUpload;

    @BindView(R.id.llUpload_mv)
    public LinearLayout llUpload;

    @Override
    public void onRequestDataError(int requestCode, String message) {
        ToastUtil.showToast(R.string.nonet);
        finish();
    }

    public FilterMirrorAdapter fiterSelectAdapter;
    private int selectedFilterId;//当前选中的滤镜
    public CountDownAdapter countDownAdapter;
    public RecordMenuView recordMenu;
    public TextView tvNext;
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
    private LinearLayoutManager layoutManager;
    private int space = 12;
    private SpacesItemDecoration spacesItemDecoration;
    //显示item个数
    private static final int COUNTS = 6;

    public MvMaterialManager mvMaterialManager;

    public VoiceBeatManager voiceBeatManager;
    /**
     * 当前所在的素材的id
     */
    public static int currentIndex;
    private int countDownNum = 0;//录制倒计时时间
    private long opusId;
    private ArrayList<DownloadBean> downloadList;
    public int PREVIEW_CODE = 0000001;

    public RecordMvThreadManager recordMvThreadManager;

    private int templateAndBgCount = -1;

    private int templateAndBgCountTemp = 0;


    List<UploadMaterialEntity> materials;//踹轨素材

    public static final String PATH_FROM_LOCALALBUM = "path_from_localalbum";
    public static final String LENGTH_FROM_LOCALALBUM = "length_from_localalbum";

    String templateNameFromIntent;//上一个页面传递进来的模板名称
    String bgNameFromIntent;//上一个页面传递进来的背景

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
        initIntentData();
        initProduct();
        initView();
        initHelper();
        initDifFrom();
    }

    /**
     * 初始化作品实体类
     */
    private void initProduct() {
        RecordManager.get().getProductEntityMv(RecordMvActivityHelper.initDefaultFrame(MvConfig.MATERIALNUM)).getExtendInfo().productCreateType = RecordMvActivityHelper.TYPE_RECORD_MV;
        DCScriptManager.scriptManager().clearScripts();//防止没有清除脚本
        TopicInfoEntity topicInfo = getIntent().getParcelableExtra(RecordMvActivityHelper.EXTRA_TOPIC_INFO);
        if (topicInfo != null && !TextUtils.isEmpty(topicInfo.topicTitle)) {
            RecordManager.get().setTopicInfo(topicInfo);
        }
    }

    /**
     * 初始化上个activity传递的参数
     */
    private void initIntentData() {

        opusId = getIntent().getLongExtra(RecordMvKeys.EXTRA_OPUS_ID, 0L);
        typeFrom = getIntent().getIntExtra(EXTRA_RECORD_TYPE, 0);
        templateNameFromIntent = getIntent().getStringExtra(EXTRA_TEMPLATE_NAME);
        bgNameFromIntent = getIntent().getStringExtra(EXTRA_BG_NAME);
    }

    /**
     * 将数据设置到控件中
     */
    private void initView() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        recordMvActivityView = new RecordMvActivityView(this);
        initToolbarMenu();
        recordMvActivityView.initView();
    }

    private void initHelper() {
        handler = new Handler(Looper.getMainLooper());
        mvMaterialManager = new MvMaterialManager();
        voiceBeatManager = new VoiceBeatManager();
        recordMvActivityHelper = new RecordMvActivityHelper(this);
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
            case RecordMvActivityHelper.EXTRA_RECORD_TYPE_FROM_JUMP://所有跳转处理（h5，首页弹窗，参与话题等跳转处理）
                resultReceiver = new DownLoadReceiver(this, handler);
                String templateName = getIntent().getStringExtra(EXTRA_TEMPLATE_NAME);
                String bgName = getIntent().getStringExtra(EXTRA_BG_NAME);
                getPresenter().getTemplate(templateName, bgName);
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
        //将读取的数据显示到 列表，tips
        showMvList(RecordManager.get().getProductEntity().shortVideoList, configlist);
        recordMvActivityView.showRecordTips();
    }

    /**
     * 从草稿箱进入
     * 从数据库读取已经获取到该模板信息  1.根据模板读取配置信息，2.根据信息展示 tips，列表， 3.根据保存的数据展示到 列表
     */
    private void handleTypeOfDraft() {
        String template_name = RecordManager.get().getProductEntity().getExtendInfo().template_name;
        recordMvActivityHelper.readConfigOnly(template_name);
        recordMvActivityHelper.createConfigList(RecordManager.get().getProductEntity().shortVideoList.size());

        /**声音分析*/
        voiceBeatManager.checkAllData(RecordManager.get().getProductEntity().shortVideoList);
        //数据展示
        showMvList(RecordManager.get().getProductEntity().shortVideoList, configlist);
        recordMvActivityView.showRecordTips();
    }

    /**
     * 踹轨进入
     */
    private void handleTypeOfReplace() {
        resultReceiver = new DownLoadReceiver(this, handler);
        //去请求素材 详细信息
        getPresenter().getOpusMaterial(opusId);
        recordMvActivityView.showDialog(R.string.template_downloading);
    }


    /**
     * 使用当前模板创作进入
     */
    private void handleTypeOfUseCurentTemplate() {
        resultReceiver = new DownLoadReceiver(this, handler);
        getPresenter().getOpusTemplate(opusId, "1");
        recordMvActivityView.showDialog(R.string.template_downloading);
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
//        if(dcRecorderBaseHelper==null){
        dcRecorderBaseHelper = new DCRecorderBaseHelper(this);
        dcRecorderBaseHelper.setRecordListener(this);
        dcRecorderBaseHelper.initRecorderConfig(recordMenu);
//        dcRecorderBaseHelper.setPara();
//        }
        dcRecorderBaseHelper.selectCamera(previewRelative);
        CameraView preview = (CameraView) previewRelative.getChildAt(0);
        preview.setOutlineProvider(new TextureVideoViewOutlineProvider(30));
        preview.setClipToOutline(true);
    }


    boolean isRecyclerViewInit;
    ViewTreeObserver vto2;
    private int listSize;

    ViewTreeObserver.OnGlobalLayoutListener listener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            isRecyclerViewInit = true;
            modifySpace();
        }
    };

    RecyclerView.OnScrollListener scorllListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState != RecyclerView.SCROLL_STATE_DRAGGING || iconPrompt.getVisibility() != View.VISIBLE) {
                return;
            }

            iconPrompt.startAnimation(AnimationUtils.loadAnimation(RecordMvActivity.this, R.anim.anim_right_promt));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    iconPrompt.setVisibility(View.GONE);
                }
            }, 500);
        }
    };

    /**
     * 显示mv列表
     *
     * @param list
     */
    public void showMvList(List<ShortVideoEntity> list, List<MvConfigItem> configList) {
        if (configList != null) {
            listSize = configList.size();
            if (listSize > 6 && needShowPrompt) {
                KLog.i("===yang showMvList");
                iconPrompt.setVisibility(View.VISIBLE);
            }
        }

        if (mvMaterialAdapter == null) {
            mvMaterialAdapter = new MvMaterialAdapter(list, configList);
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            mvRecyclerView.setLayoutManager(layoutManager);
            mvRecyclerView.setAdapter(mvMaterialAdapter);
            mvMaterialAdapter.setItemClickListener(this);
            KLog.i("showMvList---?" + mvRecyclerView.getChildCount());

        } else {
            mvMaterialAdapter.setDataList(list, configList);
        }
        mvRecyclerView.addOnScrollListener(scorllListener);

        vto2 = mvRecyclerView.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(listener);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initRecordHelper();
        KLog.i("selectedFilterId==->" + selectedFilterId);
        //设置当前的 滤镜
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                selectedPositionChanged(selectedFilterId);
            }
        }, 100);

    }

    @Override
    protected void onPause() {
        super.onPause();
        dcRecorderBaseHelper.resetPreItem();
    }

    public void modifySpace() {
        if (!isRecyclerViewInit) {
            return;
        }
        int total = ScreenUtil.getWidth(this);
        //item宽度手动设置
        int firstVisibleItems1 = ((LinearLayoutManager) mvRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        // 真实Position就是position - firstVisibleItems[0]
        View childView = mvRecyclerView.getLayoutManager().findViewByPosition(firstVisibleItems1 + 1);
        if (childView == null)
            return;
        int itemWidth = childView.getWidth();

        if (configlist != null & configlist.size() > 6) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mvRecyclerView.getLayoutParams();
            layoutParams.rightMargin = 0;
            layoutParams.leftMargin = 0;
            int margin = layoutParams.rightMargin + layoutParams.leftMargin;
            mvRecyclerView.setLayoutParams(layoutParams);
            space = (total - margin - COUNTS * itemWidth) / (COUNTS - 1);
        } else if (configlist != null & configlist.size() <= 6) {
            space = (total - COUNTS * itemWidth) / (COUNTS - 1) - 12;
        }
        KLog.i("===yang modifySpace " + total + " | " + itemWidth + " | " + space);

        if (spacesItemDecoration != null) {
            mvRecyclerView.removeItemDecoration(spacesItemDecoration);
        }
        spacesItemDecoration = new SpacesItemDecoration(space);
        mvRecyclerView.addItemDecoration(spacesItemDecoration);

        if (mvRecyclerView.getViewTreeObserver().isAlive()) {
            mvRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }


    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {

            int childPosition = parent.getChildAdapterPosition(view);
            outRect.right = space;
            if (childPosition == listSize - 1) {
                outRect.right = 0;
            }

        }
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
            case R.id.tvUpload:
                float template_duration = RecordManager.get().getProductEntity().extendInfo.getTemplate_duration();
                template_duration*=1000;
                SearchVideoActivity.startSearchVideoActivity((BaseCompatActivity) this, currentIndex, SearchVideoActivity.TYPE_FROM_RECORDMV,(long)template_duration);
                break;

            case R.id.llUpload_mv://本地上传,点击跳转本地上传
//                SearchVideoActivity.startSearchVideoActivity();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocalUploadOkEvent(EventEntity entity) {
        KLog.d("clipVideo--onTrimOkEvent" + entity.code);
        if (entity.code == GlobalParams.EventType.TYPE_LOCAL_UPLOAD_MV) {//本地上传
            //返回 视频地址，
//            加载到 shortVideoEntity
//            setData();
//            entity.data;
//
        }
    }

    private String lastTemplate;
    private boolean needShowPrompt;

    /**
     * 处理点击下一步按钮事件
     */
    private void handleClickNextEvent() {
        lastTemplate = recordMvActivityHelper.getCurrentTemplateName();
        if (RecordMvActivityHelper.checkNext()) {//没有录制视频，
            recordMvActivityHelper.splitMetarialVedioAudio();
            //视频转成图片
        } else {
            RecordMvActivityView.showRecordMvToPreviewDialog(this);
        }
    }

    /**
     * 处理点击录制按钮事件
     */
    public void handleClickRecordEvent() {
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
        if (typeFrom == RecordMvActivityHelper.EXTRA_RECORD_TYPE_DRAFT) {
            recordMvActivityView.showDraftBoxDialog();
        } else {//非草稿箱
            if (RecordManager.get().getProductEntity() != null && RecordManager.get().getProductEntity().hasMvVideo()) {//存在视频
                RecordMvActivityView.showRecordMvBackDialog(this);
            } else {
                finish();
            }
        }
    }

    /**
     * 开始录制,删除之前的视频文件,如果存在的话
     */
    private void startRecordMv() {
        ShortVideoEntity shortVideoEntityMv = RecordManager.get().getProductEntity().shortVideoList.get(currentIndex);
        configlist.get(currentIndex).setState(MvConfigItem.STATE_RECORDING);
        recordMvActivityView.setMVListData(currentIndex);
        dcRecorderBaseHelper.startRecord(shortVideoEntityMv);
    }

    /**
     * 录制倒计时
     */
    @Override
    public void onCountdownClick() {
        recordMvActivityView.clickCountDown();
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
        recordOptionPanelMV.setCountDown(position);
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
        dcRecorderBaseHelper.switchFilter(pos % FILTER_LIST.size());
        RecordManager.get().getShortVideoEntity(currentIndex).setFilterId(pos);
        selectedFilterId = pos;
        dcRecorderBaseHelper.selectFilter = selectedFilterId;
        KLog.i("selectedFilterId--selectedPositionChanged" + selectedFilterId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlPreview:
                recordMvActivityView.hidePanel();
                recordMvActivityView.showRecordTips();
                break;
        }
        super.onClick(v);
    }

    /**
     * 打开,关闭滤镜选择器
     */
    @Override
    public void onFilterClick() {
        recordMvActivityView.onFilterClick();
        if (selectedFilterId == 0) {
            recordFilterSelector.moveToPosition(FILTER_LIST.size() * 40);
        } else {
            recordFilterSelector.moveToPosition(selectedFilterId);
        }
//      优化修改
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
        dcRecorderBaseHelper.selectFilter = selectedFilterId;
    }

    boolean lastPlay = false;

    @Override
    public void onFrameItemClick(ShortVideoEntity shortVideoEntityMv, int position) {
        //选中后开始录制
        currentIndex = position;

        //更新ui状态,如果已经录制则显示进度,没有录制显示未0.
        recordMvActivityView.showRecordTips();
        recordMvActivityView.hidePanel();
        lastPlay = false;
    }

    @Override
    public void onFrameItemClickPlay(ShortVideoEntity shortVideoEntityMv, int position, ImageView playerIv) {
        //播放该视频
        recordMvActivityHelper.measurePreviewHeight(previewRelative);
        if (shortVideoEntityMv.hasMvVideo()) {//如果有视频
            if (lastPlay) {
                recordMvActivityView.playVideo(playerIv);
            } else {
                recordMvActivityView.playeVideo(shortVideoEntityMv, playerIv);
            }
            //正在播放，录制按钮不可用
            recordMvActivityView.onRecordView();
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
        configlist.get(downloadBean.index).setState(MvConfigItem.STATE_DONWLOADING);
        recordMvActivityView.setMVListData(downloadBean.index);
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
                        recordMvActivityView.showDialog(0);
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
        if(RecordManager.get().getProductEntity()==null || RecordManager.get().getProductEntity().shortVideoList==null){
            ToastUtil.showToast("录制失败");
            return;
        }
        KLog.i("record---end" + result + videoPath);
        if (result == DCCameraConfig.RECORD_PRE) {//预停止
            recordMvActivityView.handler.sendEmptyMessageDelayed(1, 500);
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
        shortVideoEntityMv.setVideoType("2");//设置类型为本地录制
        shortVideoEntityMv.originalId = 0;//原创视频
        configlist.get(currentIndex).setState(MvConfigItem.STATE_RECORD_END);
        voiceBeatManager.start(currentIndex + 1, shortVideoEntityMv);//预处理声音
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recordMvActivityView.onStopRecordView();
                recordMvActivityView.dismissDialog();
                recordOptionPanelMV.getProgressBar().setProgress(0);
                //可以播放，
                recordMvActivityView.setMVListData(currentIndex);
            }
        });

        //处理数据 视频转图片
        mvMaterialManager.start(shortVideoEntityMv);
        //获取视频截图
        shortVideoEntityMv.setCoverUrl(recordMvActivityHelper.getSnapShot(videoPath));
        RecordManager.get().getProductEntity().setCoverPath(shortVideoEntityMv.getCoverUrl());
        //更新截图
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mvMaterialAdapter.notifyDataSetChanged();
//                动画
//                Animation scale = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.record_mv_img);//获取平移缩放动画资源
//                imgAnim.startAnimation(scale);
//                imgAnim.setVisibility(View.VISIBLE);
//                imgAnim.setImageURI(Uri.parse(shortVideoEntityMv.getCoverUrl()));
            }
        });
        RecordManager.get().updateProduct();
    }


    /**
     * 踹轨进来获取素材
     *
     * @param response
     */
    @Override
    public void onGetMaterial(MvMaterialEntity response) {
        KLog.i("onGetMaterial==--" + response);
        materials = response.materials;

        downloadTemplateZip(response.template.getTemplate_name(), response.template.getZip_path(), response.bg.getBg_name(), response.bg.getBg_resource());
    }


    /**
     * 网络请求 获取模板
     * <p>
     * 2.使用该模板创作
     * <p>
     * 准备去下载模板
     *
     * @param response
     */
    @Override
    public void onGetTemplate(SingleTemplateBean response) {
        if (response.bg == null) {
            ToastUtil.showToast("背景异常.");
            finish();
            return;
        }
        if (response.template == null) {
            ToastUtil.showToast("模板异常.");
            finish();
            return;
        }

        downloadTemplateZip(response.template.getTemplate_name(), response.template.getZip_path(), response.bg.getBg_name(), response.bg.bg_resource);

    }

    /**
     * 根据模板，背景名字获取 模板，背景
     *
     * @param response
     */
    @Override
    public void onGetTemlateSocal(CreativeTemplateBean response) {
        CreativeTemplateBean.TemplateBean template = response.getTemplate();
        CreativeTemplateBean.BgBean bg = response.getBg();
        downloadTemplateZip(template.getTemplate_name(), template.getZip_path(), bg.getBg_name(), bg.getBg_resource());
    }

    @Override
    public void onRequestError(int code, String msg) {

    }

    /**
     * 下载模板和背景zip
     *
     * @param template_name 模板名字
     * @param template_path 模板下载地址
     * @param bg_name       背景名字
     * @param bg_resource   背景下载地址
     */
    private void downloadTemplateZip(String template_name, String template_path, String bg_name, String bg_resource) {
        RecordManager.get().getProductEntity().getExtendInfo().template_name = template_name;
        RecordManager.get().getProductEntity().getExtendInfo().bg_name = bg_name;
        recordMvActivityHelper.setCurrentTemplateName(template_name);
        ArrayList<DownloadBean> downloadList = new ArrayList<>();
        if (FileUtil.isTemplateFileEmpty(template_name)) {
            String tempPath = template_path;
            int tempDownloadId = FileDownloadUtils.generateId(tempPath, AppCacheFileUtils.getAppCreativePath());
            DownloadBean tempDownload = new DownloadBean(tempDownloadId, tempPath, AppCacheFileUtils.getAppCreativePath(), "", "", DownloadBean.DOWNLOAD_ID_TEMPLATE);
            downloadList.add(tempDownload);
        } else {
            initTemplateAndShowData();
        }
        if (FileUtil.isTemplateFileEmpty(bg_name)) {
            String bgPath = bg_resource;
            int bgDownloadId = FileDownloadUtils.generateId(bgPath, AppCacheFileUtils.getAppCreativePath());
            DownloadBean bgDownload = new DownloadBean(bgDownloadId, bgPath, AppCacheFileUtils.getAppCreativePath(), "", "", DownloadBean.DOWNLOAD_ID_BG);
            downloadList.add(bgDownload);
        } else {

        }
        //下载任务开启
        if (downloadList.size() > 0) {
            if (typeFrom == RecordMvActivityHelper.EXTRA_RECORD_TYPE_REPLACE) {//设置最大数字
                templateAndBgCount = downloadList.size();
                templateAndBgCountTemp = 0;
            } else {//使用模板创作，不需要下载素材
                templateAndBgCount = -1;
            }
            KLog.d("", "downloadTemplateZip: templateAndBgCount=" + templateAndBgCount);
            FileDownload.start(this, downloadList, resultReceiver, true);
        } else {
            recordMvActivityView.dismissDialog();
        }
        //共同创作进来的去下载素材
        if (typeFrom == RecordMvActivityHelper.EXTRA_RECORD_TYPE_REPLACE && downloadList.size() == 0) {
            downLoadMaterial();
        }
    }


    public static class DownLoadReceiver extends ResultReceiver {

        private WeakReference<RecordMvActivity> activity;

        @SuppressLint("RestrictedApi")
        public DownLoadReceiver(RecordMvActivity activity, Handler handler) {
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
                        //隐藏正在下载的dialog
                        activity.get().recordMvActivityView.dismissDialog();
                        activity.get().recordMvActivityView.setMVListData();
                        break;
                }
            }
        }
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
                templateAndBgCountTemp++;
                RecordMvActivityView.showTempDownloadFailDialog(this, downloadBean, resultReceiver);
            } else {
                int index = downloadBean.index;
                if (configlist != null) {
                    configlist.get(index).setState(MvConfigItem.STATE_DONWLOAD_ERROR);
                    recordMvActivityView.setMVListData(index);
                }
            }
        }
    }

    /**
     * 下载成功回调处理
     *
     * @param downloadBean
     */
    private void handleDownSuccess(DownloadBean downloadBean) {
        if (downloadBean != null && RecordManager.get().getProductEntity()!=null) {
            String savePath = downloadBean.wholePathName;
            if (!TextUtils.isEmpty(savePath) && savePath.endsWith(".zip") && downloadBean.index == DownloadBean.DOWNLOAD_ID_TEMPLATE) {//模板下载
                KLog.i("模板下载成共--downloadSuccess--->");
                TemplaterManager.unZipTemplate(savePath);
                initTemplateAndShowData();
                //        //展示数据
                templateAndBgCountTemp++;
                if (templateAndBgCountTemp == templateAndBgCount) {//全部下载，结束
                    recordMvActivityView.dismissDialog();
                    if (typeFrom == RecordMvActivityHelper.EXTRA_RECORD_TYPE_REPLACE) {//两个都下载了
                        downLoadMaterial();
                    }
                }

            } else if ((!TextUtils.isEmpty(savePath) && savePath.endsWith(".zip") && downloadBean.index == DownloadBean.DOWNLOAD_ID_BG)) {//背景
                TemplaterManager.unZipTemplate(savePath);
                templateAndBgCountTemp++;
                if (templateAndBgCountTemp == templateAndBgCount) {//全部下载，结束
                    recordMvActivityView.dismissDialog();
                    if (templateAndBgCountTemp == templateAndBgCount) {//两个都下载了
                        downLoadMaterial();
                    }
                }

            } else {
                KLog.i("模板下载成共--downloadSuccess---素材>");
                int index = downloadBean.index;
                //更新数据数据
                setDownState(index, 1, downloadBean);
//                展示数据，控件
                recordMvActivityView.setMVListData(index);
                recordMvActivityView.setNextEnable(true);
                //处理数据
                //将视频进行分离
                mvMaterialManager.start(RecordManager.get().getShortVideoEntity(index));
            }
        }
    }

    /**
     * 初始化模板
     */
    private void initTemplateAndShowData() {
        //        //转化数据 为 需要使用的，
        recordMvActivityHelper.readConfig(recordMvActivityHelper.getCurrentTemplateName());
        recordMvActivityView.showRecordTips();
        //展示数据
        showMvList(RecordManager.get().getProductEntity().shortVideoList, configlist);

        //更新ui 列表宽度
        recordMvActivityView.handler.sendEmptyMessageDelayed(2, 100);
    }

    /**
     * 下载素材，踹轨进来
     */
    private void downLoadMaterial() {
        updateDownLoadState(); //异步下载素材，更新下载状态
        if (typeFrom == RecordMvActivityHelper.EXTRA_RECORD_TYPE_REPLACE) {
            downloadList = new ArrayList<>();
            ArrayList<DownloadBean> metrialList = recordMvActivityHelper.createMetrialDownload(materials);
            downloadList.addAll(metrialList);
            //下载任务开启mvMaterialAdapter
            FileDownload.start(this, downloadList, resultReceiver, true);
        }
    }


    /**
     * 设置下载状态
     *
     * @param index
     * @param state
     */
    private void setDownState(int index, int state, DownloadBean downloadBean) {
        String localPath = downloadBean.wholePathName;
        for (int i = 0; i < materials.size(); i++) {
            if (materials.get(i).material_index == index) {
                materials.get(i).downloadState = state;
                materials.get(i).fileDownloadPath = localPath;
                if (RecordManager.get().getProductEntity().shortVideoList != null) {
                    RecordManager.get().getProductEntity().shortVideoList.get(index).combineVideoAudio = localPath;
                }
                if (configlist != null) {
                    configlist.get(index).setState(MvConfigItem.STATE_DONWLOAD_FINISH);
                }

            }
        }
    }

    /**
     * 将下载的信息更新的 数据上
     */
    private void updateDownLoadState() {
        if (materials == null)
            return;
        for (int i = 0; i < materials.size(); i++) {
            UploadMaterialEntity materialEntity = materials.get(i);
            int index = materialEntity.material_index;
            if (RecordManager.get().getProductEntity().shortVideoList != null)//更新缩率图
                RecordManager.get().getProductEntity().shortVideoList.get(index).setCoverUrl(materialEntity.material_cover);

            if (materialEntity.downloadState == 0) {//正在下载
                configlist.get(index).setState(MvConfigItem.STATE_DONWLOADING);
            } else if (materialEntity.downloadState == 1) {//下载成功
                configlist.get(index).setState(MvConfigItem.STATE_DONWLOAD_FINISH);
                RecordManager.get().getProductEntity().shortVideoList.get(index).combineVideoAudio = materials.get(i).material_video;
                //将视频进行分离
                mvMaterialManager.start(RecordManager.get().getShortVideoEntity(index));

            } else {//下载失败
                configlist.get(index).setState(MvConfigItem.STATE_DONWLOAD_ERROR);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        KLog.i(requestCode + "onActivityResult--->" + data);
        if (requestCode == PREVIEW_CODE) {
            if (recordMvThreadManager != null)
                recordMvThreadManager.removeListener();
            DCScriptManager.scriptManager().clearScripts();
//          读数据
            recordMvActivityHelper.readConfig(RecordManager.get().getProductEntity().getExtendInfo().template_name);
            if (lastTemplate != null && !lastTemplate.equals(RecordManager.get().getProductEntity().getExtendInfo().template_name)) {
                needShowPrompt = true;
            } else {
                needShowPrompt = false;
            }
            showMvList(RecordManager.get().getProductEntity().shortVideoList, configlist);
            recordMvActivityView.showRecordTips();
            lastPlay = false;
        }else if(requestCode == SearchVideoActivity.TYPE_FROM_RECORDMV){//本地导入视频
            if(data==null){
                return;
            }
            String videoPath = data.getStringExtra(SearchVideoActivity.SHORT_VIDEO_PATH);
            String audioPath = data.getStringExtra(SearchVideoActivity.SHORT_AUDIO_PATH);

            KLog.i(requestCode + "onActivityResult--->" + "videoPath {"+videoPath +"}"+ "audioPath {"+audioPath +"}"+"audioSize-->");
            if(TextUtils.isEmpty(videoPath)&&TextUtils.isEmpty(audioPath)){
                return;
            }
            onRecordEnd(DCCameraConfig.SUCCESS,videoPath,audioPath);//本地导入成功

        }
    }

    @Override
    protected void onDestroy() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        if (dcRecorderBaseHelper != null)
            dcRecorderBaseHelper.onDestroy();
        if (recordMvActivityHelper != null)
            recordMvActivityHelper.onDestory();
        if (recordMvActivityView != null)
            recordMvActivityView.onDestroy();
        Download.pause(this);
        mvRecyclerView.removeOnScrollListener(scorllListener);
        FileDownload.pause(this);
        currentIndex = 0;
        super.onDestroy();
    }


}
