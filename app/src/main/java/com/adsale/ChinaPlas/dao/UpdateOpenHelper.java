package com.adsale.ChinaPlas.dao;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.adsale.ChinaPlas.dao.ApplicationCompanyDao;
import com.adsale.ChinaPlas.dao.ApplicationIndustryDao;
import com.adsale.ChinaPlas.dao.CountryDao;
import com.adsale.ChinaPlas.dao.DaoMaster;
import com.adsale.ChinaPlas.dao.ExhibitorDao;
import com.adsale.ChinaPlas.dao.ExhibitorIndustryDtlDao;
import com.adsale.ChinaPlas.dao.ExhibitorUserUpdateDao;
import com.adsale.ChinaPlas.dao.FloorDao;
import com.adsale.ChinaPlas.dao.IndustryDao;
import com.adsale.ChinaPlas.dao.MainIconDao;
import com.adsale.ChinaPlas.dao.NameCardDao;
import com.adsale.ChinaPlas.dao.NewsDao;
import com.adsale.ChinaPlas.dao.NewsLinkDao;
import com.adsale.ChinaPlas.dao.ProductIDDao;
import com.adsale.ChinaPlas.dao.ScheduleInfoDao;
import com.adsale.ChinaPlas.dao.UpdateDateDao;
import com.adsale.ChinaPlas.dao.WebContentDao;

/**
 * ���ݿ�����
 	* @Description: TODO update
 	* @author Carrie
 	* @version ����ʱ�䣺2016��6��21�� ����5:06:40
 */
public class UpdateOpenHelper extends DaoMaster.OpenHelper {
	public UpdateOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
		super(context, name, factory);
	}

	@Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		
        MigrationHelper.migrate(db,ApplicationCompanyDao.class,ApplicationIndustryDao.class,CountryDao.class,ExhibitorDao.class,ExhibitorIndustryDtlDao.class,ExhibitorUserUpdateDao.class,
        		FloorDao.class,IndustryDao.class,
        		MainIconDao.class,NameCardDao.class,NewsDao.class,NewsLinkDao.class,ProductIDDao.class,ScheduleInfoDao.class,UpdateDateDao.class,WebContentDao.class);
    }
}
