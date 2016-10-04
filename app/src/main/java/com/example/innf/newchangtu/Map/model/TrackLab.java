package com.example.innf.newchangtu.Map.model;

import android.content.Context;

import com.example.innf.newchangtu.Map.bean.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Inno Fang
 * Time: 2016/8/25 16:33
 * Description:Track操作类
 */

public class TrackLab {
    private static TrackLab sTrackLab;

    private List<Track> mTrackList;

    public static TrackLab get(Context context){
        if (null == sTrackLab){
            sTrackLab = new TrackLab(context);
        }
        return sTrackLab;
    }

    public TrackLab(Context context) {
        mTrackList = new ArrayList<>();
    }

    public List<Track> getTrackList(){
        return mTrackList;
    }

    public void addTrack(Track track) {
        mTrackList.add(track);
    }

    public void delTrack(Track track){
        mTrackList.remove(track);
    }
}
