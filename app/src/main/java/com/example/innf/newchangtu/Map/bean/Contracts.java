package com.example.innf.newchangtu.Map.bean;

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
    private User mContractUser; /*联系人的User对象*/
    private String mPhoneNumber;/*联系人号码*/
    private boolean isShareConnect; /*用户与联系人是否建立了地图共享的联系*/

    public Contracts(){
        mUserName = (String) BmobUser.getObjectByKey("username");
    }

    public boolean isShareConnect() {
        return mContractUser.isShare();
    }

    public void setShareConnect(boolean shareConnect) {
        isShareConnect = shareConnect;
    }

    public User getContractUser() {
        return mContractUser;
    }

    public void setContractUser(User contractUser) {
        mContractUser = contractUser;
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
