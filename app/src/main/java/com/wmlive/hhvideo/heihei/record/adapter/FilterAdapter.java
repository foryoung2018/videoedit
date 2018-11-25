package com.wmlive.hhvideo.heihei.record.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wmlive.hhvideo.heihei.beans.record.FilterInfoEntity;
import com.wmlive.hhvideo.heihei.record.manager.RecordSetting;

import java.util.List;

import cn.wmlive.hhvideo.R;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.VH> {
    private Context context;
    private List<String> datas;
    private boolean mIsNotLoop;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private OnItemClickListener onItemClickListener;
//    private List<FilterInfoEntity> dates = RecordSetting.FILTER_LIST;

    public FilterAdapter(Context context) {
        this.context = context;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        VH h = new VH(LayoutInflater.from(context)
                .inflate(R.layout.filter_select_items, parent, false));

        return h;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
//            holder.tv.setText("Number :" + (position % datas.size()));
        holder.tv.setText(context.getString(RecordSetting.FILTER_LIST.get(position % RecordSetting.FILTER_LIST.size()).titleId));

        holder.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position % RecordSetting.FILTER_LIST.size(), holder.tv);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mIsNotLoop ? datas.size() : Integer.MAX_VALUE;
//        return RecordSetting.FILTER_LIST.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position, Object obj);
    }

    class VH extends RecyclerView.ViewHolder {

        TextView tv;

        public VH(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tvTitle);
        }
    }
}
