package com.example.monster.airgesture.model.phase;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.example.monster.airgesture.GlobalConfig;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by FruitBasket on 2017/6/5.
 */

public class WavRecorderV1 extends Recorder {
    private static final String TAG=".WavRecorder";

    private boolean isRecording;
    private String audioName;//录音文件的名字

    private String subDir;//用于存放录音文件的子目录

    public WavRecorderV1(){
        super();
    }

    public WavRecorderV1(int channelIn, int sampleRate, int encoding){
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
                    return;
                } else if (bufferSize == AudioRecord.ERROR) {
                    Log.e(TAG, "recordingBufferSize==AudioRecord.ERROR");
                    return;
                }

                byte[] buffer = new byte[bufferSize];
                try {
                    //创建子目录
                    File subFile=new File(GlobalConfig.sFileResultPath+File.separator+subDir+File.separator);
                    boolean state=(subFile).mkdir();
                    Log.d(TAG,"create sub dir state=="+state);

                    String[] files=subFile.list();
                    int fileNumber;
                    if(files==null){
                        fileNumber=0;
                    }
                    else{
                        fileNumber=subFile.list().length;
                    }
                    audioName =getRecordedFileName("_Watch"+(fileNumber+1));//命名方式有些奇怪，后续要改进

                    File audioFile;
                    DataOutputStream output;

                    audioFile=new File(GlobalConfig.sFileResultPath+File.separator+subDir+File.separator+audioName);

                    output= new DataOutputStream(
                            new BufferedOutputStream(
                                    new FileOutputStream(audioFile)
                            )
                    );
                    /*else{//否则
                        Log.i(TAG,"mark2: the device has not got a external storage");
                        String string=subDir+File.separator+audioName;
                        output= new DataOutputStream(
                                new BufferedOutputStream(
                                        MyApp.getContext().openFileOutput(string, Context.MODE_PRIVATE)
                                )
                        );
                        audioFile=MyApp.getContext().getFileStreamPath(string);
                    }*/

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
                            for (int i = 0; i < readResult; i++) {
                                output.writeByte(buffer[i]);
                            }
                        }
                    }

                    //结束以上循环后就停止播放并释放资源
                    audioRecord.stop();
                    output.flush();
                    output.close();
                    audioRecord.release();
                    audioRecord = null;

                    Log.i(TAG, "begin to make wav file");
                    //制作wav文件
                    ///这里先将原始音频保存起来，在改装成wav文件，这不是一个好做法
                    BufferedInputStream inputStream;
                    BufferedOutputStream outputStream;

                    int length;
                    FileInputStream fis = new FileInputStream(audioFile);
                    inputStream= new BufferedInputStream(fis);

                    outputStream = new BufferedOutputStream(
                            new FileOutputStream(GlobalConfig.fResultPath+File.separator+subDir+File.separator+audioName + ".wav")
                    );
                    length= (int) fis.getChannel().size();

                    byte[] readBuffer = new byte[1024];

                    Log.i(TAG, "create a wav file header");
                    WavHeader wavHeader = new WavHeader();
                    wavHeader.setAdjustFileLength(length - 8);
                    wavHeader.setAudioDataLength(length - 44);
                    wavHeader.setBlockAlign(channelIn, encoding);
                    wavHeader.setByteRate(channelIn, sampleRate, encoding);
                    wavHeader.setChannelCount(channelIn);
                    wavHeader.setEncodingBit(encoding);
                    wavHeader.setSampleRate(sampleRate);
                    wavHeader.setWaveFormatPcm(WavHeader.WAV_FORMAT_PCM);

                    outputStream.write(wavHeader.getHeader());
                    while (inputStream.read(readBuffer) != -1) {
                        outputStream.write(readBuffer);
                    }
                    inputStream.close();
                    outputStream.close();
                    audioFile.delete();//删除原始的pcm文件

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }).start();
    }

    @Override
    public boolean stop() {
        isRecording = false;
        return true;
    }

    /**
     * get a recorded file name by time.
     * @param extensionName extension name of the file
     * @return file name
     */
    private static String getRecordedFileName(String extensionName){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH'h'-mm'm'-ss's'");
        return df.format(System.currentTimeMillis())+"."+extensionName;
    }
}
