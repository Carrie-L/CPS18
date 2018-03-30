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
        checkSeminarInfoDao();
        ArrayList<SeminarInfo> list = new ArrayList<>();
        Cursor cursor = App.mDBHelper.db.rawQuery("select * from SEMINAR_INFO where LANG_ID=" + langId + " order by DATE,TIME,ORDER_MOB,ROOM_NO", null);
        SeminarInfo entity;
        adAdvertisementObj adObj = adHelper.getAdObj();

         ArrayList<SeminarInfo> seminars1 = new ArrayList<>();
         ArrayList<SeminarInfo> seminars2 = new ArrayList<>();
         ArrayList<SeminarInfo> seminars3 = new ArrayList<>();
         ArrayList<SeminarInfo> seminars4 = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                entity = mSeminarInfoDao.readEntity(cursor, 0);
                entity.isTypeLabel = false;
                int index = convertDateToIndex(entity.getDate());

                if (!adObj.M6.version[index].equals("0") && String.valueOf(entity.getEventID()).equals(adObj.M6.EventID.getEventId(App.mLanguage.get())[index])) {
                    entity.isADer.set(true);
                    entity.setAdLogoUrl(adHelper.getM6LogoUrl(index));
                    entity.setAdHeaderUrl(adHelper.getM6HeaderUrl(index));

                    if (index == 0) {
                        seminars1.add(0,entity);
                    } else if (index == 1) {
                        seminars2.add(0,entity);
                    } else if (index == 2) {
                        seminars3.add(0,entity);
                    } else if (index == 3) {
                        seminars4.add(0,entity);
                    }

                } else{
                    if (index == 0) {
                        seminars1.add(entity);
                    } else if (index == 1) {
                        seminars2.add(entity);
                    } else if (index == 2) {
                        seminars3.add(entity);
                    } else if (index == 3) {
                        seminars4.add(entity);
                    }
                }




            }
            cursor.close();

//            LogUtil.i(TAG, "seminars1  //// " + seminars1.size() + "," + seminars1.toString());
//            LogUtil.i(TAG, "adList=" + adList.size() + "," + adList.toString());
        }

        list.addAll(seminars1);
        list.addAll(seminars2);
        list.addAll(seminars3);
        list.addAll(seminars4);


        return list;
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
        checkSeminarSpeakerDao();
        return (ArrayList<SeminarSpeaker>) mSeminarSpeakerDao.queryBuilder().where(SeminarSpeakerDao.Properties.CompanyID.eq(companyID), SeminarSpeakerDao.Properties.LangID.eq(langID)).list();
    }

    public SeminarInfo getItemSeminarInfo(String id) {
        checkSeminarInfoDao();
        List<SeminarInfo> list = mSeminarInfoDao.queryBuilder().where(SeminarInfoDao.Properties.ID.eq(id)).list();
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    private void checkSeminarInfoDao() {
        if (mSeminarInfoDao == null) {
            initTechSeminarDao();
        }
    }

    private void checkSeminarSpeakerDao() {
        if (mSeminarSpeakerDao == null) {
            initSeminarSpeakDao();
        }
    }

    public void clearSeminarInfo() {
        checkSeminarInfoDao();
        mSeminarInfoDao.deleteAll();
    }

    public void clearSeminarSpeaker() {
        checkSeminarSpeakerDao();
        mSeminarSpeakerDao.deleteAll();
    }

    public void insertSeminarInfoAll(ArrayList<SeminarInfo> list) {
        checkSeminarInfoDao();
        mSeminarInfoDao.insertInTx(list);
    }

    public void insertSeminarSpeakerAll(ArrayList<SeminarSpeaker> list) {
        checkSeminarSpeakerDao();
        mSeminarSpeakerDao.insertInTx(list);
    }

    /* ```````````````[Update Center]`````````````````````````````` */
    public void initUpdateCenterDao() {
        mUpdateCenterDao = App.mDBHelper.mUpdateCenterDao;
    }

    private void checkUpdateCenterDao() {
        if (mUpdateCenterDao == null) {
            initUpdateCenterDao();
        }
    }

    public ArrayList<UpdateCenter> getUpdateCenters() {
        checkUpdateCenterDao();
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

    /**
     * 同期活动是否有更新
     *
     * @return
     */
    public boolean isEventCanUpdate() {
        checkUpdateCenterDao();
        return !mUpdateCenterDao.queryBuilder().where(UpdateCenterDao.Properties.Id.eq(4), UpdateCenterDao.Properties.Status.eq(0)).list().isEmpty();
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
