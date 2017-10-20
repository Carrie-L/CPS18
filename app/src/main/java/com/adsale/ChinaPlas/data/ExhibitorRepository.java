package com.adsale.ChinaPlas.data;

import android.content.ContentValues;
import android.database.Cursor;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.dao.ExhibitorDao;
import com.adsale.ChinaPlas.dao.Industry;
import com.adsale.ChinaPlas.dao.IndustryDao;
import com.adsale.ChinaPlas.dao.SideBar;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.data.model.SideLetter;
import com.adsale.ChinaPlas.data.model.SideLetter;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;

import static android.R.attr.filter;
import static android.R.attr.key;
import static android.R.id.list;
import static android.content.ContentValues.TAG;
import static com.adsale.ChinaPlas.R.id.booth_filter_view;
import static com.adsale.ChinaPlas.R.id.language;
import static com.adsale.ChinaPlas.R.id.list_item;
import static com.adsale.ChinaPlas.utils.AppUtil.getName;
import static com.adsale.ChinaPlas.utils.Constant.EXHIBITOR;

/**
 * Created by Carrie on 2017/8/12.
 */

public class ExhibitorRepository implements DataSource<Exhibitor> {
    private final String TAG = "ExhibitorRepository";
    private static ExhibitorRepository INSTANCE;
    private ExhibitorDao mExhibitorDao = App.mDBHelper.mExhibitorDao;

    public static ExhibitorRepository getInstance() {
        if (INSTANCE == null) {
            return new ExhibitorRepository();
        }
        return INSTANCE;
    }

    /**
     * 获取所有参展商的侧边列表
     */
    public ArrayList<SideLetter> getAllExhiLetters() {
        ArrayList<SideLetter> bars = new ArrayList<>();
        // SideBar列表及排序
        String sql = "select distinct " + getStroke() + " from EXHIBITOR " + orderByStroke();
        return getOrderedIndexList(bars, sql, null);


    }

    public String getAllSideSQL() {
        int language = AppUtil.getCurLanguage();
        if (language == 0) {
            return "select distinct STROKE_TRAD from EXHIBITOR order by cast(STROKE_TRAD as int)";
        } else if (language == 1) {
            return "select distinct STROKE_ENG from EXHIBITOR order by STROKE_ENG";
        } else {
            return "select distinct PYSIMP from EXHIBITOR order by PYSIMP";
        }
    }

    public String getSearchSideSQL(String keyword) {
        String sql = "select distinct ".concat(getStroke()).concat(" from EXHIBITOR where COMPANY_NAME_EN like \"%@%\" or COMPANY_NAME_EN like \"%@%\" or COMPANY_NAME_EN like \"%@%\" or BOOTH_NO like \"%@%\"".concat((orderByStroke())));
        sql = sql.replaceAll("@",keyword);
        LogUtil.i(TAG, "getSearchSideSQL: " + sql);
        return sql;
    }

    public ArrayList<String> getSideList(String sql) {
        ArrayList<String> letters = new ArrayList<>();
        Cursor cursor = App.mDBHelper.db.rawQuery(sql, null);
        String letter;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                letter = cursor.getString(0);
                if (letter.contains("#")) {
                    letter = "#";
                }
                letters.add(letter);
            }
        }
        return letters;
    }

    /**
     * select distinct STROKE_TRAD From EXHIBITOR WHERE COMPANY_NAME_TW like "%4.1%" or BOOTH_NO like "%4.1%" order by cast (STROKE_TRAD as INT)
     *
     * @param keyword
     */
    public ArrayList<String> getSearchedLetters(String keyword) {
        return getSideList(getSearchSideSQL(keyword));
    }

    public ArrayList<Exhibitor> getExhibitorSearchResults(ArrayList<Exhibitor> exhibitors,
                                                          String keyword) {
        ArrayList<Exhibitor> exhibitorsTemps = new ArrayList<>();
        int size = exhibitors.size();
        Exhibitor exhibitor;
        keyword = keyword.toLowerCase(Locale.getDefault());
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                exhibitor = exhibitors.get(i);
                if (exhibitor.getCompanyNameCN().toLowerCase(Locale.getDefault()).contains(keyword)
                        || exhibitor.getCompanyNameEN().toLowerCase(Locale.getDefault()).contains(keyword)
                        || exhibitor.getCompanyNameTW().toLowerCase(Locale.getDefault()).contains(keyword)
                        || exhibitor.getBoothNo().toLowerCase(Locale.getDefault()).contains(keyword)) {
                    exhibitorsTemps.add(exhibitor);
                }
            }
        }
        LogUtil.i(TAG, "exhibitorsTemps=" + exhibitorsTemps.size());
        return exhibitorsTemps;
    }

    public ArrayList<Exhibitor> getDataTest() {
        long startTime = System.currentTimeMillis();
        ArrayList<Exhibitor> temps = new ArrayList<>();
        ArrayList<Exhibitor> allList = new ArrayList<>();
        int language = AppUtil.getCurLanguage();

        if (language == 0) {
            temps = (ArrayList<Exhibitor>) mExhibitorDao.queryBuilder()
                    .where(ExhibitorDao.Properties.StrokeTrad.eq("#"))
                    .orderAsc(ExhibitorDao.Properties.StrokeTrad)
                    .orderAsc(ExhibitorDao.Properties.SeqTC).list();
            allList = (ArrayList<Exhibitor>) mExhibitorDao.queryBuilder().where(new WhereCondition.StringCondition(
                    "STROKE_TRAD!=\"#\" order by CAST(STROKE_TRAD AS INT) ASC,CAST(SEQ_TC AS INT) ASC")).list();
            // orderAsc(ExhibitorDao.Properties.StrokeTrad).orderAsc(ExhibitorDao.Properties.SeqTC).list();
        } else if (language == 1) {
            temps = (ArrayList<Exhibitor>) mExhibitorDao.queryBuilder()
                    .where(ExhibitorDao.Properties.StrokeEng.eq("#")).orderAsc(ExhibitorDao.Properties.StrokeEng)
                    .orderAsc(ExhibitorDao.Properties.SeqEN).list();
            allList = (ArrayList<Exhibitor>) mExhibitorDao.queryBuilder()
                    .where(ExhibitorDao.Properties.StrokeEng.notEq("#")).orderAsc(ExhibitorDao.Properties.StrokeEng)
                    .orderAsc(ExhibitorDao.Properties.SeqEN).list();
        } else {
            temps = (ArrayList<Exhibitor>) mExhibitorDao.queryBuilder().where(ExhibitorDao.Properties.PYSimp.eq("#"))
                    .orderAsc(ExhibitorDao.Properties.PYSimp).orderAsc(ExhibitorDao.Properties.SeqSC).list();
            allList = (ArrayList<Exhibitor>) mExhibitorDao.queryBuilder()
                    .where(ExhibitorDao.Properties.PYSimp.notEq("#")).orderAsc(ExhibitorDao.Properties.PYSimp)
                    .orderAsc(ExhibitorDao.Properties.SeqSC).list();
        }
        allList.addAll(temps);
        long endTime = System.currentTimeMillis();
        LogUtil.e(TAG, "查询所有数据所花费的时间为：" + (endTime - startTime) + "ms");//1758ms
        return allList;
    }

    public ArrayList<Exhibitor> getData() {
        long startTime = System.currentTimeMillis();
        ArrayList<Exhibitor> allList;
        int language = AppUtil.getCurLanguage();
        if (language == 0) {
            allList = (ArrayList<Exhibitor>) mExhibitorDao.queryBuilder().orderRaw("order by CAST(STROKE_TRAD AS INT) ASC,CAST(SEQ_TC AS INT) ASC").list();
        } else if (language == 1) {
            allList = (ArrayList<Exhibitor>) mExhibitorDao.queryBuilder()
                    .orderAsc(ExhibitorDao.Properties.StrokeEng).orderAsc(ExhibitorDao.Properties.SeqEN).list();
        } else {
            allList = (ArrayList<Exhibitor>) mExhibitorDao.queryBuilder()
                    .orderAsc(ExhibitorDao.Properties.PYSimp).orderAsc(ExhibitorDao.Properties.SeqSC).list();
        }
        long endTime = System.currentTimeMillis();
        LogUtil.e(TAG, "查询所有数据所花费的时间为：" + (endTime - startTime) + "ms");//1758ms
        return allList;
    }

    public void updateIsFavourite(String companyID) {
        ContentValues cv = new ContentValues();
        cv.put("IS_FAVOURITE", 1);
        App.mDBHelper.db.update("EXHIBITOR", cv, "COMPANY_ID=?", new String[]{companyID});
    }

    @Override
    public Exhibitor getItemData(Object obj) {
        return null;
    }

    @Override
    public void updateItemData(Exhibitor entity) {
        mExhibitorDao.update(entity);
    }

    @Override
    public void insertItemData(Exhibitor entity) {

    }

    @Override
    public void deleteItemData(Object obj) {

    }

    private String getCompanyNameColumn() {
        return getName(ExhibitorDao.Properties.CompanyNameTW.columnName,
                ExhibitorDao.Properties.CompanyNameEN.columnName, ExhibitorDao.Properties.CompanyNameCN.columnName);
    }

    private String getBoothColumn() {
        return ExhibitorDao.Properties.BoothNo.columnName;
    }

    /**
     * EXHIBITOR 表的排序字段 ： 简 PYSIMP 英 STROKE_ENG 繁 STROKE_TRAD
     */
    private String getStroke() {
        return getName(ExhibitorDao.Properties.StrokeTrad.columnName,
                ExhibitorDao.Properties.StrokeEng.columnName, ExhibitorDao.Properties.PYSimp.columnName);
    }

    /**
     * EXHIBITOR 表：按Stroke排序 ： 简 PYSIMP 英 STROKE_ENG 繁 STROKE_TRAD
     */
    private String orderByStroke() {
        return getName(" order by CAST(STROKE_TRAD AS INT) ASC,CAST(SEQ_TC AS INT) ASC",
                " ORDER BY STROKE_ENG,SEQ_EN", " ORDER BY PYSIMP,SEQ_SC");
    }

    private ArrayList<SideLetter> getOrderedIndexList(ArrayList<SideLetter> bars, String sql,
                                                      String[] selectionArgs) {
        int language = AppUtil.getCurLanguage();
        ArrayList<SideLetter> temps = new ArrayList<>();
        Cursor cursor = App.mDBHelper.db.rawQuery(sql, selectionArgs);
        String indicator = "";
        SideLetter entity;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                indicator = cursor.getString(0);

                if (indicator.contains("#")) {
                    if (language == 0) {
                        temps.add(new SideLetter(indicator.contains("劃") ? indicator : indicator + Constant.TRAD_STROKE));
                    } else {
                        temps.add(new SideLetter(indicator));
                    }
                } else if (language == 0) {
                    entity = new SideLetter(indicator.contains("劃") ? indicator : indicator + Constant.TRAD_STROKE);
                    bars.add(entity);
                } else {
                    entity = new SideLetter(indicator);
                    bars.add(entity);
                }
            }
            cursor.close();
        }
        bars.addAll(temps);
        return bars;
    }

    /**
     * "select * from EXHIBITOR WHERE HALL_NO IN (%1$s) and COUNTRY_ID in (%2$s) and COMPANY_ID IN (%3$s) and COMPANY_ID IN (%4$s) order by xxx "
     */
    private String filterSql(ArrayList<ExhibitorFilter> filters) {
        int size = filters.size();
        ArrayList<String> halls = new ArrayList<>();
        ArrayList<String> countries = new ArrayList<>();
        ArrayList<String> industriesStr = new ArrayList<>();
        ArrayList<String> appStr = new ArrayList<>();
        ExhibitorFilter filter;
        int index;
        String sql = "select * from EXHIBITOR where 1 ";
        for (int i = 0; i < size; i++) {
            filter = filters.get(i);
            index = filter.index;
            if (index == 3) {
                if (halls.size() == 0) {
                    sql = sql.concat(" and HALL_NO IN (%1$s) ");
                }
                halls.add(filter.id);
            } else if (index == 2) {
                if (countries.size() == 0) {
                    sql = sql.concat(" and COUNTRY_ID in (%2$s) ");
                }
                countries.add(filter.id);
            } else if (index == 0) {
                if (industriesStr.size() == 0) {
                    sql = sql.concat(" and COMPANY_ID IN (%3$s)");
                }
                industriesStr.add(" select COMPANY_ID from EXHIBITOR_INDUSTRY_DTL where CATALOG_PRODUCT_SUB_ID = " + filter.id);
            } else if (index == 1) {
                if (appStr.size() == 0) {
                    sql = sql.concat(" and COMPANY_ID IN (%4$s)");
                }
                appStr.add(" select COMPANY_ID from APPLICATION_COMPANY where INDUSTRY_ID=" + filter.id);
            }
        }
        sql = sql.concat(orderByStroke());
        LogUtil.i(TAG, "sql=" + sql);
        sql = String.format(sql, halls.toString().replace("[", "").replace("]", ""),
                countries.toString().replace("[", "").replace("]", ""),
                industriesStr.toString().replaceAll(",", " intersect").replace("[", "").replace("]", ""),
                appStr.toString().replaceAll(",", " intersect").replace("[", "").replace("]", ""));
        LogUtil.i(TAG, "sql sql=" + sql);
        return sql;
    }

    public ArrayList<Exhibitor> queryFilterExhibitor(ArrayList<ExhibitorFilter> filters, ArrayList<String> letters) {
        ArrayList<Exhibitor> exhibitors = new ArrayList<>();

        Cursor cursor = App.mDBHelper.db.rawQuery(filterSql(filters), null);
        if (cursor != null) {
            Exhibitor exhibitor;
            while (cursor.moveToNext()) {
                exhibitor = new Exhibitor(cursor.getString(cursor.getColumnIndex("COMPANY_ID")),
                        cursor.getString(cursor.getColumnIndex("COMPANY_NAME_EN")), cursor.getString(cursor.getColumnIndex("COMPANY_NAME_TW")), cursor.getString(cursor.getColumnIndex("COMPANY_NAME_CN")),
                        cursor.getString(cursor.getColumnIndex("ADDRESS_E")), cursor.getString(cursor.getColumnIndex("ADDRESS_T")), cursor.getString(cursor.getColumnIndex("ADDRESS_S")),
                        null,
                        cursor.getString(cursor.getColumnIndex("TEL")), cursor.getString(cursor.getColumnIndex("FAX")), cursor.getString(cursor.getColumnIndex("EMAIL")), cursor.getString(cursor.getColumnIndex("WEBSITE")),
                        cursor.getString(cursor.getColumnIndex("COUNTRY_ID")), null, null, null, null, null, null,
                        cursor.getString(cursor.getColumnIndex("BOOTH_NO")),
                        cursor.getString(cursor.getColumnIndex("STROKE_ENG")), cursor.getString(cursor.getColumnIndex("STROKE_TRAD")), cursor.getString(cursor.getColumnIndex("STROKE_SIMP")), cursor.getString(cursor.getColumnIndex("PYSIMP")),
                        null, null, null, null,
                        cursor.getString(cursor.getColumnIndex("DESC_E")), cursor.getString(cursor.getColumnIndex("DESC_S")), cursor.getString(cursor.getColumnIndex("DESC_T")),
                        cursor.getString(cursor.getColumnIndex("PHOTO_FILE_NAME")), cursor.getInt(cursor.getColumnIndex("SEQ_EN")), cursor.getInt(cursor.getColumnIndex("SEQ_TC")), cursor.getInt(cursor.getColumnIndex("SEQ_SC")),
                        cursor.getString(cursor.getColumnIndex("HALL_NO")), cursor.getInt(cursor.getColumnIndex("IS_FAVOURITE")), cursor.getString(cursor.getColumnIndex("NOTE")));
                exhibitors.add(exhibitor);
                letters.add(exhibitor.getSort());
            }
            LogUtil.i(TAG, "exhibitors= " + exhibitors.size() + "," + exhibitors.toString());
            cursor.close();
        }

        ArrayList<String> temps = new ArrayList<>(new LinkedHashSet<>(letters));
        letters.clear();
        letters.addAll(temps);
        LogUtil.i(TAG, "letters= " + letters.size() + "," + letters.toString());

        return exhibitors;
    }


}
