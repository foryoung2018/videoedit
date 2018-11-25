package com.dongci.sun.gpuimglibrary.api.apiTest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.dongci.sun.gpuimglibrary.R;
import com.dongci.sun.gpuimglibrary.api.DCRecorderCore;
import com.dongci.sun.gpuimglibrary.camera.CameraView;
import com.dongci.sun.gpuimglibrary.gles.filter.FiterJsonUtils;
import com.dongci.sun.gpuimglibrary.gles.filter.FilterUtils;
import com.dongci.sun.gpuimglibrary.common.DialogUtils;
import com.dongci.sun.gpuimglibrary.common.SLClipVideo;
import com.dongci.sun.gpuimglibrary.activity.FilterPanelAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.dongci.sun.gpuimglibrary.common.FileUtils.getPath;

public class DcTestActivity extends AppCompatActivity implements FilterPanelAdapter.OnFilterItemSelectListener, SeekBar.OnSeekBarChangeListener {
    private String TAG = "Main2Activity";
    private String START_RECORDER = "录制视频";
    private String STOP_RECORDER = "停止录制";
    private String BEAUTY_OPEN = "美颜开启";
    private String BEAUTY_CLOSE = "美颜关闭";
    private CameraView cameraview;
    private ExecutorService executorService;
    private String path;

    private Button btnPlay;

    private Button composeButton;

    private DCRecorderCore dcRecorderCore;

    public static int R1 = 1;
    public static int G = 1;
    public static int B = 1;

//    RecordFilterPanel pannel;

//    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dc_test);
        initPannel();
//        GpuConfig.context = this;
        cameraview = (CameraView) findViewById(R.id.cameraview);

        btnPlay = (Button) findViewById(R.id.btn_play);

        composeButton = (Button) findViewById(R.id.composeButton);

        executorService = Executors.newSingleThreadExecutor();

        dcRecorderCore = DCRecorderCore.getDcRecorderCore();

//        pannel = (RecordFilterPanel) findViewById(R.id.recordpannel);
//        pannel.setFilterItemSelectListener(this);

        startPreviewWithPermission();
//        new FiterJsonUtils().getClassName(this,"com.dongci.sun.gpuimglibrary.camera2.filternew");
        new FiterJsonUtils().parseDetail("");
//        new FiterJsonUtils().main(getPackageCodePath());
//        ClassUtil.main();
//        initBar();
    }

    private void initBar(FilterEx filterEx){
        SeekBar seekBarR = (SeekBar) findViewById(R.id.seekbar_r);
        SeekBar seekBarG = (SeekBar) findViewById(R.id.seekbar_g);
        SeekBar seekBarB = (SeekBar) findViewById(R.id.seekbar_b);
        if(filterEx.paramsNum==0){
            seekBarR.setVisibility(View.GONE);
            seekBarG.setVisibility(View.GONE);
            seekBarB.setVisibility(View.GONE);
        }else if(filterEx.paramsNum==1){
            seekBarR.setVisibility(View.VISIBLE);
            seekBarG.setVisibility(View.GONE);
            seekBarB.setVisibility(View.GONE);
        }else if(filterEx.paramsNum==2){
            seekBarR.setVisibility(View.VISIBLE);
            seekBarG.setVisibility(View.VISIBLE);
            seekBarB.setVisibility(View.GONE);
        }else if(filterEx.paramsNum==3){
            seekBarR.setVisibility(View.VISIBLE);
            seekBarG.setVisibility(View.VISIBLE);
            seekBarB.setVisibility(View.VISIBLE);
        }

        seekBarR.setMax((int)filterEx.max1);
        seekBarG.setMax((int)filterEx.max2);
        seekBarB.setMax((int)filterEx.max3);


//        seekBarR.setMin((int)filterEx.max1);
//        seekBarG.setMin((int)filterEx.max2);
//        seekBarB.setMin((int)filterEx.max3);

        seekBarB.setOnSeekBarChangeListener(this);
        seekBarG.setOnSeekBarChangeListener(this);
        seekBarR.setOnSeekBarChangeListener(this);
    }

    public static List<FilterUtils.FilterType> LIST = new ArrayList<FilterUtils.FilterType>();

    /**
     * 滤镜
     */
    private void initPannel(){
        LIST = new FilterUtils().init1();

    }

    @Override
    protected void onResume() {
        super.onResume();
//        cameraview.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dcRecorderCore.onDestroy();
    }

    public void startPreview(){
        dcRecorderCore.onResume();
    }

    private void startPreviewWithPermission() {
        int cameraPerm = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int audioPerm = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int writeExternal = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (cameraPerm != PackageManager.PERMISSION_GRANTED || audioPerm != PackageManager.PERMISSION_GRANTED ||
                writeExternal != PackageManager.PERMISSION_GRANTED) {

            String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissions,
                    1);

        } else {//有权限了
            startPreview();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    boolean isForbid = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]);
                    if (isForbid) {
                        if (Manifest.permission.CAMERA.equals(permissions[i])) {
                            Log.d(TAG, "onRequestPermissionsResult: 录像");
                        } else if(Manifest.permission.RECORD_AUDIO.equals(permissions[i])){
                            Log.d(TAG, "onRequestPermissionsResult: 录音");
                        }else if(Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permissions[i])){
                            Log.d(TAG, "onRequestPermissionsResult: 读写本地文件");
                        }
                    } else {
                        startPreviewWithPermission();
                    }
                    return;
                }
            }
            startPreview();
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            cameraview.setSavePath(getPath(DcTestActivity.this,"record/", System.currentTimeMillis() + ".mp4"));
            cameraview.startRecord();
        }
    };

    /**
     * 录制视频
     *
     * @param view
     */
    public void recorder(View view) {
        Button b = (Button) view;
        String trim = b.getText().toString().trim();
        if (trim.equals(START_RECORDER)) {
            b.setText(STOP_RECORDER);
            path = getPath(DcTestActivity.this,"record/", System.currentTimeMillis() + ".mp4");
            dcRecorderCore.setOutPath(path);
            try {
                dcRecorderCore.startRecord(path,0);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            cameraview.setSavePath(path);
//            cameraview.startRecord();
        } else {
            b.setText(START_RECORDER);
            dcRecorderCore.stopRecord();
//            cameraview.stopRecord();
            btnPlay.setVisibility(View.VISIBLE);
            btnPlay.setText(path);
        }
    }

    /**
     * 摄像头切换
     *
     * @param view
     */
    public void switchcamera(View view) {
//        cameraview.switchCamera();
//        dcRecorderCore.switchCamera(DCRecorderCore.cameraView.isFaceFront()? Camera.CameraInfo.CAMERA_FACING_BACK: Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    /**
     * 美颜
     *
     * @param view
     */
    public void beautiful(View view) {
        Button b = (Button) view;
        String trim = b.getText().toString().trim();

        dcRecorderCore.enableBeautify(trim.equals(BEAUTY_OPEN));
        if (trim.equals(BEAUTY_OPEN)) {
            b.setText(BEAUTY_CLOSE);
//            cameraview.changeBeautyLevel(4);
        } else {
            b.setText(BEAUTY_OPEN);
//            cameraview.changeBeautyLevel(0);
        }
    }

    public void play(View view) {
        Intent intent = new Intent(this, VideoPlayAcctivity.class);
        intent.putExtra("path", path);
        startActivity(intent);
        btnPlay.setVisibility(View.GONE);
    }

    /**
     * 视频合并
     *
     * @param view
     */
    public void videoComposeBtnDidclick(View view) {

    }


    class FilterEx{
        FilterUtils.FilterType type;
        int paramsNum = 0;
        float min1 = 0;
        float max1 = 1;
        float min2 = 0;
        float max2 = 1;
        float min3 = 0;
        float max3 = 1;
    }
    List<FilterEx> filterExes = new ArrayList<FilterEx>();

    private void addFilter(FilterEx filterEx){
        for(FilterEx f:filterExes){
            if(f.type==filterEx.type){
                return;
            }
        }
        filterExes.add(filterEx);
    }

    /**
     * 切换滤镜
     * @param view
     */
    public void switchfilterAcv(View view){
//        DialogAcvUtils.showFilterDialog(this, new DialogAcvUtils.SwitchFilterListener() {
//            @Override
//            public void onFilterSwitch(String item) {
//                Log.d("selectFilter-302-onFilterSwitch","select：" + item);
//                dcRecorderCore.switchCamera(FilterUtils.filterWithType(DcTestActivity.this,Integer.parseInt(item)));
//            }
//        });
    }

    /**
     * 切换滤镜
     *
     * @param view
     */
    public void switchfilter(View view) {

//        pannel.setVisibility(pannel.getVisibility()==View.VISIBLE ? View.INVISIBLE:View.VISIBLE);
//        DialogUtils.setData(LIST);
        DialogUtils.showFilterDialog(this, new DialogUtils.SwitchFilterListener() {
            @Override
            public void onFilterSwitch(String item) {
                Log.d(TAG, "onFilterSwitch: item==" + item);
                currentType = FilterUtils.FilterType.valueOf(item);
//                cameraview.switchFilter(FilterUtils.getFilter(item));
//                dcRecorderCore.switchFilter(currentType);
//                FilterEx filterEx = new FilterEx();
//                filterEx.type = currentType;
//                if(currentType.equals(FilterUtils.FilterType.RGB)){//
//
//                    filterEx.paramsNum = 3;
//                    filterEx.max1 = 256;
//                    filterEx.max2 = 256;
//                    filterEx.max3= 256;
//                }else if(currentType.equals(FilterUtils.FilterType.CONTRAST)) {//对比 1个参数  0，4
//                    filterEx.paramsNum = 1;
//                    filterEx.max1 = 4;
//                }else if(currentType.equals(FilterUtils.FilterType.GAMMA)) {//对比 1个参数  0，3 灰度
//                    filterEx.paramsNum = 1;
//                    filterEx.max1 = 3;
//                }else if(currentType.equals(FilterUtils.FilterType.HUE)) {//对比 1个参数 色调   90默认  0 180
//                    filterEx.paramsNum = 1;
//                    filterEx.max1 = 180;
//                }else if(currentType.equals(FilterUtils.FilterType.SHARPEN)) {//对比 1个参数
//
//                }else if(currentType.equals(FilterUtils.FilterType.SATURATION)) {//对比 1个参数 饱和度 0，2  默认1
//                    filterEx.paramsNum = 1;
//                    filterEx.max1 = 2;
//                }else if(currentType.equals(FilterUtils.FilterType.EXPOSURE)) {//对比 1个参数  曝光 -10，10
//                    filterEx.paramsNum = 1;
//                    filterEx.max1 = 10;
//                    filterEx.min1 = -10;
//                }else if(currentType.equals(FilterUtils.FilterType.HIGHLIGHT_SHADOW)) {//对比 2个参数
//
//                }else if(currentType.equals(FilterUtils.FilterType.MONOCHROME)) {//对比 1个参数  根据图像的像素亮度将其转换成单色。
//
//
//                }else if(currentType.equals(FilterUtils.FilterType.OPACITY)) {//对比 1个参数  透明 0，1
//                    filterEx.paramsNum = 1;
//                    filterEx.max1 = 1;
//                }else{
//
//                }
//                else if(currentType.equals(FilterUtils.FilterType.WHITE_BALANCE)) {//对比 1个参数
//
//                }else if(currentType.equals(FilterUtils.FilterType.VIGNETTE)) {//对比 2个参数
//
//                }else if(currentType.equals(FilterUtils.FilterType.LEVELS_FILTER_MIN)) {//对比 3个参数
//
//                }

//                initBar(filterEx);
//                addFilter(filterEx);


            }
        });
    }

    public void clipVideoBtnDidclick(View view){
        new SLClipVideo().clipVideo(path,2*1000*1000,2*1000*10000);
    }

    @Override
    public void onFilterSelected(int selectIndex, int newId, int oldId) {

//        dcRecorderCore.switchFilter(FilterUtils.getFilter(selectIndex));

    }

    private FilterUtils.FilterType currentType;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(seekBar.getId()== R.id.seekbar_r){
            R1 = progress;
        }else if(seekBar.getId()== R.id.seekbar_g){
            G = progress;
        }else if(seekBar.getId()== R.id.seekbar_b){
            B = progress;
        }
//        dcRecorderCore.switchFilter(currentType);
//        if(currentType.equals(FilterUtils.FilterType.RGB)){
//            dcRecorderCore.switchFilter(FilterUtils.FilterType.RGB);
//        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
