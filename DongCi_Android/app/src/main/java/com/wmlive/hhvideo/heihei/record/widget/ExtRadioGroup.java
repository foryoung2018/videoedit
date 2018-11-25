package com.wmlive.hhvideo.heihei.record.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;


import com.wmlive.hhvideo.heihei.record.utils.PaintUtils;
import com.wmlive.hhvideo.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

import cn.wmlive.hhvideo.R;


/**
 * 录制调速组件
 * Created by JIAN on 2017/5/12.
 */

public class ExtRadioGroup extends View {

    private String TAG = ExtRadioGroup.this.toString();

    public ExtRadioGroup(Context context) {
        super(context);
        init();
    }

    public ExtRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        createHandler();
        Resources res = getResources();
        bgColor = res.getColor(R.color.transparent_black);
        paint = new Paint();
        paint.setAntiAlias(true);
        color_n = res.getColor(R.color.white);
        color_p = res.getColor(R.color.extspeed_ed);
        checkedColor = res.getColor(R.color.green);
        pbg = new Paint();
        pbg.setAntiAlias(true);
        checkedId = 2;
        text_n = getResources().getDimensionPixelSize(R.dimen.text_size_13);
        text_p = getResources().getDimensionPixelSize(R.dimen.text_size_14);
        marginLeft = ScreenUtil.dip2px(getContext(),5);
    }

    private int text_n, text_p;
    private int bgColor;
    private int color_n, color_p;
    private List<String> list = new ArrayList<String>();


    private Paint paint, pbg;
    private Rect src = new Rect();
    private int checkedId = 0;
    private Rect dst = new Rect();
    private int checkedColor = 0;
    private PaintFlagsDrawFilter filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
            | Paint.FILTER_BITMAP_FLAG);

    private boolean ismoving = false;
    private int text_n_height[], text_p_height[];

    public void addMenu(int checkedId, String... menu) {
        this.checkedId = checkedId;
        if (null != menu) {
            for (int i = 0; i < menu.length; i++) {
                list.add(menu[i]);
            }
        }
        invalidate();

    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            src.set(getLeft(), getTop(), getRight(), getBottom());
            radio = (getHeight() / 2) - 1;
        }
    }

    private int radio = 5;
    private int marginLeft = 5;

    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.setDrawFilter(filter);
        canvas.drawColor(Color.TRANSPARENT);
        pbg.setColor(bgColor);
        canvas.drawRoundRect(new RectF(src), radio, radio, pbg);
        if (list.size() > 0) {
            int itemWidth = (getWidth() - marginLeft * 2) / list.size();
            int left;
            int tw, th;
            if (ismoving) {
                pbg.setColor(checkedColor);
                canvas.drawRoundRect(new RectF(dst.left, dst.top + 0.1f, dst.right, dst.bottom - 0.1f), radio, radio, pbg);
            }
            for (int i = 0; i < list.size(); i++) {
                left = getLeft() + itemWidth * i + marginLeft;
                if (checkedId == i) {
                    if (!ismoving) {
                        dst.set(left - marginLeft, getTop(), left + itemWidth + marginLeft, getBottom());
                        pbg.setColor(checkedColor);
                        canvas.drawRoundRect(new RectF(dst.left, dst.top + 0.1f, dst.right, dst.bottom - 0.1f), radio, radio, pbg);
                    }
                    paint.setColor(color_p);
                    paint.setTextSize(text_p);
                    tw = PaintUtils.getWidth(paint, list.get(i));
                    if (null == text_p_height) {
                        text_p_height = PaintUtils.getHeight(paint);
                    }
                    th = text_p_height[0];
                    canvas.drawText(list.get(i), left + itemWidth / 2 - tw / 2, radio + th / 2 - text_p_height[1], paint);
                } else {
                    paint.setTextSize(text_n);
                    paint.setColor(color_n);
                    tw = PaintUtils.getWidth(paint, list.get(i));
                    if (null == text_n_height) {
                        text_n_height = PaintUtils.getHeight(paint);
                    }
                    th = text_n_height[0];
                    canvas.drawText(list.get(i), left + itemWidth / 2 - tw / 2, radio + th / 2 - text_n_height[1], paint);
                }


            }

        }


    }

    private int itemWidth = 5;
    private int half = 2;
    private boolean isMoved = false;
    private int lastCheckIndex = 2;
    private int downx = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.e(TAG, isMoved + "onTouchEvent: " + event.getAction() + "..." + dst.toShortString() + "..." + event.getX());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downx = (int) event.getX();
                isMoved = false;
                itemWidth = getWidth() / list.size();
                half = itemWidth / 2;
                lastCheckIndex = checkedId;
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                int tempx = (int) event.getX();
                int mleft = tempx - half;
                int mright = tempx + half;
                if (Math.abs(tempx - downx) > itemWidth / 6 && mleft > getLeft() && mright < getRight()) {
                    isMoved = true;
                    int left;
                    int tempCheckId = 0;
                    for (int i = 0; i < list.size(); i++) {
                        left = getLeft() + itemWidth * i + marginLeft;
                        if (event.getX() >= left && event.getX() <= left + itemWidth) {
                            tempCheckId = i;
                            break;
                        }
                    }
                    if (tempCheckId != checkedId) {
                        checkedId = tempCheckId;
                    }
                    dst.set(mleft, getTop(), tempx + half, getBottom());
                    ismoving = true;
                    invalidate();
                }
            }
            break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                int left;
                int tempCheckId = 0;
                for (int i = 0; i < list.size(); i++) {
                    left = getLeft() + itemWidth * i;
                    if (event.getX() >= left && event.getX() <= left + itemWidth) {
                        tempCheckId = i;
                        break;
                    }
                }
                checkedId = tempCheckId;
                if (null != iListener) {
                    iListener.onSpeedChanged(checkedId);
                }
                if (isMoved) {
                    ismoving = false;
                    invalidate();
                } else {
                    if (lastCheckIndex != checkedId) {
                        ismoving = true;
                        //执行移动rect
                        targetLeft = itemWidth * checkedId + marginLeft;
                        lastLeft = (itemWidth * lastCheckIndex) + getLeft() + marginLeft;
                        createAnimation(lastLeft, targetLeft);
                        mHandler.obtainMessage(MSG_ANIM, lastLeft, targetLeft).sendToTarget();
                    }

                }
                lastCheckIndex = checkedId;


            }
            break;
            default:
                break;

        }
        return true;
    }

    /**
     * 设置选中id
     *
     * @param id
     */
    public void setCheckedId(int id) {
        checkedId = id;
        if (null != iListener) {
            iListener.onSpeedChanged(checkedId);
        }
        invalidate();
    }


    /**
     * 被选中的Item
     *
     * @return
     */
    public int getCheckedId() {
        return checkedId;
    }

    private ValueAnimator anim;
    private final int ANIM_DRUATION = 200;

    /**
     * 定义滑动加速器
     *
     * @param last
     * @param target
     */
    private void createAnimation(int last, int target) {
        anim = ValueAnimator.ofInt(last, target);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //更新按住缩放
                mHandler.obtainMessage(MSG_ANIM, (int) animation.getAnimatedValue(), targetLeft).sendToTarget();
            }
        });
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setDuration(ANIM_DRUATION);
        anim.start();
    }

    private final int MSG_ANIM = 56;
    private int targetLeft = 1, lastLeft = 1;
    private Handler mHandler;

    private void createHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_ANIM: {
                        if (Math.abs(targetLeft - msg.arg1) < 5) {
                            ismoving = false;
                            invalidate();
                        } else {
                            dst.set(msg.arg1, getTop(), msg.arg1 + itemWidth, getBottom());
                            invalidate();
                        }
                    }
                    break;
                    default:
                        break;
                }
            }
        };
    }


    public void setIListener(IGroupListener listener) {
        iListener = listener;
    }

    private IGroupListener iListener;


    public static interface IGroupListener {

        public void onSpeedChanged(int itemId);
    }

    public void recycle() {
        mHandler.removeMessages(MSG_ANIM);
        mHandler = null;
    }
}
