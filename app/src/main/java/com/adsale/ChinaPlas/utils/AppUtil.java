package com.adsale.ChinaPlas.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.LocaleList;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.viewmodel.NavViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.adsale.ChinaPlas.R.id.language;
import static com.adsale.ChinaPlas.dao.SeminarSpeakerDao.Properties.Language;

/**
 * Created by Carrie on 2017/8/8.
 */

public class AppUtil {
    private static final String TAG = "AppUtil";

    public static boolean isPadDevice(Context c) {
        return !App.mSP_Config.getBoolean("isPhone", false);
    }

    public static void setIsPhone(boolean bool) {
        App.mSP_Config.edit().putBoolean("isPhone", bool).apply();
    }

    public static void setDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        LogUtil.i(TAG, "deviceId=" + deviceId);
        App.mSP_Config.edit().putString("DeviceId", deviceId).apply();
    }

    public static void saveDeviceId(String deviceId) {
        App.mSP_Config.edit().putString("DeviceId", deviceId).apply();
    }

    public static String getDeviceId(Context context) {
        String deviceId = App.mSP_Config.getString("DeviceId", "");
        return App.mSP_Config.getString("DeviceId", "");
    }

//    public static boolean isLogin(Context context) {
//        return context.getSharedPreferences(Constant.SP_LOGIN, Context.MODE_PRIVATE).getString("login", "").equals("success");
//    }

    /**
     * 是否为第一次从网上下载数据，是的话，就清空数据库，插入新数据
     *
     * @return
     */
    public static boolean isFirstGetData() {
        return App.mSP_Config.getBoolean("IsFirstGetData", true);
    }

    /**
     * 获取对应语言的列名
     *
     * @param language
     * @return cast(SortTW as INT) || SortCN || SortEN
     */
    public static String Sort(int language) {
        return getName(language, " cast(SORT_TW as INT) ", " SORT_EN ", " SORT_CN ");
    }

    public static String getName(int language, String tc, String en, String sc) {
        if (language == 0) {
            return tc;
        } else if (language == 1) {
            return en;
        } else {
            return sc;
        }
    }

    public static String getName(String tc, String en, String sc) {
        int language = getCurLanguage();
        if (language == 0) {
            return tc;
        } else if (language == 1) {
            return en;
        } else {
            return sc;
        }
    }


    public static boolean isLogin() {
        return App.mSP_Login.getBoolean(Constant.IS_LOGIN, false);
    }

    public static void putLogout() {
        App.mSP_Login.edit().putBoolean(Constant.IS_LOGIN, false).apply();
    }

    public static String getUserEmail() {
        return App.mSP_Login.getString(Constant.USER_EMAIL, "");
    }

    /**
     *    0:ZhTw; 1:en;2:ZhCn;
     */
    public static int getCurLanguage() {
        return App.mSP_Config.getInt("CUR_LANGUAGE", 0);
//        return App.language.get();
    }

    public static void setCurLanguage(int language) {
        App.mSP_Config.edit().putInt("CUR_LANGUAGE", language).apply();
    }

    /**
     * @param mContext
     * @param language 0:ZhTw; 1:en;2:ZhCn;
     */
    public static void switchLanguage(Context mContext, int language) {
        setCurLanguage(language);

        Resources resources = mContext.getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        config.locale=getLocale(language);
        resources.updateConfiguration(config, dm);
    }

    private static Locale getLocale(int language) {
        if (language == 1) {
            return Locale.US;
        } else if (language == 2) {
            return Locale.SIMPLIFIED_CHINESE;
        }
        return Locale.TRADITIONAL_CHINESE;
    }

    public static String getLocaleLanguage(Context context) {
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        LogUtil.d(TAG, "getLocaleLanguage=" + config.locale.getCountry());
        return config.locale.getCountry();
    }


    // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\  工具  \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        } else {
            return reference;
        }
    }

    public static void showAlertDialog(Activity activity, String msg, DialogInterface.OnClickListener positiveListener) {
        new AlertDialog.Builder(activity).setMessage(msg).setPositiveButton(activity.getString(R.string.confirm), positiveListener).setNegativeButton(activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public static void showAlertDialog(Context context, String msg, DialogInterface.OnClickListener posListener, DialogInterface.OnClickListener negListener) {
        new AlertDialog.Builder(context).setMessage(msg).setPositiveButton(context.getString(R.string.confirm), posListener).setNegativeButton(context.getString(R.string.cancel), negListener).show();
    }

    /**
     * 给TextView设置下划线和蓝色字体
     *
     * @param tv
     */
    public static void setUnderLine(TextView tv) {
        tv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下劃線
        tv.setTextColor(Color.BLUE);// 設置超鏈接顏色
    }

    /**
     * 给TextView设置下划线和蓝色字体
     *
     * @param tv
     */
    public static void setUnderLineColor(TextView tv, int color) {
        tv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下劃線
        tv.setTextColor(color);// 設置超鏈接顏色
    }

    public static String dateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(date);
    }

    public static String timeToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(date);
    }

    public static String getCurrentTime(){
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
        return sFormat.format(new Date());
    }

    /**
     * 截取符号前面的字符串 如：E6_20160219.jpg ，c='_'  ——》 E6
     *
     * @param str
     * @param c
     * @return
     * @version 创建时间：2016年4月14日 下午2:48:29
     */
    public static String subStringFront(String str, char c) {
        return str.substring(0, str.length() - str.substring(str.lastIndexOf(c) - 1).length() + 1);
    }

    public static String subStringFront(String str, String c) {
        return str.substring(0, str.length() - str.substring(str.lastIndexOf(c) - 1).length() + 1);
    }

    /**
     * (截取包括c的部分) 如：E6_20160219.jpg  ，c='_'  ——》 E6_
     *
     * @param str
     * @param c
     * @return
     * @version 创建时间：2016年4月14日 下午2:48:29
     */
    public static String subStringFront1(String str, char c) {
        return str.substring(0, str.length() - str.substring(str.lastIndexOf(c) - 1).length() + 2);
    }

    /**
     * 截取字符串符号后的字段 如：E6_20160219.jpg ，c='_' ——》 jpg
     *
     * @param str
     * @param c
     * @return
     * @version 创建时间：2016年4月14日 下午2:47:43
     */
    public static String subStringLast(String str, char c) {
        return str.substring(str.lastIndexOf(c) + 1);
    }

    /**
     * 截取字符串符号后的字段 如：E6_20160219.jpg ，c='.' ——》 jpg
     *
     * @param str
     * @param c
     * @return
     */
    public static String subStringLast(String str, String c) {
        return str.substring(str.lastIndexOf(c) + 1);
    }

    /**
     * 截取(包括字符串)的字段 如：E6_20160219.jpg ，c='_' ——》 .jpg
     *
     * @param str
     * @param c
     * @return
     * @version 创建时间：2016年4月14日 下午2:47:43
     */
    public static String subStringLast1(String str, char c) {
        return str.substring(str.lastIndexOf(c));
    }

    /**
     * 获取中间部分
     *
     * @param str https://o97tbiy1f.qnssl.com/ExhibtorInfo/ExhibitorInfo.zip
     * @param c1  '/' 先截取最后部分 ——> ExhibitorInfo.zip
     * @param c2  '.' 再获取前面部分 ——> ExhibitorInfo
     * @return String <font color="#f97798">ExhibitorInfo</font>
     * @version 创建时间：2016年9月6日 下午2:03:29
     */
    public static String subStringLastFront(String str, char c1, char c2) {
        String str1 = subStringLast(str, c1);
        return subStringFront(str1, c2);
    }

    /**
     * 获取中间部分
     *
     * @param str https://o97tbiy1f.qnssl.com/ExhibtorInfo/ExhibitorInfo.zip
     * @param c1  '/' 先截取最后部分 ——> ExhibitorInfo.zip
     * @param c2  '.' 再获取前面部分 ——> ExhibitorInfo.   (截取包括c2的部分)
     * @return String <font color="#f97798">ExhibitorInfo</font>
     * @version 创建时间：2016年9月6日 下午2:03:29
     */
    public static String subStringLastFront1(String str, char c1, char c2) {
        String str1 = subStringLast1(str, c1);
        return subStringFront1(str1, c2);
    }

    /**
     * 截取字母前的数字，如11.2D1——> 11.2
     * @param str 完整字符串
     * @return
     */
    public static String subLetterFront(String str){
        StringBuilder sb=new StringBuilder();
        char c;
        for(int i=0;i<str.length();i++){
            c=str.charAt(i);
            if(!Character.isLetter(c)){
                sb.append(c);
            }else{
                LogUtil.i(TAG,"Character.isLetter="+c);
                break;
            }
        }
        return sb.toString();
    }

}
