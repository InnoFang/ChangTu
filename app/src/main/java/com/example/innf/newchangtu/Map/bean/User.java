package com.example.innf.newchangtu.Map.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobUser;

/**
 * Author: Inno Fang
 * Time: 2016/7/20 17:54
 * Description: 畅途用户模型层
 */

public class User extends BmobUser implements Serializable{

    private String name;
    private Integer age;
    private String address;
    private String gender;

    private double mLatitude;
    private double mLongitude;

    private boolean mIsShare;

    public boolean isShare() {
        return mIsShare;
    }

    public void setShare(boolean share) {
        mIsShare = share;
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

    /******紧急联系人一，二，三*******/
    private String mFirstContractName;
    private String mSecondContractName;
    private String mThirdContractName;

    private String mFirstContractPhone;
    private String mSecondContractPhone;
    private String mThirdContractPhone;
    /********************************/

    public String getFirstContractName() {
        return mFirstContractName;
    }

    public String getSecondContractName() {
        return mSecondContractName;
    }

    public String getThirdContractName() {
        return mThirdContractName;
    }

    public String getSecondContractPhone() {
        return mSecondContractPhone;
    }

    public String getFirstContractPhone() {
        return mFirstContractPhone;
    }

    public String getThirdContractPhone() {
        return mThirdContractPhone;
    }

    public void setFirstContractName(String firstContractName) {
        mFirstContractName = firstContractName;
    }

    public void setSecondContractName(String secondContractName) {
        mSecondContractName = secondContractName;
    }

    public void setThirdContractName(String thirdContractName) {
        mThirdContractName = thirdContractName;
    }

    public void setFirstContractPhone(String firstContractPhone) {
        mFirstContractPhone = firstContractPhone;
    }

    public void setSecondContractPhone(String secondContractPhone) {
        mSecondContractPhone = secondContractPhone;
    }

    public void setThirdContractPhone(String thirdContractPhone) {
        mThirdContractPhone = thirdContractPhone;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public String getAddress() {
        return address;
    }

    public String getGender() {
        return gender;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof User))
            return false;
        User other = (User) obj;
        if (address == null) {
            if (other.address != null)
                return false;
        } else if (!address.equals(other.address))
            return false;
        if (age == null) {
            if (other.age != null)
                return false;
        } else if (!age.equals(other.age))
            return false;
        if (gender == null) {
            if (other.gender != null)
                return false;
        } else if (!gender.equals(other.gender))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
