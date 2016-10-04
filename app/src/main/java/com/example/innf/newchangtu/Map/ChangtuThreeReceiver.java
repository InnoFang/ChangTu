package com.example.innf.newchangtu.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.innf.newchangtu.R;

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
    static int index=0;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.i("TGA","CHECK IN RECIVER WHEN ON");
            wasScreenOn = false;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Log.i("TGA","CHECK IN RECIVER WHEN OFF");
            wasScreenOn = true;
        }

        if (prevTime == 0) {
            prevTime = System.currentTimeMillis();
            index++;

            Log.i("TGA", "one Clicked power button");
        }
        else if(index==1&&((prev2Time = System.currentTimeMillis()) - prevTime) < 1500 ){
            Log.i("TGA", "two  Clicked power button");
            index++;
        }

        else if(index==2&&((currTime = System.currentTimeMillis()) - prev2Time) < 1500 ){
            AlertDialog.Builder builder= new AlertDialog.Builder(context);
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(R.drawable.changtu_back);
            builder.setView(imageView)
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).show();
            Toast.makeText(context, "three Clicked power button",
                    Toast.LENGTH_LONG).show();
            Log.i("TGA", "three Clicked power button");
            index=0;
            prevTime = 0;
            prev2Time = 0;
            currTime = prev2Time;



        }else {
            index=0;
            prevTime = 0;
            prev2Time = 0;
            currTime = prev2Time;

        }

    }
}