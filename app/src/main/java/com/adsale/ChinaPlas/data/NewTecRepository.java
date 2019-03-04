package com.adsale.ChinaPlas.data;

import android.database.Cursor;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.dao.Application;
import com.adsale.ChinaPlas.dao.DBHelper;
import com.adsale.ChinaPlas.dao.NewCategoryID;
import com.adsale.ChinaPlas.dao.NewCategorySub;
import com.adsale.ChinaPlas.dao.NewCategorySubDao;
import com.adsale.ChinaPlas.dao.NewCategoryIDDao;
import com.adsale.ChinaPlas.dao.NewProductInfo;
import com.adsale.ChinaPlas.dao.NewProductInfoDao;
import com.adsale.ChinaPlas.dao.NewProductsID;
import com.adsale.ChinaPlas.dao.NewProductsIDDao;
import com.adsale.ChinaPlas.dao.ProductImage;
import com.adsale.ChinaPlas.dao.ProductImageDao;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.data.model.NewTec;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.Parser;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.AbstractDao;

/**
 * Created by Carrie on 2017/11/16.
 * 新技术产品
 */

public class NewTecRepository {

    /**
     * 非广告展商的 new tec 资料
     */
    private NewProductInfoDao mInfoDao;
    private NewCategoryIDDao mNewCategoryIDDao;
    private NewProductsIDDao mNewProductsIDDao;
    private ProductImageDao mProductImageDao;
    private NewCategorySubDao mCategorySubDao;
    private static NewTecRepository INSTANCE;

    public static NewTecRepository newInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NewTecRepository();
        }
        return INSTANCE;
    }

    public void initDao() {
        DBHelper dbHelper = App.mDBHelper;
        mInfoDao = dbHelper.mNewProductInfoDao;
        mNewCategoryIDDao = dbHelper.mNewCategoryIDDao;
        mNewProductsIDDao = dbHelper.mNewProductsIDDao;
        mProductImageDao = dbHelper.mProductImageDao;
        mCategorySubDao = dbHelper.mCategorySubDao;
    }

    public ArrayList<NewProductInfo> getAllProductInfoList() {
        if (mInfoDao == null) {
            throw new NullPointerException("mInfoDao cannot be null,please #initDao()");
        }
        Cursor cursor = App.mDBHelper.db.rawQuery("select N.*,I.IMAGE__FILE AS IMAGE from NEW_PRODUCT_INFO N, PRODUCT_IMAGE I WHERE N.RID=I.RID order by N.RID", null);
        return getInfoList(cursor);
    }

    public ArrayList<NewProductInfo> getProductInfoList(String companyId) {
        if (mInfoDao == null) {
            throw new NullPointerException("mInfoDao cannot be null,please #initDao()");
        }
        Cursor cursor = App.mDBHelper.db.rawQuery("select N.*,I.IMAGE__FILE AS IMAGE from NEW_PRODUCT_INFO N, PRODUCT_IMAGE I WHERE N.RID=I.RID AND N.COMPANY_ID='".concat(companyId).concat("'"), null);
        return getInfoList(cursor);
    }

    private ArrayList<NewProductInfo> getInfoList(Cursor cursor) {
        ArrayList<NewProductInfo> list = new ArrayList<>();
        NewTec newTec = Parser.parseJsonFilesDirFile(NewTec.class, Constant.TXT_NEW_TEC);
        if (cursor != null) {
            NewProductInfo info;
            while (cursor.moveToNext()) {
                info = new NewProductInfo(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10), cursor.getString(11));
                info.image = newTec.imageLink.concat(cursor.getString(cursor.getColumnIndex("IMAGE")));
                info.imageThumb = newTec.listImageLink.concat(cursor.getString(cursor.getColumnIndex("IMAGE")));
                list.add(info);
            }
            cursor.close();
        }
        return list;
    }

    public ArrayList<Application> getApplications(ArrayList<Application> list) {
        Cursor cursor = App.mDBHelper.db.rawQuery("SELECT * FROM PRODUCT_APPLICATION ORDER BY ORDERING", null);
        if (cursor != null) {
            Application entity;
            while (cursor.moveToNext()) {
//                entity = new Application(cursor.getString(0), cursor.getString(1), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(5));
//                list.add(entity);
            }
            cursor.close();
        }
        return list;
    }

    /**
     * 新技术产品——筛选：点击产品、应用或主题专集后，进入的对应列表
     *
     * @param list
     * @param typeId MainTypeId
     * @return
     */
    public ArrayList<Application> getNewTecFilterList(ArrayList<Application> list, String typeId) {
        if (mCategorySubDao == null) {
            mCategorySubDao = App.mDBHelper.mCategorySubDao;
        }
        Cursor cursor = App.mDBHelper.db.rawQuery("select * from NEW_CATEGORY_SUB where MainTypeId='" + typeId + "' ORDER BY OrderId", null);
        if (cursor != null) {
            Application entity;
            while (cursor.moveToNext()) {
                entity = new Application(cursor.getString(4), cursor.getString(1), cursor.getString(2), cursor.getString(3), null, null);
                list.add(entity);
            }
            cursor.close();
        }
        return list;
//        return (ArrayList<NewCategorySub>) mCategorySubDao.queryBuilder().where(NewCategorySubDao.Properties.MainTypeId.eq(typeId)).list();
    }


    public ArrayList<NewProductInfo> getFilterList(ArrayList<NewProductInfo> productsAll, ArrayList<ExhibitorFilter> filters) {
        Cursor cursor = App.mDBHelper.db.rawQuery(getFilterSql(filters), null);
        ArrayList<NewProductInfo> list = new ArrayList<>();
        if (cursor != null) {
            NewProductInfo info;
            int size = productsAll.size();
            while (cursor.moveToNext()) {
                info = new NewProductInfo(cursor.getString(0));
                for (int i = 0; i < size; i++) {
                    if (productsAll.get(i).getRID().equals(info.getRID())) {
                        list.add(productsAll.get(i));
                        break;
                    }
                }
            }
            cursor.close();
        }
        return list;
    }

    private String getFilterSql(ArrayList<ExhibitorFilter> filters) {
        int size = filters.size();
        ArrayList<String> industriesStr = new ArrayList<>();
        ArrayList<String> appStr = new ArrayList<>();
        ExhibitorFilter filter;
        int index;
        String sql = "select RID from NEW_PRODUCT_INFO WHERE ";

        StringBuilder industrySql = new StringBuilder();
        industrySql.append(sql);

        for (int i = 0; i < size; i++) {
            filter = filters.get(i);
            index = filter.index;
            if (index == 0 || index == 5) { // 1 产品； 6：首发技术

                if (industriesStr.size() == 0) {
                    industriesStr.add(" select RID from NEW_CATEGORY_ID where CATEGORY = '" + filter.id + "' ");

                } else {
                    industriesStr.add("  and CATEGORY = '" + filter.id + "' ");
                }
//                industriesStr.add(" select RID from NEW_CATEGORY_ID where CATEGORY = '".concat(filter.id).concat("'"));
            } else if (index == 1) { // 应用
                appStr.add(" SELECT RID FROM NEW_PRODUCTS_ID WHERE SPOT = " + filter.id);
            }
        }
        if (industriesStr.size() > 0 && appStr.size() > 0) {
            sql = sql.concat(" RID IN (%1$s) AND RID IN (%2$s)");
            LogUtil.i("getFilterSql 0: ", "sql=" + sql);
            sql = String.format(sql, industriesStr.toString().replaceAll(",", "").replace("[", "").replace("]", ""),
                    appStr.toString().replaceAll(",", " intersect").replace("[", "").replace("]", ""));
        } else if (industriesStr.size() > 0) {
            sql = sql.concat(" RID IN (%1$s)");
            LogUtil.i("getFilterSql 1: ", "sql=" + sql);
            sql = String.format(sql, industriesStr.toString().replaceAll(",", "").replace("[", "").replace("]", ""));
        } else {
            sql = sql.concat(" RID IN (%1$s)");
            LogUtil.i("getFilterSql 2: ", "sql=" + sql);
            sql = String.format(sql, appStr.toString().replaceAll(",", " intersect").replace("[", "").replace("]", ""));
        }
        LogUtil.i("NewTecRepository", ">>>> sql=" + sql);
        return sql;
    }

    public void insertNewProductInfoAll(ArrayList<NewProductInfo> list) {
        mInfoDao.insertOrReplaceInTx(list);
    }

    public void insertNewProductsIDAll(ArrayList<NewProductsID> list) {
        mNewProductsIDDao.insertOrReplaceInTx(list);
    }

    public void insertNewCategoryIDAll(ArrayList<NewCategoryID> list) {
        mNewCategoryIDDao.insertInTx(list);
    }

    public void insertProductImageAll(ArrayList<ProductImage> list) {
        mProductImageDao.insertOrReplaceInTx(list);
    }

    public void insertCategorySubAll(ArrayList<NewCategorySub> list) {
        LogUtil.i("NEWTECH:", "insertCategorySubAll:" + list.size());
        mCategorySubDao.insertOrReplaceInTx(list);
    }

    public void clearProductInfo() {
        checkDao(mInfoDao);
        mInfoDao.deleteAll();
    }

    public void clearNewProductID() {
        checkDao(mNewProductsIDDao);
        mNewProductsIDDao.deleteAll();
    }

    public void clearCategoryID() {
        checkDao(mNewCategoryIDDao);
        mNewCategoryIDDao.deleteAll();
    }

    public void clearProductImage() {
        checkDao(mProductImageDao);
        mProductImageDao.deleteAll();
    }

    public void clearCategorySub() {
        LogUtil.i("NEWTECH:", "clearCategorySub");
        checkDao(mCategorySubDao);
        mCategorySubDao.deleteAll();
    }

    private void checkDao(AbstractDao dao) {
        if (dao == null) {
            initDao();
        }
    }

    /**
     * 获取
     *
     * @param RID
     * @return
     */
    public List<NewProductInfo> getNewProductInfoScroll(String RID) {
        checkDao(mInfoDao);
//        List<NewProductInfo> list = mInfoDao.queryBuilder().orderAsc(NewProductInfoDao.Properties.RID).list();
        List<NewProductInfo> list = getAllProductInfoList();
        List<NewProductInfo> results = new ArrayList<>();
        int size = list.size();
        NewProductInfo entity;
        for (int i = 0; i < size; i++) {
            entity = list.get(i);
            if (entity.getRID().equals(RID)) {
                results.add(entity);
                if (i == 0) {
                    LogUtil.i("NewTecRepo", "第0个，添加后面一个❤ " + list.get(i + 1).getCompanyName());
                    results.add( list.get(i + 1));
                } else if (i == size - 1) {
                    LogUtil.i("NewTecRepo", "最后一个，添加前面一个❤ " + list.get(i - 1).getCompanyName());
                    results.add(0, list.get(i - 1));
                } else {
                    LogUtil.i("NewTecRepo", "在中间，添加左右❤ " + list.get(i - 1).getCompanyName() + ",   " + list.get(i + 1).getCompanyName());
                    results.add(0,list.get(i - 1));
                    results.add(2, list.get(i + 1));
                }
                break;
            }
        }
        return results;
    }


}
