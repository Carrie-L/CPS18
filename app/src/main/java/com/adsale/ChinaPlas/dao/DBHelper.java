package com.adsale.ChinaPlas.dao;

import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库管理器
 * Created by Administrator on 2016/6/23 0023.
 */
public class DBHelper {
    private static final String TAG = "DBHelper";

    private DaoSession mDaoSession;
    public SQLiteDatabase db;

    public MainIconDao mIconDao;
    public ApplicationIndustryDao mAppIndustryDao;
    public ApplicationCompanyDao mAppCompanyDao;
    public CountryDao mCountryDao;
    public ExhibitorDao mExhibitorDao;
    public ExhibitorIndustryDtlDao mIndustryDtlDao;
    public ExhibitorUserUpdateDao mUserUpdateDao;
    public FloorDao mFloorDao;
    public IndustryDao mIndustryDao;
    public MapFloorDao mapFloorDao;
    public NameCardDao mNameCardDao;
    public NewsDao mNewsDao;
    public NewsLinkDao mLinkDao;
    public ProductIDDao mProductIDDao;
    public ScheduleInfoDao mScheduleInfoDao;
    public UpdateDateDao mUpdateDateDao;
    public WebContentDao mWebContentDao;
    public BussinessMappingDao mBsnsMappingDao;
    public HistoryExhibitorDao mHistoryExhibitorDao;
    public UpdateCenterDao mUpdateCenterDao;
    public FloorPlanCoordinateDao mFloorPlanCdntDao;
    public SeminarInfoDao mSeminarInfoDao;
    public SeminarSpeakerDao mSeminarSpeakerDao;
    public EXHIBITOR_TABLE_TEMPDao mExhibitorTempDao;
    public HISTORY_EXHIBITOR_TEMPDao mHistoryTempDao;
    public NAME_CARD_TEMPDao mNameCardTempDao;
    public SCHEDULE_INFO_TEMPDao mScheduleTempDao;


    private DBHelper(Builder builder) {
        mDaoSession = builder.mDaoSession;
        db = builder.mDB;
        initDao();
    }

    public static final class Builder {
        private DaoSession mDaoSession;
        private SQLiteDatabase mDB;

        public Builder(DaoSession daoSession, SQLiteDatabase db) {
            this.mDaoSession = daoSession;
            this.mDB = db;
        }

        public DBHelper build() {
            return new DBHelper(this);
        }
    }

    private void initDao() {
        mIconDao = mDaoSession.getMainIconDao();
        mAppIndustryDao = mDaoSession.getApplicationIndustryDao();
        mAppCompanyDao = mDaoSession.getApplicationCompanyDao();
        mCountryDao = mDaoSession.getCountryDao();
        mExhibitorDao = mDaoSession.getExhibitorDao();
        mIndustryDtlDao = mDaoSession.getExhibitorIndustryDtlDao();
        mUserUpdateDao = mDaoSession.getExhibitorUserUpdateDao();
        mFloorDao = mDaoSession.getFloorDao();
        mIndustryDao = mDaoSession.getIndustryDao();
        mapFloorDao = mDaoSession.getMapFloorDao();
        mNameCardDao = mDaoSession.getNameCardDao();
        mLinkDao = mDaoSession.getNewsLinkDao();
        mNewsDao = mDaoSession.getNewsDao();
        mProductIDDao = mDaoSession.getProductIDDao();
        mScheduleInfoDao = mDaoSession.getScheduleInfoDao();
        mUpdateDateDao = mDaoSession.getUpdateDateDao();
        mWebContentDao = mDaoSession.getWebContentDao();
        mBsnsMappingDao = mDaoSession.getBussinessMappingDao();
        mHistoryExhibitorDao = mDaoSession.getHistoryExhibitorDao();
        mUpdateCenterDao = mDaoSession.getUpdateCenterDao();
        mFloorPlanCdntDao = mDaoSession.getFloorPlanCoordinateDao();
        mSeminarInfoDao = mDaoSession.getSeminarInfoDao();
        mSeminarSpeakerDao = mDaoSession.getSeminarSpeakerDao();
    }

}