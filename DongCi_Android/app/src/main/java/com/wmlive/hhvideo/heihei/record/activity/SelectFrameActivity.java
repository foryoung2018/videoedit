package com.wmlive.hhvideo.heihei.record.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.os.ResultReceiver;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import com.example.loopback.DCLatencytestTool;
import com.example.loopback.DCLatencytestTool;
import com.example.loopback.DCLoopbackTool;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.BaseCompatActivity;
import com.wmlive.hhvideo.common.base.BaseModel;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.frame.FrameInfo;
import com.wmlive.hhvideo.heihei.beans.frame.FrameSortBean;
import com.wmlive.hhvideo.heihei.beans.frame.Frames;
import com.wmlive.hhvideo.heihei.beans.log.MaterialDownLoad;
import com.wmlive.hhvideo.heihei.beans.opus.OpusMaterialEntity;
import com.wmlive.hhvideo.heihei.beans.opus.UploadMaterialEntity;
import com.wmlive.hhvideo.heihei.beans.record.MusicInfoEntity;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.beans.record.TopicInfoEntity;
import com.wmlive.hhvideo.heihei.discovery.DiscoveryUtil;
import com.wmlive.hhvideo.heihei.discovery.activity.SearchVideoActivity;
import com.wmlive.hhvideo.heihei.record.adapter.FrameAdapter;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.manager.RecordSetting;
import com.wmlive.hhvideo.heihei.record.presenter.SelectFramePresenter;
import com.wmlive.hhvideo.heihei.record.utils.RecordFileUtil;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtil;
import com.wmlive.hhvideo.heihei.record.widget.CircleProgressDialog;
import com.wmlive.hhvideo.heihei.record.widget.CustomFrameView;
import com.wmlive.hhvideo.heihei.record.widget.SmallFrameView;
import com.wmlive.hhvideo.heihei.record.widget.SysAlertDialog;
import com.wmlive.hhvideo.utils.AppCacheFileUtils;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.FileUtil;
import com.wmlive.hhvideo.utils.JsonUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ParamUtis;
import com.wmlive.hhvideo.utils.ScreenUtil;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.utils.download.Download;
import com.wmlive.hhvideo.utils.download.DownloadBean;
import com.wmlive.hhvideo.utils.download.FileDownload;
import com.wmlive.hhvideo.utils.preferences.SPUtils;
import com.wmlive.hhvideo.widget.ZTablayout.ZTabLayout;
import com.wmlive.hhvideo.widget.dialog.MyDialog;
import com.wmlive.hhvideo.widget.dialog.PermissionDialog;
import com.wmlive.networklib.util.EventHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lsq on 8/25/2017.
 * 选择画框页面
 */
public class SelectFrameActivity extends DcBaseActivity<SelectFramePresenter>
        implements SelectFramePresenter.ISelectFrameView, FrameAdapter.OnFrameItemClickListener {
    public static final byte VIDEO_TYPE_IMPORT = 1; // 导入本地
    public static final byte VIDEO_TYPE_RECORD = 2; // 录制开拍 & 直接开拍
    public static final byte VIDEO_TYPE_TEAMWORK = 3; // 共同创作


    //    public static final String EXTRA_MUSIC_INFO = "extra_music_info";
    public static final String EXTRA_TOPIC_INFO = "extra_topic_info";
    public static final String EXTRA_VIDEO_INFO = "extra_video_info";
    public static final String EXTRA_IMPORT_TYPE = "extra_is_import_type";
    public static final String EXTRA_OPUS_ID = "extra_opus_id";
    public static final String EXTRA_FRAME_LAYOUT = "extra_frame_layout";
    public static final String EXTRA_SINGLE_VIDEO = "extra_video_single";
    private static final int MESSAGE_DOWNLAOD_MATERIAL = 1;
    public static final int DOWNLOAD_STATE_NONE = 0; // 下载未开始
    public static final int DOWNLOAD_STATE_DOWNLOADING = 1; // 下载中
    public static final int DOWNLOAD_STATE_DOWNLOADED = 2; // 下载成功
    public static final int DOWNLOAD_STATE_ERROR = 3; // 下载失败


    private static final int VIEWHEIGHT = 280;//设置中间画框高度== 屏幕高度减去此值

    @BindView(R.id.rvFrames)
    RecyclerView rvFrames;
    @BindView(R.id.customFrameView)
    CustomFrameView customFrameView;
    @BindView(R.id.ztab)
    ZTabLayout ztab;

    @BindView(R.id.top_bar)
    LinearLayout top_bar;

    @BindView(R.id.iv_bar_left)
    ImageView iv_bar_left;

    @BindView(R.id.tv_bar_right)
    TextView tv_bar_right;

    @BindView(R.id.fr_delete)
    RelativeLayout fr_delete;

    @BindView(R.id.fl_container)
    FrameLayout fl_container;

    private ArrayList<String> tabNames = new ArrayList<>();//存放所有分比率分组的集合

    private FrameAdapter frameAdapter;
    private FrameInfo mFrameInfo;
    private List<SmallFrameView> itemViewList;
    private byte importType;
    private Context mContext;
    private List<UploadMaterialEntity> materials;
    private List<FrameInfo> frameList; // 当前比例下的画框集合
    private List<FrameSortBean> layoutList; // 所有比例画框的集合的集合
    private Map<String, FrameSortBean> framGroups = new HashMap<>();
    private String currentGroup = "推荐";//当前分辨率tab
    private int selectFrameIndex;//共同创作画框的下标 第一次进来有用，用完就弃用了

    private float curentRatio = 3.0f / 4;

    //记录当前大画框所在的组 和坐标
    private String currentFrameGroup;
    private int currentFramIndex = 0;


    private String frameLayout;
    private List<ShortVideoEntity> videoList;
    private volatile boolean hasResetFrame = false; // 是否重置画框
    private Map<Integer, Integer> indexMap; // 素材原来的位置 对应现在的画框坐标
    private List<Integer> downloadStateList; // 素材下载状态(对应于初始画框)
    private TextView tvNext;
    private CircleProgressDialog prepareDialog;
    private ResultReceiver resultReceiver;
    private long opusId; // 作品ID
    private MyDialog noticeDialog;
    //    private MusicInfoEntity musicInfo;
//    private boolean hasDownloadMusic = true;
    private Handler handler;
    private boolean isSingleVideo;
    private int videoQuality = FrameInfo.VIDEO_QUALITY_LOW;


    public static void startSelectFrameActivity(BaseCompatActivity ctx, MusicInfoEntity musicInfo, byte importType) {

        startSelectFrameActivity(ctx, importType, null, -1, null);
//        DCLatencytestTool.startLatency(ctx);
    }

    public static void startSelectFrameActivity(BaseCompatActivity ctx, TopicInfoEntity topicInfo, byte importType) {
        startSelectFrameActivity(ctx, importType, topicInfo, -1, null);
    }

    public static void startSelectFrameActivity(final BaseCompatActivity context, final byte importType, final long opusId, final String frameLayout) {
        startSelectFrameActivity(context, importType, null, opusId, frameLayout);
    }

    private static void startSelectFrameActivity(final BaseCompatActivity context, final byte importType, TopicInfoEntity topicInfo, final long opusId, final String frameLayout) {
        // 共同创作
        final BaseModel count = new BaseModel();
        new RxPermissions(context)
                .requestEach(RecordSetting.RECORD_PERMISSIONS)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        KLog.i(count.type+"====请求权限：" + permission.toString()+!permission.granted);
                        if (!permission.granted) {
                            if (Manifest.permission.CAMERA.equals(permission.name)) {
                                new PermissionDialog(context, 20).show();
                            } else if (Manifest.permission.RECORD_AUDIO.equals(permission.name)) {
                                new PermissionDialog(context, 10).show();
                            }
                        } else {
                            count.type++;
                        }
                        if (count.type == 3) {
                            KLog.i("=====获取权限：成功");
                            int result = -1;//-1表示权限获取失败，-2表示相机初始化失败，0表示权限和相机都成功
                            result = RecordManager.get().initRecordCore(context) ? 0 : -2;
                            if (result == 0) {
                                Intent intent = new Intent(context, SelectFrameActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putParcelable(EXTRA_TOPIC_INFO, topicInfo);
                                bundle.putByte(EXTRA_IMPORT_TYPE, importType);
                                bundle.putLong(EXTRA_OPUS_ID, opusId);
                                bundle.putString(EXTRA_FRAME_LAYOUT, frameLayout);
                                KLog.i("hsing", "opusId " + opusId + " frameLayout " + frameLayout);
                                intent.putExtras(bundle);
                                context.startActivity(intent);
                            } else if (result == -1) {
                                ToastUtil.showToast("请在系统设置中允许App运行必要的权限");
                            } else {
                                KLog.i("=====初始化相机失败" + result);
                                ToastUtil.showToast("初始化相机失败");
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.getMessage();
                        KLog.i("=====初始化相机失败:" + throwable.getMessage());
                        ToastUtil.showToast("初始化相机失败");
                    }
                });

    }

    @Override
    protected SelectFramePresenter getPresenter() {
        return new SelectFramePresenter(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        DCLoopbackTool.stopBind();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_select_frame;
    }

    @Override
    protected void initData() {
        super.initData();
        mContext = SelectFrameActivity.this;
        importType = getIntent().getByteExtra(EXTRA_IMPORT_TYPE, VIDEO_TYPE_RECORD);
        handler = new Handler(Looper.getMainLooper());
        getFramsData();//获取画框数据
        initGTCZ();//对共同创作进来的分开设置
        frameAdapter = new FrameAdapter(new ArrayList<FrameInfo>());
        frameAdapter.setItemClickListener(this);
        rvFrames.setLayoutManager(new GridLayoutManager(SelectFrameActivity.this, 4, GridLayoutManager.VERTICAL, false));
        rvFrames.setAdapter(frameAdapter);
        ztab.setOnTabSelectedListener(new ZTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                String s = tabNames.get(position);
                if (currentGroup.equalsIgnoreCase(s)) {
                    return;
                }
                currentGroup = s;
                frameList = framGroups.get(s).layout;
                frameAdapter.addData(frameList);
                if (materials != null && materials.size() > 0) {
                    if (currentGroup.equals(currentFrameGroup)) {
                        frameAdapter.setInitPosition(currentFramIndex, materials.size() - deletCount);
                    } else {
                        frameAdapter.setInitPosition(-1, materials.size() - deletCount);
                    }
                } else {
                    if (currentGroup.equals(currentFrameGroup)) {
                        frameAdapter.setInitPosition(currentFramIndex, -1);
                    } else {
                        frameAdapter.setInitPosition(-1, -1);
                    }
                }
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });
        ViewGroup.LayoutParams layoutParams = fl_container.getLayoutParams();
        layoutParams.height = ScreenUtil.getHeight(this) - ScreenUtil.dip2px(this, VIEWHEIGHT);
        layoutParams.width = -1;
        fl_container.setLayoutParams(layoutParams);
        DCLoopbackTool.onStart(this);
    }

    private void initGTCZ() {
        if (importType == VIDEO_TYPE_TEAMWORK) {
            opusId = getIntent().getLongExtra(EXTRA_OPUS_ID, -1);
            frameLayout = getIntent().getStringExtra(EXTRA_FRAME_LAYOUT);
            tvNext = tv_bar_right;
            tvNext.setTextColor(mContext.getResources().getColor(R.color.hh_color_c));
            tvNext.setEnabled(false);

            RecordManager.get().trimFile(new File(RecordFileUtil.getMaterialDir()));
            resultReceiver = new VideoReceiver(this, handler);

            requestData(opusId);//下载共同创作信息
            setTitle();
            top_bar.setVisibility(View.VISIBLE);
            tvNext.setOnClickListener(this);
            iv_bar_left.setOnClickListener(this);
        } else {
            tvNext = new TextView(this);
            tvNext.setText("下一步");
            tvNext.setTextSize(16);
            TypedValue tv = new TypedValue();
            tvNext.setBackgroundResource(tv.resourceId);
            tvNext.setTextColor(getResources().getColor(R.color.hh_color_g));
            tvNext.setGravity(Gravity.CENTER);
            tvNext.setPadding(10, 6, DeviceUtils.dip2px(mContext, 15), 6);
            setTitle("选择画框", true);
            setBlackToolbar();
            top_bar.setVisibility(View.GONE);
            setToolbarRightView(tvNext, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (RecordManager.get().getProductEntity() == null) {
                        toastFinish();
                        return;
                    }
                    if (importType == VIDEO_TYPE_IMPORT) {
                        List<ShortVideoEntity> videoList = RecordManager.get().getProductEntity().shortVideoList;
                        for (int i = 0, size = videoList.size(); i < size; i++) {
                            if (videoList.get(i) != null) {
                                videoList.get(i).setOriginalMixFactor(RecordSetting.MAX_VOLUME / 2);
                                videoList.get(i).quality = videoQuality;
                            }
                        }
                        SearchVideoActivity.startSearchVideoActivity(SelectFrameActivity.this, 0, SearchVideoActivity.TYPE_FROM_SEARCH);
                    } else if (importType == VIDEO_TYPE_RECORD) {
                        TopicInfoEntity topicInfo = getIntent().getParcelableExtra(EXTRA_TOPIC_INFO);
                        if (topicInfo != null && !TextUtils.isEmpty(topicInfo.topicTitle)) {
                            RecordManager.get().setTopicInfo(topicInfo);
                        }
                        List<ShortVideoEntity> videoList = RecordManager.get().getProductEntity().shortVideoList;
                        for (int i = 0, size = videoList.size(); i < size; i++) {
                            if (videoList.get(i) != null) {
                                videoList.get(i).setOriginalMixFactor(RecordSetting.MAX_VOLUME / 2);
                                videoList.get(i).quality = videoQuality;
                            }
                        }
                        DCLoopbackTool.stopBind();
                        RecordActivitySdk.startRecordActivity(SelectFrameActivity.this, RecordActivitySdk.TYPE_NORMAL);
//                    RecordActivity.startRecordActivity(SelectFrameActivity.this, RecordActivity.TYPE_NORMAL);
                    }
//                } else if (importType == VIDEO_TYPE_TEAMWORK) {
//                    // 共同创作
//                    if (prepareDialog == null) {
//                        prepareDialog = SysAlertDialog.showCircleProgressDialog(SelectFrameActivity.this, getString(R.string.prepare), true, false);
//                    }
//                    if (importType == VIDEO_TYPE_IMPORT) {
//                        List<ShortVideoEntity> videoList = RecordManager.get().getProductEntity().shortVideoList;
//                        for (int i = 0, size = videoList.size(); i < size; i++) {
//                            if (videoList.get(i) != null) {
//                                videoList.get(i).setOriginalMixFactor(RecordSetting.MAX_VOLUME / 2);
//                                videoList.get(i).quality = videoQuality;
//                            }
//                        }
//                        SearchVideoActivity.startSearchVideoActivity(SelectFrameActivity.this, 0, SearchVideoActivity.TYPE_FROM_SEARCH);
//                    } else if (importType == VIDEO_TYPE_RECORD) {
//                        //直接开拍进去录制界面
//                        TopicInfoEntity topicInfo = getIntent().getParcelableExtra(EXTRA_TOPIC_INFO);
//                        if (topicInfo != null && !TextUtils.isEmpty(topicInfo.topicTitle)) {
//                            RecordManager.get().setTopicInfo(topicInfo);
//                        }
//                        List<ShortVideoEntity> videoList = RecordManager.get().getProductEntity().shortVideoList;
//                        for (int i = 0, size = videoList.size(); i < size; i++) {
//                            if (videoList.get(i) != null) {
//                                videoList.get(i).setOriginalMixFactor(RecordSetting.MAX_VOLUME / 2);
//                                videoList.get(i).quality = videoQuality;
//                            }
//                        }
//
//                        RecordActivitySdk.startRecordActivity(SelectFrameActivity.this, RecordActivitySdk.TYPE_NORMAL);
//                    } else if (importType == VIDEO_TYPE_TEAMWORK) {
//
//                        tempList.addAll(videoList);
//                    }
//                    if (itemViewList.size() == videoSize) {
//                        // 素材满时
//                        if (prepareDialog != null) {
//                            prepareDialog.dismiss();
//                        }
//                        showNoticeDialog();
//                        return;
//                    } else {
//                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
////                        if (musicInfo != null && !TextUtils.isEmpty(musicInfo.getMusicPath())) {
////                            RecordManager.get().getProductEntity().musicInfo = musicInfo;
////                            RecordManager.get().setOriginalMixFactor(0);
////                        } else {
//                        for (int i = 0, size = tempList.size(); i < size; i++) {
//                            ShortVideoEntity videoEntity = tempList.get(i);
//                            if (videoEntity != null) {
//                                videoEntity.setOriginalMixFactor(RecordSetting.MAX_VOLUME / 2);
//                                if (TextUtils.isEmpty(videoEntity.importVideoPath)) {
//                                    videoEntity.quality = videoQuality;
//                                }
//                            }
//                        }
////                        }
//
//                        RecordManager.get().getProductEntity().setShortVideos(tempList);
//                        // 设置作品ID
//                        if (RecordManager.get().getProductEntity().shortVideoList.size() > 0) {
//                            RecordManager.get().getProductEntity().originalId = opusId;
//                        }
//                        copyVideos();
//                        RecordActivitySdk.startRecordActivity(SelectFrameActivity.this, RecordActivity.TYPE_TOGETHER);
////                        RecordActivity.startRecordActivity(SelectFrameActivity.this, RecordActivity.TYPE_TOGETHER);
//
//                    }
//                }
                }
            });

        }
    }

    private void getFramsData() {
        Observable.just(1)
                .subscribeOn(Schedulers.io())
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        return SPUtils.getString(DCApplication.getDCApp(), SPUtils.FRAME_LAYOUT_DATA, "");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String frames) throws Exception {
                        Frames f = JsonUtils.parseObject(frames, Frames.class);
                        if(f==null){
                            ToastUtil.showToast("数据错误，请稍后重试");
                            finish();
                            return;
                        }
                        setFramData(f);
                        downFrameImage(f.layouts);
                        KLog.i("hsing", "使用网络画框数据");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
//                        getLocalFrameLayouts();
                    }
                });

    }

    /**
     * 处理画框数据
     *
     * @param f
     */
    private void setFramData(Frames f) {
        if (f != null) {
            layoutList = f.layouts;
            for (FrameSortBean sortBean : layoutList) {
                framGroups.put(sortBean.name, sortBean);
                tabNames.add(sortBean.name);
            }
            ztab.setupWithoutViewPager(tabNames.toArray(new String[tabNames.size()]), 0);
            if (importType == VIDEO_TYPE_TEAMWORK) {
                mFrameInfo = getFrameInfo(frameLayout);
                frameList = framGroups.get(currentGroup).layout;
            } else {
                frameList = framGroups.get(currentGroup).layout;
                mFrameInfo = frameList.get(0);
            }
            ztab.selectTab(currentGroup);
            currentFrameGroup = currentGroup;
            videoQuality = mFrameInfo.video_quality;
            RecordManager.get().newProductEntity(mFrameInfo);
            setFrameView(false);
            frameAdapter.addData(frameList);
        }
    }

    /**
     * \
     * 下载frame的背景图片
     */
    private void downFrameImage(List<FrameSortBean> layouts) {
        if (layouts == null) {
            return;
        }
        ArrayList<DownloadBean> downloadList = new ArrayList<DownloadBean>();
        for (FrameSortBean sortBean : layouts) {
            List<FrameInfo> layout = sortBean.layout;
            for (int i = 0; i < layout.size(); i++) {
                FrameInfo frameInfo = layout.get(i);
                if (frameInfo != null) {
                    if (!TextUtils.isEmpty(frameInfo.sep_image)) {
                        DownloadBean downloadBean = new DownloadBean(frameInfo.id, frameInfo.sep_image,
                                AppCacheFileUtils.getAppFramesImagePath(), "", "");
                        downloadList.add(downloadBean);
                    }
                    if (!TextUtils.isEmpty(frameInfo.publish_image)) {
                        DownloadBean downloadBean = new DownloadBean(frameInfo.id, frameInfo.publish_image,
                                AppCacheFileUtils.getAppFramesImagePath(), "", "");
                        downloadList.add(downloadBean);
                    }
                }
            }
        }
        FileDownload.start(mContext, downloadList, new FrameReceiver(this, handler));
    }

    private static class FrameReceiver extends ResultReceiver {
        private WeakReference<SelectFrameActivity> activity;

        @SuppressLint("RestrictedApi")
        public FrameReceiver(SelectFrameActivity activity, Handler handler) {
            super(handler);
            this.activity = new WeakReference<>(activity);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            switch (resultCode) {
                case FileDownload.RESULT_COMPLETE_ALL:
                    KLog.i("SelectFrameActivity FileDownload", "下载画框图片完成");
                    break;
            }
        }
    }


    /**
     * 创建导入视频备份
     */
    private void copyVideos() {
        List<ShortVideoEntity> shortVideoList = RecordManager.get().getProductEntity().shortVideoList;
        if (shortVideoList != null) {
            for (int i = 0, size = shortVideoList.size(); i < size; i++) {
                ShortVideoEntity videoEntity = shortVideoList.get(i);
                if (null != videoEntity && !TextUtils.isEmpty(videoEntity.importVideoPath)) {
                    if (prepareDir(i)) {
                        String combineVideoAudio = RecordFileUtil.createVideoFile(RecordManager.get().getShortVideoEntity(i).baseDir, String.valueOf(i));
                        videoEntity.combineVideoAudio = combineVideoAudio;
                        if (importType == VIDEO_TYPE_IMPORT) {
                            videoEntity.setImport(true, true);
                            videoEntity.setVideoType(String.valueOf(VIDEO_TYPE_IMPORT));
                        } else if (importType == VIDEO_TYPE_TEAMWORK) {
                            videoEntity.setImport(true, false);
                            videoEntity.setVideoType(String.valueOf(VIDEO_TYPE_TEAMWORK));
                        }
                        FileUtil.copy(videoEntity.importVideoPath, combineVideoAudio);
                    }
                }
            }
            RecordManager.get().updateProduct();
        }
    }

    /**
     * 获取本地framelayout数据
     */
    private void getLocalFrameLayouts() {
        Observable.just(1)
                .subscribeOn(Schedulers.io())
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        AssetManager assetManager = getAssets();
                        try {
                            InputStream is = assetManager.open("listView.json");
                            BufferedReader br = new BufferedReader(new InputStreamReader(is));
                            StringBuffer stringBuffer = new StringBuffer();
                            String str = null;
                            while ((str = br.readLine()) != null) {
                                stringBuffer.append(str);
                            }
                            is.close();
                            KLog.d("使用了assets里存储的画框");
                            return stringBuffer.toString();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String frames) throws Exception {
                        Frames f = JsonUtils.parseObject(frames, Frames.class);
                        setFramData(f);
                        downFrameImage(f.layouts);
                        KLog.i("hsing", "使用本地画框数据");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        SelectFrameActivity.this.toastFinish();
                    }
                });
    }

    /**
     * 下载共同创作信息
     *
     * @param opusId
     */
    private void requestData(long opusId) {
        getPresenter().getOpusMaterial(opusId);
        //开始下载

    }

    @Override
    protected void onSingleClick(View v) {
        if (v == tv_bar_right) {
            //共同创作 用单独的bar
            if (prepareDialog == null) {
                prepareDialog = SysAlertDialog.showCircleProgressDialog(SelectFrameActivity.this, getString(R.string.prepare), true, false);
            }
            int videoSize = 0;
            List<ShortVideoEntity> tempList = new ArrayList<>();
            if (hasResetFrame) {//画框重置，更新作品的videolist
                if (mFrameInfo != null && mFrameInfo.getLayout() != null) {
                    int size = mFrameInfo.getLayout().size();
                    for (int i = 0; i < size; i++) {
                        tempList.add(new ShortVideoEntity());
                    }
                }
                if (videoList != null) {
                    for (int i = 0, size = videoList.size(); i < size; i++) {
                        ShortVideoEntity videoEntity = videoList.get(i);
                        if (null != videoEntity && !TextUtils.isEmpty(videoEntity.importVideoPath)) {
                            int viewIndex = indexMap.get(i); // 根据素材位置得到view位置
                            tempList.set(viewIndex, videoEntity);
                            videoSize++;
                        }
                    }
                }
            } else {
                if (videoList != null) {
                    for (int i = 0, size = videoList.size(); i < size; i++) {
                        ShortVideoEntity videoEntity = videoList.get(i);
                        if (null != videoEntity && !TextUtils.isEmpty(videoEntity.importVideoPath)) {
                            videoSize++;
                        }
                    }
                }
                tempList.addAll(videoList);
            }
            if (itemViewList.size() == videoSize) {
                // 素材满时
                if (prepareDialog != null) {
                    prepareDialog.dismiss();
                }
                showNoticeDialog();
                return;
            } else {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                for (int i = 0, size = tempList.size(); i < size; i++) {
                    ShortVideoEntity videoEntity = tempList.get(i);
                    if (videoEntity != null) {
                        videoEntity.setOriginalMixFactor(RecordSetting.MAX_VOLUME / 2);
                        if (TextUtils.isEmpty(videoEntity.importVideoPath)) {
                            videoEntity.quality = videoQuality;
                        }
                    }
                }

                RecordManager.get().getProductEntity().setShortVideos(tempList);
                // 设置作品ID
                if (RecordManager.get().getProductEntity().shortVideoList.size() > 0) {
                    RecordManager.get().getProductEntity().originalId = opusId;
                }
                copyVideos();
                RecordActivitySdk.startRecordActivity(SelectFrameActivity.this, RecordActivitySdk.TYPE_TOGETHER);
            }
        } else if (v == iv_bar_left) {
            onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        KLog.d("onresume");
        if (prepareDialog != null) {
            prepareDialog.dismiss();
        }
    }

    @Override
    public void onFrameItemClick(FrameInfo info, boolean enable, int position) {
        KLog.d("当前选中画框的数据+info.toString() enable==" + enable + "  " + info.toString());
        if (enable) {
            mFrameInfo = info;
            RecordManager.get().newProductEntity(mFrameInfo);
            KLog.i("xxxx", "onFrameItemClick video quality high " + (mFrameInfo.video_quality == FrameInfo.VIDEO_QUALITY_HIGH));
            currentFramIndex = position;
            currentFrameGroup = currentGroup;

            setFrameView(true);
            if (videoQuality != mFrameInfo.video_quality) {
                videoQuality = mFrameInfo.video_quality;
                pauseDownload();
                downloadVideo();
                if (materials != null && materials.size() > 0) {
                    refreshView();
                }
            }
            videoQuality = mFrameInfo.video_quality;
        }
    }

    private void pauseDownload() {
        KLog.i("xxxx", "pauseDownload ");
        if (materials != null && materials.size() > 0) {
            for (int i = 0, size = materials.size(); i < size; i++) {
                UploadMaterialEntity materialEntity = materials.get(i);
                if (materialEntity != null) {
                    if (downloadStateList.get(materialEntity.material_index) == SelectFrameActivity.DOWNLOAD_STATE_DOWNLOADING) {
                        BaseDownloadTask baseDownloadTask = FileDownload.taskArray.get(materialEntity.downloadId);
                        KLog.i("xxxx", "pauseDownload index " + materialEntity.material_index + " baseDownloadTask" + baseDownloadTask);
                        if (baseDownloadTask != null) {
                            FileDownload.pause(materialEntity.downloadId, baseDownloadTask.getId());
                        }
                    }
                }
            }
        }
        resetVideoList();
    }

    /**
     * 重置布局信息
     *
     * @param isResetFrame 是否重置画框
     */
    public void setFrameView(boolean isResetFrame) {
        this.hasResetFrame = isResetFrame;
        itemViewList = new ArrayList<>();
        ParamUtis.setLayoutParam(this, customFrameView, mFrameInfo.canvas_height, VIEWHEIGHT);
        if (mFrameInfo != null && mFrameInfo.getLayout() != null) {
            int size = mFrameInfo.getLayout().size();
            SmallFrameView itemView;
            for (int i = 0; i < size; i++) {
                itemView = new SmallFrameView(this);
                itemView.setLayoutInfo(mFrameInfo.getLayout().get(i), importType == VIDEO_TYPE_TEAMWORK);
                itemView.setTag(i);
                itemView.setOnSmallFrameClickListener(mSmallFrameClickListener);
                itemViewList.add(itemView);
            }
            customFrameView.setFrameView(mFrameInfo, itemViewList, true);

            customFrameView.setEventListener(new CustomFrameView.EventListener() {
                @Override
                public void onChildClick(int index, int x, int y, int width, int height) {

                }

                @Override
                public void onChangePosition(int selectIndex, int targetIndex) {
                    //1 获取当前view素材下标
                    KLog.d("indexMap==" + indexMap);
                    if (selectIndex == targetIndex) {
                        return;
                    }
                    //根据现在的位置去找原来的素材位置
                    int materialIndex_s = findMaterialIndex(selectIndex);
                    int materialIndex_t = findMaterialIndex(targetIndex);

                    UploadMaterialEntity s_material = findMaterial(materialIndex_s);
                    if (s_material == null) {
                        s_material = new UploadMaterialEntity();
                    }

                    UploadMaterialEntity t_material = findMaterial(materialIndex_t);
                    if (t_material == null) {
                        t_material = new UploadMaterialEntity();
                    }

                    KLog.d("ggq", "materialIndex_s==" + materialIndex_s + "  materialIndex_t==" + materialIndex_t);
                    if (materialIndex_s != -1) {
                        //设置目标区图片
                        itemViewList.get(targetIndex).setMaterialCover(s_material.material_cover, s_material.material_cover);
                        //设置目标区视频时间
                        if (s_material.material_length <= 0) {
                            itemViewList.get(targetIndex).hideMaterialCover();
                        } else {
                            String during = DiscoveryUtil.convertTime((int) s_material.material_length / 1000);
                            itemViewList.get(targetIndex).setDuration(during);
                        }
                        indexMap.put(materialIndex_s, targetIndex);
                    }
                    if (materialIndex_t != -1) {

                        itemViewList.get(selectIndex).setMaterialCover(t_material.material_cover, t_material.material_cover);
                        if (t_material.material_length <= 0) {
                            itemViewList.get(selectIndex).hideMaterialCover();
                        } else {
                            String during = DiscoveryUtil.convertTime((int) t_material.material_length / 1000);
                            itemViewList.get(selectIndex).setDuration(during);
                        }
                        indexMap.put(materialIndex_t, selectIndex);
                    }
                    if (materialIndex_t == -1) {
                        itemViewList.get(selectIndex).hideMaterialCover();
                    }
                    if (materialIndex_s == -1) {
                        itemViewList.get(targetIndex).hideMaterialCover();
                    }
                    hasResetFrame = true;

                }

                @Override
                public void showDeletField(boolean show, int index) {
                    String coverPath = itemViewList.get(index).getCoverPath();
                    if (show && !TextUtils.isEmpty(coverPath)) {
                        fr_delete.setVisibility(View.VISIBLE);
                    } else {
                        fr_delete.setVisibility(View.GONE);
                    }
                }

                @Override
                public void deleteFramItem(int index) {
                    SmallFrameView smallFrameView = itemViewList.get(index);
                    String coverPath = smallFrameView.getCoverPath();
                    if (materials == null || materials.size() == 0) {
                        return;
                    }
                    KLog.d("deleteFramItem" + coverPath);
                    if (!TextUtils.isEmpty(coverPath)) {
                        smallFrameView.hideMaterialCover();
                        if (indexMap == null) {
                            new Handler() {
                            }.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    deletSmallFrameView(index, smallFrameView);
                                }
                            }, 1000);
                        } else {
                            deletSmallFrameView(index, smallFrameView);
                        }

                    }

                }

            });
        }
        if (hasResetFrame) {
            // 重置画框
            indexMap = new HashMap<>();
            if (materials != null && materials.size() > 0) {
                int index = 0;
                for (int i = 0, size = materials.size(); i < size; i++) {
                    UploadMaterialEntity materialEntity = materials.get(i);
                    if (materialEntity != null
                            && (!TextUtils.isEmpty(materialEntity.material_video) || !TextUtils.isEmpty(materialEntity.material_video_high))) {
                        // 素材有删除，素材有为空，为空跳过，index为view的位置是连续的
                        int materialIndex = materialEntity.material_index;
                        indexMap.put(materialIndex, index);
                        if (itemViewList.size() > index) {
                            itemViewList.get(index).setMaterialCover(materialEntity.material_cover, materialEntity.material_cover);
                            itemViewList.get(index).setDuration(DiscoveryUtil.convertTime((int) materialEntity.material_length / 1000));
                            if (downloadStateList.get(materialIndex) == DOWNLOAD_STATE_DOWNLOADED) {
                                // 已下载成功的显示封面
                                itemViewList.get(index).showMaterialCover(true);
                            } else if (downloadStateList.get(materialIndex) == DOWNLOAD_STATE_ERROR) {
                                // 下载失败的不显示封面，显示刷新
                                itemViewList.get(index).showMaterialCover(false);
                            } else {
                                // 未下载,下载中显示加载进度
                                itemViewList.get(index).showProgress();
                            }
                        }
                        index++;
                    }
                }
            }
        }
    }

    private void initVideoList() {
        videoList = new ArrayList<>();
        downloadStateList = new ArrayList<>();
        if (mFrameInfo != null && mFrameInfo.getLayout() != null) {
            int size = mFrameInfo.getLayout().size();
            for (int i = 0; i < size; i++) {
                videoList.add(new ShortVideoEntity());
                downloadStateList.add(DOWNLOAD_STATE_NONE);
            }
        }
    }


    private void resetVideoList() {
        if (downloadStateList != null) {
            int size = downloadStateList.size();
            for (int i = 0; i < size; i++) {
                downloadStateList.add(i, DOWNLOAD_STATE_NONE);
            }
        }
    }

    @Override
    public void onGetMaterial(OpusMaterialEntity response) {
        KLog.d(TAG, "onGetMaterial22222222");
        materials = response.materials;
        if (TextUtils.isEmpty(frameLayout)) {
            mFrameInfo = frameList.get(0);
        } else {
            if (importType == VIDEO_TYPE_TEAMWORK) {
                mFrameInfo = getFrameInfo(frameLayout);
                KLog.d("共同创作 currentGroup==" + currentGroup);
                ztab.selectTab(currentGroup);
            }
            if (rvFrames == null) {
                return;
            }
            rvFrames.smoothScrollToPosition(selectFrameIndex);
        }
        currentFrameGroup = currentGroup;
        currentFramIndex = selectFrameIndex;
        RecordManager.get().newProductEntity(mFrameInfo);
        videoQuality = mFrameInfo.video_quality;
        initVideoList();
        setFrameView(false);
        if (frameAdapter != null && materials != null) {
            if (importType == VIDEO_TYPE_TEAMWORK && materials.size() == 1) {
                frameAdapter.setInitPosition(selectFrameIndex, 2);
            } else {
                frameAdapter.setInitPosition(selectFrameIndex, materials.size());
            }
        }
//        if (response.music != null && !TextUtils.isEmpty(response.music.getMusic_path())) {
//            downloadMusic(response.music);
//        }
        downloadVideo();
    }

    private void downloadVideo() {
        ArrayList<DownloadBean> downloadList = new ArrayList<>();
        KLog.d("ggq", "下载视频1111111");
        if (indexMap == null) {
            indexMap = new HashMap<>();
        }
        KLog.d(TAG, "downloadVideo 33333");
        if (materials != null && materials.size() > 0) {
            for (int i = 0, size = materials.size(); i < size; i++) {
                UploadMaterialEntity materialEntity = materials.get(i);
                if (materialEntity != null
                        && (!TextUtils.isEmpty(materialEntity.material_video_high) || !TextUtils.isEmpty(materialEntity.material_video))) {
                    int index = materialEntity.material_index;
                    if (hasResetFrame) {
                        index = indexMap.get(materialEntity.material_index);
                    } else {
                        indexMap.put(index, i);
                    }
                    if (index >= 0 && index < itemViewList.size()) {
                        KLog.d("ggq", "显示封面==" + materialEntity.material_cover);
                        KLog.d("ggq", "显示封面materialEntity==" + materialEntity);
                        itemViewList.get(index).setMaterialCover(materialEntity.material_cover, materialEntity.material_cover);
                        itemViewList.get(index).showProgress();
                        itemViewList.get(index).setDuration(DiscoveryUtil.convertTime((int) materialEntity.material_length / 1000));
                    }

                    int realQuality = FrameInfo.VIDEO_QUALITY_LOW;
                    String videopath = materialEntity.material_video;
                    if (videoQuality == FrameInfo.VIDEO_QUALITY_HIGH && !TextUtils.isEmpty(materialEntity.material_video_high)) {
                        realQuality = FrameInfo.VIDEO_QUALITY_HIGH;
                        videopath = materialEntity.material_video_high;
                    }
                    if (materialEntity.material_index >= 0 && materialEntity.material_index < videoList.size()) {
                        ShortVideoEntity videoEntity = videoList.get(materialEntity.material_index);
                        if (videoEntity != null) {
                            videoEntity.quality = realQuality;
                            // 设置素材原始Id
                            videoEntity.originalId = materialEntity.ori_id;
                            if (materialEntity.ori_id == 0) {
                                videoEntity.originalId = materialEntity.id;
                            }
                        }
                    }

                    if (!TextUtils.isEmpty(videopath)) {
                        int downloadId = FileDownloadUtils.generateId(videopath, RecordFileUtil.getMaterialDir());
                        materialEntity.downloadId = downloadId;
                        DownloadBean downloadBean = new DownloadBean(downloadId, videopath,
                                RecordFileUtil.getMaterialDir(), "", "mp4", materialEntity.material_index);
                        KLog.i(TAG, " baseDownloadTask downloadBean" + downloadBean);
                        downloadList.add(downloadBean);
                    }
                }
            }
            downloadMaterial(downloadList);
        }
    }

    public FrameInfo getFrameInfo(String frameLayout) {

        for (FrameSortBean bean : layoutList) {
            List<FrameInfo> layout = bean.layout;
            for (int i = 0; i < layout.size(); i++) {
                FrameInfo frame = layout.get(i);
                if (frame.name.equalsIgnoreCase(frameLayout)) {
                    if (frame.video_count == 1) {
                        currentGroup = tabNames.get(0);
                        selectFrameIndex = 0;
                        return layoutList.get(0).layout.get(0);
                    } else {
                        currentGroup = bean.name;
                        selectFrameIndex = i;
                        return frame;
                    }
                }
            }
        }
        return layoutList.get(0).layout.get(0);
    }


//    //下载音乐
//    private void downloadMusic(final SearchMusicBean musicBean) {
//        if (musicBean == null) {
//            return;
//        }
//        Download.start(mContext, musicBean.getMusic_path(), AppCacheFileUtils.getAppMusicCachePath(), "", "", 100,
//                new MusicReceiver(this, handler, musicBean));
//    }
//
//    private static class MusicReceiver extends ResultReceiver {
//        private WeakReference<SelectFrameActivity> activity;
//        private SearchMusicBean musicBean;
//
//        public MusicReceiver(SelectFrameActivity activity, Handler handler, SearchMusicBean musicBean) {
//            super(handler);
//            this.activity = new WeakReference<>(activity);
//            this.musicBean = musicBean;
//        }
//
//        @Override
//        protected void onReceiveResult(int resultCode, Bundle resultData) {
//            String message = resultData.getString("message");
//            if (activity != null && activity.get() != null) {
//                switch (resultCode) {
//                    case Download.RESULT_PREPARE:
//                        break;
//                    case Download.RESULT_PAUSE:
//                        activity.get().dismissLoad();
//                        break;
//                    case Download.RESULT_DOWNLOADING:
//                        break;
//                    case Download.RESULT_ERROR:
//                        activity.get().hasDownloadMusic = true;
//                        activity.get().refreshView();
//                        activity.get().dismissLoad();
//                        activity.get().showToast(message);
//                        break;
//                    case Download.RESULT_COMPLETE:
//                        activity.get().hasDownloadMusic = true;
//                        activity.get().refreshView();
//                        KLog.i("hsing", "下载音乐完成");
//                        activity.get().musicInfo = new MusicInfoEntity();
//                        activity.get().musicInfo.musicId = musicBean.getId();
//                        activity.get().musicInfo.setMusicPath(resultData.getString("savePath"));
//                        activity.get().musicInfo.musicIconUrl = musicBean.getAlbum_cover();
//                        activity.get().musicInfo.title = musicBean.getName();
//                        activity.get().musicInfo.author = musicBean.getSinger_name();
//                        activity.get().musicInfo.setTrimRange(0, activity.get().musicInfo.getDuring());
//                        break;
//                }
//            }
//
//        }
//    }

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

    /**
     * 刷新view状态
     */
    private void refreshView() {
        int completeCount = 0;
        if (downloadStateList != null) {
            for (Integer state : downloadStateList) {
                if (state == DOWNLOAD_STATE_DOWNLOADED || state == DOWNLOAD_STATE_ERROR) {
                    completeCount++;
                }
            }
        }
//        if (materials != null && completeCount == materials.size() && hasDownloadMusic) {
        if (materials != null && completeCount == materials.size()) {
            setNextEnable();
        } else {
            tvNext.setEnabled(false);
            tvNext.setTextColor(mContext.getResources().getColor(R.color.hh_color_c));
        }
    }

    private void setNextEnable() {
        if (tv_bar_right != null) {
            tv_bar_right.setEnabled(true);
            tv_bar_right.setTextColor(getResources().getColor(R.color.hh_color_g));
        }
    }

    /**
     * 初始化目录
     *
     * @param shortVideoIndex
     * @return
     */
    private boolean prepareDir(int shortVideoIndex) {
        if (RecordManager.get().getProductEntity() == null) {
            RecordManager.get().newProductEntity(mFrameInfo);
        }
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
            RecordManager.get().getShortVideoEntity(shortVideoIndex).baseDir = shortVideoDir;
        }

        if (!RecordFileUtil.createDir(new File(RecordManager.get().getShortVideoEntity(shortVideoIndex).baseDir))) {
            KLog.i("====创建shortVideoDir文件夹失败");
            return false;
        }
        return true;
    }

    /**
     * SmallFrameView点击回调
     */
    private SmallFrameView.OnSmallFrameClickListener mSmallFrameClickListener = new SmallFrameView.OnSmallFrameClickListener() {
        @Override
        public void onRefreshClick(int index, SmallFrameView view) {
            int currentIndex = index;
            if (hasResetFrame) {
                int materialIndex = findMaterialIndex(index);
                if (materialIndex != -1) {
                    currentIndex = materialIndex;
                }
            }
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

        @Override
        public void onDeleteClick(int index, SmallFrameView view) {
        }

    };

    private final String TAG = "TESTTAG";

    private void deletSmallFrameView(int index, SmallFrameView view) {
        KLog.d(TAG, "deletSmallFrameView1111111");
        if (!TextUtils.isEmpty(frameLayout) &&
                downloadStateList != null) {
            if (materials != null && materials.size() > 0) {
                int materialIndex = findMaterialIndex(index);
                KLog.d(TAG, "deletSmallFrameView1111111  materialIndex==" + materialIndex);
                UploadMaterialEntity materialEntity = findMaterial(materialIndex);
                if (materialEntity != null) {
                    BaseDownloadTask baseDownloadTask = FileDownload.taskArray.get(materialEntity.downloadId);
                    if (baseDownloadTask != null) {
                        downloadStateList.set(index, SelectFrameActivity.DOWNLOAD_STATE_DOWNLOADED);
                        FileDownload.pause(materialEntity.downloadId, baseDownloadTask.getId());
                        refreshView();
                    }
                }
            }
        }
        int currentIndex = index;
        if (hasResetFrame) {
            int materialIndex = findMaterialIndex(index);
            if (materialIndex != -1) {
                currentIndex = materialIndex;
            }
        }
        if (materials != null && materials.size() > 0) {
            for (int i = 0, size = materials.size(); i < size; i++) {
                UploadMaterialEntity materialEntity = materials.get(i);
                if (materialEntity != null && materialEntity.material_index == currentIndex) {
                    materials.set(i, new UploadMaterialEntity());
                }
            }
        }
        if (videoList != null && videoList.size() > 0) {
            videoList.set(currentIndex, new ShortVideoEntity());
        }
        if (frameAdapter != null) {
            frameAdapter.setInitCount(frameAdapter.getInitCount() - 1);
            deletCount++;
        }
        hasResetFrame = true;
    }

    private int deletCount;

    /**
     * 根据view位置找到素材位置
     *
     * @param viewIndex
     * @return
     */
    public int findMaterialIndex(int viewIndex) {
        for (int key : indexMap.keySet()) {
            int value = indexMap.get(key);
            if (viewIndex == value) {
                return key;
            }
        }
        return -1;
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

    public void showNoticeDialog() {
        if (noticeDialog == null) {
            MyDialog.Builder builder = new MyDialog
                    .Builder(SelectFrameActivity.this)
                    .setMessage(mContext.getResources().getString(R.string.stringDeleteOrReplace))
                    .setPositive(mContext.getResources().getString(R.string.stringISee))
                    .setCancelable(true);
            noticeDialog = new MyDialog(builder);
        }
        if (!noticeDialog.isShowing()) {
            noticeDialog.show(this);
        }
    }

    @Override
    public void onBackPressed() {
        RecordUtil.deleteProduct(RecordManager.get().getProductEntity(), true);
        RecordManager.get().clearAll();
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        Download.pause(SelectFrameActivity.this);
        FileDownload.pause(SelectFrameActivity.this);

        if (prepareDialog != null) {
            prepareDialog.dismiss();
            prepareDialog = null;
        }
        super.onDestroy();
    }

    private static class VideoReceiver extends ResultReceiver {
        private WeakReference<SelectFrameActivity> activity;

        @SuppressLint("RestrictedApi")
        public VideoReceiver(SelectFrameActivity activity, Handler handler) {
            super(handler);
            this.activity = new WeakReference<>(activity);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String message = resultData.getString("message");
            int downloadIndex = resultData.getInt("index");
            KLog.i("TESTTAG", "downloadMaterial: " + downloadIndex + " resultCode " + resultCode + "Msg:" + message);
            if (activity != null && activity.get() != null) {
                switch (resultCode) {
                    case FileDownload.DOWNLOAD_SERVER_START://开始下载
                        DownloadBean downloadBean1 = resultData.getParcelable("downloadBean");
                        activity.get().onDownLoadStart(downloadBean1.downloadUrl);
                        break;
                    case FileDownload.RESULT_PREPARE:
//                        KLog.d("视频下载状态", "RESULT_PREPARE  activity.get().indexMap==" + activity.get().indexMap);
//                        activity.get().downloadStateList.set(downloadIndex, DOWNLOAD_STATE_DOWNLOADING);
//                        if (activity.get().hasResetFrame) {
//                            int currentIndex = activity.get().indexMap.get(downloadIndex);
//                            activity.get().itemViewList.get(currentIndex).showProgress();
//                        } else {
//                            activity.get().itemViewList.get(downloadIndex).showProgress();
//                        }
                        break;
                    case FileDownload.RESULT_PAUSE:
                        activity.get().dismissLoad();
                        activity.get().downloadStateList.set(downloadIndex, DOWNLOAD_STATE_ERROR);
                        if (activity.get().hasResetFrame) {
                            int currentIndex = activity.get().indexMap.get(downloadIndex);
                            activity.get().itemViewList.get(currentIndex).hideMaterialCover();
                        } else {
                            activity.get().itemViewList.get(downloadIndex).hideMaterialCover();
                        }
                        activity.get().refreshView();
                        break;
                    case FileDownload.RESULT_DOWNLOADING:
                        KLog.d("视频下载状态", "RESULT_DOWNLOADING  activity.get().indexMap==" + activity.get().indexMap);
                        int progress = (int) resultData.getFloat("percent", 0f);
                        if (activity.get().hasResetFrame) {
                            int currentIndex = activity.get().indexMap.get(downloadIndex);
                            activity.get().itemViewList.get(currentIndex).setProgress(progress);
                        } else {
                            activity.get().itemViewList.get(downloadIndex).setProgress(progress);
                        }
                        break;
                    case FileDownload.RESULT_ERROR:
                        DownloadBean downloadBeanError = resultData.getParcelable("downloadBean");
                        activity.get().onDownLoadSuccess(HttpConstant.Fail, downloadBeanError);

                        KLog.d("视频下载状态", "RESULT_ERROR  activity.get().indexMap==" + activity.get().indexMap);
//                        activity.get().showToast(message);
                        activity.get().downloadStateList.set(downloadIndex, DOWNLOAD_STATE_ERROR);
                        if (activity.get().hasResetFrame) {
                            int currentIndex = activity.get().indexMap.get(downloadIndex);
                            activity.get().itemViewList.get(currentIndex).showMaterialCover(false);
                        } else {
                            activity.get().itemViewList.get(downloadIndex).showMaterialCover(false);
                        }
                        activity.get().refreshView();
                        break;
                    case FileDownload.RESULT_COMPLETE:
                        activity.get().downloadStateList.set(downloadIndex, DOWNLOAD_STATE_DOWNLOADED);
                        DownloadBean downloadBean = resultData.getParcelable("downloadBean");

                        if (null != downloadBean) {
                            activity.get().onDownLoadSuccess(HttpConstant.SUCCESS, downloadBean);
                            String savePath = downloadBean.wholePathName;
                            KLog.i("hsing", "下载视频" + downloadIndex + "完成，savePath" + savePath);
                            ShortVideoEntity videoEntity = activity.get().videoList.get(downloadIndex);
                            if (null != videoEntity) {
                                videoEntity.importVideoPath = savePath;
                            }
                        }
                        KLog.d("视频下载状态", "下载完成  activity.get().indexMap==" + activity.get().indexMap);
                        if (activity.get().hasResetFrame) {
                            int currentIndex = activity.get().indexMap.get(downloadIndex);
                            activity.get().itemViewList.get(currentIndex).showMaterialCover(true);
                        } else {
                        }
                        activity.get().itemViewList.get(downloadIndex).hideProgressbar();
                        activity.get().refreshView();
                        break;
                    case FileDownload.RESULT_COMPLETE_ALL:
                        activity.get().setNextEnable();
                        break;
                }
            }

        }
    }

    long downloadStartTime;

    /**
     * 开始下载
     */
    private void onDownLoadStart(String url) {
        downloadStartTime = System.currentTimeMillis();
    }


    /**
     * 下载成功
     */
    private void onDownLoadSuccess(String code, DownloadBean downloadBean) {
        if (downloadBean == null)
            return;
        long duration = (System.currentTimeMillis() - downloadStartTime / 1000);
        File f = new File(downloadBean.wholePathName);
        if (!f.exists()) {
            return;
        }
        long fileSize = f.length() / 1024;//byte -》k
        int speed = (int) (fileSize / duration);//下载速度

        MaterialDownLoad materialDownLoad = new MaterialDownLoad();
        materialDownLoad.setUrl(downloadBean.downloadUrl);
        materialDownLoad.setDownload_len(fileSize + "");//Kb 确定单位
        materialDownLoad.setDownload_duration(duration + "");
        materialDownLoad.setDownload_speed(speed + "");//Kb/s 确定单位
        materialDownLoad.setFile_len("");//目标大小
        materialDownLoad.setMaterial_id(downloadBean.downloadId + "");//? 有问题
        materialDownLoad.setRes(code);//success fail cancel

        EventHelper.post(GlobalParams.EventType.TYPE_CREATE_DOWNLOAD, materialDownLoad);
    }

    @BindView(R.id.rl_custoomframeview)
    RelativeLayout rl_custoomframeview;

    public boolean getValue(String s) {
        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '[' || c == '{' || c == '(') {
                stack.push(c);
            }
            if (c == ']' || c == '}' || c == ')') {
                if (stack.empty() ||
                        c == ']' && stack.pop() != '[' ||
                        c == '}' && stack.pop() != '{' ||
                        c == ')' && stack.pop() != '(') {
                    return false;
                }
            }
        }
        return stack.empty();
    }
}

