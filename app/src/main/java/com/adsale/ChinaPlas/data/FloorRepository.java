package com.adsale.ChinaPlas.data;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.dao.MapFloor;
import com.adsale.ChinaPlas.dao.MapFloorDao;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/10/30.
 */

public class FloorRepository {
    private final String TAG = "FloorRepository";
    private static FloorRepository INSTANCE;
    private MapFloorDao mapFloorDao;

    public static FloorRepository getInstance() {
        if (INSTANCE == null) {
            return new FloorRepository();
        }
        return INSTANCE;
    }

    public void initMapFloorDao() {
        mapFloorDao = App.mDBHelper.mapFloorDao;
    }

    private void checkMapFloorDaoNull() {
        if (mapFloorDao == null) {
            throw new NullPointerException("mapFloorDao cannot be null, please #initMapFloorDao");
        }
    }

    /**
     * 获取MapFloor中需要更新下载的图片名称
     */
    public ArrayList<MapFloor> getMapFloorNeedDowns(String lut) {
        checkMapFloorDaoNull();
        LogUtil.i(TAG, "lut=" + lut);
        return (ArrayList<MapFloor>) mapFloorDao.queryBuilder().whereOr(MapFloorDao.Properties.UpdateDateTime.lt(lut), MapFloorDao.Properties.Down.eq(0)).list();// TODO: 2016/10/14   测试下载，将[le]改为[ge]
    }

}
