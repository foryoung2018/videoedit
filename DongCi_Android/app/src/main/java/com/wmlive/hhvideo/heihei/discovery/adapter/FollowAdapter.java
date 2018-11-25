package com.wmlive.hhvideo.heihei.discovery.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.discovery.viewholder.FollowUserViewHolder;
import com.wmlive.hhvideo.utils.MyClickListener;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;

import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 9/20/2017.
 */

public class FollowAdapter extends RecyclerView.Adapter<FollowUserViewHolder> {
    private List<UserInfo> userEntityList;
    private DiscoveryAdapter.OnDiscoverClickListener listener;

    public FollowAdapter(List<UserInfo> userEntityList) {
        this.userEntityList = userEntityList;
    }

    @Override
    public FollowUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FollowUserViewHolder(parent, R.layout.item_follow_user);
    }

    @Override
    public void onBindViewHolder(FollowUserViewHolder holder, final int position) {
        final UserInfo entity = userEntityList.get(position);
        GlideLoader.loadCircleImage(entity.getCover_url(), holder.ivAvatar, entity.isFemale() ? R.drawable.ic_default_female : R.drawable.ic_default_male);
        holder.tvNickname.setText(entity.getName());
        holder.tvFollow.setText(holder.tvFollow.getResources().getString(entity.isFollowed() ? R.string.stringFollowed : R.string.stringFollow));
//        holder.tvFollow.setTextColor(holder.tvFollow.getResources().getColor(entity.isFollowed() ? R.color.hh_color_b : R.color.hh_color_a));
        holder.tvFollow.setBackgroundDrawable(entity.isFollowed() ? null : holder.tvFollow.getResources().getDrawable(R.drawable.bg_btn_c_follow_shape));

        holder.itemView.setOnClickListener(new MyClickListener() {
            @Override
            protected void onMyClick(View v) {
                if (listener != null) {
                    listener.onAvatarClick(entity.getId());
                }
            }
        });
        holder.tvFollow.setOnClickListener(new MyClickListener() {
            @Override
            protected void onMyClick(View v) {
                if (listener != null) {
                    listener.onFollowClick(entity.getId(), position, entity.isFollowed());
                }
            }
        });

        if (entity.getVerify() != null && !TextUtils.isEmpty(entity.getVerify().icon)) {
            holder.ivVerifyIcon.setVisibility(View.VISIBLE);
            GlideLoader.loadImage(entity.getVerify().icon, holder.ivVerifyIcon);
        } else {
            holder.ivVerifyIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return userEntityList == null ? 0 : userEntityList.size();
    }

    public void addData(List<UserInfo> list) {
        userEntityList.clear();
        userEntityList.addAll(list);
    }

    public void setDiscoverClickListener(DiscoveryAdapter.OnDiscoverClickListener listener) {
        this.listener = listener;
    }


}
