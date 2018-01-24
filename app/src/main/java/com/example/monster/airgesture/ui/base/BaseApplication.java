package com.example.monster.airgesture.ui.base;

import android.content.Context;

import org.litepal.LitePalApplication;

/**
 * Created by WelkinShadow on 2017/11/15.
 */

public class BaseApplication extends LitePalApplication {
    private static BaseApplication mApplication;

    /**
     * 获取Context
     */
    public static Context getContext(){
        return mApplication.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.mApplication = this;
    }
}
