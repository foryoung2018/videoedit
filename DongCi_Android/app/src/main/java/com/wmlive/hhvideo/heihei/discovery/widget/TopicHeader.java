package com.wmlive.hhvideo.heihei.discovery.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.wmlive.hhvideo.heihei.beans.discovery.MusicInfoBean;
import com.wmlive.hhvideo.heihei.beans.discovery.TopicInfoBean;
import com.wmlive.hhvideo.heihei.discovery.DiscoveryUtil;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;
import com.wmlive.hhvideo.widget.BaseCustomView;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 6/5/2017.
 * 话题内容头部
 */

public class TopicHeader extends BaseCustomView {
    @BindView(R.id.ivAvatar)
    ImageView ivAvatar;
    @BindView(R.id.tvJoin)
    TextView tvJoin;
    @BindView(R.id.tvDesc)
    TextView tvDesc;
    @BindView(R.id.tvInitiator)
    TextView tvInitiator;
    @BindView(R.id.tvType)
    TextView tvType;
    @BindView(R.id.ivBackCover)
    ImageView ivBackCover;
    @BindView(R.id.ivTopicPic)
    ImageView ivTopicPic;
    @BindView(R.id.flJoin)
    FrameLayout flJoin;
    private TopicInfoBean topicInfoBean;
    private MusicInfoBean musicInfoBean;
    private OnHeaderClickListener clickListener;

    public TopicHeader(Context context) {
        super(context);
    }

    public TopicHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {
        tvJoin.setOnClickListener(this);
        ivAvatar.setOnClickListener(this);
        setViewLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_topic_header;
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        if (topicInfoBean != null && clickListener != null) {
            switch (v.getId()) {
                case R.id.ivAvatar:
                    clickListener.onAvatarClick(topicInfoBean);
                    break;
                case R.id.tvJoin:
                    clickListener.onJoinTopicClick(topicInfoBean);
                    break;
                default:
                    break;
            }
        }
    }

    public TopicHeader setData(TopicInfoBean data) {
        if (data != null) {
            topicInfoBean = data;
            GlideLoader.loadCircleImage(topicInfoBean.getUser() == null ? "" : topicInfoBean.getUser().getCover_url(), ivAvatar, R.drawable.ic_default_male);
            tvInitiator.setText(getContext().getString(R.string.stringInitiator, topicInfoBean.getUser() == null ? null : topicInfoBean.getUser().getName()));
            tvType.setText(topicInfoBean.getName());
            tvDesc.setText(topicInfoBean.getDescription());
            int drawableId = 0;
            if (!TextUtils.isEmpty(data.getTopic_type())) {
                switch (data.getTopic_type()) {
                    case "Topic":
                        drawableId = R.drawable.icon_search_topic;
                        break;
                    case "Music":
                        drawableId = R.drawable.icon_search_music_sel;
                        break;
                    default:
                        break;
                }
            }
            DiscoveryUtil.setDrawable(tvType, drawableId, 0, DeviceUtils.dip2px(getContext(), 14), DeviceUtils.dip2px(getContext(), 14));
            if (null != data.getUser()) {
                GlideLoader.loadImage(data.getUser().getCover_url(), ivBackCover);
            }
            ivTopicPic.setVisibility(!TextUtils.isEmpty(data.getCover_url()) ? VISIBLE : GONE);
            GlideLoader.loadCornerImage(data.getCover_url(), ivTopicPic, DeviceUtils.dip2px(ivTopicPic.getContext(), 6));
        } else {
            ivTopicPic.setVisibility(GONE);
        }
        return this;
    }

    public TopicHeader setData(MusicInfoBean data) {
        if (data != null) {
            musicInfoBean = data;
            GlideLoader.loadCircleImage(musicInfoBean.getAlbum_cover(), ivAvatar, R.drawable.ic_default_male);
            tvInitiator.setText(musicInfoBean.getSinger_name());
        }
        return this;
    }

    public int getJoinHeight() {
        return flJoin.getMeasuredHeight();
    }

    public void setClickListener(OnHeaderClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface OnHeaderClickListener {
        void onJoinTopicClick(TopicInfoBean topicInfoBean);

        void onAvatarClick(TopicInfoBean topicInfoBean);
    }
}
