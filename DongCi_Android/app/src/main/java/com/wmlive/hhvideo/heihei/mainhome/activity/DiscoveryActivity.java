package com.wmlive.hhvideo.heihei.mainhome.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.heihei.mainhome.fragment.DiscoverFragment;

import cn.wmlive.hhvideo.R;

public class DiscoveryActivity extends DcBaseActivity {
    public static void startDiscoveryActivity(Context context) {
        Intent intent = new Intent(context, DiscoveryActivity.class);
        context.startActivity(intent);
    }



    private DiscoverFragment discoverFragment;


    @Override
    protected void onSingleClick(View v) {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_discovery2;
    }


    @Override
    protected void initData() {
        super.initData();
        discoverFragment = DiscoverFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.flContainer, discoverFragment)
                .commit();
    }
}
