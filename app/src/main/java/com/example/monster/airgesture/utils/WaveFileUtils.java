package com.example.monster.airgesture.utils;


import android.os.Environment;

/**
 * Created by Administrator on 2017/5/4.
 */

public class WaveFileUtils {

    public final static String sAbsolutePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/data/files/phase/";  //存放数据的绝对路径


    public void destroy() {

    }

    /**
     * byte数组与short数组转换
     *
     * @param data * @param items
     * @return
     */
    public static short[] byteArray2ShortArray(byte[] data) {
        if (data == null) {
            return null;
        }

        short[] retVal = new short[data.length / 2];
        for (int i = 0; i < retVal.length; i++) {
            retVal[i] = (short) ((data[i * 2] & 0xff) | (data[i * 2 + 1] & 0xff) << 8);
        }

        return retVal;
    }

    public static short max(short[] A, int iLen) {
        short result = A[0];
        for (int i = 0; i < iLen; i++) {
            if (result < A[i]) {
                result = A[i];
            }
        }

        return result;
    }

    public static byte max(byte[] A, int iLen) {
        byte result = A[0];
        for (int i = 0; i < iLen; i++) {
            if (result < A[i]) {
                result = A[i];
            }
        }

        return result;
    }
}
