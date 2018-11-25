package com.wmlive.hhvideo.heihei.record.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wmlive.hhvideo.heihei.beans.record.FilterInfoEntity;
import com.wmlive.hhvideo.heihei.record.viewholder.FilterViewHolder;
import com.wmlive.hhvideo.utils.CollectionUtil;

import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 8/28/2017.
 */

public class FilterPanelAdapter extends RecyclerView.Adapter<FilterViewHolder> {
    private List<FilterInfoEntity> filterList;
    private int selectedPosition = 0;
    private OnFilterItemSelectListener selectListener;

    public FilterPanelAdapter(List<FilterInfoEntity> filterList) {
        this.filterList = filterList;
    }

    @Override
    public FilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FilterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter, parent, false));
    }

    @Override
    public void onBindViewHolder(FilterViewHolder holder, final int position) {
        FilterInfoEntity entity = filterList.get(position);
        int filterIndex = entity.filterId;
        holder.tvTitle.setText(holder.tvTitle.getResources().getString(filterList.get(position).titleId));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition != position) {
                    int old = selectedPosition;
                    selectedPosition = position;

                    if (selectListener != null) {

                        selectListener.onFilterSelected(selectedPosition, filterList.get(selectedPosition).filterId, filterList.get(old).filterId);
                    }
                    notifyDataSetChanged();
                }
            }
        });
    }

    public void addData(List<FilterInfoEntity> list) {
        if (!CollectionUtil.isEmpty(list)) {
            filterList.clear();
            filterList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @Override
    public int getItemCount() {
        return filterList == null ? 0 : filterList.size();
    }

    public boolean setSelectItem(int index) {
        if (index > -1 && index < getItemCount() && selectedPosition != index) {
            selectedPosition = index;
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public void setFilterItemSelectListener(OnFilterItemSelectListener listener) {
        this.selectListener = listener;
    }

    public interface OnFilterItemSelectListener {
        void onFilterSelected(int selectIndex, int newId, int oldId);
    }
}
