package com.wmlive.hhvideo.widget.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.TextView;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 7/14/2017.
 * 下载框
 */

public class DownloadProgressDialog extends ProgressDialog {
    private TextView tvProgress;

    public DownloadProgressDialog(Context context) {
        super(context, R.style.BaseDialogThemeNoBackground);
    }

    public DownloadProgressDialog showDownload(OnDismissListener listener) {
        show();
        setContentView(R.layout.dialog_download);
        tvProgress = (TextView) findViewById(R.id.tvProgress);
        setOnDismissListener(listener);
        return this;
    }

    public void updateProgress(int progress) {
        tvProgress.setText(progress + "%");
    }

    public void dissmissDownload() {
        dismiss();
    }
}
