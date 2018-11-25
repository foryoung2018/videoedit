package com.wmlive.hhvideo.heihei.mainhome.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.fresco.animation.drawable.AnimatedDrawable2;
import com.wmlive.hhvideo.common.manager.gift.GiftManager;
import com.wmlive.hhvideo.heihei.beans.gifts.GiftEntity;
import com.wmlive.hhvideo.heihei.beans.gifts.RebateEntity;
import com.wmlive.hhvideo.heihei.mainhome.util.GiftUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.widget.BaseCustomView;

import cn.wmlive.hhvideo.R;

import static com.facebook.fresco.animation.drawable.animator.AnimatedDrawable2ValueAnimatorHelper.createAnimatorUpdateListener;

/**
 * Created by lsq on 1/9/2018.5:47 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class FlyView extends BaseCustomView implements ValueAnimator.AnimatorUpdateListener {
    private static final String URI_BURST_WEBP = "asset:///gift/six_coin.webp";

    private ImageView ivDiamond;
    private TextView tvHitCount;
    private ImageView ivFlyTone;
    private ImageView ivFlyDiamond;
    private RelativeLayout rlHitRoot;
    private SimpleDraweeView svHit;
    private FlyViewListener listener;
    private ValueAnimator hitAnim;
    private ValueAnimator diamondAnim;
    private ValueAnimator flyToneAnim;

    public FlyView(Context context) {
        super(context);
    }

    public FlyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {
        ivFlyTone = findViewById(R.id.ivFlyTone);
        ivFlyDiamond = findViewById(R.id.ivFlyDiamond);
        svHit = findViewById(R.id.svHit);
        rlHitRoot = findViewById(R.id.rlHitRoot);
        ivDiamond = findViewById(R.id.ivDiamond);
        tvHitCount = findViewById(R.id.tvHitCount);
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_fly_gift;
    }


    /**
     * 飞翔的音符
     *
     * @param clickCount
     * @param startPoint
     * @param endPoint
     * @param controlPoint
     */
    public void playFlyToneAnim(int clickCount, GiftEntity giftEntity, Point startPoint, Point endPoint, Point controlPoint) {
        KLog.i("=====飞翔的音符，贝塞尔点 start：" + startPoint.toString() + " end:" + endPoint.toString() + " control:" + controlPoint);
        ivFlyDiamond.setVisibility(GONE);
        rlHitRoot.setVisibility(GONE);
        String imageUrl = GiftManager.getFlyIcon(giftEntity.id, giftEntity.unique_id);
        if (TextUtils.isEmpty(imageUrl)) {
            imageUrl = giftEntity.icon_url;
        } else {
            imageUrl = "file://" + imageUrl;
        }
        ivFlyTone.setImageURI(Uri.parse(imageUrl));
        ivFlyTone.setVisibility(VISIBLE);
        flyToneAnim = ValueAnimator.ofObject(new BezierEvaluator(controlPoint), startPoint, endPoint);
        flyToneAnim.addUpdateListener(this);
        flyToneAnim.setDuration(400);
        flyToneAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                KLog.i("=======onAnimationEnd");
                if (listener != null) {
                    listener.onFlyEnd(clickCount, getTag() == null ? "0" : (String) getTag());
                }
                ViewGroup viewGroup = (ViewGroup) getParent();
                if (viewGroup != null) {
                    viewGroup.removeView(FlyView.this);
                }
            }
        });
        flyToneAnim.setInterpolator(new LinearInterpolator());
        flyToneAnim.start();
    }

    /**
     * 分贝和返钻的暴击
     *
     * @param startPoint
     * @param endPoint
     * @param controlPoint
     */
    public void playHitAnim(GiftEntity giftEntity, Point startPoint, Point endPoint, Point controlPoint, int rebateType, int rebateCount) {
        KLog.i("=====分贝和返钻 ，贝塞尔点 start：" + startPoint.toString() + " end:" + endPoint.toString() + " control:" + controlPoint);
        ivFlyDiamond.setVisibility(GONE);
        rlHitRoot.setVisibility(VISIBLE);
        ivFlyTone.setVisibility(GONE);
        if (rebateType == RebateEntity.TYPE_REBATE) {
            ivDiamond.setVisibility(VISIBLE);
            tvHitCount.setText(String.valueOf(rebateCount));
        } else if (rebateType == RebateEntity.TYPE_DECIBEL) {
            ivDiamond.setVisibility(GONE);
            tvHitCount.setText(rebateCount + "分贝");
        }
        svHit.setController(Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse(URI_BURST_WEBP))
                .setOldController(svHit.getController())
                .build());

        hitAnim = ValueAnimator.ofObject(new BezierEvaluator(controlPoint), startPoint, endPoint);
        hitAnim.addUpdateListener(this);
        hitAnim.setDuration(200);
        hitAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                KLog.i("=======onAnimationEnd");
                if (FlyView.this.getVisibility() == VISIBLE) {
                    if (listener != null) {
                        listener.onBurstEnd(giftEntity);
                    }
                    FlyView.this.startAnimation(GiftUtils.getScaleAnimation(1.2f));
                    ViewGroup parent = (ViewGroup) getParent();
                    if (parent != null) {
                        FlyView.this.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (getVisibility() == VISIBLE && svHit.getController() != null) {
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
                        }, 200);

                        FlyView.this.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (getVisibility() == VISIBLE && listener != null) {
                                    int[] location = new int[2];
                                    ivDiamond.getLocationOnScreen(location);
                                    listener.onDiamondFlyStart(rebateType, giftEntity,
                                            new Point(location[0] + (int) (0.3f * ivDiamond.getWidth()),
                                                    location[1] + (int) (0.5f * ivDiamond.getHeight())));
                                }
                            }
                        }, 400);

                        FlyView.this.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (parent != null) {
                                    Animatable animatable = svHit.getController().getAnimatable();
                                    if (animatable != null) {
                                        if (animatable.isRunning()) {
                                            animatable.stop();
                                        }
                                    }
                                    parent.removeView(FlyView.this);
                                }
                            }
                        }, 800);
                    }
                }
            }
        });
        hitAnim.setInterpolator(new LinearInterpolator());
        hitAnim.start();
    }

    public void playDiamondAnim(int clickCount, Point startPoint, Point endPoint, Point controlPoint) {
        KLog.i("=====掉落的钻石，贝塞尔点 start：" + startPoint.toString() + " end:" + endPoint.toString() + " control:" + controlPoint);
        ivFlyDiamond.setVisibility(VISIBLE);
        rlHitRoot.setVisibility(GONE);
        ivFlyTone.setVisibility(GONE);
        diamondAnim = ValueAnimator.ofObject(new BezierEvaluator(controlPoint), startPoint, endPoint);
        diamondAnim.addUpdateListener(this);
        diamondAnim.setDuration(500);
        diamondAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                KLog.i("=======onAnimationEnd");
                ViewGroup viewGroup = (ViewGroup) getParent();
                if (viewGroup != null) {
                    viewGroup.removeView(FlyView.this);
                }
            }
        });
        diamondAnim.setInterpolator(new LinearInterpolator());
        diamondAnim.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        Point point = (Point) animation.getAnimatedValue();
        setX(point.x);
        setY(point.y);
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        KLog.i("======FlyView=onDetachedFromWindow");
        if (diamondAnim != null) {
            diamondAnim.cancel();
            diamondAnim = null;
        }

        if (hitAnim != null) {
            hitAnim.cancel();
            hitAnim = null;
        }

        if (flyToneAnim != null) {
            flyToneAnim.cancel();
            flyToneAnim = null;
        }
        clearAnimation();
    }


    public void setFlyViewListener(FlyViewListener listener) {
        this.listener = listener;
    }

    public interface FlyViewListener {
        void onFlyEnd(int clickCount, String giftId);

        void onBurstEnd(GiftEntity giftEntity);

        void onDiamondFlyStart(int rebateType, GiftEntity giftEntity, Point startPoint);
    }
}
