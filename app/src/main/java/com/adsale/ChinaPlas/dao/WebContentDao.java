package com.adsale.ChinaPlas.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table "WEB_CONTENT".
*/
public class WebContentDao extends AbstractDao<WebContent, String> {

    public static final String TABLENAME = "WebContent";

    /**
     * Properties of entity WebContent.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property ContentID = new Property(0, String.class, "ContentID", true, "ContentID");
        public final static Property FileName = new Property(1, String.class, "FileName", false, "FileName");
        public final static Property CreatedAt = new Property(2, String.class, "createdAt", false, "createdAt");
        public final static Property UpdatedAt = new Property(3, String.class, "updatedAt", false, "updatedAt");
    };


    public WebContentDao(DaoConfig config) {
        super(config);
    }
    
    public WebContentDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"WEB_CONTENT\" (" + //
                "\"CONTENT_ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: ContentID
                "\"FILE_NAME\" TEXT," + // 1: FileName
                "\"CREATED_AT\" TEXT," + // 2: createdAt
                "\"UPDATED_AT\" TEXT);"); // 3: updatedAt
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"WEB_CONTENT\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, WebContent entity) {
        stmt.clearBindings();
 
        String ContentID = entity.getContentID();
        if (ContentID != null) {
            stmt.bindString(1, ContentID);
        }
 
        String FileName = entity.getFileName();
        if (FileName != null) {
            stmt.bindString(2, FileName);
        }
 
        String createdAt = entity.getCreatedAt();
        if (createdAt != null) {
            stmt.bindString(3, createdAt);
        }
 
        String updatedAt = entity.getUpdatedAt();
        if (updatedAt != null) {
            stmt.bindString(4, updatedAt);
        }
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public WebContent readEntity(Cursor cursor, int offset) {
        WebContent entity = new WebContent( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // ContentID
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // FileName
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // createdAt
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3) // updatedAt
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, WebContent entity, int offset) {
        entity.setContentID(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setFileName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCreatedAt(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setUpdatedAt(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(WebContent entity, long rowId) {
        return entity.getContentID();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(WebContent entity) {
        if(entity != null) {
            return entity.getContentID();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
