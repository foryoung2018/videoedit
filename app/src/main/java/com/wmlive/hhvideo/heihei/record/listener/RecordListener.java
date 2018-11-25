package com.wmlive.hhvideo.heihei.record.listener;

/**
 * 录制回调
 */
public interface RecordListener {

     void onProgress(int progress);

     void onRecordEnd(final int var1, String VideoPath,String audioPath);
}
