package com.example.innf.newchangtu.Map.bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.BmobUser;

/**
 * Author: Inno Fang
 * Time: 2016/8/26 12:00
 * Description:
 */

public class NearbyComments implements Serializable{
    private String mName;
    private String mContent;
    private Date mDate;
    public NearbyComments(){
        mDate = new Date();
        mName = (String) BmobUser.getObjectByKey("username");
    }

    public void setContent(String content) {
        mContent = content;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public String getContent() {
        return mContent;
    }

    public String getDate() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ").format(mDate);
    }
}
