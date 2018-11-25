package com.wmlive.hhvideo.heihei.personal.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.heihei.beans.main.ShareInfo;
import com.wmlive.hhvideo.heihei.beans.opus.ShareEventEntity;
import com.wmlive.hhvideo.heihei.login.AccountUtil;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by XueFei on 2017/6/6.
 * <p>
 * 添加好友
 */

public class AddFriendActivity extends DcBaseActivity {
    public static final String KEY_PARAM = "shareinfo";

    @BindView(R.id.btn_add_qq)
    RelativeLayout btnAddQQ;
    @BindView(R.id.btn_add_wechat)
    RelativeLayout btnAddWechat;
    @BindView(R.id.btn_add_sina)
    RelativeLayout btnAddSina;

    private ShareInfo shareInfo;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_add_friend;
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(R.string.user_add_friend_title, true);
        shareInfo = (ShareInfo) getIntent().getSerializableExtra(KEY_PARAM);
        if (null == shareInfo) {
            toastFinish();
        } else {
            btnAddQQ.setOnClickListener(this);
            btnAddWechat.setOnClickListener(this);
            btnAddSina.setOnClickListener(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        dismissLoad();
    }

    @Override
    protected void onSingleClick(View v) {
        shareInfo.objId = AccountUtil.getUserId();
        shareInfo.shareType = ShareEventEntity.TYPE_MY;
        if (v.getId() == R.id.btn_add_qq) {
            shareInfo.shareTarget = ShareEventEntity.TARGET_QQ;
            qqShare(shareInfo);
        } else if (v.getId() == R.id.btn_add_wechat) {
            shareInfo.shareTarget = ShareEventEntity.TARGET_WECHAT;
            wechatShare(0, shareInfo);
        } else if (v.getId() == R.id.btn_add_sina) {
            shareInfo.shareTarget = ShareEventEntity.TARGET_WEIBO;
            weiboShare(shareInfo);
        }
    }

    public static void startAddFriendActivity(Context context, ShareInfo shareInfo) {
        Intent intent = new Intent(context, AddFriendActivity.class);
        intent.putExtra(KEY_PARAM, shareInfo);
        context.startActivity(intent);
    }
//
//    @Override
//    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
//        dismissLoad();
//        showToast(R.string.share_suc);
//    }
//
//    @Override
//    public void onError(Platform platform, int i, Throwable throwable) {
//        dismissLoad();
//        showToast(R.string.share_faid);
//    }
//
//    @Override
//    public void onCancel(Platform platform, int i) {
//        dismissLoad();
//        showToast(R.string.share_cancel);
//    }
}
