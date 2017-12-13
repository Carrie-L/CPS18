package com.adsale.ChinaPlas.data;

import android.database.Cursor;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.dao.SeminarInfo;
import com.adsale.ChinaPlas.dao.SeminarInfoDao;
import com.adsale.ChinaPlas.dao.SeminarSpeaker;
import com.adsale.ChinaPlas.dao.SeminarSpeakerDao;
import com.adsale.ChinaPlas.dao.UpdateCenter;
import com.adsale.ChinaPlas.dao.UpdateCenterDao;
import com.adsale.ChinaPlas.dao.WebContentDao;
import com.adsale.ChinaPlas.data.model.adAdvertisementObj;
import com.adsale.ChinaPlas.helper.ADHelper;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import static com.adsale.ChinaPlas.viewmodel.TechViewModel.DATE1;
import static com.adsale.ChinaPlas.viewmodel.TechViewModel.DATE2;
import static com.adsale.ChinaPlas.viewmodel.TechViewModel.DATE3;
import static com.adsale.ChinaPlas.viewmodel.TechViewModel.DATE4;

/**
 * Created by Carrie on 2017/8/13.
 */

public class OtherRepository {
    private static OtherRepository INSTANCE;
    private final String TAG = "OtherRepository";
    private SeminarInfoDao mSeminarInfoDao;
    private SeminarSpeakerDao mSeminarSpeakerDao;
    private UpdateCenterDao mUpdateCenterDao;
    private WebContentDao mWebContentDao;

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

    public ArrayList<SeminarInfo> getAllSeminars(int langId, ADHelper adHelper) {
        if (mSeminarInfoDao == null) {
            throw new NullPointerException("mSeminarInfoDao cannot be null, please #initTechSeminarDao() first.");
        }
        ArrayList<SeminarInfo> list = new ArrayList<>();
        Cursor cursor = App.mDBHelper.db.rawQuery("select * from SEMINAR_INFO where LANG_ID=" + langId + " order by DATE,TIME,ORDER_MOB", null);
        SeminarInfo entity;
        adAdvertisementObj adObj = adHelper.getAdObj();
        SeminarInfo newEntity;
        int d0 = 0, d1 = 0, d2 = 0;
        ArrayList<SeminarInfo> adList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                entity = mSeminarInfoDao.readEntity(cursor, 0);
                int index = convertDateToIndex(entity.getDate());
                if (!adObj.M6B.version[index].equals("0") && adObj.M6B.companyID[index].equals(entity.getCompanyID())) {
                    entity.isADer.set(true);
                    entity.setAdLogoUrl(adHelper.getM6LogoUrl(index));
                    entity.setAdHeaderUrl(adHelper.getM6HeaderUrl(index));
                }
                // am  entity.getTime().compareTo("12:00") < 0
                /*  因为要在每个日期的bar下加一条广告图片，因此把每个日期中的ader（1个）加入到List，如果当天没有广告，则随便插入一个（因为只要日期） */

                if (adList.size() == index) {
                    /*  不能直接用 newEntity=entity; 会数据混乱。 */
                    newEntity = new SeminarInfo();
                    newEntity.setDate(entity.getDate());
                    newEntity.setTime(entity.getTime());
                    newEntity.setID(entity.getID());
                    newEntity.setLangID(entity.getLangID());
                    newEntity.isTypeLabel = true;
                    newEntity.setAdHeaderUrl(entity.getAdHeaderUrl());
                    newEntity.isADer.set(entity.isADer.get());
                    adList.add(newEntity);
                } else if (adList.size() == (index + 1) && !adList.get(index).isADer.get() && entity.isADer.get()) {
                    newEntity = new SeminarInfo();
                    newEntity.setDate(entity.getDate());
                    newEntity.setTime(entity.getTime());
                    newEntity.setID(entity.getID());
                    newEntity.setLangID(entity.getLangID());
                    newEntity.isTypeLabel = true;
                    newEntity.setAdHeaderUrl(entity.getAdHeaderUrl());
                    newEntity.isADer.set(entity.isADer.get());
                    adList.set(index, newEntity);
                }

                entity.isTypeLabel = false;
                list.add(entity);

                if (index == 0) {
                    d0++;
                } else if (index == 1) {
                    d1++;
                } else if (index == 2) {
                    d2++;
                }
            }
            cursor.close();

            LogUtil.i(TAG, "list  //// " + list.size() + "," + list.toString());
            LogUtil.i(TAG, "adList=" + adList.size() + "," + adList.toString());

            list.add(0, adList.get(0));
            list.add(d0 + 1, adList.get(1));
            list.add(d0 + d1 + 1, adList.get(2));
            list.add(d0 + d1 + d2 + 1, adList.get(3));
            LogUtil.i(TAG, "list  >>>> " + list.size() + "," + list.toString());

        }
        return list;
//        return (ArrayList<SeminarInfo>) mSeminarInfoDao.queryBuilder().where(SeminarInfoDao.Properties.LangID.eq(langId)).orderAsc(SeminarInfoDao.Properties.Date).orderAsc(SeminarInfoDao.Properties.Time).orderAsc(SeminarInfoDao.Properties.OrderMob).list();
    }

    private int convertDateToIndex(String date) {
        if (date.contains(DATE1)) {
            return 0;
        } else if (date.contains(DATE2)) {
            return 1;
        } else if (date.contains(DATE3)) {
            return 2;
        } else if (date.contains(DATE4)) {
            return 3;
        }
        return 0;
    }

    /**
     * select * from SEMINAR_SPEAKER  WHERE COMPANY_ID="229048" AND LANG_ID="936"
     *
     * @return ArrayList<SeminarSpeaker> may be null
     */
    public ArrayList<SeminarSpeaker> getSeminarSpeakerItem(String companyID, int langID) {
        if (mSeminarSpeakerDao == null) {
            throw new NullPointerException("mSeminarSpeakerDao cannot be null, please #initSeminarSpeakDao() first.");
        }
        return (ArrayList<SeminarSpeaker>) mSeminarSpeakerDao.queryBuilder().where(SeminarSpeakerDao.Properties.CompanyID.eq(companyID), SeminarSpeakerDao.Properties.LangID.eq(langID)).list();
    }

    public SeminarInfo getItemSeminarInfo(String id) {
        List<SeminarInfo> list = mSeminarInfoDao.queryBuilder().where(SeminarInfoDao.Properties.ID.eq(id)).list();
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }


    /* ```````````````[Update Center]`````````````````````````````` */
    public void initUpdateCenterDao() {
        mUpdateCenterDao = App.mDBHelper.mUpdateCenterDao;
    }

    public ArrayList<UpdateCenter> getUpdateCenters() {
        if (mUpdateCenterDao == null) {
            throw new NullPointerException("mUpdateCenterDao cannot be null, please #initUpdateCenterDao() first.");
        }
        return (ArrayList<UpdateCenter>) mUpdateCenterDao.queryBuilder().orderAsc(UpdateCenterDao.Properties.Id).list();
    }

    public void updateCenterItem(UpdateCenter entity) {
        mUpdateCenterDao.update(entity);
    }

    public void updateCenterAll(ArrayList<UpdateCenter> list) {
        mUpdateCenterDao.updateInTx(list);
    }

    /**
     * 获取更新中心需要下载的个数
     */
    public int getNeedUpdatedCount() {
        LogUtil.i(TAG, "getNeedUpdatedCount_" + mUpdateCenterDao.queryBuilder().where(UpdateCenterDao.Properties.Status.eq(0)).list().toString());
        return mUpdateCenterDao.queryBuilder().where(UpdateCenterDao.Properties.Status.eq(0)).list().size();
    }


    /* ```````````````[ Register]`````````````````````````````` */
    public void initWebContentDao() {
        mWebContentDao = App.mDBHelper.mWebContentDao;
    }

    public String getPreHeader(int language) {
        return mWebContentDao.queryBuilder().whereOr(WebContentDao.Properties.TitleTW.eq("pre-reg header"), WebContentDao.Properties.TitleCN.eq("pre-reg header"), WebContentDao.Properties.TitleEN.eq("pre-reg header")).list().get(0).getContent(language);
    }

    public String getPreFooter(int language) {
        return mWebContentDao.queryBuilder().whereOr(WebContentDao.Properties.TitleTW.eq("pre-reg footer"), WebContentDao.Properties.TitleCN.eq("pre-reg footer"), WebContentDao.Properties.TitleEN.eq("pre-reg footer")).list().get(0).getContent(language);
    }


}
