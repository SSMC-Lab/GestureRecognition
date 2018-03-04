package com.example.monster.airgesture;

/**
 * Created by bostinshi on 2017/7/27.
 *
 * 不要改变这个类的位置，否则会报错
 */

public class PhaseProcessI {

    static {
        System.loadLibrary("PhaseProcess");  //导入C代码生成的二进制库
    }

    //保存c++类的地址
    public long nativePerson;
    public long nativeSignalProcess;

    //构造函数
    public PhaseProcessI() {
        nativeSignalProcess = createNativeSignalProcess();
    }

    public native String getJniString(); //去PhaseProcess.cpp文件中找出对应的方法实现

    public native long createNativeRangeFinder(int inMaxFramesPerSlice, int inNumFreq, float inStartFreq, float inFreqInterv);

    public native float getDistanceChange(long thizptr, short[] recordData, int size);

    public native float[] getBaseBand(long thizptr, int inNumFreq);

    public native long createNativeSignalProcess();

    public native float doActionRecognition(long thizptr, float[] recordData, int iLen);

    public native float[] doActionRecognitionV2(long thizptr, float[] recordData, int iLen, String sPath, String sName);

    public native float doActionRecognitionV3(long thizptr, short[] recordData, int iLen, String sPath, String sName);

    public native float doInit(long thizptr, String sPath);
}
