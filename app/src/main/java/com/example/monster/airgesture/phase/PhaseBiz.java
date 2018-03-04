package com.example.monster.airgesture.phase;

import com.example.monster.airgesture.Conditions;

import com.example.monster.airgesture.utils.LogUtils;

import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;


/**
 * 手势解析模块实现类，在{@link IPhaseBiz.PhaseListener}回调解析动作数据
 */

public class PhaseBiz implements IPhaseBiz {
    private PhaseProxy mPhaseProxy;
    private PlayerTask mPlayerTask;
    private RecorderTask mRecorderTask;
    private boolean isRunning;

    PhaseBiz(PhaseProxy mPhaseProxy, PlayerTask mPlayerTask,
             RecorderTask mRecorderTask) {
        this.mPhaseProxy = mPhaseProxy;
        this.mPlayerTask = mPlayerTask;
        this.mRecorderTask = mRecorderTask;
        Conditions.fAbsolutePath.mkdirs();//创建文件夹
        Conditions.fTemplatePath.mkdirs();//创建文件夹
        Conditions.fResultPath.mkdirs();//创建文件夹
    }

    @Override
    public void startRecognition(PhaseListener listener) {
        LogUtils.d("startRecognition");
        Queue<short[]> queue = new LinkedTransferQueue<>();
        if (!isRunning) {
            mPlayerTask.start();
            mRecorderTask.start(queue);
            mPhaseProxy.start(queue,listener);
            isRunning = true;
        }
    }

    @Override
    public void stopRecognition() {
        LogUtils.d("stopRecognition");
        if (isRunning){
            mPlayerTask.stop();
            mRecorderTask.stop();
            mPhaseProxy.stop();
            isRunning = false;
        }
    }
}
