package com.dongci.sun.gpuimglibrary.player;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;


/**
 * Created by ZXGoto on 26/10/2017.
 */

public class DCVideoRetriever {
    private static final String TAG = "SLVideoThumbnail";
    private static final boolean VERBOSE = true;
    private static final long TIMEOUT_US = 20000;

    private static final int COLOR_FormatI420 = 1;
    private static final int COLOR_FormatNV21 = 2;

    private DCMediaInfoExtractor.MediaInfo mMediaInfo;
    private long mEstimatedKeyframeInterval;
    private long mIdenticalFrameInterval;

    private MediaExtractor mExtractor = null;
    private MediaCodec mDecoder = null;
    private MediaFormat mMediaFormat;
    private MediaCodec.BufferInfo mCurFrameInfo = new MediaCodec.BufferInfo();
    private boolean mIsExtractorReachedEOS;
    private long mPrevDecoderOutputFramePtsUs;

    public void setDataSource(String videoFilePath) {
        try {
            mMediaInfo = DCMediaInfoExtractor.extract(videoFilePath);
            mEstimatedKeyframeInterval = mMediaInfo.videoInfo.perFrameDurationUs * mMediaInfo.videoInfo.fps;
            mIdenticalFrameInterval = (long) (mMediaInfo.videoInfo.perFrameDurationUs * 0.5);

            mExtractor = new MediaExtractor();
            mExtractor.setDataSource(videoFilePath);
            Log.d("tag",this+"snapshot--setDataSource>"+mExtractor);
            int trackIndex = selectTrack(mExtractor);
            if (trackIndex < 0) {
                throw new RuntimeException("No video track found in " + videoFilePath);
            }
            mExtractor.selectTrack(trackIndex);
            mMediaFormat = mExtractor.getTrackFormat(trackIndex);
            restartDecoder();
        } catch (Exception e) {
            e.printStackTrace();
            release();
        }
    }

    public void release() {
        if (mDecoder != null) {
            mDecoder.stop();
            mDecoder.release();
            mDecoder = null;
        }
        if (mExtractor != null) {
            mExtractor.release();
            mExtractor = null;
            Log.d("tag","snapshot--release>"+mExtractor);
        }
    }

    private boolean isDecoderReachEOS() {
        return ((mCurFrameInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0);
    }

    private void extractorSeekTo(long timestamp, int seekFlag) {
        Log.d("tag",this+"snapshot--extractorSeekTo>"+mExtractor);
        while (true) {
            mExtractor.seekTo(timestamp, seekFlag);
            if (mExtractor.getSampleTime() <= timestamp) {
                break;
            } else {
                timestamp = Math.max(0, timestamp - mEstimatedKeyframeInterval);
            }
        }
        mIsExtractorReachedEOS = false;
    }

    private void restartDecoder() {
        if (mDecoder != null) {
            mDecoder.stop();
            mDecoder.release();
            mDecoder = null;
        }
        try {
            String mime = mMediaFormat.getString(MediaFormat.KEY_MIME);
            mDecoder = MediaCodec.createDecoderByType(mime);
            mDecoder.configure(mMediaFormat, null, null, 0);
            mDecoder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // targetTimestamp: microsecond
    public Bitmap getFrameAtTime(long targetTimestamp) {
        Bitmap bmp = null;
        boolean sawOutputEOS = false;
        long seekInterval = targetTimestamp - mCurFrameInfo.presentationTimeUs;
        if (seekInterval < 0
                || (isDecoderReachEOS() && mCurFrameInfo.presentationTimeUs == 0)) {
            extractorSeekTo(targetTimestamp, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
            if (isDecoderReachEOS()) {
                restartDecoder();
            } else {
                if (mDecoder != null) {
                    mDecoder.flush();
                }
            }
        } else {
            if (isDecoderReachEOS()) {
                return null;
            }
            if (seekInterval > mEstimatedKeyframeInterval) {
                extractorSeekTo(targetTimestamp, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
                if (mDecoder != null) {
                    mDecoder.flush();
                }
            }
        }
        if (mDecoder == null) {
            return null;
        }
        if (mDecoder == null) {
            return null;
        }

        while (!sawOutputEOS) {
            if (!mIsExtractorReachedEOS) {
                int inputBufferId = mDecoder.dequeueInputBuffer(TIMEOUT_US);
                if (inputBufferId >= 0) {
                    ByteBuffer inputBuffer = mDecoder.getInputBuffer(inputBufferId);
                    if (inputBuffer != null) {
                        int sampleSize = mExtractor.readSampleData(inputBuffer, 0);
                        if (sampleSize < 0) {
                            mDecoder.queueInputBuffer(inputBufferId, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                            mIsExtractorReachedEOS = true;
                        } else {
                            long presentationTimeUs = mExtractor.getSampleTime();
                            mDecoder.queueInputBuffer(inputBufferId, 0, sampleSize, presentationTimeUs, 0);
                            mExtractor.advance();
                        }
                    }
                }
            }
            mPrevDecoderOutputFramePtsUs = mCurFrameInfo.presentationTimeUs;
            int outputBufferId = mDecoder.dequeueOutputBuffer(mCurFrameInfo, TIMEOUT_US);
            if (outputBufferId >= 0) {
                if ((mCurFrameInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    sawOutputEOS = true;
                }
                boolean doRender = (mCurFrameInfo.size != 0);
                if (doRender) {
                    boolean isFinished = currentFrameIsTargetFrame(mCurFrameInfo.presentationTimeUs, targetTimestamp);
                    if (isFinished) {
                        Image image = mDecoder.getOutputImage(outputBufferId);
                        if (image != null) {
                            bmp = convertToBitmap(image);
                            image.close();
                        }
                    }
                    mDecoder.releaseOutputBuffer(outputBufferId, false);
                    if (isFinished) break;
                }
            }
        }
        return bmp;
    }

    private boolean currentFrameIsTargetFrame(long currentTimestamp, long targetTimestamp) {
        return (Math.abs(currentTimestamp - targetTimestamp) < mIdenticalFrameInterval
                || (targetTimestamp > mPrevDecoderOutputFramePtsUs && targetTimestamp < currentTimestamp));
    }

    private static int selectTrack(MediaExtractor extractor) {
        int numTracks = extractor.getTrackCount();
        for (int i = 0; i < numTracks; i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("video/")) {
                if (VERBOSE) {
                    Log.d(TAG, "Extractor selected track " + i + " (" + mime + "): " + format);
                }
                return i;
            }
        }
        return -1;
    }

    private static boolean isImageFormatSupported(Image image) {
        int format = image.getFormat();
        switch (format) {
            case ImageFormat.YUV_420_888:
            case ImageFormat.NV21:
            case ImageFormat.YV12:
                return true;
        }
        return false;
    }

    private static byte[] getDataFromImage(Image image, int colorFormat) {
        if (colorFormat != COLOR_FormatI420 && colorFormat != COLOR_FormatNV21) {
            throw new IllegalArgumentException("only support COLOR_FormatI420 " + "and COLOR_FormatNV21");
        }
        if (!isImageFormatSupported(image)) {
            throw new RuntimeException("can't convert Image to byte array, format " + image.getFormat());
        }
        Rect crop = image.getCropRect();
        int format = image.getFormat();
        int width = crop.width();
        int height = crop.height();
        Image.Plane[] planes = image.getPlanes();
        byte[] data = new byte[width * height * ImageFormat.getBitsPerPixel(format) / 8];
        byte[] rowData = new byte[planes[0].getRowStride()];
        int channelOffset = 0;
        int outputStride = 1;
        for (int i = 0; i < planes.length; i++) {
            switch (i) {
                case 0:
                    channelOffset = 0;
                    outputStride = 1;
                    break;
                case 1:
                    if (colorFormat == COLOR_FormatI420) {
                        channelOffset = width * height;
                        outputStride = 1;
                    } else if (colorFormat == COLOR_FormatNV21) {
                        channelOffset = width * height + 1;
                        outputStride = 2;
                    }
                    break;
                case 2:
                    if (colorFormat == COLOR_FormatI420) {
                        channelOffset = (int) (width * height * 1.25);
                        outputStride = 1;
                    } else if (colorFormat == COLOR_FormatNV21) {
                        channelOffset = width * height;
                        outputStride = 2;
                    }
                    break;
            }
            ByteBuffer buffer = planes[i].getBuffer();
            int rowStride = planes[i].getRowStride();
            int pixelStride = planes[i].getPixelStride();
            if (VERBOSE) {
                Log.v(TAG, "pixelStride " + pixelStride);
                Log.v(TAG, "rowStride " + rowStride);
                Log.v(TAG, "width " + width);
                Log.v(TAG, "height " + height);
                Log.v(TAG, "buffer size " + buffer.remaining());
            }
            int shift = (i == 0) ? 0 : 1;
            int w = width >> shift;
            int h = height >> shift;
            buffer.position(rowStride * (crop.top >> shift) + pixelStride * (crop.left >> shift));
            for (int row = 0; row < h; row++) {
                int length;
                if (pixelStride == 1 && outputStride == 1) {
                    length = w;
                    buffer.get(data, channelOffset, length);
                    channelOffset += length;
                } else {
                    length = (w - 1) * pixelStride + 1;
                    buffer.get(rowData, 0, length);
                    for (int col = 0; col < w; col++) {
                        data[channelOffset] = rowData[col * pixelStride];
                        channelOffset += outputStride;
                    }
                }
                if (row < h - 1) {
                    buffer.position(buffer.position() + rowStride - length);
                }
            }
        }
        return data;
    }

    private Bitmap convertToBitmap(Image image) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Rect rect = image.getCropRect();
        YuvImage yuvImage = new YuvImage(getDataFromImage(image, COLOR_FormatNV21), ImageFormat.NV21, rect.width(), rect.height(), null);
        yuvImage.compressToJpeg(rect, 100, out);

        byte[] imageBytes = out.toByteArray();
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
}

