package com.example.monster.airgesture.phase;

import android.os.Handler;

import com.example.monster.airgesture.Conditions;
import com.example.monster.airgesture.GlobalConfig;
import com.example.monster.airgesture.utils.LogUtils;

import java.io.File;


/**
 * 识别功能的开关管理
 * Created by WelkinShadow on 2017/12/13.
 */

public class RecognitionSwitch {
    private static RecognitionSwitch manager;

    private RecognitionSwitch() {
        if (!Conditions.configInit) {
            GlobalConfig.fAbsolutepath.mkdirs();//创建文件夹
            GlobalConfig.fTemplatePath.mkdirs();//创建文件夹
            GlobalConfig.fResultPath.mkdirs();//创建文件夹
            GlobalConfig.stPhaseProxy.init();
            Conditions.configInit = true;
        }
    }

    public static RecognitionSwitch getInstance() {
        if (manager == null) {
            manager = new RecognitionSwitch();
        }else if (manager!=null && GlobalConfig.isRecording || GlobalConfig.isPlaying){
            manager.stopRecognition();
            LogUtils.w("No explicit stop recognition in the code before you restart recognition");
        }
        return manager;
    }

    /**
     * 启用识别功能，handler从录音线程回传识别结果
     *
     * @param handler
     */
    public void startRecognition(Handler handler) {
        LogUtils.d("startRecognition");
        GlobalConfig.stPhaseProxy.sendHandler(handler);
        //播放声波
        if (!GlobalConfig.isPlaying) {
            GlobalConfig.stWavePlayer.play();//在这里接受数据
            GlobalConfig.isPlaying = true;
            LogUtils.d("stWavePlayer play");
        }
        //启动录音
        if (!GlobalConfig.isRecording) {
            GlobalConfig.stWavRecorder.start();//在这里处理手势识别
            GlobalConfig.isRecording = true;
            LogUtils.d("stWavRecorder start");
        }
        GlobalConfig.fPcmRecordFile2 = new File(GlobalConfig.getRecordedFileName(GlobalConfig.sRecordPcmFileName2));
    }

    /**
     * 关闭识别功能
     */
    public void stopRecognition() {
        LogUtils.d("stopRecognition");
        GlobalConfig.isRecording = false;
        GlobalConfig.isPlaying = false;
        GlobalConfig.stPhaseProxy.destroy();
        GlobalConfig.stWaveFileUtil.destroy();
        GlobalConfig.stWavRecorder.stop();
        GlobalConfig.stWavePlayer.stop();
        GlobalConfig.stPhaseProxy.dropHandler();
    }
}
