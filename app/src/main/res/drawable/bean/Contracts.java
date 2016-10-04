package com.example.innf.changtu.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

/**
 * Author: Inno Fang
 * Time: 2016/8/30 15:18
 * Description:
 */

public class Contracts extends BmobObject implements Serializable{
    private String mUserName;/*用户*/
    private String mName;/*联系人名字*/
    private String mPhoneNumber;/*联系人号码*/

    public Contracts(){
       mUserName = (String) BmobUser.getObjectByKey("username");
    }

    public String getUserName() {
        return mUserName;
    }

    public String getName() {
        return mName;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }
}
