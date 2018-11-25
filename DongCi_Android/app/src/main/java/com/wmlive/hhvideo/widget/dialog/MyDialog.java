package com.wmlive.hhvideo.widget.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by lsq on 6/5/2017.
 * 基础Dialog
 */

@SuppressLint("ValidFragment")
public class MyDialog extends DialogFragment {
    private int iconId;
    private String title;
    private String message;
    private String positive;
    private String negative;
    private String[] items;
    private boolean cancelable;
    private MyDialogClickListener listener;
    private Builder mBuilder;

    public MyDialog() {

    }

    public MyDialog(String title, String message, String positive, String negative, boolean cancelable, MyDialogClickListener listener) {
        this(title, message, null, positive, negative, cancelable, listener);
    }

    public MyDialog(String title, String message, boolean cancelable, MyDialogClickListener listener) {
        this(title, message, null, "确定", "取消", cancelable, listener);
    }

    /**
     * 需要在Dialog中添加items
     *
     * @param title
     * @param message
     * @param positive
     * @param negative
     * @param cancelable
     * @param listener
     */

    public MyDialog(String title, String message, String[] items, String positive, String negative, boolean cancelable, MyDialogClickListener listener) {
        this.title = title;
        this.message = message;
        this.items = items;
        this.positive = positive;
        this.negative = negative;
        this.cancelable = cancelable;
        this.listener = listener;
    }

    public MyDialog(Builder builder) {
        this.mBuilder = builder;
        this.setCancelable(builder.cancelable);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (mBuilder == null) {
            mBuilder = new Builder(getActivity());
            mBuilder.setIconId(iconId);
            mBuilder.setTitle(title);
            mBuilder.setMessage(message);
            mBuilder.setItems(items);
            mBuilder.setPositive(positive);
            mBuilder.setNegative(negative);
            mBuilder.setOnClickListener(listener);
            this.setCancelable(cancelable);
        }
        return mBuilder.create();

    }

    public static class Builder {
        private int iconId = -1;
        private int style = -1;
        private Object view;
        private String title;
        private String message;
        private String positive;
        private String negative;
        private String[] items;
        private boolean cancelable = true;
        private MyDialogClickListener listener;
        private WeakReference<FragmentActivity> mContext;
        private DialogInterface.OnDismissListener mDismissListener;

        public Builder(FragmentActivity context) {
            mContext = new WeakReference<>(context);
        }

        public Builder setIconId(int icon_id) {
            this.iconId = icon_id;
            return this;
        }

        public Builder setStyle(@StyleRes int style) {
            this.style = style;
            return this;
        }

        public Builder setView(Object view) {
            this.view = view;
            return this;
        }

        public Builder setTitle(Object title) {
            if (title instanceof String) {
                this.title = (String) title;
            }
            if (title instanceof Integer) {
                this.title = mContext.get().getString((Integer) title);
            }
            return this;
        }

        public Builder setMessage(Object message) {
            if (message instanceof String) {
                this.message = (String) message;
            }
            if (message instanceof Integer) {
                this.message = mContext.get().getString((Integer) message);
            }
            return this;
        }

        public Builder setPositive(Object positive) {
            if (positive instanceof String) {
                this.positive = (String) positive;
            }
            if (positive instanceof Integer) {
                this.positive = mContext.get().getString((Integer) positive);
            }
            return this;
        }

        public Builder setNegative(Object negative) {
            if (negative instanceof String) {
                this.negative = (String) negative;
            }
            if (negative instanceof Integer) {
                this.negative = mContext.get().getString((Integer) negative);
            }
            return this;
        }

        public Builder setItems(String[] items) {
            this.items = items;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setOnClickListener(MyDialogClickListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setOnDismissListener(DialogInterface.OnDismissListener listener) {
            this.mDismissListener = listener;
            return this;
        }

        private AlertDialog create() {
            Activity activity = mContext.get();
            if (activity != null) {
                AlertDialog.Builder builder;
                if (style != -1) {
                    builder = new AlertDialog.Builder(activity, style);
                } else {
                    builder = new AlertDialog.Builder(activity);
                }

                if (iconId != -1) {
                    builder.setIcon(iconId);
                }

                if (view != null) {
                    if (view instanceof Integer) {
                        builder.setView((Integer) view);
                    }
                    if (view instanceof View) {
                        builder.setView((View) view);
                    }
                }

                if (!TextUtils.isEmpty(title)) {
                    builder.setTitle(title);
                }
                if (!TextUtils.isEmpty(message)) {
                    builder.setMessage(message);
                }

                if (items != null && items.length != 0) {
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (listener != null) {
                                listener.onItemClick(dialog, which);
                            }
                        }
                    });
                }

                if (!TextUtils.isEmpty(positive)) {
                    builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (null != listener) {
                                listener.onPositiveClick(dialog, which);
                            }
                        }
                    });
                }

                if (!TextUtils.isEmpty(negative)) {
                    builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (null != listener) {
                                listener.onNegativeClick(dialog, which);
                            }
                        }
                    });
                }
                return builder.create();
            }
            return null;
        }
    }

    /*使用builder方式调用这个方法即可*/
    public void show() {
        if (mBuilder != null && mBuilder.mContext != null) {
            show(mBuilder.mContext.get());
        } else {
            throw new NullPointerException("You need invoke method show(FragmentActivity context)");
        }
    }

    public void show(FragmentActivity context) {
        show(context, "mydialog");
    }

    public void show(FragmentActivity context, String tag) {
        if (context != null && !isShowing()) {
            FragmentTransaction fragmentTransaction = context.getSupportFragmentManager()
                    .beginTransaction()
                    .add(this, tag);
            if (!context.isDestroyed()) {
                fragmentTransaction.commitAllowingStateLoss();
            }
        }
    }

    public void dismiss() {
        this.dismissAllowingStateLoss();
    }

    public boolean isShowing() {
        return this.getDialog() != null && this.getDialog().isShowing();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mBuilder != null && mBuilder.mDismissListener != null) {
            mBuilder.mDismissListener.onDismiss(dialog);
        }
    }
}
