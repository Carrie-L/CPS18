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
import com.adsale.ChinaPlas.utils.AppUtil;
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

    /**
     * 广告鸡脚：  select * from SEMINAR_INFO where (LANG_ID=1252 or LANG_ID=950 or LANG_ID=936) and COMPANY_ID=237479 order by DATE,TIME,ORDER_MOB,ROOM_NO
     *
     * @param langId
     * @param adHelper
     * @return
     */
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
            StringBuilder sbUrl = new StringBuilder();
            while (cursor.moveToNext()) {
                entity = mSeminarInfoDao.readEntity(cursor, 0);
                entity.isTypeLabel = false;
                int index = convertDateTimeToIndex(entity.getDate(), entity.getTime());

//                if (!adObj.M6_V2.version[index].equals("0") && String.valueOf(entity.getEventID()).equals(adObj.M6_V2.EventID.getEventId(App.mLanguage.get())[index])) {
                if (!adObj.M6_V2.version[index].equals("0") && adObj.M6_V2.companyID.contains(entity.getCompanyID())) {
                    entity.isADer.set(true);

                    sbUrl.delete(0, sbUrl.length());
                    sbUrl.append(adObj.Common.baseUrl).append(adObj.M6_V2.filepath).append(entity.getCompanyID()).append("/")
                            .append(AppUtil.isTablet() ? adObj.Common.tablet : adObj.Common.phone).append(adObj.M6_V2.logo).append("_").append(adObj.M6_V2.version[index])
                            .append(adObj.M6_V2.format);
                    entity.setAdLogoUrl(sbUrl.toString());
                    LogUtil.i(TAG, "getM6_V2LogoUrl= " + sbUrl.toString());
                    entity.setAdHeaderUrl(adHelper.getM6HeaderUrl(index));

                    //置顶一个广告  ()
                    if (index == 0 || index == 1) {
                        if (seminars1.size() > 0 && seminars1.get(0).isADer.get()) { // 当天置顶位已经有广告了,则不再置顶广告
                            seminars1.add(entity);
                        } else {  // 否则在第0位插入广告
                            seminars1.add(0, entity);
                        }
                    } else if (index == 2 || index == 3) {
                        if (seminars2.size() > 0 && seminars2.get(0).isADer.get()) { // 当天置顶位已经有广告了,则不再置顶广告
                            seminars2.add(entity);
                        } else {  // 否则在第0位插入广告
                            seminars2.add(0, entity);
                        }
                    } else if (index == 4 || index == 5) {
                        if (seminars3.size() > 0 && seminars3.get(0).isADer.get()) { // 当天置顶位已经有广告了,则不再置顶广告
                            seminars3.add(entity);
                        } else {  // 否则在第0位插入广告
                            seminars3.add(0, entity);
                        }
                    } else if (index == 6) {
                        if (seminars4.size() > 0 && seminars4.get(0).isADer.get()) { // 当天置顶位已经有广告了,则不再置顶广告
                            seminars4.add(entity);
                        } else {  // 否则在第0位插入广告
                            seminars4.add(0, entity);
                        }
                    }
                } else {
                    if (index == 0 || index == 1) {
                        seminars1.add(entity);
                    } else if (index == 2 || index == 3) {
                        seminars2.add(entity);
                    } else if (index == 4 || index == 5) {
                        seminars3.add(entity);
                    } else if (index == 6 || index == 7) {
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

    private int convertDateTimeToIndex(String Date, String Time) {
        if (Date.contains("24") && Time.compareTo("12:00") < 0) {
            return 0;  // 24 am
        } else if (Date.contains("24") && Time.compareTo("12:00") > 0) {
            return 1;  // 24 pm
        } else if (Date.contains("25") && Time.compareTo("12:00") < 0) {
            return 2;  // 25 am
        } else if (Date.contains("25") && Time.compareTo("12:00") > 0) {
            return 3;
        } else if (Date.contains("26") && Time.compareTo("12:00") < 0) {
            return 4;  // 26 am
        } else if (Date.contains("26") && Time.compareTo("12:00") > 0) {
            return 5;
        } else if (Date.contains("27") && Time.compareTo("12:00") < 0) {
            return 6;  // 27 am
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

    public SeminarInfo getItemSeminarInfo(String eventId) {
        checkSeminarInfoDao();
        List<SeminarInfo> list = mSeminarInfoDao.queryBuilder().where(SeminarInfoDao.Properties.EventID.eq(eventId)
                ,SeminarInfoDao.Properties.LangID.eq(App.mLanguage.get()==0?950:App.mLanguage.get()==1?1252:936)).list();
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
