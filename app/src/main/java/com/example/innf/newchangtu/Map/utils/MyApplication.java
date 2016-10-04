package com.example.innf.newchangtu.Map.utils;

import android.app.Application;
import android.content.Context;

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
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
