package com.wmlive.hhvideo.heihei.record.activityview;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.os.ResultReceiver;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;

import com.wmlive.hhvideo.common.manager.MyAppActivityManager;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.mainhome.activity.MainActivity;
import com.wmlive.hhvideo.heihei.record.activity.RecordMvActivity;
import com.wmlive.hhvideo.heihei.record.engine.PlayerEngine;
import com.wmlive.hhvideo.heihei.record.engine.content.PlayerContentFactory;
import com.wmlive.hhvideo.heihei.record.engine.listener.PlayerCreateListener;
import com.wmlive.hhvideo.heihei.record.engine.listener.PlayerListener;
import com.wmlive.hhvideo.heihei.record.engine.model.MediaObject;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtil;
import com.wmlive.hhvideo.heihei.record.widget.CircleProgressDialog;
import com.wmlive.hhvideo.heihei.record.widget.SysAlertDialog;
import com.wmlive.hhvideo.heihei.record.widget.TextureVideoViewOutlineProvider;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ScreenUtil;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.utils.download.DownloadBean;
import com.wmlive.hhvideo.utils.download.FileDownload;

import java.util.List;

import cn.wmlive.hhvideo.R;

import static android.content.Context.ACTIVITY_SERVICE;


/**
 * 创意Mv ui 更新部分
 */
public class RecordMvActivityView {

    RecordMvActivity context;

    private CircleProgressDialog dialog;
    /**播放临时进度条*/
    private float progressTemp;

    public RecordMvActivityView(RecordMvActivity context) {
        this.context = context;
    }

    public void onDestroy() {
        context = null;
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1://更新录制失败，
                    ToastUtil.showToast("录制失败，请重试");
                    dismissDialog();
                    onStopRecordView();
                    break;
            }
        }
    };

    /**
     * 录制MV界面返回时提示弹框
     *
     * @param context
     */
    public static void showRecordMvBackDialog(Context context) {
        SysAlertDialog.createAlertDialog(context, "", context.getResources().getString(R.string.giveup_mv_recording), context.getResources().getString(R.string.still_back),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        RecordUtil.deleteProduct(RecordManager.get().getProductEntity(), true);
                        RecordManager.get().clearAll();
                        if(isExistActivity(context,MainActivity.class)){
                            MyAppActivityManager.getInstance().finishAllActivityExceptOne(MainActivity.class);
                        }else{
                            ((Activity)context).finish();
                        }
                    }
                }, context.getResources().getString(R.string.still_recording), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, false, null)
                .show();
    }

    /**
     * 录制MV界面返回时提示弹框
     *
     * @param context
     */
    public static void showTempDownloadFailDialog(Context context, DownloadBean downloadBean,ResultReceiver resultReceiver) {
        SysAlertDialog.createAlertDialog(context, "", context.getResources().getString(R.string.current_template_download_fail), context.getResources().getString(R.string.download_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ((Activity)context).finish();
                    }
                }, context.getResources().getString(R.string.re_downloading), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        FileDownload.start(context, downloadBean, resultReceiver, true, true);
                    }
                }, false, null)
                .show();
    }

    /**
     * 录制MV界面到阅览界面时提示弹框
     *
     * @param context
     */
    public static void showRecordMvToPreviewDialog(Context context) {
        SysAlertDialog.createAlertDialog(context, "", context.getResources().getString(R.string.record_mv_review_tips), context.getResources().getString(R.string.i_know),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, "", null, false, null).show();
    }

    /**
     * 初始化底部录制控制bar
     */
    public void initRecordOptionPanelMV() {
        context.recordOptionPanelMV.getProgressBar().setInterval(0, (int)(context.recordMvActivityHelper.mvConfig.duration * 1000));
    }

    /**
     *设置录制按钮是否可点击
     */
    public void setEnableRecordBtn(boolean enableRecordBtn) {
        context.extBtnRecord.setEnabled(enableRecordBtn);
    }

    public void setNextEnable(boolean enable){
        if(enable){
            context.tvNext.setEnabled(true);
            context.tvNext.setTextColor(context.getResources().getColor(R.color.hh_color_g));
        }else{

        }
    }

    public void updateProgress(int progress) {
        context.recordOptionPanelMV.getProgressBar().setProgress(progress);
    }

    PlayerEngine playerEngine;

    /**
     * 直接播放视频
     * @param playerIv
     */
    public void playVideo(ImageView playerIv){
        if(playerEngine!=null){
            hideCameraPreview();
            context.playerContainer.setVisibility(View.VISIBLE);
            playerIv.setVisibility(View.GONE);
            if(context.playerContainer.getChildCount()>0){
                context.playerContainer.getChildAt(0).setVisibility(View.VISIBLE);
            }
            playerEngine.seekToPlay(0,true);

        }
    }

    public void playeVideo(ShortVideoEntity shortVideoEntity, ImageView playerIv) {
        if (playerEngine == null)
            playerEngine = new PlayerEngine();
        else
            playerEngine.reset();

        PlayerContentFactory playerContentFactory = new PlayerContentFactory();
        List<MediaObject> medias = playerContentFactory.getMvMediaPlayer(shortVideoEntity, ScreenUtil.getWidth(context)-30*2,getPreviewHeight());
        TextureView textureView = new TextureView(context);
        textureView.setOutlineProvider(new TextureVideoViewOutlineProvider(30));
        textureView.setClipToOutline(true);

        context.playerContainer.removeAllViews();
        context.playerContainer.addView(textureView);
        hideCameraPreview();
        playerIv.setVisibility(View.GONE);
        KLog.i("playVideo-->pre");
        playerEngine.build(textureView, new PlayerCreateListener() {

            @Override
            public void playCreated() {
                KLog.i("playVideo-->end");
                playerEngine.setMediaAndPrepare(medias);
                playerEngine.start();
                playerEngine.setOnPlaybackListener(new PlayerListener() {

                    @Override
                    public void onPlayerPrepared() {

                    }

                    @Override
                    public boolean onPlayerError(int var1, int var2) {

                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showCameraPreview(playerIv);
                            }
                        });
                        return false;
                    }

                    @Override
                    public void onPlayerCompletion() {
                        KLog.i("player-->onPlayerCompletion");
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showCameraPreview(playerIv);
                            }
                        });
                    }

                    @Override
                    public void onGetCurrentPosition(float progress) {

                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                KLog.i("play-progress" + (int) (progress*2000));
                                if(progress<1){
                                    context.recordOptionPanelMV.getProgressBar().setProgress((int) (progress*2000));
                                }else{
                                    context.recordOptionPanelMV.getProgressBar().setProgress(0);
                                }


                            }
                        });
                    }
                });

            }
        });
    }

    /**
     *  开始录制
     */
    public void onRecordView(){
        context.recordOptionPanelMV.showAllOption(false);
        hidePanel();
        setEnableRecordBtn(false);
        KLog.i("click--enable--false");
        context.recordActionTipsTv.setVisibility(View.VISIBLE);
        context.canNotClickMvlistLl.setVisibility(View.VISIBLE);
        showToolBarMenu(false);
    }

    /**
     * 录制结束
     */
    public void onStopRecordView(){
        showToolBarMenu(true);
        context.canNotClickMvlistLl.setVisibility(View.GONE);
        context.recordOptionPanelMV.showAllOption(true);
        setEnableRecordBtn(true);
    }

    /**
     * toolbar菜单和按钮是否显示
     *
     * @param isShow
     */
    public void showToolBarMenu(boolean isShow) {
        context.showBack(isShow);
        context.tvNext.setVisibility(isShow == true ? View.VISIBLE : View.INVISIBLE);
        context.recordMenu.setVisibility(isShow == true ? View.VISIBLE : View.INVISIBLE);
    }

    private void hideCameraPreview() {
        context.previewRelative.setVisibility(View.GONE);
        context.playerContainer.setVisibility(View.VISIBLE);
    }

    private void showCameraPreview(ImageView  playerIv) {

        context.previewRelative.setVisibility(View.VISIBLE);
        context.playerContainer.setVisibility(View.GONE);
        for (int i = 0; i < context.playerContainer.getChildCount(); i++) {
            context.playerContainer.getChildAt(i).setVisibility(View.GONE);
        }
        playerIv.setVisibility(View.VISIBLE);
    }

    private int getPreviewHeight(){
        return context.playerContainer.getHeight();
    }

    /**
     * 开启倒计时
     */
    public void startCountDown(int count) {
        if (!context.countdownView.isStarted()) {
            context.countdownView.setVisibility(View.VISIBLE);
            context.countdownView.start(count);
        }
    }

    /**
     * 倒计时结束
     */
    public void countDownEnd() {
        context.countdownView.postDelayed(new Runnable() {
            @Override
            public void run() {
                context.countdownView.setVisibility(View.GONE);
            }
        }, 500);
    }

    /**
     * 倒计时取消
     */
    public void countDownCancel(){
        context.recordOptionPanelMV.showAllOption(true);
    }

    /**
     * 隐藏倒计时和滤镜按钮
     */
    public void hidePanel() {
        if (context.filterLayout != null) {
            context.filterLayout.setVisibility(View.INVISIBLE);
        }
        if (context.llSpeedPanel != null) {
            context.llSpeedPanel.setVisibility(View.INVISIBLE);
        }
        if(context.countDownRv!=null){
            context.countDownRv.setVisibility(View.INVISIBLE);
        }
    }


    public void dismissDialog() {
        if (dialog != null)
            dialog.dismiss();
    }

    public void showDialog(int content){
        if (dialog == null) {
            dialog = SysAlertDialog.createCircleProgressDialog(context, context.getString(content), true, false);
        }else{
            dialog.setMessage(context.getString(content));
        }
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }
    /**
     * 判断某一个类是否存在任务栈里面
     *
     * @return
     */
    protected static boolean isExistActivity(Context context,Class<?> cls) {
        Intent intent = new Intent(context, cls);
        ComponentName cmpName = intent.resolveActivity(context.getPackageManager());
        boolean flag = false;
        if (cmpName != null) { // 说明系统中存在这个activity
            ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfoList = am.getRunningTasks(10);
            for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
                if (taskInfo.baseActivity.equals(cmpName)) { // 说明它已经启动了
                    flag = true;
                    break;  //跳出循环，优化效率
                }
            }
        }
        return flag;
    }


}
