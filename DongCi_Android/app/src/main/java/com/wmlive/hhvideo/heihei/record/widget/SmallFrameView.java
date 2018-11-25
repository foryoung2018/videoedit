package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.wmlive.hhvideo.heihei.beans.opus.UploadMaterialEntity;
import com.wmlive.hhvideo.heihei.discovery.DiscoveryUtil;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.MyClickListener;
import com.wmlive.hhvideo.utils.download.FileDownload;

import cn.wmlive.hhvideo.R;

/**
 * 选择画框与交换位置FrameView
 * Created by wenlu on 2017/8/31.
 */
public class SmallFrameView extends AnomalyView {

    public static final int VIEW_TYPE_SELECT_FRAME = 0;//不显示移动的图标
    public static final int VIEW_TYPE_SWAP_POSITION = 1;//显示移动的图标
    private SimpleDraweeView sdvCover;//动态图
    private ImageView ivDrag;
    private LinearLayout progressBar;
    private LinearLayout refreshLayout;
    private ImageView ivRefresh;
    private ImageView ivDelete;
    public TextView tvProgress;
    public TextView tvDuring;
    private OnSmallFrameClickListener mFrameClickListener;
    private int mViewType = 0;
    /*素材的封面地址*/
    private String coverPath;

    public UploadMaterialEntity materialEntity;

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public SmallFrameView(@NonNull Context context) {
        this(context, null);
    }

    public SmallFrameView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmallFrameView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_small_frame_view, this, false);
//        view.setBackgroundColor(getResources().getColor(R.color.bg_frame_view_bg));
        sdvCover = (SimpleDraweeView) view.findViewById(R.id.sdvCover);
        ivDrag = (ImageView) view.findViewById(R.id.ivDrag);
        ivDelete = (ImageView) view.findViewById(R.id.ivDelete);
        progressBar = (LinearLayout) view.findViewById(R.id.progressLayout);
        refreshLayout = (LinearLayout) view.findViewById(R.id.refreshLayout);
        ivRefresh = (ImageView) view.findViewById(R.id.ivRefresh);
        tvProgress = (TextView) view.findViewById(R.id.tvProgress);
        tvDuring = (TextView) view.findViewById(R.id.tvDuring);
        ivRefresh.setOnClickListener(clickListener);
//        ivDelete.setOnClickListener(clickListener);
        addView(view);
    }

    private void showDragView(boolean isShow) {
        progressBar.setVisibility(View.INVISIBLE);
        refreshLayout.setVisibility(View.INVISIBLE);
        if (isShow) {
            ivDrag.setImageDrawable(getResources().getDrawable(R.drawable.icon_video_move));
            ivDrag.setVisibility(VISIBLE);
            setDefaultCoverImage();
        } else {
            ivDrag.setVisibility(INVISIBLE);
        }
    }

    /**
     * 设置每个layout封面
     *
     * @param coverPath
     */
    public void setCoverImage(String coverPath) {
        this.coverPath = coverPath;
        sdvCover.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(coverPath)) {
            KLog.d("coverPath==" + coverPath);
            if (coverPath.startsWith("http")) {//网络图片地址
                sdvCover.setController(Fresco.newDraweeControllerBuilder().setUri(coverPath)
                        .setLowResImageRequest(ImageRequest.fromUri(coverPath))
                        .setOldController(sdvCover.getController())
                        .setAutoPlayAnimations(true)
                        .build());
            } else {//本地图片地址
                Uri uri = new Uri.Builder()
                        .scheme(UriUtil.LOCAL_FILE_SCHEME)
                        .path(coverPath)
                        .build();
                ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                        .setLocalThumbnailPreviewsEnabled(true)
                        .build();
                sdvCover.setController(Fresco.newDraweeControllerBuilder().setImageRequest(request)
                        .setOldController(sdvCover.getController())
                        .setAutoPlayAnimations(true)
                        .build());
            }
        } else {
            setDefaultCoverImage();
        }
    }

    public void setDefaultCoverImage() {
        sdvCover.setImageDrawable(new ColorDrawable(0x000000));
//        sdvCover.setImageDrawable(new ColorDrawable(0x000000));
//        Uri uri = new Uri.Builder()
//                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
//                .path(String.valueOf(R.drawable.bg_video_default))
//                .build();
//        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
//                .setLocalThumbnailPreviewsEnabled(true)
//                .build();
////        sdvCover.setController(Fresco.newDraweeControllerBuilder().setUri(uri)
//        sdvCover.setController(Fresco.newDraweeControllerBuilder().setImageRequest(request)
//                .setOldController(sdvCover.getController())
//                .setAutoPlayAnimations(true)
//                .build());
    }

    /**
     * 设置封面
     *
     * @param materialCover
     * @param materialSmallCover
     */
    public void setMaterialCover(String materialCover, String materialSmallCover) {
        this.coverPath = materialCover;
        KLog.d("setMaterialCover:    materialCover==" + materialCover);
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
//            ivDelete.setVisibility(View.VISIBLE);
        } else {
            refreshLayout.setVisibility(View.VISIBLE);
            sdvCover.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * 显示加载进度条
     */
    public void showProgress() {
        refreshLayout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        sdvCover.setVisibility(View.VISIBLE);
//        ivDelete.setVisibility(View.VISIBLE);
    }

    public void setProgress(int progress) {
        showProgress();
        tvProgress.setText(getResources().getString(R.string.load_progress, progress));
        KLog.d("TESTTAG", "setProgress: " + progress);
    }

    public void setDuration(String duration) {
        tvDuring.setVisibility(VISIBLE);
        tvDuring.setText(duration);
    }

    private MyClickListener clickListener = new MyClickListener() {
        @Override
        protected void onMyClick(View v) {
            if (mFrameClickListener != null) {
                int index = getTag() == null ? -1 : (Integer) getTag();
                switch (v.getId()) {
                    case R.id.ivRefresh:
                        progressBar.setVisibility(View.VISIBLE);
                        refreshLayout.setVisibility(View.INVISIBLE);
                        sdvCover.setVisibility(View.INVISIBLE);
                        mFrameClickListener.onRefreshClick(index, SmallFrameView.this);
                        break;
                    case R.id.ivDelete:
                        progressBar.setVisibility(View.GONE);
                        refreshLayout.setVisibility(View.INVISIBLE);
                        sdvCover.setVisibility(View.GONE);
                        ivDelete.setVisibility(View.GONE);
                        tvDuring.setVisibility(View.GONE);
                        mFrameClickListener.onDeleteClick(index, SmallFrameView.this);
                        break;
                }
            }

        }
    };

    public void setOnSmallFrameClickListener(OnSmallFrameClickListener frameClickListener) {
        this.mFrameClickListener = frameClickListener;
    }

    public void setViewType(int viewType) {
        this.mViewType = viewType;
        if (viewType == VIEW_TYPE_SELECT_FRAME) {
            showDragView(false);
        } else if (viewType == VIEW_TYPE_SWAP_POSITION) {
            showDragView(true);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    /**
     * 隐藏所有ui
     */
    public void hideMaterialCover() {
        coverPath = "";
        progressBar.setVisibility(View.GONE);
        refreshLayout.setVisibility(View.INVISIBLE);
        sdvCover.setVisibility(View.GONE);
        ivDelete.setVisibility(View.GONE);
        tvDuring.setVisibility(View.GONE);
    }

    public void hideProgressbar() {
        progressBar.setVisibility(View.GONE);
    }

    /**
     * 设置ui状态
     * <p>
     * 时长、封面、加载进度、加载失败
     *
     * @param uploadMaterialEntity
     */
    public void setUIState(UploadMaterialEntity uploadMaterialEntity) {
        this.materialEntity = uploadMaterialEntity;
        if (materialEntity != null) {
            setMaterialCover(uploadMaterialEntity.material_cover, uploadMaterialEntity.material_cover);
            setDuration(DiscoveryUtil.convertTime((int) uploadMaterialEntity.material_length / 1000));
            showProgressState();
        } else {
            hideMaterialCover();
        }
    }

    private void showProgressState() {
        if (materialEntity.downloadState == FileDownload.RESULT_DOWNLOADING) {
            refreshLayout.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
        if (materialEntity.downloadState == FileDownload.RESULT_ERROR) {
            refreshLayout.setVisibility(View.VISIBLE);
        } else {
            refreshLayout.setVisibility(View.INVISIBLE);
        }
    }

    public interface OnSmallFrameClickListener {
        void onRefreshClick(int index, SmallFrameView view);

        void onDeleteClick(int index, SmallFrameView view);
    }
}
