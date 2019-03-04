package com.adsale.ChinaPlas.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.dao.*;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.io.File;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

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
    private boolean isExhibitorMigrate = true;
    private boolean isHistroryMigrate = true;
    private boolean isScheduleMigrate = true;
    private boolean isNameCardMigrate = true;

    public static LoadTransferTempDB getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new LoadTransferTempDB(context);
        }
        return INSTANCE;
    }

    private LoadTransferTempDB(Context context) {
        db = App.mDBHelper.db;
        this.mContext = context;

        com.adsale.ChinaPlas.dao.TempOpenHelper mTempOpenHelper = new com.adsale.ChinaPlas.dao.TempOpenHelper(context, "temp.db", null, 1);
        mTempOpenHelper.getReadableDatabase();
        tempDB = mTempOpenHelper.getWritableDatabase();
    }

    public void deleteOldDB() {
        long startTime = System.currentTimeMillis();
        File dbFile = new File(App.DB_PATH + "/" + DATABASE_NAME);
        if (dbFile.exists()) {
            boolean delete = dbFile.delete();
            LogUtil.i("LoadTransferTempDB", App.DB_PATH + "/" + DATABASE_NAME + "删除 " + (delete ? "成功" : "失败"));
        }
        long endTime = System.currentTimeMillis();
        LogUtil.i(TAG, "deleteOldDB:" + (endTime - startTime) + "ms");
    }

    public void importNewDB() {
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = App.openDatabase(App.DB_PATH + "/" + DATABASE_NAME);
        if (db != null && db.isOpen()) {
            db.close();
        }
        resetDB();
        long endTime = System.currentTimeMillis();
        LogUtil.i(TAG, "importNewDB:" + (endTime - startTime) + "ms");
    }

    private void resetDB() {
        long startTime = System.currentTimeMillis();
        App.mDBHelper.setToNull();
        App.mDBHelper = new DBHelper.Builder(getDaoSession(), daoMaster, db).build();
        long endTime = System.currentTimeMillis();
        LogUtil.i(TAG, "resetDB:" + (endTime - startTime) + "ms");
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

    public Flowable<Boolean> processTempData() {
        return Flowable.create(new FlowableOnSubscribe<Boolean>() {

            @Override
            public void subscribe(FlowableEmitter<Boolean> e) throws Exception {
                long startTime = System.currentTimeMillis();
//                将数据插入临时表
                insertExhibitorTemp();
                insertHistoryTemp();
                insertScheduleTemp();
                insertNameCardTemp();

                deleteOldDB();
                importNewDB();
                insertNewTable();
                clearTemp();
                long endTime = System.currentTimeMillis();
                LogUtil.i(TAG, "processTempData:" + (endTime - startTime) + "ms");

                e.onNext(true);
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER);
    }

    /**
     * select COMPANY_ID,IS_FAVOURITE,NOTE from EXHIBITOR WHERE IS_FAVOURITE=1 OR NOTE!=''
     */
    private void insertExhibitorTemp() {
        long startTime = System.currentTimeMillis();
        StringBuilder sql = new StringBuilder();
        sql.append("select ").append(ExhibitorDao.Properties.CompanyID.columnName)
                .append(",").append(ExhibitorDao.Properties.IsFavourite.columnName)
                .append(",").append(ExhibitorDao.Properties.Note.columnName)
                .append(",").append(ExhibitorDao.Properties.Rate.columnName)
                .append(" from ").append(ExhibitorDao.TABLENAME)
                .append(" WHERE ").append(ExhibitorDao.Properties.IsFavourite.columnName)
                .append(" OR ").append(ExhibitorDao.Properties.Note.columnName).append("!=''");
        Cursor cursor = db.rawQuery(sql.toString(), null);
        ContentValues cv;
        long success = 0;
        if (cursor != null) {
            LogUtil.i(TAG, "cursor.getCount()=" + cursor.getCount());
            if (cursor.getCount() <= 0) {
                isExhibitorMigrate = false;
                return;
            }
            while (cursor.moveToNext()) {
                cv = new ContentValues();
                cv.put("COMPANY_ID", cursor.getString(0));
                cv.put("IS_FAVOURITE", cursor.getString(1));
                cv.put("NOTE", cursor.getString(2));
                cv.put("RATE", cursor.getString(3));
                success = tempDB.insert(EXHIBITOR_TABLE_TEMP, null, cv);
            }
            cursor.close();
        }

        long endTime = System.currentTimeMillis();
        LogUtil.i(TAG, "insertExhibitorTemp:" + (endTime - startTime) + "ms");
    }

    private void insertHistoryTemp() {
        long startTime = System.currentTimeMillis();
        Cursor cursor = db.rawQuery("select * from " + HistoryExhibitorDao.TABLENAME, null);
        ContentValues cv = new ContentValues();
        if (cursor != null) {
            if (cursor.getCount() <= 0) {
                isHistroryMigrate = false;
                return;
            }
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
        long endTime = System.currentTimeMillis();
        LogUtil.i(TAG, "insertHistoryTemp:" + (endTime - startTime) + "ms");
    }

    private void insertScheduleTemp() {
        long startTime = System.currentTimeMillis();
        Cursor cursor = db.rawQuery("select * from " + ScheduleInfoDao.TABLENAME, null);
        ContentValues cv = new ContentValues();
        if (cursor != null) {
            if (cursor.getCount() <= 0) {
                isScheduleMigrate = false;
                return;
            }
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
        long endTime = System.currentTimeMillis();
        LogUtil.i(TAG, "insertScheduleTemp:" + (endTime - startTime) + "ms");
    }

    private void insertNameCardTemp() {
        long startTime = System.currentTimeMillis();
        Cursor cursor = db.rawQuery("select * from NAME_CARD", null);
        ContentValues cv = new ContentValues();
        if (cursor != null) {
            if (cursor.getCount() <= 0) {
                isNameCardMigrate = false;
                return;
            }
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
        long endTime = System.currentTimeMillis();
        LogUtil.i(TAG, "insertNameCardTemp:" + (endTime - startTime) + "ms");
    }

    /**
     * 将数据从临时表中取出来，插入新表
     */
    public void insertNewTable() {
        if (tempDB != null) {
            if (isExhibitorMigrate) {
                getExhibitorTemp();
            }
            if (isHistroryMigrate) {
                getHistoryTemp();
            }
            if (isScheduleMigrate) {
                getScheduleTemp();
            }
            if (isNameCardMigrate) {
                getNameCardTemp();
            }
        } else {
            LogUtil.i("LoadTransferTempDB", "insertNewTable:tempDB==null");
        }

    }

    private void getExhibitorTemp() {
        long startTime = System.currentTimeMillis();
        Cursor cursor = tempDB.rawQuery("select * from " + EXHIBITOR_TABLE_TEMP, null);
        ContentValues cv = new ContentValues();
        String companyId = "";
        int success;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                companyId = cursor.getString(0);
                cv.put(ExhibitorDao.Properties.CompanyID.columnName, companyId);
                cv.put(ExhibitorDao.Properties.IsFavourite.columnName, cursor.getString(1));
                cv.put(ExhibitorDao.Properties.Note.columnName, cursor.getString(2));
                cv.put(ExhibitorDao.Properties.Rate.columnName, cursor.getString(3));
                success = db.update(ExhibitorDao.TABLENAME, cv, ExhibitorDao.Properties.CompanyID.columnName + "=?", new String[]{companyId});
            }
            cursor.close();
        }
        long endTime = System.currentTimeMillis();
        LogUtil.i(TAG, "getExhibitorTemp:" + (endTime - startTime) + "ms");
    }

    private void getHistoryTemp() {
        long startTime = System.currentTimeMillis();
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
        long endTime = System.currentTimeMillis();
        LogUtil.i(TAG, "getHistoryTemp:" + (endTime - startTime) + "ms");
    }

    private void getScheduleTemp() {
        long startTime = System.currentTimeMillis();
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
        long endTime = System.currentTimeMillis();
        LogUtil.i(TAG, "getScheduleTemp:" + (endTime - startTime) + "ms");
    }

    private void getNameCardTemp() {
        long startTime = System.currentTimeMillis();
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
        long endTime = System.currentTimeMillis();
        LogUtil.i(TAG, "getNameCardTemp:" + (endTime - startTime) + "ms");
    }

    public void clearTemp() {
        if (tempDB != null) {
            long startTime = System.currentTimeMillis();
            LogUtil.i("LoadTransferTempDB", "clearTemp: tempDB!=null");
            tempDB.delete(EXHIBITOR_TABLE_TEMP, null, null);
            tempDB.delete(HISTORY_TABLE_TEMP, null, null);
            tempDB.delete(SCHEDULE_TABLE_TEMP, null, null);
            tempDB.delete(NAME_CARD_TABLE_TEMP, null, null);

            tempDB.close();

            long endTime = System.currentTimeMillis();
            LogUtil.i(TAG, "clearTemp:" + (endTime - startTime) + "ms");

//            tempDB.execSQL("drop table "+EXHIBITOR_TABLE_TEMP);
//            tempDB.execSQL("drop table "+HISTORY_TABLE_TEMP);
//            tempDB.execSQL("drop table "+SCHEDULE_TABLE_TEMP);
//            tempDB.execSQL("drop table "+NAME_CARD_TABLE_TEMP);
        } else {
            LogUtil.i("LoadTransferTempDB", "clearTemp: tempDB==null");
        }

    }

    public static void destroyInstance() {
        INSTANCE = null;
    }


}
