package com.adsale.ChinaPlas.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table "MAIN_ICON".
 */
public class MainIconDao extends AbstractDao<MainIcon, String> {

    public static final String TABLENAME = "MainIcon";

    /**
     * Properties of entity MainIcon.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property IconID = new Property(0, String.class, "IconID", true, "IconID");
        public final static Property TitleTW = new Property(1, String.class, "TitleTW", false, "TitleTW");
        public final static Property TitleCN = new Property(2, String.class, "TitleCN", false, "TitleCN");
        public final static Property TitleEN = new Property(3, String.class, "TitleEN", false, "TitleEN");
        public final static Property MenuSeq = new Property(4, String.class, "MenuSeq", false, "MenuSeq");
        public final static Property DrawerSeq = new Property(5, String.class, "DrawerSeq", false, "DrawerSeq");
        public final static Property BaiDu_TJ = new Property(6, String.class, "BaiDu_TJ", false, "BaiDu_TJ");
        public final static Property Google_TJ = new Property(7, String.class, "Google_TJ", false, "Google_TJ");
        public final static Property CFile = new Property(8, String.class, "CFile", false, "CFile");
        public final static Property DrawerIcon = new Property(9, String.class, "DrawerIcon", false, "DrawerIcon");
        public final static Property Icon = new Property(10, String.class, "Icon", false, "Icon");
        public final static Property IsDelete = new Property(11, Boolean.class, "IsDelete", false, "IsDelete");
        public final static Property IsHidden = new Property(12, Boolean.class, "IsHidden", false, "IsHidden");
        public final static Property CreatedAt = new Property(13, String.class, "createdAt", false, "createdAt");
        public final static Property UpdatedAt = new Property(14, String.class, "updatedAt", false, "updatedAt");
    }

    public MainIconDao(DaoConfig config) {
        super(config);
    }

    public MainIconDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        //CREATE TABLE MainIcon (
//        IconID TEXT,TitleTW TEXT NOT NULL ,TitleCN TEXT NOT NULL ,TitleEN TEXT NOT NULL ,MenuSeq TEXT,DrawerSeq TEXT,BaiDu_TJ TEXT,Google_TJ TEXT,CFile TEXT,DrawerIcon TEXTIcon TEXT,IsDelete INTEGER,IsHidden INTEGER,createdAt INTEGER,updatedAt INTEGER);

        db.execSQL(""); // 14: updatedAt
    }

    /**
     * Drops the underlying database table.
     */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MAIN_ICON\"";
        db.execSQL(sql);
    }

    /**
     * @inheritdoc
     */
    @Override
    protected void bindValues(SQLiteStatement stmt, MainIcon entity) {
        stmt.clearBindings();

        String IconID = entity.getIconID();
        if (IconID != null) {
            stmt.bindString(1, IconID);
        }
        stmt.bindString(2, entity.getTitleTW());
        stmt.bindString(3, entity.getTitleCN());
        stmt.bindString(4, entity.getTitleEN());

        String MenuSeq = entity.getMenuSeq();
        if (MenuSeq != null) {
            stmt.bindString(5, MenuSeq);
        }

        String DrawerSeq = entity.getDrawerSeq();
        if (DrawerSeq != null) {
            stmt.bindString(6, DrawerSeq);
        }

        String BaiDu_TJ = entity.getBaiDu_TJ();
        if (BaiDu_TJ != null) {
            stmt.bindString(7, BaiDu_TJ);
        }

        String Google_TJ = entity.getGoogle_TJ();
        if (Google_TJ != null) {
            stmt.bindString(8, Google_TJ);
        }

        String CFile = entity.getCFile();
        if (CFile != null) {
            stmt.bindString(9, CFile);
        }

        String DrawerIcon = entity.getDrawerIcon();
        if (DrawerIcon != null) {
            stmt.bindString(10, DrawerIcon);
        }

        String Icon = entity.getIcon();
        if (Icon != null) {
            stmt.bindString(11, Icon);
        }

        Boolean IsDelete = entity.getIsDelete();
        if (IsDelete != null) {
            stmt.bindLong(12, IsDelete ? 1L : 0L);
        }

        Boolean IsHidden = entity.getIsHidden();
        if (IsHidden != null) {
            stmt.bindLong(13, IsHidden ? 1L : 0L);
        }

        String createdAt = entity.getCreatedAt();
        if (createdAt != null) {
            stmt.bindString(14, createdAt);
        }

        String updatedAt = entity.getUpdatedAt();
        if (updatedAt != null) {
            stmt.bindString(15, updatedAt);
        }
//
//        Date createdAt = entity.getCreatedAt();
//        if (createdAt != null) {
//            stmt.bindLong(14, createdAt.getTime());
//        }
//
//        java.util.Date updatedAt = entity.getUpdatedAt();
//        if (updatedAt != null) {
//            stmt.bindLong(15, updatedAt.getTime());
//        }
    }

    /**
     * @inheritdoc
     */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return null;
    }

    /**
     * @inheritdoc
     */
    @Override
    public MainIcon readEntity(Cursor cursor, int offset) {
        MainIcon entity = new MainIcon( //
                cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // IconID
                cursor.getString(offset + 1), // TitleTW
                cursor.getString(offset + 2), // TitleCN
                cursor.getString(offset + 3), // TitleEN
                cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // MenuSeq
                cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // DrawerSeq
                cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // BaiDu_TJ
                cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // Google_TJ
                cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // CFile
                cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // DrawerIcon
                cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // Icon
                cursor.isNull(offset + 11) ? null : cursor.getShort(offset + 11) != 0, // IsDelete
                cursor.isNull(offset + 12) ? null : cursor.getShort(offset + 12) != 0, // IsHidden
                cursor.isNull(offset + 13) ? null :cursor.getString(offset + 13), // createdAt
                cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14) // updatedAt
        );
        return entity;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void readEntity(Cursor cursor, MainIcon entity, int offset) {
        entity.setIconID(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setTitleTW(cursor.getString(offset + 1));
        entity.setTitleCN(cursor.getString(offset + 2));
        entity.setTitleEN(cursor.getString(offset + 3));
        entity.setMenuSeq(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setDrawerSeq(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setBaiDu_TJ(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setGoogle_TJ(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setCFile(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setDrawerIcon(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setIcon(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setIsDelete(cursor.isNull(offset + 11) ? null : cursor.getShort(offset + 11) != 0);
        entity.setIsHidden(cursor.isNull(offset + 12) ? null : cursor.getShort(offset + 12) != 0);
        entity.setCreatedAt(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setUpdatedAt(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
    }

    /**
     * @inheritdoc
     */
    @Override
    protected String updateKeyAfterInsert(MainIcon entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }

    /**
     * @inheritdoc
     */
    @Override
    public String getKey(MainIcon entity) {
        return null;
    }

    /**
     * @inheritdoc
     */
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }

}



