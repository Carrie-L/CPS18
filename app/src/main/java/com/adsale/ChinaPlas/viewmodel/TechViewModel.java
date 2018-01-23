package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.adapter.TechAdapter;
import com.adsale.ChinaPlas.dao.SeminarInfo;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.OtherRepository;
import com.adsale.ChinaPlas.data.model.adAdvertisementObj;
import com.adsale.ChinaPlas.helper.ADHelper;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.bumptech.glide.Glide;

import java.util.ArrayList;


/**
 * Created by Carrie on 2017/9/19.
 */

public class TechViewModel {
    private static final String TAG = "TechViewModel";
    public static final String DATE1 = "5.16";
    public static final String DATE2 = "5.17";
    public static final String DATE3 = "5.18";
    public static final String DATE4 = "5.19";
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
    private boolean isCalM6;

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
        allSeminarCaches = mRepository.getAllSeminars(getCurrLangId(), adHelper);
        showM6(0);
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
        return mClickPos.get() == 1 ? DATE1 : mClickPos.get() == 2 ? DATE2 : mClickPos.get() == 3 ? DATE3 : DATE4;
    }

    public void onDateClick(int index, boolean am) {
        if (index == 5) {//平面图

        } else {
            getPartList(index, am);
            adapter.setList(mSeminars);
            showM6(index);
        }
    }

    public void getPartList(int index, boolean am) {
        LogUtil.i(TAG, "onDateClick::index=" + index);
        mClickPos.set(index);
        isAm.set(am);
        if (index == 0) {
            getAllSeminars();
        } else {
            getPartSeminars();
        }
    }

    private void getPartSeminars() {
        int size = allSeminarCaches.size();
        mSeminars.clear();
        for (int i = 0; i < size; i++) {
            seminarInfo = allSeminarCaches.get(i);
            if (seminarInfo.getDate().contains(getCurrDate()) && seminarInfo.getLangID().equals(getCurrLangId()) && compareTime(seminarInfo.getTime())) {
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

    private void getAdList() {
        if (!adHelper.isAdOpen()) {
            return;
        }
        int size = allSeminarCaches.size();
        int index;

        int adSize = adObj.M6.version.length;
        for (int t = 0; t < adSize; t++) {
            for (int i = 0; i < size; i++) {
                seminarInfo = allSeminarCaches.get(i);
                index = getCurrIndex();
                if (!adObj.M6.version[index].equals("0") && seminarInfo.getCompanyID().equals(adObj.M6.companyID[index])) {
//                    seminarInfo.isADer = true;
//                    seminarInfo.adLogoUrl = adHelper.getM6LogoUrl(index);
//                    seminarInfo.adHeaderUrl = adHelper.getM6HeaderUrl(index);
                }
                allSeminarCaches.set(i, seminarInfo);
            }
        }

        for (int i = 0; i < size; i++) {
            seminarInfo = allSeminarCaches.get(i);
            index = getCurrIndex();
            if (!adObj.M6.version[index].equals("0") && seminarInfo.getCompanyID().equals(adObj.M6.companyID[index])) {
//                seminarInfo.isADer = true;
//                seminarInfo.adLogoUrl = adHelper.getM6LogoUrl(index);
//                seminarInfo.adHeaderUrl = adHelper.getM6HeaderUrl(index);


            }
            allSeminarCaches.set(i, seminarInfo);
        }
    }

    private int getCurrIndex() {
        LogUtil.i(TAG, "seminarInfo.getDate()=" + seminarInfo.getDate());
        if (seminarInfo.getDate().contains(DATE1)) {
            return 0;
        } else if (seminarInfo.getDate().contains(DATE2)) {
            return 1;
        } else if (seminarInfo.getDate().contains(DATE3)) {
            return 2;
        } else if (seminarInfo.getDate().contains(DATE4)) {
            return 3;
        }
        return 0;
    }

    private void showM6(int index) {
        LogUtil.i(TAG, "showM6:index=" + index);
        if (adHelper.isAdOpen() && adHelper.isM6Open(index)) {
            calculateM6Size();
            ivM6.setVisibility(View.VISIBLE);
            adHelper.getM6HeaderUrl(index);
            Glide.with(mContext).load(adHelper.getM6HeaderUrl(index)).into(ivM6);
            adObj = adHelper.getAdObj();
            AppUtil.trackViewLog(206, "Ad", "M6" + "_Date" + (16 + index), adObj.M6.companyID[index]);// 统计广告出现次数
            AppUtil.setStatEvent(mContext, "ViewM6", "M6" + "_Date" + (16 + index) + adObj.M6.companyID[index]);
        } else {
            ivM6.setVisibility(View.GONE);
        }
    }

    private void calculateM6Size() {
        if (!isCalM6) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(AppUtil.getScreenWidth(),
                    AppUtil.getCalculatedHeight(Constant.M6_BANNER_WIDTH_PHONE, Constant.M6_BANNER_HEIGHT_PHONE));
            ivM6.setLayoutParams(params);
            isCalM6 = true;
        }
    }


}
