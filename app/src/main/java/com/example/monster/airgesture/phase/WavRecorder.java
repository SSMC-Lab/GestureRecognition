package com.example.monster.airgesture.phase;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.example.monster.airgesture.GlobalConfig;

public class WavRecorder extends Recorder {

    private static final String TAG="..WavRecorder";

    private boolean isRecording;
    private String audioName;//录音文件的名字
    private String subDir;//用于存放录音文件的子目录

    public WavRecorder(){
        super();
    }

    public WavRecorder(int channelIn, int sampleRate, int encoding){
        super(channelIn,sampleRate,encoding);
    }

    @Override
    public void start() {

        Log.i(TAG,"start()");
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
                    return ;
                } else if (bufferSize == AudioRecord.ERROR) {
                    Log.e(TAG, "recordingBufferSize==AudioRecord.ERROR");
                    return ;
                }
                byte[] buffer = new byte[bufferSize];

                AudioRecord audioRecord = new AudioRecord(
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

                        try {
                            GlobalConfig.getInstance().pushRecData(buffer);
                        }
                        catch (InterruptedException exp)
                        {
                            exp.printStackTrace();
                        }

                        /*for (int i = 0; i < readResult; i++) {
                            output.writeByte(buffer[i]);
                        }*/
                    }
                }
            }
        }).start();
    }

    @Override
    public boolean stop() {
        isRecording = false;
        return true;
    }
}
