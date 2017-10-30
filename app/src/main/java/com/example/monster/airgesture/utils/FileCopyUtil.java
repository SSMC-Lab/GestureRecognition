package com.example.monster.airgesture.utils;

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
        try {
            OutputStream outputStream = new FileOutputStream(des);
            InputStream inputStream = new FileInputStream(src);
            boolean result = copy(inputStream,outputStream);
            inputStream.close();
            outputStream.close();
            return result;
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
        }
    }
}
