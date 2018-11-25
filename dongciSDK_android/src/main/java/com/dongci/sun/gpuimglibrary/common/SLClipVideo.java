package com.dongci.sun.gpuimglibrary.common;

import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import com.dongci.sun.gpuimglibrary.api.DCVideoManager;

import java.io.IOException;

/**
 * Created by yangjiangang on 2018/6/4.
 * 裁剪视频
 */

public class SLClipVideo {

    public boolean clipVideo(String url, long clipPoint, long clipDuration) {

        // url:/storage/emulated/0/DCIM/DongCi/play?id=135975&sign=a8b25cde54a853c3a214d2292eab4af7&mask=1.mp4 clipPoint:2 000 000 clipDuration:53 900 000

        Log.e("RecordActivitySdk1", "url:" + url + " clipPoint:" + clipPoint + " clipDuration:" + clipDuration);

        String outPath = (url.substring(0, url.lastIndexOf(".")) + "_output.mp4").replace(" ", "");//去掉其中的 空格

        return SLVideoProcessor.getInstance().trimVideo(url, clipPoint * 1f / 1000000.f, clipDuration * 1f / 1000000.f, outPath);

    }

    public static long duration = 0;

    public boolean clipVideoToEndRecord(String url, long clipPoint) {

        Log.e("recordActivitySdk", "duration:" + duration);

        long clipDuration = (duration * 1000 - clipPoint) / 1000;

        clipPoint = clipPoint / 1000;

        Log.e("recordActivitySdk", "duration:" + duration + " clipPoint:" + clipPoint + " clipDuration:" + clipDuration);

        // duration:11183310 clipPoint:39 064 clipDuration:11 144 246
        String outPath = (url.substring(0, url.lastIndexOf(".")) + "_output.mp4").replace(" ", "");//去掉其中的 空格
        return SLVideoProcessor.getInstance().trimVideo(url, clipPoint * 1f / 1000000.f, clipDuration * 1f / 1000000.f, outPath);

    }


}
