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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 素材管理
 * 1.素材处理，分解成图片
 * 2.素材下载管理
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
        if(!hasProcessing()){//已经处理完成
            video2ImgListener.onFinish(0);
        }
    }

    public void start(ShortVideoEntity shortVideoEntity){
        if(shortVideoEntity!=null){
            String path = shortVideoEntity.baseDir;
            String videoPath = shortVideoEntity.editingVideoPath==null?shortVideoEntity.combineVideoAudio:shortVideoEntity.editingVideoPath;
            if(shortVideoEntity.baseDir==null){
                shortVideoEntity.baseDir = getVideoPath(videoPath);
            }

            Future<String> fature = task.get(path);
            if(fature==null){//不存在正在进行的任务
                deleteImg(shortVideoEntity);//删除之前进行的任务
                addPool(shortVideoEntity);
            }else{//存在进行的任务,暂停现在的，进行新的
                cancelAThread(path);
                deleteImg(shortVideoEntity);
                //开启新的任务
                addPool(shortVideoEntity);
            }
        }else {
            KLog.e("ShortVideoEntity is Null");
        }
    }

    /**
     * 确定是否 有踹轨视频，需要进行所有分析
     * @param shortVideoList
     */
    public void checkAllData(List<ShortVideoEntity> shortVideoList) {
        for(int i=0;i<shortVideoList.size();i++){
            ShortVideoEntity shortVideoEntity=shortVideoList.get(i);
                if(shortVideoEntity.hasVideoNoImg()){//没有图片
                    start(shortVideoEntity);
                }
        }
    }


    private void addPool(ShortVideoEntity shortVideoEntity){
        Callable<String> callable = new Callable<String>() {

            @Override
            public String call() throws Exception {
                //分离完成，需要将路径保存
                return doImg(shortVideoEntity);
            }
        };
        Future<String> f = cachedThreadPool.submit(callable);
        task.put(shortVideoEntity.baseDir,f);
        KLog.i("img==-addPool>"+task.size());
    }

    private synchronized String doImg(ShortVideoEntity shortVideoEntity){
        String videoPath = shortVideoEntity.editingVideoPath==null?shortVideoEntity.combineVideoAudio:shortVideoEntity.editingVideoPath;
        if(shortVideoEntity.baseDir==null){
            shortVideoEntity.baseDir = getVideoPath(videoPath);
        }
        KLog.i("ExtractVideoFrame","count--0>"+videoPath+"||"+shortVideoEntity.baseDir);

        if(videoPath==null){
            task.remove(shortVideoEntity.baseDir);
            return "true";
        }
        KLog.i("ExtractVideoFrame","count--01>"+shortVideoEntity.baseDir);
        shortVideoEntity.extendInfo.hasImg = false;
        String basePath = getImgFile(shortVideoEntity.baseDir);
        KLog.i("ExtractVideoFrame","count--1>"+new File(basePath).listFiles().length);
        SLVideoProcessor.getInstance().extractImagesFromVideo(videoPath,24, 360, 480, basePath);
        File file = new File(basePath);
        String[] images = file.list();
        List<String> imgs = new ArrayList <String>(images.length);
        for(int i=0;i<images.length;i++){
            imgs.add(basePath+File.separator+images[i]);
        }
        Collections.sort(imgs);
        for(String s:imgs){
            KLog.i("ExtractVideoFrame","count--3>"+s);
        }
        shortVideoEntity.setImgs(imgs);

        shortVideoEntity.extendInfo.hasImg = true;
        //处理结束
        task.remove(shortVideoEntity.baseDir);
        KLog.i("ExtractVideoFrame","count--3>"+task.size());
        hanlder.sendEmptyMessage(1);
        return "true";
    }

    private String getVideoPath(String videoPath){
        if(videoPath==null)
            return null;
        String path = videoPath.substring(0,videoPath.length()-4);
        File file = new File(path);
        file.mkdirs();
        return path;
//        String[] videos = videoPath.split("/");
//        StringBuilder sb = new StringBuilder();
//        for(int i =0;i<videos.length-1;i++){
//            sb.append(videos[i]).append("/");
//        }
//        return sb.substring(0,sb.length()-1);
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
     * 删除数据结构中的数据
     */
    private void deleteImg(ShortVideoEntity shortVideoEntity){
        String path = getImgFile(shortVideoEntity.baseDir);
        File file = new File(path);
//        KLog.i("delete-img--before>"+file.listFiles().length);
        for(File f:file.listFiles()){
            boolean r = f.delete();
//            KLog.i("delete-img--ing>"+file.listFiles().length+r);
        }
//        KLog.i("delete-img-->"+file.listFiles().length);
        shortVideoEntity.extendInfo.videoImgs.clear();
    }

    private String getImgFile(String basePath){
        String path = basePath+File.separator+"img";
        File f = new File(path);
        if(!f.exists()){
            f.mkdirs();
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

    public final class FileString implements Comparable<FileString> {
        private final String fileName;
        private final int prefix_num;
        Pattern number = Pattern.compile("(\\d+)\\..*");

        public FileString(String fileName) {
            this.fileName = fileName;
            Matcher matcher = number.matcher(fileName);
            if (matcher.find()) {
                prefix_num = Integer.parseInt(matcher.group(1));
            } else {
                prefix_num = 0;
            }
        }

        @Override
        public int compareTo(FileString o) {
            return o.prefix_num > prefix_num ? -1 : o.prefix_num == prefix_num ? 0 : 1;
        }

        @Override
        public String toString() {
            return fileName;
        }
    }

}
