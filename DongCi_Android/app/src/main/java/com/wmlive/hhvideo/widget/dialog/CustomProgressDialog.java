package com.wmlive.hhvideo.widget.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import cn.wmlive.hhvideo.R;

/**
 * Created by XueFei on 2017/6/7.
 * <p>
 * 正在加载的 dialog
 */

public class CustomProgressDialog extends ProgressDialog {

    private TextView tv_loading;

    public CustomProgressDialog(Context context) {
        super(context, R.style.BaseDialogThemeNoBackground);
    }

    public void loading() {
        show();
        setContentView(R.layout.dialog_loading);
        tv_loading = findViewById(R.id.tv_loading);
//        setCanceledOnTouchOutside(false);
//        setCancelable(false);
    }

    public void loading(String text) {
        show();
        setContentView(R.layout.dialog_loading);
        tv_loading = findViewById(R.id.tv_loading);
//        setCanceledOnTouchOutside(false);
//        setCancelable(false);
        tv_loading.setText(text);
    }

    public void setText(String s) {
        tv_loading.setText(s);
    }
}
