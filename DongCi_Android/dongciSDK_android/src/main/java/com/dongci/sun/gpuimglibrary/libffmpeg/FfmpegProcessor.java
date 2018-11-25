// AUTOGENERATED FILE - DO NOT MODIFY!
// This file generated by Djinni from ffmpeg.djinni

package com.dongci.sun.gpuimglibrary.libffmpeg;

import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class FfmpegProcessor {

    public abstract boolean getImageFromVideo(String video, float time, String output);

    public abstract boolean generateVideoWithImage(String image, int duration, String output);

    public abstract boolean addWatermarks(String video, ArrayList<FfmpegWatermark> watermarks, String output);

    public abstract boolean generateLoopAudio(String audio, int loopCount, String output);

    public abstract boolean generateLoopAudioWithDuration(String audio, int loopCount, int durationMs, String output);

    public abstract boolean extractAudioFromVideo(String video, int vol, String output);

    public abstract boolean mixAudio(String audio0, String audio1, String output);

    public abstract boolean generateFadeOutAudio(String audio, float fadeOutTime, String output);

    public abstract boolean muxVideoAndAudio(String video, String audio, String output);

    public abstract boolean moveMoovFlgToBeginning(String video, String output);

    public abstract boolean getTs(String video, String output);

    public abstract boolean concatVideos(ArrayList<String> videos, String output);

    public abstract boolean concatVideosWithDirectory(String directory, ArrayList<String> videos, String output);

    public abstract boolean generateBlurVideo(String video, int blur, String output);

    public abstract boolean trimVideo(String video, float fromTime, float duration, String output);

    public abstract boolean makeAudioSilent(String video, String output);

    public abstract boolean trimVideoWithWatermark(String video, float fromTime, float duration, FfmpegWatermark watermark, String output);

    public abstract boolean timeScale(String video, float timeScale, String output);

    public abstract boolean videoCopy(String video, String output);

    public abstract boolean execute(String cmd);

    public abstract boolean cropVideo(String video, int x, int y, int width, int height, String output);

    public abstract boolean transcodeVideo(String video, int width, int height, int fps,int bitrate, String output);

    public abstract boolean transcodeAudio(String video, int samplerate, int channels, int bitrate, String output);

    public abstract boolean rotateVideo(String video,String output);

    public abstract void setTranscodeListener(TranscodeListener listener);


    public static native FfmpegProcessor createFfmpegProcessor();

//    public static FfmpegProcessor createFfmpegProcessor()
//    {
//        return CppProxy.createFfmpegProcessor();
//    }

    static final class CppProxy extends FfmpegProcessor
    {
        private final long nativeRef;
        private final AtomicBoolean destroyed = new AtomicBoolean(false);

        private CppProxy(long nativeRef)
        {
            if (nativeRef == 0) throw new RuntimeException("nativeRef is zero");
            this.nativeRef = nativeRef;
        }


        private native void nativeDestroy(long nativeRef);

        public void destroy()
        {
            boolean destroyed = this.destroyed.getAndSet(true);
            if (!destroyed) nativeDestroy(this.nativeRef);
        }

        protected void finalize() throws java.lang.Throwable
        {
            destroy();
            super.finalize();
        }

        @Override
        public boolean execute(String cmd)
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_execute(this.nativeRef, cmd);
        }
        private native boolean native_execute(long _nativeRef, String cmd);


        @Override
        public boolean getImageFromVideo(String video, float time, String output)
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_getImageFromVideo(this.nativeRef, video, time, output);
        }
        private native boolean native_getImageFromVideo(long _nativeRef, String video, float time, String output);

        @Override
        public boolean generateVideoWithImage(String image, int duration, String output)
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_generateVideoWithImage(this.nativeRef, image, duration, output);
        }
        private native boolean native_generateVideoWithImage(long _nativeRef, String image, int duration, String output);

        @Override
        public boolean addWatermarks(String video, ArrayList<FfmpegWatermark> watermarks, String output)
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_addWatermarks(this.nativeRef, video, watermarks, output);
        }
        private native boolean native_addWatermarks(long _nativeRef, String video, ArrayList<FfmpegWatermark> watermarks, String output);

        @Override
        public boolean generateLoopAudio(String audio, int loopCount, String output)
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_generateLoopAudio(this.nativeRef, audio, loopCount, output);
        }
        private native boolean native_generateLoopAudio(long _nativeRef, String audio, int loopCount, String output);

        @Override
        public boolean generateLoopAudioWithDuration(String audio, int loopCount, int durationMs, String output)
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_generateLoopAudioWithDuration(this.nativeRef, audio, loopCount, durationMs, output);
        }
        private native boolean native_generateLoopAudioWithDuration(long _nativeRef, String audio, int loopCount, int durationMs, String output);

        @Override
        public boolean extractAudioFromVideo(String video, int vol, String output)
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_extractAudioFromVideo(this.nativeRef, video, vol, output);
        }
        private native boolean native_extractAudioFromVideo(long _nativeRef, String video, int vol, String output);

        @Override
        public boolean mixAudio(String audio0, String audio1, String output)
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_mixAudio(this.nativeRef, audio0, audio1, output);
        }
        private native boolean native_mixAudio(long _nativeRef, String audio0, String audio1, String output);

        @Override
        public boolean generateFadeOutAudio(String audio, float fadeOutTime, String output)
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_generateFadeOutAudio(this.nativeRef, audio, fadeOutTime, output);
        }
        private native boolean native_generateFadeOutAudio(long _nativeRef, String audio, float fadeOutTime, String output);

        @Override
        public boolean muxVideoAndAudio(String video, String audio, String output)
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_muxVideoAndAudio(this.nativeRef, video, audio, output);
        }
        private native boolean native_muxVideoAndAudio(long _nativeRef, String video, String audio, String output);

        @Override
        public boolean moveMoovFlgToBeginning(String video, String output)
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_moveMoovFlgToBeginning(this.nativeRef, video, output);
        }
        private native boolean native_moveMoovFlgToBeginning(long _nativeRef, String video, String output);

        @Override
        public boolean getTs(String video, String output)
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_getTs(this.nativeRef, video, output);
        }
        private native boolean native_getTs(long _nativeRef, String video, String output);

        @Override
        public boolean concatVideos(ArrayList<String> videos, String output)
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_concatVideos(this.nativeRef, videos, output);
        }
        private native boolean native_concatVideos(long _nativeRef, ArrayList<String> videos, String output);

        @Override
        public boolean concatVideosWithDirectory(String directory, ArrayList<String> videos, String output)
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_concatVideosWithDirectory(this.nativeRef, directory, videos, output);
        }
        private native boolean native_concatVideosWithDirectory(long _nativeRef, String directory, ArrayList<String> videos, String output);

        @Override
        public boolean generateBlurVideo(String video, int blur, String output)
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_generateBlurVideo(this.nativeRef, video, blur, output);
        }
        private native boolean native_generateBlurVideo(long _nativeRef, String video, int blur, String output);

        @Override
        public boolean trimVideo(String video, float fromTime, float duration, String output)
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            Log.e("SLClipVideo","this.nativeRef:" + this.nativeRef + " video：" + video + " fromTime:" + fromTime + " duration:" + duration + " output:" + output);
            return native_trimVideo(this.nativeRef, video, fromTime, duration, output);
        }
        private native boolean native_trimVideo(long _nativeRef, String video, float fromTime, float duration, String output);

        @Override
        public boolean makeAudioSilent(String video, String output)
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_makeAudioSilent(this.nativeRef, video, output);
        }
        private native boolean native_makeAudioSilent(long _nativeRef, String video, String output);

        @Override
        public boolean trimVideoWithWatermark(String video, float fromTime, float duration, FfmpegWatermark watermark, String output)
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_trimVideoWithWatermark(this.nativeRef, video, fromTime, duration, watermark, output);
        }
        private native boolean native_trimVideoWithWatermark(long _nativeRef, String video, float fromTime, float duration, FfmpegWatermark watermark, String output);

        @Override
        public boolean timeScale(String video, float timeScale, String output)
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_timeScale(this.nativeRef, video, timeScale, output);
        }
        private native boolean native_timeScale(long _nativeRef, String video, float timeScale, String output);

        @Override
        public boolean videoCopy(String video, String output)
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_videoCopy(this.nativeRef, video, output);
        }
        private native boolean native_videoCopy(long _nativeRef, String video, String output);


        @Override
        public boolean cropVideo(String video, int x, int y, int width, int height, String output)
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_cropVideo(this.nativeRef, video, x, y, width, height, output);
        }
        private native boolean native_cropVideo(long _nativeRef, String video, int x, int y, int width, int height, String output);

        @Override
        public boolean transcodeVideo(String video, int width, int height, int fps, int bitrate, String output) {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_transcodeVideo(this.nativeRef,video,width,height,fps,bitrate,output);
        }
        private native boolean native_transcodeVideo(long _nativeRef, String video, int width, int height, int fps, int bitrate, String output);

        @Override
        public boolean transcodeAudio(String video, int samplerate, int channels, int bitrate, String output) {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_transcodeAudio(this.nativeRef,video,samplerate,channels,bitrate,output);
        }
        private native boolean native_transcodeAudio(long _nativeRef, String video, int samplerate, int channels, int bitrate, String output);

        @Override
        public boolean rotateVideo(String video, String output) {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_rotateVideo(this.nativeRef,video,output);
        }
        private native boolean native_rotateVideo(long _nativeRef, String video, String output);


        @Override
        public void setTranscodeListener(TranscodeListener listener)
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            native_setTranscodeListener(this.nativeRef, listener);
        }
        private native void native_setTranscodeListener(long _nativeRef, TranscodeListener listener);


    }
}
