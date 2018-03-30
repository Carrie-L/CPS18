package com.adsale.ChinaPlas.utils;

/**
 * Created by new on 2017/4/6.
 * 上线版本的一些设置
 *
 * db UPDATE_CENTER 中的 LUT 要为最新时间
 *
 */
public class ReleaseHelper {
    static String TRACK_IS_TEST = "true"; // true:测试版本；false:上线版本
    public static boolean LOG_OPEN = true; // true:打印log; false:关闭Log
    public static Integer DB_VERSION = 17; // 如果本地数据库有更改，则version+1

    //// TODO: 2017/5/18 TrckLog 每5条记录上传一次，退出的时候，上传数据
}
