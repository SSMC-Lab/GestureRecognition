package com.example.monster.airgesture.phase;

import android.os.Handler;
import android.os.Message;

import com.example.monster.airgesture.Conditions;
import com.example.monster.airgesture.GlobalConfig;
import com.example.monster.airgesture.utils.HandlerUtils;
import com.example.monster.airgesture.utils.LogUtils;

import java.io.File;


/**
 * 识别功能的业务逻辑接口
 * Created by WelkinShadow on 2017/12/13.
 */

public class PhaseBiz {

    private static PhaseBiz INSTANCE;
    private static PhaseProxy mPhaseProxy;

    private PhaseBiz() {
        GlobalConfig.fPcmRecordFile2 = new File(
                GlobalConfig.getRecordedFileName(
                        GlobalConfig.sRecordPcmFileName2));
        if (!Conditions.configInit) {
            GlobalConfig.fAbsolutePath.mkdirs();//创建文件夹
            GlobalConfig.fTemplatePath.mkdirs();//创建文件夹
            GlobalConfig.fResultPath.mkdirs();//创建文件夹
            GlobalConfig.stPhaseProxy.init();
            Conditions.configInit = true;
        }
        mPhaseProxy = new PhaseProxy();
    }

    public static PhaseBiz getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PhaseBiz();
        } else if (GlobalConfig.isRecording || GlobalConfig.isPlaying) {
            INSTANCE.stopRecognition();
            LogUtils.w("No explicit stop recognition in the code before you restart recognition");
        }
        return INSTANCE;
    }

    /**
     * 启用识别功能，handler从解析任务线程回传识别结果
     */
    public void startRecognition(Handler handler) {
        LogUtils.d("startRecognition");
        mPhaseProxy.setHandler(handler);
        mPhaseProxy.start();
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
    }

    /**
     * 启用识别功能，handler从解析任务线程回传识别结果
     */
    public void startRecognition(final ActionTypeListener listener) {
        HandlerUtils.OnReceiveMessageListener handlerListener = new HandlerUtils.OnReceiveMessageListener() {
            @Override
            public void handlerMessage(Message msg) {
                switch (msg.what) {
                    case Conditions.MESSAGE_PHASE_MODEL:
                        listener.receiveActionType(msg.getData().getFloat(Conditions.TYPE));
                        break;
                }
            }
        };
        Handler handler = new HandlerUtils.HandlerHolder(handlerListener);
        startRecognition(handler);
    }

    /**
     * 关闭识别功能
     */
    public void stopRecognition() {
        LogUtils.d("stopRecognition");
        GlobalConfig.isRecording = false;
        GlobalConfig.isPlaying = false;
        GlobalConfig.stPhaseProxy.destroy();
        GlobalConfig.stWavRecorder.stop();
        GlobalConfig.stWavePlayer.stop();
    }
}
