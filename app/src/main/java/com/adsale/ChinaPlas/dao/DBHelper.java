package com.adsale.ChinaPlas.dao;

import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库管理器
 * Created by Administrator on 2016/6/23 0023.
 */
public class DBHelper {
    private static final String TAG = "DBHelper";

    private DaoSession mDaoSession;
    private DaoMaster mDaoMaster;
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
    public NewProductInfoDao mNewProductInfoDao;
    public NewProductAndCategoryDao mNewProductAndCategoryDao;
    public NewProductsAndApplicationDao mNewProductsAndApplicationDao;
    public ProductApplicationDao mProductApplicationDao;
    public ProductImageDao mProductImageDao;
    public ExhibitorZoneDao mExhibitorZoneDao;
    public ZoneDao mZoneDao;


    private DBHelper(Builder builder) {
        mDaoSession = builder.mDaoSession;
        mDaoMaster = builder.mDaoMaster;
        db = builder.mDB;
        initDao();
    }

    public static final class Builder {
        private DaoSession mDaoSession;
        private DaoMaster mDaoMaster;
        private SQLiteDatabase mDB;

        public Builder(DaoSession daoSession, DaoMaster daoMaster, SQLiteDatabase db) {
            this.mDaoSession = daoSession;
            this.mDaoMaster = daoMaster;
            this.mDB = db;
        }

        public DBHelper build() {
            return new DBHelper(this);
        }
    }

    public void initDao() {
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
        mNewProductInfoDao=mDaoSession.getNewProductInfoDao();
        mNewProductAndCategoryDao=mDaoSession.getNewProductAndCategoryDao();
        mNewProductsAndApplicationDao=mDaoSession.getNewProductsAndApplicationDao();
        mProductImageDao=mDaoSession.getProductImageDao();
        mProductApplicationDao=mDaoSession.getProductApplicationDao();
        mExhibitorZoneDao = mDaoSession.getExhibitorZoneDao();
        mZoneDao = mDaoSession.getZoneDao();
    }

    /**
     * 当有更新的时候，将oldDB的这些数据清空。不清空的话仍保存原来的数据，不是新的。
     */
    public void setToNull() {
        mDaoMaster = null;
        mDaoSession = null;
        db = null;
    }


}