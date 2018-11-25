package com.wmlive.hhvideo.heihei.record.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.beans.record.MvConfigItem;
import com.wmlive.hhvideo.heihei.record.activity.RecordMvActivity;
import com.wmlive.hhvideo.heihei.record.viewholder.MvEntityHolder;
import com.wmlive.hhvideo.utils.DensityUtil;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;

import java.util.List;

import cn.wmlive.hhvideo.R;

public class MvMaterialAdapter extends RecyclerView.Adapter<MvEntityHolder> {

    private List<ShortVideoEntity> dataList;
    List<MvConfigItem> configList;
    private OnFrameItemClickListener itemClickListener;
    private Context context;
    private int[] defalutIcons = new int[]{R.drawable.icon_video_shot_1, R.drawable.icon_video_shot_2, R.drawable.icon_video_shot_3,
            R.drawable.icon_video_shot_4, R.drawable.icon_video_shot_5, R.drawable.icon_video_shot_6};



    public MvMaterialAdapter(List<ShortVideoEntity> dataList, List<MvConfigItem> configList) {
        this.dataList = dataList;
        this.configList = configList;
    }

    public void setDataList(List<ShortVideoEntity> dataList, List<MvConfigItem> configList) {
        this.dataList = dataList;
        this.configList = configList;
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
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    int state = configList.get(position).state;
                    if (RecordMvActivity.currentIndex != position) {
                        itemClickListener.onFrameItemClick(dataList.get(position), position);
                        RecordMvActivity.currentIndex = position;
                        notifyDataSetChanged();
                    } else {//播放视频
                        itemClickListener.onFrameItemClickPlay(dataList.get(position), position, holder.itemPlayIv);
                    }

                    if (0 == state) {
                        itemClickListener.onReloadingClick(dataList.get(position), position);
                    }
                }
            }
        });

        GlideLoader.loadCornerImage(shortVideoEntityMv.getCoverUrl(), holder.image, R.drawable.icon_video_shot, DensityUtil.dip2px(context, 5));
        holder.setState(configList.get(position).getState());

        if (RecordMvActivity.currentIndex == position) {
            holder.itemPlayIv.setVisibility(View.VISIBLE);
            holder.root.setBackground(context.getResources().getDrawable(R.drawable.bg_record_mv_icon_shape));
            holder.grayBg.setVisibility(View.VISIBLE);
        } else {
            holder.root.setBackgroundColor(context.getResources().getColor(R.color.hh_color_sec_bg));
            holder.itemPlayIv.setVisibility(View.GONE);
            holder.grayBg.setVisibility(View.GONE);
        }

        if(!shortVideoEntityMv.hasMvVideo()){
            holder.itemPlayIv.setVisibility(View.GONE);
            holder.fontView.setVisibility(View.VISIBLE);
            holder.fontView.setText(position+1+"");
        }else{
            holder.fontView.setVisibility(View.GONE);
        }

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
