package com.example.innf.newchangtu.Map.model;

import android.content.Context;

import com.example.innf.newchangtu.Map.bean.Nearby;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

/**
 * Author: Inno Fang
 * Time: 2016/8/22 20:03
 * Description:
 */

public class NearbyLab {

    private List<Nearby> mNearbyList;

    private static NearbyLab sNearbyLab;

    private NearbyLab(Context context){
        mNearbyList = new ArrayList<>();
    }

    public static NearbyLab get(Context context){
        if (null == sNearbyLab){
            sNearbyLab = new NearbyLab(context);
        }
        return sNearbyLab;
    }

    public List<Nearby> getNearbyList(){
        return mNearbyList;
    }

    public Nearby getNearby(String id){
        for (Nearby nearby : mNearbyList) {
            if (nearby.getObjectId().equals(id)){
                return nearby;
            }
        }
        return null;
    }

    public void addNearby(Nearby nearby) {
        mNearbyList.add(nearby);
    }

    public void delNearby(Nearby nearby){
        mNearbyList.remove(nearby);
    }

    public static String getNearbyAddress(Nearby mNearby) {
        final String[] address = new String[1];
        BmobQuery<Nearby> query = new BmobQuery<>();
        query.getObject(mNearby.getObjectId(), new QueryListener<Nearby>() {
            @Override
            public void done(Nearby nearby, BmobException e) {
                if (null == e){
                    address[0] = nearby.getAddress();
                }
            }
        });

        return address[0];
    }

}
