package com.example.innf.newchangtu.Map.configure;

import android.content.Context;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;

/**
 * Author: Inno Fang
 * Time: 2016/7/21 17:02
 * Description: 存储Bmob的API KEY以及Bmob初始化
 */

public class BmobConf {

    public static final String BMOB_APP_ID = "8082ab4c99e8d4493580be6a7fbfa33d";

    //初始化Bmob SDK
    public static void Bmobnitialize(Context context, long connectTimeout, int blockSize, long expiration, boolean configBoolean) {
        //第一：默认初始化
        Bmob.initialize(context, BmobConf.BMOB_APP_ID);

        if (configBoolean){
            //第二：自v3.4.7版本开始,设置BmobConfig,允许设置请求超时时间、文件分片上传时每片的大小、文件的过期时间(单位为秒)，
            BmobConfig config =new BmobConfig.Builder(context)
                    //设置appkey
                    .setApplicationId(BmobConf.BMOB_APP_ID)
                    //请求超时时间（单位为秒）：默认15s
                    .setConnectTimeout(connectTimeout)
                    //文件分片上传时每片的大小（单位字节），默认512*1024
                    .setUploadBlockSize(blockSize)
                    //文件的过期时间(单位为秒)：默认1800s
                    .setFileExpiration(expiration)
                    .build();
            Bmob.initialize(config);
        }
    }

}
