package com.adsale.ChinaPlas.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table "NAME__CARD__TEMP".
*/
public class NAME_CARD_TEMPDao extends AbstractDao<NAME_CARD_TEMP, String> {

    public static final String TABLENAME = "NAME__CARD__TEMP";

    /**
     * Properties of entity NAME_CARD_TEMP.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property DEVICE_ID = new Property(0, String.class, "DEVICE_ID", true, "DEVICE__ID");
        public final static Property COMPANY = new Property(1, int.class, "COMPANY", false, "COMPANY");
        public final static Property NAME = new Property(2, String.class, "NAME", false, "NAME");
        public final static Property TITLE = new Property(3, String.class, "TITLE", false, "TITLE");
        public final static Property PHONE = new Property(4, String.class, "PHONE", false, "PHONE");
        public final static Property EMAIL = new Property(5, String.class, "EMAIL", false, "EMAIL");
        public final static Property QQ = new Property(6, Integer.class, "QQ", false, "QQ");
        public final static Property WE_CHAT = new Property(7, Integer.class, "WE_CHAT", false, "WE__CHAT");
        public final static Property UPDATE_DATE_TIME = new Property(8, String.class, "UPDATE_DATE_TIME", false, "UPDATE__DATE__TIME");
    }


    public NAME_CARD_TEMPDao(DaoConfig config) {
        super(config);
    }
    
    public NAME_CARD_TEMPDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"NAME__CARD__TEMP\" (" + //
                "\"DEVICE__ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: DEVICE_ID
                "\"COMPANY\" INTEGER NOT NULL ," + // 1: COMPANY
                "\"NAME\" TEXT NOT NULL ," + // 2: NAME
                "\"TITLE\" TEXT NOT NULL ," + // 3: TITLE
                "\"PHONE\" TEXT NOT NULL ," + // 4: PHONE
                "\"EMAIL\" TEXT NOT NULL ," + // 5: EMAIL
                "\"QQ\" INTEGER," + // 6: QQ
                "\"WE__CHAT\" INTEGER," + // 7: WE_CHAT
                "\"UPDATE__DATE__TIME\" TEXT);"); // 8: UPDATE_DATE_TIME
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"NAME__CARD__TEMP\"";
        db.execSQL(sql);
    }



    @Override
    protected final void bindValues(SQLiteStatement stmt, NAME_CARD_TEMP entity) {
        stmt.clearBindings();
 
        String DEVICE_ID = entity.getDEVICE_ID();
        if (DEVICE_ID != null) {
            stmt.bindString(1, DEVICE_ID);
        }
        stmt.bindLong(2, entity.getCOMPANY());
        stmt.bindString(3, entity.getNAME());
        stmt.bindString(4, entity.getTITLE());
        stmt.bindString(5, entity.getPHONE());
        stmt.bindString(6, entity.getEMAIL());
 
        Integer QQ = entity.getQQ();
        if (QQ != null) {
            stmt.bindLong(7, QQ);
        }
 
        Integer WE_CHAT = entity.getWE_CHAT();
        if (WE_CHAT != null) {
            stmt.bindLong(8, WE_CHAT);
        }
 
        String UPDATE_DATE_TIME = entity.getUPDATE_DATE_TIME();
        if (UPDATE_DATE_TIME != null) {
            stmt.bindString(9, UPDATE_DATE_TIME);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public NAME_CARD_TEMP readEntity(Cursor cursor, int offset) {
        NAME_CARD_TEMP entity = new NAME_CARD_TEMP( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // DEVICE_ID
            cursor.getInt(offset + 1), // COMPANY
            cursor.getString(offset + 2), // NAME
            cursor.getString(offset + 3), // TITLE
            cursor.getString(offset + 4), // PHONE
            cursor.getString(offset + 5), // EMAIL
            cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6), // QQ
            cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7), // WE_CHAT
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8) // UPDATE_DATE_TIME
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, NAME_CARD_TEMP entity, int offset) {
        entity.setDEVICE_ID(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setCOMPANY(cursor.getInt(offset + 1));
        entity.setNAME(cursor.getString(offset + 2));
        entity.setTITLE(cursor.getString(offset + 3));
        entity.setPHONE(cursor.getString(offset + 4));
        entity.setEMAIL(cursor.getString(offset + 5));
        entity.setQQ(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
        entity.setWE_CHAT(cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7));
        entity.setUPDATE_DATE_TIME(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
     }
    
    @Override
    protected final String updateKeyAfterInsert(NAME_CARD_TEMP entity, long rowId) {
        return entity.getDEVICE_ID();
    }
    
    @Override
    public String getKey(NAME_CARD_TEMP entity) {
        if(entity != null) {
            return entity.getDEVICE_ID();
        } else {
            return null;
        }
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
