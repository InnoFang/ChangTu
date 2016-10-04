package com.example.innf.changtu.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.innf.changtu.R;
import com.example.innf.changtu.utils.MyApplication;

import java.util.List;

/**
 * Author: Inno Fang
 * Time: 2016/8/26 12:00
 * Description:
 */

public class NearbyCommentsAdapter extends RecyclerView.Adapter<com.example.innf.changtu.adapter.NearbyCommentsAdapter.NearbyCommentsHolder>{

    private List<com.example.innf.changtu.bean.NearbyComments> mNearbyCommentsList;

    public void setNearbyCommentsList(List<com.example.innf.changtu.bean.NearbyComments> nearbyCommentsList) {
        mNearbyCommentsList = nearbyCommentsList;
    }

    public NearbyCommentsAdapter(List<com.example.innf.changtu.bean.NearbyComments> nearbyCommentsList) {
        mNearbyCommentsList = nearbyCommentsList;
    }

    @Override
    public NearbyCommentsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(MyApplication.getContext());
        View view  = layoutInflater.inflate(R.layout.nearby_comments_item, parent, false);
        return new NearbyCommentsHolder(view);
    }

    @Override
    public void onBindViewHolder(NearbyCommentsHolder holder, int position) {
        com.example.innf.changtu.bean.NearbyComments nearbyComments = mNearbyCommentsList.get(position);
        holder.bindNearbyComments(nearbyComments);
    }

    @Override
    public int getItemCount() {
        return mNearbyCommentsList.size();
    }



    class NearbyCommentsHolder extends RecyclerView.ViewHolder{

        private com.example.innf.changtu.bean.NearbyComments mNearbyComments;

        private TextView mNameTextView;
        private TextView mContentTextView;
        private TextView mDateTextView;

        public NearbyCommentsHolder(View itemView) {
            super(itemView);
            mNameTextView = (TextView) itemView.findViewById(R.id.nearby_comments_name_text_view);
            mContentTextView = (TextView) itemView.findViewById(R.id.nearby_comments_content_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.nearby_comments_date_text_view);
        }

        public void bindNearbyComments(com.example.innf.changtu.bean.NearbyComments nearbyComments){
            mNearbyComments = nearbyComments;
            mNameTextView.setText(mNearbyComments.getName());
            mContentTextView.setText(mNearbyComments.getContent());
            mDateTextView.setText(mNearbyComments.getDate());
        }
    }
}