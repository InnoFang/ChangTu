package com.example.innf.newchangtu.Map.view.base;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.example.innf.newchangtu.Map.ChangtuThreeReceiver;
import com.example.innf.newchangtu.Map.bean.User;
import com.example.innf.newchangtu.Map.configure.BmobConf;
import com.example.innf.newchangtu.Map.utils.AppCompatActivityCollector;
import com.example.innf.newchangtu.Map.utils.PermissionListener;

import cn.bmob.v3.BmobUser;

/**
 * Author: Inno Fang
 * Time: 2016/8/8 07:39
 * Description: 基类
 */

public class BaseActivity extends AppCompatActivity {

    public User mUser;/*当前用户对象*/
    private ChangtuThreeReceiver mChangtuThreeReceiver;
    private static PermissionListener mListener;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*百度地图SDK初始化*/
        SDKInitializer.initialize(getApplicationContext());

        //初始化Bmob SDK
        BmobConf.Bmobnitialize(this, 30 , 1024*1024, 2500, true);
        /*检查是否具有Bmob当前对象*/
        mUser = BmobUser.getCurrentUser(User.class);
        if (null == mUser){
            mUser = new User();
        }
//        BmobConfig config = new BmobConfig.Builder(this)
//                .setApplicationId(BmobConf.API_KEY) //设置App key
//                .setConnectTimeout(30) // 设置超时时间（单位为秒）：默认15s
//                .setUploadBlockSize(1024*1024) // 文件分片上传时每片的大小（单位字节），默认512*1024
//                .setFileExpiration(2500)//文件的过期时间(单位为秒)：默认1800s
//                .build();
//        Bmob.initialize(config);

        IntentFilter mInflater = new IntentFilter(Intent.ACTION_SCREEN_ON);
        mInflater.addAction(Intent.ACTION_SCREEN_OFF);
        mChangtuThreeReceiver = new ChangtuThreeReceiver();
        registerReceiver(mChangtuThreeReceiver, mInflater);


        AppCompatActivityCollector.addAppCompatActivity(this);
    }

//    public static void requestRuntimePermiussion

    Toast mToast;

    public void showToast(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (mToast == null) {
                mToast = Toast.makeText(getApplicationContext(), text,
                        Toast.LENGTH_SHORT);
            } else {
                mToast.setText(text);
            }
            mToast.show();
        }
    }

    @Override
    protected void onPause() {
        // when the screen is about to turn off
        if (ChangtuThreeReceiver.wasScreenOn) {
            // this is the case when onPause() is called by the system due to a screen state change
            System.out.println("SCREEN TURNED OFF");

        } else {
            // this is when onPause() is called when the screen state has not changed
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        // only when screen turns on
        if (!ChangtuThreeReceiver.wasScreenOn) {
            // this is when onResume() is called due to a screen state change
            Log.i("SCREEN","on");
        } else {
            // this is when onResume() is called when the screen state has not changed
        }
        super.onResume();
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_POWER) {
            // Do something here...
            Log.i("ONKEYDOWN", "ONKEYDOWN");
            event.startTracking(); // Needed to track long presses
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_POWER) {
            // Do something here...
            Log.i("onKeyLongPress", "ONKEYDOWN");
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_POWER) {
            Log.i("dispatchKeyEvent", "ONKEYDOWN");

            return true;
        }

        return super.dispatchKeyEvent(event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppCompatActivityCollector.removeAppCompatActivity(this);
    }
}
