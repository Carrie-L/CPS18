package com.adsale.ChinaPlas.dao;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.adsale.ChinaPlas.utils.LogUtil;


/**
 * Created by new on 2017/3/15.
 */

public class TempOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = "TempOpenHelper";

    public TempOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public TempOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LogUtil.i(TAG, "onCreate创建临时表");
        createExhibitorTempTable(db);
        createHistoryTempTable(db);
        createScheduleTempTable(db);
        createNameCardTempTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createExhibitorTempTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE EXHIBITOR_TEMP (COMPANY_ID TEXT PRIMARY KEY,IS_FAVOURITE INTEGER,NOTE TEXT,RATE INTEGER)");
    }

    private void createScheduleTempTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE \"SCHEDULE_INFO_TEMP\" \n" +
                "(\"_id\" INTEGER PRIMARY KEY ,\"TITLE\" TEXT,\"NOTE\" TEXT,\n" +
                "\"LOCATION\" TEXT,\"COMPANY_ID\" TEXT,\"START_DATE\" TEXT,\"START_TIME\" TEXT,\"HOUR\" INTEGER,\"MINUTE\" INTEGER, EVENT_CID TEXT);"); // 8: EVENT_CID
    }

    private void createHistoryTempTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + "\"HISTORY_EXHIBITOR_TEMP\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"COMPANY_ID\" TEXT," + // 1: CompanyID
                "\"COMPANY_NAME_EN\" TEXT," + // 2: CompanyNameEN
                "\"COMPANY_NAME_CN\" TEXT," + // 3: CompanyNameCN
                "\"COMPANY_NAME_TW\" TEXT," + // 4: CompanyNameTW
                "\"BOOTH\" TEXT," + // 5: Booth
                "\"TIME\" TEXT);"); // 6: Time
    }

    private void createNameCardTempTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + "\"NAME_CARD_TEMP\" (" + //
                "\"DEVICE_ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: DeviceId
                "\"COMPANY\" TEXT NOT NULL ," + // 1: Company
                "\"NAME\" TEXT NOT NULL ," + // 2: Name
                "\"TITLE\" TEXT NOT NULL ," + // 3: Title
                "\"PHONE\" TEXT NOT NULL ," + // 4: Phone
                "\"EMAIL\" TEXT NOT NULL ," + // 5: Email
                "\"QQ\" TEXT," + // 6: QQ
                "\"WE_CHAT\" TEXT," + // 7: WeChat
                "\"UPDATE_DATE_TIME\" TEXT);"); // 8: UpdateDateTime
    }
}
