package com.wmlive.hhvideo.heihei.mainhome.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.MyClickListener;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;
import com.wmlive.hhvideo.widget.BaseCustomView;

import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 5/28/2018 - 6:14 PM
 * 类描述：
 */
public class RecommendUserPanel extends BaseCustomView {
    @BindView(R.id.llLine1)
    LinearLayout llLine1;
    @BindView(R.id.llLine2)
    LinearLayout llLine2;
    @BindView(R.id.tvFollowAll)
    TextView tvFollowAll;
    private static final String GAP_DOT = ",";
    @BindView(R.id.llRecommendPanel)
    LinearLayout llRecommendPanel;
    @BindView(R.id.llLoginPanel)
    LinearLayout llLoginPanel;
    @BindView(R.id.tvLogin)
    TextView tvLogin;

    private List<UserInfo> userList;

    private OnFollowListener onFollowListener;

    public RecommendUserPanel(Context context) {
        super(context);
    }

    public RecommendUserPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecommendUserPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {
        tvFollowAll.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.tvFollowAll:
                if (!CollectionUtil.isEmpty(userList)) {
                    String ids = getAllIds();
                    if (!TextUtils.isEmpty(ids) && onFollowListener != null) {
                        onFollowListener.onFollowAllClick(0, ids);
                    }
                }
                break;
            case R.id.tvLogin:
                if (onFollowListener != null) {
                    onFollowListener.onLoginClick();
                }
                break;
            default:
                break;
        }
    }

    @NonNull
    private String getAllIds() {
        StringBuilder sb = new StringBuilder(20);
        for (UserInfo userInfo : userList) {
            if (userInfo != null && !userInfo.isFollowed()) {
                sb.append(userInfo.getId()).append(GAP_DOT);
            }
        }
        String ids = sb.toString();
        if (!TextUtils.isEmpty(ids)) {
            if (ids.endsWith(GAP_DOT)) {
                sb.deleteCharAt(ids.lastIndexOf(GAP_DOT));
            }
        }
        ids = sb.toString();
        KLog.i("====批量关注的id是：" + ids);
        return ids;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_recommend_user_panel;
    }

    public void setData(final List<UserInfo> list) {
        userList = list;
        setVisibility(VISIBLE);
        llRecommendPanel.setVisibility(VISIBLE);
        llLoginPanel.setVisibility(GONE);
        llLine1.removeAllViews();
        llLine2.removeAllViews();
        boolean hasData = !CollectionUtil.isEmpty(list);
        llLine1.setVisibility(hasData ? VISIBLE : GONE);
        llLine2.setVisibility(hasData && list.size() > 3 ? VISIBLE : GONE);
        if (hasData) {
            View view;
            UserInfo userInfo;
            ImageView ivVerifyIcon;
            ImageView ivAvatar;
            for (int i = 0, size = list.size(); i < size; i++) {
                userInfo = list.get(i);
                if (i < 6) {
                    view = LayoutInflater.from(getContext()).inflate(R.layout.item_follow_user, i < 3 ? llLine1 : llLine2, false);
                    if (i < 3) {
                        llLine1.addView(view);
                    } else {
                        llLine2.addView(view);
                    }
                    ivAvatar = view.findViewById(R.id.ivAvatar);
                    GlideLoader.loadCircleImage(userInfo.getCover_url(), ivAvatar,
                            userInfo.isFemale() ? R.drawable.ic_default_female : R.drawable.ic_default_male);

                    ivVerifyIcon = (ImageView) view.findViewById(R.id.ivVerifyIcon);
                    if (userInfo.getVerify() != null && !TextUtils.isEmpty(userInfo.getVerify().icon)) {
                        ivVerifyIcon.setVisibility(View.VISIBLE);
                        GlideLoader.loadImage(userInfo.getVerify().icon, ivVerifyIcon);
                    } else {
                        ivVerifyIcon.setVisibility(View.GONE);
                    }
                    ((TextView) view.findViewById(R.id.tvNickname)).setText(userInfo.getName());
                    int finalI = i;
                    ivAvatar.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onFollowListener != null) {
                                onFollowListener.onUserClick(userList.get(finalI).getId());
                            }
                        }
                    });
                    refreshFollow(view, userInfo, i);
                } else {
                    break;
                }
            }
        }
    }

    public void refreshItem(boolean isFollow, long userId) {
        if (!CollectionUtil.isEmpty(userList)) {
            UserInfo userInfo;
            for (int i = 0; i < userList.size(); i++) {
                userInfo = userList.get(i);
                if (userInfo != null && userInfo.getId() == userId) {
                    refreshItem(i, isFollow);
                    break;
                }
            }
        }
    }

    public void refreshItem(int position, boolean isFollow) {
        if (position > -1 && position < userList.size()) {
            View childAt;
            if (position >= 3) {
                childAt = llLine2.getChildAt(position - 3);
            } else {
                childAt = llLine1.getChildAt(position);
            }
            UserInfo userInfo = userList.get(position);
            userInfo.setFollowed(isFollow);
            refreshFollow(childAt, userInfo, position);
        }
    }

    private void refreshFollow(View view, UserInfo userInfo, int index) {
        TextView tvFollow = (TextView) view.findViewById(R.id.tvFollow);
        tvFollow.setTag(index);
        tvFollow.setText(tvFollow.getResources().getString(userInfo.isFollowed() ? R.string.stringFollowed : R.string.stringFollow));
        tvFollow.setOnClickListener(new MyClickListener() {
            @Override
            protected void onMyClick(View v) {
                if (v.getTag() != null && v.getTag() instanceof Integer) {
                    int index = (int) v.getTag();
                    if (onFollowListener != null) {
                        onFollowListener.onFollowClick(true, index, 0, userList.get(index).getId(), userList.get(index).isFollowed());
                    }
                }
            }
        });
    }

    public void showRelogin() {
        setVisibility(VISIBLE);
        llRecommendPanel.setVisibility(GONE);
        llLoginPanel.setVisibility(VISIBLE);
    }

    public void setOnFollowListener(OnFollowListener onFollowListener) {
        this.onFollowListener = onFollowListener;
    }

    public interface OnFollowListener {
        void onFollowClick(boolean isRecommend, int position, long videoId, long userId, boolean isFollow);

        void onFollowAllClick(int position, String ids);

        void onUserClick(long userId);

        void onLoginClick();
    }
}
