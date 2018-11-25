package com.wmlive.hhvideo.heihei.mainhome.widget;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.utils.CommonUtils;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;
import com.wmlive.hhvideo.widget.BaseCustomView;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by hsing on 2018/4/9.
 */

public class VideoDetailOptionView extends BaseCustomView {

    public final static int THEME_BLACK = 0;
    public final static int THEME_WHITE = 1;
    private int theme;

    @BindView(R.id.rlRootView)
    public RelativeLayout rlRootView;
    @BindView(R.id.ivUserAvatar)
    public ImageView ivUserAvatar;
    @BindView(R.id.ivVerifyIcon)
    public ImageView ivVerifyIcon;
    @BindView(R.id.tvFollow)
    public TextView tvFollow;
    @BindView(R.id.tvTopic)
    public TextView tvTopic;
    @BindView(R.id.llTopicInfo)
    public LinearLayout llTopicInfo;
    @BindView(R.id.ivLike)
    public ImageView ivLike;
    @BindView(R.id.tvLikeCount)
    public TextView tvLikeCount;
    @BindView(R.id.llLike)
    public View llLike;
    @BindView(R.id.ivComment)
    public ImageView ivComment;
    @BindView(R.id.tvCommentCount)
    public TextView tvCommentCount;
    @BindView(R.id.rlComment)
    public RelativeLayout rlComment;
    @BindView(R.id.ivGift)
    public ImageView ivGift;
    @BindView(R.id.tvGiftCount)
    public TextView tvGiftCount;
    @BindView(R.id.llGift)
    public LinearLayout llGift;
    @BindView(R.id.ivShare)
    public ImageView ivShare;
    @BindView(R.id.tvShare)
    public TextView tvShare;
    @BindView(R.id.llShare)
    public LinearLayout llShare;
    @BindView(R.id.llOptionContainer)
    public LinearLayout llOptionContainer;
    @BindView(R.id.rlUserInfo)
    public RelativeLayout rlUserInfo;
    private ShortVideoItem shortVideoItem;

    private OnVideoOptionClickListener optionClickListener;
    private Animator loveAnimator;

    public VideoDetailOptionView(Context context) {
        super(context);
    }

    public VideoDetailOptionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoDetailOptionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initAttribute(AttributeSet attrs) {
        super.initAttribute(attrs);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.VideoDetailOptionView, 0, 0);
        theme = typedArray.getInt(R.styleable.VideoDetailOptionView_vdov_theme, THEME_BLACK);
        typedArray.recycle();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.video_detail_option_view;
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {
//        changeTheme(theme);
        if (DeviceUtils.hasNavigationBar(DCApplication.getDCApp())) {
            // 适配手机有虚拟按键时显示
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtils.dip2px(DCApplication.getDCApp(), 40));
            layoutParams.bottomMargin = DeviceUtils.dip2px(DCApplication.getDCApp(), 5);
            layoutParams.topMargin = DeviceUtils.dip2px(DCApplication.getDCApp(), 5);
            layoutParams.leftMargin = DeviceUtils.dip2px(DCApplication.getDCApp(), 15);
            layoutParams.addRule(RelativeLayout.BELOW, rlUserInfo.getId());
            llOptionContainer.setLayoutParams(layoutParams);
        }
        llLike.setOnClickListener(this);
        rlComment.setOnClickListener(this);
        llShare.setOnClickListener(this);
        llGift.setOnClickListener(this);
        ivUserAvatar.setOnClickListener(this);
        tvFollow.setOnClickListener(this);
        tvTopic.setOnClickListener(this);
    }

    public void changeTheme(int theme, ShortVideoItem shortVideoItem) {
        this.theme = theme;
        this.shortVideoItem = shortVideoItem;
        if (THEME_WHITE == theme) {
            tvTopic.setTextColor(getResources().getColor(R.color.transparent80_white));
            tvGiftCount.setTextColor(getResources().getColor(R.color.white));
            tvShare.setTextColor(getResources().getColor(R.color.white));
            ivShare.setImageDrawable(getResources().getDrawable(R.drawable.icon_home_share_white));
            ivGift.setImageDrawable(getResources().getDrawable(R.drawable.icon_home_gift_white));

            tvLikeCount.setTextColor(getResources().getColor(R.color.white));
            ivLike.setImageDrawable(getResources().getDrawable(R.drawable.icon_home_like_white));

            tvCommentCount.setTextColor(getResources().getColor(R.color.white));
            ivComment.setImageDrawable(getResources().getDrawable(R.drawable.icon_home_comment_white));
        } else {
            tvTopic.setTextColor(getResources().getColor(R.color.black));
            tvGiftCount.setTextColor(getResources().getColor(R.color.black));
            tvShare.setTextColor(getResources().getColor(R.color.black));
            ivShare.setImageDrawable(getResources().getDrawable(R.drawable.icon_home_share));
            ivGift.setImageDrawable(getResources().getDrawable(R.drawable.icon_home_gift));

            ivLike.setImageDrawable(getResources().getDrawable(R.drawable.icon_home_like_nor));
            tvLikeCount.setTextColor(getResources().getColor(R.color.black));

            tvCommentCount.setTextColor(getResources().getColor(R.color.white));
            ivComment.setImageDrawable(getResources().getDrawable(R.drawable.icon_home_comment_white));
        }
        setFollow(shortVideoItem.isFollow(), AccountUtil.isLoginUser(shortVideoItem.getOwner_id()));
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        if (optionClickListener != null) {
            switch (v.getId()) {
                case R.id.llLike:
                    if (shortVideoItem != null && shortVideoItem.is_delete == 1) {
                        ToastUtil.showToast(R.string.stringWorkDeleted);
                        return;
                    }
                    optionClickListener.onLikeClick();
                    break;
                case R.id.rlComment:
                    if (shortVideoItem != null && shortVideoItem.is_delete == 1) {
                        ToastUtil.showToast(R.string.stringWorkDeleted);
                        return;
                    }
                    optionClickListener.onCommentClick();
                    break;
                case R.id.llShare:
                    if (shortVideoItem != null && shortVideoItem.is_delete == 1) {
                        ToastUtil.showToast(R.string.stringWorkDeleted);
                        return;
                    }
                    optionClickListener.onShareClick();
                    break;
                case R.id.llGift:
                    if (shortVideoItem != null && shortVideoItem.is_delete == 1) {
                        ToastUtil.showToast(R.string.stringWorkDeleted);
                        return;
                    }
                    optionClickListener.onGiftClick();
                    break;
                case R.id.ivUserAvatar:
                    optionClickListener.onUserClick();
                    break;
                case R.id.tvFollow:
                    optionClickListener.onFollowClick();
                    break;
                case R.id.tvTopic:
                    optionClickListener.onTopicClick();
                    break;
                default:
                    break;
            }
        }
    }

    public void setLike(boolean showAnim, boolean isLike, int likeCount) {
        tvLikeCount.setText(CommonUtils.getCountString(likeCount, false));
        ivLike.setImageResource(isLike ? R.drawable.icon_home_like_sel : (theme == THEME_WHITE ? R.drawable.icon_home_like_white : R.drawable.icon_home_like_nor));
        if (showAnim) {
            showLoveAnim(isLike);
        }
    }

    public void setComment(int commentCount) {
        tvCommentCount.setText(CommonUtils.getCountString(commentCount, false));
    }

    private void showLoveAnim(boolean like) {
        if (like) {
            ivLike.setImageResource(R.drawable.icon_home_like_sel);
            loveAnimator = AnimatorInflater.loadAnimator(getContext(), R.animator.love_anim);
            loveAnimator.setInterpolator(new LinearInterpolator());
            loveAnimator.setTarget(ivLike);
            loveAnimator.start();
        } else {
            ivLike.setImageResource((theme == THEME_WHITE ? R.drawable.icon_home_like_white : R.drawable.icon_home_like_nor));
        }
    }

    public void showShadow(boolean show) {
        rlRootView.setBackgroundResource(show ? R.drawable.bg_home_shade : R.color.transparent);
    }

    public void setGift(int giftCount) {
        tvGiftCount.setText(CommonUtils.getCountString(giftCount, false));
    }

    public void refreshFollow(ShortVideoItem shortVideoItem) {
        setFollow(shortVideoItem.getUser().isFollowed(), AccountUtil.getUserId() == shortVideoItem.getOwner_id());
    }

    public void setOptionClickListener(OnVideoOptionClickListener optionClickListener) {
        this.optionClickListener = optionClickListener;
    }

    public void setOpusInfo(ShortVideoItem shortVideoItem) {
        setComment(shortVideoItem.getComment_count());
        setGift(shortVideoItem.total_point);
        setLike(false, shortVideoItem.is_like(), shortVideoItem.getLike_count());
        refreshFollow(shortVideoItem);

        UserInfo author = (shortVideoItem.getUser() == null ? new UserInfo() : shortVideoItem.getUser());
        GlideLoader.loadCircleImage(author.getCover_url(), ivUserAvatar, author.isFemale() ? R.drawable.ic_default_female : R.drawable.ic_default_male);
        if (author.getVerify() != null && !TextUtils.isEmpty(author.getVerify().icon)) {
            ivVerifyIcon.setVisibility(View.VISIBLE);
            GlideLoader.loadImage(author.getVerify().icon, ivVerifyIcon);
        } else {
            ivVerifyIcon.setVisibility(View.GONE);
        }
        setTitle(shortVideoItem);
    }

    public void setTitle(ShortVideoItem shortVideoItem) {
        if (!TextUtils.isEmpty(shortVideoItem.getTopic_name())) {//设置话题的标签
            tvTopic.setText("#" + shortVideoItem.getTopic_name());
            tvTopic.setVisibility(View.VISIBLE);
            llTopicInfo.setVisibility(View.VISIBLE);
        } else {
            tvTopic.setVisibility(View.INVISIBLE);
            llTopicInfo.setVisibility(View.GONE);
        }
    }

    public void setFollow(boolean isFollowed, boolean isSelf) {
        tvFollow.setVisibility(isSelf ? View.GONE : View.VISIBLE);
        tvFollow.setText(isFollowed ? R.string.stringFollowed : R.string.user_focus_normal);
        int p = DeviceUtils.dip2px(DCApplication.getDCApp(), 15);
        tvFollow.setPadding(isFollowed ? 0 : p, 0, isFollowed ? 0 : p, 0);
        if (THEME_BLACK == theme) {
            tvFollow.setTextColor(getResources().getColor(R.color.hh_color_cc));
            tvFollow.setBackgroundDrawable(isFollowed ? null : getResources().getDrawable(R.drawable.bg_btn_c_follow_shape));
        } else if (THEME_WHITE == theme) {
            tvFollow.setTextColor(getResources().getColor(R.color.white));
            tvFollow.setBackgroundDrawable(isFollowed ? null : getResources().getDrawable(R.drawable.bg_btn_c_follow_shape_white));
        } else {
            tvFollow.setTextColor(getResources().getColor(R.color.hh_color_cc));
            tvFollow.setBackgroundDrawable(isFollowed ? null : getResources().getDrawable(R.drawable.bg_btn_c_follow_shape));
        }
    }

    public interface OnVideoOptionClickListener {
        void onLikeClick();

        void onCommentClick();

        void onShareClick();

        void onGiftClick();

        void onUserClick();

        void onFollowClick();

        void onTopicClick();
    }
}
