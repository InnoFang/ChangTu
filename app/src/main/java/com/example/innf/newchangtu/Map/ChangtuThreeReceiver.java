package com.example.innf.newchangtu.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.example.innf.newchangtu.Map.view.activity.MainActivity;

/**
 * Author: Inno Fang
 * Time: 2016/10/2 17:15
 * Description:
 */

public class ChangtuThreeReceiver extends BroadcastReceiver {
    public static boolean wasScreenOn = true;
    static long prevTime = 0;
    static long prev2Time = 0;
    static long currTime = 0;
    static int index = 0;
    private double latitude = 0.0;
    private double longitude = 0.0;

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.i("TGA", "CHECK IN RECIVER WHEN ON");
            wasScreenOn = false;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Log.i("TGA", "CHECK IN RECIVER WHEN OFF");
            wasScreenOn = true;
        }

        if (prevTime == 0) {
            prevTime = System.currentTimeMillis();
            index++;

            Log.i("TGA", "one Clicked power button");
        } else if (index == 1 && ((prev2Time = System.currentTimeMillis()) - prevTime) < 1500) {
            Log.i("TGA", "two  Clicked power button");
            index++;
        } else if (index == 2 && ((currTime = System.currentTimeMillis()) - prev2Time) < 1500) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            ImageView imageView = new ImageView(context);
//            imageView.setImageResource(R.drawable.ct_background);
//            builder.setView(imageView)
//                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//
//                        }
//                    }).show();

            StringBuilder sms = new StringBuilder();

            if (MainActivity.firstPhone != null && MainActivity.firstName != null)
            {
                SmsManager.getDefault().sendTextMessage(MainActivity.firstPhone, null, MainActivity.firstName + ",我现在的位置是" + MainActivity.location + ",我现在无法拨打电话，急要你的帮助", null, null);
                Log.i("TGA", MainActivity.firstName + ",我现在的位置是" + MainActivity.location + ",我现在无法拨打电话，急要你的帮助");
                sms.append(MainActivity.firstName + " ");
            }
            if (MainActivity.secondPhone != null && MainActivity.secondName != null)
            {
                SmsManager.getDefault().sendTextMessage(MainActivity.secondPhone, null, MainActivity.secondName + ",我现在的位置是" + MainActivity.location + ",我现在无法拨打电话，急要你的帮助", null, null);
                Log.i("TGA", MainActivity.secondName + ",我现在的位置是" + MainActivity.location + ",我现在无法拨打电话，急要你的帮助");
                sms.append(MainActivity.secondName + " ");
            }
            if (MainActivity.thirdPhone != null && MainActivity.thirdName != null)
            {
                SmsManager.getDefault().sendTextMessage(MainActivity.thirdPhone, null, MainActivity.thirdName + ",我现在的位置是" + MainActivity.location + ",我现在无法拨打电话，急要你的帮助", null, null);
                Log.i("TGA", MainActivity.thirdName + ",我现在的位置是" + MainActivity.location + ",我现在无法拨打电话，急要你的帮助");
                sms.append(MainActivity.thirdName + " ");
            }

            Toast.makeText(context, "已成功将紧急短信发送给了 " + sms, Toast.LENGTH_LONG).show();
            Log.i("TGA", "已成功将紧急短信发送给了 " + sms);
            index = 0;
            prevTime = 0;
            prev2Time = 0;
            currTime = prev2Time;

        } else {
            index = 0;
            prevTime = 0;
            prev2Time = 0;
            currTime = prev2Time;
        }
    }
}