package com.example.monster.airgesture.ui.base;

import android.app.Application;
import android.content.Context;

/**
 * Created by WelkinShadow on 2017/11/15.
 */

public class BaseApplication extends Application {
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
