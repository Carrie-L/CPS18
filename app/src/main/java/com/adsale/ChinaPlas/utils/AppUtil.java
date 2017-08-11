package com.adsale.ChinaPlas.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.adsale.ChinaPlas.R.id.language;
import static com.adsale.ChinaPlas.dao.SeminarSpeakerDao.Properties.Language;

/**
 * Created by Carrie on 2017/8/8.
 */

public class AppUtil {
    private static final String TAG="AppUtil";

    public static boolean isPadDevice(Context c) {
        return !App.mSP_Config.getBoolean("isPhone",false);
    }

    public static void setIsPhone(boolean bool){
        App.mSP_Config.edit().putBoolean("isPhone",bool).apply();
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


    public static boolean isLogin(){
       return App.mSP_Login.getBoolean(Constant.IS_LOGIN,false);
    }

    public static void putLogout(){
        App.mSP_Login.edit().putBoolean(Constant.IS_LOGIN,false).apply();
    }

    public static String getUserEmail(){
        return App.mSP_Login.getString(Constant.USER_EMAIL,"");
    }

    /**
     * @param context 0:ZhTw; 1:en;2:ZhCn;
     */
    public static int getCurLanguage() {
        return App.mSP_Config.getInt("CUR_LANGUAGE",0);
    }

    public static void setCurLanguage(int language){
        App.mSP_Config.edit().putInt("CUR_LANGUAGE",language).apply();
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
        config.locale = getLocale(language);
        resources.updateConfiguration(config, dm);
    }

    private static Locale getLocale(int language){
        if(language==1){
            return Locale.US;
        }else if(language==2){
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

    public static void showAlertDialog(Context context, String msg, DialogInterface.OnClickListener posListener,DialogInterface.OnClickListener negListener) {
        new AlertDialog.Builder(context).setMessage(msg).setPositiveButton(context.getString(R.string.confirm), posListener).setNegativeButton(context.getString(R.string.cancel),negListener).show();
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

}
