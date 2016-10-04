package com.example.innf.newchangtu.Map.bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Author: Inno Fang
 * Time: 2016/8/25 16:12
 * Description: Track记录
 */

public class Track extends BmobObject implements Serializable{
    private String mUserName;/*用户*/
    private String mStartPosition; /*开始位置*/
    private String mEndPosition; /*结束位置*/
    private String mDetailTrack; /*出发地和目的地详细地址*/
    private Date mDate;  /*轨迹创建时间*/
    private String mTakeTime;  /*轨迹全程用时*/
    private String mDistance;  /*轨迹全程距离*/
    private String mTransportation;  /*轨迹乘车类型*/
    private String mRemark;/*备注信息*/
    private BmobFile mPhoto;

    public Track(){
        mDate = new Date();
        mUserName = (String) BmobUser.getObjectByKey("username");
    }

    public BmobFile getPhoto() {
        return mPhoto;
    }

    public void setPhoto(BmobFile photo) {
        mPhoto = photo;
    }

    public String getPhotoFilename(Track track){
        return "IMG_" + track.getObjectId() + ".jpg";
    }

    public String getUserName() {
        return mUserName;
    }

    public String getStartPosition() {
        return mStartPosition;
    }

    public String getEndPosition() {
        return mEndPosition;
    }

    public void setStartPosition(String startPosition) {
        mStartPosition = startPosition;
    }

    public void setEndPosition(String endPosition) {
        mEndPosition = endPosition;
    }

    public String getRemark() {
        return mRemark;
    }

    public void setRemark(String remark) {
        mRemark = remark;
    }

    public String getDetailTrack() {
        return getStartPosition() + " - " + getEndPosition();
    }

    public String getTakeTime() {
        return mTakeTime;
    }

    public String getDistance() {
        return mDistance;
    }

    public String getTransportation() {
        return mTransportation;
    }

    public void setTakeTime(String takeTime) {
        mTakeTime = takeTime;
    }

    public void setDistance(String distance) {
        mDistance = distance;
    }

    public void setTransportation(String transportation) {
        mTransportation = transportation;
    }

    public String getTrackDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(mDate);
    }

    public String getTrackTime() {
        return new SimpleDateFormat("HH:mm:ss ").format(mDate);
    }
}
