package com.wmlive.hhvideo.heihei.personal.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.wmlive.hhvideo.heihei.beans.personal.UserHomeRelation;
import com.wmlive.hhvideo.heihei.beans.search.SearchUserBean;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshAdapter;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;

import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 * Created by XueFei on 2017/5/27.
 * <p>
 * 粉丝
 */

public class FansAdapter extends RefreshAdapter<FansHolder, SearchUserBean> {
    private long mCurrentId = 0;

    public FansAdapter(RefreshRecyclerView refreshView, List<SearchUserBean> list, long current_id) {
        super(list, refreshView);
        mCurrentId = current_id;
    }

    @Override
    public FansHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new FansHolder(parent, R.layout.activity_fans_item);
    }

    @Override
    public void onBindHolder(FansHolder holder, final int position, SearchUserBean data) {
        GlideLoader.loadCircleImage(data.getCover_url(), holder.ivFansItemHead, data.isFemale() ? R.drawable.ic_default_female : R.drawable.ic_default_male);
        holder.tvFansItemName.setText(data.getName());
        UserHomeRelation relation = data.getRelation();
        if (mCurrentId == data.getId()) {
//            holder.ivFansItemState.setVisibility(View.GONE);
            holder.tvFollow.setVisibility(View.GONE);
        } else {
            holder.tvFollow.setVisibility(View.VISIBLE);
            if (null != relation) {
                if (relation.is_follow && relation.is_fans) {
//                    GlideLoader.loadImage(R.drawable.icon_friend_followeach, holder.ivFocusItemState, R.drawable.icon_profile_other_add);
                    holder.tvFollow.setText(holder.tvFollow.getContext().getString(R.string.stringFollowedBoth));
                    holder.tvFollow.setBackgroundDrawable(null);
                } else if (relation.is_follow) {
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

        holder.tvFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnClickFansCustom) {
                    mOnClickFansCustom.onClick(v, position);
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

    private OnClickFansCustom mOnClickFansCustom;

    public interface OnClickFansCustom {
        void onClick(View view, int position);
    }

    public void setOnClickCustom(OnClickFansCustom onClickFansCustom) {
        mOnClickFansCustom = onClickFansCustom;
    }
}
