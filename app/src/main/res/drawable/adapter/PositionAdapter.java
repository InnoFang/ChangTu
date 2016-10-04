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
 * Time: 2016/8/24 23:11
 * Description: Position列表适配器
 */

public class PositionAdapter extends RecyclerView.Adapter<com.example.innf.changtu.adapter.PositionAdapter.PositionHolder>{

    private static final String TAG = "PositionAdapter";

    private List<com.example.innf.changtu.bean.Position> mPositionList;

    public PositionAdapter(List<com.example.innf.changtu.bean.Position> positionList) {
        mPositionList = positionList;
    }

    @Override
    public PositionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(MyApplication.getContext());
        View view = layoutInflater.inflate(R.layout.position_item, parent, false);
        return new PositionHolder(view);
    }

    @Override
    public void onBindViewHolder(PositionHolder holder, int position) {
        com.example.innf.changtu.bean.Position position1 = mPositionList.get(position);
        holder.bindPosition(position1);
    }

    @Override
    public int getItemCount() {
        return mPositionList.size();
    }

    public void setPositionList(List<com.example.innf.changtu.bean.Position> positionList){
        mPositionList = positionList;
    }

    class PositionHolder extends RecyclerView.ViewHolder{

        private com.example.innf.changtu.bean.Position mPosition;

        private View mViewUp;
        private ImageView mCircleImageView;
        private View mViewDown;
        private TextView mPositionTextView;
        private TextView mDateTextView;
        private TextView mMessageTextView;

        public PositionHolder(View itemView) {
            super(itemView);
            mViewUp = itemView.findViewById(R.id.view_up);
            mViewDown = itemView.findViewById(R.id.view_down);
            mCircleImageView = (ImageView) itemView.findViewById(R.id.circle_image_view);
            mPositionTextView = (TextView) itemView.findViewById(R.id.position_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.date_text_view);
            mMessageTextView = (TextView) itemView.findViewById(R.id.message_text_view);
        }

        public void bindPosition(com.example.innf.changtu.bean.Position position){
            mPosition = position;
            mPositionTextView.setText(mPosition.getPosition());
            mDateTextView.setText(mPosition.getDate());
            mMessageTextView.setText(mPosition.getMessage());
            Log.d(TAG, "bindPosition: " + mPosition.getMessage());
        }
    }
}
