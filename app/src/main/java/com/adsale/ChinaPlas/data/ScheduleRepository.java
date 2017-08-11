package com.adsale.ChinaPlas.data;

import com.adsale.ChinaPlas.dao.DBHelper;
import com.adsale.ChinaPlas.dao.ScheduleInfo;
import com.adsale.ChinaPlas.dao.ScheduleInfoDao;

import java.util.ArrayList;

import static android.R.id.list;
import static com.adsale.ChinaPlas.App.mDBHelper;

/**
 * Created by Carrie on 2017/8/11.
 */

public class ScheduleRepository implements DataSource<ScheduleInfo> {
    private ScheduleInfoDao mScheduleInfoDao = mDBHelper.mScheduleInfoDao;
    private static ScheduleRepository INSTANCE;

    public static ScheduleRepository getInstance(){
        if(INSTANCE==null){
            return new ScheduleRepository();
        }
        return INSTANCE;
    }

    //StartTime 的格式： 0/09:00 ； 1/13:00 ; 0,1,2,3 代表第几天

    public ArrayList<ScheduleInfo> getDateSchedules(String date){
        return (ArrayList<ScheduleInfo>) mScheduleInfoDao.queryBuilder().where(ScheduleInfoDao.Properties.StartTime.like("%"+date+"%")).list();
    }


    @Override
    public void insertData(ArrayList<ScheduleInfo> list) {

    }

    @Override
    public void insertItemData(ScheduleInfo entity) {
        mScheduleInfoDao.insert(entity);
    }

    @Override
    public ArrayList<ScheduleInfo> getData() {
        return null;
    }

    @Override
    public ScheduleInfo getItemData(String id) {
        return null;
    }

    public ScheduleInfo getItemData(long id){
        return mScheduleInfoDao.load(id);
    }

    @Override
    public void saveData(ArrayList<ScheduleInfo> list) {

    }

    @Override
    public void saveItemData(ScheduleInfo entity) {

    }

    @Override
    public void updateData(ArrayList<ScheduleInfo> list) {

    }

    @Override
    public void updateItemData(ScheduleInfo entity) {

    }

    @Override
    public void deleteData() {

    }

    @Override
    public void deleteItemData(String id) {

    }

    @Override
    public void queryData(String text) {

    }
}
