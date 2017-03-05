package com.example.innf.newchangtu.Map.utils;

import java.util.List;

/**
 * Author: Inno Fang
 * Time: 2017/3/5 11:10
 * Description:
 */


public interface PermissionListener {

    void onGranted();  //授权

    void onDenied(List<String> deniedPermission);   //拒绝 ,并传入被拒绝的权限

}
