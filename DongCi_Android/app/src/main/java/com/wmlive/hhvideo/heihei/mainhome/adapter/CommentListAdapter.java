package com.wmlive.hhvideo.heihei.mainhome.adapter;

import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wmlive.hhvideo.heihei.beans.main.CommText;
import com.wmlive.hhvideo.heihei.beans.main.Comment;
import com.wmlive.hhvideo.heihei.mainhome.viewholder.CommentListViewHolder;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.MyClickListener;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;
import com.wmlive.hhvideo.widget.UrlImageSpan;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshAdapter;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;

import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 6/19/2017.
 * 评论列表的Adapter
 */

public class CommentListAdapter extends RefreshAdapter<CommentListViewHolder, Comment> {
    private OnCommentItemClickListener commentItemClickListener;

    public CommentListAdapter(List<Comment> list, RefreshRecyclerView refreshView) {
        super(list, refreshView);
    }

    @Override
    public CommentListViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new CommentListViewHolder(parent, R.layout.item_video_comment);
    }

    @Override
    public void onBindHolder(CommentListViewHolder holder, final int dataPosition, Comment data) {
        data = (data == null ? new Comment() : data);
        GlideLoader.loadCircleImage(data.getUser() == null ? null : data.getUser().getCover_url(), holder.ivAvatar, data.getUser().isFemale() ? R.drawable.ic_default_female : R.drawable.ic_default_male);
        if (data.getUser() != null && data.getUser().getVerify() != null && !TextUtils.isEmpty(data.getUser().getVerify().icon)) {
            holder.ivVerifyIcon.setVisibility(View.VISIBLE);
            GlideLoader.loadImage(data.getUser().getVerify().icon, holder.ivVerifyIcon);
        } else {
            holder.ivVerifyIcon.setVisibility(View.GONE);
        }

        holder.viewTopLine.setVisibility(View.GONE);
        holder.tvName.setText(data.getUser() == null ? "" : data.getUser().getName());
        if (data.getLike_count() > 0) {
            holder.tvStartCount.setVisibility(View.VISIBLE);
            holder.tvStartCount.setText(String.valueOf(data.getLike_count()));
        } else {
            holder.tvStartCount.setVisibility(View.GONE);
        }
        holder.ivStarts.setImageResource(data.is_like() ? R.drawable.like_17 : R.drawable.icon_comment);
        if (data.getComm_type() == 1) {
            CommText commText = data.getComm_text();
            if (null != commText) {
                setPan(holder.tvComment, commText);
                holder.tvComment.setTextColor(holder.tvComment.getResources().getColor(R.color.hh_color_i));
            } else {
                holder.tvComment.setTextColor(holder.tvComment.getResources().getColor(R.color.hh_color_dd));
                holder.tvComment.setText(data.getTitle());
            }
        } else {
            holder.tvComment.setTextColor(holder.tvComment.getResources().getColor(R.color.hh_color_dd));
            if (!TextUtils.isEmpty(data.getReply_user_name())) {
                holder.tvComment.setText("回复" + data.getReply_user_name() + "：" + data.getTitle());
            } else {
                holder.tvComment.setText(data.getTitle());
            }
        }

        final Comment finalData = data;
        holder.ivAvatar.setOnClickListener(new MyClickListener() {
            @Override
            protected void onMyClick(View v) {
                if (null != commentItemClickListener) {
                    commentItemClickListener.onAvatarClick(finalData.getUser_id());
                }
            }
        });
        holder.tvName.setOnClickListener(new MyClickListener() {
            @Override
            protected void onMyClick(View v) {
                if (null != commentItemClickListener) {
                    commentItemClickListener.onAvatarClick(finalData.getUser_id());
                }
            }
        });
        holder.llStarts.setOnClickListener(new MyClickListener() {
            @Override
            protected void onMyClick(View v) {
                if (null != commentItemClickListener) {
                    commentItemClickListener.onStarClick(dataPosition, finalData);
                }
            }
        });
        holder.getRootView().setOnClickListener(new MyClickListener() {
            @Override
            protected void onMyClick(View v) {
                if (null != commentItemClickListener) {
                    commentItemClickListener.onNameClick(dataPosition, finalData);
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            int p = position - (hasHeader() ? 1 : 0);
            KLog.i("===最终刷新点赞的位置：" + p);
            if (holder instanceof CommentListViewHolder) {
                if (getItemData(p).getLike_count() > 0) {
                    ((CommentListViewHolder) holder).tvStartCount.setVisibility(View.VISIBLE);
                    ((CommentListViewHolder) holder).tvStartCount.setText(String.valueOf(getItemData(p).getLike_count()));
                } else {
                    ((CommentListViewHolder) holder).tvStartCount.setVisibility(View.INVISIBLE);
                }
                ((CommentListViewHolder) holder).ivStarts.setImageResource(getItemData(p).is_like() ? R.drawable.like_17 : R.drawable.icon_comment);
                if (getItemData(p).is_like()) {
                    animationStars(((CommentListViewHolder) holder).ivStarts);
                }
            }
        }
    }

    public void refreshItemDelete(int position, long commentId) {
        if (position > -1 && position < getDataContainer().size()) {
            if (getDataContainer().get(position).getId() == commentId) {
                getDataContainer().remove(position);
                notifyDataSetChanged();
            }
        }
    }

    public void setCommentItemClickListener(OnCommentItemClickListener commentItemClickListener) {
        this.commentItemClickListener = commentItemClickListener;
    }

    public void insertData(Comment comment) {
        if (comment != null) {
            if (!hasContent()) {
                getDataContainer().clear();
            }
            getDataContainer().add(0, comment);
            notifyDataSetChanged();
        }
    }

    public void clearData() {
        getDataContainer().clear();
        notifyDataSetChanged();
    }

    public interface OnCommentItemClickListener {
        void onAvatarClick(long userId);

        void onNameClick(int position, Comment comment);

        void onStarClick(int position, Comment comment);
    }

    /**
     * 图文混排
     *
     * @param textView
     * @param commText
     */
    private void setPan(TextView textView, CommText commText) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append("送了xx");

        UrlImageSpan urlImageSpan = new UrlImageSpan(textView.getContext(), commText.icon_url, textView);
        builder.setSpan(urlImageSpan, 0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        String name = "送了" + commText.name + "";
        builder.insert(0, name, 0, name.length());
        builder.append(String.valueOf(" x" + commText.count));

        textView.setText(builder);
    }

    private void animationStars(ImageView imageView) {
        imageView.setImageResource(R.drawable.like_animation_list);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
        animationDrawable.setOneShot(true);
        animationDrawable.start();
    }
}