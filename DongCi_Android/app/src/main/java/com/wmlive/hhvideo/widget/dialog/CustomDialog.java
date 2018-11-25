package com.wmlive.hhvideo.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.wmlive.hhvideo.utils.imageloader.GlideLoader;

import cn.wmlive.hhvideo.R;

/**
 * 自定义对话框
 *
 * @author feijoy
 */
public class CustomDialog extends Dialog {
    private Context mContext; // 上下文对象
    private String mUrl, mTitle, mContent; // 对话框标题,内容
    private String mConfirmBtnText; // 按钮名称“确定”
    private String mCancelBtnText; // 按钮名称“取消”
    private String mThirdBtnText;//第三个按钮
    private OnClickListener mConfirmBtnClickListener, mCancelBtnClickListener, mThirdBtnClickListener;
    private TextView tvTitle, tvContent, btnConfirm, btnCancel, btnThird;
    private View center_line_one, center_line;
    private ImageView iv_img;
    public CustomDialog(Context context) {
        super(context, R.style.BaseDialogTheme);
        mContext = context;
    }

    public CustomDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public void setTitle(int titleResId) {
        mTitle = (String) mContext.getText(titleResId);
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setContent(int mContentResId) {
        mContent = (String) mContext.getText(mContentResId);
    }

    public void setContent(String mContent) {
        this.mContent = mContent;
    }

    public void setPositiveButton(int confirm_btnText,
                                  OnClickListener listener) {
        mConfirmBtnText = (String) mContext.getText(confirm_btnText);
        mConfirmBtnClickListener = listener;
    }


    public void setPositiveButton(String confirm_btnText,
                                  OnClickListener listener) {
        mConfirmBtnText = confirm_btnText;
        mConfirmBtnClickListener = listener;
    }

    public void setNegativeButton(int cancel_btnText,
                                  OnClickListener listener) {
        mCancelBtnText = (String) mContext.getText(cancel_btnText);
        mCancelBtnClickListener = listener;
    }

    public void setNegativeButton(String cancel_btnText,
                                  OnClickListener listener) {
        mCancelBtnText = cancel_btnText;
        mCancelBtnClickListener = listener;
    }

    /**
     * 第三个按钮
     *
     * @param btnText
     * @param listener
     */
    public void setThirdButton(int btnText,
                               OnClickListener listener) {
        mThirdBtnText = (String) mContext.getText(btnText);
        mThirdBtnClickListener = listener;
    }

    public void setThirdButton(String btnText,
                               OnClickListener listener) {
        mThirdBtnText = btnText;
        mThirdBtnClickListener = listener;
    }

    @Override
    public void show() {
        super.show();
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.dialog_custom, null);
        addContentView(layout, new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        iv_img = (ImageView) layout.findViewById(R.id.iv_img);
        if (TextUtils.isEmpty(mUrl)) {
            iv_img.setVisibility(View.GONE);
        } else {
            GlideLoader.loadImage(mUrl, iv_img);
            iv_img.setVisibility(View.VISIBLE);
        }

        tvTitle = (TextView) layout.findViewById(R.id.tv_title);
        if (TextUtils.isEmpty(mTitle)) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(mTitle);
        }

        tvContent = (TextView) layout.findViewById(R.id.tv_content);
        if (TextUtils.isEmpty(mContent)) {
            tvContent.setVisibility(View.GONE);
        } else {
            tvContent.setText(mContent);
        }

        btnConfirm = ((TextView) layout.findViewById(R.id.confirm_btn));
        btnCancel = ((TextView) layout.findViewById(R.id.cancel_btn));
        btnThird = ((TextView) layout.findViewById(R.id.third_btn));
        center_line = layout.findViewById(R.id.center_line);
        center_line_one = layout.findViewById(R.id.center_line_one);

        if (TextUtils.isEmpty(mConfirmBtnText)) {
            btnConfirm.setVisibility(View.GONE);
        } else {
            btnConfirm.setText(mConfirmBtnText);
            if (mConfirmBtnClickListener != null) {
                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mConfirmBtnClickListener.onClick(CustomDialog.this,
                                DialogInterface.BUTTON_POSITIVE);
                    }
                });
            }
        }

        if (TextUtils.isEmpty(mCancelBtnText)) {
            btnCancel.setVisibility(View.GONE);
        } else {
            center_line_one.setVisibility(View.VISIBLE);
            btnCancel.setText(mCancelBtnText);
            if (mCancelBtnClickListener != null) {
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCancelBtnClickListener.onClick(CustomDialog.this,
                                DialogInterface.BUTTON_NEGATIVE);
                    }
                });
            }
        }

        if (TextUtils.isEmpty(mThirdBtnText)) {
            btnThird.setVisibility(View.GONE);
            center_line.setVisibility(View.GONE);
        } else {
            center_line_one.setVisibility(View.VISIBLE);
            btnThird.setText(mThirdBtnText);
            if (mThirdBtnClickListener != null) {
                btnThird.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mThirdBtnClickListener.onClick(CustomDialog.this,
                                DialogInterface.BUTTON_NEUTRAL);
                    }
                });
            }
        }
        setContentView(layout);
    }
}