package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.TechAdapter;
import com.adsale.ChinaPlas.dao.SeminarInfo;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.OtherRepository;
import com.adsale.ChinaPlas.data.model.adAdvertisementObj;
import com.adsale.ChinaPlas.helper.ADHelper;
import com.adsale.ChinaPlas.ui.WebContentActivity;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import cn.sharesdk.framework.authorize.ResizeLayout;


/**
 * Created by Carrie on 2017/9/19.
 */

public class TechViewModel {
    private static final String TAG = "TechViewModel";
    public static final String DATE1 = "4.24";
    public static final String DATE2 = "4.25";
    public static final String DATE3 = "4.26";
    public static final String DATE4 = "4.27";
    private Context mContext;
    public final ObservableInt mClickPos = new ObservableInt(0);
    /**
     * 0:全部；1：am；2：pm
     */
    public final ObservableBoolean isAm = new ObservableBoolean();
    private TechAdapter adapter;
    private OtherRepository mRepository;
    private int currLang;
    private OnIntentListener mListener;

    public ArrayList<SeminarInfo> mSeminars = new ArrayList<>();
    private ArrayList<SeminarInfo> allSeminarCaches = new ArrayList<>();
    private adAdvertisementObj adObj;
    private ADHelper adHelper;
    private SeminarInfo seminarInfo;
    private ImageView ivM6;
    private int mIndex;
    public ArrayList<Integer> headerItemPositions = new ArrayList<>();

    public TechViewModel(Context mContext, ImageView iv) {
        this.mContext = mContext;
        this.ivM6 = iv;
        init();
    }

    private void init() {
        currLang = AppUtil.getCurLanguage();
        mRepository = OtherRepository.getInstance();
        mRepository.initTechSeminarDao();
        adHelper = new ADHelper(mContext);
        mSeminars = mRepository.getAllSeminars(getCurrLangId(), adHelper);
        allSeminarCaches.addAll(mSeminars);

        insertHeaderItems();
        showM6V2(0);
    }

    private void insertHeaderItems() {
        int size = allSeminarCaches.size();
        SeminarInfo entity;
        int index = 0;
        for (int i = 0; i < size; i++) {
            entity = allSeminarCaches.get(i);
            if (i == 0) { // header
                entity.isTypeLabel = true;
            } else if (entity.getDate().equals(allSeminarCaches.get(i - 1).getDate())) {
                if (entity.getTime().equals(allSeminarCaches.get(i - 1).getTime())) {
                    entity.isTypeLabel = false;
                } else {
                    entity.isTypeLabel = true;
                }
            } else {
                entity.isTypeLabel = true;
            }

            if (entity.isTypeLabel) {
                index = convertDateTimeToIndex(entity.getDate(), entity.getTime());
                entity.headerStr = String.format(compareTime(entity.getTime()) ? mContext.getString(R.string.seminar_time_pm)
                        : mContext.getString(R.string.seminar_time_am), indexToDate(index));
                LogUtil.i(TAG, "isTypeLabel:index=" + index + ",headerStr=" + entity.headerStr);
                headerItemPositions.add(i);
            }
        }


    }

    public void onStart(OnIntentListener listener, TechAdapter adapter) {
        this.adapter = adapter;
        mListener = listener;
    }

    private void getAllSeminars() {
        mSeminars.clear();
        mSeminars.addAll(allSeminarCaches);
    }

    public ArrayList<SeminarInfo> getList() {
        getAllSeminars();
        return mSeminars;
    }

    private int getCurrLangId() {
        return currLang == 0 ? 950 : currLang == 1 ? 1252 : 936;
    }

    private String getCurrDate() {
        return mClickPos.get() == 1 || mClickPos.get() == 0 ? DATE1 : mClickPos.get() == 3 || mClickPos.get() == 2 ? DATE2 :
                mClickPos.get() == 5 || mClickPos.get() == 4 ? DATE3 : mClickPos.get() == 6 ? DATE4 : DATE1;
    }

    public int convertDateTimeToIndex(String Date, String Time) {
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
     * 左边按钮点击
     *
     * @param index [-1,7]
     *              -1: all
     *              0-6: 按上午下午依次排列
     *              7：跳转平面图
     * @param am
     */
    public void onDateClick(int index, boolean am) {
        if (index == 7) {//平面图
            mListener.onIntent(null, WebContentActivity.class);
        } else if (index == -1) {
            mIndex = 0;
            mClickPos.set(index);
            showM6V2(0);
            isAm.set(am);
            getAllSeminars();
            adapter.setList(mSeminars);
        } else {
            mIndex = index;  // 因为Index=0为ALL，而M6[]从0开始，因此需要-1
            getPartList(index, am);
            adapter.setList(mSeminars);
        }
    }

    public void getPartList(int index, boolean am) {
        LogUtil.i(TAG, "onDateClick::index=" + index);
        showM6V2(index);
        isAm.set(am);
        mClickPos.set(index);
        getPartSeminars();
        mIndex = index;
    }

    private void getPartSeminars() {
        int size = allSeminarCaches.size();
        mSeminars.clear();
        for (int i = 0; i < size; i++) {
            seminarInfo = allSeminarCaches.get(i);
            if (seminarInfo.getDate().contains(getCurrDate()) && seminarInfo.getLangID().equals(getCurrLangId()) && compareTime(seminarInfo.getTime())) {
                LogUtil.i(TAG, mClickPos.get() + ",getCurrDate()=" + getCurrDate());
                mSeminars.add(seminarInfo);
            }
        }
    }

    private boolean compareTime(String time) {
        if (isAm.get()) {
            return time.compareTo("12:00") < 0;
        }
        return time.compareTo("12:00") > 0;
    }

    private String indexToDate(int index) {
        LogUtil.i(TAG, "indexToDate:" + index);
        if (index == 0 || index == 1) {
            return DATE1;
        } else if (index == 2 || index == 3) {
            return DATE2;
        } else if (index == 4 || index == 5) {
            return DATE3;
        } else if (index == 6 || index == 7) {
            return DATE4;
        }
        return DATE1;
    }

    /**
     * @param adIndex
     */
    public void showM6V2(int adIndex) {
        mIndex = adIndex;
        LogUtil.i(TAG, "showM6:index=" + adIndex);
        if (adHelper.isAdOpen() && adHelper.isM6Open(adIndex)) {
            LogUtil.i(TAG, "showM6:VISIBLE");
            ivM6.setVisibility(View.VISIBLE);
            adHelper.getM6HeaderUrl(adIndex);

            RequestOptions options = new RequestOptions().override(AppUtil.getScreenWidth(), AppUtil.getCalculatedHeight(Constant.M3_WIDTH, Constant.M3_HEIGHT));
            Glide.with(mContext).load(adHelper.getM6HeaderUrl(adIndex)).apply(options).into(ivM6);
            adObj = adHelper.getAdObj();
            LogUtil.i(TAG, "trackLog:" + "M6" + "_Date" + (adHelper.getM6TimeSession(adIndex)));
            AppUtil.trackViewLog(206, "Ad", "M6" + "_Date" + (adHelper.getM6TimeSession(adIndex)), adObj.M6_V2.companyID.get(adIndex));// 统计广告出现次数
            AppUtil.setStatEvent(mContext, "ViewM6", "M6" + "_Date" + (adHelper.getM6TimeSession(adIndex)) + adObj.M6_V2.companyID.get(adIndex));
        } else {
            ivM6.setVisibility(View.GONE);
            LogUtil.i(TAG, "showM6:GONE");
        }


    }

    /**
     * 处理后（-1，从0开始）的mIndex
     */
    public void onM6Click() {
        String eventId = adObj.M6_V2.EventID.getEventId(App.mLanguage.get())[mIndex];
        LogUtil.i(TAG, "onM6Click:mIndex=" + mIndex + ",eventId=" + eventId);
        if (TextUtils.isEmpty(eventId)) {
            return;
        }
        int size = mSeminars.size();
        for (int i = 0; i < size; i++) {
            if (eventId.equals(String.valueOf(mSeminars.get(i).getEventID()))) {
                mListener.onIntent(mSeminars.get(i).getEventID() + "", null);
                break;
            }
        }
    }


}
