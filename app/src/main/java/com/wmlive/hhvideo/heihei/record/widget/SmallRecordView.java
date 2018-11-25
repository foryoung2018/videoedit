package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.MyClickListener;
import com.wmlive.hhvideo.widget.CustomFontTextView;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 8/29/2017.
 */

public class SmallRecordView extends AnomalyView {

    private ImageView ivAdd;
    private ImageView ivRec;
    public LinearLayout llUpload;
    private TextView tvUpload;
    private ImageView ivDelete;
    private ImageView ivZoom;
    public CustomFontTextView tvDuring;
    private View viewMask;
    private RelativeLayout rlPreview;
    private RelativeLayout rlRoot;
    private LinearLayout progressBar;
    private LinearLayout refreshLayout;
    private ImageView ivRefresh;
    public TextView tvProgress;
    private SimpleDraweeView sdvCover;
    private OnSmallRecordClickListener recordClickListener;
    private boolean isFocus = false;
    public boolean clickable = true;
    private boolean showZoom = true;
    public CustomFontTextView tv_record_time;
    public LinearLayout ll_recorder_time;

    public SmallRecordView(@NonNull Context context) {
        super(context);
        initViews(context);
    }

    public SmallRecordView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    private void initViews(Context context) {
        KLog.i("SmallRecordView--initView");
        View view = LayoutInflater.from(context).inflate(R.layout.view_small_record, this, false);
        rlPreview = (RelativeLayout) view.findViewById(R.id.rlPreview);
        rlRoot = (RelativeLayout) view.findViewById(R.id.rlRoot);
        ivAdd = (ImageView) view.findViewById(R.id.ivAdd);
        ivRec = (ImageView) view.findViewById(R.id.ivRec);
        ll_recorder_time = (LinearLayout) view.findViewById(R.id.ll_recorder_time);
        llUpload = (LinearLayout) view.findViewById(R.id.llUpload);
        tvUpload = (TextView) view.findViewById(R.id.tvUpload);
        ivDelete = (ImageView) view.findViewById(R.id.ivDelete);
        ivZoom = (ImageView) view.findViewById(R.id.ivZoom);
        tvDuring = (CustomFontTextView) view.findViewById(R.id.tvDuring);
        tv_record_time = (CustomFontTextView) view.findViewById(R.id.tv_record_time);
        viewMask = view.findViewById(R.id.viewMask);


        sdvCover = (SimpleDraweeView) view.findViewById(R.id.sdvCover);
        progressBar = (LinearLayout) view.findViewById(R.id.progressLayout);
        refreshLayout = (LinearLayout) view.findViewById(R.id.refreshLayout);
        ivRefresh = (ImageView) view.findViewById(R.id.ivRefresh);
        tvProgress = (TextView) view.findViewById(R.id.tvProgress);

        addView(view);
        ivRefresh.setOnClickListener(clickListener);
        rlRoot.setOnClickListener(clickListener);
        llUpload.setOnClickListener(clickListener);
//        ivDelete.setOnClickListener(clickListener);
        ivZoom.setOnClickListener(clickListener);

    }

    public void showDuring(String during) {
        KLog.d("during==" + during);
        if (tvDuring.getVisibility() != VISIBLE) {
            tvDuring.setVisibility(VISIBLE);
        }
        if (during.equals("00:00")) {
            tvDuring.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(during)) {
            tvDuring.setVisibility(View.GONE);
        }
        tvDuring.setText(during);
    }

    public void setShowZoom(boolean showZoom) {
        this.showZoom = showZoom;
    }

    public boolean isFocus() {
        return isFocus;
    }

    public void setFocus(boolean focus) {
        isFocus = focus;
    }

    public void showStatus(boolean isCurrent, boolean hasVideo, boolean isImport, boolean reachMin) {
        showStatus(isCurrent, hasVideo, isImport, reachMin, false);
    }

    public void showStatus(boolean isCurrent, boolean hasVideo, boolean isImport, boolean reachMin, boolean hasDownload) {
//        if (showZoom) {
        if (isCurrent) {
            if (hasVideo) {
                if (isImport) {
                    showEditDelete(reachMin, hasVideo);
                } else {
                    showEditZoom(reachMin);
                }
            } else {
                showUploadZoom(hasDownload);
            }
        } else {
            if (hasVideo) {
                showEditDelete(reachMin, hasVideo);
            } else {
                if (hasDownload) {
                    showEditDelete(reachMin, true);
                } else {
                    showAdd();
                }
            }
        }
//    }
//        else {
//            showEditDelete(reachMin, hasVideo);
//        }
    }

    public void showAllViews(boolean show, boolean isCurrent, boolean hasVideo, boolean isImport, boolean reachMin) {
        if (show) {
            showStatus(isCurrent, hasVideo, isImport, reachMin);
            viewMask.setVisibility(GONE);
        } else {
//            ivAdd.setVisibility(GONE);
//            ivEdit.setVisibility(GONE);
//            ivDelete.setVisibility(GONE);
//            ivZoom.setVisibility(GONE);
            viewMask.setVisibility(isCurrent ? GONE : VISIBLE);
        }
    }

    /**
     * 当前正在录制
     */
    public void showRecord() {
        ivAdd.setVisibility(GONE);
        ll_recorder_time.setVisibility(VISIBLE);
        ivDelete.setVisibility(GONE);
        llUpload.setVisibility(GONE);
        ivZoom.setVisibility(GONE);
        showRecordAnimotion(VISIBLE);
    }

    private void showRecordAnimotion(int i) {
        if (i == View.VISIBLE || i == VISIBLE) {
            KLog.d("可见");
            AlphaAnimation alphaAnimation1 = new AlphaAnimation(0.3f, 1.0f);
            alphaAnimation1.setDuration(700);
            alphaAnimation1.setRepeatCount(Animation.INFINITE);
            alphaAnimation1.setRepeatMode(Animation.REVERSE);
            ivRec.setAnimation(alphaAnimation1);
            alphaAnimation1.start();
        } else if (i == INVISIBLE || i == GONE) {
            KLog.d("不可见");
            ivRec.clearAnimation();
        }
    }

    /**
     * 如果正在播放，隐藏删除按钮
     */
    public void showPlaying(boolean play){
        if(play){
            ivDelete.setVisibility(GONE);
        }
    }

    private void showUploadZoom(boolean hasDownload) {
        ivAdd.setVisibility(GONE);
        ll_recorder_time.setVisibility(GONE);
        ivDelete.setVisibility(GONE);
        if (hasDownload) {
//            llUpload.setVisibility(GONE);
            llUpload.setVisibility(VISIBLE);
            llUpload.setEnabled(false);
            tvUpload.setAlpha(0.5f);
        } else {
            llUpload.setVisibility(VISIBLE);
            llUpload.setEnabled(true);
            tvUpload.setAlpha(1f);
        }
        ivZoom.setVisibility(showZoom ? VISIBLE : GONE);
        tvDuring.setVisibility(GONE);
    }

    private void showEditDelete(boolean reachMin, boolean hasVideo) {
        ivAdd.setVisibility(GONE);
        ll_recorder_time.setVisibility(GONE);
//        ivDelete.setVisibility(hasVideo ? VISIBLE : GONE);
        llUpload.setVisibility(GONE);
        ivZoom.setVisibility(GONE);
    }

    private void showEditZoom(boolean reachMin) {
        ivAdd.setVisibility(GONE);
        ll_recorder_time.setVisibility(GONE);
//        ivDelete.setVisibility(!showZoom ? VISIBLE : GONE);
        llUpload.setVisibility(GONE);
        ivZoom.setVisibility(showZoom ? VISIBLE : GONE);
    }

    public void hideAdd(){
        ivAdd.setVisibility(GONE);
    }


    public void showAdd() {
        ivAdd.setVisibility(VISIBLE);
        ll_recorder_time.setVisibility(GONE);
        ivDelete.setVisibility(GONE);
        llUpload.setVisibility(GONE);
        ivZoom.setVisibility(GONE);
        tvDuring.setVisibility(GONE);
    }

    public void hideCoverView() {
        sdvCover.setVisibility(GONE);
        progressBar.setVisibility(View.GONE);
        ivDelete.setVisibility(View.GONE);
    }

    /**
     * 设置封面
     *
     * @param materialCover
     * @param materialSmallCover
     */
    public void setMaterialCover(String materialCover, String materialSmallCover) {
        sdvCover.setVisibility(View.VISIBLE);
        sdvCover.setController(Fresco.newDraweeControllerBuilder().setUri(materialCover)
                .setLowResImageRequest(ImageRequest.fromUri(materialSmallCover))
                .setOldController(sdvCover.getController())
                .setAutoPlayAnimations(true)
                .build());
    }

    /**
     * 展示封面
     *
     * @param requestOK 请求成功
     */
    public void showMaterialCover(boolean requestOK) {
        progressBar.setVisibility(View.INVISIBLE);
        if (requestOK) {
            refreshLayout.setVisibility(View.INVISIBLE);
            sdvCover.setVisibility(View.VISIBLE);
        } else {
            refreshLayout.setVisibility(View.VISIBLE);
            sdvCover.setVisibility(View.GONE);
            ivAdd.setVisibility(INVISIBLE);
        }
    }

    public void hideMaterialCover() {
        progressBar.setVisibility(View.INVISIBLE);
//        ivDelete.setVisibility(View.VISIBLE);
        sdvCover.setVisibility(View.GONE);
    }

    /**
     * 显示加载进度条
     */
    public void showProgress() {
        refreshLayout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void setProgress(int progress) {
        tvProgress.setText(getResources().getString(R.string.load_progress, progress));
    }


    public RelativeLayout getPreview() {
        rlPreview.setVisibility(VISIBLE);
        return rlPreview;
    }

    public void setRecorderClickListener(OnSmallRecordClickListener recordClickListener) {
        this.recordClickListener = recordClickListener;
    }

    public void setCanClick(boolean clickable) {
        KLog.i("click---setCanClick>"+clickable);
        this.clickable = clickable;
    }

    private MyClickListener clickListener = new MyClickListener() {
        @Override
        protected void onMyClick(View v) {
            KLog.i("click--->"+v.getId()+clickable+recordClickListener);
            if (recordClickListener != null && clickable) {
                int index = getTag() == null ? -1 : (Integer) getTag();
                switch (v.getId()) {
                    case R.id.ivAdd:
                        break;
                    case R.id.ivEdit:
                        recordClickListener.onEditClick(index, SmallRecordView.this);
                        break;
                    case R.id.llUpload:
                        recordClickListener.onUploadClick(index, SmallRecordView.this);
                        break;
                    case R.id.ivDelete:
//                        recordClickListener.onDeleteClick(index, SmallRecordView.this);
                        break;
                    case R.id.ivZoom:
                        recordClickListener.onZoomClick(index, SmallRecordView.this);
                        break;
                    case R.id.rlRoot:
                        recordClickListener.onAddClick(index, SmallRecordView.this);
                        break;
                    case R.id.ivRefresh:
                        progressBar.setVisibility(View.VISIBLE);
                        refreshLayout.setVisibility(View.INVISIBLE);
                        sdvCover.setVisibility(View.INVISIBLE);
                        recordClickListener.onRefreshClick(index, SmallRecordView.this);
                        break;
                }
            }

        }
    };

    public interface OnSmallRecordClickListener {
        void onEditClick(int index, SmallRecordView view);

        void onUploadClick(int index, SmallRecordView view);

        void onDeleteClick(int index, SmallRecordView view);

        void onZoomClick(int index, SmallRecordView view);

        void onAddClick(int index, SmallRecordView view);

        void onRefreshClick(int index, SmallRecordView view);
    }

}
