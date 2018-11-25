package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.wmlive.hhvideo.widget.BaseCustomView;
import com.wmlive.hhvideo.widget.CustomFontTextView;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 8/29/2017.
 */

public class CountdownView extends BaseCustomView {

    private static final int MSG_COUNTDOWN = 10;
    @BindView(R.id.ivCountdown)
    CustomFontTextView ivCountdown;
    private int count = 0;
    private Animation animation;
    private OnCountdownEndListener countdownEndListener;
    private boolean isStarted = false;

    public CountdownView(Context context) {
        super(context);
    }

    public CountdownView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {

    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_COUNTDOWN) {
                if (animation == null) {
                    animation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_s2l);
                }
                switch (count) {
                    case 10:
                        ivCountdown.setText("10");
                        break;
                    case 9:
                        ivCountdown.setText("9");
                        break;
                    case 8:
                        ivCountdown.setText("8");
                        break;
                    case 7:
                        ivCountdown.setText("7");
                        break;
                    case 6:
                        ivCountdown.setText("6");
                        break;
                    case 5:
                        ivCountdown.setText("5");
                        break;
                    case 4:
                        ivCountdown.setText("4");
                        break;
                    case 3:
                        ivCountdown.setText("3");
                        break;
                    case 2:
                        ivCountdown.setText("2");
                        break;
                    case 1:
                        ivCountdown.setText("1");
                        break;
                    case 0:
                        stop(false);
                        break;
                }
                count--;
                if (count >= 0 && handler != null) {
                    if (ivCountdown != null) {
                        ivCountdown.startAnimation(animation);
                        handler.sendEmptyMessageDelayed(MSG_COUNTDOWN, 1000);
                    }
                }
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.view_countdown;
    }

    public void start(int num) {
        setVisibility(VISIBLE);
        ivCountdown.setText(num+"");
        count = num;
        isStarted = true;
        if (countdownEndListener != null) {
            countdownEndListener.onCountdownStart();
        }
        handler.sendEmptyMessage(MSG_COUNTDOWN);
    }

    public void stop(boolean release) {
        isStarted = false;
        if (animation != null) {
            animation.cancel();
            if (release) {
                ivCountdown.clearAnimation();
                animation = null;
            } else {
                if (countdownEndListener != null) {
                    countdownEndListener.onCountdownEnd();
                }
            }
        }
        handler.removeCallbacksAndMessages(null);
        setVisibility(GONE);
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void cancel() {
        isStarted = false;
        if (animation != null) {
            animation.cancel();
        }
        if (countdownEndListener != null) {
            countdownEndListener.onCountdownCancel();
        }
        handler.removeCallbacksAndMessages(null);
        setVisibility(GONE);
    }

    public void release() {
        stop(true);
        handler = null;
    }

    public void setCountdownEndListener(OnCountdownEndListener countdownEndListener) {
        this.countdownEndListener = countdownEndListener;
    }

    public interface OnCountdownEndListener {
        void onCountdownStart();

        void onCountdownEnd();

        void onCountdownCancel();
    }
}
