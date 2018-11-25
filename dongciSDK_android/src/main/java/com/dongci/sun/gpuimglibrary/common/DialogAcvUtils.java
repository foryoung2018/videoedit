package com.dongci.sun.gpuimglibrary.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import static com.dongci.sun.gpuimglibrary.api.DCRecorderConfig.FILTER_LIST;

public class DialogAcvUtils {

    static String[] data = null;

    private static void initData(Context context) {
        data = new String[FILTER_LIST.size()];
        for (int i = 0; i < FILTER_LIST.size(); i++) {
            data[i] = String.valueOf(context.getText(FILTER_LIST.get(i).titleId));
        }
    }

    public static void showFilterDialog(Context context, final SwitchFilterListener listener) {
        initData(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose a filter");
        builder.setItems(data,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int item) {
                        listener.onFilterSwitch(String.valueOf(FILTER_LIST.get(item).filterId));
                    }
                });

        builder.create().show();
    }

    public interface SwitchFilterListener {
        void onFilterSwitch(String item);
    }
}
