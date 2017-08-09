package com.adsale.ChinaPlas.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by new on 2017/4/14.
 * 权限工具
 */

public class PermissionUtil {
    //权限
    public static final String PERMISSION_RECEIVE_SMS= Manifest.permission.RECEIVE_SMS;
    public static final String PERMISSION_WRITE_EXTERNAL_STORAGE= Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String PERMISSION_READ_CALENDAR= Manifest.permission.READ_CALENDAR;

    //请求码
    public static final Integer PMS_CODE_SEND_SMS=101;//发送短信
    public static final Integer PMS_CODE_WRITE_SD=102;//写入SD
    public static final Integer PMS_CODE_READ_SD=103;//读取SD
    public static final Integer PMS_CODE_READ_CALENDAR=104;//读取日历

    /**
     * 检查是否有该权限
     * @param activity 类
     * @param permissionType Manifest.permission.XXX
     * @return true 有，false:没有
     */
    public static boolean checkPermission(Activity activity, String permissionType){
        return ContextCompat.checkSelfPermission(activity, permissionType) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(Activity activity,String permissionType,int requestCode){
        ActivityCompat.requestPermissions(activity, new String[]{permissionType}, requestCode);
    }

    public static boolean getGrantResults(int[] grantResults){
       return grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }
}
