package com.example.innf.changtu.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.innf.changtu.R;
import com.example.innf.changtu.bean.Nearby;
import com.example.innf.changtu.utils.MyApplication;

import java.util.List;

/**
 * Author: Inno Fang
 * Time: 2016/8/22 13:01
 * Description: Nearby列表的适配器
 */

public class NearbyAdapter extends RecyclerView.Adapter<com.example.innf.changtu.adapter.NearbyAdapter.NearbyHolder> {
    private static final String TAG = "NearbyAdapter";

//    private NearbyActivity mNearbyActivity = NearbyActivity.get();

    private List<Nearby> mNearbyList;


    private OnItemClickListener mOnItemClickListener;

    /*clear data list*/
    public void clear(){
        mNearbyList.clear();
        notifyDataSetChanged();
    }
    /*****************/

    /*add all list data*/
    public void addAll(List<Nearby> elem, String address){
        for (Nearby nearby: elem) {
            Log.d(TAG, "addAll: is called " );
//            Toast.makeText(MyApplication.getContext(), nearby.getAddress() + " & " + address + " : " + nearby.getAddress().equals(address), Toast.LENGTH_LONG).show();
            if (nearby.getAddress().equals(address)){
                mNearbyList.add(nearby);
            }
            Log.d(TAG, "addAll: called twice");
        }
        notifyDataSetChanged();
    }
    public void addAll(List<Nearby> elem){
        mNearbyList.addAll(elem);
        notifyDataSetChanged();
    }
    /******************/


    public NearbyAdapter(List<Nearby> nearbyList) {
        mNearbyList = nearbyList;
    }

    @Override
    public NearbyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(MyApplication.getContext());
        View view = layoutInflater.inflate(R.layout.nearby_item, parent, false);
        return new NearbyHolder(view);
    }

    @Override
    public void onBindViewHolder(final NearbyHolder holder, final int position) {
        Nearby nearby = mNearbyList.get(position);
        holder.bindNearby(nearby);
        if (null != mOnItemClickListener){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick(holder.itemView, mNearbyList.get(position));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mNearbyList.size();
    }

    public void setNearbyList(List<Nearby> nearbyList){
        mNearbyList = nearbyList;
    }

    class NearbyHolder extends RecyclerView.ViewHolder{

        private Nearby mNearby;

        public TextView mNameTextView;
        private TextView mContentTextView;
        private TextView mCommentsNumberTextView;
        private TextView mDateTextView;

        public NearbyHolder(View itemView) {
            super(itemView);
            mNameTextView = (TextView) itemView.findViewById(R.id.item_author);
            mContentTextView = (TextView) itemView.findViewById(R.id.item_content);
            mCommentsNumberTextView = (TextView) itemView.findViewById(R.id.item_comments);
            mDateTextView = (TextView) itemView.findViewById(R.id.item_date);
        }

        public void bindNearby(Nearby nearby){
            mNearby = nearby;
            mNameTextView.setText(mNearby.getName());
            mDateTextView.setText(mNearby.getDate());
            mContentTextView.setText(mNearby.getContent());
            mCommentsNumberTextView.setText(mNearby.getCommentsNumber());
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener{
        void onItemClick(View view, Nearby nearby);
    }
}