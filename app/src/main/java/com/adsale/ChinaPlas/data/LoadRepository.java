package com.adsale.ChinaPlas.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.dao.MainIcon;
import com.adsale.ChinaPlas.dao.MainIconDao;
import com.adsale.ChinaPlas.dao.MapFloor;
import com.adsale.ChinaPlas.dao.MapFloorDao;
import com.adsale.ChinaPlas.dao.News;
import com.adsale.ChinaPlas.dao.NewsDao;
import com.adsale.ChinaPlas.dao.NewsLink;
import com.adsale.ChinaPlas.dao.NewsLinkDao;
import com.adsale.ChinaPlas.dao.UpdateCenter;
import com.adsale.ChinaPlas.dao.UpdateCenterDao;
import com.adsale.ChinaPlas.dao.WebContent;
import com.adsale.ChinaPlas.dao.WebContentDao;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;

/**
 * Created by Carrie on 2017/9/12.
 */

public class LoadRepository {

    private SQLiteDatabase db = App.mDBHelper.db;
    private UpdateCenterDao mUpdateCenterDao = App.mDBHelper.mUpdateCenterDao;
    private WebContentDao mWebContentDao = App.mDBHelper.mWebContentDao;
    private MainIconDao mMainIconDao = App.mDBHelper.mIconDao;
    private NewsDao mNewsDao = App.mDBHelper.mNewsDao;
    private NewsLinkDao mNewsLinkDao = App.mDBHelper.mLinkDao;
    private MapFloorDao mMapFloorDao = App.mDBHelper.mapFloorDao;

    private Context mContext;
    private boolean isFirstGetMaster;
    private String TAG;
    private SharedPreferences mSP_lut;


    public static LoadRepository getInstance(Context context) {
        return new LoadRepository(context);
    }

    private LoadRepository(Context context) {
        mContext = context;
    }

    /**
     * 获取数据库中 UPDATE_CENTER表 中该 @fileName 的最后更新时间
     *
     * @param fileName 文件名
     * @return LUT
     */
    public String getLocalTxtLUT(String fileName) {
        return mUpdateCenterDao.queryBuilder().where(UpdateCenterDao.Properties.ScanFile.eq(fileName)).list().get(0).getLUT();
    }

    public void updateLocalLUT(UpdateCenter entity) {
        mUpdateCenterDao.update(entity);
    }

    void prepareInsertXmlData() {
        isFirstGetMaster = mContext.getSharedPreferences(Constant.SP_CONFIG, Context.MODE_PRIVATE).getBoolean("isFirstGetMaster", false);
        LogUtil.i("LoadRepo", "isFirstGetMaster=" + isFirstGetMaster);
        mSP_lut = mContext.getSharedPreferences(Constant.SP_LUT, Context.MODE_PRIVATE);
    }

    private <T> boolean deleteAll(ArrayList<T> list) {
        return isFirstGetMaster && list.size() > 0;
    }

    private <T> void insertAll(ArrayList<T> list, AbstractDao<T, String> dao) {
        if (list.size() > 0) {
            long startTime = System.currentTimeMillis();
            dao.insertOrReplaceInTx(list);
            LogUtil.i(TAG, "insertMainIconAll：" + (System.currentTimeMillis() - startTime) + "ms");
        }
    }

    void insertMainIconAll(ArrayList<MainIcon> list) {
        if (deleteAll(list)) {
            LogUtil.e(TAG, mMainIconDao.getTablename() + " --->>> clear all data");
            mMainIconDao.deleteAll();
        }
        insertAll(list, mMainIconDao);
    }

    void insertNewsAll(ArrayList<News> list) {
        if (deleteAll(list)) {
            LogUtil.e(TAG, mNewsDao.getTablename() + " --->>> clear all data");
            mNewsDao.deleteAll();
        }
        insertAll(list, mNewsDao);
    }

    void insertNewsLinkAll(ArrayList<NewsLink> list) {
        if (deleteAll(list)) {
            LogUtil.e(TAG, mNewsLinkDao.getTablename() + " --->>> clear all data");
            mNewsLinkDao.deleteAll();
        }
        insertAll(list, mNewsLinkDao);
    }

    void insertWebContentAll(ArrayList<WebContent> list) {
        if (deleteAll(list)) {
            LogUtil.e(TAG, mWebContentDao.getTablename() + " --->>> clear all data");
            mWebContentDao.deleteAll();
        }
        insertAll(list, mWebContentDao);
    }

    void insertMapFloorAll(ArrayList<MapFloor> list) {
        if (deleteAll(list)) {
            LogUtil.e(TAG, mMapFloorDao.getTablename() + " --->>> clear all data");
            mMapFloorDao.deleteAll();
        }
        insertAll(list, mMapFloorDao);
    }

    void setLUT() {
        mSP_lut.edit()
                .putString(MainIconDao.TABLENAME, getMaxUT(MainIconDao.Properties.UpdateDateTime, mMainIconDao).getUpdateDateTime())
                .putString(NewsDao.TABLENAME, getMaxUT(NewsDao.Properties.UpdateDateTime, mNewsDao).getUpdateDateTime())
                .putString(NewsLinkDao.TABLENAME, getMaxUT(NewsLinkDao.Properties.UpdateDateTime, mNewsLinkDao).getUpdateDateTime())
                .putString(WebContentDao.TABLENAME, getMaxUT(WebContentDao.Properties.UpdateDateTime, mWebContentDao).getUpdateDateTime())
                .putString(MapFloorDao.TABLENAME, getMaxUT(MapFloorDao.Properties.UpdateDateTime, mMapFloorDao).getUpdateDateTime())
                .apply();
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
