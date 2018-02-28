package com.example.monster.airgesture;

import android.app.Application;
import android.media.AudioFormat;

import com.example.monster.airgesture.phase.PhaseProxy;
import com.example.monster.airgesture.phase.WavRecorder;
import com.example.monster.airgesture.phase.WavePlayer;
import com.example.monster.airgesture.utils.WaveFileUtils;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2017/7/5.
 */

public class GlobalConfig  {

    private static GlobalConfig stGlobalConfig;
    //Record sample rate
    public static int AUDIO_SAMPLE_RATE = 44100;//44100;//16000;//48000
    public static int AUDIO_PLAY_FREQ = 20000;//44100;//16000;//48000
    public static boolean bByte = true;
    public static int RECORD_FRAME_SIZE = 512 * 2; //byte
    public static int MAX_RECORD_CACHE_SIZE = 4096;
    //switch
    public static boolean isPlaying = false;   //是否正在播放
    public static boolean isRecording = false;  //是否正在录制
    public static boolean bPlayDataReady = false; //bPlayData是否READY
    public static boolean bPlayThreadFlag = true;//播放线程false
    public static boolean bDataProcessThreadFlag = true;//处理线程true
    //存放音频数据的队列
    private SyncLinkedList<byte[]> byteSyncLinkedList = new SyncLinkedList<byte[]>();

    //播放器和录音器
    public static WavePlayer stWavePlayer = new WavePlayer(
            3,
            AUDIO_PLAY_FREQ,
            1,
            AUDIO_SAMPLE_RATE);
    public static WavRecorder stWavRecorder = new WavRecorder(
            AudioFormat.CHANNEL_IN_MONO,
            AUDIO_SAMPLE_RATE,
            AudioFormat.ENCODING_PCM_16BIT);

    //文件配置
    //播放的pcm文件路径
    public static String sFileTemplatePath = WaveFileUtils.sAbsolutePath + "/template/";
    public static String sFileResultPath = WaveFileUtils.sAbsolutePath + "/result/";
    public static String sRecordPcmFileName2 = sFileResultPath + "ProxyRecording";
    public static File fPcmRecordFile2;

    public static PhaseProxy stPhaseProxy = new PhaseProxy();
    public static File fAbsolutePath = new File(WaveFileUtils.sAbsolutePath);
    public static File fTemplatePath = new File(sFileTemplatePath);
    public static File fResultPath = new File(sFileResultPath);

    public static String getRecordedFileName(String extensionName) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH'h'-mm'm'-ss's'");
        return extensionName + df.format(System.currentTimeMillis());
    }

    private GlobalConfig() {
    }

    public static GlobalConfig getInstance() {
        if (stGlobalConfig == null) {
            stGlobalConfig = new GlobalConfig();
        }
        return stGlobalConfig;
    }

    public void pushRecData(byte[] recData) throws InterruptedException {
        byteSyncLinkedList.push(recData);
    }

    public byte[] popByteRecData() throws InterruptedException {
        byte[] recData = byteSyncLinkedList.pop(true);
        return recData;
    }

}

