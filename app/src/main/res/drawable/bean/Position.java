package com.example.innf.changtu.bean;

import java.util.Date;

/**
 * Author: Inno Fang
 * Time: 2016/8/24 23:04
 * Description:
 */

public class Position {
    private String mPosition;
    private Date mDate;
    private String mMessage;

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
        return mPosition;
    }

    public String getMessage() {
        return mMessage;
    }/*中北大学  柳巷 张三*/

    public String getDate() {
        return " ";
//        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ").format(mDate);
    }

}
