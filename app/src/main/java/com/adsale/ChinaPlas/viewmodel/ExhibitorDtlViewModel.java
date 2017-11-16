package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.adapter.NewTecAdapter;
import com.adsale.ChinaPlas.adapter.TextAdapter;
import com.adsale.ChinaPlas.adapter.TextAdapter2;
import com.adsale.ChinaPlas.dao.ApplicationIndustry;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.dao.Industry;
import com.adsale.ChinaPlas.dao.NewProductInfo;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.data.FilterRepository;
import com.adsale.ChinaPlas.data.NewTecRepository;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.model.NewTec;
import com.adsale.ChinaPlas.data.model.Text2;
import com.adsale.ChinaPlas.ui.view.ExhiDtlInfoView;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.FileUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.Parser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.adsale.ChinaPlas.R.id.tv_product;
import static com.adsale.ChinaPlas.utils.Parser.parseJsonFilesDirFile;
import static com.unionpay.mobile.android.global.a.H;


/**
 * Created by Carrie on 2017/11/15.
 * 展商详情
 */

public class ExhibitorDtlViewModel {
    private final String TAG = "ExhibitorDtlViewModel";
    public final ObservableField<Exhibitor> exhibitorObs = new ObservableField<>();
    public final ObservableBoolean isAder = new ObservableBoolean(); // 是否广告展商
    public final ObservableBoolean isIndustryEmpty = new ObservableBoolean(); // 产品列表是否为空，空则不显示“产品”Tab
    public final ObservableBoolean isAppIndEmpty = new ObservableBoolean(); // 应用行业列表是否为空，空则不显示“产品”Tab
    public final ObservableBoolean isNewTecEmpty = new ObservableBoolean(); // 新技术产品列表是否为空，空则不显示“产品”Tab

    private Context mContext;
    private OnIntentListener mListener;
    private FrameLayout mFrameLayout;
    private ExhiDtlInfoView mInfoView;
    private Exhibitor exhibitor;
    private ExhibitorRepository mRepository;
    private TextAdapter2 mIndustryAdapter;
    private TextAdapter2 mAppIndAdapter;
    private ArrayList<String> mProductList;
    private ArrayList<String> mAppList;
    private ArrayList<Text2> industries;
    private ArrayList<Text2> appIndustries = new ArrayList<>();
    private ArrayList<NewProductInfo> newProductInfos = new ArrayList<>();
    private FilterRepository mFilterRepository;
    private RecyclerView mCatalogRV; // 产品
    private RecyclerView mAppIndustryRV; // 应用行业
    private RecyclerView mNewTecRV; // 新技术产品
    private NewTecAdapter mTecAdapter;

    public ExhibitorDtlViewModel(Context mContext, FrameLayout frameLayout) {
        this.mContext = mContext;
        this.mFrameLayout = frameLayout;


    }

    public void start(String companyID, OnIntentListener listener) {
        mListener = listener;
        ExhibitorRepository mRepository = ExhibitorRepository.getInstance();
        exhibitor = mRepository.getExhibitor(companyID);
        showInfo();
        mFilterRepository = FilterRepository.getInstance();
        getProductList();
        getAppIndList();
        getNewTecList();
    }

    private void showInfo() {
        mFrameLayout.removeAllViews();
        if (mInfoView == null) {
            LogUtil.e(TAG, "mInfoView==null, so init it.");
            mInfoView = new ExhiDtlInfoView(mContext);
            mInfoView.setData(exhibitor);
        }
        mFrameLayout.addView(mInfoView);
    }

    private void getProductList() {
        industries = new ArrayList<>();
        mFilterRepository.initIndustryDao();
        industries = mFilterRepository.getIndustries(exhibitor.getCompanyID());
        isIndustryEmpty.set(industries.isEmpty());
    }

    private void getAppIndList() {
        mFilterRepository.initAppIndustryDao();
        appIndustries = mFilterRepository.queryAppIndustryLists(exhibitor.getCompanyID());
        isAppIndEmpty.set(appIndustries.isEmpty());
    }

    private void getNewTecList() {
        NewTecRepository repository = new NewTecRepository();
        repository.initDao();
        newProductInfos = repository.getProductInfoList(exhibitor.getCompanyID());
        isNewTecEmpty.set(newProductInfos.isEmpty());
    }

    /**
     * 关于 ： 广告商才有
     */
    public void onAbout() {

    }

    /**
     * 公司资料
     */
    public void onInfo() {
        showInfo();
    }

    /**
     * 产品
     */
    public void onCatalog() {
        if (mCatalogRV == null) {
            mCatalogRV = new RecyclerView(mContext);
            mCatalogRV.setHasFixedSize(true);
            mCatalogRV.setLayoutManager(new LinearLayoutManager(mContext));
            mCatalogRV.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
            mIndustryAdapter = new TextAdapter2(industries, mListener);
            mCatalogRV.setAdapter(mIndustryAdapter);
        }
        mFrameLayout.removeAllViews();
        mFrameLayout.addView(mCatalogRV);
    }

    /**
     * 应用行业
     */
    public void onAppIndustry() {
        if (mAppIndustryRV == null) {
            mAppIndustryRV = new RecyclerView(mContext);
            mAppIndustryRV.setHasFixedSize(true);
            mAppIndustryRV.setLayoutManager(new LinearLayoutManager(mContext));
            mAppIndustryRV.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
            mAppIndAdapter = new TextAdapter2(appIndustries, mListener);
            mAppIndustryRV.setAdapter(mAppIndAdapter);
        }
        mFrameLayout.removeAllViews();
        mFrameLayout.addView(mAppIndustryRV);
    }

    /**
     * 新技术产品
     */
    public void onNewTec() {
        if (mNewTecRV == null) {
            mNewTecRV = new RecyclerView(mContext);
            mNewTecRV.setHasFixedSize(true);
            mNewTecRV.setLayoutManager(new LinearLayoutManager(mContext));
            mNewTecRV.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
            mTecAdapter = new NewTecAdapter(mContext, newProductInfos, "https://www.chinaplasonline.com/CPS17/Files/FirstLaunchProduct/", mListener);
            mNewTecRV.setAdapter(mTecAdapter);
        }
        mFrameLayout.removeAllViews();
        mFrameLayout.addView(mNewTecRV);
    }

    /**
     * 收藏/取消 到我的参展商
     */
    public void onCollect() {

    }

    /**
     * 笔记、评分、拍照
     */
    public void onNote() {

    }

    public void onSchedule() {

    }

    public void onShare() {

    }


}
