package com.wmlive.hhvideo.heihei.record.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.wmlive.hhvideo.heihei.record.service.RecordActivitySdkViewImpl;

import cn.wmlive.hhvideo.R;

public class TestAct extends Activity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_option_view);
        RecordActivitySdkViewImpl recordActivitySdkView = new RecordActivitySdkViewImpl();
    }
}
