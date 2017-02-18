package com.example.innf.newchangtu.Map.bean;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
    private Date mDate;  /*轨迹创建时间*/
    private String mEndDate;  /*轨迹结束时间*/
    private String mDistance;  /*轨迹全程距离*/
    private String mTransportation;  /*轨迹乘车类型*/
    private String mRemark;/*备注信息*/
    private BmobFile mPhoto;    /*照片揭露*/
    private List<Position> mPositionList;   /*显示保存的足迹记录*/
    private String mContracts;  /*联系人姓名*/
    private String mPhone;  /*联系人电话*/
    private int mTimeInterval;

    public Track(){
        mDate = new Date();
        mUserName = (String) BmobUser.getObjectByKey("username");
    }

    public int getTimeInterval() {
        return mTimeInterval;
    }

    public void setTimeInterval(int timeInterval) {
        mTimeInterval = timeInterval;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public String getContracts() {
        return mContracts;
    }

    public void setContracts(String contracts) {
        mContracts = contracts;
    }

    public List<Position> getPositionList() {
        return mPositionList;
    }

    public void setPositionList(List<Position> positionList) {
        mPositionList = positionList;
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

    public String getEndDate() {
        return getPositionList().get(getPositionList().size() - 1).getDate();
    }

    public String getDistance() {
        Position start = getPositionList().get(0);
        Position end = getPositionList().get(getPositionList().size() - 1);
        LatLng startLatLng = new LatLng(start.getLatitude(), start.getLongitude());
        LatLng endLatLng = new LatLng(end.getLatitude(), end.getLongitude());
        double dis = DistanceUtil.getDistance(startLatLng, endLatLng);
        int distance = (int) dis;
        return distance + "米";
    }

    public String getTransportation() {
        return mTransportation;
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
