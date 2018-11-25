package com.wmlive.hhvideo.widget.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wmlive.hhvideo.utils.ScreenUtil;

import cn.wmlive.hhvideo.R;

/**
 * Created by XueFei on 2017/6/05.
 * <p>
 * 举报pop
 */

public class PopActionSheetNoTitle extends PopupWindow implements OnClickListener {

    private boolean isUserBlock;

    public interface OnSnsClickListener {

        void onSnsClick();

        /**
         * 用户拉黑
         *
         * @param isUserBlock 当前是否拉黑
         */
        void onUserBlockClick(boolean isUserBlock);
    }

    public void setOnSnsClickListener(OnSnsClickListener l) {
        this.l = l;
    }

    private OnSnsClickListener l;

    private View root;
    private LinearLayout menuContainer;
    private Context context;

    private String mConfirmBtnText; // 按钮名称“确定”
    private String mCancelBtnText; // 按钮名称“取消”

    private TextView btnActionSheetConfirm, btnActionSheetCancel;
    private TextView btnActionBlock;

    public PopActionSheetNoTitle(Activity context) {
        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root = inflater.inflate(R.layout.pop_action_sheet_no_title, null);

        btnActionSheetConfirm = (TextView) root.findViewById(R.id.btn_action_sheet_confirm);
        btnActionSheetCancel = (TextView) root.findViewById(R.id.btn_action_sheet_cancel);
        btnActionBlock = root.findViewById(R.id.btn_action_block);

        btnActionSheetConfirm.setOnClickListener(this);
        btnActionSheetCancel.setOnClickListener(this);
        btnActionBlock.setOnClickListener(this);

        menuContainer = (LinearLayout) root
                .findViewById(R.id.share_action_sheet_button_container);

        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        root.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = menuContainer.getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

    public void setIsUserBlock(boolean isUserBlock) {
        this.isUserBlock = isUserBlock;
        btnActionBlock.setText(isUserBlock ? R.string.user_unblock : R.string.user_block);
    }

    public void setmConfirmBtnText(String mConfirmBtnText) {
        this.mConfirmBtnText = mConfirmBtnText;
    }

    public void setmCancelBtnText(String mCancelBtnText) {
        this.mCancelBtnText = mCancelBtnText;
    }


    public void show() {
        if (!TextUtils.isEmpty(mConfirmBtnText)) {
            btnActionSheetConfirm.setText(mConfirmBtnText);
        }
        if (!TextUtils.isEmpty(mCancelBtnText)) {
            btnActionSheetCancel.setText(mCancelBtnText);
        }
        // 设置SelectPicPopupWindow的View
        this.setContentView(root);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        // this.setAnimationStyle(R.style.popwin_anim_style);
        ColorDrawable dw = new ColorDrawable(context.getResources().getColor(android.R.color.transparent));
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        this.showAtLocation(root, Gravity.BOTTOM
                | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private void hide() {
        super.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_action_sheet_confirm:
                if (null != l) {
                    l.onSnsClick();
                }
                break;
            case R.id.btn_action_block:
                if (null != l) {
                    l.onUserBlockClick(isUserBlock);
                }
                break;
            case R.id.btn_action_sheet_cancel:
                break;
            default:
                break;
        }
        this.dismiss();
    }
}
