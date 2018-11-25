package com.wmlive.hhvideo.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.utils.DeviceUtils;

import cn.wmlive.hhvideo.R;

/**
 * 升级对话框
 */
public class UpDateDialog extends Dialog implements OnClickListener {

    public UpDateDialog(Context context) {
        super(context, R.style.BaseDialog);
        if (context instanceof Activity)
            setOwnerActivity((Activity) context);

        setCancelable(false);
        setCanceledOnTouchOutside(false);

        setContentView(R.layout.dialog_def);

        Window w = getWindow();
        if (w != null) {
            WindowManager.LayoutParams lp = w.getAttributes();
            lp.width = (int) (DeviceUtils.getScreenWH(DCApplication.getDCApp())[0] * 0.75);
            lp.gravity = Gravity.CENTER;
            onWindowAttributesChanged(lp);
        }

        autoLoad_dialog_def();

        cancel.setOnClickListener(this);
        ok.setOnClickListener(this);
    }

    // ----------------R.layout.dialog_def-------------Start
    private TextView tips_message;
    private LinearLayout cancel;
    private TextView cancel_text;
    private LinearLayout ok;
    private TextView ok_text;

    public void autoLoad_dialog_def() {
        tips_message = (TextView) findViewById(R.id.tips_message);
        cancel = (LinearLayout) findViewById(R.id.cancel);
        cancel_text = (TextView) findViewById(R.id.cancel_text);
        ok = (LinearLayout) findViewById(R.id.ok);
        ok_text = (TextView) findViewById(R.id.ok_text);
    }

    // ----------------R.layout.dialog_def-------------End

    public void setContent(CharSequence string) {
        tips_message.setText(string);
    }

    public void setBtnCancelText(CharSequence txt) {
        cancel_text.setText(txt);
    }

    public void setBtnCancelVisibity(int visibity) {
        cancel.setVisibility(visibity);
    }

    public void setBtnOKText(CharSequence txt) {
        ok_text.setText(txt);
    }


    public void setBaseDialogOnclicklistener(BaseDialog.BaseDialogOnclicklistener listener) {
        this.listener = listener;
    }

    BaseDialog.BaseDialogOnclicklistener listener;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok: {
                if (listener != null)
                    listener.onOkClick(this);
            }
            break;
            case R.id.cancel: {
                if (listener != null)
                    listener.onCancleClick(this);
            }
            break;
        }
    }

}
