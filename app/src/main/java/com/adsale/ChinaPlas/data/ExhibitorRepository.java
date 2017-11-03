package com.adsale.ChinaPlas.data;

import android.content.ContentValues;
import android.database.Cursor;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.dao.ExhibitorDao;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Locale;

import static com.adsale.ChinaPlas.R.id.language;
import static com.adsale.ChinaPlas.utils.AppUtil.getName;

/**
 * Created by Carrie on 2017/8/12.
 * 展商资料库
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

    public ArrayList<Exhibitor> getExhibitorSearchResults(ArrayList<Exhibitor> exhibitors, ArrayList<String> letters,
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
                    letters.add(exhibitor.getSort());
                }
            }
        }
        LogUtil.i(TAG, "exhibitorsTemps=" + exhibitorsTemps.size());
        ArrayList<String> temps = new ArrayList<>(new LinkedHashSet<>(letters));
        letters.clear();
        letters.addAll(temps);
        return exhibitorsTemps;
    }

    /**
     * select E.COMPANY_ID,E.COMPANY_NAME_EN,E.COMPANY_NAME_TW,E.COMPANY_NAME_CN,E.COUNTRY_ID,E.BOOTH_NO,E.STROKE_ENG,E.STROKE_TRAD,E.STROKE_SIMP,E.PYSIMP,E.SEQ_EN,E.SEQ_TC,E.SEQ_SC,E.HALL_NO,E.IS_FAVOURITE
     * ,C.COUNTRY_NAME_TW AS COUNTRY_NAME from EXHIBITOR E,COUNTRY C WHERE E.COUNTRY_ID=C.COUNTRY_ID order by CAST(STROKE_TRAD AS INT) ASC,CAST(SEQ_TC AS INT) ASC
     */
    public ArrayList<Exhibitor> getAllExhibitors(ArrayList<String> letters) {
        String sql = getExhibitorSql() + orderByStroke();
        return cursorList(sql, letters, true);
    }

    private String getExhibitorSql() {
        int language = App.mLanguage.get();
        if (language == 0) {
            return "select E.COMPANY_ID,E.COMPANY_NAME_EN,E.COMPANY_NAME_TW,E.COMPANY_NAME_CN,E.COUNTRY_ID,E.BOOTH_NO,E.STROKE_TRAD,E.SEQ_TC,E.HALL_NO,E.IS_FAVOURITE\n" +
                    ",C.COUNTRY_NAME_TW AS COUNTRY_NAME from EXHIBITOR E,COUNTRY C WHERE E.COUNTRY_ID=C.COUNTRY_ID ";
        } else if (language == 1) {
            return "select E.COMPANY_ID,E.COMPANY_NAME_EN,E.COMPANY_NAME_TW,E.COMPANY_NAME_CN,E.BOOTH_NO,E.STROKE_ENG,E.SEQ_EN,E.HALL_NO,E.IS_FAVOURITE\n" +
                    ",C.COUNTRY_NAME_EN AS COUNTRY_NAME from EXHIBITOR E,COUNTRY C WHERE E.COUNTRY_ID=C.COUNTRY_ID ";
        } else {
            return "select E.COMPANY_ID,E.COMPANY_NAME_EN,E.COMPANY_NAME_TW,E.COMPANY_NAME_CN,E.BOOTH_NO,E.PYSIMP,E.SEQ_SC,E.HALL_NO,E.IS_FAVOURITE\n" +
                    ",C.COUNTRY_NAME_CN AS COUNTRY_NAME from EXHIBITOR E,COUNTRY C WHERE E.COUNTRY_ID=C.COUNTRY_ID ";
        }
    }

    public ArrayList<Exhibitor> cursorList(String sql, ArrayList<String> letters, boolean orderByAZ) {
        long startTime = System.currentTimeMillis();
        int language = App.mLanguage.get();
        Cursor cursor = App.mDBHelper.db.rawQuery(sql, null);
        ArrayList<Exhibitor> exhibitors = new ArrayList<>();
        if (cursor != null) {
            Exhibitor exhibitor;
            while (cursor.moveToNext()) {
                if (language == 0) {
                    exhibitor = new Exhibitor(cursor.getString(cursor.getColumnIndex("COMPANY_ID")),
                            cursor.getString(cursor.getColumnIndex("COMPANY_NAME_EN")), cursor.getString(cursor.getColumnIndex("COMPANY_NAME_TW")), cursor.getString(cursor.getColumnIndex("COMPANY_NAME_CN")),
                            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                            cursor.getString(cursor.getColumnIndex("BOOTH_NO")),
                            null, cursor.getString(cursor.getColumnIndex("STROKE_TRAD")), null, null,
                            null, null, null, null, null, null, null, null,
                            null, cursor.getInt(cursor.getColumnIndex("SEQ_TC")), null,
                            cursor.getString(cursor.getColumnIndex("HALL_NO")), cursor.getInt(cursor.getColumnIndex("IS_FAVOURITE")), null);
                } else if (language == 1) {
                    exhibitor = new Exhibitor(cursor.getString(cursor.getColumnIndex("COMPANY_ID")),
                            cursor.getString(cursor.getColumnIndex("COMPANY_NAME_EN")), cursor.getString(cursor.getColumnIndex("COMPANY_NAME_TW")), cursor.getString(cursor.getColumnIndex("COMPANY_NAME_CN")),
                            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                            cursor.getString(cursor.getColumnIndex("BOOTH_NO")),
                            cursor.getString(cursor.getColumnIndex("STROKE_ENG")), null, null, null,
                            null, null, null, null, null, null, null, null,
                            cursor.getInt(cursor.getColumnIndex("SEQ_EN")), null, null,
                            cursor.getString(cursor.getColumnIndex("HALL_NO")), cursor.getInt(cursor.getColumnIndex("IS_FAVOURITE")), null);
                } else {
                    exhibitor = new Exhibitor(cursor.getString(cursor.getColumnIndex("COMPANY_ID")),
                            cursor.getString(cursor.getColumnIndex("COMPANY_NAME_EN")), cursor.getString(cursor.getColumnIndex("COMPANY_NAME_TW")), cursor.getString(cursor.getColumnIndex("COMPANY_NAME_CN")),
                            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                            cursor.getString(cursor.getColumnIndex("BOOTH_NO")),
                            null, null, null, cursor.getString(cursor.getColumnIndex("PYSIMP")),
                            null, null, null, null, null, null, null, null,
                            null, null, cursor.getInt(cursor.getColumnIndex("SEQ_SC")),
                            cursor.getString(cursor.getColumnIndex("HALL_NO")), cursor.getInt(cursor.getColumnIndex("IS_FAVOURITE")), null);
                }
                exhibitor.CountryName = cursor.getString(cursor.getColumnIndex("COUNTRY_NAME"));
                exhibitor.isCollected.set(exhibitor.getIsFavourite()==1);
                if (orderByAZ) {
                    letters.add(exhibitor.getSort());
                } else {
                    letters.add(exhibitor.getHallNo());
                    exhibitor.setStroke(language, exhibitor.getHallNo());
                }
                exhibitors.add(exhibitor);
            }
            cursor.close();
        }
        long endTime = System.currentTimeMillis();
        LogUtil.e(TAG, "查询所有展商列表所花费的时间为：" + (endTime - startTime) + "ms");

        long startTime1 = System.currentTimeMillis();
        ArrayList<String> temps = new ArrayList<>(new LinkedHashSet<>(letters));
        letters.clear();
        letters.addAll(temps);
        LogUtil.i(TAG, "letters= " + letters.size() + "," + letters.toString());
        long endTime1 = System.currentTimeMillis();
        LogUtil.e(TAG, "去重letters所花费的时间为：" + (endTime1 - startTime1) + "ms");

        return exhibitors;
    }

    public void updateIsFavourite(String companyID) {
        ContentValues cv = new ContentValues();
        cv.put("IS_FAVOURITE", 1);
        App.mDBHelper.db.update("EXHIBITOR", cv, "COMPANY_ID=?", new String[]{companyID});
    }

    @Override
    public ArrayList<Exhibitor> getData() {
        return null;
    }

    @Override
    public Exhibitor getItemData(Object obj) {
        return mExhibitorDao.load((String) obj);
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


    /**
     * EXHIBITOR 表：按Stroke排序 ： 简 PYSIMP 英 STROKE_ENG 繁 STROKE_TRAD
     */
    private String orderByStroke() {
        return getName(" order by CAST(STROKE_TRAD AS INT) ASC,CAST(SEQ_TC AS INT) ASC",
                " ORDER BY STROKE_ENG,SEQ_EN", " ORDER BY PYSIMP,SEQ_SC");
    }

    /**
     * "select * from EXHIBITOR WHERE HALL_NO IN (%1$s) and COUNTRY_ID in (%2$s) and COMPANY_ID IN (%3$s) and COMPANY_ID IN (%4$s) order by xxx "
     */
    private String filterSql(ArrayList<ExhibitorFilter> filters, boolean orderByAZ) {
        int size = filters.size();
        ArrayList<String> halls = new ArrayList<>();
        ArrayList<String> countries = new ArrayList<>();
        ArrayList<String> industriesStr = new ArrayList<>();
        ArrayList<String> appStr = new ArrayList<>();
        ExhibitorFilter filter;
        int index;
        String sql = getExhibitorSql();
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
                    sql = sql.concat(" and E.COUNTRY_ID in (%2$s) ");
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
        if(orderByAZ){
            sql = sql.concat(orderByStroke());
        }else{
            sql = sql.concat(" order by CAST(HALL_NO AS INT),BOOTH_NO");
        }

        LogUtil.i(TAG, "sql=" + sql);
        sql = String.format(sql, halls.toString().replace("[", "").replace("]", ""),
                countries.toString().replace("[", "").replace("]", ""),
                industriesStr.toString().replaceAll(",", " intersect").replace("[", "").replace("]", ""),
                appStr.toString().replaceAll(",", " intersect").replace("[", "").replace("]", ""));
        LogUtil.i(TAG, "sql sql=" + sql);
        return sql;
    }

    public ArrayList<Exhibitor> queryFilterExhibitor(ArrayList<ExhibitorFilter> filters, ArrayList<String> letters, boolean orderByAZ) {
        return cursorList(filterSql(filters,orderByAZ), letters, orderByAZ);
    }

    public ArrayList<Exhibitor> sortByHallList(ArrayList<String> letters) {
        String sql0 = getExhibitorSql().concat("and HALL_NO LIKE \"%.%\" order by CAST(HALL_NO AS INT),BOOTH_NO");
        String sql1 = getExhibitorSql().concat("and HALL_NO NOT LIKE \"%.%\" order by HALL_NO");

        ArrayList<String> letters0 = new ArrayList<>();
        ArrayList<String> letters1 = new ArrayList<>();
        ArrayList<Exhibitor> exhibitors = new ArrayList<>();
        ArrayList<Exhibitor> exhibitors0 = cursorList(sql0, letters0, false);
        ArrayList<Exhibitor> exhibitors1 = cursorList(sql1, letters1, false);
        exhibitors.addAll(exhibitors0);
        exhibitors.addAll(exhibitors1);
        LogUtil.i(TAG, "exhibitors0= " + exhibitors0.size());
        LogUtil.i(TAG, "exhibitors1= " + exhibitors1.size() + "," + exhibitors1.toString());
        LogUtil.i(TAG, "exhibitors= " + exhibitors.size());
        letters.addAll(letters0);
        letters.addAll(letters1);
        LogUtil.i(TAG, "letters0= " + letters0.size() + "," + letters0.toString());
        LogUtil.i(TAG, "letters1= " + letters1.size() + "," + letters1.toString());
        LogUtil.i(TAG, "letters= " + letters.size() + "," + letters.toString());

        setToNull(exhibitors0);
        setToNull(exhibitors1);
        setToNull(letters0);
        setToNull(letters1);

        return exhibitors;
    }

    private <T> void setToNull(ArrayList<T> list) {
        if (!list.isEmpty()) {
            list.clear();
            list = null;
        }
    }


}
