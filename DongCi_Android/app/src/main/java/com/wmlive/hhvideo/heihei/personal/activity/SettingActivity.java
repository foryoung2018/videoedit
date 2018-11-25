package com.wmlive.hhvideo.heihei.personal.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.common.manager.MyAppActivityManager;
import com.wmlive.hhvideo.common.manager.gift.GiftManager;
import com.wmlive.hhvideo.common.manager.message.MessageManager;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.mainhome.activity.MainActivity;
import com.wmlive.hhvideo.heihei.mainhome.util.ScreenShot;
import com.wmlive.hhvideo.heihei.personal.presenter.SettingPresenter;
import com.wmlive.hhvideo.heihei.personal.view.ISettingView;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.splash.SplashActivity;
import com.wmlive.hhvideo.service.DcWebSocketService;
import com.wmlive.hhvideo.utils.AppCacheFileUtils;
import com.wmlive.hhvideo.utils.CommonUtils;
import com.wmlive.hhvideo.utils.FileUtil;
import com.wmlive.hhvideo.widget.dialog.CustomDialog;

import java.io.File;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by XueFei on 2017/6/3.
 * <p>
 * 设置
 */

public class SettingActivity extends DcBaseActivity implements ISettingView {
    @BindView(R.id.btn_clear_memory)
    RelativeLayout btnClearMemory;
    @BindView(R.id.tv_memory_size)
    TextView tvMemorySize;
    @BindView(R.id.btn_common)
    RelativeLayout btnCommon;
    @BindView(R.id.btn_about)
    RelativeLayout btnAbout;
    @BindView(R.id.rlBlackList)
    RelativeLayout rlBlackList;
    @BindView(R.id.rlUseHelp)
    RelativeLayout rlUseHelp;
    @BindView(R.id.rlNetCheck)
    RelativeLayout rlNetCheck;
    @BindView(R.id.btn_unlogin)
    Button btnUnlogin;

    @BindView(R.id.tv_sys_version)
    TextView tv_sys_version;
    private SettingPresenter settingPresenter;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(R.string.setting_title, true);
        settingPresenter = new SettingPresenter(this);
        addPresenter(settingPresenter);
        bindListener();
        tv_sys_version.setText(CommonUtils.getVersion(GlobalParams.Config.IS_DEBUG));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tvMemorySize != null) {
            tvMemorySize.setText(String.valueOf(getCacheFileSize()));
        }
    }

    private void bindListener() {
        btnClearMemory.setOnClickListener(this);
        btnCommon.setOnClickListener(this);
        btnAbout.setOnClickListener(this);
        btnUnlogin.setOnClickListener(this);
        rlBlackList.setOnClickListener(this);
        rlUseHelp.setOnClickListener(this);
        rlNetCheck.setOnClickListener(this);
    }

    private CustomDialog customDialog;

    @Override
    protected void onSingleClick(View v) {
        switch (v.getId()) {
            case R.id.btn_clear_memory:
                customDialog = new CustomDialog(this, R.style.BaseDialogTheme);
                customDialog.setContent(R.string.setting_clear_memory_dialog);
                customDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        customDialog.dismiss();
                        showToast(R.string.setting_cleared_memory);
                        FileUtil.deleteFiles(Glide.getPhotoCacheDir(SettingActivity.this));
                        AppCacheFileUtils.clearVideoCache();
                        AppCacheFileUtils.clearAppCachePath();
                        AppCacheFileUtils.initClearAppCachePath(getApplicationContext());
                        tvMemorySize.setText("0MB");
                    }
                });
                customDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        customDialog.dismiss();
                    }
                });
                customDialog.show();
                break;
//            case R.id.btn_common:
//                WebViewActivity.startWebActivity(this, InitCatchData.sysServiceTerms(), getString(R.string.setting_common));
//                break;
            case R.id.btn_about:
                WebViewActivity.startWebActivity(this, InitCatchData.sysAboutUs(), getString(R.string.setting_about));
                break;
            case R.id.btn_unlogin:
                if (RecordManager.get().hasPublishingProduct()) {
                    showToast("正在上传作品，请确保上传完成之后再退出当前账号");
                    return;
                }
                customDialog = new CustomDialog(this, R.style.BaseDialogTheme);
                customDialog.setContent(R.string.user_logout);
                customDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        customDialog.dismiss();
                        settingPresenter.loginOut();

                    }
                });
                customDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        customDialog.dismiss();
                    }
                });
                customDialog.show();
                break;
            case R.id.rlBlackList:
                startActivity(new Intent(this, BlankListActivity.class));
                break;
            case R.id.rlUseHelp:
                String url = InitCatchData.getUsinghelp();
                if (!TextUtils.isEmpty(url)) {
                    WebViewActivity.startWebActivity(this, url, "使用帮助");
                } else {
                    showToast("参数错误，无效的url");
                }
                break;
            case R.id.rlNetCheck:
                startActivity(new Intent(this,CheckNetWorkActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public void handleLogoutSucceed() {
        showToast("退出成功");
        GiftManager.get().stopGiftService();
        AccountUtil.clearAccount();
        DcWebSocketService.stopSocket(this);
        MyAppActivityManager.getInstance().popAllActivityExceptOne(SettingActivity.class);
        MessageManager.get().deleteRedundantRecord();
        getWeakHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }, 1000);
    }

    @Override
    public void handlerLogoutFailure(String error_msg) {
        showToast(error_msg);
    }

    /**
     * 获取缓存的文件大小
     *
     * @return
     */
    public String getCacheFileSize() {
        long tempSize = FileUtil.getFileSize(new File((AppCacheFileUtils.getAppTempPath())));//临时文件
        long downSize = FileUtil.getFileSize(new File((AppCacheFileUtils.getAppDownloadPath())));//下载目录
        long httpSize = FileUtil.getFileSize(new File((AppCacheFileUtils.getAppHttpCachePath())));//网络缓存目录
        long imageSize = FileUtil.getFileSize(new File((AppCacheFileUtils.getAppImagesPath())));//图片缓存目录

//        long logSize = FileUtil.getFileSize(new File((AppCacheFileUtils.getAppLogPath())));//日志缓存
//        long dbSize = FileUtil.getFileSize(new File((AppCacheFileUtils.getAppDbPath())));//数据库
        long videoCacheSize = FileUtil.getFileSize(new File(AppCacheFileUtils.getAppVideoCachePath()));//视频缓存
        long musicCacheSize = FileUtil.getFileSize(new File(AppCacheFileUtils.getAppMusicCachePath()));//音乐缓存
        long glidCacheSize = FileUtil.getFileSize(Glide.getPhotoCacheDir(this));


        long cacheAllSize = tempSize + downSize + imageSize + videoCacheSize + musicCacheSize + httpSize + glidCacheSize;
        return FileUtil.getFormatSize(cacheAllSize);
    }
}
