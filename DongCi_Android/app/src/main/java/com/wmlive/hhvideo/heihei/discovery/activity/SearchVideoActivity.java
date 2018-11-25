package com.wmlive.hhvideo.heihei.discovery.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.BaseCompatActivity;
import com.wmlive.hhvideo.common.base.BaseModel;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.heihei.beans.discovery.LocalVideoBean;
import com.wmlive.hhvideo.heihei.discovery.DiscoveryUtil;
import com.wmlive.hhvideo.heihei.discovery.UriUtils;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.record.activity.LocalPublishActivity;
import com.wmlive.hhvideo.heihei.record.activity.TrimVideoActivity;
import com.wmlive.hhvideo.heihei.record.engine.config.MVideoConfig;
import com.wmlive.hhvideo.heihei.record.engine.constant.SdkConstant;
import com.wmlive.hhvideo.heihei.record.engine.listener.ExportListener;
import com.wmlive.hhvideo.heihei.record.engine.model.Scene;
import com.wmlive.hhvideo.heihei.record.engine.model.TranslateModel;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.manager.RecordSetting;
import com.wmlive.hhvideo.heihei.record.utils.RecordFileUtil;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtil;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtilSdk;
import com.wmlive.hhvideo.heihei.record.widget.CircleProgressDialog;
import com.wmlive.hhvideo.heihei.record.widget.SysAlertDialog;
import com.wmlive.hhvideo.utils.FileUtil;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.widget.dialog.PermissionDialog;
import com.wmlive.networklib.entity.EventEntity;
import com.wmlive.networklib.util.EventHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.wmlive.hhvideo.heihei.record.manager.RecordManager.PREFIX_COMBINE_FILE;
import static com.wmlive.hhvideo.heihei.record.manager.RecordManager.SUFFIX_VIDEO_FILE;

public class SearchVideoActivity extends DcBaseActivity implements TabLayout.OnTabSelectedListener {

    public static final String START_FROM_SELECT_FRAME = "start_from_select_frame";
    public static final String START_TYPE_FROM = "start_type_from";
    public static final String SHORT_VIDEO_INDEX = "short_video_index";
    public static final String SHORT_VIDEO_PATH = "short_video_path";

    public static final int TYPE_FROM_SEARCH = 10;
    public static final int TYPE_FROM_RECORD = 20;
    public static final int TYPE_FROM_DIRECT_UPLOAD = 30;//本地直接上传作品

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.frame_layout)
    FrameLayout frameLayout;
    @BindView(R.id.file_select_ll)
    LinearLayout fileSelectLl;
    private AllLocalViedoFragment allLocalViedoFragment;
    private LocalAlbumsFragment localAlbumsFragment;
    private int startType; // 启动选取视频的类型
    private int shortVideoIndex; // 启动选取视频的位置

    public static void startSearchVideoActivity(final BaseCompatActivity context, final int index, final int startType) {
        final BaseModel count = new BaseModel();
        new RxPermissions(context).requestEach(RecordSetting.RECORD_PERMISSIONS)
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
                                Intent intent = new Intent(context, SearchVideoActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putInt(START_TYPE_FROM, startType);
                                bundle.putInt(SHORT_VIDEO_INDEX, index);
                                intent.putExtras(bundle);
                                context.startActivity(intent);
                            } else if (result == -1) {
                                ToastUtil.showToast("请在系统设置中允许App运行必要的权限");
                            } else {
                                KLog.i("=====初始化相机失败");
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
    protected void onSingleClick(View v) {
        if (v.getId() == R.id.file_select_ll) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("video/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_search_video2;
    }

    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();
        KLog.i("initData--SearchVideoActivity>");
        if (null != intent) {
            EventHelper.register(this);
            startType = intent.getIntExtra(START_TYPE_FROM, TYPE_FROM_RECORD);
            shortVideoIndex = intent.getIntExtra(SHORT_VIDEO_INDEX, 0);
            setTitle("", true);
            setBlackToolbar();
            initView();
        } else {
            toastFinish();
        }
    }


    protected void initView() {
        fileSelectLl.setOnClickListener(this);

        tabLayout.addTab(tabLayout.newTab().setText("相册视频"));
        tabLayout.addTab(tabLayout.newTab().setText("全部视频"));

        allLocalViedoFragment = AllLocalViedoFragment.newInstance(startType, shortVideoIndex);
        localAlbumsFragment = LocalAlbumsFragment.newInstance(startType, shortVideoIndex);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.frame_layout, localAlbumsFragment);
        transaction.add(R.id.frame_layout, allLocalViedoFragment);
        transaction.show(localAlbumsFragment);
        transaction.hide(allLocalViedoFragment);
        transaction.commit();
        tabLayout.addOnTabSelectedListener(this);
    }


    private void prepareVideo(LocalVideoBean bean) {
        if (bean != null && !TextUtils.isEmpty(bean.path)) {

            File file = new File(bean.path);
            if (file.exists() && file.isFile()) {
                final BaseModel count = new BaseModel();
                new RxPermissions(this)
                        .requestEach(RecordSetting.RECORD_PERMISSIONS)
                        .subscribe(new Consumer<Permission>() {
                            @Override
                            public void accept(Permission permission) throws Exception {
                                KLog.i("====请求权限：" + permission.toString());
                                if (!permission.granted) {
                                    if (Manifest.permission.CAMERA.equals(permission.name)) {
                                        new PermissionDialog(SearchVideoActivity.this, 20).show();
                                    } else if (Manifest.permission.RECORD_AUDIO.equals(permission.name)) {
                                        new PermissionDialog(SearchVideoActivity.this, 10).show();
                                    }
                                } else {
                                    count.type++;
                                }
                                if (count.type == 3) {
                                    KLog.i("=====获取权限：成功");
                                    int result = -1;//-1表示权限获取失败，-2表示相机初始化失败，0表示权限和相机都成功
                                    result = RecordManager.get().initRecordCore(SearchVideoActivity.this) ? 0 : -2;
                                    if (result == 0) {
                                        //权限请求成功
                                        doTransformVideo(bean);
                                    } else if (result == -1) {
                                        ToastUtil.showToast("请在系统设置中允许App运行必要的权限");
                                    } else {
                                        KLog.i("=====初始化相机失败");
                                        ToastUtil.showToast("初始化相机失败");
                                        new PermissionDialog(SearchVideoActivity.this, 20).show();
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
            } else {
                showToast(R.string.hintErrorDataDelayTry);
            }
        } else {
            showToast(R.string.hintErrorDataDelayTry);
        }
    }

    CircleProgressDialog dialog = null;

    private void doTransformVideo(LocalVideoBean bean) {
        byte result = prepareDir(bean);
        if (result > -1) {
            if (result == 0) {
                final String exportPath = RecordManager.get().getProductEntity().baseDir
                        + File.separator
                        + PREFIX_COMBINE_FILE
                        + RecordFileUtil.getTimestampString()
                        + SUFFIX_VIDEO_FILE;
                KLog.i("======复制文件路径：" + exportPath);
                Disposable disposable = Observable.just(1)
                        .subscribeOn(Schedulers.io())
                        .map(new Function<Integer, Boolean>() {
                            @Override
                            public Boolean apply(Integer integer) throws Exception {
                                return FileUtil.copy(bean.path, exportPath);
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean copyOk) throws Exception {
                                if (copyOk) {
                                    RecordManager.get().getProductEntity().combineVideo = exportPath;
                                    RecordManager.get().getProductEntity().userId = AccountUtil.getUserId();
                                    KLog.i("=====复制文件成功:" + RecordManager.get().getProductEntity().combineVideoAudio);
//                                    LocalVideoTrimActivity.startLocalVideoTrimActivity(SearchVideoActivity.this, RecordManager.get().getProductEntity().combineVideoAudio);
                                    TrimVideoActivity.startTrimVideoActivity(SearchVideoActivity.this, shortVideoIndex, RecordManager.get().getProductEntity().combineVideo, startType);
                                } else {
                                    showToast("创建文件失败，请稍后重试");
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                KLog.e("======复制文件失败：" + throwable.getMessage());
                            }
                        });
            } else {
                List<Scene> list = TranslateModel.strToScence(bean.path);

                RecordUtilSdk.exportLocalVideo(list, new MVideoConfig(), new ExportListener() {
                    @Override
                    public void onExportStart() {
                        KLog.i("=====onJoinStart");
                        if (dialog == null) {
                            dialog = SysAlertDialog.createCircleProgressDialog(SearchVideoActivity.this,
                                    getString(R.string.stringTransferVideo), true, false);
                        }
                        if (!dialog.isShowing()) {
                            dialog.show();
                        }
                    }

                    @Override
                    public void onExporting(int progress, int max) {
                        KLog.i("=====onJoining,progress:" + progress + " ,max:" + max);
                        if (dialog != null) {
                            dialog.setProgress(progress / 10);
                        }
                    }

                    @Override
                    public void onExportEnd(int var1, String path) {
                        KLog.i("=====onJoinEnd,result:" + result + " ,message:" + path);
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        if (var1 == SdkConstant.RESULT_SUCCESS) {
                            LocalPublishActivity.startLocalPublishActivity(SearchVideoActivity.this, LocalPublishActivity.FORM_SEARCH);
                        } else {
                            showToast("无法使用该视频");
                        }
                    }
                });

            }
        } else {
            showToast("创建文件夹失败");
        }
    }


    private byte prepareDir(LocalVideoBean localVideoBean) {
        RecordManager.createLocalVideoEntity();
        if (TextUtils.isEmpty(RecordManager.get().getProductEntity().baseDir)) {
            String productPath = RecordFileUtil.createTimestampDir(RecordFileUtil.getTempDir(), "");
            if (TextUtils.isEmpty(productPath)) {
                KLog.i("====创建productDir文件夹失败");
                return -1;
            }
            RecordManager.get().getProductEntity().baseDir = productPath;
        }

        if (!RecordFileUtil.createDir(new File(RecordManager.get().getProductEntity().baseDir))) {
            KLog.i("====创建productDir文件夹失败");
            return -1;
        }
        KLog.i("====创建productDir文件夹成功：" + RecordManager.get().getProductEntity().baseDir);
        int[] wh = RecordUtil.getVideoWH(localVideoBean.path);
        byte result;
        //视频太宽或太高，需限制宽度到RecordSetting.LOCAL_UPLOAD_VIDEO_MAX
        if (wh[0] > RecordSetting.LOCAL_UPLOAD_VIDEO_MAX || wh[1] > RecordSetting.LOCAL_UPLOAD_VIDEO_MAX) {
            float ratio = wh[0] * 1f / wh[1];
            boolean isLandscape = wh[0] > wh[1];
            if (isLandscape) {
                localVideoBean.expectWidth = RecordSetting.LOCAL_UPLOAD_VIDEO_MAX;
                localVideoBean.expectHeight = (int) (RecordSetting.LOCAL_UPLOAD_VIDEO_MAX / ratio);
            } else {
                localVideoBean.expectHeight = RecordSetting.LOCAL_UPLOAD_VIDEO_MAX;
                localVideoBean.expectWidth = (int) (RecordSetting.LOCAL_UPLOAD_VIDEO_MAX * ratio);
            }
            result = 0;//临时不做处理 上传
        } else {
            localVideoBean.expectWidth = wh[0];
            localVideoBean.expectHeight = wh[1];
            result = 0;
        }
        KLog.i("======视频信息：" + localVideoBean);
        RecordManager.get().setExceptWH(localVideoBean.expectWidth, localVideoBean.expectHeight);
        return result;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFinishEvent(EventEntity entity) {
        if (entity.code == GlobalParams.EventType.TYPE_TRIM_FINISH) {
            finish();
        }
    }

    @Override
    public void onDestroy() {
        EventHelper.unregister(this);
        super.onDestroy();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int pos = tab.getPosition();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (pos == 0) {
            transaction.show(localAlbumsFragment);
            transaction.hide(allLocalViedoFragment);
        } else {
            transaction.show(allLocalViedoFragment);
            transaction.hide(localAlbumsFragment);
        }
        transaction.commit();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
            String path = UriUtils.getPath(this, uri);
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path);
                if (file != null && !file.getName().endsWith("mp4")) {
                    showToast(getString(R.string.selectVideoTypeError, (int) (RecordSetting.MIN_VIDEO_DURATION / 1000)));
                    return;
                }
                LocalVideoBean bean = new LocalVideoBean();
                bean.path = path;
                String ringDuring =DiscoveryUtil.getRingDuring(bean.path)[0];
                if (ringDuring != null)
                    bean.duration = Long.parseLong(ringDuring);
                if (bean.duration > 5000){
                    if (startType == TYPE_FROM_DIRECT_UPLOAD) {
                        prepareVideo(bean);
                    } else {
                        TrimVideoActivity.startTrimVideoActivity(this, shortVideoIndex, path, startType);
                    }
                }
            }
        }
    }
}
