package com.wmlive.hhvideo.heihei.discovery.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.FrameLayout;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wmlive.hhvideo.common.base.BaseCompatActivity;
import com.wmlive.hhvideo.common.base.BaseModel;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.heihei.beans.record.MusicInfoEntity;
import com.wmlive.hhvideo.heihei.beans.subject.TopicInfo;
import com.wmlive.hhvideo.heihei.discovery.fragment.SearchMusicFragment;
import com.wmlive.hhvideo.heihei.record.activity.SelectFrameActivity;
import com.wmlive.hhvideo.heihei.record.activity.SelectFrameActivity2;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.manager.RecordSetting;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.widget.dialog.PermissionDialog;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;
import io.reactivex.functions.Consumer;

/**
 * Created by lsq on 6/2/2017.
 * 搜索音乐和视频的页面
 */

public class SearchMusicVideoActivity extends DcBaseActivity {

    private String START_RECORD_TYPE = "start_record_type";

    @BindView(R.id.tabMusic)
    TabLayout tabMusic;
    @BindView(R.id.vpContainer)
    FrameLayout vpContainer;

    private String startType;
    private TopicInfo topicInfo;

    public static void startSearchMusicActivity(final BaseCompatActivity context, final TopicInfo topicInfo) {
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
                                Intent intent = new Intent(context, SearchMusicVideoActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("topicInfo", topicInfo);
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

    /**
     * 从DyUIAPICallbackImpl类启动Activity
     *
     * @param context
     */
    public static void startForMusic(Activity context, int requestCode) {
        Intent intent = new Intent(context, SearchMusicVideoActivity.class);
        intent.putExtra("startType", "fromDyUIAPI");
        context.startActivityForResult(intent, requestCode);
    }


    @Override
    protected void onSingleClick(View v) {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_search_music_video;
    }


    @Override
    protected void initData() {
        super.initData();
        topicInfo = getIntent().getParcelableExtra("topicInfo");
        startType = getIntent().getStringExtra("startType");
        tabMusic.setVisibility("fromDyUIAPI".equalsIgnoreCase(startType) ? View.GONE : View.VISIBLE);
        initFragment();
        setTabLayout();
    }

    private void initFragment() {
        Fragment fragment = SearchMusicFragment.newInstance(startType, topicInfo);
        FragmentManager ft = getSupportFragmentManager();
        ft.beginTransaction().replace(R.id.vpContainer, fragment).commitAllowingStateLoss();
    }

    private void setTabLayout() {
        tabMusic.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab != null) {
                    switch (tab.getPosition()) {
                        case 0:
                            SelectFrameActivity.startSelectFrameActivity(SearchMusicVideoActivity.this, new MusicInfoEntity(), SelectFrameActivity.VIDEO_TYPE_IMPORT);
                            getWeakHandler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (tabMusic != null && tabMusic.getTabAt(1) != null) {
                                                tabMusic.getTabAt(1).select();
                                            }
                                        }
                                    });
                                }
                            }, 1000);
                            break;
                        case 1:
                            break;
                        case 2:
                            SelectFrameActivity.startSelectFrameActivity(SearchMusicVideoActivity.this, new MusicInfoEntity(), SelectFrameActivity2.VIDEO_TYPE_RECORD);
                            getWeakHandler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (tabMusic != null && tabMusic.getTabAt(1) != null) {
                                                tabMusic.getTabAt(1).select();
                                            }
                                        }
                                    });
                                }
                            }, 1000);
                            break;
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if (tabMusic != null && tabMusic.getTabAt(1) != null) {
            tabMusic.getTabAt(1).select();
        }
    }

}
