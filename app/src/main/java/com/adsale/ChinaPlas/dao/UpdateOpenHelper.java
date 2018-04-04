package com.adsale.ChinaPlas.dao;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

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
        		MainIconDao.class,NameCardDao.class,NewsDao.class,NewsLinkDao.class,ScheduleInfoDao.class,UpdateDateDao.class,WebContentDao.class);
    }
}
