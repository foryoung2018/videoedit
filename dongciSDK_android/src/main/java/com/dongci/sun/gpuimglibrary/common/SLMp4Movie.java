package com.dongci.sun.gpuimglibrary.common;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaFormat;

import com.dongci.sun.gpuimglibrary.coder.SLTrack;
import com.googlecode.mp4parser.util.Matrix;

import java.io.File;
import java.util.ArrayList;

@TargetApi(16)
public class SLMp4Movie {
    private Matrix matrix = Matrix.ROTATE_0;
    private ArrayList<SLTrack> tracks = new ArrayList<SLTrack>();
    private File cacheFile;
    private int width;
    private int height;

    public Matrix getMatrix() {
        return matrix;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setCacheFile(File file) {
        cacheFile = file;
    }

    public void setRotation(int angle) {
        if (angle == 0) {
            matrix = Matrix.ROTATE_0;
        } else if (angle == 90) {
            matrix = Matrix.ROTATE_90;
        } else if (angle == 180) {
            matrix = Matrix.ROTATE_180;
        } else if (angle == 270) {
            matrix = Matrix.ROTATE_270;
        }
    }

    public void setSize(int w, int h) {
        width = w;
        height = h;
    }

    public ArrayList<SLTrack> getTracks() {
        return tracks;
    }

    public File getCacheFile() {
        return cacheFile;
    }

    public void addSample(int trackIndex, long offset, MediaCodec.BufferInfo bufferInfo) throws Exception {
        if (trackIndex < 0 || trackIndex >= tracks.size()) {
            return;
        }
        SLTrack track = tracks.get(trackIndex);
        track.addSample(offset, bufferInfo);
    }

    public int addTrack(MediaFormat mediaFormat, boolean isAudio) throws Exception {
        tracks.add(new SLTrack(tracks.size(), mediaFormat, isAudio));
        return tracks.size() - 1;
    }
}