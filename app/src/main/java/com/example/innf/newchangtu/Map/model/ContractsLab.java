package com.example.innf.newchangtu.Map.model;

import android.content.Context;

import com.example.innf.newchangtu.Map.bean.Contracts;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Inno Fang
 * Time: 2016/8/30 15:19
 * Description:
 */

public class ContractsLab {
    private List<Contracts> mContractsList;
    private static ContractsLab sContractsLab;

    private ContractsLab(Context context) {
        mContractsList = new ArrayList<>();
    }

    public static ContractsLab get(Context context){
        if (null == sContractsLab){
            sContractsLab = new ContractsLab(context);
        }
        return sContractsLab;
    }

    /*查找是否添加了相同联系人*/
//    private  boolean mResult;
//
//    private  void setResult(boolean result) {
//        mResult = result;
//    }
//
//    public boolean queryContractsByName(String name){
//        BmobQuery<Contracts> query = new BmobQuery<>();
//        query.addWhereEqualTo("mName", name);
//        query.findObjects(new FindListener<Contracts>() {
//            @Override
//            public void done(List<Contracts> list, BmobException e) {
//                if (list.size() == 0){
//                    setResult(false);
//                }else {
//                    setResult(true);
//                }
//            }
//        });
//        return mResult;
//    }
//
//    public  boolean queryContractsByPhone(String phone){
//        BmobQuery<Contracts> query = new BmobQuery<>();
//        query.findObjects(new FindListener<Contracts>() {
//            @Override
//            public void done(List<Contracts> list, BmobException e) {
//                if (list.size() == 0){
//                    setResult(false);
//                }else {
//                    setResult(true);
//                }
//            }
//        });
//        return mResult;
//    }

    public List<Contracts> getContractsList() {
        return mContractsList;
    }

    public void addContracts(Contracts contracts){
        mContractsList.add(contracts);
    }

    public void delContracts(Contracts contracts){
        mContractsList.remove(contracts);
    }
}
