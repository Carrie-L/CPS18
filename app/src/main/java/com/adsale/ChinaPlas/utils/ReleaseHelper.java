package com.adsale.ChinaPlas.utils;

import com.adsale.ChinaPlas.ui.NewTecActivity;

/**
 * Created by new on 2017/4/6.
 * 上线版本的一些设置
 * <p>
 * db UPDATE_CENTER 中的 LUT 要为最新时间
 */
public class ReleaseHelper {
    public static boolean IsMyTestVersion = true;   //  我的测试版本，非上线版本；上线版本一定要修改为 false .   JPUSH 别名、
    public static String TRACK_IS_TEST = "true"; // true:测试版本；false:上线版本
    public static boolean LOG_OPEN = true; // true:打印log; false:关闭Log
    public static Integer DB_VERSION = 25; // 如果本地数据库结构有更改，则version+1
    public static Integer NEW_APP_FIRST_VERSION = 133; // 新一年的APP第一版本号，新数据，如果版本号<133，则是上一年的APP，则无需备份旧数据库，直接导入新的。只有在同一年APP有更新时，才备份原来的数据

    //// TODO: 2017/5/18 TrckLog 每5条记录上传一次，退出的时候，上传数据

    //  ----------  ↓↓↓ 其他测试开关 ------------
    /**
     * {@link com.adsale.ChinaPlas.viewmodel.MainViewModel#testExhibitorDataCsv()}
     */
    public static boolean IsExhibitorDataCsvTest = false;

    /**
     * {@link NewTecActivity#processCSVTest()}
     */
    public static boolean IsNewTecCSVTest = false;

    /**
     * {@link Constant#TXT_AD}
     */
    public static boolean IsAdTest = true;

}
