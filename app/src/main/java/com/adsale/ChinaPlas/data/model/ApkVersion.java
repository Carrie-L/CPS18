package com.adsale.ChinaPlas.data.model;

import com.adsale.ChinaPlas.utils.AppUtil;

/**
 * Created by Carrie on 2018/3/27.
 */

public class ApkVersion {
    public int versionCode;
    public String apk;
    public boolean isForceUpdate; // 是否强制更新， 是 则不弹出对话框，直接下载
    public String[] updateMsg; // 更新日历
//    public boolean isAutoUpdate; // 是否自动更新 (不弹出对话框，直接下载)

    public String getUpdateMsg() {
        if (updateMsg.length == 0) {
            return "";
        } else {
            return AppUtil.getName(updateMsg[1], updateMsg[0], updateMsg[2]);
        }
    }


    @Override
    public String toString() {
        return "ApkVersion{" +
                "versionCode=" + versionCode +
                ", apk='" + apk + '\'' +
                ", isForceUpdate='" + isForceUpdate +
                '}';
    }
}
