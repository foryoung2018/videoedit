package com.wmlive.hhvideo.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import cn.wmlive.hhvideo.R;


/**
 * 基础对话框
 * （携带阴影背景）
 */
public class BaseDialog extends Dialog {
    public Context mContext;
    public OnActionSheetSelected mOnActionSheetSelected;
    public BaseDialogOnclicklistener mBaseDialogOnclicklistener;

    /**
     * 存放订阅的容器,方便统一解除订阅
     */
    public BaseDialog(Context context, int styleId) {
        super(context, styleId);
        if (context instanceof Activity) {
            setOwnerActivity((Activity) context);
        }
        mContext = context;
    }

    public BaseDialog(Context context) {
        super(context, R.style.BaseDialogTheme);
        if (context instanceof Activity) {
            setOwnerActivity((Activity) context);
        }
        mContext = context;
    }

    public void setmOnActionSheetSelected(OnActionSheetSelected mOnActionSheetSelected) {
        this.mOnActionSheetSelected = mOnActionSheetSelected;
    }

    public void setmBaseDialogOnclicklistener(BaseDialogOnclicklistener mBaseDialogOnclicklistener) {
        this.mBaseDialogOnclicklistener = mBaseDialogOnclicklistener;
    }

    /**
     * 轮子列表
     */
    public static interface OnActionSheetSelected {

        void onClick(int whichButton);
    }

    /**
     * 确定与取消按钮
     */
    public static interface BaseDialogOnclicklistener {

        public void onOkClick(Dialog dialog);

        public void onCancleClick(Dialog dialog);
    }
}
