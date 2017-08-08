package com.adsale.ChinaPlas.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.ReleaseHelper;

import java.io.File;

import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.identityscope.IdentityScopeType;

import static com.adsale.ChinaPlas.App.mSP_Config;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * Master of DAO (schema version 1): knows all DAOs.
*/
public class DaoMaster extends AbstractDaoMaster {
    private static final int SCHEMA_VERSION = ReleaseHelper.DB_VERSION;

    /** Creates underlying database table using DAOs. */
    public static void createAllTables(SQLiteDatabase db, boolean ifNotExists) {
        MainIconDao.createTable(db, ifNotExists);
        ApplicationIndustryDao.createTable(db, ifNotExists);
        ApplicationCompanyDao.createTable(db, ifNotExists);
        CountryDao.createTable(db, ifNotExists);
        ExhibitorDao.createTable(db, ifNotExists);
        ExhibitorUserUpdateDao.createTable(db, ifNotExists);
        FloorDao.createTable(db, ifNotExists);
        MapFloorDao.createTable(db, ifNotExists);
        NameCardDao.createTable(db, ifNotExists);
        NewsDao.createTable(db, ifNotExists);
        NewsLinkDao.createTable(db, ifNotExists);
        ProductIDDao.createTable(db, ifNotExists);
        ScheduleInfoDao.createTable(db, ifNotExists);
        UpdateDateDao.createTable(db, ifNotExists);
        WebContentDao.createTable(db, ifNotExists);
        BussinessMappingDao.createTable(db, ifNotExists);
        HistoryExhibitorDao.createTable(db, ifNotExists);
        UpdateCenterDao.createTable(db, ifNotExists);
        ExhibitorIndustryDtlDao.createTable(db, ifNotExists);
        IndustryDao.createTable(db, ifNotExists);
        SeminarInfoDao.createTable(db, ifNotExists);
        SeminarSpeakerDao.createTable(db, ifNotExists);
        FloorPlanCoordinateDao.createTable(db, ifNotExists);
    }
    
    /** Drops underlying database table using DAOs. */
    public static void dropAllTables(SQLiteDatabase db, boolean ifExists) {
        MainIconDao.dropTable(db, ifExists);
        ApplicationIndustryDao.dropTable(db, ifExists);
        ApplicationCompanyDao.dropTable(db, ifExists);
        CountryDao.dropTable(db, ifExists);
        ExhibitorDao.dropTable(db, ifExists);
        ExhibitorUserUpdateDao.dropTable(db, ifExists);
        FloorDao.dropTable(db, ifExists);
        MapFloorDao.dropTable(db, ifExists);
        NameCardDao.dropTable(db, ifExists);
        NewsDao.dropTable(db, ifExists);
        NewsLinkDao.dropTable(db, ifExists);
        ProductIDDao.dropTable(db, ifExists);
        ScheduleInfoDao.dropTable(db, ifExists);
        UpdateDateDao.dropTable(db, ifExists);
        WebContentDao.dropTable(db, ifExists);
        BussinessMappingDao.dropTable(db, ifExists);
        HistoryExhibitorDao.dropTable(db, ifExists);
        UpdateCenterDao.dropTable(db, ifExists);
        ExhibitorIndustryDtlDao.dropTable(db, ifExists);
        IndustryDao.dropTable(db, ifExists);
        SeminarInfoDao.dropTable(db, ifExists);
        SeminarSpeakerDao.dropTable(db, ifExists);
        FloorPlanCoordinateDao.dropTable(db, ifExists);
    }
    
    public static abstract class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
//            createAllTables(db, false);
        }
    }
    
    /** WARNING: Drops all table on Upgrade! Use only during development. */
    public static class DevOpenHelper extends OpenHelper {
        private static final String TAG = "DevOpenHelper";

        public DevOpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory);
            LogUtil.i(TAG,"DevOpenHelper(Context context, String name, CursorFactory factory)");

            App.mSP_Config.edit().putInt("DB_Version",SCHEMA_VERSION).apply();

            SQLiteDatabase db=App.openDatabase(App.DB_PATH+"/"+App.DATABASE_NAME);
            if(db!=null&&db.isOpen()){
                  db.close();
            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//            dropAllTables(db, true);
            LogUtil.e(TAG,"greenDAO"+"Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");

            mSP_Config.edit().putBoolean("DB_UPGRADE",true).apply();


            //更新时，要到这一方法也执行完，才会返回DBHelper
//            onCreate(db);
        }
    }

    public DaoMaster(SQLiteDatabase db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(MainIconDao.class);
        registerDaoClass(ApplicationIndustryDao.class);
        registerDaoClass(ApplicationCompanyDao.class);
        registerDaoClass(CountryDao.class);
        registerDaoClass(ExhibitorDao.class);
        registerDaoClass(ExhibitorUserUpdateDao.class);
        registerDaoClass(FloorDao.class);
        registerDaoClass(MapFloorDao.class);
        registerDaoClass(NameCardDao.class);
        registerDaoClass(NewsDao.class);
        registerDaoClass(NewsLinkDao.class);
        registerDaoClass(ProductIDDao.class);
        registerDaoClass(ScheduleInfoDao.class);
        registerDaoClass(UpdateDateDao.class);
        registerDaoClass(WebContentDao.class);
        registerDaoClass(BussinessMappingDao.class);
        registerDaoClass(HistoryExhibitorDao.class);
        registerDaoClass(UpdateCenterDao.class);
        registerDaoClass(ExhibitorIndustryDtlDao.class);
        registerDaoClass(IndustryDao.class);
        registerDaoClass(SeminarInfoDao.class);
        registerDaoClass(SeminarSpeakerDao.class);
        registerDaoClass(FloorPlanCoordinateDao.class);
    }
    
    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }
    
    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }

    public void createTempTables(){
        HistoryExhibitorDao.createTable(db, false);
    }


    
}
