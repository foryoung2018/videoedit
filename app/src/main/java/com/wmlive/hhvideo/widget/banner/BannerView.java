package com.wmlive.hhvideo.widget.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.widget.UnScrollViewPager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 5/31/2017.
 * 轮播图控件
 */
public class BannerView<Item> extends RelativeLayout {
    private static final String TAG = BannerView.class.getSimpleName();

    public interface ViewFactory<Item> {
        View create(Item item, int position, ViewGroup container);
    }

    public interface TitleAdapter<Item> {
        CharSequence getTitle(Item item);
    }

    public static final int VISIBLE_AUTO = 0;
    public static final int VISIBLE_ALWAYS = 1;
    public static final int VISIBLE_NEVER = 2;

    // 设备密度
    private DisplayMetrics mDm;

    private long mDelay;        // 多久后开始滚动
    private long mInterval;     // 滚动间隔
    private boolean mIsAuto;    // 是否自动滚动
    private boolean mBarVisibleWhenLast;    // 最后一条 item 是否显示背景条
    private int mCurrentPosition;

    private boolean mIsStarted = false;
    private boolean mIsVisible = false;
    private boolean mIsResumed = true;
    private boolean mIsRunning = false;
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            Log.e(TAG, "running=" + mIsRunning + ",pre_pos=" + mCurrentPosition);
            if (mIsRunning) {
                vViewPager.setCurrentItem(mCurrentPosition + 1);
                if (isLoop() || mCurrentPosition + 1 < mDataList.size()) {
                    postDelayed(mRunnable, mInterval);
                } else {
                    mIsRunning = false;
                }
            }
        }
    };

    // 内容宽高
    private int mItemWidth;
    private int mItemHeight = FrameLayout.LayoutParams.WRAP_CONTENT;

    private UnScrollViewPager vViewPager;
    private LinearLayout vBottomBar;
    private TextView vTitleBar;
    private ViewPagerIndicator vIndicator;

    private int mIndicatorVisible;

    private List<Item> mDataList = new ArrayList();
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private ViewFactory<Item> mViewFactory;
    private TitleAdapter<Item> mTitleAdapter = new TitleAdapter<Item>() {
        @Override
        public CharSequence getTitle(Item o) {
            return o.toString();
        }
    };

    public BannerView(Context context) {
        this(context, null, 0);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressWarnings("ResourceType")
    public BannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        setOrientation(LinearLayout.VERTICAL);
        mDm = context.getResources().getDisplayMetrics();

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BannerView);

        boolean hasAspectRatio = ta.hasValue(R.styleable.BannerView_bvAspectRatio);
        float aspectRatio = ta.getFloat(R.styleable.BannerView_bvAspectRatio, 0f);
        boolean isLoop = ta.getBoolean(R.styleable.BannerView_bvIsLoop, true);
        mDelay = ta.getInt(R.styleable.BannerView_bvDelay, 5000);
        mInterval = ta.getInt(R.styleable.BannerView_bvInterval, 5000);
        mIsAuto = ta.getBoolean(R.styleable.BannerView_bvIsAuto, true);

        mBarVisibleWhenLast = ta.getBoolean(R.styleable.BannerView_bvBarVisibleWhenLast, true);

        int indicatorGravity = ta.getInt(R.styleable.BannerView_bvIndicatorGravity, Gravity.CENTER);
        int barColor = ta.getColor(R.styleable.BannerView_bvBarColor, Color.TRANSPARENT);
        float barPaddingLeft = ta.getDimension(R.styleable.BannerView_bvBarPaddingLeft, dp2px(10));
        float barPaddingTop = ta.getDimension(R.styleable.BannerView_bvBarPaddingTop, dp2px(7));
        float barPaddingRight = ta.getDimension(R.styleable.BannerView_bvBarPaddingRight, dp2px(10));
        float barPaddingBottom = ta.getDimension(R.styleable.BannerView_bvBarPaddingBottom, dp2px(7));

        int titleColor = ta.getColor(R.styleable.BannerView_bvTitleColor, Color.WHITE);
        float titleSize = ta.getDimension(R.styleable.BannerView_bvTitleSize, sp2px(14f));
        boolean titleVisible = ta.getBoolean(R.styleable.BannerView_bvTitleVisible, false);

        // auto, aways, never
        mIndicatorVisible = ta.getInteger(R.styleable.BannerView_bvIndicatorVisible, VISIBLE_AUTO);

        int indicatorWidth = ta.getDimensionPixelSize(R.styleable.BannerView_bvIndicatorWidth, dp2px(6));
        int indicatorHeight = ta.getDimensionPixelSize(R.styleable.BannerView_bvIndicatorHeight, dp2px(6));
        int indicatorGap = ta.getDimensionPixelSize(R.styleable.BannerView_bvIndicatorGap, dp2px(6));
        int indicatorColor = ta.getColor(R.styleable.BannerView_bvIndicatorColor, 0x88ffffff);
        int indicatorColorSelected = ta.getColor(R.styleable.BannerView_bvIndicatorColorSelected, Color.WHITE);

        Drawable indicatorDrawable = ta.getDrawable(R.styleable.BannerView_bvIndicatorDrawable);
        Drawable indicatorDrawableSelected = ta.getDrawable(R.styleable.BannerView_bvIndicatorDrawableSelected);
        ta.recycle();


        //create ViewPager
        vViewPager = isLoop ? new LoopViewPager(context) : new UnScrollViewPager(context);
        vViewPager.setOffscreenPageLimit(1);

        int[] systemAttrs = {android.R.attr.layout_width, android.R.attr.layout_height};
        TypedArray a = context.obtainStyledAttributes(attrs, systemAttrs);
        mItemWidth = a.getLayoutDimension(0, mDm.widthPixels);
        mItemHeight = a.getLayoutDimension(1, mItemHeight);
        a.recycle();

        if (mItemWidth < 0) {
            mItemWidth = mDm.widthPixels;
        }

        if (aspectRatio > 0) {
            if (aspectRatio > 1) {
                aspectRatio = 1;
            }
            mItemHeight = (int) ((mItemWidth - getPaddingLeft() - getPaddingRight()) * aspectRatio);
        }

        Log.e(TAG, "DcApp Banner w = " + mItemWidth + ", h = " + mItemHeight);
        LayoutParams lp = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mItemHeight);
        addView(vViewPager, lp);

        // bottom bar
        vBottomBar = new LinearLayout(context);
        vBottomBar.setBackgroundColor(barColor);
        vBottomBar.setPadding((int) barPaddingLeft, (int) barPaddingTop, (int) barPaddingRight, (int) barPaddingBottom);
        vBottomBar.setClipChildren(false);
        vBottomBar.setClipToPadding(false);
        vBottomBar.setOrientation(LinearLayout.HORIZONTAL);
        vBottomBar.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams commentLp =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        commentLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        commentLp.addRule(RelativeLayout.CENTER_HORIZONTAL);

//        addView(vBottomBar, new LayoutParams(mItemWidth - getPaddingLeft() - getPaddingRight(), LayoutParams.WRAP_CONTENT, Gravity.BOTTOM));
        addView(vBottomBar, commentLp);
        vIndicator = new ViewPagerIndicator(context);
        vIndicator.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dp2px(9)));
        vIndicator.setItemSize(indicatorWidth, indicatorHeight);
        vIndicator.setItemGap(indicatorGap);
        if (indicatorDrawable != null && indicatorDrawableSelected != null) {
            vIndicator.setItemDrawable(indicatorDrawable, indicatorDrawableSelected);
        } else {
            vIndicator.setItemColor(indicatorColor, indicatorColorSelected);
        }

        // title
        vTitleBar = new TextView(context);
        vTitleBar.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0F));
        vTitleBar.setSingleLine(true);
        vTitleBar.setTextColor(titleColor);
        vTitleBar.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
        vTitleBar.setEllipsize(TextUtils.TruncateAt.END);
        vTitleBar.setVisibility(titleVisible ? VISIBLE : INVISIBLE);

        if (indicatorGravity == Gravity.CENTER) {
            vBottomBar.addView(vIndicator);
        } else if (indicatorGravity == Gravity.RIGHT) {
            vBottomBar.addView(vTitleBar);
            vBottomBar.addView(vIndicator);

            vTitleBar.setPadding(0, 0, dp2px(10), 0);
            vTitleBar.setGravity(Gravity.LEFT);
        } else if (indicatorGravity == Gravity.LEFT) {
            vBottomBar.addView(vIndicator);
            vBottomBar.addView(vTitleBar);

            vTitleBar.setPadding(dp2px(10), 0, 0, 0);
            vTitleBar.setGravity(Gravity.RIGHT);
        }

    }

    /**
     * 设置图片宽高比
     *
     * @param ratio
     */
    public void setRatio(float ratio) {

    }

    public void setDelay(long delay) {
        this.mDelay = delay;
    }

    public void setInterval(long interval) {
        this.mInterval = interval;
    }

    public void setIsAuto(boolean isAuto) {
        this.mIsAuto = isAuto;
    }

    public void setIndicatorVisible(int value) {
        mIndicatorVisible = value;
    }

    public void setBarVisibleWhenLast(boolean value) {
        this.mBarVisibleWhenLast = value;
    }

    public void setBarColor(int barColor) {
        vBottomBar.setBackgroundColor(barColor);
    }

    public void setBarPadding(float left, float top, float right, float bottom) {
        vBottomBar.setPadding(dp2px(left), dp2px(top), dp2px(right), dp2px(bottom));
    }

    public void setTitleColor(int textColor) {
        vTitleBar.setTextColor(textColor);
    }

    public void setTitleSize(float sp) {
        vTitleBar.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
    }

    public void setTitleVisible(boolean isTitleVisible) {
        vTitleBar.setVisibility(isTitleVisible ? VISIBLE : INVISIBLE);
    }


    public boolean isLoop() {
        return vViewPager instanceof LoopViewPager;
    }

    public ViewPager getViewPager() {
        return vViewPager;
    }

    public ViewPagerIndicator getIndicator() {
        return vIndicator;
    }

    public void setViewFactory(@NonNull ViewFactory factory) {
        mViewFactory = factory;
    }

    public void setTitleAdapter(@NonNull TitleAdapter adapter) {
        mTitleAdapter = adapter;
    }

    public void setDataList(@NonNull List<Item> list) {
        mDataList = list;
        if (vViewPager != null) {
            vViewPager.setScrollable(list.size() != 1);
        }
    }

    public void setOnPageChangeListener(@NonNull ViewPager.OnPageChangeListener listener) {
        mOnPageChangeListener = listener;
    }


    void initViewPager() {
        vViewPager.setAdapter(mInternalPagerAdapter);
        vViewPager.removeOnPageChangeListener(mInternalPageListener);
        vViewPager.addOnPageChangeListener(mInternalPageListener);
        vViewPager.setOffscreenPageLimit(mDataList.size());
        mInternalPagerAdapter.notifyDataSetChanged();
        try {
            if (isLoop()) {
                setDuration(vViewPager, 500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void initIndicator() {
        vIndicator.setupWithViewPager(vViewPager);
        boolean visible = mIndicatorVisible == VISIBLE_ALWAYS || (mIndicatorVisible == VISIBLE_AUTO && mDataList.size() > 1);
        vIndicator.setVisibility(visible ? VISIBLE : INVISIBLE);
        vIndicator.setPosition(mCurrentPosition);
    }

    void setCurrentTitle(int position) {
        vTitleBar.setText(mTitleAdapter.getTitle(mDataList.get(position)));
    }


    boolean isValid() {
        if (vViewPager == null) {
            Log.e(TAG, "ViewPager is not exist!");
            return false;
        }
        if (mViewFactory == null) {
            Log.e(TAG, "ViewFactory must be not null!");
            return false;
        }
        if (mTitleAdapter == null) {
            Log.e(TAG, "TitleAdapter must be not null!");
            return false;
        }
        if (mDataList == null || mDataList.size() == 0) {
            Log.e(TAG, "DataList must be not empty!");
            return false;
        }
        return true;
    }

    public void start() {
        if (!isValid()) {
            return;
        }

        if (mCurrentPosition > mDataList.size() - 1) {
            mCurrentPosition = 0;
        }
        initViewPager();
        initIndicator();

        setCurrentTitle(mCurrentPosition);
        mIsStarted = true;
        KLog.i("--start-update");
        update();
    }

    public void resume() {
        if (mIsStarted && !mIsResumed) {
            KLog.i("--resume-update");
            mIsResumed = true;
            update();
        }
    }

    public void stop() {
        if (mIsStarted) {
            KLog.i("--stop-update");
            mIsResumed = false;
            update();
        }
    }

    void update() {
        if (!isValid()) {
            return;
        }
        boolean running = mIsVisible && mIsResumed && mIsStarted && mIsAuto && mDataList.size() > 1 && (isLoop() || mCurrentPosition + 1 <
                mDataList.size());
        if (running != mIsRunning) {
            if (running) {
                postDelayed(mRunnable, mDelay);
            } else {
                removeCallbacks(mRunnable);
            }
            mIsRunning = running;
        }
        Log.e(TAG, "update:running=" + mIsRunning + ",visible=" + mIsVisible + ",started=" + mIsStarted + ",resumed=" + mIsResumed);
        Log.e(TAG, "update:auto=" + mIsAuto + ",loop=" + isLoop() + ",size=" + mDataList.size() + ",current=" + mCurrentPosition);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIsVisible = false;
        if (mIsStarted) {
            KLog.i("---onDetachedFromWindow_update");
            update();
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        mIsVisible = visibility == VISIBLE;
        if (mIsStarted) {
            KLog.i("---onWindowVisibilityChanged_update");
            update();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mIsResumed = false;
                update();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsResumed = true;
                update();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private int dp2px(float dp) {
        return (int) (dp * mDm.density + 0.5f);
    }

    private float sp2px(float sp) {
        return sp * mDm.scaledDensity;
    }

    private ViewPager.OnPageChangeListener mInternalPageListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageSelected(int position) {

            mCurrentPosition = position % mDataList.size();
            Log.e(TAG, "onPageSelected, current_pos=" + mCurrentPosition);
            setCurrentTitle(mCurrentPosition);
            vBottomBar.setVisibility(mCurrentPosition == mDataList.size() - 1 && !mBarVisibleWhenLast ? GONE : VISIBLE);

            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageSelected(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrollStateChanged(state);
            }
        }
    };

    private ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    private PagerAdapter mInternalPagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return mDataList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = mViewFactory.create(mDataList.get(position), position, container);
            container.addView(view, layoutParams);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != onItemClickListener) {
                        onItemClickListener.onItemClick(position, mDataList.get(position));
                    }
                }
            });
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    };

    private OnItemClickListener<Item> onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener<Item> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener<Item> {
        void onItemClick(int position, Item item);
    }

    private void setDuration(ViewPager pager, int duration) {
        try {
            FixedSpeedScroller scroller = new FixedSpeedScroller(pager.getContext(), new AccelerateDecelerateInterpolator(), duration);
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            field.set(pager, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class FixedSpeedScroller extends Scroller {
        private int mDuration = 500;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator, int duration) {
            super(context, interpolator);
            this.mDuration = duration;
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, this.mDuration);
        }
    }
}