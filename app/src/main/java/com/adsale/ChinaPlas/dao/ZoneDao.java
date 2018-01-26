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

    public static final String TABLENAME = "ZONE";

    /**
     * Properties of entity Zone.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property ThemeZoneCode = new Property(0, String.class, "ThemeZoneCode", true, "ThemeZoneCode");
        public final static Property ThemeZoneDescription = new Property(1, String.class, "ThemeZoneDescription", false, "ThemeZoneDescription");
        public final static Property ThemeZoneDescriptionTC = new Property(2, String.class, "ThemeZoneDescriptionTC", false, "ThemeZoneDescriptionTC");
        public final static Property ThemeZoneDescriptionSC = new Property(3, String.class, "ThemeZoneDescriptionSC", false, "ThemeZoneDescriptionSC");
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
        db.execSQL("CREATE TABLE " + constraint + "\"ZONE\" (\"ThemeZoneCode\" TEXT PRIMARY KEY, \"ThemeZoneDescription\" TEXT, \"ThemeZoneDescriptionTC\" TEXT, \"ThemeZoneDescriptionSC\" TEXT)");// 4: SEQ
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

    }

    @Override
    protected String updateKeyAfterInsert(Zone entity, long rowId) {
        return entity.getThemeZoneCode();
    }

    @Override
    protected String getKey(Zone entity) {
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
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }

    /**
     * @inheritdoc
     */
    @Override
    public Zone readEntity(Cursor cursor, int offset) {
        Zone entity = new Zone( //
                cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // ZoneID
                cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // ZoneNameTW
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // ZoneNameEN
                cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3) // ZoneNameCN
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
    }


    /**
     * @inheritdoc
     */
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }

}
