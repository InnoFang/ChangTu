package com.example.innf.newchangtu.Map;

import android.app.Application;
import android.content.Context;

import com.example.innf.newchangtu.Map.utils.CrashHandler;

/**
 * Author: Inno Fang
 * Time: 2016/8/6 15:17
 * Description:全局Context
 */

public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        CrashHandler.getInstance().init(getApplicationContext());
    }

    public static Context getContext() {
        return context;
    }
}
