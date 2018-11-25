package com.wmlive.hhvideo.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.wmlive.hhvideo.utils.MyClickListener;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 6/8/2018 - 4:32 PM
 * 类描述：
 */
public class AvatarDialog extends Dialog {
    public AvatarDialog(@NonNull Activity context, boolean isMale, String imageUrl) {
        super(context, R.style.BaseDialogTheme);
        setContentView(R.layout.dialog_avatar);
        setCancelable(true);
        setOwnerActivity(context);
        setCanceledOnTouchOutside(true);
        ImageView ivAvatar = findViewById(R.id.ivAvatar);
        GlideLoader.loadFitCenterImage(imageUrl, ivAvatar, isMale ? R.drawable.ic_default_male : R.drawable.ic_default_female);
        ivAvatar.setOnClickListener(new MyClickListener() {
            @Override
            protected void onMyClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void show() {
        super.show();
        if (getWindow() != null) {
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            getWindow().getDecorView().setPadding(0, 0, 0, 0);
            getWindow().setAttributes(layoutParams);
        }
    }
}
