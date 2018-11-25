package com.wmlive.hhvideo.heihei.mainhome.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wmlive.hhvideo.heihei.beans.search.SearchUserBean;
import com.wmlive.hhvideo.heihei.discovery.DiscoveryUtil;
import com.wmlive.hhvideo.heihei.mainhome.viewholder.ContactViewHolder;
import com.wmlive.hhvideo.utils.MyClickListener;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshAdapter;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;

import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 * Created by Administrator on 3/15/2018.
 */

public class ContactAdapter extends RefreshAdapter<ContactViewHolder, SearchUserBean> {
    private String keyword;
    private OnContactClickListener contactClickListener;

    public ContactAdapter(List<SearchUserBean> list, RefreshRecyclerView refreshView) {
        super(list, refreshView);
    }

    @Override
    public ContactViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new ContactViewHolder(parent, R.layout.item_contact);
    }

    @Override
    public void onBindHolder(ContactViewHolder holder, int position, SearchUserBean data) {
        if (data == null) {
            return;
        }
        holder.tvFocusLabel.setVisibility(TextUtils.isEmpty(keyword) && position == 0 ? View.VISIBLE : View.GONE);
        GlideLoader.loadCircleImage(data.getCover_url(), holder.ivAvatar, data.isFemale() ? R.drawable.ic_default_female : R.drawable.ic_default_male);
        if (data.getVerify() != null && !TextUtils.isEmpty(data.getVerify().icon)) {
            holder.ivVerifyIcon.setVisibility(View.VISIBLE);
            GlideLoader.loadImage(data.getVerify().icon, holder.ivVerifyIcon);
        } else {
            holder.ivVerifyIcon.setVisibility(View.GONE);
        }
        holder.tvName.setText(data.getName());
        if (!TextUtils.isEmpty(data.getName()) && !TextUtils.isEmpty(keyword)) {
            changeTextColor(data.getName(), keyword, holder.tvName);
        }
        holder.ivAvatar.setOnClickListener(new MyClickListener() {
            @Override
            protected void onMyClick(View v) {
                if (contactClickListener != null) {
                    contactClickListener.onAvatarClick(data.getId());
                }
            }
        });
        holder.tvName.setOnClickListener(new MyClickListener() {
            @Override
            protected void onMyClick(View v) {
                if (contactClickListener != null) {
                    contactClickListener.onAvatarClick(data.getId());
                }
            }
        });
    }

    private void changeTextColor(String string, String keyword, TextView view) {
        int start = string.indexOf(keyword);
        int end = start + keyword.length();
        if (start >= 0 && end <= string.length()) {
            DiscoveryUtil.changeTextColor(view, string, start, end, 0xFF0090FF);
        }
    }

    public void addDatas(String keyword, boolean isRefresh, List<SearchUserBean> newDataList, boolean hasMore) {
        this.keyword = keyword;
        addData(isRefresh, newDataList, hasMore);
    }

    public void setContactClickListener(OnContactClickListener contactClickListener) {
        this.contactClickListener = contactClickListener;
    }

    public interface OnContactClickListener {
        void onAvatarClick(long userId);
    }
}
