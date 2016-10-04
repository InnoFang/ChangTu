package com.example.innf.changtu.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.innf.changtu.R;
import com.example.innf.changtu.bean.Contracts;
import com.example.innf.changtu.utils.MyApplication;

import java.util.List;

/**
 * Author: Inno Fang
 * Time: 2016/8/30 15:37
 * Description:
 */

public class ContractsAdapter extends RecyclerView.Adapter<com.example.innf.changtu.adapter.ContractsAdapter.ContractsHolder>{

    private static final String TAG = "ContractsAdapter";

    private List<Contracts> mContractsList;
    private OnItemClickListener mOnItemClickListener;

    public ContractsAdapter(List<Contracts> contractsList) {
        mContractsList = contractsList;
    }

    public void setContractsList(List<Contracts> contractsList) {
        mContractsList = contractsList;
    }


    public void clear(){
        mContractsList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Contracts> elem){
        mContractsList.addAll(elem);
        notifyDataSetChanged();
    }
    public void addAll(List<Contracts> elem, String userName){
        for (Contracts contracts: elem) {
            Log.d(TAG, "addAll: is called" + contracts.getUserName().equals(userName));
            if (contracts.getUserName().equals(userName)){
                mContractsList.add(contracts);
            }
        }
        notifyDataSetChanged();
    }


    @Override
    public ContractsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(MyApplication.getContext());
        View view = layoutInflater.inflate(R.layout.contracts_item, parent, false);
        return new ContractsHolder(view);
    }

    @Override
    public void onBindViewHolder(final ContractsHolder holder, final int position) {
        Contracts contracts = mContractsList.get(position);
        holder.bindContracts(contracts);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.onItemClick(holder.itemView, mContractsList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mContractsList.size();
    }

    class ContractsHolder extends RecyclerView.ViewHolder{

        private Contracts mContracts;

        private TextView mContractNameTextView;

        private TextView mContractPhoneNumberTextView;
        public ContractsHolder(View itemView) {
            super(itemView);
            mContractNameTextView = (TextView) itemView.findViewById(R.id.contracts_name_text_view);
            mContractPhoneNumberTextView = (TextView) itemView.findViewById(R.id.contracts_phone_number_text_view);
        }

        public void bindContracts(Contracts contracts){
            mContracts= contracts;
            mContractNameTextView.setText(mContracts.getName());
            mContractPhoneNumberTextView.setText(mContracts.getPhoneNumber());
        }

    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
       void onItemClick(View view, Contracts contracts);
    }
}
