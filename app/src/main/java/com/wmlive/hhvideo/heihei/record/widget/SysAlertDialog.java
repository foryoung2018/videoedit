package com.wmlive.hhvideo.heihei.record.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.wmlive.hhvideo.heihei.record.utils.CoreUtils;

import java.lang.ref.SoftReference;

import cn.wmlive.hhvideo.R;

/**
 * 显示提示框
 *
 * @author abreal
 */
public class SysAlertDialog {

    public interface CancelListener {
        void cancel();
    }

    private static Dialog m_dlgLoading;
    public static final int LENGTH_SHORT = 2 * 1000;
    public static final int LENGTH_LONG = 5 * 1000;
    private static Handler handler = new Handler(Looper.getMainLooper());
    private static SoftReference<Toast> wrToast = null;
    private static Object synObj = new Object();
    private static SoftReference<Toast> wrToastScore = null;
    private static Object synObjScore = new Object();

    public void onDestory(){
        if(m_dlgLoading!=null)
            m_dlgLoading.dismiss();
        m_dlgLoading = null;
    }

    /**
     * 取消加载中对话框
     */
    public static synchronized void cancelLoadingDialog() {
        if (m_dlgLoading != null) {
            try {
                m_dlgLoading.cancel();
            } catch (Exception ex) {
            }
            m_dlgLoading = null;
        }
    }


    public static Dialog showAlertDialog(Context context, int nMessageResId,
                                         int nPositiveBtnResId,
                                         OnClickListener positiveButtonClickListener,
                                         int nNegativeBtnResId,
                                         OnClickListener negativeButtonClickListener) {
        Resources res = context.getResources();
        return showAlertDialog(context, getString(res, nMessageResId),
                getString(res, nPositiveBtnResId), positiveButtonClickListener,
                getString(res, nNegativeBtnResId), negativeButtonClickListener);
    }

    public static Dialog showAlertDialog(Context context, int nTitleResId,
                                         int nMessageResId, int nPositiveBtnResId,
                                         OnClickListener positiveButtonClickListener,
                                         int nNegativeBtnResId,
                                         OnClickListener negativeButtonClickListener) {
        Resources res = context.getResources();
        return showAlertDialog(context, getString(res, nTitleResId),
                getString(res, nMessageResId),
                getString(res, nPositiveBtnResId), positiveButtonClickListener,
                getString(res, nNegativeBtnResId), negativeButtonClickListener);
    }

    public static Dialog showAlertDialog(Context context, String strMessage,
                                         String strPositiveBtnText,
                                         OnClickListener positiveButtonClickListener,
                                         String strNegativeBtnText,
                                         OnClickListener negativeButtonClickListener) {
        Dialog dlg = createAlertDialog(context, null, strMessage,
                strPositiveBtnText, positiveButtonClickListener,
                strNegativeBtnText, negativeButtonClickListener, true, null);
        dlg.show();
        return dlg;
    }

    public static Dialog showAlertDialog(Context context, String strTitle,
                                         String strMessage, String strPositiveBtnText,
                                         OnClickListener positiveButtonClickListener,
                                         String strNegativeBtnText,
                                         OnClickListener negativeButtonClickListener) {
        Dialog dlg = createAlertDialog(context, TextUtils.isEmpty(strTitle) ? ""
                        : strTitle, strMessage, strPositiveBtnText,
                positiveButtonClickListener, strNegativeBtnText,
                negativeButtonClickListener, true, null);
        try {
            dlg.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dlg;
    }

    public static Dialog showAlertDialog(Context context, String strTitle,
                                         String strMessage, CharSequence[] items,
                                         final OnClickListener listener) {
        Dialog dlg = createAlertDialog(context,
                TextUtils.isEmpty(strTitle) ? "" : strTitle, strMessage, items,
                listener, true, null);
        try {
            dlg.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dlg;
    }

    /**
     * @param context
     * @param titleId
     * @param msgId
     * @param duration
     */
    public static void showAutoHideDialog(Context context, int titleId,
                                          int msgId, int duration) {

        showAutoHideDialog(context, context.getString(titleId),
                context.getString(msgId), duration);
    }

    /**
     * 创建自定义水平进度条对话框
     *
     * @param context
     * @param message
     * @param indeterminate
     * @param cancelable
     * @return
     */
    public static CircleProgressDialog showCircleProgressDialog(Context context,
                                                                String message, boolean indeterminate, boolean cancelable) {
        CircleProgressDialog dialog = new CircleProgressDialog(context);
        dialog.setMessage(message);
        dialog.setIndeterminate(indeterminate);
        dialog.setCancelable(cancelable);
        dialog.show();
        return dialog;
    }

    public static CircleProgressDialog createCircleProgressDialog(Context context,
                                                                  String message, boolean indeterminate, boolean cancelable) {
        CircleProgressDialog dialog = new CircleProgressDialog(context);
        dialog.setMessage(message);
        dialog.setIndeterminate(indeterminate);
        dialog.setCancelable(cancelable);
        return dialog;
    }


    /**
     * 创建自定义转圈进度条对话框
     *
     * @param context
     * @param message
     * @param indeterminate
     * @param cancelable
     * @return
     */
    public static HoriProgressDialog showHoriProgressDialog(Context context,
                                                            String message, boolean indeterminate, boolean cancelable) {
        HoriProgressDialog dialog = new HoriProgressDialog(context);
        dialog.setMessage(message);
        dialog.setIndeterminate(indeterminate);
        dialog.setCancelable(cancelable);
        dialog.show();
        return dialog;
    }

    /**
     * @param context
     * @param strTitle
     * @param strMessage
     * @param duration
     */
    public static void showAutoHideDialog(final Context context,
                                          final String strTitle, final String strMessage, final int duration) {
//
//        ThreadPoolUtils.execute(new Runnable() {
//            public void run() {
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        synchronized (synObj) {
//                            if (wrToast != null) {
//                                if (android.os.Build.VERSION.SDK_INT < 14) { // android
//                                    // 4.0以上不调用cancel,cancel会造成show生成异常！
//                                    if (wrToast.get() != null) {
//                                        wrToast.get().cancel();
//                                    }
//                                }
//                                if (wrToast.get() != null) {
//                                    refreshToast(context, strTitle, strMessage,
//                                            duration);
//                                } else {
//                                    newToast(context, strTitle, strMessage,
//                                            duration);
//                                }
//                            } else {
//                                newToast(context, strTitle, strMessage,
//                                        duration);
//                            }
//                            if (wrToast.get() != null) {
//                                wrToast.get().show();
//                            }
//                        }
//                    }
//                });
//            }
//        });

    }

    /**
     * 创建一个自定义toast
     *
     * @param context
     * @param strTitle
     * @param strMessage
     * @param duration
     */

    @SuppressLint("ShowToast")
    private static void newToast(Context context, String strTitle,
                                 String strMessage, int duration) {
        try {
            Toast toast = Toast.makeText(context, strMessage, duration);
            wrToast = new SoftReference<Toast>(toast);
            refreshToast(context, strTitle, strMessage, duration);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 刷新自定义toast
     *
     * @param context
     * @param strTitle
     * @param strMessage
     * @param duration
     */
    private static void refreshToast(Context context, String strTitle,
                                     String strMessage, int duration) {
        LayoutInflater inflate = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.auto_hide_dialog, null);
        // v.getBackground().setAlpha(50);
        TextView textView = (TextView) v.findViewById(R.id.tvMessage);
        textView.setText(strMessage);
        textView = (TextView) v.findViewById(R.id.tvTitle);
        // LinearLayout llAutoHideDialogTilte = (LinearLayout) v
        // .findViewById(R.id.llAutoHideDialogTilte);
        if (!TextUtils.isEmpty(strTitle)) {
            textView.setText(strTitle);
        } else {
            textView.setVisibility(View.GONE);
        }

        if (wrToast.get() != null) {
            wrToast.get().setView(v);
            wrToast.get().setGravity(Gravity.CENTER, 0, 0);
            wrToast.get().setDuration(duration);
        }
    }

    public static Dialog createAlertDialog(Context context, String strTitle,
                                           String strMessage, String strPositiveBtnText,
                                           OnClickListener positiveButtonClickListener,
                                           String strNegativeBtnText,
                                           OnClickListener negativeButtonClickListener,
                                           boolean cancelable, OnCancelListener cancelListener) {
        AlertDialog ad = new AlertDialog(context);
        ad.setTitle(strTitle);
        ad.setMessage(strMessage);
        ad.setPositiveButton(strPositiveBtnText, positiveButtonClickListener);
        ad.setNegativeButton(strNegativeBtnText, negativeButtonClickListener);
        ad.setCancelable(cancelable);
        ad.setOnCancelListener(cancelListener);
        return ad;
    }

    public static Dialog createAlertDialog(Context context, String strTitle,
                                           String strMessage, CharSequence[] items,
                                           final OnClickListener listener, boolean cancelable,
                                           OnCancelListener cancelListener) {
        AlertDialog ad = new AlertDialog(context);
        ad.setTitle(strTitle);
        ad.setMessage(strMessage);
        ad.setItems(items, listener);
        ad.setCancelable(cancelable);
        ad.setOnCancelListener(cancelListener);
        return ad;
    }


    private static String getString(Resources res, int nStringResId) {
        try {
            return res.getString(nStringResId);
        } catch (NotFoundException ex) {

        }
        return null;
    }

}

class AlertDialog extends Dialog {

    private TextView m_tvMessage;
    private String m_strMessage;
    private String m_strTitle;
    private OnClickListener m_positiveButtonClickListener;
    private OnClickListener m_negativeButtonClickListener;
    private String m_strPositiveButtonText;
    private String m_strNegativeButtonText;
    private boolean m_bCreated = false;
    private View m_vContentView;
    private CharSequence[] m_arrItems;
    private ListView m_lvItems;
    private OnClickListener m_listenerItems;

    public AlertDialog(Context context) {
        super(context, R.style.dialog);
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        m_vContentView = inflater.inflate(R.layout.dyuiapi_alert_dialog, null);
        setContentView(m_vContentView);
        m_tvMessage = (TextView) m_vContentView.findViewById(R.id.tvMessage);
        Button btnTmp = (Button) m_vContentView.findViewById(R.id.btnNegative);
        doSetNegativeButton(m_strNegativeButtonText);
        btnTmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick(DialogInterface.BUTTON_NEGATIVE);
            }
        });
        btnTmp = (Button) m_vContentView.findViewById(R.id.btnPositive);
        doSetPositiveButton(m_strPositiveButtonText);
        btnTmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick(DialogInterface.BUTTON_POSITIVE);
            }
        });
        setTitle(m_strTitle);
        setMessage(m_strMessage);
        m_lvItems = (ListView) m_vContentView.findViewById(R.id.lvItems);
        m_lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (m_listenerItems != null) {
                    m_listenerItems.onClick(AlertDialog.this, position);
                }
                dismiss();
            }
        });
        if (null != m_arrItems && m_listenerItems != null) {
            setItems(m_arrItems, m_listenerItems);
        }
        LayoutParams lp = getWindow().getAttributes();
//        lp.gravity = Gravity.CENTER;
        CoreUtils.init(getContext());
        DisplayMetrics metrics = CoreUtils.getMetrics();
//        Log.e("metrics", "onCreate: "+metrics.heightPixels+"...."+CoreUtils.getStatusBarHeight(getContext()) );
        lp.width = metrics.widthPixels;
        lp.height = metrics.heightPixels;
//        lp.alpha = 0.5f; dialog中所有界面半透明（保存背景部分）
        this.onWindowAttributesChanged(lp);
        setButtonBackground();
        m_bCreated = true;
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                onDestory();
            }
        });

    }

    public View getContentView() {
        return m_vContentView;
    }

    /**
     * @param items
     * @param listener
     */
    public void setItems(CharSequence[] items, OnClickListener listener) {
        m_arrItems = items;
        m_listenerItems = listener;
        if (null != m_lvItems) {
            m_lvItems.setAdapter(new ArrayAdapter<CharSequence>(getContext(),
                    R.layout.alert_listview_dialog_item, R.id.tvDialogItem,
                    items));
            m_lvItems.setVisibility(View.VISIBLE);
            m_vContentView.findViewById(R.id.llButtons)
                    .setVisibility(View.GONE);
        }
    }

    public void setMessage(String strMessage) {
        m_strMessage = strMessage;
        if (null != m_tvMessage) {
            if (!TextUtils.isEmpty(strMessage)) {
                m_tvMessage.setText(strMessage);
                m_tvMessage.setVisibility(View.VISIBLE);
            } else {
                m_tvMessage.setVisibility(View.GONE);
            }
        }
    }

    public void setPositiveButton(String strText,
                                  OnClickListener clickListener) {
        m_positiveButtonClickListener = clickListener;
        m_strPositiveButtonText = strText;
        if (m_bCreated) {
            doSetPositiveButton(strText);
        }
    }

    private void doSetPositiveButton(String strText) {
        Button btnPositive = (Button) this.findViewById(R.id.btnPositive);
        if (TextUtils.isEmpty(strText)) {
            btnPositive.setVisibility(View.GONE);
        } else {
            btnPositive.setText(strText);
            btnPositive.setVisibility(View.VISIBLE);
        }
    }

    public void setNegativeButton(String strText,
                                  OnClickListener clickListener) {
        m_negativeButtonClickListener = clickListener;
        m_strNegativeButtonText = strText;
        if (m_bCreated) {
            doSetNegativeButton(strText);
        }
    }

    private void doSetNegativeButton(String strText) {
        Button btnNegative = (Button) this.findViewById(R.id.btnNegative);
        if (TextUtils.isEmpty(strText)) {
            btnNegative.setVisibility(View.GONE);
        } else {
            btnNegative.setText(strText);
            btnNegative.setVisibility(View.VISIBLE);
        }
    }

    private void setButtonBackground() {
        Button btnPositive = (Button) this.findViewById(R.id.btnPositive);
        Button btnNegative = (Button) this.findViewById(R.id.btnNegative);
        if (btnPositive.getVisibility() == View.GONE) {
            // btnNegative.setBackgroundResource(R.drawable.alert_dialog_normal_button);
            ImageView iv = (ImageView) this.findViewById(R.id.ivInterval);
            iv.setVisibility(View.GONE);
        } else if (btnNegative.getVisibility() == View.GONE) {
            // btnPositive.setBackgroundResource(R.drawable.alert_dialog_normal_button);
            ImageView iv = (ImageView) this.findViewById(R.id.ivInterval);
            iv.setVisibility(View.GONE);
        }
    }

    protected void onButtonClick(int whichButton) {
        if (whichButton == BUTTON_POSITIVE) {
            if (m_positiveButtonClickListener != null) {
                m_positiveButtonClickListener.onClick(this, whichButton);
            }
            this.cancel();
        } else if (whichButton == BUTTON_NEGATIVE) {
            if (m_negativeButtonClickListener != null) {
                m_negativeButtonClickListener.onClick(this, whichButton);
            }
            this.cancel();
        }
    }

    public void onDestory(){
        CoreUtils.onDestory();
    }

}


