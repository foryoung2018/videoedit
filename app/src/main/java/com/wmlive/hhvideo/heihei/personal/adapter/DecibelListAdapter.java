package com.wmlive.hhvideo.heihei.personal.adapter;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.ViewGroup;

import com.wmlive.hhvideo.heihei.beans.personal.DecibelEntity;
import com.wmlive.hhvideo.heihei.beans.personal.DecibelListResponse;
import com.wmlive.hhvideo.heihei.personal.viewholder.DecibelViewHolder;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshAdapter;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;

import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 1/10/2018.11:07 AM
 *
 * @author lsq
 * @describe 添加描述
 */

public class DecibelListAdapter extends RefreshAdapter<DecibelViewHolder, DecibelEntity> {
    public static final short RANKLIST_TYPE_VIDEO_DETAIL = 10;
    public static final short RANKLIST_TYPE_USER_OWN = 20;
    private OnUserClickListener onUserClickListener;
    private short rankListType;
    private DecibelListResponse.StatisticEntity statistic;
    private boolean changeBackground = false;

    public DecibelListAdapter(List<DecibelEntity> list, RefreshRecyclerView refreshView, short rankListType) {
        super(list, refreshView);
        this.rankListType = rankListType;
    }

    @Override
    public DecibelViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new DecibelViewHolder(parent, R.layout.item_decibel_view_holder);
    }

    @Override
    public void onBindHolder(DecibelViewHolder holder, int position, DecibelEntity data) {
        if (data == null) {
            return;
        }
        if (position == 0) {
            if (statistic == null) {
                statistic = new DecibelListResponse.StatisticEntity();
            }
            if (rankListType == RANKLIST_TYPE_USER_OWN) {
                if (statistic.total_gift_point != 0) {
                    holder.tvDecibelCount.setText("累计获得" + statistic.total_point + "分贝(暴击" + statistic.total_prize_point + "分贝)");
                } else {
                    holder.tvDecibelCount.setText("累计获得" + statistic.total_point + "分贝");
                }
            } else if (rankListType == RANKLIST_TYPE_VIDEO_DETAIL) {
                if (data.prize_point != 0) {
                    holder.tvDecibelCount.setText("该作品累计获得" + statistic.total_point + "分贝(暴击" + statistic.total_prize_point + "分贝)");
                } else {
                    holder.tvDecibelCount.setText("该作品累计获得" + statistic.total_point + "分贝");
                }
            }
        }
        if (data.user != null) {
            GlideLoader.loadCircleImage(data.user.getCover_url(), holder.ivAvatar, data.user.isFemale() ? R.drawable.ic_default_female : R.drawable.ic_default_male);
            holder.tvNickname.setText(data.user.getName());
            String decibelString = "送了" + data.total_point + "分贝";
            if (data.prize_point != 0) {
                decibelString += "(暴击" + data.prize_point + "分贝)";
            }
            SpannableString spanString = new SpannableString(decibelString);
//            ForegroundColorSpan span = new ForegroundColorSpan(holder.tvDesc.getContext().getResources().getColor(R.color.hh_color_k));
            AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(18, true);
//            spanString.setSpan(span, 2, 4 + String.valueOf(data.total_point).length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            spanString.setSpan(sizeSpan, 2, 2 + String.valueOf(data.total_point).length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            holder.tvDesc.setText(spanString);

            if (data.user.getVerify() != null && !TextUtils.isEmpty(data.user.getVerify().icon)) {
                holder.ivVerifyIcon.setVisibility(View.VISIBLE);
                GlideLoader.loadImage(data.user.getVerify().icon, holder.ivVerifyIcon);
            } else {
                holder.ivVerifyIcon.setVisibility(View.GONE);
            }

        } else {
            holder.tvNickname.setText("");
            holder.tvDesc.setText("");
            holder.ivVerifyIcon.setVisibility(View.GONE);
        }
        holder.rlDecibelCount.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        holder.rlDecibelCount.setBackgroundResource(changeBackground ? R.drawable.bg_ranklist : R.drawable.bg_btn_c_follow_shape);
        holder.ivRank.setVisibility(position > 2 ? View.GONE : View.VISIBLE);
        if (position == 0) {
            holder.ivRank.setImageDrawable(holder.ivRank.getResources().getDrawable(R.drawable.icon_ranklist_gold));
        } else if (position == 1) {
            holder.ivRank.setImageDrawable(holder.ivRank.getResources().getDrawable(R.drawable.icon_ranklist_silver));
        } else if (position == 2) {
            holder.ivRank.setImageDrawable(holder.ivRank.getResources().getDrawable(R.drawable.icon_ranklist_copper));
        }


        holder.ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUserClickListener.onUserClick(data != null ? String.valueOf(data.user.getId()) : null);
            }
        });
        holder.tvNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUserClickListener.onUserClick(data != null ? String.valueOf(data.user.getId()) : null);
            }
        });
    }

    public void setChangeBackground(boolean changeBackground) {
        this.changeBackground = changeBackground;
    }

    public void setOnUserClickListener(OnUserClickListener onUserClickListener) {
        this.onUserClickListener = onUserClickListener;
    }

    public void setStatistic(DecibelListResponse.StatisticEntity statistic) {
        this.statistic = statistic;
    }

    public void clearData() {
        getDataContainer().clear();
        notifyDataSetChanged();
    }

    public interface OnUserClickListener {
        void onUserClick(String userId);
    }
}
