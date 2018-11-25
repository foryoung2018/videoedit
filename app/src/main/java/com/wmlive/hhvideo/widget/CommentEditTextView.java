package com.wmlive.hhvideo.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

@SuppressLint("AppCompatCustomView")
public class CommentEditTextView extends EditText {

    public CommentEditTextView(Context context) {
        super(context);
    }

    public CommentEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommentEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == 1) {
            super.onKeyPreIme(keyCode, event);
            if (onKeyBoardHideListener != null) {
                onKeyBoardHideListener.onKeyHide(keyCode, event);
            }
            return false;
        }
        return super.onKeyPreIme(keyCode, event);
    }


    private OnKeyBoardHideListener onKeyBoardHideListener;

    public void setOnKeyBoardHideListener(OnKeyBoardHideListener onKeyBoardHideListener) {
        this.onKeyBoardHideListener = onKeyBoardHideListener;
    }

    public interface OnKeyBoardHideListener {
        void onKeyHide(int keyCode, KeyEvent event);
    }
}
