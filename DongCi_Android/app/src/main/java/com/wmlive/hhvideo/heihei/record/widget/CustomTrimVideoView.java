package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;


import com.wmlive.hhvideo.heihei.record.engine.PlayerEngine;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.widget.BaseCustomView;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by wenlu on 2017/9/9.
 */

public class CustomTrimVideoView extends BaseCustomView {
    public static final int MARGIN_LEFT_RIGHT = 14;

    @BindView(R.id.customClipView)
    CustomClipView customClipView;
    @BindView(R.id.tv_trim_duration)
    public TextView mTvTrimDuration;
    @BindView(R.id.tv_trim_start)
    public TextView mTvTrimStart;
    private OnRangeChangeListener mListener;

    public CustomTrimVideoView(Context context) {
        super(context);
    }

    public CustomTrimVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTrimVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_video_clip_layout;
    }

    public void setOnRangeChangeListener(OnRangeChangeListener listener) {
        mListener = listener;
    }

    /**
     * @param duration      目前截取的时长
     * @param totalDuration 视频总时长
     * @param maxDuration   多个视频最大时长
     */
    public void setPlayer(PlayerEngine playerEngine, int duration, int totalDuration, int maxDuration) {
        setPlayer(playerEngine, duration, totalDuration, maxDuration, 0);
    }

    /**
     * @param duration      目前截取的时长
     * @param totalDuration 视频总时长
     * @param maxDuration   多个视频最大时长
     */
    public void setPlayer(PlayerEngine playerEngine, int duration, int totalDuration, int maxDuration, int startTime) {
        customClipView.setOnRangeChangeListener(new CustomClipView.OnRangeChangeListener() {
            @Override
            public void onValuesChanged(int minValue, int maxValue, int duration, int changeType) {
                int i = minValue / 100 % 10;
                mTvTrimStart.setText(getResources().getString(R.string.edit_trim_start_time, getFormatString(minValue)));
                mTvTrimDuration.setText(getResources().getString(R.string.edit_trim_duration, getFormatString(duration)));
                if (mListener != null) {
                    mListener.onValuesChanged(minValue, maxValue, duration, changeType);
                }
            }
        });
        mTvTrimStart.setText(getResources().getString(R.string.edit_trim_start_time, getFormatString(startTime)));
        mTvTrimDuration.setText(getResources().getString(R.string.edit_trim_duration, getFormatString(duration)));
        customClipView.setPlayer(playerEngine, duration, totalDuration, maxDuration, startTime);
        KLog.i("maxDurqation--->"+maxDuration);
    }

    /**
     * 重绘画册
     */
    public void viewThumbnailInvalidate() {
        if (customClipView != null) {
            customClipView.invalidate();
        }
    }

    /**
     * 销毁  回收内存
     */
    public void onDestroy(){
        customClipView.recycle();
        mListener = null;
    }

    private String getFormatString(int number) {
        int base = number % 100;
        float modulus;
        if (base < 25) {
            modulus = 0f;
        } else if (base < 75) {
            modulus = 0.05f;
        } else {
            modulus = 0.1f;
        }
        float result = number / 100 / 10f;
        String format = String.format("%.2f", result + modulus);
        KLog.i("xxxx", "number " + number + " format to " + format);
        return format;
    }


    public interface OnRangeChangeListener {

        /**
         * 响应值发生改变
         *
         * @param minValue
         * @param maxValue
         * @param changeType
         */
        void onValuesChanged(int minValue, int maxValue, int duration, int changeType);
    }
}
