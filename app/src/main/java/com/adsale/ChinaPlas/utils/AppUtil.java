package com.adsale.ChinaPlas.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;

/**
 * Created by Carrie on 2017/8/8.
 */

public class AppUtil {
    private static final String TAG="AppUtil";

    /**
     * @param context 0:ZhTw; 1:en;2:ZhCn;
     */
    public static int getCurLanguage(Context context) {
        return context.getSharedPreferences("LANGUAGE_TYPE", Context.MODE_PRIVATE).getInt("CUR_LANGUAGE", 0);
    }

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

}
