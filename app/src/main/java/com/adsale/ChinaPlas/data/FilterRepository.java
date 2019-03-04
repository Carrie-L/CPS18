package com.adsale.ChinaPlas.data;

import android.database.Cursor;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.dao.Application;
import com.adsale.ChinaPlas.dao.ApplicationDao;
import com.adsale.ChinaPlas.dao.CompanyApplicationDao;
import com.adsale.ChinaPlas.dao.CompanyProductDao;
import com.adsale.ChinaPlas.dao.Country;
import com.adsale.ChinaPlas.dao.CountryDao;
import com.adsale.ChinaPlas.dao.ExhibitorDao;
import com.adsale.ChinaPlas.dao.Map;
import com.adsale.ChinaPlas.dao.MapDao;
import com.adsale.ChinaPlas.dao.Industry;
import com.adsale.ChinaPlas.dao.IndustryDao;
import com.adsale.ChinaPlas.dao.Zone;
import com.adsale.ChinaPlas.dao.ZoneDao;
import com.adsale.ChinaPlas.data.model.Text2;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.LogUtil;

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
    private ApplicationDao mApplicationDao;
    private CountryDao mCountryDao;
    private MapDao mFloorDao;
    private ZoneDao mZoneDao;

    public static FilterRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FilterRepository();
        }
        return INSTANCE;
    }

    /* ------------------- Industry -------------------------- */
    public void initIndustryDao() {
        mIndustryDao = App.mDBHelper.mIndustryDao;
    }

    private void checkIndustryDaoNull() {
        if (mIndustryDao == null) {
//            throw new NullPointerException("mIndustryDao cannot be null, please #initIndustryDao");
            initIndustryDao();
        }
    }

    public ArrayList<Industry> getIndustries(int language, ArrayList<String> letters) {
        checkIndustryDaoNull();
        ArrayList<Industry> list = new ArrayList<>();
//        List<Industry> lett = new ArrayList<>();

//        if (language == 0) {
//            list = (ArrayList<Industry>) mIndustryDao.queryBuilder().orderCustom(IndustryDao.Properties.TCStroke, "ORDER BY cast(TCStroke as int)").list();
//        } else if (language == 1) {
//            list = (ArrayList<Industry>) mIndustryDao.queryBuilder().orderAsc(IndustryDao.Properties.SortEN).list();
//        } else {
//            list = (ArrayList<Industry>) mIndustryDao.queryBuilder().orderAsc(IndustryDao.Properties.SCPY).list();
//        }
//        lett = mIndustryDao.queryBuilder().distinct().orderAsc(IndustryDao.Properties.SortEN).list();
//        LogUtil.i(TAG, "lett= " + lett);

//        String sql = "select * from INDUSTRY order by %s";
        StringBuilder sql = new StringBuilder();
        sql.append("select * from ").append(IndustryDao.TABLENAME).append(" order by ");
        if (language == 0) {
            sql.append("cast(").append(IndustryDao.Properties.TCStroke.columnName).append(" as int)");
        } else if (language == 1) {
            sql.append(IndustryDao.Properties.SortEN.columnName);
        } else {
            sql.append(IndustryDao.Properties.SCPY.columnName);
        }
        LogUtil.i(TAG, "sql= " + sql.toString());
        Cursor cursor = App.mDBHelper.db.rawQuery(sql.toString(), null);
        Industry entity;
        if (cursor != null) {
            while (cursor.moveToNext()) {
//                String sortEN = cursor.getString(cursor.getColumnIndex("SortEN"));
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

    /**
     * SELECT T."CatalogProductSubID",T."CatEng",T."CatTC",T."CatSC",T."TCStroke",T."SCPY",T."SortEN",T."IsDelete",T."createdAt",T."updatedAt",T."IsSelected" FROM "Industry" T  WHERE CatEng like "%y%" or CatTC like "%y%" or CatSC like "%y%"  order by SCPY
     *
     * @param text kw
     * @return ArrayList
     */
    public ArrayList<Industry> getSearchIndustries(String text) {
        checkIndustryDaoNull();
        StringBuilder sbuilder = new StringBuilder();
        sbuilder.append(IndustryDao.Properties.CatEng.columnName).append(" like \"%").append(text).append("%\" or ")
                .append(IndustryDao.Properties.CatTC.columnName).append(" like \"%").append(text).append("%\" or ")
                .append(IndustryDao.Properties.CatSC.columnName).append(" like \"%").append(text).append("%\" ")
                .append(" order by ")
                .append(App.mLanguage.get() == 0 ? "CAST(TCStroke AS INT)" : App.mLanguage.get() == 1 ? IndustryDao.Properties.SortEN.columnName : IndustryDao.Properties.SCPY.columnName);

        return (ArrayList<Industry>) mIndustryDao.queryBuilder().where
                (new WhereCondition.StringCondition(sbuilder.toString())).list();
    }

    /**
     * Exhibitor Dtl
     *
     * @return ArrayList<Text2>
     */
    public ArrayList<Text2> getIndustries(String companyID) {
        List<Industry> industries = mIndustryDao.queryRawCreate("," + CompanyProductDao.TABLENAME + " E where T."
                + IndustryDao.Properties.CatalogProductSubID.columnName + "=E."
                + CompanyProductDao.Properties.CatalogProductSubID.columnName + " AND E."
                + CompanyProductDao.Properties.CompanyID.columnName + "=? ", companyID).list();

        int size = industries.size();
        ArrayList<Text2> texts = new ArrayList<>();
        Industry industry;
        for (int i = 0; i < size; i++) {
            industry = industries.get(i);
            texts.add(new Text2(industry.getCatalogProductSubID(), industry.getIndustryName()));
        }
        return texts;
    }


    /* ------------------- Application -------------------------- */
    public void initAppIndustryDao() {
        mApplicationDao = App.mDBHelper.mApplicationDao;
    }

    private void checkAppIndustryDaoNull() {
        if (mApplicationDao == null) {
            throw new NullPointerException("mApplicationDao cannot be null, please #initAppIndustryDao");
        }
    }

    /**
     * select * from APPLICATION_INDUSTRY where TCSTROKE!="#" order by cast(TCSTROKE as int)
     */
    public ArrayList<Application> getApplicationIndustries() {
        checkAppIndustryDaoNull();
        ArrayList<Application> list;
        if (AppUtil.getCurLanguage() == 0) {
            list = (ArrayList<Application>) mApplicationDao.queryBuilder().where(new WhereCondition.StringCondition(" 1 order by cast(TCStroke as int)")).list();
        } else {
            list = (ArrayList<Application>) mApplicationDao.queryBuilder().orderAsc(ApplicationDao.Properties.SCPY).list();
        }
        return list;
    }

    /**
     * Exhibitor Dtl
     * 与AppCompany表连接查询
     * <p>
     * ,CompanyApplication AS A where T.INDUSTRY_ID=A.INDUSTRY_ID and A.COMPANY_ID=?
     *
     * @return ArrayList<Text2>
     */
    public ArrayList<Text2> queryAppIndustryLists(String companyID) {
        StringBuilder sb = new StringBuilder();
        sb.append(",").append(CompanyApplicationDao.TABLENAME).append(" AS A where T.").append(ApplicationDao.Properties.IndustryID.columnName)
                .append("=A.").append(CompanyApplicationDao.Properties.IndustryID.columnName).append(" and A.").append(CompanyApplicationDao.Properties.CompanyID.columnName).append("=?");
        List<Application> entities = mApplicationDao.queryRawCreate(
//                ",CompanyApplication AS A where T.INDUSTRY_ID=A.INDUSTRY_ID and A.COMPANY_ID=?",
                sb.toString(),
                new Object[]{companyID}).list();

        int size = entities.size();
        LogUtil.i(TAG, "queryAppIndustryLists  size= " + size);
        ArrayList<Text2> texts = new ArrayList<>();
        Application entity;
        for (int i = 0; i < size; i++) {
            entity = entities.get(i);
            texts.add(new Text2(entity.getIndustryID(), entity.getApplicationName()));
        }
        LogUtil.i(TAG, "queryAppIndustryLists= " + texts.size());
        return texts;
    }


    /* ------------------- Country -------------------------- */
    public void initCountryDao() {
        mCountryDao = App.mDBHelper.mCountryDao;
    }

    private void checkCountryDaoNull() {
        if (mCountryDao == null) {
            initCountryDao();
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
        StringBuilder sql = new StringBuilder();
        sql.append("select C.* from Country C WHERE CountryID IN (SELECT DISTINCT ")
                .append(ExhibitorDao.Properties.CountryID.columnName).append(" FROM ")
                .append(ExhibitorDao.TABLENAME).append(" )order by  ");
//        String sql = "select C.* from Country C WHERE CountryID IN (SELECT DISTINCT CountryID FROM Exhibitor )order by ";   //  WHERE CountryID IN (SELECT DISTINCT CountryID FROM Exhibitor )   zzzs 20181129 todo 目前Exhibitor表里貌似没有CountryID字段，导致查询失败
        if (AppUtil.getCurLanguage() == 0) {
            sql = sql.append("cast(SortTC as int)");
        } else if (AppUtil.getCurLanguage() == 1) {
            sql = sql.append(CountryDao.Properties.SortEN.columnName);
        } else {
            sql = sql.append(CountryDao.Properties.SortSC.columnName);
        }
        Cursor cursor = App.mDBHelper.db.rawQuery(sql.toString(), null);
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
        mFloorDao = App.mDBHelper.mMapDao;
    }

    private void checkFloorDaoNull() {
        if (mFloorDao == null) {
            initFloorDao();
        }
    }

    /**
     * select F.FloorNameSC, F.FloorID , count(F.FloorID) as Count,E.HallNo from Map F, EXHIBITOR E where  (E.HallNo = F.FloorID) OR (F.FloorID like "%Y" AND E.HallNo like "%Y") and F.FloorID not like "%csv%"
     group by F.FloorID ORDER BY SEQ
     */
    public ArrayList<Map> getFloorsWiithExhibitor() {
        checkFloorDaoNull();
        ArrayList<Map> floors = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select F.");
        if (App.mLanguage.get() == 0) {
            sql.append(MapDao.Properties.FloorNameTC.columnName);
        } else if (App.mLanguage.get() == 1) {
            sql.append(MapDao.Properties.FloorNameEN.columnName);
        } else {
            sql.append(MapDao.Properties.FloorNameSC.columnName);
        }
        sql.append(", F.").append(MapDao.Properties.FloorID.columnName).append(",count(F.").append(MapDao.Properties.FloorID.columnName)
                .append(") as Count from ").append(MapDao.TABLENAME).append(" F, ").append(ExhibitorDao.TABLENAME)
                .append(" E WHERE (E.HallNo = F.FloorID) OR (F.FloorID like \"%Y\" AND E.HallNo like \"%Y\") and F.FloorID not like \"%csv%\" ")
                .append(" group by F.").append(MapDao.Properties.FloorID.columnName).append(" order by ").append(MapDao.Properties.SEQ.columnName);
        LogUtil.i(TAG, "sql = " + sql.toString());

        Cursor cursor = App.mDBHelper.db.rawQuery(sql.toString(), new String[]{});
        if (cursor != null) {
            Map entity;
            while (cursor.moveToNext()) {
                entity = new Map();
                entity.setFloorName(cursor.getString(0));
                entity.setFloorID(cursor.getString(1));
                entity.count.set(cursor.getInt(2));
                floors.add(entity);
            }
            cursor.close();
        }
        return floors;
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
//        return (ArrayList<Zone>) mZoneDao.loadAll();
    }

}
