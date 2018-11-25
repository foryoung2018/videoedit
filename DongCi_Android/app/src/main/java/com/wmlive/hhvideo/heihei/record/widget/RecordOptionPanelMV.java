package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.wmlive.hhvideo.widget.BaseCustomView;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 8/25/2017.
 * 视频录制页面底部的tab
 */

public class RecordOptionPanelMV extends BaseCustomView {
    @BindView(R.id.viewCountDown)
    RecordOptionView viewCountDown;
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

    public RecordOptionPanelMV(Context context) {
        super(context);
    }

    public RecordOptionPanelMV(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {
        viewCountDown.setOnClickListener(this);
        viewFrame.setOnClickListener(this);
        viewRollback.setOnClickListener(this);
        viewFilter.setOnClickListener(view -> {
            if (optionClickListener != null) {
                optionClickListener.onFilterClick();
            }
        });
        viewCountDown.setOnClickListener(v -> {
            if (optionClickListener != null) {
                optionClickListener.onCountdownClick();
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        setCountdownEnable(true);
        setFilterEnable(true);
    }

    public void setCountdownEnable(boolean enable) {
        viewCountDown.setClickable(enable);
        viewCountDown.setEnable(enable);
        viewCountDown.tvTitle.setText(getResources().getString(titleIds[1]));
        viewCountDown.ivIcon.setImageResource(enable ? R.drawable.icon_video_bottombar_count_nor : R.drawable.icon_video_bottombar_count_dis);
    }

    public void setFilterEnable(boolean enable) {
        viewFilter.setClickable(enable);
        viewFilter.setEnable(enable);
        viewFilter.tvTitle.setText(getResources().getString(titleIds[3]));
        viewFilter.ivIcon.setImageResource(enable ? R.drawable.icon_video_topbar_filter_nor : R.drawable.icon_video_topbar_filter_dis);
    }

    @Override
    protected void onSingleClick(View v) {
    }

    public ExtProgressBar getProgressBar() {
        return pbProgress;
    }

    public void showAllOption(boolean show) {
        viewCountDown.setVisibility(show ? VISIBLE : INVISIBLE);
        viewFilter.setVisibility(show ? VISIBLE : INVISIBLE);
    }

    public void setOptionClickListener(OnOptionClickListener optionClickListener) {
        this.optionClickListener = optionClickListener;
    }

    public interface OnOptionClickListener {
        void onCountdownClick();

        void onFilterClick();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_record_option_panel_mv;
    }
}
