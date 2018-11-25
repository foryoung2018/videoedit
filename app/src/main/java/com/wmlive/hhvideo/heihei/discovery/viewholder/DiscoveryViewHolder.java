package com.wmlive.hhvideo.heihei.discovery.viewholder;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.wmlive.hhvideo.heihei.discovery.adapter.DiscoverImageAdapter;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.widget.CustomFontTextView;
import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;


/**
 * Created by lsq on 5/27/2017.
 */

public class DiscoveryViewHolder extends BaseRecyclerViewHolder {

    @BindView(R.id.rvContainer)
    public RecyclerView rvContainer;
    @BindView(R.id.ivTopicType)
    public ImageView ivTopicType;
    @BindView(R.id.tvTopicName)
    public TextView tvTopicName;
    @BindView(R.id.tvCount)
    public TextView tvCount;
    @BindView(R.id.llTypePanel)
    public LinearLayout llTypePanel;
    @BindView(R.id.tvLabel)
    public CustomFontTextView tvLabel;
    @BindView(R.id.allTopicsRv)
    public RecyclerView allTopicsRv;

    public DiscoveryViewHolder(ViewGroup parent, int layoutId) {
        super(parent, layoutId);
    }

    public void showAnimImage(boolean show) {
        RecyclerView.LayoutManager layoutManager = rvContainer.getLayoutManager();
        RecyclerView.Adapter adapter = rvContainer.getAdapter();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager manager = (LinearLayoutManager) layoutManager;
            int first = manager.findFirstVisibleItemPosition();
            int last = manager.findLastVisibleItemPosition();
            if (adapter instanceof DiscoverImageAdapter) {
                DiscoverImageAdapter imageAdapter = (DiscoverImageAdapter) adapter;
                if (first > -1 && last < imageAdapter.getItemCount() && first <= last) {
                    for (int i = first; i <= last; i++) {
                        RecyclerView.ViewHolder viewHolder = rvContainer.findViewHolderForAdapterPosition(i);
                        if (viewHolder instanceof DiscoverImageViewHolder) {
                            ((DiscoverImageViewHolder) viewHolder).showAnimImage(show);
                            KLog.i("======showAnimImage position:" + i + " status:" + show);
                        }
                    }
                }
            }
        }

    }
}
