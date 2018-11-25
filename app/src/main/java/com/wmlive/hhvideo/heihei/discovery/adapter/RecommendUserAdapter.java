package com.wmlive.hhvideo.heihei.discovery.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.discovery.viewholder.RecommendUserViewHolder;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.utils.MyClickListener;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshAdapter;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;

import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 9/20/2017.
 */

public class RecommendUserAdapter extends RefreshAdapter<RecommendUserViewHolder, UserInfo> {
    private OnFollowClickListener followClickListener;

    public RecommendUserAdapter(List<UserInfo> list, RefreshRecyclerView refreshView) {
        super(list, refreshView);
    }

    @Override
    public RecommendUserViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new RecommendUserViewHolder(parent, R.layout.item_recommend_user);
    }

    @Override
    public void onBindHolder(RecommendUserViewHolder holder, final int position, final UserInfo data) {
        if (data == null) {
            return;
        }
        GlideLoader.loadCircleImage(data.getCover_url(), holder.ivAvatar, data.isFemale() ? R.drawable.ic_default_female : R.drawable.ic_default_male);
        holder.tvNickname.setText(data.getName());
        holder.tvDcId.setText(holder.tvDcId.getResources().getString(R.string.stringHeiheiId, data.getDc_num(), data.getFansCount()));
        holder.ivFollow.setImageResource(data.isFollowed() ? R.drawable.icon_friend_check_nor : R.drawable.icon_profile_other_add);


        if (AccountUtil.getUserId() == data.getId()) {
//            holder.ivFansItemState.setVisibility(View.GONE);
            holder.tvFollow.setVisibility(View.GONE);
        } else {
            holder.tvFollow.setVisibility(View.VISIBLE);
            if (null != data.getRelation()) {
                if (data.getRelation().is_follow && data.getRelation().is_fans) {
//                    GlideLoader.loadImage(R.drawable.icon_friend_followeach, holder.ivFocusItemState, R.drawable.icon_profile_other_add);
                    holder.tvFollow.setText(holder.tvFollow.getContext().getString(R.string.stringFollowedBoth));
                    holder.tvFollow.setBackgroundDrawable(null);
                } else if (data.getRelation().is_follow) {
//                    GlideLoader.loadImage(R.drawable.icon_friend_check_nor, holder.ivFocusItemState, R.drawable.icon_profile_other_add);
                    holder.tvFollow.setText(holder.tvFollow.getContext().getString(R.string.stringFollowed));
                    holder.tvFollow.setBackgroundDrawable(null);
                } else {
//                    GlideLoader.loadImage(R.drawable.icon_profile_other_add, holder.ivFocusItemState, R.drawable.icon_profile_other_add);
                    holder.tvFollow.setText(holder.tvFollow.getContext().getString(R.string.stringFollow));
                    holder.tvFollow.setBackgroundDrawable(holder.tvFollow.getResources().getDrawable(R.drawable.bg_btn_c_follow_shape));
                }
            } else {
                holder.tvFollow.setText(holder.tvFollow.getContext().getString(R.string.stringFollow));
                holder.tvFollow.setBackgroundDrawable(holder.tvFollow.getResources().getDrawable(R.drawable.bg_btn_c_follow_shape));
//                GlideLoader.loadImage(R.drawable.icon_profile_other_add, holder.ivFocusItemState, R.drawable.icon_profile_other_add);
            }
        }

        holder.tvFollow.setOnClickListener(new MyClickListener() {
            @Override
            protected void onMyClick(View v) {
                if (followClickListener != null) {
                    followClickListener.onFollowClick(position, data.getId(), data.isFollowed());
                }
            }
        });
        holder.ivAvatar.setOnClickListener(new MyClickListener() {
            @Override
            protected void onMyClick(View v) {
                if (followClickListener != null) {
                    followClickListener.onAvatarClick(data.getId());
                }
            }
        });
        holder.tvNickname.setOnClickListener(new MyClickListener() {
            @Override
            protected void onMyClick(View v) {
                if (followClickListener != null) {
                    followClickListener.onAvatarClick(data.getId());
                }
            }
        });

        if (data.getVerify() != null && !TextUtils.isEmpty(data.getVerify().icon)) {
            holder.ivVerifyIcon.setVisibility(View.VISIBLE);
            GlideLoader.loadImage(data.getVerify().icon, holder.ivVerifyIcon);
        } else {
            holder.ivVerifyIcon.setVisibility(View.GONE);
        }

    }

    public void refresh(int position, long userId, boolean isFollowed) {
        if (position > -1 && position < getDataContainer().size()) {
            UserInfo userEntity = getItemData(position);
            if (userEntity.getId() == userId) {
                userEntity.setFollowed(isFollowed);
                notifyItemChanged(position);
            }
        }
    }

    public void setFollowClickListener(OnFollowClickListener followClickListener) {
        this.followClickListener = followClickListener;
    }

    public interface OnFollowClickListener {
        void onFollowClick(int position, long userId, boolean isFollowed);

        void onAvatarClick(long userId);
    }
}
