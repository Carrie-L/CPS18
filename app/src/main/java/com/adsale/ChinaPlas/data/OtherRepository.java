package com.adsale.ChinaPlas.data;

import android.content.ContentValues;
import android.database.Cursor;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.dao.Application;
import com.adsale.ChinaPlas.dao.ConcurrentEvent;
import com.adsale.ChinaPlas.dao.ConcurrentEventDao;
import com.adsale.ChinaPlas.dao.EventApplication;
import com.adsale.ChinaPlas.dao.EventApplicationDao;
import com.adsale.ChinaPlas.dao.MainIconDao;
import com.adsale.ChinaPlas.dao.News;
import com.adsale.ChinaPlas.dao.NewsDao;
import com.adsale.ChinaPlas.dao.NewsLink;
import com.adsale.ChinaPlas.dao.NewsLinkDao;
import com.adsale.ChinaPlas.dao.SeminarInfo;
import com.adsale.ChinaPlas.dao.SeminarInfoDao;
import com.adsale.ChinaPlas.dao.SeminarSpeaker;
import com.adsale.ChinaPlas.dao.SeminarSpeakerDao;
import com.adsale.ChinaPlas.dao.UpdateCenter;
import com.adsale.ChinaPlas.dao.UpdateCenterDao;
import com.adsale.ChinaPlas.dao.WebContent;
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
    private NewsDao mNewsDao;
    private NewsLinkDao mNewsLinkDao;
    private ConcurrentEventDao mEventDao;
    private EventApplicationDao mEventAppDao;

    public static OtherRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OtherRepository();
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
    public ArrayList<SeminarSpeaker> getSeminarSpeakerItem(int eventID, int langID) {
        checkSeminarSpeakerDao();
        return (ArrayList<SeminarSpeaker>) mSeminarSpeakerDao.queryBuilder().where(SeminarSpeakerDao.Properties.EventID.eq(eventID), SeminarSpeakerDao.Properties.LangID.eq(langID)).list();
    }

    public SeminarInfo getItemSeminarInfo(String eventId) {
        checkSeminarInfoDao();
        List<SeminarInfo> list = mSeminarInfoDao.queryBuilder().where(SeminarInfoDao.Properties.EventID.eq(eventId)
                , SeminarInfoDao.Properties.LangID.eq(App.mLanguage.get() == 0 ? 950 : App.mLanguage.get() == 1 ? 1252 : 936)).list();
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

    private void checkWebContentDao() {
        if (mWebContentDao == null) {
            initWebContentDao();
        }
    }

    private void checkNewsDao() {
        if (mNewsDao == null) {
            initNewsDao();
        }
    }

    private void checkNewsLinkDao() {
        if (mNewsLinkDao == null) {
            initNewsLinkDao();
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

    /* ```````````````[ WebContnet]`````````````````````````````` */
    public String geWebContentLUT() {
        checkWebContentDao();
        List<WebContent> list = mWebContentDao.queryBuilder().orderDesc(WebContentDao.Properties.UpdatedAt).limit(1).list();
        if (list != null && list.size() > 0) {
            return list.get(0).getUpdatedAt();
        }
        return "";
    }

    public void updateOrInsertWebContents(List<WebContent> entities) {
        checkWebContentDao();
        mWebContentDao.insertOrReplaceInTx(entities);
    }

    public void updateOrInsertWebContent(WebContent entity) {
        checkWebContentDao();
        mWebContentDao.insertOrReplace(entity);
    }


    /* ```````````````[ News]`````````````````````````````` */
    private void initNewsDao() {
        mNewsDao = App.mDBHelper.mNewsDao;
    }

    private void initNewsLinkDao() {
        mNewsLinkDao = App.mDBHelper.mLinkDao;
    }

    public String getNewsLUT() {
        checkNewsDao();
        List<News> list = mNewsDao.queryBuilder().orderDesc(NewsDao.Properties.UpdatedAt).limit(1).list();
        if (list != null && list.size() > 0) {
            return list.get(0).getUpdateDateTime();
        }
        return "";
    }

    public String getNewsLinkLUT() {
        checkNewsLinkDao();
        List<NewsLink> list = mNewsLinkDao.queryBuilder().orderDesc(NewsLinkDao.Properties.UpdatedAt).limit(1).list();
        if (list != null && list.size() > 0) {
            return list.get(0).getUpdateDateTime();
        }
        return "";
    }

    public void updateOrInsertNews(List<News> entities) {
        checkNewsDao();
        mNewsDao.insertOrReplaceInTx(entities);
    }

    /* ```````````````[ ConcurrentEvent]`````````````````````````````` */

    private void checkEventDao() {
        if (mEventDao == null) {
            initEventDao();
        }
    }

    private void initEventDao() {
        mEventDao = App.mDBHelper.mEventDao;
    }

    public String getEventLUT() {
        checkEventDao();
        List<ConcurrentEvent> list = mEventDao.queryBuilder().orderDesc(ConcurrentEventDao.Properties.UpdatedAt).limit(1).list();
        if (list != null && list.size() > 0) {
            return list.get(0).getUpdatedAt();
        }
        return "";
    }

    public void updateOrInsertEvents(List<ConcurrentEvent> entities) {
        checkEventDao();
        mEventDao.insertOrReplaceInTx(entities);
    }

    public ArrayList<ConcurrentEvent> getEvents() {
        checkEventDao();
        ArrayList<ConcurrentEvent> list = new ArrayList<ConcurrentEvent>();
        list = (ArrayList<ConcurrentEvent>) mEventDao.queryBuilder().where(ConcurrentEventDao.Properties.IsDelete.eq(0)).orderDesc(ConcurrentEventDao.Properties.seq).list();
        return list;
    }

    /**
     * 不重复的zip包
     * @return
     */
    public ArrayList<ConcurrentEvent> getEventsNonRepeatZip() {
        checkEventDao();
        ConcurrentEvent entity;
        ArrayList<ConcurrentEvent> list = new ArrayList<ConcurrentEvent>();
        Cursor cursor = App.mDBHelper.db.rawQuery("select * from ConcurrentEvent group by " + ConcurrentEventDao.Properties.EventID.columnName, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                entity = mEventDao.readEntity(cursor, 0);
                list.add(entity);
            }
            cursor.close();
        }
        return list;
    }

    public void updateEventIsDown(String pageID, boolean needDown) {
        ContentValues cv = new ContentValues();
        cv.put("needDown", needDown);
        App.mDBHelper.db.update(ConcurrentEventDao.TABLENAME, cv, "PageID = ?", new String[]{pageID});
    }

    public ConcurrentEvent getItemEvent(String eventId) {
        checkEventDao();
        return mEventDao.load(eventId);
    }

     /* ```````````````[ EventApplication ]`````````````````````````````` */

    private void checkEventAppDao() {
        if (mEventAppDao == null) {
            initEventAppDao();
        }
    }

    private void initEventAppDao() {
        mEventAppDao = App.mDBHelper.mEventAppDao;
    }

    public String getEventAppLUT() {
        checkEventAppDao();
        List<EventApplication> list = mEventAppDao.queryBuilder().orderDesc(EventApplicationDao.Properties.UpdatedAt).limit(1).list();
        if (list != null && list.size() > 0) {
            return list.get(0).getUpdatedAt();
        }
        return "";
    }

    public void updateOrInsertEventApplications(List<EventApplication> entities) {
        checkEventAppDao();
        mEventAppDao.insertOrReplaceInTx(entities);
    }

    public ArrayList<Application> getEventApplications() {
        checkEventAppDao();
        ArrayList<EventApplication> list = (ArrayList<EventApplication>) mEventAppDao.queryBuilder().whereOr(EventApplicationDao.Properties.IsDelete.isNull(), EventApplicationDao.Properties.IsDelete.notEq(1)).list();
        ArrayList<EventApplication> list3 = (ArrayList<EventApplication>) mEventAppDao.loadAll();
        LogUtil.i(TAG, "getEventApplications: list=" + list.size());
        LogUtil.i(TAG, "getEventApplications: list3=" + list3.size());
        ArrayList<Application> applications = new ArrayList<>();
        Application application;
        EventApplication entity;
        int size = list.size();
        for (int i = 0; i < size; i++) {
            entity = list.get(i);
            application = new Application(entity.getIndustryID(), entity.getApplicationEng(), entity.getApplicationTC(), entity.getApplicationSC(), entity.getSortCN(), entity.getSortEN());
            applications.add(application);
        }
        LogUtil.i(TAG, "getEventApplications: applications=" + applications.size());
        return applications;
    }

}
