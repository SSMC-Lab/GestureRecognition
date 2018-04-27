package com.example.monster.airgesture.phase;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;


import java.util.Queue;

/**
 * 录制声音
 */
class RecorderTask {

    private static final String TAG = "RecorderTask";

    private boolean isReadyRecording;

    private AudioRecord mAudioRecord;
    private int mBufferSize;

    /*RecorderTask(AudioRecord audioRecord, int bufferSize) {
        this.mAudioRecord = audioRecord;
        this.mBufferSize = bufferSize;
    }*/

    RecorderTask(int channelIn, int sampleRate, int encoding) {
        mBufferSize = AudioRecord.getMinBufferSize(
                sampleRate,
                channelIn,
                encoding);
        mAudioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelIn,
                encoding,
                mBufferSize);
    }

    public void start(final Queue<short[]> queue) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                short[] buffer = new short[mBufferSize / 2];
                mAudioRecord.startRecording();

                isReadyRecording = true;
                while (isReadyRecording) {
                    int readResult = mAudioRecord.read(buffer, 0, buffer.length);
                    if (readResult == AudioRecord.ERROR_INVALID_OPERATION) {
                        Log.e(TAG, "readState == AudioRecord.ERROR_INVALID_OPERATION");
                        return;
                    } else if (readResult == AudioRecord.ERROR_BAD_VALUE) {
                        Log.e(TAG, "readState == AudioRecord.ERROR_BAD_VALUE");
                        return;
                    }
                    queue.add(buffer);
                }
            }
        }).start();
    }

    public void stop() {
        if (mAudioRecord != null) {
            mAudioRecord.stop();
            mAudioRecord.release();
        }
        isReadyRecording = false;
    }
}
