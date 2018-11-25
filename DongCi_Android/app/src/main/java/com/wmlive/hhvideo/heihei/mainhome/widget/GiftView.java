package com.wmlive.hhvideo.heihei.mainhome.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.fresco.animation.drawable.AnimatedDrawable2;
import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.manager.gift.GiftManager;
import com.wmlive.hhvideo.heihei.beans.gifts.GiftEntity;
import com.wmlive.hhvideo.heihei.beans.gifts.GiftRebateEntity;
import com.wmlive.hhvideo.heihei.beans.gifts.GiftRecordEntity;
import com.wmlive.hhvideo.heihei.beans.gifts.RebateEntity;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.mainhome.util.GiftUtils;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.CommonUtils;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.utils.WeakHandler;
import com.wmlive.hhvideo.widget.BaseCustomView;
import com.wmlive.hhvideo.widget.flowrecycler.FlowLayoutManager;
import com.wmlive.hhvideo.widget.flowrecycler.FlowRecyclerView;
import com.wmlive.networklib.util.EventHelper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.wmlive.hhvideo.R;

import static com.facebook.fresco.animation.drawable.animator.AnimatedDrawable2ValueAnimatorHelper.createAnimatorUpdateListener;

/**
 * Created by lsq on 1/8/2018.4:57 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class GiftView extends BaseCustomView implements
        GiftAdapter.GiftItemClickListener,
        FlowLayoutManager.OnSelected,
        FlyView.FlyViewListener,
        Handler.Callback,
        CountdownView.OnCountdownListener {

    private static final String URI_FLY_MUSIC_BACKUP = "gift/fly_tone_music.mp3";
    private static final String URI_BURST_MUSIC_BACKUP = "gift/fly_tone_burst.mp3";
    private static final String URI_FLY_TONE = "asset:///gift/fly_tone_burst.webp";

    public static final String REPLACE_TOKEN = ",";

    private static final int SEND_GIFT_TO_PRODUCT = 0;
    private static final int SEND_GIFT_TO_USER = 1;

    private GiftAdapter giftAdapter;
    private GiftViewListener giftViewListener;
    private int dataPosition;
    private int giftListSize = 0;
    private List<GiftEntity> giftEntityList;
    private FlowRecyclerView frList;
    private TextView tvClickCount;
    private TextView tvOk;
    private ImageView ivMusicIcon;
    private ImageView ivRechargeDiamond;
    private RelativeLayout rlRoot;
    private RelativeLayout rlRootView;
    private RelativeLayout rlHitResult;
    private RelativeLayout rlRechargePanel;
    private FreeGiftResultView llFreeGiftResult;
    private TextView tvGiftName;
    private TextView tvExperience;
    private TextView tvGoldCount;
    private TextView tvCallSomebody;
    private TextView tvDecibelCount;
    private TextView tvRechargeLabel;
    private ImageView ivArrow;
    private SimpleDraweeView svHit;
    private int currentCenterPosition = -1;
    private boolean isFlyToneLeft = true;
    private boolean isHitLeft = true;
    private WeakHandler weakHandler;
    private Map<String, GiftRecordEntity> sendList;
    private long videoId;
    private int allDeduct = 0;
    private volatile boolean isPendingPay = false;
    private Point rootPoint;
    private int screenWidth;
    private SoundSpeaker burstSoundSpeaker;
    private SoundSpeaker clickSoundSpeaker;
    private int sendType = 0;

    private static final short TYPE_PAUSE_MUSIC = 10;
    private static final String TAG_FLY_VIEW = "fly_view";
    private UserInfo otherUserInfo;

    public GiftView(Context context) {
        super(context);
    }

    public GiftView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GiftView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_gift;
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {
        rlRoot = findViewById(R.id.rlRoot);
        rlRootView = findViewById(R.id.rlRootView);
        tvClickCount = findViewById(R.id.tvClickCount);
        ivMusicIcon = findViewById(R.id.ivMusicIcon);
        svHit = findViewById(R.id.svHit);
        frList = findViewById(R.id.frList);
        rlHitResult = findViewById(R.id.rlHitResult);
        rlRechargePanel = findViewById(R.id.rlRechargePanel);
        tvGiftName = findViewById(R.id.tvGiftName);
        tvExperience = findViewById(R.id.tvExperience);
        tvGoldCount = findViewById(R.id.tvGoldCount);
        tvDecibelCount = findViewById(R.id.tvDecibelCount);
        tvOk = findViewById(R.id.tvOk);
        tvCallSomebody = findViewById(R.id.tvCallSomebody);
        llFreeGiftResult = findViewById(R.id.llFreeResult);
        ivRechargeDiamond = findViewById(R.id.ivRechargeDiamond);
        tvRechargeLabel = findViewById(R.id.tvRechargeLabel);
        ivArrow = findViewById(R.id.ivArrow);
    }

    @Override
    public void initData() {
        super.initData();
        weakHandler = new WeakHandler(this);
        giftAdapter = new GiftAdapter();
        screenWidth = DeviceUtils.getScreenWH(getContext())[0];
        sendList = new HashMap<>(2);
        giftAdapter.setItemClickListener(this);
        giftAdapter.setCountdownListener(this);
        frList.setIntervalRatio(0.84f);
        frList.setAdapter(giftAdapter);
        frList.setOnItemSelectedListener(this);
        rlRoot.setOnClickListener(this);
        tvRechargeLabel.setOnClickListener(this);
        ivArrow.setOnClickListener(this);
        tvOk.setOnClickListener(this);
        llFreeGiftResult.setOnClickListener(this);
        rlRechargePanel.setOnClickListener(this);
        svHit.setVisibility(INVISIBLE);
        svHit.setController(Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse(URI_FLY_TONE))
                .setOldController(svHit.getController())
                .build());
        initSoundPool();
    }

    private void initSoundPool() {
        burstSoundSpeaker = new SoundSpeaker();
        clickSoundSpeaker = new SoundSpeaker();
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.llFreeResult:
                break;
            case R.id.tvOk:
                setVisibility(GONE);
                break;
            case R.id.rlRoot:
                dismiss();
                break;
            case R.id.rlRechargePanel:
                break;
            case R.id.tvRechargeLabel:
            case R.id.ivArrow:
                if (giftViewListener != null) {
                    giftViewListener.onRechargeClick(dataPosition);
                }
                break;
            default:
                break;
        }
    }

    public void adjustCallSomebody(int marginTop) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tvCallSomebody.getLayoutParams();
        layoutParams.topMargin += marginTop;
    }

    public void dismiss() {
        frList.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_zoom_in));
        rlRechargePanel.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_bottom_pop_out));
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!CollectionUtil.isEmpty(giftEntityList)) {
                    frList.scrollToPosition(0);
                }
                frList.clearAnimation();
                rlRechargePanel.clearAnimation();
                setVisibility(GONE);
            }
        }, 280);
        GiftManager.get().pauseMusic();
        GiftManager.get().resetCurrentPlayPath();
        EventHelper.post(GlobalParams.EventType.TYPE_REFRESH_COUNTDOWN, 0);
        tvClickCount.setText("");
        ivMusicIcon.setImageBitmap(null);
        int count = rlRootView.getChildCount();
        if (count > 0) {
            View view;
            try {
                for (int i = count - 1; i >= 0; i--) {
                    view = rlRootView.getChildAt(i);
                    if (view != null && view.getTag() != null) {
                        view.clearAnimation();
                        rlRootView.removeView(view);
                        KLog.i("========移除一个动画的View");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (giftViewListener != null) {
            if (!preparePay(3)) {
            }
            giftViewListener.onGiftDismiss(dataPosition);
        }
        dataPosition = -1;
        currentCenterPosition = -1;
        videoId = 0;
        rlHitResult.setVisibility(GONE);
        releaseSoundPool();
    }

    /**
     * 结算
     *
     * @param type 1:只结算免费礼物    2：只结算收费礼物，   3:免费收费都结算
     * @return
     */
    private boolean preparePay(int type) {
        if (!CollectionUtil.isEmpty(sendList)) {
            StringBuilder sbIds = new StringBuilder(2);
            StringBuilder sbCount = new StringBuilder(2);
            Iterator<Map.Entry<String, GiftRecordEntity>> iterator = sendList.entrySet().iterator();
            Map.Entry<String, GiftRecordEntity> next;
            int allClickCount = 0;
            int allDecibelCount = 0;
            GiftEntity giftEntity;
            while (iterator.hasNext()) {
                next = iterator.next();
                if ((type == 1 && !next.getValue().isFree())
                        || (type == 2 && next.getValue().isFree())) {
                    continue;
                }
                if (next.getValue().clickCount > 0) {
                    if (!next.getValue().isPendingSettlement) {
                        isPendingPay = true;
                        next.getValue().isPendingSettlement = true;
                        sbIds.append(next.getKey()).append(REPLACE_TOKEN);
                        sbCount.append(next.getValue().clickCount).append(REPLACE_TOKEN);
                        allClickCount += next.getValue().gold * next.getValue().clickCount;
                        giftEntity = getCurrentGift(next.getKey());
                        if (giftEntity != null) {
                            allDecibelCount += giftEntity.getDecibelRebateCount(next.getValue().clickCount);
                        }
                    }
                }
            }
            String ids = sbIds.toString();
            String count = sbCount.toString();
            KLog.i("=====需要送的礼物id:" + ids + " ,礼物的次数：" + count);
            if (!TextUtils.isEmpty(ids) && !TextUtils.isEmpty(count)) {
                if (ids.endsWith(REPLACE_TOKEN)) {
                    ids = ids.substring(0, ids.lastIndexOf(REPLACE_TOKEN));
                }
                if (count.endsWith(REPLACE_TOKEN)) {
                    count = count.substring(0, count.lastIndexOf(REPLACE_TOKEN));
                }
                if (giftViewListener != null) {
                    if (sendType == SEND_GIFT_TO_PRODUCT) {
                        giftViewListener.doPayGift(dataPosition, videoId, ids, count, allClickCount, allDecibelCount);
                    } else if (sendType == SEND_GIFT_TO_USER) {
                        giftViewListener.doPayGift(dataPosition, otherUserInfo != null ? otherUserInfo.getId() : 0, ids, count, allClickCount, allDecibelCount);
                    }
                    if (svHit != null && svHit.getController() != null) {
                        Animatable animatable = svHit.getController().getAnimatable();
                        if (animatable != null) {
                            if (animatable.isRunning()) {
                                animatable.stop();
                            }
                        }
                    }
                    return true;
                }
            } else {
                KLog.i("=======没有送礼物，不需要结算");
            }
        }
        return false;
    }

    /**
     * 显示送礼物面板
     *
     * @param position
     * @param videoId
     * @param author
     */
    public void showGiftPanel(int position, long videoId, String author, List<GiftEntity> giftEntities) {
        GiftManager.get().resetCurrentPlayPath();
        rlRechargePanel.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_bottom_pop_in));
        this.sendType = SEND_GIFT_TO_PRODUCT;
        allDeduct = 0;
        this.dataPosition = position;
        this.videoId = videoId;
        rlRootView.setVisibility(VISIBLE);
        llFreeGiftResult.setVisibility(GONE);
        tvDecibelCount.setVisibility(GONE);
        rlHitResult.setVisibility(INVISIBLE);
        giftEntityList = giftEntities;
        initFlowRecyclerView();
        if (!TextUtils.isEmpty(author)) {
            tvCallSomebody.setVisibility(VISIBLE);
            String tipsString = "为 " + author + " 作品赠送分贝";
            SpannableString spanString = new SpannableString(tipsString);
            ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color.hh_color_i));
            spanString.setSpan(span, 2, 2 + author.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            tvCallSomebody.setText(spanString);
        } else {
            tvCallSomebody.setVisibility(GONE);
        }
        initSoundPool();
        isPendingPay = false;
    }

    /**
     * 显示送礼物面板（IM中为用户送礼物）
     *
     * @param otherUserInfo
     */
    public void showGiftPanel(UserInfo otherUserInfo, List<GiftEntity> giftEntities) {
        GiftManager.get().resetCurrentPlayPath();
        giftEntityList = giftEntities;
        rlRechargePanel.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_bottom_pop_in));
        this.sendType = SEND_GIFT_TO_USER;
        this.otherUserInfo = otherUserInfo;
        allDeduct = 0;
        this.dataPosition = 0;
        rlRootView.setVisibility(VISIBLE);
        llFreeGiftResult.setVisibility(GONE);
        tvDecibelCount.setVisibility(GONE);
        rlHitResult.setVisibility(INVISIBLE);
        initFlowRecyclerView();
        tvCallSomebody.setVisibility(GONE);
        initSoundPool();
        isPendingPay = false;
    }

    public void initFlowRecyclerView() {
        if (frList != null && giftAdapter != null) {
//            giftEntityList = GiftManager.get().getGiftList();
            giftAdapter.addData(giftEntityList);
            giftListSize = giftEntityList.size();
            if (!CollectionUtil.isEmpty(giftEntityList)) {
//                frList.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
                frList.getFlowLayoutManager().scrollToPosition(giftEntityList.size() * GiftAdapter.MAX_RATIO / 2);
//                    frList.scrollToPosition(giftEntityList.size() * GiftAdapter.MAX_RATIO / 2);
//                    }
//                }, 100);
                for (GiftEntity giftEntity : giftEntityList) {
                    sendList.put(giftEntity.id, new GiftRecordEntity(giftEntity.id, giftEntity.gold));
                }
                frList.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_zoom_out));
                frList.setVisibility(VISIBLE);
            } else {
                frList.setVisibility(GONE);
            }
        }
    }

    /**
     * 显示免费礼物的结算结果
     *
     * @param giftRebateEntity
     */
    public void showFreeResultPanel(GiftRebateEntity giftRebateEntity) {
        GiftManager.get().pauseMusic();
        GiftManager.get().resetCurrentPlayPath();
        tvDecibelCount.setVisibility(GONE);
        rlRootView.setVisibility(GONE);
        llFreeGiftResult.setVisibility(VISIBLE);
        if (giftRebateEntity != null) {
            llFreeGiftResult.setData(giftRebateEntity);
        } else {
            dismiss();
        }
    }

    public void removeSendGift(String giftIds) {
        allDeduct = 0;
        if (!TextUtils.isEmpty(giftIds)) {
            String[] ids = giftIds.split(REPLACE_TOKEN);
            if (ids.length > 0) {
                for (String id : ids) {
                    GiftRecordEntity giftRecordEntity = sendList.get(id);
                    if (giftRecordEntity != null) {
                        KLog.i("======移除已送的礼物：" + id);
                        sendList.remove(id);
                    }
                }
            }
        }
        postDelayed(new Runnable() {
            @Override
            public void run() {
                isPendingPay = false;
            }
        }, 1000);
    }

    public void removeAllSendGift() {
        allDeduct = 0;
        isPendingPay = false;
        if (sendList != null) {
            sendList.clear();
        }
    }

    @Override
    public void onItemSelected(int position) {
        int currentIndex = getIndex(position);
        if (currentIndex < 0) {
            return;
        }
        tvClickCount.setText("");
        tvDecibelCount.setText("");
        ivMusicIcon.setImageBitmap(null);
        rlHitResult.setVisibility(INVISIBLE);
        int oldIndex = getIndex(currentCenterPosition);
        GiftEntity currentGift = giftEntityList.get(currentIndex);
        if (oldIndex > -1) {
            GiftEntity oldGift = giftEntityList.get(oldIndex);
            if (oldGift.isFree() || currentGift.isFree()) {
                EventHelper.post(GlobalParams.EventType.TYPE_REFRESH_COUNTDOWN, 0);
                GiftRecordEntity giftRecordEntity = sendList.get(oldGift.id);
                KLog.i("====准备结算免费礼物giftId:" + oldGift.id + " 记录内容"
                        + (giftRecordEntity == null ? "null" : giftRecordEntity.toString()));
                if (giftRecordEntity != null && giftRecordEntity.clickCount > 0) {
                    preparePay(oldGift.isFree() ? 1 : 2);
                }
            }
        }
        GiftRecordEntity giftRecord = sendList.get(currentGift.id);
        rlHitResult.setVisibility(GONE);
        tvDecibelCount.setVisibility(GONE);
        if (giftRecord != null) {
            giftRecord.tempClickCount = 0;
            giftRecord.decibelRebate = 0;
            giftRecord.goldRebate = 0;
//            rlHitResult.setVisibility((!isPendingPay) && (giftRecord.tempClickCount > 0) ? VISIBLE : GONE);
//            setClickCountText(giftRecord.clickCount * (giftRecord.gold > 0 ? giftRecord.gold : 1));
//            tvDecibelCount.setVisibility(giftRecord.decibelRebate > 0 ? VISIBLE : GONE);
//            tvDecibelCount.setText("+" + giftRecord.decibelRebate);
        }
        String imageUrl = GiftManager.getFlyIcon(currentGift.id, currentGift.unique_id);
        if (TextUtils.isEmpty(imageUrl)) {
            imageUrl = currentGift.icon_url;
        } else {
            imageUrl = "file://" + imageUrl;
        }
        ivMusicIcon.setImageURI(Uri.parse(imageUrl));
        KLog.i("=====之前的位置：" + currentCenterPosition + " 现在的位置：" + position);
        playGiftAnim(currentCenterPosition, false);
        currentCenterPosition = position;
        if (giftListSize > 0 && currentIndex < giftListSize) {
            rlHitResult.post(new Runnable() {
                @Override
                public void run() {
                    setGiftNameText(currentGift.name, currentGift.gold, currentGift.isFree());
                    tvExperience.setText("+" + currentGift.exp + "经验");
                    tvGoldCount.setText(getCurrentGold());
                }
            });
        } else {
            tvGiftName.setText("");
            tvExperience.setText("");
            tvGoldCount.setText("");
        }
        frList.postDelayed(playGiftRunnable, 400);
    }

    @Override
    public boolean onGiftItemClick(int position, int index, GiftEntity giftEntity, Point startPoint) {
        if (giftEntity == null || TextUtils.isEmpty(giftEntity.id)) {
            ToastUtil.showToast(R.string.hintErrorDataDelayTry);
            return false;
        }
        if (currentCenterPosition != position || isPendingPay) {
            if (isPendingPay) {
                KLog.i("=======正在结算，不能再点击");
            }
            return false;
        }

        if (GlobalParams.StaticVariable.sCurrentNetwork == 2) {
            ToastUtil.showToast(R.string.network_null);
            return false;
        }

        int currentGold = (AccountUtil.getGoldCount() - allDeduct);
        if (currentGold >= 0 && (currentGold - giftEntity.gold) >= 0) {
            GiftRecordEntity giftRecord = sendList.get(giftEntity.id);
            if (giftRecord == null) {
                giftRecord = new GiftRecordEntity(giftEntity.id, giftEntity.gold);
                sendList.put(giftEntity.id, giftRecord);
            }
            giftRecord.clickCount++;
            giftRecord.tempClickCount++;
            allDeduct += giftEntity.gold;
            tvGoldCount.setText(getCurrentGold());
            if (giftEntity.isFree()) {
                EventHelper.post(GlobalParams.EventType.TYPE_REFRESH_COUNTDOWN, giftEntity.show_second);
            }

            playFlyAnim(giftRecord.clickCount, giftEntity, startPoint, RebateEntity.TYPE_NONE, 0, 0);

            String musicUri = GiftManager.getGiftMusic(giftEntity.id, giftEntity.unique_id);
            if (TextUtils.isEmpty(musicUri)) {
                musicUri = giftEntity.icon_music_url;
            }
            KLog.i("=====使用的点击音效文件：" + musicUri);
            GiftManager.get().playMusic(musicUri);
            weakHandler.removeMessages(TYPE_PAUSE_MUSIC);
            weakHandler.sendEmptyMessageDelayed(TYPE_PAUSE_MUSIC, 500);
            KLog.i("======点击的礼物列表:" + CommonUtils.printMap(sendList));
            return true;
        } else {
            dismiss();
            if (giftViewListener != null) {
                giftViewListener.onGoldNotEnough();
            }
            return false;
        }
    }


    @Override
    public void onFlyEnd(int clickCount, String giftId) {
        svHit.setVisibility(VISIBLE);
        GiftEntity giftEntity = getCurrentGift(giftId);
        if (giftEntity != null && !isPendingPay) {
            GiftRecordEntity giftRecord = sendList.get(giftId);
            if (giftRecord == null) {
                giftRecord = new GiftRecordEntity(giftEntity.id, giftEntity.gold);
                sendList.put(giftId, giftRecord);
            }
            rlHitResult.setVisibility((!isPendingPay) && giftRecord.clickCount > 0 ? VISIBLE : INVISIBLE);
            setClickCountText(giftRecord.tempClickCount * (giftRecord.gold > 0 ? giftRecord.gold : 1));
            KLog.i("===onFlyEnd==giftId:" + giftId + " ,clickCount:" + clickCount + " count:" + giftRecord);

            playFlySound(giftEntity);

            if (!CollectionUtil.isEmpty(giftEntity.prizes)) {
                int c;
                for (RebateEntity rebateEntity : giftEntity.prizes) {
                    if (rebateEntity != null) {
                        c = rebateEntity.getDecibelRebate(clickCount);
                        if (c > 0) {
                            //触发分贝暴击
                            giftRecord.decibelRebate += c;
                            KLog.i("=======触发分贝暴击：" + clickCount + " ,分贝数：" + c + " ,giftRecord:" + giftRecord);
                            playFlyAnim(clickCount, giftEntity, null, RebateEntity.TYPE_DECIBEL, c, giftRecord.decibelRebate);
                        }
                        c = rebateEntity.getGoldRebate(clickCount);
                        allDeduct -= c;
                        if (c > 0) {
                            //触发返钻暴击
                            giftRecord.goldRebate += c;
                            KLog.i("=======触发返钻暴击：" + clickCount + " ,返钻数：" + c + " ,giftRecord:" + giftRecord);
                            playFlyAnim(clickCount, giftEntity, null, RebateEntity.TYPE_REBATE, c, giftRecord.goldRebate);
                        }
                    }
                }
            }

            KLog.i("=====需要扣除钻石数量：" + allDeduct);
            tvGoldCount.setText(getCurrentGold());
        }

        if (!isPendingPay) {
            rlHitResult.startAnimation(GiftUtils.getScaleAnimation(1.3f));
        }
        if (!isPendingPay && svHit.getController() != null) {
            Animatable animatable = svHit.getController().getAnimatable();
            if (animatable != null) {
                if (animatable instanceof AnimatedDrawable2) {
                    AnimatedDrawable2 animatedDrawable2 = (AnimatedDrawable2) animatable;
                    ValueAnimator animator = new ValueAnimator();
                    animator.setIntValues(0, (int) animatedDrawable2.getLoopDurationMs());
                    animator.setDuration(animatedDrawable2.getLoopDurationMs());
                    animator.setRepeatCount(0);
                    animator.setInterpolator(null);
                    animator.addUpdateListener(createAnimatorUpdateListener(animatedDrawable2));
                    animator.start();
                }
            }
        }
    }

    @Override
    public void onBurstEnd(GiftEntity giftEntity) {
        playBurstSound(giftEntity);
    }

    private void playBurstSound(GiftEntity giftEntity) {
        if (giftEntity != null) {
            String musicUri = GiftManager.getBurstMusic(giftEntity.id, giftEntity.unique_id);
            if (burstSoundSpeaker != null) {
                if (TextUtils.isEmpty(musicUri)) {
                    KLog.i("====需要播放本地的备份暴击音乐：" + musicUri);
                    burstSoundSpeaker.playSound(URI_BURST_MUSIC_BACKUP);
                } else {
                    KLog.i("====需要播放的暴击音乐：" + musicUri);
                    burstSoundSpeaker.playSound(musicUri);
                }
            }
        }
    }

    private void playFlySound(GiftEntity giftEntity) {
        if (giftEntity != null) {
            String flyUri = GiftManager.getFlyMusic(giftEntity.id, giftEntity.unique_id);
            if (clickSoundSpeaker != null) {
                if (TextUtils.isEmpty(flyUri)) {
                    KLog.i("====需要播放本地的备份音符音乐：" + flyUri);
                    clickSoundSpeaker.playSound(URI_FLY_MUSIC_BACKUP);
                } else {
                    KLog.i("====需要播放本地的音符音乐：" + flyUri);
                    clickSoundSpeaker.playSound(flyUri);
                }
            }
        }
    }

    private void setClickCountText(int count) {
        String result = "X" + count;
        SpannableString str = new SpannableString(result);
        str.setSpan(new RelativeSizeSpan(0.8f), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvClickCount.setText(str);
    }

    private void setGiftNameText(String name, int gold, boolean isFree) {
        String result = name + "  " + gold + "钻";
        SpannableString resultSp = new SpannableString(result);
        ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(isFree ? R.color.hh_color_i : R.color.hh_color_a));
        resultSp.setSpan(span, 0, !TextUtils.isEmpty(name) ? name.length() : 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tvGiftName.setText(resultSp);
    }

    @Override
    public void onDiamondFlyStart(int rebateType, GiftEntity giftEntity, Point startPoint) {
        if ((!isPendingPay) && getVisibility() == VISIBLE) {
            if (rebateType == RebateEntity.TYPE_REBATE) {
                FlyView flyView = new FlyView(getContext());
                flyView.setTag(TAG_FLY_VIEW);
                rlRootView.addView(flyView);
                startPoint.set(startPoint.x, startPoint.y - rootPoint.y);
                int[] location = new int[2];
                ivRechargeDiamond.getLocationOnScreen(location);

                Point endPoint = new Point(location[0] - rootPoint.x, location[1] - rootPoint.y);
                rlRootView.post(new Runnable() {
                    @Override
                    public void run() {
                        flyView.playDiamondAnim(1, startPoint,
                                endPoint,
                                new Point(endPoint.x, startPoint.y));
                    }
                });
            }
        }
    }

    private void playFlyAnim(int clickCount, GiftEntity giftEntity, Point startPoint,
                             int rebateType, int rebateCount, int allRebateCount) {
        if (giftEntity != null) {
            rlHitResult.post(new Runnable() {
                @Override
                public void run() {
                    FlyView flyView = new FlyView(getContext());
                    flyView.setTag(giftEntity.id);
                    flyView.setFlyViewListener(GiftView.this);

                    int[] rootLocation = new int[2];
                    rlRoot.getLocationOnScreen(rootLocation);
                    rootPoint = new Point(rootLocation[0], rootLocation[1]);
                    int[] location = new int[2];
                    ivMusicIcon.getLocationOnScreen(location);
                    Point endPoint = new Point();
                    endPoint.set(location[0], location[1] - rootPoint.y);
                    KLog.i("====结束位置：" + endPoint);
                    if (startPoint != null) {//飞翔的音符
                        startPoint.set(startPoint.x - ivMusicIcon.getWidth() / 2, startPoint.y - ivMusicIcon.getHeight() / 2);
                        rlRootView.addView(flyView);
                        flyView.playFlyToneAnim(clickCount, giftEntity, startPoint,
                                endPoint,
                                new Point(isFlyToneLeft ? CommonUtils.getRandom(-200, 600) - 120 : screenWidth,
                                        DeviceUtils.dip2px(DCApplication.getDCApp(), 60) + CommonUtils.getRandom(-200, 600)));
                        isFlyToneLeft = !isFlyToneLeft;
                    } else {//返分贝和返钻
                        if (rebateType == RebateEntity.TYPE_DECIBEL) {
                            tvDecibelCount.setVisibility(allRebateCount > 0 ? VISIBLE : GONE);
                            tvDecibelCount.setText("+" + allRebateCount);
                        }

                        rlRootView.addView(flyView);
                        Point endP = new Point(isHitLeft ? (20 + CommonUtils.getRandom(20, 180)) : (int) (screenWidth * 0.6f),
                                140 + CommonUtils.getRandom(20, 180));
                        flyView.playHitAnim(giftEntity, endPoint,
                                endP,
                                new Point((int) (screenWidth * 0.4f), endP.y), rebateType, rebateCount);
                        isHitLeft = !isHitLeft;

                    }
                }
            });
        }
    }

    public void playGiftAnim(int position, boolean start) {
        RecyclerView.ViewHolder view = frList.findViewHolderForLayoutPosition(position);
        if (null != view && view instanceof GiftAdapter.GiftViewHolder) {
            KLog.i("========viewHolder:" + view.getClass().getSimpleName());
            GiftAdapter.GiftViewHolder currentViewHolder = (GiftAdapter.GiftViewHolder) view;
            DraweeController controller = currentViewHolder.ivGift.getController();
            if (controller != null) {
                Animatable animatable = controller.getAnimatable();
                if (animatable != null) {
                    if (animatable.isRunning()) {
                        animatable.stop();
                    }
                    if (start) {
                        animatable.start();
                    }
                }

            }
        }
    }

    @Override
    public void onCountdownEnd(String giftId, int index, int position) {
        if (giftViewListener != null) {
            preparePay(1);
        } else {
            dismiss();
        }
    }

    private String getCurrentGold() {
        int c = AccountUtil.getGoldCount() - allDeduct;
        return c > 0 ? String.valueOf(c) : "0";
    }

    private int getIndex(int position) {
        return giftListSize == 0 ? -1 : (position % giftListSize);
    }

    private GiftEntity getCurrentGift(String giftId) {
        if (!CollectionUtil.isEmpty(giftEntityList) && !TextUtils.isEmpty(giftId)) {
            for (GiftEntity giftEntity : giftEntityList) {
                if (giftEntity != null) {
                    if (giftId.equals(giftEntity.id)) {
                        return giftEntity;
                    }
                }
            }
        }
        return null;
    }

    public void setGiftViewListener(GiftViewListener giftViewListener) {
        this.giftViewListener = giftViewListener;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case TYPE_PAUSE_MUSIC:
                GiftManager.get().pauseMusic();
                GiftManager.get().resetCurrentPlayPath();
                break;
            default:
                break;
        }
        return true;
    }

    private Runnable playGiftRunnable = new Runnable() {
        @Override
        public void run() {
            playGiftAnim(currentCenterPosition, true);
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (playGiftRunnable != null) {
            if (frList != null) {
                frList.removeCallbacks(playGiftRunnable);
            }
            playGiftRunnable = null;
        }
        if (getHandler() != null) {
            getHandler().removeCallbacksAndMessages(null);
        }

        releaseSoundPool();
    }

    private void releaseSoundPool() {
        if (burstSoundSpeaker != null) {
            burstSoundSpeaker.release();
            burstSoundSpeaker = null;
        }

        if (clickSoundSpeaker != null) {
            clickSoundSpeaker.release();
            clickSoundSpeaker = null;
        }
    }

    public interface GiftViewListener {
        void onGiftDismiss(int position);

        /**
         * 礼物支付
         *
         * @param position
         * @param videoId      赠送的作品ID或者用户ID
         * @param giftIds
         * @param giftsAmount
         * @param all
         * @param decibelCount
         */
        void doPayGift(int position, long videoId, String giftIds, String giftsAmount, int all, int decibelCount);

        void onRechargeClick(int position);

        void onGoldNotEnough();
    }
}
