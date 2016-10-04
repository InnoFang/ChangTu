package com.example.innf.newchangtu.Map.utils;

import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Inno Fang
 * Time: 2016/8/8 13:57
 * Description:活动收集器，便于一键退出
 */

public class AppCompatActivityCollector {

    public static List<AppCompatActivity> sAppCompatActivities = new ArrayList<>();

    public static void addAppCompatActivity(AppCompatActivity appCompatActivity){
        sAppCompatActivities.add(appCompatActivity);
    }

    public static void removeAppCompatActivity(AppCompatActivity appCompatActivity){
        sAppCompatActivities.remove(appCompatActivity);
    }

    public static void finishAll(){
        for (AppCompatActivity appCompatActivity: sAppCompatActivities) {
            if (!appCompatActivity.isFinishing()){
                appCompatActivity.finish();
            }
        }
    }
}
