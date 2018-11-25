package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.wmlive.hhvideo.heihei.beans.record.MusicInfoEntity;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ScreenUtil;
import com.wmlive.hhvideo.widget.BaseCustomView;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;


/**
 * Created by wenlu on 2017/9/9.
 */

public class CustomTrimMusicView extends BaseCustomView {
    private static final String TAG = CustomTrimMusicView.class.getSimpleName();
    private static final int MSG_PREPARED = 565;//播放器准备成功
    private static final int MSG_RESTORE = 575;//还原截取音乐时间轴

    @BindView(R.id.mCropMusic)
    public CropView mCropMusic;
    @BindView(R.id.mCropMusicBg)
    public CropViewBg mCropMusicBg;
    @BindView(R.id.mHorScrollView)
    public ExtHorizontalScrollView mHorScrollView;
    @BindView(R.id.musicContentLayout)
    public LinearLayout mMusicLayout;
    @BindView(R.id.tv_trim_duration)
    public TextView mTvTrimDuration;
    @BindView(R.id.tv_trim_start)
    public TextView mTvTrimStart;
    @BindView(R.id.esb_trim_music)
    public ExtRangeSeekbarPlus mSeekbarTrimMusic;

    private int mMusicSrcDuration = 1000;
    private int minMusic = 0, maxMusic = 1000;
    private int maxCropMusic = 1000;
    private int currentProgress = 0;
    private boolean isMultEdit = false;
    //定义每段音乐截取最大15秒
    private int itemDuration = 60000;
    private int maxDuration;
    private int mScreenWidth = 0;

    private float lengthRatio; // 视频最大长度与截取长度的比例
    private OnRangeChangeListener mListener;
    private View paddingView;
    private boolean isUserScroll = true;

    private Handler mHanlder = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            if (msg.what == MSG_PREPARED) {
//                mCropMusic.setInterval(minMusic, maxMusic);
//                mCropMusic.setProgress(minMusic);
//                mScreenWidth = CoreUtils.getMetrics().widthPixels;
//
//                int marginLeft = mCropMusic.getMarginLeft();
//                int itemWidth = (int) (mScreenWidth * lengthRatio) - marginLeft * 2;
////                int itemWidth = mCropMusic.getItemWidth();//获取15s的音频px
//                float musicWidth = itemWidth * ((mCropMusic.getDuration() + 0.0f) / itemDuration);
//                /** 最小最大值间的像素 */
//                float mCurrentItemWidth = itemWidth * (mCropMusic.getMax() - mCropMusic.getMin() + 0.0f) / itemDuration;
//                /** 末尾填补空白 */
//                float mNoneWidth = itemWidth - mCurrentItemWidth;
//
//                /** 左右margin的margin和 */
//                float allmargin = marginLeft + (marginLeft + mNoneWidth);
//                float tWidth = allmargin + musicWidth;
//
//                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) tWidth, getResources().getDimensionPixelSize(R.dimen.t60dp));
//                lp.leftMargin = 0;
//                lp.rightMargin = 0;
////                KLog.e(TAG, "onPrepared: " + tWidth + "*" + lp.height);
//                mCropMusic.setMargin(marginLeft, (int) (marginLeft + mNoneWidth));
//                mCropMusic.setLayoutParams(lp);
//                Rect rect = new Rect(marginLeft, 0, (int) (marginLeft + mCurrentItemWidth), lp.height);
//                mCropMusicBg.setItemDuration(rect, mScreenWidth - rect.right, marginLeft);
//                mCropMusicBg.setMin(minMusic);
//
//                mSeekbarTrimMusic.setPadding(ScreenUtil.dip2px(getContext(),10));
//                if (tWidth < CoreUtils.getMetrics().widthPixels) {
//                    // 根据图册的长度绘制SeekBar长度
//                    FrameLayout.LayoutParams fllp = new FrameLayout.LayoutParams((int) tWidth, getResources().getDimensionPixelSize(R.dimen.t60dp));
//                    mSeekbarTrimMusic.setLayoutParams(fllp);
//                    mSeekbarTrimMusic.setSeekbarWidth((int) tWidth);
//                } else {
//                    FrameLayout.LayoutParams fllp = new FrameLayout.LayoutParams(
//                            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
//                    mSeekbarTrimMusic.setLayoutParams(fllp);
//                    mSeekbarTrimMusic.setSeekbarWidth(CoreUtils.getMetrics().widthPixels);
//                }
//                float itemDurationSec = itemDuration / 1000f;
//                float maxDurationSec = maxDuration / 1000f;
//                float maxSec = maxMusic / 1000f;
//                float minSec = minMusic / 1000f;
//                if (isMultEdit) {
//                    mSeekbarTrimMusic.setDuration(maxDurationSec);
//                    mSeekbarTrimMusic.setMinDuration(0);
//                    mSeekbarTrimMusic.setMaxDuration(maxDurationSec);
//                    mSeekbarTrimMusic.setSeekBarRangeValues(0, itemDurationSec);
//                    mCropMusic.setCropRange(maxSec - minSec);
//                } else {
//                    mSeekbarTrimMusic.setDuration(itemDurationSec);
//                    mSeekbarTrimMusic.setMinDuration(0);
//                    mSeekbarTrimMusic.setMaxDuration(itemDurationSec);
//                    mSeekbarTrimMusic.setSeekBarRangeValues(0, itemDurationSec);
//                    mCropMusic.setCropRange(maxSec - minSec);
//                }
//                mTvTrimDuration.setText(getContext().getString(R.string.edit_trim_duration, String.format("%.1f", itemDurationSec)));
//                mTvTrimStart.setText(getContext().getString(R.string.edit_trim_music_start_time, String.format("%.1f", minMusic / 1000f)));
//                mSeekbarTrimMusic.invalidate();
//
//                mHanlder.removeMessages(MSG_RESTORE);
//                mHanlder.sendEmptyMessage(MSG_RESTORE);
//            } else if (msg.what == MSG_RESTORE) {//huanyu
//                if (minMusic == 0) {
//                    mHorScrollView.appScrollTo(mCropMusic.getLeftMinBar(), true);
//                } else {
//                    isUserScroll = false;
//                    mHorScrollView.appScrollTo(mCropMusic.getProgressPx(), true);
//                }
//            }
        }
    };

    public CustomTrimMusicView(Context context) {
        super(context);
    }

    public CustomTrimMusicView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTrimMusicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getMinMusic() {
        return minMusic;
    }


    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {
        paddingView = new View(getContext());
        mMusicLayout.addView(paddingView);
        mSeekbarTrimMusic.setOnRangSeekBarChangeListener(new ExtRangeSeekbarPlus.OnRangeSeekBarChangeListener() {
            @Override
            public boolean beginTouch(int thumbPressed) {
                return false;
            }

            @Override
            public void rangeSeekBarValuesChanged(float minValue, float maxValue, float currentValue) {
                KLog.e(TAG, "rangeSeekBarValuesChanged: " + minValue + "..." + maxValue + ".." + currentValue);
                ViewGroup.LayoutParams params = paddingView.getLayoutParams();
                int blankWidth = mHorScrollView.getRight() - mCropMusic.getRight();
                int paddingWidth = mSeekbarTrimMusic.getRight() - mSeekbarTrimMusic.getHandleRight();
                params.width = blankWidth + paddingWidth;
                paddingView.setLayoutParams(params);
                mCropMusic.setCropRange(maxValue - minValue);
                if (mListener != null) {
                    mListener.rangeSeekBarValuesChanged(minValue, maxValue, currentValue, currentValue + (maxValue - minValue));
                }
            }

            @Override
            public void rangeSeekBarValuesChanging(float setValue) {
                KLog.e(TAG, "rangeSeekBarValuesChanged: " + setValue);
                if (mListener != null) {
                    mListener.rangeSeekBarValuesChanging(setValue);
                }
                float duration = mSeekbarTrimMusic.getSelectedMaxValue() - mSeekbarTrimMusic.getSelectedMinValue();
                mTvTrimDuration.setText(getContext().getString(R.string.edit_trim_duration, String.format("%.1f", duration)));
            }
        });

        mSeekbarTrimMusic.setIsCropMusic(true);
        setMultEdit(true);

    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_music_edit_layout;
    }

    private ExtHorizontalScrollView.ScrollViewListener mScrollViewListener = new ExtHorizontalScrollView.ScrollViewListener() {
        @Override
        public void onScrollBegin(View view, int scrollX, int scrollY, boolean appScroll) {
            KLog.e(TAG, "onScrollBegin: " + scrollX + "..." + appScroll);
            if (!appScroll) {
                mCropMusic.setScrollX(scrollX, mScreenWidth);
            }

        }

        @Override
        public void onScrollProgress(View view, int scrollX, int scrollY, boolean appScroll) {
            KLog.e(TAG, "onScrollProgress: " + scrollX + "..." + appScroll);
            if (!appScroll) {
                mCropMusic.setScrollX(scrollX, mScreenWidth);
            }
        }

        @Override
        public void onScrollEnd(View view, int scrollX, int scrollY, boolean appScroll) {
            KLog.e(TAG, "onScrollEnd: " + scrollX + "..." + appScroll);
            if (!appScroll && mCropMusic != null) {
                mCropMusic.setScrollX(scrollX, mScreenWidth);
                minMusic = mCropMusic.getMin();
                mSeekbarTrimMusic.setMinValue(minMusic);
                // 滚动条总长度
                maxMusic = mCropMusic.getMax();
                // 裁剪最大值
                maxCropMusic = minMusic + (int) (mCropMusic.getCropRange() * 1000);
                KLog.e(TAG, "onScrollEnd: minMusic" + minMusic + "...maxMusic " + maxMusic);
                currentProgress = mCropMusic.getProgress();
                mTvTrimStart.setText(getContext().getString(R.string.edit_trim_music_start_time, String.format("%.1f", currentProgress / 1000f)));
                if (mListener != null) {
                    mListener.onScrollChanged(minMusic / 1000f, maxMusic / 1000f, currentProgress / 1000f, maxCropMusic / 1000f, isUserScroll);
                    isUserScroll = true;
                }
            }

        }
    };

    /**
     * 是否多视频编辑
     *
     * @param multEdit
     */
    public void setMultEdit(boolean multEdit) {
        isMultEdit = multEdit;
    }


    public void setDuration(float maxVideoDuration) {
        mHorScrollView.addScrollListener(mScrollViewListener);
        MusicInfoEntity musicInfo = RecordManager.get().getProductEntity().musicInfo;
        if (null != musicInfo && !TextUtils.isEmpty(musicInfo.getMusicPath())) {
            mMusicSrcDuration = (int) (musicInfo.getDuring() * 1000);
            minMusic = (int) (musicInfo.trimStart * 1000);
            maxMusic = (int) (musicInfo.trimEnd * 1000);
            int maxVideoInt = (int) (maxVideoDuration * 1000);
            maxMusic = maxMusic > maxVideoInt ? maxVideoInt : maxMusic;

            if (isMultEdit) {
                itemDuration = (int) Math.min(mMusicSrcDuration, Math.min(maxVideoDuration * 1000, maxMusic - minMusic));
                maxDuration = (int) Math.min(mMusicSrcDuration, Math.max(maxVideoDuration * 1000, maxMusic - minMusic));
            } else {
                maxMusic = minMusic + mMusicSrcDuration;
                itemDuration = (int) Math.min(mMusicSrcDuration, Math.max(maxVideoDuration * 1000, maxMusic - minMusic));
            }
            if (maxVideoDuration == 0) {
                maxVideoDuration = 1;
            }
            lengthRatio = itemDuration / (maxVideoDuration * 1000);
            mCropMusic.setDuration(mMusicSrcDuration, itemDuration);
            mHanlder.sendEmptyMessage(MSG_PREPARED);
        }
    }

    public void setOnRangeChangeListener(OnRangeChangeListener listener) {
        mListener = listener;
    }

    public void setProgress(int progress) {
        mCropMusic.setProgress(progress);
    }

    /**
     * 裁剪音乐回调
     */
    public interface OnRangeChangeListener {
        /**
         * seek bar响应值发生改变完成后
         *
         * @param minValue
         * @param maxValue
         * @param currentValue
         */
        void rangeSeekBarValuesChanged(float minValue, float maxValue, float currentValue, float maxCropMusic);

        /**
         * seek bar响应值改变时
         *
         * @param setValue
         */
        void rangeSeekBarValuesChanging(float setValue);

        /**
         * 当滚动条滚动时
         *
         * @param minValue     最小
         * @param maxValue     滚动条最大值
         * @param currentValue 当前值
         * @param maxCropMusic 裁剪
         * @param isUserScroll
         */
        void onScrollChanged(float minValue, float maxValue, float currentValue, float maxCropMusic, boolean isUserScroll);
    }

//    /**
//     * 开始循环遍历
//     */
//    public void startRunnable(){
//        mHandler.removeCallbacks(progressRunnable);
//        mHandler.post(progressRunnable);
//    }
//
//    public void stopRunnable(){
//        mHandler.removeCallbacks(progressRunnable);
//    }
//
//    /**
//     * 更新当前播放器进度
//     */
//    private Runnable progressRunnable = new Runnable() {
//        @Override
//        public void run() {
//            MusicPlayer musicPlayer = RecordManager.get().getMusicPlayer();
//            if (null != musicPlayer) {
//                    setProgress((int) (getMinMusic() + musicPlayer.getCurrentPostion() * 1000));
//                    mHandler.removeCallbacks(this);
//                    mHandler.postDelayed(this, 150);
//            }
//
//        }
//    };
//
//    Handler mHandler = new Handler();

}
