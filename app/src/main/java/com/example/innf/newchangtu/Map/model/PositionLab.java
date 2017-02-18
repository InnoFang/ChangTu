package com.example.innf.newchangtu.Map.model;

import android.content.Context;

import com.example.innf.newchangtu.Map.bean.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Inno Fang
 * Time: 2016/8/24 23:48
 * Description: Position操作类
 */

public class PositionLab {
    private static PositionLab sPositionLab;

    private List<Position> mPositionList;

    public static PositionLab get(Context context){
        if (null == sPositionLab){
            sPositionLab = new PositionLab(context);
        }
        return sPositionLab;
    }

    private PositionLab(Context context){
//        final Resources res = Resources.getSystem();
        mPositionList = new ArrayList<>();
//        testList();
    }

    public List<Position> getPositionList(){
        return mPositionList;
    }

    public void setPositionList(List<Position> positionList) {
        mPositionList = positionList;
    }

    public void addPosition(Position position){
        mPositionList.add(position);
    }

    public void delPosition(Position position){
        mPositionList.remove(position);
    }

    /*添加测试列表*/
    public void testList(){
        Position first = new Position();
        first.setPosition("你当前的位置：尖草坪区南环路");
        first.setMessage("距离上一个目的地1公里，用时5分钟");
        mPositionList.add(first);

        Position second = new Position();
        second.setPosition("你当前的位置：尖草坪区西环路");
        second.setMessage("距离上一个目的地1公里，用时5分钟");
        mPositionList.add(second);

        Position third = new Position();
        third.setPosition("你当前的位置：尖草坪区中北大学中央大道");
        third.setMessage("距离上一个目的地1公里，用时5分钟");
        mPositionList.add(third);
//        for (int i = 1; i <= 20; i++) {
//            Position position4 = new Position();
//            position4.setPosition("你当前的位置：太原" + i + "路");
//            position4.setMessage("距离上一个目的地" + i + "公里，用时" + i + "个小时");
//            mPositionList.add(position4);
//        }

    }
    /************/

}
