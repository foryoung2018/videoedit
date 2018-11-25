package com.wmlive.hhvideo.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.view.View;

import com.wmlive.hhvideo.utils.MyClickListener;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 5/31/2018 - 2:18 PM
 * 类描述：
 */
public class RemindDialog extends Dialog {
    private RemindClickListener listener;

    public RemindDialog(@NonNull Activity context) {
        super(context, R.style.BaseDialogTheme);
        setContentView(R.layout.dialog_remind);
        setCancelable(false);
        setOwnerActivity(context);
        setCanceledOnTouchOutside(false);
        findViewById(R.id.tvOk).setOnClickListener(new MyClickListener() {
            @Override
            protected void onMyClick(View v) {
                if (listener != null) {
                    listener.onOkClick();
                }
                dismiss();
            }
        });
    }

    public RemindDialog setListener(RemindClickListener listener) {
        this.listener = listener;
        return this;
    }

    public interface RemindClickListener {
        void onOkClick();
    }

}
