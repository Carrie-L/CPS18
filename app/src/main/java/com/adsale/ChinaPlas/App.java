package com.adsale.ChinaPlas;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.ObservableInt;
import android.net.ConnectivityManager;
import android.os.Environment;

import com.adsale.ChinaPlas.dao.DBHelper;
import com.adsale.ChinaPlas.dao.DaoMaster;
import com.adsale.ChinaPlas.dao.DaoSession;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.CrashHandler;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by Carrie on 2017/8/8.
 */

public class App extends Application {
    private static final String TAG = "App";

//    public static final ObservableInt language=new ObservableInt(0);

    public static SharedPreferences mSP_Config;
    public static SharedPreferences mSP_UpdateInfo;
    public static SharedPreferences mSP_UpdateInfoBefore;
    public static SharedPreferences mSP_LastModified;
    public static SharedPreferences mSP_UpdateTime;
    public static SharedPreferences mSP_Login;

    public static final String DATABASE_NAME = "cps18.db";

    private DaoMaster daoMaster;
    public SQLiteDatabase db;
    public static String mAppVersion;
    public static int mVersionCode;
    private long mDiaryId;
    private DaoSession daoSession;
    public static DBHelper mDBHelper;
    public static String DB_PATH = "";// 在手机里存放数据库的位置
    private static Resources resources;
    public static String rootDir, filesDir;
    public static ConnectivityManager mConnectivityManager;
    public static AssetManager mAssetManager;

    public static OkHttpClient mOkHttpClient;

    public static final ObservableInt mLanguage = new ObservableInt();

    //    public static String memoryFileDir;

    @Override
    public void onCreate() {
        super.onCreate();
        initCrashHandle();
        initSP();
        resources = getResources();
        DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/" + getPackageName() + "/databases";

        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        rootDir = getDir("cps18", MODE_PRIVATE).getAbsolutePath() + "/";
        filesDir = getFilesDir().getAbsolutePath() + "/";// /data/user/0/com.adsale.ChinaPlas/files/
        LogUtil.i(TAG, "rootDir=" + rootDir);
        LogUtil.i(TAG, "filesDir=" + filesDir);

        mAssetManager = getAssets();

        mOkHttpClient = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).build();

        getDbHelper();
    }

    private void initSP() {
        mSP_Config = getSharedPreferences(Constant.SP_CONFIG, MODE_PRIVATE);
        mSP_UpdateInfo = getSharedPreferences(Constant.SP_UPDATE_INFO, MODE_PRIVATE);
        mSP_UpdateInfoBefore = getSharedPreferences(Constant.SP_UPDATE_INFO_BEFORE, MODE_PRIVATE);
        mSP_LastModified = getSharedPreferences(Constant.SP_LASTMODIFIED, MODE_PRIVATE);
        mSP_UpdateTime = getSharedPreferences(Constant.SP_LUT, MODE_PRIVATE);
        mSP_Login = getSharedPreferences(Constant.SP_LOGIN, MODE_PRIVATE);
    }

    private void initCrashHandle() {
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
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

    private void checkUpdateDB() {
        boolean hasUpdate = mSP_Config.getBoolean("DB_UPDATE", false);
        if (hasUpdate) {
//            updateDB();
            if (mDiaryId > 0) {//因为id从1开始自增，所以如果插入成功，返回的id一定是大于0的
                mSP_Config.edit().putBoolean("DB_UPDATE", false).apply();
            }
        }
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
                InputStream is = resources.openRawResource(R.raw.cps18); // 欲导入的数据库
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
