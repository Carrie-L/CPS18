package com.adsale.ChinaPlas.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.dao.DBHelper;
import com.adsale.ChinaPlas.dao.DaoMaster;
import com.adsale.ChinaPlas.dao.DaoSession;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.dao.ExhibitorDao;
import com.adsale.ChinaPlas.dao.HistoryExhibitorDao;
import com.adsale.ChinaPlas.dao.NameCardDao;
import com.adsale.ChinaPlas.dao.ScheduleInfoDao;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.io.File;
import java.util.List;

import static com.adsale.ChinaPlas.App.DATABASE_NAME;

/**
 * Created by Carrie on 2017/9/18.
 * ① 将数据存入临时表
 * ② 导入新数据库
 * ③ 从临时表中取出数据，插入新数据库中:
 * ④ 清空临时表
 */

public class LoadTransferTempDB {
    private SQLiteDatabase tempDB;
    public SQLiteDatabase db;
    private Context mContext;
    private final String HISTORY_TABLE_TEMP = "HISTORY_EXHIBITOR_TEMP";
    private final String SCHEDULE_TABLE_TEMP = "SCHEDULE_INFO_TEMP";
    private final String EXHIBITOR_TABLE_TEMP = "EXHIBITOR_TEMP";
    private final String NAME_CARD_TABLE_TEMP = "NAME_CARD_TEMP";

    private static LoadTransferTempDB INSTANCE;
    private static final String TAG = "LoadTransferTempDB";

    public static LoadTransferTempDB getInstance(Context context, SQLiteDatabase tempDB) {
        if (INSTANCE == null) {
            return new LoadTransferTempDB(context, tempDB);
        }
        return INSTANCE;
    }

    private LoadTransferTempDB(Context context, SQLiteDatabase tempDB) {
        db = App.mDBHelper.db;
        this.tempDB = tempDB;
        this.mContext = context;
    }

    public void deleteOldDB() {
        File dbFile = new File(App.DB_PATH + "/" + DATABASE_NAME);
        if (dbFile.exists()) {
            boolean delete = dbFile.delete();
            LogUtil.i("LoadTransferTempDB", App.DB_PATH + "/" + DATABASE_NAME + "删除 " + (delete ? "成功" : "失败"));
        }
    }

    public void importNewDB() {
        SQLiteDatabase db = App.openDatabase(App.DB_PATH + "/" + DATABASE_NAME);
        if (db != null && db.isOpen()) {
            db.close();
        }
        resetDB();
    }

    private void resetDB() {
        App.mDBHelper.setToNull();
        App.mDBHelper = new DBHelper.Builder(getDaoSession(), daoMaster, db).build();
    }

    private DaoMaster daoMaster;
    private DaoSession daoSession;

    private DaoSession getDaoSession() {
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

    private DaoMaster getDaoMaster() {
        DaoMaster.OpenHelper openHelper = new DaoMaster.DevOpenHelper(mContext, DATABASE_NAME, null);
        db = openHelper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        return daoMaster;
    }

    /**
     * 将数据插入临时表
     */
    public void saveTempData() {
        insertExhibitorTemp();
        insertHistoryTemp();
        insertScheduleTemp();
        insertNameCardTemp();
    }

    private void insertExhibitorTemp() {
        Cursor cursor = db.rawQuery("select COMPANY_ID,IS_FAVOURITE,NOTE from EXHIBITOR WHERE IS_FAVOURITE=1 OR NOTE NOT NULL", null);
        if (App.mDBHelper.db != null) {
            LogUtil.i("LoadTransferTempDB", "tempDB!=null");
        } else {
            LogUtil.i("LoadTransferTempDB", "tempDB==null");
        }
        ContentValues cv;
        long success = 0;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                cv = new ContentValues();
                cv.put("COMPANY_ID", cursor.getString(0));
                cv.put("IS_FAVOURITE", cursor.getString(1));
                cv.put("NOTE", cursor.getString(2));
                success = tempDB.insert(EXHIBITOR_TABLE_TEMP, null, cv);
            }
            cursor.close();
        }
    }

    private void insertHistoryTemp() {
        Cursor cursor = db.rawQuery("select * from HISTORY_EXHIBITOR", null);
        ContentValues cv = new ContentValues();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                cv.put("_id", cursor.getString(0));
                cv.put("COMPANY_ID", cursor.getString(1));
                cv.put("COMPANY_NAME_EN", cursor.getString(2));
                cv.put("COMPANY_NAME_CN", cursor.getString(3));
                cv.put("COMPANY_NAME_TW", cursor.getString(4));
                cv.put("BOOTH", cursor.getString(5));
                cv.put("TIME", cursor.getString(6));
                tempDB.insert(HISTORY_TABLE_TEMP, null, cv);
            }
            cursor.close();
        }
    }

    private void insertScheduleTemp() {
        Cursor cursor = db.rawQuery("select * from SCHEDULE_INFO", null);
        ContentValues cv = new ContentValues();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                cv.put("_id", cursor.getString(0));
                cv.put("TITLE", cursor.getString(1));
                cv.put("NOTE", cursor.getString(2));
                cv.put("LOCATION", cursor.getString(3));
                cv.put("COMPANY_ID", cursor.getString(4));
                cv.put("START_TIME", cursor.getString(5));
                cv.put("LENGTH", cursor.getInt(6));
                cv.put("ALLDAY", cursor.getInt(7));
                cv.put("EVENT_CID", cursor.getString(8));
                tempDB.insert(SCHEDULE_TABLE_TEMP, null, cv);
            }
            cursor.close();
        }
    }

    private void insertNameCardTemp() {
        Cursor cursor = db.rawQuery("select * from NAME_CARD", null);
        ContentValues cv = new ContentValues();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                cv.put("DEVICE_ID", cursor.getString(0));
                cv.put("COMPANY", cursor.getString(1));
                cv.put("NAME", cursor.getString(2));
                cv.put("TITLE", cursor.getString(3));
                cv.put("PHONE", cursor.getString(4));
                cv.put("EMAIL", cursor.getString(5));
                cv.put("QQ", cursor.getInt(6));
                cv.put("WE_CHAT", cursor.getInt(7));
                cv.put("UPDATE_DATE_TIME", cursor.getString(8));
                tempDB.insert(NAME_CARD_TABLE_TEMP, null, cv);
            }
            cursor.close();
        }
    }

    /**
     * 将数据从临时表中取出来，插入新表
     */
    public void insertNewTable() {
        if (tempDB != null) {
            getExhibitorTemp();
            getHistoryTemp();
            getScheduleTemp();
            getNameCardTemp();
        } else {
            LogUtil.i("LoadTransferTempDB", "insertNewTable:tempDB==null");
        }

    }

    private void getExhibitorTemp() {
        Cursor cursor = tempDB.rawQuery("select * from " + EXHIBITOR_TABLE_TEMP, null);
        ContentValues cv = new ContentValues();
        String companyId = "";
        int success;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                companyId = cursor.getString(0);
                cv.put("COMPANY_ID", companyId);
                cv.put("IS_FAVOURITE", cursor.getString(1));
                cv.put("NOTE", cursor.getString(2));
                success = db.update(ExhibitorDao.TABLENAME, cv, "COMPANY_ID=?", new String[]{companyId});
            }
            cursor.close();
        }

        List<Exhibitor> exhibitors = App.mDBHelper.mExhibitorDao.queryBuilder().where(ExhibitorDao.Properties.IsFavourite.eq(1)).list();
        LogUtil.i("LoadTransferTempDB", "exhibitors=" + exhibitors.size() + "," + exhibitors.toString());
    }

    private void getHistoryTemp() {
        Cursor cursor = tempDB.rawQuery("select * from " + HISTORY_TABLE_TEMP, null);
        ContentValues cv = new ContentValues();
        long success;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                cv.put("_id", cursor.getString(0));
                cv.put("COMPANY_ID", cursor.getString(1));
                cv.put("COMPANY_NAME_EN", cursor.getString(2));
                cv.put("COMPANY_NAME_CN", cursor.getString(3));
                cv.put("COMPANY_NAME_TW", cursor.getString(4));
                cv.put("BOOTH", cursor.getString(5));
                cv.put("TIME", cursor.getString(6));

                success = db.insert(HistoryExhibitorDao.TABLENAME, null, cv);
            }
            cursor.close();
        }
    }

    private void getScheduleTemp() {
        Cursor cursor = tempDB.rawQuery("select * from " + SCHEDULE_TABLE_TEMP, null);
        ContentValues cv = new ContentValues();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                cv.put("_id", cursor.getString(0));
                cv.put("TITLE", cursor.getString(1));
                cv.put("NOTE", cursor.getString(2));
                cv.put("LOCATION", cursor.getString(3));
                cv.put("COMPANY_ID", cursor.getString(4));
                cv.put("START_TIME", cursor.getString(5));
                cv.put("LENGTH", cursor.getInt(6));
                cv.put("ALLDAY", cursor.getInt(7));
                cv.put("EVENT_CID", cursor.getString(8));
                db.insert(ScheduleInfoDao.TABLENAME, null, cv);
            }
            cursor.close();
        }
    }

    private void getNameCardTemp() {
        Cursor cursor = tempDB.rawQuery("select * from " + NAME_CARD_TABLE_TEMP, null);
        ContentValues cv = new ContentValues();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                cv.put("DEVICE_ID", cursor.getString(0));
                cv.put("COMPANY", cursor.getString(1));
                cv.put("NAME", cursor.getString(2));
                cv.put("TITLE", cursor.getString(3));
                cv.put("PHONE", cursor.getString(4));
                cv.put("EMAIL", cursor.getString(5));
                cv.put("QQ", cursor.getInt(6));
                cv.put("WE_CHAT", cursor.getInt(7));
                cv.put("UPDATE_DATE_TIME", cursor.getString(8));
                db.insert(NameCardDao.TABLENAME, null, cv);
            }
            cursor.close();
        }
    }

    public void clearTemp() {
        if (tempDB != null) {
            LogUtil.i("LoadTransferTempDB", "clearTemp: tempDB!=null");
            tempDB.delete(EXHIBITOR_TABLE_TEMP, null, null);
            tempDB.delete(HISTORY_TABLE_TEMP, null, null);
            tempDB.delete(SCHEDULE_TABLE_TEMP, null, null);
            tempDB.delete(NAME_CARD_TABLE_TEMP, null, null);

//            tempDB.execSQL("drop table "+EXHIBITOR_TABLE_TEMP);
//            tempDB.execSQL("drop table "+HISTORY_TABLE_TEMP);
//            tempDB.execSQL("drop table "+SCHEDULE_TABLE_TEMP);
//            tempDB.execSQL("drop table "+NAME_CARD_TABLE_TEMP);
        } else {
            LogUtil.i("LoadTransferTempDB", "clearTemp: tempDB==null");
        }

    }


}
