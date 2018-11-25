package com.dongci.sun.gpuimglibrary.activity;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dongci.sun.gpuimglibrary.R;
import com.dongci.sun.gpuimglibrary.common.FilterInfoEntity;
import com.dongci.sun.gpuimglibrary.gles.filter.FilterViewHolder;

import java.util.List;


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
    public void onBindViewHolder(final FilterViewHolder holder, final int position) {
        holder.ivFilter.setImageResource(filterList.get(position).drawableId);
        holder.ivFilter.setChecked(selectedPosition == position);
//        holder.tvTitle.setText(holder.tvTitle.getResources().getString(filterList.get(position).titleId));
        holder.tvTitle.setText(filterList.get(position).titleStr);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition != holder.getAdapterPosition()) {
                    int old = selectedPosition;
                    selectedPosition = holder.getAdapterPosition();
                    notifyDataSetChanged();
                    if (selectListener != null) {
                        selectListener.onFilterSelected(selectedPosition, filterList.get(selectedPosition).filterId, filterList.get(old).filterId);
                    }
                }
            }
        });
    }

    public void addData(List<FilterInfoEntity> list) {
        if (list != null && list.size() > 0) {
            filterList.clear();
            filterList.addAll(list);
            notifyDataSetChanged();
            Log.d("tag", "FILTER_LIST--size-addData>" + filterList.size());
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
