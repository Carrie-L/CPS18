package com.adsale.ChinaPlas.data;

import com.adsale.ChinaPlas.dao.DBHelper;
import com.adsale.ChinaPlas.dao.ScheduleInfo;
import com.adsale.ChinaPlas.dao.ScheduleInfoDao;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;

import static android.R.id.list;
import static android.content.ContentValues.TAG;
import static com.adsale.ChinaPlas.App.mDBHelper;

/**
 * Created by Carrie on 2017/8/11.
 */

public class ScheduleRepository implements DataSource<ScheduleInfo> {
    private ScheduleInfoDao mScheduleInfoDao = mDBHelper.mScheduleInfoDao;
    private static ScheduleRepository INSTANCE;

    public static ScheduleRepository getInstance() {
        if (INSTANCE == null) {
            return new ScheduleRepository();
        }
        return INSTANCE;
    }

    public ArrayList<ScheduleInfo> getDateSchedules(String date) {
        return (ArrayList<ScheduleInfo>) mScheduleInfoDao.queryBuilder().where(ScheduleInfoDao.Properties.StartDate.eq(date))
                .orderAsc(ScheduleInfoDao.Properties.StartDate).orderAsc(ScheduleInfoDao.Properties.StartTime).list();
    }

    @Override
    public void insertItemData(ScheduleInfo entity) {
        mScheduleInfoDao.insert(entity);
    }

    public void insertOrReplaceItemData(ScheduleInfo entity) {
        mScheduleInfoDao.insertOrReplace(entity);
    }

    @Override
    public void deleteItemData(Object obj) {
        mScheduleInfoDao.deleteByKey((Long) obj);
    }

    @Override
    public ArrayList<ScheduleInfo> getData() {
        return null;
    }

    @Override
    public ScheduleInfo getItemData(Object id) {
        return mScheduleInfoDao.load((Long) id);
    }

    public ScheduleInfo getItemData(long id) {
        return mScheduleInfoDao.load(id);
    }

    @Override
    public void updateItemData(ScheduleInfo entity) {
        mScheduleInfoDao.update(entity);
    }

    public boolean isSameTimeSchedule(String date, String time) {
        return !mScheduleInfoDao.queryBuilder().where(ScheduleInfoDao.Properties.StartDate.eq(date), ScheduleInfoDao.Properties.StartTime.like("%" + time + "%")).limit(1).list().isEmpty();
    }


}
