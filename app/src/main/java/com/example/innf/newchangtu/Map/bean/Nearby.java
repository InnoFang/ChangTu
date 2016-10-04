package com.example.innf.newchangtu.Map.bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.BmobObject;

/**
 * Author: Inno Fang
 * Time: 2016/8/11 14:36
 * Description:所有“附近”
 */

public class Nearby extends BmobObject implements Serializable{
    private String mContent;
    private String mName;
    private String mAddress;
    private Date mDate;

    private String mCommentsNumber;

    public Nearby(){
        mDate = new Date();
    }

    public String getDate() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ").format(mDate);
    }

    public void setCommentsNumber(String commentsNumber) {
        mCommentsNumber = commentsNumber;
    }

    public String getCommentsNumber() {
        return mCommentsNumber;
    }

    public void setAddress(String address) {
        this.mAddress = address;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public String getContent() {
        return mContent;
    }

    public String getName() {
        return mName;
    }
}