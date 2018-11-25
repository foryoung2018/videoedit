package com.wmlive.hhvideo.heihei.record.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.wmlive.hhvideo.heihei.beans.record.FilterInfoEntity;
import com.wmlive.hhvideo.heihei.record.viewholder.FilterViewHolder;
import com.wmlive.hhvideo.heihei.record.widget.LocateCenterHorizontalView;
import org.greenrobot.eventbus.EventBus;
import java.util.List;
import cn.wmlive.hhvideo.R;

/**
 * Created by jht on 2018/8/9.
 */

public class FilterMirrorAdapter extends RecyclerView.Adapter<FilterViewHolder>
        implements LocateCenterHorizontalView.IAutoLocateHorizontalView {
    private View mView;
    private List<FilterInfoEntity> filterList;
    private OnFilterClickListener onFilterClickListener;
    private int circle;

    public void setOnFilterClickListener(OnFilterClickListener onFilterClickListener) {
        this.onFilterClickListener = onFilterClickListener;
    }

    public FilterMirrorAdapter(List<FilterInfoEntity> filterList, int circle) {
        this.filterList = filterList;
        this.circle = circle;
    }

    @Override
    public FilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_select_items, parent, false);
        return new FilterViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(FilterViewHolder holder, final int position) {
        int size = filterList.size();

        holder.tvTitle.setText(holder.tvTitle.getResources().getString(filterList.get(position % size).titleId));
        holder.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onFilterClickListener!=null){
                    onFilterClickListener.onFilterItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (filterList == null) {
            return 0;
        }
        return filterList.size()*circle;
    }

    @Override
    public View getItemView() {
        return mView;
    }

    public List<FilterInfoEntity> getData() {
        return filterList;
    }

    @Override
    public void onViewSelected(boolean isSelected, int pos, RecyclerView.ViewHolder holder, int itemWidth) {
        if (isSelected) {
            ((FilterViewHolder) holder).tvTitle.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            ((FilterViewHolder) holder).tvTitle.setTextColor(Color.parseColor("#999999"));
        }
    }

    public interface OnFilterClickListener{
        void onFilterItemClick(int position);
    }
}
