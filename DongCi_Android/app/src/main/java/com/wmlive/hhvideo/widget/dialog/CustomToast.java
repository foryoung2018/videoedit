package com.wmlive.hhvideo.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.utils.imageloader.GlideLoader;

import cn.wmlive.hhvideo.R;

/**
 * 自定义对话框
 * <p>
 * 只显示图文，不显示按钮，要按钮请使用CustomDialog
 *
 * @author feijoy
 */
public class CustomToast extends Dialog {
    private Context mContext; // 上下文对象
    private String mToast, mToastSecond, mUrl;
    private OnClickListener mClickListener;
    private LinearLayout llRoot;
    private TextView tvToast, tvToastSecond;
    private ImageView ivToast;
    private boolean isOnlyImg;
    private ImageView ivClose;

    public CustomToast(Context context) {
        super(context, R.style.BaseDialogTheme);
        mContext = context;
    }

    public CustomToast(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    public void setTitle(int titleResId) {
        mToast = (String) mContext.getText(titleResId);
    }

    public void setTitle(String title) {
        mToast = title;
    }

    public void setToastSecond(int toastSecondResId) {
        mToastSecond = (String) mContext.getText(toastSecondResId);
    }

    public void setToastSecond(String mToastSecond) {
        this.mToastSecond = mToastSecond;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public void setOnlyImg(boolean onlyImg) {
        isOnlyImg = onlyImg;
    }

    public void setmClickListener(OnClickListener listener) {
        mClickListener = listener;
    }

    @Override
    public void show() {
        super.show();
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.toast_custom, null);
        addContentView(layout, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        llRoot = (LinearLayout) layout.findViewById(R.id.ll_root);
        if (isOnlyImg) {
            llRoot.setBackgroundResource(android.R.color.transparent);
        } else {

        }

        tvToast = (TextView) layout.findViewById(R.id.tv_toast);
        ivToast = (ImageView) layout.findViewById(R.id.iv_toast);
        ivClose = (ImageView) layout.findViewById(R.id.ivClose);
        tvToastSecond = (TextView) layout.findViewById(R.id.tv_toast_second);

        if (TextUtils.isEmpty(mUrl)) {
            ivToast.setVisibility(View.GONE);
            ivClose.setVisibility(View.GONE);
        } else {
            GlideLoader.loadImage(mUrl, ivToast);
            ivToast.setVisibility(View.VISIBLE);
            ivClose.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(mToast)) {
            tvToast.setVisibility(View.GONE);
        } else {
            tvToast.setVisibility(View.VISIBLE);
            tvToast.setText(mToast);
        }

        if (TextUtils.isEmpty(mToastSecond)) {
            tvToastSecond.setVisibility(View.GONE);
        } else {
            tvToastSecond.setVisibility(View.VISIBLE);
            tvToastSecond.setText(mToastSecond);
        }

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        llRoot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mClickListener.onClick(CustomToast.this,
                        DialogInterface.BUTTON_POSITIVE);
            }
        });

        setContentView(layout);
    }
}