package com.wmlive.hhvideo.heihei.mainhome.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.networklib.entity.EventEntity;
import com.wmlive.networklib.util.EventHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 1/16/2018.12:18 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class CountdownView extends AppCompatTextView {

    private int startNumber;
    private OnCountdownListener countdownListener;
    private boolean isStarted;
    private int index;
    private int position;
    private String giftId;

    public CountdownView(Context context) {
        super(context);
        initViews();
    }

    public CountdownView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public CountdownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews() {
        setBackground(getResources().getDrawable(R.drawable.icon_gift_time));
        setTag(false);
    }

    public void setData(String giftId, int index, int position, boolean isFree) {
        this.giftId = giftId;
        this.index = index;
        this.position = position;
        setTag(isFree);
    }

    public void start(int start) {
        if (!isStarted) {
            startNumber = start;
            isStarted = true;
            setVisibility(VISIBLE);
            setText(startNumber + "s");
            postDelayed(runnable, 1000);
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (startNumber > 0) {
                --startNumber;
                KLog.i("=====倒计时：" + startNumber);
                setText(startNumber + "s");
                postDelayed(this, 1000);
            } else {
                if (countdownListener != null) {
                    countdownListener.onCountdownEnd(giftId, index, position);
                }
                isStarted = false;
                setVisibility(GONE);
            }
        }
    };

    public void stop() {
        if (runnable != null) {
            removeCallbacks(runnable);
        }
        isStarted = false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventHelper.register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventHelper.unregister(this);
        stop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventEntity eventEntity) {
        if (eventEntity != null && eventEntity.code == GlobalParams.EventType.TYPE_REFRESH_COUNTDOWN) {
            if (eventEntity.data != null && eventEntity.data instanceof Integer) {
                int times = (int) eventEntity.data;
                KLog.i("======收到刷新倒计时的消息times：" + times);
                if ((boolean) getTag()) {
                    if (times > 0) {
                        start(times);
                    } else {
                        stop();
                    }
                }
            }
        }
    }

    public void setCountdownListener(OnCountdownListener countdownListener) {
        this.countdownListener = countdownListener;
    }

    public interface OnCountdownListener {
        void onCountdownEnd(String giftId, int index, int position);
    }
}
