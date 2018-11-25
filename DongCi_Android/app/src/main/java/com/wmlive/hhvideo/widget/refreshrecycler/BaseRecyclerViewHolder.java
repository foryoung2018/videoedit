package com.wmlive.hhvideo.widget.refreshrecycler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;


/**
 * RecyclerView的ViewHolder，所有的ViewHolder务必继承自这个类
 * <p>
 * adapter和layout的位置会有时间差(<16ms), 如果改变了Adapter的数据然后刷新视图,
 * layout需要过一段时间才会更新视图, 在这段时间里面, getAdapterPosition()、getLayoutPosition()这两个方法返回的position会不一样.
 * 在notifyDataSetChanged之后并不能马上获取Adapter中的position, 要等布局结束之后才能获取到.
 * 而对于Layout的position, 在notifyItemInserted之后, Layout不能马上获取到新的position,
 * 因为布局还没更新(需要<16ms的时间刷新视图), 所以只能获取到旧的, 但是Adapter中的position就可以马上获取到最新的position.
 */
public class BaseRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

    private ItemClickListener mItemClickListener;

    private ItemLongClickListener mItemLongClickListener;

    public BaseRecyclerViewHolder(ViewGroup parent, int layoutId) {
        this(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
    }

    public BaseRecyclerViewHolder(final View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        getRootView().setOnClickListener(new ClickListener() {
            @Override
            protected void onMyClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(itemView, getLayoutPosition());
                }
            }
        });
        getRootView().setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
        if (mItemLongClickListener != null) {
            mItemLongClickListener.onItemLongClick(v, getLayoutPosition());
            return true;
        }
        return false;
    }

    public BaseRecyclerViewHolder setItemClickListener(ItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
        return this;
    }

    public BaseRecyclerViewHolder setItemLongClickListener(ItemLongClickListener itemLongClickListener) {
        mItemLongClickListener = itemLongClickListener;
        return this;
    }

    public interface ItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public interface ItemLongClickListener {
        void onItemLongClick(View itemView, int position);
    }

    public View getRootView() {
        return super.itemView;
    }
}
