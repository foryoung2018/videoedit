package com.wmlive.hhvideo.heihei.mainhome.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.heihei.quickcreative.ChooseStyle4QuickActivity;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.TimeUtil;
import com.wmlive.hhvideo.widget.BaseCustomView;

import org.greenrobot.greendao.annotation.NotNull;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

import static com.wmlive.hhvideo.heihei.quickcreative.ChooseStyle4QuickActivity.SEEKBAR;

/**
 * Created by hsing on 2018/4/10.
 */

public class VideoControlBar extends BaseCustomView implements SeekBar.OnSeekBarChangeListener {

    @BindView(R.id.ivVideoPlay)
    public ImageView ivVideoPlay;
    @BindView(R.id.tvCurrentTime)
    public TextView tvCurrentTime;
    @BindView(R.id.sbPosition)
    public SeekBar sbPosition;
    @BindView(R.id.tvEndTime)
    public TextView tvEndTime;
    @BindView(R.id.ivFullScreen)
    public ImageView ivFullScreen;

    private String type;

    private OnControlBarClickListener mOnControlBarClickListener;


    public VideoControlBar(Context context) {
        super(context);
    }

    public VideoControlBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoControlBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.video_control_bar_view;
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {
        setListener();
    }

    private void setListener() {
        ivVideoPlay.setOnClickListener(this);
        ivFullScreen.setOnClickListener(this);
        sbPosition.setOnSeekBarChangeListener(this);
    }


    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.ivVideoPlay:
                if (null != mOnControlBarClickListener) {
                    mOnControlBarClickListener.onPlayClick();
                }
                break;
            case R.id.ivFullScreen:
                if (null != mOnControlBarClickListener) {
                    boolean isFull = isFullScreen();
                    mOnControlBarClickListener.onFullScreenClick(needRotate());
                    ivFullScreen.setTag(isFull ? 1 : 2);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (null != mOnControlBarClickListener) {
            mOnControlBarClickListener.onProgressChanged(seekBar, progress, fromUser);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (null != mOnControlBarClickListener) {
            mOnControlBarClickListener.onStartTrackingTouch(seekBar);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (null != mOnControlBarClickListener) {
            mOnControlBarClickListener.onStopTrackingTouch(seekBar);
        }
    }

    public void setOnControlBarListener(@NotNull OnControlBarClickListener listener) {
        mOnControlBarClickListener = listener;
    }

    /**
     * 设置时间进度条
     *
     * @param duration        msec
     * @param currentPosition msec
     */
    public void setPosition(int duration, int currentPosition) {
        tvCurrentTime.setText(TimeUtil.getHSFormat(currentPosition / 1000));
        tvEndTime.setText(TimeUtil.getHSFormat(duration / 1000));
        sbPosition.setProgress((int) (currentPosition * 100f / duration));
    }

    public void init(String showType) {
        type = showType;
        KLog.d("type==" + type);
        if (ShortVideoItem.VIDEO_SHOW_TYPE_FULL_SCREEN.equals(showType)) {
            ivFullScreen.setVisibility(VISIBLE);
            ivFullScreen.setTag(0);
            tvCurrentTime.setTextColor(getResources().getColor(R.color.white));
            tvEndTime.setTextColor(getResources().getColor(R.color.white));
            sbPosition.setProgressDrawable(getResources().getDrawable(R.drawable.video_seekbar_progress_white));
            sbPosition.setThumb(getResources().getDrawable(R.drawable.video_seekbar_thumb_white));
            ivVideoPlay.setImageDrawable(getResources().getDrawable(R.drawable.icon_video_pause_white));
            ivFullScreen.setImageDrawable(getResources().getDrawable(R.drawable.icon_video_full_white));
        } else if (ShortVideoItem.VIDEO_SHOW_TYPE_TOP_SCREEN.equals(showType)) {
            ivFullScreen.setVisibility(VISIBLE);
            ivFullScreen.setTag(0);
            tvCurrentTime.setTextColor(getResources().getColor(R.color.video_text_color));
            tvEndTime.setTextColor(getResources().getColor(R.color.video_text_color));
            sbPosition.setProgressDrawable(getResources().getDrawable(R.drawable.video_seekbar_progress));
            sbPosition.setThumb(getResources().getDrawable(R.drawable.video_seekbar_thumb_red));
            ivVideoPlay.setImageDrawable(getResources().getDrawable(R.drawable.icon_video_pause));
            ivFullScreen.setImageDrawable(getResources().getDrawable(R.drawable.icon_video_full));
        } else if (ShortVideoItem.VIDEO_SHOW_TYPE_CENTER_SCREEN.equals(showType)) {
            ivFullScreen.setVisibility(VISIBLE);
            ivFullScreen.setTag(1);
            tvCurrentTime.setTextColor(getResources().getColor(R.color.video_text_color));
            tvEndTime.setTextColor(getResources().getColor(R.color.video_text_color));
            sbPosition.setProgressDrawable(getResources().getDrawable(R.drawable.video_seekbar_progress));
            sbPosition.setThumb(getResources().getDrawable(R.drawable.video_seekbar_thumb_red));
            ivVideoPlay.setImageDrawable(getResources().getDrawable(R.drawable.icon_video_pause));
            ivFullScreen.setImageDrawable(getResources().getDrawable(R.drawable.icon_video_full));
        } else if (showType.equals(SEEKBAR)) {
            ivFullScreen.setVisibility(View.GONE);
            tvCurrentTime.setTextColor(getResources().getColor(R.color.white));
            tvEndTime.setTextColor(getResources().getColor(R.color.white));
            sbPosition.setProgressDrawable(getResources().getDrawable(R.drawable.video_seekbar_progress_white));
            sbPosition.setThumb(getResources().getDrawable(R.drawable.video_seekbar_thumb_white));
            ivVideoPlay.setImageDrawable(getResources().getDrawable(R.drawable.icon_video_pause_white));
        } else {
            ivFullScreen.setTag(0);
        }
    }

    public boolean isFullScreen() {
        KLog.i("=======isFullScreen");
        if (ivFullScreen.getTag() != null && ivFullScreen.getTag() instanceof Integer) {
            return (int) ivFullScreen.getTag() > 1;
        }
        return false;
    }

    public boolean needRotate() {
        return type.equals(ShortVideoItem.VIDEO_SHOW_TYPE_FULL_SCREEN) || type.equals(ShortVideoItem.VIDEO_SHOW_TYPE_TOP_SCREEN);

    }

    public void setFullScreen(boolean full) {
        KLog.i("=======setFullScreen");
        ivFullScreen.setTag(full ? 2 : 1);
    }

    /**
     * 设置播放状态
     *
     * @param isPlay   是否播放
     * @param showType
     */
    public void setPlayStatus(boolean isPlay, String showType) {
        KLog.i("=======setPlayStatus:" + isPlay + " ,showType:" + showType);
        if (ShortVideoItem.VIDEO_SHOW_TYPE_FULL_SCREEN.equals(showType)) {
            ivVideoPlay.setImageDrawable(getResources().getDrawable(isPlay ? R.drawable.icon_video_play_white : R.drawable.icon_video_pause_white));
        } else if (ShortVideoItem.VIDEO_SHOW_TYPE_TOP_SCREEN.equals(showType)) {
            ivVideoPlay.setImageDrawable(getResources().getDrawable(isPlay ? R.drawable.icon_video_play : R.drawable.icon_video_pause));
        } else if (ShortVideoItem.VIDEO_SHOW_TYPE_CENTER_SCREEN.equals(showType)) {
            ivVideoPlay.setImageDrawable(getResources().getDrawable(isPlay ? R.drawable.icon_video_play : R.drawable.icon_video_pause));
        } else if (showType.equals(SEEKBAR)) {
            ivVideoPlay.setImageDrawable(getResources().getDrawable(isPlay ? R.drawable.icon_video_play_white : R.drawable.icon_video_pause_white));
        }
    }

    public interface OnControlBarClickListener {

        void onPlayClick();

        void onFullScreenClick(boolean isFull);

        void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser);

        void onStartTrackingTouch(SeekBar seekBar);

        void onStopTrackingTouch(SeekBar seekBar);
    }
}
