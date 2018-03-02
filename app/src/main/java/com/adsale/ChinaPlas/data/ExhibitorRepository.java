package com.adsale.ChinaPlas.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.dao.ApplicationCompany;
import com.adsale.ChinaPlas.dao.ApplicationCompanyDao;
import com.adsale.ChinaPlas.dao.ApplicationIndustry;
import com.adsale.ChinaPlas.dao.ApplicationIndustryDao;
import com.adsale.ChinaPlas.dao.BussinessMapping;
import com.adsale.ChinaPlas.dao.BussinessMappingDao;
import com.adsale.ChinaPlas.dao.DBHelper;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.dao.ExhibitorDao;
import com.adsale.ChinaPlas.dao.ExhibitorIndustryDtl;
import com.adsale.ChinaPlas.dao.ExhibitorIndustryDtlDao;
import com.adsale.ChinaPlas.dao.ExhibitorZone;
import com.adsale.ChinaPlas.dao.ExhibitorZoneDao;
import com.adsale.ChinaPlas.dao.Floor;
import com.adsale.ChinaPlas.dao.FloorDao;
import com.adsale.ChinaPlas.dao.HistoryExhibitor;
import com.adsale.ChinaPlas.dao.HistoryExhibitorDao;
import com.adsale.ChinaPlas.dao.Industry;
import com.adsale.ChinaPlas.dao.IndustryDao;
import com.adsale.ChinaPlas.dao.Zone;
import com.adsale.ChinaPlas.dao.ZoneDao;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.SyncViewModel;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Locale;

import de.greenrobot.dao.query.QueryBuilder;

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

    private ApplicationCompanyDao mAppCompanyDao;
    private ApplicationIndustryDao mAppIndustryDao;
    private IndustryDao mIndustryDao;
    private ExhibitorIndustryDtlDao mIndustryDtlDao;
    private BussinessMappingDao mBsnsMappingDao;
    private FloorDao mFloorDao;
    private Exhibitor exhibitor;
    private ExhibitorZoneDao mExhibitorZoneDao;
    private ZoneDao mZoneDao;

    public static ExhibitorRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ExhibitorRepository();
        }
        return INSTANCE;
    }

    /**
     * <font color="#f97798">根据广告id查找该公司是否在Exhibitor表中存在</font>
     *
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
        LogUtil.i(TAG, "lettersHall  >>>>  " + lettersHall.toString());
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
        int language = AppUtil.getCurLanguage();
        if (language == 0) {
            return "select E.COMPANY_ID,E.COMPANY_NAME_EN,E.COMPANY_NAME_TW,E.COMPANY_NAME_CN,E.COUNTRY_ID,E.BOOTH_NO,E.STROKE_TRAD,E.SEQ_TC,E.HALL_NO,E.IS_FAVOURITE,E.DESC_E,E.DESC_S,E.DESC_T,E.PHOTO_FILE_NAME,C.COUNTRY_NAME_TW AS COUNTRY_NAME from EXHIBITOR E,COUNTRY C WHERE E.COUNTRY_ID=C.COUNTRY_ID ";
        } else if (language == 1) {
            return "select E.COMPANY_ID,E.COMPANY_NAME_EN,E.COMPANY_NAME_TW,E.COMPANY_NAME_CN,E.BOOTH_NO,E.STROKE_ENG,E.SEQ_EN,E.HALL_NO,E.IS_FAVOURITE,E.DESC_E,E.DESC_S,E.DESC_T,E.PHOTO_FILE_NAME,C.COUNTRY_NAME_EN AS COUNTRY_NAME from EXHIBITOR E,COUNTRY C WHERE E.COUNTRY_ID=C.COUNTRY_ID ";
        } else {
            return "select E.COMPANY_ID,E.COMPANY_NAME_EN,E.COMPANY_NAME_TW,E.COMPANY_NAME_CN,E.BOOTH_NO,E.PYSIMP,E.SEQ_SC,E.HALL_NO,E.IS_FAVOURITE,E.DESC_E,E.DESC_S,E.DESC_T,E.PHOTO_FILE_NAME,C.COUNTRY_NAME_CN AS COUNTRY_NAME from EXHIBITOR E,COUNTRY C WHERE E.COUNTRY_ID=C.COUNTRY_ID ";
        }
    }

    private ArrayList<Exhibitor> cursorList(String sql, ArrayList<String> letters, boolean orderByAZ) {
        long startTime = System.currentTimeMillis();
        int language = AppUtil.getCurLanguage();
        Cursor cursor = mDBHelper.db.rawQuery(sql, null);
        ArrayList<Exhibitor> exhibitors = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                cursor(cursor, language);
                exhibitor.setHallNo(cursor.getString(cursor.getColumnIndex("HALL_NO")).replace("999", ""));
                exhibitor.CountryName = cursor.getString(cursor.getColumnIndex("COUNTRY_NAME"));
                exhibitor.isCollected.set(exhibitor.getIsFavourite() == 1);
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

    public void updateFavourite(String companyID) {
        ContentValues cv = new ContentValues();
        cv.put("IS_FAVOURITE", 1);
        mDBHelper.db.update("EXHIBITOR", cv, "COMPANY_ID=?", new String[]{companyID});
    }

    public void updateIsFavourite(Context context,String companyID, Integer isFavourite) {
        ContentValues cv = new ContentValues();
        cv.put("IS_FAVOURITE", isFavourite);
        mDBHelper.db.update("EXHIBITOR", cv, "COMPANY_ID=?", new String[]{companyID});
        SyncViewModel sync=new SyncViewModel(context);
        if(isFavourite==1){
            sync.add(companyID);
        }else{
            sync.remove(companyID);
        }
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
     * "select * from EXHIBITOR WHERE HALL_NO IN (%1$s) and COUNTRY_ID in (%2$s) and COMPANY_ID IN (%3$s) and COMPANY_ID IN (%4$s) and COMPANY_ID IN (%5$s)  and (E.DESC_E LIKE "%%" OR E.DESC_S LIKE "%%" OR E.DESC_T LIKE "%%") order by xxx "
     * new tec: SELECT COMPANY_ID FROM NEW_PRODUCT_INFO WHERE RID IN (select RID from NEW_PRODUCT_AND_CATEGORY where CATEGORY="C")
     */
    private String filterSql(ArrayList<ExhibitorFilter> filters, boolean orderByAZ) {
        int size = filters.size();
        ArrayList<String> halls = new ArrayList<>();
        ArrayList<String> countries = new ArrayList<>();
        ArrayList<String> industriesStr = new ArrayList<>();
        ArrayList<String> appStr = new ArrayList<>();
        ArrayList<String> zoneStr = new ArrayList<>();
        ArrayList<String> newTecStr = new ArrayList<>();
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
                halls.add("'".concat(filter.id).concat("'"));
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
            } else if (index == 4) {
                if (zoneStr.size() == 0) {
                    sql = sql.concat(" and COMPANY_ID IN (%5$s)");
                }
                zoneStr.add(" select CompanyId from EXHIBITOR_ZONE where ThemeZoneCode='" + filter.id + "'");
            } else if (index == 6) {// new tec
                if (newTecStr.size() == 0) {
                    sql = sql.concat(" and COMPANY_ID IN (%6$s)");
                }
                newTecStr.add(" SELECT COMPANY_ID FROM NEW_PRODUCT_INFO WHERE RID IN (select RID from NEW_PRODUCT_AND_CATEGORY where CATEGORY='C') ");
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
                appStr.toString().replaceAll(",", " intersect").replace("[", "").replace("]", ""),
                zoneStr.toString().replaceAll(",", " intersect").replace("[", "").replace("]", ""),
                newTecStr.toString().replaceAll(",", " intersect").replace("[", "").replace("]", ""));
        sql = sql.replaceAll("carriecps", "E.DESC_E LIKE '%".concat(keyword).concat("%' OR E.DESC_S LIKE '%").concat(keyword).concat("%' OR E.DESC_T LIKE '%").concat(keyword).concat("%'"));
        LogUtil.i(TAG, ">>>> sql=" + sql);
        return sql;
    }

    public ArrayList<Exhibitor> queryFilterExhibitor(ArrayList<ExhibitorFilter> filters, ArrayList<String> letters, boolean orderByAZ) {
        return cursorList(filterSql(filters, orderByAZ), letters, orderByAZ);
    }

    public ArrayList<Exhibitor> sortByHallList(ArrayList<String> letters) {
        String sql0 = getExhibitorSql().concat("and HALL_NO NOT LIKE \"%TBC%\" order by CAST(HALL_NO AS INT),BOOTH_NO");
        String sql1 = getExhibitorSql().concat("and HALL_NO LIKE \"%TBC%\" order by HALL_NO");

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

    public Exhibitor getExhibitor(String id) {
        return mExhibitorDao.load(id);
    }


    /*  *********************************   从csv中插入数据库           **********************************       */
    public void initCsvDao() {
        mAppCompanyDao = mDBHelper.mAppCompanyDao;
        mAppIndustryDao = mDBHelper.mAppIndustryDao;
        mIndustryDao = mDBHelper.mIndustryDao;
        mIndustryDtlDao = mDBHelper.mIndustryDtlDao;
        mBsnsMappingDao = mDBHelper.mBsnsMappingDao;
        mFloorDao = mDBHelper.mFloorDao;
        mExhibitorZoneDao = mDBHelper.mExhibitorZoneDao;
        mZoneDao = mDBHelper.mZoneDao;
    }

    public void insertExhibitorAll(ArrayList<Exhibitor> list) {
        mExhibitorDao.insertOrReplaceInTx(list);
    }

    public void deleteAllAppCompany() {
        mAppCompanyDao.deleteAll();
    }

    public void insertApplicationCompaniesAll(ArrayList<ApplicationCompany> entities) {
        if (entities == null || entities.isEmpty()) {
            return;
        }
        mAppCompanyDao.insertInTx(entities);
    }

    public void insertAppIndustryAll(final ArrayList<ApplicationIndustry> entities) {
        final long startTime = System.currentTimeMillis();
        if (entities == null || entities.isEmpty()) {
            return;
        }
        mAppIndustryDao.insertOrReplaceInTx(entities);
        LogUtil.i(TAG, "插入ApplicationIndustry成功：" + (System.currentTimeMillis() - startTime) + "ms");
    }

    public void insertIndustryAll(final ArrayList<Industry> entities) {
        final long startTime = System.currentTimeMillis();
        if (entities == null || entities.isEmpty()) {
            return;
        }
        mIndustryDao.insertOrReplaceInTx(entities);
        LogUtil.i(TAG, "插入Industry成功：" + (System.currentTimeMillis() - startTime) + "ms");
    }

    public void deleteExhibitorIndustryDtlAll() {
        mIndustryDtlDao.deleteAll();
    }

    public void insertExhibitorDtlAll(final ArrayList<ExhibitorIndustryDtl> entities) {
        final long startTime = System.currentTimeMillis();
        if (entities == null || entities.isEmpty()) {
            return;
        }
        mIndustryDtlDao.insertOrReplaceInTx(entities);
        LogUtil.i(TAG, "插入 insertExhibitorDtlAll 成功：" + (System.currentTimeMillis() - startTime) + "ms");
    }

    public void insertBsnsMappingAll(ArrayList<BussinessMapping> lists) {
        mBsnsMappingDao.insertInTx(lists);
    }


    public void insertExhibitorZoneAll(final ArrayList<ExhibitorZone> entities) {
        final long startTime = System.currentTimeMillis();
        if (entities == null || entities.isEmpty()) {
            return;
        }
        mExhibitorZoneDao.insertOrReplaceInTx(entities);
        LogUtil.i(TAG, "插入 ExhibitorZone 成功：" + (System.currentTimeMillis() - startTime) + "ms");
    }

    public void insertZoneAll(final ArrayList<Zone> entities) {
        final long startTime = System.currentTimeMillis();
        if (entities == null || entities.isEmpty()) {
            return;
        }
        mZoneDao.insertOrReplaceInTx(entities);
        LogUtil.i(TAG, "插入 Zone 成功：" + (System.currentTimeMillis() - startTime) + "ms");
    }

    public void deleteBsnsMappingAll() {
        mBsnsMappingDao.deleteAll();
    }

    public void clearAppIndustry() {
        mAppIndustryDao.deleteAll();
    }

    public void clearIndustry() {
        mIndustryDao.deleteAll();
    }

    public void clearExhibitorAll() {
        mExhibitorDao.deleteAll();
    }

    public void clearFloor() {
        mFloorDao.deleteAll();
    }

    public void clearExhibitorZone() {
        mExhibitorZoneDao.deleteAll();
    }

    public void clearZone() {
        mZoneDao.deleteAll();
    }

    public void insertFloorAll(final ArrayList<Floor> entities) {
        if (entities == null || entities.isEmpty()) {
            return;
        }
        mFloorDao.insertOrReplaceInTx(entities);
    }

    public void setCsvDaoNull() {
        mAppCompanyDao = null;
        mAppIndustryDao = null;
        mIndustryDao = null;
        mIndustryDtlDao = null;
        mBsnsMappingDao = null;
    }


    /*  ---------------------------------  Exhibitor Dtl Industry or App Industry  ------------------------------------------ */
    private String getExhibitorDtlSql() {
        int language = AppUtil.getCurLanguage();
        if (language == 0) {
            return "select E.COMPANY_ID,E.COMPANY_NAME_EN,E.COMPANY_NAME_TW,E.COMPANY_NAME_CN,E.COUNTRY_ID,E.BOOTH_NO,E.STROKE_TRAD,E.SEQ_TC,E.HALL_NO,E.IS_FAVOURITE,E.DESC_E,E.DESC_S,E.DESC_T\n" +
                    ",C.COUNTRY_NAME_TW AS COUNTRY_NAME from EXHIBITOR E,COUNTRY C,%1$s I WHERE E.COUNTRY_ID=C.COUNTRY_ID AND E.COMPANY_ID=I.COMPANY_ID AND %2$s";
        } else if (language == 1) {
            return "select E.COMPANY_ID,E.COMPANY_NAME_EN,E.COMPANY_NAME_TW,E.COMPANY_NAME_CN,E.BOOTH_NO,E.STROKE_ENG,E.SEQ_EN,E.HALL_NO,E.IS_FAVOURITE,E.DESC_E,E.DESC_S,E.DESC_T\n" +
                    ",C.COUNTRY_NAME_EN AS COUNTRY_NAME from EXHIBITOR E,COUNTRY C,%1$s I WHERE E.COUNTRY_ID=C.COUNTRY_ID AND E.COMPANY_ID=I.COMPANY_ID AND %2$s";
        } else {
            return "select E.COMPANY_ID,E.COMPANY_NAME_EN,E.COMPANY_NAME_TW,E.COMPANY_NAME_CN,E.BOOTH_NO,E.PYSIMP,E.SEQ_SC,E.HALL_NO,E.IS_FAVOURITE,E.DESC_E,E.DESC_S,E.DESC_T\n" +
                    ",C.COUNTRY_NAME_CN AS COUNTRY_NAME from EXHIBITOR E,COUNTRY C,%1$s I WHERE E.COUNTRY_ID=C.COUNTRY_ID AND E.COMPANY_ID=I.COMPANY_ID AND %2$s";
        }
    }

    /**
     * 包含产品id的展商
     *
     * @param id 产品id
     *           SELECT * FRom EXHIBITOR E, EXHIBITOR_INDUSTRY_DTL I,COUNTRY C WHERE E.COUNTRY_ID=C.COUNTRY_ID and I.CATALOG_PRODUCT_SUB_ID="31" AND E.COMPANY_ID=I.COMPANY_ID
     */
    public ArrayList<Exhibitor> getIndustryExhibitors(ArrayList<String> letters, String id) {
        String sql = String.format(getExhibitorDtlSql(), "EXHIBITOR_INDUSTRY_DTL", "I.CATALOG_PRODUCT_SUB_ID=" + id) + orderByStroke();
        return cursorList(sql, letters, true);
    }

    /**
     * 包含应用行业的展示
     *
     * @param id 应用行业id
     *           SELECT E.* FRom EXHIBITOR E, APPLICATION_COMPANY I WHERE I.INDUSTRY_ID="10" AND E.COMPANY_ID=I.COMPANY_ID;
     */
    public ArrayList<Exhibitor> getAppIndustryExhibitors(ArrayList<String> letters, String id) {
        String sql = String.format(getExhibitorDtlSql(), "APPLICATION_COMPANY", "I.INDUSTRY_ID=" + id) + orderByStroke();
        return cursorList(sql, letters, true);
    }

    /*  ---------------------------------  My Exhibitor ------------------------------------------ */
    public ArrayList<Exhibitor> getMyExhibitors(ArrayList<String> letters) {
        return myExhibitorCursorList(getMyExhibitorSql().concat(orderByStroke()), letters);
    }

    private ArrayList<Exhibitor> myExhibitorCursorList(String sql, ArrayList<String> letters) {
        long startTime = System.currentTimeMillis();
        int language = AppUtil.getCurLanguage();
        Cursor cursor = mDBHelper.db.rawQuery(sql, null);
        ArrayList<Exhibitor> exhibitors = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                cursor(cursor, language);
                letters.add(exhibitor.getSort());
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

    private String getMyExhibitorSql() {
        int language = AppUtil.getCurLanguage();
        if (language == 0) {
            return "select E.COMPANY_ID,E.COMPANY_NAME_EN,E.COMPANY_NAME_TW,E.COMPANY_NAME_CN,E.BOOTH_NO,E.STROKE_TRAD,E.SEQ_TC,E.IS_FAVOURITE,E.PHOTO_FILE_NAME\n" +
                    " from EXHIBITOR E WHERE E.IS_FAVOURITE=1";
        } else if (language == 1) {
            return "select E.COMPANY_ID,E.COMPANY_NAME_EN,E.COMPANY_NAME_TW,E.COMPANY_NAME_CN,E.BOOTH_NO,E.STROKE_ENG,E.SEQ_EN,E.IS_FAVOURITE,E.PHOTO_FILE_NAME\n" +
                    " from EXHIBITOR E WHERE E.IS_FAVOURITE=1";
        } else {
            return "select E.COMPANY_ID,E.COMPANY_NAME_EN,E.COMPANY_NAME_TW,E.COMPANY_NAME_CN,E.BOOTH_NO,E.PYSIMP,E.SEQ_SC,E.IS_FAVOURITE,E.PHOTO_FILE_NAME\n" +
                    " from EXHIBITOR E WHERE E.IS_FAVOURITE=1";
        }
    }

    // -----------------------------------------------------------------------------------------------------------------------
    private void cursor(Cursor cursor, int language) {
        exhibitor = new Exhibitor();
        exhibitor.setCompanyID(cursor.getString(cursor.getColumnIndex("COMPANY_ID")));
        exhibitor.setCompanyNameEN(cursor.getString(cursor.getColumnIndex("COMPANY_NAME_EN")));
        exhibitor.setCompanyNameCN(cursor.getString(cursor.getColumnIndex("COMPANY_NAME_CN")));
        exhibitor.setCompanyNameTW(cursor.getString(cursor.getColumnIndex("COMPANY_NAME_TW")));
        exhibitor.setBoothNo(cursor.getString(cursor.getColumnIndex("BOOTH_NO")));
        exhibitor.setIsFavourite(cursor.getInt(cursor.getColumnIndex("IS_FAVOURITE")));
        exhibitor.setPhotoFileName(cursor.getString(cursor.getColumnIndex("PHOTO_FILE_NAME")));
        if (language == 0) {
            exhibitor.setStrokeTrad(cursor.getString(cursor.getColumnIndex("STROKE_TRAD")));
            exhibitor.setSeqTC(cursor.getInt(cursor.getColumnIndex("SEQ_TC")));
            if (!exhibitor.getStrokeTrad().contains("劃")) {
                exhibitor.setStrokeTrad(exhibitor.getStrokeTrad().concat("劃"));
            }
        } else if (language == 1) {
            exhibitor.setStrokeEng(cursor.getString(cursor.getColumnIndex("STROKE_ENG")));
            exhibitor.setSeqEN(cursor.getInt(cursor.getColumnIndex("SEQ_EN")));
        } else {
            exhibitor.setPYSimp(cursor.getString(cursor.getColumnIndex("PYSIMP")));
            exhibitor.setSeqSC(cursor.getInt(cursor.getColumnIndex("SEQ_SC")));
        }
    }

    /**
     * 設置：重置所有設定 —— 清空我的參展商（本地）
     */
    public void cancelMyExhibitor() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("IS_FAVOURITE", 0);
        App.mDBHelper.db.update(mExhibitorDao.getTablename(), contentValues, "IS_FAVOURITE=?", new String[]{"1"});
    }

    /**
     * 設置：重置所有設定 —— 清空興趣參展商
     */
    public void clearInterestedExhibitor() {
        ContentValues values = new ContentValues();
        values.put("IS_SELECTED", 0);
        App.mDBHelper.db.update(BussinessMappingDao.TABLENAME, values, null, null);
    }


}
