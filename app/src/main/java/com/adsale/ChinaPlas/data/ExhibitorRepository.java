package com.adsale.ChinaPlas.data;

import android.content.ContentValues;
import android.database.Cursor;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.dao.ExhibitorDao;
import com.adsale.ChinaPlas.dao.SideBar;
import com.adsale.ChinaPlas.data.model.SideLetter;
import com.adsale.ChinaPlas.data.model.SideLetter;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;
import java.util.Locale;

import de.greenrobot.dao.query.WhereCondition;

import static android.R.attr.key;
import static android.content.ContentValues.TAG;
import static com.adsale.ChinaPlas.R.id.language;
import static com.adsale.ChinaPlas.utils.AppUtil.getName;

/**
 * Created by Carrie on 2017/8/12.
 */

public class ExhibitorRepository implements DataSource<Exhibitor> {
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

    /**
     * select distinct STROKE_TRAD From EXHIBITOR WHERE COMPANY_NAME_TW like "%4.1%" or BOOTH_NO like "%4.1%" order by cast (STROKE_TRAD as INT)
     * @param keyword
     */
    public ArrayList<SideLetter>  getSearchedLetters(String keyword){
        ArrayList<SideLetter> bars = new ArrayList<>();
        // SideBar列表及排序
        String sql = "select distinct " + getStroke() + " from EXHIBITOR where " +getCompanyNameColumn()+" like '%"+keyword+"%' or BOOTH_NO like '%"+keyword+"%' "+ orderByStroke();
        return getOrderedIndexList(bars, sql, null);
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
		LogUtil.i(TAG, "exhibitorsTemps=" + exhibitorsTemps.size() );
        return exhibitorsTemps;
    }

    @Override
    public ArrayList<Exhibitor> getData() {
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
        return getName(" order by cast(" + ExhibitorDao.Properties.StrokeTrad.columnName + " as INT)",
                " ORDER BY " + ExhibitorDao.Properties.StrokeEng.columnName, " ORDER BY "
                        + ExhibitorDao.Properties.PYSimp.columnName);
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
}
