package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.adapter.TextAdapter;
import com.adsale.ChinaPlas.dao.ApplicationIndustry;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.dao.Industry;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.data.FilterRepository;
import com.adsale.ChinaPlas.ui.view.ExhiDtlInfoView;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;

import static com.adsale.ChinaPlas.R.id.tv_product;


/**
 * Created by Carrie on 2017/11/15.
 * 展商详情
 */

public class ExhibitorDtlViewModel {
    private final String TAG = "ExhibitorDtlViewModel";
    public final ObservableField<Exhibitor> exhibitorObs = new ObservableField<>();
    public final ObservableBoolean isAder = new ObservableBoolean(); // 是否广告展商

    private Context mContext;
    private FrameLayout mFrameLayout;
    private ExhiDtlInfoView mInfoView;
    private Exhibitor exhibitor;
    private ExhibitorRepository mRepository;
    private TextAdapter mTextAdapter;
    private ArrayList<String> mProductList;
    private ArrayList<String> mAppList;
    private ArrayList<Industry> industries;
    private ArrayList<ApplicationIndustry> appIndustries;
    private FilterRepository mFilterRepository;

    public ExhibitorDtlViewModel(Context mContext, FrameLayout frameLayout) {
        this.mContext = mContext;
        this.mFrameLayout = frameLayout;


    }

    public void start(String companyID) {
        ExhibitorRepository mRepository = ExhibitorRepository.getInstance();
        exhibitor = mRepository.getExhibitor(companyID);
        showInfo();
        mFilterRepository = FilterRepository.getInstance();
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
        RecyclerView catalogRV = new RecyclerView(mContext);
        catalogRV.setHasFixedSize(true);
        catalogRV.setLayoutManager(new LinearLayoutManager(mContext));
        if (mTextAdapter == null) {
            LogUtil.e(TAG, "adapter==null");
            mTextAdapter = new TextAdapter(mProductList);
            rvProduct.setAdapter(mTextAdapter);
        }

    }

    private void getProductList() {
        industries = new ArrayList<>();
        mFilterRepository.initIndustryDao();
        industries = mFilterRepository.getIndustries(exhibitor.getCompanyID(), industries);
        LogUtil.i(TAG, "industries=" + industries.size());

        mProductList = new ArrayList<>();
        mProductSize = industries.size();

        LogUtil.i(TAG, "产品列表lists=" + mProductList.toString());
        if (mProductSize > 0) {
            for (int i = 0; i < mProductSize; i++) {
                mProductList.add(industries.get(i).getIndustryName(mCurLanguage));
            }
        } else {
            tv_product.setVisibility(View.GONE);
        }
    }

    /**
     * 应用行业
     */
    public void onAppIndustry() {

    }

    /**
     * 新技术产品
     */
    public void onNewTec() {

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
