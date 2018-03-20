package com.example.monster.airgesture.phase;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.monster.airgesture.GlobalConfig;

import com.example.monster.airgesture.PhaseProcessI;
import com.example.monster.airgesture.R;
import com.example.monster.airgesture.base.BaseApplication;
import com.example.monster.airgesture.utils.SPUtils;

import java.util.Queue;

/**
 * 录音数据解析处理模块(native层{@link PhaseProxy#ppi})代理
 */

public class PhaseProxy {
    private PhaseProcessI ppi;
    private IPhaseBiz.PhaseListener mListener;
    private Handler mHandler;
    private boolean isReadyRunning;
    private Queue<short[]> mQueue;

    PhaseProxy() {
        ppi = new PhaseProcessI();
        ppi.doInit(ppi.nativeSignalProcess, GlobalConfig.sFileTemplatePath);
    }

    public void start(Queue<short[]> queue, IPhaseBiz.PhaseListener listener) {
        this.mListener = listener;
        this.mQueue = queue;
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == GlobalConfig.MESSAGE_PHASE_MODEL) {
                    mListener.receiveActionType(msg.getData().getFloat(GlobalConfig.TYPE));
                }
                return true;
            }
        });
        isReadyRunning = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                saveRecordDataToFile();
            }
        }).start();

    }

    public void stop() {
        isReadyRunning = false;
    }

    private void saveRecordDataToFile() {
        while (isReadyRunning) {
            short[] recData = mQueue.poll();
            if (recData != null) {
                int sensitive = (int) SPUtils.get(BaseApplication.getContext().getString(R.string.key_sensibility), 60);
                //LogUtils.d("recData len in PhaseProxy : " + recData.length);
                String sFileName = GlobalConfig.getRecordedFileName("jni");
                float iType = ppi.doActionRecognitionV3(ppi.nativeSignalProcess, recData, recData.length,
                        GlobalConfig.sFileResultPath, sFileName, sensitive);
                if (iType > 0.0f) {
                    Bundle bundle = new Bundle();
                    bundle.putFloat(GlobalConfig.TYPE, iType);
                    Message message = Message.obtain(mHandler, GlobalConfig.MESSAGE_PHASE_MODEL);
                    message.setData(bundle);
                    message.sendToTarget();
                }
            }
        }
    }
}
