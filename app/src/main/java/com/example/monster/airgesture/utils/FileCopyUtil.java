package com.example.monster.airgesture.utils;

import android.content.Context;
import android.util.Log;

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

    private static final String DB_PATH = "/data/data/com.example.monster.airgesture/database";

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

    public static boolean databaseCopy(Context context,String dbName){
        File dbFile = new File(DB_PATH + dbName);
        InputStream inputStream = null;
        OutputStream outputStream = null;
        boolean result = false;
        if (!dbFile.exists()) {
            try {
                dbFile.createNewFile();
                inputStream = context.getAssets().open(dbName);
                outputStream = new FileOutputStream(dbFile);
                result = FileCopyUtil.copy(inputStream, outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
        return result;
    }
}
