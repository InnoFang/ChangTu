package com.example.innf.changtu.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.innf.changtu.R;
import com.example.innf.changtu.utils.MyApplication;

import java.util.List;

/**
 * Author: Inno Fang
 * Time: 2016/8/25 16:21
 * Description:
 */

public class TrackAdapter extends RecyclerView.Adapter<com.example.innf.changtu.adapter.TrackAdapter.TrackHolder>{

    private static final String TAG = "TrackAdapter";

    private List<com.example.innf.changtu.bean.Track> mTrackList;
    private OnItemClickListener mOnItemClickListener;


    /*clear data list*/
    public void clear(){
        mTrackList.clear();
        notifyDataSetChanged();
    }
    /*****************/


    public void addAll(List<com.example.innf.changtu.bean.Track> elem){
        mTrackList.addAll(elem);
        notifyDataSetChanged();
        Log.d(TAG, "addAll: is called");
    }

    public void addAll(List<com.example.innf.changtu.bean.Track> elem, String userName){/*中北大学 li*/
        for (com.example.innf.changtu.bean.Track track: elem) {
            if (track.getUserName().equals(userName)){
                Log.d(TAG, "add all is true");
                mTrackList.add(track);
            }
        }
        notifyDataSetChanged();
        Log.d(TAG, "addAll: is called");
    }
    /******************/

    public TrackAdapter(List<com.example.innf.changtu.bean.Track> trackList){
        mTrackList = trackList;
    }

    @Override
    public TrackHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(MyApplication.getContext());
        View view = layoutInflater.inflate(R.layout.track_record_item, parent, false);
        return new TrackHolder(view);
    }

    @Override
    public void onBindViewHolder(final TrackHolder holder, final int position) {
        com.example.innf.changtu.bean.Track track = mTrackList.get(position);
        holder.bindTrack(track);
        if (null != mOnItemClickListener){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick(holder.itemView, mTrackList.get(position));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mTrackList.size();
    }

    public void setTrackList(List<com.example.innf.changtu.bean.Track> trackList){
        mTrackList = trackList;
    }

    class TrackHolder extends RecyclerView.ViewHolder{

        private com.example.innf.changtu.bean.Track mTrack;

        private View mViewUp;
        private ImageView mCircleImageView;
        private View mViewDown;
        private TextView mPlaceTextView;
        private TextView mDateTextView;
        private TextView mTimeTextView;

        public TrackHolder(View itemView) {
            super(itemView);
            mViewUp = itemView.findViewById(R.id.view_up);
            mViewDown = itemView.findViewById(R.id.view_down);
            mCircleImageView = (ImageView) itemView.findViewById(R.id.circle_image_view);
            mPlaceTextView = (TextView) itemView.findViewById(R.id.track_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.date_text_view);
            mTimeTextView = (TextView) itemView.findViewById(R.id.time_text_view);
        }

        public void bindTrack(com.example.innf.changtu.bean.Track track){
            mTrack = track;
            mPlaceTextView.setText(mTrack.getPlace());
            mDateTextView.setText(mTrack.getTrackDate());
            mTimeTextView.setText(mTrack.getTrackTime());
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mOnItemClickListener = onItemClickListener;
    }

    /*item 点击事件*/
    public interface OnItemClickListener{
        void onItemClick(View view, com.example.innf.changtu.bean.Track track);
    }
}
