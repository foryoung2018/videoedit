package com.wmlive.hhvideo.heihei.discovery.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.wmlive.hhvideo.heihei.beans.discovery.FocusBean;
import com.wmlive.hhvideo.heihei.beans.discovery.TopicTypeListBean;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.beans.main.RefreshCommentBean;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoLoveResponse;
import com.wmlive.hhvideo.heihei.beans.personal.UserHomeRelation;
import com.wmlive.hhvideo.heihei.discovery.viewholder.DiscoveryViewHolder;
import com.wmlive.hhvideo.heihei.discovery.viewholder.FollowUserPanelViewHolder;
import com.wmlive.hhvideo.heihei.discovery.viewholder.RecommendVideoViewHolder;
import com.wmlive.hhvideo.heihei.mainhome.viewholder.FocusingViewHolder;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.MyClickListener;
import com.wmlive.hhvideo.widget.CustomFontTextView;
import com.wmlive.hhvideo.widget.refreshrecycler.BaseRecyclerViewHolder;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshAdapter;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;
import com.wmlive.hhvideo.widget.snaprecycler.ScalableCardHelper;

import java.util.ArrayList;
import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 5/27/2017.
 * 发现的Adapter
 */

public class DiscoveryAdapter extends RefreshAdapter<BaseRecyclerViewHolder, TopicTypeListBean.TopicListBean> {

    private OnDiscoverClickListener onPictureClickListener;
    private int iconSize;
    private RecyclerView.RecycledViewPool viewPool;
    private static final int TYPE_FOLLOWS = 100;
    private static final int TYPE_RECOMMEND_VIDEO = 110;
    private static final int TYPE_FOCUS_SINGER = 111;
    private boolean hasRecommendVideo = false;
    private boolean hasFocusSinger = false;
    private FollowAdapter followAdapter;
    private RecommendDayVideoAdapter recommendDayVideoAdapter;
    private FocusSingerAdapter focusSingerAdapter;
    private TextTopicAdapter textTopicAdapter;

    public DiscoveryAdapter(List<TopicTypeListBean.TopicListBean> list, RefreshRecyclerView refreshView) {
        super(list, refreshView);
        iconSize = DeviceUtils.dip2px(refreshView.getContext(), 14);
        viewPool = new RecyclerView.RecycledViewPool();
        setItemTypes(new ArrayList<Integer>() {{
//            add(TYPE_FOLLOWS);
            add(TYPE_RECOMMEND_VIDEO);
            add(TYPE_FOCUS_SINGER);
        }});
    }

    @Override
    public BaseRecyclerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_RECOMMEND_VIDEO) {
//            return new FollowUserPanelViewHolder(parent, R.layout.item_follow_user_panel);
            return new RecommendVideoViewHolder(parent, R.layout.item_recomend_video);
        } else if (viewType == TYPE_FOCUS_SINGER) {
            return new FocusingViewHolder(parent, R.layout.item_focusing_singer_list);
        } else {
            DiscoveryViewHolder holder = new DiscoveryViewHolder(parent, R.layout.item_discovery);
            holder.rvContainer.setRecycledViewPool(viewPool);
            return holder;
        }
    }

    @Override
    public void onBindHolder(BaseRecyclerViewHolder h, final int position, TopicTypeListBean.TopicListBean data) {
        if (data == null) {
            return;
        }
        final TopicTypeListBean.TopicListBean bean = data;
        if (h instanceof RecommendVideoViewHolder) {
            final RecommendVideoViewHolder holder = (RecommendVideoViewHolder) h;
            LinearLayoutManager layoutManager = new LinearLayoutManager(holder.flRecycler.getContext(), LinearLayoutManager.HORIZONTAL, false);
            holder.flRecycler.setLayoutManager(layoutManager);
            recommendDayVideoAdapter = new RecommendDayVideoAdapter(data.day_recommend, onPictureClickListener, layoutManager);
            holder.flRecycler.setAdapter(recommendDayVideoAdapter);
            ScalableCardHelper scalableCardHelper = new ScalableCardHelper();
            scalableCardHelper.attachToRecyclerView(holder.flRecycler);
            holder.flRecycler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(data.day_recommend==null)
                        return;
                    holder.flRecycler.scrollToPosition((int) (RecommendDayVideoAdapter.MAX_RATIO * (data.day_recommend.size() == 1 ? 0.5f : 1)));
                    holder.flRecycler.smoothScrollBy(DeviceUtils.dip2px(holder.flRecycler.getContext(), 4), 0);
                }
            }, 50);
        } else if (h instanceof FocusingViewHolder) {//聚焦
            FocusingViewHolder holder = (FocusingViewHolder) h;
            LinearLayoutManager layoutManager = new LinearLayoutManager(holder.focusSingerRecycler.getContext(), LinearLayoutManager.HORIZONTAL, false);
            holder.focusSingerRecycler.setLayoutManager(layoutManager);
            focusSingerAdapter = new FocusSingerAdapter(data.discover_focus, onPictureClickListener);
            holder.focusSingerRecycler.setAdapter(focusSingerAdapter);
        } else if (h instanceof FollowUserPanelViewHolder) {
            final FollowUserPanelViewHolder holder = (FollowUserPanelViewHolder) h;
            followAdapter = new FollowAdapter(data.recommend_users);
            holder.rvFollows.setLayoutManager(new LinearLayoutManager(holder.rvFollows.getContext(), LinearLayoutManager.HORIZONTAL, false));
            holder.rvFollows.setAdapter(followAdapter);
            followAdapter.setDiscoverClickListener(onPictureClickListener);
            holder.tvMore.setOnClickListener(new MyClickListener() {
                @Override
                protected void onMyClick(View v) {
                    if (onPictureClickListener != null) {
                        onPictureClickListener.onMoreFollowClick();
                    }
                }
            });
            holder.rvFollows.smoothScrollBy(DeviceUtils.dip2px(holder.rvFollows.getContext(), 10), 0);
        } else if (h instanceof DiscoveryViewHolder) {
            DiscoveryViewHolder holder = (DiscoveryViewHolder) h;
//        holder.tvCount.setText(String.valueOf(bean.getOpus_count()));
            int drawableId = 0;
            if (!TextUtils.isEmpty(bean.getTopic_type())) {
                switch (bean.getTopic_type()) {
                    case "Topic":
//                        holder.ivTopicType.setTextColor(holder.tvTopicType.getResources().getColor(R.color.hh_color_g));
                        drawableId = R.drawable.icon_discover_topic;
//                        holder.tvTopicType.setText(R.string.stringTopic);
//                        holder.ivTopicType.setText("");
                        break;
                    case "Music":
//                        holder.ivTopicType.setTextColor(holder.tvTopicType.getResources().getColor(R.color.hh_color_f));
                        drawableId = R.drawable.icon_find_music;
//                        holder.ivTopicType.setText(R.string.stringMusic);
                        break;
                    default:
                        break;
                }
                holder.llTypePanel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != onPictureClickListener && !TextUtils.isEmpty(bean.getTopic_type())) {
                            switch (bean.getTopic_type()) {
                                case "Topic":
                                    onPictureClickListener.onTopicClick(position, true, bean.getId());
                                    break;
                                case "Music":
                                    onPictureClickListener.onTopicClick(position, false, bean.getDefault_music_id());
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                });
            }

            //纯文字话题
            holder.tvLabel.setVisibility(bean.isFirst ? View.VISIBLE : View.GONE);
            if (bean.isFirst) {
                showTextTopicView(bean, holder);
            } else {
                holder.allTopicsRv.setVisibility(View.GONE);
            }

            //带图片话题
            holder.ivTopicType.setImageResource(drawableId);
            holder.tvTopicName.setText(bean.getName());
            showPicTopicView(bean, holder);
        }
    }

    @Override
    public int getItemType(int position) {
        if (position == 0 && hasRecommendVideo) {
            return TYPE_RECOMMEND_VIDEO;
        } else if (position == 1 && hasFocusSinger) {
            return TYPE_FOCUS_SINGER;
        } else {
            return TYPE_CONTENT;
        }
    }

    public void refreshFollow(int position, long userId, boolean isFollowed) {
        if (getDataContainer().get(0) != null && !CollectionUtil.isEmpty(getDataContainer().get(0).recommend_users)) {
            List<UserInfo> list = getDataContainer().get(0).recommend_users;
            if (position > -1 && position < list.size()) {
                UserInfo userEntity = list.get(position);
                if (userEntity.getId() == userId) {
                    if (userEntity.getRelation() == null) {
                        userEntity.setRelation(new UserHomeRelation());
                    }
                    userEntity.setFollowed(isFollowed);
                    if (followAdapter != null) {
                        followAdapter.notifyItemChanged(position);
                    }
//                    notifyItemChanged(hasHeader() ? 1 : 0);
                }
            }
        }
    }

    /**
     * 显示文字话题view
     *
     * @param bean
     * @param holder
     */
    public void showTextTopicView(TopicTypeListBean.TopicListBean bean, DiscoveryViewHolder holder) {
        List<TopicTypeListBean.TopicListBean> topicListBeanList = bean.text_topic;
        holder.allTopicsRv.setVisibility(View.VISIBLE);
        if (!CollectionUtil.isEmpty(topicListBeanList)) {
            if (topicListBeanList.size() > 4) {
                textTopicAdapter = new TextTopicAdapter(topicListBeanList.subList(0, 4), onPictureClickListener);
            } else {
                textTopicAdapter = new TextTopicAdapter(topicListBeanList, onPictureClickListener);
            }
            GridLayoutManager layoutManager = new GridLayoutManager(holder.allTopicsRv.getContext(), 2);
            holder.allTopicsRv.setLayoutManager(layoutManager);
            holder.allTopicsRv.setAdapter(textTopicAdapter);
            textTopicAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 展示带图片的话题列表
     *
     * @param bean
     * @param holder
     */
    public void showPicTopicView(TopicTypeListBean.TopicListBean bean, DiscoveryViewHolder holder) {
        if (!CollectionUtil.isEmpty(bean.getTopic_opus_list())) {
            holder.rvContainer.setVisibility(View.VISIBLE);
            List<ShortVideoItem> subList = new ArrayList<>(11);
            subList.addAll(bean.getTopic_opus_list());
            if (subList.size() > 10) {
                subList = subList.subList(0, 9);
            }
            if (subList.size() > 9) {
                subList.add(null);
            }
            holder.rvContainer.setLayoutManager(new LinearLayoutManager(holder.rvContainer.getContext(), LinearLayoutManager.HORIZONTAL, false));
            holder.rvContainer.setAdapter(new DiscoverImageAdapter(subList, bean, onPictureClickListener));
            holder.rvContainer.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == 0) {
                        Fresco.getImagePipeline().resume();
                    } else {
                        Fresco.getImagePipeline().pause();
                    }
                }
            });
        } else {
            holder.rvContainer.setVisibility(View.GONE);
        }
    }

    //刷新评论数量
    public void refreshCommentCount(RefreshCommentBean bean) {
        if (bean != null && recommendDayVideoAdapter != null) {
            recommendDayVideoAdapter.refreshCommentCount(bean);
        }
    }

    //刷新点赞
    public void refreshLike(long videoId, ShortVideoLoveResponse bean) {
        if (bean != null && recommendDayVideoAdapter != null) {
            recommendDayVideoAdapter.refreshLike(videoId, bean);
        }
    }

    public void addDatas(boolean isRefresh, List<TopicTypeListBean.TopicListBean> list, boolean hasMore, boolean hasRecommendVideo, boolean hasFocusSinger) {
        if (isRefresh) {
            this.hasRecommendVideo = hasRecommendVideo;
            this.hasFocusSinger = hasFocusSinger;
        }
        addData(isRefresh, list, hasMore);
    }

    public void setOnPictureClickListener(OnDiscoverClickListener onPictureClickListener) {
        this.onPictureClickListener = onPictureClickListener;
    }

    public interface OnDiscoverClickListener {
        void onPictureClick(long topicId, TopicTypeListBean.TopicListBean topicListBean, List<ShortVideoItem> listBean, long videoId, int position);

        void onTopicClick(int position, boolean isTopic, long id);

        void onAvatarClick(long userId);

        void onFollowClick(long userId, int position, boolean isFollowed);

        void onMoreFollowClick();

        void onJoinClick(ShortVideoItem item);

        void onFocusClick(FocusBean focusBean);

    }

}
