package com.wmlive.hhvideo.heihei.message.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.login.AccountUtil;

import java.lang.ref.WeakReference;

import cn.wmlive.hhvideo.R;

public class CircleRotateProgressBar extends ImageView {

    public CircleRotateProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CircleRotateProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleRotateProgressBar(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (AccountUtil.getUserInfo() != null && UserInfo.FEMALE.equals(AccountUtil.getUserInfo().getGender())) {
            setImageResource(R.drawable.juhua_n_00002);
        } else {
            setImageResource(R.drawable.juhua_00000);
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        if (visibility == View.VISIBLE) {
            if (mHandler == null) {
                mHandler = new RotateHandler();
            }

            degrees = 0;
            mHandler.removeMessages(0);

            Message msg = Message.obtain();
            msg.what = 0;
            msg.obj = new WeakReference<CircleRotateProgressBar>(this);
            mHandler.sendMessage(msg);

        } else {
            degrees = 0;
            if (mHandler != null) {
                mHandler.removeMessages(0);
            }
        }
    }

    public void setMineVisibility(int visibility, int gender) {
//        if (gender == UserInfo.MALE) {
//            setImageResource(R.drawable.juhua_n_00002);
//        } else {
//            setImageResource(R.drawable.juhua_00000);
//        }

        setVisibility(visibility);
    }

    private RotateHandler mHandler;

    private int degrees = 0;// 当前角度

    private static final int DURATION = 500;// 旋转一圈的时长
    private static final int DEGREES_INCREASE = 30;// 每次旋转增加的角度
    private static final int TIME_INTEVAL = DURATION / (360 / DEGREES_INCREASE);// 间隔时间

    public void rotate() {
        degrees += DEGREES_INCREASE;
        degrees = degrees % 360;
        invalidate();

        if (getVisibility() == View.VISIBLE) {
            Message msg = Message.obtain();
            msg.what = 0;
            msg.obj = new WeakReference<CircleRotateProgressBar>(this);
            mHandler.sendMessageDelayed(msg, TIME_INTEVAL);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.rotate(degrees, getWidth() / 2, getHeight() / 2);
        super.onDraw(canvas);
    }

    private static class RotateHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            WeakReference<CircleRotateProgressBar> wr = (WeakReference<CircleRotateProgressBar>) msg.obj;
            if (wr != null && wr.get() != null) {
                wr.get().rotate();
            }
        }
    }

}
