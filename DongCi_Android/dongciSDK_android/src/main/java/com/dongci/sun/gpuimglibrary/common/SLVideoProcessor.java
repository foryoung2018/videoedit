package com.dongci.sun.gpuimglibrary.common;

import android.support.annotation.FloatRange;
import android.util.Log;

import com.dongci.sun.gpuimglibrary.libffmpeg.FfmpegProcessor;
import com.dongci.sun.gpuimglibrary.libffmpeg.FfmpegWatermark;
import com.dongci.sun.gpuimglibrary.libffmpeg.TranscodeListener;
import com.dongci.sun.gpuimglibrary.player.DCMediaInfoExtractor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class SLVideoProcessor {

    static {
        System.loadLibrary("dcffmpeg-native");
    }

    private FfmpegProcessor mProcessor = FfmpegProcessor.createFfmpegProcessor();

    private final Object mProcessorSyncObject = new Object();

    private static class VideoProcessor {
        private final static SLVideoProcessor instance = new SLVideoProcessor();
    }

    public static SLVideoProcessor getInstance() {
        return VideoProcessor.instance;
    }

    public static class WaterMark {

        private ArrayList<FfmpegWatermark> mWatermarks = new ArrayList<FfmpegWatermark>();

        public void addWatermark(String image, int x, int y, float fromTime, float duration) {
            FfmpegWatermark watermark = new FfmpegWatermark(image, x, y, fromTime, duration);
            mWatermarks.add(watermark);
        }

    }

    public boolean transcodeVideo(String video, int width, int height, int fps, int bitrate, String output) {
        synchronized (mProcessorSyncObject) {
            return mProcessor.transcodeVideo(video, width, height, fps, bitrate, output);
        }
    }

    public boolean addWatermarksOnVideo(String video, WaterMark waterMark, String output) {
        synchronized (mProcessorSyncObject) {
            return mProcessor.addWatermarks(video, waterMark.mWatermarks, output);
        }
    }

    public boolean cropVideo(String video, int x, int y, int width, int height, String output) {
        synchronized (mProcessorSyncObject) {
            return mProcessor.cropVideo(video, x, y, width, height, output);
        }
    }

    public boolean trimVideo(String video, float fromTime, float duration, String output) {
        synchronized (mProcessorSyncObject) {
            return mProcessor.trimVideo(video, fromTime, duration, output);
        }
    }

    public boolean concatVideos(ArrayList<String> videos, String output) {
        synchronized (mProcessorSyncObject) {
            return mProcessor.concatVideos(videos, output);
        }
    }

    public boolean transcodeAudio(String video, int samplerate, int channels, int bitrate, String output) {
        synchronized (mProcessorSyncObject) {
            return mProcessor.transcodeAudio(video, samplerate, channels, bitrate, output);
        }
    }

    public boolean rotateVideo(String video, String output) {
        synchronized (mProcessorSyncObject) {
            return mProcessor.rotateVideo(video, output);
        }
    }

    /**
     * 几段短音频连接成一个长音频
     *
     * @param audios
     * @param output
     * @return
     */
    public boolean concatAudios(List<String> audios, String output) {
        synchronized (mProcessorSyncObject) {
            if (audios.size() == 0) {
                return false;
            }
            if (audios.size() == 1) {
                return FileUtils.copy(audios.get(0), output);
            }
            StringBuilder cmd = new StringBuilder();
            for (String audio : audios) {
                cmd.append(" -i ");
                cmd.append(audio);
            }
            cmd.append(" -filter_complex ");
            for (int i = 0; i < audios.size(); ++i) {
                cmd.append("[");
                cmd.append(i);
                cmd.append(":0]");
            }
            cmd.append("concat=n=");
            cmd.append(audios.size());
            cmd.append(":v=0:a=1[out] -map [out] -y ");
            cmd.append(output);
            return mProcessor.execute(cmd.toString());
        }
    }


    public boolean trimAudio(String audio, float fromTime, float duration, @FloatRange(from = 0.0f, to = 1.0f) float volume, String output) {
        String cmd = " -ss " + fromTime + " -t " + duration + " -i " + audio + " -af volume=" + volume + " -c:a pcm_s16le -y " + output;
        Log.i("sun", "trim--" + cmd);
        return execute(cmd);
    }

    public boolean setVolume(String audio, @FloatRange(from = 0.0f, to = 1.0f) float volume, String output) {
        String cmd = "-i " + audio + " -af volume=" + volume + " -c:a pcm_s16le -y " + output;
        return execute(cmd);
    }

    /**
     * 多个音频组合成一个，长度相同
     *
     * @param audios
     * @param output
     * @return
     */
    public boolean mixAudios(List<String> audios, String output) {
        synchronized (mProcessorSyncObject) {
            if (audios.size() == 0) {
                return false;
            }
            if (audios.size() == 1) {
                return FileUtils.copy(audios.get(0), output);
            }
            StringBuilder cmd = new StringBuilder();
            for (String audio : audios) {
                cmd.append(" -i ");
                cmd.append(audio);
            }
            cmd.append(" -ar 44100 -ac 1 -filter_complex amix=inputs=");
            cmd.append(audios.size());
            cmd.append(":duration=longest -loglevel error");
            cmd.append(" -y ");
            cmd.append(output);
            return mProcessor.execute(cmd.toString());
        }
    }

    public boolean mux(String video, String audio, String output) {
        String cmd = "-i " + video +
                " -i " + audio +
                " -c:v copy -c:a aac -shortest -map 0:v -map 1:a -y " + output;

        return execute(cmd);
    }

    public boolean extractAudio(String video, String output) {
        String cmd = "-i " + video + " -vn -c:a pcm_s16le -y " + output;
        return execute(cmd);
    }

    public boolean extractVideo(String video, String output) {
        String cmd = "-i " + video + " -an -c:v copy -y " + output;
        return execute(cmd);
    }

    public boolean extractImageFromVideo(String video, double time, String output) {
        String cmd = "-i " + video + " -ss " + time + " -vframes 1 -y " + output;
        return execute(cmd);
    }

    public boolean extractImagesFromVideo(String video, int fps, String outputDir) {
        String cmd = "-i " + video + " -vf fps=" + fps + " " + outputDir + "/%d.png";
        return execute(cmd);
    }
  
    /**
     *
     * @param video     背景视频
     * @param duration 秒，音频长度
     * @param output   长的 视频
     * @return
     * @throws IOException
     */
    public boolean generateLoopVideo(String video, double duration, String output) throws IOException {
        synchronized (mProcessorSyncObject) {
            List<String> tmpVideos = new ArrayList<>();
            double videosDuration = 0;
            if (!new File(video).exists()) {
                return false;
            }


            // calculate video list
            DCMediaInfoExtractor.MediaInfo mediaInfo = DCMediaInfoExtractor.extract(video);
            double videoDuration = mediaInfo.durationUs / 1000000.0;
            while (videosDuration < duration) {
                videosDuration +=videoDuration;
                tmpVideos.add(video);
            }
            if (tmpVideos.size() == 0) {
                return false;
            }

            // generate concat config file
            String dir = new File(output).getParent();
            String configFile = dir + "/concatConfig.txt";
            generateConcatConfig(tmpVideos, configFile);

            // generate concat video
            String concatVideo = output;
            if (videosDuration != duration) {
                concatVideo = dir + "/concat_" + System.nanoTime() + ".mp4";
            }
            String cmd = "-f concat -safe 0 -i " + configFile + " -c copy -y " + concatVideo;
            boolean result = mProcessor.execute(cmd);

            // remove concat config file
            File file = new File(configFile);
            if (file.exists()) {
                file.delete();
            }

            if (videosDuration == duration) {
                return result;
            } else {
                // trim video
                if (result) {
                    String destCmd = " -ss 0 -t " + duration + " -i " + concatVideo + " -c copy -y " + output;
                    result = mProcessor.execute(destCmd);
                    new File(concatVideo).delete();
                    return result;
                }
            }
            return false;
        }
    }

    public boolean execute(String cmd) {
        synchronized (mProcessorSyncObject) {
            return mProcessor.execute(cmd);
        }
    }

    public void setListener(TranscodeListener listener) {
        synchronized (mProcessorSyncObject) {
            mProcessor.setTranscodeListener(listener);
        }
    }

    protected void finalize() throws java.lang.Throwable {

        Log.e("SLClipVideo-finalize", "SLVideoProcessor");

        synchronized (mProcessorSyncObject) {
            mProcessor = null;
        }

        super.finalize();
    }

    private void generateConcatConfig(List<String> videos, String output) throws IOException {
        File file = new File(output);
        if (file.exists()) {
            file.delete();
        }
        OutputStream outputStream = new FileOutputStream(output);
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        for (String path : videos) {
            writer.write("file ");
            writer.write(path);
            writer.append('\n');
        }
        writer.close();
        outputStream.close();
    }

}
