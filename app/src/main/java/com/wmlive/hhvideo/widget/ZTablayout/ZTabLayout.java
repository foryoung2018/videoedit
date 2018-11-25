package com.wmlive.hhvideo.widget.ZTablayout;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.text.style.ReplacementSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.utils.ScreenUtil;

import org.w3c.dom.Text;

import cn.wmlive.hhvideo.R;

/**
 * 自定义tablayout
 */
public class ZTabLayout extends HorizontalScrollView {
    //默认字体大小
    private final int DEFAULT_NORMAL_TEXT_SIZE_SP = ScreenUtil.sp2px(DCApplication.getDCApp(), 14);
    private int mNormalTextSize = DEFAULT_NORMAL_TEXT_SIZE_SP;
    //选中字体大小
    private final int DEFAULT_SELECT_TEXT_SIZE_SP = ScreenUtil.sp2px(DCApplication.getDCApp(), 16);
    private int mSelectTextSize = DEFAULT_SELECT_TEXT_SIZE_SP;
    //字体颜色
    private final int DEFAULT_NORMAL_TEXT_COLOR = Color.BLACK;
    private final int DEFAULT_SELECT_TEXT_COLOR = Color.RED;
    private ColorStateList mTextColor;
    //tab背景
    private int mTabBackgroundResourceId;
    //指示器高度
    private final int DEFAULT_INDICATOR_HEIGHT_DP = ScreenUtil.dip2px(DCApplication.getDCApp(), 2);
    private int mIndicatorHeight = DEFAULT_INDICATOR_HEIGHT_DP;
    //指示器颜色
    private final int DEFAULT_INDICATOR_COLOR = Color.RED;
    private int mIndicatorColor = DEFAULT_INDICATOR_COLOR;
    //tab最小宽度
    private final int DEFAULT_TAB_MIN_WIDTH = ScreenUtil.dip2px(DCApplication.getDCApp(), 50);
    private int mMinTabWidth = DEFAULT_TAB_MIN_WIDTH;
    //tab padding
    private int mTabPaddingLeft;
    private int mTabPaddingRight;
    private int mTabPaddingTop;
    private int mTabPaddingBottom;
    //关联的viewpager
    private ViewPager mViewPager;
    private PagerAdapter pagerAdapter;

    //第一个子View
    private IndicationTabLayout mTabContainer;
    //Tab总数
    private int mTabCount;
    //当前选中的Tab
    private int mCurrentTabPosition;
    //当前切换Tab的偏移量
    private float mCurrentOffset;

    //tab高度
    private int mTabHeight = 0;
    //tab宽度
    private int mTabWidth = 0;
    //中间线
    private final int DEFAULT_DIVIDER_WIDTH = ScreenUtil.dip2px(DCApplication.getDCApp(), 1);
    private int mDividerWidth = DEFAULT_DIVIDER_WIDTH;
    private final int DEFAULT_DIVIDER_COLOR = Color.GRAY;
    private int mDividerColor = DEFAULT_DIVIDER_COLOR;
    private Paint mDividerPaint;
    private int DEFAULT_DIVIDER_PADDING = ScreenUtil.dip2px(DCApplication.getDCApp(), 5);
    private int mDividerPadding = DEFAULT_DIVIDER_PADDING;
    private boolean hasShowDivider = false;
    private boolean mTabIsBisectNoViewPager = true;

    //红点显示
    private final int DEFAULT_MSG_ROUND_COLOR = Color.RED;
    private int mMsgRoundColor = DEFAULT_MSG_ROUND_COLOR;
    private SparseBooleanArray mInitSetMap;
    private SparseIntArray mMsgNumMap;
    private Paint mMsgPaint;
    private Paint mMsgNumPaint;
    private int mMsgNumColor = Color.WHITE;
    private int mMsgTextSizeSp = ScreenUtil.sp2px(DCApplication.getDCApp(), 8);
    private int mMsgPadding;
    private int[] mResIds;
    private TabDrawableLocation mLocation;
    private String[] mTitles;


    public ZTabLayout(Context context) {
        this(context, null);
    }

    public ZTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initStyle(context, attrs);
        setFillViewport(true);
        setHorizontalScrollBarEnabled(false);
        mTabContainer = new IndicationTabLayout(context);
        mTabContainer.setSelectedIndicatorColor(mIndicatorColor);
        mTabContainer.setSelectedIndicatorHeight(mIndicatorHeight);
        Log.d("ZTabLayout", "ZTabLayout: " + mIndicatorHeight);
        addView(mTabContainer, 0, new HorizontalScrollView.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMsgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMsgNumPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInitSetMap = new SparseBooleanArray();
        mMsgNumMap = new SparseIntArray();
    }

    public void setSelectedIndicatorHeight(int IndicatorHeight) {
        mTabContainer.setSelectedIndicatorHeight(IndicatorHeight);
    }

    private void initStyle(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ZTabLayout, 0, 0);
        mNormalTextSize = typedArray.getDimensionPixelSize(R.styleable.ZTabLayout_tab_normal_textSize, DEFAULT_NORMAL_TEXT_SIZE_SP);
        mSelectTextSize = typedArray.getDimensionPixelSize(R.styleable.ZTabLayout_tab_select_textSize, DEFAULT_SELECT_TEXT_SIZE_SP);
        mTextColor = typedArray.getColorStateList(R.styleable.ZTabLayout_tab_textColor);
        if (mTextColor == null)
            mTextColor = createDefaultTextColor();

        mTabBackgroundResourceId = typedArray.getResourceId(R.styleable.ZTabLayout_tab_bg_resource_id, 0);
        mIndicatorHeight = typedArray.getDimensionPixelSize(R.styleable.ZTabLayout_tab_indicatorHeight, DEFAULT_INDICATOR_HEIGHT_DP);
        mIndicatorColor = typedArray.getColor(R.styleable.ZTabLayout_tab_indicatorColor, DEFAULT_INDICATOR_COLOR);
        mMinTabWidth = typedArray.getColor(R.styleable.ZTabLayout_tab_min_width, DEFAULT_TAB_MIN_WIDTH);
        mDividerColor = typedArray.getColor(R.styleable.ZTabLayout_tab_dividerColor, DEFAULT_DIVIDER_COLOR);
        mDividerWidth = typedArray.getDimensionPixelSize(R.styleable.ZTabLayout_tab_dividerWidth, DEFAULT_DIVIDER_WIDTH);
        mDividerPadding = typedArray.getDimensionPixelSize(R.styleable.ZTabLayout_tab_dividerPadding, DEFAULT_DIVIDER_PADDING);
        mTabPaddingLeft = typedArray.getDimensionPixelSize(R.styleable.ZTabLayout_tab_PaddingLeft, 0);
        mTabPaddingTop = typedArray.getDimensionPixelSize(R.styleable.ZTabLayout_tab_PaddingTop, 0);
        mTabPaddingRight = typedArray.getDimensionPixelSize(R.styleable.ZTabLayout_tab_PaddingRight, 0);
        mTabPaddingBottom = typedArray.getDimensionPixelSize(R.styleable.ZTabLayout_tab_PaddingBottom, 0);
        hasShowDivider = typedArray.getBoolean(R.styleable.ZTabLayout_tab_dividerShow, false);
        mTabIsBisectNoViewPager = typedArray.getBoolean(R.styleable.ZTabLayout_tab_is_bisect_no_viewpager, true);
        mTabHeight = typedArray.getDimensionPixelSize(R.styleable.ZTabLayout_tab_height, 0);
        mTabWidth = typedArray.getDimensionPixelSize(R.styleable.ZTabLayout_tab_width, 0);
        typedArray.recycle();
    }

    private ColorStateList createDefaultTextColor() {
        ColorStateList colorStateList = new ColorStateList(new int[][]{{android.R.attr.state_selected}
                , {0}}, new int[]{DEFAULT_SELECT_TEXT_COLOR, DEFAULT_NORMAL_TEXT_COLOR});
        return colorStateList;
    }


    public void setupWithViewPager(ViewPager viewPager) {
        this.mViewPager = viewPager;
        if (viewPager == null)
            throw new IllegalArgumentException("viewpager not is null");
        pagerAdapter = viewPager.getAdapter();
        if (pagerAdapter == null)
            throw new IllegalArgumentException("pagerAdapter not is null");
        this.mViewPager.addOnPageChangeListener(new TabPagerChanger());
        mTabCount = pagerAdapter.getCount();
        mCurrentTabPosition = viewPager.getCurrentItem();
        notifyTabChanged();
    }


    public void setupWithoutViewPager(String[] title, int currentTabPosition) {
        setupWithoutViewPager(title, currentTabPosition, null);
    }

    public void setupWithoutViewPager(String[] title, int currentTabPosition, int[] resIds) {
        setupWithoutViewPager(title, currentTabPosition, resIds, TabDrawableLocation.Left);
    }

    public void setupWithoutViewPager(String[] title, int currentTabPosition, int[] resIds, TabDrawableLocation location) {
        mResIds = resIds;
        mLocation = location;
        mTabCount = title.length;
        mTitles = title;
        Log.d("ZTabLayout", "setupWithoutViewPager: title=" + title);
        notifyTabChanged(currentTabPosition);
    }

    public void notifyTabChanged() {
        mTabContainer.removeAllViews();
        for (int i = 0; i < mTabCount; i++) {
            final int currentPosition = i;
            TextView tabTextView = createTextView();
            tabTextView.setPadding(mTabPaddingLeft, mTabPaddingTop, mTabPaddingRight, mTabPaddingBottom);
            tabTextView.setText(pagerAdapter.getPageTitle(i));
            tabTextView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mViewPager.setCurrentItem(currentPosition);
                }
            });
            mTabContainer.addView(tabTextView, new LinearLayout.LayoutParams(0, mTabHeight != 0 ? mTabHeight : LinearLayout.LayoutParams.MATCH_PARENT, 1));
        }

        setSelectedTabView(mCurrentTabPosition);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                setScrollPosition(mCurrentTabPosition, 0);
            }
        }, 0);
    }

    public void notifyTabChanged(int currentTabPosition) {
        mTabContainer.removeAllViews();
        for (int i = 0; i < mTabCount; i++) {
            final int currentPosition = i;
            TextView tabTextView = createTextView();
            tabTextView.setPadding(mTabPaddingLeft, mTabPaddingTop, mTabPaddingRight, mTabPaddingBottom);
            tabTextView.setText(mTitles[i]);
            tabTextView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnTabSelectedListener != null) {
                        mOnTabSelectedListener.onTabSelected(currentPosition);
                    }
                    setSelectedTabView(currentPosition);
                    setScrollPosition(currentPosition, 0);
                }
            });

            if (mTabIsBisectNoViewPager) {
                mTabContainer.addView(tabTextView, new LinearLayout.LayoutParams(0, mTabHeight != 0 ? mTabHeight : LinearLayout.LayoutParams.MATCH_PARENT, 1));
            } else {

                mTabContainer.addView(tabTextView, new LinearLayout.LayoutParams(mTabWidth != 0 ? mTabWidth : LinearLayout.LayoutParams.WRAP_CONTENT, mTabHeight != 0 ? mTabHeight : LinearLayout.LayoutParams.MATCH_PARENT));

            }


        }
        mCurrentTabPosition = currentTabPosition;
        setSelectedTabView(mCurrentTabPosition);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                setScrollPosition(mCurrentTabPosition, 0);
            }
        }, 0);
    }

    private TextView createTextView() {
        TextView textView = new TextView(getContext());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mNormalTextSize);
        textView.setTextColor(mTextColor);
        textView.setMinWidth(mMinTabWidth);
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(mTabBackgroundResourceId);
        return textView;
    }

    public void selectTab(int position) {
        if (mViewPager != null) {
            mViewPager.setCurrentItem(position);
            return;
        }
        if (mOnTabSelectedListener != null) {
            mOnTabSelectedListener.onTabSelected(position);
        }
        setSelectedTabView(position);
        setScrollPosition(position, 0);
    }

    public void selectTab(String s) {
        if (TextUtils.isEmpty(s) || mTitles == null || mTitles.length == 0)
            return;
        for (int i = 0; i < mTitles.length; i++) {
            if (mTitles[i] == s) {
                selectTab(i);
                break;
            }
        }
    }

    public enum TabDrawableLocation {

        Left, Top, Right, Bottom

    }

    public void setTabDrawable(int position, int resId, TabDrawableLocation location) {

        if (getTabView(position) == null)
            return;
        Drawable drawable = getResources().getDrawable(resId);
        drawable.setState(getTabView(position).getDrawableState());
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        SpannableString spannableString;
        VerticalImageSpan imageSpan = new VerticalImageSpan(drawable);


        if (location == TabDrawableLocation.Bottom) {

            spannableString = new SpannableString(mTitles[position] + "\n" + " ");
            spannableString.setSpan(imageSpan, mTitles[position].length(), mTitles[position].length() + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        } else if (location == TabDrawableLocation.Top) {

            spannableString = new SpannableString(" " + "\n" + mTitles[position]);
            spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        } else if (location == TabDrawableLocation.Right) {

            spannableString = new SpannableString(mTitles[position] + " ");
            spannableString.setSpan(imageSpan, mTitles[position].length(), mTitles[position].length() + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        } else {

            //默认图片在 左边；
            spannableString = new SpannableString("  " + mTitles[position] + " ");
            spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

//            // TODO: 2017/6/27  增加选中背景逻辑
//            if (drawable instanceof StateListDrawable) {
//        boolean selected = getTabView(position).isSelected();
//                int green = getResources().getColor(R.color.main_green_color);
//                int transparent = getResources().getColor(R.color.transparent);
//                RadiusBackgroundSpan backgroundSpan = new RadiusBackgroundSpan(selected ? green : transparent, 100);
//                spannableString.setSpan(backgroundSpan, 1, mTitles[position].length() + 5, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//            }

        }

        getTabView(position).setText(spannableString);
    }


    public void setSelectedTabView(int position) {
        for (int i = 0; i < mTabCount; i++) {
            View view = mTabContainer.getChildAt(i);
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                textView.setSelected(position == i);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, position == i ? mSelectTextSize : mNormalTextSize);
                if (mResIds != null)
                    setTabDrawable(i, mResIds[i], mLocation);
            }
        }
    }


    public TextView getTabView(int position) {
        if (mTabCount - 1 < position) {
            throw new ArrayIndexOutOfBoundsException("此位置没有 tabview！ ");
        }

        View view = mTabContainer.getChildAt(position);
        if (view instanceof TextView) {
            return (TextView) view;
        }
        return null;
    }

    private void setScrollPosition(int position, float positionOffset) {
        this.mCurrentTabPosition = position;
        this.mCurrentOffset = positionOffset;
        mTabContainer.setIndicatorPositionFromTabPosition(position, positionOffset);
        scrollTo(calculateScrollXForTab(mCurrentTabPosition, mCurrentOffset), 0);
    }

    private int calculateScrollXForTab(int position, float positionOffset) {
        try {
            final View selectedChild = mTabContainer.getChildAt(position);
            if (selectedChild != null) {
                final View nextChild = position + 1 < mTabContainer.getChildCount()
                        ? mTabContainer.getChildAt(position + 1)
                        : null;
                final int selectedWidth = selectedChild != null ? selectedChild.getWidth() : 0;
                final int nextWidth = nextChild != null ? nextChild.getWidth() : 0;
                return selectedChild.getLeft()
                        + ((int) ((selectedWidth + nextWidth) * positionOffset * 0.5f))
                        + (selectedChild.getWidth() / 2)
                        - (getWidth() / 2);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInEditMode() || mTabCount <= 0) {
            return;
        }
        int height = getHeight();
        int paddingLeft = getPaddingLeft();
        // 画中间线间隔
        if (mDividerWidth > 0 && hasShowDivider) {
            mDividerPaint.setStrokeWidth(mDividerWidth);
            mDividerPaint.setColor(mDividerColor);
            for (int i = 0; i < mTabCount - 1; i++) {
                View tab = mTabContainer.getChildAt(i);
                canvas.drawLine(paddingLeft + tab.getRight(), mDividerPadding, paddingLeft + tab.getRight(), height - mDividerPadding, mDividerPaint);
            }
        }
        //画消息提示
        for (int i = 0; i < mTabCount - 1; i++) {
            if (mInitSetMap.get(i)) {
                updateMsgPosition(canvas, mTabContainer.getChildAt(i), mMsgNumMap.get(i));
            }
        }
    }

    private void updateMsgPosition(final Canvas canvas, final View updateView, final int msgNum) {
        if (updateView == null)
            return;
        int circleX, circleY;
        if (updateView.getWidth() > 0) {
            int selectTextPadding = (int) ((updateView.getWidth() - measureTextLength(updateView)) / 2 + 0.5f);
            circleX = updateView.getRight() - selectTextPadding + mMsgPadding;
            circleY = (int) ((mTabContainer.getHeight() - measureTextHeight(updateView)) / 2 - mMsgPadding);
            drawMsg(canvas, circleX, circleY, msgNum);
        }
    }

    private void drawMsg(Canvas canvas, int mMsgCircleX, int mMsgCircleY, int mMsgNum) {
        mMsgPaint.setStyle(Paint.Style.FILL);
        mMsgPaint.setColor(mMsgRoundColor);
        if (mMsgNum > 0) {
            mMsgNumPaint.setTextSize(mMsgTextSizeSp);
            mMsgNumPaint.setColor(mMsgNumColor);
            mMsgNumPaint.setTextAlign(Paint.Align.CENTER);
            String showTxt = mMsgNum > 99 ? "99+" : String.valueOf(mMsgNum);
            int mMsgNumRadius = (int) Math.max(mMsgNumPaint.descent() - mMsgNumPaint.ascent(),
                    mMsgNumPaint.measureText(showTxt)) / 2 + ScreenUtil.dip2px(DCApplication.getDCApp(), 2);
            canvas.drawCircle(mMsgCircleX + mMsgNumRadius, mMsgCircleY, mMsgNumRadius, mMsgPaint);
            Paint.FontMetricsInt fontMetrics = mMsgNumPaint.getFontMetricsInt();
            int baseline = (int) ((2 * mMsgCircleY - (fontMetrics.descent - fontMetrics.ascent)) / 2 - fontMetrics.ascent + 0.5f);
            canvas.drawText(showTxt, mMsgCircleX + mMsgNumRadius,
                    baseline, mMsgNumPaint);
        } else {
            canvas.drawCircle(mMsgCircleX + ScreenUtil.dip2px(DCApplication.getDCApp(), 2), mMsgCircleY, ScreenUtil.dip2px(DCApplication.getDCApp(), 2), mMsgPaint);
        }
    }

    public void showMsg(int msgPosition, int msgNum, int msgPadding) {
        mInitSetMap.put(msgPosition, true);
        this.mMsgNumMap.put(msgPosition, msgNum);
        mMsgPadding = msgPadding;
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void hideMsg(int msgPosition) {
        mInitSetMap.put(msgPosition, false);
        this.mMsgNumMap.delete(msgPosition);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    private float measureTextLength(View measureView) {
        if (measureView instanceof TextView) {
            TextView textView = ((TextView) measureView);
            String text = textView.getText().toString();
            return textView.getPaint().measureText(text);
        }
        return 0;
    }

    private float measureTextHeight(View measureView) {
        if (measureView instanceof TextView) {
            TextView textView = ((TextView) measureView);
            Paint textPaint = textView.getPaint();
            return textPaint.descent() - textPaint.ascent();
        }
        return 0;
    }

    private OnTabSelectedListener mOnTabSelectedListener;

    public void setOnTabSelectedListener(OnTabSelectedListener onTabSelectedListener) {
        mOnTabSelectedListener = onTabSelectedListener;
    }

    private class TabPagerChanger implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            setScrollPosition(position, positionOffset);
            Log.d("TabPagerChanger", position + "," + positionOffset);
        }

        @Override
        public void onPageSelected(int position) {
            setSelectedTabView(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    public interface OnTabSelectedListener {

        /**
         * Called when a tab enters the selected state.
         *
         * @param position The tab that was selected
         */
        public void onTabSelected(int position);

        /**
         * Called when a tab exits the selected state.
         *
         * @param position The tab that was unselected
         */
        public void onTabUnselected(int position);

        /**
         * Called when a tab that is already selected is chosen again by the user. Some applications
         * may use this action to return to the top level of a category.
         *
         * @param position The tab that was reselected.
         */
        public void onTabReselected(int position);
    }

    /**
     * 垂直居中的ImageSpan
     */
    public class VerticalImageSpan extends ImageSpan {

        public VerticalImageSpan(Drawable drawable) {
            super(drawable);
        }

        public int getSize(Paint paint, CharSequence text, int start, int end,
                           Paint.FontMetricsInt fontMetricsInt) {
            Drawable drawable = getDrawable();
            Rect rect = drawable.getBounds();
            if (fontMetricsInt != null) {
                Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
                int fontHeight = fmPaint.bottom - fmPaint.top;
                int drHeight = rect.bottom - rect.top;

                int top = drHeight / 2 - fontHeight / 4;
                int bottom = drHeight / 2 + fontHeight / 4;

                fontMetricsInt.ascent = -bottom;
                fontMetricsInt.top = -bottom;
                fontMetricsInt.bottom = top;
                fontMetricsInt.descent = top;
            }
            return rect.right;
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end,
                         float x, int top, int y, int bottom, Paint paint) {
            Drawable drawable = getDrawable();
            canvas.save();
            int transY = 0;
            transY = ((bottom - top) - drawable.getBounds().bottom) / 2 + top;
            canvas.translate(x, transY);
            drawable.draw(canvas);
            canvas.restore();
        }
    }

    public class RadiusBackgroundSpan extends ReplacementSpan {

        private int mSize;
        private int mColor;
        private int mRadius;
        private int mPaddingY = ScreenUtil.dip2px(getContext(), 3);

        /**
         * @param color  背景颜色
         * @param radius 圆角半径
         */
        public RadiusBackgroundSpan(int color, int radius) {
            mColor = color;
            mRadius = radius;
        }

        @Override
        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            mSize = (int) (paint.measureText(text, start, end));
            return mSize;
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            int color = paint.getColor();//保存文字颜色
            paint.setColor(mColor);//设置背景颜色
            paint.setAntiAlias(true);// 设置画笔的锯齿效果
            RectF oval = new RectF(x, y + paint.ascent() - mPaddingY, x + mSize, y + paint.descent() + mPaddingY);
            //设置文字背景矩形，x为span其实左上角相对整个TextView的x值，y为span左上角相对整个View的y值。paint.ascent()获得文字上边缘，paint.descent()获得文字下边缘
            canvas.drawRoundRect(oval, mRadius, mRadius, paint);//绘制圆角矩形，第二个参数是x半径，第三个参数是y半径
            paint.setColor(color);//恢复画笔的文字颜色
            canvas.drawText(text, start, end, x, y, paint);//绘制文字
        }
    }
}
