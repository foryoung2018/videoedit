package com.dongci.sun.gpuimglibrary.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.dongci.sun.gpuimglibrary.gles.filter.FilterUtils;


public class DialogUtils {

    static String[] data = null;

    private static void initData() {
        data = new String[FilterUtils.FilterType.values().length];
        int i = 0;
        for (FilterUtils.FilterType type : FilterUtils.FilterType.values()) {
            data[i] = i++ + "." + String.valueOf(type);
        }
    }

    public static void showFilterDialog(Context context, final SwitchFilterListener listener) {
        initData();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose a filter");
        builder.setItems(data,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int item) {
                        listener.onFilterSwitch(data[item].split("\\.")[1]);
                    }
                });

        builder.create().show();
    }

    public interface SwitchFilterListener {
        void onFilterSwitch(String item);
    }
}
