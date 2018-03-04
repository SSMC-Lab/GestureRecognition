package com.example.monster.airgesture.phase;

import com.example.monster.airgesture.Conditions;

import com.example.monster.airgesture.utils.LogUtils;


/**
 * 手势解析模块实现类，在{@link PhaseBiz#mListener}回调解析动作数据
 */

public class PhaseBiz implements IPhaseBiz {
    private PhaseProxy mPhaseProxy;
    private PlayerTask mPlayerTask;
    private RecorderTask mRecorderTask;
    private IPhaseBiz.PhaseListener mListener;
    private boolean isRunning;

    PhaseBiz(PhaseProxy mPhaseProxy, PlayerTask mPlayerTask,
             RecorderTask mRecorderTask, final PhaseListener mListener) {
        this.mPhaseProxy = mPhaseProxy;
        this.mPlayerTask = mPlayerTask;
        this.mRecorderTask = mRecorderTask;
        this.mListener = mListener;
        Conditions.fAbsolutePath.mkdirs();//创建文件夹
        Conditions.fTemplatePath.mkdirs();//创建文件夹
        Conditions.fResultPath.mkdirs();//创建文件夹
    }

    @Override
    public void startRecognition() {
        LogUtils.d("startRecognition");
        SyncLinkedList<short[]> queue = new SyncLinkedList<>();
        if (!isRunning) {
            mPlayerTask.start();
            mRecorderTask.start(queue);
            mPhaseProxy.start(mListener,queue);
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
