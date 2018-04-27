package com.example.monster.airgesture.phase;

import android.content.Context;

import com.example.monster.airgesture.utils.FileCopyUtils;
import java.io.File;
import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;


/**
 * 手势解析模块实现类
 */

public class PhaseBiz implements IPhaseBiz {

    private PhaseProxy mPhaseProxy;
    private PlayerTask mPlayerTask;
    private RecorderTask mRecorderTask;
    private Context mContext;

    private boolean isRunning;

    PhaseBiz(Context context, PhaseProxy mPhaseProxy,
             PlayerTask mPlayerTask, RecorderTask mRecorderTask) {
        this.mPhaseProxy = mPhaseProxy;
        this.mPlayerTask = mPlayerTask;
        this.mRecorderTask = mRecorderTask;
        this.mContext = context;
        File fAbsolutePath = new File(Condition.S_ABSOLUTE_PATH);
        File fTemplatePath = new File(Condition.S_FILE_TEMPLATE_PATH);
        File fResultPath = new File(Condition.S_FILE_RESULT_PATH);
        //创建文件夹
        if (!fAbsolutePath.exists()) fAbsolutePath.mkdirs();
        if (!fTemplatePath.exists()) fTemplatePath.mkdirs();
        if (!fResultPath.exists()) fResultPath.mkdirs();
        //拷贝Assets下的模板数据
        copyTemplate("heng2.txt");
        copyTemplate("shu2.txt");
        copyTemplate("youhu2.txt");
        copyTemplate("youxie2.txt");
        copyTemplate("zuohu2.txt");
        copyTemplate("zuoxie2.txt");
    }

    @Override
    public void startRecognition(PhaseListener listener) {
        Queue<short[]> queue = new LinkedTransferQueue<>();
        if (!isRunning) {
            mPlayerTask.start();
            mRecorderTask.start(queue);
            mPhaseProxy.start(queue, listener);
            isRunning = true;
        }
    }

    @Override
    public void stopRecognition() {
        if (isRunning) {
            mPlayerTask.stop();
            mRecorderTask.stop();
            mPhaseProxy.stop();
            isRunning = false;
        }
    }

    @Override
    public void modifySensitivity(int sensitivity) {
        mPhaseProxy.modifySensitivity(sensitivity);
    }

    /**
     * 拷贝模板数据
     */
    private void copyTemplate(String templateName) {
        FileCopyUtils.copyInAssets(templateName,
                Condition.S_FILE_TEMPLATE_PATH + templateName, mContext);
    }
}
