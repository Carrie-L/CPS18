package com.adsale.ChinaPlas.data;

import android.os.Bundle;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.dao.DBHelper;
import com.adsale.ChinaPlas.dao.NewProductAndCategoryDao;
import com.adsale.ChinaPlas.dao.NewProductInfo;
import com.adsale.ChinaPlas.dao.NewProductInfoDao;
import com.adsale.ChinaPlas.dao.NewProductsAndApplicationDao;
import com.adsale.ChinaPlas.dao.ProductApplicationDao;
import com.adsale.ChinaPlas.dao.ProductImageDao;
import com.adsale.ChinaPlas.data.model.NewTec;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/11/16.
 * 新技术产品
 */

public class NewTecRepository {

    /**
     * 非广告展商的 new tec 资料
     */
    private NewProductInfoDao mInfoDao;
    public NewProductAndCategoryDao mNewProductAndCategoryDao;
    public NewProductsAndApplicationDao mNewProductsAndApplicationDao;
    public ProductApplicationDao mProductApplicationDao;
    public ProductImageDao mProductImageDao;

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

    public ArrayList<NewProductInfo> getProductInfoList(String companyId) {
        if(mInfoDao==null){
            throw new NullPointerException("mInfoDao cannot be null,please #initDao()");
        }
        return (ArrayList<NewProductInfo>) mInfoDao.queryBuilder().where(NewProductInfoDao.Properties.CompanyID.eq(companyId)).list();
    }

    public void insertNewProductInfoAll(ArrayList<NewProductInfo> list) {
        mInfoDao.insertInTx(list);
    }

    public void clearProductInfo() {
        if(mInfoDao==null){
            throw new NullPointerException("mInfoDao cannot be null,please #initDao()");
        }
        mInfoDao.deleteAll();
    }


}
