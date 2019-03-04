package com.adsale.ChinaPlas;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.ObservableInt;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Process;
import android.support.multidex.MultiDexApplication;

import com.adsale.ChinaPlas.dao.DBHelper;
import com.adsale.ChinaPlas.dao.DaoMaster;
import com.adsale.ChinaPlas.dao.DaoSession;
import com.adsale.ChinaPlas.dao.TempOpenHelper;
import com.adsale.ChinaPlas.helper.BmobHelper;
import com.adsale.ChinaPlas.helper.LogHelper;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.CrashHandler;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.ReleaseHelper;
import com.baidu.mobstat.StatService;
import com.crashlytics.android.Crashlytics;
import com.mob.MobSDK;
import com.squareup.leakcanary.LeakCanary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;
import cn.bmob.v3.BmobQuery;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.sharesdk.framework.ShareSDK;
import io.fabric.sdk.android.Fabric;
import okhttp3.OkHttpClient;

/**
 * Created by Carrie on 2017/8/8.
 */

public class App extends MultiDexApplication {
    private static final String TAG = "App";

//    public static final ObservableInt language=new ObservableInt(0);

    public static SharedPreferences mSP_Config;
    public static SharedPreferences mSP_UpdateInfo;
    public static SharedPreferences mSP_UpdateInfoBefore;
    public static SharedPreferences mSP_LastModified;
    public static SharedPreferences mSP_UpdateTime;
    public static SharedPreferences mSP_Login;
    public static SharedPreferences mSP_Sync;
    public static SharedPreferences mSP_HP;
    public static SharedPreferences mSP_DownloadCenter;
    public static SharedPreferences mSP_EventPage;

    public static final String DATABASE_NAME = "cps19.db";

    private DaoMaster daoMaster;
    public SQLiteDatabase db;
    public static String mAppVersion;
    public static int mVersionCode;
    private DaoSession daoSession;
    public static DBHelper mDBHelper;
    public static String DB_PATH = "";// 在手机里存放数据库的位置
    private static Resources resources;
    public static String rootDir, filesDir;
    public static ConnectivityManager mConnectivityManager;
    public static AssetManager mAssetManager;

    public static OkHttpClient mOkHttpClient;

    public static final ObservableInt mLanguage = new ObservableInt();
    private TempOpenHelper mTempOpenHelper;
    public static Configuration configuration;
    public static LogHelper mLogHelper;

    //    public static String memoryFileDir;

    @Override
    public void onCreate() {
        super.onCreate();
        initSP();
        String processName = getProcessName();
        LogUtil.i(TAG, "processName=" + processName);
        if (processName != null && processName.endsWith("pushcore")) {
            //因为极光推送开启了独立进程，因此onCreate()会调用两次。所以根据进程名做出区分
            initJPush();
            LogUtil.i(TAG, "---------APP START-----初始化了极光推送的sdk!!");
            return;
        }

        LogUtil.i(TAG, "~~~~~~~~~~~After JPUSH~~~~~~~~");
        initCrashHandle();
        resources = getResources();

        mLogHelper = LogHelper.getInstance();

//      initLeakCanary();

        DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/" + getPackageName() + "/databases";

        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        rootDir = getDir("cps19", MODE_PRIVATE).getAbsolutePath() + "/";
        filesDir = getFilesDir().getAbsolutePath() + "/";// /data/user/0/com.adsale.ChinaPlas/files/
        LogUtil.i(TAG, "rootDir=" + rootDir);
        LogUtil.i(TAG, "filesDir=" + filesDir);

        mAssetManager = getAssets();

//        isNetworkAvailable = AppUtil.isNetworkAvailable();
//        mOkHttpClient = new OkHttpClient.Builder().build();//connectTimeout(15, TimeUnit.SECONDS).

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(15, TimeUnit.SECONDS);
        builder.readTimeout(15, TimeUnit.SECONDS);
        builder.writeTimeout(15, TimeUnit.SECONDS);
        mOkHttpClient = builder.build();

        getDbHelper();

        StatService.setAppChannel(this, "RepleceWithYourChannel", true);

//        MobSDK.init(this);

        //第一：默认初始化
        Bmob.initialize(this, "7fba88ccf93642ad18f878d23354a483");
        BmobQuery.clearAllCachedResults();




    }

    private void initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }

    private void initJPush() {
        // ------------ 极光推送初始化------------------
        JPushInterface.setDebugMode(ReleaseHelper.LOG_OPEN); // 设置调试模式，用于开启Debug模式，显示更多的日志信息
        JPushInterface.init(this); // 初始化sdk
        String registrationID = JPushInterface.getRegistrationID(getApplicationContext());
        LogUtil.i("App_JPushInterface", "registrationID=" + registrationID);
        // 别名
        if (ReleaseHelper.IsMyTestVersion) {
            testJPush();
        } else {
            setJPushAlias();
        }


    }

    private void setJPushAlias() {
        LogUtil.i(TAG, "-setJPushAlias-");
        int language = AppUtil.getCurLanguage();
        if (language == 0) {
            JPushInterface.setAlias(getApplicationContext(), 1, "TCUser");
        } else if (language == 1) {
            JPushInterface.setAlias(getApplicationContext(), 1, "ENUser");
        } else {
            JPushInterface.setAlias(getApplicationContext(), 1, "SCUser");
        }
    }

    private void testJPush() {
        LogUtil.i(TAG, "-testJPush-");
        JPushInterface.setAlias(this, "Carrie2019", new TagAliasCallback() {
            @Override
            public void gotResult(int arg0, String arg1, Set<String> arg2) {
                LogUtil.i("App:JPushInterface", "返回状态码arg0=" + arg0 + ",别名arg1=" +
                        arg1 + ",标签arg2=" + arg2);
            }
        });
    }

    private String getProcessName() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == Process.myPid()) {
                return procInfo.processName;
            }
        }
        return null;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(AppUtil.createConfigurationContext(base));
    }

    public static boolean isNetworkAvailable;

    private void isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        String typeName = networkInfo.getTypeName().toLowerCase();
        LogUtil.i(TAG, "isNetworkAvailable:typeName 0 = " + typeName);
        if (typeName.equalsIgnoreCase("wifi")) {
            boolean isWifiConn = networkInfo.isConnected();
            LogUtil.i(TAG, "isNetworkAvailable:isWifiConn  = " + isWifiConn);
            if (isWifiConn) {
//                isNetworkAvailable = false;
//                ping("www.baidu.com",2,new StringBuffer());
//                isNetworkAvailable = pingWifi();
            }
        } else {
            typeName = networkInfo.getExtraInfo().toLowerCase();
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//            isNetworkAvailable = activeNetwork != null &&
//                    activeNetwork.isConnectedOrConnecting();
        }
        LogUtil.i(TAG, "isNetworkAvailable:typeName 1 = " + typeName);
//        LogUtil.i(TAG, "isNetworkAvailable = " + isNetworkAvailable);
    }

    private void initSP() {
        mSP_Config = getSharedPreferences(Constant.SP_CONFIG, MODE_PRIVATE);
        mSP_UpdateInfo = getSharedPreferences(Constant.SP_UPDATE_INFO, MODE_PRIVATE);
        mSP_UpdateInfoBefore = getSharedPreferences(Constant.SP_UPDATE_INFO_BEFORE, MODE_PRIVATE);
        mSP_LastModified = getSharedPreferences(Constant.SP_LASTMODIFIED, MODE_PRIVATE);
        mSP_UpdateTime = getSharedPreferences(Constant.SP_LUT, MODE_PRIVATE);
        mSP_Login = getSharedPreferences(Constant.SP_LOGIN, MODE_PRIVATE);
        mSP_Sync = getSharedPreferences(Constant.SYNC_DATA, MODE_PRIVATE);
        mSP_DownloadCenter = getSharedPreferences(Constant.SP_DOWNLOAD_CENTER, MODE_PRIVATE);
        mSP_HP = getSharedPreferences(Constant.SP_HP, MODE_PRIVATE);
        mSP_EventPage = getSharedPreferences(Constant.SP_EVENT_PAGE, MODE_PRIVATE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 必不可少。否則平板多語言會混亂
        AppUtil.switchLanguage(getApplicationContext(), AppUtil.getCurLanguage());
    }

    private void initCrashHandle() {
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        Fabric.with(this, new Crashlytics());
    }

    private void getPackageVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            mAppVersion = packageInfo.versionName;
            mVersionCode = packageInfo.versionCode;
            LogUtil.i("App", "mAppVersion=" + mAppVersion + ",mVersionCode=" + mVersionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void getDbHelper() {
        mDBHelper = new DBHelper.Builder(getDaoSession(), daoMaster, db).build();
    }

    public static SQLiteDatabase openDatabase(String dbfile) {
        LogUtil.i(TAG, "openDatabase:" + dbfile);
        SQLiteDatabase db;
        try {
            File dir = new File(DB_PATH);
            if (!dir.exists())
                dir.mkdir();
            if (!(new File(dbfile).exists())) {
                LogUtil.e(TAG, "数据库不存在，从raw中导入");
                // 判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
                InputStream is = resources.openRawResource(R.raw.cps19); // 欲导入的数据库
                FileOutputStream fos = new FileOutputStream(dbfile);
                byte[] buffer = new byte[4000];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
                LogUtil.e(TAG, "Copy db file Success ！");
                db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
            } else {
                LogUtil.e(TAG, "数据库存在，直接打开");
                db = SQLiteDatabase.openDatabase(dbfile, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
            }
            return db;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public DaoSession getDaoSession() {
        LogUtil.i(TAG, "getDaoSession_____________________");
        if (daoMaster == null) {
            daoMaster = getDaoMaster();
            LogUtil.i(TAG, "getDaoSession: daoMaster == null");
        }
        if (daoSession == null) {
            daoSession = daoMaster.newSession();
            LogUtil.i(TAG, "getDaoSession: daoSession == null");
        }
        return daoSession;
    }

    public DaoMaster getDaoMaster() {
        DaoMaster.OpenHelper openHelper = new DaoMaster.DevOpenHelper(getApplicationContext(), DATABASE_NAME, null);
        db = openHelper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        return daoMaster;
    }

    public void finish() {
        finish();
    }

}
