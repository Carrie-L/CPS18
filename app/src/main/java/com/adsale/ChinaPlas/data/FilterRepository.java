package com.adsale.ChinaPlas.data;

import android.database.Cursor;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.dao.ApplicationIndustry;
import com.adsale.ChinaPlas.dao.ApplicationIndustryDao;
import com.adsale.ChinaPlas.dao.Country;
import com.adsale.ChinaPlas.dao.CountryDao;
import com.adsale.ChinaPlas.dao.ExhibitorIndustryDtlDao;
import com.adsale.ChinaPlas.dao.Floor;
import com.adsale.ChinaPlas.dao.FloorDao;
import com.adsale.ChinaPlas.dao.Industry;
import com.adsale.ChinaPlas.dao.IndustryDao;
import com.adsale.ChinaPlas.dao.Zone;
import com.adsale.ChinaPlas.dao.ZoneDao;
import com.adsale.ChinaPlas.data.model.Text2;
import com.adsale.ChinaPlas.utils.AppUtil;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import de.greenrobot.dao.query.WhereCondition;

/**
 * Created by Carrie on 2017/10/17.
 * 5个筛选列表的数据库操作
 */

public class FilterRepository {
    private final String TAG = "FilterRepository";
    private static FilterRepository INSTANCE;
    private IndustryDao mIndustryDao;
    private ApplicationIndustryDao mAppIndustryDao;
    private CountryDao mCountryDao;
    private FloorDao mFloorDao;
    private ZoneDao mZoneDao;

    public static FilterRepository getInstance() {
        if (INSTANCE == null) {
            return new FilterRepository();
        }
        return INSTANCE;
    }

    /* ------------------- Industry -------------------------- */
    public void initIndustryDao() {
        mIndustryDao = App.mDBHelper.mIndustryDao;
    }

    private void checkIndustryDaoNull() {
        if (mIndustryDao == null) {
            throw new NullPointerException("mIndustryDao cannot be null, please #initIndustryDao");
        }
    }

    public ArrayList<Industry> getIndustries(int language, ArrayList<String> letters) {
        checkIndustryDaoNull();
        ArrayList<Industry> list = new ArrayList<>();
        String sql = "select * from INDUSTRY order by %s";
        sql = String.format(sql, language == 0 ? "TCSTROKE" : language == 1 ? "EN_SORT" : "SCPY");
        Cursor cursor = App.mDBHelper.db.rawQuery(sql, null);
        Industry entity;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                entity = mIndustryDao.readEntity(cursor, 0);
                list.add(entity);
                letters.add(entity.getSort());
            }
            cursor.close();
        }
        //去重
        ArrayList<String> temps = new ArrayList<>(new LinkedHashSet<>(letters));
        letters.clear();
        letters.addAll(temps);
        return list;
    }

    public ArrayList<Industry> getSearchIndustries(String text) {
        checkIndustryDaoNull();
        return (ArrayList<Industry>) mIndustryDao.queryBuilder().where
                (new WhereCondition.StringCondition(" CAT_ENG like \"%" + text + "%\" or CAT_TC like \"%" + text + "%\" or CAT_SC like \"%" + text + "%\" order by ".concat(orderBy())))
                .list();
    }

    public ArrayList<String> getIndustryLetters() {
        int language = AppUtil.getCurLanguage();
        ArrayList<String> list = new ArrayList<>();
        String sql;
        if (language == 0) {
            sql = "select distinct TCSTROKE from INDUSTRY order by TCSTROKE";
        } else if (language == 1) {
            sql = "select distinct EN_SORT from INDUSTRY order by EN_SORT";
        } else {
            sql = "select distinct SCPY from INDUSTRY order by SCPY";
        }
        Cursor cursor = App.mDBHelper.db.rawQuery(sql, null);
        String sort = "";
        if (cursor != null) {
            while (cursor.moveToNext()) {
                sort = cursor.getString(0);
                if (language == 1 && sort.contains("#")) {
                    sort = "#";
                }
                list.add(sort);
            }
            cursor.close();
        }
        return list;
    }

    private String orderBy() {
        return AppUtil.getName("TCSTROKE ASC", "EN_SORT ASC", "SCPY ASC");
    }

    /**
     * Exhibitor Dtl
     *
     * @return ArrayList<Text2>
     */
    public ArrayList<Text2> getIndustries(String companyID) {
        List<Industry> industries = mIndustryDao.queryRawCreate("," + ExhibitorIndustryDtlDao.TABLENAME + " E where T."
                + IndustryDao.Properties.CatalogProductSubID.columnName + "=E."
                + ExhibitorIndustryDtlDao.Properties.CatalogProductSubID.columnName + " AND E."
                + ExhibitorIndustryDtlDao.Properties.CompanyID.columnName + "=? ", companyID).list();

        int size = industries.size();
        ArrayList<Text2> texts = new ArrayList<>();
        Industry industry;
        for (int i = 0; i < size; i++) {
            industry = industries.get(i);
            texts.add(new Text2(industry.getCatalogProductSubID(), industry.getIndustryName()));
        }
        return texts;
    }


    /* ------------------- ApplicationIndustry -------------------------- */
    public void initAppIndustryDao() {
        mAppIndustryDao = App.mDBHelper.mAppIndustryDao;
    }

    private void checkAppIndustryDaoNull() {
        if (mAppIndustryDao == null) {
            throw new NullPointerException("mAppIndustryDao cannot be null, please #initAppIndustryDao");
        }
    }

    /**
     * select * from APPLICATION_INDUSTRY where TCSTROKE!="#" order by cast(TCSTROKE as int)
     */
    public ArrayList<ApplicationIndustry> getApplicationIndustries() {
        checkAppIndustryDaoNull();
        ArrayList<ApplicationIndustry> list;
        ArrayList<ApplicationIndustry> temps;
        if (AppUtil.getCurLanguage() == 0) {
            list = (ArrayList<ApplicationIndustry>) mAppIndustryDao.queryBuilder().where(new WhereCondition.StringCondition(" TCSTROKE!=\"#\" order by cast(TCSTROKE as int)")).list();
            temps = (ArrayList<ApplicationIndustry>) mAppIndustryDao.queryBuilder().where(new WhereCondition.StringCondition(" TCSTROKE=\"#\" order by cast(TCSTROKE as int)")).list();
        } else {
            list = (ArrayList<ApplicationIndustry>) mAppIndustryDao.queryBuilder().where(ApplicationIndustryDao.Properties.SCPY.notEq("#")).orderAsc(ApplicationIndustryDao.Properties.SCPY).list();
            temps = (ArrayList<ApplicationIndustry>) mAppIndustryDao.queryBuilder().where(ApplicationIndustryDao.Properties.SCPY.eq("#")).orderAsc(ApplicationIndustryDao.Properties.SCPY).list();
        }
        list.addAll(temps);
        return list;
    }

    /**
     * Exhibitor Dtl
     * 与AppCompany表连接查询
     *
     * @return ArrayList<Text2>
     */
    public ArrayList<Text2> queryAppIndustryLists(String companyID) {
        List<ApplicationIndustry> entities = mAppIndustryDao.queryRawCreate(
                ",APPLICATION_COMPANY AS A where T.INDUSTRY_ID=A.INDUSTRY_ID and A.COMPANY_ID=?",
                new Object[]{companyID}).list();

        int size = entities.size();
        ArrayList<Text2> texts = new ArrayList<>();
        ApplicationIndustry entity;
        for (int i = 0; i < size; i++) {
            entity = entities.get(i);
            texts.add(new Text2(entity.getIndustryID(), entity.getApplicationName()));
        }
        return texts;
    }


    /* ------------------- Country -------------------------- */
    public void initCountryDao() {
        mCountryDao = App.mDBHelper.mCountryDao;
    }

    private void checkCountryDaoNull() {
        if (mCountryDao == null) {
            throw new NullPointerException("mCountryDao cannot be null, please #initCountryDao");
        }
    }

    /**
     * TC: select C.* from COUNTRY C WHERE COUNTRY_ID IN (SELECT DISTINCT COUNTRY_ID FROM EXHIBITOR ) order by cast(SORT_TW as int)
     * EN: order by SORT_EN
     * CN: order by SORT_CN
     *
     * @return
     */
    public ArrayList<Country> getCountries(ArrayList<String> letters) {
        checkCountryDaoNull();
        String sql = "select C.* from COUNTRY C WHERE COUNTRY_ID IN (SELECT DISTINCT COUNTRY_ID FROM EXHIBITOR ) order by ";
        if (AppUtil.getCurLanguage() == 0) {
            sql = sql.concat("cast(SORT_TW as int)");
        } else if (AppUtil.getCurLanguage() == 1) {
            sql = sql.concat("SORT_EN");
        } else {
            sql = sql.concat("SORT_CN");
        }
        Cursor cursor = App.mDBHelper.db.rawQuery(sql, null);
        ArrayList<Country> list = new ArrayList<>();
        Country entity;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                entity = mCountryDao.readEntity(cursor, 0);
                list.add(entity);
                letters.add(entity.getSort());
            }
            cursor.close();
        }
        //去重
        ArrayList<String> tempsAZ = new ArrayList<>(new LinkedHashSet<>(letters));
        letters.clear();
        letters.addAll(tempsAZ);

        return list;
    }

    /**
     * @return ArrayList<String>
     */
    public ArrayList<String> getCountryLetters() {
        int language = AppUtil.getCurLanguage();
        ArrayList<String> list = new ArrayList<>();
        String sql;
        if (language == 0) {
            sql = "select distinct SORT_TW from COUNTRY order by cast(SORT_TW as int)";
        } else if (language == 1) {
            sql = "select distinct SORT_EN from COUNTRY order by SORT_EN";
        } else {
            sql = "select distinct SORT_CN from COUNTRY order by SORT_CN";
        }
        Cursor cursor = App.mDBHelper.db.rawQuery(sql, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(cursor.getString(0));
            }
            cursor.close();
        }
        return list;
    }


    /* ------------------- Hall -------------------------- */
    public void initFloorDao() {
        mFloorDao = App.mDBHelper.mFloorDao;
    }

    private void checkFloorDaoNull() {
        if (mFloorDao == null) {
            throw new NullPointerException("mFloorDao cannot be null, please #initFloorDao");
        }
    }

    public ArrayList<Floor> getFloors() {
        checkFloorDaoNull();
        return (ArrayList<Floor>) mFloorDao.queryBuilder().orderAsc(FloorDao.Properties.SEQ).list();
    }

    /* ------------------- Zone -------------------------- */
    private void checkZoneDaoNull() {
        if (mZoneDao == null) {
            mZoneDao = App.mDBHelper.mZoneDao;
        }
    }

    public ArrayList<Zone> getZones() {
        checkZoneDaoNull();
        if (App.mLanguage.get() == 0) {
            return (ArrayList<Zone>) mZoneDao.queryBuilder().where(ZoneDao.Properties.ThemeZoneDescriptionTC.notEq("NULL")).list();
        } else if (App.mLanguage.get() == 1) {
            return (ArrayList<Zone>) mZoneDao.queryBuilder().where(ZoneDao.Properties.ThemeZoneDescription.notEq("NULL")).list();
        } else {
            return (ArrayList<Zone>) mZoneDao.queryBuilder().where(ZoneDao.Properties.ThemeZoneDescriptionSC.notEq("NULL")).list();
        }
    }

}
