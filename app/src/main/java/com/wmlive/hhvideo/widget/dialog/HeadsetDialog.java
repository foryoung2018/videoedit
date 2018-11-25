package com.wmlive.hhvideo.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.view.View;

import com.wmlive.hhvideo.utils.MyClickListener;

import butterknife.ButterKnife;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 1/4/2018.12:24 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class HeadsetDialog extends Dialog {
    private HeadsetDialogClickListener headsetDialogClickListener;

    public HeadsetDialog(@NonNull Activity context) {
        super(context, R.style.BaseDialogTheme);
        setContentView(R.layout.dialog_headset);
        ButterKnife.bind(this);
        setCancelable(false);
        setOwnerActivity(context);
        setCanceledOnTouchOutside(false);
        findViewById(R.id.tvCancel).setOnClickListener(clickListener);
        findViewById(R.id.tvContinue).setOnClickListener(clickListener);
    }

    private MyClickListener clickListener = new MyClickListener() {
        @Override
        protected void onMyClick(View v) {
            if (headsetDialogClickListener != null) {
                switch (v.getId()) {
                    case R.id.tvCancel:
                        headsetDialogClickListener.onCancel();
                        dismiss();
                        break;
                    case R.id.tvContinue:
                        headsetDialogClickListener.onContinue();
                        dismiss();
                        break;
                    default:
                        break;

                }
            }

        }
    };

    public HeadsetDialog setHeadsetDialogClickListener(HeadsetDialogClickListener headsetDialogClickListener) {
        this.headsetDialogClickListener = headsetDialogClickListener;
        return this;
    }

    public interface HeadsetDialogClickListener {
        void onCancel();

        void onContinue();
    }
}
