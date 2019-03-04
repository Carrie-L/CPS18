package com.adsale.ChinaPlas.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.dao.Application;
import com.adsale.ChinaPlas.dao.CompanyApplication;
import com.adsale.ChinaPlas.dao.CompanyApplicationDao;
import com.adsale.ChinaPlas.dao.ApplicationDao;
import com.adsale.ChinaPlas.dao.BussinessMapping;
import com.adsale.ChinaPlas.dao.BussinessMappingDao;
import com.adsale.ChinaPlas.dao.CompanyProduct;
import com.adsale.ChinaPlas.dao.CompanyProductDao;
import com.adsale.ChinaPlas.dao.Country;
import com.adsale.ChinaPlas.dao.CountryDao;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.dao.ExhibitorDao;
import com.adsale.ChinaPlas.dao.ExhibitorZone;
import com.adsale.ChinaPlas.dao.ExhibitorZoneDao;
import com.adsale.ChinaPlas.dao.Map;
import com.adsale.ChinaPlas.dao.MapDao;
import com.adsale.ChinaPlas.dao.HistoryExhibitor;
import com.adsale.ChinaPlas.dao.HistoryExhibitorDao;
import com.adsale.ChinaPlas.dao.Industry;
import com.adsale.ChinaPlas.dao.IndustryDao;
import com.adsale.ChinaPlas.dao.Zone;
import com.adsale.ChinaPlas.dao.ZoneDao;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.SyncViewModel;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

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

    private CompanyApplicationDao mAppCompanyDao;
    private ApplicationDao mApplicationDao;
    private IndustryDao mIndustryDao;
    private CompanyProductDao mCompanyProductDao;
    private BussinessMappingDao mBsnsMappingDao;
    private MapDao mFloorDao;
    private Exhibitor exhibitor;
    private ExhibitorZoneDao mExhibitorZoneDao;
    private ZoneDao mZoneDao;
    private CountryDao mCountryDao;


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
     * ,C.CountryTC AS COUNTRY_NAME from EXHIBITOR E,COUNTRY C WHERE E.COUNTRY_ID=C.CountryID order by CAST(STROKE_TRAD AS INT) ASC,CAST(SEQ_TC AS INT) ASC
     */
    public ArrayList<Exhibitor> getAllExhibitors(ArrayList<String> letters) {
        String sql = getExhibitorSql() + orderByStroke();
        return cursorList(sql, letters, true);
    }

    /**
     * select * from EXHIBITOR E,COUNTRY C WHERE E.COUNTRY_ID=C.CountryID
     *
     * @return
     */
    private StringBuilder getExhibitorSql() {
        int language = AppUtil.getCurLanguage();
        StringBuilder sb = new StringBuilder();
        sb.append("select E.* ,C.").append(language == 0 ? "CountryTC AS COUNTRY_NAME" : language == 1 ? "CountryEng AS COUNTRY_NAME" : "CountrySC AS COUNTRY_NAME")
                .append(" from ").append(ExhibitorDao.TABLENAME).append(" E, ")
                .append(CountryDao.TABLENAME).append(" C WHERE E.").append(ExhibitorDao.Properties.CountryID.columnName)
                .append(" = C.").append(CountryDao.Properties.CountryID.columnName);
        return sb;
//        if (language == 0) {
//            return "select E.COMPANY_ID,E.COMPANY_NAME_EN,E.COMPANY_NAME_TW,E.COMPANY_NAME_CN,E.COUNTRY_ID,E.BOOTH_NO,E.STROKE_TRAD,E.SEQ_TC,E.HALL_NO,E.IS_FAVOURITE,E.DESC_E,E.DESC_S,E.DESC_T,E.PHOTO_FILE_NAME,C.CountryTC AS COUNTRY_NAME from EXHIBITOR E,COUNTRY C WHERE E.COUNTRY_ID=C.CountryID ";
//        } else if (language == 1) {
//            return "select E.COMPANY_ID,E.COMPANY_NAME_EN,E.COMPANY_NAME_TW,E.COMPANY_NAME_CN,E.BOOTH_NO,E.STROKE_ENG,E.SEQ_EN,E.HALL_NO,E.IS_FAVOURITE,E.DESC_E,E.DESC_S,E.DESC_T,E.PHOTO_FILE_NAME,C.CountryEng AS COUNTRY_NAME from EXHIBITOR E,COUNTRY C WHERE E.COUNTRY_ID=C.CountryID ";
//        } else {
//            return "select E.COMPANY_ID,E.COMPANY_NAME_EN,E.COMPANY_NAME_TW,E.COMPANY_NAME_CN,E.BOOTH_NO,E.PYSIMP,E.SEQ_SC,E.HALL_NO,E.IS_FAVOURITE,E.DESC_E,E.DESC_S,E.DESC_T,E.PHOTO_FILE_NAME,C.CountrySC AS COUNTRY_NAME from EXHIBITOR E,COUNTRY C WHERE E.COUNTRY_ID=C.CountryID ";
//        }
    }

    private ArrayList<Exhibitor> cursorList(String sql, ArrayList<String> letters, boolean orderByAZ) {
        long startTime = System.currentTimeMillis();
        int language = AppUtil.getCurLanguage();

        Cursor cursor = mDBHelper.db.rawQuery(sql, null);
        ArrayList<Exhibitor> exhibitors = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                cursor(cursor, language);
                exhibitor.setHallNo(cursor.getString(cursor.getColumnIndex(ExhibitorDao.Properties.HallNo.columnName)));   // .replace("999", "")
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
        cv.put(ExhibitorDao.Properties.IsFavourite.columnName, 1);
        mDBHelper.db.update(ExhibitorDao.TABLENAME, cv, "CompanyID=?", new String[]{companyID});
    }

    public void updateIsFavourite(Context context, String companyID, Integer isFavourite) {
        ContentValues cv = new ContentValues();
        cv.put(ExhibitorDao.Properties.IsFavourite.columnName, isFavourite);
        mDBHelper.db.update(ExhibitorDao.TABLENAME, cv, "CompanyID=?", new String[]{companyID});
        SyncViewModel sync = new SyncViewModel(context);
        if (isFavourite == 1) {
            sync.add(companyID);
        } else {
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
        return getName(" order by CAST(StrokeTrad AS INT) ASC,CAST(SeqTC AS INT) ASC",
                " ORDER BY StrokeEng,SeqEN", " ORDER BY PYSimp,SeqSC");
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
        StringBuilder sql = getExhibitorSql();
        for (int i = 0; i < size; i++) {
            filter = filters.get(i);
            index = filter.index;
            if (index == 3) {
                if (halls.size() == 0) {
                    if (filter.id.contains("Y")) {
                        // select E.* ,C.CountrySC AS COUNTRY_NAME from EXHIBITOR E, Country C WHERE E.CountryID = C.CountryID and HallNo LIKE ('%Y') ORDER BY PYSimp,SeqSC
                        sql = sql.append(" and ").append(ExhibitorDao.Properties.HallNo.columnName).append(" LIKE ('%%%1$s')");  // % 的转义字符： %%， 相当于 %Y
                        halls.add(filter.id);
                    } else {
                        sql = sql.append(" and ").append(ExhibitorDao.Properties.HallNo.columnName).append(" IN (%1$s) ");
                        halls.add("'".concat(filter.id).concat("'"));
                    }
                }
            } else if (index == 2) {
                if (countries.size() == 0) {
                    sql = sql.append(" and E.").append(ExhibitorDao.Properties.CountryID.columnName).append(" in (%2$s) ");
                }
                countries.add(filter.id);
            } else if (index == 0) {
                if (industriesStr.size() == 0) {
                    sql = sql.append(" and CompanyID IN (%3$s)");
                }
                industriesStr.add(" select CompanyID from CompanyProduct where CatalogProductSubID = " + filter.id);
            } else if (index == 1) {
                if (appStr.size() == 0) {
                    sql = sql.append(" and CompanyID IN (%4$s)");
                }
                appStr.add(" select CompanyID from CompanyApplication where IndustryID=" + filter.id);
            } else if (index == 4) {
                if (zoneStr.size() == 0) {
                    sql = sql.append(" and CompanyID IN (%5$s)");
                }
                zoneStr.add(" select CompanyId from ExhibitorZone where ThemeZoneCode='" + filter.id + "'");
            } else if (index == 6) {// new tec
                if (newTecStr.size() == 0) {
                    sql = sql.append(" and CompanyID IN (%6$s)");
                }
                newTecStr.add(" SELECT COMPANY_ID FROM NEW_PRODUCT_INFO WHERE RID IN (select RID from NEW_CATEGORY_ID where CATEGORY='C') ");
            } else if (index == 5) { // keyword
                keyword = filter.filter;
                sql = sql.append(" and (carriecps)");
//                sql = sql.concat(" and (E.DESC_E LIKE '\u0025").concat(filter.filter).concat("\u0025' OR E.DESC_S LIKE '\u0025").concat(filter.filter).concat("\u0025' OR E.DESC_T LIKE '\u0025").concat(filter.filter).concat("\u0025')");
            }
        }
        if (orderByAZ) {
            sql = sql.append(orderByStroke());
        } else {
            sql = sql.append(" order by seqHall");
        }

        LogUtil.i(TAG, ">>>> exhibitor filter sbsql=" + sql.toString());

        String sqlStr = String.format(sql.toString(), halls.toString().replace("[", "").replace("]", ""),
                countries.toString().replace("[", "").replace("]", ""),
                industriesStr.toString().replaceAll(",", " intersect").replace("[", "").replace("]", ""),
                appStr.toString().replaceAll(",", " intersect").replace("[", "").replace("]", ""),
                zoneStr.toString().replaceAll(",", " intersect").replace("[", "").replace("]", ""),
                newTecStr.toString().replaceAll(",", " intersect").replace("[", "").replace("]", ""));
        sqlStr = sqlStr.replaceAll("carriecps", "E.DescE LIKE '%".concat(keyword).concat("%' OR E.DescS LIKE '%").concat(keyword).concat("%' OR E.DescT LIKE '%").concat(keyword).concat("%'"));
        LogUtil.i(TAG, ">>>> sqlStr=" + sqlStr);
        return sqlStr;
    }

    public ArrayList<Exhibitor> queryFilterExhibitor(ArrayList<ExhibitorFilter> filters, ArrayList<String> letters, boolean orderByAZ) {
        return cursorList(filterSql(filters, orderByAZ), letters, orderByAZ);
    }

    /**
     * and HALL_NO NOT LIKE "%TBC%" order by CAST(HALL_NO AS INT),BOOTH_NO
     * and HALL_NO LIKE \"%TBC%\" order by HALL_NO
     */
    public ArrayList<Exhibitor> sortByHallList(ArrayList<String> letters) {
        StringBuilder sql0 = getExhibitorSql().append(" and ").append(ExhibitorDao.Properties.HallNo.columnName)
//                .append(" NOT LIKE \"%TBC%\" order by CAST(").append(ExhibitorDao.Properties.HallNo.columnName).append(" AS INT),").append(ExhibitorDao.Properties.BoothNo.columnName);
                .append(" NOT LIKE \"%TBC%\" order by seqHall");
        StringBuilder sql1 = getExhibitorSql()
                .append(" and ")
                .append(ExhibitorDao.Properties.HallNo.columnName)
                .append(" LIKE \"%TBC%\" order by ").append(ExhibitorDao.Properties.HallNo.columnName);

        ArrayList<String> letters0 = new ArrayList<>();
        ArrayList<String> letters1 = new ArrayList<>();
        ArrayList<Exhibitor> exhibitors = new ArrayList<>();
        ArrayList<Exhibitor> exhibitors0 = cursorList(sql0.toString(), letters0, false);
        ArrayList<Exhibitor> exhibitors1 = cursorList(sql1.toString(), letters1, false);
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
            initHistoryDao();
        }
    }

    public void insertHistoryExhiItem(HistoryExhibitor entity) {
        checkHistoryDao();
        mHistoryExhibitorDao.insert(entity);
    }

    public ArrayList<HistoryExhibitor> getAllHistoryExhibitors(int offset) {
        checkHistoryDao();
        ArrayList<HistoryExhibitor> list = (ArrayList<HistoryExhibitor>) mHistoryExhibitorDao.queryBuilder()
                .orderDesc(HistoryExhibitorDao.Properties.Time).offset(offset).list();
        return list;
    }

    public ArrayList<HistoryExhibitor> getHistoryFrequency(String date, ArrayList<HistoryExhibitor> list, int status) {
        String sql = "";
        if (status == -1) {
            sql = "select *,count(COMPANY_ID) as frequency from HISTORY_EXHIBITOR  where TIME < '" + date + "' group by COMPANY_ID order by TIME DESC";
        } else {
            sql = "select *,count(COMPANY_ID) as frequency from HISTORY_EXHIBITOR  where TIME like '%" + date + "%' group by COMPANY_ID order by TIME DESC";
        }
        LogUtil.i(TAG, "getHistoryFrequency: status = " + status + ", " + sql);

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
                entity.status.set(status);
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
        mAppCompanyDao = mDBHelper.mCompanyApplicationDao;
        mApplicationDao = mDBHelper.mApplicationDao;
        mIndustryDao = mDBHelper.mIndustryDao;
        mCompanyProductDao = mDBHelper.mCompanyProductDao;
        mBsnsMappingDao = mDBHelper.mBsnsMappingDao;
        mFloorDao = mDBHelper.mMapDao;
        mExhibitorZoneDao = mDBHelper.mExhibitorZoneDao;
        mZoneDao = mDBHelper.mZoneDao;
        mCountryDao = mDBHelper.mCountryDao;
    }

    public void insertExhibitorAll(ArrayList<Exhibitor> list) {
        mExhibitorDao.insertOrReplaceInTx(list);
    }

    public void deleteAllAppCompany() {
        mAppCompanyDao.deleteAll();
    }

    public void deleteAllCompanyProduct() {
        mCompanyProductDao.deleteAll();
    }

    public void insertCompanyProductAll(ArrayList<CompanyProduct> entities) {
        if (entities == null || entities.isEmpty()) {
            return;
        }
        mCompanyProductDao.insertInTx(entities);
    }

    public void insertApplicationCompaniesAll(ArrayList<CompanyApplication> entities) {
        if (entities == null || entities.isEmpty()) {
            return;
        }
        mAppCompanyDao.insertInTx(entities);
    }

    public void insertAppIndustryAll(final ArrayList<Application> entities) {
        final long startTime = System.currentTimeMillis();
        if (entities == null || entities.isEmpty()) {
            return;
        }
        mApplicationDao.insertOrReplaceInTx(entities);
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

    public void insertCountryAll(final ArrayList<Country> entities) {
        final long startTime = System.currentTimeMillis();
        if (entities == null || entities.isEmpty()) {
            return;
        }
        mCountryDao.insertOrReplaceInTx(entities);
        LogUtil.i(TAG, "插入 Country 成功：" + (System.currentTimeMillis() - startTime) + "ms");
    }


    public void deleteBsnsMappingAll() {
        mBsnsMappingDao.deleteAll();
    }

    public void clearAppIndustry() {
        mApplicationDao.deleteAll();
    }

    public void clearCompanyApplication() {
        mAppCompanyDao.deleteAll();
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

    public void clearCounutry() {
        mCountryDao.deleteAll();
    }

    public void insertFloorAll(final ArrayList<Map> entities) {
        if (entities == null || entities.isEmpty()) {
            return;
        }
        mFloorDao.insertOrReplaceInTx(entities);
    }

    public void setCsvDaoNull() {
        mAppCompanyDao = null;
        mApplicationDao = null;
        mIndustryDao = null;
        mBsnsMappingDao = null;
    }


    /*  ---------------------------------  Exhibitor Dtl Industry or App Industry  ------------------------------------------ */
    private String getExhibitorDtlSql() {
        int language = AppUtil.getCurLanguage();
        StringBuilder sb = new StringBuilder();
        sb.append("select E.").append(ExhibitorDao.Properties.CompanyID.columnName)
                .append(",E.").append(ExhibitorDao.Properties.CompanyNameEN.columnName)
                .append(",E.").append(ExhibitorDao.Properties.CompanyNameTW.columnName)
                .append(",E.").append(ExhibitorDao.Properties.CompanyNameCN.columnName)
                .append(",E.").append(ExhibitorDao.Properties.CountryID.columnName)
                .append(",E.").append(ExhibitorDao.Properties.BoothNo.columnName)
                .append(",E.").append(ExhibitorDao.Properties.IsFavourite.columnName)
                .append(",E.").append(ExhibitorDao.Properties.DescE.columnName)
                .append(",E.").append(ExhibitorDao.Properties.DescS.columnName)
                .append(",E.").append(ExhibitorDao.Properties.DescT.columnName);
        if (language == 0) {
            sb.append(",E.").append(ExhibitorDao.Properties.StrokeTrad.columnName)
                    .append(",E.").append(ExhibitorDao.Properties.SeqTC.columnName)
                    .append(",C.").append(CountryDao.Properties.CountryTC.columnName);
        } else if (language == 1) {
            sb.append(",E.").append(ExhibitorDao.Properties.StrokeEng.columnName)
                    .append(",E.").append(ExhibitorDao.Properties.SeqEN.columnName);
        } else {
            sb.append(",E.").append(ExhibitorDao.Properties.PYSimp.columnName)
                    .append(",E.").append(ExhibitorDao.Properties.SeqSC.columnName);
        }
        sb.append(" AS COUNTRY_NAME from ").append(ExhibitorDao.TABLENAME).append(" E ,COUNTRY C,%1$s I WHERE E.")
                .append(ExhibitorDao.Properties.CountryID.columnName).append("=C.").append(CountryDao.Properties.CountryID.columnName)
                .append(" and E.").append(ExhibitorDao.Properties.CompanyID.columnName).append("=I.CompanyID AND %2$s");
        LogUtil.i(TAG, "getExhibitorDtlSql: " + sb.toString());
        return sb.toString();

//        if (language == 0) {
//            return "select E.COMPANY_ID,E.COMPANY_NAME_EN,E.COMPANY_NAME_TW,E.COMPANY_NAME_CN,E.COUNTRY_ID,E.BOOTH_NO,E.STROKE_TRAD,E.SEQ_TC,E.HALL_NO,E.IS_FAVOURITE,E.DESC_E,E.DESC_S,E.DESC_T\n" +
//                    ",C.CountryTC AS COUNTRY_NAME from EXHIBITOR E,COUNTRY C,%1$s I WHERE E.COUNTRY_ID=C.CountryID AND E.COMPANY_ID=I.COMPANY_ID AND %2$s";
//        } else if (language == 1) {
//            return "select E.COMPANY_ID,E.COMPANY_NAME_EN,E.COMPANY_NAME_TW,E.COMPANY_NAME_CN,E.BOOTH_NO,E.STROKE_ENG,E.SEQ_EN,E.HALL_NO,E.IS_FAVOURITE,E.DESC_E,E.DESC_S,E.DESC_T\n" +
//                    ",C.CountryEng AS COUNTRY_NAME from EXHIBITOR E,COUNTRY C,%1$s I WHERE E.COUNTRY_ID=C.CountryID AND E.COMPANY_ID=I.COMPANY_ID AND %2$s";
//        } else {
//            return "select E.COMPANY_ID,E.COMPANY_NAME_EN,E.COMPANY_NAME_TW,E.COMPANY_NAME_CN,E.BOOTH_NO,E.PYSIMP,E.SEQ_SC,E.HALL_NO,E.IS_FAVOURITE,E.DESC_E,E.DESC_S,E.DESC_T\n" +
//                    ",C.CountrySC AS COUNTRY_NAME from EXHIBITOR E,COUNTRY C,%1$s I WHERE E.COUNTRY_ID=C.CountryID AND E.COMPANY_ID=I.COMPANY_ID AND %2$s";
//        }
    }

    /**
     * 包含产品id的展商
     *
     * @param id 产品id
     *           SELECT * FRom EXHIBITOR E, EXHIBITOR_INDUSTRY_DTL I,COUNTRY C WHERE E.COUNTRY_ID=C.CountryID and I.CATALOG_PRODUCT_SUB_ID="31" AND E.COMPANY_ID=I.COMPANY_ID
     *           SELECT * FRom EXHIBITOR E, CompanyProduct I,Country C WHERE E.COUNTRY_ID=C.CountryID and I.CatalogProductSubID="31" AND E.COMPANY_ID=I.CompanyID
     */
    public ArrayList<Exhibitor> getIndustryExhibitors(ArrayList<String> letters, String id) {
        String sql = String.format(getExhibitorDtlSql(), CompanyProductDao.TABLENAME, "I.CatalogProductSubID=" + id) + orderByStroke();
        return cursorList(sql, letters, true);
    }

    /**
     * 包含应用行业的展示
     *
     * @param id 应用行业id
     *           SELECT E.* FRom EXHIBITOR E, APPLICATION_COMPANY I WHERE I.INDUSTRY_ID="10" AND E.COMPANY_ID=I.COMPANY_ID;
     */
    public ArrayList<Exhibitor> getAppIndustryExhibitors(ArrayList<String> letters, String id) {
        String sql = String.format(getExhibitorDtlSql(), CompanyApplicationDao.TABLENAME, "I.IndustryID=" + id) + orderByStroke();
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
        StringBuilder sb = new StringBuilder();
        sb.append("select E.").append(ExhibitorDao.Properties.CompanyID.columnName)
                .append(",E.").append(ExhibitorDao.Properties.CompanyNameEN.columnName)
                .append(",E.").append(ExhibitorDao.Properties.CompanyNameTW.columnName)
                .append(",E.").append(ExhibitorDao.Properties.CompanyNameCN.columnName)
                .append(",E.").append(ExhibitorDao.Properties.BoothNo.columnName)
                .append(",E.").append(ExhibitorDao.Properties.IsFavourite.columnName)
                .append(",E.").append(ExhibitorDao.Properties.PhotoFileName.columnName);
        if (language == 0) {
            sb.append(",E.").append(ExhibitorDao.Properties.StrokeTrad.columnName)
                    .append(",E.").append(ExhibitorDao.Properties.SeqTC.columnName);
        } else if (language == 1) {
            sb.append(",E.").append(ExhibitorDao.Properties.StrokeEng.columnName)
                    .append(",E.").append(ExhibitorDao.Properties.SeqEN.columnName);
        } else {
            sb.append(",E.").append(ExhibitorDao.Properties.PYSimp.columnName)
                    .append(",E.").append(ExhibitorDao.Properties.SeqSC.columnName);
        }
        sb.append("  from ").append(ExhibitorDao.TABLENAME).append(" E WHERE E.")
                .append(ExhibitorDao.Properties.IsFavourite.columnName).append("=1");

        LogUtil.i(TAG, "getMyExhibitor = " + sb.toString());

        return sb.toString();


//        if (language == 0) {
//            return "select E.COMPANY_ID,E.COMPANY_NAME_EN,E.COMPANY_NAME_TW,E.COMPANY_NAME_CN,E.BOOTH_NO,E.STROKE_TRAD,E.SEQ_TC,E.IS_FAVOURITE,E.PHOTO_FILE_NAME\n" +
//                    " from EXHIBITOR E WHERE E.IS_FAVOURITE=1";
//        } else if (language == 1) {
//            return "select E.COMPANY_ID,E.COMPANY_NAME_EN,E.COMPANY_NAME_TW,E.COMPANY_NAME_CN,E.BOOTH_NO,E.STROKE_ENG,E.SEQ_EN,E.IS_FAVOURITE,E.PHOTO_FILE_NAME\n" +
//                    " from EXHIBITOR E WHERE E.IS_FAVOURITE=1";
//        } else {
//            return "select E.COMPANY_ID,E.COMPANY_NAME_EN,E.COMPANY_NAME_TW,E.COMPANY_NAME_CN,E.BOOTH_NO,E.PYSIMP,E.SEQ_SC,E.IS_FAVOURITE,E.PHOTO_FILE_NAME\n" +
//                    " from EXHIBITOR E WHERE E.IS_FAVOURITE=1";
//        }
    }

    // -----------------------------------------------------------------------------------------------------------------------
    private void cursor(Cursor cursor, int language) {
        exhibitor = new Exhibitor();
        exhibitor.setCompanyID(cursor.getString(cursor.getColumnIndex(ExhibitorDao.Properties.CompanyID.columnName)));
        exhibitor.setCompanyNameEN(cursor.getString(cursor.getColumnIndex(ExhibitorDao.Properties.CompanyNameEN.columnName)));
        exhibitor.setCompanyNameCN(cursor.getString(cursor.getColumnIndex(ExhibitorDao.Properties.CompanyNameCN.columnName)));
        exhibitor.setCompanyNameTW(cursor.getString(cursor.getColumnIndex(ExhibitorDao.Properties.CompanyNameTW.columnName)));
        exhibitor.setBoothNo(cursor.getString(cursor.getColumnIndex(ExhibitorDao.Properties.BoothNo.columnName)));
        exhibitor.setIsFavourite(cursor.getInt(cursor.getColumnIndex(ExhibitorDao.Properties.IsFavourite.columnName)));
        exhibitor.setPhotoFileName(cursor.getString(cursor.getColumnIndex(ExhibitorDao.Properties.PhotoFileName.columnName)));
        if (language == 0) {
            exhibitor.setStrokeTrad(cursor.getString(cursor.getColumnIndex(ExhibitorDao.Properties.StrokeTrad.columnName)));
            exhibitor.setSeqTC(cursor.getString(cursor.getColumnIndex(ExhibitorDao.Properties.SeqTC.columnName)));
            if (!exhibitor.getStrokeTrad().contains("劃")) {
                exhibitor.setStrokeTrad(exhibitor.getStrokeTrad().concat("劃"));
            }
        } else if (language == 1) {
            exhibitor.setStrokeEng(cursor.getString(cursor.getColumnIndex(ExhibitorDao.Properties.StrokeEng.columnName)));
            exhibitor.setSeqEN(cursor.getString(cursor.getColumnIndex(ExhibitorDao.Properties.SeqEN.columnName)));
        } else {
            exhibitor.setPYSimp(cursor.getString(cursor.getColumnIndex(ExhibitorDao.Properties.PYSimp.columnName)));
            exhibitor.setSeqSC(cursor.getString(cursor.getColumnIndex(ExhibitorDao.Properties.SeqSC.columnName)));
        }
    }

    /**
     * 設置：重置所有設定 —— 清空我的參展商（本地）
     */
    public void cancelMyExhibitor() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("IsFavourite", 0);
        App.mDBHelper.db.update(mExhibitorDao.getTablename(), contentValues, "IsFavourite=?", new String[]{"1"});
    }

    /**
     * 設置：重置所有設定 —— 清空興趣參展商
     */
    public void clearInterestedExhibitor() {
        ContentValues values = new ContentValues();
        values.put("IS_SELECTED", 0);
        App.mDBHelper.db.update(BussinessMappingDao.TABLENAME, values, null, null);
    }

    /*  ------------------------------------- **/
    public String getExhibitorLUT() {
        List<Exhibitor> list = mExhibitorDao.queryBuilder().orderDesc(ExhibitorDao.Properties.UpdatedAt).limit(1).list();
        if (list != null && list.size() > 0) {
            return list.get(0).getUpdatedAt();
        }
        return "";
    }

    public String getExhibitorDescLUT() {
        List<Exhibitor> list = mExhibitorDao.queryBuilder().orderDesc(ExhibitorDao.Properties.DescUpdatedAt).limit(1).list();
        if (list != null && list.size() > 0) {
            return list.get(0).getDescUpdatedAt();
        }
        return "";
    }

    public void updateOrInsertExhibitor(List<Exhibitor> list) {
        mExhibitorDao.insertOrReplaceInTx(list);
    }

    public void updateOrInsertExhibitorDesc(List<Exhibitor> list) {
        int size = list.size();
        Exhibitor entity;
        for (int i = 0; i < size; i++) {
            entity = list.get(i);
            ContentValues cv = new ContentValues();
            cv.put(ExhibitorDao.Properties.DescE.columnName, entity.getDescE());
            cv.put(ExhibitorDao.Properties.DescT.columnName, entity.getDescT());
            cv.put(ExhibitorDao.Properties.DescS.columnName, entity.getDescS());
            cv.put(ExhibitorDao.Properties.DescUpdatedAt.columnName, entity.getUpdatedAt());
            App.mDBHelper.db.insert(ExhibitorDao.TABLENAME, ExhibitorDao.Properties.CompanyID.columnName + "=" + entity.getCompanyID(), cv);
        }


    }

    public String getApplicationLUT() {
        List<Application> list = mApplicationDao.queryBuilder().orderDesc(ApplicationDao.Properties.UpdatedAt).limit(1).list();
        if (list != null && list.size() > 0) {
            return list.get(0).getUpdatedAt();
        }
        return "";
    }

    public String getCompanyApplicationLUT() {
        List<CompanyApplication> list = mAppCompanyDao.queryBuilder().orderDesc(CompanyApplicationDao.Properties.UpdatedAt).limit(1).list();
        if (list != null && list.size() > 0) {
            return list.get(0).getUpdatedAt();
        }
        return "";
    }

    public String getIndustryLUT() {
        List<Industry> list = mIndustryDao.queryBuilder().orderDesc(IndustryDao.Properties.UpdatedAt).limit(1).list();
        LogUtil.i(TAG, "getIndustryLUT list= " + list);
        if (list != null && list.size() > 0) {
            LogUtil.i(TAG, "getIndustryLUT=" + list.get(0).getUpdatedAt());
            return list.get(0).getUpdatedAt();
        }
        return "";
    }

    public String getCompanyProductLUT() {
        List<CompanyProduct> list = mCompanyProductDao.queryBuilder().orderDesc(CompanyProductDao.Properties.UpdatedAt).limit(1).list();
        if (list != null && list.size() > 0) {
            return list.get(0).getUpdatedAt();
        }
        return "";
    }

    public String getZoneLUT() {
        List<Zone> list = mZoneDao.queryBuilder().orderDesc(ZoneDao.Properties.UpdatedAt).limit(1).list();
        if (list != null && list.size() > 0) {
            return list.get(0).getUpdatedAt();
        }
        return "";
    }

    public String getExhibitorZoneLUT() {
        List<ExhibitorZone> list = mExhibitorZoneDao.queryBuilder().orderDesc(ExhibitorZoneDao.Properties.UpdatedAt).limit(1).list();
        if (list != null && list.size() > 0) {
            return list.get(0).getUpdatedAt();
        }
        return "";
    }

    public String getCountryLUT() {
        List<Country> list = mCountryDao.queryBuilder().orderDesc(CountryDao.Properties.UpdatedAt).limit(1).list();
        if (list != null && list.size() > 0) {
            return list.get(0).getUpdatedAt();
        }
        return "";
    }

    public void updateOrInsertApplication(List<Application> list) {
        mApplicationDao.insertOrReplaceInTx(list);
    }

    public void updateOrInsertCompanyApplication(List<CompanyApplication> list) {
        mAppCompanyDao.insertOrReplaceInTx(list);
    }

    public void updateOrInsertIndustry(List<Industry> list) {
        mIndustryDao.insertOrReplaceInTx(list);
    }

    public void updateOrInsertCompanyProduct(List<CompanyProduct> list) {
        mCompanyProductDao.insertOrReplaceInTx(list);
    }

    public void updateOrInsertZone(List<Zone> list) {
        mZoneDao.insertOrReplaceInTx(list);
    }

    public void updateOrInsertExhibitorZone(List<ExhibitorZone> list) {
        mExhibitorZoneDao.insertOrReplaceInTx(list);
    }

    public void updateOrInsertCountry(List<Country> list) {
        mCountryDao.insertOrReplaceInTx(list);
    }


}
