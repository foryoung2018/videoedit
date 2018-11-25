package com.wmlive.hhvideo.widget;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.common.VideoProxy;
import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.heihei.beans.opus.UploadMaterialEntity;
import com.wmlive.hhvideo.heihei.beans.personal.UserHomeRelation;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.mainhome.adapter.RecommendAdapter;
import com.wmlive.hhvideo.heihei.mainhome.presenter.ShortVideoViewCallback;
import com.wmlive.hhvideo.utils.CommonUtils;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;

import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 11/27/2017.11:59 AM
 * 视频播放的item
 *
 * @author lsq
 * @describe 添加描述
 */

public class BaseVideoPlayItemView extends BaseCustomView {
    @BindView(R.id.flPlayerContainer)
    public FrameLayout flPlayerContainer;
    @BindView(R.id.ivCover)
    public ImageView ivCover;
    //    @BindView(R.id.dvDanmaku)
//    public IDanmakuView dvDanmaku;
    @BindView(R.id.iv_player_status)
    public ImageView ivPlayerStatus;
    @BindView(R.id.iv_loading)
    public ImageView ivLoading;
    @BindView(R.id.tvPreloadStatus)
    public TextView tvPreloadStatus;
    @BindView(R.id.ivUserAvatar)
    public ImageView ivUserAvatar;
    @BindView(R.id.tvUser)
    public CustomFontTextView tvUser;
    @BindView(R.id.llUser)
    public LinearLayout llUser;
    @BindView(R.id.tvFollow)
    public TextView tvFollow;
    @BindView(R.id.rlPlayerContainer)
    public RatioLayout rlPlayerContainer;
    @BindView(R.id.llJoin)
    public LinearLayout llJoin;
    @BindView(R.id.llTopicInfo)
    public LinearLayout llTopicInfo;
    @BindView(R.id.tvTopic)
    public CustomFontTextView tvTopic;
    @BindView(R.id.ivTopicLabel)
    public ImageView ivTopicLabel;
    @BindView(R.id.tvMusicDesc)
    public CustomFontTextView tvMusicDesc;
    @BindView(R.id.ivLike)
    public ImageView ivLike;
    @BindView(R.id.ivComment)
    public ImageView ivComment;
    @BindView(R.id.ivArrow)
    public ImageView ivArrow;
    @BindView(R.id.viewTopLine)
    public View viewTopLine;
    @BindView(R.id.viewBottomLine)
    public View viewBottomLine;
    @BindView(R.id.tvCommentCount)
    public CustomFontTextView tvCommentCount;
    @BindView(R.id.tvLikeCount)
    public CustomFontTextView tvLikeCount;
    @BindView(R.id.viewVideoClickHolder)
    public GestureView viewVideoClickHolder;
    @BindView(R.id.rlUserInfo)
    public RelativeLayout rlUserInfo;
    @BindView(R.id.ivVerifyIcon)
    public ImageView ivVerifyIcon;
    @BindView(R.id.ivShadow)
    public ImageView ivShadow;
    private boolean showCountInfo = true;


    protected Animator loadingAnimator;
    public int position;
    protected ShortVideoItem shortVideoItem;
    protected ShortVideoViewCallback shortVideoViewCallback;
    protected UserInfo author;

    protected Animator loveAnimator;
    protected int videoType;
    protected int pageId;
    protected Animation rightPopAnimation;
    protected Animation zoomAnimation;
    private List<UploadMaterialEntity> materials;

    public BaseVideoPlayItemView(Context context) {
        super(context);
    }

    public BaseVideoPlayItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseVideoPlayItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.video_play_item_view;
    }

    public void initData(int pageId, int videoType, int position, ShortVideoItem item,
                         ShortVideoViewCallback clickListener, final RecommendAdapter recommendNewAdapter) {
        KLog.i("======ViewHolder initData:" + position);
        KLog.d("视频信息===" + shortVideoItem);
        this.pageId = pageId;
        this.videoType = videoType;
        this.position = position;
        this.shortVideoViewCallback = clickListener;
        shortVideoItem = (item == null ? new ShortVideoItem() : item);
        setVideoCover(shortVideoItem.getOpus_cover());   //设置视频的封面
        if (shortVideoItem.itemType == 0) {
            rlPlayerContainer.setRatio(shortVideoItem.feed_width_height_rate);
            rlUserInfo.setVisibility(VISIBLE);
            llTopicInfo.setVisibility(VISIBLE);
            refreshFollow();
            rightPopAnimation = AnimationUtils.loadAnimation(DCApplication.getDCApp(), R.anim.anim_right_pop_out);
            zoomAnimation = AnimationUtils.loadAnimation(DCApplication.getDCApp(), R.anim.anim_zoom_out);
            initVideoLoading();
            ivPlayerStatus.setVisibility(View.GONE);
            ivLoading.setVisibility(View.GONE);
            tvPreloadStatus.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(shortVideoItem.getTopic_name())) {//设置话题的标签
                tvTopic.setText("#" + shortVideoItem.getTopic_name());
                tvTopic.setVisibility(View.VISIBLE);
//                ivTopicLabel.setVisibility(View.VISIBLE);
            } else {
                tvTopic.setVisibility(View.INVISIBLE);
//                ivTopicLabel.setVisibility(View.GONE);
            }

            author = (shortVideoItem.getUser() == null ? new UserInfo() : shortVideoItem.getUser());
            if (!TextUtils.isEmpty(author.getName())) {//设置用户Name
                llUser.setVisibility(View.VISIBLE);
                tvUser.setVisibility(View.VISIBLE);
                tvUser.setText(author.getName());
            } else {
                llUser.setVisibility(View.GONE);
                tvUser.setVisibility(View.GONE);
            }

            setTitle();

            GlideLoader.loadCircleImage(author.getCover_url(), ivUserAvatar, author.isFemale() ? R.drawable.ic_default_female : R.drawable.ic_default_male);
            if (author.getVerify() != null && !TextUtils.isEmpty(author.getVerify().icon)) {
                ivVerifyIcon.setVisibility(View.VISIBLE);
                GlideLoader.loadImage(author.getVerify().icon, ivVerifyIcon);
            } else {
                ivVerifyIcon.setVisibility(View.GONE);
            }
//        if (dvDanmaku == null) {
//            dvDanmaku = (IDanmakuView) getRootView().findViewById(R.id.dvDanmaku);
//        }
//        if (shortVideoItem.getIs_teamwork() == 1) {
//            llJoin.setVisibility(View.VISIBLE);
//            ivArrow.setVisibility(View.VISIBLE);
////            int iconSize = DeviceUtils.dip2px(llJoin.getContext(), 14);
////            DiscoveryUtil.setDrawable(llJoin, R.drawable.icon_profile_create, 0, iconSize, iconSize);
//        } else {
//            ivArrow.setVisibility(View.GONE);
//            llJoin.setVisibility(View.GONE);
//        }

//        dvDanmaku.setVisibility(View.VISIBLE);

            if (shortVideoItem.getIs_delete() == 0) {//视频是否已被删除
                if (VideoProxy.get().getProxy() != null
                        /*&& VideoProxy.get().getProxy().isCaching(shortVideoItem.getOpus_path())*/) {//本地已缓存
                    dismissVideoLoading();
                } else {
                    showVideoLoading();
                }
            } else {
                dismissVideoLoading();
            }
            setCommentCount();
            setGiftCount();
            setLikeCount(false);
            viewTopLine.setVisibility(position == 0 ? View.GONE : VISIBLE);
//        if (videoType == RecommendFragment.TYPE_SINGLE_WORK) {
//            refreshFollow();
//            getRootView().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    recommendNewAdapter.forcePlay(false);
//                }
//            }, 300);
//        }
        } else if (shortVideoItem.itemType == 1) {
            //这里是广告图片
            rlUserInfo.setVisibility(GONE);
            llTopicInfo.setVisibility(GONE);
            tvMusicDesc.setVisibility(GONE);
            dismissVideoLoading();
            if (shortVideoItem.banner != null) {
                float ratio = shortVideoItem.banner.banner_height == 0 ?
                        1 : shortVideoItem.banner.banner_width * 1f / shortVideoItem.banner.banner_height;
                rlPlayerContainer.setRatio(ratio);
                setVideoCover(shortVideoItem.banner.cover);
            }
        } else {
            dismissVideoLoading();
            rlUserInfo.setVisibility(GONE);
            llTopicInfo.setVisibility(GONE);
            tvMusicDesc.setVisibility(GONE);
        }

        ViewGroup.LayoutParams layoutParams = ivShadow.getLayoutParams();
        layoutParams.height = DeviceUtils.dip2px(getContext(), shortVideoItem.itemType == 1 ? 20 : 40);
        ivShadow.setLayoutParams(layoutParams);
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


    public void viewVisible() {
        // 该控件需每次页面可见时重新赋值，每次页面不可见时销毁
//        initMusicBgAnimator();
//        initCdAnimator();
//        if (shortVideoItem != null && !TextUtils.isEmpty(shortVideoItem.getMusic_name())) {
//            llMusicName.setVisibility(View.VISIBLE);
//            viewMusicName.setTextContent(shortVideoItem.getMusic_name());
//        } else {
//            llMusicName.setVisibility(View.GONE);
//        }
    }

    //设置视频封面是否可见
    public void setCoverVisible(boolean show) {
        ivCover.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setVideoOptionVisible(boolean show) {
//        llOption.setVisibility(show ? VISIBLE : GONE);
    }

    public void setBottomLineVisible(boolean show) {
        viewBottomLine.setVisibility(show ? VISIBLE : GONE);
    }

    //设置礼物数
    public void setGiftCount() {
//        if (showCountInfo) {
//            String giftCount = shortVideoItem.total_point < 1 ? "" : (shortVideoItem.total_point >= 10000
//                    ? String.format(Locale.getDefault(), "%.1f", shortVideoItem.total_point / 10000.0f) + "w"
//                    : String.valueOf(shortVideoItem.total_point));
//            llOption.setGift(giftCount);
//        } else {
//            llOption.setGift("送礼");
//        }
    }

    //设置评论数
    public void setCommentCount() {
        tvCommentCount.setText(CommonUtils.getCountString(shortVideoItem.getComment_count(), false));
    }

    //设置点赞状态
    public void setLikeCount(boolean showAnim) {
        tvLikeCount.setText(CommonUtils.getCountString(shortVideoItem.getLike_count(), false));
        showLoveAnim(shortVideoItem.is_like(), showAnim);
    }

    private void showLoveAnim(boolean like, boolean showAnim) {
        if (like && showAnim) {
            ivLike.setImageResource(R.drawable.icon_home_like_sel);
            loveAnimator = AnimatorInflater.loadAnimator(getContext(), R.animator.love_anim);
            loveAnimator.setInterpolator(new LinearInterpolator());
            loveAnimator.setTarget(ivLike);
            loveAnimator.start();
        } else {
            ivLike.setImageResource(like ? R.drawable.icon_home_like_sel : R.drawable.icon_home_like);
        }
    }

    //设置封面
    public void setVideoCover(String url) {
        ivCover.setVisibility(View.VISIBLE);
        GlideLoader.loadImage(url, ivCover, R.drawable.bg_home_video_default);
    }


    //刷新钻数量
    public void refreshGold() {
    }


    public void refreshFollow() {
        if (shortVideoItem.getUser() == null) {
            shortVideoItem.setUser(new UserInfo());
        }
        if (shortVideoItem.getUser().getRelation() == null) {
            shortVideoItem.getUser().setRelation(new UserHomeRelation());
        }
        boolean isFollowed = shortVideoItem.getUser().getRelation().is_follow;
        tvFollow.setVisibility((AccountUtil.getUserId() == shortVideoItem.getOwner_id() ? View.GONE : View.VISIBLE));
        tvFollow.setText(isFollowed ? R.string.stringFollowed : R.string.stringFollow);
        tvFollow.setBackgroundDrawable(isFollowed ? null : getResources().getDrawable(R.drawable.bg_btn_c_follow_shape));
    }

    public void showVideoLoading() {
        ivLoading.setVisibility(View.VISIBLE);
        initVideoLoading();
        loadingAnimator.start();
    }

    private void initVideoLoading() {
        loadingAnimator = AnimatorInflater.loadAnimator(getContext(), R.animator.loading);
        loadingAnimator.setInterpolator(new LinearInterpolator());
        loadingAnimator.setTarget(ivLoading);
    }

    public void dismissVideoLoading() {
        if (loadingAnimator != null) {
            loadingAnimator.cancel();
            loadingAnimator = null;
        }
        ivLoading.clearAnimation();
        ivLoading.setVisibility(View.GONE);
    }


    public void setTitle() {
        if (!TextUtils.isEmpty(shortVideoItem.getTitle())) {//设置用户的描述
            tvMusicDesc.setVisibility(View.VISIBLE);
            tvMusicDesc.setText(shortVideoItem.getTitle());
        } else {
            tvMusicDesc.setVisibility(View.GONE);
        }
    }

    public void setPlayIcon(boolean isPlaying) {
        setPlayIcon(isPlaying, false);
    }

    //是否显示播放按钮
    public void setPlayIcon(boolean isPlaying, boolean isError) {
//        KLog.i("=========setPlayIcon:" + isPlaying);
//        llJoin.setVisibility(isPlaying ? VISIBLE : INVISIBLE);
//        ivPlayerStatus.setVisibility(isPlaying ? View.GONE : View.VISIBLE);
    }

    public void showCountInfo(boolean show) {
        showCountInfo = show;
    }


    /**
     * 连续点击，播放屏幕点赞动画
     *
     * @param downX
     * @param downY
     */
    public void showLikeAnim(float downX, float downY, float targetX, float targetY) {
        if (AccountUtil.isLogin()) {
            //获取ivLike控件的屏幕坐标
            shortVideoViewCallback.onLikeClick(position, shortVideoItem.getId(), shortVideoItem.is_like(),
                    true, !shortVideoItem.is_like(),
                    downX, downY,
                    targetX + ivLike.getWidth() / 2, targetY + ivLike.getHeight() / 2);
            if (!shortVideoItem.is_like()) {
                shortVideoItem.setIs_like(true);//这里为了防止多次请求，请求失败需要把这个值重置
            }
        }
    }

    public void refreshPreload(long count) {
        tvPreloadStatus.setVisibility(View.VISIBLE);
        tvPreloadStatus.setText("下一个已加载：" + (count / 1024) + "Kb");
    }

    public ShortVideoItem getVideoItemBean() {
        return shortVideoItem;
    }
}
