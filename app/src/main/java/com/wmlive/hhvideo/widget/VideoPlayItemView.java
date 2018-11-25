package com.wmlive.hhvideo.widget;

import android.content.Context;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.heihei.beans.main.DcDanmaEntity;
import com.wmlive.hhvideo.heihei.beans.main.DcDanmaWrapEntity;
import com.wmlive.hhvideo.heihei.beans.main.RefreshCommentBean;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.heihei.mainhome.adapter.RecommendAdapter;
import com.wmlive.hhvideo.heihei.mainhome.presenter.ShortVideoViewCallback;
import com.wmlive.hhvideo.heihei.mainhome.util.BiliDanmukuParser;
import com.wmlive.hhvideo.heihei.mainhome.util.DanmakuUtil;
import com.wmlive.hhvideo.heihei.mainhome.viewholder.DamankuViewHolder;
import com.wmlive.hhvideo.heihei.mainhome.viewholder.MyImageWare;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ToastUtil;

import java.util.List;

import cn.wmlive.hhvideo.R;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.android.AndroidDisplayer;
import master.flame.danmaku.danmaku.model.android.ViewCacheStuffer;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;


/**
 * Created by lsq on 11/27/2017.3:16 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class VideoPlayItemView extends BaseVideoPlayItemView implements GestureView.GestureViewListener {
    private static final int MSG_REMOVE_GIFT_POINT = 100;
    private static final String EXTRA_POSITION = "position";
    private static final String EXTRA_GIFT_ID = "gift_id";

//    private DanmakuContext danmakuContext;
//    private BaseDanmakuParser danmakuParser;
//    private DanmakuTask danmakuTask;
//    private boolean isDanmaStarted = false;//弹幕是否已经开始了
//    private int avatarWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, DCApplication.getDCApp().getResources().getDisplayMetrics());
//    private int giftAvatarWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18f, DCApplication.getDCApp().getResources().getDisplayMetrics());
//    private int giftWidth = DeviceUtils.dip2px(DCApplication.getDCApp(), 60);

    public VideoPlayItemView(Context context) {
        super(context);
    }

    public VideoPlayItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoPlayItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void initData(int pageId, int videoType, int position, ShortVideoItem item, ShortVideoViewCallback clickListener, RecommendAdapter recommendNewAdapter) {
//        if (SPUtils.getBoolean(DCApplication.getDCApp(), SPUtils.KEY_SHOW_DAMANKU, true)) {
//            initDanmaku();
//        }
        super.initData(pageId, videoType, position, item, clickListener, recommendNewAdapter);
        setViewClickListener();
//            dvDanmaku.removeAllDanmakus(true);
//        }
    }

    //初始化弹幕库
    public void initDanmaku() {
//        KLog.i("======Danmaku==initDanmaku1:" + position);
//        isDanmaStarted = false;
//        if (dvDanmaku == null) {
//            KLog.i("======Danmaku==initDanmaku2:" + position);
//            dvDanmaku = (DanmakuView) getRootView().findViewById(R.id.dvDanmaku);
//        }
//        // 设置最大显示行数
//        HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
//        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5); // 滚动弹幕最大显示5行
//        // 设置是否禁止重叠
//        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
//        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
//        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, false);
//        danmakuContext = DanmakuContext.create();
//        danmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_NONE, 3)
//                .setDuplicateMergingEnabled(false)
//                .setScrollSpeedFactor(1.5f)
//                .setScaleTextSize(1.2f)
//                .setCacheStuffer(viewCacheStuffer, null)  // 绘制背景使用BackgroundCacheStuffer
//                .setMaximumLines(maxLinesPair)
//                .preventOverlapping(overlappingEnablePair)
//                .setDanmakuMargin(DeviceUtils.dip2px(DCApplication.getDCApp(), 65));
//        danmakuParser = createParser();
//        if (dvDanmaku != null) {
//            dvDanmaku.setCallback(new DrawHandler.Callback() {
//                @Override
//                public void prepared() {
//                    KLog.i("======Danmaku=prepared");
//                    dvDanmaku.start();
//                }
//
//                @Override
//                public void updateTimer(DanmakuTimer timer) {
//                }
//
//                @Override
//                public void danmakuShown(BaseDanmaku danmaku) {
//                    KLog.i("======Danmaku=danmakuShown");
//                }
//
//                @Override
//                public void drawingFinished() {
//                    KLog.i("======Danmaku=drawingFinished");
//                }
//            });
//            dvDanmaku.prepare(danmakuParser, danmakuContext);
//            dvDanmaku.enableDanmakuDrawingCache(true);
//        }
    }

    /**
     * 弹幕item的适配器
     */
    private ViewCacheStuffer viewCacheStuffer = new ViewCacheStuffer<DamankuViewHolder>() {

        @Override
        public DamankuViewHolder onCreateViewHolder(int viewType) {
            return new DamankuViewHolder(View.inflate(getContext(), R.layout.item_gift_damanku, null));
        }

        @Override
        public void onBindViewHolder(int viewType, DamankuViewHolder viewHolder, BaseDanmaku danmaku,
                                     AndroidDisplayer.DisplayerConfig displayerConfig, TextPaint paint) {
            if (danmaku.tag != null && danmaku.tag instanceof DcDanmaWrapEntity) {
                DcDanmaWrapEntity dcDanmaEntity = (DcDanmaWrapEntity) danmaku.tag;
                if (dcDanmaEntity.dcDanmaEntity == null) {
                    return;
                }
                //正常弹幕
                if (dcDanmaEntity.dcDanmaEntity.comm_type == 0) {
                    viewHolder.tvContent.setText(dcDanmaEntity.dcDanmaEntity.title);
                    viewHolder.tvContent.setVisibility(View.VISIBLE);
                    viewHolder.llGiftPanel.setVisibility(View.GONE);
                    viewHolder.llGiftPanel2.setVisibility(View.GONE);
                } else if (dcDanmaEntity.dcDanmaEntity.comm_type == 1) {//礼物1
                    viewHolder.tvContent.setVisibility(View.GONE);
                    viewHolder.llGiftPanel.setVisibility(View.VISIBLE);
                    viewHolder.llGiftPanel2.setVisibility(View.GONE);
                    //个人头像
                    if (null != dcDanmaEntity.dcDanmaEntity.user) {
                        viewHolder.tvName.setText(dcDanmaEntity.dcDanmaEntity.user.getName());
                        MyImageWare avatarImageWare = dcDanmaEntity.avatarImageWare;
                        if (avatarImageWare != null && avatarImageWare.bitmap != null) {
                            viewHolder.ivAvatar.setImageBitmap(avatarImageWare.bitmap);
                        }
                    }
                    //礼物部分
                    if (null != dcDanmaEntity.dcDanmaEntity.comm_text) {
                        viewHolder.tvGift.setText("送了 " + dcDanmaEntity.dcDanmaEntity.comm_text.name);
                        viewHolder.tvGiftCount.setText("X" + dcDanmaEntity.dcDanmaEntity.comm_text.count);
                        if (dcDanmaEntity.hasLocalIcon) {
                            viewHolder.ivGift.setImageURI(Uri.parse("file://" + dcDanmaEntity.localIconUri));
                        } else {
                            MyImageWare giftImageWare = dcDanmaEntity.giftImageWare;
                            if (giftImageWare != null && giftImageWare.bitmap != null) {
                                viewHolder.ivGift.setImageBitmap(giftImageWare.bitmap);
                            }
                        }
                    }
                } else if (dcDanmaEntity.dcDanmaEntity.comm_type == 2) {//礼物2
                    viewHolder.tvContent.setVisibility(View.GONE);
                    viewHolder.llGiftPanel.setVisibility(View.GONE);
                    viewHolder.llGiftPanel2.setVisibility(View.VISIBLE);

                    //头像
                    if (null != dcDanmaEntity.dcDanmaEntity.user) {
                        viewHolder.tvName2.setText(dcDanmaEntity.dcDanmaEntity.user.getName());
                        MyImageWare avatarImageWare = dcDanmaEntity.avatarImageWare;
                        if (avatarImageWare != null && avatarImageWare.bitmap != null) {
                            viewHolder.ivAvatar2.setImageBitmap(avatarImageWare.bitmap);
                        } else {
                            viewHolder.ivAvatar2.setImageResource(R.drawable.ic_default_male);
                        }
                    } else {
                        viewHolder.ivAvatar2.setImageResource(R.drawable.ic_default_male);
                    }

                    String tipsString = "送了 " + dcDanmaEntity.dcDanmaEntity.total_point + " 分贝";
                    SpannableString spanString = new SpannableString(tipsString);
                    ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color.hh_color_i));
                    spanString.setSpan(span, 3, 3 + dcDanmaEntity.dcDanmaEntity.total_point.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    spanString.setSpan(new RelativeSizeSpan(1.2f), 3, 3 + dcDanmaEntity.dcDanmaEntity.total_point.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    viewHolder.tvGift2.setText(spanString);
                    viewHolder.tvHitCount2.setVisibility(dcDanmaEntity.dcDanmaEntity.prize_point > 0 ? VISIBLE : GONE);
                    viewHolder.tvHitCount2.setText("(暴击" + dcDanmaEntity.dcDanmaEntity.prize_point + "分贝)");

                }
            } else {
                viewHolder.tvContent.setVisibility(View.GONE);
                viewHolder.llGiftPanel.setVisibility(View.GONE);
                viewHolder.llGiftPanel2.setVisibility(View.GONE);
            }
        }

        @Override
        public void prepare(BaseDanmaku danmaku, boolean fromWorkerThread) {
//            if (danmaku.isTimeOut()) {
//                return;
//            }
//            if (danmaku.tag != null && danmaku.tag instanceof DcDanmaWrapEntity) {
//                DcDanmaWrapEntity dcDanmaEntity = (DcDanmaWrapEntity) danmaku.tag;
//                MyImageWare avatarImageWare = dcDanmaEntity.avatarImageWare;
//                if (avatarImageWare == null) {
//                    if (dcDanmaEntity.dcDanmaEntity != null
//                            && dcDanmaEntity.dcDanmaEntity.user != null
//                            && !TextUtils.isEmpty(dcDanmaEntity.dcDanmaEntity.user.getCover_url())) {
//                        avatarImageWare = new MyImageWare(dcDanmaEntity.dcDanmaEntity.user.getCover_url(), danmaku, avatarWidth, avatarWidth, dvDanmaku);
//                        dcDanmaEntity.avatarImageWare = avatarImageWare;
//                    }
//                }
//                if (avatarImageWare != null) {
//                    ImageLoader.getInstance().displayImage(avatarImageWare.getImageUri(), dcDanmaEntity.avatarImageWare);
//                }
//
////                List<GiftLocalBean> list = GiftResultInstance.getInstance().getGiftLocalData();
//                if (dcDanmaEntity.dcDanmaEntity != null && dcDanmaEntity.dcDanmaEntity.comm_text != null) {
////                    for (GiftLocalBean giftLocalBean : list) {
////                        if (giftLocalBean.getId() == dcDanmaEntity.dcDanmaEntity.comm_text.id) {
////                            dcDanmaEntity.hasLocalIcon = giftLocalBean.hasLocalIcon();
////                            dcDanmaEntity.localIconUri = giftLocalBean.getIcon_url_local();
////                            KLog.i("=======找到礼物的id：" + giftLocalBean.getId());
////                            break;
////                        }
////                    }
//
//                    MyImageWare giftImageWare = dcDanmaEntity.giftImageWare;
//                    if (giftImageWare == null) {
//                        if (!TextUtils.isEmpty(dcDanmaEntity.dcDanmaEntity.comm_text.icon_url)) {
//                            giftImageWare = new MyImageWare(dcDanmaEntity.dcDanmaEntity.comm_text.icon_url, danmaku, giftAvatarWidth, giftAvatarWidth, dvDanmaku);
//                            dcDanmaEntity.giftImageWare = giftImageWare;
//                        }
//                    }
//                    if (giftImageWare != null) {
//                        ImageLoader.getInstance().displayImage(giftImageWare.getImageUri(), dcDanmaEntity.giftImageWare);
//                    }
//                }
//                danmaku.setTag(dcDanmaEntity);
//            }
        }

        @Override
        public void releaseResource(BaseDanmaku danmaku) {
            if (danmaku.tag != null && danmaku.tag instanceof DcDanmaWrapEntity) {
                DcDanmaWrapEntity dcDanmaEntity = (DcDanmaWrapEntity) danmaku.tag;
                MyImageWare avatarImageWare = dcDanmaEntity.avatarImageWare;
                if (avatarImageWare != null) {
                    ImageLoader.getInstance().cancelDisplayTask(avatarImageWare);
                }
                MyImageWare giftImageWare = dcDanmaEntity.giftImageWare;
                if (giftImageWare != null) {
                    ImageLoader.getInstance().cancelDisplayTask(giftImageWare);
                }
            }
            if (danmaku.tag != null) {
                danmaku.setTag(null);
            }
        }
    };

    private BaseDanmakuParser createParser() {
        KLog.i("======Danmaku==createParser position:" + position);
        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);
        loader.loadFromString(DanmakuUtil.createTestData());   //这是读取内存中的弹幕文字String
        BaseDanmakuParser parser = new BiliDanmukuParser();
        IDataSource<?> dataSource = loader.getDataSource();
        parser.load(dataSource);
        return parser;
    }

    //开始弹幕库
    public void startDanmaKu(final List<DcDanmaEntity> list) {
//        if (SPUtils.getBoolean(DCApplication.getDCApp(), SPUtils.KEY_SHOW_DAMANKU, true)) {
//            KLog.i("=====Danmaku=来一波弹幕isDanmaStarted:" + isDanmaStarted);
//            if (!isDanmaStarted) {
//                if (dvDanmaku == null || !dvDanmaku.isPrepared()) {
//                    initDanmaku();
//                }
//                if (dvDanmaku != null) {
//                    dvDanmaku.setVisibility(View.VISIBLE);
//                    dvDanmaku.show();
//                    danmakuTask = new DanmakuTask(list);
//                    getRootView().postDelayed(danmakuTask, 1000);
//                    isDanmaStarted = true;
//                }
//            }
//        }
    }

    @Override
    public void onSingleClick(float rawX, float rawY) {
        if (shortVideoViewCallback != null) {
            shortVideoViewCallback.onVideoClick(false, position, flPlayerContainer,
                    ivCover, shortVideoItem, 0);
        }
    }

    @Override
    public void onContinunousClick(float rawX, float rawY) {
        if (shortVideoViewCallback != null) {
            shortVideoViewCallback.onContinunousClick(position, shortVideoItem, rawX, rawY);
        }
    }

    /**
     * 添加弹幕的任务
     */
    private class DanmakuTask implements Runnable {
        private List<DcDanmaEntity> list;

        public DanmakuTask(List<DcDanmaEntity> list) {
            this.list = list;
        }

        @Override
        public void run() {
//            KLog.i("======Danmaku==startDanmaKu1");
//            if (dvDanmaku != null) {
//                KLog.i("======Danmaku==startDanmaKu1:" + dvDanmaku.isPrepared()
//                        + " isPause?" + dvDanmaku.isPaused()
//                        + " ,isShown?" + dvDanmaku.isShown() + " isVisable?" + (((DanmakuView) dvDanmaku).getVisibility() == View.VISIBLE));
//                dvDanmaku.removeAllDanmakus(true);
//                if (dvDanmaku.isPrepared()) {
//                    KLog.i("======Danmaku==startDanmaKu2");
//                    dvDanmaku.setVisibility(View.VISIBLE);
//                    DanmakuUtil.createDanmaEntity(dvDanmaku, list, danmakuContext.mDanmakuFactory, danmakuContext);
//                }
//            }
        }
    }

    //暂停弹幕库
    public void pauseDanmaKu() {
//        if (dvDanmaku != null && dvDanmaku.isPrepared()) {
//            dvDanmaku.pause();
//            KLog.i("======Danmaku==pauseDanmaKu");
//        }
    }

    //恢复弹幕库
    public void resumeDanmaku() {
//        getRootView().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (dvDanmaku != null) {
//                    KLog.i("======Danmaku==isPrepared:" + dvDanmaku.isPrepared());
//                    KLog.i("======Danmaku==isPaused:" + dvDanmaku.isPaused());
//                    if (dvDanmaku.isShown() && !dvDanmaku.isPaused()) {
//                        return;
//                    }
//                }
//                if (dvDanmaku != null && dvDanmaku.isPrepared()) {
//                    KLog.i("======Danmaku==resumeDanmaku");
//                    dvDanmaku.resume();
//                }
//            }
//        }, 200);
    }

    //释放弹幕库
    public void releaseDanmaKu(boolean fullRelease) {
//        if (danmakuTask != null) {
//            getRootView().removeCallbacks(danmakuTask);
//            danmakuTask = null;
//            KLog.i("======Danmaku==releaseDanmaKu1 position");
//        }
//        if (dvDanmaku != null) {
//            dvDanmaku.removeAllDanmakus(true);
//            KLog.i("======Danmaku==releaseDanmaKu2 position:" + position + " fullRelease:" + fullRelease);
//            if (fullRelease) {
//                dvDanmaku.release();
//                dvDanmaku = null;
//            } else {
//                dvDanmaku.setVisibility(View.GONE);
//            }
//        }
//        isDanmaStarted = false;
    }

    public void switchDanma(boolean open) {
//        if (open) {//打开弹幕
//            //情况1：之前没有加载过弹幕库
//            if (!isDanmaStarted) {
//                startDanmaKu(shortVideoItem.getBarrage_list());
//            } else {
//                //情况2：已经加载过弹幕，处于hide状态
//                if (dvDanmaku != null) {
//                    KLog.i("=====弹幕恢复");
//                    dvDanmaku.removeAllDanmakus(true);
//                    dvDanmaku.setVisibility(View.VISIBLE);
//                    DanmakuUtil.createDanmaEntity(dvDanmaku, shortVideoItem.getBarrage_list(), danmakuContext.mDanmakuFactory, danmakuContext);
//                }
//            }
//        } else {//关闭弹幕
//            if (dvDanmaku != null && dvDanmaku.isPrepared()) {
//                dvDanmaku.setVisibility(View.GONE);
//                KLog.i("=====弹幕隐藏");
//            }
//        }
    }

    //隐藏弹幕库
    public void hideDanmaKu() {
//        if (dvDanmaku != null) {
//            dvDanmaku.hide();
//            KLog.i("======Danmaku==hideDanmaKu");
//        }
    }

    /**
     * @param giftId
     * @param totalCount 总分贝
     * @param hintCount  暴击分贝
     */
    public void sendGift2Danma(long giftId, int totalCount, int hintCount) {
//        if (dvDanmaku != null && dvDanmaku.isPrepared()) {
//            KLog.i("======Danmaku==startDanmaKu2");
//
//            DanmakuUtil.createDanmaEntity(dvDanmaku, new ArrayList<DcDanmaEntity>() {{
//                DcDanmaEntity danmaEntity;
//                for (int i = 0; i < 3; i++) {   //无奈，只能这么搞
//                    danmaEntity = new DcDanmaEntity();
//                    danmaEntity.comm_type = 0;
//                    danmaEntity.title = " ";
//                    add(danmaEntity);
//                }
//            }}, danmakuContext.mDanmakuFactory, danmakuContext);
//
//
//            DcDanmaEntity dcDanmaEntity = new DcDanmaEntity();
//            dcDanmaEntity.comm_type = 2;
//            dcDanmaEntity.total_point = String.valueOf(totalCount + hintCount);
//            dcDanmaEntity.prize_point = hintCount;
//            if (AccountUtil.getUserInfo() != null) {
//                dcDanmaEntity.user = new UserInfo(AccountUtil.getUserInfo().getName(), AccountUtil.getUserInfo().getCover_url(), AccountUtil.getUserInfo().getId());
//            }
//            DanmakuUtil.createDanmaEntity(dvDanmaku, dcDanmaEntity, danmakuContext.mDanmakuFactory);
//        }
    }

    //发送评论的弹幕
    public void sendCommentDanma(RefreshCommentBean bean) {
//        if (bean != null && dvDanmaku != null && dvDanmaku.isPrepared()) {
//            DcDanmaEntity dcDanmaEntity = new DcDanmaEntity();
//            dcDanmaEntity.comm_type = 0;
//            dcDanmaEntity.title = bean.comment;
//            DanmakuUtil.createDanmaEntity(dvDanmaku, dcDanmaEntity, danmakuContext.mDanmakuFactory);
//        }
    }

//    @Override
//    public void onLikeClick() {
//        if (needBlock()) {
//            return;
//        }
//        if (shortVideoItem.getIs_delete() == 1) {
//            ToastUtil.showToast(R.string.stringWorkDeleted);
//            return;
//        }
//        if (shortVideoViewCallback != null) {
//            shortVideoViewCallback.onLikeClick(position, shortVideoItem.getId(),
//                    false, true,
//                    0, 0, 0, 0);
//        }
//    }
//
//    @Override
//    public void onCommentClick() {
//        if (needBlock()) {
//            return;
//        }
//        if (shortVideoItem.getIs_delete() == 1) {
//            ToastUtil.showToast(R.string.stringWorkDeleted);
//            return;
//        }
//        if (shortVideoViewCallback != null) {
//            shortVideoViewCallback.onCommentListShow(pageId, position, true, shortVideoItem.getComment_count(), shortVideoItem);
//        }
//    }
//
//    @Override
//    public void onShareClick() {
//        if (shortVideoItem == null || needBlock()) {
//            return;
//        }
//        if (shortVideoItem.getIs_delete() == 1) {
//            ToastUtil.showToast(R.string.stringWorkDeleted);
//            return;
//        }
//        if (shortVideoViewCallback != null && (shortVideoItem.getShare_info() != null
//                && (!TextUtils.isEmpty(shortVideoItem.getShare_info().share_url)))) {
//            shortVideoViewCallback.onShareClick(position, shortVideoItem.getId(),
//                    AccountUtil.isLogin() && (AccountUtil.getUserId() == author.getId()),
//                    shortVideoItem.getShare_info(), shortVideoItem.getIs_teamwork() == 1);
//        } else {
//            ToastUtil.showToast(R.string.hintErrorDataDelayTry);
//        }
//    }
//
//    @Override
//    public void onGiftClick() {
//        if (shortVideoViewCallback != null) {
//            shortVideoViewCallback.onGiftClick(AccountUtil.isLogin(), position, shortVideoItem.getId(), shortVideoItem.getAuthorName());
//        }
//    }

    private boolean needBlock() {
        if (GlobalParams.StaticVariable.sCurrentNetwork == 2) {
            ToastUtil.showToast(R.string.network_null);
            return true;
        }
        return false;
    }


    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        if (needBlock()) {
            return;
        }
        if (shortVideoItem != null) {
            switch (v.getId()) {
                case R.id.ivUserAvatar:
                case R.id.tvUser:
                    if (author.getId() > 0) {
                        shortVideoViewCallback.onUserClick(author.getId());
                    } else {
                        ToastUtil.showToast(R.string.hintErrorDataDelayTry);
                    }
                    break;
                case R.id.tvFollow:
                    if (shortVideoItem.getUser() != null && shortVideoItem.getUser().getId() > 0) {
                        shortVideoViewCallback.onFollowClick(position, shortVideoItem.getId(), shortVideoItem.getUser().getId(), shortVideoItem.getUser().isFollowed());
                    } else {
                        ToastUtil.showToast(R.string.hintErrorDataDelayTry);
                    }
                    break;
//                case R.id.ivCdCover:
//                case R.id.llMusicName:
//                case R.id.viewMusicName:
//                    shortVideoViewCallback.onMusicClick(shortVideoItem.getMusic_id());
//                    break;
                case R.id.tvTopic:
                    shortVideoViewCallback.onTopicClick(shortVideoItem.getTopic_id());
                    break;
                case R.id.ivLike:
                    shortVideoViewCallback.onLikeClick(position, shortVideoItem.getId(),shortVideoItem.is_like(),
                            false, true,
                            0, 0, 0, 0);
                    break;
                case R.id.ivComment:
                    if (shortVideoItem.getIs_delete() == 1) {
                        ToastUtil.showToast(R.string.stringWorkDeleted);
                        return;
                    }
                    shortVideoViewCallback.onCommentClick(position, shortVideoItem.getId(), shortVideoItem);
                    break;
//                case R.id.flBlockView:
//                    break;
//                case R.id.llRechargePanel:
//                    if (shortVideoViewCallback != null) {
//                        shortVideoViewCallback.onRechargeClick(position);
//                    }
//                    break;
//                case R.id.llJoin:
//                    if (shortVideoViewCallback != null) {
//                        shortVideoViewCallback.onJoinClick(position, shortVideoItem.getId());
//                    }
//                    break;
                default:
                    break;
            }
        } else {
            ToastUtil.showToast(R.string.hintErrorDataDelayTry);
        }
    }

    private void setViewClickListener() {
        ivUserAvatar.setOnClickListener(this);
        tvUser.setOnClickListener(this);
        tvTopic.setOnClickListener(this);
        viewVideoClickHolder.setGestureViewListener(this);
//        llJoin.setOnClickListener(this);
        tvFollow.setOnClickListener(this);
        ivLike.setOnClickListener(this);
        ivComment.setOnClickListener(this);
    }
}
