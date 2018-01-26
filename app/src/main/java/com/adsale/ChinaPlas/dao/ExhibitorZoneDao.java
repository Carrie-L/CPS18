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

public class ExhibitorZoneDao extends AbstractDao<ExhibitorZone, String> {
    public static final String TABLENAME = "EXHIBITOR_ZONE";

    /**
     * Properties of entity ExhibitorZone.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property ThemeZoneCode = new Property(0, String.class, "ThemeZoneCode", true, "ThemeZoneCode");
        public final static Property CompanyId = new Property(1, String.class, "CompanyId", false, "CompanyId");
    }

    public ExhibitorZoneDao(DaoConfig config) {
        super(config);
    }

    public ExhibitorZoneDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "\"EXHIBITOR_ZONE\" (\"ThemeZoneCode\" TEXT, \"CompanyId\" TEXT PRIMARY KEY);");
    }

    /**
     * Drops the underlying database table.
     */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"EXHIBITOR_ZONE\"";
        db.execSQL(sql);
    }

    /**
     * @inheritdoc
     */
    @Override
    protected void bindValues(SQLiteStatement stmt, ExhibitorZone entity) {
        stmt.clearBindings();

        String ThemeZoneCode = entity.getThemeZoneCode();
        if (ThemeZoneCode != null) {
            stmt.bindString(1, ThemeZoneCode);
        }

        String CompanyId = entity.getCompanyId();
        if (CompanyId != null) {
            stmt.bindString(2, CompanyId);
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
    public ExhibitorZone readEntity(Cursor cursor, int offset) {
        ExhibitorZone entity = new ExhibitorZone( //
                cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // ExhibitorZoneID
                cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1) // ExhibitorZoneNameTW
        );
        return entity;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void readEntity(Cursor cursor, ExhibitorZone entity, int offset) {
        entity.setThemeZoneCode(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setCompanyId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
    }

    /**
     * @inheritdoc
     */
    @Override
    protected String updateKeyAfterInsert(ExhibitorZone entity, long rowId) {
        return entity.getCompanyId();
    }

    /**
     * @inheritdoc
     */
    @Override
    public String getKey(ExhibitorZone entity) {
        if (entity != null) {
            return entity.getCompanyId();
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
