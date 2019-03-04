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
    public MainIconTestDao mIconTestDao;
    public ApplicationDao mApplicationDao;
    public CompanyApplicationDao mCompanyApplicationDao;
    public IndustryDao mIndustryDao;
    public CompanyProductDao mCompanyProductDao;
    public CountryDao mCountryDao;
    public ExhibitorDao mExhibitorDao;
    public ExhibitorUserUpdateDao mUserUpdateDao;
    public MapDao mMapDao;
    public MapFloorDao mapFloorDao;
    public NameCardDao mNameCardDao;
    public NewsDao mNewsDao;
    public NewsLinkDao mLinkDao;
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
    public NewCategoryIDDao mNewCategoryIDDao;
    public NewProductsIDDao mNewProductsIDDao;
    public ProductImageDao mProductImageDao;
    public NewCategorySubDao mCategorySubDao;
    public ExhibitorZoneDao mExhibitorZoneDao;
    public ZoneDao mZoneDao;
    public ConcurrentEventDao mEventDao;
    public EventApplicationDao mEventAppDao;


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
        mIconTestDao = mDaoSession.getMainIconTestDao();
        mApplicationDao = mDaoSession.getApplicationDao();
        mCompanyApplicationDao = mDaoSession.getApplicationCompanyDao();
        mCountryDao = mDaoSession.getCountryDao();
        mExhibitorDao = mDaoSession.getExhibitorDao();
        mUserUpdateDao = mDaoSession.getExhibitorUserUpdateDao();
        mMapDao = mDaoSession.getMapDao();
        mIndustryDao = mDaoSession.getIndustryDao();
        mCompanyProductDao = mDaoSession.getCompanyProductDao();
        mapFloorDao = mDaoSession.getMapFloorDao();
        mNameCardDao = mDaoSession.getNameCardDao();
        mLinkDao = mDaoSession.getNewsLinkDao();
        mNewsDao = mDaoSession.getNewsDao();
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
        mNewCategoryIDDao =mDaoSession.getNewCategoryIDDao();
        mNewProductsIDDao =mDaoSession.getNewProductsIDDao();
        mProductImageDao=mDaoSession.getProductImageDao();
        mCategorySubDao=mDaoSession.getCategorySubDao();
        mExhibitorZoneDao = mDaoSession.getExhibitorZoneDao();
        mZoneDao = mDaoSession.getZoneDao();
        mEventDao = mDaoSession.getEventDao();
        mEventAppDao = mDaoSession.getEventApplicationDao();
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