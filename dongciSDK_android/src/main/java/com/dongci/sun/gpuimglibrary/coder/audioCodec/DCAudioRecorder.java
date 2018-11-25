package com.dongci.sun.gpuimglibrary.coder.audioCodec;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Looper;
import android.util.Log;

import com.dongci.sun.gpuimglibrary.coder.videoCodec.VideoEncoderCore;
import com.dongci.sun.gpuimglibrary.common.SLVideoTool;
import com.dongci.sun.gpuimglibrary.common.SyncRecord;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class DCAudioRecorder {

    private AudioRecord mAudioRecord;
    private boolean mIsRecording = false;

    private int mBufferSizeInBytes = 0;
    private int mSampleRate;
    private String mPcmDataPath;
    private String mOutputPath;

    private Thread mWorkThread;
    private boolean hasWrittenOneFrame = false;

    public DCAudioRecorder(int sampleRate, String outputPath) {
        mOutputPath = outputPath;
        File file = new File(mOutputPath);
        if (file.exists()) {
            file.delete();
        }
        mPcmDataPath = outputPath + ".pcm";
        file = new File(mPcmDataPath);
        if (file.exists()) {
            file.delete();
        }
        mSampleRate = sampleRate;
    }

    public String getOutputPath() {
        return mOutputPath;
    }

    public int startRecording() {
        if (mIsRecording) {
            return -1;
        } else {
            if (mAudioRecord == null) {
                // get buffer size
                mBufferSizeInBytes = AudioRecord.getMinBufferSize(
                        mSampleRate,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);

                // create recorder
                mAudioRecord = new AudioRecord(
                        MediaRecorder.AudioSource.MIC,
                        mSampleRate,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        mBufferSizeInBytes);
            }

            mAudioRecord.startRecording();

            // get recording time
            SLVideoTool.startRecordTime = System.nanoTime();

            mIsRecording = true;
            // start work thread
            mWorkThread = new Thread(new AudioRecordThread());
            mWorkThread.start();
            Log.d("sun", "syncRecord--audio--thread-是主线程" + (Looper.myLooper() == Looper.getMainLooper()));
        }
        return 0;
    }

    public void stopRecording() {
        mIsRecording = false;
        try {
            if (mWorkThread != null)
                mWorkThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (mAudioRecord != null) {
            mAudioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;
        }
    }

    class AudioRecordThread implements Runnable {
        @Override
        public void run() {

            if (writePcmData()) {
                writeHeaderToWavFile(mPcmDataPath, mOutputPath);
            }
            File pcmFile = new File(mPcmDataPath);
            if (pcmFile.exists()) {
                pcmFile.delete();
            }
        }
    }

    private boolean writePcmData() {
        byte[] audioBuffer = new byte[mBufferSizeInBytes];
        FileOutputStream fos;
        int readSize;
        try {
            fos = new FileOutputStream(new File(mPcmDataPath));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        while (mIsRecording) {

            if (!SLVideoTool.isStartedToRecordAudio) {
                SLVideoTool.isStartedToRecordAudio = true;
            }

            SyncRecord.waitAudio();
            SyncRecord.isAudioOk = true;

            if (!hasWrittenOneFrame) {
                hasWrittenOneFrame = true;
                if (writeData != null) {
                    writeData.onWriteOk();
                }
            }

            readSize = mAudioRecord.read(audioBuffer, 0, mBufferSizeInBytes);
            SLVideoTool.lastRecordTime = System.nanoTime() - 23219000;

            if (AudioRecord.ERROR_INVALID_OPERATION != readSize) {
                try {
                    fos.write(audioBuffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void writeHeaderToWavFile(String inFilename, String outFilename) {
        FileInputStream in;
        FileOutputStream out;
        int channels = 1;
        long byteRate = 16 * mSampleRate * channels / 8;
        byte[] data = new byte[mBufferSizeInBytes];
        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            long totalAudioLen = in.getChannel().size();
            long totalDataLen = totalAudioLen + 36;
            writeHeader(
                    out,
                    totalAudioLen,
                    totalDataLen,
                    mSampleRate,
                    channels,
                    byteRate);
            while (in.read(data) != -1) {
                out.write(data);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeHeader(FileOutputStream out,
                             long totalAudioLen,
                             long totalDataLen,
                             long longSampleRate,
                             int channels,
                             long byteRate) throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = 16; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }

    VideoEncoderCore.WriteData writeData;

    public void setWriteData(VideoEncoderCore.WriteData writeData) {
        this.writeData = writeData;
    }
}
