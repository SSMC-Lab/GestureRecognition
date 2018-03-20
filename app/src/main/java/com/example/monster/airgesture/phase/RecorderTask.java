package com.example.monster.airgesture.phase;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.example.monster.airgesture.utils.LogUtils;

import java.util.Queue;

/**
 * 录制声音
 */
public class RecorderTask {

    private boolean isReadyRecording;

    private int channelIn;//用于指定音频的来源
    private int sampleRate;//声音的采样频率
    private int encoding;//指定每一个声音帧的位大小
    private AudioRecord audioRecord;

    RecorderTask(int channelIn, int sampleRate, int encoding) {
        this.channelIn = channelIn;
        this.sampleRate = sampleRate;
        this.encoding = encoding;
    }

    public void start(final Queue<short[]> queue) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int bufferSize = AudioRecord.getMinBufferSize(
                        sampleRate,
                        channelIn,
                        AudioFormat.ENCODING_PCM_16BIT);
                if (bufferSize == AudioRecord.ERROR_BAD_VALUE) {
                    LogUtils.e("recordingBufferSize == AudioRecord.ERROR_BAD_VALUE");
                    return;
                } else if (bufferSize == AudioRecord.ERROR) {
                    LogUtils.e("recordingBufferSize == AudioRecord.ERROR");
                    return;
                }
                short[] buffer = new short[bufferSize];
                audioRecord = new AudioRecord(
                        MediaRecorder.AudioSource.MIC,
                        sampleRate,
                        channelIn,
                        encoding,
                        bufferSize);
                audioRecord.startRecording();

                isReadyRecording = true;
                while (isReadyRecording) {
                    int readResult = audioRecord.read(buffer, 0, bufferSize);
                    if (readResult == AudioRecord.ERROR_INVALID_OPERATION) {
                        LogUtils.e("readState == AudioRecord.ERROR_INVALID_OPERATION");
                        return;
                    } else if (readResult == AudioRecord.ERROR_BAD_VALUE) {
                        LogUtils.e("readState == AudioRecord.ERROR_BAD_VALUE");
                        return;
                    } else {
                        queue.add(buffer);
                    }
                }
            }
        }).start();
    }

    public void stop() {
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
        }
        isReadyRecording = false;
    }
}
