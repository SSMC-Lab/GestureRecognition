package com.example.monster.airgesture.phase;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.monster.airgesture.Conditions;

import com.example.monster.airgesture.PhaseProcessI;
import com.example.monster.airgesture.utils.LogUtils;

/**
 * 录音数据解析处理模块(native层{@link PhaseProxy#ppi})代理
 */

public class PhaseProxy {
    private PhaseProcessI ppi;
    private IPhaseBiz.PhaseListener mListener;
    private Handler mHandler;
    private boolean isReadyRunning;
    private SyncLinkedList<short[]> mQueue;

    PhaseProxy() {
        ppi = new PhaseProcessI();
        ppi.doInit(ppi.nativeSignalProcess, Conditions.sFileTemplatePath);

        isReadyRunning = true;
    }

    public void start(IPhaseBiz.PhaseListener listener, SyncLinkedList<short[]> Queue) {
        this.mListener = listener;
        this.mQueue = Queue;
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == Conditions.MESSAGE_PHASE_MODEL) {
                    mListener.receiveActionType(msg.getData().getFloat(Conditions.TYPE));
                }
                return true;
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isReadyRunning) {
                    saveRecordDataToFile();
                }
            }
        }).start();

    }

    public void stop() {
        isReadyRunning = false;
        mHandler = null;
    }

    private void saveRecordDataToFile() {
        short[] recData = mQueue.pop(true);
        if (recData != null) {
            String sFileName = Conditions.getRecordedFileName("jni");
            float iType = ppi.doActionRecognitionV3(ppi.nativeSignalProcess, recData, recData.length,
                    Conditions.sFileResultPath, sFileName);
            if (iType > 0.0f) {
                Bundle bundle = new Bundle();
                bundle.putFloat(Conditions.TYPE, iType);
                Message message = Message.obtain(mHandler, Conditions.MESSAGE_PHASE_MODEL);
                message.setData(bundle);
                message.sendToTarget();
                LogUtils.d("type:" + iType);
            }
        }
    }
}
