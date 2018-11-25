package com.wmlive.hhvideo.heihei.personal.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.wmlive.hhvideo.common.base.DcBaseActivity;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * 网络诊断结果详情
 * Author：create by jht on 2018/9/3 16:22
 * Email：haitian.jiang@welines.cn
 */
public class CheckNetDetailsActivity extends DcBaseActivity {

    @BindView(R.id.check_details_tv)
    TextView checkDetailsTv;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_netcheck_details;
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(R.string.setting_network_check, true);
        Intent intent = getIntent();
        String detais = intent.getStringExtra("checkInfoDetais");
        checkDetailsTv.setText(detais);

    }

    @Override
    protected void onSingleClick(View v) {
    }
}
