package com.adsale.ChinaPlas.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.adsale.ChinaPlas.dao.News;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "NEWS".
*/
public class NewsDao extends AbstractDao<News, String> {

    public static final String TABLENAME = "NEWS";

    /**
     * Properties of entity News.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property NewsID = new Property(0, String.class, "NewsID", true, "NEWS_ID");
        public final static Property LType = new Property(1, Integer.class, "LType", false, "LTYPE");
        public final static Property Logo = new Property(2, String.class, "Logo", false, "LOGO");
        public final static Property ShareLink = new Property(3, String.class, "ShareLink", false, "SHARE_LINK");
        public final static Property Title = new Property(4, String.class, "Title", false, "TITLE");
        public final static Property Description = new Property(5, String.class, "Description", false, "DESCRIPTION");
        public final static Property CreateDateTime = new Property(6, String.class, "CreateDateTime", false, "CREATE_DATE_TIME");
        public final static Property UpdateDateTime = new Property(7, String.class, "UpdateDateTime", false, "UPDATE_DATE_TIME");
        public final static Property RecordTimeStamp = new Property(8, String.class, "RecordTimeStamp", false, "RECORD_TIME_STAMP");
        public final static Property PublishDate = new Property(9, String.class, "PublishDate", false, "PUBLISH_DATE");
    };


    public NewsDao(DaoConfig config) {
        super(config);
    }
    
    public NewsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"NEWS\" (" + //
                "\"NEWS_ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: NewsID
                "\"LTYPE\" INTEGER," + // 1: LType
                "\"LOGO\" TEXT," + // 2: Logo
                "\"SHARE_LINK\" TEXT," + // 3: ShareLink
                "\"TITLE\" TEXT," + // 4: Title
                "\"DESCRIPTION\" TEXT," + // 5: Description
                "\"CREATE_DATE_TIME\" TEXT NOT NULL ," + // 6: CreateDateTime
                "\"UPDATE_DATE_TIME\" TEXT NOT NULL ," + // 7: UpdateDateTime
                "\"RECORD_TIME_STAMP\" TEXT NOT NULL ," + // 8: RecordTimeStamp
                "\"PUBLISH_DATE\" TEXT);"); // 9: PublishDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"NEWS\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, News entity) {
        stmt.clearBindings();
 
        String NewsID = entity.getNewsID();
        if (NewsID != null) {
            stmt.bindString(1, NewsID);
        }
 
        Integer LType = entity.getLType();
        if (LType != null) {
            stmt.bindLong(2, LType);
        }
 
        String Logo = entity.getLogo();
        if (Logo != null) {
            stmt.bindString(3, Logo);
        }
 
        String ShareLink = entity.getShareLink();
        if (ShareLink != null) {
            stmt.bindString(4, ShareLink);
        }
 
        String Title = entity.getTitle();
        if (Title != null) {
            stmt.bindString(5, Title);
        }
 
        String Description = entity.getDescription();
        if (Description != null) {
            stmt.bindString(6, Description);
        }
        stmt.bindString(7, entity.getCreateDateTime());
        stmt.bindString(8, entity.getUpdateDateTime());
        stmt.bindString(9, entity.getRecordTimeStamp());
 
        String PublishDate = entity.getPublishDate();
        if (PublishDate != null) {
            stmt.bindString(10, PublishDate);
        }
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public News readEntity(Cursor cursor, int offset) {
        News entity = new News( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // NewsID
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // LType
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // Logo
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // ShareLink
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // Title
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // Description
            cursor.getString(offset + 6), // CreateDateTime
            cursor.getString(offset + 7), // UpdateDateTime
            cursor.getString(offset + 8), // RecordTimeStamp
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9) // PublishDate
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, News entity, int offset) {
        entity.setNewsID(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setLType(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setLogo(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setShareLink(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setTitle(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setDescription(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setCreateDateTime(cursor.getString(offset + 6));
        entity.setUpdateDateTime(cursor.getString(offset + 7));
        entity.setRecordTimeStamp(cursor.getString(offset + 8));
        entity.setPublishDate(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(News entity, long rowId) {
        return entity.getNewsID();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(News entity) {
        if(entity != null) {
            return entity.getNewsID();
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
