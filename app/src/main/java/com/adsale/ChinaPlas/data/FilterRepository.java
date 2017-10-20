package com.adsale.ChinaPlas.data;

import android.database.Cursor;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.dao.ApplicationIndustry;
import com.adsale.ChinaPlas.dao.ApplicationIndustryDao;
import com.adsale.ChinaPlas.dao.Country;
import com.adsale.ChinaPlas.dao.CountryDao;
import com.adsale.ChinaPlas.dao.ExhibitorDao;
import com.adsale.ChinaPlas.dao.Floor;
import com.adsale.ChinaPlas.dao.FloorDao;
import com.adsale.ChinaPlas.dao.HallDao;
import com.adsale.ChinaPlas.dao.Industry;
import com.adsale.ChinaPlas.dao.IndustryDao;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;

import de.greenrobot.dao.query.WhereCondition;

import static android.content.ContentValues.TAG;

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

    public ArrayList<Industry> getIndustries(int language) {
        checkIndustryDaoNull();
        ArrayList<Industry> list;
        if (language == 0) {
            list = (ArrayList<Industry>) mIndustryDao.queryBuilder().orderAsc(IndustryDao.Properties.TCStroke).list();
        } else if (language == 1) {
            list = (ArrayList<Industry>) mIndustryDao.queryBuilder().orderAsc(IndustryDao.Properties.EN_SORT).list();
        } else {
            list = (ArrayList<Industry>) mIndustryDao.queryBuilder().orderAsc(IndustryDao.Properties.SCPY).list();
        }
        LogUtil.i(TAG, "仓库中：getIndustries=" + list.size() + "," + list.toString());
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
            list = (ArrayList<ApplicationIndustry>) mAppIndustryDao.queryBuilder().where(new WhereCondition.StringCondition(" TCSTROKE=\"#\" order by cast(TCSTROKE as int)")).list();
            temps = (ArrayList<ApplicationIndustry>) mAppIndustryDao.queryBuilder().where(new WhereCondition.StringCondition(" TCSTROKE!=\"#\" order by cast(TCSTROKE as int)")).list();
        } else {
            list = (ArrayList<ApplicationIndustry>) mAppIndustryDao.queryBuilder().where(ApplicationIndustryDao.Properties.SCPY.eq("#")).orderAsc(ApplicationIndustryDao.Properties.SCPY).list();
            temps = (ArrayList<ApplicationIndustry>) mAppIndustryDao.queryBuilder().where(ApplicationIndustryDao.Properties.SCPY.notEq("#")).orderAsc(ApplicationIndustryDao.Properties.SCPY).list();
        }
        list.addAll(temps);
        return list;
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

    public ArrayList<Country> getCountries(int language) {
        checkCountryDaoNull();
        ArrayList<Country> list;
        if (language == 0) {
            list = (ArrayList<Country>) mCountryDao.queryBuilder().where(new WhereCondition.StringCondition(" 1 order by cast(SORT_TW as int)")).list();
        } else if (language == 1) {
            list = (ArrayList<Country>) mCountryDao.queryBuilder().orderAsc(CountryDao.Properties.SortEN).list();
        } else {
            list = (ArrayList<Country>) mCountryDao.queryBuilder().orderAsc(CountryDao.Properties.SortCN).list();
        }
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


}
