package com.wmlive.hhvideo.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;

import com.wmlive.hhvideo.utils.KLog;

/**
 * Created by lsq on 5/4/2018 - 6:23 PM
 * 类描述：
 */
public class GestureView extends RelativeLayout implements View.OnTouchListener {
    private static final String TAG = GestureView.class.getSimpleName();
    private int touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    private GestureViewListener gestureViewListener;

    public GestureView(@NonNull Context context) {
        super(context);
        initViews();
    }

    public GestureView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public GestureView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initViews();
    }

    private void initViews() {
        setOnTouchListener(this);
    }

    private float downX;
    private float downY;
    private long lastTime = 0;

    private static final byte MSG_SINGLE_CLICK = 1;
    private static final byte MSG_CONTINUOUS_CLICK = 2;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (gestureViewListener != null) {
                switch (msg.what) {
                    case MSG_SINGLE_CLICK:
                        KLog.i(TAG, "=====单击handleMessage: ");
                        gestureViewListener.onSingleClick(msg.arg1, msg.arg2);
                        break;
                    case MSG_CONTINUOUS_CLICK:
                        KLog.i(TAG, "=====双击handleMessage: ");
                        gestureViewListener.onContinunousClick(msg.arg1, msg.arg2);
                        break;
                    default:
                        break;
                }
            }
        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getRawX();
                downY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                return false;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                long nowTime = System.currentTimeMillis();
                if (nowTime - lastTime > 220) {
                    if (Math.abs(downX - event.getRawX()) < touchSlop
                            && Math.abs(downY - event.getRawY()) < touchSlop) {
                        Message message = Message.obtain();
                        message.what = MSG_SINGLE_CLICK;
                        message.arg1 = (int) event.getRawX();
                        message.arg2 = (int) event.getRawY();
                        handler.sendMessageDelayed(message, 240);
                    }
                } else {//连续点击
                    handler.removeMessages(MSG_SINGLE_CLICK);
                    Message message = Message.obtain();
                    message.what = MSG_CONTINUOUS_CLICK;
                    message.arg1 = (int) event.getRawX();
                    message.arg2 = (int) event.getRawY();
                    handler.sendMessage(message);
                }
                lastTime = nowTime;
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    public void setGestureViewListener(GestureViewListener gestureViewListener) {
        this.gestureViewListener = gestureViewListener;
    }

    public interface GestureViewListener {
        void onSingleClick(float rawX, float rawY);

        void onContinunousClick(float rawX, float rawY);
    }
}
