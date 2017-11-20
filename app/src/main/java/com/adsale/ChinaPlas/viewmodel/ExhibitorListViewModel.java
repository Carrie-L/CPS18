package com.adsale.ChinaPlas.viewmodel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.adsale.ChinaPlas.adapter.ExhibitorAdapter;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.ui.ExhibitorFilterActivity;
import com.adsale.ChinaPlas.ui.view.SideLetter;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.RecyclerViewScrollTo;

import java.util.ArrayList;

/**
 * Created by Carrie on 2017/11/9.
 * 操作：
 * 1. 全部参展商列表：
 * ① 默认，AZ排序
 * ② 点击Hall, Hall排序
 * ③ 点击AZ，AZ排序，同①
 * [使用缓存]
 * 2. 搜索：
 * isSearching = true;
 * ① 当前在AZ，AZ排序；当前在Hall,Hall排序
 * ② 点击Hall
 * ③ 点击AZ
 * ④ 清除搜索框  mAllExhibitorXCaches, mAllSideXCaches
 * [edit时，清空search list.只有当点击AZ或Hall时，使用mSearchExhibitorsX]
 * 3.  筛选：
 * isFiltering = true;
 * 列表使用 mAllExhibitorXCaches，mAllSideXCaches，几乎同①
 */

public class ExhibitorListViewModel {
    private final String TAG = "ExhibitorListViewModel";
    public final ObservableField<String> etFilter = new ObservableField<>();
    public final ObservableField<String> dialogLetter = new ObservableField<>();
    /**
     * 当前是否以拼音字母排序，true是；false hall排序。
     */
    public final ObservableBoolean isSortAZ = new ObservableBoolean(true);
    private ExhibitorRepository mExhibitorRepo;

    /**
     * 从Exhibitor Dtl 的 Industry or App Industry 跳转过来的
     */
    public static final int TYPE_INDUSTRY = 1;
    public static final int TYPE_APP_INDUSTRY = 2;
    private int mType = 0;
    private String mId;

    private RecyclerViewScrollTo mRVScrollTo;
    private ExhibitorAdapter adapter;
    private SideLetter sideLetter;
    private ArrayList<Exhibitor> searchTemps = new ArrayList<>();
    private OnIntentListener mListener;

    /* 展商列表 */
    private ArrayList<Exhibitor> mAllExhibitorAZCaches = new ArrayList<>();
    private ArrayList<Exhibitor> mAllExhibitorHallCaches = new ArrayList<>();
    private ArrayList<Exhibitor> mSearchExhibitorsAZ = new ArrayList<>();
    private ArrayList<Exhibitor> mSearchExhibitorsHall = new ArrayList<>();
    public final ObservableArrayList<Exhibitor> mExhibitors = new ObservableArrayList<>();
    /* Side列表 */
    private ArrayList<String> mAllSideAZCaches = new ArrayList<>();
    private ArrayList<String> mAllSideHallCaches = new ArrayList<>();
    private ArrayList<String> mSearchSideAZs = new ArrayList<>();
    private ArrayList<String> mSearchSideHalls = new ArrayList<>();
    public ArrayList<String> mLetters = new ArrayList<>();

    public boolean isFiltering;
    private boolean isSearching;
    private String mKeyword;

    public ExhibitorListViewModel(ExhibitorRepository repository, OnIntentListener listener) {
        this.mExhibitorRepo = repository;
        this.mListener = listener;
    }

    public void setLayoutManager(RecyclerViewScrollTo scrollTo, SideLetter sideLetter) {
        mRVScrollTo = scrollTo;
        this.sideLetter = sideLetter;
    }

    public void setAdapter(ExhibitorAdapter adapter) {
        this.adapter = adapter;
    }

    public void setPartList(int type, String id) {
        mType = type;
        mId = id;
    }

    public void getAllExhibitorsAZ() {
        mExhibitors.clear();
        mLetters.clear();
        if (mAllExhibitorAZCaches.isEmpty()) {
            if (mType == TYPE_INDUSTRY) {
                mAllExhibitorAZCaches.addAll(mExhibitorRepo.getIndustryExhibitors(mAllSideAZCaches, mId));
            } else if (mType == TYPE_APP_INDUSTRY) {
                mAllExhibitorAZCaches.addAll(mExhibitorRepo.getAppIndustryExhibitors(mAllSideAZCaches, mId));
            } else {
                mAllExhibitorAZCaches.addAll(mExhibitorRepo.getAllExhibitors(mAllSideAZCaches));
            }
        }
        mExhibitors.addAll(mAllExhibitorAZCaches);
        mLetters.addAll(mAllSideAZCaches);
        if (adapter != null) { /* 第一次进入页面时，adapter == null ,不需要刷新 */
            refreshUI();
        }
    }

    private void getAllExhibitorsHall() {
        isSortAZ.set(false);
        mExhibitors.clear();
        mLetters.clear();
        if (mAllExhibitorHallCaches.isEmpty()) {
            mAllExhibitorHallCaches.addAll(mExhibitorRepo.sortByHallList(mAllSideHallCaches));
        }
        mExhibitors.addAll(mAllExhibitorHallCaches);
        mLetters.addAll(mAllSideHallCaches);
        refreshUI();
    }

    private void getSearchExhibitorsAZ() {
        isSearching = true;
        clearList();
        searchTemps.clear();
        searchTemps.addAll(mAllExhibitorAZCaches);
        if (mSearchExhibitorsAZ.isEmpty()) {
            mSearchExhibitorsAZ.addAll(mExhibitorRepo.getExhibitorSearchAZ(searchTemps, mSearchSideAZs, mKeyword));
        }
        mExhibitors.addAll(mSearchExhibitorsAZ);
        mLetters.addAll(mSearchSideAZs);
        LogUtil.i(TAG, "mSideListAZ= " + mSearchSideAZs.size() + "," + mSearchSideAZs.toString());
        refreshUI();
    }

    private void getSearchExhibitorsHall() {
        isSearching = true;
        clearList();
        searchTemps.clear();
        searchTemps.addAll(mAllExhibitorHallCaches);
        if (mSearchExhibitorsAZ.isEmpty()) {
            mSearchExhibitorsHall.addAll(mExhibitorRepo.getExhibitorSearchHalls(searchTemps, mSearchSideHalls, mKeyword));
        }
        mExhibitors.addAll(mSearchExhibitorsHall);
        mLetters.addAll(mSearchSideHalls);
        refreshUI();
    }

    /**
     * 筛选，清空原有的全部列表缓存，将新的筛选结果当成AllExhibitorsCache
     */
    public void getFilterExhibitorsAZ() {
        clearList();
        clearSearchList();
        if (mFilters.isEmpty()) {
            return;
        }
        if (mAllExhibitorAZCaches.isEmpty()) {
            mAllExhibitorAZCaches.addAll(mExhibitorRepo.queryFilterExhibitor(mFilters, mAllSideAZCaches, true));
        }
        mExhibitors.addAll(mAllExhibitorAZCaches);
        mLetters.addAll(mAllSideAZCaches);
        refreshUI();
    }

    private void getFilterExhibitorsHall() {
        clearList();
        clearSearchList();
        if (mFilters.isEmpty()) {
            return;
        }
        if (mAllExhibitorHallCaches.isEmpty()) {
            mAllExhibitorHallCaches.addAll(mExhibitorRepo.queryFilterExhibitor(mFilters, mAllSideHallCaches, false));
        }
        mExhibitors.addAll(mAllExhibitorHallCaches);
        mLetters.addAll(mAllSideHallCaches);
        refreshUI();
    }

    private ArrayList<ExhibitorFilter> mFilters;

    public void setFilters(ArrayList<ExhibitorFilter> filters) {
        mFilters = filters;
    }

    public void onSortAZ() {
        isSortAZ.set(true);
        if (isSearching) {
            getSearchExhibitorsAZ();
        } else if (isFiltering) {
            getFilterExhibitorsAZ();
        } else {
            getAllExhibitorsAZ();
        }
    }

    public void onSortHall() {
        isSortAZ.set(false);
        if (isSearching) {
            getSearchExhibitorsHall();
        } else if (isFiltering) {
            getFilterExhibitorsHall();
        } else {
            getAllExhibitorsHall();
        }
    }

    public void search(String keyword) {
        mKeyword = keyword;
        clearSearchList();
        if (isSortAZ.get()) {
            getSearchExhibitorsAZ();
        } else {
            getSearchExhibitorsHall();
        }
    }

    public void resetList() {
        LogUtil.e(TAG, "resetList");
        isSearching = false;
        clearList();
        clearSearchList();
        if (isSortAZ.get()) {
            mExhibitors.addAll(mAllExhibitorAZCaches);
            mLetters.addAll(mAllSideAZCaches);
        } else {
            mExhibitors.addAll(mAllExhibitorHallCaches);
            mLetters.addAll(mAllSideHallCaches);
        }
        refreshUI();
    }

    public void onImgFilter() {
        mListener.onIntent(null, ExhibitorFilterActivity.class);
    }

    private void refreshUI() {
        adapter.setList(mExhibitors);
        refreshSideLetter();
    }

    private void refreshSideLetter() {
        sideLetter.setList(mLetters);
        sideLetter.refresh();
    }

    public void clearCache() {
        mAllExhibitorHallCaches.clear();
        mAllExhibitorAZCaches.clear();
        mAllSideAZCaches.clear();
        mAllSideHallCaches.clear();
    }

    private void clearList() {
        mExhibitors.clear();
        mLetters.clear();
    }

    private void clearSearchList() {
        mSearchExhibitorsAZ.clear();
        mSearchSideAZs.clear();
        mSearchExhibitorsHall.clear();
        mSearchSideHalls.clear();
    }

    public void scrollTo(String letter) {
        int size = mExhibitors.size();
        for (int j = 0; j < size; j++) {
            if (mExhibitors.get(j).getSort().equals(letter)) {
                mRVScrollTo.scroll(j);
                break;
            }
        }
    }


}
