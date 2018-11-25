package com.wmlive.hhvideo.widget.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wmlive.hhvideo.common.base.BaseCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wmlive.hhvideo.R;

/**
 * 开启权限的弹窗
 */
public class PermissionDialog extends Dialog {

    @BindView(R.id.iv_dismiss)
    ImageView ivDismiss;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvDesc)
    TextView tvDesc;
    @BindView(R.id.tvOpen)
    TextView tvOpen;
    private int type;//10是麦克风权限,20是摄像头权限

    public PermissionDialog(BaseCompatActivity context, int type) {
        super(context, R.style.BaseDialogTheme);
        setContentView(R.layout.dialog_permission);
        ButterKnife.bind(this);
        setCancelable(false);
        this.type = type;
        setOwnerActivity(context);
        setCanceledOnTouchOutside(false);
        tvOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent.setData(uri);
                context.startActivity(intent);
                dismiss();
            }
        });
        ivDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        switch (type) {
            case 10:
                tvTitle.setText("木有开启麦克风权限呀");
                tvDesc.setText("请前往系统设置打开权限，允许\n动次访问你的麦克风");
                break;
            case 20:
                tvTitle.setText("木有开启照相机权限呀");
                tvDesc.setText("请前往系统设置打开权限，允许\n动次访问你的相机");
                break;
            default:
                dismiss();
                break;
        }

    }


}
