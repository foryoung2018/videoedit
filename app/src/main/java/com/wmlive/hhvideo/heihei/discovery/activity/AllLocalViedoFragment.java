package com.wmlive.hhvideo.heihei.discovery.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.BaseCompatActivity;
import com.wmlive.hhvideo.common.base.BaseFragment;
import com.wmlive.hhvideo.common.base.BaseModel;
import com.wmlive.hhvideo.heihei.beans.discovery.LocalVideoBean;
import com.wmlive.hhvideo.heihei.discovery.DiscoveryUtil;
import com.wmlive.hhvideo.heihei.discovery.adapter.SearchVideoAdapter;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.record.activity.EditLocalMvActivity;
import com.wmlive.hhvideo.heihei.record.activity.LocalPublishActivity;
import com.wmlive.hhvideo.heihei.record.activity.RecordMvActivity;
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
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.FileUtil;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.PermissionUtils;
import com.wmlive.hhvideo.utils.SdkUtils;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.widget.dialog.PermissionDialog;
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

/**
 * 本地所有视频列表
 * Author：create by jht on 2018/9/18 16:07
 * Email：haitian.jiang@welines.cn
 */
public class AllLocalViedoFragment extends BaseFragment implements SearchVideoAdapter.OnVideoSelectListener {
    public static final int TYPE_FROM_DIRECT_UPLOAD = 30;//本地直接上传作品
    private static final long FAST_CLICK_DELAY_TIME =500;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tvBlank)
    TextView tvBlank;
    @BindView(R.id.dialog_iv)
    ImageView dialogIv;
    private SearchVideoAdapter searchVideoAdapter;
    private Disposable disposable;
    private Animator loadingAnimator;
    private List<LocalVideoBean> allLocalVideoBeen;
    private int startType; // 启动选取视频的类型
    private int shortVideoIndex; // 启动选取视频的位置
    private long minDuration;

    public static AllLocalViedoFragment newInstance(int startType, int shortVideoIndex, long minDuration) {
        AllLocalViedoFragment fragment = new AllLocalViedoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("startType", startType);
        bundle.putInt("shortVideoIndex", shortVideoIndex);
        bundle.putLong("short_video_duration", minDuration);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initData() {
        super.initData();
        initVideoLoading();
        startType = getArguments().getInt("startType");
        shortVideoIndex = getArguments().getInt("shortVideoIndex");
        minDuration  = getArguments().getLong("short_video_duration");
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        searchVideoAdapter = new SearchVideoAdapter();
        searchVideoAdapter.setOnVideoSelectListener(this);
        recyclerView.setAdapter(searchVideoAdapter);

        if (!(SdkUtils.isMarshmallow() && !PermissionUtils.hasSDCardRWPermission(getActivity()))) {
            final long start = System.currentTimeMillis();
            KLog.i("开始扫描视频：" + start);
            showVideoLoading();
            disposable = DiscoveryUtil.getAllVideos(getActivity())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<LocalVideoBean>>() {
                        @Override
                        public void accept(List<LocalVideoBean> localVideoBeen) throws Exception {
                            if (GlobalParams.Config.IS_DEBUG) {
                                for (LocalVideoBean bean : localVideoBeen) {
                                    KLog.i("扫描到视频：" + bean.toString());
                                }
                            }
                            allLocalVideoBeen  = localVideoBeen;
                            if (CollectionUtil.isEmpty(localVideoBeen)) {
                                findViewById(R.id.llBlankPanel).setVisibility(View.VISIBLE);
                                if(findViewById(R.id.ivBlank)!=null){
                                    findViewById(R.id.ivBlank).setVisibility(View.VISIBLE);
                                    tvBlank.setText(R.string.search_local_video_null);
                                    recyclerView.setVisibility(View.GONE);
                                }
                            } else {
                                findViewById(R.id.llBlankPanel).setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                searchVideoAdapter.setData(allLocalVideoBeen);
                            }
                            dismissVideoLoading();
                            KLog.i("完成扫描视频：" + (System.currentTimeMillis() - start));
                        }
                    });
        }
    }
    //初始化加载动画
    private void initVideoLoading() {
        loadingAnimator = AnimatorInflater.loadAnimator(getActivity(), R.animator.loading);
        loadingAnimator.setInterpolator(new LinearInterpolator());
        loadingAnimator.setTarget(dialogIv);
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public void onVideoSelect(LocalVideoBean bean) {
        if (bean != null && !TextUtils.isEmpty(bean.path)) {
            if(minDuration>0){
                if(bean.duration<=minDuration){
                    showToast(getString(R.string.change_speed_error_6s, (int) (minDuration / 1000)));
                    return;
                }else if(bean.duration>=1000*60*10){
                    showToast(getString(R.string.change_speed_error_10min));
                    return;
                }
                EditLocalMvActivity.startEditLocalMvActivity(getActivity(),startType,shortVideoIndex,bean.path,minDuration);
            } else {
                if (bean.duration < RecordSetting.MIN_VIDEO_DURATION) {
                    showToast(getString(R.string.change_speed_error_6s, (int) (RecordSetting.MIN_VIDEO_DURATION / 1000)));
                    return;
                }
                if (!bean.path.endsWith("mp4")) {
                    showToast(getString(R.string.selectVideoTypeError, (int) (RecordSetting.MIN_VIDEO_DURATION / 1000)));
                    return;
                }
                if (startType == TYPE_FROM_DIRECT_UPLOAD) {
                    prepareVideo(bean);
                } else if (startType == SearchVideoActivity.TYPE_FROM_RECORDMV) {

                } else {
                    dialog = SysAlertDialog.createCircleProgressDialog(getActivity(),
                            getString(R.string.loading), true, false);
                    if (!dialog.isShowing()) {
                        dialog.show();
                    }
                    TrimVideoActivity.startTrimVideoActivity(getActivity(), shortVideoIndex, bean.path, startType);
                }
            }


        } else {
            showToast(R.string.hintErrorDataDelayTry);
        }
    }

    private void prepareVideo(LocalVideoBean bean) {
        if (System.currentTimeMillis() - lastClickTime < FAST_CLICK_DELAY_TIME){
            return;
        }
        lastClickTime = System.currentTimeMillis();



        if (bean != null && !TextUtils.isEmpty(bean.path)) {
//            if (dialog == null) {
            dialog = SysAlertDialog.createCircleProgressDialog(getActivity(),
                    getString(R.string.loading), true, false);
//            }
            if (!dialog.isShowing()) {
                dialog.show();
            }
            KLog.i("path---==>"+bean.path);
            File file = new File(bean.path);
            if (file.exists() && file.isFile()) {
                final BaseModel count = new BaseModel();
                new RxPermissions(getActivity())
                        .requestEach(RecordSetting.RECORD_PERMISSIONS)
                        .subscribe(new Consumer<Permission>() {
                            @Override
                            public void accept(Permission permission) throws Exception {
                                KLog.i("====请求权限：" + permission.toString());
                                if (!permission.granted) {
                                    if (Manifest.permission.CAMERA.equals(permission.name)) {
                                        new PermissionDialog((BaseCompatActivity) getActivity(), 20).show();
                                    } else if (Manifest.permission.RECORD_AUDIO.equals(permission.name)) {
                                        new PermissionDialog((BaseCompatActivity) getActivity(), 10).show();
                                    }
                                } else {
                                    count.type++;
                                }
                                if (count.type == 3) {
                                    KLog.i("=====获取权限：成功");
                                    int result = -1;//-1表示权限获取失败，-2表示相机初始化失败，0表示权限和相机都成功
                                    result = RecordManager.get().initRecordCore(getContext()) ? 0 : -2;
                                    if (result == 0) {
                                        //权限请求成功
                                        doTransformVideo(bean);
                                        return;
                                    } else if (result == -1) {
                                        ToastUtil.showToast("请在系统设置中允许App运行必要的权限");
                                    } else {
                                        KLog.i("=====初始化相机失败");
                                        ToastUtil.showToast("初始化相机失败");
                                        new PermissionDialog((BaseCompatActivity) getActivity(), 20).show();
                                    }
                                    if(dialog!=null)
                                        dialog.dismiss();
                                }
                            }

                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                throwable.getMessage();
                                KLog.i("=====初始化相机失败:" + throwable.getMessage());
                                ToastUtil.showToast("该视频已经损坏");
                                if(dialog!=null)
                                    dialog.dismiss();
                            }
                        });
            } else {
                showToast(R.string.hintVideoNotExist);
                if(dialog!=null&& dialog.isShowing())
                    dialog.dismiss();
                allLocalVideoBeen.remove(bean);
                searchVideoAdapter.notifyDataSetChanged();
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
                                    TrimVideoActivity.startTrimVideoActivity(getActivity(), shortVideoIndex, RecordManager.get().getProductEntity().combineVideo, startType);
                                } else {
                                    showToast("创建文件失败，请稍后重试");
                                }
                                if(dialog!=null)
                                    dialog.dismiss();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                KLog.e("======复制文件失败：" + throwable.getMessage());
                                if(dialog!=null)
                                    dialog.dismiss();
                            }
                        });
            } else {
                List<Scene> list = TranslateModel.strToScence(bean.path);

                RecordUtilSdk.exportLocalVideo(list, new MVideoConfig(), new ExportListener() {
                    @Override
                    public void onExportStart() {
                        KLog.i("=====onJoinStart");
//                        if (dialog == null) {
                        dialog = SysAlertDialog.createCircleProgressDialog(getActivity(),
                                getString(R.string.stringTransferVideo), true, false);
//                        }
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
                            LocalPublishActivity.startLocalPublishActivity((BaseCompatActivity) getActivity(), LocalPublishActivity.FORM_SEARCH);
                        } else {
                            showToast(R.string.generate_video_fail);
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

    @Override
    protected int getBaseLayoutId() {
        return R.layout.fragment_search_video;
    }

    @Override
    protected void onSingleClick(View v) {
    }

    @Override
    public void onResume() {
        super.onResume();
        if(dialog!=null)
            dialog.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != disposable && !disposable.isDisposed()) {
            disposable.dispose();
        }
        if(dialog!=null&& dialog.isShowing())
            dialog.dismiss();
    }

    public void dismissVideoLoading() {
        if (loadingAnimator != null) {
            loadingAnimator.cancel();
            loadingAnimator = null;
        }
        if (dialogIv != null) {
            dialogIv.clearAnimation();
            dialogIv.setVisibility(View.GONE);
        }
        tvBlank.setVisibility(View.GONE);
    }

    public void showVideoLoading() {
        if (dialogIv != null) {
            dialogIv.setVisibility(View.VISIBLE);
        }
        tvBlank.setVisibility(View.VISIBLE);
        initVideoLoading();
        loadingAnimator.start();
    }
}
