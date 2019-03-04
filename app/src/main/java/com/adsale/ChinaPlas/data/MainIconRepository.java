package com.adsale.ChinaPlas.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.dao.MainIcon;
import com.adsale.ChinaPlas.dao.MainIconDao;
import com.adsale.ChinaPlas.dao.MainIconTest;
import com.adsale.ChinaPlas.dao.MainIconTestDao;
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
    private MainIconTestDao mIconTestDao = App.mDBHelper.mIconTestDao;

    private ArrayList icons;

    public static MainIconRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MainIconRepository();
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
                                    getMaxUT(MainIconDao.Properties.UpdatedAt, mIconDao).toString())
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
        return (ArrayList<MainIcon>) mIconDao.queryBuilder().where(MainIconDao.Properties.MenuSeq.like("%M%"), MainIconDao.Properties.IsHidden.notEq(1), MainIconDao.Properties.IsDelete.notEq(1)).orderAsc(MainIconDao.Properties.MenuSeq).list();
    }// like("%M%")

    public ArrayList<MainIcon> getAllIcons() {
        return (ArrayList<MainIcon>) mIconDao.queryBuilder().where(MainIconDao.Properties.IsHidden.notEq(1), MainIconDao.Properties.IsDelete.notEq(1)).orderAsc(MainIconDao.Properties.MenuSeq).list();
    }

    /**
     * 获取侧边栏数据
     */
    public ArrayList<MainIcon> getLeftMenus(ArrayList<MainIcon> leftMenus, ArrayList<MainIcon> parentList, ArrayList<MainIcon> childList) {
        long startTime = System.currentTimeMillis();

        Cursor cursor = db.rawQuery(
                "select * from MainIcon where IsHidden NOT LIKE 1 AND DrawerSeq LIKE \"%S%\" AND IsDelete NOT LIKE 1 ORDER BY DrawerSeq ASC", null);  // AND DrawerSeq LIKE "%S%"
        if (cursor != null) {
            MainIcon entity = null;
            while (cursor.moveToNext()) {
                entity = new MainIcon();
                entity.setIconID(cursor.getString(cursor.getColumnIndex("IconID")));
                entity.setTitleTW(cursor.getString(cursor.getColumnIndex("TitleTW")));
                entity.setTitleEN(cursor.getString(cursor.getColumnIndex("TitleEN")));
                entity.setTitleCN(cursor.getString(cursor.getColumnIndex("TitleCN")));
                entity.setIcon(cursor.getString(cursor.getColumnIndex("Icon")));
                entity.setCFile(cursor.getString(cursor.getColumnIndex("CFile")));
                entity.setBaiDu_TJ(cursor.getString(cursor.getColumnIndex("BaiDu_TJ")));
                entity.setMenuSeq(cursor.getString(cursor.getColumnIndex("MenuSeq")));
                entity.setDrawerSeq(cursor.getString(cursor.getColumnIndex("DrawerSeq")).replace("S_", ""));  //   .replace("S_", "")
                entity.setDrawerIcon(cursor.getString(cursor.getColumnIndex("DrawerIcon")));
//                if (entity.getBaiDu_TJ().equals("ContentUpdate")) {
//                    OtherRepository repository = OtherRepository.getInstance();
//                    repository.initUpdateCenterDao();
//                    int uc_count = repository.getNeedUpdatedCount();
//                    entity.updateCount.set(uc_count);
//                }
                if (entity.getDrawerSeq().contains("_")) {
                    childList.add(entity);
                } else {
                    parentList.add(entity);
                }
                leftMenus.add(entity);
            }
            cursor.close();
        }
        long endTime = System.currentTimeMillis();
        LogUtil.i(TAG, " getLeftMenus spend : " + (endTime - startTime) + "ms");
//        Log.i(TAG, "CPS:leftMenus====================" + leftMenus.size() + "," + leftMenus.toString());
        Log.i(TAG, "CPS:parentList====================" + parentList.size() + "," + parentList.toString());
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

    public void updateOrInsertMainIcons(ArrayList<MainIcon> list) {
        mIconDao.insertOrReplaceInTx(list);

        // 加上 删除，隐藏  更新  新增    文件地址

//        ContentValues cv = new ContentValues();
//        int size = list.size();
//        for (int i = 0; i < size; i++) {
//
//        }


    }

    public String getLastUpdateDate() {
        List<MainIcon> list = mIconDao.queryBuilder().orderDesc(MainIconDao.Properties.UpdatedAt).limit(1).list();
        if (list != null && list.size() > 0) {
            return list.get(0).getUpdatedAt();
        }
        return "";
    }

    /// -------------- MainIconTest

    public void updateOrInsertMainIconsTest(ArrayList<MainIconTest> list) {
        mIconTestDao.insertOrReplaceInTx(list);
    }

    public String getLastUpdateDateTest() {
        List<MainIconTest> list = mIconTestDao.queryBuilder().orderDesc(MainIconTestDao.Properties.UpdatedAt).limit(1).list();
        if (list != null && list.size() > 0) {
            return list.get(0).getUpdatedAt();
        }
        return "";
    }

}
