package com.example.innf.newchangtu.Map.bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author: Inno Fang
 * Time: 2016/8/24 23:04
 * Description: 主界面位置显示
 */

public class Position implements Serializable{
    private String mPosition;
    private Date mDate;
    private String mMessage;
    private double mLatitude;
    private double mLongitude;

    public Position(){
        mDate = new Date();
    }

    public void setPosition(String position) {
        mPosition = position;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public String getPosition() {
        return "当前的位置: " + mPosition;
    }
    public String getEndPosition() {
            return mPosition;
    }


    public String getMessage() {
        return mMessage;
    }

    public String getDate() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ").format(mDate);
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeString(mPosition);
//        parcel.writeSerializable(mDate);
//        parcel.writeString(mMessage);
//        parcel.writeDouble(mLatitude);
//        parcel.writeDouble(mLongitude);
//    }
//
//    public static final Parcelable.Creator<Position> CREATOR = new Parcelable.Creator<Position>(){
//
//        @Override
//        public Position createFromParcel(Parcel parcel) {
//            Position position = new Position();
//            position.mPosition = parcel.readString();
//            position.mDate = (Date) parcel.readSerializable();
//            position.mMessage = parcel.readString();
//            position.mLatitude = parcel.readDouble();
//            position.mLongitude = parcel.readDouble();
//            return position;
//        }
//
//        @Override
//        public Position[] newArray(int size) {
//            return new Position[size];
//        }
//    };
}
