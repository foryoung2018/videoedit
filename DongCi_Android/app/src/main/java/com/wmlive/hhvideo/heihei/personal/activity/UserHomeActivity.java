package com.wmlive.hhvideo.heihei.personal.activity;

import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.View;

import com.wmlive.hhvideo.common.DcRouter;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.common.manager.DcIjkPlayerManager;
import com.wmlive.hhvideo.heihei.mainhome.activity.MainActivity;
import com.wmlive.hhvideo.heihei.mainhome.fragment.RecommendFragment;
import com.wmlive.hhvideo.utils.CommonUtils;
import com.wmlive.hhvideo.utils.KLog;

import cn.magicwindow.mlink.annotation.MLinkRouter;
import cn.wmlive.hhvideo.R;

/**
 * 用户的主页
 * Created by lsq on 10/13/2017.
 */
@MLinkRouter(keys = {GlobalParams.MWConfig.LINK_KEY_USER})
public class UserHomeActivity extends DcBaseActivity {
    public static final int REQUEST_MEDIA_PROJECTION = 18;
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_FROM_IMCHAT = "from_imchat";
    private long userId;
    private RecommendFragment userHomeFragment;
    private boolean isMwJump;

    @Override
    protected void onSingleClick(View v) {

    }

    public static void startUserHomeActivity(Context context, long userId) {
        startUserHomeActivity(context, userId, null);
    }

    public static void startUserHomeActivity(Context context, long userId, String routeKey) {
        startUserHomeActivity(context, userId, routeKey, false);
    }

    public static void startUserHomeActivity(Context context, long userId, String routeKey, boolean fromIMChat) {
        Intent intent = new Intent(context, UserHomeActivity.class);
        intent.putExtra(KEY_USER_ID, userId);
        intent.putExtra(DcRouter.KEY_ROUTE_KEY, routeKey);
        intent.putExtra(KEY_FROM_IMCHAT, fromIMChat);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_user_home;
    }

    @Override
    protected void initData() {
        super.initData();
        //从魔窗跳转过来的topicId
        String mwUserId = getIntent().getStringExtra("id");
        userId = CommonUtils.stringParseLong(mwUserId);
        isMwJump = userId != -1L;
        KLog.i("=====从魔窗跳转获取的用户id是：", userId);
        userId = getIntent().getLongExtra(KEY_USER_ID, userId);
        boolean fromIMChat = getIntent().getBooleanExtra(KEY_FROM_IMCHAT, false);
        if (userId > 0) {
            userHomeFragment = RecommendFragment.newInstance(RecommendFragment.TYPE_USER_HOME, userId, null, fromIMChat);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.flContainer, userHomeFragment)
                    .commit();
        } else {
            toastFinish();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        DcIjkPlayerManager.get().sendUserBehavior();
    }

    @Override
    public void onBack() {
        onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (userHomeFragment != null && userHomeFragment.onBackPressed()) {
                return true;
            }

            DcIjkPlayerManager.get().resetUrl();
            DcIjkPlayerManager.get().removeListener(userHomeFragment.getPageId());
            finish();
            if (isMwJump) {
                MainActivity.startMainActivity(this);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (isMwJump) {
            // 魔窗跳转，进入主页
            MainActivity.startMainActivity(this);
        } else {
            //判断task栈里是否存在MainActivity实例
            if (!isExistActivity(MainActivity.class)) {
                MainActivity.startMainActivity(this);
            }
        }
        super.onBackPressed();
    }
}
