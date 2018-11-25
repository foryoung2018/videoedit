package com.dongci.sun.gpuimglibrary.api.apiTest;


import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dongci.sun.gpuimglibrary.R;
import com.dongci.sun.gpuimglibrary.camera.VideoInfo;
import com.dongci.sun.gpuimglibrary.coder.audioCodec.MediaMuxerRunnable;
import com.dongci.sun.gpuimglibrary.common.FileUtils;
import com.dongci.sun.gpuimglibrary.common.SLVideoComposer;
import com.dongci.sun.gpuimglibrary.common.SLVideoCompressor1;
import com.dongci.sun.gpuimglibrary.common.Constants;

import java.io.File;
import java.util.ArrayList;

public class VideoComposeAct extends BaseActivity {

    ArrayList<String> videos = new ArrayList<String>();

    String basePath;
    TextView tv;
    String path;
    int type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_compose);
        type = getIntent().getIntExtra("type",0);
        Log.d("tag","type--->"+type);
        tv = (TextView) findViewById(R.id.videopath);
        if(type==0){//合成
            initData();
        }else{//压缩
            initData1();
        }

    }

    /**
     *
     */
    private void initData(){
        basePath = FileUtils.getVideoPath(this);
        File file = new File(basePath);
        if(file.exists()){
            String[] files = file.list();
            if(files!=null && files.length>1){
                String set = files[files.length-1] +"\n" +files[files.length-2];
                tv.setText(set);
                videos.add(basePath+files[files.length-1]);
                videos.add(basePath+files[files.length-2]);
            }else{
                tv.setText("视频太少，先去录制吧");
            }
        }else{
            tv.setText("视频不存在，先去录制吧");
        }
    }

    /*
     * 合并视频
     */
    private String outputPath;
    private ArrayList<VideoInfo> mInfoList = new ArrayList<>();
    public void setDataSource(String[] dataSource) {

        MediaMetadataRetriever retr = new MediaMetadataRetriever();
        mInfoList.clear();

        for (int i = 0; i < dataSource.length; i++) {

            String path = dataSource[i];
            retr.setDataSource(path);
            String rotation = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            String width = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String height = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String duration = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            VideoInfo info = new VideoInfo();

            info.path = path;
            info.rotation = Integer.parseInt(rotation);
            info.width = Integer.parseInt(width);
            info.height = Integer.parseInt(height);
            info.duration = Integer.parseInt(duration);

            mInfoList.add(info);

        }

        outputPath = Constants.getPath("video/output/", System.currentTimeMillis() + "");
        showLoading("视频拼接中");
        MediaMuxerRunnable instance = new MediaMuxerRunnable();
        instance.setVideoInfo(mInfoList, outputPath);
        instance.addMuxerListener(new MediaMuxerRunnable.MuxerListener() {

            @Override
            public void onStart() {

            }
            @Override
            public void onFinish() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        endLoading();
                        Toast.makeText(VideoComposeAct.this," 拼接完成 文件地址 "+outputPath,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        instance.start();

    }

    public void onClick(View view){
        if(type==0){//合成
            compose(view);
        }else{
            compress(view);
        }
    }

    public void compose(final View view){
        Log.e("compose", "compose");
        if(videos.size()<2){
            Toast.makeText(this,"视频太少，先去录制吧",Toast.LENGTH_SHORT).show();
        }
        for(String s:videos){
            Log.d("sun", "video-->"+s);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
//                ArrayList videoList = new ArrayList<>();
//                //待合成的2个视频文件
//                videoList.add("test1.mp4");
//                videoList.add("test2.mp4");
                SLVideoComposer composer = new SLVideoComposer(videos, basePath+"out.mp4");
                final boolean result = composer.joinVideo();
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(VideoComposeAct.this, "合成结果 " + result, Toast.LENGTH_LONG);
                        tv.setText("合成成功:"+basePath+"out.mp4");
                    }
                });
//                Log.i(TAG, "compose result: " + result);
            }
        }).start();

    }

    private void initData1(){
        path = getIntent().getStringExtra("path");
        tv.setText(path);
    }

    /**
     * 压缩
     * @param view
     */
    private void compress(View view){
//        final String path0 = Environment.getExternalStorageDirectory() + "/Movies/ZEPP_STANDZ_2017_V5_R2.mov";

        final String path1 = path + "out.mp4";
        Log.d("tag","press--->"+path1);
        SLVideoCompressor1.compressVideo(path, path1, new SLVideoCompressor1.onCompressCompleteListener() {
            @Override
            public void onComplete(boolean compressed, String outPath) {
                if (compressed) {
                    Log.d("压缩转码", "Compression successfully!");
                    if(tv!=null)
                        tv.setText("压缩成功:"+path1);
                }
            }
        }, new SLVideoCompressor1.OnVideoProgressListener() {
            @Override
            public void progress(int progress) {
                if(tv!=null)
                    tv.setText("压缩进度:"+progress);
                Log.d("压缩转码", "progress:" + progress);
            }
        });
    }

}
