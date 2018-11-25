package com.wmlive.hhvideo.heihei.mainhome.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.wmlive.hhvideo.heihei.beans.main.DcDanmaEntity;
import com.wmlive.hhvideo.heihei.mainhome.viewholder.RollCommentViewHolder;
import com.wmlive.hhvideo.utils.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 5/10/2018 - 6:40 PM
 * 类描述：
 */
public class RollCommentAdapter extends RecyclerView.Adapter<RollCommentViewHolder> {
    private List<DcDanmaEntity> dataList;
    private static final int MAY_VISIBLE_SIZE = 10;
    private boolean showWhiteText = false;

    public RollCommentAdapter(boolean showWhiteText) {
        dataList = new ArrayList<>(32);
        this.showWhiteText = showWhiteText;
    }

    @Override
    public RollCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RollCommentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_roll_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(RollCommentViewHolder holder, int position) {
        DcDanmaEntity danmaEntity = dataList.get(position);
        if (danmaEntity != null && danmaEntity.user != null) {
            holder.tvComment.setText(
                    "@" + danmaEntity.user.getName() +
                            (!TextUtils.isEmpty(danmaEntity.reply_user_name) ? " 回复 " + danmaEntity.reply_user_name : "")
                            + "：" + danmaEntity.title);
        } else {
            holder.tvComment.setText("");
        }
        if (showWhiteText) {
            holder.tvComment.setShadowLayer(1, 2, 2, R.color.app_background_other_back);
        } else {
            holder.tvComment.setShadowLayer(0, 0, 0, R.color.app_background_other_back);
        }
        holder.tvComment.setTextColor(holder.tvComment.getResources().getColor(showWhiteText ? R.color.white : R.color.hh_color_cc));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void addData(List<DcDanmaEntity> list) {
        dataList.clear();
        if (!CollectionUtil.isEmpty(list)) {
            dataList.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void addData(DcDanmaEntity text) {
        dataList.add(text);
        int oldSize = dataList.size();
        notifyItemInserted(oldSize);
        if (oldSize > (MAY_VISIBLE_SIZE + 4)) {
            int subSize = MAY_VISIBLE_SIZE + 4;
            dataList = dataList.subList(oldSize - subSize, dataList.size());
            notifyItemRangeRemoved(0, oldSize - subSize);
        }
    }

    public void clearData() {
        if (!CollectionUtil.isEmpty(dataList)) {
            dataList.clear();
            notifyDataSetChanged();
        }
    }
}
