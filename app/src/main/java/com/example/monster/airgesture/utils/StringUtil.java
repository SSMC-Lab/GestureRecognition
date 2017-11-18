package com.example.monster.airgesture.utils;

/**
 * 大小写转换工具
 * Created by WelkinShadow on 2017/11/4.
 */

public class StringUtil {
    /**
     * 首字母大写
     */
    public static String transformCapsFirst(String text) {
        if (text != null && text.length() > 0) {
            char[] textArray = text.toCharArray();
            if (textArray[0] >= 'a' && textArray[0] <= 'z') {
                textArray[0] -= 32;
            }
            return String.valueOf(textArray);
        } else {
            return "";
        }
    }

    /**
     * 全部大写
     */
    public static String transformCapsAll(String text) {
        if (text != null && text.length() > 0) {
            char[] textArray = text.toCharArray();
            for (int i = 0;i<textArray.length;i++){
                if (textArray[i] >= 'a' && textArray[i] <= 'z') {
                    textArray[i] -= 32;
                }
            }
            return String.valueOf(textArray);
        } else {
            return "";
        }
    }

    /**
     * 全部小写
     */
    public static String transformNoCapsAll(String text){
        if (text != null && text.length() > 0) {
            char[] textArray = text.toCharArray();
            for (int i = 0;i<textArray.length;i++){
                if (textArray[i] >= 'A' && textArray[i] <= 'Z') {
                    textArray[i] += 32;
                }
            }
            return String.valueOf(textArray);
        } else {
            return "";
        }
    }
}
