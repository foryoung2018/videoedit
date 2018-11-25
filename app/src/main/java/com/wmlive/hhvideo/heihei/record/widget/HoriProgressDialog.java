package com.wmlive.hhvideo.heihei.record.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.math.BigDecimal;

import cn.wmlive.hhvideo.R;


/**
 * @author scott
 */
public class HoriProgressDialog extends Dialog {

    private TextView m_tvMessage;
    private ProgressBar m_pwProgress;
    private TextView m_tvProgress;
    private String m_strMessage;
    private boolean m_bIndeterminate;
    private int m_nMax = 100, m_nProgress = 0;
    private onCancelClickListener cancelListener = null;

    public HoriProgressDialog(Context context) {
        super(context, R.style.dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.horizontal_progress_dialog,
                null);
        m_tvMessage = (TextView) view.findViewById(R.id.tvMessage);
        m_pwProgress = (ProgressBar) view.findViewById(R.id.horiProgress);
        m_tvProgress = (TextView) view.findViewById(R.id.tvExportProgress);
        setMessage(m_strMessage);
        setContentView(view);
        setIndeterminate(m_bIndeterminate);
        if (!m_bIndeterminate) {
            setProgress(m_nProgress);
        }
        super.onCreate(savedInstanceState);
        LayoutParams lp = getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
        this.onWindowAttributesChanged(lp);
    }

    public interface onCancelClickListener {
        void onCancel();
    }

    public void setOnCancelClickListener(onCancelClickListener listener) {
        cancelListener = listener;
    }

    @Override
    public void onBackPressed() {
        if (cancelListener != null) {
            cancelListener.onCancel();
            return;
        }
        super.onBackPressed();
    }

    public void setMessage(String strMessage) {
        m_strMessage = strMessage;
        if (null != m_tvMessage) {
            m_tvMessage.setText(strMessage);
            m_tvMessage.setVisibility(TextUtils.isEmpty(strMessage) ? View.GONE
                    : View.VISIBLE);
        }
    }

    public void setIndeterminate(boolean indeterminate) {
        m_bIndeterminate = indeterminate;
        if (m_pwProgress != null) {
            m_pwProgress.setIndeterminate(indeterminate);
        }
    }

    public void setMax(int max) {
        m_nMax = max;
        setProgress(m_nProgress);
    }

    public int getMax() {
        return m_nMax;
    }

    public void setProgress(int nProgress) {
        nProgress = Math.min(m_nMax, nProgress);
        nProgress = Math.max(0, nProgress);
        m_nProgress = nProgress;
        if (m_pwProgress != null) {
            m_pwProgress.setMax(m_nMax);
            m_pwProgress.setProgress(m_nProgress);
            BigDecimal b = new BigDecimal((double) nProgress / m_nMax * 100);
            double progress = b.setScale(1, BigDecimal.ROUND_HALF_UP)
                    .doubleValue();
            m_tvProgress.setText(progress + "%");
        }
    }
}
