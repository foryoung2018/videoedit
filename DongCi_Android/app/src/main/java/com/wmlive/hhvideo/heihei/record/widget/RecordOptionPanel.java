package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.wmlive.hhvideo.heihei.record.manager.RecordSetting;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;
import com.wmlive.hhvideo.widget.BaseCustomView;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 8/25/2017.
 * 视频录制页面底部的tab
 */

public class RecordOptionPanel extends BaseCustomView {
    @BindView(R.id.viewCountDown)
    RecordOptionView viewCountDown;
    //    @BindView(R.id.viewSpeed)
//    RecordOptionView viewSpeed;
    @BindView(R.id.viewFrame)
    RecordOptionView viewFrame;
    @BindView(R.id.flRecord)
    FrameLayout flRecord;
    @BindView(R.id.viewRollback)
    RecordOptionView viewRollback;
    @BindView(R.id.viewFilter)
    RecordOptionView viewFilter;
    @BindView(R.id.pbProgress)
    ExtProgressBar pbProgress;
    private OnOptionClickListener optionClickListener;
    private static final int MIN_CLICK_DELAY_TIME = 500;
    private int clickDelay = MIN_CLICK_DELAY_TIME;
    private long lastClickTime;
    private int lastViewId;

    private final int[] titleIds = new int[]{
            R.string.stringFrame,
            R.string.stringCountDown,
            R.string.stringRollback,
            R.string.stringFilter};

    private final int[] iconIds = new int[]{
            R.drawable.icon_video_bottombar_frame,
            R.drawable.icon_video_bottombar_count_nor,
            R.drawable.icon_video_bottombar_repeal,
            R.drawable.icon_video_topbar_filter_nor};

    public RecordOptionPanel(Context context) {
        super(context);
    }

    public RecordOptionPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {
        viewCountDown.setOnClickListener(this);
//        viewSpeed.setOnClickListener(this);
        viewFrame.setOnClickListener(this);
        viewRollback.setOnClickListener(this);
        viewFilter.setOnClickListener(view -> {
            if (optionClickListener != null) {
                optionClickListener.onFilterClick();
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        setCountdownEnable(false);
        setFrameEnable(true);
        setRollbackEnable(false);
        setFilterEnable(true);
    }

    public void setCountdownEnable(boolean enable) {
        viewCountDown.setClickable(enable);
        viewCountDown.setEnable(enable);
        viewCountDown.tvTitle.setText(getResources().getString(titleIds[1]));
        viewCountDown.ivIcon.setImageResource(enable ? R.drawable.icon_video_bottombar_count_nor : R.drawable.icon_video_bottombar_count_dis);
    }

    public void setRollbackEnable(boolean enable) {
        viewRollback.setClickable(enable);
        viewRollback.setEnable(enable);
        viewRollback.tvTitle.setText(getResources().getString(titleIds[2]));
        viewRollback.ivIcon.setImageResource(enable ? R.drawable.icon_video_bottombar_repeal : R.drawable.icon_video_bottombar_repeal_dis);
    }

    public void setSpeedEnable(boolean enable) {
//        viewFrame.setClickable(enable);
//        viewFrame.setEnable(enable);
//        viewFrame.tvTitle.setText(getResources().getString(titleIds[0]));
//        viewFrame.ivIcon.setImageResource(enable ? R.drawable.icon_video_bottombar_frame : R.drawable.icon_video_bottombar_frame_dis);
    }

    public void setFrameEnable(boolean enable) {
        viewFrame.setClickable(enable);
        viewFrame.setEnable(enable);
        viewFrame.tvTitle.setText(getResources().getString(titleIds[0]));
        viewFrame.ivIcon.setImageResource(enable ? R.drawable.icon_video_bottombar_frame : R.drawable.icon_video_bottombar_frame_dis);
    }

    public void setFilterEnable(boolean enable) {
        viewFilter.setClickable(enable);
        viewFilter.setEnable(enable);
        viewFilter.tvTitle.setText(getResources().getString(titleIds[3]));
        viewFilter.ivIcon.setImageResource(enable ? R.drawable.icon_video_topbar_filter_nor : R.drawable.icon_video_topbar_filter_dis);
    }

    @Override
    protected void onSingleClick(View v) {
        if (optionClickListener != null) {
            switch (v.getId()) {
                case R.id.viewCountDown:
                    optionClickListener.onCountdownClick();
                    break;
                case R.id.viewFrame:
                    optionClickListener.onSpeedClick();
                    break;
                case R.id.viewRollback:
                    optionClickListener.onRollbackClick();
                    break;
            }
        }
    }

    public ExtProgressBar getProgressBar() {
        return pbProgress;
    }

    public void showAllOption(boolean show) {
        viewCountDown.setVisibility(show ? VISIBLE : INVISIBLE);
        viewFrame.setVisibility(show ? VISIBLE : INVISIBLE);
        viewRollback.setVisibility(show ? VISIBLE : INVISIBLE);
        viewFilter.setVisibility(show ? VISIBLE : INVISIBLE);
    }

    public void setOptionClickListener(OnOptionClickListener optionClickListener) {
        this.optionClickListener = optionClickListener;
    }

    public interface OnOptionClickListener {
        void onCountdownClick();

        void onSpeedClick();

        void onRollbackClick();

        void onFilterClick();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_record_option_panel;
    }
}
