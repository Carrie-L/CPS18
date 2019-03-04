package com.adsale.ChinaPlas.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * Created by Carrie on 2018/1/26.
 */

public class ZoneDao extends AbstractDao<Zone, String> {

    public static final String TABLENAME = "Zone";

    /**
     * Properties of entity Zone.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property ThemeZoneCode = new Property(0, String.class, "ThemeZoneCode", true, "ThemeZoneCode");
        public final static Property ThemeZoneDescription = new Property(1, String.class, "ThemeZoneDescription", false, "ThemeZoneDescription");
        public final static Property ThemeZoneDescriptionTC = new Property(2, String.class, "ThemeZoneDescriptionTC", false, "ThemeZoneDescriptionTC");
        public final static Property ThemeZoneDescriptionSC = new Property(3, String.class, "ThemeZoneDescriptionSC", false, "ThemeZoneDescriptionSC");
        public final static Property IsDelete = new Property(4, Boolean.class, "IsDelete", false, "IsDelete");
        public final static Property CreatedAt = new Property(5, String.class, "createdAt", false, "createdAt");
        public final static Property UpdatedAt = new Property(6, String.class, "updatedAt", false, "updatedAt");
    }

    ;


    public ZoneDao(DaoConfig config) {
        super(config);
    }

    public ZoneDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "\"Zone\" (" + //
                "\"ThemeZoneCode\" TEXT PRIMARY KEY NOT NULL ," + // 0: ThemeZoneCode
                "\"ThemeZoneDescription\" TEXT," + // 1: ThemeZoneDescription
                "\"ThemeZoneDescriptionTC\" TEXT," + // 2: ThemeZoneDescriptionTC
                "\"ThemeZoneDescriptionSC\" TEXT," + // 3: ThemeZoneDescriptionSC
                "\"IsDelete\" INTEGER," + // 4: IsDelete
                "\"createdAt\" TEXT," + // 5: createdAt
                "\"updatedAt\" TEXT);"); // 6: updatedAt
    }

    /**
     * Drops the underlying database table.
     */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"ZONE\"";
        db.execSQL(sql);
    }

    /**
     * @inheritdoc
     */
    @Override
    protected void bindValues(SQLiteStatement stmt, Zone entity) {
        stmt.clearBindings();

        String ThemeZoneCode = entity.getThemeZoneCode();
        if (ThemeZoneCode != null) {
            stmt.bindString(1, ThemeZoneCode);
        }

        String ThemeZoneDescription = entity.getThemeZoneDescription();
        if (ThemeZoneDescription != null) {
            stmt.bindString(2, ThemeZoneDescription);
        }

        String ThemeZoneDescriptionTC = entity.getThemeZoneDescriptionTC();
        if (ThemeZoneDescriptionTC != null) {
            stmt.bindString(3, ThemeZoneDescriptionTC);
        }

        String ThemeZoneDescriptionSC = entity.getThemeZoneDescriptionSC();
        if (ThemeZoneDescriptionSC != null) {
            stmt.bindString(4, ThemeZoneDescriptionSC);
        }

        Boolean IsDelete = entity.getIsDelete();
        if (IsDelete != null) {
            stmt.bindLong(5, IsDelete ? 1L : 0L);
        }

        String createdAt = entity.getCreatedAt();
        if (createdAt != null) {
            stmt.bindString(6, createdAt);
        }

        String updatedAt = entity.getUpdatedAt();
        if (updatedAt != null) {
            stmt.bindString(7, updatedAt);
        }
    }

    /**
     * @inheritdoc
     */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }

    /**
     * @inheritdoc
     */
    @Override
    public Zone readEntity(Cursor cursor, int offset) {
        Zone entity = new Zone( //
                cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // ThemeZoneCode
                cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // ThemeZoneDescription
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // ThemeZoneDescriptionTC
                cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // ThemeZoneDescriptionSC
                cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4) != 0, // IsDelete
                cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // createdAt
                cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6) // updatedAt
        );
        return entity;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void readEntity(Cursor cursor, Zone entity, int offset) {
        entity.setThemeZoneCode(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setThemeZoneDescription(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setThemeZoneDescriptionTC(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setThemeZoneDescriptionSC(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setIsDelete(cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4) != 0);
        entity.setCreatedAt(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setUpdatedAt(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
    }

    /**
     * @inheritdoc
     */
    @Override
    protected String updateKeyAfterInsert(Zone entity, long rowId) {
        return entity.getThemeZoneCode();
    }

    /**
     * @inheritdoc
     */
    @Override
    public String getKey(Zone entity) {
        if (entity != null) {
            return entity.getThemeZoneCode();
        } else {
            return null;
        }
    }

    /**
     * @inheritdoc
     */
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }

}
