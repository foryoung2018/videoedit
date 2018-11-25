package com.wmlive.hhvideo.heihei.personal.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.beans.personal.UserHomeRelation;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.utils.CommonUtils;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;
import com.wmlive.hhvideo.widget.BaseCustomView;
import com.wmlive.hhvideo.widget.CustomFontTextView;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 10/12/2017.
 */

public class UserInfoHeader extends BaseCustomView implements ProductTypePanel.OnTypeClickListener {
    @BindView(R.id.ivAvatar)
    ImageView ivAvatar;
    @BindView(R.id.tvName)
    CustomFontTextView tvName;
    @BindView(R.id.tvLocation)
    TextView tvLocation;
    @BindView(R.id.tvId)
    TextView tvId;
    @BindView(R.id.tvSign)
    TextView tvSign;
    @BindView(R.id.tvFollow)
    TextView tvFollow;
    @BindView(R.id.tvFollowCount)
    CustomFontTextView tvFollowCount;
    @BindView(R.id.tvFansCount)
    CustomFontTextView tvFansCount;
    @BindView(R.id.tvLikeCount)
    CustomFontTextView tvLikeCount;
    @BindView(R.id.llCountPanel)
    LinearLayout llCountPanel;
    @BindView(R.id.llProductType)
    ProductTypePanel llProductType;
    @BindView(R.id.llFollowPanel)
    LinearLayout llFollowPanel;
    @BindView(R.id.llFansPanel)
    LinearLayout llFansPanel;
    @BindView(R.id.llLikePanel)
    LinearLayout llLikePanel;
    @BindView(R.id.llEdit)
    LinearLayout llEdit;
    @BindView(R.id.ivAccount)
    ImageView ivAccount;
    @BindView(R.id.tvAccount)
    TextView tvAccount;
    @BindView(R.id.llAccount)
    LinearLayout llAccount;
    @BindView(R.id.tvVerifyType)
    CustomFontTextView tvVerifyType;
    @BindView(R.id.llVerify)
    LinearLayout llVerify;
    @BindView(R.id.ivWeiboIcon)
    ImageView ivWeiboIcon;
    @BindView(R.id.ivVerifyIcon)
    ImageView ivVerifyIcon;
    @BindView(R.id.tvWeiboHome)
    TextView tvWeiboHome;
    @BindView(R.id.tvDecibelCount)
    CustomFontTextView tvDecibelCount;
    @BindView(R.id.tvLevel)
    CustomFontTextView tvLevel;
    @BindView(R.id.llDecibelCount)
    LinearLayout llDecibelCount;
    @BindView(R.id.llWeiboHome)
    LinearLayout llWeiboHome;
    private OnUserInfoClickListener infoClickListener;
    private UserInfo userInfo;
    @BindView(R.id.user_info_top_ll)
    public LinearLayout userInfoTopll;

    public UserInfoHeader(Context context) {
        super(context);
    }

    public UserInfoHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {
        ivAvatar.setOnClickListener(this);
        tvFollow.setOnClickListener(this);
        llFollowPanel.setOnClickListener(this);
        llFansPanel.setOnClickListener(this);
        llLikePanel.setOnClickListener(this);
        llWeiboHome.setOnClickListener(this);
        llAccount.setOnClickListener(this);
        llEdit.setOnClickListener(this);
        llDecibelCount.setOnClickListener(this);
        llProductType.setOnTypeClickListener(this);
        setViewLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void initData(UserInfo userInfo) {
        this.userInfo = userInfo;
        if (userInfo != null) {
            llEdit.setVisibility(AccountUtil.isLoginUser(userInfo.getId()) ? VISIBLE : GONE);
            llAccount.setVisibility(AccountUtil.isLoginUser(userInfo.getId()) ? VISIBLE : GONE);
//            llMessage.setVisibility(!AccountUtil.isLoginUser(userInfo.getId()) ? VISIBLE : GONE);
            ivAvatar.setClickable(true);
            tvFollow.setClickable(true);
            GlideLoader.loadCircleImage(userInfo.getCover_url(), ivAvatar, userInfo.isFemale() ? R.drawable.ic_default_female : R.drawable.ic_default_male);
            tvName.setText(userInfo.getName());
            tvLevel.setText("LV." + userInfo.getLevel());
            tvLocation.setText(
                    "   ·   "
                            + (!TextUtils.isEmpty(userInfo.getRegion()) ? userInfo.getRegion() : tvLocation.getContext().getString(R.string.user_location_fail))
                            + "   ·   "
                            + (!TextUtils.isEmpty(userInfo.getConstellation()) ? userInfo.getConstellation() : tvLocation.getContext().getString(R.string.user_default_constellation)));

            tvId.setText(tvId.getContext().getString(R.string.user_id, userInfo.getDc_num()));
            if (!TextUtils.isEmpty(userInfo.getDescription())) {
                tvSign.setText(userInfo.getDescription());
            } else {
                tvSign.setText(R.string.stringDefaultSign);
            }
            if (userInfo.getData() != null) {
                llProductType.initData(userInfo.getData().getOpus_count(),
                        userInfo.getData().getCo_create_count(),
                        userInfo.getData().getLike_opus_count());
                tvLikeCount.setText(CommonUtils.getCountString(userInfo.getData().getLike()));
                tvDecibelCount.setText(CommonUtils.getCountString(userInfo.getData().getAll_earn_point()));
            } else {
                llProductType.initData(0, 0, 0);
                tvLikeCount.setText("0");
                tvDecibelCount.setText("0");
            }
            if (userInfo.getRelation() != null) {
                tvFollowCount.setText(CommonUtils.getCountString(userInfo.getRelation().follow_count));
                tvFansCount.setText(CommonUtils.getCountString(userInfo.getRelation().fans_count));
                setFollowed(userInfo.getRelation().is_follow);
            } else {
                setFollowed(false);
                tvFollowCount.setText("0");
                tvFansCount.setText("0");
            }
            if (userInfo.getBind_weibo() != null && !TextUtils.isEmpty(userInfo.getBind_weibo().weibo_id)) {
                llWeiboHome.setVisibility(VISIBLE);
                if(!TextUtils.isEmpty(userInfo.getOfficial_website().link)&&!TextUtils.isEmpty(userInfo.getOfficial_website().name)){
                    ivWeiboIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon_official));
                    tvWeiboHome.setText(userInfo.getOfficial_website().name);
                }
            } else {
                llWeiboHome.setVisibility(GONE);
            }

            if (userInfo.getVerify() == null || TextUtils.isEmpty(userInfo.getVerify().type) || userInfo.getVerify().type.equalsIgnoreCase("normal")) {
                llVerify.setVisibility(GONE);
            } else {
                GlideLoader.loadImage(userInfo.getVerify().icon, ivVerifyIcon);
                llVerify.setVisibility(VISIBLE);
                tvVerifyType.setText(userInfo.getVerify().verify_reason);
            }

        } else {
            setFollowed(false);
            llEdit.setVisibility(GONE);
            llAccount.setVisibility(GONE);
//            llMessage.setVisibility(GONE);
            ivAvatar.setClickable(false);
            tvFollow.setClickable(false);
            ivAvatar.setImageResource(R.drawable.ic_default_male);
            tvName.setText("");
            tvLocation.setText("");
            tvLevel.setText("");
            tvId.setText("");
            tvVerifyType.setText("");
            tvDecibelCount.setText("0");
            tvFollowCount.setText("0");
            tvFansCount.setText("0");
            tvLikeCount.setText("0");
            tvSign.setVisibility(GONE);
            llProductType.initData(0, 0, 0);
            llWeiboHome.setVisibility(GONE);
            llVerify.setVisibility(GONE);
        }
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        if (userInfo != null && infoClickListener != null) {
            long userId = userInfo.getId();
            boolean isSelf = userId == AccountUtil.getUserId();
            switch (v.getId()) {
                case R.id.llEdit:
                    if (isSelf) {
                        infoClickListener.onNameClick(userId);
                    }
                    break;
                case R.id.ivAvatar:
                    infoClickListener.onAvatarClick(userId, AccountUtil.isLoginUser(userInfo.getId()));
                    break;
                case R.id.tvFollow:
                    infoClickListener.onFollowClick(userId, userInfo.getRelation() != null && userInfo.getRelation().is_follow);
                    break;
                case R.id.llFollowPanel:
                    infoClickListener.onFollowCountClick(userId);
                    break;
                case R.id.llFansPanel:
                    infoClickListener.onFansCountClick(userId);
                    break;
                case R.id.llLikePanel:
                    infoClickListener.onLikeCountClick(userId);
                    break;
                case R.id.llDecibelCount:
                    infoClickListener.onDecibelCountClick(userId);
                    break;
                case R.id.llWeiboHome:
                    if(userInfo.getOfficial_website()!=null && !TextUtils.isEmpty(userInfo.getOfficial_website().link)){
                        infoClickListener.onOfficalWebSite(userInfo.getOfficial_website().link);
                    }else if (userInfo.getBind_weibo() != null && !TextUtils.isEmpty(userInfo.getBind_weibo().weibo_id)) {
                        infoClickListener.onWeiboHomeClick(userInfo.getBind_weibo().weibo_id);
                    }
                    break;
                case R.id.llAccount:
                    infoClickListener.onAccountClick(userId);
                    break;
//                case R.id.llMessage:
//                    infoClickListener.onMessageClick(userId, userInfo);
//                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_user_info_header;
    }

    public void setInfoClickListener(OnUserInfoClickListener infoClickListener) {
        this.infoClickListener = infoClickListener;
    }

    public void showFollow(boolean show) {
        tvFollow.setVisibility(show ? VISIBLE : GONE);
    }

    public void setFollowed(boolean followed) {
        if (userInfo.getRelation() != null) {
            userInfo.getRelation().is_follow = followed;
        } else {
            UserHomeRelation userHomeRelation = new UserHomeRelation();
            userHomeRelation.is_fans = followed;
            userInfo.setRelation(userHomeRelation);
        }
        tvFollow.setText(followed ? R.string.user_follower : R.string.user_focus_normal);
        tvFollow.setBackgroundDrawable(followed ? null : getResources().getDrawable(R.drawable.bg_btn_c_follow_shape));
    }

    public void selectItem(int index) {
        selectItem(index, false);
    }

    public void selectItem(int index, boolean click) {
        if (llProductType != null) {
            llProductType.selectItem(index, click);
        }
    }

    @Override
    public void onTypeClick(int index) {
        if (infoClickListener != null) {
            infoClickListener.onTypeClick(index);
        }
    }

    public View getProductTypeView() {
        return llProductType;
    }

    public interface OnUserInfoClickListener {
        void onAvatarClick(long userId, boolean editable);

        void onNameClick(long userId);

        void onTypeClick(int index);

        void onFollowClick(long userId, boolean isFollow);

        void onFollowCountClick(long userId);

        void onFansCountClick(long userId);

        void onLikeCountClick(long userId);

        void onDecibelCountClick(long userId);

        void onWeiboHomeClick(String weiboId);

        void onOfficalWebSite(String link);

        void onAccountClick(long userId);

        void onMessageClick(long userId, UserInfo userInfo);
    }
}
