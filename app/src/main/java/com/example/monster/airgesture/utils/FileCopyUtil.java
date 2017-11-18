package com.example.monster.airgesture.utils;

import android.content.Context;
import android.util.Log;

import com.example.monster.airgesture.ui.base.BaseApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by WelkinShadow on 2017/10/28.
 */

public class FileCopyUtil {

    public static boolean copy(String src, String des) {
        OutputStream outputStream;
        InputStream inputStream;
        try {
            outputStream = new FileOutputStream(des);
            inputStream = new FileInputStream(src);
            return copy(inputStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean copy(InputStream src, OutputStream des) {
        try {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = src.read(buffer)) > 0) {
                des.write(buffer, 0, length);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (src != null) {
                    src.close();
                }
                if (des != null) {
                    des.close();
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    /**
     * 从Assets下拷贝文件到指定目录
     * @param src assets目录下文件名
     * @param des 需要拷贝到的目录
     */
    public static boolean copyInAssets(String src, String des) {
        Context context = BaseApplication.getContext();
        boolean isSuccessful = false;
        try {
            isSuccessful = copy(context.getAssets().open(src), new FileOutputStream(des));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return isSuccessful;
        }
    }

}
