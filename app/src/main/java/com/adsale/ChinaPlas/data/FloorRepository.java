package com.adsale.ChinaPlas.data;

import android.database.Cursor;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.dao.ExhibitorDao;
import com.adsale.ChinaPlas.dao.FloorDao;
import com.adsale.ChinaPlas.dao.FloorPlanCoordinate;
import com.adsale.ChinaPlas.dao.FloorPlanCoordinateDao;
import com.adsale.ChinaPlas.dao.MapFloor;
import com.adsale.ChinaPlas.dao.MapFloorDao;
import com.adsale.ChinaPlas.data.model.InterestedExhibitor;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carrie on 2017/10/30.
 */

public class FloorRepository {
    private final String TAG = "FloorRepository";
    private static FloorRepository INSTANCE;
    private MapFloorDao mapFloorDao;
    private FloorDao mFloorDao;
    private FloorPlanCoordinateDao mFloorCoordinateDao;

    public static FloorRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FloorRepository();
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

    private void checkFloorDaoNull() {
        if (mFloorDao == null) {
            throw new NullPointerException("mFloorDao cannot be null, please #initFloorDao");
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


    //展商分布
    public void initFloorDao() {
        mFloorDao = App.mDBHelper.mFloorDao;
    }

    /**
     * 查询Floor数据库所哟数据
     *
     * @return
     */
    public ArrayList<InterestedExhibitor> getFloorIDLists() {
        ArrayList<InterestedExhibitor> floors = new ArrayList<InterestedExhibitor>();
        Cursor cursor = App.mDBHelper.db.query("FLOOR", new String[]{"FLOOR_ID"}, null, null, null, null, "SEQ");
        if (cursor != null) {
            String floorID;
            InterestedExhibitor floor;
            while (cursor.moveToNext()) {
                floorID = cursor.getString(cursor.getColumnIndex("FLOOR_ID"));
                if (!floorID.startsWith("A") && !floorID.equals("TBC")) {
                    floor = new InterestedExhibitor();
                    floor.floorID = floorID;
                    floors.add(floor);
                }
            }
            cursor.close();
        }
        return floors;
    }

    /**
     * 展商分布：每个展馆的兴趣展商（我的参展商）的数量
     *
     * @param floors
     * @return
     */
    public ArrayList<InterestedExhibitor> getMyExhibitorFloor(ArrayList<InterestedExhibitor> floors) {
        ArrayList<InterestedExhibitor> lists = new ArrayList<InterestedExhibitor>();
        String floorId = "";
        int count = 0;
        InterestedExhibitor entity = null;

        try {
            Cursor cursor = App.mDBHelper.db.rawQuery(
                    "select count(COMPANY_ID) as Count," + ExhibitorDao.Properties.HallNo.columnName + " from EXHIBITOR where IS_FAVOURITE=? group by " + ExhibitorDao.Properties.HallNo.columnName,
                    new String[]{"1"});
            if (cursor != null) {
                int size = floors.size();
                while (cursor.moveToNext()) {
                    count = cursor.getInt(0);//cursor.getColumnIndex("Count")
                    floorId = cursor.getString(1);// floorID_ cursor.getColumnIndex(ExhibitorDao.Properties.HallNo.columnName)
                    LogUtil.i(TAG, "floorID:" + floorId + ":" + count);
                    entity = new InterestedExhibitor();
                    entity.floorID = floorId;
                    entity.count = count;
                    lists.add(entity);

                    for (int i = 0; i < size; i++) {
                        if (floors.get(i).floorID.equals(floorId)) {
                            floors.set(i, entity);
                            break;
                        }
                    }

                }
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return floors;
    }

    /*   FloorPlanCoordinate  */
    private void checkFloorCoordinateDao() {
        if (mFloorCoordinateDao == null) {
            mFloorCoordinateDao = App.mDBHelper.mFloorPlanCdntDao;
        }
    }

    public void clearFloorCoordinate() {
        checkFloorCoordinateDao();
        mFloorCoordinateDao.deleteAll();
    }

    public void insertFloorCoordinate(ArrayList<FloorPlanCoordinate> list) {
        checkFloorCoordinateDao();
        mFloorCoordinateDao.insertOrReplaceInTx(list);
    }

    public ArrayList<FloorPlanCoordinate> getFloorCoors(String hall) {
        checkFloorCoordinateDao();
        return (ArrayList<FloorPlanCoordinate>) mFloorCoordinateDao.queryBuilder().where(FloorPlanCoordinateDao.Properties.Hall.eq(hall)).list();
    }

    public FloorPlanCoordinate getItemFloorCoor(String booth) {
        checkFloorCoordinateDao();
        List<FloorPlanCoordinate> list = mFloorCoordinateDao.queryBuilder().where(FloorPlanCoordinateDao.Properties.BoothNum.eq(booth)).list();
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 通过 BoothNum 得到 Exhibitor
     * select E.COMPANY_ID,E.COMPANY_NAME_EN,E.COMPANY_NAME_TW,E.COMPANY_NAME_CN,E.TEL,E.EMAIL,E.IS_FAVOURITE
     * from EXHIBITOR E,FLOOR_PLAN_COORDINATE F WHERE F.BOOTH_NUM=E.BOOTH_NO AND F.BOOTH_NUM='1A91'
     */
    public ArrayList<Exhibitor> getBoothExhibitors(String boothNo) {
        String sql = "select E.COMPANY_ID,E.COMPANY_NAME_EN,E.COMPANY_NAME_TW,E.COMPANY_NAME_CN,E.TEL,E.EMAIL,E.IS_FAVOURITE,E.BOOTH_NO\n" +
                "  from EXHIBITOR E,FLOOR_PLAN_COORDINATE F WHERE F.BOOTH_NUM=E.BOOTH_NO AND F.BOOTH_NUM='%s'";
        Cursor cursor = App.mDBHelper.db.rawQuery(String.format(sql, boothNo), null);
        ArrayList<Exhibitor> list = new ArrayList<>();
        Exhibitor exhibitor;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                exhibitor = new Exhibitor();
                exhibitor.setCompanyID(cursor.getString(0));
                exhibitor.setCompanyNameEN(cursor.getString(1));
                exhibitor.setCompanyNameTW(cursor.getString(2));
                exhibitor.setCompanyNameCN(cursor.getString(3));
                exhibitor.setTel(cursor.getString(4));
                exhibitor.setEmail(cursor.getString(5));
                exhibitor.setIsFavourite(cursor.getInt(6));
                exhibitor.setBoothNo(cursor.getString(7));
                list.add(exhibitor);
            }
            cursor.close();
        }
        LogUtil.i(TAG, "List=" + list.size());
        return list;
    }

    /**
     * select COMPANY_ID,COMPANY_NAME_EN,COMPANY_NAME_TW,COMPANY_NAME_CN,IS_FAVOURITE,BOOTH_NO
     * from EXHIBITOR WHERE (COMPANY_NAME_EN like '%etContent%' or COMPANY_NAME_TW like '%etContent%' or COMPANY_NAME_CN like '%etContent%' or BOOTH_NO like '%etContent%') AND HALL_NO='2H'
     *
     * @param etContent
     * @return
     */
    public ArrayList<Exhibitor> getEditExhibitors(String etContent, String hall) {
        StringBuilder sbSQL = new StringBuilder();
        sbSQL.append("select COMPANY_ID,COMPANY_NAME_EN,COMPANY_NAME_TW,COMPANY_NAME_CN,IS_FAVOURITE,BOOTH_NO" +
                "   from EXHIBITOR WHERE (COMPANY_NAME_EN like '%")
                .append(etContent).append("%' or COMPANY_NAME_TW like '%")
                .append(etContent).append("%' or COMPANY_NAME_CN like '%")
                .append(etContent).append("%' or BOOTH_NO like '%")
                .append(etContent).append("%') AND HALL_NO='").append(hall).append("' order by BOOTH_NO ");
        Cursor cursor = App.mDBHelper.db.rawQuery(sbSQL.toString(), null);
        ArrayList<Exhibitor> list = new ArrayList<>();
        Exhibitor exhibitor;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                exhibitor = new Exhibitor();
                exhibitor.setCompanyID(cursor.getString(0));
                exhibitor.setCompanyNameEN(cursor.getString(1));
                exhibitor.setCompanyNameTW(cursor.getString(2));
                exhibitor.setCompanyNameCN(cursor.getString(3));
                exhibitor.setIsFavourite(cursor.getInt(4));
                exhibitor.setBoothNo(cursor.getString(5));
                list.add(exhibitor);
            }
            cursor.close();
        }
        LogUtil.i(TAG, "List=" + list.size());
        return list;
    }

}
