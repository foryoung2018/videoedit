package com.wmlive.hhvideo.heihei.mainhome.viewholder;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;

import java.io.File;

/**
 * Created by lsq on 9/14/2017.
 */

public class UploadProgressHolder {
    private RelativeLayout rlUploadProgressPanel;
    private ProgressBar pbProgress;
    private TextView tvProgress;
    private ImageView ivCover;
    private ImageView ivUploadHint;
    private int currentProgress = -1;

    public UploadProgressHolder(RelativeLayout rlUploadProgressPanel, ProgressBar pbProgress,
                                TextView tvProgress, ImageView ivCover, ImageView ivUploadHint) {
        this.rlUploadProgressPanel = rlUploadProgressPanel;
        this.pbProgress = pbProgress;
        this.tvProgress = tvProgress;
        this.ivCover = ivCover;
        this.ivUploadHint = ivUploadHint;
    }

    /**
     * 出错和发布完成的时候使用
     *
     * @param visible
     */
    public void setVisible(boolean visible) {
        rlUploadProgressPanel.setVisibility(visible ? View.VISIBLE : View.GONE);
        ivUploadHint.setVisibility(visible ? View.VISIBLE : View.GONE);
        if (!visible) {
            currentProgress = -1;
            ivCover.setImageDrawable(null);
        }
    }

    public void setShow(boolean show) {
        rlUploadProgressPanel.setVisibility(show && currentProgress > 0 ? View.VISIBLE : View.GONE);
        ivUploadHint.setVisibility(show && currentProgress > 0 ? View.VISIBLE : View.GONE);
    }

    public void loadCover(String url) {
        KLog.i("====开始去加载封面url:" + url);
        if (!TextUtils.isEmpty(url) && new File(url).exists()) {
            GlideLoader.loadImage(url, ivCover);
        }
    }

    public void setProgress(int progress) {
        if (progress <= currentProgress) {
            return;
        }
        currentProgress = progress;
        if (progress < 0) {
            progress = 0;
        }
        if (progress > 100) {
            progress = 100;
        }
        pbProgress.setProgress(progress);
        tvProgress.setText(progress + "%");
    }
}
