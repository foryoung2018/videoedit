package com.wmlive.hhvideo.heihei.quickcreative;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dongci.sun.gpuimglibrary.common.SLVideoProcessor;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.utils.FileUtil;
import com.wmlive.hhvideo.utils.KLog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 素材管理
 */
public class MvMaterialManager {

    HashMap<String,Future<String>> task;

    ExecutorService cachedThreadPool;

    Video2ImgListener video2ImgListener;


    public MvMaterialManager(){
        init();
    }

    public void init(){
        task = new HashMap <String,Future<String>>();
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
    public void setVideoListener(Video2ImgListener videoListener){
        this.video2ImgListener = videoListener;
    }

    public void start(ShortVideoEntity shortVideoEntity){
        if(shortVideoEntity!=null){
            String path = shortVideoEntity.baseDir;
            Future<String> fature = task.get(path);
            if(fature==null){//不存在正在进行的任务
                addPool(shortVideoEntity);
            }else{//存在进行的任务,暂停现在的，进行新的
                cancelAThread(path);
                deleteImg(path);
                //开启新的任务
                addPool(shortVideoEntity);
            }
        }else {
            KLog.e("ShortVideoEntity is Null");
        }
    }


    private void addPool(ShortVideoEntity shortVideoEntity){
        Callable<String> callable = new Callable<String>() {

            @Override
            public String call() throws Exception {
                String videoPath = shortVideoEntity.editingVideoPath==null?shortVideoEntity.combineVideoAudio:shortVideoEntity.editingVideoPath;
                if(videoPath==null){
                    task.remove(shortVideoEntity.baseDir);
                    return "true";
                }
                String basePath = getImgFile(shortVideoEntity.baseDir);
                KLog.i("ExtractVideoFrame","count--1>"+shortVideoEntity.extendInfo);
                SLVideoProcessor.getInstance().extractImagesFromVideo(videoPath,12,basePath);
                File file = new File(basePath);
                String[] images = file.list();
                List<String> imgs = new ArrayList <String>(images.length);
                for(int i=0;i<images.length;i++){
                    imgs.add(basePath+File.separator+images[i]);
                }
                shortVideoEntity.setImgs(imgs);
                //处理结束
                task.remove(shortVideoEntity.baseDir);
                KLog.i("ExtractVideoFrame","count--3>"+task.size());
                hanlder.sendEmptyMessage(1);
                //分离完成，需要将路径保存
                return "true";
            }
        };
        Future<String> f = cachedThreadPool.submit(callable);
        task.put(shortVideoEntity.baseDir,f);
        KLog.i("img==-addPool>"+task.size());
    }

    /**
     *
     * @param path
     */
    private void cancelAThread(String path) {
        Future<String> fature = task.get(path);
        System.out.println(fature.isCancelled());
        System.out.println(fature.isDone());
        fature.cancel(true);
        task.remove(path);
        KLog.i("img==-cancelAThread>"+task.size());
    }


    /**
     * 移除正在继续宁队列
     * 删除图片
     */
    private void deleteImg(String basePath){
        FileUtil.deleteFile(getImgFile(basePath));
    }

    private String getImgFile(String basePath){
        String path = basePath+File.separator+"img";
        File f = new File(path);
        if(!f.exists()){
            f.mkdir();
        }
        return path;
    }

    Handler hanlder = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){//完成
                Log.i("ExtractVideoFrame","count-->"+task.size()+video2ImgListener);
                if(task.size()==0){//全部完成
                    if(video2ImgListener!=null)
                        video2ImgListener.onFinish(1);
                }else {//正在进行

                }
            }else if(msg.what == 0){//正在进行

            }
        }
    };



    /**
     * 设置是否完成的监听
     */
    public interface Video2ImgListener{
        public void onProgress(int index,int progress);
        public void onFinish(int code);
    }

}
