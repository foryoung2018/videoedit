package com.wmlive.hhvideo.heihei.discovery.adapter;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wmlive.hhvideo.heihei.beans.search.SearchMusicBean;
import com.wmlive.hhvideo.heihei.beans.search.SearchTopicBean;
import com.wmlive.hhvideo.heihei.beans.search.SearchUserBean;
import com.wmlive.hhvideo.heihei.discovery.DiscoveryUtil;
import com.wmlive.hhvideo.heihei.discovery.activity.SearchActivity;
import com.wmlive.hhvideo.heihei.discovery.viewholder.SearchExplosionViewHolder;
import com.wmlive.hhvideo.heihei.discovery.viewholder.SearchMusicViewHolder;
import com.wmlive.hhvideo.heihei.discovery.viewholder.SearchUserViewHolder;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;
import com.wmlive.hhvideo.utils.imageloader.LoadCallback;
import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshAdapter;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;

import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 5/31/2017.
 * 搜索页面的Adapter
 */

public class SearchAdapter<T> extends RefreshAdapter<BaseRecyclerViewHolder, T> {
    public static final int TYPE_USER = 11;
    public static final int TYPE_TOPIC = 12;
    public static final int TYPE_MUSIC = 13;
    private int searchType = TYPE_USER;
    private String keyword;

    public SearchAdapter(List<T> list, RefreshRecyclerView refreshView) {
        super(list, refreshView);
    }

    @Override
    public BaseRecyclerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_USER:
                return new SearchUserViewHolder(parent, R.layout.item_search_user_result);
            case TYPE_TOPIC:
                return new SearchExplosionViewHolder(parent, R.layout.item_search_explosion_result);
            case TYPE_MUSIC:
                return new SearchMusicViewHolder(parent, R.layout.item_search_music_result);
            default:
                break;
        }
        return null;
    }

    @Override
    public void onBindHolder(final BaseRecyclerViewHolder holder, int position, T data) {
        switch (searchType) {
            case TYPE_USER:
                if (holder instanceof SearchUserViewHolder) {
                    SearchUserViewHolder userHolder = (SearchUserViewHolder) holder;
                    SearchUserBean bean = (SearchUserBean) data;
                    userHolder.tvName.setText(bean == null ? "" : bean.getName());
                    GlideLoader.loadCircleImage(bean == null ? "" : bean.getCover_url(), userHolder.ivAvatar,
                            bean == null || !bean.isFemale() ? R.drawable.ic_default_male : R.drawable.ic_default_female);
                    if (bean != null && bean.getVerify() != null && !TextUtils.isEmpty(bean.getVerify().icon)) {
                        userHolder.ivVerifyIcon.setVisibility(View.VISIBLE);
                        GlideLoader.loadImage(bean.getVerify().icon, userHolder.ivVerifyIcon);
                    } else {
                        userHolder.ivVerifyIcon.setVisibility(View.GONE);
                    }
                    if (null != bean) {
                        if (null != bean.getRelation()) {
                            userHolder.tvDesc.setText(userHolder.tvDesc.getResources().getString(R.string.stringHeiheiId, bean.getDc_num(), bean.getRelation().fans_count));
                        } else {
                            userHolder.tvDesc.setText(userHolder.tvDesc.getResources().getString(R.string.stringHeiheiId, bean.getDc_num(), 0));
                        }
                        if (null != bean.getData()) {
                            userHolder.tvCount.setText(userHolder.tvCount.getResources().getString(R.string.stringWorkCount, bean.getData().getOpus_count()));
                        } else {
                            userHolder.tvCount.setText(userHolder.tvCount.getResources().getString(R.string.stringWorkCount, 0));
                        }
                    }
//                    userHolder.tvDesc.setText(userHolder.tvDesc.getResources().getString(R.string.stringHeiheiId, bean == null ? "" : bean.getDc_num(), bean == null ? 0 : bean.getFans_count()));
//                    userHolder.tvCount.setText(userHolder.tvCount.getResources().getString(R.string.stringWorkCount, bean == null ? 0 : bean.getOpus_count()));
                    if (bean != null && !TextUtils.isEmpty(bean.getName()) && !TextUtils.isEmpty(keyword)) {
                        int start = bean.getName().indexOf(keyword);
                        int end = start + keyword.length();
                        if (start >= 0 && end <= bean.getName().length()) {
                            changeTextColor(bean.getName(), keyword, userHolder.tvName);
                        }
                    }
                }
                break;
            case TYPE_TOPIC:
                if (holder instanceof SearchExplosionViewHolder) {
                    SearchExplosionViewHolder topicHolder = (SearchExplosionViewHolder) holder;
                    SearchTopicBean bean = (SearchTopicBean) data;
                    topicHolder.tvTitle.setText(bean == null ? "" : bean.getName());
                    topicHolder.tvCount.setText(topicHolder.tvCount.getResources().getString(R.string.stringJoinCount, bean == null ? 0 : bean.getOpus_count()));
                    if (bean != null && !TextUtils.isEmpty(bean.getName()) && !TextUtils.isEmpty(keyword)) {
                        changeTextColor(bean.getName(), keyword, topicHolder.tvTitle);
                    }
                }
                break;
            case TYPE_MUSIC:
                if (holder instanceof SearchMusicViewHolder) {
                    final SearchMusicViewHolder musicHolder = (SearchMusicViewHolder) holder;
                    SearchMusicBean bean = (SearchMusicBean) data;
                    GlideLoader.loadCircleImage(bean == null ? "" : bean.getAlbum_cover(), musicHolder.ivAvatar, R.drawable.bg_search_music_default, new LoadCallback() {
                        @Override
                        public void onDrawableLoaded(Drawable drawable) {
                            musicHolder.ivAvatarCover.setVisibility(View.VISIBLE);
                        }
                    });
                    musicHolder.tvName.setText(bean == null ? "" : bean.getName());
                    musicHolder.tvDesc.setText(bean == null ? "" : bean.getSinger_name());
                    musicHolder.tvDuring.setText(DiscoveryUtil.convertTime(bean == null ? 0 : bean.getLongs()));
                    musicHolder.tvCount.setText(musicHolder.tvCount.getResources().getString(R.string.stringUseCount, bean == null ? 0 : bean.getUse_count()));
                    if (bean != null && !TextUtils.isEmpty(bean.getName()) && !TextUtils.isEmpty(keyword)) {
                        changeTextColor(bean.getName(), keyword, musicHolder.tvName);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void changeTextColor(String string, String keyword, TextView view) {
        int start = string.indexOf(keyword);
        int end = start + keyword.length();
        if (start >= 0 && end <= string.length()) {
            DiscoveryUtil.changeTextColor(view, string, start, end, 0xFF0090FF);
        }
    }

    @Override
    public int getItemType(int position) {
        return searchType;
    }


    //添加数据
    public void addData(String keyword, boolean isRefresh, List<T> newDataList, String type, boolean hasMore) {
        switch (type) {
            case SearchActivity.TYPE_MUSIC:
                this.searchType = TYPE_MUSIC;
                break;
            case SearchActivity.TYPE_TOPIC:
                this.searchType = TYPE_TOPIC;
                break;
            case SearchActivity.TYPE_USER:
                this.searchType = TYPE_USER;
                break;
            default:
                break;
        }
        this.keyword = keyword;
        addData(isRefresh, newDataList, hasMore);
    }


}
