package com.wmlive.hhvideo.heihei.record.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.os.ResultReceiver;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.loopback.DCLatencytestTool;
import com.example.loopback.DCLoopbackTool;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.base.BaseCompatActivity;
import com.wmlive.hhvideo.common.base.BaseModel;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.heihei.beans.frame.FrameInfo;
import com.wmlive.hhvideo.heihei.beans.frame.FrameSortBean;
import com.wmlive.hhvideo.heihei.beans.frame.Frames;
import com.wmlive.hhvideo.heihei.beans.opus.OpusMaterialEntity;
import com.wmlive.hhvideo.heihei.beans.opus.UploadMaterialEntity;
import com.wmlive.hhvideo.heihei.beans.record.MusicInfoEntity;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.beans.record.TopicInfoEntity;
import com.wmlive.hhvideo.heihei.mainhome.util.PublishUtils;
import com.wmlive.hhvideo.heihei.record.adapter.FrameAdapter;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.manager.RecordSetting;
import com.wmlive.hhvideo.heihei.record.presenter.SelectFramePresenter;
import com.wmlive.hhvideo.heihei.record.utils.RecordFileUtil;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtil;
import com.wmlive.hhvideo.heihei.record.widget.CircleProgressDialog;
import com.wmlive.hhvideo.heihei.record.widget.CustomFrameView;
import com.wmlive.hhvideo.heihei.record.widget.SmallFrameView;
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

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;
import io.reactivex.functions.Consumer;

/**
 * Created by lsq on 8/25/2017.
 * 选择画框页面
 */
public class SelectFrameActivity2 extends DcBaseActivity<SelectFramePresenter>
        implements SelectFramePresenter.ISelectFrameView, FrameAdapter.OnFrameItemClickListener {
    public static final byte VIDEO_TYPE_RECORD = 2; // 录制开拍 & 直接开拍
    public static final byte VIDEO_TYPE_TEAMWORK = 3; // 共同创作
    //    public static final String EXTRA_MUSIC_INFO = "extra_music_info";
    public static final String EXTRA_TOPIC_INFO = "extra_topic_info";
    public static final String EXTRA_VIDEO_INFO = "extra_video_info";
    public static final String EXTRA_IMPORT_TYPE = "extra_is_import_type";
    public static final String EXTRA_OPUS_ID = "extra_opus_id";
    public static final String EXTRA_FRAME_LAYOUT = "extra_frame_layout";
    public static final String EXTRA_SINGLE_VIDEO = "extra_video_single";
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

    @BindView(R.id.rl_custoomframeview)
    RelativeLayout rl_custoomframeview;

    private Handler handler;//下载handle
    private FrameAdapter frameAdapter;
    private byte importType;
    private Context mContext;
    private FrameInfo mFrameInfo;
    private List<FrameInfo> frameList; // 一种比例下的画框集合
    private List<FrameSortBean> layoutList; // 四种比例的画框集合
    private String currentGroup = "推荐";//当前分辨率tab
    private int selectFrameIndex;//共同创作画框的下标 第一次进来有用，用完就弃用

    //记录当前大画框名称
    private String currentFramName;

    private List<SmallFrameView> itemViewList;
    private ArrayList<String> tabNames = new ArrayList<>();//存放所有分比率分组的集合

    private Map<String, FrameSortBean> framGroups = new HashMap<>();


    private CircleProgressDialog prepareDialog;
    private ResultReceiver resultReceiver;
    private long opusId; // 作品ID
    private MyDialog noticeDialog;

    private Map<String, UploadMaterialEntity> mainDownloadMap = new HashMap<>();


    private String frameLayout;//当前画框的 Name

    public static void startSelectFrameActivity2(BaseCompatActivity context, MusicInfoEntity musicInfo, byte importType) {
        startSelectFrameActivity2(context, importType, null, -1, null);
//        DCLatencytestTool.startLatency(context);
    }

    public static void startSelectFrameActivity2(BaseCompatActivity context, TopicInfoEntity topicInfo, byte importType) {
        startSelectFrameActivity2(context, importType, topicInfo, -1, null);
//        DCLatencytestTool.startLatency(context);
    }

    public static void startSelectFrameActivity2(final BaseCompatActivity context, final byte importType, final long opusId, final String frameLayout) {
        startSelectFrameActivity2(context, importType, null, opusId, frameLayout);
//        DCLatencytestTool.startLatency(context);
    }

    private static void startSelectFrameActivity2(final BaseCompatActivity context, final byte importType, TopicInfoEntity topicInfo, final long opusId, final String frameLayout) {
        if (PublishUtils.showToast()) {
            return;
        }
        // 共同创作
        final BaseModel count = new BaseModel();
        new RxPermissions(context)
                .requestEach(RecordSetting.RECORD_PERMISSIONS)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        KLog.i("====请求权限：" + permission.toString());
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
                                Intent intent = new Intent(context, SelectFrameActivity2.class);
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
    protected int getLayoutResId() {
        return R.layout.activity_select_frame;
    }

    @Override
    protected void initData() {
        super.initData();
        mContext = SelectFrameActivity2.this;
        importType = getIntent().getByteExtra(EXTRA_IMPORT_TYPE, VIDEO_TYPE_RECORD);
        handler = new Handler(Looper.getMainLooper());
        initType();      //区分是不是踹跪
        initFrame();     //设置底部小画框集合
        setFrameView();  //设置大画框
        DCLoopbackTool.onStart(SelectFrameActivity2.this);
    }

    private void initType() {
        if (importType == VIDEO_TYPE_TEAMWORK) {
            opusId = getIntent().getLongExtra(EXTRA_OPUS_ID, -1);
            frameLayout = getIntent().getStringExtra(EXTRA_FRAME_LAYOUT);
            RecordManager.get().trimFile(new File(RecordFileUtil.getMaterialDir()));
            resultReceiver = new SelectFrameActivity2.VideoReceiver(this, handler);
            getPresenter().getOpusMaterial(opusId);//下载共同创作信息
            setTitle();
            top_bar.setVisibility(View.VISIBLE);
            tv_bar_right.setOnClickListener(this);
            iv_bar_left.setOnClickListener(this);
            tv_bar_right.setTextColor(mContext.getResources().getColor(R.color.hh_color_c));
            tv_bar_right.setEnabled(false);
            customFrameView.setEventListener(new CustomFrameView.EventListener() {
                @Override
                public void onChildClick(int index, int x, int y, int width, int height) {

                }

                @Override
                public void onChangePosition(int selectIndex, int targetIndex) {
                    if (selectIndex == targetIndex) {
                        return;
                    }
                    UploadMaterialEntity material_s = itemViewList.get(selectIndex).materialEntity;
                    UploadMaterialEntity material_t = itemViewList.get(targetIndex).materialEntity;
                    if (material_t != null) {
                        material_t.index = selectIndex;
                    }
                    if (material_s != null) {
                        material_s.index = targetIndex;
                    }
                    itemViewList.get(selectIndex).setUIState(material_t);
                    itemViewList.get(targetIndex).setUIState(material_s);
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
                public void deleteFramItem(int i) {
                    UploadMaterialEntity materialEntity = itemViewList.get(i).materialEntity;
                    if (materialEntity == null)
                        return;
                    String videopath = materialEntity.material_video;
                    if (TextUtils.isEmpty(videopath)) {
                        videopath = materialEntity.material_video_high;
                    }
                    mainDownloadMap.remove(videopath);
                    if (materialEntity.downloadState != FileDownload.RESULT_COMPLETE) {
                        BaseDownloadTask baseDownloadTask = FileDownload.taskArray.get(materialEntity.downloadId);
                        if (baseDownloadTask != null) {
                            FileDownload.pause(materialEntity.downloadId, baseDownloadTask.getId());
                        }
                    }
                    itemViewList.get(i).setUIState(null);
                    frameAdapter.setInitCount(frameAdapter.getInitCount() - 1);
                }
            });
        } else {

        }
    }

    private void initFrame() {
        frameAdapter = new FrameAdapter(new ArrayList<FrameInfo>());
        frameAdapter.setItemClickListener(this);
        rvFrames.setLayoutManager(new GridLayoutManager(SelectFrameActivity2.this, 4, GridLayoutManager.VERTICAL, false));
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
                if (mainDownloadMap != null && mainDownloadMap.size() > 0) {
                    frameAdapter.setInitPosition(getFrameIndex(currentFramName, frameList), mainDownloadMap.size());
                } else {
                    frameAdapter.setInitPosition(getFrameIndex(currentFramName, frameList), -1);
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
        String frames = SPUtils.getString(DCApplication.getDCApp(), SPUtils.FRAME_LAYOUT_DATA, "");
        Frames f = JsonUtils.parseObject(frames, Frames.class);
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
            currentFramName = mFrameInfo.name;
            RecordManager.get().newProductEntity(mFrameInfo);
            frameAdapter.addData(frameList);
        }
    }

    private void setFrameView() {
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
        }
    }

    //根据画框名字获取画框
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

    public int getFrameIndex(String name, List<FrameInfo> frameList) {
        for (int i = 0; i < frameList.size(); i++) {
            if (name.equals(frameList.get(i).name)) {
                return i;
            }
        }
        return -1;
    }


    @Override
    protected void onSingleClick(View v) {
        if (v == tv_bar_right) {
            if (mainDownloadMap.size() > 0) {
                if (mainDownloadMap.size() == mFrameInfo.getLayout().size()) {
                    showNoticeDialog();
                    return;
                }
                List<ShortVideoEntity> newVideoList = new ArrayList<>();
                for (int i = 0; i < itemViewList.size(); i++) {
                    ShortVideoEntity videoEntity = new ShortVideoEntity();

                    UploadMaterialEntity materialEntity = itemViewList.get(i).materialEntity;
                    if (materialEntity != null && !TextUtils.isEmpty(materialEntity.fileDownloadPath)) {
                        videoEntity.combineVideoAudio = materialEntity.fileDownloadPath;
                        videoEntity.importVideoPath = materialEntity.fileDownloadPath;
                        videoEntity.originalId = materialEntity.ori_id;
                        if (materialEntity.ori_id == 0) {
                            videoEntity.originalId = materialEntity.id;
                        }
                        videoEntity.setImport(true, false);
                        videoEntity.setVideoType(String.valueOf(VIDEO_TYPE_TEAMWORK));
                    }
                    newVideoList.add(videoEntity);
                }
                RecordManager.get().newProductEntity(mFrameInfo);
                RecordManager.get().getProductEntity().setShortVideos(newVideoList);
                RecordManager.get().updateProduct();

            }
            DCLoopbackTool.stopBind();
            RecordActivitySdk.startRecordActivity(SelectFrameActivity2.this, RecordActivitySdk.TYPE_TOGETHER);
        } else if (v == iv_bar_left) {
            onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        DCLoopbackTool.stopBind();
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
        if (enable) {
            mFrameInfo = info;
            currentFramName = mFrameInfo.name;
            RecordManager.get().newProductEntity(mFrameInfo);
            setFrameView();
            if (importType == VIDEO_TYPE_TEAMWORK) {
                int i = 0;
                Iterator<Map.Entry<String, UploadMaterialEntity>> iterator = mainDownloadMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    UploadMaterialEntity value = iterator.next().getValue();
                    value.index = i;
                    itemViewList.get(i).setUIState(value);
                    i++;
                }
            }
        }

    }


    @Override
    public void onGetMaterial(OpusMaterialEntity response) {
        List<UploadMaterialEntity> materials = response.materials;
        if(materials==null)
            return;
        ArrayList<DownloadBean> downloadList = new ArrayList<>();
        if (materials == null) return;
        if (importType == VIDEO_TYPE_TEAMWORK && materials.size() == 1) {
            frameAdapter.setInitPosition(selectFrameIndex, 2);
        } else {
            frameAdapter.setInitPosition(selectFrameIndex, materials.size());
        }
        //1、download list  2、地址——状态 映射关系 3、作品素材集合 4、ui画框显示集合
        if (materials.size() > 0) {
            for (int i = 0; i < materials.size(); i++) {
                //1
                UploadMaterialEntity uploadMaterialEntity = materials.get(i);
                uploadMaterialEntity.index = i;
                //映射
                String videopath = uploadMaterialEntity.material_video;
                if (TextUtils.isEmpty(videopath)) {
                    videopath = uploadMaterialEntity.material_video_high;
                }
                mainDownloadMap.put(videopath, uploadMaterialEntity);
                //下载任务集合
                if (!TextUtils.isEmpty(videopath)) {
                    int downloadId = FileDownloadUtils.generateId(videopath, RecordFileUtil.getMaterialDir());
                    uploadMaterialEntity.downloadId = downloadId;
                    DownloadBean downloadBean = new DownloadBean(downloadId, videopath,
                            RecordFileUtil.getMaterialDir(), "", "mp4", uploadMaterialEntity.material_index);
                    downloadList.add(downloadBean);
                }
                //ItemView的状态 1、封面 2、进度、 3、失败后的刷新按钮 4、视频时长
                itemViewList.get(i).setUIState(uploadMaterialEntity);
                KLog.d("onGetMaterial: uploadMaterialEntity==" + uploadMaterialEntity);

            }
        }
        //下载任务开启
        FileDownload.start(mContext, downloadList, resultReceiver, true);

    }

    /**
     * SmallFrameView点击回调
     */
    private SmallFrameView.OnSmallFrameClickListener mSmallFrameClickListener = new SmallFrameView.OnSmallFrameClickListener() {
        @Override
        public void onRefreshClick(int index, SmallFrameView view) {
            String videopath = view.materialEntity.material_video;
            if (TextUtils.isEmpty(videopath)) {
                videopath = view.materialEntity.material_video_high;
            }
            if (!TextUtils.isEmpty(videopath)) {
                DownloadBean downloadBean = new DownloadBean(view.materialEntity.material_index, videopath,
                        RecordFileUtil.getMaterialDir(), "", "mp4");
                FileDownload.start(mContext, downloadBean, resultReceiver, true, true);
            }
        }

        @Override
        public void onDeleteClick(int index, SmallFrameView view) {
        }

    };

    private void setNextEnable() {
        if (tv_bar_right != null) {
            tv_bar_right.setEnabled(true);
            tv_bar_right.setTextColor(getResources().getColor(R.color.hh_color_g));
        }
    }

    public void showNoticeDialog() {
        if (noticeDialog == null) {
            MyDialog.Builder builder = new MyDialog
                    .Builder(SelectFrameActivity2.this)
                    .setMessage(mContext.getResources().getString(R.string.stringDeleteOrReplace))
                    .setPositive(mContext.getResources().getString(R.string.stringISee))
                    .setCancelable(true);
            noticeDialog = new MyDialog(builder);
        }
        if (!noticeDialog.isShowing()) {
            noticeDialog.show(this);
        }
    }

    private SmallFrameView getItemView(DownloadBean bean, int downloadState) {
        UploadMaterialEntity uploadMaterialEntity = mainDownloadMap.get(bean.downloadUrl);
        if (uploadMaterialEntity != null) {
            uploadMaterialEntity.downloadState = downloadState;
            KLog.d("TESTTAG2", "uploadMaterialEntity.index==" + uploadMaterialEntity.index);
            return itemViewList.get(uploadMaterialEntity.index);
        } else {
            return null;
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
        Download.pause(SelectFrameActivity2.this);
        FileDownload.pause(SelectFrameActivity2.this);

        if (prepareDialog != null) {
            prepareDialog.dismiss();
            prepareDialog = null;
        }
        super.onDestroy();
    }

    private static class VideoReceiver extends ResultReceiver {
        private WeakReference<SelectFrameActivity2> activity;

        @SuppressLint("RestrictedApi")
        public VideoReceiver(SelectFrameActivity2 activity, Handler handler) {
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
                        SmallFrameView itemView = activity.get().getItemView(downloadBean, FileDownload.RESULT_DOWNLOADING);
                        if (itemView != null) {
                            itemView.setProgress((int) resultData.getFloat("percent", 0f));
                        }
                        break;
                    case FileDownload.RESULT_ERROR:
                        SmallFrameView itemView1 = activity.get().getItemView(downloadBean, FileDownload.RESULT_ERROR);
                        if (itemView1 != null)
                            itemView1.showMaterialCover(false);
                        break;
                    case FileDownload.RESULT_COMPLETE:
                        if (null != downloadBean) {
                            String savePath = downloadBean.wholePathName;
                            SmallFrameView itemView2 = activity.get().getItemView(downloadBean, FileDownload.RESULT_COMPLETE);
                            if (itemView2 != null) {
                                itemView2.showMaterialCover(true);
                                itemView2.materialEntity.fileDownloadPath = savePath;
                            }
                        }
                        break;
                    case FileDownload.RESULT_COMPLETE_ALL:
                        activity.get().setNextEnable();
                        break;
                }
            }
        }
    }
}

