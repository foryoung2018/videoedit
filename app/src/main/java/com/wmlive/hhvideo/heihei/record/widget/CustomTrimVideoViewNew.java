package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.heihei.record.engine.PlayerEngine;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.widget.BaseCustomView;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by wenlu on 2017/9/9.
 */

public class CustomTrimVideoViewNew extends BaseCustomView implements VolumeWheelView.VolumeChangeListener {
    public static final int MARGIN_LEFT_RIGHT = 14;

    @BindView(R.id.customClipView)
    CustomClipViewNew customClipView;
    @BindView(R.id.tv_trim_start)
    public TextView mTvTrimStart;
    @BindView(R.id.ivStart)
    public ImageView ivStart;
    @BindView(R.id.ivEnd)
    public ImageView ivEnd;
    @BindView(R.id.tvVolume)
    public TextView tvVolume;
    @BindView(R.id.volumeUP)
    public IncreaseDecreaseView volumeUP;
    @BindView(R.id.volumeDown)
    public IncreaseDecreaseView volumeDown;
    @BindView(R.id.volumeWheel)
    public VolumeWheelView volumeWheel;
    @BindView(R.id.imageFilter)
    public ImageView imageFilter;
//    @BindView(R.id.filter_layout)
//    LinearLayout filterLayout;
    private OnRangeChangeListener mListener;
    private onVolumeChangeListener mOnVolumeChangeListener;

    public void setTrimViewPresenter(TrimViewPresenter trimViewPresenter) {
        this.trimViewPresenter = trimViewPresenter;
    }

    private TrimViewPresenter trimViewPresenter;

    public CustomTrimVideoViewNew(Context context) {
        super(context);
    }

    public CustomTrimVideoViewNew(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTrimVideoViewNew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {
        ivStart.setOnClickListener(this);
        ivEnd.setOnClickListener(this);
        volumeWheel.setVolumeChangeListener(this);
        volumeUP.setOnClickListener(this);
        volumeDown.setOnClickListener(this);
        imageFilter.setOnClickListener(this);
//        filterLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == ivStart) {
            customClipView.increase(100f);
        } else if (v == ivEnd) {
            customClipView.increase(-100f);
        } else if (v == volumeUP) {
            volumeWheel.volumeUp(true);
        } else if (v == volumeDown) {
            volumeWheel.volumeUp(false);
        } else if (v == imageFilter) {
            trimViewPresenter.onFilterClick(imageFilter);

        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_video_clip_layout_new;
    }

    public void setOnRangeChangeListener(OnRangeChangeListener listener) {
        mListener = listener;
    }

    public void setOnVolumeChangeListener(onVolumeChangeListener listener) {
        mOnVolumeChangeListener = listener;
    }

    /**
     * @param duration      目前截取的时长
     * @param totalDuration 视频总时长
     * @param maxDuration   多个视频最大时长
     */
    public void setPlayer(PlayerEngine playerEngine, int duration, int totalDuration, int maxDuration,float ratio) {
        setPlayer(playerEngine, duration, totalDuration, maxDuration, 0,ratio);
    }

    /**
     * @param duration      目前截取的时长
     * @param totalDuration 视频总时长
     * @param maxDuration   多个视频最大时长
     */
    public void setPlayer(PlayerEngine playerEngine, int duration, int totalDuration, int maxDuration, int startTime, float ratio) {
        customClipView.setOnRangeChangeListener(new CustomClipViewNew.OnRangeChangeListener() {
            @Override
            public void onValuesChanged(long minValue, long maxValue, long duration, int changeType) {
//                int i = minValue / 100 % 10;
                mTvTrimStart.setText(getResources().getString(R.string.edit_trim_start_time, getFormatString((int) minValue)));
                if (mListener != null) {
                    mListener.onValuesChanged(minValue, maxValue, duration, changeType);
                }
            }

            @Override
            public void onValuesChangeEnd() {
                if (mListener != null) {
                    mListener.onValuesChangeEnd();
                }
            }

            @Override
            public void onValuesChangeStart() {
                if (mListener != null) {
                    mListener.onValuesChangeStart();
                }
            }

        });
        mTvTrimStart.setText(getResources().getString(R.string.edit_trim_start_time, getFormatString(startTime)));
        customClipView.setPlayer(playerEngine, duration, totalDuration, maxDuration, startTime,ratio);
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
    public void onDestroy() {
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

    @Override
    public void onVolumeChange(int volume) {
        tvVolume.setText(volume + "%");
        if (mOnVolumeChangeListener != null) {
            mOnVolumeChangeListener.onValuesChanged(volume);
        }
    }

    public void setRatio(float ratio) {
        customClipView.setRatio(ratio);
    }

//    public void onPlayProgress(float position) {
//        customClipView.onPlayProgress(position);
//    }
//
//    public void setRatio(float ratio) {
//        customClipView.setRatio(ratio);
//    }

    public interface OnRangeChangeListener {

        /**
         * 响应值发生改变
         *
         * @param minValue
         * @param maxValue
         * @param changeType
         */
        void onValuesChanged(long minValue, long maxValue, long duration, int changeType);

        void onValuesChangeEnd();

        void onValuesChangeStart();
    }

    public interface onVolumeChangeListener {
        void onValuesChanged(int volume);
    }

    /**
     * 此view的所有接口回调
     */
    public interface TrimViewPresenter {
        void onFilterClick(ImageView imageFilter);
    }
}
