package com.wmlive.hhvideo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

public class SelectStepPanel extends BaseCustomView {
    @BindView(R.id.llQuickShoot)
    RelativeLayout llQuickShoot;
    @BindView(R.id.llUpload)
    RelativeLayout llUpload;
    @BindView(R.id.llRecord)
    RelativeLayout llRecord;
    @BindView(R.id.ivClose)
    ImageView ivClose;
    private SelectStepPanelListener stepPanelListener;

    public SelectStepPanel(Context context) {
        super(context);
    }

    public SelectStepPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectStepPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {
        llQuickShoot.setOnClickListener(this);
        llUpload.setOnClickListener(this);
        llRecord.setOnClickListener(this);
        ivClose.setOnClickListener(this);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.llQuickShoot:
                stepPanelListener.toQuickShoot();
                break;
            case R.id.llUpload:
                stepPanelListener.toUpload();
                break;
            case R.id.llRecord:
                stepPanelListener.toRecord();
                break;
            case R.id.ivClose:
                dismiss();
            default:
                break;
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.include_select_step_new;
    }

    public void showVisible(){
        setVisibility(VISIBLE);
        llQuickShoot.setVisibility(VISIBLE);
    }

    public void show() {
        startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.alpha_in));
        setVisibility(VISIBLE);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                ivClose.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_rotate_right));
                llQuickShoot.setVisibility(VISIBLE);
                llQuickShoot.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_damp_in));


            }
        }, 200);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                llUpload.setVisibility(VISIBLE);
                llUpload.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_damp_in_1));
            }
        }, 300);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                llRecord.setVisibility(VISIBLE);
                llRecord.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_damp_in));
            }
        }, 400);
    }

    public void dismiss() {
        llQuickShoot.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_damp_out_200));
        ivClose.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_rotate_left));

        postDelayed(new Runnable() {
            @Override
            public void run() {
                llQuickShoot.setVisibility(GONE);
                llRecord.setVisibility(GONE);
                llUpload.setVisibility(GONE);
                setVisibility(GONE);
            }
        }, 200);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                llUpload.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_damp_out_150));
                llRecord.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_damp_out_100));
            }
        }, 50);


    }

    public void setStepPanelListener(SelectStepPanelListener listener) {
        stepPanelListener = listener;
    }

    public interface SelectStepPanelListener {
        void toQuickShoot();

        void toUpload();

        void toRecord();
    }
}
