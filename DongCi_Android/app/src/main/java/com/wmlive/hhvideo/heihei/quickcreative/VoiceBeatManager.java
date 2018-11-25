package com.wmlive.hhvideo.heihei.quickcreative;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dc.platform.voicebeating.DCVoiceBeatingTool;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.utils.KLog;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class VoiceBeatManager {

    HashMap<Integer,Future<String>> task;

    ExecutorService cachedThreadPool;

    VideoBeatListener videoBeatListener;

    public VoiceBeatManager(){
        init();
    }

    public void init(){
        task = new HashMap <Integer,Future<String>>();
        cachedThreadPool = Executors.newCachedThreadPool();
    }

    /**
     * 是否有正在进行的任务
     * @return
     */
    public boolean hasProcessing(){
        KLog.i("hasProcessing-->"+task.size());
        if(task.size()>0){
            //正在进行
            return true;
        }else{
            return false;
        }
    }

    /**
     * 设置监听是否完成处理视频
     * @param videoListener
     */
    public void setVideoListener(VideoBeatListener videoListener){
        this.videoBeatListener = videoListener;
    }


    public void start(int indexId,ShortVideoEntity shortVideoEntity){
        if(shortVideoEntity!=null){
            Future<String> fature = task.get(indexId);
            if(fature==null){//不存在正在进行的任务
                addPool(indexId,shortVideoEntity);
            }else{//存在进行的任务,暂停现在的，进行新的
                cancelAThread(indexId);
                //开启新的任务
                addPool(indexId,shortVideoEntity);
            }
        }else {
            KLog.e("ShortVideoEntity is Null");
        }
    }

    private void addPool(int index,ShortVideoEntity shortVideoEntity){
        Callable<String> callable = new Callable<String>() {

            @Override
            public String call() throws Exception {

                DCVoiceBeatingTool voiceBeatingTool = new DCVoiceBeatingTool();
                shortVideoEntity.extendInfo.analysisInfo =  voiceBeatingTool.getVoiceAnalysisInfo(index,shortVideoEntity.editingAudioPath);
                //处理结束
                task.remove(index);
                KLog.i("ExtractVideoFrame","finish--count--3>"+task.size());
                hanlder.sendEmptyMessage(1);
                //分离完成，需要将路径保存
                return "true";
            }
        };
        Future<String> f = cachedThreadPool.submit(callable);
        task.put(index,f);
        KLog.i("img==-addPool>"+task.size());
    }

    /**
     *
     * @param index
     */
    private void cancelAThread(int index) {
        Future<String> fature = task.get(index);
        System.out.println(fature.isCancelled());
        System.out.println(fature.isDone());
        fature.cancel(true);
        task.remove(index);
        KLog.i("img==-cancelAThread>"+task.size());
    }

    Handler hanlder = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){//完成
                Log.i("ExtractVideoFrame","VoiceBeatManager-count-->"+task.size()+videoBeatListener);
                if(task.size()==0){//全部完成
                    if(videoBeatListener!=null)
                        videoBeatListener.onFinish(1);
                }else {//正在进行

                }
            }else if(msg.what == 0){//正在进行

            }
        }
    };

    /**
     * 确定是否 有踹轨视频，需要进行所有分析
     * @param shortVideoList
     */
    public void checkAllData(List<ShortVideoEntity> shortVideoList) {
            for(int i=0;i<shortVideoList.size();i++){
                ShortVideoEntity shortVideoEntity=shortVideoList.get(i);
                if(shortVideoEntity.hasCombineVideo() && shortVideoEntity.extendInfo.analysisInfo==null){//踹轨进来的，而且没有处理过
                    start(i+1,shortVideoEntity);
                }
            }
    }

    public interface VideoBeatListener{
        public void onProgress(int index,int progress);
        public void onFinish(int code);
    }

}
