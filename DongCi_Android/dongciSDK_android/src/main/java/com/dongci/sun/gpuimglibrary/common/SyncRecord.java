package com.dongci.sun.gpuimglibrary.common;

import android.util.Log;


/**
 * 最新同步逻辑
 */
public class SyncRecord {

    /**
     * 0:未播放
     * 1:等待中
     * 2:正在播放
     */
    private static int playFlag = 1;

    private static int audioFlag = 0;

    private static int vidoFlag = 0;

    private static Object audio = new Object();
    private static Object video = new Object();
    private static Object player = new Object();

    public static boolean isAudioOk;

    public static void waitAudio(){
        if(playFlag ==2 && vidoFlag==2){//正在播放
            return;
        }else if(playFlag ==1 && vidoFlag==1){//已经准备好了
            synchronized (video){
                video.notifyAll();
            }
            synchronized (player){
                player.notifyAll();
            }
            resetFlagPlaying();
        }else{//没有准备好
            audioFlag = 1;
            synchronized (audio){
                try {
                    audio.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static void waitVideo(){
        if(playFlag ==2 && audioFlag==2){//正在播放
            return;
        }else if(playFlag ==1 && audioFlag==1){//已经准备好了
            synchronized (audio){
                audio.notifyAll();
                Log.i("sun","syncRecord-audio-notifyAll");
            }
            synchronized (player){
                player.notifyAll();
            }
            resetFlagPlaying();
        }else{//没有准备好
            vidoFlag = 1;
            synchronized (video){
                try {
                    video.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void waitPlayer(){
        if(audioFlag ==2 && vidoFlag==2){//正在播放
            return;
        }else if(audioFlag ==1 && vidoFlag==1){//已经准备好了
            video.notifyAll();
            audio.notifyAll();
            resetFlagPlaying();
        }else{//没有准备好
            playFlag = 1;
            try {
                player.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void resetFlagPlaying(){
        audioFlag = 2;
        vidoFlag = 2;
        playFlag = 2;
    }

    /**
     * 录制结束
     */
    public static void resetFlag(){
        audioFlag = 0;
        vidoFlag = 0;
        playFlag = 1;
        isAudioOk = false;
    }
}
