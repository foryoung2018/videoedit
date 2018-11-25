package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
//import com.rd.vecore.VirtualVideo;
//import com.rd.vecore.exception.InvalidArgumentException;
//import com.rd.vecore.exception.InvalidStateException;
//import com.rd.vecore.models.EffectType;
//import com.rd.vecore.models.Scene;
import com.wmlive.hhvideo.heihei.beans.record.EffectEntity;
import com.wmlive.hhvideo.heihei.beans.record.FilterEffectItem;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.beans.record.TimeEffectItem;
import com.wmlive.hhvideo.widget.BaseCustomView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;


/**
 * Created by lsq on 8/28/2017.
 * 特效调节面板
 */

public class RecordEffectPanel extends BaseCustomView implements View.OnTouchListener, VideoThumbNailView.OnEffectChangeListener {

    @BindView(R.id.viewThumbnail)
    VideoThumbNailView mViewThumbnail;
    @BindView(R.id.llEffectPanel)
    LinearLayout llEffectPanel;
    @BindView(R.id.btEffectLib)
    EffectItemButton btEffectLib;
    @BindView(R.id.llEffect)
    LinearLayout llEffect;
    @BindView(R.id.btEffectTime)
    EffectRadioButton btEffectTime;
    @BindView(R.id.btEffectFilter)
    EffectRadioButton btEffectFilter;
    @BindView(R.id.ivEffectRevoke)
    ImageView ivEffectRevoke;
    @BindView(R.id.llCategory)
    LinearLayout llCategory;

    //0：时间特效   1：滤镜特效
    private static final int EFFECT_TYPE_NONE = -1;
    private static final int EFFECT_TYPE_TIME = 0;
    private static final int EFFECT_TYPE_FILTER = 1;

    ArrayList<FilterEffectItem> mArrFilterEffectItem = new ArrayList<>();
    List<EffectEntity> mArrEffectInfo = new ArrayList<>();
    TimeEffectItem mTimeEffectItem;
    private Animation mEffectScale;
    private boolean isDown = false;

    private float mTimeEffectStartTime, mTimeEffectEndTime;
    //倒序时间跟其他特效分别保存
    private float mReverseStartTime, mReverseEndTime;
    // 初始特效状态保存，用于返回时恢复初始状态
    List<EffectEntity> mTempArrEffect = new ArrayList<>();

    private OnEffectListener mEffectListener;
    private boolean isPlaying;


//    private EffectType[] effectTime = new EffectType[]{
//            EffectType.NONE,
//            EffectType.SLOW,
//            EffectType.REPEAT,
//            EffectType.REVERSE};
    private final int[] effectTimeTitleIds = new int[]{
            R.string.effect_time_null,
            R.string.effect_time_slow,
            R.string.effect_time_repeat,
            R.string.effect_time_reverse};

    private final String[] effectTimeDrawableIds = new String[]{
            "asset:///effect_icon/normal.webp",
            "asset:///effect_icon/slow.webp",
            "asset:///effect_icon/repeat.webp",
            "asset:///effect_icon/reverse.webp"};

//    private EffectType[] effectFilter = new EffectType[]{
//            EffectType.TREMBLE,
//            EffectType.AWAKENE,
//            EffectType.HEARTBEAT,
//            EffectType.SPOTLIGHT
//    };
    private final int[] effectFilterTitleIds = new int[]{
            R.string.effect_filter_tremble,
            R.string.effect_filter_awakened,
            R.string.effect_filter_heartbeat,
            R.string.effect_filter_spotlight};

    private final String[] effectFilterDrawableIds = new String[]{
            "asset:///effect_icon/tremble.webp",
            "asset:///effect_icon/awakene.webp",
            "asset:///effect_icon/heartbeat.webp",
            "asset:///effect_icon/spotlight.webp"};


    private int effectCategory = EFFECT_TYPE_NONE;

//    private EffectType selectedEffect = EffectType.NONE;
//    private VirtualVideo mVirtualVideo;
    private boolean canTouchView = false;

    public RecordEffectPanel(Context context) {
        super(context);
    }

    public RecordEffectPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.view_effect_panel;
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {
        canTouchView = false;
        btEffectTime.setOnClickListener(this);
        btEffectFilter.setOnClickListener(this);
        btEffectLib.setOnClickListener(this);
        ivEffectRevoke.setOnClickListener(this);
    }

    @Override
    public void initData() {
        super.initData();
        setEffectCategory(EFFECT_TYPE_FILTER);
//        mVirtualVideo = new VirtualVideo();
    }

    public void setEffectCategory(int type) {
        if (type != effectCategory) {
            effectCategory = type;
            switch (effectCategory) {
                case EFFECT_TYPE_TIME:
                    btEffectTime.setChecked(true);
                    btEffectFilter.setChecked(false);
                    addEffect(effectTimeTitleIds, effectTimeDrawableIds);
                    mViewThumbnail.setDrawTimeEffect(true);
                    if (mEffectListener != null) {
                        mEffectListener.seekTo(0);
                    }
                    resetView();
                    break;
                case EFFECT_TYPE_FILTER:
                    btEffectTime.setChecked(false);
                    btEffectFilter.setChecked(true);
                    addEffect(effectFilterTitleIds, effectFilterDrawableIds);
                    mViewThumbnail.setDrawTimeEffect(false);
                    if (mEffectListener != null) {
                        mEffectListener.seekTo(0);
                    }
                    resetView();
                    break;
            }
        }
    }

    /**
     * 重置界面
     */
    private void resetView() {
        if (effectCategory == EFFECT_TYPE_FILTER) {
            if (mArrFilterEffectItem.size() > 0) {
                ivEffectRevoke.setVisibility(View.VISIBLE);
            }
        } else {
            ivEffectRevoke.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 初始特效
     *
     * @param shortVideoEntity
     */
    public void initEffect(ShortVideoEntity shortVideoEntity) {
//        mReverseStartTime = 0;
//        mReverseEndTime = mEffectListener.getDuration();
//
//        //默认时间特效在中间，占总时长的1/5;
//        float timeEffectDuration = mEffectListener.getDuration() / 5;
//        mTimeEffectStartTime = (mEffectListener.getDuration() - timeEffectDuration) / 2;
//        mTimeEffectEndTime = (mEffectListener.getDuration() + timeEffectDuration) / 2;
//
//        mArrFilterEffectItem.clear();
//        mArrEffectInfo = shortVideoEntity.getEffectList();
//        mTempArrEffect.clear();
//        mTempArrEffect.addAll(mArrEffectInfo);
//        mTimeEffectItem = new TimeEffectItem(getContext());
//        for (EffectEntity info : mArrEffectInfo) {
//            float startTime = info.getStartTime();
//            float endTime = info.getEndTime();
//            if (info.effectType == EffectType.REPEAT ||
//                    info.effectType == EffectType.REVERSE ||
//                    info.effectType == EffectType.SLOW ||
//                    info.effectType == EffectType.NONE) {
//                mTimeEffectItem.setType(info.effectType);
//                if (info.effectType == EffectType.REVERSE) {
//                    mReverseStartTime = startTime;
//                    mReverseEndTime = endTime;
//                } else {
//                    mTimeEffectStartTime = startTime;
//                    mTimeEffectEndTime = endTime;
//                }
//                mTimeEffectItem.setStartTime(startTime);
//                mTimeEffectItem.setEndTime(endTime);
//                continue;
//            }
//            FilterEffectItem effectItem = new FilterEffectItem(getContext(), info.effectType,
//                    startTime, endTime);
//            mArrFilterEffectItem.add(effectItem);
//        }
//        mEffectListener.seekTo(0);
//        mEffectListener.pause();
//        Scene scene = VirtualVideo.createScene();
//        try {
//            scene.addMedia(shortVideoEntity.editingVideoPath);
//        } catch (InvalidArgumentException e) {
//            e.printStackTrace();
//        }
//        if (mArrFilterEffectItem.size() > 0) {
//            ivEffectRevoke.setVisibility(View.VISIBLE);
//        }
//        mViewThumbnail.setFilterEffectList(mArrFilterEffectItem);
//        mViewThumbnail.setTimeEffect(mTimeEffectItem);
//        mViewThumbnail.setOnEffectChangeListener(this);
//        initThumbnail(mEffectListener.getDuration(), scene);
//        canTouchView = true;
    }

//    public void initThumbnail(float duration, Scene scene) {
//        VirtualVideo virtualVideo = new VirtualVideo();
//        virtualVideo.addScene(scene);
//        try {
//            virtualVideo.build(getContext());
//        } catch (InvalidStateException e) {
//            e.printStackTrace();
//        }
//        int padding = 0;
//        int[] params = mViewThumbnail.setPlayer(virtualVideo, CoreUtils.getMetrics().widthPixels, padding, duration);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(params[0], params[1]);
//        mViewThumbnail.setLayoutParams(lp);
//        lp.setMargins(padding, 0, padding, 0);
//        mViewThumbnail.setStartThumb();
//        for (FilterEffectItem effectItem : mArrFilterEffectItem) {
//            mViewThumbnail.drawEffectRect(effectItem);
//        }
//        mViewThumbnail.drawTimeEffectRect(mTimeEffectItem.getStartTime(), mTimeEffectItem.getEndTime());
//        setPosition(0);
//    }


    public void setPosition(float position) {
        if (mViewThumbnail != null) {
            mViewThumbnail.setPosition(position);
            if (!isDown && position >= mEffectListener.getDuration()) {
                mViewThumbnail.setPosition(0);
            }
        }
    }


    private void addEffect(int[] titles, String[] paths) {
//        llEffectPanel.removeAllViews();
//        int min = Math.min(titles.length, paths.length);
//        View view;
//        for (int i = 0; i < min; i++) {
//            view = LayoutInflater.from(this.getContext()).inflate(R.layout.view_effect_item, llEffectPanel, false);
//            view.setTag(effectCategory == EFFECT_TYPE_TIME ? effectTime[i] : effectFilter[i]);
//            llEffectPanel.addView(view);
//            ((TextView) view.findViewById(R.id.tvEffect)).setText(getResources().getString(titles[i]));
//            ((CircleAnimationView) view.findViewById(R.id.ivEffect)).setController(Fresco.newDraweeControllerBuilder().
//                    setUri(paths[i]).
//                    setAutoPlayAnimations(true).
//                    build());
//            view.setOnTouchListener(this);
//            view.setOnClickListener(this);
//        }
    }


    @Override
    protected void onSingleClick(View v) {
        if (!canTouchView) {
            return;
        }
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.btEffectFilter:
                btEffectTime.setChecked(false);
                btEffectFilter.setChecked(true);
                llCategory.setVisibility(GONE);
                setEffectCategory(EFFECT_TYPE_FILTER);
                break;
            case R.id.btEffectTime:
                btEffectTime.setChecked(true);
                btEffectFilter.setChecked(false);
                llCategory.setVisibility(GONE);
                setEffectCategory(EFFECT_TYPE_TIME);
                break;
            case R.id.btEffectLib:
                ivEffectRevoke.setVisibility(View.INVISIBLE);
                int visiable = llCategory.getVisibility() == VISIBLE ? GONE : VISIBLE;
                llCategory.setVisibility(visiable);
                if (visiable == GONE) {
                    resetView();
                }
                break;
            case R.id.llEffect:
//                llCategory.setVisibility(GONE);
//                resetView();
//                if ((mEffectListener != null) && (v.getTag() != null) && (v.getTag() instanceof EffectType)) {
//                    dealTimeEffect(v);
//                }
                break;
            case R.id.ivEffectRevoke:
                revokeEffect();
                break;
        }
    }

    /**
     * 撤回按钮
     */
    public void revokeEffect() {
        if (mArrFilterEffectItem.size() > 0) {
            FilterEffectItem effectItem = mArrFilterEffectItem.remove(mArrFilterEffectItem.size() - 1);
            reloadEffect();
            if (mArrFilterEffectItem.size() == 0) {
                mEffectListener.seekTo(0);
                setPosition(0);
            } else {
                mEffectListener.seekTo(effectItem.getStartTime());
                setPosition(effectItem.getStartTime());
            }

            mEffectListener.updateEffects();
            mViewThumbnail.invalidate();
        }
        if (mArrFilterEffectItem.size() == 0) {
            ivEffectRevoke.setVisibility(View.INVISIBLE);
        }
        mEffectListener.pause();
    }

    private void dealTimeEffect(View view) {

//        EffectType type = (EffectType) view.getTag();
//        if (EffectType.REVERSE == type && ((CircleAnimationView) view.findViewById(R.id.ivEffect)).isLoading()) {
//            return;
//        }
//        resetEffectButton();
//        ((CircleAnimationView) view.findViewById(R.id.ivEffect)).setChecked(true);
//        if (mTimeEffectItem.getType() == type) {
//            mEffectListener.seekTo(mTimeEffectItem.getStartTime());
//            return;
//        }
//        mTimeEffectItem.setType(type);
//        if (mTimeEffectItem.getType() == EffectType.NONE) {
//            mTimeEffectItem.setStartTime(0);
//            mTimeEffectItem.setEndTime(0);
//        } else if (mTimeEffectItem.getType() == EffectType.REVERSE) {
//            mViewThumbnail.drawTimeEffectRect(mReverseStartTime, mReverseEndTime);
//        } else {
//            mViewThumbnail.drawTimeEffectRect(mTimeEffectStartTime, mTimeEffectEndTime);
//        }
//        reloadEffect();
//        mEffectListener.reload();
//        mEffectListener.seekTo(mTimeEffectItem.getStartTime());
    }

    private void resetEffectButton() {
        View view;
        int childCount = llEffectPanel.getChildCount();
        for (int i = 0; i < childCount; i++) {
            view = llEffectPanel.getChildAt(i);
            CircleAnimationView animView = (CircleAnimationView) view.findViewById(R.id.ivEffect);
            if (animView != null) {
                animView.setChecked(false);
            }
        }
    }

    public void setmEffectListener(OnEffectListener effectListener) {
        this.mEffectListener = effectListener;
    }

    @Override
    public void onPositionMove(float position) {
        llCategory.setVisibility(GONE);
        resetView();
        mEffectListener.seekTo(position);
        if (mEffectListener.isPlaying()) {
            mEffectListener.pause();
        }
    }

    @Override
    public void onPositionUp() {
        llCategory.setVisibility(GONE);
        resetView();
        reloadEffect();
        mEffectListener.reload();
        mEffectListener.seekTo(mTimeEffectItem.getStartTime());
    }

    private void reloadEffect() {
        mArrEffectInfo.clear();
        EffectEntity timeEffect = new EffectEntity();
//        if (mTimeEffectItem.getType() != EffectType.NONE) {
//            float startTime = mTimeEffectItem.getStartTime();
//            float endTime = mTimeEffectItem.getEndTime();
//            if (mTimeEffectItem.getType() == EffectType.REVERSE) {
//                mReverseStartTime = startTime;
//                mReverseEndTime = endTime;
//            } else {
//                mTimeEffectStartTime = startTime;
//                mTimeEffectEndTime = endTime;
//            }
//            timeEffect.effectType = mTimeEffectItem.getType();
//            timeEffect.setTimeRange(mTimeEffectItem.getStartTime(), mTimeEffectItem.getEndTime());
//            mArrEffectInfo.add(timeEffect);
//        }
        for (FilterEffectItem effectItem : mArrFilterEffectItem) {
            EffectEntity info = new EffectEntity();
//            info.effectType = effectItem.getType();
            info.setTimeRange(effectItem.getStartTime(), effectItem.getEndTime());
            mArrEffectInfo.add(info);
        }
    }

    public List<EffectEntity> getOriginalEffects() {
        return mTempArrEffect;
    }

    /**
     * 特效是否有变更
     *
     * @return
     */
    public boolean hasChangeEffects() {
        return !((mTempArrEffect == null ? "" : mTempArrEffect.toString()).equals(
                mArrEffectInfo == null ? "" : mArrEffectInfo.toString()));
    }

    public interface OnEffectListener {

        float getDuration();

        void seekTo(float i);

        void pause();

        float getCurrentPosition();

        boolean isPlaying();

        void start();

        void reload();

        void updateEffects();
    }


    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (!canTouchView) {
            return false;
        }
        llCategory.setVisibility(GONE);
        int action = event.getAction();
        if (effectCategory == EFFECT_TYPE_TIME) {
            return false;
        }
//        if (action == MotionEvent.ACTION_DOWN) {
//            if (isDown) {
//                return false;
//            }
//            isDown = true;
////            EffectType type = (EffectType) view.getTag();
//            if (mEffectScale == null) {
//                mEffectScale = AnimationUtils.loadAnimation(getContext(), R.anim.filter_effect_scale);
//                mEffectScale.setFillAfter(true);
//            }
//            CircleAnimationView animView = (CircleAnimationView) view.findViewById(R.id.ivEffect);
//            FilterEffectItem filterEffectItem = new FilterEffectItem(getContext(), type);
//            if (animView != null) {
//                animView.setAnimation(mEffectScale);
//                animView.startAnimation(mEffectScale);
//                animView.showShadow(filterEffectItem.getColor());
//            }
//            mArrFilterEffectItem.add(filterEffectItem);
//            float startTime = mEffectListener.getCurrentPosition();
//            if (startTime >= mEffectListener.getDuration()) {
//                startTime = 0;
//            }
//            mViewThumbnail.drawEffectRect(startTime, filterEffectItem);
//            EffectEntity info = new EffectEntity();
//            info.effectType = type;
//            info.setTimeRange(startTime, mEffectListener.getDuration());
//            mArrEffectInfo.add(info);
//            mEffectListener.updateEffects();
//            if (mEffectListener.isPlaying()) {
//                isPlaying = true;
//            } else {
//                isPlaying = false;
//                mEffectListener.start();
//            }
//            return true;
//        } else if (action == MotionEvent.ACTION_UP
//                || action == MotionEvent.ACTION_CANCEL) {
//            isDown = false;
////                if (!isPlaying) {
//            mEffectListener.pause();
////                }
//            setPosition(mEffectListener.getCurrentPosition());
//            cleanAnimation();
//            float startTime = mViewThumbnail.stopDrawEffectRect(mEffectListener.getCurrentPosition());
//            if (mArrEffectInfo != null && mArrEffectInfo.size() >= 1) {
//                mArrEffectInfo.get(mArrEffectInfo.size() - 1).setTimeRange(
//                        startTime, mEffectListener.getCurrentPosition());
//            }
//            ivEffectRevoke.bringToFront();
//            ivEffectRevoke.setVisibility(View.VISIBLE);
//            mEffectListener.updateEffects();
//        }
        return false;
    }


    /**
     * 清除动画效果
     */
    private void cleanAnimation() {
        View view;
        int childCount = llEffectPanel.getChildCount();
        for (int i = 0; i < childCount; i++) {
            view = llEffectPanel.getChildAt(i);
            CircleAnimationView animView = (CircleAnimationView) view.findViewById(R.id.ivEffect);
            if (animView != null) {
                animView.clearAnimation();
                animView.cancelShadow();
            }
        }
    }

}
