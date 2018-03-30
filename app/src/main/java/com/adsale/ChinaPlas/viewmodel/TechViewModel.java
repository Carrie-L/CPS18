package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
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
import com.adsale.ChinaPlas.ui.WebViewActivity;
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
    private boolean isCalM6;
    private int mIndex;
    private ArrayList<SeminarInfo> adList;

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
        adList = new ArrayList<>();
       mSeminars= mRepository.getAllSeminars(getCurrLangId(), adHelper);
        allSeminarCaches.addAll(mSeminars);

        insertHeaderItems();
        showM6(0);
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
                index = convertDateToIndex(entity.getDate());
                entity.headerStr = String.format(compareTime(entity.getTime()) ?
                                mContext.getString(R.string.seminar_time_pm) : mContext.getString(R.string.seminar_time_am),
                        indexToDate(index)); //index==0?"24":index==1?"25":index==2?"26":"27"

//                if(adList.get(index).isADer.get() && adList.get(index).getTime().equals(entity.getTime())){ // entity 是header，且这个时间段里有ad,则将那个ad seminar 置顶
//                    adCount++;
//                    allSeminarCaches.add(i + adCount, adList.get(index));
//                }
            }
        }


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
        mIndex = index;
        if (index == 5) {//平面图
            mListener.onIntent(null, WebContentActivity.class);
        } else {
            getPartList(index, am);
            adapter.setList(mSeminars);
        }
    }

    public void getPartList(int index, boolean am) {
        mIndex = index;
        LogUtil.i(TAG, "onDateClick::index=" + index);
        showM6(index);
        mClickPos.set(index);
        isAm.set(am);
        if (index == 0) {
            getAllSeminars();
        } else {
            getPartSeminars();
//            mSeminars.clear();
//            if (index == 1) {
//                mSeminars.addAll(mSeminars1);
//            } else if (index == 2) {
//                mSeminars.addAll(mSeminars2);
//            } else if (index == 3) {
//                mSeminars.addAll(mSeminars3);
//            } else if (index == 4) {
//                mSeminars.addAll(mSeminars4);
//            }
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

    private String indexToDate(int index) {
        if (index == 0) {
            return DATE1;
        } else if (index == 1) {
            return DATE2;
        } else if (index == 2) {
            return DATE3;
        } else if (index == 3) {
            return DATE4;
        }
        return DATE1;

    }

    public void showM6(int index) {
        if (index > 0) {  // 因为在侧边Index中，0表示all，1-4 表示4天，5表示Floor Plan，因此用在ad[]中需要-1.
            index = index - 1;
        }
        mIndex = index;
        LogUtil.i(TAG, "showM6:index=" + index);
        if (adHelper.isAdOpen() && adHelper.isM6Open(index)) {
            LogUtil.i(TAG, "showM6:VISIBLE");
            ivM6.setVisibility(View.VISIBLE);
            adHelper.getM6HeaderUrl(index);

            RequestOptions options = new RequestOptions().override(AppUtil.getScreenWidth(), AppUtil.getCalculatedHeight(Constant.M6_BANNER_WIDTH, Constant.M6_BANNER_HEIGHT));
            Glide.with(mContext).load(adHelper.getM6HeaderUrl(index)).apply(options).into(ivM6);
            adObj = adHelper.getAdObj();
            AppUtil.trackViewLog(206, "Ad", "M6" + "_Date" + (16 + index), adObj.M6.companyID[index]);// 统计广告出现次数
            AppUtil.setStatEvent(mContext, "ViewM6", "M6" + "_Date" + (16 + index) + adObj.M6.companyID[index]);
        } else {
            ivM6.setVisibility(View.GONE);
            LogUtil.i(TAG, "showM6:GONE");
        }
    }

    public void onM6Click() {
        LogUtil.i(TAG, "onM6Click:mIndex=" + mIndex + ",id=" + adObj.M6.topics.get(mIndex).id);
        mListener.onIntent(adObj.M6.topics.get(mIndex).id, null);
    }

    private void calculateM6Size() {
        if (!isCalM6) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(AppUtil.getScreenWidth(),
                    AppUtil.getCalculatedHeight(Constant.M6_BANNER_WIDTH, Constant.M6_BANNER_HEIGHT));
            params.addRule(RelativeLayout.ALIGN_BOTTOM);
            ivM6.setLayoutParams(params);
            isCalM6 = true;
        }
    }


}
