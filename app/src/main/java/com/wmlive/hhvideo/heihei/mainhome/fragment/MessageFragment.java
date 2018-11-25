package com.wmlive.hhvideo.heihei.mainhome.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.wmlive.hhvideo.common.DcRouter;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.BaseCompatActivity;
import com.wmlive.hhvideo.common.base.DcBaseFragment;
import com.wmlive.hhvideo.common.manager.DcIjkPlayerManager;
import com.wmlive.hhvideo.common.manager.greendao.GreenDaoManager;
import com.wmlive.hhvideo.common.manager.message.MessageManager;
import com.wmlive.hhvideo.heihei.beans.discovery.BannerListBean;
import com.wmlive.hhvideo.heihei.beans.discovery.FollowUserResponseEntity;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.beans.main.VideoCommentResponse;
import com.wmlive.hhvideo.heihei.db.Conversation;
import com.wmlive.hhvideo.heihei.db.MessageDetail;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.mainhome.activity.ContactActivity;
import com.wmlive.hhvideo.heihei.mainhome.adapter.MessageAdapter;
import com.wmlive.hhvideo.heihei.mainhome.presenter.CommentPresenter;
import com.wmlive.hhvideo.heihei.mainhome.presenter.FollowUserPresenter;
import com.wmlive.hhvideo.heihei.mainhome.presenter.ImBannerPresenter;
import com.wmlive.hhvideo.heihei.mainhome.widget.MessageTabPanel;
import com.wmlive.hhvideo.heihei.mainhome.widget.swipe.MessageRecyclerView;
import com.wmlive.hhvideo.heihei.message.activity.IMMessageActivity;
import com.wmlive.hhvideo.heihei.personal.activity.UserHomeActivity;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.MyClickListener;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.utils.observer.DcObserver;
import com.wmlive.hhvideo.widget.CustomFontTextView;
import com.wmlive.hhvideo.widget.dialog.CommentDialog;
import com.wmlive.networklib.entity.EventEntity;
import com.wmlive.networklib.util.EventHelper;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lsq on 2/8/2018.10:49 AM
 *
 * @author lsq
 * @describe 新的消息页面
 */

public class MessageFragment extends DcBaseFragment implements
        MessageTabPanel.OnMessageTabSelectListener,
        FollowUserPresenter.IFollowUserView,
        ImBannerPresenter.IImBanner, SwipeMenuRecyclerView.LoadMoreListener, CommentPresenter.ICommentView {
    private static final byte TYPE_NONE = 0;
    private static final byte TYPE_CHAT = 1;
    private static final byte TYPE_PRODUCT = 2;//评论
    private static final byte TYPE_FOLLOW = 3;
    private static final byte TYPE_LIKE = 4;
    private static final int PAGE_SIZE = 12;

    @BindView(R.id.messageTabPanel)
    MessageTabPanel messageTabPanel;
    @BindView(R.id.rvChat)
    MessageRecyclerView rvChat;
    @BindView(R.id.rvProduct)
    MessageRecyclerView rvProduct;
    @BindView(R.id.rvFans)
    MessageRecyclerView rvFollow;
    @BindView(R.id.rvLike)
    MessageRecyclerView rvLike;
    @BindView(R.id.llEmpty)
    LinearLayout llEmpty;
    @BindView(R.id.fl_attention)
    FrameLayout fl_attention;
    @BindView(R.id.fr_replace)
    FrameLayout fr_replace;
    @BindView(R.id.ll_publish)
    LinearLayout ll_publish;
    @BindView(R.id.iv_photo_pub)
    ImageView iv_photo_pub;
    @BindView(R.id.tv_publish_note)
    TextView tv_publish_note;
    @BindView(R.id.tv_progress)
    CustomFontTextView tv_progress;

    @Override
    protected int getBaseLayoutId() {
        return super.getBaseLayoutId();
    }

    @BindView(R.id.pb_publishing)
    ProgressBar pb_publishing;
    private ImBannerPresenter bannerPresenter;
    private MessageAdapter chatAdapter;
    private MessageAdapter productAdapter;
    private MessageAdapter followAdapter;
    private MessageAdapter likeAdapter;
    private byte rvType = TYPE_NONE;
    private FollowUserPresenter followUserPresenter;
    private RecommendFragment followFragment;
    private FragmentTransaction fragmentTransaction;

    public static MessageFragment newInstance() {
        MessageFragment fragment = new MessageFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(LAZY_MODE, true);
        bundle.putBoolean(SINGLE_MODE, true);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_message;
    }


    @Override
    protected void initData() {
        super.initData();
        setTitle("", true);
        EventHelper.register(this);
        bannerPresenter = new ImBannerPresenter(this);
        ImageView ivContact = new ImageView(getContext());
        ivContact.setImageResource(R.drawable.icon_message_address);
        int padding = DeviceUtils.dip2px(getActivity(), 8);
        ivContact.setPadding(padding, 0, padding, 0);
        toolbar.addRightView(ivContact, new MyClickListener() {
            @Override
            protected void onMyClick(View v) {
                if (AccountUtil.isLogin()) {
                    startActivity(new Intent(getActivity(), ContactActivity.class));
                } else {
                    showReLogin();
                }
            }
        });
        followUserPresenter = new FollowUserPresenter(this);
        messageTabPanel.setOnMessageTabSelectListener(this);
        addPresenter(bannerPresenter, followUserPresenter);
        rvChat.setEnabled(false);
        rvProduct.setEnabled(false);
        rvFollow.setEnabled(false);
        rvLike.setEnabled(false);
        rvChat.setSwipeMenuCreator(swipeMenuCreator);
        rvChat.setSwipeMenuItemClickListener(menuItemClickListener);
        rvProduct.setLoadMoreListener(this);
        rvFollow.setLoadMoreListener(this);
        rvLike.setLoadMoreListener(this);
        rvType = TYPE_CHAT;
        loadChatData();
        followFragment = RecommendFragment.newInstance(RecommendFragment.TYPE_FOLLOW);
        FragmentManager childFragmentManager = getChildFragmentManager();
        fragmentTransaction = childFragmentManager.beginTransaction().add(R.id.fl_attention, followFragment);
        fragmentTransaction.commit();
        getWeakHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bannerPresenter.getImBanner();
            }
        }, 1000);
    }


    @Override
    protected void onSingleClick(View v) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (GlobalParams.StaticVariable.ispublishing) {
            ll_publish.setVisibility(View.VISIBLE);
            if (RecordManager.get().getProductEntity() != null)
                Glide.with(this).load(RecordManager.get().getProductEntity().coverPath).into(iv_photo_pub);
        } else {
            ll_publish.setVisibility(View.GONE);
        }
        Log.d("发布进度", "onResume: GlobalParams.StaticVariable.ispublishing==" + GlobalParams.StaticVariable.ispublishing);
        if (chatAdapter != null) {
            List<MessageDetail> dataList = chatAdapter.getDataList();
            if (!CollectionUtil.isEmpty(dataList)) {
                for (MessageDetail messageDetail : dataList) {
                    if (messageDetail != null) {
                        messageDetail.unreadCount = MessageManager.get().queryConversationUnreadCount(
                                AccountUtil.getUserId(),
                                messageDetail.fromUserId,
                                messageDetail.toUserId,
                                System.currentTimeMillis());
                    }
                }
                chatAdapter.refreshAllItemMessageCount();
            }
        }
        setMessageCount();
        showEmpty();
    }

    @Override
    public void onTabSelect1() {
        fr_replace.setVisibility(View.GONE);
        fl_attention.setVisibility(View.VISIBLE);
        fragmentTransaction.show(followFragment);
        followFragment.getView().setVisibility(View.VISIBLE);
        followFragment.resumePlay();
    }

    public void changePage() {
        fr_replace.setVisibility(View.VISIBLE);
        fl_attention.setVisibility(View.GONE);
        DcIjkPlayerManager.get().pausePlay();
        fragmentTransaction.hide(followFragment);
        followFragment.getView().setVisibility(View.INVISIBLE);
    }

    @Override
    public void onTabSelect2() {//私信聊天
        changePage();
        rvType = TYPE_CHAT;
        rvChat.setVisibility(View.VISIBLE);
        rvProduct.setVisibility(View.INVISIBLE);
        rvFollow.setVisibility(View.INVISIBLE);
        rvLike.setVisibility(View.INVISIBLE);
        showEmpty();
        setMessageCount();
        bannerPresenter.getImBanner();
    }

    @Override
    public void onTabSelect3() {//评论
        changePage();
        rvType = TYPE_PRODUCT;
        if (productAdapter == null) {
            productAdapter = new MessageAdapter(new ArrayList<>(6));
            productAdapter.setMessageClickListener(messageClickListener);
            rvProduct.setAdapter(productAdapter);
            loadProductData(true, System.currentTimeMillis());
        } else {
            MessageManager.get().updateMessageRead(productAdapter.getDataList());
            showEmpty();
            setMessageCount();
        }
        rvChat.setVisibility(View.INVISIBLE);
        rvProduct.setVisibility(View.VISIBLE);
        rvFollow.setVisibility(View.INVISIBLE);
        rvLike.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onTabSelect4() {//粉丝和点赞
        changePage();
        rvType = TYPE_LIKE;
        if (likeAdapter == null) {
            likeAdapter = new MessageAdapter(new ArrayList<>(6));
            likeAdapter.setMessageClickListener(messageClickListener);
            rvLike.setAdapter(likeAdapter);
            loadLikeData(true, System.currentTimeMillis());
        } else {
            MessageManager.get().updateMessageRead(likeAdapter.getDataList());
            showEmpty();
            setMessageCount();
        }
        rvChat.setVisibility(View.INVISIBLE);
        rvProduct.setVisibility(View.INVISIBLE);
        rvFollow.setVisibility(View.INVISIBLE);
        rvLike.setVisibility(View.VISIBLE);
    }

    private void loadChatData() {
        chatAdapter = new MessageAdapter(new ArrayList<>(4), true, true);
        chatAdapter.setMessageClickListener(messageClickListener);
        rvChat.setAdapter(chatAdapter);
        Observable.just(1)
                .subscribeOn(Schedulers.io())
                .map(new Function<Integer, List<Conversation>>() {
                    @Override
                    public List<Conversation> apply(Integer integer) throws Exception {
                        KLog.i("====首次查询会话列表");
                        return MessageManager.get().queryAllConversation(AccountUtil.getUserId(), System.currentTimeMillis());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Conversation>>() {
                    @Override
                    public void accept(List<Conversation> conversations) throws Exception {
                        KLog.i("====首次查询到会话数量：" + conversations.size());
                        refreshConversation(conversations);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        showEmpty();
                        setMessageCount();
                        KLog.e("======loadChatData出错：" + throwable.getMessage());
                    }
                });
    }

    private void loadProductData(boolean isRefresh, long startTimestamp) {
        if (productAdapter != null) {
            Observable.just(1)
                    .subscribeOn(Schedulers.io())
                    .map(new Function<Integer, List<MessageDetail>>() {
                        @Override
                        public List<MessageDetail> apply(Integer integer) throws Exception {
                            return MessageManager.get().queryProductMessage(AccountUtil.getUserId(),
                                    startTimestamp, PAGE_SIZE, MessageDetail.IM_STATUS_NONE);
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<MessageDetail>>() {
                        @Override
                        public void accept(List<MessageDetail> messageDetailList) throws Exception {
                            MessageManager.get().updateMessageRead(messageDetailList);
                            productAdapter.addData(isRefresh, messageDetailList);
                            rvProduct.loadMoreFinish(messageDetailList.size() == PAGE_SIZE);
                            showEmpty();
                            setMessageCount();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            KLog.e("======loadProductData出错：" + throwable.getMessage());
                            rvProduct.loadMoreFinish(false);
                            showEmpty();
                        }
                    });
        }
    }

//    private void loadFollowData(boolean isRefresh, long startTimestamp) {
//        if (followAdapter != null) {
//            Observable.just(1)
//                    .subscribeOn(Schedulers.io())
//                    .map(new Function<Integer, List<MessageDetail>>() {
//                        @Override
//                        public List<MessageDetail> apply(Integer integer) throws Exception {
//                            return MessageManager.get().queryFansMessage(AccountUtil.getUserId(),
//                                    startTimestamp, PAGE_SIZE, MessageDetail.IM_STATUS_NONE);
//                        }
//                    })
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Consumer<List<MessageDetail>>() {
//                        @Override
//                        public void accept(List<MessageDetail> messageDetailList) throws Exception {
//                            MessageManager.get().updateMessageRead(messageDetailList);
//                            followAdapter.addData(isRefresh, messageDetailList);
//                            rvFollow.loadMoreFinish(messageDetailList.size() == PAGE_SIZE);
//                            showEmpty();
//                            setMessageCount();
//                        }
//                    }, new Consumer<Throwable>() {
//                        @Override
//                        public void accept(Throwable throwable) throws Exception {
//                            KLog.e("======loadProductData出错：" + throwable.getMessage());
//                            rvFollow.loadMoreFinish(false);
//                            showEmpty();
//                        }
//                    });
//        }
//    }

    private void loadLikeData(boolean isRefresh, long startTimestamp) {
        if (likeAdapter != null) {
            Observable.just(1)
                    .subscribeOn(Schedulers.io())
                    .map(new Function<Integer, List<MessageDetail>>() {
                        @Override
                        public List<MessageDetail> apply(Integer integer) throws Exception {
                            return MessageManager.get().queryLikeMessage(AccountUtil.getUserId(),
                                    startTimestamp, PAGE_SIZE, MessageDetail.IM_STATUS_NONE);
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<MessageDetail>>() {
                        @Override
                        public void accept(List<MessageDetail> messageDetailList) throws Exception {
                            MessageManager.get().updateMessageRead(messageDetailList);
                            likeAdapter.addData(isRefresh, messageDetailList);
                            rvLike.loadMoreFinish(messageDetailList.size() == PAGE_SIZE);
                            showEmpty();
                            setMessageCount();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            KLog.e("======loadLikeData出错：" + throwable.getMessage());
                            rvLike.loadMoreFinish(false);
                            showEmpty();
                        }
                    });
        }
    }

    /**
     * 非私信消息
     */
    public void receiveSystemMessage(List<MessageDetail> productList,
                                     List<MessageDetail> followList,
                                     List<MessageDetail> likeList) {
        List<MessageDetail> updateMessageList = new ArrayList<>(6);
        if (productAdapter != null && !CollectionUtil.isEmpty(productList)) {
            Collections.sort(productList);
            productAdapter.addData(0, productList);
//            rvProduct.scrollToPosition(0);
        }

//        if (followAdapter != null && !CollectionUtil.isEmpty(followList)) {
//            Collections.sort(followList);
//            followAdapter.addData(0, followList);
////            rvFollow.scrollToPosition(0);
//        }

        if (likeAdapter != null && !CollectionUtil.isEmpty(likeList)) {
            Collections.sort(likeList);
            likeAdapter.addData(0, likeList);
//            rvLike.scrollToPosition(0);
        }

        showEmpty();

        if (rvType == TYPE_PRODUCT) {//刷新评论页面
            updateState(updateMessageList, productList);
        }
        if (rvType == TYPE_FOLLOW) {
            updateState(updateMessageList, followList);
        }
        if (rvType == TYPE_LIKE) {
            updateState(updateMessageList, likeList);
        }
        if (!CollectionUtil.isEmpty(updateMessageList)) {
            MessageManager.get().updateMessageInfo(updateMessageList);
        }
        setMessageCount();
    }

    private void updateState(List<MessageDetail> updateMessageList, List<MessageDetail> messageList) {
        if (!CollectionUtil.isEmpty(messageList)) {
            for (MessageDetail messageDetail : messageList) {
                if (messageDetail != null) {
                    messageDetail.setStatus(MessageDetail.IM_STATUS_READ);
                }
            }
            updateMessageList.addAll(messageList);
        }
    }

    private void setMessageCount() {
        long startTime = System.currentTimeMillis();
        long chatUnreadCount = MessageManager.get().queryAllConversationUnreadCount(AccountUtil.getUserId(), startTime);
        long productUnreadCount = MessageManager.get().queryProductUnreadCount(AccountUtil.getUserId(),
                startTime, MessageDetail.IM_STATUS_UNREAD);
        long followUnreadCount = MessageManager.get().queryFansUnreadCount(startTime);
        long likeUnreadCount = MessageManager.get().queryLikeUnreadCount(startTime);
        KLog.i("======chatUnreadCount:" + chatUnreadCount +
                " productUnreadCount:" + productUnreadCount +
                " followUnreadCount:" + followUnreadCount +
                " likeUnreadCount:" + likeUnreadCount);
        if (messageTabPanel != null) {
            messageTabPanel.setMessageCount(1, chatUnreadCount);
            messageTabPanel.setMessageCount(2, productUnreadCount);
            messageTabPanel.setMessageCount(3, likeUnreadCount + followUnreadCount);//点赞和粉丝合并
        }
        EventHelper.post(GlobalParams.EventType.TYPE_REFRESH_HOME_IM_COUNT,
                chatUnreadCount + productUnreadCount + followUnreadCount + likeUnreadCount);
    }

    /**
     * 刷新会话列表
     */
    public void refreshConversation(List<Conversation> conversations) {
        List<MessageDetail> dataList;
        List<MessageDetail> latestMessageList = new ArrayList<>(2);
        MessageDetail latestMessage;
        long unreadCount;
        for (Conversation con : conversations) {
            if (con != null) {
                dataList = chatAdapter.getDataList();
                latestMessage = MessageManager.get().queryLatestConversationMessage(AccountUtil.getUserId(),
                        con.fromUserId, con.toUserId, System.currentTimeMillis());
                unreadCount = MessageManager.get().queryConversationUnreadCount(AccountUtil.getUserId(),
                        con.fromUserId, con.toUserId, System.currentTimeMillis());
                KLog.i("=====query加载消息：" + latestMessage);
                if (!CollectionUtil.isEmpty(dataList)) {
                    boolean exist = false;
                    int position = 0;
                    MessageDetail message = null;
                    for (int i = 0, size = dataList.size(); i < size; i++) {
                        message = dataList.get(i);
                        if (message != null) {
                            if ((con.fromUserId == message.fromUserId && con.toUserId == message.toUserId)
                                    || (con.fromUserId == message.toUserId && con.toUserId == message.fromUserId)) {
                                //会话存在
                                exist = true;
                                position = i;
                                break;
                            }
                        }
                    }

                    if (exist) {
                        //会话存在，更新该位置会话
                        KLog.i("======列表会话存在，更新该位置会话");
                        if (latestMessage != null) {
                            latestMessage.unreadCount = unreadCount;
                            dataList.set(position, latestMessage);
                            chatAdapter.refreshItemMessage(position);
                            rvChat.scrollToPosition(0);
                            con.createTime = latestMessage.create_time;
                            GreenDaoManager.get().getConversationDao().update(con);
                        } else {
                            GreenDaoManager.get().getConversationDao().delete(con);
                        }
                    } else {
                        //会话不存在，添加新的会话
                        KLog.i("======列表会话不存在，添加新会话");
                        if (latestMessage != null) {
                            latestMessage.unreadCount = unreadCount;
                            latestMessageList.add(latestMessage);
                            con.createTime = latestMessage.create_time;
                            con.belongUserId = AccountUtil.getUserId();
                            GreenDaoManager.get().getConversationDao().update(con);
                        }
                    }

                } else {
                    //会话不存在，添加新的会话
                    KLog.i("======列表为空，添加新会话");
                    if (latestMessage != null) {
                        latestMessage.unreadCount = unreadCount;
                        latestMessageList.add(latestMessage);
                        con.createTime = latestMessage.create_time;
                        con.belongUserId = AccountUtil.getUserId();
                        GreenDaoManager.get().getConversationDao().update(con);
                    }
                }
            }
        }
        if (!CollectionUtil.isEmpty(latestMessageList)) {
            Collections.sort(latestMessageList);
            chatAdapter.addData(0, latestMessageList);
            rvChat.scrollToPosition(0);
        }
        showEmpty();
        setMessageCount();


    }

    @Override
    public void onFollowUserOk(boolean isRecommendFollow, boolean isFollowAll, int position, long userId, long videoId, boolean isFollowed) {
        if (isFollowed) {
            showToast(R.string.user_follower);
        }
        likeAdapter.refreshItemFollow(position, isFollowed);
    }

    public void refreshFollowList(long userId, boolean isFollowed) {
        if (followAdapter != null && !CollectionUtil.isEmpty(followAdapter.getDataList())) {
            MessageDetail messageDetail;
            for (int i = 0; i < followAdapter.getDataList().size(); i++) {
                messageDetail = followAdapter.getDataList().get(i);
                if (messageDetail != null) {
                    if (messageDetail.fromUserId == userId) {
                        followAdapter.refreshItemFollow(i, isFollowed);
                        KLog.i("======需要刷新的关注位置：" + i);
                    }
                }
            }
        }
    }

    private void showEmpty() {
        if (rvType == TYPE_CHAT) {
            chatAdapter.refreshEmpty();
            if (llEmpty != null) {
                llEmpty.setVisibility(View.GONE);
            }
        } else {
            boolean show = (rvType == TYPE_PRODUCT && CollectionUtil.isEmpty(productAdapter.getDataList()))
                    || (rvType == TYPE_FOLLOW && CollectionUtil.isEmpty(followAdapter.getDataList()))
                    || (rvType == TYPE_LIKE && CollectionUtil.isEmpty(likeAdapter.getDataList()));
            if (llEmpty != null) {
                llEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        }
    }

    //刷新数据库中个人信息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshUserEvent(EventEntity eventEntity) {
        if (eventEntity.code == GlobalParams.EventType.TYPE_UPDATE_USER_INFO) {
            if (AccountUtil.isLogin()) {
                if (eventEntity.data != null && eventEntity.data instanceof UserInfo) {
                    UserInfo userInfo = (UserInfo) eventEntity.data;
                    KLog.i("=====收到需要更新个人信息的事件，当前登录id：" + AccountUtil.getUserId() + " ,需要更新的id：" + userInfo.getId() + ",userInfo：" + userInfo.toString());
                    if (!AccountUtil.isLoginUser(userInfo.getId())) {
                        Observable.just(1)
                                .subscribeOn(Schedulers.io())
                                .map(new Function<Integer, Boolean>() {
                                    @Override
                                    public Boolean apply(Integer integer) throws Exception {
                                        List<MessageDetail> messageList = MessageManager.get().queryUserMessage(userInfo.getId());
                                        if (!CollectionUtil.isEmpty(messageList)) {
                                            List<MessageDetail> needUpdateList = new ArrayList<>(2);
                                            for (MessageDetail messageDetail : messageList) {
                                                if (messageDetail != null) {
                                                    if (userInfo.getId() == messageDetail.fromUserId && messageDetail.from_user != null) {
                                                        messageDetail.from_user = userInfo;
                                                        needUpdateList.add(messageDetail);
                                                    }
                                                    if (userInfo.getId() == messageDetail.toUserId && messageDetail.to_user != null) {
                                                        messageDetail.to_user = userInfo;
                                                        needUpdateList.add(messageDetail);
                                                    }
                                                }
                                            }
                                            KLog.i("=====需要更新的数量是：" + needUpdateList.size());
                                            if (!CollectionUtil.isEmpty(needUpdateList)) {
                                                MessageManager.get().updateMessageInfo(needUpdateList);
                                                //如果太多了，这里不做页面更新
                                            }
                                        }
                                        return true;
                                    }
                                })
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new DcObserver<>());
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(EventEntity eventEntity) {
        if (eventEntity.code == GlobalParams.EventType.TYPE_FOLLOW_OK) {
            if (eventEntity.data != null && eventEntity.data instanceof FollowUserResponseEntity) {
                FollowUserResponseEntity followUserEntity = (FollowUserResponseEntity) eventEntity.data;
                Observable.just(1)
                        .subscribeOn(Schedulers.io())
                        .map(new Function<Integer, Boolean>() {
                            @Override
                            public Boolean apply(Integer integer) throws Exception {
                                List<MessageDetail> messageList = MessageManager.get().queryUserMessage(followUserEntity.userId);
                                if (!CollectionUtil.isEmpty(messageList)) {
                                    List<MessageDetail> needUpdateList = new ArrayList<>(2);
                                    for (MessageDetail messageDetail : messageList) {
                                        if (messageDetail != null) {
                                            if (followUserEntity.userId == messageDetail.fromUserId && messageDetail.from_user != null) {
                                                messageDetail.from_user.setFollowed(followUserEntity.is_follow);
                                                messageDetail.fromUser = JSON.toJSONString(messageDetail.from_user);
                                                needUpdateList.add(messageDetail);
                                            }
                                            if (followUserEntity.userId == messageDetail.toUserId && messageDetail.to_user != null) {
                                                messageDetail.to_user.setFollowed(followUserEntity.is_follow);
                                                messageDetail.toUser = JSON.toJSONString(messageDetail.to_user);
                                                needUpdateList.add(messageDetail);
                                            }
                                        }
                                    }
                                    KLog.i("=====需要更新的数量是：" + needUpdateList.size());
                                    if (!CollectionUtil.isEmpty(needUpdateList)) {
                                        MessageManager.get().updateMessageInfo(needUpdateList);
                                    }
                                }
                                return true;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                refreshFollowList(followUserEntity.userId, followUserEntity.is_follow);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                KLog.e("=====onFollowEvent出错：" + throwable.getMessage());
                            }
                        });
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginOk(EventEntity eventEntity) {
        if (eventEntity != null && eventEntity.code == GlobalParams.EventType.TYPE_LOGIN_OK) {
            KLog.i("=====登录成功");
            loadChatData();
            loadProductData(true, System.currentTimeMillis());
            loadLikeData(true, System.currentTimeMillis());
            setMessageCount();
            if (bannerPresenter != null) {
                bannerPresenter.getImBanner();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void publishEvent(EventEntity eventEntity) {
        if (eventEntity.code == GlobalParams.EventType.TYPE_PUBLISH_START) {
            ll_publish.setVisibility(View.VISIBLE);
            Glide.with(this).load(RecordManager.get().getProductEntity().coverPath).into(iv_photo_pub);
        } else if (eventEntity.code == GlobalParams.EventType.TYPE_PUBLISH_PROGRESS) {
            if (GlobalParams.StaticVariable.ispublishing) {
                ll_publish.setVisibility(View.VISIBLE);
                tv_publish_note.setText(getString(R.string.publishing));
//            Glide.with(this).load(RecordManager.get().getProductEntity().coverPath).into(iv_photo_pub);
                pb_publishing.setProgress((int) eventEntity.data);
                tv_progress.setText((int) eventEntity.data + "%");
            }
        } else if (eventEntity.code == GlobalParams.EventType.TYPE_PUBLISH_FINISH) {
            ll_publish.setVisibility(View.GONE);
            followFragment.onRefresh();
        } else if (eventEntity.code == GlobalParams.EventType.TYPE_PUBLISH_ERRER) {
            ll_publish.setVisibility(View.GONE);
        } else if (eventEntity.code == GlobalParams.EventType.TYPE_PUBLISH_RETRY) {
            tv_publish_note.setText(getString(R.string.publishing_retry));
        }

    }

    private CommentPresenter commentPresenter = new CommentPresenter(this);

    private MessageAdapter.MessageClickListener messageClickListener = new MessageAdapter.MessageClickListener() {

        @Override
        public void onAvatarClick(long userId) {
            if (userId == AccountUtil.getUserId()) {
                Fragment parent = getParentFragment();
                if (parent != null && parent instanceof HomeFragment) {
                    ((HomeFragment) parent).onMineClick();
                }
            } else {
                UserHomeActivity.startUserHomeActivity(getContext(), userId);
            }
        }

        @Override
        public void onFollowClick(int dataPosition, long userId, boolean isFollowed) {
            followUserPresenter.follow(dataPosition, userId, isFollowed);
        }

        @Override
        public void onCommentClick(int dataPosition, long userId, String userName, long videoId) {
            CommentDialog commentDialog = new CommentDialog();
            String replayhint = userId > 0 ? "回复:" + userName : getResources().getString(R.string.stringLikeJustSay);
            commentDialog.showDialog(getActivity(), "", replayhint, new CommentDialog.CommentListener() {
                @Override
                public void onSendComment(String comment) {
                    commentPresenter.comment(dataPosition, videoId, comment, null, userId);
                }

                @Override
                public void onDialogDismiss(String lastMsg) {

                }
            });
            commentDialog.show(getActivity().getFragmentManager(), "CommentDialog");
        }

        @Override
        public void onVideoClick(int dataPosition, String videoDeepLink) {
            DcRouter.linkTo(getActivity(), videoDeepLink);
        }

        @Override
        public void onChatClick(int dataPosition, UserInfo userInfo) {
            if (AccountUtil.isLogin()) {
                IMMessageActivity.startIMMessageActivity((BaseCompatActivity) getActivity(), userInfo.getId(), userInfo);
            } else {
                showReLogin();
            }
        }
    };

    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            if (viewType == MessageAdapter.TYPE_MESSAGE) {
                int width = getResources().getDimensionPixelSize(R.dimen.t60dp);
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                // 添加右侧的，可以添加多个,左侧添加同理。如果不添加，则右侧不会出现菜单。
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity())
                        .setBackgroundColorResource(R.color.hh_color_b)
                        .setImage(R.drawable.icon_video_trash)
//                        .setText("删除")
//                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。
            }
        }
    };

    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private SwipeMenuItemClickListener menuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();
            int adapterPosition = menuBridge.getAdapterPosition();
            chatAdapter.deleteConversation(adapterPosition);
            showEmpty();
            setMessageCount();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventHelper.unregister(this);
    }

    @Override
    public void onImBannerOk(BannerListBean bannerList) {
        chatAdapter.refreshBanner(bannerList);
        showEmpty();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImChatMessageEvent(EventEntity eventEntity) {
        if (eventEntity != null && eventEntity.code == GlobalParams.EventType.TYPE_IM_CHAT_MSG) {
            HashSet<Long> imChatConIdSet = (HashSet<Long>) eventEntity.data;
            if (!CollectionUtil.isEmpty(imChatConIdSet)) {
                List<Conversation> conversationList = new ArrayList<>(imChatConIdSet.size());
                Conversation con;
                for (Long conId : imChatConIdSet) {
                    con = GreenDaoManager.get().getConversationDao().load(conId);
                    if (con != null) {
                        KLog.i("======需要更新的会话id：" + conId + " ,会话内容:" + con);
                        conversationList.add(con);
                    }
                }
                refreshConversation(conversationList);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImMessageEvent(EventEntity eventEntity) {
        if (eventEntity != null && eventEntity.code == GlobalParams.EventType.TYPE_IM_SYSTEM_MSG) {
            KLog.i("====收到新的系统Im消息");
            if (eventEntity.data != null && eventEntity.data instanceof List) {
                List<Long> idList = (List<Long>) eventEntity.data;
                if (!CollectionUtil.isEmpty(idList)) {
                    ArrayList<MessageDetail> messageList = new ArrayList<>(idList.size());
                    MessageDetail msg;
                    for (Long id : idList) {
                        if (id != null) {
                            msg = GreenDaoManager.get().getMessageDetailDao().load(id);
                            messageList.add(msg);
                            KLog.i("=======加载MessageDetail：" + msg);
                        }
                    }
                    List<MessageDetail> followList = new ArrayList<>(2);
                    List<MessageDetail> likeList = new ArrayList<>(2);
                    List<MessageDetail> productList = new ArrayList<>(2);
                    for (MessageDetail messageDetail : messageList) {
                        if (messageDetail != null) {
                            switch (messageDetail.getMsg_type()) {
                                case MessageDetail.TYPE_GIFT:
                                case MessageDetail.TYPE_GIFT_V2:
                                case MessageDetail.TYPE_CO_CREATE:
                                case MessageDetail.TYPE_COMMENT:
                                    productList.add(messageDetail);
                                    break;
                                case MessageDetail.TYPE_FOLLOW:
                                    likeList.add(messageDetail);
                                    break;
                                case MessageDetail.TYPE_LIKE:
                                    likeList.add(messageDetail);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    receiveSystemMessage(productList, followList, likeList);
                }
            }
        }
    }

    @Override
    public void onLoadMore() {
        if (rvType == TYPE_PRODUCT) {
            MessageDetail lastItem = productAdapter.getLastItem();
            if (lastItem != null) {
                loadProductData(false, lastItem.create_time);
            }
        }
//        if (rvType == TYPE_FOLLOW) {
//            MessageDetail lastItem = followAdapter.getLastItem();
//            if (lastItem != null) {
//                loadFollowData(false, lastItem.create_time);
//            }
//        }
        if (rvType == TYPE_LIKE) {
            MessageDetail lastItem = likeAdapter.getLastItem();
            if (lastItem != null) {
                loadLikeData(false, lastItem.create_time);
            }
        }
    }

    @Override
    public void onCommentOk(int dataPosition, VideoCommentResponse bean) {
        ToastUtil.showToast(getString(R.string.stringCommentReplyOk));
    }

    @Override
    public void onCommentFailed(String msg) {
        ToastUtil.showToast(msg);
    }
}
