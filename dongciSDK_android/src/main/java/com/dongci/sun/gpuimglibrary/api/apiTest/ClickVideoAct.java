package com.dongci.sun.gpuimglibrary.api.apiTest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dongci.sun.gpuimglibrary.R;
import com.dongci.sun.gpuimglibrary.common.SLClipVideo;

/**
 * 剪裁视频
 */
public class ClickVideoAct extends AppCompatActivity {

    TextView tv;
    EditText etStart;
    EditText etDuration;

    String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_video);

        tv = (TextView) findViewById(R.id.videopath_clip);
        etStart = (EditText) findViewById(R.id.act_click_video_start);
        etDuration = (EditText) findViewById(R.id.act_click_video_duration);

        path = getIntent().getStringExtra("path");
        tv.setText(path);
    }

    public void Clip(View view){
        final int start = Integer.parseInt(etStart.getText().toString());
        final int duration = Integer.parseInt(etDuration.getText().toString());
        new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean result = new SLClipVideo().clipVideo(path,start*1000*1000,duration*1000*1000);

                if(tv!=null)
                    tv.post(new Runnable() {
                        @Override
                        public void run() {
                            tv.setText("click结果:"+result);
                        }
                    });
            }}){ }.start();

    }
}
