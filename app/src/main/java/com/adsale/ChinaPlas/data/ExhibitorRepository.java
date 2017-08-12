package com.adsale.ChinaPlas.data;

import android.database.Cursor;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.dao.ExhibitorDao;
import com.adsale.ChinaPlas.data.model.SideLetter;
import com.adsale.ChinaPlas.data.model.SideLetter;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;

import java.util.ArrayList;
import java.util.Locale;

import static com.adsale.ChinaPlas.R.id.language;
import static com.adsale.ChinaPlas.utils.AppUtil.getName;

/**
 * Created by Carrie on 2017/8/12.
 */

public class ExhibitorRepository implements DataSource<Exhibitor> {
    private static ExhibitorRepository INSTANCE;
    private ExhibitorDao mExhibitorDao = App.mDBHelper.mExhibitorDao;

    public static ExhibitorRepository getInstance(){
        if(INSTANCE==null){
            return new ExhibitorRepository();
        }
        return INSTANCE;
    }

    /**
     * 获取所有参展商的侧边列表
     */
    public ArrayList<SideLetter> getAllExhiLetters() {
        ArrayList<SideLetter> bars=new ArrayList<>();
        // SideBar列表及排序
        String sql = "select distinct " + getStroke() + " from EXHIBITOR " + orderByStroke();
        return getOrderedIndexList(bars, sql, null);
    }

//    public ArrayList<Exhibitor> queryData(String text){
////        mExhibitorDao.
//    }

    public ArrayList<Exhibitor> getExhibitorSearchResults(ArrayList<Exhibitor> exhibitors,
                                                          ArrayList<SideLetter> indexBars, String keyword, int language) {
        ArrayList<Exhibitor> exhibitorsTemps = new ArrayList<Exhibitor>();
        int size = exhibitors.size();
        Exhibitor exhibitor;
        String sort = "";
        int j = 0;
        keyword = keyword.toLowerCase(Locale.getDefault());
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                exhibitor = exhibitors.get(i);
                if (exhibitor.getCompanyNameCN().toLowerCase(Locale.getDefault()).contains(keyword)
                        || exhibitor.getCompanyNameEN().toLowerCase(Locale.getDefault()).contains(keyword)
                        || exhibitor.getCompanyNameTW().toLowerCase(Locale.getDefault()).contains(keyword)
                        || exhibitor.getBoothNo().toLowerCase(Locale.getDefault()).contains(keyword)) {
                    exhibitorsTemps.add(exhibitor);
                    sort = exhibitor.getSort(language);
                    if (j == 0) {
                        indexBars.add(new SideLetter(sort));
                        j++;
                    } else if (!indexBars.get(j - 1).letter.contains(sort)) {
                        indexBars.add(new SideLetter(sort));
                        j++;
                    }
                }
            }
        }
//		LogUtil.i(TAG, "indexBarAlls=" + indexBars.size() + "," + indexBars.toString());

        return exhibitorsTemps;
    }

    @Override
    public ArrayList<Exhibitor> getData() {
        return (ArrayList<Exhibitor>) mExhibitorDao.loadAll();
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
        int language= AppUtil.getCurLanguage();
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
