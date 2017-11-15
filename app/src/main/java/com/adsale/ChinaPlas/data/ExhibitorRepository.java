package com.adsale.ChinaPlas.data;

import android.content.ContentValues;
import android.database.Cursor;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.dao.ExhibitorDao;
import com.adsale.ChinaPlas.dao.HistoryExhibitor;
import com.adsale.ChinaPlas.dao.HistoryExhibitorDao;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Locale;

import static android.text.TextUtils.concat;
import static com.adsale.ChinaPlas.App.mDBHelper;
import static com.adsale.ChinaPlas.utils.AppUtil.getName;

/**
 * Created by Carrie on 2017/8/12.
 * 展商资料库
 */

public class ExhibitorRepository implements DataSource<Exhibitor> {
    private final String TAG = "ExhibitorRepository";
    private static ExhibitorRepository INSTANCE;
    private ExhibitorDao mExhibitorDao = mDBHelper.mExhibitorDao;
    private HistoryExhibitorDao mHistoryExhibitorDao;

    public static ExhibitorRepository getInstance() {
        if (INSTANCE == null) {
            return new ExhibitorRepository();
        }
        return INSTANCE;
    }

    /**
     * <font color="#f97798">根据广告id查找该公司是否在Exhibitor表中存在</font>
     * @param companyID
     * @return boolean
     * @version 创建时间：2016年6月22日 下午3:54:44
     */
    public boolean isExhibitorIDExists(String companyID) {
        return !(mExhibitorDao.load(companyID) == null);
    }

    public ArrayList<Exhibitor> getExhibitorSearchAZ(ArrayList<Exhibitor> allExhibitorsAZ, ArrayList<String> lettersAZ, String keyword) {
        ArrayList<Exhibitor> exhibitorsTemps = new ArrayList<>();
        int size = allExhibitorsAZ.size();
        Exhibitor exhibitor;
        keyword = keyword.toLowerCase(Locale.getDefault());
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                exhibitor = allExhibitorsAZ.get(i);
                if (exhibitor.getCompanyNameCN().toLowerCase(Locale.getDefault()).contains(keyword)
                        || exhibitor.getCompanyNameEN().toLowerCase(Locale.getDefault()).contains(keyword)
                        || exhibitor.getCompanyNameTW().toLowerCase(Locale.getDefault()).contains(keyword)
                        || exhibitor.getBoothNo().toLowerCase(Locale.getDefault()).contains(keyword)) {
                    exhibitorsTemps.add(exhibitor);
                    lettersAZ.add(exhibitor.getSort());
                }
            }
        }
        LogUtil.i(TAG, "exhibitorsTemps=" + exhibitorsTemps.size());
        //去重
        ArrayList<String> tempsAZ = new ArrayList<>(new LinkedHashSet<>(lettersAZ));
        lettersAZ.clear();
        lettersAZ.addAll(tempsAZ);
        return exhibitorsTemps;
    }

    public ArrayList<Exhibitor> getExhibitorSearchHalls(ArrayList<Exhibitor> allExhibitorsHall, ArrayList<String> lettersHall, String keyword) {
        ArrayList<Exhibitor> exhibitorsTemps = new ArrayList<>();
        int size = allExhibitorsHall.size();
        Exhibitor exhibitor;
        keyword = keyword.toLowerCase(Locale.getDefault());
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                exhibitor = allExhibitorsHall.get(i);
                if (exhibitor.getCompanyNameCN().toLowerCase(Locale.getDefault()).contains(keyword)
                        || exhibitor.getCompanyNameEN().toLowerCase(Locale.getDefault()).contains(keyword)
                        || exhibitor.getCompanyNameTW().toLowerCase(Locale.getDefault()).contains(keyword)
                        || exhibitor.getBoothNo().toLowerCase(Locale.getDefault()).contains(keyword)) {
                    exhibitorsTemps.add(exhibitor);
                    lettersHall.add(exhibitor.getHallNo());
                }
            }
        }
        LogUtil.i(TAG, "exhibitorsTemps=" + exhibitorsTemps.size());
        //去重
        ArrayList<String> tempsHall = new ArrayList<>(new LinkedHashSet<>(lettersHall));
        lettersHall.clear();
        lettersHall.addAll(tempsHall);

        LogUtil.i(TAG, "lettersHall before: >>>>  " + lettersHall.toString());

        AppUtil.sort(lettersHall, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        LogUtil.i(TAG, "lettersHall after: >>>>  \n " + lettersHall.toString());
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
            return "select E.COMPANY_ID,E.COMPANY_NAME_EN,E.COMPANY_NAME_TW,E.COMPANY_NAME_CN,E.COUNTRY_ID,E.BOOTH_NO,E.STROKE_TRAD,E.SEQ_TC,E.HALL_NO,E.IS_FAVOURITE,E.DESC_E,E.DESC_S,E.DESC_T\n" +
                    ",C.COUNTRY_NAME_TW AS COUNTRY_NAME from EXHIBITOR E,COUNTRY C WHERE E.COUNTRY_ID=C.COUNTRY_ID ";
        } else if (language == 1) {
            return "select E.COMPANY_ID,E.COMPANY_NAME_EN,E.COMPANY_NAME_TW,E.COMPANY_NAME_CN,E.BOOTH_NO,E.STROKE_ENG,E.SEQ_EN,E.HALL_NO,E.IS_FAVOURITE,E.DESC_E,E.DESC_S,E.DESC_T\n" +
                    ",C.COUNTRY_NAME_EN AS COUNTRY_NAME from EXHIBITOR E,COUNTRY C WHERE E.COUNTRY_ID=C.COUNTRY_ID ";
        } else {
            return "select E.COMPANY_ID,E.COMPANY_NAME_EN,E.COMPANY_NAME_TW,E.COMPANY_NAME_CN,E.BOOTH_NO,E.PYSIMP,E.SEQ_SC,E.HALL_NO,E.IS_FAVOURITE,E.DESC_E,E.DESC_S,E.DESC_T\n" +
                    ",C.COUNTRY_NAME_CN AS COUNTRY_NAME from EXHIBITOR E,COUNTRY C WHERE E.COUNTRY_ID=C.COUNTRY_ID ";
        }
    }

    private ArrayList<Exhibitor> cursorList(String sql, ArrayList<String> letters, boolean orderByAZ) {
        long startTime = System.currentTimeMillis();
        int language = App.mLanguage.get();
        Cursor cursor = mDBHelper.db.rawQuery(sql, null);
        ArrayList<Exhibitor> exhibitors = new ArrayList<>();
        if (cursor != null) {
            Exhibitor exhibitor;
            while (cursor.moveToNext()) {
                exhibitor = new Exhibitor();
                exhibitor.setCompanyID(cursor.getString(cursor.getColumnIndex("COMPANY_ID")));
                exhibitor.setCompanyNameEN(cursor.getString(cursor.getColumnIndex("COMPANY_NAME_EN")));
                exhibitor.setCompanyNameCN(cursor.getString(cursor.getColumnIndex("COMPANY_NAME_CN")));
                exhibitor.setCompanyNameTW(cursor.getString(cursor.getColumnIndex("COMPANY_NAME_TW")));
                exhibitor.setBoothNo(cursor.getString(cursor.getColumnIndex("BOOTH_NO")));
                exhibitor.setHallNo(cursor.getString(cursor.getColumnIndex("HALL_NO")).replace("999", ""));
                exhibitor.setIsFavourite(cursor.getInt(cursor.getColumnIndex("IS_FAVOURITE")));
                exhibitor.CountryName = cursor.getString(cursor.getColumnIndex("COUNTRY_NAME"));
                exhibitor.isCollected.set(exhibitor.getIsFavourite() == 1);
                if (language == 0) {
                    exhibitor.setStrokeTrad(cursor.getString(cursor.getColumnIndex("STROKE_TRAD")));
                    exhibitor.setSeqTC(cursor.getInt(cursor.getColumnIndex("SEQ_TC")));
                } else if (language == 1) {
                    exhibitor.setStrokeEng(cursor.getString(cursor.getColumnIndex("STROKE_ENG")));
                    exhibitor.setSeqEN(cursor.getInt(cursor.getColumnIndex("SEQ_EN")));
                } else {
                    exhibitor.setPYSimp(cursor.getString(cursor.getColumnIndex("PYSIMP")));
                    exhibitor.setSeqSC(cursor.getInt(cursor.getColumnIndex("SEQ_SC")));
                }
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
        mDBHelper.db.update("EXHIBITOR", cv, "COMPANY_ID=?", new String[]{companyID});
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
     * "select * from EXHIBITOR WHERE HALL_NO IN (%1$s) and COUNTRY_ID in (%2$s) and COMPANY_ID IN (%3$s) and COMPANY_ID IN (%4$s) and (E.DESC_E LIKE "%%" OR E.DESC_S LIKE "%%" OR E.DESC_T LIKE "%%") order by xxx "
     */
    private String filterSql(ArrayList<ExhibitorFilter> filters, boolean orderByAZ) {
        int size = filters.size();
        ArrayList<String> halls = new ArrayList<>();
        ArrayList<String> countries = new ArrayList<>();
        ArrayList<String> industriesStr = new ArrayList<>();
        ArrayList<String> appStr = new ArrayList<>();
        ExhibitorFilter filter;
        String keyword = "";
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
            } else if (index == 5) { // keyword
                keyword = filter.filter;
                sql = sql.concat(" and (carriecps)");
//                sql = sql.concat(" and (E.DESC_E LIKE '\u0025").concat(filter.filter).concat("\u0025' OR E.DESC_S LIKE '\u0025").concat(filter.filter).concat("\u0025' OR E.DESC_T LIKE '\u0025").concat(filter.filter).concat("\u0025')");
            }
        }
        if (orderByAZ) {
            sql = sql.concat(orderByStroke());
        } else {
            sql = sql.concat(" order by CAST(HALL_NO AS INT),BOOTH_NO");
        }
        sql = String.format(sql, halls.toString().replace("[", "").replace("]", ""),
                countries.toString().replace("[", "").replace("]", ""),
                industriesStr.toString().replaceAll(",", " intersect").replace("[", "").replace("]", ""),
                appStr.toString().replaceAll(",", " intersect").replace("[", "").replace("]", ""));
        sql = sql.replaceAll("carriecps", "E.DESC_E LIKE '%".concat(keyword).concat("%' OR E.DESC_S LIKE '%").concat(keyword).concat("%' OR E.DESC_T LIKE '%").concat(keyword).concat("%'"));
        LogUtil.i(TAG, ">>>> sql=" + sql);
        return sql;
    }

    public ArrayList<Exhibitor> queryFilterExhibitor(ArrayList<ExhibitorFilter> filters, ArrayList<String> letters, boolean orderByAZ) {
        return cursorList(filterSql(filters, orderByAZ), letters, orderByAZ);
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

    /* -------------------------------展商历史记录HistoryExhibitor--------------------------------------------------- */
    public void initHistoryDao() {
        mHistoryExhibitorDao = App.mDBHelper.mHistoryExhibitorDao;
    }

    private void checkHistoryDao() {
        if (mHistoryExhibitorDao == null) {
            throw new NullPointerException("mHistoryExhibitorDao cannot be null, please initHistoryDao first.");
        }
    }

    public void insertHistoryExhiItem(HistoryExhibitor entity) {
        checkHistoryDao();
        mHistoryExhibitorDao.insert(entity);
    }

    public ArrayList<HistoryExhibitor> getAllHistoryExhibitors(int offset) {
        checkHistoryDao();
        ArrayList<HistoryExhibitor> list = (ArrayList<HistoryExhibitor>) mHistoryExhibitorDao.queryBuilder()
                .orderDesc(HistoryExhibitorDao.Properties.Time).offset(offset).limit(10).list();
        HistoryExhibitor entity;
        int size = list.size();
        LogUtil.i(TAG, "HistoryExhibitor=" + size);
        for (int i = 0; i < size; i++) {
            entity = list.get(i);
            list.set(i, entity);
        }
        return list;
    }

    public ArrayList<HistoryExhibitor> getHistoryFrequency(String date, ArrayList<HistoryExhibitor> list, int status) {
        String sql = "";
        if (status == -1) {
            sql = "select *,count(COMPANY_ID) as frequency from HISTORY_EXHIBITOR  where TIME < '" + date + "' group by COMPANY_ID order by TIME DESC";
        } else {
            sql = "select *,count(COMPANY_ID) as frequency from HISTORY_EXHIBITOR  where TIME like '%" + date + "%' group by COMPANY_ID order by TIME DESC";
        }
        Cursor cursor = App.mDBHelper.db.rawQuery(sql, null);
        if (cursor != null) {
            HistoryExhibitor entity;
            while (cursor.moveToNext()) {
                entity = new HistoryExhibitor();
                entity.setId(cursor.getLong(0));
                entity.setCompanyID(cursor.getString(1));
                entity.setCompanyNameEN(cursor.getString(2));
                entity.setCompanyNameCN(cursor.getString(3));
                entity.setCompanyNameTW(cursor.getString(4));
                entity.setBooth(cursor.getString(5));
                entity.setTime(cursor.getString(6));
                entity.frequency = cursor.getInt(7);
                entity.status = status;

                list.add(entity);
            }
            cursor.close();
            entity = null;
        }

        return list;
    }

    public Exhibitor getExhibitor(String id){
        return mExhibitorDao.load(id);
    }








}
