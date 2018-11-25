package com.wmlive.hhvideo.heihei.record.service;

import android.app.Activity;
import android.icu.text.AlphabeticIndex;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.wmlive.hhvideo.heihei.beans.frame.FrameInfo;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.record.activity.EditProductionActivity;
import com.wmlive.hhvideo.heihei.record.activity.EditVideoActivity;
import com.wmlive.hhvideo.heihei.record.activity.EditVideoGroupActivity;
import com.wmlive.hhvideo.heihei.record.activity.RecordActivitySdk;
import com.wmlive.hhvideo.heihei.record.activity.SelectFrameActivity;
import com.wmlive.hhvideo.heihei.record.activity.TestAct;
import com.wmlive.hhvideo.heihei.record.engine.DCRecorderHelper;
import com.wmlive.hhvideo.heihei.record.engine.PlayerEngine;
import com.wmlive.hhvideo.heihei.record.engine.content.PlayerContentFactory;
import com.wmlive.hhvideo.heihei.record.engine.listener.PlayerCreateListener;
import com.wmlive.hhvideo.heihei.record.engine.model.MediaObject;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.manager.RecordSetting;
import com.wmlive.hhvideo.heihei.record.widget.CountdownView;
import com.wmlive.hhvideo.heihei.record.widget.CustomFrameView;
import com.wmlive.hhvideo.heihei.record.widget.ExtBtnRecord;
import com.wmlive.hhvideo.heihei.record.widget.ExtRadioGroup;
import com.wmlive.hhvideo.heihei.record.widget.FullRecordView;
import com.wmlive.hhvideo.heihei.record.widget.GlTouchView;
import com.wmlive.hhvideo.heihei.record.widget.LiveCameraZoomHandler;
import com.wmlive.hhvideo.heihei.record.widget.LocateCenterHorizontalView;
import com.wmlive.hhvideo.heihei.record.widget.RecordMenuView;
import com.wmlive.hhvideo.heihei.record.widget.RecordOptionPanel;
import com.wmlive.hhvideo.heihei.record.widget.SmallRecordView;
import com.wmlive.hhvideo.utils.KLog;

import java.io.File;
import java.util.List;

import cn.wmlive.hhvideo.R;

import static com.wmlive.hhvideo.heihei.record.manager.RecordSetting.FILTER_LIST;
import static com.wmlive.hhvideo.utils.ToastUtil.showToast;

/**
 * ui 更新，点击 等事件
 */
public class RecordActivitySdkViewImpl {

    /**
     * 是否全屏录制
     */
    private boolean isFullRecord;

    RecordActivitySdk act;

    public RecordOptionPanel recordOptionPanel;
    public TextureView videoViewSdk;
    public FrameLayout videoFrameLayout;
    public LinearLayout llSpeedPanel;
    public ExtRadioGroup extSpeedPanel;
    public TextView filterIndicator;
    public LocateCenterHorizontalView recordFilterSelector;
    public RelativeLayout filterLayout;
    public RecyclerView countDownRv;
    public CountdownView countdownView;
    public CustomFrameView customFrameView;
    public ExtBtnRecord btRecorder;
    public RelativeLayout rlRoot;
    public FullRecordView flFullRecord;
    public FrameLayout fr_container;
    public RelativeLayout rlPreview;
    public View cutdown;
    public RecordMenuView recordMenu;
    TextView tvNext;

    DCRecorderHelper dcRecorderHelper;

    public void init(RecordActivitySdk activitySdk) {
        this.act = activitySdk;
        initView();
    }

    /**
     *
     */
    private void initView() {
        recordOptionPanel = act.recordOptionPanel;
        videoViewSdk = act.videoViewSdk;
        videoFrameLayout = act.videoFrameLayout;
        llSpeedPanel = act.llSpeedPanel;
        extSpeedPanel = act.extSpeedPanel;
        filterIndicator = act.filterIndicator;
        recordFilterSelector = act.recordFilterSelector;
        filterLayout = act.filterLayout;
        countDownRv = act.countDownRv;
        countdownView = act.countdownView;
        customFrameView = act.customFrameView;
        btRecorder = act.btRecorder;
        rlRoot = act.rlRoot;
        flFullRecord = act.flFullRecord;
        fr_container = act.fr_container;
        rlPreview = act.rlPreview;
        cutdown = act.cutdown;
        recordMenu = act.recordMenu;
        tvNext = act.tvNext;

        dcRecorderHelper = act.dcRecorderHelper;
    }


    /**
     * 释放掉引用
     */
    public void onDestroy() {
        act = null;
    }

    public void selectCamera() {

    }

    /**
     * 重置上一个框的状态
     */
    public void resetPreItem(final int preIndex, boolean needJoin, final boolean needCombine, final boolean combineAll, final boolean seekEnd, final boolean entryEdit, boolean entrySort) {
        final ShortVideoEntity videoEntity = RecordManager.get().getShortVideoEntity(preIndex);
        final boolean hasVideo = videoEntity.hasVideo();
        act.smallRecordViewList.get(preIndex).showStatus(
                preIndex == act.getCurrentPreviewIndex(), hasVideo, videoEntity.isImport(), videoEntity.reachMin());
        act.smallRecordViewList.get(preIndex).setFocus(false);
        act.smallRecordViewList.get(preIndex).showDuring(videoEntity.getDuringString());
//        resetPreItemPreview(preIndex, needJoin, needCombine, combineAll, seekEnd, entryEdit, entrySort, videoEntity);
    }

    public void startCountDown(int count) {
        if (!countdownView.isStarted()) {
            countdownView.start(count);
        }
        hidePanel();
    }

    public void countDownEnd() {
        showToast("开始录制");
        if (!hasFocus()) {
            selectSmallCamera(0,true);
        }
        btRecorder.setEnabled(true);
        btRecorder.setStartRecord(true);
        setNextEnable(true);
        cutdown.postDelayed(new Runnable() {
            @Override
            public void run() {
                cutdown.setVisibility(View.GONE);
            }
        }, 500);
    }

    /**
     * 倒计时取消
     */
    public void countDownCancel(){
        setNextEnable(true);
        btRecorder.setEnabled(true);
        recordOptionPanel.showAllOption(true);
        recordMenu.setVisibility(View.VISIBLE);
    }

    public void onStart() {
        cutdown.setVisibility(View.VISIBLE);
        cutdown.postDelayed(new Runnable() {
            @Override
            public void run() {
                cutdown.setVisibility(View.GONE);
            }
        }, 1000);
        btRecorder.setEnabled(true);
        btRecorder.setImageResource(R.drawable.btn_recorder_end);
    }

    /**
     * 更新录制中进度条
     * @param position
     */
    public int recordUpdatePosition(int position){
        int nowP = position + RecordManager.get().getShortVideoEntity(act.getCurrentPreviewIndex()).getDuringMS();
        recordOptionPanel.getProgressBar().setProgress(nowP);
        return nowP;
    }

    public void onRecording() {
        cutdown.setVisibility(View.VISIBLE);
        cutdown.postDelayed(new Runnable() {
            @Override
            public void run() {
                cutdown.setVisibility(View.GONE);
            }
        }, 1000);

        btRecorder.setEnabled(true);
    }

    public void onRecordEnd(int currentPreviewIndex) {
        btRecorder.onForcedExit();
        btRecorder.setImageResource(R.drawable.btn_recorder_start);
        btRecorder.setEnabled(false);
        KLog.i("record======setStartRecord-onRecordEnd-false");
        btRecorder.enableTouchScroll(true);
        //正在录制的画框修改ui
        if (act.smallRecordViewList == null)
            return;
//        LinearLayout ll_recorder_time = act.smallRecordViewList.get(currentPreviewIndex).ll_recorder_time;
//        ll_recorder_time.setVisibility(View.GONE);
//        LinearLayout llUpload = act.smallRecordViewList.get(currentPreviewIndex).llUpload;
//        llUpload.setVisibility(View.GONE);
//        TextView tvDuring = act.smallRecordViewList.get(currentPreviewIndex).tvDuring;
//        tvDuring.setVisibility(View.VISIBLE);

        if (dcRecorderHelper.isFullRecord) {
            flFullRecord.showRecording(false);
//            flFullRecord.setVisibility(View.GONE);
//            isFullRecord = false;
//            act.getWeakHandler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    exitFullRecord(act.getCurrentSmallRecordView().getPreview());
//                }
//            }, 200);
            KLog.i("======onRecordEndUpdate--showRecording--false");
        }else{

//            RecorderCore.recycleCameraView();
        }
//        resetMenu(RecordManager.get().getShortVideoEntity(currentPreviewIndex));
    }

    public void resetMenu(ShortVideoEntity videoEntity) {

        boolean isEmpty = videoEntity == null;
        boolean hasVideo = false;
        if (videoEntity != null) {
            hasVideo = videoEntity.hasVideo();
        }
        btRecorder.setEnabled(!isEmpty && videoEntity.canContinueRecord());
        KLog.i(videoEntity+"resetMenu--btnenable->" + btRecorder.isEnabled());
        recordOptionPanel.setRollbackEnable(!isEmpty && videoEntity.hasVideo() && !videoEntity.isImport());
        recordOptionPanel.setCountdownEnable(!isEmpty && videoEntity.canContinueRecord());
        recordOptionPanel.setSpeedEnable(!isEmpty && videoEntity.canContinueRecord());
        recordOptionPanel.setFilterEnable(!isEmpty && videoEntity.canContinueRecord());
        recordMenu.setBeautyEnable(!isEmpty && !videoEntity.isImport(), dcRecorderHelper.isBeautifyEnabled());//RecorderCore.isBeautifyEnabled()
        recordMenu.setFlashEnable(!isEmpty && !videoEntity.isImport() && !dcRecorderHelper.isUseFrontCamera() && hasVideo, act.dcRecorderHelper.getFlashMode());
        recordMenu.setToggleEnable(!isEmpty && !videoEntity.isImport() && !videoEntity.isImport());
        recordMenu.setCutEnable(RecordManager.get().canCutMusic());
        if(RecordManager.get().getProductEntity()==null)
            return;
        List<ShortVideoEntity> videoList = RecordManager.get().getProductEntity().shortVideoList;
        int videoSize = 0;
        if (videoList != null) {
            videoSize = videoList.size();
        }
        recordMenu.setSortEnable(RecordManager.get().getProductEntity().hasVideo() && videoSize != 1);
        setNextEnable(RecordManager.get().getProductEntity().isReachMin());
    }

    /**
     * 开始播放视频
     */
    public void playVideo(int currentIndex) {
        tvNext.setVisibility(View.INVISIBLE);
        recordMenu.setVisibility(View.INVISIBLE);
        if (isFullRecord) {
            flFullRecord.showRecording(true);
        } else {
            act.smallRecordViewList.get(currentIndex).showRecord();
        }
    }

    public void pauseVideo(int currentPreviewIndex) {
//        act.showBack(true);
        KLog.i("======pauseAllVideo" + RecordManager.get().getProductEntity().isReachMin());
        setNextEnable(RecordManager.get().getProductEntity().isReachMin());
        tvNext.setVisibility(View.VISIBLE);
        recordMenu.setVisibility(View.VISIBLE);
        recordOptionPanel.showAllOption(true);
        SmallRecordView smallRecordView;
        ShortVideoEntity videoEntity;
        for (int i = 0, n = act.smallRecordViewList.size(); i < n; i++) {
            videoEntity = RecordManager.get().getShortVideoEntity(i);
            smallRecordView = act.smallRecordViewList.get(i);
            smallRecordView.showAllViews(n != 1, i == currentPreviewIndex, videoEntity.hasVideo(), videoEntity.isImport(), videoEntity.reachMin());
            smallRecordView.setCanClick(true);
            smallRecordView.showDuring(videoEntity.getDuringString());
            KLog.i("====暂停播放视频" + i + "record-end-clickable->" + smallRecordView.clickable);
        }
        resetMenu(RecordManager.get().getShortVideoEntity(currentPreviewIndex));
    }

    public void enterFullRecord(int currentPreviewIndex,SmallRecordView smallRecordView1){
        LiveCameraZoomHandler mCameraZoomHlr = new LiveCameraZoomHandler(flFullRecord.getContext(), null);
        FrameInfo mFrameInfo = RecordManager.get().getFrameInfo();
        flFullRecord.setVisibility(View.VISIBLE);
        isFullRecord = true;
        flFullRecord.setFullRecordListener(new exitFullRecordListener(smallRecordView1, currentPreviewIndex) {

            @Override
            public void onExitFullRecord() {
                exitFullRecord(smallRecordView1.getPreview());
            }
        });
        KLog.i("======enterFullRecord--showRecording--false");
        flFullRecord.showRecording(false);
        flFullRecord.setVisibleRatio(mFrameInfo.getLayoutAspectRatio(currentPreviewIndex));
        float layoutAspectRatio = mFrameInfo.getLayoutAspectRatio(currentPreviewIndex);
        KLog.d("mFrameInfo.getLayoutAspectRatio(currentPreviewIndex)==" + mFrameInfo.getLayoutAspectRatio(currentPreviewIndex));
//        flFullRecord.getGlTouchView().setZoomHandler(mCameraZoomHlr);

        dcRecorderHelper.previewCamera(flFullRecord.getPreview());
    }


    public void exitFullRecord(RelativeLayout relativeLayout) {
        flFullRecord.setVisibility(View.GONE);
        isFullRecord = false;
        flFullRecord.getGlTouchView().setViewHandler(null);
//        flFullRecord.getGlTouchView().setZoomHandler(null);
        selectSmallCamera(relativeLayout);
    }

    public void selectSmallCamera(RelativeLayout relativeLayout) {
        previewCamera(relativeLayout);
    }

    public void selectSmallCamera(int index) {
        KLog.i(index+"resetView--shortvideo--selectSmallCamera>"+(act.smallRecordViewList.size()));
        if (index>-1 && index < act.smallRecordViewList.size()) {
            selectSmallCamera(act.smallRecordViewList.get(index).getPreview());
        }
    }

    public void resetView(int currentPreviewIndex) {
        for (int i = 0, n = act.smallRecordViewList.size(); i < n; i++) {
            ShortVideoEntity videoEntity = RecordManager.get().getShortVideoEntity(i);
            if (videoEntity != null) {
                if (!TextUtils.isEmpty(videoEntity.editingVideoPath)
                        && new File(videoEntity.editingVideoPath).exists()) {
                    resetPreItem(i, false, false, false, true,false,false);
                } else {
                    if(i<act.smallRecordViewList.size())
                        act.smallRecordViewList.get(i).showStatus(
                                i == currentPreviewIndex, videoEntity.hasVideo(), videoEntity.isImport(), videoEntity.reachMin());
                }
            }
        }
        KLog.i(currentPreviewIndex+"resetView--shortvideo-->"+(!RecordManager.get().getShortVideoEntity(currentPreviewIndex).isImport()));
        if (!RecordManager.get().getShortVideoEntity(currentPreviewIndex).isImport()) {
            selectSmallCamera(currentPreviewIndex);
        }
        hidePanel();
    }

    /**
     * 视频下载完成后
     */
    public void refreshView(int currentPreviewIndex) {
        //替换进来   素材下载完毕~~~~
        if (recordOptionPanel == null)
            return;
        recordOptionPanel.setFrameEnable(true);
        //更新界面
        for (int i = 0, n = act.smallRecordViewList.size(); i < n; i++) {
            act.smallRecordViewList.get(i).hideCoverView();
            ShortVideoEntity videoEntity = RecordManager.get().getShortVideoEntity(i);
            if (videoEntity != null) {
                if (!TextUtils.isEmpty(videoEntity.editingVideoPath)
                        && new File(videoEntity.editingVideoPath).exists()) {
                    resetPreItem(i, false, false, false, true,false,false);
                } else {
                    KLog.i("index i " + i + " currentPreviewIndex " + currentPreviewIndex);
                    if (i != currentPreviewIndex) {
                        act.smallRecordViewList.get(i).showStatus(
                                i == currentPreviewIndex, videoEntity.hasVideo(), videoEntity.isImport(), videoEntity.reachMin());
                    }
                }
            }
        }
        List<ShortVideoEntity> shortVideoList = RecordManager.get().getProductEntity().shortVideoList;
        act.recordActivitySdkVideoHelper.transformAudio2to1(shortVideoList);//转换声道
        selectSmallCamera(currentPreviewIndex);
        hidePanel();
    }

    private void previewCamera(RelativeLayout relativeLayout) {
        //调用DcRecorder
        DCRecorderHelper dcRecorderHelper = new DCRecorderHelper(act);
        dcRecorderHelper.previewCamera(relativeLayout);
    }

    /**
     * 判断设置下一步是否可用
     *
     * @param enable
     */
    public void setNextEnable(boolean enable) {
        if (enable) {
            boolean isOk = false;
            List<ShortVideoEntity> videoList = RecordManager.get().getProductEntity().shortVideoList;
            if (videoList != null) {
                for (int i = 0, size = videoList.size(); i < size; i++) {
                    ShortVideoEntity videoEntity = videoList.get(i);
                    if (videoEntity != null && !videoEntity.getVideoType().equals(String.valueOf(SelectFrameActivity.VIDEO_TYPE_TEAMWORK)) && videoEntity.hasVideo()) {
                        if (videoEntity.getDuring() >= RecordManager.get().getSetting().getMinDuration()) {
                            isOk = true;
                            break;
                        }
                    }
                }
            }
            enable = isOk;
        }
        tvNext.setClickable(enable);
        tvNext.setTextColor(act.getResources().getColor(enable ? R.color.hh_color_g : R.color.hh_color_c));
    }

    public void hidePanel() {
        if (filterLayout != null) {
            filterLayout.setVisibility(View.INVISIBLE);
        }
        if (llSpeedPanel != null) {
            llSpeedPanel.setVisibility(View.INVISIBLE);
        }
        if(countDownRv!=null){
            countDownRv.setVisibility(View.INVISIBLE);
        }
    }

    private boolean hasFocus() {
        boolean hasFocus = false;
        for (int i = 0, n = act.smallRecordViewList.size(); i < n; i++) {
            if (act.smallRecordViewList.get(i).isFocus()) {
                hasFocus = true;
                break;
            }
        }
        return hasFocus;
    }

    public void selectSmallCamera(int index, boolean enableRecord) {
        if (index < act.smallRecordViewList.size()) {
            ShortVideoEntity videoEntity = RecordManager.get().getShortVideoEntity(index);
            KLog.i("currentPreviewIndex==-->focus"+index);
            act.smallRecordViewList.get(index).setFocus(true);
            act.smallRecordViewList.get(index).showStatus(
                    true, videoEntity.hasVideo(), videoEntity.isImport(), videoEntity.reachMin(), !enableRecord);
            if (enableRecord) {
                if (extSpeedPanel != null) {
                    extSpeedPanel.setCheckedId(videoEntity.getCurrentSpeedIndex());
                }
                resetMenu(videoEntity);
                }
            selectSmallCamera(act.smallRecordViewList.get(index).getPreview());
            loadRecordProgress(index);
        }
    }

    /**
     * 预览
     * @param seekEnd
     * @param entryEdit
     * @param entrySort
     */
    public void combinePreview(final boolean seekEnd, final boolean entryEdit, final boolean entrySort) {
        for(ShortVideoEntity videoEntity1:RecordManager.get().getProductEntity().shortVideoList){
            if(videoEntity1.editingVideoPath!=null)
            KLog.i("resetPre---video>" + videoEntity1.editingVideoPath+(new File(videoEntity1.editingVideoPath).exists()));
            if(videoEntity1.editingAudioPath!=null)
            KLog.i("resetPre---audio>" + videoEntity1.editingAudioPath+(new File(videoEntity1.editingAudioPath).exists()));
        }
        KLog.d("combinePreview==-->" + act.getCurrentPreviewIndex());
        if (act.playerEngine == null || act.playerEngine.isNull()) {
            act.playerEngine = new PlayerEngine();
        }

        final List<MediaObject> mediaObjects = PlayerContentFactory.getPlayerMediaFromProductWidthAudio(RecordManager.get().getProductEntity());
        KLog.i("combinePreview-====当前index:" + act.getCurrentPreviewIndex());
        //不用播放正在录制的格子下方的视频
        if(act.getCurrentPreviewIndex()>0 && act.getCurrentPreviewIndex()<RecordManager.get().getProductEntity().shortVideoList.size()){
            String targetPath = RecordManager.get().getShortVideoEntity(act.getCurrentPreviewIndex()).editingVideoPath;
            for(int i=0;i<mediaObjects.size();i++){
                if(mediaObjects.get(i).getFilePath()==targetPath&& RecordManager.get().getShortVideoEntity(act.getCurrentPreviewIndex()).getClipList().size()>0){
                    mediaObjects.remove(i);
                    break;
                }
            }
        }
        for(MediaObject mediaObject:mediaObjects){
            KLog.i("combinePreview-====all-id::" + mediaObject.assetId +mediaObject.getFilePath());
        }
        act.playerEngine.reset();
        if (mediaObjects != null && mediaObjects.size() > 0) {//不用合成  if()
            try {
                TextureView textureView = new TextureView(act);
                videoFrameLayout.removeAllViews();
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                videoFrameLayout.addView(textureView, params);
                act.playerEngine.build(textureView, new PlayerCreateListener() {
                    @Override
                    public void playCreated() {
                        Log.d("realease--->", "combinePreview-init-player-Created");
                        act.playerEngine.setMediaAndPrepare(mediaObjects);
                        float position = 0.1f;
                        if (seekEnd) {
                            position = RecordManager.get().getShortVideoEntity(act.getCurrentPreviewIndex()).getDuring();
                            if (act.playerEngine != null && !act.playerEngine.isNull()) {
                                KLog.i( "combinePreview====当前视频总时长：" + position + "==虚拟视频时长：" + act.playerEngine.getDuration());
                                position = act.playerEngine.getDuration() / 1000000;//(position > playerEngine.getDuration() ? playerEngine.getDuration() : position) - 0.3f;
                            }
                        }
                        KLog.i("combinePreview-====最终seek to :" + position);
                        if (act.playerEngine != null && !act.playerEngine.isNull()){
                            KLog.i("combinePreview-====最终seek to 1:" + position);
                            act.playerEngine.seekTo(position);
                        }
                        btRecorder.setEnabled(true);
                        try {
                            act.recordActivitySdkVideoHelper.dismissDialog();
                        }catch (Exception e){

                        }
                        if (entryEdit) {
                            EditVideoActivity.startEditVideoActivity(act, act.getCurrentPreviewIndex());
                        }
                        if (entrySort) {//进入更改画框
                            EditVideoGroupActivity.startEditVideoGroupActivity(act, EditProductionActivity.REQUEST_PAGE_TYPE_SORT, act.getCurrentPreviewIndex(), act.recordType);
                        }


                    }
                });
                KLog.i("combinePreview-======视频的组合预览完成-end" + videoFrameLayout.getChildCount());
            } catch (Exception e) {
                e.printStackTrace();
                KLog.e("combinePreview======视频的组合预览出错" + e);
            }
        } else {
            KLog.i("combinePreview======视频的组合预览没有视频");
        }
    }



    public void loadRecordProgress(int index) {
        ShortVideoEntity videoEntity = RecordManager.get().getShortVideoEntity(index);
        int count = videoEntity.getClipVideoSize();
        int[] duringItems = new int[count];
        for (int i = 0; i < count; i++) {
            duringItems[i] = (int) (((i == 0) ? 0 : duringItems[i - 1]) + videoEntity.getClipVideoDuring(i) * 1000);
        }
        recordOptionPanel.getProgressBar().removeAllItem();
        int p = videoEntity.getDuringMS();
        recordOptionPanel.getProgressBar().setProgress(p);
        recordOptionPanel.getProgressBar().addItemLines(duringItems);
    }

    //退出全屏录制的监听
    class exitFullRecordListener implements FullRecordView.OnExitFullRecordListener {

        SmallRecordView smallRecordView;
        int currentPreviewIndex;

        public exitFullRecordListener(SmallRecordView smallRecordView, int currentPreviewIndex) {
            this.smallRecordView = smallRecordView;
            this.currentPreviewIndex = currentPreviewIndex;
        }

        @Override
        public void onExitFullRecord() {

        }

    }
}
