package com.example.innf.newchangtu.Map.manager;

import android.content.Context;
import android.widget.Toast;

import com.example.innf.newchangtu.Map.bean.User;
import com.example.innf.newchangtu.Map.utils.MyApplication;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Author: Inno Fang
 * Time: 2016/8/8 17:13
 * Description:用户操作
 * 单例模式
 */

public class UserLab {

    private static UserLab sUserLab;

    private Context mContext;

    private UserLab(Context context){
        mContext = MyApplication.getContext();
    }

    /*调用该方法获得UserLab对象，用于其他操作*/
    public static UserLab get(Context context){
        if (null == sUserLab){
            sUserLab = new UserLab(context);
        }
        return sUserLab;
    }

    //更新数据
    public void updateBmobUserData(User user){
        user.update(user.getObjectId(), new UpdateListener() {
            public void done(BmobException e) {
                if (e == null){
                    Toast.makeText(mContext, "更新成功", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(mContext, "更新失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }




    //增加数据
    //   public void addBmobUserData(User user, final View v){
//        final View view = v;
//        user.save(new SaveListener<String>() {
//
//            @Override
//            public void done(String objectId, BmobException e) {
//                if (e == null){
//                    Snackbar.make(view, "添加数据成功，返回objectId为：" + objectId, Snackbar.LENGTH_LONG).show();
//                }else{
//                    Snackbar.make(view, "创建数据失败：" + e.getMessage(), Snackbar.LENGTH_LONG).show();
//                }
//            }
//        });
    // }



    //查询数据
    public void queryBmobUserData(User user){

    }

    //删除数据
    public void deleteBmobUserData(User user){

    }

}
