package com.example.monster.airgesture.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.monster.airgesture.base.BaseApplication;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * SharedPreferences相关的封装
 * Created by WelkinShadow on 2017/12/10.
 */

public class SPUtils {

    private static SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(BaseApplication.getContext());

    /**
     * 依据obj的类型填装
     * @param key
     * @param obj
     */
    public static void put(String key, Object obj){
        SharedPreferences.Editor editor = sp.edit();
        if (obj instanceof String) {
            editor.putString(key, (String) obj);
        } else if (obj instanceof Integer) {
            editor.putInt(key, (Integer) obj);
        } else if (obj instanceof Boolean) {
            editor.putBoolean(key, (Boolean) obj);
        } else if (obj instanceof Float) {
            editor.putFloat(key, (Float) obj);
        } else if (obj instanceof Long) {
            editor.putLong(key, (Long) obj);
        } else {
            editor.putString(key, obj.toString());
        }
        editor.apply();
    }

    /**
     * 依据defaultObject的类型取出
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object get(String key, Object defaultObject){
        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }
        return null;
    }

    /**
     * 查询key是否已经存在
     * @param key
     * @return
     */
    public static boolean contains( String key) {
        return sp.contains(key);
    }


    /**
     * 移除key对应的值
     * @param key
     */
    public static void remove(String key) {
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }

    /**
     * 返回所有的键值对
     *
     * @return
     */
    public static Map<String, ?> getEntry() {
        return sp.getAll();
    }

    /**
     * 返回所有的键的集合
     *
     * @return
     */
    public static Set<String> ketSet() {
        return getEntry().keySet();
    }

    /**
     * 返回所有的值的集合
     *
     * @return
     */
    public static Collection<?> values() {
        return getEntry().values();
    }

    /**
     * 返回所有的值的集合
     *
     * @return
     */
    public static Set<? extends Map.Entry<String, ?>> entrySet() {
        return getEntry().entrySet();
    }

}
