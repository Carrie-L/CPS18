package com.adsale.ChinaPlas.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.LocaleList;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.dao.WebContent;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.data.model.LogJson;
import com.adsale.ChinaPlas.data.model.MessageCenter;
import com.adsale.ChinaPlas.ui.ConcurrentEventActivity;
import com.adsale.ChinaPlas.ui.ExhibitorDetailActivity;
import com.adsale.ChinaPlas.ui.NewsActivity;
import com.adsale.ChinaPlas.ui.NewsDtlActivity;
import com.adsale.ChinaPlas.ui.RegisterActivity;
import com.adsale.ChinaPlas.ui.WebContentActivity;
import com.adsale.ChinaPlas.ui.WebViewActivity;
import com.baidu.mobstat.StatService;
import com.github.promeg.pinyinhelper.Pinyin;
import com.google.gson.Gson;

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

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Carrie on 2017/8/8.
 */

public class AppUtil {
    private static final String TAG = "AppUtil";
    private static final String isTesting = ReleaseHelper.TRACK_IS_TEST;//上线版本则改为false
    private static final String TRACKING_OS = "Android_CPS18";
    private static String strLogJson;
    private static ArrayList<LogJson> logJsonArr;

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

//    public static String getUUID() {
//        return App.mSP_Config.getString("UUID", "");
//    }

    public static String getDeviceId() {
        return App.mSP_Config.getString("deviceId", "");
    }

    public static int getScreenWidth() {
        return App.mSP_Config.getInt(Constant.SCREEN_WIDTH, 0);
    }

    public static void setScreenWidth(int sw) {
        App.mSP_Config.edit().putInt(Constant.SCREEN_WIDTH, sw).apply();
    }

    public static int getPadLeftMargin() {
        return App.mSP_Config.getInt(Constant.PAD_LEFT_MARGIN, 0);
    }

    public static float getPadWidthRate() {
        return App.mSP_Config.getFloat("PadWidthRate", 0);
    }

    public static float getPadHeightRate() {
        return App.mSP_Config.getFloat("PadHeightRate", 0);
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
        int language = AppUtil.getCurLanguage();
        if (language == 0) {
            return tc;
        } else if (language == 1) {
            return en;
        } else {
            return sc;
        }
    }

    public static void putLoginTest() {
        App.mSP_Login.edit().putBoolean(Constant.IS_LOGIN, true).putString(Constant.USER_EMAIL, "894750Test@qq.com").apply();
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

    public static void setJPUSHRegId(String regId) {
        App.mSP_Config.edit().putString(JPushInterface.EXTRA_REGISTRATION_ID, regId).apply();
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
        config.setLocale(getLocale(language));
        resources.updateConfiguration(config, dm);
        App.mLanguage.set(language);
    }

    public static Context createConfigurationContext(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context);
        }
        return context;
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context) {
        Resources resources = context.getResources();
        Locale locale = getLocale(context.getSharedPreferences(Constant.SP_CONFIG, MODE_PRIVATE).getInt("CUR_LANGUAGE", 0));
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        configuration.setLocales(new LocaleList(locale));
        return context.createConfigurationContext(configuration);
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
            if (PermissionUtil.checkPermission((Activity) context, PermissionUtil.PERMISSION_CALL_PHONE)) {
                PermissionUtil.requestPermission((Activity) context, PermissionUtil.PERMISSION_CALL_PHONE, PermissionUtil.PMS_CODE_CALL_PHONE);
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

    public static String getAppVersion() {
        return App.mSP_Config.getString("AppVersion", "");
    }

    /**
     * <font color="#f97798"></font>
     *
     * @param actionId not null
     * @param type     not null 缩写
     * @param subType
     * @param location <font color="#f97798">not null</font>
     * @return void
     * @version 创建时间：2016年5月25日 上午10:20:29
     */
    public static void trackViewLog(int actionId, String type, String subType, String location) {
        if (strLogJson == null) {
            strLogJson = new String();
        }
        if (logJsonArr == null) {
            logJsonArr = new ArrayList<>();
        }

        if (subType == null) {
            subType = "";
        }
        LogJson logJson = new LogJson();
        logJson.ActionID = actionId;
        logJson.AppName = LOG_APP_NAME.concat(getAppVersion());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        LogUtil.i(TAG, "当前时间为：" + df.format(Calendar.getInstance().getTime()));
        logJson.CreateDate = df.format(Calendar.getInstance().getTime());
        logJson.DeviceID = getDeviceId();
        logJson.SystemLanguage = Locale.getDefault().getLanguage();
        logJson.CountryCode = Locale.getDefault().getCountry();

        logJson.SubType = subType;

        if (!TextUtils.isEmpty(subType)) {
            subType = subType + "_";
        }


        String completeType = "";

        if (type.contains("Page")) {
            logJson.ActionGroup = 100;

            //189\190\191(newPhoto、newsLink、newsDetail的trackingName不加subType)
            if (actionId == 189 || actionId == 190 || actionId == 191 || actionId == 192 || actionId == 198) {
                subType = "";
            }

        } else if (type.equals("Ad")) {
            logJson.ActionGroup = 200;

//			if (subType.contains("_")) {
//				LogUtil.i(TAG, "Ad_subType.contains(_)");
//				completeType = subType.split("_")[0];
//			}

        } else if (type.equals("Event")) {
            logJson.ActionGroup = 300;
        } else if (type.equals("Download")) {
            logJson.ActionGroup = 400;
        } else if (type.equals("CA")) {
            logJson.ActionGroup = 400;
            completeType = "ClickAD";
        } else if (type.equals("BE")) {
            logJson.ActionGroup = 400;
            completeType = "BookmarkExh";
        } else if (type.equals("CB")) {
            logJson.ActionGroup = 400;
            completeType = "ClickBooth";
        } else if (type.equals("UC")) {
            logJson.ActionGroup = 400;
            completeType = "UpdateContent";
        } else if (actionId == 422) {
            logJson.ActionGroup = 400;
            completeType = "SubscribeE";
        } else if (type.equals("PR")) {
            logJson.ActionGroup = 400;
            completeType = "PreregReset";
        } else if (type.equals("PS")) {
            logJson.ActionGroup = 400;
            completeType = "PreregSuccess";
        } else if (type.equals("SME")) {
            logJson.ActionGroup = 400;
            completeType = "SyncMyExhibitor";
        } else if (type.equals("Technical")) {
            logJson.ActionGroup = 300;
            completeType = "Technical Seminar";
        } else if (type.contains("Msg")) {
            logJson.ActionGroup = 400;
        } else if (type.equals("US")) {
            completeType = "UpdateSchedule";
            logJson.ActionGroup = 400;
        } else if (type.equals("UN")) {
            completeType = "UpdateNote";
            logJson.ActionGroup = 400;
        } else if (type.equals("SA")) {
            completeType = "ShareApp";
            logJson.ActionGroup = 400;
        } else if (type.equals("SN")) {
            completeType = "ShareNews";
            logJson.ActionGroup = 400;
        } else if (actionId == 425) {
            completeType = "ShareExhibitor";
            logJson.ActionGroup = 400;
        } else if (actionId == 426) {
            completeType = "LoadingTime";
            logJson.ActionGroup = 400;
        } else if (type.equals("CU")) {
            completeType = "ContentUpdate";
            logJson.ActionGroup = 400;
        }// TODO: 2017/2/13 貌似少了 427 keySearch，428 AdvanceSearch
        else if (actionId == 429) {
            completeType = "VisitorNamecard";
            logJson.ActionGroup = 400;
        } else if (actionId == 430) {
            completeType = "CompanyNamecard";
            logJson.ActionGroup = 400;
        } else if (actionId == 431) {
            completeType = "HallMap";
            logJson.ActionGroup = 400;
        } else if (actionId == 432) {
            completeType = "HallMapAd";
            logJson.ActionGroup = 400;
        } else if (actionId == 433) {
            completeType = "Link";
            logJson.ActionGroup = 400;
        } else {
            logJson.ActionGroup = 500;
        }
        if (type.equals("Download")) {
            completeType = "DL";
        }

        LogUtil.i(TAG, "completeType111=" + completeType);
        if (TextUtils.isEmpty(completeType)) {
            LogUtil.i(TAG, "completeType222=" + completeType);
            logJson.Type = type;
        } else {
            LogUtil.i(TAG, "completeType333=" + completeType);
            logJson.Type = completeType;
        }

//        logJson.Location = location.isEmpty() ? " " : location;

        if (TextUtils.isEmpty(location)) {
            logJson.Location = " ";
        } else {
            logJson.Location = location;
            if (actionId == 192) {
                logJson.Location = "ScheduleDetailInfo";
            }
            location = location + "_";
        }
        int language = AppUtil.getCurLanguage();

        switch (language) {
            case 1:
                logJson.TrackingName = type + "_" + subType + location + "en_Android";
                break;
            case 2:
                logJson.LangID = 950;
                logJson.TrackingName = type + "_" + subType + location + "sc_Android";
                break;
            default:
                logJson.LangID = 936;
                logJson.TrackingName = type + "_" + subType + location + "tc_Android";
                break;
        }
        logJson.IsTest = isTesting;
        String visitorID = getVmid();
        LogUtil.e(TAG, "visitorID=" + visitorID);
        if (TextUtils.isEmpty(visitorID)) {
            logJson.VisitorID = "-1";
        } else {
            logJson.VisitorID = visitorID;
        }
        logJson.Platform = "Android".concat(Build.VERSION.RELEASE).concat("_").concat(Build.MODEL);
        //todo don't know how to get PreregID
//        if (getBooleanSharedPreferences(context, "IsRegister")) {
//            logJson.PreregID = getSharedPreferences(context, "PreregID");
//        } else {
//            logJson.PreregID = "-1";
//        }
        logJson.PreregID = "-1";
        logJson.TimeZone = "+8";
        logJson.TrackingOS = TRACKING_OS;
        logJson.Year = YEAR;

        sendLog(logJson);

    }

    public static void trackLog(Context context, int actionGroup, int actionId, String type, String subType, String location, String trackingNamePrefix) {
        if (strLogJson == null) {
            strLogJson = "";
        }
        if (logJsonArr == null) {
            logJsonArr = new ArrayList<>();
//            readSDLog(); // TODO: 2017/2/14 检测为什么加了这句，提交log就会失败
        }
        LogJson logJson = new LogJson();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        LogUtil.i(TAG, "当前时间为：" + df.format(Calendar.getInstance().getTime()));
        int curLang = AppUtil.getCurLanguage();

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
        int zone = zoneOffset / 60 / 60 / 1000;

        logJson.AppName = LOG_APP_NAME.concat(getAppVersion());
        logJson.ActionGroup = actionGroup;
        logJson.ActionID = actionId;
        logJson.Platform = "Android".concat(Build.VERSION.RELEASE).concat("_").concat(Build.MODEL);
        logJson.DeviceID = getDeviceId();
        logJson.LangID = curLang == 0 ? 936 : curLang == 1 ? 1252 : 950;
//        if (getBooleanSharedPreferences(context, "IsRegister")) {
//            logJson.PreregID = getSharedPreferences(context, "PreregID");
//        } else {
//            logJson.PreregID = "-1";
//        }
        logJson.PreregID = "-1";
        logJson.Type = type;
        logJson.SubType = subType;
        logJson.Location = location;
        logJson.CountryCode = Locale.getDefault().getCountry();
        logJson.SystemLanguage = Locale.getDefault().getLanguage();
        logJson.TrackingName = trackingNamePrefix.concat(getLanguageType()).concat("_Android");
        logJson.TrackingOS = TRACKING_OS;
        logJson.CreateDate = df.format(Calendar.getInstance().getTime());
        logJson.Year = YEAR;
        logJson.TimeZone = zone > 0 ? ("+" + zone) : zone + "";
        logJson.IsTest = isTesting;
        String visitorID = context.getSharedPreferences(Constant.SP_LOGIN, 0).getString("vmid", "");
        logJson.VisitorID = TextUtils.isEmpty(visitorID) ? "-1" : visitorID;
        LogUtil.e(TAG, "visitorID=" + visitorID);
        LogUtil.i(TAG, "TrackingName=" + trackingNamePrefix + getLanguageType(curLang) + "_Android");

        sendLog(logJson);

    }

    private static void sendLog(LogJson logJson) {
        LogUtil.i(TAG, "sendLog:logJson=" + logJson.toString());
        LogUtil.i(TAG, "sendLog:logJsonArr=" + logJsonArr.size());

        //2016.6.23: 那你就每次寫入的時候加個簡單判斷，若時間、trackingname一致，就不重複寫入
        boolean isRepeat = false;
        int size = logJsonArr.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                LogJson log = logJsonArr.get(i);
                if (logJson.Location.equals(log.Location) && (logJson.TrackingName).equals(log.TrackingName)) {
                    isRepeat = true;
                    break;
                }
            }
        }

        if (!isRepeat) {
            logJsonArr.add(logJson);
        }
        if (logJsonArr == null || logJsonArr.size() == 0) {
            return;
        }
        strLogJson = new Gson().toJson(logJsonArr);

        LogUtil.i(TAG, "sendLog:strLogJson=" + strLogJson);

        // 目的是为了退出之后再打开app还是会记得退出之前的事件
//        writeFileToSD(strLogJson, LOG_JSON_STR_KEY);
//        final String strLogSD = readFromSD(LOG_JSON_STR_KEY);
//        LogUtil.i(TAG, "strLogSD=" + strLogSD);
//        if (!TextUtils.isEmpty(strLogSD)) {
//            ArrayList<LogJson> logList = Parser.parseJsonArray(strLogSD);
////            LogUtil.e(TAG, "----------------trackingLog=" + logList.toString());
//
//            int logSize = logList.size();
//            LogUtil.i(TAG, "logSize=" + logSize);
//            LogUtil.i(TAG, "logJsonArr.size()=" + logJsonArr.size());
////            LogUtil.e(TAG, "----------------strLogJson=" + strLogJson);
//        }

        if (logJsonArr.size() >= 5) {// >= 5
            FormBody formBody = new FormBody.Builder().add("logArr", strLogJson).build();
            Request request = new Request.Builder().url(NetWorkHelper.LOGJSON).post(formBody).build();
            App.mOkHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtil.e(TAG, "PostLogFailed: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    LogUtil.i(TAG, "PostLogSuccess");
                    if (logJsonArr != null) {
                        logJsonArr.clear();
                    }
                    strLogJson = "";
//                    writeFileToSD(strLogJson, LOG_JSON_STR_KEY);
                }
            });
        }
    }

    public static String getVmid() {
        return App.mSP_Login.getString(Constant.VMID, "");
    }

    /**
     * <font color="#f97798">百度统计自定义事件</font>
     *
     * @param context
     * @param id      自定义事件id
     * @param lable   标签 （TrackingName）前缀BE_219644|| Page_AdvancedSearch
     * @return void
     * @version 创建时间：2016年6月15日 下午2:21:19
     */
    public static void setStatEvent(Context context, String id, String lable) {
        //lable: Page_AdvancedSearch_sc_Android
        lable = lable.concat("_").concat(getLanguageType()).concat("_Android");
        StatService.onEvent(context, id, lable, 1);
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

    public static void showAlertDialog(Context context, int messageResID, int positiveResID, int negativeResID, DialogInterface.OnClickListener positiveClickListener) {
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setMessage(messageResID);
        ad.setPositiveButton(positiveResID, positiveClickListener);
        ad.setNegativeButton(negativeResID, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
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
        LogUtil.i(TAG, "time=" + time);
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSZ", Locale.CHINA);
//        SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
//        try {
//            Date date = format.parse(time);
//            System.out.println("time=" + time + ",,,time2=" + sformat.format(date));
//            return sformat.format(date);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        return time;
    }

    public static long getShowCountDown() {
        long diff = 0;
        String today = AppUtil.getTodayDate();
        String showStartDate = Constant.SCHEDULE_DAY01;//yyyy-MM-dd
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date0 = sdf.parse(today);
            Date date1 = sdf.parse(showStartDate);
            diff = (date1.getTime() - date0.getTime()) / (1000 * 60 * 60 * 24);
            LogUtil.i(TAG, "diff=" + diff);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diff;
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

    public static String subLast(String str, char c) {
        return str.substring(0, str.lastIndexOf(c) + 1);
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
        return ni.isAvailable() && ni.isConnected();
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

    /**
     * 得到计算后的高度，宽度为屏幕宽
     *
     * @return height
     */
    public static int getCalculatedHeight(int originWidth, int originHeight) {
        int screenWidth = App.mSP_Config.getInt(Constant.SCREEN_WIDTH, 0);
        return (screenWidth * originHeight) / originWidth;
    }

    public static void messageIntent(Context context, MessageCenter.Message message, boolean pendingIntent) {
        Intent intent = null;
        switch (Integer.valueOf(message.function)) {
            case 1://展商詳情頁
                ExhibitorRepository repository = ExhibitorRepository.getInstance();
                if (!repository.isExhibitorIDExists(message.ID)) {
                    Toast.makeText(context, context.getString(R.string.no_exhibitor), Toast.LENGTH_SHORT).show();
                } else {
                    LogUtil.i(TAG, ",companyID=" + message.ID);
                    intent = new Intent(context, ExhibitorDetailActivity.class);
                    intent.putExtra("CompanyID", message.ID);
                    intent.putExtra("title", context.getString(R.string.title_exhibitor_deti));
                }
                break;

            case 2://新闻
                if (TextUtils.isEmpty(message.ID)) {
                    intent = new Intent(context, NewsActivity.class);
                } else {
                    intent = new Intent(context, NewsDtlActivity.class);
                    intent.putExtra("NewsID", message.ID);
                }
                break;

            case 3://同期活动
                intent = new Intent(context, WebContentActivity.class);
                intent.putExtra(Constant.WEB_URL, "ConcurrentEvent/".concat(message.ID));
                break;

            case 4://link
                intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(Constant.WEB_URL, message.ID);
                break;

            case 5://预登记
                intent = new Intent(context, RegisterActivity.class);
                break;

            case 6://新闻详情
                intent = new Intent(context, NewsDtlActivity.class);
                intent.putExtra("ID", message.ID);
                break;

            default:
                break;
        }

        //不是PendingIntent，就直接跳转
        if (!pendingIntent && intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//必不可少
            intent.putExtra("Type", Constant.PUSH_INTENT);
            context.startActivity(intent);
            if (AppUtil.isTablet()) {
                ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }

    }


}
