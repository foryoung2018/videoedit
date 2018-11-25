package com.wmlive.hhvideo.heihei.quickcreative;

import android.graphics.Bitmap;

import com.dongci.sun.gpuimglibrary.player.DCMediaInfoExtractor;
import com.wmlive.hhvideo.utils.KLog;

import org.jcodec.api.JCodecException;
import org.jcodec.api.android.AndroidFrameGrab;
import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.tools.MainUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExtractVideoFrameThread extends Thread {

    public List<String> paths = new ArrayList<>();

    private String filename;
    private float fps;
    private String prefix;
    private String outputDir;

    public ExtractVideoFrameThread(String filename, float fps, String prefix, String outputDir) {
        this.filename = filename;
        this.fps = fps;
        this.prefix = prefix;
        this.outputDir = outputDir;
    }

    @Override
    public void run() {
        paths = extractImages(filename, fps, prefix, outputDir);
    }

    private List<String> extractImages(String filename, float fps, String prefix, String outputDir) {
        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdir();
        }
        List<String> paths = new ArrayList<>();
        DCMediaInfoExtractor.MediaInfo mediaInfo = null;
        try {
            mediaInfo = DCMediaInfoExtractor.extract(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long duration = mediaInfo.durationUs;
        long time = 0;
        long frameInterval = (long) (1000000 * 1.0 / fps);

        FileChannelWrapper in = null;
        try {
            in = NIOUtils.readableChannel(MainUtils.tildeExpand(filename));
            AndroidFrameGrab frameGrab = AndroidFrameGrab.createAndroidFrameGrab(in);
            int index = 0;
            while (time < duration) {
                double sec = (double) time / 1000000.0;
                frameGrab.seekToSecondPrecise(sec);
                Bitmap bitmap = frameGrab.getFrame();
                if (bitmap != null) {
                    Bitmap destBmp = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);

//                        String path = String.format("%s/%s_image_%d.png", outputDir, prefix, index++);//String.format("%s/%s_image_%02d.png", outputDir, prefix, index++);
                    String path = outputDir + "/" + prefix + "_image_" + (index++) + ".png";
                    File file = new File(path);
                    if (file.exists()) {
                        file.delete();
                    }
                    try (FileOutputStream out = new FileOutputStream(path)) {
                        if (destBmp.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                            paths.add(path);
                            destBmp.recycle();
                            bitmap.recycle();
                        }
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    KLog.e("ExtractVideoFrameThread", "--- " + path);
                }
                time += frameInterval;
            }
            KLog.e("ExtractVideoFrameThread", "--- End" );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JCodecException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            NIOUtils.closeQuietly(in);
        }
        return paths;
    }
}
