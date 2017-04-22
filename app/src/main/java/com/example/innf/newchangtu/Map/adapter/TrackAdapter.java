package com.example.innf.newchangtu.Map.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.innf.newchangtu.Map.bean.Track;
import com.example.innf.newchangtu.Map.MyApplication;
import com.example.innf.newchangtu.R;

import java.util.List;

/**
 * Author: Inno Fang
 * Time: 2016/8/25 16:21
 * Description:
 */

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackHolder>{

    private static final String TAG = "TrackAdapter";

    private List<Track> mTrackList;
    private OnItemClickListener mOnItemClickListener;

    /*clear data list*/
    public void clear(){
        mTrackList.clear();
        notifyDataSetChanged();
    }
    /*****************/


    public void addAll(List<Track> elem){
        mTrackList.addAll(elem);
        notifyDataSetChanged();
        Log.d(TAG, "addAll: is called");
    }

    public void addAll(List<Track> elem, String userName){/*中北大学 li*/
        for (Track track: elem) {
            if (track.getUserName().equals(userName)){
                Log.d(TAG, "add all is true");
                mTrackList.add(track);
            }
        }
        notifyDataSetChanged();
        Log.d(TAG, "addAll: is called");
    }
    /******************/

    public TrackAdapter(List<Track> trackList){
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
        Track track = mTrackList.get(position);
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

    public void setTrackList(List<Track> trackList){
        mTrackList = trackList;
    }

    class TrackHolder extends RecyclerView.ViewHolder{

        private Track mTrack;

        private View mViewUp;
        private ImageView mCircleImageView;
        private View mViewDown;
        private TextView mStartPositionTextView;
        private TextView mEndPositionTextView;
        private TextView mDateTextView;
        private TextView mTimeTextView;

        public TrackHolder(View itemView) {
            super(itemView);
            mViewUp = itemView.findViewById(R.id.view_up);
            mViewDown = itemView.findViewById(R.id.view_down);
            mCircleImageView = (ImageView) itemView.findViewById(R.id.circle_image_view);
            mStartPositionTextView = (TextView) itemView.findViewById(R.id.start_position_text_view);
            mEndPositionTextView = (TextView) itemView.findViewById(R.id.end_position_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.date_text_view);
            mTimeTextView = (TextView) itemView.findViewById(R.id.time_text_view);
        }

        public void bindTrack(Track track){
            mTrack = track;
            mStartPositionTextView.setText(mTrack.getStartPosition());
            mEndPositionTextView.setText(mTrack.getEndPosition());
            mDateTextView.setText(mTrack.getTrackDate());
            mTimeTextView.setText(mTrack.getTrackTime());
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mOnItemClickListener = onItemClickListener;
    }

    /*item 点击事件*/
    public interface OnItemClickListener{
        void onItemClick(View view, Track track);
    }
}
