package com.wmlive.hhvideo.heihei.mainhome.widget;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.wmlive.hhvideo.dcijkplayer.AbsIjkPlayListener;
import com.wmlive.hhvideo.dcijkplayer.widget.media.IRenderView;
import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.manager.DcIjkPlayerManager;
import com.wmlive.hhvideo.heihei.beans.frame.FrameInfo;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.heihei.beans.opus.UploadMaterialEntity;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.mainhome.adapter.RollCommentAdapter;
import com.wmlive.hhvideo.heihei.mainhome.adapter.VideoDetailListRecyclerAdapter;
import com.wmlive.hhvideo.heihei.mainhome.fragment.RecommendFragment;
import com.wmlive.hhvideo.heihei.mainhome.presenter.ShortVideoViewCallback;
import com.wmlive.hhvideo.heihei.personal.activity.UserHomeActivity;
import com.wmlive.hhvideo.heihei.record.activitypresenter.RecordMvActivityHelper;
import com.wmlive.hhvideo.heihei.record.widget.CustomFrameView;
import com.wmlive.hhvideo.heihei.record.widget.SmallShowVideoView;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.CommonUtils;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.FrameUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ParamUtis;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;
import com.wmlive.hhvideo.widget.AutoLineFeedView;
import com.wmlive.hhvideo.widget.BaseCustomView;
import com.wmlive.hhvideo.widget.CustomFontTextView;
import com.wmlive.hhvideo.widget.GestureView;
import com.wmlive.hhvideo.widget.RatioLayout;
import com.wmlive.hhvideo.widget.RollRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.wmlive.hhvideo.R;

/**
 * Created by hsing on 2018/4/9.
 */

public class VideoDetailItemView1 extends BaseCustomView implements
        VideoDetailOptionView.OnVideoOptionClickListener,
        VideoControlBar.OnControlBarClickListener,
        GestureView.GestureViewListener {

    public static final int SHOW_TYPE_NORMAL = 0;
    public static final int SHOW_TYPE_CONTROL_BAR = 1;
    public static final int SHOW_TYPE_FULL_SCREEN = 2;
    public static final int SHOW_TYPE_FRAME_VIEW = 3;

    public ImageView ivCover;
    public TextView tvJoin;
    public ImageView ivClose;
    public FrameLayout flPlayerContainer;
    public ImageView ivLoading;
    public View pauseShadowView;
    public RelativeLayout useCurrentTemplateRl;
    public CustomFontTextView useCurrentTemplateTv;
    public TextView tvUserName;

    public VideoDetailOptionView detailOptionView;
    public RatioLayout ratioLayout;
    public CustomFrameView customFrameView;
    public GestureView viewBlankHolder;
    public VideoControlBar videoControlBar;
    public RelativeLayout rlFullSize;
    private RollRecyclerView rvComment;
    private View viewMask;

    public LinearLayout llDescTop;
    public TextView tvTitleT;
    public AutoLineFeedView autoLineFeedViewT;

    public LinearLayout llDescBottom;
    public TextView tvTitleB;
    public AutoLineFeedView autoLineFeedViewB;

    private ShortVideoItem shortVideoItem;
    private Animator loadingAnimator;
    private ShortVideoViewCallback mListener;
    private int showType = SHOW_TYPE_NORMAL;
    private float showRatio;
    private boolean changeAspectRatio;
    private int videoPosition;
    public boolean isDragging = false; // 是否在拖动进度条
    private ControlBarHandler mHandler;
    private boolean playStatus = true;
    private int videoType;
    private int pageId;
    private VideoDetailListRecyclerAdapter adapter;
    private AbsIjkPlayListener playListener;

    private static final float sScreenRatio = DeviceUtils.getScreenWHRatio();


    public VideoDetailItemView1(Context context) {
        super(context);
    }

    public VideoDetailItemView1(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoDetailItemView1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.video_detail_item_full_size_view1;
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {

    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.ivClose:
                if (mListener != null) {
                    mListener.onCloseClick();
                }
                break;
            case R.id.tvJoin:
                if (shortVideoItem != null && shortVideoItem.is_delete == 1) {
                    ToastUtil.showToast(R.string.stringWorkDeleted);
                    return;
                }
                if (mListener != null) {
                    mListener.onJoinSingleClick(videoPosition, shortVideoItem);
                }
                break;
            case R.id.use_current_template_tv:
                if (mListener != null) {
                    mListener.onJoinCurrentTemplateClick(videoPosition, shortVideoItem);
                }
                break;
            default:
                break;
        }
    }

    public void initData(int pageId, VideoDetailListRecyclerAdapter adapter, int videoType,
                         ShortVideoItem shortVideoItem, int position, AbsIjkPlayListener playListener) {
        findViews();
        this.pageId = pageId;
        this.playListener = playListener;
        this.adapter = adapter;
        this.videoType = videoType;
        this.videoPosition = position;
        this.shortVideoItem = shortVideoItem;
        if (shortVideoItem != null) {
            mHandler = new ControlBarHandler(this);
            viewBlankHolder.setGestureViewListener(this);
            iniViewLayout();
            setClose(shortVideoItem.detail_show_type);
            initCustomFrameView();
            setListener();
            videoControlBar.init(shortVideoItem.detail_show_type);
            videoControlBar.setOnControlBarListener(this);
            ratioLayout.setRatio(showRatio, true);
            if (ShortVideoItem.VIDEO_SHOW_TYPE_CENTER_SCREEN.equals(shortVideoItem.detail_show_type)) {
                if (showRatio == 1) {
//                    ivCover.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                }
            }
            setTitle();
            initAutherView();
            detailOptionView.setOpusInfo(shortVideoItem);
            if (null != shortVideoItem.getShare_info()) {
                shortVideoItem.getShare_info().download_link = shortVideoItem.getDownload_link();
            }
            setVideoCover(shortVideoItem.getOpus_cover());
            if (videoType == RecommendFragment.TYPE_SINGLE_WORK) {
                getRootView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DcIjkPlayerManager.get().resetUrl();
                        adapter.forcePlay(false);
                        if (!CollectionUtil.isEmpty(shortVideoItem.barrage_list)) {
                            VideoDetailItemView1.this.runRollComment(true);
                        }
                    }
                }, 100);
            }
        } else {
            ToastUtil.showToast("数据错误");
        }
    }

    private int iniViewLayout() {
        KLog.i("=======iniViewLayout:" + showType);
        int playerRule;
        RelativeLayout.LayoutParams playerLp =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams commentLp =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        if (ShortVideoItem.VIDEO_SHOW_TYPE_FULL_SCREEN.equals(shortVideoItem.detail_show_type)) {
            playerRule = RelativeLayout.ALIGN_PARENT_TOP;
            showRatio = sScreenRatio;
            detailOptionView.changeTheme(VideoDetailOptionView.THEME_WHITE, shortVideoItem);
            ivClose.setImageDrawable(getResources().getDrawable(R.drawable.icon_close_white));
            tvJoin.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_video_create_white));
            tvTitleT.setTextColor(getResources().getColor(R.color.white));
            tvTitleB.setTextColor(getResources().getColor(R.color.white));
            commentLp.removeRule(RelativeLayout.BELOW);
            commentLp.height = DeviceUtils.dip2px(DCApplication.getDCApp(), 100);
        } else if (ShortVideoItem.VIDEO_SHOW_TYPE_TOP_SCREEN.equals(shortVideoItem.detail_show_type)) {
            playerRule = RelativeLayout.ALIGN_PARENT_TOP;
            detailOptionView.changeTheme(VideoDetailOptionView.THEME_BLACK, shortVideoItem);
            showRatio = 0.8f;
            ivClose.setImageDrawable(getResources().getDrawable(R.drawable.icon_close_white));
            tvJoin.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_video_create_white));
            tvTitleT.setTextColor(getResources().getColor(R.color.white));
            tvTitleB.setTextColor(getResources().getColor(R.color.white));
            commentLp.addRule(RelativeLayout.BELOW, R.id.ratioLayout);
        } else if (ShortVideoItem.VIDEO_SHOW_TYPE_CENTER_SCREEN.equals(shortVideoItem.detail_show_type)) {
            playerRule = RelativeLayout.CENTER_IN_PARENT;
            detailOptionView.changeTheme(VideoDetailOptionView.THEME_BLACK, shortVideoItem);
            showRatio = shortVideoItem.opus_height == 0 ? 0f : shortVideoItem.opus_width * 1.0f / shortVideoItem.opus_height;
            ivClose.setImageDrawable(getResources().getDrawable(R.drawable.icon_close));
            tvJoin.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_video_create));
            tvTitleT.setTextColor(getResources().getColor(R.color.black));
            tvTitleB.setTextColor(getResources().getColor(R.color.black));
            if (showRatio < 1) {
                // 宽小于高，限高展示，以高为基准左右留间隙
                showRatio = 1;
                changeAspectRatio = true;
            }
            commentLp.addRule(RelativeLayout.BELOW, R.id.ratioLayout);
        } else {
            tvTitleT.setTextColor(getResources().getColor(R.color.black));
            tvTitleB.setTextColor(getResources().getColor(R.color.black));
            ivClose.setImageDrawable(getResources().getDrawable(R.drawable.icon_close));
            tvJoin.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_video_create));
            showRatio = 1;
            detailOptionView.changeTheme(VideoDetailOptionView.THEME_BLACK, shortVideoItem);
            playerRule = RelativeLayout.ALIGN_PARENT_TOP;
            commentLp.addRule(RelativeLayout.BELOW, R.id.ratioLayout);
        }
        detailOptionView.showShadow(ShortVideoItem.VIDEO_SHOW_TYPE_FULL_SCREEN.equals(shortVideoItem.detail_show_type));
        llDescTop.setBackgroundColor(getResources().getColor(ShortVideoItem.VIDEO_SHOW_TYPE_CENTER_SCREEN.equals(shortVideoItem.detail_show_type)
                ? R.color.app_background_color : R.color.transparent));
        llDescBottom.setVisibility(showBottomTitle() ? VISIBLE : GONE);
        llDescTop.setVisibility(showBottomTitle() ? GONE : VISIBLE);
        playerLp.addRule(playerRule, R.id.rlFullSize);
        ratioLayout.setLayoutParams(playerLp);
        commentLp.addRule(RelativeLayout.ABOVE, R.id.detailOptionView);
        rvComment.setLayoutParams(commentLp);
        viewMask.setVisibility(ShortVideoItem.VIDEO_SHOW_TYPE_FULL_SCREEN.equals(shortVideoItem.detail_show_type) ? GONE : VISIBLE);
        rlFullSize.setBackgroundColor(getResources().getColor(ShortVideoItem.VIDEO_SHOW_TYPE_FULL_SCREEN.equals(shortVideoItem.detail_show_type) ? R.color.black : R.color.white));
        return playerRule;
    }

    private void setClose(String showType) {
        KLog.i("=======setClose:" + showType);
        int playerAspectRatio = IRenderView.AR_ADJUST_MATCH_WIDTH;
        if (ShortVideoItem.VIDEO_SHOW_TYPE_FULL_SCREEN.equals(showType)) {
            ivClose.setImageDrawable(getResources().getDrawable(R.drawable.icon_close_white));
            tvJoin.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_video_create_white));
            playerAspectRatio = IRenderView.AR_ADJUST_MATCH_WIDTH;
        } else if (ShortVideoItem.VIDEO_SHOW_TYPE_TOP_SCREEN.equals(showType)) {
            ivClose.setImageDrawable(getResources().getDrawable(R.drawable.icon_close_white));
            tvJoin.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_video_create_white));
        } else if (ShortVideoItem.VIDEO_SHOW_TYPE_CENTER_SCREEN.equals(showType)) {
            ivClose.setImageDrawable(getResources().getDrawable(R.drawable.icon_close));
            tvJoin.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_video_create));
            if (isChangeAspectRatio()) {
                // 调整视频显示比例，适配限高
                playerAspectRatio = IRenderView.AR_ASPECT_FIT_PARENT;
            } else {
                playerAspectRatio = IRenderView.AR_ADJUST_MATCH_WIDTH;
            }
        } else {
            playerAspectRatio = IRenderView.AR_ADJUST_MATCH_WIDTH;
            ivClose.setImageDrawable(getResources().getDrawable(R.drawable.icon_close_white));
            tvJoin.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_video_create_white));
        }
        if (DcIjkPlayerManager.get().getPlayer() != null) {
            DcIjkPlayerManager.get().getPlayer().setAspectRatio(playerAspectRatio);
        }
    }

    public void refreshShortVideoItem(ShortVideoItem shortVideoItem) {
        this.shortVideoItem = shortVideoItem;
        KLog.i("=======refreshShortVideoItem:");
        initCustomFrameView();
        setTitle();
        initAutherView();
    }

    private void initAutherView() {
        KLog.i("=======initAutherView:");
        Map<Long, UserInfo> userMap = new ArrayMap<>(6);
        if (shortVideoItem.getUser() != null) {
            UserInfo author = shortVideoItem.getUser();
            if (!TextUtils.isEmpty(author.getName())) {
                userMap.put(author.getId(), author);
            }
        }

        if (!CollectionUtil.isEmpty(shortVideoItem.materials)) {
            for (UploadMaterialEntity materialEntity : shortVideoItem.materials) {
                if (materialEntity != null && materialEntity.user != null) {
                    userMap.put(materialEntity.user.getId(), materialEntity.user);
                }
            }
        }
        autoLineFeedViewT.removeAllViews();
        autoLineFeedViewB.removeAllViews();
        AutoLineFeedView autoLineFeedView = showBottomTitle() ? autoLineFeedViewB : autoLineFeedViewT;
        if (autoLineFeedView != null) {
            int index = 0;
            for (Long userId : userMap.keySet()) {
                CustomFontTextView textView = new CustomFontTextView(getContext());
                if (index == 0) {
                    textView.setText("@" + userMap.get(userId).getName());
                } else {
                    textView.setText(" + @" + userMap.get(userId).getName());
                }
                textView.setGravity(Gravity.CENTER);
                if (ShortVideoItem.VIDEO_SHOW_TYPE_CENTER_SCREEN.equals(shortVideoItem.detail_show_type)) {
                    textView.setTextColor(getResources().getColor(R.color.border_no_checked));
                } else {
                    textView.setTextColor(getResources().getColor(R.color.transparent80_white));
                }
                // 字体大小, 单位sp
                textView.setTextSize(14);
                textView.setTag(userId);
                textView.setCustomFont(getContext(), "font/notosans_bold.ttf");
                autoLineFeedView.addView(textView, new AutoLineFeedView.OnChildClickListener() {

                    @Override
                    public void onClick(View view) {
                        KLog.d("xxxx", "child view click view tag " + view.getTag());
                        if (mListener != null) {
                            try {
                                Long userId = (Long) view.getTag();
                                mListener.onUserClick(userId);
                            } catch (ClassCastException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                index++;
            }
        }
    }

    private void setVideoCover(String opusCover) {
        ivCover.setVisibility(View.VISIBLE);
        GlideLoader.loadImage(opusCover, ivCover, R.drawable.bg_home_video_default);
    }

    private void setListener() {
        detailOptionView.setOptionClickListener(this);
        ivClose.setOnClickListener(this);
        tvJoin.setOnClickListener(this);
        useCurrentTemplateTv.setOnClickListener(this);
    }

    private void findViews() {
        ivClose = findViewById(R.id.ivClose);
        ivCover = findViewById(R.id.ivCover);
        tvJoin = findViewById(R.id.tvJoin);
        viewBlankHolder = findViewById(R.id.viewBlankHolder);
        videoControlBar = findViewById(R.id.videoControlBar);
        flPlayerContainer = findViewById(R.id.flPlayerContainer);
        ivLoading = findViewById(R.id.ivLoading);
        pauseShadowView = findViewById(R.id.pause_shadow_view);
        useCurrentTemplateRl = findViewById(R.id.use_current_template_rl);
        useCurrentTemplateTv = findViewById(R.id.use_current_template_tv);
        tvUserName = findViewById(R.id.tvUserName);

        llDescTop = findViewById(R.id.llDescTop);
        tvTitleT = findViewById(R.id.tvTitleT);
        autoLineFeedViewT = findViewById(R.id.autoLineFeedViewT);

        llDescBottom = findViewById(R.id.llDescBottom);
        tvTitleB = findViewById(R.id.tvTitleB);
        autoLineFeedViewB = findViewById(R.id.autoLineFeedViewB);

        detailOptionView = findViewById(R.id.detailOptionView);
        ratioLayout = findViewById(R.id.ratioLayout);
        viewMask = findViewById(R.id.viewMask);
        customFrameView = findViewById(R.id.customFrameView);
        rlFullSize = findViewById(R.id.rlFullSize);
        rvComment = findViewById(R.id.rvComment);
    }

    public void dismissVideoLoading() {
        if (loadingAnimator != null) {
            loadingAnimator.cancel();
            loadingAnimator = null;
        }
        if (ivLoading != null) {
            ivLoading.clearAnimation();
            ivLoading.setVisibility(View.GONE);
        }
    }

    public void setPlayIcon(boolean isPlaying) {
        setPlayIcon(isPlaying, false);
    }

    //是否显示播放按钮
    public void setPlayIcon(boolean isPlaying, boolean isError) {
        KLog.i("=========setPlayIcon:" + isPlaying);
//        llJoin.setVisibility(isPlaying ? VISIBLE : INVISIBLE);
//        ivPlayerStatus.setVisibility(isPlaying ? View.GONE : View.VISIBLE);
    }

    public void showVideoLoading() {
        if (ivLoading != null) {
            ivLoading.setVisibility(View.VISIBLE);
        }
        initVideoLoading();
        loadingAnimator.start();
    }

    private void initVideoLoading() {
        loadingAnimator = AnimatorInflater.loadAnimator(getContext(), R.animator.loading);
        loadingAnimator.setInterpolator(new LinearInterpolator());
        loadingAnimator.setTarget(ivLoading);
    }

    private void initCustomFrameView() {
        KLog.i("=======initCustomFrameView:");
        customFrameView.removeAllViews();
        if (shortVideoItem != null) {
            if (5 == shortVideoItem.origin) {//创意mv视频
                tvUserName.setText(shortVideoItem.user.getName());
            }
            List<SmallShowVideoView> itemViewList = new ArrayList<>();
            FrameInfo mFrameInfo = FrameUtils.ins().getFrameInfo(shortVideoItem.frame_layout);
            KLog.d("videoPosition:" + videoPosition + ",shortVideoItem:" + shortVideoItem + " ,mFrameInfo:" + mFrameInfo);
            KLog.d("ggq", "shortVideoItem.opus_width" + shortVideoItem.opus_width + "  shortVideoItem.opus_height" + shortVideoItem.opus_height);
            if (shortVideoItem.materials != null && mFrameInfo != null) {
                customFrameView.setUseScreenWidth(true);
                customFrameView.setVideoSizeRatio((shortVideoItem.opus_width - 4) * 1.0f / shortVideoItem.opus_height);//减去4是边界宽度
                SmallShowVideoView itemView;
                int layoutSize = mFrameInfo.getLayout().size();
                for (int i = 0; i < layoutSize; i++) {
                    itemView = new SmallShowVideoView(getContext());
                    if (5 == shortVideoItem.origin) {
                        itemView.ivReplace.setVisibility(INVISIBLE);
                    }
                    itemView.setLayoutInfo(mFrameInfo.getLayout().get(i), true);
                    itemView.setTag(i);
                    if (layoutSize == 1) {
                        itemView.setViewType(SmallShowVideoView.VIEW_TYPE_JOIN);
                    } else {
                        itemView.setViewType(SmallShowVideoView.VIEW_TYPE_REPLACE);
                    }
                    final UploadMaterialEntity materialEntity = findMaterial(i);
                    if (materialEntity != null) {
                        if (materialEntity.user != null) {
                            KLog.i("**** * entity.user.getName() " + materialEntity.user.getName());
                            itemView.setUserName(materialEntity.user.getName());
                        }
                        itemView.setTag(materialEntity.material_index);
                    }
                    itemView.setOnSmallShowVideoClickListener(new SmallShowVideoView.OnSmallShowVideoClickListener() {
                        @Override
                        public void onReplaceClick(int index, SmallShowVideoView view) {
                            if (shortVideoItem.is_teamwork == 1) {
                                if (mListener != null) {
                                    mListener.onJoinReplaceClick(index, shortVideoItem);
                                }
                            } else {
                                ToastUtil.showToast(shortVideoItem.teamwork_tips);
                            }
                        }

                        @Override
                        public void onJoinClick(int index, SmallShowVideoView view) {
                            if (shortVideoItem.is_teamwork == 1) {
                                if (shortVideoItem != null && shortVideoItem.is_delete == 1) {
                                    ToastUtil.showToast(R.string.stringWorkDeleted);
                                    return;
                                }
                                if (mListener != null) {
                                    mListener.onJoinSingleClick(index, shortVideoItem);
                                }
                            } else {
                                ToastUtil.showToast(shortVideoItem.teamwork_tips);
                            }
                        }

                        @Override
                        public void onUserNameClick(int index, SmallShowVideoView view) {
                            if (materialEntity != null) {
                                UserHomeActivity.startUserHomeActivity(customFrameView.getContext(), materialEntity.owner_id);
                            }
                        }
                    });
                    itemViewList.add(itemView);
                }
                if (itemViewList.size() > 0) {
                    customFrameView.setFrameView(mFrameInfo, itemViewList, false);
                }
            }
        }
    }

    public UploadMaterialEntity findMaterial(int materialIndex) {
        if (shortVideoItem.materials != null && shortVideoItem.materials.size() > 0) {
            for (int i = 0, size = shortVideoItem.materials.size(); i < size; i++) {
                UploadMaterialEntity materialEntity = shortVideoItem.materials.get(i);
                if (materialEntity != null && materialEntity.material_index == materialIndex) {
                    return materialEntity;
                }
            }
        }
        return null;
    }


    public boolean hasControlBarVisiable() {
        if (videoControlBar != null) {
            return videoControlBar.getVisibility() == View.VISIBLE;
        } else {
            return false;
        }
    }

    public int setCurrentPosition() {
        int duration = getDuration();
        int currentPosition = 0;
        if (DcIjkPlayerManager.get().getPlayer() != null) {
            currentPosition = DcIjkPlayerManager.get().getPlayer().getCurrentPosition();
        }
        return setPosition(duration, currentPosition);
    }

    @Override
    public void onLikeClick() {
        //这里不是连续点击
        if (mListener != null) {
            mListener.onLikeClick(videoPosition, shortVideoItem.getId(),
                    shortVideoItem.is_like(),
                    false,
                    true,
                    0, 0, 0, 0);
        }
    }

    @Override
    public void onCommentClick() {
        if (mListener != null) {
            mListener.onCommentClick(videoPosition, shortVideoItem.getId(), shortVideoItem);
        }
    }

    @Override
    public void onShareClick() {
        if (mListener != null) {
            mListener.onShareClick(videoPosition, shortVideoItem);
        }
    }

    @Override
    public void onGiftClick() {
        if (mListener != null) {
            mListener.onGiftClick(videoPosition, shortVideoItem);
        }
    }

    @Override
    public void onUserClick() {
        if (mListener != null && shortVideoItem != null) {
            UserInfo author = (shortVideoItem.getUser() == null ? new UserInfo() : shortVideoItem.getUser());
            mListener.onUserClick(author.getId());
        }
    }

    @Override
    public void onFollowClick() {
        if (mListener != null && shortVideoItem != null && shortVideoItem.getUser() != null && shortVideoItem.getUser().getId() > 0) {
            mListener.onFollowClick(videoPosition, shortVideoItem.getId(), shortVideoItem.getUser().getId(), shortVideoItem.getUser().isFollowed());
        } else {
            ToastUtil.showToast(R.string.hintErrorDataDelayTry);
        }
    }

    @Override
    public void onTopicClick() {
        if (mListener != null && shortVideoItem != null) {
            mListener.onTopicClick(shortVideoItem.getTopic_id());
        }
    }

    public void setOnVideoDetailViewClickListener(ShortVideoViewCallback listener) {
        this.mListener = listener;
    }

    private void resumePlay() {
        KLog.i("=======resumePlay:");
        if (shortVideoItem.getOpus_path().equals(DcIjkPlayerManager.get().getUrl())) {
            DcIjkPlayerManager.get().getPlayer().resumePlay();
        } else {
            //说明不是之前的视频了
            DcIjkPlayerManager.get().attachPlayer(flPlayerContainer, null);
            DcIjkPlayerManager.get().setVideoUrl(pageId, shortVideoItem.getId(), shortVideoItem.getOpus_path(), playListener);
        }
    }

    private void setShowType(int showType) {
        KLog.i("=======setShowType:" + showType);
        this.showType = showType;
        if (SHOW_TYPE_NORMAL == showType) {
            llDescBottom.setVisibility(showBottomTitle() ? VISIBLE : GONE);
            llDescTop.setVisibility(showBottomTitle() ? GONE : VISIBLE);
            detailOptionView.setVisibility(View.VISIBLE);
            ratioLayout.setRatio(showRatio, true);
            showFrameView(false);
        } else if (SHOW_TYPE_CONTROL_BAR == showType) {
            llDescBottom.setVisibility(GONE);
            llDescTop.setVisibility(GONE);
            detailOptionView.setVisibility(View.INVISIBLE);
            ratioLayout.setRatio(showRatio, true);
            showFrameView(false);
        } else if (SHOW_TYPE_FULL_SCREEN == showType) {
            llDescBottom.setVisibility(GONE);
            llDescTop.setVisibility(GONE);
            ratioLayout.setRatio(sScreenRatio, true);
            showFrameView(false);
        } else if (SHOW_TYPE_FRAME_VIEW == showType) {
            llDescBottom.setVisibility(GONE);
            llDescTop.setVisibility(GONE);
            detailOptionView.setVisibility(View.INVISIBLE);
//            if (!FrameUtils.ins().isSingleFrame(shortVideoItem.frame_layout)) {
            showFrameView(true);
//            } else {
//                showFrameView(false);
//            }
        }

        if (VideoDetailItemView1.SHOW_TYPE_NORMAL == showType) {
            showControlBar(false);
            useCurrentTemplateRl.setVisibility(GONE);
        } else {
            showControlBar(true);
            if(shortVideoItem.origin==5){
                useCurrentTemplateRl.setVisibility(VISIBLE);
            }else{
                useCurrentTemplateRl.setVisibility(GONE);
            }
        }
    }

    private boolean showBottomTitle() {
        if (shortVideoItem != null) {
            return ShortVideoItem.VIDEO_SHOW_TYPE_TOP_SCREEN.equals(shortVideoItem.detail_show_type);
        }
        return false;
    }

    public int getShowType() {
        return showType;
    }

    public float getShowRatio() {
        return showRatio;
    }

    public boolean isChangeAspectRatio() {
        return changeAspectRatio;
    }

    public void setLike(boolean showAnim) {
        if (detailOptionView != null) {
            detailOptionView.setLike(showAnim, shortVideoItem.is_like(), shortVideoItem.getLike_count());
        }
    }

    public boolean isRolling() {
        return rvComment != null && rvComment.isRolling();
    }

    public void runRollComment(boolean start) {
        KLog.i("======滚动评论：" + start);
        if (rvComment != null) {
            if (start) {
                if (rvComment.isRolling()) {
                    setRollCommentVisiable(false);
                }
                rvComment.setLayoutManager(new LinearLayoutManager(rvComment.getContext()));
                RollCommentAdapter rollCommentAdapter = new RollCommentAdapter(shortVideoItem != null
                        && ShortVideoItem.VIDEO_SHOW_TYPE_FULL_SCREEN.equals(shortVideoItem.detail_show_type));
//                rollCommentAdapter.addData(shortVideoItem.barrage_list);
                rvComment.setAdapter(rollCommentAdapter);
                rvComment.setDataList(shortVideoItem.barrage_list);
                setRollCommentVisiable(true);
                rvComment.startRoll();
            } else {
                rvComment.stopRoll();
            }
        }
    }

    public void setRollCommentVisiable(boolean visiable) {
        rvComment.setVisibility((VideoDetailItemView1.SHOW_TYPE_NORMAL == showType) && visiable ? VISIBLE : GONE);
        if (rvComment.getVisibility() == VISIBLE) {
            rvComment.startRoll();
        } else {
            rvComment.stopRoll();
        }
    }

    public void setComment() {
        if (detailOptionView != null) {
            detailOptionView.setComment(shortVideoItem.getComment_count());
        }
    }

    public void setGift() {
        if (detailOptionView != null) {
            detailOptionView.setGift(shortVideoItem.total_point);
        }
    }

    public void setTitle() {
        if (!TextUtils.isEmpty(shortVideoItem.getTitle())) {//设置用户的描述
            tvTitleT.setVisibility(View.VISIBLE);
            tvTitleT.setText(shortVideoItem.getTitle());
            tvTitleB.setVisibility(View.VISIBLE);
            tvTitleB.setText(shortVideoItem.getTitle());
        } else {
            tvTitleT.setVisibility(View.GONE);
            tvTitleB.setVisibility(View.GONE);
        }
    }

    public void showFrameView() {
        if (customFrameView != null) {
            customFrameView.setVisibility(customFrameView.getVisibility() == VISIBLE ? View.GONE : View.VISIBLE);
        }
    }

    public void showFrameView(boolean isShow) {
        if (customFrameView != null) {
            customFrameView.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }
    }

    public void setFollow() {
        if (detailOptionView != null) {
            detailOptionView.setFollow(shortVideoItem.isFollow(), AccountUtil.isLoginUser(shortVideoItem
                    .getOwner_id()));
        }
    }

    private int getDuration() {
        if (DcIjkPlayerManager.get().getPlayer() != null) {
            return DcIjkPlayerManager.get().getPlayer().getDuration();
        } else {
            return 0;
        }
    }

    private int setPosition(int duration, int position) {
        if (videoControlBar != null) {
            videoControlBar.setPosition(duration, position);
            return duration;
        } else {
            return -1;
        }
    }

    @Override
    public void onPlayClick() {
        onSingleClick(1, 1);
    }

    public boolean isFullScreen() {
        return videoControlBar.isFullScreen();
    }

    @Override
    public void onFullScreenClick(boolean isFull) {
        KLog.i("========onFullScreenClick：" + isFull);
//        if (isFull) {
//            iniViewLayout();
//            DcIjkPlayerManager.get().setRotation(0);
//            setShowType(VideoDetailItemView1.SHOW_TYPE_CONTROL_BAR);
//            showControlBar(true);
//            showTeamwork(true);
//            ivClose.setVisibility(View.VISIBLE);
//            videoControlBar.setFullScreen(false);
//        } else {
        if (isFull) {
            DcIjkPlayerManager.get().setRotation(0);
        } else {
            DcIjkPlayerManager.get().setRotation(90);
        }
        setShowType(VideoDetailItemView1.SHOW_TYPE_FULL_SCREEN);
        videoControlBar.setFullScreen(true);
        showControlBar(false);
        showTeamwork(false);
        ivClose.setVisibility(View.INVISIBLE);
        useCurrentTemplateRl.setVisibility(INVISIBLE);
        resumePlay();
        playStatus = true;
//        }
        if (mListener != null) {
            mListener.showFullScreen(videoControlBar.isFullScreen(), showType);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            return;
        } else {
            int duration = getDuration();
            int position = (int) ((duration * progress) / 100f);
            setPosition(duration, position);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isDragging = true;
        mHandler.removeMessages(ControlBarHandler.MESSAGE_UPDATE_PROGRESS);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeMessages(ControlBarHandler.MESSAGE_UPDATE_PROGRESS);
        int mSec = getDuration() * seekBar.getProgress() / 100;
        KLog.d("xxxx", "mSec " + mSec);
        if (DcIjkPlayerManager.get().getPlayer() != null) {
            DcIjkPlayerManager.get().getPlayer().seekTo(mSec);
        }
//        KLog.d("xxxx", "duration " + duration + " position " + position);
        isDragging = false;

        resumePlay();
        if (videoControlBar != null) {
            videoControlBar.setPlayStatus(false, shortVideoItem.detail_show_type);
        }
        playStatus = true;
        showControlBar(true);
        showTeamwork(true);
        ivClose.setVisibility(View.VISIBLE);
        setShowType(VideoDetailItemView1.SHOW_TYPE_NORMAL);
        setRollCommentVisiable(true);
    }

    private void showTeamwork(boolean isShow) {
        tvJoin.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    private void showControlBar(boolean show) {
        videoControlBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        if (show) {
            mHandler.sendEmptyMessage(ControlBarHandler.MESSAGE_UPDATE_PROGRESS);
        }
    }

    public boolean isPlayStatus() {
        return playStatus;
    }

    @Override
    public void onSingleClick(float rawX, float rawY) {
        KLog.i("========onSingleClick:" + rawX + " : " + rawY + " ,showType:" + showType);
        if (VideoDetailItemView1.SHOW_TYPE_NORMAL == showType) {
            pauseShadowView.setVisibility(VISIBLE);
            playStatus = false;
            DcIjkPlayerManager.get().pausePlay();
            if (shortVideoItem != null && shortVideoItem.is_delete == 1) {
                ToastUtil.showToast(R.string.stringWorkDeleted);
                return;
            }
            if (mHandler != null) {
                mHandler.removeMessages(ControlBarHandler.MESSAGE_UPDATE_PROGRESS);
            }
            if (videoControlBar != null) {
                videoControlBar.setPlayStatus(true, shortVideoItem.detail_show_type);
            }
            showControlBar(false);
            showTeamwork(true);
            ivClose.setVisibility(View.VISIBLE);
            setShowType(VideoDetailItemView1.SHOW_TYPE_FRAME_VIEW);
            setRollCommentVisiable(false);
        } else if (VideoDetailItemView1.SHOW_TYPE_CONTROL_BAR == showType) {
            playStatus = true;
            showControlBar(true);
            showTeamwork(true);
            ivClose.setVisibility(View.VISIBLE);
            setShowType(VideoDetailItemView1.SHOW_TYPE_NORMAL);
            setRollCommentVisiable(true);
        } else if (VideoDetailItemView1.SHOW_TYPE_FULL_SCREEN == showType) {
            playStatus = true;
            resumePlay();
            showNormal();
        } else if (VideoDetailItemView1.SHOW_TYPE_FRAME_VIEW == showType) {
            playStatus = true;
            resumePlay();
            if (videoControlBar != null) {
                videoControlBar.setPlayStatus(true, shortVideoItem.detail_show_type);
            }
            showControlBar(false);
            showTeamwork(true);
            ivClose.setVisibility(View.VISIBLE);
            setShowType(VideoDetailItemView1.SHOW_TYPE_NORMAL);
            setRollCommentVisiable(true);
        }
        if (mListener != null) {
            mListener.showFullScreen(videoControlBar.isFullScreen(), showType);
        }
    }

    public void showNormal() {
        if (videoControlBar != null) {
            videoControlBar.setPlayStatus(true, shortVideoItem.detail_show_type);
        }
        DcIjkPlayerManager.get().setRotation(0);
        videoControlBar.setFullScreen(false);
        showControlBar(false);
        showTeamwork(true);
        ivClose.setVisibility(View.VISIBLE);
        setShowType(VideoDetailItemView1.SHOW_TYPE_NORMAL);
        setRollCommentVisiable(true);
    }

    @Override
    public void onContinunousClick(float rawX, float rawY) {
        if (shortVideoItem != null && shortVideoItem.is_delete == 1) {
            ToastUtil.showToast(R.string.stringWorkDeleted);
            return;
        }

        if (mListener != null && !shortVideoItem.is_like() && AccountUtil.isLogin()) {
            shortVideoItem.setIs_like(true);//这里是为了防止连续请求，如果请求失败，需要置回
            mListener.onContinunousClick(videoPosition, shortVideoItem, rawX, rawY);
        }
//        if (AccountUtil.isLogin()) {
        //群魔乱舞的心
        int[] targetLocation = new int[2];
        detailOptionView.ivLike.getLocationOnScreen(targetLocation);
        CommonUtils.showFlyHeart(rlFullSize, rawX, rawY,
                targetLocation[0] + detailOptionView.ivLike.getWidth() * 0.5f,
                targetLocation[1] + detailOptionView.ivLike.getHeight() * 2f);
//        }
    }
}
