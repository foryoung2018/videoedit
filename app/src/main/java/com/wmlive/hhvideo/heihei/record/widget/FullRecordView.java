package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ScreenUtil;
import com.wmlive.hhvideo.widget.BaseCustomView;
import com.wmlive.hhvideo.widget.CustomFontTextView;
import com.wmlive.hhvideo.widget.MaskView;
import com.wmlive.hhvideo.widget.dialog.LoginDialog;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 8/29/2017.
 */

public class FullRecordView extends BaseCustomView {
    @BindView(R.id.rlPreview)
    RelativeLayout rlPreview;
    @BindView(R.id.flMask)
    MaskView flMask;
    @BindView(R.id.ivZoom)
    ImageView ivZoom;
    @BindView(R.id.ivRec_redpoint)
    ImageView ivRec;
    @BindView(R.id.glTouchView)
    GlTouchView glTouchView;
    @BindView(R.id.tv_record_time)
    CustomFontTextView tvTime;
    @BindView(R.id.ll_recorder_time)
    LinearLayout llrecordTime;
    private OnExitFullRecordListener fullRecordListener;

    public FullRecordView(Context context) {
        super(context);
    }

    public FullRecordView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {
        setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        ivZoom.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_full_record;
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.ivZoom:
                if (fullRecordListener != null) {
                    fullRecordListener.onExitFullRecord();
                }
                hideTime();
                break;
        }
    }

    public void showRecording(boolean isRecording) {
        ivZoom.setVisibility(isRecording ? GONE : VISIBLE);
        ivRec.setVisibility(isRecording ? VISIBLE : GONE);
        KLog.i("======enterFullRecord--showRecording--false" + ivZoom.getVisibility());
    }

    public void setVisibleRatio(float ratio) {
        if (flMask != null) {
            flMask.setVisibleRatio(ratio);
            KLog.i("MaskView--onDraw---onSizeChanged-setVisibleRatio>" + ratio);
        }
    }

    public RelativeLayout getPreview() {
        return rlPreview;
    }

    public GlTouchView getGlTouchView() {
        return glTouchView;
    }

    public void releaseView() {
        if (glTouchView != null) {
            glTouchView.recycle();
        }
        glTouchView = null;
    }

    public void setFullRecordListener(OnExitFullRecordListener fullRecordListener) {
        this.fullRecordListener = fullRecordListener;
    }

    public interface OnExitFullRecordListener {
        void onExitFullRecord();
    }

    public void setTime(String time){
        tvTime.setText(time);
    }

    public void showTime(){
        if(llrecordTime.getVisibility() ==View.GONE){
            llrecordTime.setVisibility(View.VISIBLE);
            showRecordAnimotion(View.VISIBLE);
        }
    }

    /**
     * 隐藏时间
     */
    public void hideTime(){
        llrecordTime.setVisibility(View.GONE);
        showRecordAnimotion(View.GONE);
    }

    private void showRecordAnimotion(int i) {
        if (i == View.VISIBLE || i == VISIBLE) {
            KLog.d("可见");
            AlphaAnimation alphaAnimation1 = new AlphaAnimation(0.3f, 1.0f);
            alphaAnimation1.setDuration(700);
            alphaAnimation1.setRepeatCount(Animation.INFINITE);
            alphaAnimation1.setRepeatMode(Animation.REVERSE);
            ivRec.setAnimation(alphaAnimation1);
            alphaAnimation1.start();
        } else if (i == INVISIBLE || i == GONE) {
            KLog.d("不可见");
            ivRec.clearAnimation();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        KLog.d("ggqFull", "onSizeChanged: " + w + "  h==" + h);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        KLog.d("ggqFull", "onLayout: " + "changed==" + changed + "  left==" + left + "  right==" + right + "  bottom==" + bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        KLog.d("ggqFull", "onMeasure: ");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        KLog.d("ggqFull", "onDraw: ");
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        KLog.d("ggqFull", "requestLayout: ");
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams != null) {
            flMask.width = layoutParams.width;
            flMask.height = layoutParams.height;
        }
    }
}
