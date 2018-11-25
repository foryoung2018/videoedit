package com.wmlive.hhvideo.heihei.mainhome.activity;

import android.view.View;

import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.heihei.mainhome.fragment.ExplosionFragment;

import cn.wmlive.hhvideo.R;

public class LatestActivity extends DcBaseActivity {

    @Override
    protected void onSingleClick(View v) {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_latest;
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle("最新", true);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.flContainer, ExplosionFragment.newInstance())
                .commit();
    }
}
