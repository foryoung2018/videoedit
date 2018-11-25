package com.example.loopback;

import android.content.Context;
import android.content.Intent;

/**
 * Created by yangjiangang on 2018/9/20.
 */

public class DCLatencytestTool {
    /**
     * 裁剪时间 ，ms
     */
    public static long cutTime = 0;

    public static void startLatency(Context context) {
        Intent intent = new Intent(context, LoopBackTestAct.class);
        context.startActivity(intent);
    }
}
