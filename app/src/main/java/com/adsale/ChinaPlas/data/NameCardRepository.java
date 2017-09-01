package com.adsale.ChinaPlas.data;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.dao.NameCard;
import com.adsale.ChinaPlas.dao.NameCardDao;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/8/28.
 */

public class NameCardRepository implements DataSource<NameCard> {
    private static final String TAG = "NameCardRepository";
    private NameCardDao nameCardDao = App.mDBHelper.mNameCardDao;

    public ArrayList<NameCard> getSearchData(String text) {
        return (ArrayList<NameCard>) nameCardDao.queryBuilder().whereOr(NameCardDao.Properties.Company.like("%" + text + "%"), NameCardDao.Properties.Name.like("%" + text + "%")).list();
    }

    @Override
    public ArrayList<NameCard> getData() {
        return (ArrayList<NameCard>) nameCardDao.loadAll();
    }

    @Override
    public NameCard getItemData(Object obj) {
        return null;
    }

    @Override
    public void updateItemData(NameCard entity) {
        nameCardDao.update(entity);
    }

    @Override
    public void insertItemData(NameCard entity) {
        nameCardDao.insert(entity);
    }

    @Override
    public void deleteItemData(Object obj) {

    }

    public boolean isNameCardExisits(String deviceId){
        return  !nameCardDao.queryBuilder().where(NameCardDao.Properties.DeviceId.eq(deviceId)).list().isEmpty();
    }
}
