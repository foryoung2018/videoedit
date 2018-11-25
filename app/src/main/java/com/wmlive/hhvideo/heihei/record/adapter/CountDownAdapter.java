package com.wmlive.hhvideo.heihei.record.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import com.wmlive.hhvideo.heihei.record.viewholder.CountViewHolder;
import cn.wmlive.hhvideo.R;

/**
 * Author：create by jht on 2018/9/28 20:57
 * Email：haitian.jiang@welines.cn
 */
public class CountDownAdapter extends RecyclerView.Adapter<CountViewHolder> {
    private int selectedPos = 0;
    private String[] counts = new String[]{"0", "3", "10"};
    private  Context context;

    public CountDownAdapter(Context context,int defaultPos) {
        this.context = context;
        if(defaultPos>2)
            selectedPos = 0;
        selectedPos = defaultPos;
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    //define interface
    public interface OnRecyclerViewItemClickListener {
        void onCountDownItemClick(int position, String data);
    }

    //创建新View，被LayoutManager所调用
    @Override
    public CountViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        CountViewHolder vh = new CountViewHolder(viewGroup, R.layout.count_down_rv_items);
        return vh;
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(final CountViewHolder viewHolder, final int position) {
        viewHolder.itemView.setSelected(selectedPos == position);
        viewHolder.countTv.setText(counts[position]);
        if (selectedPos == position) {
            viewHolder.countTv.setTextColor(context.getResources().getColor(R.color.white));
            viewHolder.secondsTv.setTextColor(context.getResources().getColor(R.color.white));
            viewHolder.indicator.setVisibility(View.VISIBLE);
        } else {
            viewHolder.countTv.setTextColor(context.getResources().getColor(R.color.hh_color_kk));
            viewHolder.secondsTv.setTextColor(context.getResources().getColor(R.color.hh_color_kk));
            viewHolder.indicator.setVisibility(View.INVISIBLE);
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemChanged(selectedPos);
                selectedPos = position;
                notifyItemChanged(selectedPos);
                mOnItemClickListener.onCountDownItemClick(viewHolder.getAdapterPosition(), counts[viewHolder.getAdapterPosition()]);
            }
        });
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


    //获取数据的数量
    @Override
    public int getItemCount() {
        return counts.length;
    }
}

