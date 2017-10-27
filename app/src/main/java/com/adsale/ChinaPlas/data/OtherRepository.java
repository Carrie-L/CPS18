package com.adsale.ChinaPlas.data;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.dao.SeminarInfo;
import com.adsale.ChinaPlas.dao.SeminarInfoDao;
import com.adsale.ChinaPlas.dao.SeminarSpeaker;
import com.adsale.ChinaPlas.dao.SeminarSpeakerDao;
import com.adsale.ChinaPlas.dao.UpdateCenter;
import com.adsale.ChinaPlas.dao.UpdateCenterDao;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/8/13.
 */

public class OtherRepository {
    private static OtherRepository INSTANCE;
    private final String TAG = "OtherRepository";
    private SeminarInfoDao mSeminarInfoDao;
    private SeminarSpeakerDao mSeminarSpeakerDao;
    private UpdateCenterDao mUpdateCenterDao;

    public static OtherRepository getInstance() {
        if (INSTANCE == null) {
            return new OtherRepository();
        }
        return INSTANCE;
    }




    /* [News] */

    /* ```````````````[Technical Seminar]`````````````````````````````` */
    public void initTechSeminarDao() {
        mSeminarInfoDao = App.mDBHelper.mSeminarInfoDao;
    }

    public void initSeminarSpeakDao() {
        mSeminarSpeakerDao = App.mDBHelper.mSeminarSpeakerDao;
    }

    public ArrayList<SeminarInfo> getAllSeminars(int langId) {
        if(mSeminarInfoDao==null){
            throw new NullPointerException("mSeminarInfoDao cannot be null, please #initTechSeminarDao() first.");
        }
        return (ArrayList<SeminarInfo>) mSeminarInfoDao.queryBuilder().where(SeminarInfoDao.Properties.LangID.eq(langId)).orderAsc(SeminarInfoDao.Properties.Date).orderAsc(SeminarInfoDao.Properties.Time).orderAsc(SeminarInfoDao.Properties.OrderMob).list();
    }

    /**
     * select * from SEMINAR_SPEAKER  WHERE COMPANY_ID="229048" AND LANG_ID="936"
     * @return ArrayList<SeminarSpeaker> may be null
     */
    public ArrayList<SeminarSpeaker> getSeminarSpeakerItem(String companyID, int langID){
        if(mSeminarSpeakerDao==null){
            throw new NullPointerException("mSeminarSpeakerDao cannot be null, please #initSeminarSpeakDao() first.");
        }
        return (ArrayList<SeminarSpeaker>) mSeminarSpeakerDao.queryBuilder().where(SeminarSpeakerDao.Properties.CompanyID.eq(companyID), SeminarSpeakerDao.Properties.LangID.eq(langID)).list();
    }


    /* ```````````````[Technical Seminar]`````````````````````````````` */
    public void initUpdateCenterDao() {
        mUpdateCenterDao = App.mDBHelper.mUpdateCenterDao;
    }

    public ArrayList<UpdateCenter> getUpdateCenters(){
        if(mUpdateCenterDao==null){
            throw new NullPointerException("mUpdateCenterDao cannot be null, please #initUpdateCenterDao() first.");
        }
        return (ArrayList<UpdateCenter>) mUpdateCenterDao.queryBuilder().orderAsc(UpdateCenterDao.Properties.Id).list();
    }
 }
