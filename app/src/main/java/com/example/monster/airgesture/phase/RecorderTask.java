package com.example.monster.airgesture.phase;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.example.monster.airgesture.utils.LogUtils;

/**
 * 录制声音
 */
public class RecorderTask {

    private static final String TAG = "..RecorderTask";

    private boolean isRecording;

    private int channelIn;//用于指定音频的来源
    private int sampleRate;//声音的采样频率
    private int encoding;//指定每一个声音帧的位大小
    private AudioRecord audioRecord;

    public RecorderTask(int channelIn, int sampleRate, int encoding) {
        this.channelIn = channelIn;
        this.sampleRate = sampleRate;
        this.encoding = encoding;
    }

    public void start(final SyncLinkedList<short[]> queue) {
        //使用异步的方法录制音频
        new Thread(new Runnable() {
            @Override
            public void run() {
                int bufferSize = AudioRecord.getMinBufferSize(
                        sampleRate,
                        channelIn,
                        AudioFormat.ENCODING_PCM_16BIT);
                if (bufferSize == AudioRecord.ERROR_BAD_VALUE) {
                    Log.e(TAG, "recordingBufferSize==AudioRecord.ERROR_BAD_VALUE");
                    return;
                } else if (bufferSize == AudioRecord.ERROR) {
                    Log.e(TAG, "recordingBufferSize==AudioRecord.ERROR");
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

                isRecording = true;
                while (isRecording) {
                    int readResult = audioRecord.read(buffer, 0, bufferSize);
                    if (readResult == AudioRecord.ERROR_INVALID_OPERATION) {
                        Log.e(TAG, "readState==AudioRecord.ERROR_INVALID_OPERATION");
                        return;
                    } else if (readResult == AudioRecord.ERROR_BAD_VALUE) {
                        Log.e(TAG, "readState==AudioRecord.ERROR_BAD_VALUE");
                        return;
                    } else {
                        //LogUtils.d("bufferSize in WavRecord" + buffer.length);
                        queue.push(buffer);
                    }
                }
            }
        }).start();
    }

    public void stop() {
        if (audioRecord!=null){
            audioRecord.stop();
            audioRecord.release();
        }
        isRecording = false;
    }
}
