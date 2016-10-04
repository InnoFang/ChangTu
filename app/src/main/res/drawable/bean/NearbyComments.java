package com.example.innf.changtu.bean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Author: Inno Fang
 * Time: 2016/8/26 12:00
 * Description:
 */

public class NearbyComments extends BmobObject{
    private String mName;
    private String mContent;
    private Date mDate;

    private List<com.example.innf.changtu.bean.NearbyComments> mNearbyCommentsList;

    public NearbyComments(){
        mDate = new Date();
//        mNearbyCommentsList = NearbyCommentsLab.get(MyApplication.getContext()).getNearbyCommentsList();
    }

    public List<com.example.innf.changtu.bean.NearbyComments> getNearbyCommentsList() {
        return mNearbyCommentsList;
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
