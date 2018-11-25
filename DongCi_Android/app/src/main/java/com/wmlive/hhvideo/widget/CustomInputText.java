package com.wmlive.hhvideo.widget;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.ToastUtil;

import java.util.Timer;
import java.util.TimerTask;

import cn.wmlive.hhvideo.R;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * 邀请码输入框
 * Created by wenlu on 2017/10/18.
 */
public class CustomInputText extends EditText implements View.OnLongClickListener {

    private Context mContext;
    /**
     * 第一个圆开始绘制的圆心坐标
     */
    private float startX;
    private float startY;

    private float cX;

    /**
     * 实心圆的半径
     */
    private int radius = 10;
    /**
     * view的高度
     */
    private int height;
    private int width;

    /**
     * 当前输入密码位数
     */
    private int textLength = 0;
    private int bottomLineLength;
    /**
     * 最大输入位数
     */
    private int maxCount = 8;
    /**
     * 圆的颜色
     */
    private int circleColor = Color.WHITE;
    /**
     * 文本的颜色
     */
    private int textColor = Color.WHITE;
    /**
     * 底部线的颜色   默认GRAY
     */
    private int underlineColor = Color.parseColor("#313539");

    /**
     * 分割线的颜色
     */
    private int borderColor = Color.GRAY;
    /**
     * 分割线的画笔
     */
    private Paint borderPaint;
    /**
     * 分割线开始的坐标x
     */
    private int divideLineWStartX;

    /**
     * 分割线的宽度  默认2
     */
    private int divideLineWidth = 2;
    /**
     * 竖直分割线的颜色
     */
    private int divideLineColor = Color.GRAY;
    private int focusedColor = Color.BLUE;
    private RectF rectF = new RectF();
    private RectF focusedRecF = new RectF();
    private final static int MODE_UNDERLINE = 0; // 下换线样式
    private final static int MODE_RECT = 1; // 边框样式
    private int mode = MODE_UNDERLINE;

    /**
     * 矩形边框的圆角
     */
    private int rectAngle = 0;
    /**
     * 竖直分割线的画笔
     */
    private Paint divideLinePaint;
    /**
     * 圆的画笔
     */
    private Paint circlePaint;
    /**
     * 圆的画笔
     */
    private Paint textPaint;
    /**
     * 底部线的画笔
     */
    private Paint underlinePaint;

    /**
     * 需要对比的密码  一般为上次输入的
     */
    private String mComparePassword = null;

    /**
     * 当前输入的位置索引
     */
    private int position = 0;

    private OnTextChangeListener mListener;
    private boolean showPassword;
    private Timer timer;
    private TimerTask timerTask;
    private int cursorFlashTime;
    private boolean isCursorShowing;
    private int cursorColor;
    private boolean isCursorEnable;
    private Paint cursorPaint;
    private boolean isInputComplete;
    private int textSize;
    private int cursorHeight;
    private PopupWindow popupWindow;

    public CustomInputText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        getAtt(attrs);
        initPaint();

        this.setBackgroundColor(Color.TRANSPARENT);
        this.setCursorVisible(false); // 不显示默认光标
        this.setSingleLine();
        this.setTextIsSelectable(false);
        this.setOnLongClickListener(this);
        this.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!isLetterOrDigit(source.charAt(i)) && !Character.toString(source.charAt(i)).equals("_") && !Character.toString(source.charAt(i)).equals("-")) {
                        ToastUtil.showToast("包含特殊字符");
                        return "";
                    }
                }
                // 返回空字符串，匹配不成功，返回null 匹配成功
                return null;
            }
        };
        this.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxCount), filter});
        this.setCustomSelectionActionModeCallback(new ActionMode.Callback() { // 禁止系统粘贴菜单
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
        initTimer();
    }

    private boolean isLetterOrDigit(char chr) {
        return Character.isDigit(chr) || Character.isLowerCase(chr) || Character.isUpperCase(chr);
    }

    private void initTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                isCursorShowing = !isCursorShowing;
                postInvalidate();
            }
        };
    }

    private void getAtt(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.CustomInputText);
        maxCount = typedArray.getInt(R.styleable.CustomInputText_cit_maxCount, maxCount);
        circleColor = typedArray.getColor(R.styleable.CustomInputText_cit_circleColor, circleColor);
        underlineColor = typedArray.getColor(R.styleable.CustomInputText_cit_underlineColor, underlineColor);
        radius = typedArray.getDimensionPixelOffset(R.styleable.CustomInputText_cit_radius, radius);

        divideLineWidth = typedArray.getDimensionPixelSize(R.styleable.CustomInputText_cit_divideLineWidth, divideLineWidth);
        divideLineColor = typedArray.getColor(R.styleable.CustomInputText_cit_divideLineColor, divideLineColor);
        mode = typedArray.getInt(R.styleable.CustomInputText_cit_mode, mode);
        rectAngle = typedArray.getDimensionPixelOffset(R.styleable.CustomInputText_cit_rectAngle, rectAngle);
        focusedColor = typedArray.getColor(R.styleable.CustomInputText_cit_focusedColor, focusedColor);
        showPassword = typedArray.getBoolean(R.styleable.CustomInputText_cit_showPassword, true);
        cursorFlashTime = typedArray.getInteger(R.styleable.CustomInputText_cit_cursorFlashTime, 500);
        cursorColor = typedArray.getColor(R.styleable.CustomInputText_cit_cursorColor, getResources().getColor(R.color.hh_color_e));
        isCursorEnable = typedArray.getBoolean(R.styleable.CustomInputText_cit_isCursorEnable, true);

        typedArray.recycle();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        circlePaint = getPaint(5, Paint.Style.FILL, circleColor);
        textPaint = getPaint(5, Paint.Style.FILL, textColor);
        textSize = DeviceUtils.sp2px(mContext, 18);
        textPaint.setTextSize(textSize);
        cursorHeight = textSize / 2;

        underlinePaint = getPaint(DeviceUtils.dip2px(mContext, 1), Paint.Style.FILL, underlineColor);
        borderPaint = getPaint(3, Paint.Style.STROKE, borderColor);
        divideLinePaint = getPaint(divideLineWidth, Paint.Style.FILL, borderColor);
        cursorPaint = getPaint(DeviceUtils.dip2px(mContext, 2), Paint.Style.FILL, cursorColor);
    }

    /**
     * 设置画笔
     *
     * @param strokeWidth 画笔宽度
     * @param style       画笔风格
     * @param color       画笔颜色
     * @return
     */
    private Paint getPaint(int strokeWidth, Paint.Style style, int color) {
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(style);
        paint.setColor(color);
        paint.setAntiAlias(true);

        return paint;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        height = h;
        width = w;

        divideLineWStartX = w / maxCount;

        startX = w / maxCount / 2;
        startY = h / 2;

        bottomLineLength = (int) (w * 0.57f / maxCount);

        rectF.set(0, 0, width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //不删除的画会默认绘制输入的文字
//       super.onDraw(canvas);

        switch (mode) {
            case MODE_UNDERLINE:
                drawBottomBorder(canvas);
                break;
            case MODE_RECT:
                drawBorder(canvas);
                drawItemFocused(canvas, position);
                break;
        }
        // 绘制文本
        drawText(canvas);
        //绘制光标
        drawCursor(canvas, cursorPaint);
    }

    /**
     * 绘制光标
     *
     * @param canvas
     * @param paint
     */
    private void drawCursor(Canvas canvas, Paint paint) {
        //光标未显示 && 开启光标 && 输入位数未满 && 获得焦点
        if (!isCursorShowing && isCursorEnable && !isInputComplete && hasFocus()) {
            // 起始点x = paddingLeft + 单个密码框大小 / 2 + (单个密码框大小 + 密码框间距) * 光标下标
            // 起始点y = paddingTop + 光标大小 / 2
            // 终止点x = 起始点x
            // 终止点y = 起始点y + 光标高度
            int cursorPosition = textLength;
            canvas.drawLine(getPaddingLeft() + startX + cursorPosition * 2 * startX,
                    getPaddingTop() + cursorHeight / 2,
                    getPaddingLeft() + startX + cursorPosition * 2 * startX,
                    getPaddingTop() + startY + cursorHeight / 2,
                    paint);
        }
    }

    /**
     * 绘制矩形密码框的样式
     *
     * @param canvas
     */
    private void drawBorder(Canvas canvas) {

        canvas.drawRoundRect(rectF, rectAngle, rectAngle, borderPaint);

        for (int i = 0; i < maxCount - 1; i++) {
            canvas.drawLine((i + 1) * divideLineWStartX,
                    0,
                    (i + 1) * divideLineWStartX,
                    height,
                    divideLinePaint);
        }

    }

    private void drawItemFocused(Canvas canvas, int position) {
        if (position > maxCount - 1) {
            return;
        }
        focusedRecF.set(position * divideLineWStartX, 0, (position + 1) * divideLineWStartX,
                height);
        canvas.drawRoundRect(focusedRecF, rectAngle, rectAngle, getPaint(3, Paint.Style.STROKE, focusedColor));
    }

    /**
     * 画底部显示的分割线
     *
     * @param canvas
     */
    private void drawBottomBorder(Canvas canvas) {

        for (int i = 0; i < maxCount; i++) {
            cX = startX + i * 2 * startX;
            canvas.drawLine(cX - bottomLineLength / 2,
                    height,
                    cX + bottomLineLength / 2,
                    height, underlinePaint);
        }
    }

    /**
     * 绘制文本
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        String textContent = getText().toString();
        for (int i = 0; i < textLength; i++) {
            if (showPassword) {
                // 显示密码
                String text = textContent.substring(i, i + 1);
                if (text.length() > 0) {
                    float[] widths = new float[text.length()];
                    textPaint.getTextWidths(text, widths);
                    Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
                    // 注意 float y 参数是基线
                    float baseline = (startY * 2 - fontMetrics.top - fontMetrics.bottom) / 2;
                    canvas.drawText(text, startX + i * 2 * startX - widths[0] / 2,
                            baseline, textPaint);
                }
            } else {
                // 不显示密码，展示圆点
                canvas.drawCircle(startX + i * 2 * startX,
                        startY,
                        radius,
                        circlePaint);
            }
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        this.position = start + lengthAfter;
        textLength = text.toString().length();

        isInputComplete = (textLength == maxCount);
        if (mListener != null) {
            mListener.onTextChange(getPasswordString(), isInputComplete);
            if (textLength == maxCount && mComparePassword != null) {
                if (TextUtils.equals(mComparePassword, getPasswordString())) {
                    mListener.onEqual(getPasswordString());
                } else {
                    mListener.onDifference();
                }
            }
        }
        invalidate();

    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);

        //保证光标始终在最后
        if (selStart == selEnd) {
            setSelection(getText().length());
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //cursorFlashTime为光标闪动的间隔时间
        if (timer == null || timerTask == null) {
            initTimer();
        }
        timer.scheduleAtFixedRate(timerTask, 0, cursorFlashTime);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    /**
     * 获取输入的密码
     *
     * @return
     */
    public String getPasswordString() {
        return getText().toString().trim();
    }

    public void setOnTextChangeListener(OnTextChangeListener listener) {
        mListener = listener;
    }

    public void setOnTextChangeListener(String comparePassword, OnTextChangeListener listener) {
        mComparePassword = comparePassword;
        mListener = listener;
    }

    @Override
    public boolean onLongClick(View v) {
        showPopWindow();
        return true;
    }

    /**
     * 长按弹出粘贴菜单
     */
    private void showPopWindow() {
        if (popupWindow == null) {
            popupWindow = new PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            final ClipboardManager clipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            TextView tv = new TextView(mContext);
            tv.setText("粘贴");
            tv.setTextColor(getResources().getColor(R.color.black));
            tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_white_shape));
            tv.setPadding(30, 20, 30, 20);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CharSequence charSequence = clipboardManager.getText();
                    if (clipboardManager == null || TextUtils.isEmpty(charSequence)) {
                        popupWindow.dismiss();
                        return;
                    }
                    String text = charSequence.toString().trim();
                    if (text.length() > maxCount) {
                        text = text.substring(0, maxCount);
                    }
                    setText(text);
                    popupWindow.dismiss();

                }
            });

            popupWindow.setContentView(tv);
            popupWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
            popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            popupWindow.setFocusable(true);// 获取焦点
            popupWindow.setTouchable(true); // 设置PopupWindow可触摸
            popupWindow.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
            ColorDrawable dw = new ColorDrawable(0x00000000);
            popupWindow.setBackgroundDrawable(dw);
        }
        popupWindow.showAsDropDown(this, 10, 0, Gravity.LEFT | Gravity.TOP);
    }

    /**
     * 密码比较监听
     */
    public interface OnTextChangeListener {
        void onDifference();

        void onEqual(String text);

        void onTextChange(String text, boolean isComplete);
    }
}
