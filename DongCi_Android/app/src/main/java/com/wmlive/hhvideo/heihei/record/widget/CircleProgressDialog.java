package com.wmlive.hhvideo.heihei.record.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wmlive.hhvideo.utils.KLog;

import java.math.BigDecimal;

import cn.wmlive.hhvideo.R;

/**
 * @author scott
 */
public class CircleProgressDialog extends Dialog {

    private TextView m_tvMessage;
    private ProgressBar m_pwProgress;
    private TextView m_tvProgress;
    private String m_strMessage;
    private boolean m_bIndeterminate;
    private int m_nMax = 100, m_nProgress = 0;
    private onCancelClickListener cancelListener = null;

    public CircleProgressDialog(Context context) {
        super(context, R.style.dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.circle_progress_dialog, null);
        m_tvMessage = (TextView) view.findViewById(R.id.tv_export_progress);
        m_pwProgress = (ProgressBar) view.findViewById(R.id.progress_circle);
        m_tvProgress = (TextView) view.findViewById(R.id.tv_progress);
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
            BigDecimal b = new BigDecimal((double) nProgress / m_nMax * 100);
            double progress = b.setScale(1, BigDecimal.ROUND_HALF_UP)
                    .doubleValue();
            m_tvProgress.setText(progress + "%");
        }
    }

    public void setProgressAuto(int start,int end){
       new Thread(new Runnable() {
           @Override
           public void run() {
               int startCout = start;
               while(startCout<end){
                   Message msg = new Message();
                   msg.what = 1;
                   msg.arg1 = startCout;
                   msg.arg2 = end;
                    handler.sendMessage(msg);
                    startCout++;
                   try {
                       Thread.sleep(100);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }

               }
           }}).start();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String content = m_tvProgress.getText().toString();
            float current = Float.parseFloat(content.substring(0,content.length()-1));
            if(msg.arg2>current){
                setProgress(msg.arg1);
            }
        }
    };
}
