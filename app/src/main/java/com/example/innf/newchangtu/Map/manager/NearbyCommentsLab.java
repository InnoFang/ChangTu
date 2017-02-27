package com.example.innf.newchangtu.Map.manager;

import android.content.Context;

import com.example.innf.newchangtu.Map.bean.NearbyComments;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Inno Fang
 * Time: 2016/8/26 12:30
 * Description:
 */

public class NearbyCommentsLab {
    private static NearbyCommentsLab sNearbyCommentsLab;
    private List<NearbyComments> mNearbyCommentsList;

    public List<NearbyComments> getNearbyCommentsList() {
        return mNearbyCommentsList;
    }

    public void setNearbyCommentsList(List<NearbyComments> nearbyCommentsList) {
        mNearbyCommentsList = nearbyCommentsList;
    }

    private NearbyCommentsLab(Context context) {
        mNearbyCommentsList = new ArrayList<>();
//        testList();
    }

    public static NearbyCommentsLab get(Context context){
        if (null == sNearbyCommentsLab){
            sNearbyCommentsLab = new NearbyCommentsLab(context);
        }
        return sNearbyCommentsLab;
    }

    public void testList(){
        NearbyComments nearbyComments = new NearbyComments();
        nearbyComments.setName("Inno");
        nearbyComments.setContent("你在太原南站可以坐849，到胜利桥东，然后坐835到中北大学");
        mNearbyCommentsList.add(nearbyComments);

        NearbyComments nearbyComments1 = new NearbyComments();
        nearbyComments1.setName("yang");
        nearbyComments1.setContent("你也可以先乘坐868，然后换乘849到桥东，再坐835");
        mNearbyCommentsList.add(nearbyComments1);
    }
}
