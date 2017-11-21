package com.adsale.ChinaPlas.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.TextView;
import android.widget.Toast;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.github.promeg.pinyinhelper.Pinyin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.adsale.ChinaPlas.utils.PermissionUtil.PMS_CODE_CALL_PHONE;

/**
 * Created by Carrie on 2017/8/8.
 */

public class AppUtil {
    private static final String TAG = "AppUtil";
    private static final String isTesting = ReleaseHelper.TRACK_IS_TEST;//上线版本则改为false
    private static final String TRACKING_OS = "Android_CPS";

    private static final String ACCOUNT_NAME = "ChinaPlas@gmail.com";
    private static final String CALENDAR_DISPLAY_NAME = "ChinaPlas18";
    private static final int YEAR = 2018;
    private static String LOG_APP_NAME = "CPS18v";

    public static boolean isFirstRunning() {
        return App.mSP_Config.getBoolean("isFirstRunning", true);
    }

    public static void setNotFirstRunning() {
        App.mSP_Config.edit().putBoolean("isFirstRunning", false).apply();
    }

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

    public static String getUUID() {
        return App.mSP_Config.getString("UUID", "");
    }

    public static String getDeviceId() {
        return App.mSP_Config.getString("deviceId", "");
    }

    public static int getScreenWidth() {
        return App.mSP_Config.getInt("ScreenWidth", 0);
    }

    public static int getScreenHeight() {
        return App.mSP_Config.getInt("ScreenHeight", 0);
    }

    public static boolean isTablet() {
        return App.mSP_Config.getBoolean("isTablet", false);
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
        int language = App.mLanguage.get();
        if (language == 0) {
            return tc;
        } else if (language == 1) {
            return en;
        } else {
            return sc;
        }
    }

    public static void putLoginTest(){
        App.mSP_Login.edit().putBoolean(Constant.IS_LOGIN, true).putString(Constant.USER_EMAIL,"894750Test@qq.com").apply();
    }


    public static boolean isLogin() {
        return App.mSP_Login.getBoolean(Constant.IS_LOGIN, false);
    }

    public static void putLogout() {
        App.mSP_Login.edit().putBoolean(Constant.IS_LOGIN, false).apply();
    }

    public static void putLogin() {
        App.mSP_Login.edit().putBoolean(Constant.IS_LOGIN, true).apply();
    }

    public static String getUserEmail() {
        return App.mSP_Login.getString(Constant.USER_EMAIL, "");
    }

    /**
     * 0:ZhTw; 1:en;2:ZhCn;
     */
    public static int getCurLanguage() {
        return App.mSP_Config.getInt("CUR_LANGUAGE", 0);
    }

    public static void setCurLanguage(int language) {
        App.mSP_Config.edit().putInt("CUR_LANGUAGE", language).apply();
    }

    public static String getLanguageType() {
        return getName("tc", "en", "sc");
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
        App.mLanguage.set(language);
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

    /**
     * 保存文件到data/data/com.adsale.ChinaPlas/files/目录下
     *
     * @param context
     * @param fileName 如：reg.png
     * @param bytes
     * @return true, 保存成功；
     */
    public static boolean saveFileOutput(Context context, String fileName, byte[] bytes) {
        context = context.getApplicationContext();
        try {
            FileOutputStream fos = context.openFileOutput(fileName, MODE_PRIVATE);
            fos.write(bytes);
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param dir  文件夹名称，如ConcurrentEvent
     * @param name 文件夹中的文件名
     * @return
     */
    public static boolean isFileInAsset(String dir, String name) {
        AssetManager am = App.mAssetManager;
        try {
            String[] names = am.list(dir);
            for (String str : names) {
                LogUtil.i(TAG, "names=" + str);
                if (str.equals(name)) {
                    LogUtil.i(TAG, name + "文件存在！！！！");
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogUtil.e(TAG, name + "不存在啦！！！！");
        return false;
    }

    /**
     * 读取Asset文件
     *
     * @param fileName
     * @return InputStream||null
     * @version 创建时间：2016年4月12日 下午7:24:56
     */
    public static InputStream getAssetInputStream(String fileName) {
        try {
            return App.mAssetManager.open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param fileName rootDir/  e.g:"FloorPlan/FloorPlan.csv" 确保SD下的文件夹、文件名 和 Asset目录下一样
     * @return InputStream
     */
    public static InputStream getInputStream(String fileName) {
        if (new File(App.rootDir.concat(fileName)).exists()) {
            try {
                LogUtil.i(TAG, "sd卡中getInputStream:" + fileName);
                return new FileInputStream(new File(App.rootDir.concat(fileName)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                LogUtil.i(TAG, "asset中getInputStream:" + fileName);
                return getAssetInputStream(fileName);
            }
        } else {
            LogUtil.i(TAG, "asset中getInputStream:" + fileName);
            return getAssetInputStream(fileName);
        }
    }

    public static String getUrlLangType(int language) {
        return language == 0 ? "lang-trad" : language == 1 ? "lang-eng" : "lang-simp";
    }

    /**
     * 發送郵件
     *
     * @param context
     * @param url
     */
    public static void sendEmailIntent(Context context, String url) {
        Uri uri = null;
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            if (url.contains("mailto")) {
                uri = Uri.parse("mailto:" + url);
            } else {
                uri = Uri.parse(url);
            }
            i.setData(uri);
            i.setType("plain/text");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{url.trim()});
            i.putExtra(Intent.EXTRA_SUBJECT, "");
            i.putExtra(Intent.EXTRA_TEXT, "CHINAPLAS 2017");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            if (isTablet()) {
                ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(context, context.getString(R.string.exception_toast_email), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 撥打電話
     *
     * @param context
     * @param url
     */
    public static void callPhoneIntent(Context context, String url) {
        LogUtil.i(TAG, "撥打電話:url=" + url);
        if (context instanceof App) {
            throw new ClassCastException("Application cannot be cast to Activity");
        }

        Uri uri;
        try {
            if (url.contains("tel")) {
                uri = Uri.parse(url);
            } else {
                uri = Uri.parse("tel:" + url);
            }
            Intent intent = new Intent(Intent.ACTION_CALL, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, PMS_CODE_CALL_PHONE);
                return;
            } else {
                context.startActivity(intent);
                if (isTablet()) {
                    ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, context.getString(R.string.exception_toast_phone), Toast.LENGTH_SHORT).show();
        }
    }

    /* \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\  工具  \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\ */

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
     * 圓形progress bar dialog
     */
    public static ProgressDialog createProgressDialog(Context pContext, int ResourceID) {
        ProgressDialog oProgressDialog;
        oProgressDialog = new ProgressDialog(pContext);
        oProgressDialog.setCancelable(false);
        oProgressDialog.setCanceledOnTouchOutside(false);
        oProgressDialog.setMessage(pContext.getString(ResourceID));
        return oProgressDialog;
    }

    /**
     * 只有一個“確定/OK”按鈕
     */
    public static void showConfirmAlertDialog(Context context, String msg) {
        new AlertDialog.Builder(context).setMessage(msg).setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public static void showAlertDialog(Context context, int messageResID, int positiveResID, int negativeResID, DialogInterface.OnClickListener positiveClickListener,
                                       DialogInterface.OnClickListener negativeClickListener) {
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setMessage(messageResID);
        ad.setPositiveButton(positiveResID, positiveClickListener);
        if (negativeResID > 0) {
            ad.setNegativeButton(negativeResID, negativeClickListener);
        }
        ad.create().show();
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

    public static String getCurrentTime() {
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sFormat.format(new Date());
    }

    /**
     * @return dd/MM/yyyy
     */
    public static String getCurrentDate() {
        SimpleDateFormat sFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sFormat.format(new Date());
    }

    /**
     * 获取今天的日期
     *
     * @return String yyyy-MM-dd
     */
    public static String getTodayDate() {
        SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sformat.format(Calendar.getInstance().getTime());
    }

    /**
     * 获取昨天的日期
     *
     * @return String yyyy-MM-dd
     */
    public static String getYesterdayDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
    }

    /**
     * 将GMT格式的时间转换为系统时间
     *
     * @param time 2016-09-09T09:31:00.00+08:00
     * @return <font color="#f97798">yyyy-MM-dd HH:mm:ss</font>
     */
    public static String GMT2UTC(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSZ", Locale.getDefault());
        SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date date = format.parse(time);
            /*System.out.println("time="+time+",,,time2="+sformat.format(date));*/
            return sformat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 24小时制 转化成 上午下午制 [yyyy-MM-dd HH:mm -> yyyy-MM-dd hh:mm a]
     */
    public static String formatStartTime(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());
        try {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date date = sdf1.parse(time);
            time = sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LogUtil.i(TAG, "formatStartTime::time==" + time);
        return time;
    }

    /**
     * 上午下午制 转化成 24小时制 [yyyy-MM-dd hh:mm a -> yyyy-MM-dd HH:mm ]
     */
    public static String reformatStartTime(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());
            Date date = sdf1.parse(time);
            time = sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LogUtil.i(TAG, "reformatStartTime::time==" + time);
        return time;
    }

    public static Date stringToDate(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            return sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
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
     * @param str 要截取的完整字符串，如：https://o97tbiy1f.qnssl.com/ExhibtorInfo/ExhibitorInfo.zip
     * @param c0  截取分隔符0，如： ".com/",经过这一步 str.substring(str.indexOf(".com/")+5); ——>  ExhibitorInfo/20170614.zip
     * @param c1  截取分隔符1，在c0的基础上分割 "ExhibitorInfo/20170614.zip".split("/")[0];  ——>  ExhibitorInfo
     * @return "ExhibitorInfo"
     */
    public static String subStringMiddle(String str, String c0, String c1) {
        String result = str.substring(str.indexOf(c0) + c0.length());//   ExhibitorInfo/20170614.zip
        LogUtil.i(TAG, "result0=" + result);
        result = result.split(c1)[0];
        LogUtil.i(TAG, "result1=" + result);
        return result;
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
    public static String subStringLastFront(String str, String c1, char c2) {
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
     *
     * @param str 完整字符串
     * @return
     */
    public static String subLetterFront(String str) {
        StringBuilder sb = new StringBuilder();
        char c;
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            if (!Character.isLetter(c)) {
                sb.append(c);
            } else {
                LogUtil.i(TAG, "Character.isLetter=" + c);
                break;
            }
        }
        return sb.toString();
    }

    public static boolean isNetworkAvailable() {
        NetworkInfo ni = App.mConnectivityManager.getActiveNetworkInfo();
//        return ni != null && ni.isConnected();
        // TODO: 2017/11/19
        return false;
    }

    public static <T> void logListString(ArrayList<T> list) {
        LogUtil.i(TAG, "logListString -=- " + list.size() + "," + list.toString());
    }

    public static <T> void sort(ArrayList<T> list, Comparator<T> comparator) {
        Collections.sort(list, comparator);
    }

    public static String getLanguageType(int language) {
        if (language == 0) {
            return "tc";
        } else if (language == 1) {
            return "en";
        } else {
            return "sc";
        }
    }

    public static String getFirstChar(String str) {
        char c = Pinyin.toPinyin(str, "").charAt(0);
        return (c + "").toUpperCase();
    }

    public static void getDuringTime(String tag, String str, long startTime) {
        long endTime = System.currentTimeMillis();
        LogUtil.i(tag, str + " * 所花費的時間為:" + (endTime - startTime) + "ms");
    }




}
