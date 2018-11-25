package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import cn.wmlive.hhvideo.R;


/**
 * previewActivity 播放器进度条 左右有把手的进度条
 */
public class ExtRangeSeekbarPlus extends View {

    private float touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    /**
     * 未知thumb标识值
     */
    public static final int NONE_THUMB_PRESSED = 0;
    /**
     * 选择范围最小时，thumb标识值
     */
    public static final int MIN_THUMB_PRESSED = 1;
    /**
     * 选择范围最大时，thumb标识值
     */
    public static final int MAX_THUMB_PRESSED = 2;
    /**
     * 指定当前值时，thumb标识值
     */
    public static final int CURRENT_THUMB_PRESSED = 3;
    /**
     * 多视频编辑
     */
    private boolean isMultEdit = false;

    private Paint pProgress = new Paint(),
            pLine = new Paint(), pShadow = new Paint();
    private Rect shadowLeft = new Rect(), shadowRight = new Rect(), rectHandle = new Rect();
    private final String TAG = this.toString();
    private Resources res;
    private Drawable mHandle;
    private Drawable mDrawable;
    private Drawable mCropMusicDrawable;
    private int mSeekbarWidth = 0;
    private int mPadding;
    private boolean isCropMusic = false;
    private int minValue = 0;
    private long pressValue = 0;

    public ExtRangeSeekbarPlus(Context context, AttributeSet attrs) {
        super(context, attrs);
        res = getResources();
        /* 设置渐变色 */

        pProgress.setColor(res.getColor(R.color.white));
        pProgress.setAntiAlias(true);
        pLine.setAntiAlias(true);
        pLine.setColor(res.getColor(R.color.white));
        pLine.setStrokeWidth(3);

        mHandle = res.getDrawable(R.drawable.trim_seekbar_handle);
        mDrawable = res.getDrawable(R.drawable.trim_seekbar_handle_right);
        mCropMusicDrawable = res.getDrawable(R.drawable.trim_seekbar_handle_music);

        pShadow.setAntiAlias(true);
        pShadow.setStyle(Style.FILL);
        pShadow.setColor(res.getColor(R.color.transparent_black50));
        HANDWIDTH = res.getDimensionPixelSize(R.dimen.dimen_10);
    }

    private long mDuration;

    /**
     * 设置总时长
     *
     * @param duration
     */
    public void setDuration(float duration) {
        mDuration = (long) (duration * 1000);
        max = mDuration;
        invalidate();
    }

    public void setPadding(int padding) {
        mPadding = padding;
    }

    private long min, max;
    private int HANDWIDTH = 20;// 定义左右把手的图片宽度为20px


    public void setMin(float min) {
        min *= 1000;
        if (bottom == 0) {
            initTopBottom();
        }
        if (min > max || min < 0) {
            min = 0;
        }
        this.min = (long) min;
        int mleft = (int) (getSeekbarWith() * min / mDuration) + mPadding;
        rectHandle.set(mleft, top, rectHandle.right, bottom);
        invalidate();
    }

    public int getHandleRight() {
        return rectHandle.right;
    }


    private final int DEFAULTMINDURATION = 1000;// 两个把手距离 最少1s
    private int mMinDuration = DEFAULTMINDURATION;

    public long getmDuration() {
        return mDuration;
    }

    private int mMaxDuration = 0;

    public long getPressValue() {
        return pressValue;
    }

    /**
     * 设置最小持续时间
     *
     * @param min 单位秒(至少1秒)
     */
    public void setMinDuration(float min) {
        mMinDuration = (int) (min * 1000);
        if (mMinDuration < DEFAULTMINDURATION) {
            mMinDuration = DEFAULTMINDURATION;
        }
        if (mMinDuration > mMaxDuration && mMaxDuration != 0) {
            mMinDuration = mMaxDuration;
        }
    }

    public void setSeekbarWidth(int width) {
        mSeekbarWidth = width;
    }

    /**
     * 设置最大持续时间
     *
     * @param max 单位秒(设置0为不限制)
     */
    public void setMaxDuration(float max) {
        mMaxDuration = (int) (max * 1000);
        if (mMaxDuration < DEFAULTMINDURATION) {
            mMaxDuration = DEFAULTMINDURATION;
        }
        if (mMaxDuration <= 0) {
            mMaxDuration = 0;
        }
        if (mMinDuration > mMaxDuration) {
            mMaxDuration = mMinDuration;
        }
    }

    /**
     * 设置显示的区域
     *
     * @param min
     * @param max
     */
    public void setSeekBarRangeValues(float min, float max) {
        setMax(max);
        setMin(min);
    }

    public void setMax(float max) {
        max *= 1000;
        if (max > mDuration) {
            max = mDuration;
        }
        this.max = (long) max;
        int mleft = (int) (HANDWIDTH + (getSeekbarWith() * max / mDuration)) + mPadding;
        rectHandle.set(rectHandle.left, top, mleft + HANDWIDTH, bottom);
        invalidate();
    }

    public float getSelectedMinValue() {
        return min / 1000f;
    }

    public float getSelectedMaxValue() {
        return max / 1000f;
    }


    /**
     * 0-mDuration 的组件宽度，去除两个把手的宽度
     *
     * @return
     */
    private double getSeekbarWith() {
        if (mSeekbarWidth != 0) {
            return mSeekbarWidth - 2 * HANDWIDTH + 0.0 - 2 * mPadding;
        } else {
            return getWidth() - 2 * HANDWIDTH + 0.0 - 2 * mPadding;
        }
    }

    /**
     * 是否多视频编辑
     *
     * @param multEdit
     */
    public void setMultEdit(boolean multEdit) {
        isMultEdit = multEdit;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        initTopBottom();

    }

    private int top, bottom;

    private void initTopBottom() {
        top = 0;
        bottom = getBottom();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initTopBottom();
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                | Paint.FILTER_BITMAP_FLAG));

        int leftP = (int) (getSeekbarWith() * min / mDuration) + mPadding;
        int rightP = (int) (HANDWIDTH + (getSeekbarWith() / mDuration * max)) + mPadding;
        shadowLeft.set(getLeft(), top, leftP + HANDWIDTH, bottom);
        shadowRight.set(rightP, top, getRight(), bottom);
        canvas.drawRect(shadowLeft, pShadow);
        canvas.drawRect(shadowRight, pShadow);
        if (isCropMusic) {
            mCropMusicDrawable.setBounds(rectHandle);
            mCropMusicDrawable.draw(canvas);
        } else {
            if (isMultEdit) {
                mDrawable.setBounds(rectHandle);
                mDrawable.draw(canvas);
            } else {
                mHandle.setBounds(rectHandle);
                mHandle.draw(canvas);
            }
        }

    }


    private int pressedThumb = NONE_THUMB_PRESSED;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pressValue = (long) ((event.getX() - HANDWIDTH)
                        / getSeekbarWith() * mDuration);
                if (pressValue < 0) {
                    pressValue = 0;
                }
                if (pressValue > mDuration) {
                    pressValue = mDuration;
                }
//                KLog.i("xxxx", "ACTION_DOWN setValue " + pressValue);
                int x = (int) event.getX();
                pressedThumb = evalPressedThumb(x);
                mSeekbarListener.beginTouch(pressedThumb);
                if (pressedThumb != NONE_THUMB_PRESSED) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
//                if (event.getY() > getHeight() || 0 > event.getY()
//                        || event.getX() < getLeft() || event.getX() > getRight()) {
//                    invalidate();
//                    KLog.i("hsing", "SeekBar onTouchEvent ACTION_MOVE return false");
//                    return false;
//                }
                if ((pressedThumb != NONE_THUMB_PRESSED)) {
                    long setValue = (long) ((event.getX() - HANDWIDTH)
                            / getSeekbarWith() * mDuration);
                    if (setValue < 0) {
                        setValue = 0;
                    }
                    if (setValue > mDuration) {
                        setValue = mDuration;
                    }
//                    KLog.i("xxxx", "ACTION_MOVE setValue " + setValue);
                    if (MIN_THUMB_PRESSED == pressedThumb) {
                        if (!isCropMusic && !isMultEdit) {
                            /** 单视频编辑最小值可移动 */
                            long maxPosition = max - mMinDuration;
                            if (setValue > maxPosition) {
                                long maxValue = setValue + mMinDuration;
                                if (maxValue < mDuration) {
                                    setMax(maxValue / 1000f);
                                } else {
                                    setMax(mDuration / 1000f);
                                    setValue = mDuration - mMinDuration;
                                }
                            }
                            if (mMaxDuration != 0) {
                                long minPosition = max - mMaxDuration;
                                if (setValue < minPosition) {
                                    setMax((setValue + mMaxDuration) / 1000f);
                                }
                            }
                            setMin(setValue / 1000f);
                            mSeekbarListener.rangeSeekBarValuesChanging(setValue / 1000f);
                        }
                        return true;
                    } else if (MAX_THUMB_PRESSED == pressedThumb) {
                        long minPosition = min + mMinDuration;
                        if (setValue < minPosition) {
                            long minValue = setValue - mMinDuration;
                            if (minValue >= 0) {
                                setMin(minValue / 1000f);
                            } else {
                                setMin(0);
                                setValue = mMinDuration;
                            }
                        }
                        if (mMaxDuration != 0) {
                            long maxPosition = min + mMaxDuration;
                            if (setValue > maxPosition) {
                                long maxValue = setValue - mMaxDuration;
                                setMin(maxValue / 1000f);
                            }
                        }
                        setMax(setValue / 1000f);
//                        KLog.i("xxxx", " setValue " + setValue);
                        mSeekbarListener.rangeSeekBarValuesChanging(setValue / 1000f);
                        return true;
                    } else if (CURRENT_THUMB_PRESSED == pressedThumb) {
                        if (min < setValue && setValue < max) {
                            mSeekbarListener.rangeSeekBarValuesChanging(setValue / 1000f);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (pressedThumb != NONE_THUMB_PRESSED) {
                    mSeekbarListener.rangeSeekBarValuesChanged(min / 1000f, max / 1000f, minValue / 1000f);
                }
                invalidate();
                pressedThumb = NONE_THUMB_PRESSED;
                break;

            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * Decides which (if any) thumb is touched by the given x-coordinate.
     *
     * @param touchX 触摸x轴 The x-coordinate of a touch event in screen space.
     */
    private int evalPressedThumb(float touchX) {
        int result = NONE_THUMB_PRESSED;
        boolean minThumbPressed = isInThumbRange(touchX, rectHandle.left - HANDWIDTH,
                rectHandle.left + 2 * HANDWIDTH);
        boolean maxThumbPressed = isInThumbRange(touchX, rectHandle.right - 2 * HANDWIDTH,
                rectHandle.right + HANDWIDTH);


        if (minThumbPressed && maxThumbPressed) {
            result = (touchX / getWidth() > 0.5f) ? MIN_THUMB_PRESSED
                    : MAX_THUMB_PRESSED;
        } else if (minThumbPressed) {
            result = MIN_THUMB_PRESSED;
        } else if (maxThumbPressed) {
            result = MAX_THUMB_PRESSED;
        }
        return result;
    }

    private boolean isInThumbRange(float touchX, int rectStart, int rectEnd) {
        return touchX > rectStart && touchX < rectEnd;
    }


    private OnRangeSeekBarChangeListener mSeekbarListener;

    public void setOnRangSeekBarChangeListener(OnRangeSeekBarChangeListener listener) {
        mSeekbarListener = listener;
    }

    public void setIsCropMusic(boolean cropMusic) {
        this.isCropMusic = cropMusic;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getMinValue() {
        return minValue;
    }

    /**
     * 截取框滑动回调函数
     */
    public interface OnRangeSeekBarChangeListener {
        /**
         * 响应thumb按下时
         *
         * @param thumbPressed
         * @return
         */
        boolean beginTouch(int thumbPressed);

        /**
         * seek bar响应值发生改变完成后
         *
         * @param minValue
         * @param maxValue
         * @param currentValue
         */
        void rangeSeekBarValuesChanged(float minValue, float maxValue, float currentValue);

        /**
         * seek bar响应值改变时
         *
         * @param setValue
         */
        void rangeSeekBarValuesChanging(float setValue);

    }
}
