package com.adsale.ChinaPlas.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.dao.MainIcon;
import com.adsale.ChinaPlas.dao.MainIconDao;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;

/**
 * Created by Carrie on 2017/8/8.
 */

public class MainIconRepository implements DataSource<MainIcon> {
    private static final String TAG = "MainIconRepository";
    private static MainIconRepository INSTANCE;

    private SQLiteDatabase db = App.mDBHelper.db;
    private MainIconDao mIconDao = App.mDBHelper.mIconDao;

    private ArrayList icons;

    public static MainIconRepository getInstance() {
        if (INSTANCE == null) {
            return new MainIconRepository();
        }
        return INSTANCE;
    }

    public void insertData(final ArrayList<MainIcon> icons) {
        final long startTime = System.currentTimeMillis();
        if (icons == null || icons.isEmpty()) {
            return;
        }

        if (AppUtil.isFirstGetData() && icons.size() > 0) {
            LogUtil.i(TAG, "初次运行，清空 mainIcon");
            mIconDao.deleteAll();
        }

        try {
            mIconDao.getSession().runInTx(new Runnable() {

                @Override
                public void run() {
                    mIconDao.insertOrReplaceInTx(icons);
                    LogUtil.i(TAG, "插入MainIcon成功：" + (System.currentTimeMillis() - startTime) + "ms");
                    App.mSP_UpdateTime
                            .edit()
                            .putString(Constant.mainIconLUT,
                                    getMaxUT(MainIconDao.Properties.UpdateDateTime, mIconDao).getUpdateDateTime())
                            .apply();
                }
            });
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertItemData(MainIcon entity) {

    }

    @Override
    public void deleteItemData(Object obj) {

    }

    @Override
    public ArrayList<MainIcon> getData() {
        icons = new ArrayList<>();
        mIconDao.getSession().runInTx(new Runnable() {

            @Override
            public void run() {
                icons = (ArrayList<MainIcon>) mIconDao.queryBuilder().where(MainIconDao.Properties.IsHidden.notEq("TRUE")).list();
            }
        });
        return icons;
    }

    @Override
    public MainIcon getItemData(Object id) {
        return null;
    }

    @Override
    public void updateItemData(MainIcon entity) {

    }

    /**
     * 获取主界面数据
     */
    public ArrayList<MainIcon> getMenus() {
        return (ArrayList<MainIcon>) mIconDao.queryBuilder().where(MainIconDao.Properties.MenuList.like("%M%")).orderAsc(MainIconDao.Properties.MenuList).list();
    }

    /**
     * 获取侧边栏数据
     */
    public ArrayList<MainIcon> getLeftMenus(ArrayList<MainIcon> leftMenus, ArrayList<MainIcon> parentList, ArrayList<MainIcon> childList) {
        long startTime = System.currentTimeMillis();

        Cursor cursor = db.rawQuery(
                "select * from MAIN_ICON where IS_HIDDEN NOT LIKE 1 AND GOOGLE__TJ like \"%|%\" and GOOGLE__TJ like \"%S_%\" order by GOOGLE__TJ asc", null);
        if (cursor != null) {
            MainIcon entity = null;
            String google_tj = "";
            while (cursor.moveToNext()) {
                entity = new MainIcon();
                entity.setIconID(cursor.getString(cursor.getColumnIndex("ICON_ID")));
                entity.setTitleTW(cursor.getString(cursor.getColumnIndex("TITLE_TW")));
                entity.setTitleEN(cursor.getString(cursor.getColumnIndex("TITLE_EN")));
                entity.setTitleCN(cursor.getString(cursor.getColumnIndex("TITLE_CN")));
                entity.setIcon(cursor.getString(cursor.getColumnIndex("ICON")));
                entity.setCType(cursor.getInt(cursor.getColumnIndex("CTYPE")));
                entity.setCFile(cursor.getString(cursor.getColumnIndex("CFILE")));
                entity.setBaiDu_TJ(cursor.getString(cursor.getColumnIndex("BAI_DU__TJ")));
                google_tj = cursor.getString(cursor.getColumnIndex("GOOGLE__TJ")).split("\\|")[0].replace("S_", "");
                entity.setGoogle_TJ(google_tj);//11,1_1  //SystemMethod.subStringFront(google_tj,'|')

                if (!entity.getGoogle_TJ().contains("_")) {
                    parentList.add(entity);
                } else {
                    childList.add(entity);
                }
                leftMenus.add(entity);
            }

            cursor.close();
        }
        long endTime = System.currentTimeMillis();
        LogUtil.i(TAG, " getLeftMenus spend : " + (endTime - startTime) + "ms");
        Log.i(TAG, "CPS:leftMenus====================" + leftMenus.size());
        return leftMenus;
    }

    public String getIconName(String action) {
        List<MainIcon> lists = mIconDao.queryBuilder().where(MainIconDao.Properties.BaiDu_TJ.eq(action)).list();
        if (!lists.isEmpty()) {
            return lists.get(0).getTitleTW() + "#" + lists.get(0).getTitleEN() + "#" + lists.get(0).getTitleCN();
        } else {
            return action + "#" + action + "#" + action;
        }
    }


    /**
     * 获取最大更新时间
     *
     * @param <T>
     */
    private <T, String> T getMaxUT(Property property, AbstractDao<T, String> dao) {
        return dao.queryBuilder().orderDesc(property).limit(1).list().get(0);
    }


}
