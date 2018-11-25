package com.wmlive.hhvideo.heihei.mainhome.activity;

import android.view.View;

import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.heihei.mainhome.fragment.MessageFragment;

import cn.wmlive.hhvideo.R;

public class MessageActivity extends DcBaseActivity {


    private MessageFragment messageFragment;

    @Override
    protected void onSingleClick(View v) {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_message;
    }

    @Override
    protected void initData() {
        super.initData();
        messageFragment = MessageFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.flContainer, messageFragment)
                .commit();
    }
}
