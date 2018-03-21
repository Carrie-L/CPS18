package com.adsale.ChinaPlas.data;

import android.database.Cursor;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.dao.ApplicationIndustry;
import com.adsale.ChinaPlas.dao.DBHelper;
import com.adsale.ChinaPlas.dao.NewProductAndCategory;
import com.adsale.ChinaPlas.dao.NewProductAndCategoryDao;
import com.adsale.ChinaPlas.dao.NewProductInfo;
import com.adsale.ChinaPlas.dao.NewProductInfoDao;
import com.adsale.ChinaPlas.dao.NewProductsAndApplication;
import com.adsale.ChinaPlas.dao.NewProductsAndApplicationDao;
import com.adsale.ChinaPlas.dao.ProductApplication;
import com.adsale.ChinaPlas.dao.ProductApplicationDao;
import com.adsale.ChinaPlas.dao.ProductImage;
import com.adsale.ChinaPlas.dao.ProductImageDao;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.data.model.NewTec;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.Parser;

import java.util.ArrayList;

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
    private NewProductAndCategoryDao mNewProductAndCategoryDao;
    private NewProductsAndApplicationDao mNewProductsAndApplicationDao;
    private ProductApplicationDao mProductApplicationDao;
    private ProductImageDao mProductImageDao;
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
        mNewProductAndCategoryDao = dbHelper.mNewProductAndCategoryDao;
        mNewProductsAndApplicationDao = dbHelper.mNewProductsAndApplicationDao;
        mProductApplicationDao = dbHelper.mProductApplicationDao;
        mProductImageDao = dbHelper.mProductImageDao;
    }

    public ArrayList<NewProductInfo> getAllProductInfoList() {
        if (mInfoDao == null) {
            throw new NullPointerException("mInfoDao cannot be null,please #initDao()");
        }
        Cursor cursor = App.mDBHelper.db.rawQuery("select N.*,I.IMAGE__FILE AS IMAGE from NEW_PRODUCT_INFO N, PRODUCT_IMAGE I WHERE N.RID=I.RID", null);
        return getInfoList(cursor);
    }

    public ArrayList<NewProductInfo> getProductInfoList(String companyId) {
        if (mInfoDao == null) {
            throw new NullPointerException("mInfoDao cannot be null,please #initDao()");
        }
        Cursor cursor = App.mDBHelper.db.rawQuery("select N.*,I.IMAGE__FILE AS IMAGE from NEW_PRODUCT_INFO N, PRODUCT_IMAGE I WHERE N.RID=I.RID AND N.COMPANY_ID='".concat(companyId).concat("'"),null);
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

    public ArrayList<ApplicationIndustry> getApplications(ArrayList<ApplicationIndustry> list) {
        Cursor cursor = App.mDBHelper.db.rawQuery("SELECT * FROM PRODUCT_APPLICATION ORDER BY ORDERING", null);
        if (cursor != null) {
            ApplicationIndustry entity;
            while (cursor.moveToNext()) {
                entity = new ApplicationIndustry(cursor.getString(0), cursor.getString(1), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(5));
                list.add(entity);
            }
            cursor.close();
        }
        return list;
    }

    /**
     * NewProductAndCategory.csv(划掉)，获取 产品 列表
     */
    public ArrayList<NewProductInfo> getFilterList(ArrayList<NewProductInfo> productsAll, ArrayList<ExhibitorFilter> filters) {
        Cursor cursor = App.mDBHelper.db.rawQuery(getFilterSql(filters), null);
        ArrayList<NewProductInfo> list = new ArrayList<>();
        int size = productsAll.size();
        if (cursor != null) {
            NewProductInfo info;
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
        for (int i = 0; i < size; i++) {
            filter = filters.get(i);
            index = filter.index;
            if (index == 0 || index ==6) { // 0: 产品； 6：首发技术
                industriesStr.add(" select RID from NEW_PRODUCT_AND_CATEGORY where CATEGORY = '".concat(filter.id).concat("'"));
            } else if (index == 1) {
                appStr.add(" SELECT RID FROM NEW_PRODUCTS_AND_APPLICATION WHERE SPOT IN (SELECT INDUSTRY_ID FROM PRODUCT_APPLICATION WHERE INDUSTRY_ID = ".concat(filter.id).concat(")"));
            }
        }
        if (industriesStr.size() > 0 && appStr.size() > 0) {
            sql = sql.concat(" RID IN (%1$s) AND RID IN (%2$s)");
            sql = String.format(sql, industriesStr.toString().replaceAll(",", " intersect").replace("[", "").replace("]", ""),
                    appStr.toString().replaceAll(",", " intersect").replace("[", "").replace("]", ""));
        } else if (industriesStr.size() > 0) {
            sql = sql.concat(" RID IN (%1$s)");
            sql = String.format(sql, industriesStr.toString().replaceAll(",", " intersect").replace("[", "").replace("]", ""));
        } else {
            sql = sql.concat(" RID IN (%1$s)");
            sql = String.format(sql, appStr.toString().replaceAll(",", " intersect").replace("[", "").replace("]", ""));
        }
        LogUtil.i("NewTecRepository", ">>>> sql=" + sql);
        return sql;
    }

    public void insertNewProductInfoAll(ArrayList<NewProductInfo> list) {
        mInfoDao.insertInTx(list);
    }

    public void insertNewProductsAndApplicationAll(ArrayList<NewProductsAndApplication> list) {
        mNewProductsAndApplicationDao.insertInTx(list);
    }

    public void insertNewProductAndCategoryAll(ArrayList<NewProductAndCategory> list) {
        mNewProductAndCategoryDao.insertInTx(list);
    }

    public void insertProductApplicationAll(ArrayList<ProductApplication> list) {
        mProductApplicationDao.insertInTx(list);
    }

    public void insertProductImageAll(ArrayList<ProductImage> list) {
        mProductImageDao.insertInTx(list);
    }

    public void clearProductInfo() {
        checkDao(mInfoDao);
        mInfoDao.deleteAll();
    }

    public void clearNewProductAndApplication() {
        checkDao(mNewProductsAndApplicationDao);
        mNewProductsAndApplicationDao.deleteAll();
    }

    public void clearNewProductAndCate() {
        checkDao(mNewProductAndCategoryDao);
        mNewProductAndCategoryDao.deleteAll();
    }

    public void clearProductApplication() {
        checkDao(mProductApplicationDao);
        mProductApplicationDao.deleteAll();
    }

    public void clearProductImage() {
        checkDao(mProductImageDao);
        mProductImageDao.deleteAll();
    }

    private void checkDao(AbstractDao dao) {
        if (dao == null) {
            throw new NullPointerException("dao cannot be null,please #initDao()");
        }
    }


}
