package com.wmlive.hhvideo.heihei.record.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wmlive.hhvideo.heihei.beans.record.MvConfigItem;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.record.activity.RecordMvActivity;
import com.wmlive.hhvideo.heihei.record.viewholder.MvEntityHolder;
import com.wmlive.hhvideo.utils.DensityUtil;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;

import java.util.List;

import cn.wmlive.hhvideo.R;

public class MvMaterialAdapter extends RecyclerView.Adapter<MvEntityHolder> {

    private List<ShortVideoEntity> dataList;
    List<MvConfigItem> configList;
    private OnFrameItemClickListener itemClickListener;
    private Context context;

    public MvMaterialAdapter(List<ShortVideoEntity> dataList, List<MvConfigItem> configList) {
        this.dataList = dataList;
        this.configList = configList;
    }

    public void setDataList(List<ShortVideoEntity> dataList, List<MvConfigItem> configList) {
        this.dataList = dataList;
        this.configList = configList;
        notifyDataSetChanged();
    }

    @Override
    public MvEntityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new MvEntityHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mvrecord, parent, false));
    }


    @Override
    public int getItemCount() {
        return configList == null ? 0 : configList.size();
    }

    @Override
    public void onBindViewHolder(MvEntityHolder holder, final int position) {
        ShortVideoEntity shortVideoEntityMv = dataList.get(position);
        if (shortVideoEntityMv == null) {
            return;
        }
        holder.imageBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    int state = configList.get(position).getState();
                    KLog.i("click--state-->" + state);
                    if (RecordMvActivity.currentIndex != position) {
                        itemClickListener.onFrameItemClick(dataList.get(position), position);
                        RecordMvActivity.currentIndex = position;
                        notifyDataSetChanged();
                    } else {//播放视频
                        if (configList.get(position).hasVideo())//当前没有正在录制，
                            itemClickListener.onFrameItemClickPlay(dataList.get(position), position, holder.itemPlayIv);
                    }

                    if (MvConfigItem.STATE_DONWLOAD_PPE == state || state == MvConfigItem.STATE_DONWLOAD_ERROR) {
                        itemClickListener.onReloadingClick(dataList.get(position), position);
                    }
                }
            }
        });
        GlideLoader.loadCornerImage(shortVideoEntityMv.getCoverUrl(), holder.imageBg, R.drawable.icon_video_shot, DensityUtil.dip2px(context, 5));
        holder.setState(configList.get(position).getState(),shortVideoEntityMv.getCoverUrl());

        if (RecordMvActivity.currentIndex == position ) {
            holder.itemPlayIv.setVisibility(View.VISIBLE);
            holder.root.setBackground(context.getResources().getDrawable(R.drawable.bg_record_mv_icon_shape));
            if(TextUtils.isEmpty(shortVideoEntityMv.getCoverUrl())){//
                holder.grayBg.setVisibility(View.GONE);
            }else {
                holder.grayBg.setVisibility(View.VISIBLE);
            }
            KLog.i(position + "adapter--conver--grayBg-Visibility>"+shortVideoEntityMv.getCoverUrl());
        } else {
            holder.root.setBackgroundColor(context.getResources().getColor(R.color.hh_color_sec_bg));
            holder.itemPlayIv.setVisibility(View.GONE);
            holder.grayBg.setVisibility(View.GONE);
        }
        if (!shortVideoEntityMv.hasMvVideo()) {//当前没有视频，
            holder.itemPlayIv.setVisibility(View.GONE);
            if (configList.get(position).getState() == MvConfigItem.STATE_DEFAULT) {//本地录制,没有录制
                holder.fontView.setVisibility(View.VISIBLE);
                KLog.i("===yang position " + (position));
            }
            holder.fontView.setText((position + 1) + "");
            KLog.i("===yang position --showtext" + (position));
        } else {//当前有视频
            holder.fontView.setVisibility(View.GONE);
        }

        KLog.i(position+"===adapter--hasMvVideo " + shortVideoEntityMv.hasMvVideo());
    }


    public void setItemClickListener(OnFrameItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    public interface OnFrameItemClickListener {
        void onFrameItemClick(ShortVideoEntity shortVideoEntityMv, int position);

        //二次点击时触发
        void onFrameItemClickPlay(ShortVideoEntity shortVideoEntityMv, int position, ImageView playerIv);

        void onReloadingClick(ShortVideoEntity shortVideoEntityMv, int positon);
    }
}
