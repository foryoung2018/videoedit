package com.wmlive.hhvideo.heihei.mainhome.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.BaseCompatActivity;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.common.manager.DcIjkPlayerManager;
import com.wmlive.hhvideo.heihei.beans.main.MultiTypeVideoBean;
import com.wmlive.hhvideo.heihei.mainhome.fragment.RecommendFragment;
import com.wmlive.hhvideo.heihei.mainhome.fragment.VideoDetailListFragment;
import com.wmlive.hhvideo.utils.CommonUtils;

import java.io.Serializable;

import cn.magicwindow.mlink.annotation.MLinkRouter;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 5/7/2018 - 11:12 AM
 * 类描述：
 */
@MLinkRouter(keys = {GlobalParams.MWConfig.LINK_KEY_OPUS})
public class VideoDetailListActivity extends DcBaseActivity {
    private VideoDetailListFragment videoDetailListFragment;
    private boolean isLinkJump;
    private boolean isMwJump;
    private int videoType;

    public static void startVideoDetailListActivity(Context activity, int fromPageId,
                                                    int videoType,
                                                    MultiTypeVideoBean multiTypeVideoBean, View view, View cover) {
        Intent intent = new Intent(activity, VideoDetailListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(VideoDetailListFragment.KEY_FROM_PAGE_ID, fromPageId);
        bundle.putInt(VideoDetailListFragment.KEY_VIDEO_TYPE, videoType);
        bundle.putSerializable(VideoDetailListFragment.KEY_VIDEO_LIST, (Serializable) multiTypeVideoBean);
        intent.putExtras(bundle);
//        activity.startActivity(intent);

        if (activity instanceof BaseCompatActivity && view != null && cover != null) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((BaseCompatActivity) activity,
                    new Pair<>(view, ViewCompat.getTransitionName(view)),
                    new Pair<>(cover, ViewCompat.getTransitionName(cover)));
            activity.startActivity(intent, options.toBundle());
        } else {
            activity.startActivity(intent);
        }

    }

    @Override
    protected void onSingleClick(View v) {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_video_detail_list;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Window window = getWindow();
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(Color.TRANSPARENT);
//        window.setNavigationBarColor(Color.TRANSPARENT);
    }

    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();

        String mwVideoId = intent.getStringExtra("id");
        long videoId = CommonUtils.stringParseLong(mwVideoId);
        isMwJump = videoId != -1L;
        int fromPageId = intent.getIntExtra(VideoDetailListFragment.KEY_FROM_PAGE_ID, 0);
        videoType = intent.getIntExtra(VideoDetailListFragment.KEY_VIDEO_TYPE, RecommendFragment.TYPE_SINGLE_WORK);
        MultiTypeVideoBean multiTypeVideoBean = (MultiTypeVideoBean) intent.getSerializableExtra(VideoDetailListFragment.KEY_VIDEO_LIST);
        if (isMwJump) {
            if (multiTypeVideoBean == null) {
                multiTypeVideoBean = new MultiTypeVideoBean();
                multiTypeVideoBean.videoId = videoId;
            }
        }
        videoDetailListFragment = VideoDetailListFragment.newInstance(fromPageId, videoType, multiTypeVideoBean);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.flContainer, videoDetailListFragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (videoDetailListFragment != null && videoDetailListFragment.onBackPress()) {
            return;
        }
        if (isMwJump) {
            // 魔窗跳转，进入主页
            MainActivity.startMainActivity(this);
        } else {
            if (videoType != RecommendFragment.TYPE_SINGLE_WORK) {
                ActivityCompat.finishAfterTransition(this);
            } else {
                //判断task栈里是否存在MainActivity实例
                if (!isExistActivity(MainActivity.class)) {
                    MainActivity.startMainActivity(this);
                }
            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        DcIjkPlayerManager.get().sendUserBehavior();
    }
}
