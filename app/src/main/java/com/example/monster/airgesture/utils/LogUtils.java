package com.example.monster.airgesture.utils;

import android.util.Log;

/**
 * 日志相关工具类
 * Created by WelkinShadow on 2017/11/19.
 */

public class LogUtils {

    private static boolean LOGV = true; //是否显示用LogUtil打印出的Verbose日志
    private static boolean LOGI = true; //是否显示用LogUtil打印出的Info日志
    private static boolean LOGD = true; //是否显示用LogUtil打印出的Debug日志
    private static boolean LOGW = true; //是否显示用LogUtil打印出的Warning日志
    private static boolean LOGE = true; //是否显示用LogUtil打印出的Error日志

    private static String getTag() {
        StackTraceElement[] trace = new Throwable().getStackTrace();
        String callingClass = "";
        for (int i = 2, len = trace.length; i < len; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(LogUtils.class)) {
                callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
                break;
            }
        }
        return callingClass;
    }

    /**
     * Verbose日志
     *
     * @param msg 消息
     */
    public static void v(String msg) {
        v(getTag(), msg);
    }

    /**
     * Verbose日志
     *
     * @param tag 标签
     * @param msg 消息
     */
    public static void v(String tag, String msg) {
        if (LOGV)
            Log.v(tag,msg);
    }

    /**
     * Info日志
     *
     * @param msg 消息
     */
    public static void i(String msg) {
        i(getTag(), msg);
    }

    /**
     * Info日志
     *
     * @param tag 标签
     * @param msg 消息
     */
    public static void i(String tag, String msg) {
        if (LOGI)
            Log.i(tag,msg);
    }

    /**
     * Debug日志
     *
     * @param msg 消息
     */
    public static void d(String msg) {
        d(getTag(), msg);
    }

    /**
     * Debug日志
     *
     * @param tag 标签
     * @param msg 消息
     */
    public static void d(String tag, String msg) {
        if (LOGD)
            Log.d(tag,msg);
    }

    /**
     * Warning日志
     *
     * @param msg 消息
     */
    public static void w(String msg) {
        w(getTag(), msg);
    }

    /**
     * Warning日志
     *
     * @param tag 标签
     * @param msg 消息
     */
    public static void w(String tag, String msg) {
        if (LOGW)
            Log.w(tag,msg);
    }

    /**
     * Error日志
     *
     * @param msg 消息
     */
    public static void e(String msg) {
        e(getTag(), msg);
    }

    /**
     * Error日志
     *
     * @param tag 标签
     * @param msg 消息
     */
    public static void e(String tag, String msg) {
        if (LOGE)
            Log.e(tag,msg);
    }
}
