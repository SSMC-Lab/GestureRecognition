package com.example.monster.airgesture.phase;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.monster.airgesture.Conditions;
import com.example.monster.airgesture.GlobalConfig;
import com.example.monster.airgesture.PhaseProcessI;
import com.example.monster.airgesture.utils.LogUtils;
import com.example.monster.airgesture.utils.WaveFileUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 解析任务代理类
 * 录音器录制音频数据并存放到{@link GlobalConfig}维护的一个队列中，本实例维护了一个线程来循环读取这个队列
 * 中存放的音频数据，这些音频数据将会递交给native层处理，解析出该某段音频对应的动作数据类型，当这个动作数据
 * 类型合法的时候，该动作类型对应的编号将通过handler发送到主线程中进行处理
 * Created by Administrator on 2017/6/21.
 */

public class PhaseProxy {

    private static PhaseProcessI ppi;
    private ComputeTask mComputeTask;
    private Handler mHandler = null;
    private DataOutputStream recDos;
    private boolean isComputing = false;

    /**
     * 不断把声音数据存放到起来并计算出对应的手势类型
     */
    private class ComputeTask implements Runnable {
        public void run() {
            while (isComputing) {
                saveRecordDataToFile();
            }
        }
    }

    public PhaseProxy() {
        init();
    }

    public PhaseProxy(Handler mHandler) {
        this.mHandler = mHandler;
    }

    public void init() {
        ppi = new PhaseProcessI();
        mComputeTask = new ComputeTask();
        GlobalConfig.bPlayDataReady = true;
    }

    /**
     * 设置主线程用于接收结果的handler
     */
    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    public void dropHandler() {
        this.mHandler = null;
    }

    public void destroy() {
        dropHandler();
        try {
            isComputing = false;
            recDos.flush();
            recDos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动任务
     */
    public void start() {
        isComputing = true;
        if (GlobalConfig.bDataProcessThreadFlag) {
            new Thread(mComputeTask).start();
        }
    }

    /**
     * 该方法计算出手势的具体类型，然后把数据封装到handler中
     */
    private void saveRecordDataToFile() {
        if (GlobalConfig.bPlayDataReady) {
            try {
                //取出录音得到的数据
                byte[] recData = GlobalConfig.getInstance().popByteRecData();
                if (recData != null) {
                    writeBytePcm(recData);
                    short[] shData = WaveFileUtils.byteArray2ShortArray(recData);
                    String sFileName = GlobalConfig.getRecordedFileName("jni");
                    long lBeginTime = System.currentTimeMillis();
                    //在native层计算出手势类型
                    float iType = ppi.doActionRecognitionV3(ppi.nativeSignalProcess, shData,
                            shData.length, GlobalConfig.sFileResultPath, sFileName);
                    long lCostTime = System.currentTimeMillis() - lBeginTime;
                    /*if (iType > 0)
                        LogUtils.d("cost:" + lCostTime + "|type:" + iType);*/
                    //发送结果到主线程中
                    if (mHandler != null && iType > 0.0) {
                        Bundle bundle = new Bundle();
                        bundle.putFloat(Conditions.TYPE, iType);
                        Message message = mHandler.obtainMessage(Conditions.MESSAGE_PHASE_MODEL);
                        message.setData(bundle);
                        mHandler.sendMessage(message);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private void writeBytePcm(byte[] recData) {
        int iReadSize = recData.length;
        if (iReadSize > 0) {
            if (recDos == null) {
                try {
                    recDos = new DataOutputStream(
                            new BufferedOutputStream(
                                    new FileOutputStream(GlobalConfig.fPcmRecordFile2)));
                } catch (FileNotFoundException | NullPointerException e) {
                    LogUtils.e("GlobalConfig.fPcmRecordFile2 is not exits");
                    e.printStackTrace();
                }
            }
            //循环将buffer中的音频数据写入到OutputStream中
            if (recDos != null) {
                for (int i = 0; i < iReadSize; i++) {
                    try {
                        recDos.writeByte(recData[i]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
