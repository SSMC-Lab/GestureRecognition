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
        if (isEmpty(text)) {
            return text;
        } else {
            return text.substring(0, 1).toUpperCase() + text.substring(1);
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
            return text.toUpperCase();
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
            return text.toLowerCase();
        }
    }
}
