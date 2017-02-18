package com.example.innf.newchangtu.Map.view.base;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.innf.newchangtu.Map.bean.User;
import com.example.innf.newchangtu.Map.view.activity.ShareMapActivity;
import com.example.innf.newchangtu.R;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Author: Inno Fang
 * Time: 2016/10/6 18:43
 * Description:
 */

public class BaseMainActivity extends BaseActivity {

    private static final String TAG = "BaseMainActivity";

    public User mUser;/*当前用户对象*/
    private boolean isGo = true;

    private NotificationCompat.Builder mBuilder = null;
    private NotificationManager mNotificationManager = null;

    private double[] mLatitudes;
    private double[] mLongitudes;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }

    private void queryShareMapUser() {
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("mIsShare", true);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (null == mBuilder){
                    mBuilder = new NotificationCompat.Builder(getApplicationContext());
                    mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                    mBuilder.setContentTitle("位置共享");
                    int i = 0, j = 0;
                    mLatitudes = new double[list.size()];
                    mLongitudes = new double[list.size()];
                    Log.i(TAG, "done: list size " + list.size());
                    for (User u : list) {
                        if (!u.getObjectId().equals(mUser.getObjectId())){
                            mBuilder.setContentText(u.getObjectId() + " 想与你共享位置");
                            mLatitudes[i++] = u.getLatitude();
                            mLongitudes[j++] = u.getLongitude();
                            Log.i(TAG, u.getObjectId() + "已打开位置共享,经度为：" + u.getLatitude() + ",纬度为：" + u.getLongitude());
                        }
                    }
                    mBuilder.setAutoCancel(true);
                    Intent intent = new Intent(getApplicationContext(), ShareMapActivity.class);

                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(pendingIntent);
                    intent.putExtra("latitudes", mLatitudes);
                    intent.putExtra("longitudes", mLongitudes);
                    mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(0, mBuilder.build());
                }
            }
        });
    }

    private Handler mHandler = new Handler();

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (isGo){
                mHandler.postDelayed(mRunnable, 5 * 1000);
                queryShareMapUser();
            }
        }
    };

}
