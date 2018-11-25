package com.wmlive.hhvideo.heihei.record.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wmlive.hhvideo.heihei.beans.frame.FrameInfo;
import com.wmlive.hhvideo.heihei.record.viewholder.FrameViewHolder;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ParamUtis;

import java.util.ArrayList;
import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 8/28/2017.
 */

public class FrameAdapter extends RecyclerView.Adapter<FrameViewHolder> {

    private List<FrameInfo> dataList;
    private List<Boolean> enableList;
    private OnFrameItemClickListener itemClickListener;
    private int selectIndex = 0;
    private int initCount = 0;
    private Context context;

    public FrameAdapter(List<FrameInfo> dataList) {
        this.dataList = dataList;
        enableList = new ArrayList<>();
        initEnableList();
    }

    @Override
    public FrameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new FrameViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_frame_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(FrameViewHolder holder, final int position) {
        FrameInfo frameInfo = dataList.get(position);
        if (frameInfo == null) {
            return;
        }
        ParamUtis.setLayoutParams3(context, holder.customFrameView, frameInfo.canvas_height);
        holder.customFrameView.setFrameInfo(frameInfo, true, false, true);
        holder.rlFrameSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    boolean enable = enableList.get(position);
                    if (selectIndex != position) {
                        itemClickListener.onFrameItemClick(dataList.get(position), enable, position);
                    }
                    if (enable) {
                        selectIndex = position;
                        notifyDataSetChanged();
                    }
                }
            }
        });
        if (selectIndex == position) {
//            holder.rlFrameSelect.setBackground(context.getResources().getDrawable(R.drawable.bg_btn_frame_shape));
            holder.ivSelcect.setVisibility(View.VISIBLE);
        } else {
//            holder.rlFrameSelect.setBackgroundColor(context.getResources().getColor(R.color.transparent));
            holder.ivSelcect.setVisibility(View.GONE);
        }
        holder.customFrameView.setEnable(enableList.get(position));

    }

    /**
     * @param index 选择的位置
     * @param count 选择的格子数量
     */
    public void setInitPosition(int index, int count) {
        this.selectIndex = index;
        this.initCount = count;
        initEnableList();
        notifyDataSetChanged();
    }

    public int getInitCount() {
        return initCount;
    }

    public void setInitCount(int count) {

        this.initCount = count;
        initEnableList();
        notifyDataSetChanged();
    }

    private void initEnableList() {
        KLog.d("dddddd", "setInitCount: count===" + initCount);
        enableList.clear();
        for (int i = 0, size = dataList.size(); i < size; i++) {
            FrameInfo frameInfo = dataList.get(i);
            if (frameInfo == null) {
                continue;
            }
            enableList.add(frameInfo.video_count >= initCount);
        }
    }


    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    /**
     * 添加数据
     *
     * @param list
     */
    public void addData(List<FrameInfo> list) {
        if (!CollectionUtil.isEmpty(list)) {
            dataList.clear();
            dataList.addAll(list);
            initEnableList();
            notifyDataSetChanged();
        }
    }

    public void setItemClickListener(OnFrameItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnFrameItemClickListener {
        void onFrameItemClick(FrameInfo frameInfo, boolean enable, int position);
    }
}
