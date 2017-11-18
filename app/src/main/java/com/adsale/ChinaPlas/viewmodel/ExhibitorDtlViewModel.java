package com.adsale.ChinaPlas.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;

import com.adsale.ChinaPlas.adapter.NewTecAdapter;
import com.adsale.ChinaPlas.adapter.ScheduleAdapter;
import com.adsale.ChinaPlas.adapter.TextAdapter2;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.dao.NewProductInfo;
import com.adsale.ChinaPlas.dao.ScheduleInfo;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.data.FilterRepository;
import com.adsale.ChinaPlas.data.NewTecRepository;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.ScheduleRepository;
import com.adsale.ChinaPlas.data.model.Text2;
import com.adsale.ChinaPlas.databinding.LayoutExhibitorAddScheduleBinding;
import com.adsale.ChinaPlas.databinding.ViewNoteBinding;
import com.adsale.ChinaPlas.ui.ScheduleEditActivity;
import com.adsale.ChinaPlas.ui.view.ExhiDtlInfoView;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;

import java.util.ArrayList;

import static com.adsale.ChinaPlas.utils.Constant.SCHEDULE_DAY01;


/**
 * Created by Carrie on 2017/11/15.
 * 展商详情
 */

public class ExhibitorDtlViewModel {
    private final String TAG = "ExhibitorDtlViewModel";
    public final ObservableField<String> companyName = new ObservableField<>("");
    public final ObservableBoolean isAder = new ObservableBoolean(); // 是否广告展商
    public final ObservableBoolean isCollected = new ObservableBoolean(); // 是否收藏
    public final ObservableBoolean isInfoView = new ObservableBoolean(true); // 显示 InfoView, 控制底部button背景的
    public final ObservableBoolean isNoteView = new ObservableBoolean(); // 显示 NoteView
    public final ObservableBoolean isScheduleView = new ObservableBoolean(); // 显示 ScheduleView
    public final ObservableBoolean isShareView = new ObservableBoolean(); // 显示 ShareView
    public final ObservableBoolean isIndustryEmpty = new ObservableBoolean(); // 产品列表是否为空，空则不显示“产品”Tab
    public final ObservableBoolean isAppIndEmpty = new ObservableBoolean(); // 应用行业列表是否为空，空则不显示“产品”Tab
    public final ObservableBoolean isNewTecEmpty = new ObservableBoolean(); // 新技术产品列表是否为空，空则不显示“产品”Tab

    private Context mContext;
    private OnIntentListener mListener;
    private FrameLayout mInfoFrameLayout; // company info
    private FrameLayout mContentFrameLayout; // other button view
    private LayoutInflater inflater;
    private ExhiDtlInfoView mInfoView;
    private Exhibitor exhibitor;
    private ExhibitorRepository mRepository;
    private TextAdapter2 mIndustryAdapter;
    private TextAdapter2 mAppIndAdapter;
    private ArrayList<Text2> industries;
    private ArrayList<Text2> appIndustries = new ArrayList<>();
    private ArrayList<NewProductInfo> newProductInfos = new ArrayList<>();
    private FilterRepository mFilterRepository;
    private RecyclerView mCatalogRV; // 产品
    private RecyclerView mAppIndustryRV; // 应用行业
    private RecyclerView mNewTecRV; // 新技术产品
    private NewTecAdapter mTecAdapter;
    private View mScheduleView;
    private RecyclerView mScheduleRV;
    private ArrayList<ScheduleInfo> mScheduleInfos = new ArrayList<>();
    private ScheduleAdapter mScheduleAdapter;
    private ViewStub mViewStub;
    private View mNoteView;

    public ExhibitorDtlViewModel(Context mContext, FrameLayout frameLayout) {
        this.mContext = mContext;
        this.mInfoFrameLayout = frameLayout;
        inflater = LayoutInflater.from(mContext);

    }

    public void start(String companyID, OnIntentListener listener, ViewStub viewStub) {
        mListener = listener;
        mViewStub = viewStub;
        mRepository = ExhibitorRepository.getInstance();
        exhibitor = mRepository.getExhibitor(companyID);
        companyName.set(exhibitor.getCompanyName());
        isCollected.set(exhibitor.getIsFavourite() == 1);
        showInfo();
        mFilterRepository = FilterRepository.getInstance();
        getProductList();
        getAppIndList();
        getNewTecList();
    }

    private void showInfo() {
        mViewStub.setVisibility(View.GONE);
        mInfoFrameLayout.removeAllViews();
        if (mInfoView == null) {
            LogUtil.e(TAG, "mInfoView==null, so init it.");
            mInfoView = new ExhiDtlInfoView(mContext);
            mInfoView.setData(exhibitor);
        }
        mInfoFrameLayout.addView(mInfoView);
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
        setIsView(0);
        showInfo();
    }

    /**
     * 产品
     */
    public void onCatalog() {
        if (mCatalogRV == null) {
            mCatalogRV = new RecyclerView(mContext);
            setRecyclerView(mCatalogRV);
            mIndustryAdapter = new TextAdapter2(industries, mListener);
            mCatalogRV.setAdapter(mIndustryAdapter);
        }
        mInfoFrameLayout.removeAllViews();
        mInfoFrameLayout.addView(mCatalogRV);
    }

    /**
     * 应用行业
     */
    public void onAppIndustry() {
        if (mAppIndustryRV == null) {
            mAppIndustryRV = new RecyclerView(mContext);
            setRecyclerView(mAppIndustryRV);
            mAppIndAdapter = new TextAdapter2(appIndustries, mListener);
            mAppIndustryRV.setAdapter(mAppIndAdapter);
        }
        mInfoFrameLayout.removeAllViews();
        mInfoFrameLayout.addView(mAppIndustryRV);
    }

    /**
     * 新技术产品
     */
    public void onNewTec() {
        if (mNewTecRV == null) {
            mNewTecRV = new RecyclerView(mContext);
            setRecyclerView(mNewTecRV);
            mTecAdapter = new NewTecAdapter(mContext, newProductInfos, "https://www.chinaplasonline.com/CPS17/Files/FirstLaunchProduct/", mListener);
            mNewTecRV.setAdapter(mTecAdapter);
        }
        mInfoFrameLayout.removeAllViews();
        mInfoFrameLayout.addView(mNewTecRV);
    }

    /**
     * 收藏/取消 到我的参展商
     */
    public void onCollect() {
        isCollected.set(!isCollected.get());
        exhibitor.setIsFavourite(isCollected.get() ? 1 : 0);
        mRepository.updateItemData(exhibitor);
    }

    /*   ↓↓↓ ------------------------ Note --------------------------- ↓↓↓           */
    public final ObservableInt mRate = new ObservableInt(1);

    /**
     * 笔记、评分、拍照
     */
    public void onNote() {
        setIsView(2);
        initContentFrame();
        mContentFrameLayout.removeAllViews();
        if (mNoteView == null) {
            ViewNoteBinding noteBinding = ViewNoteBinding.inflate(inflater);
            mNoteView = noteBinding.getRoot();
            noteBinding.setModel(this);
            noteBinding.executePendingBindings();
//            mScheduleRV = scheduleBinding.rvSchedule;
//            setRecyclerView(mScheduleRV);
//            getExhibitorSchedules();
//            mScheduleAdapter = new ScheduleAdapter(mScheduleInfos, mContext);
//            mScheduleRV.setAdapter(mScheduleAdapter);
        }
        mContentFrameLayout.addView(mNoteView);

    }

    public void onRate(int rate) {
        mRate.set(rate);
    }

    public void onTakePhoto() {

    }

    /*   ↓↓↓ ------------------------ Schedule --------------------------- ↓↓↓           */
    public final ObservableBoolean isScheduleNoData = new ObservableBoolean();

    public void onSchedule() {
        setIsView(3);
        initContentFrame();
        mContentFrameLayout.removeAllViews();
        if (mScheduleView == null) {
            LayoutExhibitorAddScheduleBinding scheduleBinding = LayoutExhibitorAddScheduleBinding.inflate(inflater);
            mScheduleView = scheduleBinding.getRoot();
            scheduleBinding.setModel(this);
            scheduleBinding.executePendingBindings();
            mScheduleRV = scheduleBinding.rvSchedule;
            setRecyclerView(mScheduleRV);
            getExhibitorSchedules();
            mScheduleAdapter = new ScheduleAdapter(mScheduleInfos, mContext);
            mScheduleRV.setAdapter(mScheduleAdapter);
        }
        mContentFrameLayout.addView(mScheduleView);
    }

    public void updateSchedule() {
        setIsView(3);
        getExhibitorSchedules();
        mScheduleAdapter.setList(mScheduleInfos);
    }

    private void getExhibitorSchedules() {
        mScheduleInfos.clear();
        ScheduleRepository scheduleRepository = ScheduleRepository.getInstance();
        mScheduleInfos = scheduleRepository.getExhibitorSchedules(exhibitor.getCompanyID());
        isScheduleNoData.set(mScheduleInfos.isEmpty());
    }

    public void onAddSchedule() {
        Intent intent = new Intent(mContext, ScheduleEditActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constant.INTENT_EXHIBITOR, exhibitor);
        intent.putExtra("date", SCHEDULE_DAY01);
        mContext.startActivity(intent);
    }
/*   [ ------------------------ Schedule End  --------------------------- ]           */


    public void onShare() {
        // 取消了 share sdk
    }

    public void onBackToInfo() {
        setIsView(0);
        showInfo();
    }

    private void initContentFrame() {
        if (mContentFrameLayout == null) {
            mContentFrameLayout = (FrameLayout) mViewStub.inflate();
        }
        mViewStub.setVisibility(View.VISIBLE);
    }

    /**
     * 当前显示的是哪个View，对应的按钮背景改变
     * collect 没有 View，因此不算在内
     *
     * @param currIndex 底部bar 5个按钮，（0..4,1） 0-4,除了1
     */
    private void setIsView(int currIndex) {
        isInfoView.set(currIndex == 0);
        isNoteView.set(currIndex == 2);
        isScheduleView.set(currIndex == 3);
        isShareView.set(currIndex == 4);
    }

    private void setRecyclerView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
    }

}
