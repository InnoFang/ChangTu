package com.example.innf.newchangtu.Map;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Author: Inno Fang
 * Time: 2016/10/3 10:45
 * Description:
 */

public class MyOrientationListener implements SensorEventListener{

    private SensorManager mSensorManager;
    private Context mContext;
    private Sensor mSensor;
    private OnOrientationListener mOnOrientationListener;

    private float mLastX;

    public MyOrientationListener(Context context) {
        mContext = context;
    }

     @SuppressWarnings("deprecation")
    public void start(){
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null){
            /*获得方向传感器*/
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        }

        if (mSensor != null) {
            mSensorManager.registerListener(this,mSensor,mSensorManager.SENSOR_DELAY_UI);
        }
    }

    public void stop(){
        mSensorManager.unregisterListener(this);

    }

    @SuppressWarnings("deprecation")
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION){
            float x = sensorEvent.values[SensorManager.DATA_X];

            if (Math.abs(x - mLastX) > 1.0){
                if (mOnOrientationListener != null){
                    mOnOrientationListener.onOrientationChanged(x);
                }

            }
            mLastX = x;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void setOnOrientationListener(OnOrientationListener onOrientationListener) {
        mOnOrientationListener = onOrientationListener;
    }

    public interface OnOrientationListener{
        void onOrientationChanged(float x);
    }
}
