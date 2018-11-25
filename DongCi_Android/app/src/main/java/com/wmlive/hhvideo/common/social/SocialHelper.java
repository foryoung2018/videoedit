package com.wmlive.hhvideo.common.social;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.wmlive.hhvideo.heihei.beans.main.ShareInfo;

/**
 * Created by lsq on 1/2/2018.10:34 AM
 *
 * @author lsq
 * @describe 添加描述
 */
@Deprecated
public class SocialHelper {
    private static final String TAG = "SocialHelperFragment";
    private SocialFragment socialFragment;

    public SocialHelper(AppCompatActivity activity) {
        socialFragment = getSocialFragment(activity);
    }

    private SocialFragment getSocialFragment(AppCompatActivity activity) {
        SocialFragment fragment = findFragment(activity);
        if (fragment == null) {
            fragment = new SocialFragment();
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .add(fragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return fragment;
    }

    private SocialFragment findFragment(AppCompatActivity activity) {
        return (SocialFragment) activity.getSupportFragmentManager().findFragmentByTag(TAG);
    }

    public void wechatLogin() {
        socialFragment.wechatLogin();
    }

    public void onNewIntent(Intent intent) {
        socialFragment.onNewIntent(intent);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        socialFragment.onActivityResult(requestCode, resultCode, data);
    }

    public void wechatShare(int type, ShareInfo shareInfo) {
        socialFragment.wechatShare(type, shareInfo);
    }

    public void weiboJump(String id) {
        socialFragment.weiboJump(id);
    }

    public void weiboLogin() {
        socialFragment.weiboLogin();
    }

    public void weiboShare(ShareInfo shareInfo) {
        socialFragment.weiboShare(shareInfo);
    }

    public void qqLogin() {
        socialFragment.qqLogin();
    }

    public void qqShare(ShareInfo shareInfo) {
        socialFragment.qqShare(shareInfo);
    }


}
