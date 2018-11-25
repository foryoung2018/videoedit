package com.wmlive.hhvideo.heihei.discovery.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.wmlive.hhvideo.heihei.beans.search.SearchMusicBean;
import com.wmlive.hhvideo.heihei.discovery.AudioPlayerManager;
import com.wmlive.hhvideo.heihei.discovery.DiscoveryUtil;
import com.wmlive.hhvideo.heihei.discovery.viewholder.SelectMusicViewHolder;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.MyClickListener;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshAdapter;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;

import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 6/2/2017.
 * 搜索音乐的Adapter
 */

public class SearchMusicAdapter extends RefreshAdapter<SelectMusicViewHolder, SearchMusicBean> implements AudioPlayerManager.OnStatusChangeCallback {
    private int selectPosition = -1;  //当前选择的item
    private OnItemClickListener onItemClickListener;
    private int iconSize;
    private boolean isBuffered = false;


    public SearchMusicAdapter(List<SearchMusicBean> list, RefreshRecyclerView refreshView) {
        super(list, refreshView);
        iconSize = DeviceUtils.dip2px(refreshView.getContext(), 18);
        AudioPlayerManager.get().init();
        AudioPlayerManager.get().setStatusChangeCallback(this);
    }

    @Override
    public SelectMusicViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new SelectMusicViewHolder(parent, R.layout.item_select_music);
    }

    @Override
    public void onBindHolder(SelectMusicViewHolder holder, final int position, final SearchMusicBean data) {
        holder.rlUseMusic.setVisibility(selectPosition == position ? View.VISIBLE : View.GONE);
        GlideLoader.loadCircleImage(data == null ? null : data.getAlbum_cover(), holder.ivCover, R.drawable.bg_search_music_default);
        holder.ivCollect.setImageResource((data != null && data.is_favorite()) ? R.drawable.icon_collect_sel : R.drawable.icon_music_collect_nor);
        holder.rlUseMusic.setOnClickListener(new MyClickListener() {
            @Override
            protected void onMyClick(View v) {
                if (null != onItemClickListener) {
                    onItemClickListener.onUseMusicClick(position, data);
                }
            }
        });
        holder.ivCollect.setOnClickListener(new MyClickListener() {
            @Override
            protected void onMyClick(View v) {
                onItemClickListener.onCollectClick(position, data == null ? 0 : data.getId());
            }
        });
        holder.tvName.setText(data == null ? "" : data.getName());
        holder.tvArtist.setText(data == null ? "" : data.getSinger_name());
        holder.tvDuring.setText(DiscoveryUtil.convertTime(data == null ? 0 : data.getLongs()));
        DiscoveryUtil.setDrawable(holder.tvUseMusic, R.drawable.icon_music_record, 0, iconSize, iconSize);
        holder.setPlayIcon(data == null ? 0 : data.getPlayStatus());
    }

    @Override
    public void onBuffered() {
        if (selectPosition > -1 && selectPosition < mDataContainer.size()) {
            if (!isBuffered) {
                getDataContainer().get(selectPosition).setPlayStatus(1);
                notifyItemChanged(selectPosition);
                isBuffered = true;
            }
        }
    }

    @Override
    public void onReset() {
        if (selectPosition > -1 && selectPosition < mDataContainer.size()) {
            getDataContainer().get(selectPosition).setPlayStatus(0);
            notifyItemChanged(selectPosition);
            isBuffered = false;
        }
    }


    public interface OnItemClickListener {
        void onUseMusicClick(int position, SearchMusicBean data);

        void onCollectClick(int position, long musicId);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void addData(List<SearchMusicBean> list, boolean isRefresh, boolean hasMore) {
        selectPosition = isRefresh ? -1 : selectPosition;
        addData(isRefresh, list, hasMore);
    }

    /**
     * 刷新某个item的添加按钮
     *
     * @param position 刷新的位置
     */
    public void showItemAdd(int position) {
        if (position > -1 && position < mDataContainer.size()) {
            if (AudioPlayerManager.get().getStatusChangeCallback() == null) {
                AudioPlayerManager.get().setStatusChangeCallback(this);
            }
            if (selectPosition == -1) {//首次点击
                isBuffered = false;
                selectPosition = position;
                getDataContainer().get(selectPosition).setPlayStatus(2);//当前位置变成缓冲
                notifyItemChanged(selectPosition);
            } else if (selectPosition == position) {//点了相同的Item
                int oldStatus = getDataContainer().get(selectPosition).getPlayStatus();
                getDataContainer().get(selectPosition).setPlayStatus(oldStatus == 0 ? 2 : 0);//当前位置变成缓冲
                notifyItemChanged(selectPosition);
                isBuffered = (oldStatus == 1);
            } else {//点击不同的Item
                isBuffered = false;
                getDataContainer().get(selectPosition).setPlayStatus(0);//上次的item变成暂停
                notifyItemChanged(selectPosition);
                getDataContainer().get(position).setPlayStatus(2);//被点击的item变成缓冲
                notifyItemChanged(position);
                selectPosition = position;
            }

        }


//        if (position > -1 && position < mDataContainer.size()) {
//            if (selectPosition == -1 || selectPosition != position) {
//                notifyItemChanged(selectPosition);
//                notifyItemChanged(position);
//                selectPosition = position;
//            }
//        }
    }

    /**
     * 刷新item的收藏
     *
     * @param position
     * @param isFavorite
     */
    public void refreshItemFavorite(int position, boolean isFavorite) {
        if (position > -1 && position < mDataContainer.size()) {
            mDataContainer.get(position).setIs_favorite(isFavorite);
            notifyItemChanged(position);
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
//        if (holder instanceof SelectMusicViewHolder) {
//            ((SelectMusicViewHolder) holder).clearAnimation();
//        }
    }


    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        KLog.i("======onViewDetachedFromWindow");
        if ((null != holder) && (holder instanceof SelectMusicViewHolder)) {
            ((SelectMusicViewHolder) holder).clearAnimation();
        }
    }
}
