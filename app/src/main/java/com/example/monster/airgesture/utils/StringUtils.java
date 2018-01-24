package com.example.monster.airgesture.utils;

/**
 * 字符串相关工具类
 * Created by WelkinShadow on 2017/11/4.
 */

public class StringUtils {

    /**
     * 判断字符串是否为null或长度为0
     *
     * @param s 待校验字符串
     * @return {@code true}: 空 {@code false} 不为空
     */
    public static boolean isEmpty(CharSequence s) {
        return s == null || s.length() == 0;
    }


    /**
     * 首字母大写
     *
     * @param text 待提升字符串
     * @return 首字母大写字符串
     */
    public static String upperFirstLetter(String text) {
        if (isEmpty(text) || !Character.isUpperCase(text.charAt(0))) {
            return text;
        } else {
            char[] textArray = text.toCharArray();
            textArray[0] += 32;
            return String.valueOf(textArray);
        }
    }

    /**
     * 全部大写
     *
     * @param text 待提升字符串
     * @return 大写字符串
     */
    public static String upperText(String text) {
        if (isEmpty(text)) {
            return text;
        } else {
            char[] textArray = text.toCharArray();
            for (int i = 0; i < textArray.length; i++) {
                if (Character.isUpperCase(textArray[i])) {
                    textArray[i] -= 32;
                }
            }
            return String.valueOf(textArray);
        }
    }

    /**
     * 全部小写
     *
     * @param text 待修改字符串
     * @return 小写字符串
     */
    public static String lowerText(String text) {
        if (isEmpty(text)) {
            return text;
        } else {
            char[] textArray = text.toCharArray();
            for (int i = 0; i < textArray.length; i++) {
                if (!Character.isUpperCase(textArray[i])) {
                    textArray[i] += 32;
                }
            }
            return String.valueOf(textArray);
        }
    }
}
