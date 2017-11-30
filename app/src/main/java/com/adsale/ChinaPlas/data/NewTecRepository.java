package com.adsale.ChinaPlas.data;

import android.content.Context;
import android.database.Cursor;

import com.adsale.ChinaPlas.App;
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

    public static NewTecRepository newInstance() {
        return new NewTecRepository();
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
        Cursor cursor = App.mDBHelper.db.rawQuery("select N.*,I.IMAGE__FILE AS IMAGE from NEW_PRODUCT_INFO N, PRODUCT_IMAGE I WHERE N.RID=I.RID AND N.COMPANY_ID='?'", new String[]{companyId});
        return getInfoList(cursor);
//        return (ArrayList<NewProductInfo>) mInfoDao.queryBuilder().where(NewProductInfoDao.Properties.CompanyID.eq(companyId)).list();
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
                list.add(info);
            }
            cursor.close();
        }
        return list;
    }

    /**
     * NewProductAndCategory.csv(划掉)，获取 产品 列表
     */
    public ArrayList<NewProductInfo> getFilterList(Context context){
       ArrayList<NewProductInfo> list = new ArrayList<>();

        return list;
    }

    private String getFilterSql(ArrayList<ExhibitorFilter> filters){
        int size = filters.size();
        ArrayList<String> industriesStr = new ArrayList<>();
        ArrayList<String> appStr = new ArrayList<>();
        ExhibitorFilter filter;
        String keyword = "";
        int index;
        String sql = getBaseSql();
        for (int i = 0; i < size; i++) {
            filter = filters.get(i);
            index = filter.index;
             if (index == 0) {
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
        sql = String.format(sql, halls.toString().replace("[", "").replace("]", ""),
                countries.toString().replace("[", "").replace("]", ""),
                industriesStr.toString().replaceAll(",", " intersect").replace("[", "").replace("]", ""),
                appStr.toString().replaceAll(",", " intersect").replace("[", "").replace("]", ""));
        sql = sql.replaceAll("carriecps", "E.DESC_E LIKE '%".concat(keyword).concat("%' OR E.DESC_S LIKE '%").concat(keyword).concat("%' OR E.DESC_T LIKE '%").concat(keyword).concat("%'"));
        LogUtil.i(TAG, ">>>> sql=" + sql);
        return sql;
    }

    private String getBaseSql(){

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
