package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.wmlive.hhvideo.widget.BaseCustomView;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 8/28/2017.
 * 声音调节面板
 */

public class RecordVolumePanel extends BaseCustomView implements SeekBar.OnSeekBarChangeListener {
    private OnVolumeChangeListener mVolumeChangeListener;
    @BindView(R.id.sbVolume)
    SeekBar sbVolume;
    @BindView(R.id.sbTrack)
    SeekBar sbTrack;
    private int originalMixFactor;
    private int originalMax = 100; // 原声最大音量

    public RecordVolumePanel(Context context) {
        super(context);
    }

    public RecordVolumePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {
        sbVolume.setOnSeekBarChangeListener(this);

        sbTrack.setProgress(50);
        sbTrack.setSecondaryProgress(0);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_volume_panel;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int id = seekBar.getId();
        if (fromUser) {
            if (R.id.sbVolume == id) {
                if (null != mVolumeChangeListener) {
                    mVolumeChangeListener.onOriginalVolumeChange(progress, (int) (progress * originalMax / 100f));
                }
            } else {
                if (null != mVolumeChangeListener) {
                    mVolumeChangeListener.onTrackVolumeChange(progress);
                }
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int id = seekBar.getId();
        int progress = seekBar.getProgress();
        if (R.id.sbVolume == id) {
            if (null != mVolumeChangeListener) {
                mVolumeChangeListener.onOriginalStopTrackingTouch(progress, (int) (progress * originalMax / 100f));
            }
        }
    }

    /**
     * 设置原声音量
     *
     * @param originalMix
     */
    public void setVolume(int originalMix) {
        this.originalMixFactor = originalMix;
        sbVolume.setProgress(originalMixFactor);
        sbVolume.setSecondaryProgress(0);
    }

    /**
     * 设置原声音量
     *
     * @param originalMix
     * @param max         最大音量
     */
    public void setVolume(int originalMix, int max) {
        this.originalMixFactor = originalMix;
        this.originalMax = max;
        sbVolume.setProgress((int) (originalMixFactor * 100f / max));
        sbVolume.setSecondaryProgress(0);
    }

    public void setOnVolumeChangeListener(OnVolumeChangeListener volumeChangeListener) {
        this.mVolumeChangeListener = volumeChangeListener;
    }

    public interface OnVolumeChangeListener {
        void onOriginalVolumeChange(int progress, int value);

        void onTrackVolumeChange(int progress);

        void onOriginalStopTrackingTouch(int progress, int value);
    }
}
