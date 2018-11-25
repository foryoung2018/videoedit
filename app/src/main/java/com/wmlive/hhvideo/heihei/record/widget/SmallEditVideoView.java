package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.wmlive.hhvideo.heihei.record.manager.RecordSetting;

import cn.wmlive.hhvideo.R;

/**
 * Created by hsing on 2017/11/28.
 * 视频编辑CoverView
 */

public class SmallEditVideoView extends AnomalyView implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    public static final int VIEW_TYPE_VOLUME = 0;
    public static final int VIEW_TYPE_EFFECT = 1;
    private TextView tvAddEffect;
    private TextView tvVolume;
    private LinearLayout llVolume;
    private VerticalSeekBar volumeSeekBar;
    private OnEventListener eventListener;
    private int viewType;

    public SmallEditVideoView(@NonNull Context context) {
        this(context, null);
    }

    public SmallEditVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmallEditVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_small_edit_video_view, this, false);
        tvAddEffect = view.findViewById(R.id.tvAddEffect);
        tvVolume = view.findViewById(R.id.tvVolume);
        llVolume = view.findViewById(R.id.llVolume);
        volumeSeekBar = view.findViewById(R.id.volumeSeekBar);
        volumeSeekBar.setMax(RecordSetting.MAX_VOLUME);
//        tvAddEffect.setOnClickListener(this);
        volumeSeekBar.setOnSeekBarChangeListener(this);
        addView(view);
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
        if (viewType == VIEW_TYPE_VOLUME) {
            tvAddEffect.setVisibility(INVISIBLE);
            tvVolume.setVisibility(INVISIBLE);
            llVolume.setClickable(true);
            llVolume.setVisibility(VISIBLE);
        } else if (viewType == VIEW_TYPE_EFFECT) {
            volumeSeekBar.setVisibility(INVISIBLE);
            tvVolume.setVisibility(INVISIBLE);
            llVolume.setClickable(false);
            llVolume.setVisibility(INVISIBLE);
        }
    }

    public int getViewType() {
        return viewType;
    }

    public void setVideoVolume(int videoVolume) {
        volumeSeekBar.setVisibility(VISIBLE);
        volumeSeekBar.setProgress(videoVolume);
    }

    public void showVolumeBtn(boolean isShow) {
        volumeSeekBar.setVisibility(isShow ? VISIBLE : INVISIBLE);
        tvAddEffect.setVisibility(INVISIBLE);
    }

    public void showEffectBtn(boolean isShow) {
//        tvAddEffect.setVisibility(isShow ? VISIBLE : INVISIBLE);
//        tvAddEffect.setText(getResources().getString(R.string.effect_title));
        tvVolume.setVisibility(INVISIBLE);
    }

    public void setOnEventListener(OnEventListener eventListener) {
        this.eventListener = eventListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public void onClick(View v) {
        int index = getTag() == null ? -1 : (Integer) getTag();
        switch (v.getId()) {
            case R.id.tvAddEffect:
                if (null != eventListener) {
                    eventListener.onEffectClick(index, SmallEditVideoView.this);
                }
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        tvVolume.setVisibility(VISIBLE);
        String volume = (int) (seekBar.getProgress() * 200.0f / RecordSetting.MAX_VOLUME) + "%";
        tvVolume.setText(volume);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        tvVolume.setVisibility(VISIBLE);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        tvVolume.setVisibility(INVISIBLE);
        int index = getTag() == null ? -1 : (Integer) getTag();
        int progress = seekBar.getProgress();
        if (null != eventListener) {
            eventListener.onVolumeChange(index, SmallEditVideoView.this, progress);
        }
    }

    public interface OnEventListener {
        /**
         * 添加特效
         *
         * @param index
         * @param view
         */
        void onEffectClick(int index, SmallEditVideoView view);

        /**
         * 音量调节
         *
         * @param index
         * @param view
         */
        void onVolumeChange(int index, SmallEditVideoView view, int volume);
    }
}
