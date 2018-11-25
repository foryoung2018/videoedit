package com.wmlive.hhvideo.heihei.mainhome.widget;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.wmlive.hhvideo.heihei.beans.frame.FrameInfo;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.heihei.beans.opus.UploadMaterialEntity;
import com.wmlive.hhvideo.heihei.personal.activity.UserHomeActivity;
import com.wmlive.hhvideo.heihei.record.widget.CustomFrameView;
import com.wmlive.hhvideo.heihei.record.widget.SmallShowVideoView;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.FrameUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;
import com.wmlive.hhvideo.widget.AutoLineFeedView;
import com.wmlive.hhvideo.widget.BaseCustomView;
import com.wmlive.hhvideo.widget.CustomFontTextView;
import com.wmlive.hhvideo.widget.RatioLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.wmlive.hhvideo.R;

/**
 * Created by hsing on 2018/4/9.
 */

public class VideoDetailItemView extends BaseCustomView implements VideoDetailOptionView.OnVideoOptionClickListener {

    public static final int SHOW_TYPE_NORMAL = 0;
    public static final int SHOW_TYPE_CONTROL_BAR = 1;
    public static final int SHOW_TYPE_FULL_SCREEN = 2;
    public static final int SHOW_TYPE_FRAME_VIEW = 3;

    public ImageView ivCover;
    public FrameLayout flPlayerContainer;
    private ImageView ivLoading;
    private TextView tvTitle;
    private AutoLineFeedView autoLineFeedView;
    private VideoDetailOptionView detailOptionView;
    private RatioLayout ratioLayout;
    private CustomFrameView customFrameView;

    private ShortVideoItem shortVideoItem;
    private Animator loadingAnimator;
    private OnVideoDetailViewListener mListener;
    private int showType = SHOW_TYPE_NORMAL;
    private float showRatio;
    private List<UploadMaterialEntity> materials;
    private short isTeamwork;
    private boolean changeAspectRatio;

    public VideoDetailItemView(Context context) {
        super(context);
    }

    public VideoDetailItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoDetailItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.video_detail_item_view;
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {

    }

    public void initData(ShortVideoItem shortVideoItem, int position) {
        this.shortVideoItem = shortVideoItem;
        isTeamwork = shortVideoItem.is_teamwork;
        KLog.d("item类型","shortVideoItem.detail_show_type=="+shortVideoItem.detail_show_type);
        if (shortVideoItem != null) {
            if (ShortVideoItem.VIDEO_SHOW_TYPE_FULL_SCREEN.equals(shortVideoItem.detail_show_type)) {
                ViewStub viewStubFullSize = findViewById(R.id.viewStubFullSize);
                viewStubFullSize.inflate();
                showRatio = DeviceUtils.getScreenWHRatio();
            } else if (ShortVideoItem.VIDEO_SHOW_TYPE_TOP_SCREEN.equals(shortVideoItem.detail_show_type)) {
                ViewStub viewStubFullSize = findViewById(R.id.viewStub75PercentSize);
                viewStubFullSize.inflate();
                showRatio = 0.75f;
            } else if (ShortVideoItem.VIDEO_SHOW_TYPE_CENTER_SCREEN.equals(shortVideoItem.detail_show_type)) {
                ViewStub viewStubFullSize = findViewById(R.id.viewStubCustomSize);
                viewStubFullSize.inflate();
                showRatio = shortVideoItem.opus_height == 0 ? 0f : shortVideoItem.opus_width * 1.0f / shortVideoItem.opus_height;
                if (showRatio < 1) {
                    // 宽小于高，限高展示，以高为基准左右留间隙
                    showRatio = 1;
                    changeAspectRatio = true;
                }
            }
            findViews();
            initCustomFrameView();
            setListener();

            if (ShortVideoItem.VIDEO_SHOW_TYPE_CENTER_SCREEN.equals(shortVideoItem.detail_show_type)) {
                ratioLayout.setRatio(showRatio);
                if (showRatio == 1) {
                    ivCover.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                }
            }

            if (!TextUtils.isEmpty(shortVideoItem.getTitle())) {//设置用户的描述
                tvTitle.setVisibility(View.VISIBLE);
                tvTitle.setText(shortVideoItem.getTitle());
            } else {
                tvTitle.setVisibility(View.GONE);
            }

            initAutherView();

            detailOptionView.setOpusInfo(shortVideoItem);

            if (null != shortVideoItem.getShare_info()) {
                shortVideoItem.getShare_info().download_link = shortVideoItem.getDownload_link();
            }
            setVideoCover(shortVideoItem.getOpus_cover());
        } else {
            ToastUtil.showToast("数据错误");
        }
    }

    public void refreshShortVideoItem(ShortVideoItem shortVideoItem) {
        this.shortVideoItem = shortVideoItem;
        isTeamwork = shortVideoItem.is_teamwork;
        initCustomFrameView();
        if (!TextUtils.isEmpty(shortVideoItem.getTitle())) {//设置描述
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(shortVideoItem.getTitle());
        } else {
            tvTitle.setVisibility(View.GONE);
        }
    }

    private void initAutherView() {
        Map<Long, UserInfo> userMap = new ArrayMap<>(6);
        if (shortVideoItem.getUser() != null) {
            UserInfo author = shortVideoItem.getUser();
            if (!TextUtils.isEmpty(author.getName())) {
                userMap.put(author.getId(), author);
            }
        }
        if (materials != null) {
            for (UploadMaterialEntity materialEntity : shortVideoItem.materials) {
                if (materialEntity != null && materialEntity.user != null) {
                    userMap.put(materialEntity.user.getId(), materialEntity.user);
                }
            }
        }

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
    }

    private void findViews() {
        ivCover = findViewById(R.id.ivCover);
        flPlayerContainer = findViewById(R.id.flPlayerContainer);
        ivLoading = findViewById(R.id.ivLoading);
        tvTitle = findViewById(R.id.tvTitle);
        autoLineFeedView = findViewById(R.id.autoLineFeedView);
        detailOptionView = findViewById(R.id.detailOptionView);
        ratioLayout = findViewById(R.id.ratioLayout);
        customFrameView = findViewById(R.id.customFrameView);
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
        customFrameView.removeAllViews();
        if (shortVideoItem != null) {
            List<SmallShowVideoView> itemViewList = new ArrayList<>();
            materials = shortVideoItem.materials;
            FrameInfo mFrameInfo = FrameUtils.ins().getFrameInfo(shortVideoItem.frame_layout);
            if (materials != null && mFrameInfo != null) {
                SmallShowVideoView itemView;
                int layoutSize = mFrameInfo.getLayout().size();
                for (int i = 0; i < layoutSize; i++) {
                    itemView = new SmallShowVideoView(getContext());
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
                            if (isTeamwork == 1) {
                                if (mListener != null) {
                                    mListener.onJoinReplaceClick(index, shortVideoItem);
                                }
                            } else {
                                ToastUtil.showToast(R.string.stringOpusNotAllowJoinCreation);
                            }
                        }

                        @Override
                        public void onJoinClick(int index, SmallShowVideoView view) {
                            if (isTeamwork == 1) {
                                if (mListener != null) {
                                    mListener.onJoinSingleClick(index, shortVideoItem);
                                }
                            } else {
                                ToastUtil.showToast(R.string.stringOpusNotAllowJoinCreation);
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
        if (materials != null && materials.size() > 0) {
            for (int i = 0, size = materials.size(); i < size; i++) {
                UploadMaterialEntity materialEntity = materials.get(i);
                if (materialEntity != null && materialEntity.material_index == materialIndex) {
                    return materialEntity;
                }
            }
        }
        return null;
    }

    @Override
    public void onLikeClick() {
        if (mListener != null) {
            mListener.onLikeClick();
        }
    }

    @Override
    public void onCommentClick() {
        if (mListener != null) {
            mListener.onCommentClick();
        }
    }

    @Override
    public void onShareClick() {
        if (mListener != null) {
            mListener.onShareClick();
        }
    }

    @Override
    public void onGiftClick() {
        if (mListener != null) {
            mListener.onGiftClick();
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
            mListener.onFollowClick(shortVideoItem.getId(), shortVideoItem.getUser().getId(), shortVideoItem.getUser().isFollowed());
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

    public void setOnVideoDetailViewClickListener(OnVideoDetailViewListener listener) {
        this.mListener = listener;
    }

    public void setShowType(int showType) {
        this.showType = showType;
        if (SHOW_TYPE_NORMAL == showType) {
            tvTitle.setVisibility(View.VISIBLE);
            autoLineFeedView.setVisibility(View.VISIBLE);
            detailOptionView.setVisibility(View.VISIBLE);
            showFrameView(false);
        } else if (SHOW_TYPE_CONTROL_BAR == showType) {
            tvTitle.setVisibility(View.GONE);
            autoLineFeedView.setVisibility(View.GONE);
            detailOptionView.setVisibility(View.GONE);
            ratioLayout.setRatio(showRatio);
            showFrameView(false);
        } else if (SHOW_TYPE_FULL_SCREEN == showType) {
            ratioLayout.setRatio(DeviceUtils.getScreenWHRatio());
            showFrameView(false);
        } else if (SHOW_TYPE_FRAME_VIEW == showType) {
            tvTitle.setVisibility(View.GONE);
            autoLineFeedView.setVisibility(View.GONE);
            detailOptionView.setVisibility(View.GONE);
//            if (!FrameUtils.ins().isSingleFrame(shortVideoItem.frame_layout)) {
            showFrameView(true);
//            } else {
//                showFrameView(false);
//            }
        }
        if (mListener != null) {
            mListener.onVideoClick(showType);
        }
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

    public void setLike(boolean showAnim, boolean isLike, int likeCount) {
        if (detailOptionView != null) {
            detailOptionView.setLike(showAnim, isLike, likeCount);
        }
    }

    public void setComment(int commentCount) {
        if (detailOptionView != null) {
            detailOptionView.setComment(commentCount);
        }
    }

    public void setGift(int giftCount) {
        if (detailOptionView != null) {
            detailOptionView.setGift(giftCount);
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

    public void setFollow(boolean isFollowed, boolean isSelf) {
        if (shortVideoItem != null) {
            if (shortVideoItem.getUser() == null) {
                shortVideoItem.setUser(new UserInfo());
            }
            shortVideoItem.getUser().setFollowed(isFollowed);
        }
        if (detailOptionView != null) {
            detailOptionView.setFollow(isFollowed, isSelf);
        }
    }

    public interface OnVideoDetailViewListener {

        void onVideoClick(int showType);

        void onLikeClick();

        void onCommentClick();

        void onShareClick();

        void onGiftClick();

        void onUserClick(long userId);

        void onJoinReplaceClick(int index, ShortVideoItem shortVideoItem);

        void onJoinSingleClick(int index, ShortVideoItem shortVideoItem);

        void onFollowClick(long opusId, long userId, boolean followed);

        void onTopicClick(int topicId);
    }
}
